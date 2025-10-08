package com.xuan.dev.modular.dfc.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 迁移数据参数
 *
 * @author 每天一点
 * @date 2023/8/7 22:59
 */
@Getter
@Setter
public class DevDfcMigrateParam {
    /** 主键 */
    @ApiModelProperty(value = "主键", required = true, position = 1)
    @NotBlank(message = "id不能为空")
    private String id;

    /** 数据源Id */
    @ApiModelProperty(value = "数据源Id", required = true, position = 2)
    @NotBlank(message = "dbsId不能为空")
    private String dbsId;

    /** 表名称 */
    @ApiModelProperty(value = "表名称", required = true, position = 3)
    @NotBlank(message = "tableName不能为空")
    private String tableName;

    /** 字段名称 */
    @ApiModelProperty(value = "字段名称", required = true, position = 4)
    @NotBlank(message = "tableColumn不能为空")
    private String tableColumn;

}
