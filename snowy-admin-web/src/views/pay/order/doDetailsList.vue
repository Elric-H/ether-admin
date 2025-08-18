<template>
	<xn-form-container
		title="订单明细"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
		:footer="null"
	>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:showPagination="false"
			bordered
			:row-key="(record) => record.id"
		/>
	</xn-form-container>
</template>

<script setup name="orderDoDetailsList">
	import orderApi from '@/api/pay/orderApi'

	// 默认是关闭状态
	const visible = ref(false)
	const recordData = ref({})
	const tableRef = ref(null)
	const columns = [
		{
			title: '产品名称',
			dataIndex: 'productName',
			ellipsis: true
		},
		{
			title: '产品价格',
			dataIndex: 'productAmount',
			width: 100,
			ellipsis: true
		},
		{
			title: '产品数量',
			dataIndex: 'productCount',
			width: 100,
			ellipsis: true
		}
	]
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		recordData.value = record
	}
	// 关闭抽屉
	const onClose = () => {
		recordData.value = []
		visible.value = false
	}
	// 订单明细
	const loadData = (parameter) => {
		if (recordData.value) {
			parameter = {
				id: recordData.value.id
			}
			return orderApi.orderDetailsList(Object.assign(parameter)).then((data) => {
				return data
			})
		} else {
			return new Promise((resolve) => {
				resolve([])
			})
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
