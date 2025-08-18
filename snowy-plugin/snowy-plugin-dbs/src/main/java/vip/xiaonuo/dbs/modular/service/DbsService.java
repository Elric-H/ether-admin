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
package vip.xiaonuo.dbs.modular.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dbs.modular.entity.DbsStorage;
import vip.xiaonuo.dbs.modular.param.*;
import vip.xiaonuo.dbs.modular.result.DbsTableColumnResult;
import vip.xiaonuo.dbs.modular.result.DbsTableResult;

import javax.sql.DataSource;
import java.util.List;

/**
 * 数据源Service接口
 *
 * @author xuyuxiang
 * @date 2022/6/8 19:40
 **/
public interface DbsService extends IService<DbsStorage> {

    /**
     * 获取数据源分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<DbsStorage> page(DbsStoragePageParam dbsStoragePageParam);

    /**
     * 添加数据源
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:48
     */
    void add(DbsStorageAddParam dbsStorageAddParam);

    /**
     * 编辑数据源
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:13
     */
    void edit(DbsStorageEditParam dbsStorageEditParam);

    /**
     * 删除数据源
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<DbsStorageIdParam> dbsStorageIdParamList);

    /**
     * 获取数据源详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DbsStorage detail(DbsStorageIdParam dbsStorageIdParam);

    /**
     * 获取数据源详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    DbsStorage queryEntity(String id);

    /* ====数据源部分所需要用到的选择器==== */

    /**
     * 获取数据库中所有表
     *
     * 返回结果：tableName：表名称，tableRemark：表注释
     *
     * @author xuyuxiang
     * @date 2022/6/8 19:46
     **/
    List<DbsTableResult> tables();

    /**
     * 获取数据库表中所有字段
     *
     * 返回结果：columnName：字段名称，columnRemark：字段注释
     *
     * @author xuyuxiang
     * @date 2022/6/8 19:46
     **/
    List<DbsTableColumnResult> tableColumns(DbsStorageTableColumnParam dbsStorageTableColumnParam);

    /**
     * 获取全部数据源列表
     *
     * @author xuyuxiang
     * @date 2022/7/19 18:49
     **/
    List<JSONObject> dbsSelector();

    /**
     * 获取数据源列表，只查询租户类型数据源
     *
     * @author xuyuxiang
     * @date 2022/7/19 18:49
     **/
    List<JSONObject> tenDbsSelector();

    /**
     * 获取默认的数据源名称
     *
     * @author xuyuxiang
     * @date 2022/3/11 14:25
     **/
    String getDefaultDataSourceName();

    /**
     * 获取当前正在使用的数据源名称
     *
     * @author xuyuxiang
     * @date 2022/3/8 16:31
     **/
    String getCurrentDataSourceName();

    /**
     * 获取当前正在使用的数据源ID
     *
     * @author 每天一点
     * @date 2022/8/5 14:01
     **/
    String getCurrentDataSourceId();

    /**
     * 获取当前正在使用的数据源
     *
     * @author xuyuxiang
     * @date 2022/3/8 16:31
     **/
    DataSource getCurrentDataSource();

    /**
     * 切换数据源
     *
     * @param name 数据源名称
     * @author xuyuxiang
     * @date 2022/3/8 16:31
     **/
    void changeDataSource(String name);

    /**
     * 切换到目标数据源并将数据源信息插入到目标数据源
     *
     * @param dbsId 数据源id
     * @author xuyuxiang
     * @date 2022/3/8 16:31
     **/
    void changeDataSourceAndInsertDbs(String dbsId);
}
