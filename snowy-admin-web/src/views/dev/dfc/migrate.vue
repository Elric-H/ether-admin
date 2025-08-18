<template>
	<xn-form-container title="迁移数据" :width="700" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="数据源：" name="dbsId">
						<a-select
							:disabled="true"
							v-model:value="formData.dbsId"
							:options="dbsList"
							style="width: 100%"
							placeholder="请选择数据源"
							@select="initSelectTable"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="表名称：" name="tableName">
						<a-select
							:disabled="true"
							v-model:value="formData.tableName"
							:options="tableList"
							style="width: 100%"
							placeholder="请选择表"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="字段：" name="tableColumn">
						<a-select
							v-model:value="formData.tableColumn"
							:options="tableColumns"
							style="width: 100%"
							placeholder="请选择表"
						/>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">确认</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="migrate">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import dfcApi from '@/api/dev/dfcApi'
	// 抽屉状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	// 定义
	const dbsList = ref([])
	const tableList = ref([])
	const tableColumns = ref([])
	const submitLoading = ref(false)

	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			sortCode: 99
		}
		initData(record)
	}

	// 初始化基础
	const initData = (record) => {
		// 获取数据源
		submitLoading.value = true
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
			.finally(() => {
				submitLoading.value = false
			})
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
			initSelectTable(record.dbsId)
			selectTableColumnsData(record.tableName)
		}
	}

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
		submitLoading.value = true
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
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 获取数据源下的表
	const initDbsTable = (value) => {
		const param = {
			dbsId: value
		}
		submitLoading.value = true
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
			.finally(() => {
				submitLoading.value = false
			})
	}
	// 获取表字段
	const selectTableColumnsData = (tableName) => {
		if (formData.value.dbsId === 'master') {
			// 通过这个 tableName 查到这个表下的字段
			const param = {
				tableName: tableName
			}
			dfcApi.dfcTableColumns(param).then((data) => {
				tableColumns.value = data.map((item) => {
					return {
						value: item['columnName'],
						label: item['columnRemark'] || item['columnName']
					}
				})
			})
		} else {
			const param = {
				tableName: tableName,
				dbsId: formData.value.dbsId
			}
			dfcApi.dfcTableColumnsByDbsId(param).then((data) => {
				tableColumns.value = data.map((item) => {
					return {
						value: item['columnName'],
						label: item['columnRemark'] || item['columnName']
					}
				})
			})
		}
	}

	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		dbsId: [required('请选择数据源')],
		tableName: [required('请选择表')],
		tableColumn: [required('请选择字段')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			dfcApi
				.migrate(formDataParam)
				.then(() => {
					onClose()
					emit('successful')
				})
				.finally(() => {
					submitLoading.value = false
				})
		})
	}
	// 抛出函数
	defineExpose({
		onOpen
	})
</script>
