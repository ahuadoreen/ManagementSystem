package com.example.ms.security.service;

import com.example.ms.security.entity.Role;
import com.example.ms.service.BaseService;

import java.util.List;

public interface RoleService extends BaseService<Role> {
    List<Role> getAllRoles();
}
