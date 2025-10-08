/**
 * 自定义表单导入
 *
 * @author yubaoshan
 * @date 2023-05-11 00:12:22
 */
const modules = import.meta.glob('/src/views/flw/customform/**/**.vue')
const notFound = () => import(/* @vite-ignore */ `/src/components/XnWorkflow/customForm/404.vue`)

// 直接渲染组件
export const loadComponent = (component) => {
	if (component) {
		const link = modules[`/src/views/flw/customform/${component}.vue`]
		return markRaw(defineAsyncComponent(link ? link : notFound))
	} else {
		return markRaw(defineAsyncComponent(notFound))
	}
}

// 给出判断，如果使用的地方取到是404，那么他的下一步就不走了
export const verdictComponent = (component) => {
	if (component) {
		const link = modules[`/src/views/flw/customform/${component}.vue`]
		return !!link
	} else {
		return false
	}
}
