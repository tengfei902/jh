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
-- Table structure for table `pay_proof`
--

DROP TABLE IF EXISTS `pay_proof`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pay_proof` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `merchant_no` varchar(50) NOT NULL DEFAULT '' COMMENT '商户编号',
  `status` int(3) NOT NULL DEFAULT '0',
  `outlet_no` varchar(50) NOT NULL COMMENT '门店编号',
  `total` int(50) NOT NULL COMMENT '转账金额',
  `out_trade_no` varchar(50) NOT NULL COMMENT '订单号',
  `errcode` int(11) DEFAULT NULL COMMENT '结果代码,0为下单成功或交易成功反扫时为交易成功,1为验签失败,2参数错误,3支付失败,4等待用户支付',
  `message` varchar(200) DEFAULT NULL COMMENT '成功或错误描述',
  `no` varchar(50) DEFAULT NULL COMMENT '富信通订单号',
  `code_url` varchar(200) DEFAULT NULL COMMENT '扫码service为02或07支付时才有',
  `pay_info` varchar(200) DEFAULT NULL COMMENT 'Jsapi支付调起参数',
  `actual_total` int(11) DEFAULT NULL COMMENT '支付成功的金额,分为单位.反扫service为03或08才有',
  `transaction_id` varchar(50) DEFAULT NULL COMMENT '上游的交易号,反扫service为03或08才有',
  `paytime` int(11) DEFAULT NULL COMMENT '交易成功的unix时间戳,反扫service为03或08才有',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_trd_no` (`out_trade_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pay_proof`
--

LOCK TABLES `pay_proof` WRITE;
/*!40000 ALTER TABLE `pay_proof` DISABLE KEYS */;
/*!40000 ALTER TABLE `pay_proof` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-10-30  8:02:31
