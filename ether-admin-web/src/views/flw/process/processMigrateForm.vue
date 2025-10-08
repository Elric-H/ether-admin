<template>
	<a-modal
		title="迁移"
		:width="600"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:confirmLoading="confirmLoading"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-alert message="迁移到节点后，将自动迁移到本模型最新版本" type="warning" class="mb-3" banner />
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="迁移到节点后：" name="targetActivityId">
				<a-select
					v-model:value="formData.targetActivityId"
					placeholder="请选择要迁移的节点后"
					:options="targetActivityIdList"
				/>
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="processMigrateForm">
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
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
		targetActivityId: [required('请选择要迁移的节点')]
	}
	// 打开抽屉
	const onOpen = (record) => {
		formData.value = {
			id: record.id
		}
		const params = {
			id: record.id
		}
		processApi.processGetCanMigrateNodeInfoList(params).then((data) => {
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
				.processMigrate(formData.value)
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
