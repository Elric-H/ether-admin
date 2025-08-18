<template>
	<a-card :bordered="false">
		<SnowyFormDesign
			ref="formDesign"
			:show-head="false"
			:toolbars="['preview', 'reset', 'importJson', 'exportJson']"
			show-toolbars-text
			:database-config="formTableValue"
			:fields="formDesignFields"
			:dictData="dictData"
		/>
	</a-card>
</template>

<script setup name="formDesign">
	import tool from '@/utils/tool'
	import modelApi from '@/api/flw/modelApi'
	import SnowyFormDesignCom from 'snowy-form-design'
	const dictData = tool.data.get('DICT_TYPE_TREE_DATA')
	const token = tool.data.get('TOKEN')
	const apiUrl = tool.data.get('SNOWY_SYS_BASE_CONFIG').SNOWY_SYS_API_URL + '/dev/file/uploadDynamicReturnUrl'

	const formDesign = ref()
	const formTableValue = ref([])
	// 表单设计器可以拖的组件
	const formDesignFields = [
		'input',
		'textarea',
		'number',
		'select',
		'checkbox',
		'radio',
		'date',
		'time',
		'rate',
		'slider',
		'uploadFile',
		'uploadImg',
		'treeSelect',
		'batch',
		'editor',
		'switch',
		'alert',
		'text',
		'html',
		'divider',
		'card',
		'grid',
		'table',
		'xnUserSelector',
		'xnInput'
	]
	SnowyFormDesignCom.setConfig({
		uploadFile: apiUrl, // 上传文件地址
		uploadImage: apiUrl, //apiUrl, // 上传图片地址
		uploadFileName: 'file', // 上传文件name
		uploadImageName: 'file', // 上传图片name
		uploadFileData: { data: 'snowy' }, // 上传文件额外参数
		uploadImageData: { data: 'snowy' }, // 上传图片额外参数
		uploadFileHeaders: { data: 'snowy' }, // 上传文件请求头部
		getOrgTree: (param) => {
			return modelApi.modelOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		getUserPage: (param) => {
			return modelApi.modelUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	})
	const setValue = (value, tableValue) => {
		formTableValue.value = tableValue
		formDesign.value.handleSetData(value)
	}
	const getValue = () => {
		return formDesign.value.getValue()
	}
	defineExpose({
		getValue,
		setValue
	})
</script>
