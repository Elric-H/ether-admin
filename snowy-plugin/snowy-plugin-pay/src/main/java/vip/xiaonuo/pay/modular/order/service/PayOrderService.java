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
package vip.xiaonuo.pay.modular.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.pay.modular.order.entity.PayOrder;
import vip.xiaonuo.pay.modular.order.entity.PayOrderRefund;
import vip.xiaonuo.pay.modular.order.param.PayOrderCreateParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderIdParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderPageParam;
import vip.xiaonuo.pay.modular.order.param.PayOrderRefundParam;

import java.util.List;

/**
 * 订单Service接口
 *
 * @author xuyuxiang
 * @date 2023/3/27 15:23
 **/
public interface PayOrderService extends IService<PayOrder> {

    /**
     * 创建订单
     *
     * @author xuyuxiang
     * @date 2023/4/10 13:25
     **/
    PayOrder createOrder(PayOrderCreateParam payOrderCreateParam);

    /**
     * 获取订单分页
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    Page<PayOrder> page(PayOrderPageParam payOrderPageParam);

    /**
     * 获取退款列表
     *
     * @author xuyuxiang
     * @date 2022/4/24 20:08
     */
    List<PayOrderRefund> refundList(PayOrderIdParam payOrderIdParam);

    /**
     * 执行退款
     *
     * @author xuyuxiang
     * @date 2023/3/28 13:18
     **/
    void doRefund(PayOrderRefundParam payOrderRefundParam);

    /**
     * 删除订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void delete(List<PayOrderIdParam> payOrderIdParamList);

    /**
     * 获取配置详情
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    PayOrder queryEntity(String id);

    /**
     * 同步订单
     *
     * @author xuyuxiang
     * @date 2022/4/24 21:18
     */
    void sync(List<PayOrderIdParam> payOrderIdParamList);

    /**
     * 关闭订单
     *
     * @author xuyuxiang
     * @date 2023/3/30 9:29
     **/
    void close(List<PayOrderIdParam> payOrderIdParamList);
}
