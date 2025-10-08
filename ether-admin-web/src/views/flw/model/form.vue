<template>
	<xn-form-container
		:title="formData.id ? '编辑流程' : '增加流程'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical" :label-col="labelCol">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="名称：" name="name">
						<a-input v-model:value="formData.name" placeholder="请输入名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="流程类型：" name="formType">
						<a-radio-group v-model:value="formData.formType" :options="modelFormType" :disabled="!!formData.id" />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="流程分类：" name="category">
						<a-select
							v-model:value="formData.category"
							:options="modelCategory"
							style="width: 100%"
							placeholder="请选择流程分类"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="管理员：" name="adminId">
						<a-button type="link" style="padding-left: 0px" @click="openSelector(formData.adminId)">选择</a-button>
						<a-tag v-if="formData.adminId && extJson.length > 0" color="orange" closable @close="closeUserTag">{{
							extJson[0].name
						}}</a-tag>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="选择图标：" name="icon">
						<a-input-group compact>
							<a-input
								v-model:value="formData.icon"
								style="width: calc(100% - 63px)"
								disabled
								placeholder="请选择图标"
							/>
							<a-button type="primary" @click="$refs.iconselector.showIconModal(formData.icon)">选择</a-button>
						</a-input-group>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="移动端图标：" name="iconMobile">
						<a-input-group compact>
							<a-input
								v-model:value="formData.iconMobile"
								style="width: calc(100% - 63px)"
								disabled
								placeholder="请选择移动端图标"
							/>
							<a-button type="primary" @click="$refs.iconMobileSelector.showIconModal(formData.iconMobile)"
								>选择</a-button
							>
						</a-input-group>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="选择颜色：" name="color">
						<color-picker v-model:value="formData.color" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序码：" name="sortCode">
						<a-input-number
							v-model:value="formData.sortCode"
							style="width: 100%"
							placeholder="请输入排序码"
							:max="1000"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16" v-if="formData.formType === 'DESIGN'">
				<a-col :span="12">
					<a-form-item label="选择主表：" name="dbTable">
						<a-select
							v-model:value="formData.dbTable"
							:options="tableList"
							style="width: 100%"
							placeholder="请选择主表"
							@select="seleTableColumnsData(formData.dbTable)"
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="选择主键：" name="dbTableKey">
						<a-select
							v-model:value="formData.dbTableKey"
							:options="tableColumns"
							style="width: 100%"
							placeholder="选择主键"
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-form-item label="选择子表：" name="dbChildrenTable" v-if="formData.formType === 'DESIGN'">
				<a-button type="primary" class="childAddButton" @click="addDomains()">
					<PlusOutlined />
					增加子表
				</a-button>
				<a-row :gutter="10" class="form-row">
					<a-col :span="11" class="form-row-con"> 子表 </a-col>
					<a-col :span="5" class="form-row-con"> 子表主键 </a-col>
					<a-col :span="5" class="form-row-con"> 子表外键 </a-col>
				</a-row>

				<div v-for="(childrenTable, index) in formData.dbChildrenTable" :key="index" class="form-div">
					<a-row :gutter="10">
						<a-col :span="11">
							<a-form-item
								:name="['dbChildrenTable', index, 'childrenTableName']"
								:rules="{ required: true, message: '请选择子表' }"
							>
								<a-select
									v-model:value="childrenTable.childrenTableName"
									:options="tableList"
									style="width: 100%"
									placeholder="请选择子表"
									@select="seleChildrenTableColumnsData(childrenTable.childrenTableName)"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="5">
							<a-form-item
								:name="['dbChildrenTable', index, 'childrenTableKey']"
								:rules="{ required: true, message: '请选择子表主键' }"
							>
								<a-select
									v-model:value="childrenTable.childrenTableKey"
									:options="childrenTableColumnsData(childrenTable.childrenTableName)"
									style="width: 100%"
									placeholder="子表主键"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="5">
							<a-form-item
								:name="['dbChildrenTable', index, 'tablePKey']"
								:rules="{ required: true, message: '请选择子表外键' }"
							>
								<a-select
									v-model:value="childrenTable.tablePKey"
									:options="childrenTableColumnsData(childrenTable.childrenTableName)"
									style="width: 100%"
									placeholder="子表外键"
								/>
							</a-form-item>
						</a-col>
						<a-col :span="3" style="margin-top: 4px">
							<a-button size="small" type="primary" danger ghost @click="delDomains(index)">移除</a-button>
						</a-col>
					</a-row>
				</div>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" :loading="submitLoading" @click="onSubmit">保存</a-button>
		</template>
		<icon-selector ref="iconselector" @iconCallBack="iconCallBack" />
		<icon-mobile-selector ref="iconMobileSelector" @iconCallBack="iconMobileCallBack" />
		<user-selector-plus
			ref="userSelectorPlusRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:user-page-api="selectorApiFunction.userPageApi"
			:checked-user-list-api="selectorApiFunction.checkedUserListApi"
			:radio-model="true"
			@onBack="userBack"
		/>
	</xn-form-container>
</template>

<script setup name="modelForm">
	import ColorPicker from '@/components/ColorPicker/index.vue'
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import IconSelector from '@/components/Selector/iconSelector.vue'
	import IconMobileSelector from '@/components/Selector/iconMobileSelector.vue'
	import modelApi from '@/api/flw/modelApi'
	import dbsApi from '@/api/dbs/dbsApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import UserSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import tool from '@/utils/tool'
	const emit = defineEmits({ successful: null })
	const userSelectorPlusRef = ref()
	const extJson = ref([])
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 所有数据库中的表
	const tableList = ref([])
	// 主表中的所有字段
	const tableColumns = ref([])
	// 子表中的所有字段
	const childrenTableListColumns = ref([])
	const submitLoading = ref(false)
	// 表单数据，也就是默认给一些数据
	const formData = ref({})

	// 获取到字典中的数组，转换内部对象成antdv组件需要的options格式
	const modelFormType = tool.dictList('MODEL_FORM_TYPE')
	const modelCategory = tool.dictList('MODEL_CATEGORY')

	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			sortCode: 99,
			formType: 'DESIGN',
			color: '#1890FF',
			dbChildrenTable: []
		}
		if (record) {
			formData.value = Object.assign({}, record)
			extJson.value = JSON.parse(formData.value.extJson) || []
		}
		if (formData.value !== 'DESIGN') {
			// 获取数据源列表，并转成组件需要的格式
			dbsApi.dbsTables().then((data) => {
				// 如果是编辑的时候，那就去转换数据，放在这里是因为请求顺序
				if (record && record.formType === 'DESIGN') {
					convertFormData(record)
				}
				if (data === null || data.length === 0) {
					message.warning('为查得数据库表')
					return
				}
				tableList.value = data.map((item) => {
					return {
						value: item['tableName'],
						label: `${item['tableRemark']}-${item['tableName']}`,
						tableRemark: item['tableRemark'] || item['tableName'],
						tableColumns: []
					}
				})
			})
		}
	}
	const convertFormData = (record) => {
		formData.value.dbChildrenTable = []
		// 取得主表信息
		const tableJson = JSON.parse(record.tableJson)
		const dbTableData = tableJson.filter((item) => item.tableType === 'parent')[0]
		// 查询选中的这个表下的数据
		seleTableColumnsData(dbTableData.tableName)
		// 赋值给表单中的主表
		formData.value.dbTable = dbTableData.tableName
		// 赋值给表单中的主表主键
		formData.value.dbTableKey = dbTableData.primaryKey
		// 取得子表信息
		tableJson.filter((item) => {
			if (item.tableType === 'child') {
				const obj = {
					childrenTableName: item.tableName,
					childrenTableKey: item.primaryKey,
					tablePKey: item.foreignKey
				}
				formData.value.dbChildrenTable.push(obj)
				// 查询每个子表下的字段，会加入到整个缓存中
				seleChildrenTableColumnsData(item.tableName)
				return item
			}
		})
	}
	// 打开人员选择器，选择主管
	const openSelector = (id) => {
		let checkedUserIds = []
		checkedUserIds.push(id)
		userSelectorPlusRef.value.showUserPlusModal(checkedUserIds)
	}
	// 人员选择器回调
	const userBack = (value) => {
		extJson.value = value
		if (value.length > 0) {
			formData.value.adminId = value[0].id
		} else {
			formData.value.adminId = ''
		}
	}
	// 通过小标签删除主管
	const closeUserTag = () => {
		extJson.value = []
		formData.value.adminId = ''
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		adminId: [required('请选择管理员')],
		name: [required('请输入流程名称')],
		formType: [required('请选择模型类型')],
		icon: [required('请选择图标')],
		category: [required('请选择模型分类')],
		sortCode: [required('请拖动排序')],
		dbTable: [required('请选择数据库表信息')],
		dbTableKey: [required('请选择主表主键')]
	}
	// 表单固定label实现
	const labelCol = ref({
		style: {
			width: '100px'
		}
	})
	// 图标选择器回调
	const iconCallBack = (values) => {
		formData.value.icon = values
	}
	// 移动端图标选择器回调
	const iconMobileCallBack = (values) => {
		formData.value.iconMobile = values
	}
	// 查询到主表下有多字段
	const seleTableColumnsData = (value) => {
		const param = {
			tableName: value
		}
		dbsApi.dbsTableColumns(param).then((data) => {
			if (data === null || data.length === 0) {
				message.warning('未取得表中字段列表')
				return
			}
			tableColumns.value = data.map((item) => {
				return {
					value: item['columnName'],
					label: item['columnRemark'] || item['columnName']
				}
			})
			// 给总体的里面加入选择的表的所有字段
			tableList.value.filter((item) => item.value === value)[0].tableColumns = tableColumns.value
		})
	}
	// 查询子表下的字段
	const seleChildrenTableColumnsData = async (value) => {
		const param = {
			tableName: value
		}
		let data = await dbsApi.dbsTableColumns(param)
		if (data === null || data.length === 0) {
			message.warning('未取得表中字段列表')
			return
		}
		const tableColumns = data.map((item) => {
			return {
				value: item['columnName'],
				label: item['columnRemark'] || item['columnName']
			}
		})
		const obj = {
			tableName: value,
			columnsList: tableColumns
		}
		childrenTableListColumns.value.push(obj)
		// 给总体的里面加入选择的表的所有字段
		tableList.value.filter((item) => item.value === value)[0].tableColumns = tableColumns
	}
	// 选择表字段的options
	const childrenTableColumnsData = (value) => {
		const resultData = childrenTableListColumns.value.filter((item) => item.tableName === value)
		if (resultData.length > 0) {
			return resultData[0].columnsList
		}
	}
	// 增行
	const addDomains = () => {
		formData.value.dbChildrenTable.push({
			childrenTableName: undefined,
			childrenTableKey: undefined,
			tablePKey: undefined
		})
	}
	// 删减行
	const delDomains = (index) => {
		formData.value.dbChildrenTable.splice(index, 1)
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				if (formData.value.formType === 'DESIGN') {
					formData.value.tableJson = JSON.stringify(convertTableData(tableList.value, formData.value))
				}
				formData.value.extJson = JSON.stringify(extJson.value)
				submitLoading.value = true
				modelApi
					.submitForm(formData.value, formData.value.id)
					.then(() => {
						visible.value = false
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {
				submitLoading.value = false
			})
	}
	// 数据转换
	const convertTableData = (tableList, formData) => {
		const tableData = tableList.filter((item) => item.value === formData.dbTable)[0]
		// 最终格式的JSON
		const parentTableData = [
			{
				tableRemark: tableData.tableRemark,
				tableName: tableData.value,
				tableType: 'parent',
				foreignKey: '',
				primaryKey: formData.dbTableKey,
				tableColumn: tableData.tableColumns.map((item) => {
					return {
						columnName: item['value'],
						columnRemark: item['label']
					}
				})
			}
		]
		let childTableData = []
		if (formData.dbChildrenTable.length > 0) {
			formData.dbChildrenTable.forEach((item) => {
				const tableData = tableList.filter((table) => table.value === item.childrenTableName)[0]
				const obj = {
					tableRemark: tableData.tableRemark,
					tableName: tableData.value,
					tableType: 'child',
					foreignKey: item.tablePKey,
					primaryKey: item.childrenTableKey,
					tableColumn: tableData.tableColumns.map((item) => {
						return {
							columnName: item['value'],
							columnRemark: item['label']
						}
					})
				}
				childTableData.push(obj)
			})
		}
		return parentTableData.concat(childTableData)
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return modelApi.modelOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return modelApi.modelUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedUserListApi: (param) => {
			return userCenterApi.userCenterGetUserListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
<style scoped>
	.childAddButton {
		margin-bottom: 10px;
	}
	.form-row {
		background-color: var(--item-hover-bg);
		margin-left: 0px !important;
	}
	.form-row-con {
		padding-bottom: 5px;
		padding-top: 5px;
		padding-left: 15px;
	}
	.form-div {
		padding-top: 10px;
	}
</style>
