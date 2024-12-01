package com.example.ms.security.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.ms.controller.BaseController;
import com.example.ms.security.entity.Menu;
import com.example.ms.security.service.MenuService;
import com.example.ms.security.service.UserService;
import com.example.ms.utils.WebIdentityUtils;
import com.example.tools.component.IdentityUtils;
import com.example.tools.entity.ResponseData;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "menu", description = "menu api")
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController<MenuService, Menu> {
    @Autowired
    UserService userService;

    @Autowired
    WebIdentityUtils webIdentityUtils;

    @Autowired
    IdentityUtils identityUtils;

    public List<Menu> getUserMenusByUsername(String username)
    {
        JSONObject user = userService.getUserJsonByUsername(username);
        if (identityUtils.isUserSuperAdminRole(user)) {
            return service.getAllMenus();
        }
        return service.getMenusByUsername(username);
    }

    @GetMapping(value = "/getCurrentUserMenus")
    public ResponseData<List<Menu>> getCurrentUserMenus()
    {
        ResponseData<List<Menu>> responseData = new ResponseData<>();
        responseData.ok();
        responseData.setData(getUserMenusByUsername(webIdentityUtils.getCurrentUsername()));
        return responseData;
    }
}
