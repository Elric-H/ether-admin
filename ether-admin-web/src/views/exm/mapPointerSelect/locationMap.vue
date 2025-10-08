<template>
	<a-card title="位置标记地图">
		<div ref="mapRef" class="location-map"></div>
	</a-card>
</template>

<script setup name="LocationMap">
	import AMapLoader from '@amap/amap-jsapi-loader'
	window._AMapSecurityConfig = {
		securityJsCode: '45e64ec59e9253440c6520e12bbe6b01'
	}

	const mapRef = ref(null)

	let locationMap

	const renderLocationMap = () => {
		AMapLoader.load({
			key: '40b4f025a29ff254543286fb7f87ab54',
			version: '2.0',
			plugins: ['AMap.InfoWindow', 'AMap.Marker']
		})
			.then((AMap) => {
				const option = {
					zoom: 13,
					center: [87.573452, 43.8414 + 0.005] // 初始中心点
				}
				locationMap = new AMap.Map(mapRef.value, option)
				const infoWindow = new AMap.InfoWindow({
					content: `
	           <div style="color: #333;">
	             <div style="padding: 5px;font-size: 16px;">小诺信息科技有限公司</div>
	             <div style="padding: 0 5px;">地址: 新疆乌鲁木齐市***号</div>
	             <div style="padding: 0 5px;">电话: 150****0051</div>
	           </div>
	           <a
	             class="ele-text-primary"
	             style="padding: 8px 5px 0;text-decoration: none;display: inline-block;"
	             href="//uri.amap.com/marker?position=87.573452,43.8414&name=小诺信息科技有限公司"
	             target="_blank">到这里去→
	           </a>
	         `
				})
				infoWindow.open(locationMap, [87.573452, 43.8414])
				const icon = new AMap.Icon({
					size: new AMap.Size(25, 34),
					image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png',
					imageSize: new AMap.Size(25, 34)
				})
				const marker = new AMap.Marker({
					icon: icon,
					position: [114.346084, 30.511215],
					offset: new AMap.Pixel(-12, -28)
				})
				marker.setMap(locationMap)
				marker.on('click', () => {
					infoWindow.open(locationMap)
				})
			})
			.catch((e) => {
				console.error(e)
			})
	}

	/* 渲染地图 */
	onMounted(() => {
		renderLocationMap()
	})

	/* 销毁地图 */
	onUnmounted(() => {
		if (locationMap) {
			locationMap.destroy()
		}
	})
</script>

<style lang="less" scoped>
	.location-map {
		height: 420px;
	}
</style>
