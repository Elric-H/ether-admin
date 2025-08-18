<template>
	<a-modal
		title="跳转"
		:width="600"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:confirmLoading="confirmLoading"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="跳转节点：" name="targetActivityId">
				<a-select
					v-model:value="formData.targetActivityId"
					placeholder="请选择要跳转的节点"
					:options="targetActivityIdList"
				/>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="processJumpForm">
	import { required } from '@/utils/formRules'
	import processApi from '@/api/flw/processApi'
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const confirmLoading = ref(false)
	const formRef = ref()
	// 表单数据
	const formData = ref({})
	const targetActivityIdList = ref([])
	// 默认要校验的
	const formRules = {
		targetActivityId: [required('请选择要跳转的节点')]
	}
	// 打开抽屉
	const onOpen = (record) => {
		formData.value = {
			id: record.id
		}
		const params = {
			id: record.id
		}
		processApi.processGetCanJumpNodeInfoList(params).then((data) => {
			visible.value = true
			targetActivityIdList.value = data.map((m) => {
				return {
					label: m.name,
					value: m.id
				}
			})
		})
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		visible.value = false
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			confirmLoading.value = true
			processApi
				.processJump(formData.value)
				.then(() => {
					onClose()
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
