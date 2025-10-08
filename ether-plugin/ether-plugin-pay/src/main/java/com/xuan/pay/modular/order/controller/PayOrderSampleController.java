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
package com.xuan.pay.modular.order.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.xuan.common.pojo.CommonResult;
import com.xuan.pay.modular.order.entity.PayOrder;
import com.xuan.pay.modular.order.param.PayOrderCreateParam;
import com.xuan.pay.modular.order.service.PayOrderService;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 订单示例控制器
 *
 * @author yubaoshan
 * @date 2022/2/23 18:26
 **/
@Api(tags = "订单示例控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 4)
@RestController
@Validated
public class PayOrderSampleController {

    @Resource
    private PayOrderService payOrderService;

    /**
     * 创建订单
     *
     * @author yubaoshan
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("创建订单")
    @PostMapping("/pay/order/sample/doCreateOrder")
    public CommonResult<PayOrder> doCreateOrder(@Valid @RequestBody PayOrderCreateParam payOrderCreateParam) {
        PayOrder payOrder = payOrderService.createOrder(payOrderCreateParam);
        return CommonResult.data(payOrder);
    }
}
