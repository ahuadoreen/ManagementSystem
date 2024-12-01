package com.example.tools.component;

import com.alicp.jetcache.Cache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JetCacheUtils {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void clearAllByName(String cacheName, Cache cache) {
        // 拼接key，这里采用全模糊
        Set<String> completeKeys = stringRedisTemplate.keys(cacheName + "*");
        Set<String> keys = completeKeys.stream().map(key -> key.replace(cacheName, StringUtils.EMPTY)).collect(Collectors.toSet());
        if (!keys.isEmpty()) {
            cache.removeAll(keys);
        }
    }
}
