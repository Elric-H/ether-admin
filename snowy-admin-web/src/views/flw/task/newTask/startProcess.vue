<template>
	<xn-form-container
		title="发起流程"
		:width="drawerWidth"
		:visible="visible"
		:destroy-on-close="true"
		:bodyStyle="{ 'padding-top': '0px' }"
		@close="onClose"
	>
		<a-tabs v-model:activeKey="activeKey">
			<a-tab-pane key="flowForm" tab="表单">
				<snowy-form-build
					v-if="recordData.formType === 'DESIGN'"
					ref="formBuildRef"
					:value="jsonData"
					:config="formConfig"
				/>
				<component
					v-else-if="recordData.formType === 'DEFINE'"
					ref="customFormRef"
					:is="customFormsLayouts"
					:value="initiatorDataJson"
				/>
				<div v-else>
					<a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" description="流程表单类型不正确，请联系管理员重置流程。" />
				</div>
			</a-tab-pane>
			<a-tab-pane key="flowChart" tab="流程图">
				<flowChart v-model="modelDesignData" />
			</a-tab-pane>
		</a-tabs>
		<template #footer v-if="activeKey === 'flowForm'">
			<a-spin :spinning="buttonSpinning">
				<span v-for="button in buttonInfo" :key="button.key">
					<a-button
						style="margin-left: 8px"
						:type="button.type"
						:danger="button.type === 'danger'"
						@click="buttonClick(button.key)"
						v-if="button.value === 'SHOW'"
					>
						<template #icon>
							<component :is="button.icon" />
						</template>
						{{ button.label }}
					</a-button>
				</span>
			</a-spin>
		</template>
		<userPosSelector ref="userPosSelectorRef" @ok="userPosChange" />
	</xn-form-container>
</template>

<script setup name="newProcess">
	import { message, Empty } from 'ant-design-vue'
	import { nextTick } from 'vue'
	import tool from '@/utils/tool'
	import processMyApi from '@/api/flw/processMyApi'
	import taskApi from '@/api/flw/taskApi'
	import userCenterApi from '@/api/sys/userCenterApi'
	import FlowChart from '@/components/XnWorkflow/chart/index.vue'
	import UserPosSelector from './userPosSelector.vue'
	import workFlowUtils from '@/components/XnWorkflow/nodes/utils/index'
	import { loadComponent } from '@/components/XnWorkflow/customForm'
	import { isEmpty } from 'lodash-es'
	const customFormRef = ref()
	const customFormsLayouts = ref()

	const token = tool.data.get('TOKEN')
	const formConfig = ref({
		uploadFileHeaders: { token: token }, // 上传文件请求头部
		uploadImageHeaders: { token: token }, // 上传图片请求头部
		getOrgTree: (param) => {
			return taskApi.taskOrgTreeSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		},
		getUserPage: (param) => {
			return taskApi.taskUserSelector(param).then((data) => {
				return Promise.resolve(data)
			})
		}
	})
	// 选中的tab
	const activeKey = ref('flowForm')
	const nodeData = ref()
	// 模型设计器的值
	const modelDesignData = ref({})
	const jsonData = ref([])
	const formBuildRef = ref()
	// 默认是关闭状态
	const visible = ref(false)
	const emit = defineEmits({ successful: null })

	// 职位选择ref定义
	const userPosSelectorRef = ref()
	const formData = ref({})
	const buttonInfo = ref()
	const buttonSpinning = ref(false)
	const recordData = ref()
	const buttonType = ref()
	const openType = ref()
	// json数据
	const initiatorDataJson = ref()
	// 自动获取宽度，默认获取浏览器的宽度的90%
	const drawerWidth = (window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth) * 0.5

	// 打开抽屉
	const onOpen = (record, type) => {
		openType.value = type
		// if (type === 'DRAFT') {
		// 	record.id = record.modelId
		// }
		recordData.value = record
		modelDesignData.value = JSON.parse(record.processJson)
		const thisNode = JSON.parse(record.processJson).childNode.childNode
		nodeData.value = thisNode
		buttonInfo.value = thisNode.properties.buttonInfo
		if (record.formType === 'DESIGN') {
			if (thisNode.properties.fieldInfo.length === 0 || thisNode.properties.buttonInfo.length === 0) {
				message.warning('该节点按钮与字段配置不正确，请联系管理员')
				return
			}
			// 转换数据
			jsonData.value = workFlowUtils.convSettingsField(JSON.parse(record.formJson), thisNode.properties.fieldInfo)
			if (record.dataJson) {
				const formData = Object.values(JSON.parse(record.dataJson))[0]
				nextTick(() => {
					setTimeout(() => {
						// 设置表单参数
						formBuildRef.value.setData(formData)
					}, 200)
				})
			}
		} else if (record.formType === 'DEFINE') {
			if (thisNode.properties.buttonInfo.length === 0) {
				message.warning('该节点按钮配置不正确，请联系管理员')
				return
			}
			if (!thisNode.properties.formInfo) {
				message.warning('该节点未配置表单，请联系管理员')
				return
			}
			const startTaskForm = thisNode.properties.formInfo.find((f) => f.key === 'FORM_URL').value
			customFormsLayouts.value = loadComponent(startTaskForm)
			if (record.dataJson) {
				initiatorDataJson.value = JSON.parse(record.dataJson)
			}
		}
		visible.value = true
	}
	// 关闭抽屉
	const onClose = () => {
		initiatorDataJson.value = {}
		recordData.value = {}
		visible.value = false
	}
	// 点击按钮
	const buttonClick = (key) => {
		buttonType.value = key
		getFormDataAndSubmit()
	}
	// 验证并提交数据
	const getFormDataAndSubmit = () => {
		if (recordData.value.formType === 'DESIGN') {
			formBuildRef.value.getData().then((values) => {
				formData.value = values
				if (buttonType.value === 'SUBMIT') {
					loadPos()
				} else {
					saveDraft()
				}
			})
		} else {
			customFormRef.value.getData().then((values) => {
				formData.value = values
				if (buttonType.value === 'SUBMIT') {
					loadPos()
				} else {
					saveDraft()
				}
			})
		}
	}
	// 加载职位信息，如果只有一个职位，那么我们不让弹窗直接选中
	const loadPos = () => {
		buttonSpinning.value = true
		userCenterApi
			.userLoginPositionInfo()
			.then((data) => {
				if (isEmpty(data)) {
					message.warning('发起失败：未获得职位信息！')
					return
				}
				if (data.length === 1) {
					// 拿到主职位信息，直接发起
					startProcess(data[0])
				} else {
					// 打开职位选择，让其选择以哪个职位发起
					userPosSelectorRef.value.onOpen(data)
				}
			})
			.finally(() => {
				buttonSpinning.value = false
			})
	}
	// 保存草稿
	const saveDraft = () => {
		// 拼接JSON
		const param = {
			title: recordData.value.name,
			dataJson: JSON.stringify(formData.value)
		}
		if (openType.value === 'NEW') {
			param.modelId = recordData.value.id
		}
		// 草稿再保存
		if (openType.value === 'DRAFT') {
			param.modelId = recordData.value.modelId
			param.id = recordData.value.id
		}
		buttonSpinning.valueOf = true
		processMyApi
			.processSaveDraft(param)
			.then(() => {
				if (openType.value === 'DRAFT') {
					// 如果是草稿打开的，我们给其一个回调让刷新去
					emit('successful')
				}
				onClose()
			})
			.finally(() => {
				buttonSpinning.valueOf = false
			})
	}
	// 直接发起
	const startProcess = (record) => {
		try {
			// 这里开始就打loading，以免看着点下去没反应
			buttonSpinning.valueOf = true
			const userInfo = tool.data.get('USER_INFO')
			// 拼接JSON
			const param = {
				modelId: recordData.value.id,
				dataJson: JSON.stringify(formData.value),
				initiator: userInfo.id,
				initiatorName: userInfo.name,
				initiatorOrgId: record.orgId,
				initiatorOrgName: record.orgName,
				initiatorPositionId: record.positionId,
				initiatorPositionName: record.positionName
			}
			// 这里直接换成草稿中的模型id就行
			if (openType.value === 'DRAFT') {
				param.modelId = recordData.value.modelId
			}
			// 调用接口发起流程
			processMyApi
				.processStart(param)
				.then(() => {
					// 判断如果是草稿打开后发起，那么咱们给个请求删了这个草稿
					if (openType.value === 'DRAFT') {
						const deleteParam = [
							{
								id: recordData.value.id
							}
						]
						processMyApi.processDeleteDraft(deleteParam).then(() => {
							onClose()
							emit('successful')
						})
					} else {
						message.success('操作成功')
						onClose()
					}
				})
				.finally(() => {
					buttonSpinning.valueOf = false
				})
		} catch (e) {
			message.warning('发起流程错误：' + e)
			buttonSpinning.valueOf = false
		}
	}
	// 职位选择回调，这里回调后发起流程
	const userPosChange = (record) => {
		// 选好之后，他肯定只能选择一个职位，此时我们直接将其发起
		startProcess(record)
	}
	// 调用这个函数将子组件的一些数据和方法暴露出去
	defineExpose({
		onOpen
	})
</script>
