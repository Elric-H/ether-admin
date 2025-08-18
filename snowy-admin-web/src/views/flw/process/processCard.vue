<template>
	<div class="container">
		<a-tag :color="props.color" class="container-tag">
			<component :is="props.icon" class="container-tag-icon" />
		</a-tag>
		<span class="container-span">{{ props.label }}</span>
	</div>
</template>

<script setup name="processCard">
	const props = defineProps({
		icon: {
			type: String,
			default: () => 'gateway-outlined'
		},
		label: {
			type: String,
			default: () => ''
		},
		color: {
			type: String,
			default: () => ''
		}
	})
</script>

<style scoped>
	.container {
		height: 60px;
		/*border:1px solid var(--border-color-split);*/
		border-radius: 5px;
		display: flex;
		align-items: center;
		cursor: pointer;
		/*实现渐变（时间变化效果）*/
		-webkit-transition: all 0.5s;
		-moz-transition: all 0.5s;
		-ms-transition: all 0.5s;
		-o-transition: all 0.5s;
		transition: all 0.5s;
	}
	.container:hover {
		background: var(--border-color-split);
	}
	.container-tag {
		width: 42px;
		height: 42px;
		border-radius: 2px;
		display: flex;
		align-items: center;
		margin-left: 10px;
	}
	.container-tag-icon {
		font-size: 25px;
	}
	.container-span {
		max-width: 60%;
		font-weight: 500;
	}
</style>
