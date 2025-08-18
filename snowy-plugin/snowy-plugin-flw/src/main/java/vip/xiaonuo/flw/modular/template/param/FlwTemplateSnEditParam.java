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
package vip.xiaonuo.flw.modular.template.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 流水号模板编辑参数
 *
 * @author xuyuxiang
 * @date 2022/8/1 14:15
 */
@Getter
@Setter
public class FlwTemplateSnEditParam {

    /** id */
    @ApiModelProperty(value = "id", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 名称 */
    @ApiModelProperty(value = "名称", required = true, position = 2)
    @NotBlank(message = "name不能为空")
    private String name;

    /** 前缀 */
    @ApiModelProperty(value = "前缀", required = true, position = 3)
    @NotBlank(message = "prefix不能为空")
    private String prefix;

    /** 年月格式 */
    @ApiModelProperty(value = "年月格式", required = true, position = 4)
    @NotBlank(message = "dateFormat不能为空")
    private String dateFormat;

    /** 后缀位数 */
    @ApiModelProperty(value = "后缀位数", required = true, position = 5)
    @NotNull(message = "suffixDigits不能为空")
    private Integer suffixDigits;

    /** 后缀初始值 */
    @ApiModelProperty(value = "后缀初始值", required = true, position = 6)
    @NotNull(message = "suffixInitialValue不能为空")
    private Integer suffixInitialValue;

    /** 后缀增量 */
    @ApiModelProperty(value = "后缀增量", required = true, position = 7)
    @NotNull(message = "suffixIncrementalValue不能为空")
    private Integer suffixIncrementalValue;

    /** 显示值 */
    @ApiModelProperty(value = "显示值", required = true, position = 8)
    @NotBlank(message = "previewValue不能为空")
    private String previewValue;

    /** 分类 */
    @ApiModelProperty(value = "分类", required = true, position = 9)
    @NotBlank(message = "category不能为空")
    private String category;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", required = true, position = 10)
    @NotNull(message = "sortCode不能为空")
    private Integer sortCode;

    /** 扩展信息 */
    @ApiModelProperty(value = "扩展JSON", position = 11)
    private String extJson;
}
