package com.example.ms.basic.controller;

import com.example.tools.entity.Insert;
import com.example.tools.entity.Log;
import com.example.ms.basic.service.LogService;
import com.example.ms.controller.BaseController;
import com.example.tools.entity.ResponseData;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "log", description = "log api")
@RestController
@RequestMapping("/log")
public class LogController extends BaseController<LogService, Log> {
    @PostMapping("/addMultiple")
//    @PreAuthorize("@auth.hasAuthority()")
    public ResponseData addMultiple(@Validated(value = Insert.class) @RequestBody List<Log> logs) {
        return service.addMultiple(logs);
    }
}
