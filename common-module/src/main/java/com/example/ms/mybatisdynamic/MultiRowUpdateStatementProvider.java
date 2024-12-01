package com.example.ms.mybatisdynamic;

import java.util.List;
import java.util.Map;

public interface MultiRowUpdateStatementProvider {
    List<Map<String, Object>> getParameters();

    String getUpdateStatement();
}
