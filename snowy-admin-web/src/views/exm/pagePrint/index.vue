<template>
	<div class="print-example">
		<a-alert message="温馨提示">
			<template #description>
				<div class="">
					更多示例查看
					<a class="ml-2" href="https://power-kxlee.github.io/vue-print-nb/dist/index.html" target="_blank">
						在线示例
					</a>
				</div>
				更多用法请查看
				<a class="ml-2" href="https://github.com/Power-kxLee/vue-print-nb" target="_blank"> vue-print-nb </a>
			</template>
		</a-alert>
		<a-card class="mt-2" title="对当前页面全部进行打印">
			<a-button v-print type="primary">全局打印</a-button>
		</a-card>
		<a-card class="mt-2" title="可以打印页面某部分内容，直接传入对应的唯一标识ID">
			<div id="printMe" style="background: #4b5563; color: white; padding: 10px; margin-bottom: 10px">
				<p>葫芦娃，葫芦娃</p>
				<p>一根藤上七朵花</p>
				<p>小小树藤是我家 啦啦啦啦</p>
				<p>叮当当咚咚当当 是我家</p>
				<p>啦啦啦啦</p>
				<p>...</p>
			</div>
			<a-button v-print="'#printMe'" type="primary">局部打印(快速)</a-button>
		</a-card>
		<a-card class="mt-2" title="控制打印时才显示的内容或者才隐藏的内容">
			<div class="layout-items-center font-5 mb-4" id="print-visible">
				<div class="text-primary-5 print-show">我打印时才显示</div>
				<div class="text-yellow-600 ml-4 print-hide">我打印时才隐藏</div>
			</div>
			<a-button v-print="'#print-visible'" type="primary">打印</a-button>
		</a-card>
		<a-card class="mt-2" title="控制打印页面的样式">
			<div class="text-primary-5 mb-4">下面代码可以调整页面的边距</div>
			<div class="layout-items-center font-5 mb-4" id="print-page">
				<code class="block"> @page { size: auto; margin: 10mm; } </code>
			</div>
			<a-button v-print="'#print-page'" type="primary">打印</a-button>
		</a-card>
	</div>
</template>

<script setup name="Print">
	import vPrint from 'vue3-print-nb'
</script>

<style lang="less" scoped>
	.print-show {
		display: none;
	}
	.print-hide {
	}
</style>
<style lang="less" media="print">
	@media print {
		.print-show {
			display: block !important;
		}
		.print-hide {
			display: none;
		}
	}
	// 控制打印的页面样式
	@page {
		size: auto;
		margin: 10mm;
	}
</style>
