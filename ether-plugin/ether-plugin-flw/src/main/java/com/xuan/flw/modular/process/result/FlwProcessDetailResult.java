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
package com.xuan.flw.modular.process.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.xuan.flw.modular.task.result.FlwTaskAttachmentResult;
import com.xuan.flw.modular.task.result.FlwTaskDetailResult;

import java.util.List;

/**
 * 流程详情结果
 *
 * @author xuyuxiang
 * @date 2022/8/23 15:09
 **/
@Getter
@Setter
public class FlwProcessDetailResult {

    /** 模型id */
    @ApiModelProperty(value = "模型id", position = 1)
    private String modelId;

    /** 流程实例id */
    @ApiModelProperty(value = "流程实例id", position = 2)
    private String processInstanceId;

    /** 表单类型 */
    @ApiModelProperty(value = "表单类型", position = 3)
    private String formType;

    /** 模型信息 */
    @ApiModelProperty(value = "模型信息", position = 4)
    private String initiatorModelJson;

    /** 表单信息 */
    @ApiModelProperty(value = "表单信息", position = 5)
    private String initiatorFormJson;

    /** 填写数据 */
    @ApiModelProperty(value = "填写数据", position = 6)
    private String initiatorDataJson;

    /** 审批记录 */
    @ApiModelProperty(value = "审批记录", position = 7)
    private List<FlwTaskDetailResult.FlwProcessComment> commentList;

    /**
     * 审批记录类
     *
     * @author xuyuxiang
     * @date 2022/8/23 15:48
     **/
    @Getter
    @Setter
    public static class FlwProcessComment {

        /** 任务id */
        @ApiModelProperty(value = "任务id", position = 1)
        private String taskId;

        /** 任务名称 */
        @ApiModelProperty(value = "任务名称", position = 2)
        private String taskName;

        /** 用户id */
        @ApiModelProperty(value = "用户id", position = 3)
        private String userId;

        /** 用户名称 */
        @ApiModelProperty(value = "用户名称", position = 4)
        private String userName;

        /** 审批操作 */
        @ApiModelProperty(value = "审批操作", position = 5)
        private String operate;

        /** 审批意见 */
        @ApiModelProperty(value = "审批意见", position = 6)
        private String comment;

        /** 审批时间 */
        @ApiModelProperty(value = "审批时间", position = 7)
        private String approveTime;

        /** 审批附件 */
        @ApiModelProperty(value = "审批附件", position = 8)
        private List<FlwTaskAttachmentResult> attachmentList;
    }
}
