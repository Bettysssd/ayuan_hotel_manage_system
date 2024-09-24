/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 8.0.12 : Database - hotel
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`hotel` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;

USE `hotel`;

/*Table structure for table `canteen` */

DROP TABLE IF EXISTS `canteen`;

CREATE TABLE `canteen` (
  `canteen_id` varchar(8) COLLATE utf8_unicode_ci NOT NULL COMMENT '食堂ID',
  `canteen_cover` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '食堂图片',
  `canteen_name` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '食堂名称',
  `canteen_type` int(11) NOT NULL DEFAULT '0' COMMENT '食堂类型 -> 0: 中餐， 1: 西餐， 2: 中东菜式， 3：韩式料理， 4：日式料理， 5：泰式料理， 6：潮汕菜式 ，7:广府菜式',
  `canteen_status` int(11) NOT NULL DEFAULT '0' COMMENT '食堂状态 -> 0:有余位，可预定， 1: 已满',
  `canteen_info` int(11) NOT NULL DEFAULT '0' COMMENT '食堂信息（当天提供的菜品）',
  `canteen_open_time` datetime NOT NULL,
  `canteen_closed_time` datetime NOT NULL,
  `canteen_rating` decimal(10,0) DEFAULT NULL COMMENT '食堂客户满意度（百分比）',
  `canteen_trump_up` int(11) DEFAULT NULL COMMENT '食堂好评数目',
  `canteen_trump_down` int(11) DEFAULT NULL COMMENT '食堂差评数目',
  PRIMARY KEY (`canteen_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `canteen` */

/*Table structure for table `goods` */

DROP TABLE IF EXISTS `goods`;

CREATE TABLE `goods` (
  `goods_id` varchar(8) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品ID',
  `goods_cover` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '商品图片',
  `goods_name` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '商品名称',
  `price` decimal(10,0) NOT NULL COMMENT '商品价格',
  `goods_status` int(11) NOT NULL DEFAULT '0' COMMENT '状态 -> 0:库存充足，1: 缺货，2:下架',
  `goods_info` int(11) NOT NULL DEFAULT '0' COMMENT '商品信息',
  `expire_time` datetime NOT NULL COMMENT '商品过期日期',
  `product_time` datetime NOT NULL COMMENT '商品生产日期',
  `positive_rating` decimal(10,0) DEFAULT NULL COMMENT '商品客户满意度（百分比）',
  `goods_trump_up` int(11) DEFAULT NULL COMMENT '商品好评数目',
  `goods_trump_down` int(11) DEFAULT NULL COMMENT '商品差评数目',
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `goods` */

/*Table structure for table `parking` */

DROP TABLE IF EXISTS `parking`;

CREATE TABLE `parking` (
  `parking_id` varchar(8) COLLATE utf8_unicode_ci NOT NULL COMMENT '车位ID',
  `car_number` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '车辆号码',
  `car_cover` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '汽车图片',
  `car_name` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '汽车名称',
  `last_parking_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近泊车时间',
  `last_leave_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最近取车离开时间',
  PRIMARY KEY (`parking_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `parking` */

/*Table structure for table `room` */

DROP TABLE IF EXISTS `room`;

CREATE TABLE `room` (
  `room_id` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '房间号ID',
  `room_cover` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间预览图',
  `room_info` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间介绍',
  `room_type` int(11) DEFAULT NULL COMMENT '房间类型 -> 1: 单人间 , 2: 双人间 3: 家庭间',
  `room_status` int(11) NOT NULL DEFAULT '0' COMMENT '房间状态 -> 0: 可预定， 1: 已预定',
  `room_mark` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '入住房间号： 18楼402房',
  `number _of_occupants` int(11) DEFAULT NULL COMMENT '入住人数',
  `book_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '预定时间',
  `check_in_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入住时间',
  `check_out_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '退房时间',
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `room` */

/*Table structure for table `supervisor` */

DROP TABLE IF EXISTS `supervisor`;

CREATE TABLE `supervisor` (
  `supervisor_id` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员ID',
  `supervisor_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员昵称',
  `account` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员账号',
  `passwordd` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员密码',
  PRIMARY KEY (`supervisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `supervisor` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '用户ID',
  `user_cover` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `user_name` varchar(15) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户姓名',
  `nick_name` varchar(20) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户昵称',
  `email` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '用户账号，使用邮箱注册',
  `password` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT '123456' COMMENT '密码,第一次赋初始化，稍后修改',
  `gender` int(11) NOT NULL DEFAULT '0' COMMENT '性别-> 0: 男， 1: 女',
  `sign_in_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
  `last_login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最近一次登录时间',
  `id_card_code` varchar(18) COLLATE utf8_unicode_ci NOT NULL COMMENT '身份证号码',
  `phone_number` varchar(13) COLLATE utf8_unicode_ci NOT NULL COMMENT '联系电话',
  `spend_amount` decimal(11,0) DEFAULT '0' COMMENT '消费金额',
  `wechat_account` varchar(25) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '微信账号',
  `room_id` varchar(6) COLLATE utf8_unicode_ci NOT NULL COMMENT '房间号ID',
  `parking_id` varchar(15) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '汽车号牌',
  `waiter_id` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '服务员ID',
  `canteen_id` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '食堂ID',
  PRIMARY KEY (`user_id`),
  KEY `waiter_id` (`waiter_id`),
  KEY `canteen_id` (`canteen_id`),
  KEY `room_id` (`room_id`),
  KEY `parking_id` (`parking_id`),
  CONSTRAINT `canteen_id` FOREIGN KEY (`canteen_id`) REFERENCES `canteen` (`canteen_id`),
  CONSTRAINT `parking_id` FOREIGN KEY (`parking_id`) REFERENCES `parking` (`parking_id`),
  CONSTRAINT `room_id` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`),
  CONSTRAINT `waiter_id` FOREIGN KEY (`waiter_id`) REFERENCES `waiter` (`waiter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `user` */

/*Table structure for table `waiter` */

DROP TABLE IF EXISTS `waiter`;

CREATE TABLE `waiter` (
  `waiter_id` varchar(8) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '服务员ID',
  `waiter_name` varchar(10) COLLATE utf8_unicode_ci NOT NULL COMMENT '服务员姓名',
  `gender` int(11) NOT NULL COMMENT '性别-> 0: 男， 1: 女',
  `waiter_status` int(11) NOT NULL DEFAULT '0' COMMENT '状态 -> 0:空闲，1: 忙碌中，2:休假， 3:离职',
  `job_type` int(11) NOT NULL DEFAULT '0' COMMENT '工作类型 -> 0:保洁 1: 快递员 2: 泊车员 3: 门童',
  `waiter_positive_rating` decimal(10,0) DEFAULT NULL COMMENT '服务员--客户满意度（百分比）',
  `waiter_trump_up` int(11) DEFAULT NULL COMMENT '服务员好评',
  `waiter_trump_down` int(11) DEFAULT NULL COMMENT '服务员差评',
  `time_of_employment` datetime NOT NULL COMMENT '服务员入职时间',
  `last_office_hours` datetime NOT NULL COMMENT '服务员最近上班时间',
  PRIMARY KEY (`waiter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

/*Data for the table `waiter` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
