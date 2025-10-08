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
package com.xuan.flw.modular.model.controller;

import cn.hutool.core.lang.tree.Tree;
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
import com.xuan.flw.modular.model.entity.FlwModel;
import com.xuan.flw.modular.model.param.*;
import com.xuan.flw.modular.model.result.FlwModelOrgResult;
import com.xuan.flw.modular.model.result.FlwModelPositionResult;
import com.xuan.flw.modular.model.result.FlwModelRoleResult;
import com.xuan.flw.modular.model.result.FlwModelUserResult;
import com.xuan.flw.modular.model.service.FlwModelService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 模型控制器
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:34
 **/
@Api(tags = "模型控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 3)
@RestController
@Validated
public class FlwModeController {

    @Resource
    private FlwModelService flwModelService;

    /**
     * 获取模型分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取模型分页")
    @GetMapping("/flw/model/page")
    public CommonResult<Page<FlwModel>> page(FlwModelPageParam flwModelPageParam) {
        return CommonResult.data(flwModelService.page(flwModelPageParam));
    }

    /**
     * 获取所有模型列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取所有模型列表")
    @GetMapping("/flw/model/allList")
    public CommonResult<List<FlwModel>> allList(FlwModelListParam flwModelListParam) {
        return CommonResult.data(flwModelService.allList(flwModelListParam));
    }

    /**
     * 添加模型
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("添加模型")
    @CommonLog("添加模型")
    @PostMapping("/flw/model/add")
    public CommonResult<String> add(@RequestBody @Valid FlwModelAddParam flwModelAddParam) {
        flwModelService.add(flwModelAddParam);
        return CommonResult.ok();
    }

    /**
     * 编辑模型
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("编辑模型")
    @CommonLog("编辑模型")
    @PostMapping("/flw/model/edit")
    public CommonResult<String> edit(@RequestBody @Valid FlwModelEditParam flwModelEditParam) {
        flwModelService.edit(flwModelEditParam);
        return CommonResult.ok();
    }

    /**
     * 删除模型
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("删除模型")
    @CommonLog("删除模型")
    @PostMapping("/flw/model/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<FlwModelIdParam> flwModelIdParamList) {
        flwModelService.delete(flwModelIdParamList);
        return CommonResult.ok();
    }

    /**
     * 部署模型
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("部署模型")
    @CommonLog("部署模型")
    @PostMapping("/flw/model/deploy")
    public CommonResult<String> deploy(@RequestBody @Valid FlwModelIdParam flwModelIdParam) {
        flwModelService.deploy(flwModelIdParam);
        return CommonResult.ok();
    }

    /**
     * 获取模型详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("获取模型详情")
    @GetMapping("/flw/model/detail")
    public CommonResult<FlwModel> detail(@Valid FlwModelIdParam flwModelIdParam) {
        return CommonResult.data(flwModelService.detail(flwModelIdParam));
    }

    /**
     * 停用模型
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 8)
    @ApiOperation("停用模型")
    @CommonLog("停用模型")
    @PostMapping("/flw/model/disableModel")
    public CommonResult<String> disableModel(@RequestBody FlwModelIdParam flwModelIdParam) {
        flwModelService.disableModel(flwModelIdParam);
        return CommonResult.ok();
    }

    /**
     * 启用模型
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 9)
    @ApiOperation("启用模型")
    @CommonLog("启用模型")
    @PostMapping("/flw/model/enableModel")
    public CommonResult<String> enableModel(@RequestBody @Valid FlwModelIdParam flwModelIdParam) {
        flwModelService.enableModel(flwModelIdParam);
        return CommonResult.ok();
    }

    /**
     * 模型降版
     *
     * @author xuyuxiang
     * @date 2021/10/13 14:01
     **/
    @ApiOperationSupport(order = 10)
    @ApiOperation("模型降版")
    @CommonLog("模型降版")
    @PostMapping("/flw/model/downVersion")
    public CommonResult<String> downVersion(@RequestBody @Valid FlwModelIdParam flwModelIdParam) {
        flwModelService.downVersion(flwModelIdParam);
        return CommonResult.ok();
    }

    /* ====模型部分所需要用到的选择器==== */

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("获取组织树选择器")
    @GetMapping("/flw/model/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(flwModelService.orgTreeSelector());
    }

    /**
     * 获取组织列表选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 12)
    @ApiOperation("获取组织列表选择器")
    @GetMapping("/flw/model/orgListSelector")
    public CommonResult<Page<FlwModelOrgResult>> orgListSelector(FlwModelSelectorOrgListParam flwModelSelectorOrgListParam) {
        return CommonResult.data(flwModelService.orgListSelector(flwModelSelectorOrgListParam));
    }

    /**
     * 获取职位选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 13)
    @ApiOperation("获取职位选择器")
    @GetMapping("/flw/model/positionSelector")
    public CommonResult<Page<FlwModelPositionResult>> positionSelector(FlwModelSelectorPositionParam flwModelSelectorPositionParam) {
        return CommonResult.data(flwModelService.positionSelector(flwModelSelectorPositionParam));
    }

    /**
     * 获取角色选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 14)
    @ApiOperation("获取角色选择器")
    @GetMapping("/flw/model/roleSelector")
    public CommonResult<Page<FlwModelRoleResult>> roleSelector(FlwModelSelectorRoleParam flwModelSelectorRoleParam) {
        return CommonResult.data(flwModelService.roleSelector(flwModelSelectorRoleParam));
    }

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 15)
    @ApiOperation("获取用户选择器")
    @GetMapping("/flw/model/userSelector")
    public CommonResult<Page<FlwModelUserResult>> userSelector(FlwModelSelectorUserParam flwModelSelectorUserParam) {
        return CommonResult.data(flwModelService.userSelector(flwModelSelectorUserParam));
    }

    /**
     * 获取执行监听器选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 16)
    @ApiOperation("获取执行监听器选择器")
    @GetMapping("/flw/model/executionListenerSelector")
    public CommonResult<List<String>> executionListenerSelector() {
        return CommonResult.data(flwModelService.executionListenerSelector());
    }

    /**
     * 获取自定义事件执行监听器选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 17)
    @ApiOperation("获取自定义事件执行监听器选择器")
    @GetMapping("/flw/model/executionListenerSelectorForCustomEvent")
    public CommonResult<List<String>> executionListenerSelectorForCustomEvent() {
        return CommonResult.data(flwModelService.executionListenerSelectorForCustomEvent());
    }

    /**
     * 获取任务监听器选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 18)
    @ApiOperation("获取任务监听器选择器")
    @GetMapping("/flw/model/taskListenerSelector")
    public CommonResult<List<String>> taskListenerSelector() {
        return CommonResult.data(flwModelService.taskListenerSelector());
    }
}
