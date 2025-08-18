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
package vip.xiaonuo.pay.modular.ali.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import vip.xiaonuo.common.pojo.CommonResult;
import vip.xiaonuo.pay.modular.ali.param.PayAliCodePayParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliPcParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliQrParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliWapParam;
import vip.xiaonuo.pay.modular.ali.service.PayAliService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 支付宝控制器
 *
 * @author xuyuxiang
 * @date 2022/8/16 14:23
 **/
@Api(tags = "支付宝控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 1)
@RestController
@Validated
public class PayAliController {

    @Resource
    private PayAliService payAliService;

    /**
     * 支付回调通知
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("支付回调通知")
    @PostMapping("/pay/ali/notifyUrl")
    public String notifyUrl(HttpServletRequest request) {
        return payAliService.notifyUrl(request);
    }

    /**
     * 商家账户余额查询
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("商家账户余额查询")
    @GetMapping("/pay/ali/accountQuery")
    public CommonResult<String> accountQuery() {
        return CommonResult.data(payAliService.accountQuery());
    }

    /**
     * 支付宝付款码支付
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("支付宝付款码支付")
    @GetMapping("/pay/ali/codePay")
    public CommonResult<String> codePay(@Valid PayAliCodePayParam payAliCodePayParam) {
        payAliService.codePay(payAliCodePayParam);
        return CommonResult.ok();
    }

    /**
     * 支付宝扫码支付，返回二维码base64
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("支付宝扫码支付")
    @GetMapping("/pay/ali/qrPay")
    public CommonResult<String> qrPay(@Valid PayAliQrParam payAliQrParam) {
        return CommonResult.data(payAliService.qrPay(payAliQrParam));
    }

    /**
     * 支付宝PC支付，返回PC网页地址
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("支付宝PC支付")
    @GetMapping("/pay/ali/pcPay")
    public CommonResult<String> pcPay(@Valid PayAliPcParam payAliPcParam) {
        return CommonResult.data(payAliService.pcPay(payAliPcParam));
    }

    /**
     * 支付宝WAP支付，返回WAP网页地址
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("支付宝WAP支付")
    @GetMapping("/pay/ali/wapPay")
    public CommonResult<String> wapPay(PayAliWapParam payAliWapParam) {
        return CommonResult.data(payAliService.wapPay(payAliWapParam));
    }
}
