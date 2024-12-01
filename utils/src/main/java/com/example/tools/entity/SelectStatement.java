package com.example.tools.entity;

import lombok.Data;

import java.util.Map;

@Data
public class SelectStatement {
    /**
     * 查询的sql语句
     */
    private String selectStatement;
    /**
     * 查询的参数
     */
    private Map<String, Object> parameters;
}
