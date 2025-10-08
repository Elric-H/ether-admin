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
package com.xuan.pay.modular.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.xuan.pay.modular.order.entity.PayOrder;
import com.xuan.pay.modular.order.entity.PayOrderRefund;
import com.xuan.pay.modular.order.mapper.PayOrderRefundMapper;
import com.xuan.pay.modular.order.service.PayOrderRefundService;

import java.util.Date;

/**
 * 订单Service接口实现类
 *
 * @author xuyuxiang
 * @date 2023/3/27 15:24
 **/
@Service
public class PayOrderRefundServiceImpl extends ServiceImpl<PayOrderRefundMapper, PayOrderRefund> implements PayOrderRefundService {

    @Override
    public void doCreateOrderRefund(PayOrder payOrder, String tradeNo, String refundNo, String refundUserId,
                                    String refundAccount, String refundAmount, String refundStatus, Date refundTime) {
        PayOrderRefund payOrderRefund = new PayOrderRefund();
        payOrderRefund.setOrderId(payOrder.getId());
        payOrderRefund.setOutTradeNo(payOrder.getOutTradeNo());
        payOrderRefund.setTradeNo(tradeNo);
        payOrderRefund.setRefundNo(refundNo);
        payOrderRefund.setRefundUserId(refundUserId);
        payOrderRefund.setRefundAccount(refundAccount);
        payOrderRefund.setRefundAmount(refundAmount);
        payOrderRefund.setRefundStatus(refundStatus);
        payOrderRefund.setRefundTime(refundTime);
        this.save(payOrderRefund);
    }

    @Override
    public void updateRefundInfo(String refundNo, String refundStatus, Date refundTime) {
        this.update(new LambdaUpdateWrapper<PayOrderRefund>().eq(PayOrderRefund::getRefundNo, refundNo)
                .set(PayOrderRefund::getRefundStatus, refundStatus).set(PayOrderRefund::getRefundTime, refundTime));
    }
}
