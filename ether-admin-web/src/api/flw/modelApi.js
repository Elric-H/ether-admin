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

const request = (url, ...arg) => baseRequest(`/flw/model/` + url, ...arg)
/**
 * 模型
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取模型分页
	modelPage(data) {
		return request('page', data, 'get')
	},
	// 获取所有模型列表
	modelAllList(data) {
		return request('allList', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除模型
	modelDelete(data) {
		return request('delete', data)
	},
	// 部署模型
	modelDeploy(data) {
		return request('deploy', data)
	},
	// 获取模型详情
	modelDetail(data) {
		return request('detail', data, 'get')
	},
	// 停用模型
	modelDisable(data) {
		return request('disableModel', data)
	},
	// 启用模型
	modelEnable(data) {
		return request('enableModel', data)
	},
	// 模型降版
	modelDownVersion(data) {
		return request('downVersion', data)
	},
	// 获取组织树选择器
	modelOrgTreeSelector(data) {
		return request('orgTreeSelector', data, 'get')
	},
	// 获取组织列表选择器
	modelOrgListSelector(data) {
		return request('orgListSelector', data, 'get')
	},
	// 获取职位选择器
	modelPositionSelector(data) {
		return request('positionSelector', data, 'get')
	},
	// 获取角色选择器
	modelRoleSelector(data) {
		return request('roleSelector', data, 'get')
	},
	// 获取用户选择器
	modelUserSelector(data) {
		return request('userSelector', data, 'get')
	},
	// 获取执行监听器选择器
	modelExecutionListenerSelector(data) {
		return request('executionListenerSelector', data, 'get')
	},
	// 获取自定义事件执行监听器选择器
	modelExecutionListenerSelectorForCustomEvent(data) {
		return request('executionListenerSelectorForCustomEvent', data, 'get')
	},
	// 获取任务监听器选择器
	modelTaskListenerSelector(data) {
		return request('taskListenerSelector', data, 'get')
	}
}
