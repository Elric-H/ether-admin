propFieldInfo
====

> 工作流中配置字段选择器
> eg: 发起人、审批人这两个节点使用

该组件由 [俞宝山](https://www.xiaonuo.vip) 封装


### 使用方式

```vue
<template>
	<div>
	   <prop-field-info ref="propFieldInfo" :field-info="fieldInfo" :default-field-model="defaultFieldModel" :form-field-list-value="formFieldListValue"/>
    </div>
</template>

<script setup>

// 这个数据就是下面说的，回显数据，选中的数据对象中show值为true
const fieldInfo = ref(
	{
		"key":"input_1653160767462",
		"label":"姓名",
		"value":"WRITE",
		"required":true,
	},
	{
		"key":"input_1653160770827",
		"label":"绰号",
		"value":"WRITE",
		"required":true,
	}
)

// 这个是表单设计器选择后传过来的
const formFieldListValue = ref(
	[
		{
			"type":"input",
			"label":"姓名",
			"icon":"icon-write",
			"options":{
				"type":"text",
				"width":"100%",
				"defaultValue":"",
				"placeholder":"请输入",
				"clearable":false,
				"maxLength":null,
				"addonBefore":"",
				"addonAfter":"",
				"hidden":false,
				"disabled":false
			},
			"model":"input_1653160767462",
			"key":"input_1653160767462",
			"help":"",
			"rules":[
				{
					"required":true,
					"message":"必填项"
				}
			]
		},
		{
			"type":"input",
			"label":"绰号",
			"icon":"icon-write",
			"options":{
				"type":"text",
				"width":"100%",
				"defaultValue":"",
				"placeholder":"请输入",
				"clearable":false,
				"maxLength":null,
				"addonBefore":"",
				"addonAfter":"",
				"hidden":false,
				"disabled":false
			},
			"model":"input_1653160770827",
			"key":"input_1653160770827",
			"help":"",
			"rules":[
				{
					"required":false,
					"message":"必填项"
				}
			]
		}
	],
)

// 默认的节点选中
const defaultFieldModel = ref({
			key: '',
			label: '',
			value: 'READ', // 默认设为只读
			required: false, // 必填
			extJson: '' // 额外扩展，暂无
		})
		
// 初始化我们调用这个组件的ref名称，Vue3 setup语法糖的写法，是这样的哦！
const propFieldInfo = ref(null);

// 使用的界面调用这个方法，获得组件中选好的数据
const selectedFieldList = () => {
	console.log('result data :' + JSON.stringify(propFieldInfo.selectedFieldList()))
}

</script>
```

### 事件

| 名称                   | 说明                                          | 类型   | 默认值 |
| --------------------- | ----------------------------------- | ------ | ------ |
| selectedFieldList | 调用界面使用此方法获取组件中选中的数据 | Array | []      |

### 数据

| 名称       | 说明                                                           | 类型   | 默认值 |
| ---------- | ------------------------------------------------------------------------------------ | ------ | ------ |
| form-field-list-value | 表单设计器中拖拉拽过后的字段数据，示例：`formFieldListValue`         | Array | []      |
| field-info | 这个节点已经选好勾好的数据，回显的，示例：`fieldInfo ` | Array | []      |
| default-field-model | 默认这个节点勾选的字段配置，示例：`defaultFieldModel ` | Array | []      |
