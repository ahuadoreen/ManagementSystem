package com.example.ms.security.controller;

import com.example.ms.controller.BaseController;
import com.example.tools.entity.ResponseData;
import com.example.ms.security.entity.Role;
import com.example.ms.security.entity.RoleAuth;
import com.example.ms.security.entity.User;
import com.example.ms.security.service.MenuService;
import com.example.ms.security.service.RoleAuthService;
import com.example.ms.security.service.RoleService;
import com.example.ms.security.service.UserService;
import com.example.tools.utils.Constant;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "role", description = "role api")
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController<RoleService, Role> {
    @Autowired
    RoleAuthService roleAuthService;
    @Autowired
    UserService userService;
    @Autowired
    MenuService menuService;

    @PostMapping(value = "/saveRoleAuth")
    public ResponseData saveRoleAuth(@RequestBody List<RoleAuth> roleAuthList) {
        ResponseData responseData = new ResponseData();
        Long[] roleIds = roleAuthList.stream().map(RoleAuth::getRoleId).distinct().toArray(Long[]::new);
        if (roleIds.length != 1) {
            responseData.setCode(1001);
            responseData.setMessage("一次只能修改一个角色权限");
            return responseData;
        }
        if (roleIds[0] == Constant.SUPER_ADMIN_ROLE_ID) {
            responseData.setCode(1001);
            responseData.setMessage("超级管理员权限不需要修改");
            return responseData;
        }
        responseData = roleAuthService.saveRoleAuth(roleAuthList, roleIds[0]);
        if (responseData.getCode() != 200) {
            return responseData;
        }
        List<User> updateUsers = userService.getUsersByRoleId(roleIds[0]);
        updateUsers.forEach(u -> {
            userService.updateUserCache(u);
            menuService.removeCacheByUsername(u.getUsername());
        });
        responseData.ok();
        return responseData;
    }

    @PostMapping(value = "/getRoleAuth")
    public ResponseData getRoleAuth(@RequestBody Role role) {
        ResponseData responseData = new ResponseData();
        List<RoleAuth> roleAuthList = roleAuthService.getRoleAuth(role);
        responseData.ok(roleAuthList);
        return responseData;
    }
}
