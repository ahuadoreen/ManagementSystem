package com.example.tools.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ListWithPageData<T> {
    @Schema(example = "1", title = "第几页，从1开始")
    private int pageCount;
    @Schema(example = "8", title = "总页数")
    private long total;
    private List<T> list;
}
