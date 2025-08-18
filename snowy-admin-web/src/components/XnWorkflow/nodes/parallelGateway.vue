<template>
	<div class="branch-wrap">
		<div class="branch-box-wrap">
			<div class="branch-box">
				<a-button class="add-branch" type="primary" shape="round" @click="addTerm">添加并行</a-button>
				<div v-for="(item, index) in childNode.conditionNodeList" :key="index" class="col-box">
					<div class="condition-node">
						<div class="condition-node-box">
							<user-task
								v-model="childNode.conditionNodeList[index]"
								:form-field-list-value="formFieldListValue"
								:recordData="recordData"
								:processConfigInfo="processConfigInfo"
								:execution-listener-array="executionListenerArray"
								:task-listener-array="taskListenerArray"
								:selector-api-function="selectorApiFunction"
								@deleteParalle="delTerm(index)"
							/>
						</div>
					</div>
					<slot v-if="item.childNode" :node="item" />
					<div v-if="index === 0" class="top-left-cover-line" />
					<div v-if="index === 0" class="bottom-left-cover-line" />
					<div v-if="index === childNode.conditionNodeList.length - 1" class="top-right-cover-line" />
					<div v-if="index === childNode.conditionNodeList.length - 1" class="bottom-right-cover-line" />
				</div>
			</div>
			<add-node v-model="childNode.childNode" :parent-data="childNode" />
		</div>
	</div>
</template>

<script setup>
	import { cloneDeep } from 'lodash-es'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import tool from '@/utils/tool'
	import AddNode from './addNode.vue'
	import UserTask from './userTask.vue'
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
	const childNode = ref({})
	const drawer = ref(false)
	const index = ref(0)
	const form = ref({})
	watch(props, (newValue) => {
		if (props.modelValue) {
			childNode.value = newValue.modelValue
		}
	})
	onMounted(() => {
		childNode.value = props.modelValue
	})
	const addTerm = () => {
		const len = childNode.value.conditionNodeList.length + 1
		// 创建主节点
		const nodeModel = cloneDeep(config.nodeModel.node)
		nodeModel.id = tool.snowyUuid()
		nodeModel.type = 'userTask'
		;(nodeModel.title = `审批人${len}`),
			(nodeModel.priorityLevel = len),
			(nodeModel.conditionNodeList = []),
			(nodeModel.childNode = {})
		// 创建 configInfo
		nodeModel.properties.configInfo = cloneDeep(config.nodeConfigInfo.userTaskConfigInfo)
		childNode.value.conditionNodeList.push(nodeModel)
	}
	const delTerm = (index) => {
		childNode.value.conditionNodeList.splice(index, 1)
		if (childNode.value.conditionNodeList.length === 1) {
			if (childNode.value.childNode) {
				// 这是{}
				if (JSON.stringify(childNode.value.conditionNodeList[0].childNode) !== '{}') {
					reData(childNode.value.conditionNodeList[0].childNode, childNode.value.childNode)
				} else {
					childNode.value.conditionNodeList[0].childNode = childNode.value.childNode
				}
			}
			emit('update:modelValue', childNode.value.conditionNodeList[0].childNode)
		}
	}
	const reData = (data, addData) => {
		if (JSON.stringify(data) !== '{}') {
			data.childNode = addData
		} else {
			reData(data.childNode, addData)
		}
	}
</script>
