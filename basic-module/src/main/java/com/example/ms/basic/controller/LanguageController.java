package com.example.ms.basic.controller;

import com.example.ms.basic.entity.Language;
import com.example.ms.basic.service.LanguageService;
import com.example.ms.controller.BaseController;
import com.example.tools.entity.Insert;
import com.example.tools.entity.ResponseData;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "language", description = "language api")
@RestController
@RequestMapping("/language")
public class LanguageController extends BaseController<LanguageService, Language> {
    @GetMapping("/getLangList")
    public ResponseData<Map<String, Map<String, String>>> getLangList() {
        List<Language> list = service.getAllLanguages();
        Map<String, Map<String, String>> data = new HashMap<>();
        Map<String, String> enMap = new HashMap<>();
        Map<String, String> zhCnMap = new HashMap<>();
        list.forEach(item -> {
            enMap.put(item.getKeyName(), item.getEnText());
            zhCnMap.put(item.getKeyName(), item.getCnText());
        });
        data.put("en", enMap);
        data.put("zh-cn", zhCnMap);
        ResponseData<Map<String, Map<String, String>>> responseData = new ResponseData<>();
        responseData.ok(data);
        return responseData;
    }

    @PostMapping("/autoAddMultipleByKeys")
//    @PreAuthorize("@auth.hasAuthority()")
    public ResponseData autoAddMultipleByKeys(@Validated(value = Insert.class) @RequestBody List<String> keys) {
        List<Language> languages = new ArrayList<>();
        keys.forEach(key -> {
            Language language = new Language();
            language.setKeyName(key);
            language.setEnText(key);
            language.setCnText(key);
            languages.add(language);
        });
        return service.addMultiple(languages);
    }
}
