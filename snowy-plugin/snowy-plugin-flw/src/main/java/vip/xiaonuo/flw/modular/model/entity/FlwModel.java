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
package vip.xiaonuo.flw.modular.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

/**
 * 模型实体
 *
 * @author xuyuxiang
 * @date 2022/5/11 10:38
 **/
@Getter
@Setter
@TableName("ACT_EXT_MODEL")
public class FlwModel extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 定义id */
    @ApiModelProperty(value = "定义id", position = 3)
    private String definitionId;

    /** 部署id */
    @ApiModelProperty(value = "部署id", position = 4)
    private String deploymentId;

    /** 管理员id */
    @ApiModelProperty(value = "管理员id", position = 5)
    private String adminId;

    /** 名称 */
    @ApiModelProperty(value = "名称", position = 6)
    private String name;

    /** 编码 */
    @ApiModelProperty(value = "编码", position = 7)
    private String code;

    /** 版本 */
    @ApiModelProperty(value = "版本", position = 8)
    private String versionCode;

    /** 类型（自定义表单 设计表单） */
    @ApiModelProperty(value = "类型", position = 9)
    private String formType;

    /** 分类 */
    @ApiModelProperty(value = "分类", position = 10)
    private String category;

    /** 图标 */
    @ApiModelProperty(value = "图标", position = 11)
    private String icon;

    /** 移动端图标 */
    @ApiModelProperty(value = "移动端图标", position = 12)
    private String iconMobile;

    /** 颜色 */
    @ApiModelProperty(value = "颜色", position = 13)
    private String color;

    /** 数据库表JSON */
    @ApiModelProperty(value = "数据库表JSON", position = 14)
    private String tableJson;

    /** 表单JSON */
    @ApiModelProperty(value = "表单JSON", position = 15)
    private String formJson;

    /** 表单URL（暂未使用） */
    @ApiModelProperty(value = "表单URL", position = 16)
    private String formUrl;

    /** 流程JSON */
    @ApiModelProperty(value = "流程JSON", position = 17)
    private String processJson;

    /** 流程XML */
    @ApiModelProperty(value = "流程XML", position = 18)
    private String processXml;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 19)
    private Integer sortCode;

    /** 模型状态 */
    @ApiModelProperty(value = "模型状态", position = 20)
    private String modelStatus;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 21)
    private String extJson;
}
