<template>
	<a-tabs v-model:activeKey="activeKey" tabPosition="left">
		<a-tab-pane key="timeline" tab="时间轴">
			<a-timeline class="pt-5" v-if="props.commentList">
				<a-timeline-item :color="getColor(comment.operateType)" :key="comment.id" v-for="comment in props.commentList">
					<span class="timeline-comment-taskname">{{ comment.taskName }}</span>
					<span class="timeline-comment-datetime" style="padding-left: 10px">{{ comment.approveTime }}</span>
					<div class="mt-2 mb-2">
						<user-outlined class="timeline-icon" />{{ comment.userName }}
						<a-tag :color="getColor(comment.operateType)">{{ comment.operateText }}</a-tag>
					</div>
					<div class="timeline-comment-div mb-2">
						<message-outlined class="timeline-icon mb-2" /><span class="timeline-comment-font">{{
							comment.comment
						}}</span>
					</div>
					<span v-if="comment.attachmentList.length > 0" class="mb-2">
						<file-outlined class="timeline-icon" />附件：<br />
						<a
							v-for="attachment in comment.attachmentList"
							@click="previewFile(attachment, comment.attachmentList)"
							:key="attachment.attachmentName"
							>{{ attachment.attachmentName }}
							<a-divider type="vertical" />
						</a>
					</span>
				</a-timeline-item>
			</a-timeline>
			<div v-else>
				<a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" />
			</div>
		</a-tab-pane>
		<a-tab-pane key="table" tab="表格" force-render>
			<a-table
				class="mt-4"
				size="middle"
				:columns="columns"
				:data-source="loadData"
				bordered
				:row-key="(record) => record.taskId"
			>
				<template #bodyCell="{ column, record }">
					<template v-if="column.dataIndex === 'operateText'">
						<a-tag :color="getColor(record.operateType)">{{ record.operateText }}</a-tag>
					</template>
					<template v-if="column.dataIndex === 'attachmentList'">
						<a-popover title="附件列表" v-if="record.attachmentList.length > 0">
							<template #content>
								<a
									v-for="attachment in record.attachmentList"
									@click="previewFile(attachment, record.attachmentList)"
									:key="attachment.attachmentName"
									><p><file-outlined />{{ attachment.attachmentName }}</p></a
								>
							</template>
							<a-badge :count="record.attachmentList.length"> <file-outlined />附件</a-badge>
						</a-popover>
						<span v-else>无</span>
					</template>
				</template>
			</a-table>
		</a-tab-pane>
	</a-tabs>
	<timeline-form-file-preview ref="timelineFormFilePreviewRef" />
</template>

<script setup name="timelineForm">
	import { Empty } from 'ant-design-vue'
	import TimelineFormFilePreview from './timelineFormFilePreview.vue'

	const activeKey = ref('timeline')
	const timelineFormFilePreviewRef = ref()
	const props = defineProps({
		commentList: {
			type: Array,
			// eslint-disable-next-line vue/require-valid-default-prop
			default: [],
			required: true
		}
	})
	const loadData = props.commentList
	const columns = [
		{
			title: '节点名称',
			dataIndex: 'taskName',
			ellipsis: true
		},
		{
			title: '操作人',
			dataIndex: 'userName',
			ellipsis: true
		},
		{
			title: '操作类型',
			dataIndex: 'operateText',
			align: 'center',
			ellipsis: true
		},
		{
			title: '意见',
			dataIndex: 'comment',
			ellipsis: true
		},
		{
			title: '附件',
			dataIndex: 'attachmentList',
			width: 80
		},
		{
			title: '操作时间',
			dataIndex: 'approveTime',
			width: 160
		}
	]
	// 预览文件
	const previewFile = (attachment, attachmentArray) => {
		const fileName = attachment.attachmentName
		const fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1)
		const param = {
			downloadPath: attachment.attachmentUrl,
			suffix: fileExtension
		}
		timelineFormFilePreviewRef.value.onOpen(param, attachmentArray)
	}
	// 获取颜色
	const getColor = (value) => {
		if (value === 'START') {
			return '#1890FF'
		} else if (value === 'PASS') {
			return '#52C41A'
		} else if (value === 'REJECT') {
			return '#FF4D4F'
		} else if (value === 'BACK') {
			return '#BFBFBF'
		} else if (value === 'REVOKE') {
			return '#BFBFBF'
		} else if (value === 'JUMP') {
			return '#BFBFBF'
		} else if (value === 'TURN') {
			return '#BFBFBF'
		} else if (value === 'END') {
			return '#FF4D4F'
		} else {
			return '#1890FF'
		}
	}
</script>
<style scoped>
	.timeline-comment-taskname {
		font-weight: 700;
	}
	.timeline-icon {
		margin-right: 5px;
	}
	.timeline-comment-div {
		border: 1px solid rgb(244 244 245);
		border-radius: 5px;
		background: rgb(244 244 245);
	}
	.timeline-comment-font {
		font-size: 12px;
	}
	.timeline-comment-datetime {
		color: rgb(188 189 190);
	}
</style>
