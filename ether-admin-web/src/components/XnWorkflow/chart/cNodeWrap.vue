<template>
	<start-events v-if="childNode.type === 'startEvent'" v-model="childNode" :currentActivityId="currentActivityId" />
	<start-tasks v-if="childNode.type === 'startTask'" v-model="childNode" :currentActivityId="currentActivityId" />
	<user-tasks v-if="childNode.type === 'userTask'" v-model="childNode" :currentActivityId="currentActivityId" />
	<service-tasks v-if="childNode.type === 'serviceTask'" v-model="childNode" :currentActivityId="currentActivityId" />
	<exclusive-gateways
		v-if="childNode.type === 'exclusiveGateway'"
		v-model="childNode"
		:currentActivityId="currentActivityId"
	>
		<template #default="slot">
			<c-node-wrap v-if="slot.node" v-model="slot.node.childNode" :currentActivityId="currentActivityId" />
		</template>
	</exclusive-gateways>
	<parallel-gateways
		v-if="childNode.type === 'parallelGateway'"
		v-model="childNode"
		:currentActivityId="currentActivityId"
	>
		<template #default="slot">
			<c-node-wrap v-if="slot.node" v-model="slot.node.childNode" :currentActivityId="currentActivityId" />
		</template>
	</parallel-gateways>
	<c-node-wrap v-if="childNode.childNode" v-model="childNode.childNode" :currentActivityId="currentActivityId" />
</template>

<script setup>
	import StartEvents from './nodes/cStartEvent.vue'
	import StartTasks from './nodes/cStartTask.vue'
	import UserTasks from './nodes/cUserTask.vue'
	import ExclusiveGateways from './nodes/cExclusiveGateway.vue'
	import ParallelGateways from './nodes/cParallelGateway.vue'
	import ServiceTasks from './nodes/cServiceTask.vue'
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		currentActivityId: { type: String, default: () => '' }
	})
	const emit = defineEmits(['update:modelValue'])
	const childNode = ref({})
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
	onMounted(() => {
		childNode.value = props.modelValue
	})
</script>
