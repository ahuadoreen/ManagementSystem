package com.example.ms.mybatisdynamic;

import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

public class SqlProviderAdapterExtension extends SqlProviderAdapter {
    public String updateMultiple(MultiRowUpdateStatementProvider updateStatement) {
        return updateStatement.getUpdateStatement();
    }
}
