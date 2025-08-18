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
package vip.xiaonuo.pay.modular.ali.service;

import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import vip.xiaonuo.pay.modular.ali.param.PayAliCodePayParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliPcParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliQrParam;
import vip.xiaonuo.pay.modular.ali.param.PayAliWapParam;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付宝Service接口
 *
 * @author xuyuxiang
 * @date 2023/3/21 16:14
 **/
public interface PayAliService {

    /**
     * 支付回调通知
     *
     * @author xuyuxiang
     * @date 2023/3/27 14:25
     **/
    String notifyUrl(HttpServletRequest request);

    /**
     * 商家账户余额查询
     *
     * @author xuyuxiang
     * @date 2023/3/30 13:18
     **/
    String accountQuery();

    /**
     * 支付宝付款码支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    void codePay(PayAliCodePayParam payAliCodePayParam);

    /**
     * 支付宝扫码支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    String qrPay(PayAliQrParam payAliQrParam);

    /**
     * 支付宝PC支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:17
     **/
    String pcPay(PayAliPcParam payAliPcParam);

    /**
     * 支付宝WAP支付
     *
     * @author xuyuxiang
     * @date 2023/3/21 16:21
     **/
    String wapPay(PayAliWapParam payAliWapParam);

    /**
     * 交易查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    AlipayTradeQueryResponse tradeQuery(String outTradeNo);

    /**
     * 退款查询
     *
     * @author xuyuxiang
     * @date 2023/3/28 14:19
     **/
    AlipayTradeFastpayRefundQueryResponse refundQuery(String outTradeNo, String outRequestNo);

    /**
     * 执行退款
     *
     * @author xuyuxiang
     * @date 2023/3/29 21:40
     */
    void doRefund(PayOrder payOrder, String refundAmount);

    /**
     * 执行关闭
     *
     * @author xuyuxiang
     * @date 2023/3/29 21:40
     */
    void doClose(PayOrder payOrder);

    /**
     * 执行同步，用于异步通知
     *
     * @author xuyuxiang
     * @date 2023/3/29 19:48
     */
    void doSyncForNotify(PayOrder payOrder, Map<String, String> params);

    /**
     * 执行同步，用于主动查询
     *
     * @author xuyuxiang
     * @date 2023/3/29 19:48
     */
    void doSyncForTradeQuery(PayOrder payOrder);
}
