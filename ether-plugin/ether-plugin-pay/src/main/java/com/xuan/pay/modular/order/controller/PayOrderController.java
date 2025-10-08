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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.xuan.common.annotation.CommonLog;
import com.xuan.common.pojo.CommonResult;
import com.xuan.common.pojo.CommonValidList;
import com.xuan.pay.modular.order.entity.PayOrder;
import com.xuan.pay.modular.order.entity.PayOrderDetails;
import com.xuan.pay.modular.order.entity.PayOrderRefund;
import com.xuan.pay.modular.order.param.PayOrderIdParam;
import com.xuan.pay.modular.order.param.PayOrderPageParam;
import com.xuan.pay.modular.order.param.PayOrderRefundParam;
import com.xuan.pay.modular.order.service.PayOrderDetailsService;
import com.xuan.pay.modular.order.service.PayOrderService;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 订单控制器
 *
 * @author xuyuxiang
 * @date 2022/2/23 18:26
 **/
@Api(tags = "订单控制器")
@ApiSupport(author = "SNOWY_TEAM", order = 3)
@RestController
@Validated
public class PayOrderController {

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private PayOrderDetailsService payOrderDetailsService;

    /**
     * 获取订单分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取订单分页")
    @GetMapping("/pay/order/page")
    public CommonResult<Page<PayOrder>> page(PayOrderPageParam payOrderPageParam) {
        return CommonResult.data(payOrderService.page(payOrderPageParam));
    }

    /**
     * 获取订单明细列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation("获取订单明细列表")
    @GetMapping("/pay/order/detailsList")
    public CommonResult<List<PayOrderDetails>> detailsList(@Valid PayOrderIdParam payOrderIdParam) {
        return CommonResult.data(payOrderDetailsService.orderDetailsList(payOrderIdParam.getId()));
    }

    /**
     * 获取退款列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation("获取退款列表")
    @GetMapping("/pay/order/refundList")
    public CommonResult<List<PayOrderRefund>> refundList(@Valid PayOrderIdParam payOrderIdParam) {
        return CommonResult.data(payOrderService.refundList(payOrderIdParam));
    }

    /**
     * 执行退款
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation("执行退款")
    @PostMapping("/pay/order/doRefund")
    public CommonResult<String> doRefund(@RequestBody @Valid PayOrderRefundParam payOrderRefundParam) {
        payOrderService.doRefund(payOrderRefundParam);
        return CommonResult.ok();
    }

    /**
     * 删除订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation("删除订单")
    @CommonLog("删除订单")
    @PostMapping("/pay/order/delete")
    public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<PayOrderIdParam> payOrderIdParamList) {
        payOrderService.delete(payOrderIdParamList);
        return CommonResult.ok();
    }

    /**
     * 同步订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation("同步订单")
    @CommonLog("同步订单")
    @PostMapping("/pay/order/sync")
    public CommonResult<String> sync(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                               CommonValidList<PayOrderIdParam> payOrderIdParamList) {
        payOrderService.sync(payOrderIdParamList);
        return CommonResult.ok();
    }

    /**
     * 关闭订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:00
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation("关闭订单")
    @CommonLog("关闭订单")
    @PostMapping("/pay/order/close")
    public CommonResult<String> close(@RequestBody @Valid @NotEmpty(message = "集合不能为空")
                                                  CommonValidList<PayOrderIdParam> payOrderIdParamList) {
        payOrderService.close(payOrderIdParamList);
        return CommonResult.ok();
    }
}
