package com.example.ms.security.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.template.QuickConfig;
import com.example.ms.utils.WebIdentityUtils;
import com.example.tools.entity.BaseEntity;
import com.example.tools.entity.CustomException;
import com.example.tools.entity.ResponseData;
import com.example.ms.security.entity.Role;
import com.example.ms.security.entity.RoleAuth;
import com.example.ms.security.entity.User;
import com.example.ms.security.entity.UserRole;
import com.example.ms.security.mapper.RoleMapper;
import com.example.ms.security.mapper.UserMapper;
import com.example.ms.security.mapper.UserRoleMapper;
import com.example.ms.security.service.MenuService;
import com.example.ms.security.service.RoleAuthService;
import com.example.ms.security.service.RoleService;
import com.example.ms.security.service.UserService;
import com.example.ms.service.impl.BaseServiceImpl;
import com.example.tools.utils.CommonUtils;
import com.example.tools.utils.Constant;
import com.example.tools.utils.JWTUtil;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.AndOrCriteriaGroup;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.ms.security.sqltable.SqlTableInstance.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Component
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {
    @Value("${token-expire}")
    private int tokenExpire;
    @Value("${token-refresh-expire}")
    private int tokenRefreshExpire;

    @Autowired
    UserRoleMapper userRoleMapper;

    private Cache<String, Object> tokenCache;

    @Autowired
    private RoleAuthService roleAuthService;

    @Autowired
    MenuService menuService;

    @Autowired
    WebIdentityUtils webIdentityUtils;

    @Autowired
    RoleService roleService;

    @Autowired
    RoleMapper roleMapper;

    private final String CACHE_NAME = "userCache.";

    @PostConstruct
    public void initCache() {
        clearCacheAfterUpdate = false;
        cacheName = CACHE_NAME;
        super.initCache();
        QuickConfig tokenCacheConfig = QuickConfig.newBuilder("tokenCache.")
                .expire(Duration.ofMinutes(tokenRefreshExpire * 60L))
                .cacheType(CacheType.BOTH) // two level cache
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        tokenCache = cacheManager.getOrCreateCache(tokenCacheConfig);
    }

    @SneakyThrows
    public ResponseData<Map<String, String>> login(User loginUser) {
        ResponseData<Map<String, String>> responseData = new ResponseData<>();
        String username = loginUser.getUsername();
        User existUser = getUserByUsername(username);
        if (existUser != null && existUser.getPassword().equals(loginUser.getPassword())) {
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            String token = JWTUtil.genToken(map, new Date(System.currentTimeMillis() + 60L * 1000L * tokenExpire));
            tokenCache.put(username, token);
            map.put("token", token);
            responseData.ok(map);
            cache.put(username, JSONObject.parseObject(JSON.toJSONString(existUser)));
        } else {
            responseData.setCode(401);
            responseData.setMessage("用户名密码错误");
        }
        return responseData;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData addNew(User user) {
        String password = RandomStringUtils.random(6);
        user.setPassword(CommonUtils.getMD5Hash(password));
        ResponseData responseData = super.addNew(user);
        List<Role> roleList = user.getRoles();
        if (CollectionUtils.isEmpty(roleList)) {
            return responseData;
        }
        checkUserRolesValid(roleList);
        List<UserRole> userRoles = roleList.stream().map(role -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(role.getId());
            webIdentityUtils.setCreateFields(userRole);
            return userRole;
        }).distinct().toList();
        userRoleMapper.insertMultiple(userRoles);
        responseData.setData(password);
        return responseData;
    }

    private void checkUserRolesValid(List<Role> roleList) {
        JSONObject userJson = webIdentityUtils.getCurrentUser();
        if (userJson == null) {
            throw new CustomException("缺少登录的用户信息");
        }
        User currentUser = JSONObject.parseObject(JSON.toJSONString(userJson), User.class);
        List<Role> children = getValidRoles(currentUser);
        checkRolesLevel(roleList, children);
    }

    private static void checkRolesLevel(List<Role> roleList, List<Role> children) {
        List<Role> higherLevelRoles = roleList.stream().filter(role -> !children.stream().map(BaseEntity::getId).toList().contains(role.getId())).toList();
        if (!higherLevelRoles.isEmpty()) {
            throw new CustomException("不能为用户设置高于当前用户的角色或无权限修改或删除选中的用户");
        }
        Role superAdmin = roleList.stream().filter(r -> r.getId() == Constant.SUPER_ADMIN_ROLE_ID).findFirst().orElse(null);
        if (superAdmin != null && roleList.size() > 1) {
            throw new CustomException("不能同时设置超级管理员和其他角色");
        }
    }

    private List<Role> getValidRoles(User currentUser) {
        List<Role> currentUserRoles = currentUser.getRoles();
        List<Role> children = new ArrayList<>();
        CommonUtils.getChildrenForTreeNodes(roleService.getAllRoles(), currentUserRoles, children, null);
        children.addAll(currentUserRoles);
        return children;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    @CacheUpdate(name = CACHE_NAME, key = "#user.username", value = "#user")
    public ResponseData editById(User user) {
        List<Role> roleList = user.getRoles();
        ResponseData responseData = super.editById(user);
        if (CollectionUtils.isEmpty(roleList)) {
            return responseData;
        }
        checkUserRolesValid(roleList);
        List<UserRole> existUserRoles = userRoleMapper.select(u -> u.where(userRole.userId, isEqualTo(user.getId())));
        List<UserRole> newUserRoles = new ArrayList<>();
        List<UserRole> keepUserRoles = new ArrayList<>();
        roleList.forEach(role -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(role.getId());
            webIdentityUtils.setCreateFields(userRole);
            if (!existUserRoles.contains(userRole)) {
                newUserRoles.add(userRole);
            } else {
                keepUserRoles.add(userRole);
            }
        });
        existUserRoles.removeAll(keepUserRoles);
        userRoleMapper.insertMultiple(newUserRoles);
        if (!existUserRoles.isEmpty()) {
            deleteUserRoles(existUserRoles);
        }
        // 重新获取用户的权限列表，主要是为了更新缓存
        Long[] roleIds = roleList.stream().map(BaseEntity::getId).toArray(Long[]::new);
        List<RoleAuth> roleAuthList = roleAuthService.getRoleAuthList(roleIds);
        List<String> authList = roleAuthList.stream()
                .flatMap(ra -> Arrays.stream(ra.getAuth().split(",")).map(auth -> ra.getRequestPath() + "." + auth))
                .toList();
        user.setAuthList(authList);
        responseData.setData(null);
        return responseData;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData removeById(long id) {
        ResponseData responseData = new ResponseData();
        User user = mapper.selectById(id, getRoleColumns(),
                this::extendStartExpression);
        if (user == null) {
            responseData.customError("用户不存在");
            return responseData;
        }
        List<Role> roleList = user.getRoles();
        if (CollectionUtils.isEmpty(roleList)) {
            responseData = super.removeById(id);
        } else {
            checkUserRolesValid(roleList);
            responseData = super.removeById(id);
            deleteUserRoles(roleList.stream().map(r -> {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(r.getId());
                return userRole;
            }).toList());
        }
        cache.remove(user.getUsername());
        return responseData;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData removeByIds(Long[] ids) {
        ResponseData responseData = new ResponseData();
        List<User> users = mapper.selectByIds(ids, getRoleColumns(),
                this::extendStartExpression);
        List<Role> roleList = users.stream().flatMap(u -> u.getRoles().stream()).toList();
        if (roleList.isEmpty()) {
            responseData = super.removeByIds(ids);
        } else {
            checkUserRolesValid(roleList);
            responseData = super.removeByIds(ids);
            deleteUserRoles(users.stream().flatMap(u -> u.getRoles().stream().map(r -> {
                UserRole userRole = new UserRole();
                userRole.setUserId(u.getId());
                userRole.setRoleId(r.getId());
                return userRole;
            })).toList());
        }
        cache.removeAll(users.stream().map(User::getUsername).collect(Collectors.toSet()));
        return responseData;
    }

    private void deleteUserRoles(List<UserRole> userRoles) {
        userRoleMapper.delete(u -> {
                    List<AndOrCriteriaGroup> orCriteriaGroups = new ArrayList<>();
                    for (UserRole ur : userRoles) {
                        AndOrCriteriaGroup orCriteriaGroup = or(userRole.userId, isEqualTo(ur.getUserId()), and(userRole.roleId, isEqualTo(ur.getRoleId())));
                        orCriteriaGroups.add(orCriteriaGroup);
                    }
                    return u.where(orCriteriaGroups);
                }
        );
    }

    public User getUserByUsername(String username) {
        if (username == null) return null;
        User currentUser = mapper.selectOne(c -> c.where(user.username, isEqualTo(username))
                        .and(user.enable, isEqualTo(true)).and(role.enable, isEqualTo(true)), getRoleColumns(),
                this::extendStartExpression);
        if (currentUser == null) return null;
        List<Role> roles = currentUser.getRoles();
        // 注意：超级管理员角色的权限是默认所有权限，所以不需要再查
        if (roles == null || roles.isEmpty() || roles.get(0).getId() == Constant.SUPER_ADMIN_ROLE_ID) {
            return currentUser;
        }
        // 通过角色获取权限
        Long[] roleIds = roles.stream().map(BaseEntity::getId).toArray(Long[]::new);
        List<RoleAuth> roleAuthList = roleAuthService.getRoleAuthList(roleIds);
        List<String> authList = roleAuthList.stream()
                .flatMap(ra -> Arrays.stream(ra.getAuth().split(","))
                        .map(auth -> ra.getRequestPath() == null ? auth : ra.getRequestPath().replace("/", ".") + "." + auth))
                .toList();
        currentUser.setAuthList(authList);
        return currentUser;
    }

    @Cached(name = CACHE_NAME, key = "#username", cacheType = CacheType.BOTH)
    public JSONObject getUserJsonByUsername(String username) {
        return JSON.parseObject(JSON.toJSONString(getUserByUsername(username)));
    }

    public List<User> getUsersByRoleId(Long roleId) {
        List<User> users = mapper.select(u -> u.where(user.id, isIn(
                        select(userRole.userId).from(userRole).where(userRole.roleId, isEqualTo(roleId))
                )), getRoleColumns(), this::extendStartExpression);
        Long[] allRoleIds = users.stream().flatMap(u -> u.getRoles().stream()).map(BaseEntity::getId).distinct().toArray(Long[]::new);
        List<RoleAuth> roleAuthList = roleAuthService.getRoleAuthList(allRoleIds);
        users.forEach(u -> {
            List<Long> roleIds = u.getRoles().stream().map(BaseEntity::getId).toList();
            List<RoleAuth> userRoleAuths = roleAuthList.stream().filter(ra -> roleIds.contains(ra.getRoleId())).toList();
            List<String> authList = userRoleAuths.stream()
                    .flatMap(ra -> Arrays.stream(ra.getAuth().split(","))
                            .map(auth -> ra.getRequestPath() == null ? auth : ra.getRequestPath().replace("/", ".") + "." + auth))
                    .toList();
            u.setAuthList(authList);
        });
        return users;
    }

    @CacheUpdate(name = CACHE_NAME, key = "#user.username", value = "#user")
    public void updateUserCache(User user) {
    }

    @Override
    public void extendStartExpression(QueryExpressionDSL<SelectModel> start) {
        start.leftJoin(userRole, on(user.id, equalTo(userRole.userId)))
                .leftJoin(role, on(userRole.roleId, equalTo(role.id)));
    }

    @Override
    public List<BasicColumn> getExtraColumns() {
        List<BasicColumn> basicColumns = new ArrayList<>();
        basicColumns.add(constant("group_concat(sys_role.id) roleIds"));
        basicColumns.add(constant("group_concat(sys_role.role_name) roleNames"));
        return basicColumns;
    }

    @Override
    public void extendSelectCompleter(QueryExpressionDSL<SelectModel> selectCompleter) {
        selectCompleter.groupBy(user.id);
    }

    public List<BasicColumn> getRoleColumns() {
        List<BasicColumn> basicColumns = new ArrayList<>();
        basicColumns.add(role.id.as("roleId"));
        basicColumns.add(role.roleName.as("roleName"));
        basicColumns.add(role.enable.as("roleEnable"));
        return basicColumns;
    }

    @Override
    public void importPreprocess(User user) {
        user.setPassword(CommonUtils.getMD5Hash(RandomStringUtils.random(6)));
        user.setEnable(true);
        super.importPreprocess(user);
    }

    @Override
    public ResponseData importPreprocess(List<User> users) {
        ResponseData responseData = new ResponseData();
        String roleNames = users.stream().map(User::getRoleNames).collect(Collectors.joining(","));
        if (StringUtils.isBlank(roleNames)) {
            responseData.ok();
            return responseData;
        }
        String[] roleNameList = roleNames.split(",");
        List<Role> roleList = roleMapper.select(r -> r.where(role.roleName, isIn(roleNameList)));
        JSONObject userJson = webIdentityUtils.getCurrentUser();
        if (userJson == null) {
            responseData.customError("缺少登录的用户信息");
            return responseData;
        }
        User currentUser = JSONObject.parseObject(JSON.toJSONString(userJson), User.class);
        List<Role> children = getValidRoles(currentUser);
        for (User u : users) {
            List<Role> roleListSingle = roleList.stream().filter(role -> Arrays.asList(u.getRoleNames().split(",")).contains(role.getRoleName())).toList();
            checkRolesLevel(roleListSingle, children);
            u.setRoles(roleListSingle);
        }
        responseData.ok();
        return responseData;
    }

    @Override
    public void importAfterProcess(List<User> users) {
        users.forEach(u -> {
            List<Role> roleList = u.getRoles();
            if (roleList == null || roleList.isEmpty()) {
                return;
            }
            List<UserRole> userRoles = roleList.stream().map(role -> {
                UserRole userRole = new UserRole();
                userRole.setUserId(u.getId());
                userRole.setRoleId(role.getId());
                webIdentityUtils.setCreateFields(userRole);
                return userRole;
            }).distinct().toList();
            userRoleMapper.insertMultiple(userRoles);
        });
    }
}
