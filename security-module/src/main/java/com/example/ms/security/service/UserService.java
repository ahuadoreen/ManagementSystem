package com.example.ms.security.service;

import com.alibaba.fastjson2.JSONObject;
import com.example.tools.entity.ResponseData;
import com.example.ms.security.entity.User;
import com.example.ms.service.BaseService;

import java.util.List;
import java.util.Map;

public interface UserService extends BaseService<User> {
    ResponseData<Map<String, String>> login(User user);
    User getUserByUsername(String username);
    List<User> getUsersByRoleId(Long roleId);
    void updateUserCache(User user);
    JSONObject getUserJsonByUsername(String username);
}
