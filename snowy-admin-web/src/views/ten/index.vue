<template>
	<a-card :bordered="false" style="margin-bottom: 10px">
		<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="searchKey" label="名称关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入租户名称关键词" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-button type="primary" @click="tableRef.refresh(true)">
						<template #icon><SearchOutlined /></template>
						查询
					</a-button>
					<a-button class="snowy-buttom-left" @click="reset">
						<template #icon><redo-outlined /></template>
						重置
					</a-button>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:expand-row-by-click="true"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<a-space>
					<a-button type="primary" @click="formRef.onOpen()">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchTen" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'category'">
					<a-tag color="pink">
						{{ $TOOL.dictTypeData('TEN_CATEGORY', record.category) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="formRef.onOpen(record)">编辑</a>
					<a-divider type="vertical" />
					<a-popconfirm title="确定删除此租户？" @confirm="remove(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="tenIndex">
	import tenApi from '@/api/ten/tenApi'
	import Form from './form.vue'

	const columns = [
		{
			title: '租户名称',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '租户类型',
			dataIndex: 'category'
		},
		{
			title: '数据源名称',
			dataIndex: 'dbsName',
			ellipsis: true
		},
		{
			title: '绑定域名',
			dataIndex: 'domain',
			ellipsis: true
		},
		{
			title: '排序',
			dataIndex: 'sortCode'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '150px'
		}
	]
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
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
	// 定义tableDOM
	const tableRef = ref(null)
	const formRef = ref()
	const searchFormRef = ref()
	const searchFormState = ref({})

	// 表格查询
	const loadData = (parameter) => {
		return tenApi.tenPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const remove = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		tenApi.tenDelete(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 批量删除
	const deleteBatchTen = (params) => {
		tenApi.tenDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>

<style scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.primaryAdd {
		margin-right: 10px;
	}
	.snowy-buttom-left {
		margin-left: 8px;
	}
</style>
