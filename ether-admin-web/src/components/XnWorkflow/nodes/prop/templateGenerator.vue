<template>
	<div>
		<a-textarea
			ref="smsInputRef"
			v-model:value="smsContent"
			:auto-size="{ minRows: 3, maxRows: 6 }"
			placeholder="请输入内容"
			@input.native="smsInput"
			@blur="inputBlur"
			@focus="focusHandler"
			@click.native="focusHandler"
			@keydown.up.down.left.right.native="focusHandler"
			@select.native="selectHandler"
		/>
		<a-dropdown>
			<template #overlay>
				<a-menu>
					<a-menu-item
						v-for="fields in fieldInfoListNew"
						:key="fields.value"
						:value="fields.value"
						:disabled="fields.value === 'disabled'"
						@click="insertFields(fields)"
						>{{ fields.label }}</a-menu-item
					>
				</a-menu>
			</template>
			<a-button size="small" type="primary" style="float: right; margin-top: -35px; margin-right: 10px">
				置入字段
				<DownOutlined />
			</a-button>
		</a-dropdown>
	</div>
</template>

<script setup name="TemplateGenerator">
	import config from '@/components/XnWorkflow/nodes/config/config'
	import { cloneDeep } from 'lodash-es'
	const props = defineProps({
		// 表单数据
		fieldInfoList: { type: Array, default: () => [] },
		// v-model数据
		inputValue: { type: String, default: () => '' }
	})
	const emit = defineEmits(['update:inputValue'])
	const smsContent = ref('')
	const inputFocus = ref(null)
	const fieldInfoListNew = ref([])
	const visible = ref(false)
	onMounted(() => {
		fieldInfoListNew.value = [...cloneDeep(config.templateDefaultFields), ...props.fieldInfoList]
		smsContent.value = setInput(props.inputValue)
	})
	// 入数据转换
	const setInput = (value) => {
		fieldInfoListNew.value.forEach((field) => {
			let temp = ''
			if (value.indexOf(field.value) > -1) {
				temp = value.replace(new RegExp(field.value, 'g'), '[' + field.label + ']')
			}
			if (temp !== '') {
				value = temp
			}
		})
		return value
	}
	// 选中置入的
	const insertFields = (fields) => {
		insertStr('[' + fields.label + ']')
	}
	// 插入元素
	const insertStr = (str) => {
		let before = smsContent.value.slice(0, inputFocus.value)
		let after = smsContent.value.slice(inputFocus.value, smsContent.value.length)
		inputFocus.value = inputFocus.value + str.length
		smsContent.value = before + str + after
		emit('update:inputValue', outValue(smsContent.value))
	}
	// 保存光标位置
	const inputBlur = (e) => {
		inputFocus.value = e.target.selectionStart
		visible.value = false
	}
	// 删除元素剩余部分
	const smsInput = (e) => {
		//deleteContentBackward==退格键 deleteContentForward==del键
		if (e.inputType === 'deleteContentBackward' || e.inputType === 'deleteContentForward') {
			let beforeIndex = 0
			let afterIndex = 0
			// 光标位置往前
			for (let i = e.target.selectionStart - 1; i >= 0; i--) {
				if (smsContent.value[i] === '[') {
					beforeIndex = i
					afterIndex = e.target.selectionStart
					break
				}
				if (smsContent.value[i] === ']') {
					break
				}
			}
			// 光标位置往后
			for (let i = e.target.selectionStart; i < smsContent.value.length; i++) {
				if (smsContent.value[i] === ']') {
					afterIndex = i + 1
					beforeIndex = e.target.selectionStart
					break
				}
				if (smsContent.value[i] === '[') {
					break
				}
			}
			if (beforeIndex === 0 && afterIndex === 0) {
				emit('update:inputValue', outValue(smsContent.value))
				return
			}
			let beforeStr = smsContent.value.slice(0, beforeIndex)
			let afterStr = smsContent.value.slice(afterIndex)
			smsContent.value = beforeStr + afterStr
			inputFocus.value = beforeStr.length
			nextTick(() => {
				changeFocus(e.target, inputFocus.value, inputFocus.value)
			})
		}
		emit('update:inputValue', outValue(smsContent.value))
	}
	// 选择元素剩余部分
	const selectHandler = (e) => {
		// 光标开始位置往前
		for (let i = e.target.selectionStart - 1; i >= 0; i--) {
			if (smsContent.value[i] === '[') {
				changeFocus(e.target, i, e.target.selectionEnd)
				break
			}
			if (smsContent.value[i] === ']') {
				break
			}
		}
		// 光标结束位置往后
		for (let i = e.target.selectionEnd; i < smsContent.value.length; i++) {
			if (smsContent.value[i] === ']') {
				changeFocus(e.target, e.target.selectionStart, i + 1)
				break
			}
			if (smsContent.value[i] === '[') {
				break
			}
		}
	}
	// 焦点跳出元素内
	const focusHandler = (e) => {
		setTimeout(() => {
			let selStart = e.target.selectionStart
			let beforeArrLength = smsContent.value.slice(0, selStart).split('[').length
			let afterArrLength = smsContent.value.slice(0, selStart).split(']').length
			// 根据'['和']'生成两个数组 判断数组长度 是否相等 不相等就不成对就移动光标
			if (beforeArrLength !== afterArrLength) {
				let pos = smsContent.value.indexOf(']', selStart) + 1
				if (beforeArrLength > afterArrLength && e.code === 'ArrowLeft') {
					//按下按键左箭头
					pos = smsContent.value.lastIndexOf('[', selStart)
				}
				changeFocus(e.target, pos, pos)
			}
		}, 100)
	}
	// 修改光标位置
	const changeFocus = (target, start, end) => {
		let range,
			el = target
		if (el.setSelectionRange) {
			el.setSelectionRange(start, end)
		} else {
			range = el.createTextRange()
			range.collapse(false)
			range.select()
		}
	}
	// 出口数据转换
	const outValue = (result) => {
		let index = result.indexOf('[')
		let num = 0
		while (index !== -1) {
			num++
			index = result.indexOf('[', index + 1)
		}
		if (num > 0) {
			for (let i = 1; i <= num; i++) {
				let temp = ''
				fieldInfoListNew.value.forEach((field) => {
					if (result.indexOf('[' + field.label + ']') > -1) {
						temp = result.replace('[' + field.label + ']', field.value)
						result = temp
					}
				})
			}
		}
		return result
	}
</script>
<style scoped>
	.insert-list p {
		text-align: center;
	}
	.insert-list div {
		margin: 10px 0;
		display: flex;
		justify-content: space-between;
	}
</style>
