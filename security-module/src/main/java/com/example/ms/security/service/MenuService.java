package com.example.ms.security.service;

import com.example.ms.security.entity.Menu;
import com.example.ms.service.BaseService;

import java.util.List;

public interface MenuService extends BaseService<Menu> {
    List<Menu> getAllMenus();

    List<Menu> getMenusByUsername(String username);
    void removeCacheByUsername(String username);
}
