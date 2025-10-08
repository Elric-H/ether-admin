<template>
	<a-card :bordered="false" style="margin-bottom: 10px">
		<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="poolName" label="数据源名称">
						<a-input v-model:value="searchFormState.poolName" placeholder="请输入数据源名称" />
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
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchDbs" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'category'">
					<a-tag v-if="record.category === 'MASTER'" color="pink">
						{{ $TOOL.dictTypeData('DBS_CATEGORY', record.category) }}
					</a-tag>
					<div v-else>
						{{ $TOOL.dictTypeData('DBS_CATEGORY', record.category) }}
					</div>
				</template>
				<template v-if="column.dataIndex === 'driverName'">
					<a-tag color="cyan">
						{{ $TOOL.dictTypeData('DATABASE_DRIVE_TYPE', record.driverName) }}
					</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="formRef.onOpen(record)">编辑</a>
					<a-divider type="vertical" />
					<a-popconfirm title="确定删除此数据源？" @confirm="remove(record)">
						<a-button type="link" danger size="small">删除</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="dbs">
	import dbsApi from '@/api/dbs/dbsApi'
	import Form from './form.vue'

	const columns = [
		{
			title: '数据源名称',
			dataIndex: 'poolName',
			ellipsis: true
		},
		{
			title: '驱动类型',
			dataIndex: 'driverName'
		},
		{
			title: '分类',
			dataIndex: 'category'
		},
		{
			title: '连接URL',
			dataIndex: 'url',
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
	let selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys = ref([])
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

	// 表格查询 返回 Promise 对象
	const loadData = (parameter) => {
		return dbsApi.dbsPage(Object.assign(parameter, searchFormState.value)).then((data) => {
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
		dbsApi.dbsDelete(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 批量删除
	const deleteBatchDbs = (params) => {
		dbsApi.dbsDelete(params).then(() => {
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
