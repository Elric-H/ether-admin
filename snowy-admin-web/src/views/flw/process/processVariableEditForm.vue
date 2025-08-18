<template>
	<a-modal
		title="编辑"
		:width="600"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:mask="false"
		:confirmLoading="confirmLoading"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="变量名称：" name="variableKey">
				<a-input v-model:value="formData.variableKey" placeholder="请输入变量名称" disabled />
			</a-form-item>
			<a-form-item label="变量类型：" name="variableTypeName">
				<a-input v-model:value="formData.variableTypeName" placeholder="请输入变量名称" disabled />
			</a-form-item>
			<a-form-item label="变量值：" name="variableValue">
				<a-textarea
					v-model:value="formData.variableValue"
					placeholder="请输入变量值"
					:auto-size="{ minRows: 3, maxRows: 15 }"
				/>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="processVariableEditForm">
	import processApi from '@/api/flw/processApi'
	import { message } from 'ant-design-vue'
	import { cloneDeep } from 'lodash-es'
	import { required } from '@/utils/formRules'
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const confirmLoading = ref(false)
	const formRef = ref()
	const formData = ref({})
	const recordData = ref({})
	const formRules = {
		variableKey: [required('请输入变量名称')],
		variableTypeName: [required('请输入变量类型')],
		variableValue: [required('请输入变量值')]
	}
	// 打开抽屉
	const onOpen = (record) => {
		recordData.value = record
		formData.value = {
			processInstanceId: record.processInstanceId,
			variableKey: record.name,
			variableTypeName: record.typeName,
			variableValue: record.value
		}
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		visible.value = false
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			confirmLoading.value = true
			const submitFormData = cloneDeep(formData.value)
			submitFormData.variableInfoList = [
				{
					variableKey: formData.value.variableKey,
					variableValue: formData.value.variableValue,
					typeName: formData.value.variableTypeName,
					executionId: recordData.value.executionId
				}
			]
			delete submitFormData.variableKey
			delete submitFormData.variableValue
			delete submitFormData.variableTypeName
			processApi
				.processVariableUpdateBatch(submitFormData)
				.then(() => {
					onClose()
					message.success('操作成功')
					emit('successful')
				})
				.finally(() => {
					confirmLoading.value = false
				})
		})
	}
	defineExpose({
		onOpen
	})
</script>
