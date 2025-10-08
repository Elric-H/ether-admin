/*
 * Copyright [2022] [https://www.xiaonuo.vip]
 *
 * Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *
 * 1.请不要删除和修改根目录下的LICENSE文件。
 * 2.请不要删除和修改Snowy源码头部的版权声明。
 * 3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 * 4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 * 5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 * 6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package com.xuan.dbs.modular.service.impl;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xuan.common.enums.CommonSortOrderEnum;
import com.xuan.common.exception.CommonException;
import com.xuan.common.page.CommonPageRequest;
import com.xuan.dbs.modular.entity.DbsStorage;
import com.xuan.dbs.modular.enums.DbsCategoryEnum;
import com.xuan.dbs.modular.mapper.DbsMapper;
import com.xuan.dbs.modular.param.*;
import com.xuan.dbs.modular.result.DbsTableColumnResult;
import com.xuan.dbs.modular.result.DbsTableResult;
import com.xuan.dbs.modular.service.DbsService;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据源Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/6/8 19:47
 **/
@Service
public class DbsServiceImpl extends ServiceImpl<DbsMapper, DbsStorage> implements DbsService {

    /** 创建人 */
    private static final String CREATE_USER = "CREATE_USER";

    /** 创建时间 */
    private static final String CREATE_TIME = "CREATE_TIME";

    @Resource
    private DataSource dataSource;

    @Resource
    private DefaultDataSourceCreator defaultDataSourceCreator;

    @Override
    public Page<DbsStorage> page(DbsStoragePageParam dbsStoragePageParam) {
        QueryWrapper<DbsStorage> queryWrapper = new QueryWrapper<>();
        // 查询部分字段
        queryWrapper.lambda().select(DbsStorage::getId, DbsStorage::getPoolName, DbsStorage::getUrl,
                DbsStorage::getDriverName, DbsStorage::getCategory, DbsStorage::getSortCode);
        if(ObjectUtil.isNotEmpty(dbsStoragePageParam.getCategory())) {
            queryWrapper.lambda().eq(DbsStorage::getCategory, dbsStoragePageParam.getCategory());
        }
        if(ObjectUtil.isNotEmpty(dbsStoragePageParam.getSearchKey())) {
            queryWrapper.lambda().like(DbsStorage::getPoolName, dbsStoragePageParam.getSearchKey());
        }
        if(ObjectUtil.isAllNotEmpty(dbsStoragePageParam.getSortField(), dbsStoragePageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(dbsStoragePageParam.getSortOrder());
            queryWrapper.orderBy(true, dbsStoragePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(dbsStoragePageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(DbsStorage::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(DbsStorageAddParam dbsStorageAddParam) {
        checkParam(dbsStorageAddParam);
        DbsStorage dbsStorage = BeanUtil.toBean(dbsStorageAddParam, DbsStorage.class);
        // 执行保存
        this.save(dbsStorage);
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
        Map<String, DataSource> dataSources = dynamicRoutingDataSource.getDataSources();
        DataSource newDataSource;
        try {
            DataSourceProperty dataSourceProperty = new DataSourceProperty();
            BeanUtil.copyProperties(dbsStorage, dataSourceProperty, true);
            newDataSource = defaultDataSourceCreator.createDataSource(dataSourceProperty);
        } catch (Exception e) {
            throw new CommonException("无法连接该数据源：" + dbsStorage.getPoolName());
        }
        // 将数据源添加到动态数据源
        dataSources.put(dbsStorageAddParam.getPoolName(), newDataSource);
    }

    private void checkParam(DbsStorageAddParam dbsStorageAddParam) {
        DbsCategoryEnum.validate(dbsStorageAddParam.getCategory());
        // 数据源名称不可为master
        if(dbsStorageAddParam.getPoolName().toLowerCase().equals(getDefaultDataSourceName())) {
            throw new CommonException("数据源名称：{}不可使用", dbsStorageAddParam.getPoolName());
        }
        boolean hasSameDbs = this.count(new LambdaQueryWrapper<DbsStorage>()
                .eq(DbsStorage::getPoolName, dbsStorageAddParam.getPoolName())) > 0;
        if (hasSameDbs) {
            throw new CommonException("存在重复的数据源，名称为：{}", dbsStorageAddParam.getPoolName());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(DbsStorageEditParam dbsStorageEditParam) {
        DbsStorage dbsStorage = this.queryEntity(dbsStorageEditParam.getId());
        BeanUtil.copyProperties(dbsStorageEditParam, dbsStorage);
        // 执行更新
        this.updateById(dbsStorage);
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
        Map<String, DataSource> dataSources = dynamicRoutingDataSource.getDataSources();
        DataSource newDataSource;
        try {
            DataSourceProperty dataSourceProperty = new DataSourceProperty();
            BeanUtil.copyProperties(dbsStorage, dataSourceProperty, true);
            newDataSource = defaultDataSourceCreator.createDataSource(dataSourceProperty);
        } catch (Exception e) {
            throw new CommonException("无法连接该数据源：" + dbsStorage.getPoolName());
        }
        // 将数据源添加到动态数据源
        dataSources.put(dbsStorage.getPoolName(), newDataSource);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<DbsStorageIdParam> dbsStorageIdParamList) {
        List<String> dbsStorageIdList = CollStreamUtil.toList(dbsStorageIdParamList, DbsStorageIdParam::getId);
        if(ObjectUtil.isNotEmpty(dbsStorageIdList)) {
            // 获取数据源名称列表
            List<String> poolNameList = this.listByIds(dbsStorageIdList).stream().map(DbsStorage::getPoolName).collect(Collectors.toList());
            // 删除
            this.removeBatchByIds(dbsStorageIdList);
            poolNameList.forEach(poolName -> {
                // 移除对应的数据源
                DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
                Map<String, DataSource> dataSources = dynamicRoutingDataSource.getDataSources();
                dataSources.remove(poolName);
            });
        }
    }

    @Override
    public DbsStorage detail(DbsStorageIdParam dbsStorageIdParam) {
        return this.queryEntity(dbsStorageIdParam.getId());
    }

    @Override
    public DbsStorage queryEntity(String id) {
        DbsStorage dbsStorage = this.getById(id);
        if (ObjectUtil.isEmpty(dbsStorage)) {
            throw new CommonException("数据源不存在，id值为：{}", id);
        }
        return dbsStorage;
    }

    @Override
    public List<DbsTableResult> tables() {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = this.getCurrentDataSource().getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String url = metaData.getURL();
            String schema = null;
            if (url.toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<DbsTableResult> tables = new ArrayList<>();
            rs = metaData.getTables(null, schema, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME").toUpperCase();
                if (!tableName.startsWith("ACT_")) {
                    DbsTableResult dbsTableResult = new DbsTableResult();
                    dbsTableResult.setTableName(tableName);
                    String remarks = rs.getString("REMARKS");
                    if(ObjectUtil.isEmpty(remarks)) {
                        dbsTableResult.setTableRemark(tableName);
                    } else {
                        dbsTableResult.setTableRemark(remarks);
                    }
                    tables.add(dbsTableResult);
                }
            }
            return tables;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表失败");
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    @Override
    public List<DbsTableColumnResult> tableColumns(DbsStorageTableColumnParam dbsStorageTableColumnParam) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = this.getCurrentDataSource().getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String url = metaData.getURL();
            String schema = null;
            if (url.toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<DbsTableColumnResult> columns = new ArrayList<>();
            rs = metaData.getColumns(null, schema, dbsStorageTableColumnParam.getTableName(), "%");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME").toUpperCase();
                DbsTableColumnResult dbsTableColumnResult = new DbsTableColumnResult();
                dbsTableColumnResult.setColumnName(columnName);
                String remarks = rs.getString("REMARKS");
                if(ObjectUtil.isEmpty(remarks)) {
                    dbsTableColumnResult.setColumnRemark(columnName);
                } else {
                    dbsTableColumnResult.setColumnRemark(remarks);
                }
                columns.add(dbsTableColumnResult);
            }
            return columns;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表字段失败，表名称：{}", dbsStorageTableColumnParam.getTableName());
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    @Override
    public List<JSONObject> dbsSelector() {
        return this.list().stream().map(item -> JSONUtil.createObj().set("id", item.getId()).set("poolName", item.getPoolName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<JSONObject> tenDbsSelector() {
        // 只查询租户类型数据源
        return this.list(new LambdaQueryWrapper<DbsStorage>().eq(DbsStorage::getCategory, DbsCategoryEnum.SLAVE.getValue())).stream()
                .map(item -> JSONUtil.createObj().set("id", item.getId()).set("poolName", item.getPoolName())).collect(Collectors.toList());
    }

    @Override
    public String getDefaultDataSourceName() {
        return new DynamicDataSourceProperties().getPrimary();
    }

    @Override
    public String getCurrentDataSourceName() {
        String dataSourceName = DynamicDataSourceContextHolder.peek();
        if (ObjectUtil.isEmpty(dataSourceName)) {
            dataSourceName = new DynamicDataSourceProperties().getPrimary();
        }
        return dataSourceName;
    }

    @Override
    public String getCurrentDataSourceId() {
        String currentDataSourceName = this.getCurrentDataSourceName();
        if(getDefaultDataSourceName().equals(currentDataSourceName)){
            return getDefaultDataSourceName();
        } else {
            // 使用当前的数据源名称去查，如查到则使用（主租户的SLAVE数据源）
            QueryWrapper<DbsStorage> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(DbsStorage::getPoolName, currentDataSourceName);
            DbsStorage dbsStorage = this.getOne(queryWrapper);
            if(ObjectUtil.isEmpty(dbsStorage)) {
                // 查不到则直接使用master去查，查到即使用master（子租户的主数据源）
                queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(DbsStorage::getPoolName, getDefaultDataSourceName());
                dbsStorage = this.getOne(queryWrapper);
                if(ObjectUtil.isEmpty(dbsStorage)) {
                    // 还查不到则数据错误，子租户必须有主数据源
                    throw new CommonException("数据错误，无主数据源");
                }
            }
            return dbsStorage.getId();
        }

    }

    @Override
    public DataSource getCurrentDataSource() {
        String currentDataSourceName = this.getCurrentDataSourceName();
        DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
        return dynamicRoutingDataSource.getDataSource(currentDataSourceName);
    }

    @Override
    public void changeDataSource(String name) {
        DynamicDataSourceContextHolder.push(name);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeDataSourceAndInsertDbs(String dbsId) {
        // 获取该数据源
        DbsStorage dbsStorage = this.queryEntity(dbsId);
        // 获取数据源名称
        String poolName = dbsStorage.getPoolName();
        String dbsTableName;
        Object annotationValue = AnnotationUtil.getAnnotationValue(DbsStorage.class, TableName.class);
        if(ObjectUtil.isNotEmpty(annotationValue)) {
            dbsTableName = Convert.toStr(annotationValue);
        } else {
            dbsTableName = StrUtil.toUnderlineCase(DbsStorage.class.getSimpleName());
        }
        // 修改类别为MASTER
        dbsStorage.setCategory(DbsCategoryEnum.MASTER.getValue());
        // 修改名称为master
        dbsStorage.setPoolName(getDefaultDataSourceName());
        JSONObject dbsStorageObject = JSONUtil.parseObj(dbsStorage);
        List<String> tableColumnList = CollectionUtil.newArrayList();
        List<Object> tableDataList = CollectionUtil.newArrayList();
        dbsStorageObject.forEach((key, value) -> {
            String resultKey = StrUtil.toUnderlineCase(key).toUpperCase();
            tableColumnList.add(resultKey);
            if(resultKey.equals(CREATE_TIME) || resultKey.equals(CREATE_USER)) {
                tableDataList.add(null);
            } else {
                tableDataList.add("'" + value + "'");
            }
        });
        String sql = "INSERT INTO " + dbsTableName + "(" + StrUtil.join(StrUtil.COMMA, tableColumnList) + ") VALUES " +
                "(" + StrUtil.join(StrUtil.COMMA, tableDataList) + ");";
        // 执行sql插入该数据源
        this.handleSqlExecution(sql, poolName);
    }

    /**
     * 执行SQL
     *
     * @author xuyuxiang
     * @date 2022/6/16 10:24
     **/
    public void handleSqlExecution(String sql, String dbsName) {
        if(ObjectUtil.isNotEmpty(sql)) {
            Connection conn = null;
            try {
                // 使用指定的数据源名称
                DynamicRoutingDataSource dynamicRoutingDataSource = (DynamicRoutingDataSource) dataSource;
                DataSource tenDataSource = dynamicRoutingDataSource.getDataSource(dbsName);
                conn = tenDataSource.getConnection();
                conn.setAutoCommit(false);
                Connection finalConn = conn;
                StrUtil.split(sql.trim(), ";").forEach(sqlItem -> {
                    if(ObjectUtil.isNotEmpty(sqlItem)) {
                        try {
                            SqlExecutor.execute(finalConn, sqlItem);
                        } catch (SQLException e) {
                            throw new CommonException(e.getMessage());
                        }
                    }
                });
                conn.commit();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                throw new CommonException("SQL执行失败：" + sql);
            } finally {
                DbUtil.close(conn);
            }
        }
    }
}
