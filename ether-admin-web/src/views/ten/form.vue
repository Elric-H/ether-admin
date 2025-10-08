<template>
	<xn-form-container
		:title="formData.id ? '编辑租户' : '增加租户'"
		:width="600"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="租户名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入租户名称" allow-clear />
			</a-form-item>
			<a-form-item name="category">
				<template #label>
					<a-tooltip>
						<template #title>
							库隔离租户需增加子租户数据源，且将子租户sql文件运行进去； ID隔离可直接创建租户，数据以'TENANT_ID'隔离。
						</template>
						<question-circle-outlined />
						租户类型：
					</a-tooltip>
				</template>
				<a-radio-group
					v-model:value="formData.category"
					:options="categoryOptions"
					:disabled="formData.id !== undefined"
				/>
			</a-form-item>
			<a-form-item v-if="formData.category === 'DB'" label="数据源：" name="dbsId">
				<a-select
					v-if="!formData.id"
					v-model:value="formData.dbsId"
					:options="dbsList"
					style="width: 100%"
					placeholder="请选择数据源"
				/>
				<a-input v-else disabled v-model:value="formData.dbsName" :bordered="false" />
			</a-form-item>
			<a-form-item label="域名：" name="domain">
				<a-input v-model:value="formData.domain" placeholder="请输入租户域名" allow-clear />
			</a-form-item>
			<a-form-item label="排序码：" name="sortCode">
				<a-input-number v-model:value="formData.sortCode" style="width: 100%" placeholder="请输入排序码" :max="1000" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit" :loading="submitLoading">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="tenForm">
	import { required } from '@/utils/formRules'
	import tenApi from '@/api/ten/tenApi'
	import tool from '@/utils/tool'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	// 表单数据，也就是默认给一些数据
	const formData = ref({})
	const dbsList = ref([])
	const submitLoading = ref(false)
	// 租户数据分割类型下拉框数据
	const categoryOptions = tool.dictList('TEN_CATEGORY')
	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
		// 获取数据源列表，并转成组件需要的格式
		tenApi.tenDbsList().then((data) => {
			dbsList.value = data.map((item) => {
				return {
					value: item['id'],
					label: item['poolName']
				}
			})
		})
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		name: [required('请输入租户名称')],
		dbsId: [required('请选择数据源')],
		domain: [required('请输入租户域名')],
		category: [required('请选择租户数据类型')],
		sortCode: [required('请选择排序')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value
			.validate()
			.then(() => {
				// 加入 dbsName 数据源名称
				if (formData.value.category === 'DB') {
					formData.value.dbsName = dbsList.value.find((item) => item.value === formData.value.dbsId).label
				}
				submitLoading.value = true
				tenApi
					.submitForm(formData.value, formData.value.id)
					.then(() => {
						visible.value = false
						emit('successful')
					})
					.finally(() => {
						submitLoading.value = false
					})
			})
			.catch(() => {})
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
