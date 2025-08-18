-- ----------------------------
-- 2023.08.11更新业务字典、百度和高德地图示例，记得授权给你需要的角色
-- ----------------------------
INSERT INTO `SYS_RESOURCE` VALUES ('1689891405367353346', '-1', '0', '业务字典', 'bizDict', NULL, 'MENU', '1548901111999773976', 'MENU', '/biz/dict/index', 'biz/dict/index', 'read-outlined', NULL, 12, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `SYS_RESOURCE` VALUES ('1689892202679377921', '-1', '1689891405367353346', '编辑字典', NULL, 'bizDictEdit', 'BUTTON', NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `SYS_RESOURCE` VALUES ('1689894316554067969', '-1', '1548901111999773328', '高德地图', 'gaodeMap', NULL, 'MENU', '1548901111999770525', 'MENU', '/exm/map/gaodeMap', 'exm/map/gaodeMap', 'environment-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
INSERT INTO `SYS_RESOURCE` VALUES ('1689894577238450177', '-1', '1548901111999773328', '百度地图', 'baiduMap', NULL, 'MENU', '1548901111999770525', 'MENU', '/exm/map/baiduMap', 'exm/map/baiduMap', 'compass-outlined', NULL, 99, NULL, 'NOT_DELETE', NULL, NULL, NULL, NULL);
