<template>
	<a-modal
		title="预览"
		:width="700"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:mask="false"
		:confirmLoading="confirmLoading"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<component ref="customFormRef" :is="customFormsLayouts" />
	</a-modal>
</template>

<script setup name="previewCustomForm">
	import { message } from 'ant-design-vue'
	import { loadComponent } from '../../customForm'
	const visible = ref(false)
	const confirmLoading = ref(false)
	const customFormRef = ref()
	const customFormsLayouts = ref()
	const onOpen = (url) => {
		if (url) {
			visible.value = true
			customFormsLayouts.value = loadComponent(url)
		}
	}
	const onSubmit = () => {
		customFormRef.value.getData().then((value) => {
			message.info(JSON.stringify(value))
		})
	}
	const onClose = () => {
		visible.value = false
	}
	defineExpose({
		onOpen
	})
</script>
