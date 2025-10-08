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
package com.xuan.dev.modular.dfc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xuan.common.enums.CommonSortOrderEnum;
import com.xuan.common.exception.CommonException;
import com.xuan.common.page.CommonPageRequest;
import com.xuan.dbs.api.DbsApi;
import com.xuan.dev.modular.dfc.entity.DevDfc;
import com.xuan.dev.modular.dfc.enums.DevDfcEnum;
import com.xuan.dev.modular.dfc.mapper.DevDfcMapper;
import com.xuan.dev.modular.dfc.param.*;
import com.xuan.dev.modular.dfc.result.DevDfcDbsSelectorResult;
import com.xuan.dev.modular.dfc.result.DevDfcTableColumnResult;
import com.xuan.dev.modular.dfc.result.DevDfcTableResult;
import com.xuan.dev.modular.dfc.service.DevDfcService;
import com.xuan.ten.api.TenApi;

import javax.annotation.Resource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 动态字段配置Service接口实现类
 *
 * @author 每天一点
 * @date  2023/08/04 08:18
 **/
@Service
public class DevDfcServiceImpl extends ServiceImpl<DevDfcMapper, DevDfc> implements DevDfcService {

    private static final String DB_URL_KEY = "spring.datasource.dynamic.datasource.master.url";

    private static final String DB_USERNAME_KEY = "spring.datasource.dynamic.datasource.master.username";

    private static final String DB_PASSWORD_KEY = "spring.datasource.dynamic.datasource.master.password";

    @Resource
    private Environment environment;

    @Resource
    private DbsApi dbsApi;

    @Resource
    private TenApi tenApi;

    @Override
    public Page<DevDfc> page(DevDfcPageParam devDfcPageParam) {
        QueryWrapper<DevDfc> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(devDfcPageParam.getDbsId())) {
            queryWrapper.lambda().eq(DevDfc::getDbsId, devDfcPageParam.getDbsId());
        }
        if(ObjectUtil.isNotEmpty(devDfcPageParam.getTableName())) {
            queryWrapper.lambda().eq(DevDfc::getTableName, devDfcPageParam.getTableName());
        }
        if(ObjectUtil.isAllNotEmpty(devDfcPageParam.getSortField(), devDfcPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(devDfcPageParam.getSortOrder());
            queryWrapper.orderBy(true, devDfcPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(devDfcPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByAsc(DevDfc::getDbsId).orderByAsc(DevDfc::getTableName).orderByAsc(DevDfc::getSortCode);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(DevDfcAddParam devDfcAddParam) {
        DevDfc devDfc = BeanUtil.toBean(devDfcAddParam, DevDfc.class);
        boolean repeatName = this.count(new LambdaQueryWrapper<DevDfc>()
                .eq(DevDfc::getDbsId, devDfc.getDbsId())
                .eq(DevDfc::getTableName, devDfc.getTableName())
                .eq(DevDfc::getName, devDfc.getName())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的表单域属性名，表单域属性名为：{}", devDfc.getName());
        }
        devDfc.setStatus(DevDfcEnum.ENABLE.getValue());
        this.save(devDfc);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(DevDfcEditParam devDfcEditParam) {
        DevDfc devDfc = this.queryEntity(devDfcEditParam.getId());
        BeanUtil.copyProperties(devDfcEditParam, devDfc);
        boolean repeatName = this.count(new LambdaQueryWrapper<DevDfc>()
                .eq(DevDfc::getDbsId, devDfc.getDbsId())
                .eq(DevDfc::getTableName, devDfc.getTableName())
                .eq(DevDfc::getName, devDfc.getName())
                .ne(DevDfc::getId, devDfc.getId())) > 0;
        if(repeatName) {
            throw new CommonException("存在重复的表单域属性名，表单域属性名为：{}", devDfc.getName());
        }
        this.updateById(devDfc);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<DevDfcIdParam> devDfcIdParamList) {
        // 执行删除
        this.removeByIds(CollStreamUtil.toList(devDfcIdParamList, DevDfcIdParam::getId));
    }

    @Override
    public DevDfc detail(DevDfcIdParam devDfcIdParam) {
        return this.queryEntity(devDfcIdParam.getId());
    }

    @Override
    public DevDfc queryEntity(String id) {
        DevDfc devDfc = this.getById(id);
        if(ObjectUtil.isEmpty(devDfc)) {
            throw new CommonException("动态字段配置不存在，id值为：{}", id);
        }
        return devDfc;
    }

    @Override
    public List<DevDfcDbsSelectorResult> dbsSelector() {
        // 如果是主租户，则无其他数据源可选
        if(tenApi.getCurrentTenId().equals(tenApi.getDefaultTenId())) {
            return CollectionUtil.newArrayList();
        } else {
            // 否则查询其所有数据源（子租户只会有且必有一条数据源）
            return dbsApi.dbsSelector().stream()
                    .map(jsonObject -> JSONUtil.toBean(jsonObject, DevDfcDbsSelectorResult.class)).collect(Collectors.toList());
        }
    }

    @Override
    public List<DevDfcTableResult> tablesByDbsId(DevDfcDbsTableParam devDfcDbsTableParam) {
        JSONObject jsonObject = dbsApi.dbsDetail(devDfcDbsTableParam.getDbsId());
        String poolName = jsonObject.getStr("poolName");
        String url = jsonObject.getStr("url");
        String userName = jsonObject.getStr("username");
        String password = jsonObject.getStr("password");
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("数据源{}配置信息不完整", poolName);
        }
        return queryTables(url, userName, password);
    }

    @Override
    public List<DevDfcTableResult> tables() {
        String url = environment.getProperty(DB_URL_KEY);
        String userName = environment.getProperty(DB_USERNAME_KEY);
        String password = environment.getProperty(DB_PASSWORD_KEY);
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("当前数据源配置信息不完整");
        }
        return this.queryTables(url, userName, password);
    }

    @Override
    public List<DevDfcTableColumnResult> tableColumns(DevDfcTableColumnParam genBasicTableColumnParam) {
        String url = environment.getProperty(DB_URL_KEY);
        String userName = environment.getProperty(DB_USERNAME_KEY);
        String password = environment.getProperty(DB_PASSWORD_KEY);
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("当前数据源配置信息不完整");
        }
        return this.queryTableColumns(url, userName, password, genBasicTableColumnParam.getTableName());
    }

    @Override
    public List<DevDfcTableColumnResult> tableColumnsByDbsId(DevDfcDbsTableColumnParam dbsTableColumnParam) {
        JSONObject jsonObject = dbsApi.dbsDetail(dbsTableColumnParam.getDbsId());
        String poolName = jsonObject.getStr("poolName");
        String url = jsonObject.getStr("url");
        String userName = jsonObject.getStr("username");
        String password = jsonObject.getStr("password");
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("数据源{}配置信息不完整", poolName);
        }
        return queryTableColumns(url, userName, password, dbsTableColumnParam.getTableName());
    }

    @Override
    public List<JSONObject> getTableFieldList(String dbsId, String tableName) {
        QueryWrapper<DevDfc> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(dbsId)) {
            queryWrapper.lambda().eq(DevDfc::getDbsId, dbsId);
        }
        if(ObjectUtil.isNotEmpty(tableName)) {
            queryWrapper.lambda().eq(DevDfc::getTableName, tableName);
        }
        queryWrapper.lambda().eq(DevDfc::getStatus, DevDfcEnum.ENABLE.getValue());
        queryWrapper.lambda().orderByAsc(DevDfc::getSortCode);
        return this.list(queryWrapper)
                .stream()
                .map(item -> JSONUtil.createObj()
                        .set("name", item.getName())
                        .set("label", item.getLabel())
                        .set("type", item.getType())
                        .set("required", item.getRequired())
                        .set("placeholder", item.getPlaceholder())
                        .set("selectOptionType", item.getSelectOptionType())
                        .set("dictTypeCode", item.getDictTypeCode())
                        .set("selOptionApiUrl", item.getSelOptionApiUrl())
                        .set("selDataApiUrl", item.getSelDataApiUrl())
                        .set("isMultiple", item.getIsMultiple())
                )
                .collect(Collectors.toList());
    }

    @Override
    public void migrate(DevDfcMigrateParam devDfcMigrateParam) {
        String url = environment.getProperty(DB_URL_KEY);
        String userName = environment.getProperty(DB_USERNAME_KEY);
        String password = environment.getProperty(DB_PASSWORD_KEY);
        if (!"master".equals(devDfcMigrateParam.getDbsId())){
            JSONObject jsonObject = dbsApi.dbsDetail(devDfcMigrateParam.getDbsId());
            url = jsonObject.getStr("url");
            userName = jsonObject.getStr("username");
            password = jsonObject.getStr("password");
        }
        if(ObjectUtil.hasEmpty(url, userName, password)) {
            throw new CommonException("当前数据源配置信息不完整");
        }
        this.migrateData(url, userName, password, devDfcMigrateParam);
    }
    /**
     * 迁移数据
     *
     * @author 每天一点
     * @date 2023/2/1 10:31
     **/
    private void migrateData(String url, String userName, String password, DevDfcMigrateParam devDfcMigrateParam){
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, userName, password);
            DevDfc devDfc = this.getById(devDfcMigrateParam.getId());

            boolean executeUpdateSql = false;
            PreparedStatement preparedStatement = conn.prepareStatement(StrUtil.format("SELECT COUNT(*) FROM {} WHERE {} IS NOT NULL", devDfc.getTableName(), devDfcMigrateParam.getTableColumn()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getInt(1) <= 0){
                    executeUpdateSql = true;
                }
            }
            if(!executeUpdateSql) {
                preparedStatement.close();
                conn.close();
                throw new CommonException("目标字段存在数据，终止数据迁移");
            }

            if (url.toLowerCase().contains("jdbc:mysql")) {
                preparedStatement = conn.prepareStatement(StrUtil.format("UPDATE {} SET {} = JSON_UNQUOTE(JSON_EXTRACT(EXT_JSON, '$.{}'))", devDfc.getTableName(), devDfcMigrateParam.getTableColumn(), devDfc.getName()));
            }
            if (!url.toLowerCase().contains("jdbc:mysql")) {
                preparedStatement = conn.prepareStatement(StrUtil.format("UPDATE {} SET {} = JSON_VALUE(EXT_JSON, '$.{}')", devDfc.getTableName(), devDfcMigrateParam.getTableColumn(), devDfc.getName()));
            }
            preparedStatement.executeUpdate();
            preparedStatement.close();
            conn.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表失败");
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }

    /**
     * 查询指定数据源中的所有表
     *
     * @author xuyuxiang
     * @date 2023/2/1 10:31
     **/
    private List<DevDfcTableResult> queryTables(String url, String userName, String password) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, userName, password);
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = null;
            if (metaData.getURL().toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<DevDfcTableResult> tables = new ArrayList<>();
            rs = metaData.getTables(null, schema, "%", new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!StrUtil.startWithIgnoreCase(tableName, "ACT_")
                        && !StrUtil.startWithIgnoreCase(tableName, "CLIENT_")
                        && !StrUtil.startWithIgnoreCase(tableName, "DEV_")
                        && !StrUtil.startWithIgnoreCase(tableName, "EXT_")
                        && !StrUtil.startWithIgnoreCase(tableName, "GEN_")
                        && !StrUtil.startWithIgnoreCase(tableName, "MOBILE_")
                        && !StrUtil.startWithIgnoreCase(tableName, "PAY_")
                        && !StrUtil.startWithIgnoreCase(tableName, "AUTH_")
                ) {
                    DevDfcTableResult genBasicTableResult = new DevDfcTableResult();
                    genBasicTableResult.setTableName(tableName);
                    String remarks = rs.getString("REMARKS");
                    if(ObjectUtil.isEmpty(remarks)) {
                        genBasicTableResult.setTableRemark(tableName);
                    } else {
                        genBasicTableResult.setTableRemark(remarks);
                    }
                    tables.add(genBasicTableResult);
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

    /**
     * 查询指定数据源中指定表的所有字段
     *
     * @author xuyuxiang
     * @date 2023/2/1 11:09
     **/
    private List<DevDfcTableColumnResult> queryTableColumns(String url, String userName, String password, String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = DriverManager.getConnection(url, userName, password);
            DatabaseMetaData metaData = conn.getMetaData();
            String schema = null;
            if (metaData.getURL().toLowerCase().contains("jdbc:oracle")) {
                schema = metaData.getUserName();
            }
            List<DevDfcTableColumnResult> columns = new ArrayList<>();
            rs = metaData.getColumns(null, schema, tableName, "%");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME").toUpperCase();
                DevDfcTableColumnResult devDfcTableColumnResult = new DevDfcTableColumnResult();
                devDfcTableColumnResult.setColumnName(columnName);
                String remarks = rs.getString("REMARKS");
                if(ObjectUtil.isEmpty(remarks)) {
                    devDfcTableColumnResult.setColumnRemark(columnName);
                } else {
                    devDfcTableColumnResult.setColumnRemark(remarks);
                }
                String typeName = rs.getString("TYPE_NAME").toUpperCase();
                if(ObjectUtil.isEmpty(typeName)) {
                    devDfcTableColumnResult.setTypeName("NONE");
                } else {
                    devDfcTableColumnResult.setTypeName(typeName);
                }
                columns.add(devDfcTableColumnResult);
            }
            return columns;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            throw new CommonException("获取数据库表字段失败，表名称：{}", tableName);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeConnection(conn);
        }
    }
}
