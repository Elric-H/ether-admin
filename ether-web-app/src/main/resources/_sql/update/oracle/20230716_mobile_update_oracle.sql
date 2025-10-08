-- ----------------------------
-- 2023.07.16代码生成表加移动端所属模块字段
-- ----------------------------
ALTER TABLE "SNOWY"."GEN_BASIC"
    ADD ("MOBILE_MODULE" NVARCHAR2(255));

COMMENT ON COLUMN "SNOWY"."GEN_BASIC"."MOBILE_MODULE" IS '移动端所属模块'