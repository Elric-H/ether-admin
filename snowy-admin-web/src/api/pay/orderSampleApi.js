import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/pay/order/sample/` + url, ...arg)

/**
 * 示例创建订单
 *
 * @author yubaoshan
 * @date  2023/04/09 00:25
 **/
export default {
	// 创建订单
	payOrderDoCreateOrder(data) {
		return request('doCreateOrder', data)
	}
}
