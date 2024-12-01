package com.example.tools.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class FilterWithPageParam {
    @Schema(example = "10", title = "每页显示条数")
    private int pageSize;
    @Schema(example = "1", title = "第几页，从1开始")
    private int index;
    @Schema(title = "过滤条件")
    private List<FilterCondition> filterConditions;
    private List<Sort> sorts;
    @Data
    public static class Sort{
        private String index;
        private Order order;
    }

    public enum Order {
        asc,
        desc
    }
}
