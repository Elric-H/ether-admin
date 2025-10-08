import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/pay/ali/` + url, ...arg)

/**
 * 支付宝控制器
 *
 * @author yubaoshan
 * @date  2023/03/30 00:20
 **/
export default {
	// 商家账户余额查询
	aliAccountQuery(data) {
		return request('accountQuery', data, 'get')
	},
	// 支付宝付款码支付
	aliCodePay(data) {
		return request('codePay', data, 'get')
	},
	// 支付宝扫码支付，返回二维码base64
	aliQrPay(data) {
		return request('qrPay', data, 'get')
	},
	// 支付宝PC支付，返回PC网页地址
	aliPcPay(data) {
		return request('pcPay', data, 'get')
	},
	// 支付宝PC支付，返回PC网页地址
	aliWapPay(data) {
		return request('wapPay', data, 'get')
	}
}
