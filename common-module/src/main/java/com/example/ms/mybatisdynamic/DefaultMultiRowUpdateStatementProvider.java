package com.example.ms.mybatisdynamic;

import org.mybatis.dynamic.sql.update.render.DefaultUpdateStatementProvider;

import java.util.*;

public class DefaultMultiRowUpdateStatementProvider implements MultiRowUpdateStatementProvider{
    private final String updateStatement;
    private final List<Map<String, Object>> parameters;

    private DefaultMultiRowUpdateStatementProvider(DefaultMultiRowUpdateStatementProvider.Builder builder) {
        this.parameters = new ArrayList<>();
        this.updateStatement = (String) Objects.requireNonNull(builder.updateStatement);
        this.parameters.addAll(builder.parameters);
    }

    public List<Map<String, Object>> getParameters() {
        return this.parameters;
    }

    public String getUpdateStatement() {
        return this.updateStatement;
    }

    public static DefaultUpdateStatementProvider.Builder withUpdateStatement(String updateStatement) {
        return (new DefaultUpdateStatementProvider.Builder()).withUpdateStatement(updateStatement);
    }

    public static class Builder {
        private String updateStatement;
        private final List<Map<String, Object>> parameters = new ArrayList<>();

        public Builder() {
        }

        public DefaultMultiRowUpdateStatementProvider.Builder withUpdateStatement(String updateStatement) {
            this.updateStatement = updateStatement;
            return this;
        }

        public DefaultMultiRowUpdateStatementProvider.Builder withParameters(List<Map<String, Object>> parameters) {
            this.parameters.addAll(parameters);
            return this;
        }

        public DefaultMultiRowUpdateStatementProvider build() {
            return new DefaultMultiRowUpdateStatementProvider(this);
        }
    }
}
