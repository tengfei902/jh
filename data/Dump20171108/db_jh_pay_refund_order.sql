-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: db_jh
-- ------------------------------------------------------
-- Server version	5.7.20

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `pay_refund_order`
--

DROP TABLE IF EXISTS `pay_refund_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_refund_order` (
  `id` bigint(32) NOT NULL,
  `merchant_no` varchar(50) NOT NULL DEFAULT '' COMMENT '商户编号',
  `refund` int(11) NOT NULL COMMENT '支付金额',
  `ori_no` varchar(50) NOT NULL COMMENT '原交易号',
  `refund_no` varchar(50) NOT NULL COMMENT '商户系统原退款单号',
  `out_notify_url` varchar(255) NOT NULL COMMENT '支付结果通知的URL',
  `nonce_str` varchar(32) NOT NULL COMMENT '随机字符串',
  `sign_type` varchar(10) NOT NULL DEFAULT 'MD5' COMMENT '取值：MD5默认：MD5',
  `sign` varchar(32) NOT NULL COMMENT 'MD5签名结果',
  `refund_type` int(11) NOT NULL DEFAULT '0' COMMENT '0:退款，1:撤销',
  `status` int(11) NOT NULL DEFAULT '10' COMMENT '状态',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `errcode` int(11) DEFAULT NULL COMMENT '0为退款成功，1为验签失败，2参数错误，3退款失败，4退款中',
  `message` varchar(200) DEFAULT NULL COMMENT '成功或错误描述',
  `no` varchar(60) DEFAULT NULL COMMENT '富信通退款单号',
  `actual_refund_fee` int(11) DEFAULT NULL COMMENT '退款金额',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_no` (`ori_no`),
  UNIQUE KEY `unq_refund_no` (`refund_no`,`refund_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_refund_order`
--

LOCK TABLES `pay_refund_order` WRITE;
/*!40000 ALTER TABLE `pay_refund_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_refund_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-11-08  8:37:07
