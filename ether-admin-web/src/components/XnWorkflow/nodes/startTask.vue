<template>
	<div class="node-wrap">
		<div class="node-wrap-box start-node" @click="show">
			<div class="title" style="background: #576a95">
				<user-outlined class="icon" />
				<span>{{ childNode.title }}</span>
			</div>
			<div class="content">
				<span v-if="toText(childNode)">{{ toText(childNode) }}</span>
				<span v-else class="placeholder">请配置字段与按钮</span>
			</div>
		</div>
		<add-node v-model="childNode.childNode" />
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
					<a-tab-pane key="1" tab="按钮配置" force-render>
						<prop-button-info
							ref="propButtonInfoRef"
							:button-info="selectedButtonInfo"
							:show-button="startTaskDefaultButtonkey"
							:no-checked="startTaskNoCheckedButtonkey"
						/>
					</a-tab-pane>
					<a-tab-pane key="2" tab="字段配置" force-render v-if="recordData.formType === 'DESIGN'">
						<prop-field-info
							ref="propFieldInfoRef"
							:field-info="fieldInfo"
							:form-field-list-value="formFieldListValue"
						/>
					</a-tab-pane>
					<a-tab-pane key="3" tab="表单配置" force-render v-else>
						<div class="mb-2">
							<span class="left-span-label">参与者可以填写的表单</span>
						</div>
						<a-form ref="startTaskFormRef" :model="formData" layout="vertical">
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
					<a-tab-pane key="4" tab="执行监听" force-render>
						<prop-listener-info
							ref="propExecutionListenerInfoRef"
							:listener-value="form.properties.executionListenerInfo"
							:default-listener-list="executionListenerInfo"
							:listener-value-array="executionListenerArray"
						/>
					</a-tab-pane>
					<a-tab-pane key="5" tab="节点监听" force-render>
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
		<preview-custom-form ref="previewCustomFormRef" />
	</div>
</template>

<script setup>
	import { cloneDeep } from 'lodash-es'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import tool from '@/utils/tool'
	import AddNode from './addNode.vue'
	import PropButtonInfo from './prop/propButtonInfo.vue'
	import PropFieldInfo from './prop/propFieldInfo.vue'
	import PropListenerInfo from './prop/propListenerInfo.vue'
	import PreviewCustomForm from '@/components/XnWorkflow/nodes/common/previewCustomForm.vue'
	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		formFieldListValue: { type: Array, default: () => [] },
		recordData: { type: Object, default: () => {} },
		processConfigInfo: { type: Object, default: () => {} },
		executionListenerArray: { type: Array, default: () => [] },
		taskListenerArray: { type: Array, default: () => [] }
	})
	const emit = defineEmits(['update:modelValue'])
	const nodeTitleRef = ref()
	const propButtonInfoRef = ref()
	const propExecutionListenerInfoRef = ref()
	const propTaskListenerInfoRef = ref()
	const propFieldInfoRef = ref()
	const startTaskFormRef = ref()
	const childNode = ref({})
	const drawer = ref(false)
	const isEditTitle = ref(false)
	const form = ref({})
	const activeKey = ref('1')
	const selectedButtonInfo = ref([])
	const executionListenerInfo = cloneDeep(config.listener.startTaskExecutionListenerInfo)
	const taskListenerInfo = cloneDeep(config.listener.userTaskTaskListenerInfo)
	const startTaskDefaultButtonkey = cloneDeep(config.button.startTaskDefaultButtonkey)
	const startTaskNoCheckedButtonkey = cloneDeep(config.button.startTaskNoCheckedButtonkey)
	const fieldInfo = ref([])
	const formData = ref({})
	watch(props, (newValue) => {
		if (newValue.modelValue) {
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
		selectedButtonInfo.value = form.value.properties.buttonInfo
		fieldInfo.value = form.value.properties.fieldInfo
		// 初始化自定义表单字段值
		initFormInfo()
	}
	const initFormInfo = () => {
		const processConfigInfo = cloneDeep(props.processConfigInfo)
		if (!form.value.properties.formInfo.find((f) => f.key === 'FORM_URL')) {
			formData.value.FORM_URL = processConfigInfo.processStartTaskFormUrl
		} else {
			formData.value.FORM_URL = form.value.properties.formInfo.find((f) => f.key === 'FORM_URL').value
		}
		if (!form.value.properties.formInfo.find((f) => f.key === 'MOBILE_FORM_URL')) {
			formData.value.MOBILE_FORM_URL = processConfigInfo.processStartTaskMobileFormUrl
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
		form.value.id = tool.snowyUuid()
		form.value.properties.buttonInfo = propButtonInfoRef.value.selectedButtonKeyList()
		form.value.properties.executionListenerInfo = propExecutionListenerInfoRef.value.selectedListenerList()
		form.value.properties.taskListenerInfo = propTaskListenerInfoRef.value.selectedListenerList()
		if (props.recordData.formType === 'DESIGN') {
			form.value.properties.fieldInfo = propFieldInfoRef.value.selectedFieldList()
			form.value.dataLegal = true
			emit('update:modelValue', form.value)
			drawer.value = false
		} else {
			startTaskFormRef.value
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
	const toText = (childNode) => {
		if (childNode.dataLegal) {
			return '系统自动配置参与人'
		} else {
			return false
		}
	}
</script>
<style scoped>
	.font-span {
		padding-bottom: 8px;
	}
</style>
