-- ----------------------------
-- 2023.01.27新增报表的相关字典、菜单
-- ----------------------------
-- ----------------------------
-- Records of DEV_DICT
-- ----------------------------
INSERT INTO "SNOWY"."DEV_DICT" VALUES ('1616096848417099778', '-1', '0', '报表内容分类', 'URP_CATEGORY', 'FRM', '81', NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO "SNOWY"."DEV_DICT" VALUES ('1616096961445203970', '-1', '1616096848417099778', '技术类', 'TECH', 'FRM', '82', NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO "SNOWY"."DEV_DICT" VALUES ('1618973431700992002', '-1', '1616096848417099778', '商务类', 'BUS', 'FRM', '83', NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO "SNOWY"."DEV_DICT" VALUES ('1618977132125708290', '-1', '1616096848417099778', '其他类', 'OTHER', 'FRM', '84', NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of SYS_RELATION
-- ----------------------------
INSERT INTO "SNOWY"."SYS_RELATION" VALUES ('1616096210090168361', '-1', '1570687866138206208', '1616093737749286914', 'SYS_ROLE_HAS_RESOURCE', '{"menuId":"1616093737749286914","buttonInfo":[]}');

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
INSERT INTO "SNOWY"."SYS_RESOURCE" VALUES ('1616093440759009282', '-1', '0', '报表插件', NULL, NULL, 'MENU', '1548901111999770525', 'CATALOG', '/7021876005451075584', NULL, 'exception-outlined', NULL, '99', NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO "SNOWY"."SYS_RESOURCE" VALUES ('1616093737749286914', '-1', '1616093440759009282', '报表管理', 'urpIndex', NULL, 'MENU', '1548901111999770525', 'MENU', '/urp', 'urp/index', 'file-sync-outlined', NULL, '99', NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
