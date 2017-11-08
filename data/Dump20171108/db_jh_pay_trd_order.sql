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
-- Table structure for table `pay_trd_order`
--

DROP TABLE IF EXISTS `pay_trd_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_trd_order` (
  `id` bigint(32) NOT NULL,
  `service` varchar(2) NOT NULL COMMENT '01微信公众号，02微信主扫，03微信反扫，04微信H5支付，05微信APP支付，06支付宝服务窗体，07支付宝主扫，08支付宝反扫',
  `merchant_no` varchar(50) NOT NULL COMMENT '商户编号',
  `outlet_no` varchar(50) NOT NULL COMMENT '门店编号',
  `total` int(50) NOT NULL COMMENT '转账金额',
  `name` varchar(50) NOT NULL COMMENT '订单描述',
  `remark` varchar(60) NOT NULL COMMENT '订单描述',
  `out_trade_no` varchar(50) NOT NULL COMMENT '订单号',
  `create_ip` varchar(16) NOT NULL COMMENT '支付发起ip',
  `out_notify_url` varchar(250) NOT NULL COMMENT '支付结果通知url',
  `sub_openid` varchar(60) NOT NULL COMMENT '微信open_id',
  `buyer_id` varchar(60) NOT NULL COMMENT '支付宝buyer_id',
  `authcode` varchar(32) NOT NULL COMMENT '支付宝授权码',
  `nonce_str` varchar(32) NOT NULL COMMENT '随机字符串',
  `sign_type` varchar(10) NOT NULL DEFAULT 'MD5' COMMENT '加密方式',
  `sign` varchar(32) NOT NULL COMMENT '签名',
  `status` int(5) NOT NULL DEFAULT '0' COMMENT '支付状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_trd_no` (`out_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_trd_order`
--

LOCK TABLES `pay_trd_order` WRITE;
/*!40000 ALTER TABLE `pay_trd_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_trd_order` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-11-08  8:37:08
