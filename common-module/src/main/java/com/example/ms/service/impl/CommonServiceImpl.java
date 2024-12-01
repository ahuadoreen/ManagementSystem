package com.example.ms.service.impl;

import com.example.ms.service.CommonService;
import com.example.tools.entity.ResponseData;
import io.micrometer.common.util.StringUtils;
import org.mybatis.dynamic.sql.select.render.DefaultSelectStatementProvider;
import org.mybatis.dynamic.sql.util.mybatis3.CommonSelectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommonServiceImpl implements CommonService {
    @Autowired
    CommonSelectMapper commonSelectMapper;

    @Override
    public List<Map<String, Object>> getSqlQuery(String sql, Map<String, Object> params) {
        if (params != null) {
            for (String key : params.keySet()) {
                Object param = params.get(key);
                if (param instanceof List || param instanceof Arrays) {
                    // 如果传入的是List或者数组，则默认是按in的条件查询，则需要自己拼接sql
                    // 为了上层能方便调用，只需要预留一个参数位置，然后在这里统一处理拼接
                    List<String> list;
                    if (param instanceof List) {
                        list = (List<String>) param;
                    } else {
                        list = Arrays.asList((String[]) param);
                    }
                    StringBuilder inSql = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        String item = list.get(i);
                        if (!inSql.isEmpty()) {
                            inSql.append(", ");
                        }
                        inSql.append("#{parameters.p").append(i).append("}");
                        params.put("p" + i, item);
                    }
                    sql = sql.replace("#{" + key + "}", "(" + inSql + ")");
                } else {
                    // 如果不是in条件，那也默认参数名可以自定义，这里统一加上parameters.
                    sql = sql.replace("#{" + key + "}", "#{parameters." + key + "}");
                }
            }
        } else {
            params = new HashMap<>();
        }
        DefaultSelectStatementProvider selectStatementProvider = DefaultSelectStatementProvider.withSelectStatement(sql)
                .withParameters(params)
                .build();
        return commonSelectMapper.selectManyMappedRows(selectStatementProvider);
    }
}
