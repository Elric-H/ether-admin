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
package com.xuan.pay.modular.order.enums;

import lombok.Getter;
import com.xuan.common.exception.CommonException;

/**
 * 支付宝订单退款状态枚举
 *
 * @author xuyuxiang
 * @date 2021/10/11 14:02
 **/
@Getter
public enum PayOrderRefundStatusEnum {

    /**
     * 退款处理中
     */
    REFUND_PENDING("REFUND_PENDING"),

    /**
     * 退款成功
     */
    REFUND_SUCCESS("REFUND_SUCCESS"),

    /**
     * 退款失败
     */
    REFUND_FAIL("REFUND_FAIL");

    private final String value;

    PayOrderRefundStatusEnum(String value) {
        this.value = value;
    }

    public static void validate(String value) {
        boolean flag = REFUND_PENDING.getValue().equals(value) || REFUND_SUCCESS.getValue().equals(value) || REFUND_FAIL.getValue().equals(value);
        if(!flag) {
            throw new CommonException("不支持的退款状态：{}", value);
        }
    }
}
