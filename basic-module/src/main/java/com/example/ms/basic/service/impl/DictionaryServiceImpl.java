package com.example.ms.basic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.ms.basic.entity.Dictionary;
import com.example.ms.basic.mapper.DictionaryMapper;
import com.example.ms.basic.service.DictionaryService;
import com.example.ms.service.CommonService;
import com.example.ms.service.impl.BaseServiceImpl;
import com.example.tools.entity.*;
import com.example.tools.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static com.example.tools.utils.CommonUtils.checkDataType;
import static com.example.tools.utils.Constant.*;

@Component
public class DictionaryServiceImpl extends BaseServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {
    @Autowired
    CommonService commonService;
    @Autowired
    RestTemplate restTemplate;

    public Map<String, Object> getDictionariesByKeys(List<String> keys) {
        String sql = "WITH RECURSIVE subtree AS (\n" +
                "    SELECT\n" +
                "        id,\n" +
                "        dictionary_name,\n" +
                "        dictionary_key,\n" +
                "        dictionary_value,\n" +
                "        key_type,\n" +
                "        value_type,\n" +
                "        dictionary_type,\n" +
                "        service_name,\n" +
                "        frontend_style,\n" +
                "        order_no,\n" +
                "        parent_id as parentId,\n" +
                "        1 AS depth\n" +
                "    FROM\n" +
                "        sys_dictionary\n" +
                "    WHERE\n" +
                "        dictionary_key in #{keys}\n" +
                "    UNION ALL\n" +
                "    SELECT\n" +
                "        d.id,\n" +
                "        d.dictionary_name,\n" +
                "        d.dictionary_key,\n" +
                "        d.dictionary_value,\n" +
                "        d.key_type,\n" +
                "        d.value_type,\n" +
                "        d.dictionary_type,\n" +
                "        d.service_name,\n" +
                "        d.frontend_style,\n" +
                "        d.order_no,\n" +
                "        d.parent_id as parentId,\n" +
                "        s.depth + 1\n" +
                "    FROM\n" +
                "        sys_dictionary d\n" +
                "    INNER JOIN subtree s ON d.parent_id = s.id\n" +
                ")\n" +
                "SELECT * FROM subtree";
        Map<String, Object> params = new HashMap<>();
        params.put("keys", keys);
        List<Map<String, Object>> dictionaryMaps = commonService.getSqlQuery(sql, params);
        List<Map<String, Object>> dictionaryTree = new ArrayList<>();
        CommonUtils.formatListToTree(dictionaryMaps, dictionaryTree, null);
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> dictionaryMap : dictionaryTree) {
            Integer dictionaryType = (Integer) dictionaryMap.get("dictionary_type");
            List<Map<String, Object>> data;
            if (dictionaryType == Dictionary.DictionaryType.ENUM.getValue()) {
                // 单条常量数据
                if (dictionaryMap.get("children") == null) {
                    formatMapData(dictionaryMap, "dictionary_value", "value_type", result, (String) dictionaryMap.get("dictionary_key"));
                    continue;
                }
                // 多条为一组的常量数据
                data = ((List<Map<String, Object>>) dictionaryMap.get("children")).stream()
                        .sorted(Comparator.comparingInt(o -> o.get("order_no") == null ? 10000000 : (Integer) o.get("order_no")))
                        .map(d -> {
                            Map<String, Object> map = new HashMap<>();
                            formatMapData(d, "dictionary_key", "key_type", map, "value");
                            formatMapData(d, "dictionary_value", "value_type", map, "label");
                            if (d.get("frontend_style") != null) {
                                String frontendStyle = (String) d.get("frontend_style");
                                Map<String, String> style = JSON.parseObject(frontendStyle, Map.class);
                                map.putAll(style);
                            }
                            return map;
                        }).toList();
            } else {
                String serviceName = (String) dictionaryMap.get("service_name");
                String dictionaryValue = (String) dictionaryMap.get("dictionary_value");
                if (dictionaryType == Dictionary.DictionaryType.SQL.getValue()) {
                    SelectStatement selectStatement = new SelectStatement();
                    selectStatement.setSelectStatement(dictionaryValue);
                    ResponseData<List<Map<String, Object>>> responseData = restTemplate.postForObject("http://" + serviceName + "/common/getSqlQuery", selectStatement, ResponseData.class);
                    data = responseData.getData();
                } else {
                    FilterWithPageParam filterWithPageParam = new FilterWithPageParam();
                    filterWithPageParam.setPageSize(0);
                    filterWithPageParam.setFilterConditions(List.of());
                    ResponseData<List<Map<String, Object>>> responseData = restTemplate.postForObject("http://" + serviceName + dictionaryValue, filterWithPageParam, ResponseData.class);
                    data = responseData.getData();
                }
            }
            result.put((String) dictionaryMap.get("dictionary_key"), data);
        }
        return result;
    }

    private static void formatMapData(Map<String, Object> rawData, String fieldName, String typeFieldName, Map<String, Object> result, String newFieldName) {
        Object rawFieldValue = rawData.get(fieldName);
        Integer dataTypeValue = (Integer) rawData.get(typeFieldName);
        DataType dataType = DataType.getDataType(dataTypeValue);
        switch (dataType) {
            case STRING:
                result.put(newFieldName, (String) rawFieldValue);
                break;
            case NUMBER:
                result.put(newFieldName, Long.parseLong((String) rawFieldValue));
                break;
            case BOOL:
                result.put(newFieldName, "true".equals(rawFieldValue));
                break;
            case DATE:
                result.put(newFieldName, LocalDate.parse((String) rawFieldValue, DATE_FORMATTER));
                break;
            case DATETIME:
                result.put(newFieldName, ZonedDateTime.parse((String) rawFieldValue, DATETIME_FORMATTER));
                break;
            default:
                // 处理未知类型的情况
                throw new CustomException(500, "未知的数据类型");
        }
    }

    @Override
    protected void addOrEditPreProcess(Dictionary dictionary) {
        // 字典类型不为常量
        if (dictionary.getDictionaryType() != Dictionary.DictionaryType.ENUM.getValue()) {
            // 字典值和服务名称不能为空
            if (StringUtils.isBlank(dictionary.getDictionaryValue()) || StringUtils.isBlank(dictionary.getServiceName())) {
                throw new CustomException("字典类型不为常量时，字典值和服务名称不能为空");
            }
            // 上级字典只能为0
            if (dictionary.getParentId() != 0) {
                throw new CustomException("字典类型为常量时才可以有上级字典");
            }
            // 前端样式只能为空
            if (StringUtils.isNotBlank(dictionary.getFrontendStyle())) {
                throw new CustomException("字典类型为常量时才可以设置前端样式");
            }
        } else {
            // 校验关键字值和关键字类型是否一致
            checkDataType(dictionary.getDictionaryKey(), dictionary.getKeyType(), "关键字值和关键字类型不匹配");
            // 校验字典值和字典值类型是否一致
            checkDataType(dictionary.getDictionaryValue(), dictionary.getValueType(), "字典值和字典值类型不匹配");
        }
    }
}
