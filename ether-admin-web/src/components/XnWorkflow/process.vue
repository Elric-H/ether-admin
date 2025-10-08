<template>
	<xn-form-container
		v-model:visible="drawer"
		:destroy-on-close="true"
		:title="modelTitle"
		:width="700"
		:bodyStyle="{ 'padding-top': '0px' }"
	>
		<a-form ref="noticeFormRef" :model="formData" layout="vertical">
			<a-tabs v-model:activeKey="activeKey">
				<a-tab-pane key="1" tab="人员配置" force-render>
					<div v-show="formVerify" style="margin-bottom: 10px">
						<a-alert message="请切换标签查看，填写完必填项！" type="error" />
					</div>
					<div v-show="alertShow" style="margin-bottom: 10px">
						<a-alert message="未选择任何类型的人员配置，默认所有人均可参与此流程。" type="warning" />
					</div>
					<div class="mb-2">
						<span class="left-span-label">配置使用该流程的人员</span>
					</div>
					<a-button type="primary" round @click="selectionParticipants('ORG')" size="small">
						<plus-outlined />
						选择机构
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('ORG')" />
					<a-divider />
					<a-button type="primary" round @click="selectionParticipants('ROLE')" size="small">
						<plus-outlined />
						选择角色
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('ROLE')" />
					<a-divider />
					<a-button type="primary" round @click="selectionParticipants('POSITION')" size="small">
						<plus-outlined />
						选择职位
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('POSITION')" />
					<a-divider />
					<a-button type="primary" round @click="selectionParticipants('USER')" size="small">
						<plus-outlined />
						选择用户
					</a-button>
					<p />
					<prop-tag :tagList="getTagList('USER')" />
					<a-divider />
				</a-tab-pane>
				<a-tab-pane key="2" tab="基础配置" force-render>
					<div class="mb-2">
						<span class="left-span-label">流程基础全局配置</span>
					</div>
					<a-row :gutter="[10, 0]">
						<a-col :span="12">
							<a-form-item
								label="流水号"
								name="processSnTemplateId"
								:rules="[{ required: true, message: '请选择流水号' }]"
							>
								<a-select
									v-model:value="form.properties.configInfo.processSnTemplateId"
									placeholder="请选择流水号"
									:options="snTemplateArray"
								>
								</a-select>
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item
								v-if="recordData.formType === 'DESIGN'"
								label="打印模板"
								name="processPrintTemplateId"
								:rules="[{ required: true, message: '请选择打印模板' }]"
							>
								<a-select
									v-model:value="form.properties.configInfo.processPrintTemplateId"
									placeholder="请选择打印模板"
									:options="printTemplateArray"
								/>
							</a-form-item>
							<span v-else>
								<p>打印模板</p>
								<p>自定义表单内提供打印方法</p>
							</span>
						</a-col>
					</a-row>
					<a-form-item
						label="标题模板"
						name="processTitleTemplate"
						:rules="[{ required: true, message: '请创造标题模板' }]"
					>
						<template-generator
							ref="processTitleGenerator"
							v-model:inputValue="form.properties.configInfo.processTitleTemplate"
							:fieldInfoList="fieldInfoList"
						/>
					</a-form-item>
					<a-form-item
						label="摘要模板"
						name="processAbstractTemplate"
						:rules="[{ required: true, message: '请创造摘要模板' }]"
					>
						<template-generator
							ref="processAbstractGenerator"
							v-model:inputValue="form.properties.configInfo.processAbstractTemplate"
							:fieldInfoList="fieldInfoList"
						/>
					</a-form-item>

					<a-form-item label="开启自动去重" name="processEnableAutoDistinct">
						<a-switch v-model:checked="form.properties.configInfo.processEnableAutoDistinct" />
					</a-form-item>
					<a-form-item v-show="form.properties.configInfo.processEnableAutoDistinct" name="processAutoDistinctType">
						<a-radio-group v-model:value="form.properties.configInfo.processAutoDistinctType">
							<a-radio
								v-for="autoDistinctType in processAutoDistinctTypeList"
								:key="autoDistinctType.value"
								:value="autoDistinctType.value"
								>{{ autoDistinctType.label }}</a-radio
							>
						</a-radio-group>
					</a-form-item>
					<a-row :gutter="[10, 0]">
						<a-col :span="12">
							<a-form-item label="开启审批撤销" name="processEnableRevoke">
								<a-switch v-model:checked="form.properties.configInfo.processEnableRevoke" />
							</a-form-item>
						</a-col>
						<a-col :span="12">
							<a-form-item label="开启意见必填" name="processEnableCommentRequired">
								<a-switch v-model:checked="form.properties.configInfo.processEnableCommentRequired" />
							</a-form-item>
						</a-col>
					</a-row>
				</a-tab-pane>
				<a-tab-pane key="3" tab="通知配置" force-render>
					<div class="mb-2">
						<span class="left-span-label">配置通知事项</span>
					</div>
					<a-form-item label="开启退回通知" name="processEnableBackNotice">
						<a-switch v-model:checked="formData.processEnableBackNotice" />
					</a-form-item>
					<a-form-item
						label="退回通知方式"
						v-show="formData.processEnableBackNotice"
						name="processBackNoticeChannel"
						:rules="[{ required: formData.processEnableBackNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group v-model:value="formData.processBackNoticeChannel" :options="noticeInfoList" />
					</a-form-item>
					<a-form-item
						label="退回通知模板"
						v-show="formData.processEnableBackNotice"
						name="processBackNoticeTemplate"
						:rules="[{ required: formData.processEnableBackNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="enableBackNoticeRef"
							v-model:inputValue="formData.processBackNoticeTemplate"
							:fieldInfoList="fieldInfoList"
						/>
					</a-form-item>

					<a-form-item label="开启待办通知" name="processEnableTodoNotice">
						<a-switch v-model:checked="form.properties.configInfo.processEnableTodoNotice" />
					</a-form-item>
					<a-form-item
						label="待办通知方式"
						v-show="form.properties.configInfo.processEnableTodoNotice"
						name="processTodoNoticeChannel"
						:rules="[{ required: formData.processEnableTodoNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group
							v-model:value="form.properties.configInfo.processTodoNoticeChannel"
							:options="noticeInfoList"
						/>
					</a-form-item>
					<a-form-item
						label="待办通知模板"
						v-show="form.properties.configInfo.processEnableTodoNotice"
						name="processTodoNoticeTemplate"
						:rules="[{ required: formData.processEnableTodoNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="todoNoticeChannelRef"
							:fieldInfoList="fieldInfoList"
							v-model:inputValue="form.properties.configInfo.processTodoNoticeTemplate"
						/>
					</a-form-item>

					<a-form-item label="开启抄送通知" name="processEnableCopyNotice">
						<a-switch v-model:checked="form.properties.configInfo.processEnableCopyNotice" />
					</a-form-item>
					<a-form-item
						label="抄送通知方式"
						v-show="form.properties.configInfo.processEnableCopyNotice"
						name="processCopyNoticeChannel"
						:rules="[{ required: formData.processEnableCopyNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group
							v-model:value="form.properties.configInfo.processCopyNoticeChannel"
							:options="noticeInfoList"
						/>
					</a-form-item>
					<a-form-item
						label="抄送通知模板"
						v-show="form.properties.configInfo.processEnableCopyNotice"
						name="processCopyNoticeTemplate"
						:rules="[{ required: formData.processEnableCopyNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="enableCopyNoticeRef"
							:fieldInfoList="fieldInfoList"
							v-model:inputValue="form.properties.configInfo.processCopyNoticeTemplate"
						/>
					</a-form-item>

					<a-form-item label="开启完成通知" name="processEnableCompleteNotice">
						<a-switch v-model:checked="form.properties.configInfo.processEnableCompleteNotice" />
					</a-form-item>
					<a-form-item
						label="完成通知方式"
						v-show="form.properties.configInfo.processEnableCompleteNotice"
						name="processCompleteNoticeChannel"
						:rules="[{ required: formData.processEnableCompleteNotice, message: '请选择通知方式' }]"
					>
						<a-checkbox-group
							v-model:value="form.properties.configInfo.processCompleteNoticeChannel"
							:options="noticeInfoList"
						/>
					</a-form-item>
					<a-form-item
						label="完成通知模板"
						v-show="form.properties.configInfo.processEnableCompleteNotice"
						name="processCompleteNoticeTemplate"
						:rules="[{ required: formData.processEnableCompleteNotice, message: '请创造通知模板' }]"
					>
						<template-generator
							ref="enableCompleteNoticeRef"
							:fieldInfoList="fieldInfoList"
							v-model:inputValue="form.properties.configInfo.processCompleteNoticeTemplate"
						/>
					</a-form-item>
				</a-tab-pane>
				<a-tab-pane key="4" tab="表单预设" force-render v-if="recordData.formType !== 'DESIGN'">
					<div class="mb-2">
						<span class="left-span-label">预设全局需要的表单</span>
					</div>
					<a-form-item
						label="开始节点表单"
						name="processStartTaskFormUrl"
						:rules="[{ required: true, message: '请输入开始节点表单' }]"
					>
						<a-input
							v-model:value="form.properties.configInfo.processStartTaskFormUrl"
							addon-before="src/views/flw/customform/"
							addon-after=".vue"
							placeholder="请输入开始节点表单组件"
							allow-clear
						>
							<template #suffix>
								<a-button
									v-if="form.properties.configInfo.processStartTaskFormUrl"
									type="primary"
									size="small"
									@click="$refs.previewCustomFormRef.onOpen(form.properties.configInfo.processStartTaskFormUrl)"
								>
									预览
								</a-button>
							</template>
						</a-input>
					</a-form-item>
					<a-form-item
						label="移动端开始节点表单"
						name="processStartTaskMobileFormUrl"
						:rules="[{ required: true, message: '请输入移动端开始节点表单' }]"
					>
						<a-input
							v-model:value="form.properties.configInfo.processStartTaskMobileFormUrl"
							addon-before="pages/flw/customform/"
							addon-after=".vue"
							placeholder="请输入移动端开始节点表单组件"
							allow-clear
						/>
					</a-form-item>
					<a-form-item
						label="人员节点表单"
						name="processUserTaskFormUrl"
						:rules="[{ required: true, message: '请输入人员节点表单' }]"
					>
						<a-input
							v-model:value="form.properties.configInfo.processUserTaskFormUrl"
							addon-before="src/views/flw/customform/"
							addon-after=".vue"
							placeholder="请输入人员节点表单组件"
							allow-clear
						>
							<template #suffix>
								<a-button
									v-if="form.properties.configInfo.processUserTaskFormUrl"
									type="primary"
									size="small"
									@click="$refs.previewCustomFormRef.onOpen(form.properties.configInfo.processUserTaskFormUrl)"
								>
									预览
								</a-button>
							</template>
						</a-input>
					</a-form-item>
					<a-form-item
						label="移动端人员节点表单"
						name="processUserTaskMobileFormUrl"
						:rules="[{ required: true, message: '请输入移动端人员节点表单' }]"
					>
						<a-input
							v-model:value="form.properties.configInfo.processUserTaskMobileFormUrl"
							addon-before="pages/flw/customform/"
							addon-after=".vue"
							placeholder="请输入移动端人员节点表单组件"
							allow-clear
						/>
					</a-form-item>
				</a-tab-pane>
				<a-tab-pane key="5" tab="执行监听" force-render>
					<prop-listener-info
						ref="propListenerInfoRef"
						:listenerValue="form.properties.executionListenerInfo"
						:defaultListenerList="executionListenerInfo"
						:listener-value-array="executionListenerArray"
						listener-type="global"
						:execution-listener-selector-for-custom-event-array="executionListenerSelectorForCustomEventArray"
					/>
				</a-tab-pane>
			</a-tabs>
		</a-form>

		<template #footer>
			<a-button type="primary" style="margin-right: 8px" @click="onFinish">保存</a-button>
			<a-button @click="drawer = false">取消</a-button>
		</template>

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
		<preview-custom-form ref="previewCustomFormRef" />
	</xn-form-container>
</template>

<script setup>
	import { isEmpty, cloneDeep } from 'lodash-es'
	import tool from '@/utils/tool'
	import config from '@/components/XnWorkflow/nodes/config/config'
	import TemplateGenerator from './nodes/prop/templateGenerator.vue'
	import RoleSelectorPlus from '@/components/Selector/roleSelectorPlus.vue'
	import UserSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	import PosSelectorPlus from '@/components/Selector/posSelectorPlus.vue'
	import OrgSelectorPlus from '@/components/Selector/orgSelectorPlus.vue'
	import PropTag from './nodes/prop/propTag.vue'
	import PropListenerInfo from '@/components/XnWorkflow/nodes/prop/propListenerInfo.vue'
	import PreviewCustomForm from '@/components/XnWorkflow/nodes/common/previewCustomForm.vue'

	const props = defineProps({
		modelValue: { type: Object, default: () => {} },
		formFieldListValue: { type: Array, default: () => [] },
		recordData: { type: Object, default: () => {} },
		snTemplateArray: { type: Array, default: () => [] },
		printTemplateArray: { type: Array, default: () => [] },
		executionListenerArray: { type: Array, default: () => [] },
		executionListenerSelectorForCustomEventArray: { type: Array, default: () => [] },
		taskListenerArray: { type: Array, default: () => [] },
		selectorApiFunction: { type: Object, default: () => {} }
	})
	const emit = defineEmits(['update:modelValue'])
	const noticeInfoList = cloneDeep(config.noticeInfoList)
	// 摘要模板，因为要从传来的字段中取
	const abstractStr = ref()
	const executionListenerInfo = cloneDeep(config.listener.processExecutionListenerInfo)
	// 自动去重类型
	const processAutoDistinctTypeList = [
		{
			label: '当审批人和发起人是同一个人，审批自动通过',
			value: 'SAMPLE'
		},
		{
			label: '当同一审批人在流程中连续多次出现时，自动去重',
			value: 'MULTIPLE'
		}
	]
	const drawer = ref(false)
	const modelTitle = ref('全局属性')
	const activeKey = ref('1')
	const form = ref({})
	const formData = ref({})
	const fieldInfoList = ref([])
	const formVerify = ref(false)
	const noticeFormRef = ref()
	const propListenerInfoRef = ref()
	const orgSelectorPlusRef = ref()
	const roleSelectorPlusRef = ref()
	const userSelectorPlusRef = ref()
	const posSelectorPlusRef = ref()

	const alertShow = computed(() => {
		return form.value.properties.participateInfo.length <= 0
	})
	watch(props, (newValue) => {
		if (newValue.modelValue) {
			emit('update:modelValue', newValue.modelValue)
		}
		if (!isEmpty(newValue.formFieldListValue)) {
			// 获取主表名称
			const parentTableName = JSON.parse(props.recordData.tableJson).filter((item) => item.tableType === 'parent')[0]
				.tableName
			// 监听到字段列表后，将其转至定义的变量中
			fieldInfoList.value = []
			// 不仅表单中有字段，而且还要必须选择了主表
			if (newValue.formFieldListValue.length > 0) {
				const fieldLists = getListField(newValue.formFieldListValue)
				fieldLists.forEach((item) => {
					const obj = {}
					// 判断是否是选择了表，并且选择了字段
					if (item.selectTable && item.selectColumn) {
						// 判断是否是主表的字段，并且是必填项
						const requireds = item.rules[0].required
						if ((item.selectTable === parentTableName) & requireds) {
							obj.label = item.label
							obj.value = item.model
							fieldInfoList.value.push(obj)
						}
					}
				})
			}
		}
	})
	const showDrawer = () => {
		form.value = {}
		form.value = cloneDeep(props.modelValue)
		formData.value = form.value.properties.configInfo
		drawer.value = true
		// 制作默认的摘要字段
		makeAbstractTemp()
	}
	// 制作默认显示的摘要信息
	const makeAbstractTemp = () => {
		let temp = form.value.properties.configInfo.processAbstractTemplate
		if (temp === '') {
			if (fieldInfoList.value.length > 0) {
				let fieldInfoTemp = ''
				for (let a = 0; a < fieldInfoList.value.length; a++) {
					// 最多3个摘要，按顺序而来
					if (a < 3) {
						const str = fieldInfoList.value[a].label + ':' + fieldInfoList.value[a].value
						if (a === 3 - 1 || a === fieldInfoList.value.length - 1) {
							fieldInfoTemp = fieldInfoTemp + str
						} else {
							fieldInfoTemp = fieldInfoTemp + str + ','
						}
					}
				}
				temp = fieldInfoTemp
			}
		}
	}
	const onFinish = () => {
		// 校验表单的正确性
		noticeFormRef.value
			.validate()
			.then((values) => {
				if (form.value.id === '') {
					form.value.id = tool.snowyUuid()
				}
				// 获取输入的监听
				form.value.properties.executionListenerInfo = propListenerInfoRef.value.selectedListenerList()
				form.value.dataLegal = true
				form.value.properties.configInfo = values
				emit('update:modelValue', form.value)
				drawer.value = false
			})
			.catch((info) => {
				formVerify.value = true
				setTimeout((e) => {
					formVerify.value = false
				}, 2000)
			})
	}
	// 选择参与人
	const selectionParticipants = (type) => {
		let participateInfo = form.value.properties.participateInfo
		let data = []
		if (participateInfo.length > 0) {
			participateInfo.forEach((item) => {
				if (item.key === type) {
					data.push(item)
				}
			})
		}
		if (type === 'ORG') {
			orgSelectorPlusRef.value.showOrgPlusModal(data)
		}
		if (type === 'ROLE') {
			roleSelectorPlusRef.value.showRolePlusModal(data)
		}
		if (type === 'POSITION') {
			posSelectorPlusRef.value.showPosPlusModal(data)
		}
		if (type === 'USER') {
			userSelectorPlusRef.value.showUserPlusModal(data)
		}
	}
	// 回调函数，这几个选择人员相关的设计器，都是的
	const callBack = (value) => {
		let participateInfo = form.value.properties.participateInfo
		if (participateInfo.length > 0) {
			let mark = 0
			for (let a = 0; a < participateInfo.length; a++) {
				if (value.key === participateInfo[a].key) {
					if (value.label === '') {
						participateInfo.splice(a, 1)
					} else {
						participateInfo[a] = value
					}
					mark = 1
				}
			}
			if (mark === 0) {
				participateInfo.push(value)
			}
		} else {
			form.value.properties.participateInfo.push(value)
		}
	}
	// 获取tag标签的内容
	const getTagList = (type) => {
		const participateInfo = form.value.properties.participateInfo
		if (participateInfo.length < 0) {
			return undefined
		} else {
			return participateInfo.find((item) => item.key === type)
		}
	}
	const getListField = (data) => {
		let result = []
		// 递归遍历控件树
		const traverse = (array) => {
			array.forEach((element) => {
				if (element.type === 'grid' || element.type === 'tabs') {
					// 栅格布局 and 标签页
					element.columns.forEach((item) => {
						traverse(item.list)
					})
				} else if (element.type === 'card') {
					// 卡片布局 and  动态表格
					traverse(element.list)
				} else if (element.type === 'table') {
					// 表格布局
					element.trs.forEach((item) => {
						item.tds.forEach((val) => {
							traverse(val.list)
						})
					})
				} else {
					const type = element.type
					// 排除一些
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'batch') & (type !== 'html')) {
						result.push(element)
					}
				}
			})
		}
		traverse(data)
		return result
	}
	// 抛出钩子
	defineExpose({
		showDrawer
	})
</script>
