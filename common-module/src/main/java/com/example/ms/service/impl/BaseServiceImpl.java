package com.example.ms.service.impl;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.CacheManager;
import com.alicp.jetcache.RefreshPolicy;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.template.QuickConfig;
import com.example.ms.annotation.TableName;
import com.example.ms.mapper.BaseMapper;
import com.example.ms.mybatisdynamic.DefaultMultiRowUpdateStatementProvider;
import com.example.ms.mybatisdynamic.MultiRowUpdateStatementProvider;
import com.example.ms.service.BaseService;
import com.example.ms.utils.ExcelUtil;
import com.example.ms.utils.WebIdentityUtils;
import com.example.tools.component.JetCacheUtils;
import com.example.tools.entity.*;
import com.example.tools.component.IdentityUtils;
import com.example.tools.utils.CommonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.render.BatchInsert;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSL;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component
@Validated
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> implements BaseService<T> {
    @Autowired
    protected M mapper;

    @Autowired
    IdentityUtils identityUtils;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    protected CacheManager cacheManager;

    protected String cacheName;

    protected Cache<String, Object> cache;

    @Value("${token-expire}")
    private int tokenExpire;

    @Value("${token-refresh-expire}")
    private int tokenRefreshExpire;

    @Autowired
    private Validator validator;

    @Autowired
    WebIdentityUtils webIdentityUtils;

    @Autowired
    JetCacheUtils jetCacheUtils;

    protected boolean clearCacheAfterUpdate = true;

    @PostConstruct
    public void initCache() {
        if (StringUtils.isBlank(cacheName)) {
            return;
        }
        RefreshPolicy policy = RefreshPolicy.newPolicy(tokenExpire, TimeUnit.MINUTES)
                .stopRefreshAfterLastAccess(tokenRefreshExpire, TimeUnit.HOURS);
        AtomicLong value = new AtomicLong();
        QuickConfig config = QuickConfig.newBuilder(cacheName)
                .expire(Duration.ofMinutes(tokenRefreshExpire * 60L))
                .loader(k-> {
                    log.info("2" + k.getClass() + k);
                    return value.incrementAndGet();
                })
                .refreshPolicy(policy)
                .cacheType(CacheType.BOTH) // two level cache
                .syncLocal(true) // invalidate local cache in all jvm process after update
                .build();
        cache = cacheManager.getOrCreateCache(config);
    }

    @Override
    public ResponseData addNew(T t) {
        ResponseData responseData = new ResponseData();
        addOrEditPreProcess(t);
        T existT = mapper.selectOneByUniqueKeys(t);
        if (existT != null) {
            responseData.customError("数据已存在");
            return responseData;
        }
        webIdentityUtils.setCreateFields(t);
        mapper.insert(t);
        addOrEditAfterProcess(t);
        responseData.ok();
        return responseData;
    }

    @Override
    public ResponseData editById(T t) {
        ResponseData responseData = new ResponseData();
        addOrEditPreProcess(t);
        T existT = mapper.selectOneByUniqueKeys(t);
        if (existT != null) {
            responseData.customError("数据已存在");
            return responseData;
        }
        webIdentityUtils.setUpdateFields(t);
        mapper.updateById(t);
        addOrEditAfterProcess(t);
        responseData.ok();
        return responseData;
    }

    @Override
    public ResponseData removeById(long id) {
        ResponseData responseData = new ResponseData();
        mapper.deleteById(id);
        removeSingleAfterProcess(id);
        responseData.ok();
        return responseData;
    }

    @Override
    public ResponseData removeByIds(Long[] ids) {
        ResponseData responseData = new ResponseData();
        mapper.deleteByIds(ids);
        removeBatchAfterProcess(ids);
        responseData.ok();
        return responseData;
    }

    @Override
    public List<T> list(FilterWithPageParam filterWithPageParam) {
        if (filterWithPageParam != null) {
            setDefaultSort(filterWithPageParam);
        }
        return mapper.select(filterWithPageParam, this::extendSelectCompleter, getExtraColumns(), this::extendStartExpression);
    }

    @Override
    public ListWithPageData<T> page(FilterWithPageParam filterWithPageParam) {
        setDefaultSort(filterWithPageParam);
        List<T> list = new ArrayList<>();
        long count = mapper.count(filterWithPageParam.getFilterConditions());
        int pageCount = 0;
        if (count > 0) {
            int limit = filterWithPageParam.getPageSize();
            int offset = (filterWithPageParam.getIndex() - 1) * filterWithPageParam.getPageSize();
            pageCount = (int) Math.ceil((double) count / limit);
            list = mapper.select(filterWithPageParam, where -> {
                extendSelectCompleter(where);
                where.limit(limit).offset(offset);
            }, getExtraColumns(), this::extendStartExpression);
        }
        ListWithPageData<T> pageData = new ListWithPageData<>();
        pageData.setList(list);
        pageData.setPageCount(pageCount);
        pageData.setTotal(count);
        return pageData;
    }

    public void setDefaultSort(FilterWithPageParam filterWithPageParam) {
        if (filterWithPageParam.getSorts() == null || filterWithPageParam.getSorts().isEmpty()) {
            FilterWithPageParam.Sort sort = new FilterWithPageParam.Sort();
            sort.setIndex("updateTime");
            sort.setOrder(FilterWithPageParam.Order.desc);
            List<FilterWithPageParam.Sort> sorts = new ArrayList<>();
            sorts.add(sort);
            filterWithPageParam.setSorts(sorts);
        }
    }

    @Override
    public ResponseData addMultiple(List<T> tList) {
        ResponseData responseData = new ResponseData();
        List<T> existTs = mapper.selectListByUniqueKeys(tList);
        if (!existTs.isEmpty()) {
            responseData.customError("数据已存在");
            return responseData;
        }
        String username = webIdentityUtils.getCurrentUsername();
        tList.forEach(t -> identityUtils.setCreateFields(t, username));
        mapper.insertMultiple(tList);
        addOrEditMultipleAfterProcess(tList);
        responseData.ok();
        return responseData;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseData addBatch(List<T> tList, int batchSize, boolean existUpdate) {
        ResponseData responseData = new ResponseData();
        String username = webIdentityUtils.getCurrentUsername();
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(getClass(), BaseServiceImpl.class);
            M m = (M) sqlSession.getMapper(typeArguments[0]);
            int size = tList.size();
            int idxLimit = Math.min(batchSize, size);
            int i = 1;
            List<T> batchList = new ArrayList<>();
            Class<?> entityClass = typeArguments[1];
            String[] uniqueKeys;
            if (entityClass.isAnnotationPresent(TableName.class)) {
                uniqueKeys = entityClass.getAnnotation(TableName.class).uniqueKeys();
            } else {
                uniqueKeys = new String[]{};
            }
            List<T> updateList = new ArrayList<>();
            for (T element : tList) {
                identityUtils.setCreateFields(element, username);
                batchList.add(element);
                if (i == idxLimit) {
                    List<T> existTs = mapper.selectListByUniqueKeys(batchList);
                    if (!existTs.isEmpty()) {
                        if (existUpdate) {
                            batchList.forEach(t -> {
                                existTs.stream().filter(it -> {
                                    for (String uniqueKey : uniqueKeys) {
                                        if (!Objects.equals(CommonUtils.getFieldValue(uniqueKey, t), CommonUtils.getFieldValue(uniqueKey, it))) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }).findFirst().ifPresent(exist -> {
                                    CommonUtils.setFieldValue("id", t, CommonUtils.getFieldValue("id", exist));
                                    CommonUtils.setFieldValue("createName", t, null);
                                    CommonUtils.setFieldValue("createTime", t, null);
                                    updateList.add(t);
                                });
                            });
                            batchList.removeAll(updateList);
                        } else {
                            responseData.customError("数据已存在");
                            return responseData;
                        }
                    }
                    if (!batchList.isEmpty()) {
                        BatchInsert<T> batchInsert = m.getBatchInsert(batchList);
                        List<InsertStatementProvider<T>> insertStatementProviders = batchInsert.insertStatements();
                        insertStatementProviders.forEach(m::insert);
                        sqlSession.flushStatements();
                        batchList.clear();
                    }
                    idxLimit = Math.min(idxLimit + batchSize, size);
                }
                i++;
            }
            sqlSession.commit();
            if (!updateList.isEmpty()) {
                updateBatch(updateList);
            }
        }
        addOrEditMultipleAfterProcess(tList);
        responseData.ok();
        return responseData;
    }

    public ResponseData addBatch(List<T> tList, int batchSize) {
        return addBatch(tList, 1000, false);
    }

    public ResponseData addBatch(List<T> tList) {
        return addBatch(tList, 1000);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseData updateBatch(List<T> tList, int batchSize) {
        ResponseData responseData = new ResponseData();
        String username = webIdentityUtils.getCurrentUsername();
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(getClass(), BaseServiceImpl.class);
            M m = (M) sqlSession.getMapper(typeArguments[0]);
            int size = tList.size();
            int idxLimit = Math.min(batchSize, size);
            int i = 1;
            for (T element : tList) {
                identityUtils.setUpdateFields(element, username);
                UpdateStatementProvider updateStatementProvider = m.getUpdateStatementProvider(element);
                m.update(updateStatementProvider);
                if (i == idxLimit) {
                    sqlSession.flushStatements();
                    idxLimit = Math.min(idxLimit + batchSize, size);
                }
                i++;
            }
        }
        addOrEditMultipleAfterProcess(tList);
        responseData.ok();
        return responseData;
    }

    @Override
    public ResponseData updateBatch(List<T> tList) {
        return updateBatch(tList, 1000);
    }

    @Override
    public ResponseData updateMultiple(List<T> tList) {
        ResponseData responseData = new ResponseData();
        String username = webIdentityUtils.getCurrentUsername();
        List<Map<String, Object>> params = new ArrayList<>();
        List<String> updateStatementList = new ArrayList<>();
        int i = 0;
        for (T element : tList) {
            identityUtils.setUpdateFields(element, username);
            UpdateStatementProvider updateStatementProvider = mapper.getUpdateStatementProvider(element);
            updateStatementList.add(updateStatementProvider.getUpdateStatement().replace("parameters", String.format("parameters[%s]", i)));
            params.add(updateStatementProvider.getParameters());
            i++;
        }
        MultiRowUpdateStatementProvider multipleUpdateStatement = new DefaultMultiRowUpdateStatementProvider.Builder()
                .withUpdateStatement(String.join(";", updateStatementList))
                .withParameters(params)
                .build();
        mapper.updateMultiple(multipleUpdateStatement);
        responseData.ok();
        return responseData;
    }

    @Override
    public void exportExcel(HttpServletResponse response, FilterWithPageParam filterWithPageParam, TableInfo tableInfo) {
        List<T> tList = list(filterWithPageParam);
        if (tList.isEmpty()) {
            return;
        }
        ExcelUtil.listToExcel(tList, getFieldMap(tableInfo, true), tableInfo.getLabel(), response, this::exportPreprocess);
    }

    @Override
    public void downloadTemplate(HttpServletResponse response, TableInfo tableInfo) {
        ExcelUtil.listToExcel(List.of(), getFieldMap(tableInfo, false), tableInfo.getLabel(), response, null);
    }

    private List<TableColumn> getFieldMap(TableInfo tableInfo, boolean isExport) {
        if (tableInfo == null) {
            Class<?> entityClass = mapper.getEntityClass();
            SqlTable sqlTable = mapper.getSqlTableFromEntity(entityClass);
            tableInfo = mapper.generateTableInfo(entityClass, sqlTable);
        }
        return tableInfo.getTableColumns().stream()
                .filter(tableColumn -> (isExport && tableColumn.isExportable()) || (!isExport && tableColumn.isImportable()))
                .toList();
    }

    @SneakyThrows
    @Override
    public ResponseData importExcel(MultipartFile file, TableInfo tableInfo) {
        List<T> tList = ExcelUtil.excelToList(
                file.getInputStream(),
                "sheet",
                (Class<T>) mapper.getEntityClass(),
                getFieldMap(tableInfo, false),
                this::importPreprocess
        );
        ResponseData responseData = importPreprocess(tList);
        if (responseData.getCode() != 200) {
            return responseData;
        }
        responseData = addBatch(tList, 1000, tableInfo.isImportExistUpdate());
        if (responseData.getCode() != 200) {
            return responseData;
        }
        importAfterProcess(tList);
        return responseData;
    }

    protected void extendStartExpression(QueryExpressionDSL<SelectModel> start) {
    }

    protected void extendSelectCompleter(QueryExpressionDSL<SelectModel> selectCompleter) {
    }

    protected void extendCountCompleter(CountDSL<SelectModel> countCompleter) {
    }

    protected List<BasicColumn> getExtraColumns() {
        return null;
    }

    protected void exportPreprocess(T t) {
    }

    protected void importPreprocess(T t) {
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    protected ResponseData importPreprocess(List<T> tList) {
        ResponseData responseData = new ResponseData();
        responseData.ok();
        return responseData;
    }

    protected void importAfterProcess(List<T> tList) {
        clearCache();
    }

    protected void addOrEditPreProcess(T t) {
        clearCache();
    }

    protected void addOrEditAfterProcess(T t) {
        clearCache();
    }

    protected void addOrEditMultipleAfterProcess(List<T> tList) {
        clearCache();
    }

    protected void removeSingleAfterProcess(long id) {
        clearCache();
    }

    protected void removeBatchAfterProcess(Long[] ids) {
        clearCache();
    }

    protected void clearCache() {
        if (StringUtils.isBlank(cacheName) || !clearCacheAfterUpdate) {
            return;
        }
        jetCacheUtils.clearAllByName(cacheName, cache);
    }
}
