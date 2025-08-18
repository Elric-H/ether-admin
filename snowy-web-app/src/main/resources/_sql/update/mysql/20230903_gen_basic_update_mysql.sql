-- ----------------------------
-- 2023.09.03代码生成表加使用动态字段字段
-- ----------------------------
ALTER TABLE `GEN_BASIC`
ADD COLUMN `DFC_WHETHER` varchar(255) NULL COMMENT '使用动态字段' AFTER `SORT_CODE`;
