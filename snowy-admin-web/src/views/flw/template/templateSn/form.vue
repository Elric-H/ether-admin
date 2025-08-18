<template>
	<xn-form-container
		:title="formData.id ? '编辑流水号模板' : '增加流水号模板'"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="流水号名称：" name="name">
						<a-input v-model:value="formData.name" placeholder="请输入流水号名称" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="分类：" name="category">
						<a-select v-model:value="formData.category" placeholder="请选择分类" :options="categoryOptions" />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="年月格式：" name="dateFormat">
						<a-input v-model:value="formData.dateFormat" placeholder="请输入年月格式，例如：yyyyMMdd" allow-clear />
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="8">
					<a-form-item label="前缀：" name="prefix">
						<a-input v-model:value="formData.prefix" placeholder="请输入前缀" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="16">
					<a-form-item label="显示值：" name="previewValue">
						<a-input
							v-model:value="formData.previewValue"
							placeholder="请输入显示值，例如：{yyyyMMdd}{0001}"
							allow-clear
						/>
					</a-form-item>
				</a-col>
			</a-row>
			<a-row :gutter="16">
				<a-col :span="12">
					<a-form-item label="后缀位数：" name="suffixDigits">
						<a-input-number
							style="width: 100%"
							v-model:value="formData.suffixDigits"
							placeholder="请输入后缀位数"
							:max="20"
							allow-clear
						/>
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="后缀初始值：" name="suffixInitialValue">
						<a-input v-model:value="formData.suffixInitialValue" placeholder="请输入后缀初始值" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="后缀增量：" name="suffixIncrementalValue">
						<a-input v-model:value="formData.suffixIncrementalValue" placeholder="请输入后缀增量" allow-clear />
					</a-form-item>
				</a-col>
				<a-col :span="12">
					<a-form-item label="排序:" name="sortCode">
						<a-input-number
							v-model:value="formData.sortCode"
							style="width: 100%"
							placeholder="请输入排序码"
							:max="1000"
						/>
					</a-form-item>
				</a-col>
			</a-row>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="templateSnForm">
	import tool from '@/utils/tool'
	import { required } from '@/utils/formRules'
	import templateSnApi from '@/api/flw/templateSnApi'
	// 默认是关闭状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })
	const formRef = ref()
	// 表单数据
	const formData = ref({})

	// 打开抽屉
	const onOpen = (record) => {
		visible.value = true
		formData.value = {
			sortCode: 99
		}
		if (record) {
			formData.value = Object.assign({}, record)
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		name: [required('请输入流水号名称')],
		category: [required('请选择分类')],
		dateFormat: [required('请输入年月格式，例如：yyyyMMdd')],
		prefix: [required('请输入前缀')],
		previewValue: [required('请输入显示值，例如：{yyyyMMdd}{0001}')],
		suffixDigits: [required('请输入后缀位数')],
		suffixInitialValue: [required('请输入后缀初始值')],
		suffixIncrementalValue: [required('请输入后缀增量')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			templateSnApi.templateSnSubmitForm(formData.value, formData.value.id).then(() => {
				onClose()
				emit('successful')
			})
		})
	}
	// 分类
	const categoryOptions = tool.dictList('TEMPLATE_SN_CATEGORY')
	// 暴露出去
	defineExpose({
		onOpen
	})
</script>
