<template>
	<xn-form-container
		title="已办详情"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="flowForm" tab="表单">
				<snowy-form-build ref="formBuildRef" :value="jsonData" />
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

<script setup name="detaileProcess">
	import processApi from '@/api/flw/processApi'
	import { nextTick } from 'vue'
	import flowChart from '@/components/XnWorkflow/chart/index.vue'
	import timelineForm from './timelineForm.vue'

	const visible = ref(false)
	const activeKey = ref('flowForm')
	const formBuildRef = ref()

	const jsonData = ref({})
	const modelDesignData = ref({})
	const commentList = ref([])
	// 自动获取宽度，默认获取浏览器的宽度的90%
	const drawerWidth = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) * 0.5

	const onOpen = (record) => {
		visible.value = true
		// 获取详情
		const param = {
			id: record.id
		}
		processApi.processDetail(param).then((data) => {
			modelDesignData.value = JSON.parse(data.initiatorModelJson)
			commentList.value = data.commentList

			jsonData.value = convFormFilid(JSON.parse(data.initiatorFormJson))
			const formData = Object.values(JSON.parse(data.initiatorDataJson))[0]
			nextTick(() => {
				// 设置表单参数
				formBuildRef.value.setData(formData)
			})
		})
	}
	// 遍历表单，并设置为禁用状态
	const convFormFilid = (formJson) => {
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
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'html')) {
						element.options.disabled = true
					}
				}
			})
		}
		traverse(formJson.list)
		return formJson
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
