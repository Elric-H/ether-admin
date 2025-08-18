<template>
	<div class="add-node-btn-box">
		<div class="add-node-btn">
			<a-popover v-model:visible="visible" placement="rightTop" trigger="click" :width="270">
				<template #content>
					<div class="add-node-popover-body">
						<ul style="height: 80px">
							<li>
								<a-button shape="circle" size="large" @click="addType('userTask')">
									<template #icon>
										<user-outlined style="color: #ff943e; font-size: 18px" />
									</template>
								</a-button>
								<p>审批节点</p>
							</li>
							<li>
								<a-button shape="circle" size="large" @click="addType('serviceTask')">
									<template #icon>
										<send-outlined style="color: #3296fa; font-size: 18px" />
									</template>
								</a-button>
								<p>抄送节点</p>
							</li>
							<li v-if="addExclusiveGateway">
								<a-button shape="circle" size="large" @click="addType('exclusiveGateway')">
									<template #icon>
										<share-alt-outlined style="color: #15bc83; font-size: 18px" />
									</template>
								</a-button>
								<p>条件分支</p>
							</li>
							<li v-if="addParallelGateway">
								<a-button shape="circle" size="large" @click="addType('parallelGateway')">
									<template #icon>
										<partition-outlined :rotate="180" style="color: #ac28f5; font-size: 18px" />
									</template>
								</a-button>
								<p>并行分支</p>
							</li>
						</ul>
					</div>
				</template>
				<a-button type="primary" shape="circle">
					<!--  @click="addNodeButton" -->
					<template #icon><plus-outlined /></template>
				</a-button>
			</a-popover>
		</div>
	</div>
</template>

<script setup>
	import { cloneDeep } from 'lodash-es'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import tool from '@/utils/tool'
	const NodeTitleMap = {
		userTask: '审核人',
		serviceTask: '抄送人',
		exclusiveGateway: '条件路由',
		parallelGateway: '并行路由'
	}
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		parentData: { type: Object, default: () => {} },
		nodeItem: { type: Object, default: () => {} }
	})
	const emit = defineEmits(['update:modelValue'])
	const visible = ref(false)
	const addExclusiveGateway = ref(true)
	const addParallelGateway = ref(true)
	const addNodeButton = () => {
		// 他的上级是条件分支或并行分支，将其不在添加 // 控制节点下面
		if (!props.parentData) {
			disabledChildren()
		} else {
			if (props.parentData.type === 'exclusiveGateway' || props.parentData.type === 'parallelGateway') {
				addExclusiveGateway.value = false
				addParallelGateway.value = false
			}
		}
	}
	const disabledChildren = () => {
		// 如果下级是条件分支或并行分支，将其不在添加 // 控制节点上面
		if (props.modelValue && props.modelValue.type) {
			if (props.modelValue.type === 'exclusiveGateway' || props.modelValue.type === 'parallelGateway') {
				addExclusiveGateway.value = false
				addParallelGateway.value = false
			}
		}
		// 不管其他的，如果是条件分支的项，那么他的下面无法添加条件
		if (props.nodeItem) {
			addExclusiveGateway.value = false
		}
	}
	const getBaseCondition = (type, title) => {
		const condition = cloneDeep(config.nodeModel.node)
		condition.id = tool.snowyUuid()
		condition.type = type
		condition.title = title
		return condition
	}
	const addType = (type) => {
		const nodeModel = getBaseCondition(type, NodeTitleMap[type]) || {}
		nodeModel.childNode = props.modelValue
		if (type === 'userTask') {
			// 创建 configInfo
			nodeModel.properties.configInfo = cloneDeep(config.nodeConfigInfo.userTaskConfigInfo)
		} else if (type === 'exclusiveGateway') {
			nodeModel.dataLegal = true
			// 创建分支节点1
			const condition1 = getBaseCondition('sequenceFlow', '条件1')
			// 创建分支节点1 configInfo
			const condition1ConfigInfo1 = cloneDeep(config.nodeConfigInfo.conditionConfigInfo)
			condition1ConfigInfo1.priorityLevel = 1
			condition1.properties.configInfo = condition1ConfigInfo1
			// 创建分支节点2
			const condition2 = getBaseCondition('sequenceFlow', '条件2')
			// 创建分支节点2 configInfo
			const condition1ConfigInfo2 = cloneDeep(config.nodeConfigInfo.conditionConfigInfo)
			condition1ConfigInfo2.priorityLevel = 2
			condition2.properties.configInfo = condition1ConfigInfo2
			// 装进去
			nodeModel.conditionNodeList.push(condition1)
			nodeModel.conditionNodeList.push(condition2)
		} else if (type === 'parallelGateway') {
			// 创建主节点
			nodeModel.dataLegal = true
			// 创建分支节点1
			const condition1 = getBaseCondition('userTask', '审批人1')
			condition1.properties.configInfo = cloneDeep(config.nodeConfigInfo.userTaskConfigInfo)
			condition1.dataLegal = true
			// 创建分支节点2
			const condition2 = getBaseCondition('userTask', '审批人2')
			condition2.properties.configInfo = cloneDeep(config.nodeConfigInfo.userTaskConfigInfo)
			condition2.dataLegal = true
			// 装进去
			nodeModel.conditionNodeList.push(condition1)
			nodeModel.conditionNodeList.push(condition2)
		}
		visible.value = false
		emit('update:modelValue', nodeModel)
	}
</script>
