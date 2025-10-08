-- ----------------------------
-- 2023.09.11动态字段表加状态字段
-- ----------------------------
ALTER TABLE `DEV_DFC`
ADD COLUMN `STATUS` varchar(255) NULL COMMENT '状态' AFTER `IS_MULTIPLE`;