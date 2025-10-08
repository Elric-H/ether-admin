<template>
	<div class="node-wrap">
		<div class="node-wrap-box" @click="show">
			<div class="title" style="background: #3296fa">
				<send-outlined class="icon" />
				<span>{{ childNode.title }}</span>
				<close-outlined class="close" @click.stop="delNode()" />
			</div>
			<div class="content">
				<span v-if="toText(childNode)">{{ toText(childNode) }}</span>
				<span v-else class="placeholder">请选择人员</span>
			</div>
		</div>
		<add-node v-model="childNode.childNode"></add-node>
		<xn-form-container
			v-model:visible="drawer"
			:destroy-on-close="true"
			:width="700"
			:body-style="{ 'padding-top': '0px' }"
		>
			<template #title>
				<div class="node-wrap-drawer__title">
					<label v-if="!isEditTitle" @click="editTitle"
						>{{ form.title }}<edit-outlined class="node-wrap-drawer-title-edit" />
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
					<a-tab-pane key="1" tab="抄送配置" force-render>
						<a-form layout="vertical" :model="userTypeForm">
							<div v-show="!nodeLegal" style="margin-bottom: 10px">
								<a-alert message="请选择抄送人员！" type="error" />
							</div>
							<div class="mb-2">
								<span class="left-span-label">选择要抄送的人员</span>
							</div>
							<a-radio-group v-model:value="userSelectionType" style="width: 100%">
								<a-row style="padding-bottom: 10px">
									<a-col :span="8" v-for="userSelectionType in userSelectionTypeList" :key="userSelectionType.value">
										<a-radio :value="userSelectionType.value" @click="selectionClick(userSelectionType.value)">
											{{ userSelectionType.label }}
										</a-radio>
									</a-col>
								</a-row>
							</a-radio-group>
							<a-form-item v-if="userSelectionType === 'USER'">
								<a-button class="mt-2" type="primary" size="small" round @click="openSelector">
									<template #icon>
										<search-outlined />
									</template>
									选择
								</a-button>
								<p />
								<prop-tag :tag-list="getTagList('USER')" />
							</a-form-item>
							<a-form-item v-if="recordData.formType === 'DESIGN' && userSelectionType === 'FORM_USER'">
								<div class="mt-2">
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
							</a-form-item>
							<a-form-item
								class="mt-2"
								v-if="recordData.formType === 'DEFINE' && userSelectionType === 'FORM_USER'"
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
						</a-form>
					</a-tab-pane>
					<a-tab-pane key="2" tab="执行监听" force-render>
						<prop-listener-info
							ref="propExecutionListenerInfoRef"
							:listener-value="form.properties.executionListenerInfo"
							:default-listener-list="executionListenerInfo"
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
		<form-user-selector ref="formUserSelectorRef" :form-field-list="formFieldListValue" @click="userCallBack" />
		<user-selector-plus
			ref="userSelectorPlusRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:user-page-api="selectorApiFunction.userPageApi"
			:checked-user-list-api="selectorApiFunction.checkedUserListApi"
			:data-is-converter-flw="true"
			@onBack="userCallBack"
		/>
	</div>
</template>

<script setup>
	import { cloneDeep, isEmpty } from 'lodash-es'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import AddNode from './addNode.vue'
	import UserSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import FormUserSelector from './prop/formUserSelector.vue'
	import PropTag from './prop/propTag.vue'
	import PropListenerInfo from './prop/propListenerInfo.vue'
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		recordData: { type: Object, default: () => {} },
		executionListenerArray: { type: Array, default: () => [] },
		selectorApiFunction: { type: Object, default: () => {} },
		formFieldListValue: { type: Array, default: () => [] }
	})
	const emit = defineEmits(['update:modelValue'])
	const nodeTitleRef = ref()
	const propExecutionListenerInfoRef = ref()
	const userSelectorPlusRef = ref()
	const formUserSelectorRef = ref()
	const childNode = ref({})
	const drawer = ref(false)
	const isEditTitle = ref(false)
	const activeKey = ref('1')
	const form = ref({})
	const nodeLegal = ref(false)
	const userTypeForm = ref({})
	const executionListenerInfo = cloneDeep(config.listener.serviceTaskExecutionListenerInfo)
	// 用户选择类型
	const userSelectionType = ref('')
	const userSelectionTypeList = cloneDeep(config.serviceTaskConfig.userSelectionTypeList)
	watch(props, (newValue) => {
		if (newValue.modelValue) {
			childNode.value = newValue.modelValue
		}
	})
	onMounted(() => {
		// eslint-disable-next-line vue/no-mutating-props
		childNode.value = props.modelValue
	})
	const show = () => {
		form.value = {}
		form.value = cloneDeep(childNode.value)
		drawer.value = true
		if (!isEmpty(form.value.properties.participateInfo)) {
			userSelectionType.value = form.value.properties.participateInfo[0].key
			// 如果包含表单内的人
			const formUserObj = form.value.properties.participateInfo.find((f) => f.key === 'FORM_USER')
			if (formUserObj) {
				userTypeForm.value.formUser = formUserObj.value
			}
		} else {
			userSelectionType.value = 'USER'
			userTypeForm.value = {}
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
		if (nodeLegal.value) {
			form.value.properties.executionListenerInfo = propExecutionListenerInfoRef.value.selectedListenerList()
			form.value.dataLegal = true
			emit('update:modelValue', form.value)
			drawer.value = false
		}
	}
	const delNode = () => {
		emit('update:modelValue', childNode.value.childNode)
	}
	// 选择器回调
	const userCallBack = (value) => {
		if (isEmpty(value.label)) {
			nodeLegal.value = false
		} else {
			nodeLegal.value = true
		}
		if (userSelectionType.value === 'USER') {
			form.value.properties.participateInfo[0] = value
		} else if (userSelectionType.value === 'FORM_USER') {
			form.value.properties.participateInfo = [
				{
					key: 'FORM_USER',
					label: userSelectionTypeList.value.filter((item) => item.value === 'FORM_USER')[0].label,
					value: value.model
				}
			]
		}
	}
	// 获取tag标签的内容
	const getTagList = (type) => {
		const participateInfo = form.value.properties.participateInfo
		if (participateInfo.length > 0) {
			const obj = participateInfo.find((item) => item.key === type)
			if (isEmpty(obj.label)) {
				return undefined
			} else {
				return obj
			}
		} else {
			return undefined
		}
	}
	// 选择抄送人员类型
	const selectionClick = (value) => {
		form.value.properties.participateInfo = []
		userSelectionType.value = value
		nodeLegal.value = false
	}
	// 打开选择器
	const openSelector = () => {
		let data = form.value.properties.participateInfo
		if (userSelectionType.value === 'USER') {
			userSelectorPlusRef.value.showUserPlusModal(data)
		} else if (userSelectionType.value === 'FORM_USER') {
			formUserSelectorRef.value.showFormUserModal(data[0])
		}
	}
	// 监听自定义表单人员输入
	const customFormUser = () => {
		if (userTypeForm.value.formUser) {
			form.value.properties.participateInfo = [
				{
					key: 'FORM_USER',
					label: '表单内的人',
					value: userTypeForm.value.formUser
				}
			]
			nodeLegal.value = true
		} else {
			nodeLegal.value = false
		}
	}
	const toText = (childNode) => {
		if (!isEmpty(childNode)) {
			const participateInfo = childNode.properties.participateInfo
			if (participateInfo.length > 0) {
				let resultArray = []
				if (participateInfo[0].label.indexOf(',') !== -1) {
					resultArray = participateInfo[0].label.split(',')
				} else {
					resultArray.push(participateInfo[0].label)
				}
				return resultArray.toString()
			} else {
				// return '未选择抄送人';
				return false
			}
		} else {
			return false
		}
	}
</script>
