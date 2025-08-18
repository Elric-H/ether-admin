<template>
	<xn-form-container
		title="发起退款"
		:width="1200"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
		:footer="null"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-alert type="info" :show-icon="false" banner class="mb-3">
				<template #message> 订单金额：{{ recordData.orderAmount }} </template>
			</a-alert>
			<a-form-item label="退费金额：" name="refundAmount">
				<a-input-number
					v-model:value="formData.refundAmount"
					placeholder="请输入退费金额"
					allow-clear
					:controls="false"
					style="width: 200px"
					:max="recordData.orderAmount"
				/>
				<a-button @click="balance">剩余全部</a-button>
				<a-button type="primary" class="ml-2" @click="onSubmit" :loading="submitLoading">退款</a-button>
			</a-form-item>
		</a-form>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:showPagination="false"
			bordered
			:tool-config="toolConfig"
			:row-key="(record) => record.id"
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'refundAccount'">
					{{ record.refundAccount }}
				</template>
				<template v-if="column.dataIndex === 'refundStatus'">
					{{ $TOOL.dictTypeData('PAY_ORDER_REFUND_STATUS', record.refundStatus) }}
				</template>
			</template>
		</s-table>
	</xn-form-container>
</template>

<script setup name="orderDoRefundForm">
	import { message } from 'ant-design-vue'
	import { required, rules } from '@/utils/formRules'
	import orderApi from '@/api/pay/orderApi'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	const recordData = ref({})
	const submitLoading = ref(false)
	const tableRef = ref(null)
	const toolConfig = { refresh: true, height: false, columnSetting: false, striped: false }
	const loadDataRefundAmount = ref(Number(0))
	const columns = [
		{
			title: '支付平台退款单号',
			dataIndex: 'tradeNo',
			ellipsis: true
		},
		{
			title: '商户退款单号',
			dataIndex: 'refundNo',
			ellipsis: true
		},
		{
			title: '退款到买家id',
			dataIndex: 'refundUserId',
			ellipsis: true
		},
		{
			title: '退款到买家账号',
			dataIndex: 'refundAccount',
			ellipsis: true
		},
		{
			title: '退款金额',
			dataIndex: 'refundAmount',
			width: 100
		},
		{
			title: '退款状态',
			dataIndex: 'refundStatus',
			width: 100
		},
		{
			title: '退款时间',
			dataIndex: 'refundTime',
			ellipsis: true
		}
	]
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		recordData.value = record
		formData.value = Object.assign({}, record)
	}
	// 关闭抽屉
	const onClose = () => {
		recordData.value = []
		visible.value = false
		loadDataRefundAmount.value = Number(0)
		emit('successful')
	}
	// 默认要校验的
	const formRules = {
		refundAmount: [required('请输入退费金额'), rules.price]
	}
	// 验证并提交数据
	const onSubmit = () => {
		if (formData.value.refundAmount === 0) {
			message.warning('无可退金额')
			return
		}
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const params = {
				id: recordData.value.id,
				refundAmount: formData.value.refundAmount
			}
			orderApi
				.orderDoRefund(params)
				.then(() => {
					tableRef.value.refresh(true)
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
	// 退款列表
	const loadData = (parameter) => {
		if (recordData.value) {
			parameter = {
				id: recordData.value.id
			}
			return orderApi.orderRefundList(Object.assign(parameter)).then((data) => {
				if (data) {
					// 计算列表内的已退金额
					data.forEach((item) => {
						loadDataRefundAmount.value = loadDataRefundAmount.value + Number(item.refundAmount)
					})
				}
				return data
			})
		} else {
			return new Promise((resolve) => {
				resolve([])
			})
		}
	}
	// 计算剩余可退金额
	const balance = () => {
		formData.value.refundAmount = (recordData.value.orderAmount * 100 - loadDataRefundAmount.value * 100) / 100
		if ((recordData.value.orderAmount * 100 - loadDataRefundAmount.value * 100) / 100 === 0) {
			message.warning('无可退金额')
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
