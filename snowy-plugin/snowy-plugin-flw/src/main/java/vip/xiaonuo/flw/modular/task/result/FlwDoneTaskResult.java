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
package vip.xiaonuo.flw.modular.task.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 已办任务结果
 *
 * @author xuyuxiang
 * @date 2022/5/22 16:19
 */
@Getter
@Setter
public class FlwDoneTaskResult {

    /** 任务id */
    @ApiModelProperty(value = "任务id", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 流程实例id */
    @ApiModelProperty(value = "流程实例id", position = 3)
    private String processInstanceId;

    /** 流水号 */
    @ApiModelProperty(value = "流水号", position = 4)
    private String sn;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 5)
    private String title;

    /** 摘要 */
    @ApiModelProperty(value = "摘要", position = 6)
    private String abstractTitle;

    /** 流程定义id */
    @ApiModelProperty(value = "流程定义id", position = 7)
    private String processDefinitionId;

    /** 办理节点id */
    @ApiModelProperty(value = "办理节点id", position = 8)
    private String doneActivityId;

    /** 办理节点名称 */
    @ApiModelProperty(value = "办理节点名称", position = 9)
    private String doneActivityName;

    /** 办理时间 */
    @ApiModelProperty(value = "办理时间", position = 10)
    private String doneTime;

    /** 发起人id */
    @ApiModelProperty(value = "发起人id", position = 11)
    private String initiator;

    /** 发起人姓名 */
    @ApiModelProperty(value = "发起人姓名", position = 12)
    private String initiatorName;

    /** 发起人组织id */
    @ApiModelProperty(value = "发起人组织id", position = 13)
    private String initiatorOrgId;

    /** 发起人组织名称 */
    @ApiModelProperty(value = "发起人组织名称", position = 14)
    private String initiatorOrgName;

    /** 发起人职位id */
    @ApiModelProperty(value = "发起人职位id", position = 15)
    private String initiatorPositionId;

    /** 发起人职位名称 */
    @ApiModelProperty(value = "发起人职位名称", position = 16)
    private String initiatorPositionName;

    /** 发起时间 */
    @ApiModelProperty(value = "发起时间", position = 17)
    private String initiatorTime;

    /** 状态值 */
    @ApiModelProperty(value = "状态值", position = 18)
    private String stateText;

    /** 状态码 */
    @ApiModelProperty(value = "状态码", position = 19)
    private String stateCode;
}
