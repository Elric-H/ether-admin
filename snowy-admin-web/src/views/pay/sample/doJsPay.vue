<template>
	<a-spin :spinning="loading">
		<div style="margin: 10px">
			<a-result status="success" title="订单创建成功!" sub-title="您正在使用微信支付，请在微信支付弹出后支付完成！">
			</a-result>
		</div>
	</a-spin>
</template>

<script setup name="doJsPay">
	import { message } from 'ant-design-vue'
	import { onMounted } from 'vue'
	import { useRoute } from 'vue-router'
	import routers from '@/router'
	import wxPayApi from '@/api/pay/wxPayApi'
	import orderSampleApi from '@/api/pay/orderSampleApi'
	const router = useRoute()
	const loading = ref(false)

	onMounted(() => {
		if (!router.query.openid) {
			message.warning('无效的访问')
			routers.replace({ path: '/' })
			return
		}
		// 得到路由参数 router.query.openid
		loading.value = true
		onBridgeReady()
	})
	// 创建订单并向后端获得参数，并且调起微信支付
	const onBridgeReady = () => {
		const createOrderParams = {
			payPlatform: 'WXPAY'
		}
		orderSampleApi.payOrderDoCreateOrder(createOrderParams).then((data) => {
			const params = {
				openid: router.query.openid,
				outTradeNo: data.outTradeNo
			}
			wxPayApi
				.wxJsPay(params)
				.then((data) => {
					/** 微信调起开始 **/
					// 注意，该对象在普通浏览器并不支持，只有在微信浏览器才能生效
					WeixinJSBridge.invoke(
						'getBrandWCPayRequest',
						{
							appId: data.appId, // 微信的appid
							timeStamp: data.timeStamp, // 时间戳
							nonceStr: data.nonceStr, // 随机串
							package: data.package,
							signType: data.signType, // 微信签名方式RSA
							paySign: data.paySign // 微信签名
						},
						function (res) {
							if (res.err_msg === 'get_brand_wcpay_request:ok') {
								// 使用以上方式判断前端返回,微信团队郑重提示：
								// res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
								message.success('支付成功')
								// 这里可以加载一个倒计时什么的，然后遍历获取notify的支付结果
								goBack()
							} else if (res.err_msg === 'get_brand_wcpay_request:cancel') {
								message.info('已取消支付')
							} else if (res.err_msg === 'get_brand_wcpay_request:fail') {
								message.error('支付失败')
							} else {
								message.error('支付回应未知错误')
							}
						}
					)
					/** 微信调起结束 **/
				})
				.finally(() => {
					loading.value = false
				})
		})
	}
	// 返回首页
	const goBack = () => {
		routers.replace({ path: '/' })
	}
</script>
