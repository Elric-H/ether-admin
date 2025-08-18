<!--
 * @Descripttion: 仿钉钉流程设计器
 * @version: 1.2
 * @Author: sakuya
 * @Date: 2021年9月14日08:38:35
 * @LastEditors: yubaoshan
 * @LastEditTime: 2022年2月9日16:48:49
-->
<template>
	<div class="workflow-design">
		<!-- 配置流程全局属性 -->
		<div style="float: right; padding-right: 10px">
			<span v-if="!toDataLegal(childNode)" style="padding-right: 5px">
				<exclamation-circle-outlined style="color: red; font-size: 18px" />
			</span>
			<a-tooltip>
				<template #title>配置流程全局属性</template>
				<a-button @click="processRef.showDrawer()">
					<template #icon>
						<setting-outlined />
					</template>
					全局配置
				</a-button>
			</a-tooltip>
		</div>
		<div class="box-scale">
			<node-wrap
				v-if="childNode"
				v-model="childNode.childNode"
				:form-field-list-value="childFormFieldListValue"
				:record-data="childRecordData"
				:process-config-info="childNode.properties.configInfo"
				:execution-listener-array="executionListenerArray"
				:task-listener-array="taskListenerArray"
				:selector-api-function="selectorApiFunction"
			/>
			<div class="end-node">
				<div class="end-node-circle"></div>
				<div class="end-node-text">流程结束</div>
			</div>
		</div>
		<process
			ref="processRef"
			v-model="childNode"
			:form-field-list-value="childFormFieldListValue"
			:record-data="childRecordData"
			:sn-template-array="snTemplateArray"
			:print-template-array="printTemplateArray"
			:execution-listener-array="executionListenerArray"
			:execution-listener-selector-for-custom-event-array="executionListenerSelectorForCustomEventArray"
			:task-listener-array="taskListenerArray"
			:selector-api-function="selectorApiFunction"
		/>
	</div>
</template>

<script setup>
	import NodeWrap from './nodeWrap.vue'
	import Process from './process.vue'
	const props = defineProps({
		modelValue: { type: Object, default: () => ({}) },
		formFieldListValue: { type: Array, default: () => [] },
		recordData: { type: Object, default: () => ({}) },
		snTemplateArray: { type: Array, default: () => [] },
		printTemplateArray: { type: Array, default: () => [] },
		executionListenerArray: { type: Array, default: () => [] },
		executionListenerSelectorForCustomEventArray: { type: Array, default: () => [] },
		listenerType: { type: String, default: 'default' },
		taskListenerArray: { type: Array, default: () => [] },
		selectorApiFunction: { type: Object, default: () => ({}) }
	})
	const processRef = ref()
	const emit = defineEmits(['update:modelValue'])
	const childNode = ref()
	const childFormFieldListValue = ref()
	const childRecordData = ref()

	watch(props, (newValue) => {
		if (props.modelValue) {
			childNode.value = newValue.modelValue
		}
		if (props.recordData) {
			childRecordData.value = newValue.recordData
		}
		if (props.formFieldListValue) {
			childFormFieldListValue.value = newValue.formFieldListValue
		}
	})
	watch(childNode, (newValue) => {
		emit('update:modelValue', newValue)
	})
	const toDataLegal = (childNode) => {
		if (childNode === undefined) {
			return false
		} else {
			return childNode.dataLegal
		}
	}
</script>
<style lang="less">
	@import './flowIndex.less';
</style>
