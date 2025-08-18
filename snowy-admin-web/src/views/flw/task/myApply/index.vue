<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="name" label="流程名称">
						<a-input v-model:value="searchFormState.name" placeholder="请输入流程名称" />
					</a-form-item>
				</a-col>
				<a-col :span="8">
					<a-space>
						<a-button type="primary" @click="tableRef.refresh(true)">
							<template #icon><SearchOutlined /></template>
							查询
						</a-button>
						<a-button @click="reset">
							<template #icon><redo-outlined /></template>
							重置
						</a-button>
					</a-space>
				</a-col>
			</a-row>
		</a-form>
	</a-card>
	<a-card :bordered="false">
		<s-table
			ref="tableRef"
			:columns="columns"
			:data="loadData"
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
		>
			<template #operator class="table-operator">
				<xn-batch-button
					buttonName="撤回"
					buttonDanger
					icon="rollback-outlined"
					:selectedRowKeys="selectedRowKeys"
					@batchCallBack="revokeBatchTaskMyApply"
				/>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'stateText'">
					<a-tag v-if="record.stateCode === 'ACTIVE'" color="#1890FF">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'SUSPENDED'" color="#FCC02E">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'COMPLETED'" color="#52C41A">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'END'" color="#FF5A5A">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'REVOKE'" color="#BFBFBF">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'REJECT'" color="#FF4D4F">{{ record.stateText }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="taskDetailRef.onOpen(record)">详情</a>
					<a-divider type="vertical" v-if="record.state === '运行中'" />
					<a-popconfirm title="确定要撤回流程吗？" @confirm="revokeProcess(record)">
						<a-button type="link" danger size="small" v-if="record.state === '运行中'">撤回</a-button>
					</a-popconfirm>
				</template>
			</template>
		</s-table>
	</a-card>
	<task-detail ref="taskDetailRef" />
</template>

<script setup name="flwMyApply">
	import processMyApi from '@/api/flw/processMyApi'
	import TaskDetail from './taskDetail.vue'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const taskDetailRef = ref()
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '标题',
			dataIndex: 'title',
			ellipsis: true
		},
		{
			title: '流水号',
			dataIndex: 'sn',
			ellipsis: true
		},
		{
			title: '定义名称',
			dataIndex: 'processDefinitionName',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '定义版本',
			dataIndex: 'processDefinitionVersion',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '发起组织',
			dataIndex: 'initiatorOrgName',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '发起职位',
			dataIndex: 'initiatorPositionName',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '耗时',
			dataIndex: 'durationInfo',
			ellipsis: true
		},
		{
			title: '当前节点',
			dataIndex: 'currentActivityNames',
			ellipsis: true,
			width: '100px'
		},
		{
			title: '状态',
			dataIndex: 'stateText',
			width: 80
		},
		{
			title: '发起时间',
			dataIndex: 'startTime',
			ellipsis: true
		},
		{
			title: '结束时间',
			dataIndex: 'endTime',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 100
		}
	]
	const selectedRowKeys = ref([])
	// 列表选择配置
	const options = {
		alert: {
			show: false,
			clear: () => {
				selectedRowKeys.value = ref([])
			}
		},
		rowSelection: {
			onChange: (selectedRowKey, selectedRows) => {
				selectedRowKeys.value = selectedRowKey
			}
		}
	}
	const loadData = (parameter) => {
		return processMyApi.processMyPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 撤回
	const revokeProcess = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		processMyApi.processRevoke(params).then(() => {
			tableRef.value.refresh(true)
		})
	}

	// 批量撤回
	const revokeBatchTaskMyApply = (value) => {
		processMyApi.processRevoke(value).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
