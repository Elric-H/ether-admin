<template>
	<start-event v-if="childNode.type === 'startEvent'" v-model="childNode" />
	<start-task
		v-if="childNode.type === 'startTask'"
		v-model="childNode"
		:formFieldListValue="formFieldListValue"
		:recordData="recordData"
		:processConfigInfo="processConfigInfo"
		:execution-listener-array="executionListenerArray"
		:task-listener-array="taskListenerArray"
		:selector-api-function="selectorApiFunction"
	/>
	<user-task
		v-if="childNode.type === 'userTask'"
		v-model="childNode"
		:formFieldListValue="formFieldListValue"
		:recordData="recordData"
		:processConfigInfo="processConfigInfo"
		:execution-listener-array="executionListenerArray"
		:task-listener-array="taskListenerArray"
		:selector-api-function="selectorApiFunction"
	/>
	<service-task
		v-if="childNode.type === 'serviceTask'"
		v-model="childNode"
		:execution-listener-array="executionListenerArray"
		:selector-api-function="selectorApiFunction"
		:form-field-list-value="formFieldListValue"
		:record-data="recordData"
	/>
	<exclusive-gateway
		v-if="childNode.type === 'exclusiveGateway'"
		v-model="childNode"
		:form-field-list-value="formFieldListValue"
		:record-data="recordData"
		:execution-listener-array="executionListenerArray"
		:task-listener-array="taskListenerArray"
		:selector-api-function="selectorApiFunction"
	>
		<template #default="slot">
			<node-wrap
				v-if="slot.node"
				v-model="slot.node.childNode"
				:formFieldListValue="formFieldListValue"
				:recordData="recordData"
				:processConfigInfo="processConfigInfo"
				:execution-listener-array="executionListenerArray"
				:task-listener-array="taskListenerArray"
				:selector-api-function="selectorApiFunction"
			/>
		</template>
	</exclusive-gateway>
	<parallel-gateway
		v-if="childNode.type === 'parallelGateway'"
		v-model="childNode"
		:formFieldListValue="formFieldListValue"
		:recordData="recordData"
		:processConfigInfo="processConfigInfo"
		:execution-listener-array="executionListenerArray"
		:task-listener-array="taskListenerArray"
		:selector-api-function="selectorApiFunction"
	>
		<template #default="slot">
			<node-wrap
				v-if="slot.node"
				v-model="slot.node.childNode"
				:formFieldListValue="formFieldListValue"
				:recordData="recordData"
				:processConfigInfo="processConfigInfo"
				:execution-listener-array="executionListenerArray"
				:task-listener-array="taskListenerArray"
				:selector-api-function="selectorApiFunction"
			/>
		</template>
	</parallel-gateway>
	<node-wrap
		v-if="childNode.childNode"
		v-model="childNode.childNode"
		:formFieldListValue="formFieldListValue"
		:recordData="recordData"
		:processConfigInfo="processConfigInfo"
		:execution-listener-array="executionListenerArray"
		:task-listener-array="taskListenerArray"
		:selector-api-function="selectorApiFunction"
	/>
</template>

<script setup>
	import StartEvent from './nodes/startEvent.vue'
	import StartTask from './nodes/startTask.vue'
	import UserTask from './nodes/userTask.vue'
	import ExclusiveGateway from './nodes/exclusiveGateway.vue'
	import ParallelGateway from './nodes/parallelGateway.vue'
	import ServiceTask from './nodes/serviceTask.vue'
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		formFieldListValue: { type: Array, default: () => [] },
		recordData: { type: Object, default: () => {} },
		processConfigInfo: { type: Object, default: () => {} },
		executionListenerArray: { type: Array, default: () => [] },
		taskListenerArray: { type: Array, default: () => [] },
		selectorApiFunction: { type: Object, default: () => {} }
	})
	const emit = defineEmits(['update:modelValue'])
	// 创建响应式引用
	const childNode = ref({})
	// 监听 props.modelValue 的变化，并同步到 childNode.value
	watch(
		() => props.modelValue,
		(newValue) => {
			childNode.value = newValue
		},
		{ immediate: true } // 立即执行一次监听器
	)
	// 监听 childNode.value 的变化，并发出自定义事件 update:modelValue
	watch(childNode, (newValue) => {
		emit('update:modelValue', newValue)
	})
</script>
