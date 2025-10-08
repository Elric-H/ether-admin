<template>
	<xn-form-container
		title="流程变量"
		:width="800"
		:visible="visible"
		:destroy-on-close="true"
		:confirmLoading="confirmLoading"
		@close="onClose"
	>
		<a-alert message="变量功能仅支持在审批中的状态下进行，非专业人员禁止修改！" type="warning" class="mb-3" banner />
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:scroll="{ x: 1000 }"
		>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'action'">
					<a @click="processVariableEditFormRef.onOpen(record)" v-if="hasPerm('flwProcessVariableEdit')">编辑</a>
				</template>
			</template>
		</s-table>
	</xn-form-container>
	<process-variable-edit-form ref="processVariableEditFormRef" @successful="tableRef.refresh()" />
</template>

<script setup name="processVariableForm">
	import processApi from '@/api/flw/processApi'
	import ProcessVariableEditForm from './processVariableEditForm.vue'

	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const confirmLoading = ref(false)
	const tableRef = ref(null)
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const recordData = ref()
	const searchFormState = ref()
	const processVariableEditFormRef = ref()
	const columns = [
		{
			title: '变量id',
			dataIndex: 'id',
			ellipsis: true
		},
		{
			title: '流程实例id',
			dataIndex: 'processInstanceId',
			ellipsis: true
		},
		{
			title: '执行实例id',
			dataIndex: 'executionId',
			ellipsis: true
		},
		{
			title: '变量名称',
			dataIndex: 'name',
			ellipsis: true,
			width: 160
		},
		{
			title: '变量值',
			dataIndex: 'value',
			ellipsis: true
		},
		{
			title: '变量类型',
			dataIndex: 'typeName',
			width: 80
		},
		{
			title: '操作',
			dataIndex: 'action',
			fixed: 'right',
			width: 70
		}
	]
	const loadData = (parameter) => {
		if (!recordData.value) {
			return []
		}
		return processApi.processVariablePage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 打开抽屉
	const onOpen = (record) => {
		searchFormState.value = {
			id: record.id
		}
		recordData.value = record
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		searchFormState.value = ''
		visible.value = false
	}
	defineExpose({
		onOpen
	})
</script>
