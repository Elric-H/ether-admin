<template>
	<a-card :bordered="false">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="6">
					<a-form-item label="数据源" name="dbsId">
						<a-select
							v-model:value="searchFormState.dbsId"
							:options="dbsList"
							style="width: 100%"
							placeholder="请选择数据源"
							@select="initSelectTable"
							allow-clear
						/>
					</a-form-item>
				</a-col>
				<a-col :span="6">
					<a-form-item label="表名称" name="tableName">
						<a-select
							v-model:value="searchFormState.tableName"
							:options="tableList"
							style="width: 100%"
							placeholder="请选择表"
							allow-clear
						/>
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
					<xn-batch-delete :selectedRowKeys="selectedRowKeys" @batchDelete="deleteBatchDevDfc" />
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'type'">
					<a-tag>{{ $TOOL.dictTypeData('DFC_TYPE', record.type) }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'required'">
					<a-tag :color="!!record.required ? '#f50' : ''">{{ !!record.required ? '是' : '否' }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'status'">
					<a-switch
						:loading="loading"
						:checked="record.status === 'ENABLE'"
						@change="editStatus(record)"
					/>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a-space>
						<a @click="migrateRef.onOpen(record)">迁移</a>
						<a-divider type="vertical" />
						<a @click="formRef.onOpen(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除吗？" @confirm="deleteDevDfc(record)">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</a-space>
				</template>
			</template>
		</s-table>
	</a-card>
	<Form ref="formRef" @successful="tableRef.refresh(true)" />
	<Migrate ref="migrateRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="dfc">
	import Form from './form.vue'
	import Migrate from './migrate.vue'
	import devDfcApi from '@/api/dev/dfcApi'
	import dfcApi from '@/api/dev/dfcApi'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const formRef = ref()
	const migrateRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: true, striped: false }
	const columns = [
		{
			title: '数据源',
			dataIndex: 'dbsId',
			align: 'center'
		},
		{
			title: '表名称',
			dataIndex: 'tableName',
			align: 'center'
		},
		{
			title: '排序码',
			dataIndex: 'sortCode',
			align: 'center'
		},
		{
			title: '标签文本',
			dataIndex: 'label',
			align: 'center'
		},
		{
			title: '字段类型',
			dataIndex: 'type',
			align: 'center'
		},
		{
			title: '必填',
			dataIndex: 'required',
			align: 'center'
		},
		{
			title: '状态',
			dataIndex: 'status',
			align: 'center'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '200px'
		}
	]
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		// columns数字类型字段加入 needTotal: true 可以勾选自动算账
		alert: {
			show: true,
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
	const dbsList = ref([])
	const tableList = ref([])
	// 获取数据源
	dfcApi
		.dfcDbsSelector()
		.then((data) => {
			dbsList.value = data.map((item) => {
				return {
					value: item['id'],
					label: item['poolName']
				}
			})
			const masterDb = {
				value: 'master',
				label: 'master'
			}
			dbsList.value.push(masterDb)
		})
		.finally(() => {})
	// 下拉框选择数据源
	const initSelectTable = (dbsId) => {
		if (dbsId === 'master') {
			initMasterTable()
		} else {
			initDbsTable(dbsId)
		}
	}
	// 加载主数据源的表
	const initMasterTable = () => {
		// 获取数据库中的所有表
		dfcApi
			.dfcTables()
			.then((data) => {
				tableList.value = data.map((item) => {
					return {
						value: item['tableName'],
						label: `${item['tableRemark']}-${item['tableName']}`,
						tableRemark: item['tableRemark'] || item['tableName'],
						tableColumns: []
					}
				})
			})
			.finally(() => {})
	}
	// 获取数据源下的表
	const initDbsTable = (value) => {
		const param = {
			dbsId: value
		}
		dfcApi
			.dfcTablesByDbsId(param)
			.then((data) => {
				tableList.value = data.map((item) => {
					return {
						value: item['tableName'],
						label: `${item['tableRemark']}-${item['tableName']}`,
						tableRemark: item['tableRemark'] || item['tableName'],
						tableColumns: []
					}
				})
			})
			.finally(() => {})
	}
	const loadData = (parameter) => {
		const searchFormParam = JSON.parse(JSON.stringify(searchFormState.value))
		return devDfcApi.dfcPage(Object.assign(parameter, searchFormParam)).then((data) => {
			return data
		})
	}
	const loading = ref(false)
	// 修改状态
	const editStatus = (record) => {
		loading.value = true
		if (record.status === 'ENABLE') {
			record.status = 'DISABLED'
		} else {
			record.status = 'ENABLE'
		}
		dfcApi
			.dfcSubmitForm(record, record.id)
			.then(() => {
				tableRef.value.refresh()
			})
			.finally(() => {
				loading.value = false
			})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteDevDfc = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		devDfcApi.dfcDelete(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const deleteBatchDevDfc = (params) => {
		devDfcApi.dfcDelete(params).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
