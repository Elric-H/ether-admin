<template>
	<a-modal
		title="转办"
		:width="800"
		:visible="visible"
		:destroy-on-close="true"
		:footer-style="{ textAlign: 'right' }"
		:mask="false"
		:confirmLoading="confirmLoading"
		@ok="onSubmit"
		@cancel="onClose"
	>
		<a-form ref="formRef" :model="formData" :rules="formRules" layout="vertical">
			<a-form-item label="用户：" name="userId">
				<a-input v-model:value="formData.userId" v-show="false" />
				<a-button type="link" style="padding-left: 0px" @click="openSelector(formData.userId)">选择</a-button>
				<a-tag v-if="formData.userId && formData.userName" color="orange" closable @close="closeUserTag">{{
					formData.userName
				}}</a-tag>
			</a-form-item>

			<a-form-item label="意见：" name="comment">
				<a-textarea
					v-model:value="formData.comment"
					placeholder="请输入转办意见"
					allow-clear
					:auto-size="{ minRows: 3, maxRows: 6 }"
				/>
			</a-form-item>
			<a-form-item label="附件：" name="attachment">
				<XnUpload ref="uploadRef" :uploadNumber="5" />
			</a-form-item>
		</a-form>
		<user-selector-plus
			ref="userSelectorPlus"
			:org-tree-api="selectorApiFunction.orgTreeApi"
			:user-page-api="selectorApiFunction.userPageApi"
			:checked-user-list-api="selectorApiFunction.checkedUserListApi"
			:radio-model="true"
			@onBack="userBack"
		/>
	</a-modal>
</template>

<script setup name="taskTurnForm">
	import { required } from '@/utils/formRules'
	import taskApi from '@/api/flw/taskApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import UserSelectorPlus from '@/components/Selector/userSelectorPlus.vue'
	// 定义emit事件
	const emit = defineEmits({ successful: null })
	// 默认是关闭状态
	const visible = ref(false)
	const confirmLoading = ref(false)
	const formRef = ref()
	const uploadRef = ref()
	const userSelectorPlus = ref()
	// 表单数据
	const formData = ref({})

	// 打开抽屉
	const onOpen = (record, processEnableCommentRequired) => {
		formData.value.id = record.id
		visible.value = true
		if (processEnableCommentRequired) {
			formRules.comment = [required('请输入意见')]
		}
	}
	// 关闭抽屉
	const onClose = () => {
		formRef.value.resetFields()
		formData.value = {}
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
		userSelectorPlus.value.showUserPlusModal(checkedUserIds)
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
			// 验证完主表单，此时获得上传的文件
			const fileList = uploadRef.value.uploadFileList()
			if (fileList) {
				const attachmentList = []
				fileList.forEach((item) => {
					const fileObj = {
						attachmentName: item.name,
						attachmentUrl: item.url
					}
					attachmentList.push(fileObj)
				})
				formData.value.attachmentList = attachmentList
			}
			confirmLoading.value = true
			taskApi
				.taskTurn(formData.value)
				.then(() => {
					onClose()
					emit('successful')
				})
				.finally(() => {
					confirmLoading.value = false
				})
		})
	}
	// 传递设计器需要的API
	const selectorApiFunction = {
		orgTreeApi: (param) => {
			return taskApi.taskOrgTreeSelector(param).then((orgTree) => {
				return Promise.resolve(orgTree)
			})
		},
		userPageApi: (param) => {
			return taskApi.taskUserSelector(param).then((data) => {
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
