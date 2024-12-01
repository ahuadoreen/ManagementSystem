package com.example.ms.security.service;

import com.example.ms.security.entity.Role;
import com.example.tools.entity.ResponseData;
import com.example.ms.security.entity.RoleAuth;
import com.example.ms.service.BaseService;

import java.util.List;

public interface RoleAuthService extends BaseService<RoleAuth> {
    ResponseData saveRoleAuth(List<RoleAuth> roleAuthList, Long roleId);
    List<RoleAuth> getRoleAuthList(long roleId);
    List<RoleAuth> getRoleAuthList(Long[] roleId);
    List<RoleAuth> getRoleAuth(Role role);
}
