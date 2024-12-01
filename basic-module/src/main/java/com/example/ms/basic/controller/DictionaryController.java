package com.example.ms.basic.controller;

import com.example.ms.basic.entity.Dictionary;
import com.example.ms.basic.service.DictionaryService;
import com.example.ms.controller.BaseController;
import com.example.tools.entity.ResponseData;
import com.example.tools.entity.SelectStatement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "dictionary", description = "dictionary api")
@RestController
@RequestMapping("/dictionary")
public class DictionaryController extends BaseController<DictionaryService, Dictionary> {
    @PostMapping("/getDictionariesByKeys")
    public ResponseData<Map<String, Object>> getDictionariesByKeys(@RequestBody List<String> keys) {
        Map<String, Object> data = service.getDictionariesByKeys(keys);
        ResponseData<Map<String, Object>> responseData = new ResponseData<>();
        responseData.ok(data);
        return responseData;
    }
}
