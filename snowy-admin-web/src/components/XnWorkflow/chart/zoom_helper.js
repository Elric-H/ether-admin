const defaultZoomMargin = 360
const perZoom = 0.25 // 每次放大缩小0.25倍

const ZoomHelper = {
	// 处理全屏放大的样式
	getZoomStyles(zoom, MinZoom = 1, ZoomMargin = defaultZoomMargin, rightSpace = 0) {
		const width = document.querySelector('.workflow-design').clientWidth
		let style = {}
		// 兼容Firefox浏览器的放大缩小
		if (zoom !== MinZoom) {
			style = {
				transform: `scale(${zoom})`,
				'transform-origin': '0 0',
				width: (width - ZoomMargin - rightSpace) / zoom + 'px'
			}
		}
		return style
	},
	// 获取最大的放大倍数
	getMaxZoom() {
		const width = window.innerWidth
		const mediumWidth = 1600
		const smallScreenScale = 2.5 // 小屏幕下附件放大3倍会有样式问题, 所以取2.5
		const bigScreenScale = 3 // 大于1600的最大倍数为3
		return width > mediumWidth ? bigScreenScale : smallScreenScale
	},
	// 获取点击放大缩小之后生成的最终的放大倍数和样式
	getZoomData(zoomIn, zoom) {
		const zoomResult = zoomIn ? zoom + perZoom : zoom - perZoom // 放大倍数加一次或者减少一次
		return {
			style: this.getZoomStyles(zoomResult),
			zoom: zoomResult
		}
	}
}

export default ZoomHelper
