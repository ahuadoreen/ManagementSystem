package com.example.ms.controller;

import com.example.ms.service.CommonService;
import com.example.tools.entity.ResponseData;
import com.example.tools.entity.SelectStatement;
import org.mybatis.dynamic.sql.select.render.DefaultSelectStatementProvider;
import org.mybatis.dynamic.sql.util.mybatis3.CommonSelectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    CommonService commonService;

    @PostMapping("/getSqlQuery")
    public ResponseData<List<Map<String, Object>>> getSqlQuery(@RequestBody SelectStatement selectStatement) {
        List<Map<String, Object>> data = commonService.getSqlQuery(selectStatement.getSelectStatement(), selectStatement.getParameters());
        ResponseData<List<Map<String, Object>>> responseData = new ResponseData<>();
        responseData.ok(data);
        return responseData;
    }
}
