
CREATE DATABASE /*!32312 IF NOT EXISTS*/ `uushop` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `uushop`;

--
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `img_url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户头像',
  `name` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES (1,'admin','123123','https://img1.baidu.com/it/u=1026177128,203298668&fm=26&fmt=auto&gp=0.jpg','超级玛丽'),(2,'user','123123','https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fwww.fankaol.com%2Fimages%2F201706%2Fgoods_img%2F794_P_1496906268092.png&refer=http%3A%2F%2Fwww.fankaol.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1621132659&t=80553193d3eb5080c60db742ad2861f5','魂斗罗');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_record`
--

DROP TABLE IF EXISTS `login_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userid` int DEFAULT NULL COMMENT '用户ID',
  `ipaddress` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问IP',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_record`
--

--
-- Table structure for table `order_detail`
--

DROP TABLE IF EXISTS `order_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_detail` (
  `detail_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `product_id` int NOT NULL,
  `product_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `product_price` decimal(8,2) NOT NULL COMMENT '商品单价',
  `product_quantity` int NOT NULL COMMENT '商品数量',
  `product_icon` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品小图',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`detail_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_detail`
--

--
-- Table structure for table `order_master`
--

DROP TABLE IF EXISTS `order_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_master` (
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `buyer_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '买家名字',
  `buyer_phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '买家电话',
  `buyer_address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '买家地址',
  `buyer_openid` int NOT NULL COMMENT '买家id',
  `order_amount` decimal(8,2) NOT NULL COMMENT '订单总金额',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态，默认0新下单,1完成，2取消',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态，默认0未支付，1已支付',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`order_id`),
  KEY `idx_buyer_openid` (`buyer_openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_master`
--

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类目名称',
  `category_type` int NOT NULL COMMENT '类目编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uqe_category_type` (`category_type`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='类目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (1,'生鲜',1,'2020-01-07 09:22:48','2021-04-28 08:10:07'),(2,'手机',2,'2020-01-07 09:26:42','2021-04-28 08:24:12'),(3,'食品',3,'2020-01-07 09:37:19','2021-04-28 08:10:07'),(16,'水果',4,'2020-01-22 09:34:56','2021-04-28 08:10:07'),(17,'女装',5,'2020-01-31 08:50:16','2021-04-28 08:10:07'),(19,'百货',6,'2021-04-28 08:10:07','2021-04-28 08:10:07'),(20,'男装',7,'2021-04-28 08:10:48','2021-04-28 08:10:48'),(21,'图书',8,'2021-04-28 08:10:48','2021-04-28 08:10:48'),(24,'运动',9,'2021-04-28 08:22:19','2021-04-28 08:22:19');
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_info`
--

DROP TABLE IF EXISTS `product_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_info` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `product_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `product_price` decimal(8,2) NOT NULL COMMENT '商品单价',
  `product_stock` int NOT NULL COMMENT '库存',
  `product_description` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `product_icon` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '小图',
  `category_type` int NOT NULL COMMENT '类目编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `product_status` int NOT NULL COMMENT '商品状态，1正常0下架',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_info`
--

LOCK TABLES `product_info` WRITE;
/*!40000 ALTER TABLE `product_info` DISABLE KEYS */;
INSERT INTO `product_info` VALUES (1,'生鲜大虾',82.60,100,'鲜活冷冻深海虾','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/150370/24/6079/129012/5fa4bebaEb7aea500/0ceaa6b9e0ec073d.jpg!q80.dpg.webp',1,'2020-01-07 15:45:33','2021-07-09 08:41:04',1),(2,'舟山海捕新鲜带鱼中段',72.80,6,'五斤装特大段','https://img14.360buyimg.com/n0/jfs/t1/145364/15/3380/155346/5f153f1dEeeaeb990/39984daed5369e91.jpg',1,'2020-01-07 15:32:00','2021-07-09 08:33:29',0),(3,'帝王蟹',105.00,220,'帝王蟹3.2～2.8斤·礼盒装','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/162387/1/12528/235896/604e2f4eE2c6465f9/9726751899a4f735.jpg!q80.dpg.webp',1,'2020-01-11 14:46:37','2021-07-09 02:41:20',1),(4,'迷你小八爪鱼',67.20,83,'新鲜海捕，三去工艺','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/89654/16/18615/151102/5e96b4d5E0b6c7dc5/97436995c490dd20.jpg!q80.dpg.webp',1,'2020-01-22 07:53:36','2021-07-09 02:41:20',1),(5,'福建新鲜活冻大鲍鱼大',69.00,1085,'火锅食材贝类 生鲜','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/171918/6/4940/300074/607aa8ebEf9702ba0/6cff9c166521d87c.jpg!q80.dpg.webp',1,'2020-01-22 08:15:51','2021-07-09 06:58:51',1),(6,'Redmi 9A 5000mAh大电量',599.00,93,'游戏智能手机','https://img12.360buyimg.com/n1/s450x450_jfs/t1/118064/27/12885/59959/5f17b7efE453f688d/5b33ac76b2aaea9b.jpg',2,'2020-01-22 09:35:17','2021-07-09 01:42:51',1),(7,'Redmi Note 9 4G大',1099.00,88,'游戏智能手机','https://img14.360buyimg.com/n0/jfs/t1/143051/26/15747/73035/5fbdd54cE80757a48/67c387e1d0dc5c83.jpg',2,'2020-01-23 07:42:40','2021-07-09 06:58:51',1),(10,'OPPO A52 8GB+128GB大',1349.00,650,'拍照游戏智能手机','https://img14.360buyimg.com/n1/s450x450_jfs/t1/155704/14/2313/76251/5fe764dfE36d34fcd/85141b61f6a5196e.jpg',2,'2020-01-25 05:44:55','2021-07-09 06:58:51',1),(12,'方便面干吃面大 ',5.10,77,'严选工厂·专业质检','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/134875/9/10819/349935/5f6c137aEfe86c034/5823bc25107b0d33.jpg!q80.dpg.webp',3,'2020-01-26 09:06:32','2021-07-09 06:58:51',1),(13,'酸辣无骨鸡爪',5.90,95,'泡椒凤爪 酸辣柠檬','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/167700/8/18748/219167/60799855E870b4a6a/ab6d827a71da2e21.jpg!q80.dpg.webp',3,'2020-01-26 09:40:25','2021-07-09 02:52:16',1),(17,'原味盐焗坚果干果',13.90,105,'盐焗开心果1罐装','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/103668/15/17528/180008/5e86f0d5E4388cccf/1cd7feb9bb083a06.jpg!q80.dpg.webp',3,'2020-08-26 06:44:04','2021-07-09 02:52:16',1),(41,'新鲜水果橙子',16.50,100,'吃货推荐！','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/176478/10/208/215128/605b39caE369caf46/7b7d64dd9538f049.jpg!q80.dpg.webp',4,'2021-03-31 06:10:11','2021-05-08 07:32:39',1),(66,'云南香蕉自然熟',18.80,78,'当季新鲜水果','https://img14.360buyimg.com/n0/jfs/t1/149004/38/13701/171073/5fa907a3E6f42c8c0/808ac7b6a01ef469.jpg',4,'2021-04-09 08:45:28','2021-05-08 04:16:59',1),(67,'蓝莓礼盒装',76.90,79,'新鲜水果','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/163019/38/18568/109919/607299e8E8550ba02/9fa62d9804326eba.jpg!q80.dpg.webp',4,'2021-04-09 08:45:28','2021-04-28 08:43:31',1),(70,'棉麻连衣裙',98.00,90,'气质休闲减龄','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/163455/39/19709/84143/607d0615E43ecc3f3/c7ceb1811660bbe9.jpg!q80.dpg.webp',5,'2021-04-12 01:40:05','2021-04-28 08:45:01',1),(71,'网红钻石玻璃水杯',15.90,69,'男女情侣杯子','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/124129/37/7645/187892/5f16ae93E3c697094/1b93ba29e374f7e7.jpg!q80.dpg.webp',6,'2021-04-12 01:40:05','2021-05-08 04:16:59',1),(72,'长袖T恤男春秋季',69.00,90,'圆领上衣服T恤','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/159826/7/11434/155749/6046654aE6bc60e42/6a7c17923b311f7b.jpg!q80.dpg.webp',7,'2021-04-12 02:48:56','2021-04-28 08:47:20',1),(79,'月亮与六便士',59.00,1110,'破损包退换','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/126410/23/4080/87479/5ed8e5eaEec55bc32/c16496962269af13.jpg!q80.dpg.webp',8,'2021-04-27 07:11:48','2021-04-28 08:48:24',1),(80,'护目镜防尘防风防飞溅',19.80,0,'深蓝色防雾款','https://img14.360buyimg.com/n0/jfs/t1/180224/20/1147/170542/608767bbE7a4abb10/4cd90871f84d076f.jpg',9,'2021-04-27 07:11:49','2021-04-28 08:49:53',1),(81,'苮苏 针织连衣裙女短袖',199.00,100,'收腰显瘦冰丝','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/173691/30/3880/201110/60755832E1e69629b/6a5ec9a5615887dc.jpg!q80.dpg.webp',5,'2021-04-30 05:53:01','2021-04-30 05:53:01',1),(82,'法姿迪牛仔连衣裙',98.00,200,'中长款休闲裙子','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/188765/18/529/129121/608a42f9E1a592d66/e03b2c458e1d2069.jpg!q80.dpg.webp',5,'2021-04-30 05:54:06','2021-04-30 05:54:06',1),(83,'舒媛香皂盒双层肥皂盒架子',16.20,299,'双层沥水 浅灰色（双层皂盒）','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/106977/10/18135/110315/5e8f420dEf70d9db0/f3a0d81579e0e834.jpg!q80.dpg.webp',6,'2021-04-30 05:55:36','2021-05-08 04:16:59',1),(84,'加绒防冻洗碗手套',7.90,100,'长款加绒洗碗手套','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/151236/13/5086/137027/5f9ecbfeEa79ff2bd/b998f8e07b3e6ae0.jpg!q80.dpg.webp',6,'2021-04-30 05:56:53','2021-04-30 05:56:53',1),(85,'柴曜【两件装】休闲裤男夏季',78.00,99,'潮流百搭直筒修身长裤','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/175210/34/2336/99107/606c308aEd60dd3f1/1716285bbeaa57a0.jpg!q80.dpg.webp',7,'2021-04-30 05:59:19','2021-04-30 05:59:19',1),(86,'秋季新品牛仔衬衫男长袖',29.00,98,'男士秋装上衣外套','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/130951/25/9412/30408/5f559efeEa7fad12a/05083bffc45094b7.jpg!q80.dpg.webp',7,'2021-04-30 05:59:19','2021-05-08 08:21:49',1),(87,'福尔摩斯探案10册原版',19.80,65,'推理悬疑小说书籍','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/135575/8/8079/147374/5f45bf50E6348757a/88e3e5340615416a.jpg!q80.dpg.webp',8,'2021-04-30 06:02:04','2021-05-08 04:16:59',1),(88,'了不起的盖茨比',9.00,100,'外国文学小说世界名著','https://m.360buyimg.com/mobilecms/s750x750_jfs/t1/122511/22/696/146270/5eb75f12E75be74f9/566d3cfbdda2c105.jpg!q80.dpg.webp',8,'2021-04-30 06:02:04','2021-04-30 06:02:04',1),(89,'李宁中小学教学5号篮球',79.00,98,'CBA联赛训练发泡橡胶材质','https://img14.360buyimg.com/n0/jfs/t1/68920/25/10949/835238/5d85eaa0E6ac4321c/7355ff5788c73696.jpg',9,'2021-04-30 06:04:47','2021-05-08 04:16:59',1),(90,'特步男鞋子运动鞋',167.00,200,'夏季防滑耐磨','https://img14.360buyimg.com/n0/jfs/t1/21459/40/14983/171347/5cad4c92E6049e3b7/c6ce083d8795dfd9.jpg',9,'2021-04-30 06:04:47','2021-04-30 06:04:47',1),(109,'666',666.00,60,'666','666',1,'2021-07-07 07:03:47','2021-07-09 07:02:24',0),(110,'晚餐111',16.00,90,'好吃好吃','https://s1.st.meishij.net/rs/50/123/6030800/n6030800_152708155351112.jpg',2,'2021-07-09 08:17:57','2021-07-09 08:17:57',1),(112,'666',666.00,666,'66','66',6,'2021-07-09 08:36:13','2021-07-09 08:36:13',1);
/*!40000 ALTER TABLE `product_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `mobile` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--
