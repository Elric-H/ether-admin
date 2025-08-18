<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="关键词" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入关键词订单号、标题、描述" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="支付平台" name="payPlatform">
						<a-select
							v-model:value="searchFormState.payPlatform"
							placeholder="请选择支付平台"
							:options="payPlatformOptions"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="支付状态" name="payStatus">
						<a-select
							v-model:value="searchFormState.payStatus"
							placeholder="请选择支付状态"
							:options="payStatusOptions"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="是否有退款" name="hasRefund">
						<a-select
							v-model:value="searchFormState.hasRefund"
							placeholder="请选择是否有退款"
							:options="hasRefundOptions"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="支付时间" name="payTime">
						<a-range-picker v-model:value="searchFormState.payTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" />
					</a-form-item>
				</a-col>
				<a-col :span="6" v-show="advanced">
					<a-form-item label="创建时间" name="createTime">
						<a-range-picker v-model:value="searchFormState.createTime" show-time valueFormat="YYYY-MM-DD HH:mm:ss" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
					<a @click="toggleAdvanced" style="margin-left: 8px">
						{{ advanced ? '收起' : '展开' }}
						<component :is="advanced ? 'up-outlined' : 'down-outlined'" />
					</a>
				</a-col>
			</a-row>
		</a-form>
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-space>
					<xn-batch-Button
						:selectedRowKeys="selectedRowKeys"
						icon="redo-outlined"
						buttonName="批量同步"
						buttonType="primary"
						@batchCallBack="orderBatchSync"
					/>
					<xn-batch-Button
						:selectedRowKeys="selectedRowKeys"
						icon="close-outlined"
						buttonName="批量关闭"
						@batchCallBack="orderBatchClose"
					/>
					<xn-batch-Button
						:selectedRowKeys="selectedRowKeys"
						icon="delete-outlined"
						buttonDanger
						buttonName="批量删除"
						@batchCallBack="deleteBatchPayOrder"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'payPlatform'">
					{{ $TOOL.dictTypeData('PAY_PLATFORM', record.payPlatform) }}
				</template>
				<template v-if="column.dataIndex === 'payStatus'">
					{{ $TOOL.dictTypeData('PAY_STATUS', record.payStatus) }}
				</template>
				<template v-if="column.dataIndex === 'hasRefund'">
					{{ dictTypeData(record.hasRefund) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a-button type="link" size="small" @click="orderSync(record)"> 同步 </a-button>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除吗？" placement="topRight" @confirm="deletePayOrder(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
						<a-divider type="vertical" />
						<a-dropdown>
							<a class="ant-dropdown-link">
								更多
								<DownOutlined />
							</a>
							<template #overlay>
								<a-menu>
									<a-menu-item>
										<a @click="doDetailsListRef.onOpen(record)">订单明细</a>
									</a-menu-item>
									<a-menu-item>
										<a @click="doRefundFormRef.onOpen(record)">退款详情</a>
									</a-menu-item>
									<a-menu-item>
										<a-popconfirm title="确定要关闭吗？" placement="topRight" @confirm="orderClose(record)">
											<a>关闭订单</a>
										</a-popconfirm>
									</a-menu-item>
								</a-menu>
							</template>
						</a-dropdown>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<do-refund-form ref="doRefundFormRef" @successful="tableRef.refresh()" />
	<do-details-list ref="doDetailsListRef" />
</template>

<script setup name="orderIndex">
	import tool from '@/utils/tool'
	import orderApi from '@/api/pay/orderApi'
	import DoRefundForm from './doRefundForm.vue'
	import DoDetailsList from './doDetailsList.vue'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const doRefundFormRef = ref()
	const doDetailsListRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	// 查询区域显示更多控制
	const advanced = ref(false)
	const toggleAdvanced = () => {
		advanced.value = !advanced.value
	}
	const columns = [
		{
			title: '商户订单号',
			dataIndex: 'outTradeNo',
			ellipsis: true
		},
		{
			title: '支付订单号',
			dataIndex: 'tradeNo',
			ellipsis: true
		},
		{
			title: '订单标题',
			dataIndex: 'subject',
			ellipsis: true
		},
		{
			title: '订单金额',
			dataIndex: 'orderAmount',
			width: 100,
			sorter: true
		},
		{
			title: '支付金额',
			dataIndex: 'payAmount',
			width: 100,
			sorter: true
		},
		{
			title: '支付平台',
			width: 100,
			dataIndex: 'payPlatform'
		},
		{
			title: '支付状态',
			width: 100,
			dataIndex: 'payStatus'
		},
		{
			title: '是否有退款',
			width: 100,
			dataIndex: 'hasRefund'
		},
		{
			title: '支付时间',
			dataIndex: 'payTime',
			sorter: true,
			ellipsis: true
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			sorter: true,
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '220px'
		}
	]
	// 是否有退款枚举
	const hasRefundOptions = [
		{
			label: '是',
			value: 'Y'
		},
		{
			label: '否',
			value: 'N'
		}
	]
	// 翻译是否有退款
	const dictTypeData = (value) => {
		return hasRefundOptions.find((f) => f.value === value).label
	}
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		// columns数字类型字段加入 needTotal: true 可以勾选自动算账
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	const loadData = (parameter) => {
		const searchFormParam = JSON.parse(JSON.stringify(searchFormState.value))
		// payTime范围查询条件重载
		if (searchFormParam.payTime) {
			searchFormParam.startPayTime = searchFormParam.payTime[0]
			searchFormParam.endPayTime = searchFormParam.payTime[1]
			delete searchFormParam.payTime
		}
		// createTime范围查询条件重载
		if (searchFormParam.createTime) {
			searchFormParam.startCreateTime = searchFormParam.createTime[0]
			searchFormParam.endCreateTime = searchFormParam.createTime[1]
			delete searchFormParam.createTime
		}
		return orderApi.orderPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deletePayOrder = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		orderApi.orderDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchPayOrder = (params) => {
		orderApi.orderDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 同步
	const orderSync = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		orderApi.orderSync(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 批量同步
	const orderBatchSync = (params) => {
		orderApi.orderSync(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 关闭
	const orderClose = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		orderApi.orderClose(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 批量关闭
	const orderBatchClose = (params) => {
		orderApi.orderClose(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	const payPlatformOptions = tool.dictList('PAY_PLATFORM')
	const payStatusOptions = tool.dictList('PAY_STATUS')
</script>
