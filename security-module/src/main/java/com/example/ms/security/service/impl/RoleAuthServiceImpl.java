package com.example.ms.security.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.ms.security.entity.Menu;
import com.example.ms.security.mapper.RoleMapper;
import com.example.ms.security.service.MenuService;
import com.example.ms.utils.WebIdentityUtils;
import com.example.tools.component.IdentityUtils;
import com.example.tools.entity.BaseEntity;
import com.example.tools.entity.ResponseData;
import com.example.ms.security.entity.Role;
import com.example.ms.security.entity.RoleAuth;
import com.example.ms.security.entity.User;
import com.example.ms.security.mapper.RoleAuthMapper;
import com.example.ms.security.service.RoleAuthService;
import com.example.ms.security.service.RoleService;
import com.example.ms.service.impl.BaseServiceImpl;
import com.example.tools.utils.CommonUtils;
import com.example.tools.utils.Constant;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.ms.security.sqltable.SqlTableInstance.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Component
public class RoleAuthServiceImpl extends BaseServiceImpl<RoleAuthMapper, RoleAuth> implements RoleAuthService {
    @Autowired
    WebIdentityUtils webIdentityUtils;

    @Autowired
    IdentityUtils identityUtils;

    @Autowired
    RoleService roleService;

    @Autowired
    MenuService menuService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData saveRoleAuth(List<RoleAuth> roleAuthList, Long roleId) {
        ResponseData responseData = new ResponseData();
        JSONObject userJson = webIdentityUtils.getCurrentUser();
        if (userJson == null) {
            responseData.customError("缺少登录的用户信息");
            return responseData;
        }
        User currentUser = JSONObject.parseObject(JSON.toJSONString(userJson), User.class);
        List<Role> currentUserRoles = currentUser.getRoles();
        List<Role> children = new ArrayList<>();
        CommonUtils.getChildrenForTreeNodes(roleService.getAllRoles(), currentUserRoles, children, null);
        List<Long> childrenIds = children.stream().map(BaseEntity::getId).toList();
        if (!identityUtils.isUserSuperAdminRole(userJson) && !childrenIds.contains(roleId)) {
            responseData.customError("只能设置低于当前用户角色的角色权限");
            return responseData;
        }
        List<RoleAuth> existRoleAuthList = getRoleAuthList(roleId);
        List<RoleAuth> newRoleAuthList = new ArrayList<>();
        List<RoleAuth> keepAuthList = new ArrayList<>();
        List<RoleAuth> updateAuthList = new ArrayList<>();
        roleAuthList.forEach(ra -> {
            RoleAuth existRoleAuth = existRoleAuthList.stream()
                    .filter(era -> era.getRoleId() == ra.getRoleId() && era.getMenuId() == ra.getMenuId())
                    .findFirst().orElse(null);
            if (existRoleAuth != null) {
                keepAuthList.add(ra);
                if (!existRoleAuth.getAuth().equals(ra.getAuth())) {
                    existRoleAuth.setAuth(ra.getAuth());
                    updateAuthList.add(existRoleAuth);
                }
            } else {
                webIdentityUtils.setCreateFields(ra);
                newRoleAuthList.add(ra);
            }
        });
        existRoleAuthList.removeAll(keepAuthList);
        addBatch(newRoleAuthList);
        updateBatch(updateAuthList);
        if (!existRoleAuthList.isEmpty()) {
            removeByIds(existRoleAuthList.stream().map(BaseEntity::getId).toArray(Long[]::new));
        }
        responseData.ok();
        return responseData;
    }

    @Override
    public List<RoleAuth> getRoleAuthList(long roleId) {
        return mapper.select(ra -> ra.where(roleAuth.roleId, isEqualTo(roleId)));
    }

    @Override
    public List<RoleAuth> getRoleAuthList(Long[] roleIds) {
        return mapper.select(ra -> ra.where(roleAuth.roleId, isIn(roleIds)));
    }

    public List<RoleAuth> getRoleAuth(Role role) {
        long roleId = role.getId();
        long parentId = role.getParentId();
        boolean isCurrentSuperAdmin = roleId == Constant.SUPER_ADMIN_ROLE_ID;
        boolean isParentSuperAdmin = parentId == Constant.SUPER_ADMIN_ROLE_ID;
        List<RoleAuth> roleAuthList = new ArrayList<>();
        if (isCurrentSuperAdmin || isParentSuperAdmin) {
            menuService.getAllMenus().forEach(m -> {
                RoleAuth roleAuth = new RoleAuth();
                roleAuth.setRoleId(Constant.SUPER_ADMIN_ROLE_ID);
                roleAuth.setMenuId(m.getId());
                roleAuth.setAuth(m.getAuth());
                roleAuth.setLabel(m.getLabel());
                roleAuth.setParentId(m.getParentId());
                roleAuthList.add(roleAuth);
            });
        }

        if (isCurrentSuperAdmin) {
            return roleAuthList;
        }

        SelectStatementProvider selectStatement = select(roleAuth.auth, menu.label, roleAuth.menuId.asCamelCase(),
                roleAuth.roleId.asCamelCase(), menu.parentId.asCamelCase())
                .from(menu)
                .join(roleAuth, on(menu.id, equalTo(roleAuth.menuId)))
                .where(roleAuth.roleId, isEqualTo(roleId), or(roleAuth.roleId, isEqualTo(parentId)))
                .and(menu.enable, isEqualTo(true))
                .orderBy(menu.orderNo, roleId < parentId ? roleAuth.roleId.descending() : roleAuth.roleId)
                .build()
                .render(RenderingStrategies.MYBATIS3);
        roleAuthList.addAll(mapper.selectMany(selectStatement));
        return roleAuthList;
    }
}
