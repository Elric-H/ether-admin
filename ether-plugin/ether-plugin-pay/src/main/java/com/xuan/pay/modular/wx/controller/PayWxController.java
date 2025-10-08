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
package com.xuan.pay.modular.wx.controller;

import cn.hutool.json.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.xuan.common.pojo.CommonResult;
import com.xuan.pay.modular.wx.param.PayWxCodePayParam;
import com.xuan.pay.modular.wx.param.PayWxH5Param;
import com.xuan.pay.modular.wx.param.PayWxJsParam;
import com.xuan.pay.modular.wx.param.PayWxQrParam;
import com.xuan.pay.modular.wx.service.PayWxService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 微信控制器
 *
 * @author xuyuxiang
 * @date 2022/8/16 14:23
 **/
@Api(tags = "微信控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 2)
@RestController
@Validated
public class PayWxController {

    @Resource
    private PayWxService payWxService;

    /**
     * 支付回调通知
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("支付回调通知")
    @PostMapping("/pay/wx/notifyUrl")
    public String notifyUrl(@RequestBody String notifyData) {
        return payWxService.notifyUrl(notifyData);
    }

    /**
     * 退款回调通知
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("退款回调通知")
    @PostMapping("/pay/wx/refundNotifyUrl")
    public String refundNotifyUrl(@RequestBody String notifyData) {
        return payWxService.refundNotifyUrl(notifyData);
    }

    /**
     * 获取授权地址
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("获取授权地址")
    @GetMapping("/pay/wx/authUrl")
    public CommonResult<String> authUrl() {
        return CommonResult.data(payWxService.authUrl());
    }

    /**
     * 授权回调通知
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("授权回调通知")
    @GetMapping("/pay/wx/authNotifyUrl")
    public void authNotifyUrl(String code) throws IOException {
        payWxService.authNotifyUrl(code);
    }

    /**
     * 商家账户余额查询
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("商家账户余额查询")
    @GetMapping("/pay/wx/accountQuery")
    public CommonResult<String> accountQuery() {
        return CommonResult.data(payWxService.accountQuery());
    }

    /**
     * 微信付款码支付
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("微信付款码支付")
    @GetMapping("/pay/wx/codePay")
    public CommonResult<String> codePay(@Valid PayWxCodePayParam payWxCodePayParam) {
        payWxService.codePay(payWxCodePayParam);
        return CommonResult.ok();
    }

    /**
     * 微信扫码支付，返回二维码base64
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("微信扫码支付")
    @GetMapping("/pay/wx/qrPay")
    public CommonResult<String> qrPay(@Valid PayWxQrParam payWxQrParam) {
        return CommonResult.data(payWxService.qrPay(payWxQrParam));
    }

    /**
     * 微信JSAPI支付，返回支付所需参数
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 8)
    @ApiOperation("微信JSAPI支付")
    @GetMapping("/pay/wx/jsPay")
    public CommonResult<JSONObject> jsPay(@Valid PayWxJsParam payWxJsParam) {
        return CommonResult.data(payWxService.jsPay(payWxJsParam));
    }

    /**
     * 微信H5支付，返回支付跳转链接
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 9)
    @ApiOperation("微信H5支付")
    @GetMapping("/pay/wx/h5Pay")
    public CommonResult<String> h5Pay(PayWxH5Param payWxH5Param) {
        return CommonResult.data(payWxService.h5Pay(payWxH5Param));
    }
}
