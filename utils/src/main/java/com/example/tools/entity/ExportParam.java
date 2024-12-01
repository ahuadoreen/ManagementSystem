package com.example.tools.entity;

import lombok.Data;

import java.util.List;

@Data
public class ExportParam {
    private TableInfo tableInfo;
    private FilterWithPageParam filterWithPageParam;
}
