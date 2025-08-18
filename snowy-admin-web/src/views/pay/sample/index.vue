<template>
	<a-alert :show-icon="false" banner class="mb-3">
		<template #message>
			支持 微信 扫码支付、JSAPI支付、H5支付、付款码支付、发起退款。
			<br />
			支持 支付宝 扫码支付、PC支付、WAP支付、付款码支付、发起退款。
		</template>
	</a-alert>
	<a-card title="订单信息" :bordered="false" class="mb-3">
		<a-row :gutter="24">
			<a-col :span="8">
				<a-tooltip>
					<template #title> 实际使用中订单编号需在后端按照业务规定时间戳字符串加批次或者随机码生成！ </template>
					<question-circle-outlined />
				</a-tooltip>
				订单编号：
				<span><a-tag color="cyan">接口自动生成</a-tag></span>
			</a-col>
			<a-col :span="8">
				<a-tooltip>
					<template #title> 实际使用中商品名称按实际名称传入对应支付API内！ </template>
					<question-circle-outlined />
				</a-tooltip>
				商品名称：
				<span><a-tag color="cyan">测试商品</a-tag></span>
			</a-col>
			<a-col :span="8">
				支付金额：<span><a-tag color="cyan">0.03</a-tag></span>
			</a-col>
		</a-row>
	</a-card>

	<a-card :bordered="false" class="mb-3">
		<a-row :gutter="16">
			<a-col :span="16">
				<p>微信支付：</p>
				<a-radio-group v-model:value="payType" :key="options.value" v-for="options in wxPay" class="xn-radio-wrapper">
					<a-radio-button :value="options.value">
						<img :src="'/img/pay/' + options.value + '.png'" class="xn-radio-wrapper-img" />
						{{ options.label }}
					</a-radio-button>
				</a-radio-group>
				<a-divider />
				<p>支付宝支付：</p>
				<a-radio-group v-model:value="payType" :key="options.value" v-for="options in aliPay" class="xn-radio-wrapper">
					<a-radio-button :value="options.value">
						<img :src="'/img/pay/' + options.value + '.png'" class="xn-radio-wrapper-img" />
						{{ options.label }}
					</a-radio-button>
				</a-radio-group>
				<a-divider />
				<span class="pay-price-span">￥0.03</span>
				<a-button type="primary" size="large" @click="paySubmit" :loading="payLoading">立即支付</a-button>
				<a-modal
					v-model:visible="qrPayModalOpen"
					:title="payType === 'wxJsPay' ? '微信JSAPI支付' : '扫码支付'"
					@cancel="qrPayModalClose"
				>
					<div style="text-align: center">
						<img :src="qrPayModalBase64" />
						<p>支付完成后请在 订单管理 查看支付结果</p>
					</div>
				</a-modal>
				<a-modal
					v-model:visible="codePayModalOpen"
					title="付款码支付"
					@close="onCodePayClose"
					@ok="onCodePayOk"
					:confirmLoading="submitCodePayLoading"
				>
					<a-form ref="codePayFormRef" :model="codePayFormData" :rules="codePayFormRules" layout="vertical">
						<a-form-item :label="payType === 'wxCodePay' ? '微信付款码：' : '支付宝付款码：'" name="authCode">
							<a-input v-model:value="codePayFormData.authCode" placeholder="请输入付款码" allow-clear />
						</a-form-item>
					</a-form>
					<div style="text-align: center">
						<img src="/img/pay/codePayBar.png" style="width: 260px" />
						<p class="mt-2">请使用扫码枪或者输入支付条码号</p>
						<p>支付完成后请在 订单管理 查看支付结果</p>
					</div>
				</a-modal>
			</a-col>
			<a-col :span="8">
				<p>余额查询：</p>
				<a-alert :show-icon="false" banner class="mb-3">
					<template #message> 该功能在产品内正常使用，演示界面暂不提供查询显示余额。 </template>
				</a-alert>
				<a-divider />
				<wechat-outlined style="font-size: 25px; color: #00bb00" />
				<span class="pay-price-span" style="margin-left: 10px; font-size: 18px">￥待查询...</span>
				<a-button type="link" @click="wxAccountQuery" :loading="wxAccountQueryLoading">点击查询</a-button>
				<a-divider />
				<alipay-outlined style="font-size: 25px; color: #1890ff" />
				<span class="pay-price-span" style="margin-left: 10px; font-size: 18px">￥{{ aliPayAccountNumber }}</span>
				<a-button type="link" @click="aliAccountQuery" :loading="aliAccountQueryLoading">点击查询</a-button>
			</a-col>
		</a-row>
	</a-card>

	<a-card title="付款须知" :bordered="false">
		本次支付只提供支付接口功能演示，无实际产品或服务。付款后您可在订单页面发起退款。
	</a-card>
</template>

<script setup name="paySampleIndex">
	import { message, Modal } from 'ant-design-vue'
	import { required } from '@/utils/formRules'
	import wxPayApi from '@/api/pay/wxPayApi'
	import aliPayApi from '@/api/pay/aliPayApi'
	import orderSampleApi from '@/api/pay/orderSampleApi'

	const payType = ref()
	const qrPayModalOpen = ref(false)
	const qrPayModalBase64 = ref('')
	const payLoading = ref(false)
	const wxAccountQueryLoading = ref(false)
	const aliAccountQueryLoading = ref(false)
	const aliPayAccountNumber = ref('待查询...')
	// 支付宝支付方式
	const aliPay = [
		{
			label: '扫码支付',
			value: 'aliQrPay'
		},
		{
			label: 'PC支付',
			value: 'aliPcPay'
		},
		{
			label: 'WAP支付',
			value: 'aliWapPay'
		},
		{
			label: '付款码支付',
			value: 'aliCodePay'
		},
		{
			label: 'APP支付',
			value: 'aliAppPay'
		},
		{
			label: '小程序支付',
			value: 'aliAppletPay'
		}
	]
	// 微信支付方式
	const wxPay = [
		{
			label: '扫码支付',
			value: 'wxQrPay'
		},
		{
			label: 'JSAPI支付',
			value: 'wxJsPay'
		},
		{
			label: 'H5支付',
			value: 'wxH5Pay'
		},
		{
			label: '付款码支付',
			value: 'wxCodePay'
		},
		{
			label: 'APP支付',
			value: 'wxAppPay'
		},
		{
			label: '小程序支付',
			value: 'wxAppletPay'
		}
	]
	// 提交支付
	const paySubmit = () => {
		if (!payType.value) {
			message.warning('请选择支付方式')
			return
		}
		payLoading.value = true
		if (payType.value === 'wxQrPay') {
			wxQrPay()
		} else if (payType.value === 'wxJsPay') {
			wxJsPay()
		} else if (payType.value === 'wxH5Pay') {
			wxH5Pay()
		} else if (payType.value === 'wxCodePay') {
			wxCodePay()
		} else if (payType.value === 'wxAppPay') {
			wxAppPay()
		} else if (payType.value === 'wxAppletPay') {
			wxAppletPay()
		} else if (payType.value === 'aliQrPay') {
			aliQrPay()
		} else if (payType.value === 'aliPcPay') {
			aliPcPay()
		} else if (payType.value === 'aliWapPay') {
			aliWapPay()
		} else if (payType.value === 'aliCodePay') {
			aliCodePay()
		} else if (payType.value === 'aliAppPay') {
			aliAppPay()
		} else if (payType.value === 'aliAppletPay') {
			aliAppletPay()
		} else {
			payLoading.value = false
			message.warning('无效的支付方式')
		}
	}
	// 微信扫码支付
	const wxQrPay = () => {
		const createOrderParams = {
			payPlatform: 'WXPAY'
		}
		orderSampleApi.payOrderDoCreateOrder(createOrderParams).then((data) => {
			message.success('订单创建成功')
			const payParams = {
				outTradeNo: data.outTradeNo
			}
			wxPayApi
				.wxQrPay(payParams)
				.then((data) => {
					qrPayModalOpen.value = true
					qrPayModalBase64.value = data
				})
				.finally(() => {
					payLoading.value = false
					payType.value = ''
				})
		})
	}
	// 微信JSAPI支付
	const wxJsPay = () => {
		wxPayApi
			.wxAuthUrl()
			.then((data) => {
				qrPayModalOpen.value = true
				qrPayModalBase64.value = data
			})
			.finally(() => {
				payLoading.value = false
			})
	}
	// 微信H5支付
	const wxH5Pay = () => {
		const createOrderParams = {
			payPlatform: 'WXPAY'
		}
		orderSampleApi.payOrderDoCreateOrder(createOrderParams).then((data) => {
			message.success('订单创建成功')
			const payParams = {
				outTradeNo: data.outTradeNo
			}
			wxPayApi
				.wxH5Pay(payParams)
				.then((data) => {
					window.open(data, '_blank')
					modalConfirm()
				})
				.finally(() => {
					payLoading.value = false
					payType.value = ''
				})
		})
	}
	// 微信付款码支付
	const wxCodePay = () => {
		const createOrderParams = {
			payPlatform: 'WXPAY'
		}
		orderSampleApi
			.payOrderDoCreateOrder(createOrderParams)
			.then((data) => {
				message.success('订单创建成功')
				codePayModalOpen.value = true
				codePayFormData.value.outTradeNo = data.outTradeNo
			})
			.finally(() => {
				payLoading.value = false
			})
	}
	// 微信小程序支付
	const wxAppletPay = () => {
		window.open('https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_8_0.shtml', '_blank')
		payLoading.value = false
		payType.value = ''
	}
	// 微信APP支付
	const wxAppPay = () => {
		window.open('https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_5_0.shtml', '_blank')
		payLoading.value = false
		payType.value = ''
	}
	// 支付宝扫码支付
	const aliQrPay = () => {
		const createOrderParams = {
			payPlatform: 'ALIPAY'
		}
		orderSampleApi.payOrderDoCreateOrder(createOrderParams).then((data) => {
			message.success('订单创建成功')
			const payParams = {
				outTradeNo: data.outTradeNo
			}
			aliPayApi
				.aliQrPay(payParams)
				.then((data) => {
					qrPayModalOpen.value = true
					qrPayModalBase64.value = data
				})
				.finally(() => {
					payLoading.value = false
					payType.value = ''
				})
		})
	}
	// 支付宝PC支付
	const aliPcPay = () => {
		const createOrderParams = {
			payPlatform: 'ALIPAY'
		}
		orderSampleApi.payOrderDoCreateOrder(createOrderParams).then((data) => {
			message.success('订单创建成功')
			const payParams = {
				outTradeNo: data.outTradeNo
			}
			aliPayApi
				.aliPcPay(payParams)
				.then((data) => {
					window.open(data, '_blank')
					modalConfirm()
				})
				.finally(() => {
					payLoading.value = false
					payType.value = ''
				})
		})
	}
	// 支付宝WAP支付
	const aliWapPay = () => {
		const createOrderParams = {
			payPlatform: 'ALIPAY'
		}
		orderSampleApi.payOrderDoCreateOrder(createOrderParams).then((data) => {
			message.success('订单创建成功')
			const payParams = {
				outTradeNo: data.outTradeNo
			}
			aliPayApi
				.aliWapPay(payParams)
				.then((data) => {
					window.open(data, '_blank')
				})
				.finally(() => {
					payLoading.value = false
					payType.value = ''
				})
		})
	}
	// 支付宝支付码支付
	const aliCodePay = () => {
		const createOrderParams = {
			payPlatform: 'ALIPAY'
		}
		orderSampleApi
			.payOrderDoCreateOrder(createOrderParams)
			.then((data) => {
				message.success('订单创建成功')
				codePayModalOpen.value = true
				codePayFormData.value.outTradeNo = data.outTradeNo
			})
			.finally(() => {
				payLoading.value = false
			})
	}
	// 支付宝小程序支付
	const aliAppletPay = () => {
		window.open('https://opendocs.alipay.com/mini/03l5wk?pathHash=6ed8a077', '_blank')
		payLoading.value = false
		payType.value = ''
	}
	// 支付宝APP支付
	const aliAppPay = () => {
		window.open('https://open.alipay.com/api/detail?code=I1080300001000041313', '_blank')
		payLoading.value = false
		payType.value = ''
	}
	// 关闭扫码支付弹窗
	const qrPayModalClose = () => {
		payType.value = ''
	}
	// 支付询问
	const modalConfirm = () => {
		Modal.confirm({
			title: '请确认！',
			okText: '已支付完成',
			cancelText: '未支付',
			content: '您发起的订单支付是否已支付完成？',
			onOk() {
				return new Promise((resolve, reject) => {
					setTimeout(Math.random() > 0.5 ? resolve : reject, 1000)
				}).catch(() => console.log('Oops errors!'))
			},
			onCancel() {
				// 取消选中的
				payType.value = ''
			}
		})
	}
	// 微信查询余额
	const wxAccountQuery = () => {
		wxAccountQueryLoading.value = true
		wxPayApi
			.wxAccountQuery()
			.then((data) => {})
			.finally(() => {
				wxAccountQueryLoading.value = false
			})
	}
	// 支付宝查询余额
	const aliAccountQuery = () => {
		aliAccountQueryLoading.value = true
		aliPayApi
			.aliAccountQuery()
			.then((data) => {
				aliPayAccountNumber.value = data
			})
			.finally(() => {
				aliAccountQueryLoading.value = false
			})
	}
	// 之下是付款码弹框的
	const codePayModalOpen = ref(false)
	const codePayFormRef = ref()
	const codePayFormData = ref({})
	const submitCodePayLoading = ref(false)
	// 默认要校验的
	const codePayFormRules = {
		authCode: [required('请输入付款码')]
	}
	const onCodePayClose = () => {
		codePayModalOpen.value = false
		payType.value = ''
		codePayFormData.value = {}
	}
	const onCodePayOk = () => {
		codePayFormRef.value.validate().then(() => {
			submitCodePayLoading.value = true
			const params = {
				authCode: codePayFormData.value.authCode,
				outTradeNo: codePayFormData.value.outTradeNo
			}
			if (payType.value === 'wxCodePay') {
				wxPayApi
					.wxCodePay(params)
					.then(() => {
						message.success('请在手机上确认付款并查看订单状态！')
						codePayModalOpen.value = false
					})
					.finally(() => {
						submitCodePayLoading.value = false
						payType.value = ''
					})
			} else if (payType.value === 'aliCodePay') {
				aliPayApi
					.aliCodePay(params)
					.then(() => {
						message.success('请在手机上确认付款并查看订单状态！')
						codePayModalOpen.value = false
					})
					.finally(() => {
						submitCodePayLoading.value = false
						payType.value = ''
					})
			}
		})
	}
</script>

<style scoped>
	.xn-radio-wrapper {
		padding: 0 5px;
		line-height: 20px;
		font-size: 14px;
	}
	.xn-radio-wrapper-img {
		width: 40px;
		height: 40px;
		vertical-align: middle;
		margin-right: 10px;
	}
	.ant-radio-button-wrapper {
		height: auto;
		padding: 10px 10px;
	}
	.pay-price-span {
		margin-right: 20px;
		color: rgb(255, 102, 0);
		font-size: 20px;
	}
</style>
