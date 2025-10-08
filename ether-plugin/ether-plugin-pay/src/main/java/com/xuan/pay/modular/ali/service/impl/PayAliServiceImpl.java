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
package com.xuan.pay.modular.ali.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.math.Money;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfigKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.xuan.common.exception.CommonException;
import com.xuan.pay.core.consts.PayOrderConstants;
import com.xuan.pay.core.enums.PayYesNoEnum;
import com.xuan.pay.modular.ali.param.PayAliCodePayParam;
import com.xuan.pay.modular.ali.param.PayAliPcParam;
import com.xuan.pay.modular.ali.param.PayAliQrParam;
import com.xuan.pay.modular.ali.param.PayAliWapParam;
import com.xuan.pay.modular.ali.service.PayAliService;
import com.xuan.pay.modular.order.entity.PayOrder;
import com.xuan.pay.modular.order.enums.PayOrderPayStatusEnum;
import com.xuan.pay.modular.order.enums.PayOrderRefundStatusEnum;
import com.xuan.pay.modular.order.service.PayOrderRefundService;
import com.xuan.pay.modular.order.service.PayOrderService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 支付宝Service接口实现类
 *
 * @author xuyuxiang
 * @date 2023/3/21 16:15
 **/
@Slf4j
@Service
public class PayAliServiceImpl implements PayAliService {

    /* 支付宝支付logo */
    private static final String ALI_PAY_LOGO_BASE64 = "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";

    /* 当面付产品 */
    private static final String FACE_TO_FACE_PAYMENT_KEY = "FACE_TO_FACE_PAYMENT";

    /* 新快捷即时到账产品 */
    private static final String FAST_INSTANT_TRADE_PAY_KEY = "FAST_INSTANT_TRADE_PAY";

    /* 手机网站支付产品 */
    private static final String QUICK_WAP_WAY_KEY = "QUICK_WAP_WAY";

    /* 查询余额账户类型 */
    private static final String ACCOUNT_TYPE = "ACCTRANS_ACCOUNT";

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private PayOrderRefundService payOrderRefundService;

    @Override
    public String notifyUrl(HttpServletRequest request) {
        try {
            // 获取支付宝POST过来的反馈信息
            Map<String, String> params = AliPayApi.toMap(request);
            if(ObjectUtil.isEmpty(params)) {
                return "failure";
            }
            boolean verifySuccess = AlipaySignature.certVerifyV1(params, AliPayApiConfigKit.getAliPayApiConfig().getAliPayCertPath(),
                    CharsetUtil.UTF_8, AlipayConstants.SIGN_TYPE_RSA2);
            if(verifySuccess) {
                String outTradeNo = params.get("out_trade_no");
                PayOrder payOrder = payOrderService.getOne(new LambdaQueryWrapper<PayOrder>()
                        .eq(PayOrder::getOutTradeNo, outTradeNo));
                if(ObjectUtil.isNotEmpty(payOrder)) {
                    this.doSyncForNotify(payOrder, params);
                }
                return "success";
            } else {
                log.error(">>> 支付宝异步通知验签失败");
                return "failure";
            }
        } catch (AlipayApiException e) {
            log.error(">>> 支付宝异步通知处理异常：", e);
            return "failure";
        }
    }

    @Override
    public String accountQuery() {
        AlipayFundAccountQueryResponse alipayFundAccountQueryResponse;
        try {
            AlipayFundAccountQueryRequest alipayFundAccountQueryRequest = new AlipayFundAccountQueryRequest();
            AlipayFundAccountQueryModel alipayFundAccountQueryModel = new AlipayFundAccountQueryModel();
            alipayFundAccountQueryModel.setAlipayUserId(JSONUtil.parseObj(AliPayApiConfigKit.getAliPayApiConfig().getExParams())
                    .getStr("mchUserId"));
            alipayFundAccountQueryModel.setAccountType(ACCOUNT_TYPE);
            alipayFundAccountQueryRequest.setBizModel(alipayFundAccountQueryModel);
            alipayFundAccountQueryResponse = AliPayApi.doExecute(alipayFundAccountQueryRequest);
        } catch (AlipayApiException e) {
            throw new CommonException("支付宝商家账户余额查询异常：{}", e.getErrMsg());
        }
        if(alipayFundAccountQueryResponse.isSuccess()) {
            // 可用余额
            String availableAmount = alipayFundAccountQueryResponse.getAvailableAmount();
            // 冻结余额
            String freezeAmount = alipayFundAccountQueryResponse.getFreezeAmount();
            return new Money(availableAmount).add(new Money(freezeAmount)).getAmount().toString();
        } else {
            throw new CommonException("支付宝商家账户余额查询异常：{}", alipayFundAccountQueryResponse.getSubMsg());
        }
    }

    @Override
    public void codePay(PayAliCodePayParam payAliCodePayParam) {
        AlipayTradePayResponse alipayTradePayResponse;
        PayOrder payOrder;
        try {
            AlipayTradePayRequest alipayTradePayRequest = new AlipayTradePayRequest();
            AlipayTradePayModel alipayTradePayModel = new AlipayTradePayModel();
            alipayTradePayModel.setOutTradeNo(payAliCodePayParam.getOutTradeNo());
            alipayTradePayModel.setSubject(PayOrderConstants.ORDER_SUBJECT);
            alipayTradePayModel.setBody(PayOrderConstants.ORDER_SUBJECT);
            alipayTradePayModel.setTotalAmount(PayOrderConstants.ORDER_TOTAL_AMOUNT_YUAN);
            alipayTradePayModel.setScene("bar_code");
            alipayTradePayModel.setAuthCode(payAliCodePayParam.getAuthCode());
            alipayTradePayRequest.setBizModel(alipayTradePayModel);
            alipayTradePayRequest.setNotifyUrl(JSONUtil.parseObj(AliPayApiConfigKit.getAliPayApiConfig().getExParams())
                    .getStr("notifyUrl"));
            alipayTradePayResponse = AliPayApi.doExecute(alipayTradePayRequest);
        } catch (AlipayApiException e) {
            throw new CommonException("支付宝付款码支付异常：{}", e.getErrMsg());
        }
        if(!alipayTradePayResponse.isSuccess()) {
            throw new CommonException("支付宝付款码支付异常：{}", alipayTradePayResponse.getSubMsg());
        }
        // 查询订单
        payOrder = payOrderService.getOne(new LambdaQueryWrapper<PayOrder>().eq(PayOrder::getOutTradeNo, payAliCodePayParam.getOutTradeNo()));
        // 更新支付结果
        this.doSyncForTradeQuery(payOrder);
    }

    @Override
    public String qrPay(PayAliQrParam payAliQrParam) {
        AlipayTradePrecreateResponse alipayTradePrecreateResponse;
        try {
            AlipayTradePrecreateRequest alipayTradePrecreateRequest = new AlipayTradePrecreateRequest();
            AlipayTradePrecreateModel alipayTradePrecreateModel = new AlipayTradePrecreateModel();
            alipayTradePrecreateModel.setProductCode(FACE_TO_FACE_PAYMENT_KEY);
            alipayTradePrecreateModel.setOutTradeNo(payAliQrParam.getOutTradeNo());
            alipayTradePrecreateModel.setSubject(PayOrderConstants.ORDER_SUBJECT);
            alipayTradePrecreateModel.setBody(PayOrderConstants.ORDER_BODY);
            alipayTradePrecreateModel.setTotalAmount(PayOrderConstants.ORDER_TOTAL_AMOUNT_YUAN);
            alipayTradePrecreateRequest.setBizModel(alipayTradePrecreateModel);
            alipayTradePrecreateRequest.setNotifyUrl(JSONUtil.parseObj(AliPayApiConfigKit.getAliPayApiConfig().getExParams())
                    .getStr("notifyUrl"));
            alipayTradePrecreateResponse = AliPayApi.doExecute(alipayTradePrecreateRequest);
        } catch (AlipayApiException e) {
            throw new CommonException("支付宝扫码支付二维码创建异常：{}", e.getErrMsg());
        }
        if(alipayTradePrecreateResponse.isSuccess()) {
            String qrCode = alipayTradePrecreateResponse.getQrCode();
            return QrCodeUtil.generateAsBase64(qrCode, QrConfig.create().setWidth(200).setHeight(200), "png",
                    ImgUtil.toImage(ALI_PAY_LOGO_BASE64));
        } else {
            throw new CommonException("支付宝扫码支付二维码创建异常：{}", alipayTradePrecreateResponse.getSubMsg());
        }
    }

    @Override
    public String pcPay(PayAliPcParam payAliPcParam) {
        AlipayTradePagePayResponse alipayTradePagePayResponse;
        try {
            AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
            AlipayTradePagePayModel alipayTradePagePayModel = new AlipayTradePagePayModel();
            alipayTradePagePayModel.setProductCode(FAST_INSTANT_TRADE_PAY_KEY);
            alipayTradePagePayModel.setOutTradeNo(payAliPcParam.getOutTradeNo());
            alipayTradePagePayModel.setSubject(PayOrderConstants.ORDER_SUBJECT);
            alipayTradePagePayModel.setBody(PayOrderConstants.ORDER_BODY);
            alipayTradePagePayModel.setTotalAmount(PayOrderConstants.ORDER_TOTAL_AMOUNT_YUAN);
            alipayTradePagePayRequest.setBizModel(alipayTradePagePayModel);
            alipayTradePagePayRequest.setNotifyUrl(JSONUtil.parseObj(AliPayApiConfigKit.getAliPayApiConfig().getExParams())
                    .getStr("notifyUrl"));
            alipayTradePagePayResponse = AliPayApi.pageExecute(alipayTradePagePayRequest, Method.GET.name());
        } catch (AlipayApiException e) {
            throw new CommonException("支付宝PC支付页面创建异常：{}", e.getErrMsg());
        }
        if(alipayTradePagePayResponse.isSuccess()) {
            return alipayTradePagePayResponse.getBody();
        } else {
            throw new CommonException("支付宝PC支付页面创建异常：{}", alipayTradePagePayResponse.getSubMsg());
        }
    }

    @Override
    public String wapPay(PayAliWapParam payAliWapParam) {
        AlipayTradeWapPayResponse alipayTradeWapPayResponse;
        try {
            AlipayTradeWapPayRequest alipayTradeWapPayRequest = new AlipayTradeWapPayRequest();
            AlipayTradeWapPayModel alipayTradeWapPayModel = new AlipayTradeWapPayModel();
            alipayTradeWapPayModel.setProductCode(QUICK_WAP_WAY_KEY);
            alipayTradeWapPayModel.setOutTradeNo(payAliWapParam.getOutTradeNo());
            alipayTradeWapPayModel.setSubject(PayOrderConstants.ORDER_SUBJECT);
            alipayTradeWapPayModel.setBody(PayOrderConstants.ORDER_BODY);
            alipayTradeWapPayModel.setTotalAmount(PayOrderConstants.ORDER_TOTAL_AMOUNT_YUAN);
            alipayTradeWapPayRequest.setBizModel(alipayTradeWapPayModel);
            alipayTradeWapPayRequest.setNotifyUrl(JSONUtil.parseObj(AliPayApiConfigKit.getAliPayApiConfig().getExParams())
                    .getStr("notifyUrl"));
            alipayTradeWapPayResponse = AliPayApi.pageExecute(alipayTradeWapPayRequest, Method.GET.name());
        } catch (AlipayApiException e) {
            throw new CommonException("支付宝WAP支付页面创建异常：{}", e.getErrMsg());
        }
        if(alipayTradeWapPayResponse.isSuccess()) {
            return alipayTradeWapPayResponse.getBody();
        } else {
            throw new CommonException("支付宝WAP支付页面创建异常：{}", alipayTradeWapPayResponse.getSubMsg());
        }
    }

    @Override
    public AlipayTradeQueryResponse tradeQuery(String outTradeNo) {
        AlipayTradeQueryResponse alipayTradeQueryResponse;
        try {
            AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel alipayTradeQueryModel = new AlipayTradeQueryModel();
            alipayTradeQueryModel.setOutTradeNo(outTradeNo);
            alipayTradeQueryRequest.setBizModel(alipayTradeQueryModel);
            alipayTradeQueryResponse = AliPayApi.doExecute(alipayTradeQueryRequest);
        } catch (AlipayApiException e) {
            log.error(">>> 支付宝交易查询异常：", e);
            return null;
        }
        if(alipayTradeQueryResponse.isSuccess()) {
            return alipayTradeQueryResponse;
        } else {
            log.error(">>> 支付宝交易查询异常：{}", alipayTradeQueryResponse.getSubMsg());
            return null;
        }
    }

    @Override
    public AlipayTradeFastpayRefundQueryResponse refundQuery(String outTradeNo, String outRequestNo) {
        AlipayTradeFastpayRefundQueryResponse alipayTradeFastpayRefundQueryResponse;
        try {
            AlipayTradeFastpayRefundQueryRequest alipayTradeFastpayRefundQueryRequest = new AlipayTradeFastpayRefundQueryRequest();
            AlipayTradeFastpayRefundQueryModel alipayTradeFastpayRefundQueryModel = new AlipayTradeFastpayRefundQueryModel();
            alipayTradeFastpayRefundQueryModel.setOutTradeNo(outTradeNo);
            alipayTradeFastpayRefundQueryModel.setOutRequestNo(outRequestNo);
            alipayTradeFastpayRefundQueryModel.setQueryOptions(CollectionUtil.newArrayList("gmt_refund_pay"));
            alipayTradeFastpayRefundQueryRequest.setBizModel(alipayTradeFastpayRefundQueryModel);
            alipayTradeFastpayRefundQueryResponse = AliPayApi.doExecute(alipayTradeFastpayRefundQueryRequest);
        } catch (AlipayApiException e) {
            log.error(">>> 支付宝退款查询异常：", e);
            return null;
        }
        if(alipayTradeFastpayRefundQueryResponse.isSuccess()) {
            return alipayTradeFastpayRefundQueryResponse;
        } else {
            log.error(">>> 支付宝退款查询异常：{}", alipayTradeFastpayRefundQueryResponse.getSubMsg());
            return null;
        }
    }

    @Override
    public void doRefund(PayOrder payOrder, String refundAmount) {
        AlipayTradeRefundResponse alipayTradeRefundResponse;
        String refundNo = IdWorker.getIdStr();
        try {
            AlipayTradeRefundRequest alipayTradeRefundRequest = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel alipayTradeRefundModel = new AlipayTradeRefundModel();
            alipayTradeRefundModel.setOutTradeNo(payOrder.getOutTradeNo());
            alipayTradeRefundModel.setOutRequestNo(refundNo);
            alipayTradeRefundModel.setRefundAmount(refundAmount);
            alipayTradeRefundRequest.setBizModel(alipayTradeRefundModel);
            alipayTradeRefundResponse = AliPayApi.doExecute(alipayTradeRefundRequest);
        } catch (AlipayApiException e) {
            throw new CommonException("支付宝订单退款异常：{}", e.getErrMsg());
        }
        if(alipayTradeRefundResponse.isSuccess()) {
            String fundChange = alipayTradeRefundResponse.getFundChange();
            if(PayYesNoEnum.Y.getValue().equals(fundChange)) {
                payOrderRefundService.doCreateOrderRefund(payOrder, alipayTradeRefundResponse.getTradeNo(), refundNo,
                        alipayTradeRefundResponse.getBuyerUserId(), alipayTradeRefundResponse.getBuyerLogonId(),
                        refundAmount, PayOrderRefundStatusEnum.REFUND_SUCCESS.getValue(), alipayTradeRefundResponse.getGmtRefundPay());
            } else {
                payOrderRefundService.doCreateOrderRefund(payOrder, alipayTradeRefundResponse.getTradeNo(), refundNo,
                        alipayTradeRefundResponse.getBuyerUserId(), alipayTradeRefundResponse.getBuyerLogonId(),
                        refundAmount, PayOrderRefundStatusEnum.REFUND_PENDING.getValue(), null);
            }
            payOrder.setHasRefund(PayYesNoEnum.Y.getValue());
            payOrderService.updateById(payOrder);
        } else {
            throw new CommonException("支付宝订单退款异常：{}", alipayTradeRefundResponse.getSubMsg());
        }
    }

    @Override
    public void doClose(PayOrder payOrder) {
        AlipayTradeCloseResponse alipayTradeCloseResponse;
        try {
            AlipayTradeCloseRequest alipayTradeCloseRequest = new AlipayTradeCloseRequest();
            AlipayTradeCloseModel alipayTradeCloseModel = new AlipayTradeCloseModel();
            alipayTradeCloseModel.setOutTradeNo(payOrder.getOutTradeNo());
            alipayTradeCloseRequest.setBizModel(alipayTradeCloseModel);
            alipayTradeCloseResponse = AliPayApi.doExecute(alipayTradeCloseRequest);
        } catch (AlipayApiException e) {
            throw new CommonException("支付宝订单关闭异常：{}", e.getErrMsg());
        }
        if(alipayTradeCloseResponse.isSuccess()) {
            payOrder.setPayStatus(PayOrderPayStatusEnum.TRADE_CLOSED.getValue());
            payOrderService.updateById(payOrder);
        } else {
            throw new CommonException("支付宝订单关闭异常：{}", alipayTradeCloseResponse.getSubMsg());
        }
    }

    @Override
    public void doSyncForNotify(PayOrder payOrder, Map<String, String> params) {
        String tradeStatus = params.get("trade_status");
        String tradeNo = params.get("trade_no");
        String payUserId = params.get("buyer_id");
        String payAccount = params.get("buyer_logon_id");
        String payAmount = params.get("buyer_pay_amount");
        String gmtPayment = params.get("gmt_payment");
        DateTime payTime = null;
        if(ObjectUtil.isNotEmpty(gmtPayment)) {
           payTime = DateUtil.parseDateTime(gmtPayment);
        }
        this.updateOrderInfo(payOrder, tradeStatus, tradeNo, payAmount, payUserId, payAccount, payTime);
    }

    @Override
    public void doSyncForTradeQuery(PayOrder payOrder) {
        AlipayTradeQueryResponse alipayTradeQueryResponse = this.tradeQuery(payOrder.getOutTradeNo());
        if(ObjectUtil.isNotEmpty(alipayTradeQueryResponse)) {
            this.updateOrderInfo(payOrder, alipayTradeQueryResponse.getTradeStatus(),
                    alipayTradeQueryResponse.getTradeNo(), alipayTradeQueryResponse.getBuyerPayAmount(),
                    alipayTradeQueryResponse.getBuyerUserId(), alipayTradeQueryResponse.getBuyerLogonId(),
                    alipayTradeQueryResponse.getSendPayDate());
        }
    }

    /**
     * 更新订单信息
     *
     * @author xuyuxiang
     * @date 2023/3/29 21:32
     */
    private void updateOrderInfo(PayOrder payOrder, String tradeStatus, String tradeNo, String payAmount, String payUserId,
                                 String payAccount, Date payTime) {
        payOrder.setTradeNo(tradeNo);
        payOrder.setPayUserId(payUserId);
        payOrder.setPayAccount(payAccount);
        payOrder.setPayAmount(payAmount);
        payOrder.setPayStatus(tradeStatus);
        payOrder.setPayTime(payTime);
        payOrderService.updateById(payOrder);
    }
}
