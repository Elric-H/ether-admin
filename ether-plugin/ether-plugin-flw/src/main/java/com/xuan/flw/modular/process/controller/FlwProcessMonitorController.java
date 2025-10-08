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
package com.xuan.flw.modular.process.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
import com.xuan.flw.modular.process.param.*;
import com.xuan.flw.modular.process.result.*;
import com.xuan.flw.modular.process.service.FlwProcessService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 流程监控控制器
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:50
 **/
@Api(tags = "流程控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 5)
@RestController
@Validated
public class FlwProcessMonitorController {

    @Resource
    private FlwProcessService flwProcessService;

    /**
     * 获取所有流程分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取所有流程分页")
    @SaCheckPermission("/flw/process/monitor/monitorPage")
    @GetMapping("/flw/process/monitor/monitorPage")
    public CommonResult<Page<FlwProcessResult>> monitorPage(FlwProcessMonitorPageParam flwProcessMonitorPageParam) {
        return CommonResult.data(flwProcessService.monitorPage(flwProcessMonitorPageParam));
    }

    /**
     * 删除流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("删除流程")
    @CommonLog("删除流程")
    @SaCheckPermission("/flw/process/monitor/delete")
    @PostMapping("/flw/process/monitor/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                   CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.delete(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 终止流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("终止流程")
    @CommonLog("终止流程")
    @SaCheckPermission("/flw/process/monitor/end")
    @PostMapping("/flw/process/monitor/end")
    public CommonResult<String> end(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.end(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 撤回流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("撤回流程")
    @CommonLog("撤回流程")
    @SaCheckPermission("/flw/process/monitor/revoke")
    @PostMapping("/flw/process/monitor/revoke")
    public CommonResult<String> revoke(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                            CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.revoke(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 挂起流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("挂起流程")
    @CommonLog("挂起流程")
    @SaCheckPermission("/flw/process/monitor/suspend")
    @PostMapping("/flw/process/monitor/suspend")
    public CommonResult<String> suspend(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.suspend(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 激活流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("激活流程")
    @CommonLog("激活流程")
    @SaCheckPermission("/flw/process/monitor/active")
    @PostMapping("/flw/process/monitor/active")
    public CommonResult<String> active(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.active(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 转办流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("转办流程")
    @CommonLog("转办流程")
    @SaCheckPermission("/flw/process/monitor/turn")
    @PostMapping("/flw/process/monitor/turn")
    public CommonResult<String> turn(@RequestBody @Valid FlwProcessTurnParam flwProcessTurnParam) {
        flwProcessService.turn(flwProcessTurnParam);
        return CommonResult.ok();
    }

    /**
     * 跳转流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("跳转流程")
    @CommonLog("跳转流程")
    @SaCheckPermission("/flw/process/monitor/jump")
    @PostMapping("/flw/process/monitor/jump")
    public CommonResult<String> jump(@RequestBody @Valid FlwProcessJumpParam flwProcessJumpParam) {
        flwProcessService.jump(flwProcessJumpParam);
        return CommonResult.ok();
    }

    /**
     * 复活流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("复活流程")
    @CommonLog("复活流程")
    @SaCheckPermission("/flw/process/monitor/restart")
    @PostMapping("/flw/process/monitor/restart")
    public CommonResult<String> restart(@RequestBody @Valid FlwProcessRestartParam flwProcessRestartParam) {
        flwProcessService.restart(flwProcessRestartParam);
        return CommonResult.ok();
    }

    /**
     * 迁移流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation("迁移流程")
    @CommonLog("迁移流程")
    @SaCheckPermission("/flw/process/monitor/migrate")
    @PostMapping("/flw/process/monitor/migrate")
    public CommonResult<String> migrate(@RequestBody @Valid FlwProcessMigrateParam flwProcessMigrateParam) {
        flwProcessService.migrate(flwProcessMigrateParam);
        return CommonResult.ok();
    }

    /**
     * 获取流程变量分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 10)
    @ApiOperation("获取流程变量分页")
    @SaCheckPermission("/flw/process/monitor/variablePage")
    @GetMapping("/flw/process/monitor/variablePage")
    public CommonResult<Page<FlwProcessVariableResult>> variablePage(@Valid FlwProcessIdParam flwProcessIdParam) {
        return CommonResult.data(flwProcessService.variablePage(flwProcessIdParam));
    }

    /**
     * 批量编辑流程变量
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("批量编辑流程变量")
    @SaCheckPermission("/flw/process/monitor/variableUpdateBatch")
    @PostMapping("/flw/process/monitor/variableUpdateBatch")
    public CommonResult<String> variableUpdateBatch(@RequestBody @Valid FlwProcessVariableUpdateParam flwProcessVariableUpdateParam) {
        flwProcessService.variableUpdateBatch(flwProcessVariableUpdateParam);
        return CommonResult.ok();
    }

    /**
     * 获取流程详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 12)
    @ApiOperation("获取流程详情")
    @SaCheckPermission("/flw/process/monitor/detail")
    @GetMapping("/flw/process/monitor/detail")
    public CommonResult<FlwProcessDetailResult> detail(@Valid FlwProcessIdParam flwProcessIdParam) {
        return CommonResult.data(flwProcessService.detail(flwProcessIdParam));
    }

    /**
     * 获取可跳转节点列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 13)
    @ApiOperation("获取可跳转节点列表")
    @SaCheckPermission("/flw/process/monitor/getCanJumpNodeInfoList")
    @GetMapping("/flw/process/monitor/getCanJumpNodeInfoList")
    public CommonResult<List<FlwProcessJumpNodeResult>> getCanJumpNodeInfoList(@Valid FlwProcessIdParam flwProcessIdParam) {
        return CommonResult.data(flwProcessService.getCanJumpNodeInfoList(flwProcessIdParam));
    }

    /**
     * 获取可复活到节点列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 14)
    @ApiOperation("获取可复活到节点列表")
    @SaCheckPermission("/flw/process/monitor/getCanRestartNodeInfoList")
    @GetMapping("/flw/process/monitor/getCanRestartNodeInfoList")
    public CommonResult<List<FlwProcessRestartNodeResult>> getCanRestartNodeInfoList(@Valid FlwProcessIdParam flwProcessIdParam) {
        return CommonResult.data(flwProcessService.getCanRestartNodeInfoList(flwProcessIdParam));
    }

    /**
     * 获取可迁移到节点列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 15)
    @ApiOperation("获取可迁移到节点列表")
    @SaCheckPermission("/flw/process/monitor/getCanMigrateNodeInfoList")
    @GetMapping("/flw/process/monitor/getCanMigrateNodeInfoList")
    public CommonResult<List<FlwProcessRestartNodeResult>> getCanMigrateNodeInfoList(@Valid FlwProcessIdParam flwProcessIdParam) {
        return CommonResult.data(flwProcessService.getCanMigrateNodeInfoList(flwProcessIdParam));
    }

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 16)
    @ApiOperation("获取组织树选择器")
    @SaCheckPermission("/flw/process/monitor/orgTreeSelector")
    @GetMapping("/flw/process/monitor/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(flwProcessService.orgTreeSelector());
    }

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 17)
    @ApiOperation("获取用户选择器")
    @SaCheckPermission("/flw/process/monitor/userSelector")
    @GetMapping("/flw/process/monitor/userSelector")
    public CommonResult<Page<FlwProcessUserResult>> userSelector(FlwProcessSelectorUserParam flwProcessSelectorUserParam) {
        return CommonResult.data(flwProcessService.userSelector(flwProcessSelectorUserParam));
    }
}
