package com.example.ms.service;

import com.example.tools.entity.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
public interface BaseService<T> {
    @Validated(value = Insert.class)
    ResponseData addNew(@Valid T t);

    @Validated(value = Update.class)
    ResponseData editById(@Valid T t);

    ResponseData removeById(long id);

    ResponseData removeByIds(Long[] id);

    List<T> list(FilterWithPageParam filterWithPageParam);

    ListWithPageData<T> page(FilterWithPageParam filterWithPageParam);

    @Validated(value = Insert.class)
    ResponseData addMultiple(@Valid List<T> tList);

    @Validated(value = Insert.class)
    ResponseData addBatch(@Valid List<T> tList, int batchSize, boolean existUpdate);

    @Validated(value = Insert.class)
    ResponseData addBatch(@Valid List<T> tList, int batchSize);

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Validated(value = Insert.class)
    ResponseData addBatch(@Valid List<T> tList);

    @Validated(value = Update.class)
    ResponseData updateBatch(@Valid List<T> tList, int batchSize);

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Validated(value = Update.class)
    ResponseData updateBatch(@Valid List<T> tList);

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @Validated(value = Update.class)
    ResponseData updateMultiple(@Valid List<T> tList);

    void exportExcel(HttpServletResponse response, FilterWithPageParam filterWithPageParam, TableInfo tableInfo);

    void downloadTemplate(HttpServletResponse response, TableInfo tableInfo);

    @Transactional(
            rollbackFor = {Exception.class}
    )
    ResponseData importExcel(MultipartFile file, TableInfo tableInfo);
}
