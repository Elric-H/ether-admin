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
package vip.xiaonuo.flw.modular.task.controller;

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
import vip.xiaonuo.common.annotation.CommonLog;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.flw.modular.task.param.*;
import vip.xiaonuo.flw.modular.task.result.*;
import vip.xiaonuo.flw.modular.task.service.FlwTaskService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 任务控制器
 *
 * @author xuyuxiang
 * @date 2022/5/22 15:31
 */
@Api(tags = "任务控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 6)
@RestController
@Validated
public class FlwTaskController {

    @Resource
    private FlwTaskService flwTaskService;

    /**
     * 获取待办任务分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取待办任务分页")
    @GetMapping("/flw/task/todoPage")
    public CommonResult<Page<FlwTodoTaskResult>> todoPage(FlwTaskPageTodoParam flwTaskPageTodoParam) {
        return CommonResult.data(flwTaskService.todoPage(flwTaskPageTodoParam));
    }

    /**
     * 获取已办任务分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取已办任务分页")
    @GetMapping("/flw/task/donePage")
    public CommonResult<Page<FlwDoneTaskResult>> donePage(FlwTaskPageDoneParam flwTaskPageDoneParam) {
        return CommonResult.data(flwTaskService.donePage(flwTaskPageDoneParam));
    }

    /**
     * 调整申请
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("调整申请")
    @CommonLog("调整申请")
    @PostMapping("/flw/task/adjust")
    public CommonResult<String> adjust(@RequestBody @Valid FlwTaskAdjustParam flwTaskAdjustParam) {
        flwTaskService.adjust(flwTaskAdjustParam);
        return CommonResult.ok();
    }

    /**
     * 审批保存
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("审批保存")
    @CommonLog("审批保存")
    @PostMapping("/flw/task/save")
    public CommonResult<String> save(@RequestBody @Valid FlwTaskSaveParam flwTaskSaveParam) {
        flwTaskService.save(flwTaskSaveParam);
        return CommonResult.ok();
    }

    /**
     * 审批同意
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("审批同意")
    @CommonLog("审批同意")
    @PostMapping("/flw/task/pass")
    public CommonResult<String> pass(@RequestBody @Valid FlwTaskPassParam flwTaskPassParam) {
        flwTaskService.pass(flwTaskPassParam);
        return CommonResult.ok();
    }

    /**
     * 审批拒绝
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("审批拒绝")
    @CommonLog("审批拒绝")
    @PostMapping("/flw/task/reject")
    public CommonResult<String> reject(@RequestBody @Valid FlwTaskRejectParam flwTaskRejectParam) {
        flwTaskService.reject(flwTaskRejectParam);
        return CommonResult.ok();
    }

    /**
     * 审批退回
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("审批退回")
    @CommonLog("审批退回")
    @PostMapping("/flw/task/back")
    public CommonResult<String> back(@RequestBody @Valid FlwTaskBackParam flwTaskBackParam) {
        flwTaskService.back(flwTaskBackParam);
        return CommonResult.ok();
    }

    /**
     * 任务转办
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("任务转办")
    @CommonLog("任务转办")
    @PostMapping("/flw/task/turn")
    public CommonResult<String> turn(@RequestBody @Valid FlwTaskTurnParam flwTaskTurnParam) {
        flwTaskService.turn(flwTaskTurnParam, true);
        return CommonResult.ok();
    }

    /**
     * 审批跳转
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation("审批跳转")
    @CommonLog("审批跳转")
    @PostMapping("/flw/task/jump")
    public CommonResult<String> jump(@RequestBody @Valid FlwTaskJumpParam flwTaskJumpParam) {
        flwTaskService.jump(flwTaskJumpParam);
        return CommonResult.ok();
    }

    /**
     * 审批加签
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:47
     */
    @ApiOperationSupport(order = 10)
    @ApiOperation("审批加签")
    @CommonLog("审批加签")
    @PostMapping("/flw/task/addSign")
    public CommonResult<String> addSign(@RequestBody @Valid FlwTaskAddSignParam flwTaskAddSignParam) {
        flwTaskService.addSign(flwTaskAddSignParam);
        return CommonResult.ok();
    }

    /**
     * 获取任务详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("获取任务详情")
    @GetMapping("/flw/task/detail")
    public CommonResult<FlwTaskDetailResult> detail(@Valid FlwTaskIdParam flwTaskIdParam) {
        return CommonResult.data(flwTaskService.detail(flwTaskIdParam));
    }

    /**
     * 获取可驳回节点列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 12)
    @ApiOperation("获取可驳回节点列表")
    @GetMapping("/flw/task/getCanBackNodeInfoList")
    public CommonResult<List<FlwBackNodeResult>> getCanBackNodeInfoList(@Valid FlwTaskGetBackNodeInfoParam flwTaskGetBackNodeInfoParam) {
        return CommonResult.data(flwTaskService.getCanBackNodeInfoList(flwTaskGetBackNodeInfoParam));
    }

    /**
     * 获取可跳转节点列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 13)
    @ApiOperation("获取可跳转节点列表")
    @GetMapping("/flw/task/getCanJumpNodeInfoList")
    public CommonResult<List<FlwJumpNodeResult>> getCanJumpNodeInfoList(@Valid FlwTaskGetJumpNodeInfoParam flwTaskGetJumpNodeInfoParam) {
        return CommonResult.data(flwTaskService.getCanJumpNodeInfoList(flwTaskGetJumpNodeInfoParam));
    }

    /**
     * 获取组织树选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 14)
    @ApiOperation("获取组织树选择器")
    @GetMapping("/flw/task/orgTreeSelector")
    public CommonResult<List<Tree<String>>> orgTreeSelector() {
        return CommonResult.data(flwTaskService.orgTreeSelector());
    }

    /**
     * 获取用户选择器
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 15)
    @ApiOperation("获取用户选择器")
    @GetMapping("/flw/task/userSelector")
    public CommonResult<Page<FlwTaskUserResult>> userSelector(FlwTaskSelectorUserParam flwTaskSelectorUserParam) {
        return CommonResult.data(flwTaskService.userSelector(flwTaskSelectorUserParam));
    }
}
