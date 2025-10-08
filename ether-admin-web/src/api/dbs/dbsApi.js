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

const request = (url, ...arg) => baseRequest(`/dbs/` + url, ...arg)
/**
 * 多租户
 *
 * @author yubaoshan
 * @date 2022-09-22 22:33:20
 */
export default {
	// 获取数据源分页
	dbsPage(data) {
		return request('storage/page', data, 'get')
	},
	// 提交表单 edit为true时为编辑，默认为新增
	submitForm(data, edit = false) {
		return request(edit ? 'storage/edit' : 'storage/add', data)
	},
	// 删除数据源
	dbsDelete(data) {
		return request('storage/delete', data)
	},
	// 获取数据源详情
	dbsDetail(data) {
		return request('storage/detail', data, 'get')
	},
	// 获取数据库中所有表
	dbsTables(data) {
		return request('tables', data, 'get')
	},
	// 获取数据库表中所有字段
	dbsTableColumns(data) {
		return request('tableColumns', data, 'get')
	}
}
