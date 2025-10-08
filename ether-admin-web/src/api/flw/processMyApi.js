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

const request = (url, ...arg) => baseRequest(`/flw/process/` + url, ...arg)
/**
 * 我的流程
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取我可以发起的流程模型列表
	processMyModelList(data) {
		return request('myModelList', data, 'get')
	},
	// 保存草稿
	processSaveDraft(data) {
		return request('saveDraft', data)
	},
	// 发起流程
	processStart(data) {
		return request('start', data)
	},
	// 获取我的草稿分页
	processMyDraftPage(data) {
		return request('myDraftPage', data, 'get')
	},
	// 获取草稿详情
	processDraftDetail(data) {
		return request('draftDetail', data, 'get')
	},
	// 删除草稿
	processDeleteDraft(data) {
		return request('deleteDraft', data)
	},
	// 获取我发起的流程分页
	processMyPage(data) {
		return request('myPage', data, 'get')
	},
	// 获取我的待阅流程分页
	processMyCopyUnreadPage(data) {
		return request('myCopyUnreadPage', data, 'get')
	},
	// 设置待阅流程为已阅
	processReadMyCopyProcess(data) {
		return request('readMyCopyProcess', data)
	},
	// 获取我的已阅流程分页
	processMyCopyHasReadPage(data) {
		return request('myCopyHasReadPage', data, 'get')
	},
	// 删除我的已阅流程
	processDeleteMyHasReadProcess(data) {
		return request('deleteMyHasReadProcess', data)
	},
	// 撤回流程
	processRevoke(data) {
		return request('revoke', data)
	},
	// 获取流程详情
	processDetail(data) {
		return request('detail', data, 'get')
	}
}
