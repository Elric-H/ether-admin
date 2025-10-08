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

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xuan.common.enums.CommonSortOrderEnum;
import com.xuan.common.exception.CommonException;
import com.xuan.common.page.CommonPageRequest;
import com.xuan.pay.core.consts.PayOrderConstants;
import com.xuan.pay.core.consts.PayProductConstants;
import com.xuan.pay.core.enums.PayYesNoEnum;
import com.xuan.pay.modular.ali.service.PayAliService;
import com.xuan.pay.modular.order.entity.PayOrder;
import com.xuan.pay.modular.order.entity.PayOrderDetails;
import com.xuan.pay.modular.order.entity.PayOrderRefund;
import com.xuan.pay.modular.order.enums.PayOrderPayPlatformEnum;
import com.xuan.pay.modular.order.enums.PayOrderPayStatusEnum;
import com.xuan.pay.modular.order.enums.PayOrderRefundStatusEnum;
import com.xuan.pay.modular.order.mapper.PayOrderMapper;
import com.xuan.pay.modular.order.param.PayOrderCreateParam;
import com.xuan.pay.modular.order.param.PayOrderIdParam;
import com.xuan.pay.modular.order.param.PayOrderPageParam;
import com.xuan.pay.modular.order.param.PayOrderRefundParam;
import com.xuan.pay.modular.order.service.PayOrderDetailsService;
import com.xuan.pay.modular.order.service.PayOrderRefundService;
import com.xuan.pay.modular.order.service.PayOrderService;
import com.xuan.pay.modular.wx.service.PayWxService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单Service接口实现类
 *
 * @author xuyuxiang
 * @date 2023/3/27 15:24
 **/
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements PayOrderService {

    @Resource
    private PayAliService payAliService;

    @Resource
    private PayWxService payWxService;

    @Resource
    private PayOrderRefundService payOrderRefundService;

    @Resource
    private PayOrderDetailsService payOrderDetailsService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PayOrder createOrder(PayOrderCreateParam payOrderCreateParam) {
        // 校验支付平台
        PayOrderPayPlatformEnum.validate(payOrderCreateParam.getPayPlatform());
        // 构造一个订单
        PayOrder payOrder = new PayOrder();
        payOrder.setPayPlatform(payOrderCreateParam.getPayPlatform());
        payOrder.setOutTradeNo(IdWorker.getIdStr());
        payOrder.setSubject(PayOrderConstants.ORDER_SUBJECT);
        payOrder.setBody(PayOrderConstants.ORDER_BODY);
        // 订单共0.03元
        payOrder.setOrderAmount(PayOrderConstants.ORDER_TOTAL_AMOUNT_YUAN);
        payOrder.setPayStatus(PayOrderPayStatusEnum.NO_TRADE.getValue());
        payOrder.setHasRefund(PayYesNoEnum.N.getValue());
        this.save(payOrder);

        // 模拟构造三个产品
        for (int i = 0; i < 3; i++) {
            PayOrderDetails payOrderDetails = new PayOrderDetails();
            payOrderDetails.setOrderId(payOrder.getId());
            payOrderDetails.setProductId(IdWorker.getIdStr());
            payOrderDetails.setProductName(PayProductConstants.PRODUCT_NAME);
            // 每个商品0.01元
            payOrderDetails.setProductAmount(PayProductConstants.PRODUCT_AMOUNT);
            payOrderDetails.setProductCount(1);
            payOrderDetailsService.save(payOrderDetails);
        }
        return payOrder;
    }

    @Override
    public Page<PayOrder> page(PayOrderPageParam payOrderPageParam) {
        QueryWrapper<PayOrder> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(payOrderPageParam.getSearchKey())) {
            queryWrapper.lambda().like(PayOrder::getOutTradeNo, payOrderPageParam.getSearchKey()).or()
                    .like(PayOrder::getTradeNo, payOrderPageParam.getSearchKey()).or()
                    .like(PayOrder::getSubject, payOrderPageParam.getSearchKey()).or()
                    .like(PayOrder::getBody, payOrderPageParam.getSearchKey());
        }
        if(ObjectUtil.isNotEmpty(payOrderPageParam.getPayPlatform())) {
            queryWrapper.lambda().eq(PayOrder::getPayPlatform, payOrderPageParam.getPayPlatform());
        }
        if(ObjectUtil.isNotEmpty(payOrderPageParam.getPayStatus())) {
            queryWrapper.lambda().eq(PayOrder::getPayStatus, payOrderPageParam.getPayStatus());
        }
        if(ObjectUtil.isNotEmpty(payOrderPageParam.getHasRefund())) {
            queryWrapper.lambda().eq(PayOrder::getHasRefund, payOrderPageParam.getHasRefund());
        }
        if(ObjectUtil.isAllNotEmpty(payOrderPageParam.getStartCreateTime(), payOrderPageParam.getEndCreateTime())) {
                queryWrapper.lambda().ge(PayOrder::getCreateTime, DateUtil.parse(payOrderPageParam.getStartCreateTime()))
                    .le(PayOrder::getCreateTime, DateUtil.parse(payOrderPageParam.getEndCreateTime()));
        }
        if(ObjectUtil.isAllNotEmpty(payOrderPageParam.getStartPayTime(), payOrderPageParam.getEndPayTime())) {
            queryWrapper.lambda().ge(PayOrder::getPayTime, DateUtil.parse(payOrderPageParam.getStartPayTime()))
                    .le(PayOrder::getPayTime, DateUtil.parse(payOrderPageParam.getEndPayTime()));
        }
        if(ObjectUtil.isAllNotEmpty(payOrderPageParam.getSortField(), payOrderPageParam.getSortOrder())) {
            CommonSortOrderEnum.validate(payOrderPageParam.getSortOrder());
            queryWrapper.orderBy(true, payOrderPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
                    StrUtil.toUnderlineCase(payOrderPageParam.getSortField()));
        } else {
            queryWrapper.lambda().orderByDesc(PayOrder::getCreateTime);
        }
        return this.page(CommonPageRequest.defaultPage(), queryWrapper);
    }

    @Override
    public List<PayOrderRefund> refundList(PayOrderIdParam payOrderIdParam) {
        PayOrder payOrder = this.queryEntity(payOrderIdParam.getId());
        // 先更新该订单下的退款信息
        payOrderRefundService.list(new LambdaQueryWrapper<PayOrderRefund>()
                .eq(PayOrderRefund::getOrderId, payOrderIdParam.getId())
                .eq(PayOrderRefund::getRefundStatus, PayOrderRefundStatusEnum.REFUND_PENDING.getValue())).forEach(payOrderRefund -> {
                    if(payOrder.getPayPlatform().equals(PayOrderPayPlatformEnum.ALIPAY.getValue())) {
                        AlipayTradeFastpayRefundQueryResponse alipayTradeFastpayRefundQueryResponse = payAliService
                                .refundQuery(payOrderRefund.getOutTradeNo(), payOrderRefund.getRefundNo());
                        if(ObjectUtil.isNotEmpty(alipayTradeFastpayRefundQueryResponse)) {
                            String refundStatus = alipayTradeFastpayRefundQueryResponse.getRefundStatus();
                            if("REFUND_SUCCESS".equals(refundStatus)) {
                                payOrderRefundService.updateRefundInfo(payOrderRefund.getRefundNo(), PayOrderRefundStatusEnum.REFUND_SUCCESS.getValue(),
                                        alipayTradeFastpayRefundQueryResponse.getGmtRefundPay());
                            } else {
                                payOrderRefundService.updateRefundInfo(payOrderRefund.getRefundNo(), PayOrderRefundStatusEnum.REFUND_FAIL.getValue(),
                                        null);
                            }
                        }
                    } else {
                        WxPayRefundQueryV3Result wxPayRefundQueryV3Result = payWxService
                                .refundQuery(payOrderRefund.getOutTradeNo(), payOrderRefund.getRefundNo());
                        if(ObjectUtil.isNotEmpty(wxPayRefundQueryV3Result)) {
                            String refundStatus = wxPayRefundQueryV3Result.getStatus();
                            if("SUCCESS".equals(refundStatus) || "CLOSED".equals(refundStatus)) {
                                payOrderRefundService.updateRefundInfo(payOrderRefund.getRefundNo(), PayOrderRefundStatusEnum.REFUND_SUCCESS.getValue(),
                                        DateUtil.parse(wxPayRefundQueryV3Result.getSuccessTime()));
                            } else if("PROCESSING".equals(refundStatus)) {
                                payOrderRefundService.updateRefundInfo(payOrderRefund.getRefundNo(), PayOrderRefundStatusEnum.REFUND_PENDING.getValue(),
                                        null);
                            } else if("ABNORMAL".equals(refundStatus)){
                                payOrderRefundService.updateRefundInfo(payOrderRefund.getRefundNo(), PayOrderRefundStatusEnum.REFUND_FAIL.getValue(),
                                        null);
                            }
                        }
                    }
        });
        return payOrderRefundService.list(new LambdaQueryWrapper<PayOrderRefund>().eq(PayOrderRefund::getOrderId,
                payOrderIdParam.getId()));
    }

    @Override
    public void doRefund(PayOrderRefundParam payOrderRefundParam) {
        PayOrder payOrder = this.queryEntity(payOrderRefundParam.getId());
        payOrder = this.doValidOrder(payOrder, true);
        String refundAmount = payOrderRefundParam.getRefundAmount();
        if(payOrder.getPayPlatform().equals(PayOrderPayPlatformEnum.ALIPAY.getValue())) {
            payAliService.doRefund(payOrder, refundAmount);
        } else {
            payWxService.doRefund(payOrder, refundAmount);
        }
    }

    @Override
    public void delete(List<PayOrderIdParam> payOrderIdParamList) {
        List<String> payOrderIdList = CollStreamUtil.toList(payOrderIdParamList, PayOrderIdParam::getId);
        if(ObjectUtil.isNotEmpty(payOrderIdList)) {
            payOrderRefundService.remove(new LambdaQueryWrapper<PayOrderRefund>().in(PayOrderRefund::getOrderId, payOrderIdList));
            this.removeByIds(payOrderIdList);
        }
    }

    @Override
    public PayOrder queryEntity(String id) {
        PayOrder payOrder = this.getById(id);
        if(ObjectUtil.isEmpty(payOrder)) {
            throw new CommonException("订单不存在，id值为：{}", id);
        }
        return payOrder;
    }

    @Override
    public void sync(List<PayOrderIdParam> payOrderIdParamList) {
        List<String> payOrderIdList = CollStreamUtil.toList(payOrderIdParamList, PayOrderIdParam::getId);
        if(ObjectUtil.isNotEmpty(payOrderIdList)) {
            List<PayOrder> payOrderList = this.listByIds(payOrderIdList);
            if(ObjectUtil.isNotEmpty(payOrderList)) {
                payOrderList.forEach(payOrder -> {
                    if(payOrder.getPayPlatform().equals(PayOrderPayPlatformEnum.ALIPAY.getValue())) {
                        payAliService.doSyncForTradeQuery(payOrder);
                    } else {
                        payWxService.doSyncForTradeQuery(payOrder);
                    }
                });
            }
        }
    }

    @Override
    public void close(List<PayOrderIdParam> payOrderIdParamList) {
        List<String> payOrderIdList = CollStreamUtil.toList(payOrderIdParamList, PayOrderIdParam::getId);
        if(ObjectUtil.isNotEmpty(payOrderIdList)) {
            List<PayOrder> payOrderList = this.listByIds(payOrderIdList);
            if(ObjectUtil.isNotEmpty(payOrderList)) {
                payOrderList.forEach(payOrder -> {
                    payOrder = this.doValidOrder(payOrder, false);
                    if(payOrder.getPayStatus().equals(PayOrderPayStatusEnum.TRADE_CLOSED.getValue())) {
                        throw new CommonException("该订单已经处于关闭状态");
                    } else if(payOrder.getPayStatus().equals(PayOrderPayStatusEnum.NO_TRADE.getValue())) {
                        payOrder.setPayStatus(PayOrderPayStatusEnum.TRADE_CLOSED.getValue());
                        this.updateById(payOrder);
                    } else {
                        if(payOrder.getPayPlatform().equals(PayOrderPayPlatformEnum.ALIPAY.getValue())) {
                            payAliService.doClose(payOrder);
                        } else {
                            payWxService.doClose(payOrder);
                        }
                    }
                });
            }
        }
    }

    /**
     * 执行订单基本状态校验
     *
     * @author xuyuxiang
     * @date 2023/3/30 10:36
     **/
    private PayOrder doValidOrder(PayOrder payOrder, boolean isRefund) {
        PayOrderIdParam payOrderIdParam = new PayOrderIdParam();
        payOrderIdParam.setId(payOrder.getId());
        this.sync(CollectionUtil.newArrayList(payOrderIdParam));
        payOrder = this.queryEntity(payOrder.getId());
        if(isRefund) {
            if(payOrder.getPayStatus().equals(PayOrderPayStatusEnum.NO_TRADE.getValue())) {
                throw new CommonException("订单未创建交易，不可进行此操作，id值为：{}", payOrder.getId());
            }
        }
        return payOrder;
    }
}
