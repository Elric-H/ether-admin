templateGenerator
====

> 模板编辑器
> eg: 主要用于工作流中的各种模板，也可作为通知的

该组件由 [俞宝山](https://www.xiaonuo.vip) 封装


### 使用方式

```vue
<template>
	<div>
	   <template-generator ref="templateGenerator" :fieldInfoLis="fieldInfoLis" v-model:inputValue="inputValue"/>
    </div>
</template>

<script setup>
// 初始化我们调用这个组件的dom名称，Vue3 setup语法糖的写法，是这样的哦！
const templateGenerator = ref(null)

// 这个数据就是下面说的，置入字段下拉中选择的参数
const fieldInfoLis = ref(
	[
		{
			label: '名称',
			value: 'name',
		},
		{
			label: '这有是啥',
			value: 'what',
		}
	]
)

// 组件回显数据
const inputValue = ref('发起人name在几点积分通知了，内容为：what')

// 使用的界面调用这个方法，获得组件中选好的数据
const selectedFieldList = () => {
	console.log('result data :' + JSON.stringify(templateGenerator.getValue()))
}

</script>
```

### 数据

| 名称       | 说明                                                           | 类型   | 默认值 |
| ---------- | ------------------------------------------------------------------------------------ | ------ | ------ |
| fieldInfoLis | 表单数据，下拉框中要选择的，示例：`fieldInfoLis`         | Array | []      |
| inputValue | 组件中要回显的数据，示例：`inputValue ` | String | ''      |
