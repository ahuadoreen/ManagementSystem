package com.example.ms.basic.service;

import com.example.ms.basic.entity.Dictionary;
import com.example.ms.service.BaseService;

import java.util.List;
import java.util.Map;

public interface DictionaryService extends BaseService<Dictionary> {
    Map<String, Object> getDictionariesByKeys(List<String> keys);
}
