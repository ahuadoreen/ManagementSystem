package com.example.ms.security.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.ms.controller.BaseController;
import com.example.ms.utils.WebIdentityUtils;
import com.example.tools.entity.ResponseData;
import com.example.ms.security.entity.User;
import com.example.ms.security.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "user", description = "user api")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController<UserService, User> {
    @Autowired
    WebIdentityUtils webIdentityUtils;

    @io.swagger.v3.oas.annotations.parameters.RequestBody(content =
            {
                    @Content(schemaProperties = {
                            @SchemaProperty(name = "username", schema = @Schema(implementation = String.class)),
                            @SchemaProperty(name = "password", schema = @Schema(implementation = String.class))
                    })
            })
    @PostMapping(value = "/login")
    public ResponseData<Map<String, String>> login(@RequestBody User user) {
        return service.login(user);
    }

    @GetMapping(value = "/getUserByUsername")
    public ResponseData<JSONObject> getUserByUsername(String username) {
        ResponseData<JSONObject> responseData = new ResponseData<>();
        responseData.ok();
        responseData.setData(service.getUserJsonByUsername(username));
        return responseData;
    }

    @GetMapping(value = "/getCurrentUserInfo")
    public ResponseData<JSONObject> getCurrentUserInfo() {
        ResponseData<JSONObject> responseData = new ResponseData<>();
        responseData.ok();
        responseData.setData(service.getUserJsonByUsername(webIdentityUtils.getCurrentUsername()));
        return responseData;
    }
}
