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
package vip.xiaonuo.flw.modular.process.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 草稿详情结果
 *
 * @author xuyuxiang
 * @date 2022/8/23 15:09
 **/
@Getter
@Setter
public class FlwProcessDraftDetailResult {

    /** 草稿id */
    @ApiModelProperty(value = "草稿id", position = 1)
    private String id;

    /** 模型id */
    @ApiModelProperty(value = "模型id", position = 2)
    private String modelId;

    /** 表单类型 */
    @ApiModelProperty(value = "表单类型", position = 3)
    private String formType;

    /** 流程名称 */
    @ApiModelProperty(value = "流程名称", position = 4)
    private String title;

    /** 流程JSON */
    @ApiModelProperty(value = "流程JSON", position = 5)
    private String processJson;

    /** 表单信息 */
    @ApiModelProperty(value = "表单信息", position = 6)
    private String formJson;

    /** 填写数据 */
    @ApiModelProperty(value = "填写数据", position = 7)
    private String dataJson;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 8)
    private String createTime;
}
