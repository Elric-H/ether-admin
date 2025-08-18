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
package vip.xiaonuo.flw.modular.process.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
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
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.flw.modular.model.entity.FlwModel;
import vip.xiaonuo.flw.modular.model.param.FlwModelMyParam;
import vip.xiaonuo.flw.modular.model.service.FlwModelService;
import vip.xiaonuo.flw.modular.process.param.*;
import vip.xiaonuo.flw.modular.process.result.FlwProcessDetailResult;
import vip.xiaonuo.flw.modular.process.result.FlwProcessDraftDetailResult;
import vip.xiaonuo.flw.modular.process.result.FlwProcessMyDraftResult;
import vip.xiaonuo.flw.modular.process.result.FlwProcessResult;
import vip.xiaonuo.flw.modular.process.service.FlwProcessService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 个人流程控制器
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:50
 **/
@Api(tags = "个人流程控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 4)
@RestController
@Validated
public class FlwProcessMyController {

    @Resource
    private FlwProcessService flwProcessService;

    @Resource
    private FlwModelService flwModelService;

    /**
     * 获取我可以发起的流程模型列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取我可以发起的模型列表")
    @GetMapping("/flw/process/myModelList")
    public CommonResult<List<FlwModel>> myList(FlwModelMyParam flwModelMyParam) {
        return CommonResult.data(flwModelService.myList(flwModelMyParam));
    }

    /**
     * 保存草稿
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("保存草稿")
    @CommonLog("保存草稿")
    @PostMapping("/flw/process/saveDraft")
    public CommonResult<String> saveDraft(@RequestBody @Valid FlwProcessSaveDraftParam flwProcessSaveDraftParam) {
        flwProcessService.saveDraft(flwProcessSaveDraftParam);
        return CommonResult.ok();
    }

    /**
     * 发起流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("发起流程")
    @CommonLog("发起流程")
    @PostMapping("/flw/process/start")
    public CommonResult<String> start(@RequestBody @Valid FlwProcessStartParam flwProcessStartParam) {
        flwProcessService.start(flwProcessStartParam);
        return CommonResult.ok();
    }

    /**
     * 获取我的草稿分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("获取我的草稿分页")
    @GetMapping("/flw/process/myDraftPage")
    public CommonResult<Page<FlwProcessMyDraftResult>> myDraftPage(FlwProcessMyDraftPageParam flwProcessMyDraftPageParam) {
        return CommonResult.data(flwProcessService.myDraftPage(flwProcessMyDraftPageParam));
    }

    /**
     * 获取草稿详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("获取草稿详情")
    @GetMapping("/flw/process/draftDetail")
    public CommonResult<FlwProcessDraftDetailResult> draftDetail(@Valid FlwProcessDraftIdParam flwProcessDraftIdParam) {
        return CommonResult.data(flwProcessService.draftDetail(flwProcessDraftIdParam));
    }

    /**
     * 删除草稿
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("删除草稿")
    @CommonLog("删除草稿")
    @PostMapping("/flw/process/deleteDraft")
    public CommonResult<String> deleteDraft(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<FlwProcessDraftIdParam> flwProcessDraftIdParamList) {
        flwProcessService.deleteDraft(flwProcessDraftIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取我发起的流程分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("获取我发起的流程分页")
    @GetMapping("/flw/process/myPage")
    public CommonResult<Page<FlwProcessResult>> myPage(FlwProcessMyPageParam flwProcessMyPageParam) {
        FlwProcessMonitorPageParam flwProcessMonitorPageParam = new FlwProcessMonitorPageParam();
        flwProcessMonitorPageParam.setInitiator(StpUtil.getLoginIdAsString());
        flwProcessMonitorPageParam.setName(flwProcessMyPageParam.getName());
        return CommonResult.data(flwProcessService.monitorPage(flwProcessMonitorPageParam));
    }

    /**
     * 获取我的待阅流程分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("获取我的待阅流程分页")
    @GetMapping("/flw/process/myCopyUnreadPage")
    public CommonResult<Page<FlwProcessResult>> myCopyUnreadPage(FlwProcessMyPageParam flwProcessMyPageParam) {
        FlwProcessMonitorPageParam flwProcessMonitorPageParam = new FlwProcessMonitorPageParam();
        List<String> myCopyUnreadProcessInstanceIdList = flwProcessService.getMyCopyUnreadProcessInstanceIdList();
        if(ObjectUtil.isNotEmpty(myCopyUnreadProcessInstanceIdList)) {
            flwProcessMonitorPageParam.setProcessInstanceIdList(myCopyUnreadProcessInstanceIdList);
            return CommonResult.data(flwProcessService.monitorPage(flwProcessMonitorPageParam));
        }
        return CommonResult.data(new Page<>());
    }

    /**
     * 设置待阅流程为已阅
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation("设置待阅流程为已阅")
    @CommonLog("设置待阅流程为已阅")
    @PostMapping("/flw/process/readMyCopyProcess")
    public CommonResult<String> readMyCopyProcess(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.readMyCopyProcess(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取我的已阅流程分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 10)
    @ApiOperation("获取我的已阅流程分页")
    @GetMapping("/flw/process/myCopyHasReadPage")
    public CommonResult<Page<FlwProcessResult>> myCopyHasReadPage(FlwProcessMyPageParam flwProcessMyPageParam) {
        FlwProcessMonitorPageParam flwProcessMonitorPageParam = new FlwProcessMonitorPageParam();
        List<String> myCopyUnreadProcessInstanceIdList = flwProcessService.getMyCopyHasReadProcessInstanceIdList();
        if(ObjectUtil.isNotEmpty(myCopyUnreadProcessInstanceIdList)) {
            flwProcessMonitorPageParam.setProcessInstanceIdList(myCopyUnreadProcessInstanceIdList);
            return CommonResult.data(flwProcessService.monitorPage(flwProcessMonitorPageParam));
        }
        return CommonResult.data(new Page<>());
    }

    /**
     * 删除我的已阅流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 11)
    @ApiOperation("删除我的已阅流程")
    @CommonLog("删除我的已阅流程")
    @PostMapping("/flw/process/deleteMyHasReadProcess")
    public CommonResult<String> deleteMyHasReadProcess(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                          CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.deleteMyHasReadProcess(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 撤回流程
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 12)
    @ApiOperation("撤回流程")
    @CommonLog("撤回流程")
    @PostMapping("/flw/process/revoke")
    public CommonResult<String> revoke(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                            CommonValidList<FlwProcessIdParam> flwProcessIdParamList) {
        flwProcessService.revoke(flwProcessIdParamList);
        return CommonResult.ok();
    }

    /**
     * 获取流程详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 13)
    @ApiOperation("获取流程详情")
    @GetMapping("/flw/process/detail")
    public CommonResult<FlwProcessDetailResult> detail(@Valid FlwProcessIdParam flwProcessIdParam) {
        return CommonResult.data(flwProcessService.detail(flwProcessIdParam));
    }

    /**
     * 获取流程模型详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 14)
    @ApiOperation("获取流程模型详情")
    @GetMapping("/flw/process/modelDetail")
    public CommonResult<FlwModel> modelDetail(@Valid FlwProcessModelIdParam flwProcessModelIdParam) {
        return CommonResult.data(flwProcessService.modelDetail(flwProcessModelIdParam));
    }
}
