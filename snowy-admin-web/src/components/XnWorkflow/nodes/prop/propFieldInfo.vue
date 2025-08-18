<template>
	<div class="mb-2">
		<span class="left-span-label">参与者可以看见或操作哪些字段</span>
	</div>
	<a-table
		ref="tableRef"
		:columns="columns"
		:data-source="dataSource"
		:row-key="(record) => record.key"
		:expand-row-by-click="true"
		:pagination="false"
		:show-header="false"
		size="small"
	>
		<template #bodyCell="{ column, record }">
			<template v-if="column.dataIndex === 'label'">
				<span v-if="record.required" style="color: red">* </span>
				<span v-else>&nbsp;</span>
				{{ record.label }}
			</template>
			<template v-if="column.dataIndex === 'value'">
				<a-radio-group v-model:value="record.value" :disabled="record.disabled">
					<a-radio :key="radio.value" v-for="radio in fieldRadioList" :value="radio.value">{{ radio.label }}</a-radio>
				</a-radio-group>
			</template>
		</template>
	</a-table>
</template>

<script setup name="propFieldInfo">
	import config from '@/components/XnWorkflow/nodes/config/config'
	import { cloneDeep } from 'lodash-es'
	const columns = [
		{
			title: '表单字段',
			dataIndex: 'label',
			width: 150
		},
		{
			title: '',
			dataIndex: 'value'
		}
	]
	// 定义一个表单的已拖进去的组件列表，也就是我们要的字段列表
	const listField = ref([])

	const fieldRadioList = cloneDeep(config.field.fieldRadioList)

	// 接受到值
	const props = defineProps(['formFieldListValue', 'fieldInfo', 'defaultFieldModel'])

	const dataSource = ref([])

	const getListField = () => {
		listField.value = []
		// 递归遍历控件树
		const traverse = (array) => {
			array.forEach((element) => {
				if (element.type === 'grid' || element.type === 'tabs') {
					// 栅格布局 and 标签页
					element.columns.forEach((item) => {
						traverse(item.list)
					})
				} else if (element.type === 'card') {
					// 卡片布局 and  动态表格
					traverse(element.list)
				} else if (element.type === 'table') {
					// 表格布局
					element.trs.forEach((item) => {
						item.tds.forEach((val) => {
							traverse(val.list)
						})
					})
				} else {
					const type = element.type
					// 排除一些
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'html')) {
						//  & (type !== 'batch')
						listField.value.push(element)
					}
				}
			})
		}
		traverse(props.formFieldListValue)
	}

	if (props.formFieldListValue.length > 0) {
		getListField()
		// 数据转换至模型中
		listField.value.forEach((item) => {
			// 创建数据模型
			let dataModel = {}
			// 如果调用此组件的节点没配置它默认的选中按钮，那么就用通用的
			if (props.defaultFieldModel) {
				dataModel = JSON.parse(JSON.stringify(props.defaultFieldModel))
			} else {
				dataModel = JSON.parse(JSON.stringify(config.field.fieldModel))
			}
			dataModel.key = item.model
			// 假如有选中的值，我们将其回显
			if (props.fieldInfo.length > 0) {
				props.fieldInfo.forEach((field) => {
					if (field.key === item.model) {
						dataModel.value = field.value
					}
				})
			}
			if (item.rules) {
				dataModel.required = item.rules[0].required
			} else {
				dataModel.required = false
			}
			// 判断有没有从表单那边勾选了隐藏跟禁用
			if (item.options.hidden || item.options.disabled) {
				dataModel.disabled = true
				// 并且设置它选中的选项为隐藏活禁用
				if (item.options.disabled) {
					dataModel.value = 'READ' // 只读
				}
				// 如果设置了隐藏跟禁用，我们按隐藏来算
				if (item.options.hidden) {
					dataModel.value = 'HIDE' // 隐藏
				}
			} else {
				dataModel.disabled = false
			}
			dataModel.label = item.label
			dataSource.value.push(dataModel)
		})
	}
	// 父界面需要调用获取参数
	const selectedFieldList = () => {
		return cloneDeep(dataSource.value)
	}
	// 抛出方法，让上个界面使用
	defineExpose({
		selectedFieldList
	})
</script>
