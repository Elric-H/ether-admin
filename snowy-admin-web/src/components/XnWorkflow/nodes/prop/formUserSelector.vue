<template>
	<a-modal
		v-model:visible="visible"
		title="表单人员选择"
		:mask-closable="false"
		:destroy-on-close="true"
		:width="600"
		@ok="handleOk"
		@cancel="handleCancel"
	>
		<div class="form-user-table">
			<a-table
				ref="tableRef"
				:columns="columns"
				:data-source="dataSource"
				:row-key="(record) => record.model"
				:expand-row-by-click="true"
				:row-selection="{ selectedRowKeys: state.selectedRowKeys, onChange: onSelectChange, type: 'radio' }"
				:pagination="false"
				size="small"
				bordered
			/>
		</div>
	</a-modal>
</template>

<script setup name="formUserSelector">
	import workFlowUtils from '@/components/XnWorkflow/nodes/utils/index'
	import { message } from 'ant-design-vue'
	const visible = ref(false)

	const columns = [
		{
			title: '字段名',
			dataIndex: 'label'
		},
		{
			title: '字段',
			dataIndex: 'model'
		}
	]

	// 定义emit事件
	const emit = defineEmits({ click: null })
	const props = defineProps(['formFieldList'])

	const dataSource = ref([])
	dataSource.value = workFlowUtils
		.getListField(props.formFieldList)
		.filter((f) => {
			// 判断如果是人员类型的，就让列表显示
			if (f.type === 'xnUserSelector') {
				return f
			}
		})
		.map((m) => {
			return {
				label: m.label,
				model: m.model,
				field: m.model,
				type: m.type
			}
		})
	const selectedRowKeys = ref([])
	// 设置默认选中的
	const state = reactive({
		selectedRowKeys: selectedRowKeys.value,
		loading: false
	})
	const showFormUserModal = (data = '') => {
		if (dataSource.value.length === 0) {
			message.warning('表单内无人员可提供选择！')
			return
		}
		if (dataSource.value.length === 1) {
			emit('click', dataSource.value[0])
			message.success('表单内仅有一个人员选择，默认选中！')
		} else {
			visible.value = true
			state.selectedRowKeys[0] = data
		}
	}
	// 点击复选框回调
	const onSelectChange = (selectedRowKeys) => {
		state.selectedRowKeys = selectedRowKeys
	}
	// 确定
	const handleOk = () => {
		const result = dataSource.value.filter((item) => state.selectedRowKeys[0] === item.model)
		emit('click', result[0])
		visible.value = false
		state.selectedRowKeys = []
	}
	// 关闭
	const handleCancel = () => {
		visible.value = false
	}

	// 抛出方法，让上个界面使用
	defineExpose({
		showFormUserModal
	})
</script>

<style lang="less">
	.form-user-table {
		overflow: auto;
		max-height: 400px;
	}
</style>
