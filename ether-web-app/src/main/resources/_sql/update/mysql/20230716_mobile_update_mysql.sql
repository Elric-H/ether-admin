-- ----------------------------
-- 2023.07.16代码生成表加移动端所属模块字段
-- ----------------------------
ALTER TABLE `GEN_BASIC`
ADD COLUMN `MOBILE_MODULE` varchar(255) NULL COMMENT '移动端所属模块' AFTER `MENU_PID`;



