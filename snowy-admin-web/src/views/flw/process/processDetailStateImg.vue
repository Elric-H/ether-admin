<template>
	<img v-if="props.stateCode === 'ACTIVE'" src="/src/assets/images/flw/ACTIVE.png" class="state-img" />
	<img v-if="props.stateCode === 'SUSPENDED'" src="/src/assets/images/flw/SUSPENDED.png" class="state-img" />
	<img v-if="props.stateCode === 'COMPLETED'" src="/src/assets/images/flw/COMPLETED.png" class="state-img" />
	<img v-if="props.stateCode === 'END'" src="/src/assets/images/flw/END.png" class="state-img" />
	<img v-if="props.stateCode === 'REVOKE'" src="/src/assets/images/flw/REVOKE.png" class="state-img" />
	<img v-if="props.stateCode === 'REJECT'" src="/src/assets/images/flw/REJECT.png" class="state-img" />
</template>
<script setup name="processDetailStateImg">
	const props = defineProps({
		stateCode: {
			type: String,
			default: ''
		}
	})
</script>
<style scoped>
	.state-img {
		position: absolute;
		z-index: 10;
		right: 10px;
		opacity: 0.7;
		height: 150px;
	}
</style>
