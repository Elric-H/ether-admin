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

/**
 * 流程结果
 *
 * @author xuyuxiang
 * @date 2022/5/11 15:51
 **/
@Getter
@Setter
public class FlwProcessResult {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    private String id;

    /** 流水号 */
    @ApiModelProperty(value = "流水号", position = 2)
    private String sn;

    /** 标题 */
    @ApiModelProperty(value = "标题", position = 3)
    private String title;

    /** 摘要 */
    @ApiModelProperty(value = "摘要", position = 4)
    private String abstractTitle;

    /** 业务主键 */
    @ApiModelProperty(value = "业务主键", position = 5)
    private String businessKey;

    /** 流程定义id */
    @ApiModelProperty(value = "流程定义id", position = 6)
    private String processDefinitionId;

    /** 流程定义名称 */
    @ApiModelProperty(value = "流程定义名称", position = 7)
    private String processDefinitionName;

    /** 流程定义版本 */
    @ApiModelProperty(value = "流程定义版本", position = 8)
    private String processDefinitionVersion;

    /** 发起时间 */
    @ApiModelProperty(value = "发起时间", position = 9)
    private String startTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间", position = 10)
    private String endTime;

    /** 耗时 */
    @ApiModelProperty(value = "耗时", position = 11)
    private String durationInfo;

    /** 发起人id */
    @ApiModelProperty(value = "发起人id", position = 12)
    private String initiator;

    /** 发起人姓名 */
    @ApiModelProperty(value = "发起人姓名", position = 13)
    private String initiatorName;

    /** 发起人组织id */
    @ApiModelProperty(value = "发起人组织id", position = 14)
    private String initiatorOrgId;

    /** 发起人组织名称 */
    @ApiModelProperty(value = "发起人组织名称", position = 15)
    private String initiatorOrgName;

    /** 发起人职位id */
    @ApiModelProperty(value = "发起人职位id", position = 16)
    private String initiatorPositionId;

    /** 发起人职位名称 */
    @ApiModelProperty(value = "发起人职位名称", position = 17)
    private String initiatorPositionName;

    /** 当前节点 */
    @ApiModelProperty(value = "当前节点", position = 18)
    private String currentActivityNames;

    /** 当前办理人 */
    @ApiModelProperty(value = "当前办理人", position = 19)
    private String assignees;

    /** 状态值 */
    @ApiModelProperty(value = "状态值", position = 20)
    private String stateText;

    /** 状态码 */
    @ApiModelProperty(value = "状态码", position = 21)
    private String stateCode;
}
