<template>
	<a-card
		:bordered="false"
		:active-tab-key="cardActiveKey"
		:tab-list="tabListTitle"
		:loading="cardLoading"
		@tabChange="(key) => onTabChange(key)"
	>
		<div v-if="cardActiveKey === 'newProcessTask'">
			<a-collapse v-model:activeKey="activeKey" ghost expandIconPosition="right" :bordered="true">
				<a-collapse-panel
					v-for="item in modelCategory"
					:key="item.dictValue"
					v-show="myModelList.filter((m) => m.category === item.dictValue).length > 0"
				>
					<template #header>
						<span style="font-weight: 700">{{ item.name }}</span>
						<span style="color: #71797d; margin-left: 5px">
							（{{ myModelList.filter((m) => m.category === item.dictValue).length }}）
						</span>
					</template>
					<a-row :gutter="10">
						<a-col :span="4" v-for="model in myModelList.filter((m) => m.category === item.dictValue)">
							<process-card
								:color="model.color"
								:label="model.name"
								:icon="model.icon"
								@click="startProcessRef.onOpen(model, 'NEW')"
							/>
						</a-col>
					</a-row>
				</a-collapse-panel>
			</a-collapse>
			<div v-if="activeKey.length === 0">
				<a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</div>
		</div>
		<div v-if="cardActiveKey === 'myDraft'">
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
						buttonName="删除"
						buttonDanger
						icon="delete-outlined"
						:selectedRowKeys="selectedRowKeys"
						@batchCallBack="revokeBatchTaskMyDraft"
					/>
				</template>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'action'">
						<a @click="draftDetail(record)">编辑</a>
						<a-divider type="vertical" />
						<a-popconfirm title="确定要删除此草稿吗？" @confirm="deleteMyDraft(record)" placement="topRight">
							<a-button type="link" danger size="small">删除</a-button>
						</a-popconfirm>
					</template>
				</template>
			</s-table>
		</div>
	</a-card>
	<startProcess ref="startProcessRef" @successful="tableRef.refresh(true)" />
</template>

<script setup name="flwNewTask">
	import processMyApi from '@/api/flw/processMyApi'
	import ProcessCard from '../../process/processCard.vue'
	import StartProcess from './startProcess.vue'
	import { Empty } from 'ant-design-vue'
	import tool from '@/utils/tool'
	const cardLoading = ref(true)
	const cardActiveKey = ref('newProcessTask')
	const tabListTitle = ref([
		{ key: 'newProcessTask', tab: '发起流程' },
		{ key: 'myDraft', tab: '我的草稿' }
	])
	// 默认展开的
	const activeKey = ref([])
	// 我的流程列表
	const myModelList = ref([])
	const startProcessRef = ref()
	const searchFormState = ref({})
	const searchFormRef = ref()
	const tableRef = ref()
	// 切换卡片
	const onTabChange = (value) => {
		cardActiveKey.value = value
	}
	const selectedRowKeys = ref([])
	const toolConfig = { refresh: true, height: true, columnSetting: false, striped: false }
	const columns = [
		{
			title: '流程名称',
			dataIndex: 'title',
			ellipsis: true
		},
		{
			title: '创建时间',
			dataIndex: 'createTime',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			align: 'center',
			width: 120
		}
	]
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
		return processMyApi.processMyDraftPage(Object.assign(parameter, searchFormState.value)).then((data) => {
			return data
		})
	}
	// 重置
	const reset = () => {
		searchFormRef.value.resetFields()
		tableRef.value.refresh(true)
	}
	// 删除
	const deleteMyDraft = (record) => {
		let params = [
			{
				id: record.id
			}
		]
		processMyApi.processDeleteDraft(params).then(() => {
			tableRef.value.refresh(true)
		})
	}
	// 批量删除
	const revokeBatchTaskMyDraft = (value) => {
		processMyApi.processDeleteDraft(value).then(() => {
			tableRef.value.clearRefreshSelected()
		})
	}
	// 查询详情
	const draftDetail = (record) => {
		// 获取详情
		const param = {
			id: record.id
		}
		processMyApi.processDraftDetail(param).then((data) => {
			// 这里设置打开窗口编辑草稿的id为模型id，因为要跟新发起一样
			// data.id = data.modelId
			startProcessRef.value.onOpen(data, 'DRAFT')
		})
	}
	// 流程分类的字典
	const modelCategory = tool.dictTypeList('MODEL_CATEGORY')
	processMyApi.processMyModelList().then((data) => {
		cardLoading.value = false
		myModelList.value = data
		// 加入展开的
		if (data.length > 0) {
			data.forEach((item) => {
				activeKey.value.push(item.category)
			})
		}
	})
</script>
