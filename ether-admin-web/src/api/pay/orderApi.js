import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/pay/order/` + url, ...arg)

/**
 * 订单管理Api接口管理器
 *
 * @author yubaoshan
 * @date  2023/03/30 00:20
 **/
export default {
	// 获取订单分页
	orderPage(data) {
		return request('page', data, 'get')
	},
	// 获取订单明细列表
	orderDetailsList(data) {
		return request('detailsList', data, 'get')
	},
	// 获取退款列表
	orderRefundList(data) {
		return request('refundList', data, 'get')
	},
	// 执行退款
	orderDoRefund(data) {
		return request('doRefund', data)
	},
	// 删除订单
	orderDelete(data) {
		return request('delete', data)
	},
	// 同步订单
	orderSync(data) {
		return request('sync', data)
	},
	// 关闭订单
	orderClose(data) {
		return request('close', data)
	}
}
