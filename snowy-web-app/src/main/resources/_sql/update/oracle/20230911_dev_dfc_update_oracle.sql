-- ----------------------------
-- 2023.09.11动态字段表加状态字段
-- ----------------------------
ALTER TABLE "SNOWY"."DEV_DFC"
    ADD ("STATUS" NVARCHAR2(255));

COMMENT ON COLUMN "SNOWY"."DEV_DFC"."STATUS" IS '状态'