<template>
	<a-card>
		<Workflow
			v-model="modelDesignData.modelValue"
			:form-field-list-value="formFieldListValue"
			:record-data="recordData"
			:sn-template-array="snTemplateArray"
			:print-template-array="printTemplateArray"
			:execution-listener-array="executionListenerArray"
			:execution-listener-selector-for-custom-event-array="executionListenerSelectorForCustomEventArray"
			:task-listener-array="taskListenerArray"
			:selector-api-function="selectorApiFunction"
		/>
	</a-card>
</template>
<script setup name="modelDesign">
	import Workflow from '@/components/XnWorkflow/index.vue'
	import modelApi from '@/api/flw/modelApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import templatePrintApi from '@/api/flw/templatePrintApi'
	import templateSnApi from '@/api/flw/templateSnApi'

	// 模型设计器的值
	const modelDesignData = ref({})
	// 模型主题值
	const recordData = ref({})
	// 序列号列表
	const snTemplateArray = ref([])
	// 打印模板列表
	const printTemplateArray = ref([])
	// 执行监听器数据
	const executionListenerArray = ref([])
	// 自定义事件执行监听器选择器
	const executionListenerSelectorForCustomEventArray = ref([])
	// 任务监听器数据
	const taskListenerArray = ref([])

	const formFieldListValue = ref([])
	// 获取值
	const getValue = () => {
		return modelDesignData.value.modelValue
	}
	// 回显值
	const setValue = (value, record) => {
		modelDesignData.value = value
		recordData.value = record
		// 获取序列号列表
		templateSnApi.templateFlwTemplateSnSelector().then((data) => {
			snTemplateArray.value = data.map((item) => {
				return {
					value: item['id'],
					label: item['name']
				}
			})
		})
		// 获取打印模板列表
		templatePrintApi.templateFlwTemplatePrintSelector().then((data) => {
			printTemplateArray.value = data.map((item) => {
				return {
					value: item['id'],
					label: item['name']
				}
			})
		})
		// 获取执行监听器数据
		modelApi.modelExecutionListenerSelector().then((data) => {
			executionListenerArray.value = data.map((item) => {
				return {
					label: item,
					value: item
				}
			})
		})
		// 获取自定义事件执行监听器选择器
		modelApi.modelExecutionListenerSelectorForCustomEvent().then((data) => {
			executionListenerSelectorForCustomEventArray.value = data.map((item) => {
				return {
					label: item,
					value: item
				}
			})
		})
		// 获取任务监听器数据
		modelApi.modelTaskListenerSelector().then((data) => {
			taskListenerArray.value = data.map((item) => {
				return {
					label: item,
					value: item
				}
			})
		})
	}
	// 传字段的数据过来
	const setFormFieldListValue = (value) => {
		if (value) {
			formFieldListValue.value = JSON.parse(JSON.stringify(value))
		}
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return modelApi.modelOrgTreeSelector(param).then((orgTree) => {
				return Promise.resolve(orgTree)
			})
		},
		orgPageApi: (param) => {
			return modelApi.modelOrgListSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		rolePageApi: (param) => {
			return modelApi.modelRoleSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		posPageApi: (param) => {
			return modelApi.modelPositionSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return modelApi.modelUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedUserListApi: (param) => {
			return userCenterApi.userCenterGetUserListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedPosListApi: (param) => {
			return userCenterApi.userCenterGetPositionListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedOrgListApi: (param) => {
			return userCenterApi.userCenterGetOrgListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedRoleListApi: (param) => {
			return userCenterApi.userCenterGetRoleListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	// 抛出钩子
	defineExpose({
		getValue,
		setValue,
		setFormFieldListValue
	})
</script>
