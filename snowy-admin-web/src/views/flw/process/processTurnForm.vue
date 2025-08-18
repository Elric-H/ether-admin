<template>
	<xn-form-container title="流程转办" :width="600" :visible="visible" :destroy-on-close="true" @close="onClose">
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-alert message="将把该流程正在进行所有的任务转办给指定用户" type="warning" />
			<a-form-item label="用户：" name="userId" class="mt-3">
				<a-input v-model:value="formData.userId" v-show="false" />
				<a-button type="link" style="padding-left: 0px" @click="openSelector(formData.userId)">选择</a-button>
				<a-tag v-if="formData.userId && formData.userName" color="orange" closable @close="closeUserTag">{{
					formData.userName
				}}</a-tag>
			</a-form-item>
		</a-form>
		<template #footer>
			<a-button style="margin-right: 8px" @click="onClose">关闭</a-button>
			<a-button type="primary" @click="onSubmit">保存</a-button>
		</template>
		<user-selector-plus
			ref="userSelectorPlusRef"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:user-page-api="selectorApiFunction.userPageApi"
			:checked-user-list-api="selectorApiFunction.checkedUserListApi"
			:radio-model="true"
			@onBack="userBack"
		/>
	</xn-form-container>
</template>

<script setup name="taskTurnForm">
	import { required } from '@/utils/formRules'
	import processApi from '@/api/flw/processApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import UserSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const formRef = ref()
	const userSelectorPlusRef = ref()
	// 表单数据
	const formData = ref({})
	const recordData = ref({})

	// 打开抽屉
	const onOpen = (record) => {
		formData.value.id = record.id
		recordData.value = record
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
		recordData.value = {}
		visible.value = false
	}
	// 默认要校验的
	const formRules = {
		userId: [required('请选择人员')]
	}
	// 打开人员选择器，选择人员
	const openSelector = (id) => {
		let checkedUserIds = []
		checkedUserIds.push(id)
		userSelectorPlusRef.value.showUserPlusModal(checkedUserIds)
	}
	// 人员选择器回调
	const userBack = (value) => {
		if (value.length > 0) {
			formData.value.userId = value[0].id
			formData.value.userName = value[0].name
		} else {
			formData.value.userId = ''
		}
	}
	// 通过小标签删除
	const closeUserTag = () => {
		formData.value.userId = ''
		formData.value.userName = ''
	}
	// 验证并提交数据
	const onSubmit = () => {
		formRef.value.validate().then(() => {
			processApi.processTurn(formData.value).then(() => {
				onClose()
				emit('successful')
			})
		})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return processApi.processOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		userPageApi: (param) => {
			return processApi.processUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		checkedUserListApi: (param) => {
			return userCenterApi.userCenterGetUserListByIdList(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	}
	defineExpose({
		onOpen
	})
</script>
