<template>
	<div class="node-wrap">
		<div class="node-wrap-box" @click="show">
			<div class="title" style="background: #ff943e">
				<user-outlined class="icon" />
				<span>{{ childNode.title }}</span>
				<close-outlined class="close" @click.stop="delNode()" />
			</div>
			<div class="content">
				<span v-if="toText(childNode)">{{ toText(childNode) }}</span>
				<span v-else class="placeholder">请选择</span>
			</div>
		</div>
		<add-node v-model="childNode.childNode" />

		<!-- 抽屉 -->
		<xn-form-container
			v-model:visible="drawer"
			:destroy-on-close="true"
			:width="700"
			:body-style="{ 'padding-top': '0px' }"
		>
			<template #title>
				<div class="node-wrap-drawer__title">
					<label v-if="!isEditTitle" @click="editTitle"
						>{{ form.title }}<edit-outlined class="node-wrap-drawer-title-edit"
					/></label>
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
					<a-tab-pane key="1" tab="人员配置" force-render>
						<div v-show="!nodeLegal" style="margin-bottom: 10px">
							<a-alert message="请配置节点相关人员！" type="error" />
						</div>
						<a-form layout="vertical" :model="userTypeForm" ref="userTypeFormRef">
							<div class="mb-3">
								<span class="left-span-label">选择审批人员类型</span>
							</div>
							<a-radio-group v-model:value="userSelectionType" style="width: 100%">
								<a-row style="padding-bottom: 10px">
									<a-col :span="8">
										<a-radio value="ORG" @click="selectionClick('ORG')">机构</a-radio>
									</a-col>
									<a-col :span="8">
										<a-radio value="ROLE" @click="selectionClick('ROLE')">角色</a-radio>
									</a-col>
									<a-col :span="8">
										<a-radio value="POSITION" @click="selectionClick('POSITION')">职位</a-radio>
									</a-col>
								</a-row>
								<a-row style="padding-bottom: 10px">
									<a-col :span="8">
										<a-radio value="ORG_LEADER" @click="selectionClick('ORG_LEADER')">部门主管</a-radio>
									</a-col>
									<a-col :span="8">
										<a-radio value="SUPERVISOR" @click="selectionClick('SUPERVISOR')">上级主管</a-radio>
									</a-col>
									<a-col :span="8">
										<a-radio value="MUL_LEVEL_SUPERVISOR" @click="selectionClick('MUL_LEVEL_SUPERVISOR')"
											>连续多级主管</a-radio
										>
									</a-col>
								</a-row>
								<a-row style="padding-bottom: 10px">
									<a-col :span="8">
										<a-radio value="USER" @click="selectionClick('USER')">用户</a-radio>
									</a-col>
									<a-col :span="8">
										<a-radio value="FORM_USER" @click="selectionClick('FORM_USER')">表单内的人</a-radio>
									</a-col>
									<a-col :span="8">
										<a-radio value="FORM_USER_SUPERVISOR" @click="selectionClick('FORM_USER_SUPERVISOR')"
											>表单内的人上级主管</a-radio
										>
									</a-col>
								</a-row>
								<a-row style="padding-bottom: 10px">
									<a-col :span="8">
										<a-radio
											value="FORM_USER_MUL_LEVEL_SUPERVISOR"
											@click="selectionClick('FORM_USER_MUL_LEVEL_SUPERVISOR')"
											>表单内的人连续多级主管</a-radio
										>
									</a-col>
									<a-col :span="8">
										<a-radio value="INITIATOR" @click="selectionClick('INITIATOR')">发起人</a-radio>
									</a-col>
									<a-col :span="8" />
								</a-row>
							</a-radio-group>
							<a-form-item
								class="mt-2"
								v-if="
									recordData.formType === 'DEFINE' &&
									(userSelectionType === 'FORM_USER' ||
										userSelectionType === 'FORM_USER_SUPERVISOR' ||
										userSelectionType === 'FORM_USER_MUL_LEVEL_SUPERVISOR')
								"
								name="formUser"
								:rules="[{ required: true, message: '请输入人员变量' }]"
							>
								<template #label>
									<a-tooltip>
										<template #title>单个字段可以采用：字段名，多个采用：字段名1,字段名2,字段名3</template>
										<question-circle-outlined />
										人员变量：
									</a-tooltip>
								</template>
								<a-input
									style="width: 50%"
									v-model:value="userTypeForm.formUser"
									allow-clear
									@input="customFormUser"
									@keyup.enter="customFormUser"
									placeholder="请输入人员变量"
								/>
							</a-form-item>
							<div v-if="seleType" class="mt-2">
								<a-button type="primary" @click="openSelector" size="small">
									<template #icon>
										<search-outlined />
									</template>
									选择
								</a-button>
							</div>
							<div class="mt-2">
								<prop-tag
									v-if="form.properties.participateInfo.length > 0 && form.properties.participateInfo[0].value !== ''"
									:tag-list="form.properties.participateInfo[0]"
								/>
							</div>
							<a-form-item
								class="mt-2"
								v-if="
									userSelectionType === 'SUPERVISOR' ||
									userSelectionType === 'FORM_USER_SUPERVISOR' ||
									userSelectionType === 'MUL_LEVEL_SUPERVISOR' ||
									userSelectionType === 'FORM_USER_MUL_LEVEL_SUPERVISOR'
								"
								:label="
									userSelectionType === 'SUPERVISOR' || userSelectionType === 'FORM_USER_SUPERVISOR'
										? '上级主管级别'
										: '审批终点主管级别'
								"
								name="levelSupervisor"
								:rules="[{ required: true, message: '请选择主管级别' }]"
							>
								<a-select
									style="width: 50%"
									v-model:value="userTypeForm.levelSupervisor"
									placeholder="请选择主管级别"
									@change="levelSupervisorChange"
									:options="levelSupervisorList"
								/>
							</a-form-item>
						</a-form>
					</a-tab-pane>
					<a-tab-pane key="2" tab="审批配置" force-render>
						<span class="left-span-label">配置审批方式</span>
						<a-form label-position="top" layout="vertical" class="mt-2">
							<div v-show="!nodeLegal" style="margin-bottom: 10px">
								<a-alert message="请配置节点相关人员！" type="error" />
							</div>
							<a-form-item label="任务节点类型">
								<a-radio-group v-model:value="form.properties.configInfo.userTaskType">
									<a-radio
										v-for="userTaskType in userTaskTypeList"
										:key="userTaskType.value"
										:value="userTaskType.value"
										>{{ userTaskType.label }}</a-radio
									>
								</a-radio-group>
							</a-form-item>
							<a-form-item label="审批退回类型">
								<a-radio-group v-model:value="form.properties.configInfo.userTaskRejectType">
									<a-radio
										v-for="userTaskRejectType in userTaskRejectTypeList"
										:key="userTaskRejectType.value"
										:value="userTaskRejectType.value"
										>{{ userTaskRejectType.label }}
									</a-radio>
									<br />
								</a-radio-group>
							</a-form-item>
							<a-form-item label="多人审批类型">
								<a-radio-group v-model:value="form.properties.configInfo.userTaskMulApproveType">
									<a-radio
										v-for="userTaskMulApproveType in userTaskMulApproveTypeList"
										:key="userTaskMulApproveType.value"
										:value="userTaskMulApproveType.value"
										>{{ userTaskMulApproveType.label }}
									</a-radio>
									<br />
								</a-radio-group>
							</a-form-item>
							<a-form-item label="审批人员为空时">
								<a-radio-group v-model:value="form.properties.configInfo.userTaskEmptyApproveType">
									<a-radio
										v-for="userTaskEmptyApproveType in userTaskEmptyApproveTypeList"
										:key="userTaskEmptyApproveType.value"
										:value="userTaskEmptyApproveType.value"
										@change="userTaskEmptyApproveTypeChange"
										>{{ userTaskEmptyApproveType.label }}
									</a-radio>
								</a-radio-group>
							</a-form-item>
							<a-form-item
								v-if="form.properties.configInfo.userTaskEmptyApproveType === 'AUTO_TURN'"
								label="审批人为空时转交人"
							>
								<p><a-button type="primary" size="small" @click="seleApproveUser">选择人员</a-button></p>
								<a-tag
									v-if="form.properties.configInfo.userTaskEmptyApproveUserArray.length > 0"
									color="orange"
									closable
									@close="closeApproveUserTag"
								>
									{{ form.properties.configInfo.userTaskEmptyApproveUserArray[0].name }}</a-tag
								>
							</a-form-item>
						</a-form>
					</a-tab-pane>
					<a-tab-pane key="3" tab="按钮配置" force-render>
						<prop-button-info
							ref="propButtonInfoRef"
							:button-info="selectedButtonInfo"
							:show-button="defaultButtonkey"
							:no-checked="userTaskNoCheckedButtonkey"
						/>
					</a-tab-pane>
					<a-tab-pane key="4" tab="字段配置" force-render v-if="recordData.formType === 'DESIGN'">
						<prop-field-info
							ref="propFieldInfoRef"
							:field-info="fieldInfo"
							:default-field-model="defaultFieldModel"
							:form-field-list-value="formFieldListValue"
						/>
					</a-tab-pane>
					<a-tab-pane key="5" tab="表单配置" force-render v-else>
						<div class="mb-2">
							<span class="left-span-label">参与者可以使用的表单</span>
						</div>
						<a-form ref="userTaskFormRef" :model="formData" layout="vertical">
							<a-form-item
								label="节点表单"
								name="FORM_URL"
								:rules="[{ required: true, message: '请输入节点表单组件地址' }]"
							>
								<a-input
									v-model:value="formData.FORM_URL"
									addon-before="src/views/flw/customform/"
									addon-after=".vue"
									placeholder="请输入节点表单组件地址"
									allow-clear
								>
									<template #suffix>
										<a-button
											v-if="formData.FORM_URL"
											type="primary"
											size="small"
											@click="$refs.previewCustomFormRef.onOpen(formData.FORM_URL)"
										>
											预览
										</a-button>
									</template>
								</a-input>
							</a-form-item>
							<a-form-item
								label="移动端节点表单"
								name="MOBILE_FORM_URL"
								:rules="[{ required: true, message: '请输入移动端节点表单组件地址' }]"
							>
								<a-input
									v-model:value="formData.MOBILE_FORM_URL"
									addon-before="pages/flw/customform/"
									addon-after=".vue"
									placeholder="请输入移动端节点表单组件地址"
									allow-clear
								/>
							</a-form-item>
						</a-form>
					</a-tab-pane>
					<a-tab-pane key="6" tab="执行监听" force-render>
						<prop-listener-info
							ref="propExecutionListenerInfoRef"
							:listener-value="form.properties.executionListenerInfo"
							:default-listener-list="executionListenerInfo"
							:listener-value-array="executionListenerArray"
						/>
					</a-tab-pane>
					<a-tab-pane key="7" tab="节点监听" force-render>
						<prop-listener-info
							ref="propTaskListenerInfoRef"
							:listener-value="form.properties.taskListenerInfo"
							:default-listener-list="taskListenerInfo"
							:listener-value-array="taskListenerArray"
						/>
					</a-tab-pane>
				</a-tabs>
			</a-layout-content>
			<template #footer>
				<a-button type="primary" style="margin-right: 8px" @click="save">保存</a-button>
				<a-button @click="drawer = false">取消</a-button>
			</template>
		</xn-form-container>
		<role-selector-plus
			ref="roleSelectorPlusRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:role-page-api="selectorApiFunction.rolePageApi"
			:checked-role-list-api="selectorApiFunction.checkedRoleListApi"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<user-selector-plus
			ref="userSelectorPlusRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:user-page-api="selectorApiFunction.userPageApi"
			:checked-user-list-api="selectorApiFunction.checkedUserListApi"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<pos-selector-plus
			ref="posSelectorPlusRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:pos-page-api="selectorApiFunction.posPageApi"
			:checked-pos-list-api="selectorApiFunction.checkedPosListApi"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<org-selector-plus
			ref="orgSelectorPlusRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:org-page-api="selectorApiFunction.orgPageApi"
			:checked-org-list-api="selectorApiFunction.checkedOrgListApi"
			:data-is-converter-flw="true"
			@onBack="callBack"
		/>
		<form-user-selector ref="formUserSelectorRef" :form-field-list="formFieldListValue" @click="formUserClick" />
		<user-selector-plus
			ref="userSelectorApproveRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:user-page-api="selectorApiFunction.userPageApi"
			:checked-user-list-api="selectorApiFunction.checkedUserListApi"
			:radio-model="true"
			@onBack="callBackApprove"
		/>
		<preview-custom-form ref="previewCustomFormRef" />
	</div>
</template>

<script setup>
	import { cloneDeep, isEmpty } from 'lodash-es'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import tool from '@/utils/tool'
	import AddNode from './addNode.vue'
	import PropButtonInfo from './prop/propButtonInfo.vue'
	import PropFieldInfo from './prop/propFieldInfo.vue'
	import PropListenerInfo from './prop/propListenerInfo.vue'
	import FormUserSelector from './prop/formUserSelector.vue'
	import RoleSelectorPlus from '@/components/Selector/roleSelectorPlus.vue'
	import UserSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import PosSelectorPlus from '@/components/Selector/posSelectorPlus.vue'
	import OrgSelectorPlus from '@/components/Selector/orgSelectorPlus.vue'
	import PropTag from './prop/propTag.vue'
	import PreviewCustomForm from '@/components/XnWorkflow/nodes/common/previewCustomForm.vue'
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		formFieldListValue: { type: Array, default: () => [] },
		recordData: { type: Object, default: () => {} },
		processConfigInfo: { type: Object, default: () => {} },
		executionListenerArray: { type: Array, default: () => [] },
		taskListenerArray: { type: Array, default: () => [] },
		selectorApiFunction: { type: Object, default: () => {} }
	})
	const emit = defineEmits(['update:modelValue', 'deleteParalle'])
	const nodeTitleRef = ref()
	const propButtonInfoRef = ref()
	const propFieldInfoRef = ref()
	const propExecutionListenerInfoRef = ref()
	const propTaskListenerInfoRef = ref()
	const userTaskFormRef = ref()
	const roleSelectorPlusRef = ref()
	const userSelectorPlusRef = ref()
	const posSelectorPlusRef = ref()
	const orgSelectorPlusRef = ref()
	const formUserSelectorRef = ref()
	const previewCustomFormRef = ref()
	const userSelectorApproveRef = ref()

	const nodeLegal = ref(false)
	const childNode = ref({})
	const drawer = ref(false)
	const isEditTitle = ref(false)
	const form = ref({})
	const selectedButtonInfo = ref([])
	const executionListenerInfo = cloneDeep(config.listener.userTaskExecutionListenerInfo)
	const taskListenerInfo = cloneDeep(config.listener.userTaskTaskListenerInfo)
	const activeKey = ref('1')
	const defaultButtonkey = cloneDeep(config.button.userTaskDefaultButtonkey)
	const userTaskNoCheckedButtonkey = cloneDeep(config.button.userTaskNoCheckedButtonkey)
	const defaultFieldModel = cloneDeep(config.field.userTaskFieldModel)
	const fieldInfo = ref([])
	const seleType = ref(false)
	// 新配置
	// 用户选择类型
	const userSelectionType = ref('')
	const userSelectionTypeList = cloneDeep(config.userTaskConfig.userSelectionTypeList)
	// 主管级别
	const levelSupervisorList = cloneDeep(config.userTaskConfig.levelSupervisorList)
	// 任务节点类型
	const userTaskTypeList = cloneDeep(config.userTaskConfig.userTaskTypeList)
	// 审批退回类型
	const userTaskRejectTypeList = cloneDeep(config.userTaskConfig.userTaskRejectTypeList)
	// 多人审批时类型
	const userTaskMulApproveTypeList = cloneDeep(config.userTaskConfig.userTaskMulApproveTypeList)
	// 审批人为空时类型
	const userTaskEmptyApproveTypeList = cloneDeep(config.userTaskConfig.userTaskEmptyApproveTypeList)
	// 审批人为空时转交人
	const userTaskEmptyApproveUser = ref('')
	const formData = ref({})
	const userTypeForm = ref({
		levelSupervisor: '1'
	})
	watch(props, (newValue) => {
		if (props.modelValue) {
			childNode.value = newValue.modelValue
		}
	})
	onMounted(() => {
		childNode.value = props.modelValue
	})
	const show = () => {
		form.value = {}
		form.value = cloneDeep(childNode.value)
		selectedButtonInfo.value = form.value.properties.buttonInfo
		fieldInfo.value = form.value.properties.fieldInfo
		drawer.value = true
		// 设置默认选中的用户类型单选 userSelectionType
		if (form.value.properties.participateInfo.length > 0) {
			userSelectionType.value = form.value.properties.participateInfo[0].key
			// 如果包含表单内的人
			const formUserObj = form.value.properties.participateInfo.find((f) => f.key === 'FORM_USER')
			if (formUserObj) {
				userTypeForm.value.formUser = formUserObj.value
			}
			// 如果是直属主管或者连续多级审批的
			form.value.properties.participateInfo.forEach((item) => {
				// 如果只是普通的主管以及级别
				if (item.key === 'SUPERVISOR' || item.key === 'MUL_LEVEL_SUPERVISOR') {
					userTypeForm.value.levelSupervisor = item.value
				}
				// 如果是表单内的人的主管以及级别
				if (item.key === 'FORM_USER_SUPERVISOR' || item.key === 'FORM_USER_MUL_LEVEL_SUPERVISOR') {
					userTypeForm.value.levelSupervisor = item.value
					userTypeForm.value.formUser = JSON.parse(item.extJson).value
				}
			})
		} else {
			// 设置发起人
			userSelectionType.value = 'INITIATOR'
			form.value.properties.participateInfo = [{ key: 'INITIATOR', label: '发起人', value: '发起人:${INITIATOR}' }]
		}
		// 校验状态
		isNodeLegal()
		// 设置第一个选项卡打开选择器的按钮是否展示
		if (
			userSelectionType.value === 'USER' ||
			userSelectionType.value === 'ROLE' ||
			userSelectionType.value === 'ORG' ||
			userSelectionType.value === 'POSITION' ||
			userSelectionType.value === 'FORM_USER' ||
			userSelectionType.value === 'FORM_USER_SUPERVISOR' ||
			userSelectionType.value === 'FORM_USER_MUL_LEVEL_SUPERVISOR'
		) {
			seleType.value = true
		}
		// 初始化自定义表单字段值
		initFormInfo()
	}
	const initFormInfo = () => {
		const processConfigInfo = cloneDeep(props.processConfigInfo)
		if (!form.value.properties.formInfo.find((f) => f.key === 'FORM_URL')) {
			formData.value.FORM_URL = processConfigInfo.processUserTaskFormUrl
		} else {
			formData.value.FORM_URL = form.value.properties.formInfo.find((f) => f.key === 'FORM_URL').value
		}
		if (!form.value.properties.formInfo.find((f) => f.key === 'MOBILE_FORM_URL')) {
			formData.value.MOBILE_FORM_URL = processConfigInfo.processUserTaskMobileFormUrl
		} else {
			formData.value.MOBILE_FORM_URL = form.value.properties.formInfo.find((f) => f.key === 'MOBILE_FORM_URL').value
		}
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
		if (isEmpty(form.value.id)) {
			form.value.id = tool.snowyUuid()
		}
		form.value.properties.buttonInfo = propButtonInfoRef.value.selectedButtonKeyList()
		if (props.recordData.formType === 'DESIGN') {
			form.value.properties.fieldInfo = propFieldInfoRef.value.selectedFieldList()
		}
		if (isNodeLegal()) {
			form.value.properties.executionListenerInfo = propExecutionListenerInfoRef.value.selectedListenerList()
			form.value.properties.taskListenerInfo = propTaskListenerInfoRef.value.selectedListenerList()
			if (props.recordData.formType === 'DESIGN') {
				form.value.dataLegal = true
				emit('update:modelValue', form.value)
				drawer.value = false
			} else {
				userTaskFormRef.value
					.validate()
					.then((values) => {
						form.value.dataLegal = true
						form.value.properties.formInfo = cloneDeep(config.nodeModel.formInfo)
						form.value.properties.formInfo.find((f) => f.key === 'FORM_URL').value = values.FORM_URL
						form.value.properties.formInfo.find((f) => f.key === 'MOBILE_FORM_URL').value = values.MOBILE_FORM_URL
						emit('update:modelValue', form.value)
						drawer.value = false
					})
					.catch((err) => {})
			}
		}
	}
	const selectionClick = (value) => {
		const type = value
		const result = []
		form.value.properties.participateInfo = []
		const obj = {}
		obj.key = type
		obj.label = ''
		obj.value = ''
		if (type === 'ORG_LEADER' || type === 'INITIATOR') {
			// 部门主管 发起人
			seleType.value = false
			obj.label = userSelectionTypeList.filter((item) => item.value === type)[0].label
			obj.value = userSelectionTypeList.filter((item) => item.value === type)[0].label + ':${' + type + '}'
		} else if (type === 'SUPERVISOR' || type === 'MUL_LEVEL_SUPERVISOR') {
			seleType.value = false
			// 直属主管 连续多级审批 默认选中直接主管
			obj.label = userSelectionTypeList.filter((item) => item.value === type)[0].label
			obj.value = userTypeForm.value.levelSupervisor
		} else if (type === 'FORM_USER') {
			seleType.value = props.recordData.formType === 'DESIGN'
		} else if (type === 'FORM_USER_SUPERVISOR' || type === 'FORM_USER_MUL_LEVEL_SUPERVISOR') {
			seleType.value = props.recordData.formType === 'DESIGN'
		} else {
			seleType.value = true
		}
		result.push(obj)
		form.value.properties.participateInfo = result
		// 清空自定义表单内的人员输入框
		userTypeForm.value.formUser = ''
		isNodeLegal()
	}
	// 打开各种选择器
	const openSelector = () => {
		let type = userSelectionType.value
		let data = form.value.properties.participateInfo
		if (type === 'ROLE') {
			roleSelectorPlusRef.value.showRolePlusModal(data)
		}
		if (type === 'USER') {
			userSelectorPlusRef.value.showUserPlusModal(data)
		}
		if (type === 'POSITION') {
			posSelectorPlusRef.value.showPosPlusModal(data)
		}
		if (type === 'ORG') {
			orgSelectorPlusRef.value.showOrgPlusModal(data)
		}
		if (type === 'FORM_USER' || type === 'FORM_USER_SUPERVISOR' || type === 'FORM_USER_MUL_LEVEL_SUPERVISOR') {
			formUserSelectorRef.value.showFormUserModal(data[0])
		}
	}
	const delNode = () => {
		emit('update:modelValue', childNode.value.childNode)
		emit('deleteParalle')
	}
	const delUser = (index) => {
		form.value.nodeUserList.splice(index, 1)
	}
	// 选择转交人
	const seleApproveUser = () => {
		const data = [form.value.properties.configInfo.userTaskEmptyApproveUser]
		userSelectorApproveRef.value.showUserPlusModal(data)
	}
	// 选择转交人回调
	const callBackApprove = (value) => {
		form.value.properties.configInfo.userTaskEmptyApproveUser = value[0].id
		form.value.properties.configInfo.userTaskEmptyApproveUserArray = value
	}
	// 清除转交人
	const closeApproveUserTag = () => {
		form.value.properties.configInfo.userTaskEmptyApproveUser = ''
		form.value.properties.configInfo.userTaskEmptyApproveUserArray = []
	}
	// 点击自动转交给某人单选时
	const userTaskEmptyApproveTypeChange = (value) => {
		const type = value.target.value
		if (type === 'AUTO_TURN') {
			// 赋值默认的管理员
			form.value.properties.configInfo.userTaskEmptyApproveUser = JSON.parse(props.recordData.extJson)[0].id
			form.value.properties.configInfo.userTaskEmptyApproveUserArray = JSON.parse(props.recordData.extJson)
		} else {
			// 置为空
			closeApproveUserTag()
		}
	}
	// 校验节点是否合法
	const isNodeLegal = () => {
		if (form.value.properties.participateInfo.length > 0) {
			if (isEmpty(form.value.properties.participateInfo[0].label)) {
				nodeLegal.value = false
				return false
			} else {
				// 再看看默认转交人是否OK
				// eslint-disable-next-line no-lonely-if
				if (form.value.properties.configInfo.userTaskEmptyApproveType === 'AUTO_TURN') {
					if (!isEmpty(form.value.properties.configInfo.userTaskEmptyApproveUser)) {
						nodeLegal.value = true
						return true
					} else {
						nodeLegal.value = false
						return false
					}
				} else {
					nodeLegal.value = true
					return true
				}
			}
		} else {
			nodeLegal.value = false
			return false
		}
	}
	// 公共回调方法，因为它们返回的数据结构一致
	const callBack = (value) => {
		if (isEmpty(value.label)) {
			nodeLegal.value = false
		} else {
			form.value.properties.participateInfo[0] = value
		}
		isNodeLegal()
	}
	// 表单内的人选择器回调
	const formUserClick = (value) => {
		if (value) {
			const participateInfo = form.value.properties.participateInfo[0].key
			const result = []
			const obj = {}
			if (participateInfo === 'FORM_USER_SUPERVISOR' || participateInfo === 'FORM_USER_MUL_LEVEL_SUPERVISOR') {
				obj.key = participateInfo
				obj.label = userSelectionTypeList.filter((item) => item.value === participateInfo)[0].label
				obj.value = userTypeForm.value.levelSupervisor
				const extJson = {
					key: 'FORM_USER',
					label: '表单内的人',
					value: value.model
				}
				obj.extJson = JSON.stringify(extJson)
			} else {
				obj.key = 'FORM_USER'
				obj.label = userSelectionTypeList.filter((item) => item.value === 'FORM_USER')[0].label
				obj.value = value.model
			}
			result.push(obj)
			form.value.properties.participateInfo = result
		} else {
			nodeLegal.value = false
		}
		isNodeLegal()
	}
	// 监听自定义表单人员输入
	const customFormUser = () => {
		if (userTypeForm.value.formUser) {
			const participateInfo = form.value.properties.participateInfo[0].key
			const result = []
			const obj = {}
			if (participateInfo === 'FORM_USER_SUPERVISOR' || participateInfo === 'FORM_USER_MUL_LEVEL_SUPERVISOR') {
				obj.key = participateInfo
				obj.label = userSelectionTypeList.filter((item) => item.value === participateInfo)[0].label
				obj.value = userTypeForm.value.levelSupervisor
				const extJson = {
					key: 'FORM_USER',
					label: '表单内的人',
					value: userTypeForm.value.formUser
				}
				obj.extJson = JSON.stringify(extJson)
			} else {
				obj.key = 'FORM_USER'
				obj.label = userSelectionTypeList.filter((item) => item.value === 'FORM_USER')[0].label
				obj.value = userTypeForm.value.formUser
			}
			result.push(obj)
			form.value.properties.participateInfo = result
			nodeLegal.value = false
		} else {
			form.value.properties.participateInfo = []
			nodeLegal.value = true
		}
		isNodeLegal()
	}
	// 选择主管层级
	const levelSupervisorChange = (value) => {
		form.value.properties.participateInfo[0].value = value
	}
	const toText = (childNode) => {
		if (!isEmpty(childNode)) {
			const strArray = toTag(childNode.properties.participateInfo[0])
			if (strArray.length > 0) {
				let value = ''
				for (let i = 0; i < strArray.length; i++) {
					if (strArray.length === i + 1) {
						value = value + strArray[i]
					} else {
						value = value + strArray[i] + '，'
					}
				}
				return value
			} else {
				return false
			}
		} else {
			return false
		}
	}
	const toTag = (participateInfo) => {
		if (participateInfo === undefined) {
			return []
		}
		if (isEmpty(participateInfo.label)) {
			return []
		} else {
			let resultArray = []
			if (participateInfo.label.indexOf(',') !== -1) {
				resultArray = participateInfo.label.split(',')
			} else {
				resultArray.push(participateInfo.label)
			}
			return resultArray
		}
	}
</script>
