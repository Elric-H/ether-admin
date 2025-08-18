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
				<template v-if="column.dataIndex === 'title'">
					<a-tooltip>
						<template #title>{{ record.title }}</template>
						{{ record.title }}
					</a-tooltip>
				</template>
				<template v-if="column.dataIndex === 'abstractTitle'">
					<a-tooltip>
						<template #title>{{ record.abstractTitle }}</template>
						{{ record.abstractTitle }}
					</a-tooltip>
				</template>
				<template v-if="column.dataIndex === 'currentActivityName'">
					<a-tag color="cyan">{{record.currentActivityName}}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="todoProcessRef.onOpen(record)">办理</a>
				</template>
			</template>
		</s-table>
	</a-card>
	<todoProcess ref="todoProcessRef" @successful="tableRef.refresh()" />
</template>

<script setup name="flwTodoTask">
	import taskApi from '@/api/flw/taskApi'
	import TodoProcess from './todoProcess.vue'
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const todoProcessRef = ref()
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
			title: '办理人',
			dataIndex: 'assignees',
			ellipsis: true
		},
		{
			title: '当前节点',
			dataIndex: 'currentActivityName',
			width: '100px'
		},
		{
			title: '发起人',
			dataIndex: 'initiatorName'
		},
		{
			title: '发起时间',
			dataIndex: 'initiatorTime'
		},
		{
			title: '发起人组织',
			dataIndex: 'initiatorOrgName'
		},
		{
			title: '发起人职位',
			dataIndex: 'initiatorPositionName'
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: '100px'
		}
	]
	const loadData = (parameter) => {
		return taskApi.taskTodoPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
</script>
