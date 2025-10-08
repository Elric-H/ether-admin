<template>
	<a-modal
		:title="buttonType === 'PASS' ? '同意意见' : '拒绝意见'"
		:visible="visible"
		:destroy-on-close="true"
		:width="800"
		@ok="onOk"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="意见：" name="comment">
				<a-textarea
					v-model:value="formData.comment"
					:placeholder="buttonType === 'PASS' ? '请输入同意意见' : '请输入拒绝意见'"
					:auto-size="{ minRows: 3, maxRows: 6 }"
				/>
			</a-form-item>
			<a-form-item label="附件：" name="attachment">
				<XnUpload ref="uploadRef" :uploadNumber="5" />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="passRejectForm">
	import { required } from '@/utils/formRules'
	const emit = defineEmits({ ok: null })
	// 填写意见
	const visible = ref(false)
	const formRef = ref({})
	const formData = ref({})
	const buttonType = ref()
	const formRules = {}
	const uploadRef = ref()
	const onOpen = (type, processEnableCommentRequired) => {
		buttonType.value = type
		visible.value = true
		if (processEnableCommentRequired) {
			formRules.comment = [required(buttonType.value === 'PASS' ? '请输入同意意见' : '请输入拒绝意见')]
		}
	}
	const onOk = () => {
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
			emit('ok', formData.value)
			onClose()
		})
	}
	// 关闭小抽屉
	const onClose = () => {
		formData.value = {}
		visible.value = false
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
