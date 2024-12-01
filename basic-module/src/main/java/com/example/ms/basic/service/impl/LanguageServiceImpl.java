package com.example.ms.basic.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.example.ms.basic.entity.Language;
import com.example.ms.basic.mapper.LanguageMapper;
import com.example.ms.basic.service.LanguageService;
import com.example.ms.service.impl.BaseServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LanguageServiceImpl extends BaseServiceImpl<LanguageMapper, Language> implements LanguageService {
    private final String CACHE_NAME = "langCache.";
    @PostConstruct
    @Override
    public void initCache() {
        cacheName = CACHE_NAME;
        super.initCache();
    }

    @Cached(name = CACHE_NAME, expire = 3600, cacheType = CacheType.BOTH)
    public List<Language> getAllLanguages() {
        return list(null);
    }
}
