package com.example.ms.mapper;

import com.example.ms.annotation.TableName;
import com.example.tools.entity.*;
import com.example.ms.mybatisdynamic.MultiRowUpdateStatementProvider;
import com.example.ms.mybatisdynamic.SqlProviderAdapterExtension;
import com.example.tools.utils.CommonUtils;
import com.example.tools.utils.StringUtilsExtension;
import com.example.tools.utils.YamlConfigureUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.mybatis.dynamic.sql.*;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.BatchInsertDSL;
import org.mybatis.dynamic.sql.insert.InsertDSL;
import org.mybatis.dynamic.sql.insert.render.BatchInsert;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.CountDSL;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;
import org.springframework.context.annotation.Primary;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.mybatis.dynamic.sql.SqlBuilder.*;

@Primary
public interface BaseMapper<T> {
    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "record.id")
    int insert(InsertStatementProvider<T> insertStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    long count(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    List<T> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    T selectOne(SelectStatementProvider selectStatement);

    @InsertProvider(type = SqlProviderAdapter.class, method = "insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<T> insertStatement);

    @InsertProvider(type = SqlProviderAdapterExtension.class, method = "updateMultiple")
    int updateMultiple(MultiRowUpdateStatementProvider updateStatement);

    default Class<?> getEntityClass() {
        Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(getClass(), BaseMapper.class);
        return typeArguments[0];
    }

    @SneakyThrows
    default SqlTable getSqlTableFromEntity(Class<?> entityClass) {
        SqlTable sqlTable;
        String tableInstanceName = StringUtilsExtension.firstCharToLower(entityClass.getSimpleName());
        if (entityClass.isAnnotationPresent(TableName.class)) {
            String value = entityClass.getAnnotation(TableName.class).value();
            tableInstanceName = StringUtils.isBlank(value) ? tableInstanceName : value;
        }
        Field instanceField = Class.forName(YamlConfigureUtil.getStrYmlVal("sqltable-instance-class-name")).getDeclaredField(tableInstanceName);
        sqlTable = (SqlTable) instanceField.get(null);
        return sqlTable;
    }

    @SneakyThrows
    default TableInfo generateTableInfo(Class<?> entityClass, SqlTable sqlTable) {
        TableInfo tableInfo = new TableInfo();
        List<TableColumn> tableColumns = new ArrayList<>();
        Class<?> tableClass = sqlTable.getClass();
        Field[] fields = entityClass.getDeclaredFields();
        Class<?> superClass = entityClass.getSuperclass();
        Class<?> superTableClass = tableClass.getSuperclass();
        if (entityClass.isAnnotationPresent(TableName.class)) {
            TableName tableAnnotation = entityClass.getAnnotation(TableName.class);
            tableInfo.setPrimaryKey(tableAnnotation.primaryKey());
            tableInfo.setUniqueKeys(tableAnnotation.uniqueKeys());
            tableInfo.setLabel(StringUtils.isBlank(tableAnnotation.label()) ? tableClass.getSimpleName() : tableAnnotation.label());
        } else {
            tableInfo.setPrimaryKey("id");
        }
        if (superClass != null && superTableClass != null) {
            generateTableColumns(superClass.getDeclaredFields(), sqlTable, tableColumns, superTableClass);
        }
        generateTableColumns(fields, sqlTable, tableColumns, tableClass);
        tableInfo.setTableColumns(tableColumns);
        return tableInfo;
    }

    @SneakyThrows
    default void generateTableColumns(Field[] fields, SqlTable sqlTable,
                                      List<TableColumn> tableColumns, Class<?> tableClass) {
        for (Field field : fields) {
            String fieldName = field.getName();
            TableColumn tableColumn = new TableColumn() {{
                setIndex(fieldName);
                setInsertable(true);
                setUpdatable(true);
                setQueryable(true);
                setFilterable(true);
                setExportable(true);
                setImportable(true);
            }};
            String columnName = fieldName;
            com.example.ms.annotation.TableColumn tableColumnAnnotation = field.getAnnotation(com.example.ms.annotation.TableColumn.class);
            if (tableColumnAnnotation != null) {
                if (tableColumnAnnotation.ignore()) {
                    continue;
                }
                if (StringUtils.isNotBlank(tableColumnAnnotation.value())) {
                    columnName = tableColumnAnnotation.value();
                }
                if (!tableColumnAnnotation.insertable() && !tableColumnAnnotation.queryable()) {
                    continue;
                }
                tableColumn.setInsertable(tableColumnAnnotation.insertable());
                tableColumn.setUpdatable(tableColumnAnnotation.updatable());
                tableColumn.setQueryable(tableColumnAnnotation.queryable());
                tableColumn.setFilterable(tableColumnAnnotation.filterable());
                tableColumn.setImportable(tableColumnAnnotation.importable());
                tableColumn.setExportable(tableColumnAnnotation.exportable());
                tableColumn.setTitle(StringUtils.isBlank(tableColumnAnnotation.title()) ? fieldName : tableColumnAnnotation.title());
            }
            Field columnField = tableClass.getDeclaredField(columnName);

            tableColumn.setSqlColumn((SqlColumn) columnField.get(sqlTable));
            tableColumns.add(tableColumn);
        }
    }

    default int insert(T t) {
        InsertStatementProvider<T> insertStatementProvider = getInsertStatementProvider(t);
        return this.insert(insertStatementProvider);
    }

    default int updateById(T t) {
        UpdateStatementProvider updateStatementProvider = getUpdateStatementProvider(t);
        return this.update(updateStatementProvider);
    }

    default int delete(DeleteDSLCompleter completer) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        return MyBatis3Utils.deleteFrom(this::delete, sqlTable, completer);
    }

    default int deleteByIds(Long[] ids) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.deleteFrom(this::delete, sqlTable, c -> {
                    SqlColumn idColumn = getIdSqlColumn(tableInfo);
                    if (ids.length == 1) {
                        c.where(idColumn, isEqualTo(ids[0]));
                    } else {
                        c.where(idColumn, isIn(ids));
                    }
                    return c;
                }
        );
    }

    private static SqlColumn getIdSqlColumn(TableInfo tableInfo) {
        TableColumn idTableColumn = tableInfo.getTableColumns().stream()
                .filter(column -> tableInfo.getPrimaryKey().equalsIgnoreCase(column.getIndex()))
                .findFirst().orElse(null);
        SqlColumn idColumn;
        if (idTableColumn != null) {
            idColumn = idTableColumn.getSqlColumn();
        } else {
            throw new CustomException(500, "primaryKeySqlColumnMissing");
        }
        return idColumn;
    }

    default int deleteById(long id) {
        return deleteByIds(new Long[]{id});
    }

    default List<T> select(SelectDSLCompleter completer, List<BasicColumn> extraColumns, Consumer<QueryExpressionDSL<SelectModel>> extraStartExpression) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.selectList(this::selectMany, generateSelectStart(tableInfo.getTableColumns(), sqlTable, extraColumns, extraStartExpression), completer);
    }

    default List<T> select(SelectDSLCompleter completer) {
        return select(completer, null, null);
    }

    default List<T> select(FilterWithPageParam filterWithPageParam, Consumer<QueryExpressionDSL<SelectModel>> extraSelectCompleter, List<BasicColumn> extraColumns, Consumer<QueryExpressionDSL<SelectModel>> extraStartExpression) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.selectList(this::selectMany, generateSelectStart(tableInfo.getTableColumns(), sqlTable,
                        extraColumns, extraStartExpression),
                c -> generateSelectCompleter(c, tableInfo.getTableColumns(), filterWithPageParam, extraSelectCompleter));
    }

    default QueryExpressionDSL<SelectModel> generateSelectStart(List<TableColumn> tableColumns, SqlTable sqlTable,
                                                                List<BasicColumn> extraColumns, Consumer<QueryExpressionDSL<SelectModel>> extraStartExpression) {
        List<BasicColumn> basicColumns = new ArrayList<>();
        for (TableColumn column : tableColumns) {
            if (column.isQueryable()) {
                basicColumns.add(column.getSqlColumn().asCamelCase());
            }
        }
        addExtraColumns(basicColumns);
        if (extraColumns != null) {
            basicColumns.addAll(extraColumns);
        }
        QueryExpressionDSL<SelectModel> start = SqlBuilder.select(basicColumns.toArray(new BasicColumn[0])).from(sqlTable);
        extendStartExpression(start);
        if (extraStartExpression != null) {
            extraStartExpression.accept(start);
        }
        return start;
    }

    default List<T> select(FilterWithPageParam filterWithPageParam) {
        return select(filterWithPageParam, null, null, null);
    }

    default T selectOne(SelectDSLCompleter completer, List<BasicColumn> extraColumns, Consumer<QueryExpressionDSL<SelectModel>> extraStartExpression) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.selectOne(this::selectOne, generateSelectStart(tableInfo.getTableColumns(), sqlTable, extraColumns, extraStartExpression), completer);
    }

    default T selectOne(SelectDSLCompleter completer) {
        return selectOne(completer, null, null);
    }

    default T selectOne(FilterWithPageParam filterWithPageParam, Consumer<QueryExpressionDSL<SelectModel>> extraQueryExpression, List<BasicColumn> extraColumns, Consumer<QueryExpressionDSL<SelectModel>> extraStartExpression) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.selectOne(this::selectOne,
                generateSelectStart(tableInfo.getTableColumns(), sqlTable, extraColumns, extraStartExpression),
                c -> generateSelectCompleter(c, tableInfo.getTableColumns(), filterWithPageParam, extraQueryExpression));
    }

    default T selectOne(FilterWithPageParam filterWithPageParam) {
        return selectOne(filterWithPageParam, null, null, null);
    }

    default QueryExpressionDSL<SelectModel> generateSelectCompleter(QueryExpressionDSL<SelectModel> expressionDSL, List<TableColumn> tableColumns, FilterWithPageParam filterWithPageParam,
                                                                    Consumer<QueryExpressionDSL<SelectModel>> extraQueryExpression) {
        if (filterWithPageParam == null) return expressionDSL;
        List<AndOrCriteriaGroup> andCriteriaGroups = new ArrayList<>();
        for (FilterCondition condition : filterWithPageParam.getFilterConditions()) {
            TableColumn tableColumn = tableColumns.stream()
                    .filter(tc -> Objects.equals(tc.getIndex(), condition.getField()))
                    .findFirst().orElse(null);
            if (tableColumn == null) {
                throw new CustomException(500, String.format("The field %s does not exist in the entity", condition.getField()));
            }
            if (tableColumn.isFilterable()) {
                VisitableCondition vc = switch (condition.getCondition()) {
                    case isLike -> isLike(condition.getValue()).filter(Objects::nonNull).map(s -> "%" + s + "%");
                    case isIn -> isIn(condition.getValues());
                    case isBetween -> {
                        String[] values = condition.getValues();
                        yield isBetween(values[0]).and(values[1]);
                    }
                    case isGreaterThan -> isGreaterThan(condition.getValue());
                    case isGreaterThanOrEqualTo -> isGreaterThanOrEqualTo(condition.getValue());
                    case isLessThan -> isLessThan(condition.getValue());
                    case isLessThanOrEqualTo -> isLessThanOrEqualTo(condition.getValue());
                    default -> isEqualTo(condition.getValue());
                };
                AndOrCriteriaGroup andCriteriaGroup = and(tableColumn.getSqlColumn(), vc);
                andCriteriaGroups.add(andCriteriaGroup);
            }
        }
        if (!andCriteriaGroups.isEmpty()) {
            expressionDSL.where(andCriteriaGroups);
        }

        List<FilterWithPageParam.Sort> sorts = filterWithPageParam.getSorts();
        if (sorts != null && !sorts.isEmpty()) {
            List<SortSpecification> sortSpecifications = sorts.stream()
                    .map(sort -> {
                        TableColumn sortTableColumn = tableColumns.stream()
                                .filter(tc -> Objects.equals(tc.getIndex(), sort.getIndex()))
                                .findFirst().orElse(null);
                        if (sortTableColumn == null) {
                            throw new CustomException(500, String.format("The field %s does not exist in the entity", sort.getIndex()));
                        }
                        SortSpecification ss = sortColumn(sortTableColumn.getIndex());
                        if (sort.getOrder() == FilterWithPageParam.Order.desc) {
                            ss = ss.descending();
                        }
                        return ss;
                    }).toList();
            expressionDSL.orderBy(sortSpecifications);
        }
        extendSelectCompleter(expressionDSL);
        if (extraQueryExpression != null) {
            extraQueryExpression.accept(expressionDSL);
        }
        return expressionDSL;
    }

    default T selectOneByUniqueKeys(T t) {
        Class<?> entityClass = t.getClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        String[] uniqueKeys = tableInfo.getUniqueKeys();
        if (uniqueKeys != null && uniqueKeys.length > 0) {
            return MyBatis3Utils.selectOne(this::selectOne, generateSelectStart(tableInfo.getTableColumns(), sqlTable, null, null),
                    c -> {
                        List<TableColumn> tableColumns = tableInfo.getTableColumns();
                        List<AndOrCriteriaGroup> andCriteriaGroups = new ArrayList<>();
                        for (String uniqueKey : uniqueKeys) {
                            TableColumn tableColumn = tableColumns.stream()
                                    .filter(tc -> Objects.equals(tc.getIndex(), uniqueKey))
                                    .findFirst().orElse(null);
                            if (tableColumn == null) {
                                throw new CustomException(500, String.format("The unique key %s does not exist in the entity", uniqueKey));
                            }
                            Object fieldValue = CommonUtils.getFieldValue(entityClass, uniqueKey, t);
                            AndOrCriteriaGroup andCriteriaGroup = and(tableColumn.getSqlColumn(), isEqualTo(fieldValue));
                            andCriteriaGroups.add(andCriteriaGroup);
                        }
                        // 修改的情况要排除本身的那条数据
                        Object idValue = CommonUtils.getFieldValue(entityClass, tableInfo.getPrimaryKey(), t);
                        if (idValue != null) {
                            SqlColumn idColumn = getIdSqlColumn(tableInfo);
                            AndOrCriteriaGroup andCriteriaGroup = and(idColumn, isNotEqualTo(idValue));
                            andCriteriaGroups.add(andCriteriaGroup);
                        }
                        return c.where(andCriteriaGroups);
                    });
        }
        return null;
    }

    default List<T> selectListByUniqueKeys(List<T> t) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        String[] uniqueKeys = tableInfo.getUniqueKeys();
        if (uniqueKeys != null && uniqueKeys.length > 0) {
            return MyBatis3Utils.selectList(this::selectMany, generateSelectStart(tableInfo.getTableColumns(), sqlTable, null, null),
                    c -> {
                        List<TableColumn> tableColumns = tableInfo.getTableColumns();
                        // 如果只有一个唯一键，用in查询效率更高
                        if (uniqueKeys.length == 1) {
                            TableColumn tableColumn = tableColumns.stream()
                                    .filter(tc -> Objects.equals(tc.getIndex(), uniqueKeys[0]))
                                    .findFirst().orElse(null);
                            List<Object> fieldValues = new ArrayList<>();
                            List<Object> duplicateFieldValues = new ArrayList<>();
                            for (T value : t) {
                                Object fieldValue = CommonUtils.getFieldValue(entityClass, uniqueKeys[0], value);
                                if (!fieldValues.contains(fieldValue)) {
                                    fieldValues.add(fieldValue);
                                } else {
                                    duplicateFieldValues.add(fieldValue);
                                }
                            }
                            if (!duplicateFieldValues.isEmpty()) {
                                throw new CustomException(501, String.format("列表中有重复数据，对应唯一键值为%s", duplicateFieldValues.stream().map(Object::toString).collect(Collectors.joining("，"))));
                            }
                            return c.where(tableColumn.getSqlColumn(), isIn(fieldValues));
                        }
                        List<AndOrCriteriaGroup> orCriteriaGroups = new ArrayList<>();
                        List<HashMap<String, Object>> uniqueKeyValues = new ArrayList<>();
                        List<HashMap<String, Object>> duplicatedKeyValues = new ArrayList<>();
                        for (T value : t) {
                            List<AndOrCriteriaGroup> andCriteriaGroups = new ArrayList<>();
                            HashMap<String, Object> uniqueKeyValue = new HashMap<>();
                            for (String uniqueKey : uniqueKeys) {
                                TableColumn tableColumn = tableColumns.stream()
                                        .filter(tc -> Objects.equals(tc.getIndex(), uniqueKey))
                                        .findFirst().orElse(null);
                                Object fieldValue = CommonUtils.getFieldValue(entityClass, uniqueKey, value);
                                AndOrCriteriaGroup andCriteriaGroup = and(tableColumn.getSqlColumn(), isEqualTo(fieldValue));
                                andCriteriaGroups.add(andCriteriaGroup);
                                uniqueKeyValue.put(uniqueKey, fieldValue);
                            }
                            if (uniqueKeyValues.contains(uniqueKeyValue)) {
                                duplicatedKeyValues.add(uniqueKeyValue);
                            } else {
                                uniqueKeyValues.add(uniqueKeyValue);
                                AndOrCriteriaGroup orCriteriaGroup = or(andCriteriaGroups);
                                orCriteriaGroups.add(orCriteriaGroup);
                            }
                        }
                        if (!duplicatedKeyValues.isEmpty()) {
                            throw new CustomException(501, String.format("列表中有重复数据，对应唯一键为%s", duplicatedKeyValues.stream().map(HashMap::toString).collect(Collectors.joining("，"))));
                        }
                        return c.where(orCriteriaGroups);
                    });
        }
        return List.of();
    }

    default List<T> selectByIds(Long[] ids, List<BasicColumn> extraColumns, Consumer<QueryExpressionDSL<SelectModel>> extraStartExpression) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.selectList(this::selectMany,
                generateSelectStart(tableInfo.getTableColumns(), sqlTable, extraColumns, extraStartExpression),
                c -> {
                    SqlColumn idColumn = getIdSqlColumn(tableInfo);
                    return c.where(idColumn, isIn(ids));
                });
    }

    default List<T> selectByIds(Long[] ids) {
        return selectByIds(ids, null, null);
    }

    default T selectById(long id, List<BasicColumn> extraColumns, Consumer<QueryExpressionDSL<SelectModel>> extraStartExpression) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.selectOne(this::selectOne,
                generateSelectStart(tableInfo.getTableColumns(), sqlTable, extraColumns, extraStartExpression),
                c -> {
                    SqlColumn idColumn = getIdSqlColumn(tableInfo);
                    c.where(idColumn, isEqualTo(id));
                    return c;
                });
    }

    default T selectById(long id) {
        return selectById(id, null, null);
    }

    default int insertMultiple(List<T> tList) {
        if (tList.isEmpty()) {
            return 0;
        }
        T first = tList.get(0);
        Class<?> entityClass = first.getClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.insertMultiple(this::insertMultiple, tList, sqlTable, c -> {
                    for (TableColumn column : tableInfo.getTableColumns()) {
                        if (column.isInsertable()) {
                            c.map(column.getSqlColumn()).toProperty(column.getIndex());
                        }
                    }
                    return c;
                }
        );
    }

    default long count(List<FilterCondition> conditions, Consumer<CountDSL<SelectModel>> mapper) {
        Class<?> entityClass = getEntityClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        return MyBatis3Utils.countFrom(this::count, sqlTable, c -> generateCountCompleter(c, tableInfo.getTableColumns(), conditions, mapper));
    }

    default long count(List<FilterCondition> conditions) {
        return count(conditions, null);
    }

    default CountDSL<SelectModel> generateCountCompleter(CountDSL<SelectModel> expressionDSL, List<TableColumn> tableColumns, List<FilterCondition> conditions,
                                                         Consumer<CountDSL<SelectModel>> mapper) {
        if (conditions == null) return expressionDSL;
        List<AndOrCriteriaGroup> andCriteriaGroups = new ArrayList<>();
        for (int i = 0; i < conditions.size(); i++) {
            FilterCondition condition = conditions.get(i);
            TableColumn tableColumn = tableColumns.stream()
                    .filter(tc -> Objects.equals(tc.getIndex(), condition.getField()))
                    .findFirst().orElse(null);
            if (tableColumn == null) {
                throw new CustomException(500, String.format("The field %s does not exist in the entity", condition.getField()));
            }
            if (tableColumn.isFilterable()) {
                VisitableCondition vc = switch (condition.getCondition()) {
                    case isLike -> isLike(condition.getValue()).filter(Objects::nonNull).map(s -> "%" + s + "%");
                    case isIn -> isIn(condition.getValues());
                    case isBetween -> {
                        String[] values = condition.getValues();
                        yield isBetween(values[0]).and(values[1]);
                    }
                    case isGreaterThan -> isGreaterThan(condition.getValue());
                    case isGreaterThanOrEqualTo -> isGreaterThanOrEqualTo(condition.getValue());
                    case isLessThan -> isLessThan(condition.getValue());
                    case isLessThanOrEqualTo -> isLessThanOrEqualTo(condition.getValue());
                    default -> isEqualTo(condition.getValue());
                };
                AndOrCriteriaGroup andCriteriaGroup = and(tableColumn.getSqlColumn(), vc);
                andCriteriaGroups.add(andCriteriaGroup);
            }
        }
        if (!andCriteriaGroups.isEmpty()) {
            expressionDSL.where(andCriteriaGroups);
        }
        extendCountCompleter(expressionDSL);
        if (mapper != null) {
            mapper.accept(expressionDSL);
        }
        return expressionDSL;
    }

    default UpdateStatementProvider getUpdateStatementProvider(T t) {
        Class<?> entityClass = t.getClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        UpdateDSL<UpdateModel> updateDSL = SqlBuilder.update(sqlTable);
        String primaryKey = "id";
        SqlColumn idColumn = null;
        for (TableColumn column : tableInfo.getTableColumns()) {
            String property = column.getIndex();
            if (tableInfo.getPrimaryKey().equalsIgnoreCase(property)) {
                idColumn = column.getSqlColumn();
            } else {
                if (column.isUpdatable()) {
                    updateDSL.set(column.getSqlColumn()).equalToWhenPresent(CommonUtils.getFieldValue(entityClass, column.getIndex(), t));
                }
            }
        }
        updateDSL.where(idColumn, isEqualTo((Long) CommonUtils.getFieldValue(entityClass, primaryKey, t)));
        return updateDSL.build()
                .render(RenderingStrategies.MYBATIS3);
    }

    default BatchInsert<T> getBatchInsert(List<T> tList) {
        if (tList.isEmpty()) {
            return null;
        }
        T first = tList.get(0);
        Class<?> entityClass = first.getClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        BatchInsertDSL<T> batchInsertDSL = insertBatch(tList).into(sqlTable);
        for (TableColumn column : tableInfo.getTableColumns()) {
            if (column.isInsertable()) {
                batchInsertDSL.map(column.getSqlColumn()).toProperty(column.getIndex());
            }
        }
        return batchInsertDSL.build()
                .render(RenderingStrategies.MYBATIS3);
    }

    default InsertStatementProvider<T> getInsertStatementProvider(T t) {
        Class<?> entityClass = t.getClass();
        SqlTable sqlTable = getSqlTableFromEntity(entityClass);
        TableInfo tableInfo = generateTableInfo(entityClass, sqlTable);
        InsertDSL<T> insertDSL = SqlBuilder.insert(t).into(sqlTable);
        for (TableColumn column : tableInfo.getTableColumns()) {
            if (column.isInsertable()) {
                insertDSL.map(column.getSqlColumn()).toProperty(column.getIndex());
            }
        }
        return insertDSL.build()
                .render(RenderingStrategies.MYBATIS3);
    }

    default void extendStartExpression(QueryExpressionDSL<SelectModel> start) {
    }

    default void extendSelectCompleter(QueryExpressionDSL<SelectModel> selectCompleter) {
    }

    default void extendCountCompleter(CountDSL<SelectModel> countCompleter) {
    }

    default void addExtraColumns(List<BasicColumn> basicColumns) {
    }
}
