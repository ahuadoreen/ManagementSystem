package com.example.tools.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DataType {
    STRING(0),
    NUMBER(1),
    BOOL(2),
    DATE(3),
    DATETIME(4);
    private final int value;
    DataType(int value) {
        this.value = value;
    }

    public static DataType getDataType(int value) {
        return Arrays.stream(values()).filter(it -> it.value == value).findFirst().orElse(null);
    }
}
