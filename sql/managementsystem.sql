/*
 Navicat Premium Data Transfer

 Source Server         : mysql8.2
 Source Server Type    : MySQL
 Source Server Version : 80200
 Source Host           : localhost:3307
 Source Schema         : managementsystem

 Target Server Type    : MySQL
 Target Server Version : 80200
 File Encoding         : 65001

 Date: 01/12/2024 10:29:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `sys_dictionary`;
CREATE TABLE `sys_dictionary`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dictionary_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `dictionary_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `dictionary_value` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `dictionary_type` tinyint(1) NOT NULL,
  `key_type` int NULL DEFAULT 0 COMMENT 'dictionary_key转化的类型，0:字符串，1:数字，2:布尔型，3:日期，4: 日期时间',
  `value_type` int NULL DEFAULT 0 COMMENT 'dictionary_value转化的类型，和key_type相同定义',
  `service_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `frontend_style` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `parent_id` bigint NOT NULL DEFAULT 0,
  `order_no` int NULL DEFAULT NULL,
  `enable` tinyint(1) NOT NULL DEFAULT 1,
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dictionary
-- ----------------------------
INSERT INTO `sys_dictionary` VALUES (1, '是否的tag显示', 'yes_no_tag', NULL, 0, 0, 0, NULL, NULL, 'string', 0, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (2, '是的tag显示', 'true', 'yes', 0, 2, 0, NULL, NULL, NULL, 1, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (3, '否的tag显示', 'false', 'no', 0, 2, 0, NULL, '{\"type\": \"warning\"}', NULL, 1, 1, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (4, '字典类型', 'dictionary_type', NULL, 0, 0, 0, NULL, NULL, NULL, 0, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (5, '常量字典', '0', 'constant', 0, 1, 0, NULL, NULL, NULL, 4, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (6, 'sql字典', '1', 'sql', 0, 1, 0, NULL, NULL, NULL, 4, 1, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (7, 'api字典', '2', 'Api', 0, 1, 0, NULL, NULL, NULL, 4, 2, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (8, '角色列表', 'role_list', 'select id, id as value, role_name as label, parent_id as parentId from sys_role', 1, 0, 0, 'security-module', NULL, NULL, 0, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (9, '字典列表', 'dictionary_list', 'select id, id as value, dictionary_name as label, parent_id as parentId from sys_dictionary where dictionary_type = 0 order by order_no ', 1, 0, 0, 'basic-module', NULL, NULL, 0, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (10, '数据类型', 'data_type', NULL, 0, 0, 0, NULL, NULL, NULL, 0, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (11, '字符串类型', '0', 'string', 0, 1, 0, NULL, NULL, NULL, 10, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (12, '数字类型', '1', 'number', 0, 1, 0, NULL, NULL, NULL, 10, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (13, '布尔类型', '2', 'boolean', 0, 1, 0, NULL, NULL, NULL, 10, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (14, '日期类型', '3', 'date', 0, 1, 0, NULL, NULL, '格式为YYYY-MM-dd', 10, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (15, '日期时间类型', '4', 'datetime', 0, 1, 0, NULL, NULL, '格式为YYYY-MM-dd HH:mm:ss', 10, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (16, '菜单列表', 'menu_list', 'select id, id as value, label as label, parent_id as parentId from sys_menu order by order_no', 1, 1, 0, 'security-module', NULL, NULL, 0, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (17, '权限列表', 'menu_auth', NULL, 0, NULL, NULL, NULL, NULL, NULL, 0, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (18, '查询权限', 'search', 'search', 0, 0, 0, NULL, NULL, NULL, 17, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (19, '新增权限', 'add', 'add', 0, 0, 0, NULL, NULL, NULL, 17, 1, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (20, '修改权限', 'update', 'update', 0, 0, 0, NULL, NULL, NULL, 17, 2, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (21, '删除权限', 'delete', 'delete', 0, 0, 0, NULL, NULL, NULL, 17, 3, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (22, '导入权限', 'import', 'import', 0, 0, 0, NULL, NULL, NULL, 17, 4, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (23, '导出权限', 'export', 'export', 0, 0, 0, NULL, NULL, NULL, 17, 5, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (24, '日志级别', 'log_level', NULL, 0, 0, NULL, NULL, NULL, NULL, 0, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (25, '追踪日志', '0', 'Trace', 0, 1, 0, NULL, NULL, NULL, 24, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (26, '调试日志', '1', 'Debug', 0, 1, 0, NULL, NULL, NULL, 24, 1, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (27, '通知日志', '2', 'Info', 0, 1, 0, NULL, NULL, NULL, 24, 2, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (28, '警告日志', '3', 'Warning', 0, 1, 0, NULL, NULL, NULL, 24, 3, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (29, '错误日志', '4', 'Error', 0, 1, 0, NULL, NULL, NULL, 24, 4, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (30, '致命日志', '5', 'Fatal', 0, 1, 0, NULL, NULL, NULL, 24, 5, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (31, '日志类型', 'log_type', NULL, 0, 0, NULL, NULL, NULL, NULL, 0, NULL, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (32, '登录日志', '0', 'login', 0, 1, 0, NULL, NULL, NULL, 31, 0, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (33, '权限日志', '1', 'authorize', 0, 1, 0, NULL, NULL, NULL, 31, 1, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (34, '系统日志', '2', 'system', 0, 1, 0, NULL, NULL, NULL, 31, 2, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (35, '业务日志', '3', 'business', 0, 1, 0, NULL, NULL, NULL, 31, 3, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');
INSERT INTO `sys_dictionary` VALUES (36, '异常日志', '4', 'exception', 0, 1, 0, NULL, NULL, NULL, 31, 4, 1, 'Admin', '2024-11-30 19:31:39', 'Admin', '2024-11-30 19:31:39');

-- ----------------------------
-- Table structure for sys_lang
-- ----------------------------
DROP TABLE IF EXISTS `sys_lang`;
CREATE TABLE `sys_lang`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key_name` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `en_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cn_text` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_lang
-- ----------------------------
INSERT INTO `sys_lang` VALUES (4, 'menuManage', 'Menu Manage', '菜单管理', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (5, 'dictionaryManage', 'Dictionary Manage', '字典管理', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (6, 'systemLog', 'System Log', '系统日志', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (7, 'i18nTextConfig', 'i18n Text Config', '国际化文本配置', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (8, 'home', 'Home', '首页', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (9, 'language', 'Language', '语言', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (10, 'simpleChinese', 'Simple Chinese', '简体中文', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (11, 'userInfo', 'User Info', '用户信息', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (12, 'logout', 'Logout', '退出登录', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (13, 'add', 'Add', '新增', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (14, 'delete', 'Delete', '删除', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (15, 'search', 'Search', '查询', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (16, 'dictionaryName', 'Dictionary Name', '字典名称', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (17, 'dictionaryType', 'Dictionary Type', '字典类型', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (18, 'dictionaryKey', 'Dictionary Key', '字典关键字', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (19, 'dictionaryValue', 'Dictionary Value', '字典值', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (20, 'keyType', 'Key Type', '关键字类型', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (21, 'valueType', 'Value Type', '值类型', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (22, 'serviceName', 'Service Name', '服务名称', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (23, 'frontendStyle', 'Frontend Style', '前端样式', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (24, 'highLevelDictionary', 'High Level Dictionary', '上级字典', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (25, 'order', 'Order', '顺序', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (26, 'isEnable', 'Is Enable', '是否可用', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (27, 'remarks', 'Remarks', '备注', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (28, 'modifier', 'Modifier', '修改人', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (29, 'modifyTime', 'Modify Time', '修改时间', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (30, 'operate', 'Operate', '操作', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (31, 'confirmDeleteSingle', 'Are you sure to delete this data?', '确定要删除该条数据吗？', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (32, 'confirmDeleteBatch', 'Are you sure to delete the selected data?', '确定要删除选中的数据吗？', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (33, 'submitClose', 'Submit Close', '提交关闭', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (34, 'cancel', 'Cancel', '取消', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (35, 'submitContinue', 'Submit Continue', '提交继续', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (36, 'roleName', 'Role Name', '角色名称', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (37, 'highLevelRole', 'High Level Role', '上级角色', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (38, 'reset', 'Reset', '重置', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (39, 'export', 'Export', '导出', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (40, 'import', 'Import', '导入', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (41, 'downloadTemplate', 'Download Template', '下载模板', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (42, 'username', 'Username', '用户名', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (43, 'nickname', 'Nickname', '昵称', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (44, 'role', 'Role', '角色', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (45, 'menuName', 'Menu Name', '菜单名称', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (46, 'menuTitle', 'Menu Title', '菜单标题', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (47, 'path', 'Path', '路径', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (48, 'apiPath', 'API Path', 'API 路径', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (49, 'authorization', 'Authorization', '权限', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (50, 'icon', 'Icon', '图标', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (51, 'highLevelMenu', 'High Level Menu', '上级菜单', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (52, 'isShow', 'Is Show', '是否显示', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (53, 'logLevel', 'Log Level', '日志级别', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (54, 'logType', 'Log Type', '日志类型', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (55, 'className', 'Class Name', '类名', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (56, 'browserType', 'Browser Type', '浏览器类型', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (57, 'serviceIP', 'Service IP', '服务 IP', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (58, 'requestUrl', 'Request Url', '请求路径', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (59, 'requestParam', 'Request Param', '请求参数', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (60, 'response', 'Response', '响应结果', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (61, 'exceptionInfo', 'Exception Info', '异常信息', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (62, 'userIP', 'User IP', '用户 IP', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (63, 'requestStartTime', 'Request Start Time', '请求开始时间', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (64, 'requestEndTime', 'Request End Time', '请求结束时间', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (65, 'requestElapsedTime', 'Request Elapsed Time', '请求持续时间', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (66, 'view', 'View', '查看', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (67, 'i18nKey', 'i18n Key', '国际化关键字', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (68, 'englishText', 'English Text', '英文文本', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (69, 'chineseText', 'Chinese Text', '中文文本', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (70, 'roleManage', 'Role Manage', '角色管理', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (71, 'deleteSuccess', 'Delete successfully', '删除成功', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (72, 'systemMaintain', 'System Maintain', '系统维护', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (73, 'userManage', 'User Manage', '用户管理', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (75, 'edit', 'Edit', '编辑', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (76, 'submit', 'Submit', '提交', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (77, 'string', 'String', '字符串', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (78, 'number', 'Number', '数字', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (79, 'boolean', 'Boolean', '布尔类型', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (80, 'date', 'Date', '日期', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (81, 'datetime', 'Datetime', '日期时间', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (82, 'yes', 'Yes', '是', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (83, 'no', 'No', '否', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (85, 'success', 'Success', '成功', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (87, 'login', 'Login', '登录', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (88, 'password', 'Password', '密码', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (89, 'uploadSuccess', 'Upload Success', '上传成功', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (90, 'update', 'Update', '更新', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (91, 'onlySupportFileType', 'Only support file type of {0}', '只支持{0}文件类型', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (92, 'fileSizeLimit', 'Only support files with size less than {0}', '只支持文件大小小于{0}', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (93, 'template', '_Template', '模板', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (95, 'authorize', 'Authorize', '授权', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (96, 'system', 'System', '系统', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (97, 'business', 'Business', '业务', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (98, 'exception', 'Exception', '异常', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (104, 'createTime', 'Create Time', '创建时间', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');
INSERT INTO `sys_lang` VALUES (105, 'formItemRequired', '{0} is required', '{0}为必填项', 'Admin', '2024-11-30 19:31:49', 'Admin', '2024-11-30 19:31:49');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `level` int NOT NULL,
  `log_type` int NOT NULL,
  `service_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `browser_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `service_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `url` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `request_param` blob NULL,
  `response` blob NULL,
  `exception` blob NULL,
  `user_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `start_time` datetime(6) NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `elapsed_time` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `label` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `url` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `request_path` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `auth` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `parent_id` bigint NOT NULL DEFAULT 0,
  `order_no` int NULL DEFAULT NULL,
  `is_show` tinyint(1) NOT NULL DEFAULT 1,
  `enable` tinyint(1) NOT NULL DEFAULT 1,
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 'system', 'systemMaintain', NULL, NULL, 'search', 'setting', NULL, 0, 0, 1, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');
INSERT INTO `sys_menu` VALUES (2, 'system.role', 'roleManage', '/role', 'security-module/role', 'search,add,update,delete', NULL, NULL, 1, 0, 1, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');
INSERT INTO `sys_menu` VALUES (3, 'system.user', 'userManage', '/user', 'security-module/user', 'search,add,update,delete,import,export', 'user', NULL, 1, 1, 1, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');
INSERT INTO `sys_menu` VALUES (4, 'system.menu', 'menuManage', '/menu', 'security-module/menu', 'search,add,update,delete', 'menu', 'test1', 1, 2, 1, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');
INSERT INTO `sys_menu` VALUES (6, 'system.dictionary', 'dictionaryManage', '/dictionary', 'basic-module/dictionary', 'search,add,update,delete', NULL, NULL, 1, 3, 1, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');
INSERT INTO `sys_menu` VALUES (7, 'system.log', 'systemLog', '/log', 'basic-module/log', 'search,delete', NULL, NULL, 1, 4, 1, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');
INSERT INTO `sys_menu` VALUES (8, 'user-info', 'userInfo', '/user-info', NULL, 'search,update', NULL, NULL, 0, NULL, 0, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');
INSERT INTO `sys_menu` VALUES (9, 'system.language', 'i18nTextConfig', '/language', 'basic-module/language', 'search,add,update,delete,import,export', NULL, NULL, 1, 5, 1, 1, 'Admin', '2024-11-30 19:31:30', 'Admin', '2024-11-30 19:31:30');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint NOT NULL DEFAULT 0,
  `order_no` int NULL DEFAULT NULL,
  `enable` tinyint(1) NOT NULL DEFAULT 1,
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 0, 0, 1, 'admin', '2024-11-30 19:30:41', 'admin', '2024-11-30 19:30:41');

-- ----------------------------
-- Table structure for sys_role_auth
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_auth`;
CREATE TABLE `sys_role_auth`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `auth` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_auth
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `display_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `enable` tinyint(1) NOT NULL DEFAULT 1,
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'Admin', 1, 'unknown', '2024-11-30 19:30:41', 'unknown', '2024-11-30 19:30:41');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `create_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 'admin', '2024-11-30 19:30:41');

SET FOREIGN_KEY_CHECKS = 1;
