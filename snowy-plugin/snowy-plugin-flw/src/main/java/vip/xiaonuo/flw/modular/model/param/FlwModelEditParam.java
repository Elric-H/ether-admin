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
package vip.xiaonuo.flw.modular.model.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 编辑模型参数
 *
 * @author xuyuxiang
 * @date 2022/7/31 17:55
 */
@Getter
@Setter
public class FlwModelEditParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 管理员id */
    @ApiModelProperty(value = "管理员id", required = true, position = 2)
    @NotBlank(message = "adminId不能为空")
    private String adminId;

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 3)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 类型 */
    @ApiModelProperty(value = "类型", required = true, position = 4)
    @NotBlank(message = "formType不能为空")
    private String formType;

    /** 分类 */
    @ApiModelProperty(value = "分类", required = true, position = 5)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 图标 */
    @ApiModelProperty(value = "图标", required = true, position = 6)
    @NotBlank(message = "icon不能为空")
    private String icon;

    /** 移动端图标 */
    @ApiModelProperty(value = "移动端图标", position = 7)
    private String iconMobile;

    /** 颜色 */
    @ApiModelProperty(value = "颜色", required = true, position = 8)
    @NotBlank(message = "color不能为空")
    private String color;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 9)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 数据库表JSON */
    @ApiModelProperty(value = "数据库表JSON", position = 10)
    private String tableJson;

    /** 表单JSON */
    @ApiModelProperty(value = "表单JSON", position = 11)
    private String formJson;

    /** 流程JSON */
    @ApiModelProperty(value = "流程JSON", position = 12)
    private String processJson;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展信息", position = 13)
    private String extJson;
}
