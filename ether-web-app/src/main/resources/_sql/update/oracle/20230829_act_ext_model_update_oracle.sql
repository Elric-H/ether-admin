-- ----------------------------
-- 2023.08.29工作流模型表增加移动端图标字段
-- ----------------------------
ALTER TABLE `ACT_EXT_MODEL`
ADD COLUMN `ICON_MOBILE` varchar(255) NULL COMMENT '移动端图标' AFTER `ICON`;
