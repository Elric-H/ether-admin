<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form mb-4">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="名称" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入名称关键词" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="分类" name="category">
						<a-select v-model:value="searchFormState.category" placeholder="请选择分类" :options="categoryOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-button type="primary" @click="tableRef.refresh(true)">查询</a-button>
					<a-button style="margin: 0 8px" @click="reset">重置</a-button>
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
					<a-button type="primary" @click="formRef.onOpen()">
						<template #icon><plus-outlined /></template>
						新增
					</a-button>
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchUrp" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'category'">
					{{ $TOOL.dictTypeData('URP_CATEGORY', record.category) }}
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="openPreview(record)">预览</a>
						<a-divider type="vertical" />
						<a @click="openDesign(record)">设计</a>
						<a-divider type="vertical" />
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteUrp(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="urpIndex">
	import tool from '@/utils/tool'
	import Form from './form.vue'
	import urpApi from '@/api/urp/urpApi'
	import sysConfig from '@/config'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: true }
	const columns = [
		{
			title: '名称',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '分类',
			dataIndex: 'category',
			width: 120
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			width: 80
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 250
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
	const loadData = (parameter) => {
		const searchFormParam = JSON.parse(JSON.stringify(searchFormState.value))
		return urpApi.urpPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 预览
	const openPreview = (record) => {
		window.open(
			sysConfig.API_URL +
				'/ureport/preview?_u=URP_' +
				record.name +
				'&token=' +
				tool.data.get('TOKEN') +
				'&Domain=' +
				location.origin,
			'_blank'
		)
	}
	// 设计
	const openDesign = (record) => {
		window.open(
			sysConfig.API_URL +
				'/ureport/designer?_u=URP_' +
				record.name +
				'&token=' +
				tool.data.get('TOKEN') +
				'&Domain=' +
				location.origin,
			'_blank'
		)
	}
	// 删除
	const deleteUrp = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		urpApi.urpDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchUrp = (params) => {
		urpApi.urpDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	const categoryOptions = tool.dictList('URP_CATEGORY')
</script>
