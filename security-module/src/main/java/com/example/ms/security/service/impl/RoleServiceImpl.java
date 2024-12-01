package com.example.ms.security.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.example.tools.entity.ResponseData;
import com.example.ms.security.entity.Role;
import com.example.ms.security.entity.UserRole;
import com.example.ms.security.mapper.RoleAuthMapper;
import com.example.ms.security.mapper.RoleMapper;
import com.example.ms.security.mapper.UserRoleMapper;
import com.example.ms.security.service.RoleService;
import com.example.ms.service.impl.BaseServiceImpl;
import com.example.tools.component.JetCacheUtils;
import jakarta.annotation.PostConstruct;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.mybatis3.CommonSelectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.ms.security.sqltable.SqlTableInstance.roleAuth;
import static com.example.ms.security.sqltable.SqlTableInstance.userRole;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Component
public class RoleServiceImpl extends BaseServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    UserRoleMapper userRoleMapper;

    @Autowired
    RoleAuthMapper roleAuthMapper;

    private final String CACHE_NAME = "roleCache.";

    @Autowired
    JetCacheUtils jetCacheUtils;

    @PostConstruct
    @Override
    public void initCache() {
        cacheName = CACHE_NAME;
        super.initCache();
    }

    @Cached(name = CACHE_NAME, expire = 3600, cacheType = CacheType.BOTH)
    public List<Role> getAllRoles() {
        return this.list(null);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData removeById(long id) {
        ResponseData responseData = new ResponseData();
        List<UserRole> userRoleList = userRoleMapper.select(ur -> ur.where(userRole.roleId, isEqualTo(id)));
        if (!userRoleList.isEmpty()) {
            responseData.customError("该角色下存在用户，不能删除");
            return responseData;
        }
        responseData = super.removeById(id);
        roleAuthMapper.delete(r -> r.where(roleAuth.roleId, isEqualTo(id)));
        clearCache();
        return responseData;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData removeByIds(Long[] ids) {
        ResponseData responseData = new ResponseData();
        List<UserRole> userRoleList = userRoleMapper.select(ur -> ur.where(userRole.roleId, isIn(ids)));
        if (!userRoleList.isEmpty()) {
            responseData.customError("待删除的角色下存在用户，不能删除");
            return responseData;
        }
        responseData = super.removeByIds(ids);
        roleAuthMapper.delete(r -> r.where(roleAuth.roleId, isIn(ids)));
        clearCache();
        return responseData;
    }
}
