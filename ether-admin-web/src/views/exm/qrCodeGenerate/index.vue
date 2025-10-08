<template>
	<div class="qrcode-example">
		<a-alert message="温馨提示">
			<template #description>
				更多用法请查看
				<a class="ml-2" href="https://fengyuanchen.github.io/vue-qrcode/" target="_blank"> vue-qrcode </a>
			</template>
		</a-alert>

		<a-row :gutter="10">
			<a-col :span="12">
				<a-card class="mt-2" title="基础使用" :bordered="false">
					<vue-qrcode value="https://www.xiaonuo.vip"></vue-qrcode>
					<vue-qrcode value="https://fengyuanchen.github.io/vue-qrcode"></vue-qrcode>
				</a-card>
			</a-col>
			<a-col :span="12">
				<a-card title="使用配置" class="mt-2" :bordered="false">
					<vue-qrcode
						value="Snowy 2.0!"
						:options="{
							width: 148,
							color: {
								dark: '#0074d9',
								light: '#7fdbff'
							}
						}"
					/>
				</a-card>
			</a-col>
		</a-row>

		<a-row :gutter="10">
			<a-col :span="12">
				<a-card title="自定义标签（img、svg）" class="mt-2" :bordered="false">
					<vue-qrcode value="Snowy 2.0!" tag="img"></vue-qrcode>
					<vue-qrcode value="Snowy 2.0!" tag="svg" :options="{ width: 148 }"></vue-qrcode>
				</a-card>
			</a-col>
			<a-col :span="12">
				<a-card title="Logo" class="mt-2" :bordered="false">
					<vue-qrcode
						value="Snowy 2.0!"
						:options="{
							errorCorrectionLevel: 'Q',
							width: 200
						}"
						@ready="onReady"
					></vue-qrcode>
				</a-card>
			</a-col>
		</a-row>
	</div>
</template>

<script setup name="qrCodeGenerate">
	import VueQrcode from '@chenfengyuan/vue-qrcode'

	const value = ref('https://fengyuanchen.github.io/vue-qrcode')
	const options = ref({
		color: {
			dark: '#0074d9',
			light: '#7fdbff'
		}
	})

	const drawImage = (context, image, x, y, width, height, radius = 4) => {
		context.shadowOffsetX = 0
		context.shadowOffsetY = 2
		context.shadowBlur = 4
		context.shadowColor = '#00000040'
		context.lineWidth = 8
		context.beginPath()
		context.moveTo(x + radius, y)
		context.arcTo(x + width, y, x + width, y + height, radius)
		context.arcTo(x + width, y + height, x, y + height, radius)
		context.arcTo(x, y + height, x, y, radius)
		context.arcTo(x, y, x + width, y, radius)
		context.closePath()
		context.strokeStyle = '#fff'
		context.stroke()
		context.clip()
		context.fillStyle = '#fff'
		context.fillRect(x, x, width, height)
		context.drawImage(image, x, x, width, height)
	}

	const onReady = (canvas) => {
		nextTick(() => {
			const context = canvas.getContext('2d')
			const image = new Image()

			image.src =
				'https://pan.xiaonuo.vip/?explorer/share/fileOut&shareID=8XUodP9g&path=%7BshareItemLink%3A8XUodP9g%7D%2F'
			image.crossorigin = 'anonymous'
			image.onload = () => {
				const coverage = 0.15
				const width = Math.round(canvas.width * coverage)
				const x = (canvas.width - width) / 2

				drawImage(context, image, x, x, width, width)
			}
		})
	}
</script>

<style lang="less" scoped>
	.qrcode-example img {
		vertical-align: initial;
	}
</style>
