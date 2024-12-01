package com.example.ms.security.service.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.example.ms.security.entity.Menu;
import com.example.ms.security.mapper.MenuMapper;
import com.example.ms.security.mapper.RoleAuthMapper;
import com.example.ms.security.service.MenuService;
import com.example.ms.service.impl.BaseServiceImpl;
import com.example.tools.component.JetCacheUtils;
import com.example.tools.entity.ResponseData;
import jakarta.annotation.PostConstruct;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.example.ms.security.sqltable.SqlTableInstance.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Component
public class MenuServiceImpl extends BaseServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    RoleAuthMapper roleAuthMapper;

    @Autowired
    JetCacheUtils jetCacheUtils;

    private final String CACHE_NAME = "menuCache.";

    @PostConstruct
    @Override
    public void initCache() {
        cacheName = CACHE_NAME;
        super.initCache();
    }

    @Cached(name = CACHE_NAME, expire = 3600, cacheType = CacheType.BOTH)
    public List<Menu> getAllMenus() {
        List<Menu> menus = this.list(null);
        menus.sort(Comparator.comparingInt(o -> o.getOrderNo() == null ? 10000000 : o.getOrderNo()));
        return menus;
    }

    @Cached(name = CACHE_NAME, key = "#username", expire = 3600, cacheType = CacheType.BOTH)
    public List<Menu> getMenusByUsername(String username) {
        SelectStatementProvider selectStatement = select(menu.menuName.asCamelCase(), menu.url, menu.icon, menu.orderNo.asCamelCase(), menu.parentId.asCamelCase(),
                menu.requestPath.asCamelCase(), roleAuth.auth, menu.label, menu.isShow.asCamelCase(), menu.id)
                .from(menu)
                .join(roleAuth, on(menu.id, equalTo(roleAuth.menuId)))
                .join(userRole, on(userRole.roleId, equalTo(roleAuth.roleId)))
                .join(user, on(userRole.userId, equalTo(user.id)))
                .where(user.username, isEqualTo(username))
                .and(menu.enable, isEqualTo(true))
                .orderBy(menu.orderNo)
                .build()
                .render(RenderingStrategies.MYBATIS3);
        List<Menu> userMenus = mapper.selectMany(selectStatement);
        List<Menu> distinctUserMenus = new ArrayList<>();
        userMenus.forEach(menu -> {
            if (!distinctUserMenus.contains(menu)) {
                distinctUserMenus.add(menu);
            } else {
                // 可能有包含相同菜单但是权限分配不同的角色，取最大权限的那个
                distinctUserMenus.stream().filter(m -> m.getId().equals(menu.getId())).findFirst().ifPresent(m -> {
                    if (m.getAuth().length() < menu.getAuth().length()) {
                        m.setAuth(menu.getAuth());
                    }
                });
            }
        });
        return distinctUserMenus;
    }

    @Override
    public void addOrEditPreProcess(Menu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData removeById(long id) {
        ResponseData responseData = super.removeById(id);
        roleAuthMapper.delete(r -> r.where(roleAuth.menuId, isEqualTo(id)));
        clearCache();
        return responseData;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ResponseData removeByIds(Long[] ids) {
        ResponseData responseData = super.removeByIds(ids);
        roleAuthMapper.delete(r -> r.where(roleAuth.menuId, isIn(ids)));
        clearCache();
        return responseData;
    }

    @CacheInvalidate(name = CACHE_NAME, key = "#username")
    public void removeCacheByUsername(String username) {
    }
}
