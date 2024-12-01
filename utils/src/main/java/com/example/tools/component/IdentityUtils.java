package com.example.tools.component;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.RefreshPolicy;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.example.tools.utils.Constant;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class IdentityUtils {
    @Value("${user-name-field}")
    private String userNameField;
    @Value("${user-auth-field}")
    private String userAuthField;
    @Value("${user-role-field}")
    private String userRoleField;
    @Value("${user-cache-key}")
    private String userCacheKey;
    @Autowired
    CacheManager cacheManager;
    @Value("${token-expire}")
    private int tokenExpire;
    @Value("${token-refresh-expire}")
    private int tokenRefreshExpire;
    private Cache<String, Object> cache;
    @Autowired
    IdentityService identityService;

    @PostConstruct
    public void initCache() {
        RefreshPolicy policy = RefreshPolicy.newPolicy(tokenExpire, TimeUnit.MINUTES)
                .stopRefreshAfterLastAccess(tokenRefreshExpire, TimeUnit.HOURS);
        QuickConfig config = QuickConfig.newBuilder(userCacheKey)
                .expire(Duration.ofMinutes(tokenRefreshExpire * 60L))
                .loader(k-> identityService.getCurrentUser(k.toString()))
                .refreshPolicy(policy)
                .cacheType(CacheType.REMOTE)
                .syncLocal(false)
                .build();
        cache = cacheManager.getOrCreateCache(config);
    }

    public <T> void setCreateUpdateFields(T entity, boolean isCreate, String username) {
        Class<?> entityClass = entity.getClass();
        Class<?> superClass = entityClass.getSuperclass();
        Field[] fields = entityClass.getDeclaredFields();
        Field[] superFields = superClass.getDeclaredFields();
        setCreateUpdateFields(entity, fields, isCreate, username);
        setCreateUpdateFields(entity, superFields, isCreate, username);
    }

    @SneakyThrows
    public <T> void setCreateUpdateFields(T entity, Field[] fields, boolean isCreate, String username) {
        for(Field field : fields) {
            if (isCreate) {
                if (field.getName().equals("createName")) {
                    field.setAccessible(true);
                    field.set(entity, username);
                }
                if (field.getName().equals("createTime")) {
                    field.setAccessible(true);
                    field.set(entity, Instant.now());
                }
            }
            if (field.getName().equals("updateName")) {
                field.setAccessible(true);
                field.set(entity, username);
            }
            if (field.getName().equals("updateTime")) {
                field.setAccessible(true);
                field.set(entity, Instant.now());
            }
        }
    }

    public <T> void setCreateFields(T t, String username) {
        String trueName = getCurrentUserTrueName(username);
        setCreateUpdateFields(t, true, trueName);
    }

    public <T> void setUpdateFields(T t, String username) {
        String trueName = getCurrentUserTrueName(username);
        setCreateUpdateFields(t, false, trueName);
    }

    @SneakyThrows
    public boolean isSuperAdminRole(JSONObject role) {
        return (Integer) role.get("id") == Constant.SUPER_ADMIN_ROLE_ID;
    }

    @SneakyThrows
    public JSONObject getCurrentUser(String username) {
        if (username == null) {
            return null;
        }
        if (cache == null) {
            return null;
        }
        if (cache.get(username) != null && cache.get(username) instanceof JSONObject) {
            return (JSONObject) cache.get(username);
        }
        return null;
    }

    @SneakyThrows
    public String getCurrentUserTrueName(String username) {
        JSONObject user = getCurrentUser(username);
        if (user == null) {
            return "unknown";
        }
        return (String) user.get(userNameField);
    }

    public boolean isSuperAdminRole(String username) {
        JSONObject user = getCurrentUser(username);
        if (user == null) {
            return false;
        }
        return isUserSuperAdminRole(user);
    }

    @SneakyThrows
    public boolean isUserSuperAdminRole(JSONObject user) {
        JSONArray roleList = (JSONArray) user.get(userRoleField);
        return roleList != null && !roleList.isEmpty() && isSuperAdminRole((JSONObject) roleList.get(0));
    }

    public List<String> getCurrentAuthList(String username) {
        JSONObject user = getCurrentUser(username);
        if (user == null) {
            return null;
        }
        return getCurrentAuthList(user);
    }

    @SneakyThrows
    public List<String> getCurrentAuthList(JSONObject user) {
        JSONArray authList = (JSONArray) user.get(userAuthField);
        if (authList != null) {
            return authList.toJavaList(String.class);
        }
        return null;
    }
}
