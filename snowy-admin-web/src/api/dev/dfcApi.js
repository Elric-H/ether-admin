import { baseRequest } from '@/utils/request'

const request = (url, ...arg) => baseRequest(`/dev/dfc/` + url, ...arg)

/**
 * 动态字段配置Api接口管理器
 *
 * @author 每天一点
 * @date  2023/08/04 08:18
 **/
export default {
	// 获取动态字段配置分页
	dfcPage(data) {
		return request('page', data, 'get')
	},
	// 获取动态字段配置列表
	dfcList(data) {
		return request('list', data, 'get')
	},
	// 提交动态字段配置表单 edit为true时为编辑，默认为新增
	dfcSubmitForm(data, edit = false) {
		return request(edit ? 'edit' : 'add', data)
	},
	// 删除动态字段配置
	dfcDelete(data) {
		return request('delete', data)
	},
	// 获取动态字段配置详情
	dfcDetail(data) {
		return request('detail', data, 'get')
	},
	// 获取所有数据源信息
	dfcDbsSelector(data) {
		return request('dbsSelector', data, 'get')
	},
	// 根据数据源id获取对应库所有表信息
	dfcTablesByDbsId(data) {
		return request('tablesByDbsId', data, 'get')
	},
	// 获取当前库所有表信息
	dfcTables(data) {
		return request('tables', data, 'get')
	},
	// 获取当前库数据表内所有字段信息
	dfcTableColumns(data) {
		return request('tableColumns', data, 'get')
	},
	// 根据数据源id获取对应库数据表内所有字段信息
	dfcTableColumnsByDbsId(data) {
		return request('tableColumnsByDbsId', data, 'get')
	},
	// 迁移数据
	migrate(data) {
		return request('migrate', data)
	},
}
