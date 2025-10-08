<template>
	<div class="branch-wrap">
		<div class="branch-box-wrap">
			<div class="branch-box">
				<a-button class="add-branch" type="primary" shape="round" @click="addTerm"> 添加条件 </a-button>
				<div v-for="(item, index) in childNode.conditionNodeList" :key="index" class="col-box">
					<div class="condition-node">
						<div class="condition-node-box">
							<div class="auto-judge" @click="show(index)">
								<div v-if="index != 0" class="sort-left" @click.stop="arrTransfer(index, -1)">
									<left-outlined />
								</div>
								<div class="title">
									<span class="node-title">{{ item.title }}</span>
									<span class="priority-title">优先级{{ item.properties.configInfo.priorityLevel }}</span>
									<close-outlined class="close" @click.stop="delTerm(index)" />
								</div>
								<div class="content">
									<span v-if="toText(childNode, index)">{{ toText(childNode, index) }}</span>
									<span v-else class="placeholder">请设置条件</span>
								</div>
								<div
									v-if="index !== childNode.conditionNodeList.length - 1"
									class="sort-right"
									@click.stop="arrTransfer(index)"
								>
									<right-outlined />
								</div>
							</div>
							<add-node v-model="item.childNode" :node-item="item" />
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

		<xn-form-container
			v-model:visible="drawer"
			:destroy-on-close="true"
			:width="700"
			:body-style="{ 'padding-top': '0px' }"
		>
			<template #title>
				<div class="node-wrap-drawer__title">
					<label v-if="!isEditTitle" @click="editTitle">
						{{ form.title }}
						<edit-outlined class="node-wrap-drawer-title-edit" />
					</label>
					<a-input
						v-if="isEditTitle"
						ref="nodeTitleRef"
						v-model:value="form.title"
						allow-clear
						@blur="saveTitle"
						@keyup.enter="saveTitle"
					/>
				</div>
			</template>
			<a-layout-content>
				<a-tabs v-model:activeKey="activeKey">
					<a-tab-pane key="1" tab="条件配置" force-render>
						<a-form layout="vertical">
							<div v-show="!isNodeLegal(form)" style="margin-bottom: 10px">
								<a-alert message="请填写完成所有项！" type="error" />
							</div>
							<div class="mb-2">
								<span class="left-span-label">配置要执行的条件</span>
							</div>
							<p style="margin-bottom: 2px">
								<a-button type="primary" round @click="addDynamicValidateForm" size="small">
									<plus-outlined />
									增加条件组
								</a-button>
							</p>
							<a-form-item v-for="(domain, index) in dynamicValidateForm" :key="index">
								<a-divider style="margin: 10px 0" />
								<a-row>
									<a-col :span="22">
										<a-table :data-source="domain" size="small" :pagination="false">
											<a-table-column data-index="field" title="条件字段" width="130">
												<template #default="{ record }">
													<a-select
														v-model:value="record.field"
														placeholder="请选择"
														v-if="recordData.formType === 'DESIGN'"
													>
														<a-select-option
															v-for="formField in fieldList"
															:key="formField.model"
															:value="formField.model"
															@click="record.label = formField.label"
															>{{ formField.label }}</a-select-option
														>
													</a-select>
													<a-input v-model:value="record.field" placeholder="条件" v-else />
												</template>
											</a-table-column>
											<a-table-column data-index="label" title="描述">
												<template #default="{ record }">
													<a-input v-model:value="record.label" placeholder="描述" />
												</template>
											</a-table-column>
											<a-table-column data-index="operator" width="140">
												<template #title>
													<a-tooltip>
														<template #title
															>注：自定义表单模式下条件选择完全放开，中文字段不可以使用大于、大于等于、小于、小于等于！</template
														>
														<question-circle-outlined />
														运算符
													</a-tooltip>
												</template>
												<template #default="{ record }">
													<a-select v-model:value="record.operator" placeholder="请选择">
														<a-select-option value="==">等于</a-select-option>
														<a-select-option value="!=">不等于</a-select-option>
														<a-select-option value=">" v-if="isSelectOption(record)">大于</a-select-option>
														<a-select-option value=">=" v-if="isSelectOption(record)">大于等于</a-select-option>
														<a-select-option value="<" v-if="isSelectOption(record)">小于</a-select-option>
														<a-select-option value="<=" v-if="isSelectOption(record)">小于等于</a-select-option>
														<a-select-option value="include" v-if="!isSelectOption(record, 'include')"
															>包含</a-select-option
														>
														<a-select-option value="notInclude" v-if="!isSelectOption(record, 'notInclude')"
															>不包含</a-select-option
														>
													</a-select>
												</template>
											</a-table-column>
											<a-table-column data-index="value" width="100">
												<template #title>
													<a-tooltip>
														<template #title>中文字段需判断等于，值必须加入英文双引号</template>
														<question-circle-outlined />
														值
													</a-tooltip>
												</template>
												<template #default="{ record }">
													<a-input v-model:value="record.value" placeholder="值" />
												</template>
											</a-table-column>
											<a-table-column data-index="value" title="移除" width="55">
												<template #default="{ index }">
													<a-button size="small" type="primary" danger ghost @click="deleteConditionList(index, domain)"
														>移除</a-button
													>
												</template>
											</a-table-column>
										</a-table>
										<a-button type="dashed" class="dashedButton" @click="addConditionList(index)">
											<PlusOutlined />
											增加条件
										</a-button>
									</a-col>
									<a-col :span="2" class="deleteIcon">
										<minus-circle-two-tone class="minusCircle" @click="delDomains(index)" />
									</a-col>
								</a-row>
							</a-form-item>
						</a-form>
					</a-tab-pane>
					<a-tab-pane key="2" tab="执行监听" force-render>
						<prop-listener-info
							ref="propExecutionListenerInfoRef"
							:listenerValue="form.properties.executionListenerInfo"
							:defaultListenerList="executionListenerInfo"
							:listener-value-array="executionListenerArray"
						/>
					</a-tab-pane>
				</a-tabs>
			</a-layout-content>
			<template #footer>
				<a-button type="primary" style="margin-right: 8px" @click="save">保存</a-button>
				<a-button @click="drawer = false">取消</a-button>
			</template>
		</xn-form-container>
	</div>
</template>

<script setup>
	import { cloneDeep, isEmpty } from 'lodash-es'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import workFlowUtils from '@/components/XnWorkflow/nodes/utils/index'
	import tool from '@/utils/tool'
	import AddNode from './addNode.vue'
	import PropListenerInfo from './prop/propListenerInfo.vue'
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		formFieldListValue: { type: Array, default: () => [] },
		recordData: { type: Object, default: () => {} },
		executionListenerArray: { type: Array, default: () => [] },
		taskListenerArray: { type: Array, default: () => [] }
	})
	const emit = defineEmits(['update:modelValue'])
	const nodeTitleRef = ref()
	const propExecutionListenerInfoRef = ref()
	const childNode = ref({})
	const drawer = ref(false)
	const isEditTitle = ref(false)
	const index = ref(0)
	const form = ref({})
	const dynamicValidateForm = ref([])
	const fieldList = ref([])
	const activeKey = ref('1')
	const executionListenerInfo = cloneDeep(config.listener.exclusiveGatewayExecutionListenerInfo)
	const operatorList = cloneDeep(config.exclusiveGatewayConfig.operatorList)
	watch(props, (newValue) => {
		if (newValue.modelValue) {
			childNode.value = newValue.modelValue
		}
	})
	onMounted(() => {
		childNode.value = props.modelValue
		// 把字段给掏出来
		fieldList.value = workFlowUtils.getListField(props.formFieldListValue).map((m) => {
			let type = m.type //slider rate number
			if (type === 'slider' || type === 'rate' || type === 'number') {
				m.type = 'number'
			}
			return {
				label: m.label,
				model: m.selectTable + '.' + m.model,
				type: m.type
			}
		})
	})
	const show = (value) => {
		index.value = value
		form.value = {}
		form.value = cloneDeep(childNode.value.conditionNodeList[value])
		drawer.value = true
		dynamicValidateForm.value = form.value.properties.conditionInfo
	}
	const editTitle = () => {
		isEditTitle.value = true
		nextTick(() => {
			nodeTitleRef.value.focus()
		})
	}
	const saveTitle = () => {
		isEditTitle.value = false
	}
	const save = () => {
		form.value.properties.conditionInfo = dynamicValidateForm.value
		form.value.properties.executionListenerInfo = propExecutionListenerInfoRef.value.selectedListenerList()
		if (isNodeLegal(form.value)) {
			form.value.dataLegal = true
			childNode.value.conditionNodeList[index.value] = form.value
			setCalibration()
			emit('update:modelValue', childNode)
			drawer.value = false
		} else {
			form.value.dataLegal = false
		}
	}
	const isSelectOption = (record, value) => {
		if (props.recordData.formType === 'DESIGN') {
			if (record.field) {
				return fieldList.value.find((f) => f.model === record.field).type === 'number'
			}
		} else {
			if (value === 'include' || value === 'notInclude') {
				return false
			} else {
				return true
			}
		}
	}
	// 校验此条件是否通过
	const isNodeLegal = (data) => {
		const priorityLevel = data.properties.configInfo.priorityLevel
		const len = childNode.value.conditionNodeList.length
		const priorityLevelMax = childNode.value.conditionNodeList[len - 1].properties.configInfo.priorityLevel
		// 如果往其他条件的分支中增加，那我们一视同仁
		if (priorityLevelMax === priorityLevel) {
			if (dynamicValidateForm.value.length > 0) {
				for (let i = 0; i < dynamicValidateForm.value.length; i++) {
					const obj = dynamicValidateForm.value[i]
					if (obj.length > 0) {
						return isNodeLegalItem()
					}
				}
			} else {
				return true
			}
		} else {
			return isNodeLegalItem()
		}
	}
	// 设置校验
	const setCalibration = () => {
		// 在数据返回更新之前，我要顺手吧优先级最后的条件校验设置为 true，管他设没设
		for (let i = 0; i < childNode.value.conditionNodeList.length; i++) {
			let conditionNode = childNode.value.conditionNodeList[i]
			// 取到优先级
			const priorityLevel = conditionNode.properties.configInfo.priorityLevel
			// 如果是最高的
			if (priorityLevel === childNode.value.conditionNodeList.length) {
				// 给成通过，不管他的条件，本身优先级最后的就是其他条件进入，一般也不设
				conditionNode.dataLegal = true
			} else {
				// 其他地方的，判断是否有条件，无条件的统统给 false
				if (conditionNode.properties.conditionInfo.length === 0) {
					conditionNode.dataLegal = false
				}
			}
		}
	}
	const isNodeLegalItem = () => {
		let objNum = 0
		let successNum = 0
		if (dynamicValidateForm.value.length > 0) {
			for (let i = 0; i < dynamicValidateForm.value.length; i++) {
				const obj = dynamicValidateForm.value[i]
				let objNumItem = 0
				if (!isEmpty(obj)) {
					for (let a = 0; a < obj.length; a++) {
						objNumItem++
						if (isObjLegal(obj[a])) {
							successNum++
						}
					}
					objNum = objNum + objNumItem
				} else {
					objNum++
				}
			}
		}
		if (successNum !== 0) {
			if (objNum === successNum) {
				return true
			}
		}
		return false
	}
	// 校验对象中是否有空值
	const isObjLegal = (obj) => {
		let a = 0
		for (let b in obj) {
			if (!obj[b]) {
				a++
			}
		}
		return a === 1
	}
	// 增加条件组
	const addDynamicValidateForm = () => {
		dynamicValidateForm.value.push([])
	}
	// 删除条件组
	const delDomains = (index) => {
		dynamicValidateForm.value.splice(index, 1)
	}
	const addTerm = () => {
		const len = childNode.value.conditionNodeList.length
		const priorityLevel = childNode.value.conditionNodeList[len - 1].properties.configInfo.priorityLevel
		// 创建分支节点 n
		const condition = cloneDeep(config.nodeModel.node)
		condition.id = tool.snowyUuid()
		condition.type = 'sequenceFlow'
		condition.title = `条件${priorityLevel + 1}`
		// 创建分支节点2 configInfo
		const condition1ConfigInfo = cloneDeep(config.nodeConfigInfo.conditionConfigInfo)
		condition1ConfigInfo.priorityLevel = priorityLevel + 1
		condition.properties.configInfo = condition1ConfigInfo
		childNode.value.conditionNodeList.push(condition)
	}
	const delTerm = (index) => {
		childNode.value.conditionNodeList.splice(index, 1)
		if (childNode.value.conditionNodeList.length === 1) {
			if (childNode.value.childNode) {
				// 如果有子项
				if (!isEmpty(childNode.value.conditionNodeList[0].childNode)) {
					reData(childNode.value.conditionNodeList[0].childNode, childNode.value.childNode)
				} else {
					childNode.value.conditionNodeList[0].childNode = childNode.value.childNode
				}
			}
			emit('update:modelValue', childNode.value.conditionNodeList[0].childNode)
		}
	}
	const reData = (data, addData) => {
		if (!isEmpty(data)) {
			data.childNode = addData
		} else {
			reData(data.childNode, addData)
		}
	}
	const arrTransfer = (index, type = 1) => {
		childNode.value.conditionNodeList[index] = childNode.value.conditionNodeList.splice(
			index + type,
			1,
			childNode.value.conditionNodeList[index]
		)[0]
		childNode.value.conditionNodeList.map((item, index) => {
			item.properties.configInfo.priorityLevel = index + 1
		})
		setCalibration()
		emit('update:modelValue', childNode.value)
	}
	const addConditionList = (index) => {
		const domainsObj = {
			label: '',
			key: '',
			operator: '==',
			value: ''
		}
		dynamicValidateForm.value[index].push(domainsObj)
	}
	const deleteConditionList = (index, domain) => {
		domain.splice(index, 1)
	}
	const toText = (childNode, index) => {
		const conditionList = childNode.conditionNodeList[index].properties.conditionInfo
		const priorityLevel = childNode.conditionNodeList[index].properties.configInfo.priorityLevel
		const len = childNode.conditionNodeList.length
		const priorityLevelMax = childNode.conditionNodeList[len - 1].properties.configInfo.priorityLevel
		if (JSON.stringify(conditionList) !== undefined && conditionList.length > 0) {
			let text = ''
			for (let i = 0; i < conditionList.length; i++) {
				for (let j = 0; j < conditionList[i].length; j++) {
					if (j + 1 !== conditionList[i].length) {
						text =
							text +
							conditionList[i][j].label +
							getOperatorLabel(conditionList[i][j].operator) +
							conditionList[i][j].value +
							' 且 '
					} else {
						text =
							text +
							conditionList[i][j].label +
							getOperatorLabel(conditionList[i][j].operator) +
							conditionList[i][j].value
					}
				}
				if (i + 1 !== conditionList.length) {
					text = text + ' 或 '
				}
			}
			return text
		} else if (conditionList.length === 0 && priorityLevel < priorityLevelMax) {
			return false
		} else {
			return '其他条件进入此流程'
		}
	}
	// 通过value 获取界面显示的label汉字
	const getOperatorLabel = (value) => {
		return operatorList.find((item) => item.value === value).label
	}
</script>
<style scoped>
	.deleteIcon {
		display: flex;
		justify-content: center;
		align-items: center;
	}
	.minusCircle {
		font-size: 25px;
	}
	.dashedButton {
		margin-top: 10px;
		width: 100%;
	}
</style>
