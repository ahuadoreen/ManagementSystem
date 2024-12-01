package com.example.ms.basic.service;

import com.example.ms.basic.entity.Language;
import com.example.ms.service.BaseService;

import java.util.List;

public interface LanguageService extends BaseService<Language> {
    List<Language> getAllLanguages();
}
