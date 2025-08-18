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
package vip.xiaonuo.dev.modular.dfc.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.dev.modular.dfc.entity.DevDfc;
import vip.xiaonuo.dev.modular.dfc.param.*;
import vip.xiaonuo.dev.modular.dfc.result.DevDfcDbsSelectorResult;
import vip.xiaonuo.dev.modular.dfc.result.DevDfcTableColumnResult;
import vip.xiaonuo.dev.modular.dfc.result.DevDfcTableResult;

import java.util.List;

/**
 * 动态字段配置Service接口
 *
 * @author 每天一点
 * @date  2023/08/04 08:18
 **/
public interface DevDfcService extends IService<DevDfc> {

    /**
     * 获取动态字段配置分页
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    Page<DevDfc> page(DevDfcPageParam devDfcPageParam);

    /**
     * 添加动态字段配置
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    void add(DevDfcAddParam devDfcAddParam);

    /**
     * 编辑动态字段配置
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    void edit(DevDfcEditParam devDfcEditParam);

    /**
     * 删除动态字段配置
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    void delete(List<DevDfcIdParam> devDfcIdParamList);

    /**
     * 获取动态字段配置详情
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    DevDfc detail(DevDfcIdParam devDfcIdParam);

    /**
     * 获取动态字段配置详情
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     **/
    DevDfc queryEntity(String id);

    /**
     * 获取所有数据源信息
     *
     * @author xuyuxiang
     * @date 2023/2/1 10:43
     **/
    List<DevDfcDbsSelectorResult> dbsSelector();

    /**
     * 获取所有表信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<DevDfcTableResult> tablesByDbsId(DevDfcDbsTableParam devDfcDbsTableParam);

    /**
     * 获取所有表信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<DevDfcTableResult> tables();

    /**
     * 获取表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<DevDfcTableColumnResult> tableColumns(DevDfcTableColumnParam genBasicTableColumnParam);

    /**
     * 获取表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/10/25 22:33
     **/
    List<DevDfcTableColumnResult> tableColumnsByDbsId(DevDfcDbsTableColumnParam dbsTableColumnParam);

    /**
     * 获取表中字段配置
     *
     * @author 每天一点
     * @date  2023/08/02 21:31
     */
    List<JSONObject> getTableFieldList(String dbsId, String tableName);

    /**
     * 迁移数据
     *
     * @author 每天一点
     * @date  2023/08/02 21:31
     */
    void migrate(DevDfcMigrateParam devDfcMigrateParam);
}
