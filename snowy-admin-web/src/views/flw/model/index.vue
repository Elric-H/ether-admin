<template>
	<div v-if="indexShow">
		<a-card :bordered="false" style="margin-bottom: 10px">
			<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
				<a-row :gutter="24">
					<a-col :span="8">
						<a-form-item name="name" label="模型名称">
							<a-input v-model:value="searchFormState.name" placeholder="请输入模型名称" />
						</a-form-item>
					</a-col>
					<a-col :span="8">
						<a-form-item name="category" label="模型分类">
							<a-select
								v-model:value="searchFormState.category"
								:options="modelCategory"
								style="width: 100%"
								placeholder="请选择模型分类"
							/>
						</a-form-item>
					</a-col>
					<a-col :span="8">
						<a-button type="primary" @click="tableRef.refresh(true)">
							<template #icon><SearchOutlined /></template>查询</a-button
						>
						<a-button class="snowy-buttom-left" @click="reset">
							<template #icon><redo-outlined /></template>重置</a-button
						>
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
				:expand-row-by-click="true"
				bordered
				:row-key="(record) => record.id"
				:row-selection="options.rowSelection"
			>
				<template #operator class="table-operator">
					<a-space>
						<a-button type="primary" @click="formRef.onOpen()">
							<template #icon><plus-outlined /></template>新增
						</a-button>
						<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchModel" />
					</a-space>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'adminId'">
						{{ JSON.parse(record.extJson)[0].name }}
					</template>
					<template v-if="column.dataIndex === 'icon'">
						<a-tag :color="record.color">
							<component :is="record.icon" />
						</a-tag>
					</template>
					<template v-if="column.dataIndex === 'category'">
						{{ $TOOL.dictTypeData('MODEL_CATEGORY', record.category) }}
					</template>
					<template v-if="column.dataIndex === 'formType'">
						{{ $TOOL.dictTypeData('MODEL_FORM_TYPE', record.formType) }}
					</template>
					<template v-if="column.dataIndex === 'modelStatus'">
						<a-switch
							:loading="statusLoading"
							:checked="record.modelStatus === 'ENABLE'"
							@change="editModelStatus(record)"
						/>
					</template>
					<template v-if="column.dataIndex === 'action'">
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a @click="configSteps(record)">配置</a>
						<a-divider type="vertical" />
						<a-dropdown>
							<a class="ant-dropdown-link">
								更多
								<DownOutlined />
							</a>
							<template #overlay>
								<a-menu>
									<a-menu-item>
										<template #icon>
											<hdd-outlined style="color: #1890ff" />
										</template>
										<a-popconfirm title="确定要部署吗？" placement="topRight" @confirm="modelDeploy(record)">
											<a>部署</a>
										</a-popconfirm>
									</a-menu-item>
									<a-menu-item>
										<template #icon>
											<arrow-down-outlined style="color: #fcc02e" />
										</template>
										<a-popconfirm
											title="确定要降级该流程模型吗？"
											placement="topRight"
											@confirm="modelDownVersion(record)"
										>
											<a>降级</a>
										</a-popconfirm>
									</a-menu-item>
									<a-menu-item>
										<template #icon>
											<delete-outlined style="color: #ff5a5a" />
										</template>
										<a-popconfirm title="确定要删除流程吗？" placement="topRight" @confirm="deleteModel(record)">
											<a>删除</a>
										</a-popconfirm>
									</a-menu-item>
								</a-menu>
							</template>
						</a-dropdown>
					</template>
				</template>
			</s-table>
		</a-card>
	</div>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
	<ConfigSteps v-if="!indexShow" ref="configStepsRef" @previousPage="previousPage" />
</template>

<script setup name="flwModelDesign">
	import { nextTick } from 'vue'
	import ConfigSteps from './configSteps.vue'
	import Form from './form.vue'
	import modelApi from '@/api/flw/modelApi'
	import tool from '@/utils/tool'

	const tableRef = ref(null)
	const formRef = ref(null)
	const configStepsRef = ref(null)
	const statusLoading = ref(false)
	const columns = [
		{
			title: '流程名称',
			dataIndex: 'name'
		},
		{
			title: '版本号',
			dataIndex: 'versionCode',
			width: 80
		},
		{
			title: '管理员',
			dataIndex: 'adminId'
		},
		{
			title: '图标',
			dataIndex: 'icon',
			width: 80
		},
		{
			title: '流程分类',
			dataIndex: 'category'
		},
		{
			title: '表单类型',
			dataIndex: 'formType'
		},
		{
			title: '排序码',
			dataIndex: 'sortCode',
			width: 80
		},
		{
			title: '状态',
			dataIndex: 'modelStatus',
			width: 80
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 200
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
	const searchFormRef = ref()
	const searchFormState = ref({})
	// 搜索框下拉字典数据
	let modelCategory = tool.dictList('MODEL_CATEGORY')
	// 表格数据
	const loadData = (parameter) => {
		return modelApi.modelPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 默认打开index列表界面
	const indexShow = ref(true)
	// 流程配置
	const configSteps = (record) => {
		indexShow.value = false
		// 传id过去，新界面进行数据请求
		nextTick(() => {
			configStepsRef.value.configSteps(record)
		})
	}
	// 部署
	const modelDeploy = (record) => {
		const params = {
			id: record.id
		}
		modelApi.modelDeploy(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 降级
	const modelDownVersion = (record) => {
		const params = {
			id: record.id
		}
		modelApi.modelDownVersion(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// config配置界面回调
	const previousPage = () => {
		indexShow.value = true
		nextTick(() => {
			tableRef.value.refresh()
		})
	}
	// 修改状态
	const editModelStatus = (record) => {
		statusLoading.value = true
		if (record.modelStatus === 'ENABLE') {
			modelApi
				.modelDisable(record)
				.then(() => {
					record.userStatus = 'DISABLE'
					tableRef.value.refresh()
				})
				.finally(() => {
					statusLoading.value = false
				})
		} else {
			modelApi
				.modelEnable(record)
				.then(() => {
					record.userStatus = 'ENABLE'
					tableRef.value.refresh()
				})
				.finally(() => {
					statusLoading.value = false
				})
		}
	}
	// 删除
	const deleteModel = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		modelApi.modelDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchModel = (params) => {
		modelApi.modelDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>

<style scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
	.snowy-table-avatar {
		margin-top: -10px;
		margin-bottom: -10px;
	}
	.snowy-buttom-left {
		margin-left: 8px;
	}
</style>
