import { message } from 'ant-design-vue'

// 根据自定义的此节点定义的，转换表单的隐藏、必填、禁用
export default {
	// 设置字段显示与否
	convSettingsField(formJson, fieldInfo) {
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
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'html')) {
						const obj = fieldInfo.find((i) => i.key === element.model)
						if (obj) {
							element.options.hidden = obj.value === 'HIDE' // ? true : false
							element.options.disabled = obj.value === 'READ' //  ? true : false
						} else {
							message.warning('程序检测到功能字段配置发生了异常，依然能正常使用，请联系管理员进行流程重新配置部署！')
						}
					}
				}
			})
		}
		traverse(formJson.list)
		return formJson
	},
	// 掏出所有字段，返回列表
	getListField(data) {
		let result = []
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
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'batch') & (type !== 'html')) {
						result.push(element)
					}
				}
			})
		}
		traverse(data)
		return result
	},
	// 取节点（用到按钮权限跟字段），并且给节点set一个json，也就是我们的审批记录
	getChildNode(modelJson, activityId, dataList) {
		let result = {}
		let traverse = (obj) => {
			// obj.properties.commentList = []
			if (obj.type === 'exclusiveGateway' || obj.type === 'parallelGateway') {
				// 网关下分2步走
				if (obj.conditionNodeList) {
					obj.conditionNodeList.forEach((item) => {
						traverse(item)
					})
				}
				if (obj.childNode) {
					traverse(obj.childNode)
				}
			} else {
				if (obj.id === activityId) {
					result = obj
				} else {
					// 这里追加记录
					// if (dataList) {
					// 	dataList.forEach((item) => {
					// 		// 给对应的节点
					// 		if (item.activityId === obj.id) {
					// 			obj.properties.commentList.push(item)
					// 		}
					// 	})
					// }
					// 再穿下去
					if (obj.childNode) {
						traverse(obj.childNode)
					}
				}
			}
		}
		// 传入流程的这个
		traverse(modelJson)
		return result
	},
	// 遍历表单，将组件设为禁用
	convFormComponentsDisabled(formJson) {
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
					if ((type !== 'alert') & (type !== 'text') & (type !== 'divider') & (type !== 'html')) {
						element.options.disabled = true
					}
				}
			})
		}
		traverse(formJson.list)
		return formJson
	},
	// 将渲染图形的信息跟审批记录进行融合，每个节点的配置内添加commentList
	coalesceDataListChildNode(modelJson, dataList) {
		//  activityId,
		// let result = {}
		const traverse = (obj) => {
			// obj.properties.commentList = []
			if (obj.type === 'exclusiveGateway' || obj.type === 'parallelGateway') {
				// 网关下分2步走
				if (obj.conditionNodeList) {
					obj.conditionNodeList.forEach((item) => {
						traverse(item)
					})
				}
				if (obj.childNode) {
					traverse(obj.childNode)
				}
			} else {
				if (dataList) {
					dataList.forEach((item) => {
						// 给对应的节点
						if (item.activityId === obj.id) {
							// 增加多个对象
							obj.properties.commentList.push(item)
						}
					})
				}
				// 再穿下去
				if (obj.childNode) {
					traverse(obj.childNode)
				}
			}
		}
		// 传入流程的这个
		traverse(modelJson)
		return modelJson
	}
}
