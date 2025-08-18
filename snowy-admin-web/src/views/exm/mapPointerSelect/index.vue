<template>
	<div class="map-point-example">
		<a-alert message="温馨提示" type="info">
			<template #description>
				更多用法请查看
				<a class="ml-2" href="https://lbs.amap.com/api/jsapi-v2/summary/" target="_blank"> 高德地图JS API </a>
			</template>
		</a-alert>
		<a-card class="mt-4" title="地图选点">
			<div class="">
				<a-select v-model:value="searchType" class="w-[200px]">
					<a-select-option :value="0">POI检索模式</a-select-option>
					<a-select-option :value="1">关键字检索模式</a-select-option>
				</a-select>
				<a-button class="ml-4" @click="openMapPicker"> 打开地图位置选择器</a-button>
			</div>
			<div class="mt-2">选择位置: {{ result.location }}</div>
			<div class="mt-2">详细地址: {{ result.address }}</div>
			<div class="mt-2">经 纬 度 : {{ result.lngAndLat }}</div>
		</a-card>

		<location-map class="mt-4" />

		<a-modal v-model:visible="visible" width="900px" title="选择位置" :confirmLoading="confirmLoading" @ok="onConfirm">
			<div class="mb-4">
				<a-auto-complete
					v-model:value="keywords"
					:options="suggestionList"
					class="w-[200px]"
					placeholder="请输入关键词"
					@select="onSearchSelect"
					@search="onSearch"
				/>
			</div>
			<div class="flex">
				<div ref="mapRef" class="map-container h-[400px] relative flex-1"></div>
				<a-spin :spinning="poiLoading">
					<div class="poi-list w-[300px] h-[400px] overflow-auto">
						<div
							class="poi-item cursor-pointer layout-items-center"
							:class="{ active: item.selected }"
							v-for="item in data"
							:key="item.key"
							@click="onItemClick(item)"
						>
							<environment-outlined class="text-18 text-gray-500" />
							<div class="ml-4 flex-1">
								<div class="text-14">{{ item.name }}</div>
								<div class="text-12 mt-1 text-gray-500">{{ item.name }}</div>
							</div>
							<check-circle-outlined class="text-24 ml-1 text-primary hidden poi-item-check-icon" />
						</div>
					</div>
				</a-spin>
			</div>
		</a-modal>
	</div>
</template>

<script setup name="mapPointerSelect">
	import AMapLoader from '@amap/amap-jsapi-loader'
	import { EnvironmentOutlined, CheckCircleOutlined } from '@ant-design/icons-vue'
	import LocationMap from './locationMap.vue'
	window._AMapSecurityConfig = {
		securityJsCode: '45e64ec59e9253440c6520e12bbe6b01'
	}

	const mapRef = ref(null)
	const loading = ref(true)
	const poiLoading = ref(false)
	const confirmLoading = ref(false)
	const data = ref([])
	const suggestionList = ref([])
	const keywords = ref('')
	let lastSuggestion = ''
	let selectedSuggestion = null
	let isItemClickMove = false
	let mapIns = null
	let placeSearchIns = null
	let autoCompleteIns = null
	const needCity = true
	// 是否显示地图选择弹窗
	const visible = ref(false)

	// 地点检索类型
	const searchType = ref(0)

	// 选择结果
	const result = reactive({
		location: '',
		address: '',
		lngAndLat: ''
	})

	const renderMap = () => {
		if (mapIns) {
			return
		}
		AMapLoader.load({
			key: '40b4f025a29ff254543286fb7f87ab54',
			version: '2.0',
			plugins: ['AMap.PlaceSearch', 'AMap.AutoComplete']
		})
			.then((AMap) => {
				destroyAll()
				autoCompleteIns = new AMap.AutoComplete({
					city: '全国'
				})
				placeSearchIns = new AMap.PlaceSearch({
					type: '',
					pageSize: 20,
					pageIndex: 1
				})
				mapIns = new AMap.Map(mapRef.value, {
					zoom: 11,
					resizeEnable: true
				})
				mapIns.on('complete', () => {
					loading.value = false
					const { lng, lat } = mapIns.getCenter()
					searchPOI(lng, lat)
				})
				mapIns.on('moveend', () => {
					if (isItemClickMove) {
						isItemClickMove = false
					} else if (searchType === 0) {
						const { lng, lat } = mapIns.getCenter()
						searchPOI(lng, lat)
					}
				})
			})
			.catch((e) => {
				console.error(e)
			})
	}

	/* 打开位置选择 */
	const openMapPicker = () => {
		visible.value = true
		nextTick(() => {
			renderMap()
		})
	}

	const onSearch = (value) => {
		if (!value || lastSuggestion === value) {
			return
		}
		lastSuggestion = value
		if (searchType !== 0) {
			poiLoading.value = true
		}
		searchKeywords(value)
			.then((result) => {
				if (searchType !== 0) {
					data.value = result
					poiLoading.value = false
				} else {
					suggestionList.value = result
				}
			})
			.catch((e) => {
				console.error(e)
				poiLoading.value = false
			})
	}
	const onSearchSelect = (_value, item) => {
		if (!data.value.length || data.value[0].name !== item.name) {
			data.value = [
				{ ...item, selected: true },
				...data.value.map((d) => {
					return {
						...d,
						selected: false
					}
				})
			]
		}
		setMapCenter(item.location.lng, item.location.lat, 17)
		selectedSuggestion = item
	}
	const onItemClick = (item) => {
		isItemClickMove = true
		data.value = data.value.map((d) => {
			return {
				...d,
				selected: d === item
			}
		})
		const { lng, lat } = item.location
		setMapCenter(lng, lat, 17)
	}
	const onConfirm = () => {
		const selected = getSelected()
		if (!selected) {
			confirmLoading.value = true
			getMapCenter(needCity)
				.then((result) => {
					confirmLoading.value = false
					suggestionList.value = result
					onChoose(result)
				})
				.catch((e) => {
					console.error(e)
					confirmLoading.value = false
					onChoose({})
				})
			return
		}
		const location = {
			...selected.location,
			name: selected.name,
			address: selected.address || ''
		}
		if (needCity) {
			confirmLoading.value = true
			setMapCenter(location.lng, location.lat)
			getMapCenter(true)
				.then(({ city }) => {
					location.city = city
				})
				.catch((e) => {
					console.error(e)
				})
				.finally(() => {
					confirmLoading.value = false
					onChoose(location)
				})
		} else {
			onChoose(location)
		}
	}
	const searchKeywords = (value) => {
		return new Promise((resolve, reject) => {
			if (!autoCompleteIns) {
				reject(new Error('AutoComplete instance is null'))
				return
			}
			autoCompleteIns.search(value, (_status, result) => {
				if (!(result == null ? void 0 : result.tips)) {
					resolve([])
					return
				}
				const data2 = result.tips
					.filter((d) => !!d.location)
					.map((d) => {
						const label = `${d.name}(${d.district})`
						return {
							...d,
							label,
							value: label,
							key: d.id || label,
							address: Array.isArray(d.address) ? d.address[0] : d.address
						}
					})
				resolve(data2)
			})
		})
	}
	const searchPOI = (lng, lat) => {
		poiLoading.value = true
		searchNearBy(lng, lat)
			.then((result) => {
				console.log(result)
				if (selectedSuggestion) {
					if (result.length === 0 || result[0].name !== selectedSuggestion.name) {
						data.value = [{ ...selectedSuggestion, selected: true }, ...result]
					} else {
						data.value = result.map((d, i) => {
							return { ...d, selected: i === 0 }
						})
					}
				} else {
					data.value = result
				}
				poiLoading.value = false
			})
			.catch((e) => {
				console.error(e)
				poiLoading.value = false
				data.value = []
			})
	}
	const searchNearBy = (lng, lat) => {
		return new Promise((resolve, reject) => {
			if (!placeSearchIns) {
				reject(new Error('PlaceSearch instance is null'))
				return
			}
			placeSearchIns.searchNearBy('', [lng, lat], 1000, (status, result) => {
				if (status === 'complete' && result.poiList) {
					const data2 = result.poiList.pois
						.filter((d) => !!d.location)
						.map((d, i) => {
							return {
								...d,
								key: d.id || `${d.name}-${i}`
							}
						})
					resolve(data2)
				} else if (status === 'no_data') {
					resolve([])
				} else {
					reject(new Error(status))
				}
			})
		})
	}
	const getSelected = () => {
		return data.value.find((d) => d.selected)
	}

	const setMapCenter = (lng, lat, zoom) => {
		if (mapIns && lng != null && lat != null) {
			if (zoom == null) {
				mapIns.setCenter([lng, lat])
			} else {
				mapIns.setZoomAndCenter(zoom, [lng, lat])
			}
		}
	}
	const getMapCenter = (needCity) => {
		return new Promise((resolve, reject) => {
			if (!mapIns) {
				reject(new Error('map instance is null'))
				return
			}
			const result = mapIns.getCenter()
			if (needCity) {
				mapIns.getCity((city) => {
					result.city = city
					resolve(result)
				})
			} else {
				resolve(result)
			}
		})
	}

	const destroyMap = () => {
		mapIns && mapIns.destroy()
		placeSearchIns = null
		autoCompleteIns = null
		mapIns = null
	}
	const destroyAll = () => {
		destroyMap()
		data.value = []
		suggestionList.value = []
		keywords.value = ''
		lastSuggestion = ''
		selectedSuggestion = null
		isItemClickMove = false
	}

	watch(
		() => searchType,
		() => {
			keywords.value = ''
			suggestionList.value = []
			selectedSuggestion = null
			lastSuggestion = ''
		}
	)

	onMounted(() => {})
	onBeforeUnmount(() => {
		destroyAll()
	})

	/* 地图选择后回调 */
	const onChoose = (location) => {
		console.log(location)
		result.location = `${location.city?.province}/${location.city?.city}/${location.city?.district}`
		result.address = `${location.name} ${location.address}`
		result.lngAndLat = `${location.lng},${location.lat}`
		visible.value = false
	}
</script>

<style lang="less" scoped>
	.map-container {
		background-color: #f1f1f1;
	}

	.poi-list {
		border-top: 1px solid var(--border-color-split);
	}

	.poi-item {
		padding: 10px;
		border-bottom: 1px solid var(--border-color-split);
	}

	.active .poi-item-check-icon {
		display: block;
	}
</style>
