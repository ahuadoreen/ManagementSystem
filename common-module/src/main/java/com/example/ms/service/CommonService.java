package com.example.ms.service;

import com.example.tools.entity.ResponseData;
import com.example.tools.entity.SelectStatement;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface CommonService {
    List<Map<String, Object>> getSqlQuery(String sql, Map<String, Object> params);
}
