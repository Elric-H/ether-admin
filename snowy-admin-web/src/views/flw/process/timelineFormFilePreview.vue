<template>
	<a-drawer
		v-model:visible="visible"
		:body-style="{ padding: '0px' }"
		width="80%"
		:footer="null"
		:mask="false"
		:closable="false"
		@cancel="goBack"
	>
		<template #title>
			<a-tooltip>
				<template #title>当流程有多个文件，可以在此处下拉框选择切换，无需关闭后重新打开！</template>
				<question-circle-outlined />
			</a-tooltip>

			<a-popover placement="bottomLeft">
				<template #content>
					<a v-for="attachment in recordArray" @click="preview(attachment)" :key="attachment.attachmentName"
						><p><file-outlined />{{ attachment.attachmentName }}</p>
					</a>
				</template>
				文件预览<down-outlined />
			</a-popover>
		</template>
		<xn-file-preview :src="src" :file-type="fileType" @goBack="goBack" />
	</a-drawer>
</template>

<script setup name="filePreview">
	const src = ref()
	const fileType = ref()
	const emit = defineEmits({ goBack: null })
	const visible = ref(false)
	const recordArray = ref([])
	// 打开抽屉
	const onOpen = (record, array) => {
		visible.value = true
		recordArray.value = array
		src.value = record.downloadPath
		fileType.value = record.suffix
	}
	// 返回
	const goBack = () => {
		visible.value = false
		recordArray.value = []
		emit('goBack')
	}
	// 切换文件预览
	const preview = (attachment) => {
		const fileName = attachment.attachmentName
		const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1)
		src.value = attachment.attachmentUrl
		fileType.value = fileExtension
	}
	defineExpose({
		onOpen
	})
</script>
