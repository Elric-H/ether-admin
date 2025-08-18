<template>
	<div class="mb-2">
		<span class="left-span-label">参与者可以看见或操作哪些按钮</span>
	</div>
	<a-table
		ref="tableRef"
		:columns="columns"
		:data-source="dataSource"
		:row-key="(record) => record.key"
		:expand-row-by-click="true"
		:row-selection="{
			selectedRowKeys: state.selectedRowKeys,
			onChange: onSelectChange,
			getCheckboxProps: checkboxProps
		}"
		:pagination="false"
		:show-header="false"
		size="small"
	>
		<template #bodyCell="{ column, record }">
			<template v-if="column.dataIndex === 'label'">
				<a-popover trigger="hover" placement="left">
					<template #content>
						<a-button :type="record.type" size="small">
							<template #icon>
								<component :is="record.icon" />
							</template>
							{{ record.label }}
						</a-button>
						<div v-if="record.key === 'SAVE'" style="width: 300px">
							”保存“按钮作用为发起节点保存操作，审批节点下无保存操作。
						</div>
						<div v-if="record.key === 'SUBMIT'" style="width: 300px">
							”提交“按钮作用为发起节点填写完申请单，提交流程到下一步。
						</div>
						<div v-if="record.key === 'REVOKE'" style="width: 300px">”撤回“按钮作用为发起节点发错内容，将其撤回。</div>
						<div v-if="record.key === 'PASS'" style="width: 300px">按钮作用为审批节点同意该审核之操作。</div>
						<div v-if="record.key === 'REJECT'" style="width: 300px">按钮作用为审批节点进行拒绝之操作。</div>
						<div v-if="record.key === 'BACK'" style="width: 300px">按钮作用为审批节点退回操作，可退回至任意节点。</div>
						<div v-if="record.key === 'ADD_SIGN'" style="width: 300px">按钮作用为审批节点加签操作。</div>
						<div v-if="record.key === 'TURN'" style="width: 300px">按钮作用为审批节点转给他人办理操作。</div>
						<div v-if="record.key === 'JUMP'" style="width: 300px">按钮作用为审批节点可跳转至任意节点操作。</div>
						<div v-if="record.key === 'PRINT'" style="width: 300px">
							按钮配置后，操作人到该节点可以进行管理员配置的全局打印模板，进行打印此流程进展的内容。
						</div>
					</template>
					<a-button size="small" :type="record.type">
						<template #icon>
							<component :is="record.icon" />
						</template>
						{{ record.label }}
					</a-button>
				</a-popover>
			</template>
			<template v-if="column.dataIndex === 'newLabel'">
				<a-input v-model:value="record.newLabel" show-count :maxlength="10" />
			</template>
		</template>
	</a-table>
</template>

<script setup name="propButtonInfo">
	import config from '@/components/XnWorkflow/nodes/config/config'
	import { cloneDeep } from 'lodash-es'

	const columns = [
		{
			title: '',
			dataIndex: 'label',
			align: 'center'
		},
		{
			title: '',
			dataIndex: 'newLabel'
		}
	]
	const buttonInfo = cloneDeep(config.nodeModel.buttonInfo)

	const props = defineProps(['buttonInfo', 'showButton', 'noChecked'])

	const dataSource = ref([])
	const selectedRowKeys = ref([])
	// 将其回传的跟本组件的关联性切断
	selectedRowKeys.value = cloneDeep(props.showButton)

	// 设置按钮选中及显示的
	const buttonInfoData = () => {
		buttonInfo.forEach((button) => {
			if (props.buttonInfo.length > 0) {
				props.buttonInfo.forEach((item) => {
					if (button.key === item.key) {
						button.newLabel = item.label
						button.value = item.value
					}
				})
			} else {
				button.newLabel = button.label
			}
			// 匹配选中的
			if (selectedRowKeys.value.length > 0) {
				selectedRowKeys.value.forEach((selectedRowKey) => {
					if (button.value === 'SHOW') {
						// 已有的里面如果不包含这个已经被选中的
						if (selectedRowKeys.value.indexOf(button.key) === -1) {
							selectedRowKeys.value.push(button.key)
						}
					}
					if (selectedRowKey === button.key) {
						button.value = 'SHOW'
					}
				})
			} else if (button.value === 'SHOW') {
				if (selectedRowKeys.value.indexOf(button.key) === -1) {
					selectedRowKeys.value.push(button.key)
				}
			}
		})
		return buttonInfo
	}

	dataSource.value = buttonInfoData()

	// 设置默认选中的
	const state = reactive({
		selectedRowKeys: selectedRowKeys.value,
		loading: false
	})
	// 点击复选框回调
	const onSelectChange = (selectedRowKeys) => {
		state.selectedRowKeys = selectedRowKeys
	}
	// 设置选择框的默认属性配置，将其设置成不可取消，不可选中
	const checkboxProps = (record) => ({
		disabled:
			(record.value === 'SHOW' && props.showButton.indexOf(record.key) > -1) || props.noChecked.indexOf(record.key) > -1
	})
	// 父界面需要调用获取参数
	const selectedButtonKeyList = () => {
		let resultData = cloneDeep(dataSource.value)
		resultData.forEach((dataItem) => {
			if (state.selectedRowKeys.indexOf(dataItem.key) > -1) {
				dataItem.value = 'SHOW'
			} else {
				dataItem.value = 'HIDE'
			}
			if (dataItem.newLabel !== '') {
				dataItem.label = dataItem.newLabel
			}
			delete dataItem.newLabel
		})
		return resultData
	}
	// 抛出方法，让上个界面使用
	defineExpose({
		selectedButtonKeyList
	})
</script>
