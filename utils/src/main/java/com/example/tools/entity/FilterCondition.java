package com.example.tools.entity;

import lombok.Data;

@Data
public class FilterCondition {
    /**
     * 查询条件
     */
    public enum Condition {
        isEqualTo,
        isIn,
        isLike,
        isGreaterThan,
        isGreaterThanOrEqualTo,
        isLessThan,
        isLessThanOrEqualTo,
        isBetween
    }

    /**
     * 查询的entity中对应的字段名
     */
    private String field;

    /**
     * 查询的值
     */
    private String value;

    /**
     * 查询字段的类型
     */
    private String type;

    /**
     * 查询字段的值，通常用于in或between条件的查询
     */
    private String[] values;

    /**
     * 查询条件
     */
    private Condition condition;
}
