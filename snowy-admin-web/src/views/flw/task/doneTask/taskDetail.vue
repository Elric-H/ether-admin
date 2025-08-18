<template>
	<xn-form-container
		title="详情"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		:footer-style="{ textAlign: 'right' }"
		:footer="null"
		@close="onClose"
	>
		<process-detail-state-img :state-code="recordData.stateCode" />
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="flowForm" tab="表单">
				<snowy-form-build v-if="formType === 'DESIGN'" ref="formBuildRef" :value="jsonData" />
				<component
					v-else-if="formType === 'DEFINE'"
					:is="customFormsLayouts"
					:value="initiatorDataJson"
					:disabled="true"
				/>
				<div v-else>
					<a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" description="流程表单类型不正确，请联系管理员重置流程。" />
				</div>
			</a-tab-pane>
			<a-tab-pane key="flowChart" tab="流程图">
				<flowChart v-model="modelDesignData" />
			</a-tab-pane>
			<a-tab-pane key="flowTrunRecord" tab="流转记录">
				<timelineForm :commentList="commentList" />
			</a-tab-pane>
		</a-tabs>
	</xn-form-container>
</template>

<script setup name="doneTaskDetail">
	import taskApi from '@/api/flw/taskApi'
	import { nextTick } from 'vue'
	import { Empty } from 'ant-design-vue'
	import FlowChart from '@/components/XnWorkflow/chart/index.vue'
	import ProcessDetailStateImg from '../../process/processDetailStateImg.vue'
	import TimelineForm from '../../process/timelineForm.vue'
	import { loadComponent } from '@/components/XnWorkflow/customForm'
	import workFlowUtils from '@/components/XnWorkflow/nodes/utils/index'

	const visible = ref(false)
	const recordData = ref({})
	const activeKey = ref('flowForm')
	const formType = ref('DESIGN')
	const customFormsLayouts = ref()
	const formBuildRef = ref()
	const initiatorDataJson = ref()
	const jsonData = ref({})
	const modelDesignData = ref({})
	const commentList = ref([])
	// 自动获取宽度，默认获取浏览器的宽度的90%
	const drawerWidth = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) * 0.5

	const onOpen = (record) => {
		recordData.value = record
		visible.value = true
		// 获取详情
		const param = {
			id: record.id
		}
		taskApi.taskDetail(param).then((data) => {
			modelDesignData.value = workFlowUtils.coalesceDataListChildNode(
				JSON.parse(data.initiatorModelJson),
				data.commentList
			)
			commentList.value = data.commentList
			formType.value = data.formType
			// 当前节点，里面是增加了相关记录
			const childNode = workFlowUtils.getChildNode(
				JSON.parse(data.initiatorModelJson),
				record.doneActivityId,
				data.commentList
			)
			// 判断表单类型
			if (data.formType === 'DESIGN') {
				jsonData.value = workFlowUtils.convFormComponentsDisabled(JSON.parse(data.initiatorFormJson))
				const formData = Object.values(JSON.parse(data.initiatorDataJson))[0]
				nextTick(() => {
					// 设置表单参数
					formBuildRef.value.setData(formData)
				})
			} else if (data.formType === 'DEFINE') {
				const formInfo = childNode.properties.formInfo
				const formUrl = formInfo.find((f) => f.key === 'FORM_URL').value
				customFormsLayouts.value = loadComponent(formUrl)
				initiatorDataJson.value = JSON.parse(data.initiatorDataJson)
			}
		})
	}
	// 关闭抽屉
	const onClose = () => {
		activeKey.value = 'flowForm'
		visible.value = false
	}
	defineExpose({
		onOpen
	})
</script>
