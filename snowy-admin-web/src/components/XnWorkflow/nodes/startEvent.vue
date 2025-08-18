<template>
	<div class="node-wrap" />
</template>
<script setup>
	import { cloneDeep } from 'lodash-es'
	import tool from '@/utils/tool'
	const props = defineProps({
		modelValue: { type: Object, default: () => {} }
	})
	const emit = defineEmits(['update:modelValue'])
	const childNode = ref({})
	const isEditTitle = ref(false)
	const form = ref({})
	watch(props, (newValue) => {
		if (props.modelValue) {
			childNode.value = newValue.modelValue
		}
	})
	onMounted(() => {
		childNode.value = props.modelValue
	})
	const show = () => {
		form.value = cloneDeep(childNode.value)
		isEditTitle.value = false
		drawer.value = true
	}
	const save = () => {
		form.value.id = tool.snowyUuid()
		emit('update:modelValue', form.value)
		drawer.value = false
	}
</script>
