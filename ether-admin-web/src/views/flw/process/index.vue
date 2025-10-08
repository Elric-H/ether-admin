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
			:alert="options.alert.show"
			bordered
			:row-key="(record) => record.id"
			:tool-config="toolConfig"
			:row-selection="options.rowSelection"
			:scroll="{ x: 1300 }"
		>
			<template #operator class="table-operator">
				<a-space>
					<xn-batch-button
						v-if="hasPerm('flwProcessBatchRevoke')"
						buttonName="撤回"
						buttonType="primary"
						color="#BFBFBF"
						icon="rollback-outlined"
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="revokeBatchProcess"
					/>
					<xn-batch-button
						v-if="hasPerm('flwProcessBatchEnd')"
						buttonName="终止"
						color="#FF4D4F"
						icon="close-outlined"
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="endBatchProcess"
					/>
					<xn-batch-button
						v-if="hasPerm('flwProcessBatchActive')"
						buttonName="激活"
						color="#1890FF"
						icon="redo-outlined"
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="activeBatchProcess"
					/>
					<xn-batch-button
						v-if="hasPerm('flwProcessBatchSuspend')"
						buttonName="挂起"
						color="#FCC02E"
						icon="stop-outlined"
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="suspendBatchProcess"
					/>
					<xn-batch-button
						v-if="hasPerm('flwProcessBatchDelete')"
						buttonName="删除"
						icon="delete-outlined"
						buttonDanger
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="deleteBatchProcess"
					/>
				</a-space>
			</template>
			<template #bodyCell="{ column, record }">
				<template v-if="column.dataIndex === 'title'">
					<ellipsis :length="20" tooltip>
						{{ record.title }}
					</ellipsis>
				</template>
				<template v-if="column.dataIndex === 'abstractTitle'">
					<ellipsis :length="10" tooltip>
						{{ record.abstractTitle }}
					</ellipsis>
				</template>
				<template v-if="column.dataIndex === 'stateText'">
					<a-tag v-if="record.stateCode === 'ACTIVE'" color="#1890FF">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'SUSPENDED'" color="#FCC02E">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'COMPLETED'" color="#52C41A">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'END'" color="#FF5A5A">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'REVOKE'" color="#BFBFBF">{{ record.stateText }}</a-tag>
					<a-tag v-if="record.stateCode === 'REJECT'" color="#FF4D4F">{{ record.stateText }}</a-tag>
				</template>
				<template v-if="column.dataIndex === 'action'">
					<a @click="processDetailRef.onOpen(record)" v-if="hasPerm('flwProcessDetail')">详情</a>
					<a-divider type="vertical" v-if="hasPerm('flwProcessDetail')" />
					<a @click="processVariableFormRef.onOpen(record)" v-if="hasPerm('flwProcessVariable')">变量</a>
					<a-divider type="vertical" v-if="hasPerm('flwProcessVariable')" />
					<a-popconfirm title="确认删除此流程吗？" @confirm="processRecordDelete(record)">
						<a-button type="link" danger size="small" v-if="hasPerm('flwProcessDelete')">删除</a-button>
					</a-popconfirm>
					<a-divider type="vertical" v-if="hasPerm('flwProcessDelete')" />
					<a-dropdown
						v-if="
							hasPerm([
								'flwProcessEnd',
								'flwProcessActiveSuspend',
								'flwProcessRevoke',
								'flwProcessTurn',
								'flwProcessJump',
								'flwProcessRestart',
								'flwProcessMigrate'
							])
						"
					>
						<a class="ant-dropdown-link">
							更多
							<DownOutlined />
						</a>
						<template #overlay>
							<a-menu>
								<a-menu-item
									v-if="
										hasPerm('flwProcessActiveSuspend') &&
										(record.stateCode === 'ACTIVE' || record.stateCode === 'SUSPENDED')
									"
								>
									<template #icon v-if="record.stateCode === 'ACTIVE'">
										<stop-two-tone two-tone-color="#FCC02E" />
									</template>
									<template #icon v-if="record.stateCode === 'SUSPENDED'">
										<redo-outlined style="color: #1890ff" />
									</template>
									<a-popconfirm
										v-if="record.stateCode === 'ACTIVE'"
										title="确定挂起此流程？"
										@confirm="editActiveSuspendStatus(record)"
										placement="topRight"
									>
										<a>挂起</a>
									</a-popconfirm>
									<a-popconfirm
										v-else-if="record.stateCode === 'SUSPENDED'"
										title="确定激活此流程？"
										@confirm="editActiveSuspendStatus(record)"
										placement="topRight"
									>
										<a>激活</a>
									</a-popconfirm>
								</a-menu-item>
								<a-menu-item v-if="hasPerm('flwProcessRevoke') && record.stateCode === 'ACTIVE'">
									<template #icon>
										<rollback-outlined style="color: #bfbfbf" />
									</template>
									<a-popconfirm title="确定撤回此流程？" @confirm="processRecordRevoke(record)" placement="topRight">
										<a>撤回</a>
									</a-popconfirm>
								</a-menu-item>
								<a-menu-item v-if="hasPerm('flwProcessEnd') && record.stateCode === 'ACTIVE'">
									<template #icon>
										<close-outlined style="color: #ff5a5a" />
									</template>
									<a-popconfirm title="确定终止此流程？" @confirm="processRecordEnd(record)" placement="topRight">
										<a>终止</a>
									</a-popconfirm>
								</a-menu-item>
								<a-menu-item v-if="hasPerm('flwProcessTurn') && record.stateCode === 'ACTIVE'">
									<template #icon>
										<user-switch-outlined style="color: #1890ff" />
									</template>
									<a @click="processTurnFormRef.onOpen(record)">转办</a>
								</a-menu-item>
								<a-menu-item v-if="hasPerm('flwProcessJump') && record.stateCode === 'ACTIVE'">
									<template #icon>
										<ungroup-outlined style="color: #1890ff" />
									</template>
									<a @click="processJumpFormRef.onOpen(record)">跳转</a>
								</a-menu-item>
								<a-menu-item
									v-if="
										(hasPerm('flwProcessRestart') && record.stateCode === 'COMPLETED') ||
										record.stateCode === 'END' ||
										record.stateCode === 'REJECT' ||
										record.stateCode === 'REVOKE'
									"
								>
									<template #icon>
										<poweroff-outlined style="color: #52c41a" />
									</template>
									<a @click="processRestartFormRef.onOpen(record)">复活</a>
								</a-menu-item>
								<a-menu-item v-if="hasPerm('flwProcessMigrate') && record.stateCode === 'ACTIVE'">
									<template #icon>
										<rotate-right-outlined style="color: #7265e6" />
									</template>
									<a @click="processMigrateFormRef.onOpen(record)">迁移</a>
								</a-menu-item>
							</a-menu>
						</template>
					</a-dropdown>
				</template>
			</template>
		</s-table>
		<process-detail ref="processDetailRef" />
		<process-turn-form ref="processTurnFormRef" @successful="tableRef.refresh()" />
		<process-migrate-form ref="processMigrateFormRef" @successful="tableRef.refresh()" />
		<process-restart-form ref="processRestartFormRef" @successful="tableRef.refresh()" />
		<process-jump-form ref="processJumpFormRef" @successful="tableRef.refresh()" />
		<process-variable-form ref="processVariableFormRef" @successful="tableRef.refresh()" />
	</a-card>
</template>

<script setup name="flwProcess">
	import { message } from 'ant-design-vue'
	import processApi from '@/api/flw/processApi'
	import ProcessDetail from './processDetail.vue'
	import ProcessTurnForm from './processTurnForm.vue'
	import ProcessMigrateForm from './processMigrateForm.vue'
	import ProcessRestartForm from './processRestartForm.vue'
	import ProcessJumpForm from './processJumpForm.vue'
	import ProcessVariableForm from './processVariableForm.vue'

	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	const processDetailRef = ref()
	const stateLoading = ref(false)
	const selectedRowKeys = ref([])
	const processVariableFormRef = ref()
	const processTurnFormRef = ref()
	const processMigrateFormRef = ref()
	const processRestartFormRef = ref()
	const processJumpFormRef = ref()
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
			title: '定义名称',
			dataIndex: 'processDefinitionName',
			ellipsis: true
		},
		{
			title: '定义版本',
			dataIndex: 'processDefinitionVersion',
			ellipsis: true,
			width: 100
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
			title: '当前节点',
			dataIndex: 'currentActivityNames',
			ellipsis: true
		},
		{
			title: '当前办理人',
			dataIndex: 'assignees',
			ellipsis: true
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
		}
	]
	if (
		hasPerm([
			'flwProcessDetail',
			'flwProcessVariable',
			'flwProcessEnd',
			'flwProcessDelete',
			'flwProcessActiveSuspend',
			'flwProcessRevoke',
			'flwProcessTurn',
			'flwProcessJump',
			'flwProcessRestart',
			'flwProcessMigrate'
		])
	) {
		columns.push({
			title: '操作',
			dataIndex: 'action',
			fixed: 'right',
			width: 220
		})
	}
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
		return processApi.processMonitorPage(Object.assign(parameter, searchFormState.value)).then((res) => {
			return res
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 撤回
	const processRecordRevoke = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		processApi.processRevoke(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 终止
	const processRecordEnd = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		processApi.processEnd(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 删除
	const processRecordDelete = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		processApi.processDelete(params).then(() => {
			tableRef.value.refresh()
		})
	}
	// 激活挂起
	const editActiveSuspendStatus = (record) => {
		const params = [
			{
				id: record.id
			}
		]
		if (record.stateCode === 'ACTIVE') {
			stateLoading.value = true
			processApi
				.processSuspend(params)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					stateLoading.value = false
				})
		}
		if (record.stateCode === 'SUSPENDED') {
			stateLoading.value = true
			processApi
				.processActive(params)
				.then(() => {
					tableRef.value.refresh()
				})
				.finally(() => {
					stateLoading.value = false
				})
		}
	}
	// 弹窗关闭事件
	const onClose = () => {
		tableRef.value.refresh()
	}
	// 批量撤回
	const revokeBatchProcess = (value) => {
		processApi.processRevoke(value).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 批量终止
	const endBatchProcess = (value) => {
		processApi.processEnd(value).then(() => {
			message.success('操作成功')
			tableRef.value.clearRefreshSelected()
		})
	}
	// 批量激活
	const activeBatchProcess = (value) => {
		processApi.processActive(value).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 批量挂起
	const suspendBatchProcess = (value) => {
		processApi.processSuspend(value).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 批量删除
	const deleteBatchProcess = (value) => {
		processApi.processDelete(value).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
</script>
