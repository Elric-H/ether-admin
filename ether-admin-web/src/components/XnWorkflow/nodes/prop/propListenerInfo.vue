<template>
	<div class="mb-2">
		<span class="left-span-label">配置要执行的监听</span>
	</div>
	<a-button type="primary" size="small" @click="addEditListener" class="mb-2">
		<template #icon>
			<plus-outlined />
		</template>
		新增
	</a-button>
	<a-table
		ref="tableRef"
		:columns="columns"
		:data-source="dataSource"
		:row-key="(record) => record.id"
		bordered
		:expand-row-by-click="true"
		:pagination="false"
		size="small"
	>
		<template #bodyCell="{ column, record }">
			<template v-if="column.dataIndex === 'listenerType'">
				{{ listenerlabel(record.listenerType) }}
			</template>
			<template v-if="column.dataIndex === 'action'">
				<a-popconfirm title="确定要删除此监听吗？" @confirm="deleteListener(record)" placement="topRight">
					<a-button type="link" danger size="small">删除</a-button>
				</a-popconfirm>
			</template>
		</template>
	</a-table>
	<a-modal v-model:visible="modalVisible" title="新增" @ok="handleOk" @cancel="handleCancel">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="监听类型：" name="listenerType">
				<a-select
					v-model:value="formData.listenerType"
					placeholder="请选择类型"
					:options="listenerTypeOptions"
					@change="listenerType"
				/>
			</a-form-item>
			<a-form-item label="JAVA监听器：" name="javaClass">
				<a-select v-model:value="formData.javaClass" placeholder="请选择JAVA监听器" :options="listenerValueArray" />
			</a-form-item>
		</a-form>
	</a-modal>
</template>

<script setup name="PropListenerInfo">
	import { required } from '@/utils/formRules'
	import tool from '@/utils/tool'

	const tableRef = ref()
	const listenerValueArray = ref([])
	const columns = [
		{
			title: '名称',
			dataIndex: 'listenerType',
			width: 150
		},
		{
			title: 'JAVA监听器',
			dataIndex: 'javaClass',
			ellipsis: true
		},
		{
			title: '操作',
			dataIndex: 'action',
			width: 80
		}
	]
	const dataSource = ref([])
	const props = defineProps([
		'listenerValue',
		'defaultListenerList',
		'listenerValueArray',
		'executionListenerSelectorForCustomEventArray',
		'listenerType'
	])
	if (props.listenerValue && props.listenerValue.length > 0) {
		// 转换到列表
		props.listenerValue.forEach((item) => {
			const obj = {
				listenerType: item.key,
				javaClass: item.value,
				id: tool.snowyUuid()
			}
			dataSource.value.push(obj)
		})
	}
	// 加载
	if (props.listenerValueArray) {
		listenerValueArray.value = props.listenerValueArray
	}
	// 新增或编辑
	const addEditListener = () => {
		modalVisible.value = true
	}
	// 删除
	const deleteListener = (record) => {
		// 算了，什么也不说了，留下能用的
		dataSource.value = dataSource.value.filter((item) => item.id !== record.id)
	}
	// 转换监听数据
	const selectedListenerList = () => {
		return dataSource.value.map((item) => {
			return {
				key: item.listenerType,
				value: item.javaClass,
				label: listenerlabel(item.listenerType)
			}
		})
	}
	// 通过key获得中文label
	const listenerlabel = (key) => {
		if (!props.defaultListenerList) {
			return '请重新打开'
		}
		return props.defaultListenerList.find((f) => f.key === key).label
	}
	// 小对话框
	const modalVisible = ref(false)
	const formRef = ref()
	const formData = ref({})
	const formRules = {
		listenerType: [required('请选择监听类型')],
		javaClass: [required('请选择JAVA监听器')]
	}
	const listenerTypeOptions = props.defaultListenerList.map((item) => {
		return {
			label: item.label,
			value: item.key
		}
	})
	// 选择器类型监听
	const listenerType = (value) => {
		// 只有在全局情况下，才会去加载这些自定义的类
		if (props.listenerType === 'global') {
			formData.value.javaClass = undefined
			listenerValueArray.value = []
			// 拒绝、终止、撤回、删除 这些类型的时候，加载自定义的到选择列表
			if (value === 'REJECT' || value === 'CLOSE' || value === 'REVOKE' || value === 'DELETE') {
				if (props.executionListenerSelectorForCustomEventArray) {
					listenerValueArray.value = props.executionListenerSelectorForCustomEventArray
				}
			} else {
				if (props.listenerValueArray) {
					listenerValueArray.value = props.listenerValueArray
				}
			}
		}
	}
	const handleOk = () => {
		formRef.value.validate().then(() => {
			formData.value.id = tool.snowyUuid()
			dataSource.value.push(formData.value)
			handleCancel()
		})
	}
	const handleCancel = () => {
		formData.value = {}
		modalVisible.value = false
	}
	// 抛出方法，让上个界面使用
	defineExpose({
		selectedListenerList
	})
</script>
