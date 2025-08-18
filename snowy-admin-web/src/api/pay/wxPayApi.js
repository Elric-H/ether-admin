import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/pay/wx/` + url, ...arg)

/**
 * 微信支付体验API
 *
 * @author yubaoshan
 * @date  2023/03/30 00:20
 **/
export default {
	// 商家账户余额查询
	wxAccountQuery(data) {
		return request('accountQuery', data, 'get')
	},
	// 获取授权地址
	wxAuthUrl(data) {
		return request('authUrl', data, 'get')
	},
	// 微信扫码支付，返回二维码base64
	wxQrPay(data) {
		return request('qrPay', data, 'get')
	},
	// 微信JSAPI支付，返回订单信息
	wxJsPay(data) {
		return request('jsPay', data, 'get')
	},
	// 微信H5支付，返回订单信息
	wxH5Pay(data) {
		return request('h5Pay', data, 'get')
	},
	// 微信付款码支付
	wxCodePay(data) {
		return request('codePay', data, 'get')
	}
}
