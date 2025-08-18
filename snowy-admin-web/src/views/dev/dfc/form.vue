<template>
	<xn-form-container
		:title="formData.id ? '编辑动态字段配置' : '增加动态字段配置'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="数据源：" name="dbsId">
						<a-select
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
							v-model:value="formData.tableName"
							:options="tableList"
							style="width: 100%"
							placeholder="请选择表"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item name="name">
						<template #label>
							<a-tooltip>
								<template #title> 字段的唯一性英文标识，不可重复 </template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp 表单域属性名：
						</template>
						<a-input v-model:value="formData.name" placeholder="请输入表单域属性名" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="标签文本：" name="label">
						<a-input v-model:value="formData.label" placeholder="请输入标签文本" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="必填：" name="required">
						<a-radio-group
							v-model:value="formData.required"
							:options="[
								{
									label: '是',
									value: 1
								},
								{
									label: '否',
									value: 0
								}
							]"
						>
						</a-radio-group>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="提示语：" name="placeholder">
						<a-input v-model:value="formData.placeholder" placeholder="请输入提示语" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序码：" name="sortCode">
						<a-input-number
							style="width: 100%"
							v-model:value="formData.sortCode"
							placeholder="请输入排序码"
							allow-clear
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="字段类型：" name="type">
						<a-select v-model:value="formData.type" placeholder="请选择字段类型" :options="typeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12" v-if="formData.type === 'select'">
					<a-form-item label="选择项类型：" name="selectOptionType">
						<a-select
							v-model:value="formData.selectOptionType"
							placeholder="请选择选择项类型"
							:options="selectOptionTypeOptions"
						/>
					</a-form-item>
				</a-col>
				<a-col
					:span="12"
					v-if="
						formData.type === 'radio' ||
						formData.type === 'checkbox' ||
						(formData.type === 'select' && formData.selectOptionType === 'DICT')
					"
				>
					<a-form-item label="字典：" name="dictTypeCode">
						<a-select v-model:value="formData.dictTypeCode" placeholder="请选择字典" :options="dictTypeCodeOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12" v-if="formData.selectOptionType === 'API' || formData.type === 'pageSelect'">
					<a-form-item name="selOptionApiUrl">
						<template #label>
							<a-tooltip>
								<template #title>
									注意：<br />1、get请求；<br />2、返回数据约定为id和name；<br />3、服务端接口返回Page；<br />4、参数中必须含有current和size<br />5、参考：/biz/user/userSelector
								</template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp 选择项api地址：
						</template>
						<a-input v-model:value="formData.selOptionApiUrl" placeholder="请输入选择项api地址" />
					</a-form-item>
				</a-col>
				<a-col :span="12" v-if="formData.type === 'pageSelect'">
					<a-form-item name="selDataApiUrl">
						<template #label>
							<a-tooltip>
								<template #title>
									注意：<br />1、post请求；<br />2、返回数据约定为id和name；<br />3、服务端接口返回List；<br />4、参数中必须含有idList<br />5、参考：/sys/userCenter/getUserListByIdList
								</template>
								<question-circle-outlined />
							</a-tooltip>
							&nbsp 已选择数据api地址：
						</template>
						<a-input v-model:value="formData.selDataApiUrl" placeholder="请输入已选择数据api地址" />
					</a-form-item>
				</a-col>
				<a-col :span="12" v-if="formData.type === 'select'">
					<a-form-item label="多选：" name="isMultiple">
						<a-radio-group
							v-model:value="formData.isMultiple"
							:options="[
								{
									label: '是',
									value: 1
								},
								{
									label: '否',
									value: 0
								}
							]"
						/>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="devDfcForm">
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	import dfcApi from '@/api/dev/dfcApi'
	import tool from '@/utils/tool'
	// 抽屉状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	// 定义
	const dbsList = ref([])
	const tableList = ref([])
	const typeOptions = tool.dictList('DFC_TYPE')
	const selectOptionTypeOptions = tool.dictList('DFC_SELECT_OPTION_TYPE')
	const dictTypeCodeOptions = tool.dictDataAll().map((item) => {
		return {
			label: item.name,
			value: item.dictValue
		}
	})
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
				let dataBaseMaster = false
				dbsList.value.filter((f) => {
					if (f.label === 'master') {
						dataBaseMaster = true
					}
				})
				if (!dataBaseMaster) {
					const masterDb = {
						value: 'master',
						label: 'master'
					}
					dbsList.value.push(masterDb)
				}
			})
			.finally(() => {
				submitLoading.value = false
			})
		if (record) {
			let recordData = cloneDeep(record)
			formData.value = Object.assign({}, recordData)
			initSelectTable(record.dbsId)
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
		name: [
			required('请输入表单域属性名'),
			{
				pattern: /^[a-zA-Z]+$/,
				message: '请使用英文',
				trigger: 'blur'
			}
		],
		label: [
			required('请输入标签文本'),
			{
				pattern:
					/^(?:[\u3400-\u4DB5\u4E00-\u9FEA\uFA0E\uFA0F\uFA11\uFA13\uFA14\uFA1F\uFA21\uFA23\uFA24\uFA27-\uFA29]|[\uD840-\uD868\uD86A-\uD86C\uD86F-\uD872\uD874-\uD879][\uDC00-\uDFFF]|\uD869[\uDC00-\uDED6\uDF00-\uDFFF]|\uD86D[\uDC00-\uDF34\uDF40-\uDFFF]|\uD86E[\uDC00-\uDC1D\uDC20-\uDFFF]|\uD873[\uDC00-\uDEA1\uDEB0-\uDFFF]|\uD87A[\uDC00-\uDFE0])+$/,
				message: '请使用中文',
				trigger: 'blur'
			}
		],
		required: [required('请选择必填')],
		placeholder: [required('请输入提示语')],
		sortCode: [required('请输入排序码')],
		type: [required('请选择字段类型')],
		selectOptionType: [required('请选择选择项类型')],
		dictTypeCode: [required('请选择字典')],
		selOptionApiUrl: [required('请输入选择项api地址')],
		selDataApiUrl: [required('请输入已选择数据api地址')],
		isMultiple: [required('选择是否多选')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			submitLoading.value = true
			const formDataParam = cloneDeep(formData.value)
			dfcApi
				.dfcSubmitForm(formDataParam, formDataParam.id)
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
