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
package vip.xiaonuo.pay.modular.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import vip.xiaonuo.common.pojo.CommonEntity;

import java.util.Date;

/**
 * 订单实体
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:27
 **/
@Getter
@Setter
@TableName("PAY_ORDER")
public class PayOrder extends CommonEntity {

    /** id */
    @ApiModelProperty(value = "主键", position = 1)
    private String id;

    /** 租户id */
    @ApiModelProperty(value = "租户id", position = 2)
    private String tenantId;

    /** 商户订单号 */
    @ApiModelProperty(value = "商户订单号", position = 3)
    private String outTradeNo;

    /** 支付平台订单号 */
    @ApiModelProperty(value = "支付平台订单号", position = 4)
    private String tradeNo;

    /** 订单标题 */
    @ApiModelProperty(value = "订单标题", position = 5)
    private String subject;

    /** 订单描述 */
    @ApiModelProperty(value = "订单描述", position = 6)
    private String body;

    /** 订单金额 */
    @ApiModelProperty(value = "订单金额", position = 7)
    private String orderAmount;

    /** 买家id */
    @ApiModelProperty(value = "买家id", position = 8)
    private String payUserId;

    /** 买家账号 */
    @ApiModelProperty(value = "买家账号", position = 9)
    private String payAccount;

    /** 支付金额 */
    @ApiModelProperty(value = "支付金额", position = 10)
    private String payAmount;

    /** 支付平台 */
    @ApiModelProperty(value = "支付平台", position = 11)
    private String payPlatform;

    /** 支付状态 */
    @ApiModelProperty(value = "支付状态", position = 12)
    private String payStatus;

    /** 支付时间 */
    @ApiModelProperty(value = "支付时间", position = 13)
    private Date payTime;

    /** 是否有退款 */
    @ApiModelProperty(value = "是否有退款", position = 14)
    private String hasRefund;
}
