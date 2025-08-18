propButtonInfo
====

> 工作流中配置按钮选择器
> eg: 发起人、审批人、通知人 这三个节点使用

该组件由 [俞宝山](https://www.xiaonuo.vip) 封装


### 使用方式

```vue
<template>
	<div>
	   <prop-button-info ref="propButtonInfo" :buttonInfo="selectedButtonInfo" :showButton="startTaskDefaultButtonkey"/>
    </div>
</template>

<script setup>
// 配置的一些数据
import config from '@/components/XnWorkflow/nodes/config/config';

// 这个数据就是下面说的，回显数据，选中的数据对象中show值为true
const selectedButtonInfo = ref(
	[
        {
			key: 'SAVE',
			label: '保存',
			show: false,
		},
		{
			key: 'SUBMIT',
			label: '提交',
			show: false,
		},
		{
			key: 'REVOKE',
			label: '撤回',
			show: false,
		},
		{
			key: 'COMPLETE',
			label: '同意',
			show: false,
		},
		{
			key: 'REJECT',
			label: '驳回',
			show: false,
		},
		{
			key: 'PRINT',
			label: '打印',
			show: false,
		},
    ]
)

// 某个节点调用此组件，不管三七二十一，这个是必须选中，而且不能取消的
const startTaskDefaultButtonkey = ref(
	// 注意：使用静态配置中的数据，必须切断双向绑定，不能因为不切短改变原始数据
	JSON.parse(JSON.stringify(config.button.startTaskDefaultButtonkey))
)

// 可以配置不让配置选择的按钮
const startTaskNoCheckedButtonkey = ref(
	// 注意：使用静态配置中的数据，必须切断双向绑定，不能因为不切短改变原始数据
	JSON.parse(JSON.stringify(config.button.startTaskNoCheckedButtonkey))
)

// 初始化我们调用这个组件的ref名称，Vue3 setup语法糖的写法，是这样的哦！
const propButtonInfo = ref(null);

// 使用的界面调用这个方法，获得组件中选好的数据
const getButtonInfoData = () => {
	console.log('result data :' + JSON.stringify(propButtonInfo.selectedButtonKeyList()))
}

</script>
```

### 事件

| 名称                   | 说明                                          | 类型   | 默认值 |
| --------------------- | ----------------------------------- | ------ | ------ |
| selectedButtonKeyList | 调用界面使用此方法获取组件中选中的数据 | Array | []      |

### 数据

| 名称       | 说明                                                           | 类型   | 默认值 |
| ---------- | ------------------------------------------------------------------------------------ | ------ | ------ |
| buttonInfo | 传送数据为此模型节点中选中的按钮用show=true标识，示例：`selectedButtonInfo`         | Array | []      |
| showButton | 哪个节点调用，哪个节点设置他必选的，不能改变的按钮数据，示例：`startTaskDefaultButtonkey ` | Array | []      |
| noChecked | 默认不让哪个按钮在本节点进行配置，示例：`startTaskNoCheckedButtonkey ` | Array | []      |
