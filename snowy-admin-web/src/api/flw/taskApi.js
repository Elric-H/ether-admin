/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/flw/task/` + url, ...arg)
/**
 * 待办任务
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取待办任务分页
	taskTodoPage(data) {
		return request('todoPage', data, 'get')
	},
	// 获取已办任务分页
	taskDonePage(data) {
		return request('donePage', data, 'get')
	},
	// 调整申请
	taskAdjust(data) {
		return request('adjust', data)
	},
	// 审批保存
	taskSave(data) {
		return request('save', data)
	},
	// 审批同意
	taskPass(data) {
		return request('pass', data)
	},
	// 审批拒绝
	taskReject(data) {
		return request('reject', data)
	},
	// 审批退回
	taskBack(data) {
		return request('back', data)
	},
	// 任务转办
	taskTurn(data) {
		return request('turn', data)
	},
	// 审批跳转
	taskJump(data) {
		return request('jump', data)
	},
	// 任务加签
	taskAddSign(data) {
		return request('addSign', data)
	},
	// 任务详情
	taskDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取可驳回节点列表
	taskGetCanBackNodeInfoList(data) {
		return request('getCanBackNodeInfoList', data, 'get')
	},
	// 获取可跳转节点列表
	taskGetCanJumpNodeInfoList(data) {
		return request('getCanJumpNodeInfoList', data, 'get')
	},
	// 获取组织树选择器
	taskOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取用户选择器
	taskUserSelector(data) {
		return request('userSelector', data, 'get')
	}
}
