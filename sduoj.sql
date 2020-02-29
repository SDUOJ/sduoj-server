/*
Navicat MySQL Data Transfer

Source Server         : 192.168.68.132_33306
Source Server Version : 50647
Source Host           : 192.168.68.132:33306
Source Database       : sduoj

Target Server Type    : MYSQL
Target Server Version : 50647
File Encoding         : 65001

Date: 2020-02-26 11:42:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for oj_users
-- ----------------------------
DROP TABLE IF EXISTS `oj_users`;
CREATE TABLE `oj_users` (
  `u_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `u_username` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
  `u_password` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户密码',
  `u_nickname` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `u_email` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户邮箱',
  `u_gender` tinyint(1) DEFAULT '2' COMMENT '用户性别, 0.女, 1.男, 2.问号',
  PRIMARY KEY (`u_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- ----------------------------
-- Records of oj_users
-- ----------------------------
INSERT INTO `oj_users` VALUES ('1000', 'admin', 'now_not_slat_password', 'admin', 'oj@sdu.edu.cn', '2');
INSERT INTO `oj_users` VALUES ('1001', 'tttt', 'now_not_slat_password', 'tttt', 'zhangt@mail.sdu.edu.cn', '1');
