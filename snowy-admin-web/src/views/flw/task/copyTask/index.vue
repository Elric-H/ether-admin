<template>
	<a-card :bordered="false">
		<a-row :gutter="10">
			<a-col :span="4">
				<a-menu v-model:selected-keys="myCopyTypes" mode="inline" @click="myCopyTypeClock">
					<a-menu-item :key="processMyCopy.value" v-for="processMyCopy in processCopyTypeArray">{{
						processMyCopy.label
					}}</a-menu-item>
				</a-menu>
			</a-col>
			<a-col :span="20">
				<a-form ref="searchFormRef" name="advanced_search" class="ant-advanced-search-form" :model="searchFormState">
					<a-row :gutter="24">
						<a-col :span="8">
							<a-form-item name="name">
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
				<a-divider class="m-3 mx-0" />
				<s-table
					ref="tableRef"
					:columns="columns"
					:data="loadData"
					bordered
					:row-key="(record) => record.id"
					:tool-config="toolConfig"
					:row-selection="options.rowSelection"
				>
					<template #operator>
						<xn-batch-button
							v-if="myCopyTypes[0] === 'READ'"
							buttonName="设为已阅"
							buttonType="primary"
							icon="carry-out-outlined"
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="hasReadBatchMyCopy"
						/>
						<xn-batch-button
							v-if="myCopyTypes[0] === 'HAS_READ'"
							buttonName="删除"
							buttonDanger
							icon="delete-outlined"
							:selectedRowKeys="selectedRowKeys"
							@batchCallBack="deleteBatchMyCopy"
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
							<a-divider type="vertical" />
							<a-popconfirm title="确定要设为已阅吗？" @confirm="hasReadMyCopy(record)">
								<a-button v-if="myCopyTypes[0] === 'READ'" type="link" size="small">已阅</a-button>
							</a-popconfirm>
							<a-popconfirm title="确定要删除此流程吗？" @confirm="deleteMyCopy(record)">
								<a-button v-if="myCopyTypes[0] === 'HAS_READ'" type="link" danger size="small">删除</a-button>
							</a-popconfirm>
						</template>
					</template>
				</s-table>
			</a-col>
		</a-row>
	</a-card>
	<task-detail ref="taskDetailRef" />
</template>

<script setup name="flwCopyTask">
	import processMyApi from '@/api/flw/processMyApi'
	import TaskDetail from './taskDetail.vue'

	const tableRef = ref()
	const processCopyTypeArray = ref([
		{
			label: '待阅流程',
			value: 'READ'
		},
		{
			label: '已阅流程',
			value: 'HAS_READ'
		}
	])
	const myCopyTypes = ref(new Array(processCopyTypeArray.value[0].value))
	const taskDetailRef = ref()
	const searchFormState = ref({})
	const searchFormRef = ref()
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
			dataIndex: 'stateText'
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
			width: 150
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
	// 类型点击
	const myCopyTypeClock = () => {
		// 先让上面的变量赋值，咱在查询
		nextTick(() => {
			tableRef.value.refresh(true)
		})
	}
	const loadData = (parameter) => {
		if (myCopyTypes.value[0] === 'READ') {
			return processMyApi.processMyCopyUnreadPage(Object.assign(parameter, searchFormState.value)).then((data) => {
				return data
			})
		}
		if (myCopyTypes.value[0] === 'HAS_READ') {
			return processMyApi.processMyCopyHasReadPage(Object.assign(parameter, searchFormState.value)).then((data) => {
				return data
			})
		}
	}
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 设为已阅
	const hasReadMyCopy = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		processMyApi.processReadMyCopyProcess(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量设为已阅
	const hasReadBatchMyCopy = (value) => {
		processMyApi.processReadMyCopyProcess(value).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 删除已阅流程
	const deleteMyCopy = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		processMyApi.processDeleteMyHasReadProcess(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除已阅
	const deleteBatchMyCopy = (value) => {
		processMyApi.processDeleteMyHasReadProcess(value).then(() => {
			tableRef.value.refresh(true)
		})
	}
</script>
<style scoped>
	.ant-form-item {
		margin-bottom: 0 !important;
	}
</style>
