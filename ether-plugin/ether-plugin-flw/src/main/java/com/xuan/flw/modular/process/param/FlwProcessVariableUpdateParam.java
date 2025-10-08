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
package com.xuan.flw.modular.process.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 流程变量更新参数
 *
 * @author xuyuxiang
 * @date 2022/8/1 14:45
 */
@Getter
@Setter
public class FlwProcessVariableUpdateParam {

    /** 流程实例id */
    @ApiModelProperty(value = "流程实例id", required = true)
    @NotBlank(message = "processInstanceId不能为空")
    private String processInstanceId;

    /** 变量信息 */
    @Valid
    @ApiModelProperty(value = "变量信息", required = true, position = 2)
    @NotNull(message = "variableInfoList不能为空")
    private List<FlwProcessVariable> variableInfoList;

    /**
     * 变量信息类
     *
     * @author xuyuxiang
     * @date 2022/4/28 23:19
     */
    @Getter
    @Setter
    public static class FlwProcessVariable {

        /**
         * 执行实例id
         */
        @ApiModelProperty(value = "执行实例id", position = 1)
        @NotBlank(message = "executionId不能为空")
        private String executionId;

        /**
         * 变量类型
         */
        @ApiModelProperty(value = "变量类型", position = 2)
        @NotBlank(message = "typeName不能为空")
        private String typeName;

        /**
         * 变量键
         */
        @ApiModelProperty(value = "变量键", position = 3)
        @NotBlank(message = "variableKey不能为空")
        private String variableKey;

        /**
         * 变量值
         */
        @ApiModelProperty(value = "变量值", position = 4)
        @NotBlank(message = "variableValue不能为空")
        private String variableValue;
    }
}
