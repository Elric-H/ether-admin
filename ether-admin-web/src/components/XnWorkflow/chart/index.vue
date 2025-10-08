<template>
	<div class="workflow-design">
		<div class="workflow-design-btns">
			<a-space>
				<a-tooltip>
					<template #title>放大</template>
					<a-button @click="handleZoom(true)" :disabled="zoom > 2">
						<template #icon><plus-outlined /></template>
					</a-button>
				</a-tooltip>
				<a-tooltip>
					<template #title>缩小</template>
					<a-button @click="handleZoom(false)" :disabled="zoom < 1">
						<template #icon><minus-outlined /></template>
					</a-button>
				</a-tooltip>
			</a-space>
		</div>
		<div id="div1">
			<div class="box-scale" :style="style">
				<node-wrap-chart v-if="childNode" v-model="childNode.childNode" :currentActivityId="currentActivityId" />
				<div class="end-node">
					<div class="end-node-circle"></div>
					<div class="end-node-text">流程结束</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
	import NodeWrapChart from '@/components/XnWorkflow/chart/cNodeWrap.vue'
	import FullscreenPreviewHelper from './zoom_helper'
	const FullScreenRightSpace = 20
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		currentActivityId: { type: String, default: () => '' }
	})
	const emit = defineEmits(['update:modelValue'])
	const childNode = ref(props.modelValue)
	const style = ref({})
	const zoom = ref(1)
	watch(props, (newValue) => {
		if (props.modelValue) {
			childNode.value = newValue.modelValue
		}
	})
	watch(childNode.value, (newValue) => {
		if (childNode.value) {
			emit('update:modelValue', newValue)
		}
	})
	const handleGetStyle = (zoom) => {
		return FullscreenPreviewHelper.getZoomStyles(zoom, 1, 0, FullScreenRightSpace)
	}
	const handleZoom = (zoomIn) => {
		const zoomData = FullscreenPreviewHelper.getZoomData(zoomIn, zoom.value)
		style.value = handleGetStyle(zoomData.zoom)
		zoom.value = zoomData.zoom
	}
</script>
<style lang="less">
	@import '../flowIndex.less';
	.workflow-design-btns {
		width: 100px;
	}
</style>
