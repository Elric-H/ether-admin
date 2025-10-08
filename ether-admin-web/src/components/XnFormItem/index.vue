<template>
	<div>
		<a-form-item
			v-if="['input'].includes(fieldConfig?.type)"
			:label="fieldConfig?.label"
			:name="fieldConfig?.name"
			:required="!!fieldConfig?.required"
		>
			<a-input
				v-model:value="formData[fieldConfig?.name]"
				:placeholder="fieldConfig?.placeholder"
				allow-clear
				v-bind="$attrs"
			/>
		</a-form-item>
		<a-form-item
			v-if="['number'].includes(fieldConfig?.type)"
			:label="fieldConfig?.label"
			:name="fieldConfig?.name"
			:required="!!fieldConfig?.required"
		>
			<a-input-number
				style="width: 100%"
				v-model:value="formData[fieldConfig?.name]"
				:placeholder="fieldConfig?.placeholder"
				allow-clear
				v-bind="$attrs"
			/>
		</a-form-item>
		<a-form-item
			v-if="['textarea'].includes(fieldConfig?.type)"
			:label="fieldConfig?.label"
			:name="fieldConfig?.name"
			:required="!!fieldConfig?.required"
		>
			<a-textarea
				v-model:value="formData[fieldConfig?.name]"
				:placeholder="fieldConfig?.placeholder"
				allow-clear
				:rows="4"
				v-bind="$attrs"
			/>
		</a-form-item>
		<a-form-item
			v-if="['radio'].includes(fieldConfig?.type)"
			:label="fieldConfig?.label"
			:name="fieldConfig?.name"
			:required="!!fieldConfig?.required"
		>
			<a-radio-group v-model:value="formData[fieldConfig?.name]" :options="radioOptions" v-bind="$attrs" />
		</a-form-item>
		<a-form-item
			v-if="['checkbox'].includes(fieldConfig?.type)"
			:label="fieldConfig?.label"
			:name="fieldConfig?.name"
			:required="!!fieldConfig?.required"
		>
			<a-checkbox-group v-model:value="formData[fieldConfig?.name]" :options="checkboxOptions" v-bind="$attrs" />
		</a-form-item>
		<a-form-item
			v-if="['select'].includes(fieldConfig?.type)"
			:label="fieldConfig?.label"
			:name="fieldConfig?.name"
			:required="!!fieldConfig?.required"
		>
			<a-select
				v-model:value="formData[fieldConfig?.name]"
				:placeholder="fieldConfig?.placeholder"
				:options="selectOptions"
				:field-names="
					'API' === fieldConfig?.selectOptionType
						? {
								label: 'name',
								value: 'id'
						  }
						: {
								label: 'label',
								value: 'value'
						  }
				"
				:mode="!!fieldConfig?.isMultiple ? 'multiple' : 'combobox'"
				allow-clear
				v-bind="$attrs"
			/>
		</a-form-item>
		<a-form-item
			v-if="['pageSelect'].includes(fieldConfig?.type)"
			:label="fieldConfig?.label"
			:name="fieldConfig?.name"
			:required="!!fieldConfig?.required"
		>
			<xn-page-select
				:ref="`${fieldConfig?.name}PageSelectRef`"
				v-model:value="formData[fieldConfig?.name]"
				:placeholder="fieldConfig?.placeholder"
				:page-function="selectApiFunction.pageFunction"
				:echo-function="selectApiFunction.echoFunction"
				allow-clear
				v-bind="$attrs"
			/>
		</a-form-item>
	</div>
</template>

<script setup name="XnFormItem">
	import tool from '@/utils/tool'
	import { baseRequest } from '@/utils/request'
	const props = defineProps({
		fieldConfig: {
			type: Object,
			required: true
		},
		formData: {
			type: Object,
			required: true
		}
	})
	const { proxy } = getCurrentInstance()
	const radioOptions = tool.dictList(props.fieldConfig?.dictTypeCode)
	const checkboxOptions = tool.dictList(props.fieldConfig?.dictTypeCode)
	const selectOptions = ref([])
	if ('DICT' === props.fieldConfig?.selectOptionType) {
		selectOptions.value = tool.dictList(props.fieldConfig?.dictTypeCode)
	}
	if (['select'].includes(props.fieldConfig?.type) && 'API' === props.fieldConfig?.selectOptionType) {
		const param = {
			current: -1
		}
		baseRequest(props.fieldConfig?.selOptionApiUrl, param, 'get').then((data) => {
			selectOptions.value = data?.records || []
		})
	}
	// 传递选择组件需要的API
	const selectApiFunction = {
		pageFunction: (param) => {
			return baseRequest(props.fieldConfig?.selOptionApiUrl, param, 'get').then((data) => {
				return Promise.resolve(data)
			})
		},
		echoFunction: (param) => {
			return baseRequest(props.fieldConfig?.selDataApiUrl, param, 'post').then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	nextTick(() => {
		if (['pageSelect'].includes(props.fieldConfig?.type)) {
			proxy.$refs[`${props.fieldConfig?.name}PageSelectRef`].onPage()
		}
	})
</script>
