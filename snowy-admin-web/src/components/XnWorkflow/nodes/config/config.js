export default {
	// 模型结构，就是每个节点，都有的
	nodeModel: {
		// 节点
		node: {
			id: '', // 节点id
			title: '', // 节点名称
			type: '', // 节点类型
			dataLegal: false, // 信息是否完整
			// content:'',      // 内容、备注
			properties: {
				configInfo: {}, // 除条件路由和分合流节点外均有
				conditionInfo: [], // 条件信息，  条件节点特有
				participateInfo: [], // 参与人信息，开始节点、审批节点、抄送节点才有
				buttonInfo: [], // 按钮信息，  开始节点、审批节点、抄送节点才有
				fieldInfo: [], // 字段信息，  开始节点、审批节点、抄送节点才有
				commentList: [], // 流转记录内容
				formInfo: [], // 自定义表单 发起、审批节点都有
				executionListenerInfo: [], // 执行监听器信息，所有节点都有
				taskListenerInfo: [] // 任务监听器信息（审批节点特有）
				// noticeInfo: [],         // 通知信息，  开始节点、审批节点、抄送节点才有
			},
			childNode: {}, // 子节点
			conditionNodeList: [] // 条件子节点
		},
		// 通用按钮默认配置，不需要改动，除非自定义开发的时候，再加什么按钮
		buttonInfo: [
			{
				key: 'SAVE',
				label: '保存',
				value: 'HIDE',
				type: 'default',
				icon: 'save-outlined'
			},
			{
				key: 'SUBMIT',
				label: '提交',
				value: 'HIDE',
				type: 'primary',
				icon: 'check-circle-outlined'
			},
			{
				key: 'PASS',
				label: '同意',
				value: 'HIDE',
				type: 'primary',
				icon: 'check-outlined'
			},
			{
				key: 'REJECT',
				label: '拒绝',
				value: 'HIDE',
				type: 'danger',
				icon: 'close-outlined'
			},
			{
				key: 'BACK',
				label: '退回',
				value: 'HIDE',
				type: 'default',
				icon: 'rollback-outlined'
			},
			{
				key: 'JUMP',
				label: '跳转',
				value: 'HIDE',
				type: 'default',
				icon: 'ungroup-outlined'
			},
			{
				key: 'ADD_SIGN',
				label: '加签',
				value: 'HIDE',
				type: 'default',
				icon: 'tags-outlined'
			},
			{
				key: 'PRINT',
				label: '打印',
				value: 'HIDE',
				type: 'default',
				icon: 'printer-outlined'
			},
			{
				key: 'TURN',
				label: '转办',
				value: 'HIDE',
				type: 'default',
				icon: 'user-switch-outlined'
			}
		],
		fieldInfo: [],
		formInfo: [
			{
				key: 'FORM_URL',
				label: 'PC端表单组件',
				value: ''
			},
			{
				key: 'MOBILE_FORM_URL',
				label: '移动端表单组件',
				value: ''
			}
		],
		executionListenerInfo: [],
		taskListenerInfo: [],
		noticeInfo: []
	},

	// 各个节点的configInfo,他们长的都不一样
	nodeConfigInfo: {
		// 全局配置
		processConfigInfo: {
			// 基础配置
			processSnTemplateId: undefined, // 流水号模板 id
			processPrintTemplateId: undefined, // 打印模板 id
			processTitleTemplate: 'initiator 的 processName - startTime', // 标题模板
			processAbstractTemplate: '', // 摘要模板
			processEnableAutoDistinct: false, // 开启自动去重
			processAutoDistinctType: 'SAMPLE', // 自动去重类型
			processEnableRevoke: true, // 开启审批撤销
			processEnableCommentRequired: false, // 开启意见必填
			// 通知配置
			processEnableBackNotice: false, // 开启退回通知
			processEnableTodoNotice: false, // 开启待办通知
			processEnableCopyNotice: false, // 开启抄送通知
			processEnableCompleteNotice: false, // 开启完成通知
			// 通知的方式
			processBackNoticeChannel: ['MSG'], // 退回通知渠道
			processTodoNoticeChannel: ['MSG'], // 待办通知渠道
			processCopyNoticeChannel: ['MSG'], // 抄送通知渠道
			processCompleteNoticeChannel: ['MSG'], // 完成通知渠道
			// 通知配置对应的模板
			processBackNoticeTemplate: '您于 startTime 发起的 processName 被退回', // 退回通知模板
			processTodoNoticeTemplate: '由 initiator 发起的 processName 需要您审批', // 待办通知模板
			processCopyNoticeTemplate: '您收到一条由 initiator 发起的 processName 的抄送', // 抄送通知模板
			processCompleteNoticeTemplate: '您于 startTime 发起的 processName 审批通过', // 完成通知模板
			// 全局自定义表单
			processStartTaskFormUrl: '', // 全局PC申请单
			processStartTaskMobileFormUrl: '', // 全局移动端申请单
			processUserTaskFormUrl: '', // 全局PC审批单
			processUserTaskMobileFormUrl: '' // 全局移动端审批单
		},
		// 审核节点配置
		userTaskConfigInfo: {
			userTaskType: 'ARTIFICIAL', // 任务节点类型
			userTaskRejectType: 'TO_START', // 审批退回类型
			userTaskMulApproveType: 'SEQUENTIAL', // 多人审批时类型
			userTaskEmptyApproveType: 'AUTO_COMPLETE', // 审批人为空时类型
			userTaskEmptyApproveUser: '', // 审批人为空时转交人id
			userTaskEmptyApproveUserArray: [] // 审批人为空时转交人包含name，用来前端回显，后端不解析
		},
		// 条件中默认的配置
		conditionConfigInfo: {
			priorityLevel: 1 // 优先级 默认1
		},
		// 抄送节点配置
		serviceTaseConfigInfo: {}
	},

	// 按钮相关配置
	button: {
		// 发起人节点 默认选中按钮
		startTaskDefaultButtonkey: ['SAVE', 'SUBMIT'],
		// 发起人节点 默认不让选的按钮 // 这个节点屏蔽下面配置的
		startTaskNoCheckedButtonkey: ['PASS', 'PRINT', 'REJECT', 'BACK', 'ADD_SIGN', 'TURN', 'JUMP'],
		// 审批人节点 默认选中按钮
		userTaskDefaultButtonkey: ['PASS', 'REJECT'],
		// 审批节点 默认不让选的
		userTaskNoCheckedButtonkey: ['SUBMIT', 'SAVE'],
		// 抄送人节点 默认选中按钮
		serviceTaskDefaultButtonkey: ['SUBMIT']
	},
	// 字段相关配置
	field: {
		// 其他节点中字段对象数据模型
		fieldModel: {
			key: '',
			label: '',
			value: 'WRITE', // 默认
			required: false, // 必填
			extJson: '' // 额外扩展，暂无
		},
		// 审批节点中字段对象数据模型
		userTaskFieldModel: {
			key: '',
			label: '',
			value: 'READ', // 默认设为只读
			required: false, // 必填
			extJson: '' // 额外扩展，暂无
		},
		// 字段列表中的字典
		fieldRadioList: [
			{
				label: '可编辑',
				value: 'WRITE'
			},
			{
				label: '只读',
				value: 'READ'
			},
			{
				label: '隐藏',
				value: 'HIDE'
			}
		]
	},
	// 监听器配置，不同的地方不同的选项
	listener: {
		// 全局执行监听可以选择的
		processExecutionListenerInfo: [
			{
				key: 'START',
				label: '开始',
				value: '',
				extJson: ''
			},
			{
				key: 'END',
				label: '完成',
				value: '',
				extJson: ''
			},
			{
				key: 'REJECT',
				label: '拒绝',
				value: '',
				extJson: ''
			},
			{
				key: 'CLOSE',
				label: '终止',
				value: '',
				extJson: ''
			},
			{
				key: 'REVOKE',
				label: '撤回',
				value: '',
				extJson: ''
			},
			{
				key: 'DELETE',
				label: '删除',
				value: '',
				extJson: ''
			}
		],
		// 条件执行监听可以选择的
		exclusiveGatewayExecutionListenerInfo: [
			{
				key: 'TAKE',
				label: '到达',
				value: '',
				extJson: ''
			}
		],
		// 开始节点执行监听可选择的
		startTaskExecutionListenerInfo: [
			{
				key: 'START',
				label: '开始',
				value: '',
				extJson: ''
			},
			{
				key: 'END',
				label: '结束',
				value: '',
				extJson: ''
			}
		],
		// 审批节点执行监听可以选择的
		userTaskExecutionListenerInfo: [
			{
				key: 'START',
				label: '开始',
				value: '',
				extJson: ''
			},
			{
				key: 'END',
				label: '结束',
				value: '',
				extJson: ''
			}
		],
		// 抄送节点执行监听可以选择的
		serviceTaskExecutionListenerInfo: [
			{
				key: 'START',
				label: '开始',
				value: '',
				extJson: ''
			},
			{
				key: 'END',
				label: '结束',
				value: '',
				extJson: ''
			}
		],
		// 审批节点任务监听
		userTaskTaskListenerInfo: [
			{
				key: 'CREATE',
				label: '创建',
				value: '',
				extJson: ''
			},
			{
				key: 'ASSIGNMENT',
				label: '分配',
				value: '',
				extJson: ''
			},
			{
				key: 'COMPLETE',
				label: '完成',
				value: '',
				extJson: ''
			},
			{
				key: 'DELETE',
				label: '删除',
				value: '',
				extJson: ''
			},
			{
				key: 'UPDATE',
				label: '更新',
				value: '',
				extJson: ''
			},
			{
				key: 'TIMEOUT',
				label: '超时',
				value: '',
				extJson: ''
			}
		]
	},
	// 通知方式字典
	noticeInfoList: [
		{
			label: '短信',
			value: 'SMS'
		},
		{
			label: '邮件',
			value: 'EMAIL'
		},
		{
			label: '站内信',
			value: 'MSG'
		}
	],
	// 模板默认自带字段
	templateDefaultFields: [
		{
			label: '发起人',
			value: 'initiator'
		},
		{
			label: '流程名称',
			value: 'processName'
		},
		{
			label: '发起时间',
			value: 'startTime'
		},
		{
			label: '表单字段',
			value: 'disabled' // 这里表示在组件显示的时候就截至了，是下一梭子数组的标题哦
		}
	],
	// 审批节点
	userTaskConfig: {
		// 审批人员类型
		userSelectionTypeList: [
			{
				label: '机构',
				value: 'ORG'
			},
			{
				label: '角色',
				value: 'ROLE'
			},
			{
				label: '职位',
				value: 'POSITION'
			},
			{
				label: '部门主管',
				value: 'ORG_LEADER'
			},
			{
				label: '上级主管',
				value: 'SUPERVISOR'
			},
			{
				label: '表单内的人',
				value: 'FORM_USER'
			},
			{
				label: '表单内的人上级主管',
				value: 'FORM_USER_SUPERVISOR'
			},
			{
				label: '连续多级主管',
				value: 'MUL_LEVEL_SUPERVISOR'
			},
			{
				label: '表单内的人连续多级主管',
				value: 'FORM_USER_MUL_LEVEL_SUPERVISOR'
			},
			{
				label: '用户',
				value: 'USER'
			},
			{
				label: '发起人',
				value: 'INITIATOR'
			}
		],
		// 任务节点类型
		userTaskTypeList: [
			{
				label: '人工审批',
				value: 'ARTIFICIAL'
			},
			{
				label: '自动通过',
				value: 'COMPLETE'
			},
			{
				label: '自动拒绝',
				value: 'REJECT'
			}
		],
		// 审批退回类型
		userTaskRejectTypeList: [
			{
				label: '开始节点',
				value: 'TO_START'
			},
			{
				label: '自选节点',
				value: 'USER_SELECT'
			},
			{
				label: '自动结束',
				value: 'AUTO_END'
			}
		],
		// 多人审批时类型
		userTaskMulApproveTypeList: [
			{
				label: '依次审批',
				value: 'SEQUENTIAL'
			},
			{
				label: '会签（须所有审批人同意）',
				value: 'COUNTERSIGN'
			},
			{
				label: '或签（一名审批人同意或拒绝即可）',
				value: 'ORSIGN'
			}
		],
		// 审批人为空时类型
		userTaskEmptyApproveTypeList: [
			{
				label: '自动通过',
				value: 'AUTO_COMPLETE'
			},
			{
				label: '自动转交给某个人',
				value: 'AUTO_TURN'
			}
		],
		// 主管层级
		levelSupervisorList: [
			{
				label: '最高层主管',
				value: '-1'
			},
			{
				label: '直接主管',
				value: '1'
			},
			{
				label: '第2级主管',
				value: '2'
			},
			{
				label: '第3级主管',
				value: '3'
			},
			{
				label: '第4级主管',
				value: '4'
			},
			{
				label: '第5级主管',
				value: '5'
			},
			{
				label: '第6级主管',
				value: '6'
			},
			{
				label: '第7级主管',
				value: '7'
			},
			{
				label: '第8级主管',
				value: '8'
			},
			{
				label: '第9级主管',
				value: '9'
			},
			{
				label: '第10级主管',
				value: '10'
			}
		]
	},
	// 条件节点
	exclusiveGatewayConfig: {
		operatorList: [
			{
				label: '等于',
				value: '=='
			},
			{
				label: '不等于',
				value: '!='
			},
			{
				label: '大于',
				value: '>'
			},
			{
				label: '大于等于',
				value: '>='
			},
			{
				label: '小于',
				value: '<'
			},
			{
				label: '小于等于',
				value: '<='
			},
			{
				label: '包含',
				value: 'include'
			},
			{
				label: '不包含',
				value: 'notInclude'
			}
		]
	},
	// 抄送节点
	serviceTaskConfig: {
		// 抄送人员类型
		userSelectionTypeList: [
			{
				label: '用户',
				value: 'USER'
			},
			{
				label: '表单内的人',
				value: 'FORM_USER'
			}
		]
	}
}
