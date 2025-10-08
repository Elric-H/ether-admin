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
package com.xuan.dev.modular.dfc.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fhs.core.trans.vo.TransPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.xuan.common.pojo.CommonEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 动态字段配置实体
 *
 * @author 每天一点
 * @date  2023/08/04 08:18
 **/
@Getter
@Setter
@TableName("DEV_DFC")
public class DevDfc extends CommonEntity implements TransPojo {

    /** 主键 */
    @TableId
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 数据源 */
    @ApiModelProperty(value = "数据源", position = 3)
    private String dbsId;

    /** 表名称 */
    @ApiModelProperty(value = "表名称", position = 4)
    private String tableName;

    /** 排序码 */
    @ApiModelProperty(value = "排序码", position = 5)
    private Integer sortCode;

    /** 表单域属性名 */
    @ApiModelProperty(value = "表单域属性名", position = 6)
    private String name;

    /** 标签文本 */
    @ApiModelProperty(value = "标签文本", position = 7)
    private String label;

    /** 必填 */
    @ApiModelProperty(value = "必填", position = 8)
    private Integer required;

    /** 提示语 */
    @ApiModelProperty(value = "提示语", position = 9)
    private String placeholder;

    /** 字段类型 */
    @ApiModelProperty(value = "字段类型", position = 10)
    private String type;

    /** 选择项类型 */
    @ApiModelProperty(value = "选择项类型", position = 11)
    private String selectOptionType;

    /** 字典 */
    @ApiModelProperty(value = "字典", position = 12)
    private String dictTypeCode;

    /** 选择项api地址 */
    @ApiModelProperty(value = "选择项api地址", position = 13)
    private String selOptionApiUrl;

    /** 已选择数据api地址 */
    @ApiModelProperty(value = "已选择数据api地址", position = 14)
    private String selDataApiUrl;

    /** 是否多选 */
    @ApiModelProperty(value = "是否多选", position = 15)
    private Integer isMultiple;

    /** 状态 */
    @ApiModelProperty(value = "状态", position = 16)
    private String status;
}
