<template>
	<a-card :bordered="false" :body-style="{ 'padding-bottom': '0px' }" class="mb-2">
		<a-form ref="searchFormRef" name="advanced_search" :model="searchFormState" class="ant-advanced-search-form">
			<a-row :gutter="24">
				<a-col :span="8">
					<a-form-item name="searchKey" label="发起人关键词">
						<a-input v-model:value="searchFormState.searchKey" placeholder="请输入发起人姓名关键词" />
					</a-form-item>
				</a-col>
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
			:alert="false"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
		>
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
					<a @click="detailTaskRef.onOpen(record)">详情</a>
				</template>
			</template>
		</s-table>
		<detail-task ref="detailTaskRef" />
	</a-card>
</template>

<script setup name="flwDoneTask">
	import taskApi from '@/api/flw/taskApi'
	import DetailTask from './taskDetail.vue'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const detailTaskRef = ref()

	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '标题',
			dataIndex: 'title',
			ellipsis: true
		},
		{
			title: '摘要',
			dataIndex: 'abstractTitle',
			ellipsis: true
		},
		{
			title: '办理节点',
			dataIndex: 'doneActivityName',
			ellipsis: true
		},
		{
			title: '发起人',
			dataIndex: 'initiatorName'
		},
		{
			title: '发起人组织',
			dataIndex: 'initiatorOrgName',
			ellipsis: true
		},
		{
			title: '发起人职位',
			dataIndex: 'initiatorPositionName',
			ellipsis: true
		},
		{
			title: '状态',
			dataIndex: 'stateText',
			width: 80
		},
		{
			title: '发起时间',
			dataIndex: 'initiatorTime',
			ellipsis: true
		},
		{
			title: '办理时间',
			dataIndex: 'doneTime',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 100
		}
	]
	const loadData = (parameter) => {
		return taskApi.taskDonePage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
</script>
