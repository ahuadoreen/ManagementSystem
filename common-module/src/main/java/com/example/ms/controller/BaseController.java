package com.example.ms.controller;

import com.example.ms.service.BaseService;
import com.example.tools.entity.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public abstract class BaseController<M extends BaseService<T>, T> {
    @Autowired
    protected M service;

    @PostMapping("/add")
//    @PreAuthorize("@auth.hasAuthority()")
    public ResponseData add(@Validated(value = Insert.class) @RequestBody T t) {
        return service.addNew(t);
    }

    @PostMapping("/update")
//    @PreAuthorize("@auth.hasAuthority()")
    public ResponseData edit(@Validated(value = Update.class) @RequestBody T t) {
        return service.editById(t);
    }

    @PostMapping("/delete")
//    @PreAuthorize("@auth.hasAuthority()")
    public ResponseData remove(@RequestBody long id) {
        return service.removeById(id);
    }

    @PostMapping("/search")
//    @PreAuthorize("@auth.hasAuthority()")
    public ResponseData<ListWithPageData<T>> getData(@RequestBody FilterWithPageParam param) {
        ListWithPageData<T> pageData;
        if (param.getPageSize() == 0) {
            List<T> list = service.list(param);
            pageData = new ListWithPageData<>();
            pageData.setList(list);
        } else {
            pageData = service.page(param);
        }
        ResponseData<ListWithPageData<T>> response = new ResponseData<>();
        response.ok();
        response.setData(pageData);
        return response;
    }

    @PostMapping("/export")
//    @PreAuthorize("@auth.hasAuthority()")
    public void export(HttpServletResponse response, @RequestBody ExportParam param) {
        service.exportExcel(response, param.getFilterWithPageParam(), param.getTableInfo());
    }

    @PostMapping("/downloadTemplate")
//    @PreAuthorize("@auth.hasAuthority('import')")
    public void downloadTemplate(HttpServletResponse response, @RequestBody TableInfo tableInfo) {
        service.downloadTemplate(response, tableInfo);
    }

    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    @PreAuthorize("@auth.hasAuthority()")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(encoding = @Encoding(name = "tableInfo", contentType = MediaType.APPLICATION_JSON_VALUE)))
    public ResponseData importExcel(@RequestPart TableInfo tableInfo,
                                    @RequestPart(name = "file") MultipartFile file) {
        return service.importExcel(file, tableInfo);
    }

    @PostMapping("/deleteBatch")
//    @PreAuthorize("@auth.hasAuthority()")
    public ResponseData remove(@RequestBody Long[] ids) {
        return service.removeByIds(ids);
    }
}
