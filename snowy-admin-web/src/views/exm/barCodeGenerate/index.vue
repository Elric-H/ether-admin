<template>
	<div class="barcode-example">
		<a-alert message="温馨提示" type="info">
			<template #description>
				更多用法请查看
				<a class="ml-2" href="https://github.com/lindell/JsBarcode" target="_blank"> JsBarcode </a>
			</template>
		</a-alert>
		<a-card class="mt-2" title="基础使用">
			<barcode value="Snowy 2.0!"></barcode>
			<barcode value="1234567890"></barcode>
		</a-card>

		<a-card class="mt-2" title="使用配置">
			<barcode value="Snowy 2.0!"></barcode>
			<barcode value="1234567890" :options="{ lineColor: '#007bff' }"></barcode>
		</a-card>
		<a-card class="mt-2" title="自定义标签（img、svg）">
			<barcode value="Snowy 2.0!" tag="img"></barcode>
			<barcode value="1234567890" tag="svg"></barcode>
		</a-card>
	</div>
</template>

<script setup name="barCodeGenerate">
	import Barcode from '@/components/BarCode'
</script>

<style lang="less" scoped>
	.barcode-example img {
		vertical-align: initial;
	}
</style>
