<template>
	<xn-form-container
		:title="formData.id ? '编辑打印模板' : '增加打印模板'"
		:width="600"
		:visible="visible"
		:destroy-on-close="true"
		@close="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="模板名称：" name="name">
				<a-input v-model:value="formData.name" placeholder="请输入打印模板名称" allow-clear />
			</a-form-item>
			<a-form-item label="分类：" name="category">
				<a-select v-model:value="formData.category" placeholder="请选择分类" :options="categoryOptions" />
			</a-form-item>
			<a-form-item label="内容：" name="content">
				<a-textarea v-model:value="formData.content" placeholder="请输入内容" :auto-size="{ minRows: 3, maxRows: 5 }" />
			</a-form-item>
			<a-form-item label="排序码:" name="sortCode">
				<a-input-number v-model:value="formData.sortCode" style="width: 100%" placeholder="请输入排序码" :max="1000" />
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit">保存</a-button>
		</template>
	</xn-form-container>
</template>

<script setup name="templatePrintForm">
	import tool from '@/utils/tool'
	import { required } from '@/utils/formRules'
	import templatePrintApi from '@/api/flw/templatePrintApi'
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
		name: [required('请输入模板名称')],
		category: [required('请选择分类')],
		content: [required('请输入内容')]
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			templatePrintApi.templatePrintSubmitForm(formData.value, formData.value.id).then(() => {
				onClose()
				emit('successful')
			})
		})
	}
	// 分类
	const categoryOptions = tool.dictList('TEMPLATE_PRINT_CATEGORY')
	// 暴露出去
	defineExpose({
		onOpen
	})
</script>
