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

const request = (url, ...arg) => baseRequest(`/flw/process/monitor/` + url, ...arg)
/**
 * 流程
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取所有流程分页
	processMonitorPage(data) {
		return request('monitorPage', data, 'get')
	},
	// 删除流程
	processDelete(data) {
		return request('delete', data)
	},
	// 终止流程
	processEnd(data) {
		return request('end', data)
	},
	// 撤回流程
	processRevoke(data) {
		return request('revoke', data)
	},
	// 挂起流程
	processSuspend(data) {
		return request('suspend', data)
	},
	// 激活流程
	processActive(data) {
		return request('active', data)
	},
	// 转办流程
	processTurn(data) {
		return request('turn', data)
	},
	// 跳转流程
	processJump(data) {
		return request('jump', data)
	},
	// 复活流程
	processRestart(data) {
		return request('restart', data)
	},
	// 迁移流程
	processMigrate(data) {
		return request('migrate', data)
	},
	// 获取流程变量分页
	processVariablePage(data) {
		return request('variablePage', data, 'get')
	},
	// 批量编辑流程变量
	processVariableUpdateBatch(data) {
		return request('variableUpdateBatch', data)
	},
	// 获取流程详情
	processDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取可跳转节点列表
	processGetCanJumpNodeInfoList(data) {
		return request('getCanJumpNodeInfoList', data, 'get')
	},
	// 获取可复活到节点列表
	processGetCanRestartNodeInfoList(data) {
		return request('getCanRestartNodeInfoList', data, 'get')
	},
	// 获取可迁移到节点列表
	processGetCanMigrateNodeInfoList(data) {
		return request('getCanMigrateNodeInfoList', data, 'get')
	},
	// 获取组织树选择器
	processOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取用户选择器
	processUserSelector(data) {
		return request('userSelector', data, 'get')
	}
}
