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
package com.xuan.dev.modular.dfc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.xuan.common.annotation.CommonLog;
import com.xuan.common.pojo.CommonResult;
import com.xuan.common.pojo.CommonValidList;
import com.xuan.dev.modular.dfc.entity.DevDfc;
import com.xuan.dev.modular.dfc.param.*;
import com.xuan.dev.modular.dfc.result.DevDfcDbsSelectorResult;
import com.xuan.dev.modular.dfc.result.DevDfcTableColumnResult;
import com.xuan.dev.modular.dfc.result.DevDfcTableResult;
import com.xuan.dev.modular.dfc.service.DevDfcService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 动态字段配置控制器
 *
 * @author 每天一点
 * @date  2023/08/04 08:18
 */
@Api(tags = "动态字段配置控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class DevDfcController {

    @Resource
    private DevDfcService devDfcService;

    /**
     * 获取动态字段配置分页
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取动态字段配置分页")
    @GetMapping("/dev/dfc/page")
    public CommonResult<Page<DevDfc>> page(DevDfcPageParam devDfcPageParam) {
        return CommonResult.data(devDfcService.page(devDfcPageParam));
    }

    /**
     * 添加动态字段配置
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("添加动态字段配置")
    @CommonLog("添加动态字段配置")
    @PostMapping("/dev/dfc/add")
    public CommonResult<String> add(@RequestBody @Valid DevDfcAddParam devDfcAddParam) {
        devDfcService.add(devDfcAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑动态字段配置
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("编辑动态字段配置")
    @CommonLog("编辑动态字段配置")
    @PostMapping("/dev/dfc/edit")
    public CommonResult<String> edit(@RequestBody @Valid DevDfcEditParam devDfcEditParam) {
        devDfcService.edit(devDfcEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除动态字段配置
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("删除动态字段配置")
    @CommonLog("删除动态字段配置")
    @PostMapping("/dev/dfc/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<DevDfcIdParam> devDfcIdParamList) {
        devDfcService.delete(devDfcIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取动态字段配置详情
     *
     * @author 每天一点
     * @date  2023/08/04 08:18
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取动态字段配置详情")
    @GetMapping("/dev/dfc/detail")
    public CommonResult<DevDfc> detail(@Valid DevDfcIdParam devDfcIdParam) {
        return CommonResult.data(devDfcService.detail(devDfcIdParam));
    }

    /**
     * 获取所有数据源信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("获取所有数据源信息")
    @GetMapping("/dev/dfc/dbsSelector")
    public CommonResult<List<DevDfcDbsSelectorResult>> dbsSelector() {
        return CommonResult.data(devDfcService.dbsSelector());
    }

    /**
     * 根据数据源id获取对应库所有表信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("根据数据源id获取对应库所有表信息")
    @GetMapping("/dev/dfc/tablesByDbsId")
    public CommonResult<List<DevDfcTableResult>> tablesByDbsId(@Valid DevDfcDbsTableParam devDfcDbsTableParam) {
        return CommonResult.data(devDfcService.tablesByDbsId(devDfcDbsTableParam));
    }

    /**
     * 获取当前库数据表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("获取当前库数据表内所有字段信息")
    @GetMapping("/dev/dfc/tableColumns")
    public CommonResult<List<DevDfcTableColumnResult>> tableColumns(@Valid DevDfcTableColumnParam genBasicTableColumnParam) {
        return CommonResult.data(devDfcService.tableColumns(genBasicTableColumnParam));
    }

    /**
     * 根据数据源id获取对应库数据表内所有字段信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation("根据数据源id获取对应库数据表内所有字段信息")
    @GetMapping("/dev/dfc/tableColumnsByDbsId")
    public CommonResult<List<DevDfcTableColumnResult>> tableColumnsByDbsId(@Valid DevDfcDbsTableColumnParam dbsTableColumnParam) {
        return CommonResult.data(devDfcService.tableColumnsByDbsId(dbsTableColumnParam));
    }

    /**
     * 获取当前库所有表信息
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 10)
    @ApiOperation("获取当前库所有表信息")
    @GetMapping("/dev/dfc/tables")
    public CommonResult<List<DevDfcTableResult>> tables() {
        return CommonResult.data(devDfcService.tables());
    }

    /**
     * 迁移数据
     *
     * @author 每天一点
     * @date 2023/8/7 22:53
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("迁移数据")
    @PostMapping("/dev/dfc/migrate")
    public CommonResult<String> migrate(@RequestBody @Valid DevDfcMigrateParam devDfcMigrateParam) {
        devDfcService.migrate(devDfcMigrateParam);
        return CommonResult.ok();
    }

}
