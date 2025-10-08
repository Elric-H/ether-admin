<template>
	<a-modal
		title="跳转"
		:width="800"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:mask="false"
		:confirmLoading="confirmLoading"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="节点：" name="targetActivityId">
				<a-select
					v-model:value="formData.targetActivityId"
					placeholder="请选择要跳转的节点"
					:options="backNodeInfoList"
				/>
			</a-form-item>
			<a-form-item label="意见：" name="comment">
				<a-textarea
					v-model:value="formData.comment"
					placeholder="请输入跳转意见"
					allow-clear
					:auto-size="{ minRows: 3, maxRows: 6 }"
				/>
			</a-form-item>
			<a-form-item label="附件：" name="attachment">
				<XnUpload ref="uploadRef" :uploadNumber="5" />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="taskJumpForm">
	import { required } from '@/utils/formRules'
	import { message } from 'ant-design-vue'
	import taskApi from '@/api/flw/taskApi'
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const confirmLoading = ref(false)
	const formRef = ref()
	const uploadRef = ref()
	// 表单数据
	const formData = ref({})
	const backNodeInfoList = ref([])
	// 默认要校验的
	const formRules = {
		targetActivityId: [required('请选择要跳转的节点')]
	}
	// 打开抽屉
	const onOpen = (record, dataJson, processEnableCommentRequired) => {
		formData.value = {
			id: record.id,
			dataJson: dataJson
		}
		visible.value = true
		if (processEnableCommentRequired) {
			formRules.comment = [required('请输入意见')]
		}
		const params = {
			processInstanceId: record.processInstanceId,
			currentActivityId: record.currentActivityId
		}
		taskApi.taskGetCanJumpNodeInfoList(params).then((data) => {
			backNodeInfoList.value = data.map((m) => {
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
			// 验证完主表单，此时获得上传的文件
			const fileList = uploadRef.value.uploadFileList()
			if (fileList) {
				const attachmentList = []
				fileList.forEach((item) => {
					const fileObj = {
						attachmentName: item.name,
						attachmentUrl: item.url
					}
					attachmentList.push(fileObj)
				})
				formData.value.attachmentList = attachmentList
			}
			// 提交
			confirmLoading.value = true
			taskApi
				.taskJump(formData.value)
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
