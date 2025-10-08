<template>
	<a-modal
		ref="userOrgPosSelectorRef"
		v-model:visible="visible"
		:width="700"
		title="身份选择"
		@cancel="modalClear"
		:footer="null"
	>
		<div class="snowy-orgpos-vis">
			<a-spin :spinning="spinning">
				<a-row :gutter="[15, 10]">
					<a-col :span="12" v-for="orgPos in userPosList">
						<a-badge-ribbon
							:text="orgPos.type === 'primary' ? '主职' : '兼职'"
							:color="orgPos.type === 'primary' ? 'red' : ''"
						>
							<div class="container" @click="modalOk(orgPos)">
								<a-row :gutter="[10, 10]">
									<a-col :span="18">
										<div class="container-div">
											<a-tag :color="orgPos.category === 'COMPANY' ? '#87d068' : '#fcc02e'" class="container-tag">
												<component
													:is="orgPos.category === 'COMPANY' ? 'home-outlined' : 'apartment-outlined'"
													class="container-tag-icon"
												/>
											</a-tag>
											<div>
												<div>
													<span style="font-weight: 700">{{ orgPos.positionName }}</span>
												</div>
												<div>
													<span style="color: rgb(113, 121, 125)">{{ orgPos.orgName }}</span>
												</div>
											</div>
										</div>
									</a-col>
								</a-row>
							</div>
						</a-badge-ribbon>
					</a-col>
				</a-row>
			</a-spin>
		</div>
	</a-modal>
</template>

<script setup name="userPosSelector">
	const emit = defineEmits({ ok: null })
	const visible = ref(false)
	const spinning = ref(false)
	const userPosList = ref()

	const onOpen = (data) => {
		visible.value = true
		userPosList.value = data
	}
	const modalClear = () => {
		visible.value = false
	}
	// 选中某个后，调用事件
	const modalOk = (recrod) => {
		modalClear()
		emit('ok', recrod)
	}
	defineExpose({
		onOpen
	})
</script>

<style scoped>
	.ant-col-12 {
		flex: 0 0 49% !important;
	}
	.snowy-orgpos-vis {
		margin-bottom: 20px;
		min-height: 100px;
		max-height: 450px;
		overflow: auto;
	}
	.container {
		border: 1px solid var(--border-color-split);
		border-radius: 5px;
		padding: 10px;
		cursor: pointer;
		/*实现渐变（时间变化效果）*/
		-webkit-transition: all 0.5s;
		-moz-transition: all 0.5s;
		-ms-transition: all 0.5s;
		-o-transition: all 0.5s;
		transition: all 0.5s;
	}
	.container-div {
		display: flex;
		align-items: center;
	}
	.container-tag {
		width: 42px;
		height: 42px;
		border-radius: 2px;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	.container-tag-icon {
		font-size: 20px;
	}
	.container:hover {
		background: var(--border-color-split);
	}
</style>
