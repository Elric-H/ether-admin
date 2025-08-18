<template>
	<a-tag v-for="tag in tagList()">{{ tag }}</a-tag
	><!-- 花里胡哨的颜色先去掉，用的时候拿到旁边就行 :color="randomColor()"-->
</template>

<script setup name="propTag">
	// 颜色列表
	const colorList = ['pink', 'red', 'orange', 'green', 'cyan', 'blue', 'purple']
	// 获取随机颜色
	const randomColor = () => {
		return colorList[randomNum(0, colorList.length - 1)]
	}
	// 获取minNum到maxNum内的随机数
	const randomNum = (minNum, maxNum) => {
		switch (arguments.length) {
			case 1:
				return parseInt(Math.random() * minNum + 1, 10)
			case 2:
				return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10)
			default:
				return 0
		}
	}

	const props = defineProps(['tagList'])
	// 将配置的参数转换为数组，显示tag标签
	const tagList = () => {
		const tag = props.tagList
		if (tag === undefined) {
			return []
		} else {
			if (!tag.label) {
				return
			}
			const str = tag.label
			let resultArray = []
			if (str.indexOf(',') !== -1) {
				resultArray = str.split(',')
			} else {
				resultArray.push(str)
			}
			return resultArray
		}
	}
</script>
