-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: db_jh
-- ------------------------------------------------------
-- Server version	5.7.20-log

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
-- Table structure for table `page_lay_out`
--

DROP TABLE IF EXISTS `page_lay_out`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `page_lay_out` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `path` varchar(100) NOT NULL,
  `type` varchar(50) NOT NULL,
  `row_index` int(5) NOT NULL DEFAULT '0',
  `level` int(2) NOT NULL DEFAULT '1',
  `superId` bigint(32) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `page_lay_out`
--

LOCK TABLES `page_lay_out` WRITE;
/*!40000 ALTER TABLE `page_lay_out` DISABLE KEYS */;
INSERT INTO `page_lay_out` VALUES (1,'管理首页','#','fa fa-home',0,1,0),(2,'系统设置','#','fa fa-asterisk',0,1,0),(3,'用户管理','#','fa fa-user',0,1,0),(4,'通道管理','#','fa fa-bank',0,1,0),(5,'订单管理','#','fa fa fa-sellsy',0,1,0),(6,'账户情况','#','fa fa fa-sellsy',0,1,0),(7,'账户资金变动','#','fa fa fa-sellsy',0,1,0),(8,'提款管理','#','fa fa fa-cubes',0,1,0),(9,'代理管理','#','fa fa-book',0,1,0),(10,'Dashboard','mian.html','J_menuItem',0,2,1),(11,'基本设置','Admin_System_base.html','J_menuItem',0,2,2),(12,'邮件设置','Admin_System_email.html','J_menuItem',1,2,2),(13,'权限管理','Admin_Menu_index.html','J_menuItem',0,2,3),(14,'角色管理','Admin_Auth_index.html','J_menuItem',1,2,3),(15,'用户管理','Admin_User_index.html','J_menuItem',2,2,3),(16,'邀请码管理','Admin_User_index.html','J_menuItem',3,2,3),(17,'供应商通道管理','Admin_Channel_index.html','J_menuItem',0,2,4),(18,'支付产品管理','Admin_Channel_product.html','J_menuItem',1,2,4),(19,'交易管理','Admin_Order_index.html','J_menuItem',0,2,5),(20,'资金变动管理','Admin_Order_changeRecord.html','J_menuItem',1,2,5),(21,'交易管理','Admin_Order_index.html','J_menuItem',0,2,6),(22,'资金变动管理','Admin_Order_changeRecord.html','J_menuItem',1,2,6),(23,'交易管理','Admin_Order_index.html','J_menuItem',0,2,7),(24,'资金变动管理','Admin_Order_changeRecord.html','J_menuItem',1,2,7),(25,'提款设置','Admin_Withdrawal_setting.html','J_menuItem',0,2,8),(26,'提款管理','Admin_Withdrawal_index.html','J_menuItem',1,2,8),(27,'代付管理','Admin_Withdrawal_payment.html','J_menuItem',2,2,8);
/*!40000 ALTER TABLE `page_lay_out` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-11-12  0:43:57
