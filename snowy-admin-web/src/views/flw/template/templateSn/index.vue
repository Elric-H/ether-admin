<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item label="名称关键词" name="searchKey">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入名称关键词" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-form-item label="分类" name="category">
						<a-select v-model:value="searchFormState.category" placeholder="请选择分类" :options="categoryOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-space>
						<a-button type="primary" @click="tableRef.refresh(true)">
							<template #icon><SearchOutlined /></template>
							查询
						</a-button>
						<a-button @click="reset">
							<template #icon><redo-outlined /></template>
							重置
						</a-button>
					</a-space>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
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
						<template #icon>
							<plus-outlined />
						</template>
						<span>新增</span>
					</a-button>
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchTemplateSn" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'category'">
					{{ $TOOL.dictTypeData('TEMPLATE_SN_CATEGORY', record.category) }}
				</template>
				<template v-if="column.dataIndex === 'previewValue'">
					<ellipsis :length="10" tooltip>
						{{ record.previewValue }}
					</ellipsis>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此流水号模板吗？" @confirm="flwTemplateSn(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="flwTemplateSn">
	import tool from '@/utils/tool'
	import Form from './form.vue'
	import templateSnApi from '@/api/flw/templateSnApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '名称',
			dataIndex: 'name',
			ellipsis: true
		},
		{
			title: '分类',
			dataIndex: 'category'
		},
		{
			title: '年月格式',
			dataIndex: 'dateFormat',
			ellipsis: true
		},
		{
			title: '前缀',
			dataIndex: 'prefix'
		},
		{
			title: '显示值',
			dataIndex: 'previewValue'
		},
		{
			title: '后缀位数',
			dataIndex: 'suffixDigits'
		},
		{
			title: '后缀初始值',
			dataIndex: 'suffixInitialValue'
		},
		{
			title: '后缀增量',
			dataIndex: 'suffixIncrementalValue'
		},
		{
			title: '排序',
			dataIndex: 'sortCode',
			sorter: true
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
	const loadData = (parameter) => {
		return templateSnApi.templateSnPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const flwTemplateSn = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		templateSnApi.templateSnDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchTemplateSn = (params) => {
		templateSnApi.templateSnDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 分类
	const categoryOptions = tool.dictList('TEMPLATE_SN_CATEGORY')
</script>
