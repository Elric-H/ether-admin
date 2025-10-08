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
package com.xuan.pay.modular.wx.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.math.Money;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Response;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.*;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.v3.util.PemUtils;
import com.github.binarywang.wxpay.v3.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.xuan.common.exception.CommonException;
import com.xuan.common.prop.CommonProperties;
import com.xuan.common.util.CommonIpAddressUtil;
import com.xuan.common.util.CommonResponseUtil;
import com.xuan.common.util.CommonServletUtil;
import com.xuan.pay.core.consts.PayOrderConstants;
import com.xuan.pay.core.enums.PayYesNoEnum;
import com.xuan.pay.core.wx.WxPayApi;
import com.xuan.pay.core.wx.WxPayApiConfigKit;
import com.xuan.pay.modular.order.entity.PayOrder;
import com.xuan.pay.modular.order.enums.PayOrderPayStatusEnum;
import com.xuan.pay.modular.order.enums.PayOrderRefundStatusEnum;
import com.xuan.pay.modular.order.service.PayOrderRefundService;
import com.xuan.pay.modular.order.service.PayOrderService;
import com.xuan.pay.modular.wx.param.PayWxCodePayParam;
import com.xuan.pay.modular.wx.param.PayWxH5Param;
import com.xuan.pay.modular.wx.param.PayWxJsParam;
import com.xuan.pay.modular.wx.param.PayWxQrParam;
import com.xuan.pay.modular.wx.service.PayWxService;
import com.xuan.ten.api.TenApi;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 微信支付Service接口实现类
 *
 * @author xuyuxiang
 * @date 2023/3/28 14:13
 **/
@Slf4j
@Service
public class PayWxServiceImpl implements PayWxService {

    /* 微信支付logo */
    private static final String WX_PAY_LOGO_BASE64 = "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";

    @Resource
    private CommonProperties commonProperties;

    @Resource
    private TenApi tenApi;

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private PayOrderRefundService payOrderRefundService;

    @Override
    public String notifyUrl(String notifyData) {
        try {
            if(ObjectUtil.isEmpty(notifyData)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            HttpServletRequest request = CommonServletUtil.getRequest();
            String timestamp = request.getHeader("wechatpay-timestamp");
            String nonce = request.getHeader("wechatpay-nonce");
            String signature = request.getHeader("wechatpay-signature");
            String serial = request.getHeader("wechatpay-serial");
            if(ObjectUtil.hasEmpty(timestamp, nonce, signature, serial)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            SignatureHeader signatureHeader = new SignatureHeader(timestamp, nonce, signature, serial);
            WxPayOrderNotifyV3Result wxPayOrderNotifyV3Result = WxPayApi.me().parseOrderNotifyV3Result(notifyData, signatureHeader);
            String outTradeNo = wxPayOrderNotifyV3Result.getResult().getOutTradeNo();
            PayOrder payOrder = payOrderService.getOne(new LambdaQueryWrapper<PayOrder>()
                    .eq(PayOrder::getOutTradeNo, outTradeNo));
            if(ObjectUtil.isNotEmpty(payOrder)) {
                this.doSyncForNotify(payOrder, wxPayOrderNotifyV3Result);
            }
            return WxPayNotifyV3Response.success("成功");
        } catch (WxPayException e) {
            log.error(">>> 微信异步通知处理异常：", e);
            return WxPayNotifyV3Response.fail("失败");
        }
    }

    @Override
    public String refundNotifyUrl(String notifyData) {
        try {
            if(ObjectUtil.isEmpty(notifyData)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            HttpServletRequest request = CommonServletUtil.getRequest();
            String timestamp = request.getHeader("wechatpay-timestamp");
            String nonce = request.getHeader("wechatpay-nonce");
            String signature = request.getHeader("wechatpay-signature");
            String serial = request.getHeader("wechatpay-serial");
            if(ObjectUtil.hasEmpty(timestamp, nonce, signature, serial)) {
                return WxPayNotifyV3Response.fail("失败");
            }
            SignatureHeader signatureHeader = new SignatureHeader(timestamp, nonce, signature, serial);
            WxPayRefundNotifyV3Result wxPayRefundNotifyV3Result = WxPayApi.me().parseRefundNotifyV3Result(notifyData, signatureHeader);
            WxPayRefundNotifyV3Result.DecryptNotifyResult decryptNotifyResult = wxPayRefundNotifyV3Result.getResult();
            String status = decryptNotifyResult.getRefundStatus();
            if("SUCCESS".equals(status) || "CLOSE".equals(status)) {
                payOrderRefundService.updateRefundInfo(decryptNotifyResult.getOutRefundNo(), PayOrderRefundStatusEnum.REFUND_SUCCESS.getValue(),
                        DateUtil.parse(decryptNotifyResult.getSuccessTime()));
            } else if("PROCESSING".equals(status)) {
                payOrderRefundService.updateRefundInfo(decryptNotifyResult.getOutRefundNo(), PayOrderRefundStatusEnum.REFUND_SUCCESS.getValue(),
                        null);
            } else if("ABNORMAL".equals(status)) {
                payOrderRefundService.updateRefundInfo(decryptNotifyResult.getOutRefundNo(), PayOrderRefundStatusEnum.REFUND_SUCCESS.getValue(),
                        null);
            }
            return WxPayNotifyV3Response.success("成功");
        } catch (WxPayException e) {
            log.error(">>> 微信异步通知处理异常：", e);
            return WxPayNotifyV3Response.fail("失败");
        }
    }

    @Override
    public String authUrl() {
        String authUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + WxPayApiConfigKit.getAppId() +
                "&redirect_uri=" + commonProperties.getBackendUrl() + "/pay/wx/authNotifyUrl&response_type=code" +
                "&scope=snsapi_userinfo&connect_redirect=1#wechat_redirect";
        return QrCodeUtil.generateAsBase64(authUrl, QrConfig.create().setWidth(200).setHeight(200).setRatio(4), "png",
                ImgUtil.toImage(WX_PAY_LOGO_BASE64));
    }

    @Override
    public void authNotifyUrl(String code) throws IOException {
        if(ObjectUtil.isEmpty(code)) {
            throw new CommonException("授权回调错误，未获取到code");
        }
        String result = HttpUtil.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" +
                WxPayApiConfigKit.getAppId() + "&secret=" + WxPayApi.WX_WP_APP_SECRET + "&code=" + code +
                "&grant_type=authorization_code");
        JSONObject jsonObject = JSONUtil.parseObj(result);
        String openid = jsonObject.getStr("openid");
        HttpServletResponse response = CommonServletUtil.getResponse();
        if(ObjectUtil.isNotEmpty(openid)) {
            response.sendRedirect(tenApi.getCurrentTenDomain() + "/pay/sample/doJsPay?openid="+ openid);
        } else {
            CommonResponseUtil.renderError(response, "授权回调错误，未获取到openid");
        }
    }

    @Override
    public String accountQuery() {
        throw new CommonException("暂不支持微信商家账户余额查询");
    }

    @Override
    public void codePay(PayWxCodePayParam payWxCodePayParam) {
        String outTradeNo = IdWorker.getIdStr();
        PayOrder payOrder;
        try {
            WxPayMicropayRequest wxPayMicropayRequest = new WxPayMicropayRequest();
            wxPayMicropayRequest.setBody(PayOrderConstants.ORDER_SUBJECT);
            wxPayMicropayRequest.setOutTradeNo(outTradeNo);
            wxPayMicropayRequest.setTotalFee(PayOrderConstants.ORDER_TOTAL_AMOUNT_FEN);
            wxPayMicropayRequest.setSpbillCreateIp(CommonIpAddressUtil.getIp(CommonServletUtil.getRequest()));
            wxPayMicropayRequest.setAuthCode(payWxCodePayParam.getAuthCode());
            WxPayApi.me().micropay(wxPayMicropayRequest);
            // 查询订单
            payOrder = payOrderService.getOne(new LambdaQueryWrapper<PayOrder>().eq(PayOrder::getOutTradeNo, payWxCodePayParam.getOutTradeNo()));
            // 更新支付结果
            this.doSyncForTradeQuery(payOrder);
        } catch (WxPayException e) {
            throw new CommonException("微信付款码支付异常：{}", e.getErrCodeDes());
        }
    }

    @Override
    public String qrPay(PayWxQrParam payWxQrParam) {
        WxPayUnifiedOrderV3Result wxPayUnifiedOrderV3Result;
        try {
            WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
            wxPayUnifiedOrderV3Request.setDescription(PayOrderConstants.ORDER_SUBJECT);
            wxPayUnifiedOrderV3Request.setOutTradeNo(payWxQrParam.getOutTradeNo());
            wxPayUnifiedOrderV3Request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(PayOrderConstants.ORDER_TOTAL_AMOUNT_FEN));
            wxPayUnifiedOrderV3Request.setNotifyUrl(WxPayApiConfigKit.getWxPayConfig().getNotifyUrl());
            wxPayUnifiedOrderV3Result = WxPayApi.me().unifiedOrderV3(TradeTypeEnum.NATIVE, wxPayUnifiedOrderV3Request);
        } catch (WxPayException e) {
            throw new CommonException("微信扫码支付二维码创建异常：{}", e.getCustomErrorMsg());
        }
        return QrCodeUtil.generateAsBase64(wxPayUnifiedOrderV3Result.getCodeUrl(), QrConfig.create().setWidth(200)
                .setHeight(200).setRatio(4), "png", ImgUtil.toImage(WX_PAY_LOGO_BASE64));
    }

    @Override
    public JSONObject jsPay(PayWxJsParam payWxJsParam) {
        WxPayUnifiedOrderV3Result wxPayUnifiedOrderV3Result;
        try {
            WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
            wxPayUnifiedOrderV3Request.setDescription(PayOrderConstants.ORDER_SUBJECT);
            wxPayUnifiedOrderV3Request.setOutTradeNo(payWxJsParam.getOutTradeNo());
            wxPayUnifiedOrderV3Request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(PayOrderConstants.ORDER_TOTAL_AMOUNT_FEN));
            wxPayUnifiedOrderV3Request.setPayer(new WxPayUnifiedOrderV3Request.Payer().setOpenid(payWxJsParam.getOpenid()));
            wxPayUnifiedOrderV3Request.setNotifyUrl(WxPayApiConfigKit.getWxPayConfig().getNotifyUrl());
            wxPayUnifiedOrderV3Result = WxPayApi.me().unifiedOrderV3(TradeTypeEnum.JSAPI, wxPayUnifiedOrderV3Request);
        } catch (WxPayException e) {
            throw new CommonException("微信JSAPI支付异常：{}", e.getCustomErrorMsg());
        }
        String appId = WxPayApiConfigKit.getAppId();
        String timeStamp = Convert.toStr(DateUtil.current());
        String nonceStr = IdUtil.simpleUUID();
        String packageStr = "prepay_id=" + wxPayUnifiedOrderV3Result.getPrepayId();
        String toSignStr = appId + "\n" + timeStamp + "\n" + nonceStr + "\n" + packageStr + "\n";
        String paySign =  SignUtils.sign(toSignStr, PemUtils.loadPrivateKey(FileUtil
                .getInputStream(WxPayApiConfigKit.getWxPayConfig().getPrivateKeyPath())));
        return JSONUtil.createObj().set("appId", appId)
                .set("timeStamp", timeStamp)
                .set("nonceStr", nonceStr)
                .set("package", packageStr)
                .set("signType", AsymmetricAlgorithm.RSA)
                .set("paySign", paySign);
    }

    @Override
    public String h5Pay(PayWxH5Param payWxH5Param) {
        WxPayUnifiedOrderV3Result wxPayUnifiedOrderV3Result;
        try {
            WxPayUnifiedOrderV3Request wxPayUnifiedOrderV3Request = new WxPayUnifiedOrderV3Request();
            wxPayUnifiedOrderV3Request.setDescription(PayOrderConstants.ORDER_SUBJECT);
            wxPayUnifiedOrderV3Request.setOutTradeNo(payWxH5Param.getOutTradeNo());
            wxPayUnifiedOrderV3Request.setAmount(new WxPayUnifiedOrderV3Request.Amount().setTotal(PayOrderConstants.ORDER_TOTAL_AMOUNT_FEN));
            wxPayUnifiedOrderV3Request.setSceneInfo(new WxPayUnifiedOrderV3Request.SceneInfo()
                    .setPayerClientIp(CommonIpAddressUtil.getIp(CommonServletUtil.getRequest()))
                    .setH5Info(new WxPayUnifiedOrderV3Request.H5Info().setType("Wap")));
            wxPayUnifiedOrderV3Request.setNotifyUrl(WxPayApiConfigKit.getWxPayConfig().getNotifyUrl());
            wxPayUnifiedOrderV3Result = WxPayApi.me().unifiedOrderV3(TradeTypeEnum.H5, wxPayUnifiedOrderV3Request);
        } catch (WxPayException e) {
            throw new CommonException("微信H5支付异常：{}", e.getCustomErrorMsg());
        }
        return wxPayUnifiedOrderV3Result.getH5Url();
    }

    @Override
    public WxPayOrderQueryV3Result tradeQuery(String outTradeNo) {
        try {
            WxPayOrderQueryV3Request wxPayOrderQueryV3Request = new WxPayOrderQueryV3Request();
            wxPayOrderQueryV3Request.setOutTradeNo(outTradeNo);
            return WxPayApi.me().queryOrderV3(wxPayOrderQueryV3Request);
        } catch (WxPayException e) {
            log.error(">>> 微信交易查询异常：", e);
           return null;
        }
    }

    @Override
    public WxPayRefundQueryV3Result refundQuery(String outTradeNo, String outRequestNo) {
        try {
            WxPayRefundQueryV3Request wxPayRefundQueryV3Request = new WxPayRefundQueryV3Request();
            wxPayRefundQueryV3Request.setOutRefundNo(outRequestNo);
            return WxPayApi.me().refundQueryV3(wxPayRefundQueryV3Request);
        } catch (WxPayException e) {
            log.error(">>> 微信退款查询异常：", e);
            return null;
        }
    }

    @Override
    public void doRefund(PayOrder payOrder, String refundAmount) {
        WxPayRefundV3Result wxPayRefundV3Result;
        String refundNo = IdWorker.getIdStr();
        try {
            WxPayRefundV3Request wxPayRefundV3Request = new WxPayRefundV3Request();
            wxPayRefundV3Request.setOutTradeNo(payOrder.getOutTradeNo());
            wxPayRefundV3Request.setOutRefundNo(refundNo);
            wxPayRefundV3Request.setAmount(new WxPayRefundV3Request.Amount()
                    .setTotal(new Money(payOrder.getOrderAmount()).multiply(100).getAmount().intValue())
                    .setCurrency(Money.DEFAULT_CURRENCY_CODE)
                    .setRefund(new Money(refundAmount).multiply(100).getAmount().intValue()));
            wxPayRefundV3Request.setNotifyUrl(WxPayApi.REFUND_NOTIFY_URL);
            wxPayRefundV3Result = WxPayApi.me().refundV3(wxPayRefundV3Request);
        } catch (WxPayException e) {
            throw new CommonException(e.getCustomErrorMsg());
        }
        String status = wxPayRefundV3Result.getStatus();
        if("SUCCESS".equals(status) || "CLOSED".equals(status)) {
            payOrderRefundService.doCreateOrderRefund(payOrder, wxPayRefundV3Result.getRefundId(), refundNo,
                    payOrder.getPayUserId(), wxPayRefundV3Result.getUserReceivedAccount(), refundAmount,
                    PayOrderRefundStatusEnum.REFUND_SUCCESS.getValue(), DateUtil.parse(wxPayRefundV3Result.getSuccessTime()));
        } else if("PROCESSING".equals(status)) {
            payOrderRefundService.doCreateOrderRefund(payOrder, wxPayRefundV3Result.getRefundId(), refundNo,
                    payOrder.getPayUserId(), wxPayRefundV3Result.getUserReceivedAccount(), refundAmount,
                    PayOrderRefundStatusEnum.REFUND_PENDING.getValue(), null);
        } else if("ABNORMAL".equals(status)){
            payOrderRefundService.doCreateOrderRefund(payOrder, wxPayRefundV3Result.getRefundId(), refundNo,
                    payOrder.getPayUserId(), wxPayRefundV3Result.getUserReceivedAccount(), refundAmount,
                    PayOrderRefundStatusEnum.REFUND_FAIL.getValue(), null);
        }
        payOrder.setHasRefund(PayYesNoEnum.Y.getValue());
        payOrderService.updateById(payOrder);
    }

    @Override
    public void doClose(PayOrder payOrder) {
        try {
            WxPayOrderCloseV3Request wxPayOrderCloseV3Request = new WxPayOrderCloseV3Request();
            wxPayOrderCloseV3Request.setOutTradeNo(payOrder.getOutTradeNo());
            WxPayApi.me().closeOrderV3(wxPayOrderCloseV3Request);
        } catch (WxPayException e) {
            throw new CommonException(e.getCustomErrorMsg());
        }
        payOrder.setPayStatus(PayOrderPayStatusEnum.TRADE_CLOSED.getValue());
        payOrderService.updateById(payOrder);
    }

    @Override
    public void doSyncForNotify(PayOrder payOrder, WxPayOrderNotifyV3Result wxPayOrderNotifyV3Result) {
        WxPayOrderNotifyV3Result.DecryptNotifyResult wxPayOrderNotifyV3ResultResult = wxPayOrderNotifyV3Result.getResult();
        String tradeStatus = wxPayOrderNotifyV3ResultResult.getTradeState();
        String tradeNo = wxPayOrderNotifyV3ResultResult.getTransactionId();
        String payUserId = wxPayOrderNotifyV3ResultResult.getPayer().getOpenid();
        String payAccount = wxPayOrderNotifyV3ResultResult.getPayer().getOpenid();
        String payAmount = Convert.toStr(new Money(0, wxPayOrderNotifyV3ResultResult.getAmount().getPayerTotal()).getAmount());
        String gmtPayment = wxPayOrderNotifyV3ResultResult.getSuccessTime();
        DateTime payTime = null;
        if(ObjectUtil.isNotEmpty(gmtPayment)) {
            payTime = DateUtil.parse(gmtPayment);
        }
        this.updateOrderInfo(payOrder, tradeStatus, tradeNo, payAmount, payUserId, payAccount, payTime);
    }

    @Override
    public void doSyncForTradeQuery(PayOrder payOrder) {
        WxPayOrderQueryV3Result wxPayOrderQueryV3Result = this.tradeQuery(payOrder.getOutTradeNo());
        if(ObjectUtil.isNotEmpty(wxPayOrderQueryV3Result)) {
            if(ObjectUtil.isNotEmpty(wxPayOrderQueryV3Result.getSuccessTime())) {
                this.updateOrderInfo(payOrder, wxPayOrderQueryV3Result.getTradeState(),
                        wxPayOrderQueryV3Result.getTransactionId(), Convert.toStr(new Money(0, wxPayOrderQueryV3Result.getAmount().getPayerTotal()).getAmount()),
                        wxPayOrderQueryV3Result.getPayer().getOpenid(), wxPayOrderQueryV3Result.getPayer().getOpenid(), DateUtil.parse(wxPayOrderQueryV3Result.getSuccessTime()));
            }
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
