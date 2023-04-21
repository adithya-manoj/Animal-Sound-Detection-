/*
SQLyog Community Edition- MySQL GUI v8.03 
MySQL - 5.6.12-log : Database - animal_detection
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

CREATE DATABASE /*!32312 IF NOT EXISTS*/`animal_detection` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `animal_detection`;

/*Table structure for table `animal_database` */

DROP TABLE IF EXISTS `animal_database`;

CREATE TABLE `animal_database` (
  `animal_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `description` varchar(400) DEFAULT NULL,
  `photo` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`animal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

/*Data for the table `animal_database` */

insert  into `animal_database`(`animal_id`,`name`,`description`,`photo`) values (6,'Crow','                 A crow is a bird of the genus Corvus, or more broadly a synonym for all of Corvus.','202141515581download.jpg'),(8,'Dog','         The dog is a domesticated carnivore of the family Canidae. It is part of the wolf-like canids, and is the most widely abundant terrestrial carnivores','2021415155822dog.jpg'),(9,'Bat','   Bats are mammals of the order Chiroptera. With their forelimbs adapted as wings, they are the only mammals capable of true and sustained flight. \r\n\r\nThe second largest order of mammals after rodents, bats comprise about 20% of all classified mammal species worldwide, with over 1,400 species. ','uploads_9.jpg'),(10,'Elephant','  Elephants are mammals of the family Elephantidae and the largest existing land animals. Three species are currently recognised: the African bush elephant, the African forest elephant, and the Asian elephant. Elephantidae is the only surviving family of the order Proboscidea; extinct members include the mastodons. ','uploads_10.jpg'),(11,'Junglefowl',' Junglefowl are the only four living species of bird from the genus Gallus in the bird order Galliformes, and occur in India, Sri Lanka, and Southeast Asia. They diverged from their common ancestor about 4–6 million years ago.','uploads_11.jpg'),(12,'Toad',' Toad is a common name for certain frogs, especially of the family Bufonidae, that are characterized by dry, leathery skin, short legs, and large bumps covering the parotoid glands','uploads_12.jpg'),(13,'Squirrels',' Squirrels are members of the family Sciuridae, a family that includes small or medium-size rodents. ','uploads_13.jpg'),(15,'Pig',' A pig is any of the animals in the genus Sus, within the even-toed ungulate family Suidae. Pigs include domestic pigs and their ancestor, the common Eurasian wild boar, along with other species. Pigs, like all suids, are native to the Eurasian and African continents, ranging from Europe to the Pacific islands.','uploads_15.jpg');

/*Table structure for table `authentication` */

DROP TABLE IF EXISTS `authentication`;

CREATE TABLE `authentication` (
  `login_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `user_access` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`login_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;

/*Data for the table `authentication` */

insert  into `authentication`(`login_id`,`username`,`password`,`user_access`) values (1,'admin','root','admin'),(22,'vimalvyas@gmail.com','KL768995','user'),(23,'aswin.p1800@gmail.com','12345','user'),(28,'damu@gmail.com','1234','user'),(29,'adithyamanojv@gmail.com','adhi12','user'),(30,'a@gmail.com','1234','user'),(31,'athira@gmail.com','1234','user'),(32,'demo@gmail.com','1234','user'),(33,'akshaysajeev@gmail.com','123456','user');

/*Table structure for table `feedback_data` */

DROP TABLE IF EXISTS `feedback_data`;

CREATE TABLE `feedback_data` (
  `feedback_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `date` date DEFAULT NULL,
  `feedback` varchar(200) NOT NULL,
  PRIMARY KEY (`feedback_id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;

/*Data for the table `feedback_data` */

insert  into `feedback_data`(`feedback_id`,`user_id`,`date`,`feedback`) values (18,22,'2020-12-26','Very cool feature\r\n'),(19,22,'2020-12-26','Dummy data'),(20,22,'2020-12-26','dummy data2'),(21,22,'2020-12-26','feedback '),(22,22,'2020-12-26','dummy feedback by vimal'),(23,22,'2020-12-26','feedback by vimal 2\r\n'),(24,22,'2020-12-26','dummy feedback 5'),(25,23,'2020-12-27','Feedback'),(26,28,'2021-01-01','Damus feedback'),(27,0,'2021-02-15','blah blah blah'),(28,0,'2021-02-15','blah blah blah'),(29,0,'2021-02-15','blah blah blah'),(30,0,'2021-02-15','blah blah blah'),(31,0,'2021-02-15','blah blah blah'),(32,23,'2021-03-25','feedback thannu'),(33,23,'2021-03-25','blah blah'),(34,23,'2021-03-25','blah blah blahhhhhhhhh'),(35,23,'2021-03-25','feedback'),(36,23,'2021-04-03','hey'),(37,23,'2021-04-03','Hello...'),(38,23,'2021-04-04','nice app'),(39,23,'2021-04-07','cool feature\r\n'),(40,23,'2021-04-07','hey'),(41,33,'2021-04-15','Good');

/*Table structure for table `prediction_results` */

DROP TABLE IF EXISTS `prediction_results`;

CREATE TABLE `prediction_results` (
  `prediction_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `file` varchar(200) NOT NULL,
  `date` date DEFAULT NULL,
  `animal_id` int(11) NOT NULL,
  PRIMARY KEY (`prediction_id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=latin1;

/*Data for the table `prediction_results` */

insert  into `prediction_results`(`prediction_id`,`user_id`,`file`,`date`,`animal_id`) values (3,12,'uploads_3pdf','2020-12-18',6),(4,12,'uploads_4','2020-12-21',6),(5,12,'uploads_5jpg','2020-12-21',0),(6,15,'uploads_6','2020-12-26',6),(7,15,'uploads_7mp3','2020-12-26',6),(8,23,'uploads_8mp3','2021-02-15',0),(9,23,'uploads_9.mp3','2021-02-15',0),(12,16,'uploads_12mp3','2021-02-17',0),(21,23,'uploads_21.wav','2021-02-17',10),(22,23,'uploads_22.wav','2021-02-17',10),(23,23,'uploads_23.wav','2021-02-17',10),(24,23,'uploads_24.wav','2021-02-17',10),(25,23,'uploads_25.wav','2021-02-17',10),(26,23,'uploads_26.wav','2021-02-17',10),(27,23,'uploads_27.wav','2021-02-17',10),(28,23,'uploads_28.wav','2021-02-17',11),(29,23,'uploads_29.wav','2021-02-17',11),(30,23,'uploads_30.wav','2021-02-17',11),(31,23,'uploads_31.wav','2021-02-17',11),(32,23,'uploads_32.wav','2021-02-17',11),(33,23,'uploads_33.wav','2021-02-17',11),(34,23,'uploads_34.wav','2021-02-17',11),(35,23,'uploads_35.wav','2021-02-17',11),(36,23,'uploads_36.wav','2021-02-17',10),(38,16,'uploads_38wav','2021-03-25',0),(43,16,'uploads_43.wav','2021-03-25',11),(44,16,'uploads_44.wav','2021-03-25',11),(45,16,'uploads_45.wav','2021-03-25',11),(46,16,'uploads_46.wav','2021-03-25',11),(47,16,'uploads_47.wav','2021-03-25',10),(48,16,'uploads_48.wav','2021-03-25',10),(49,16,'uploads_49.wav','2021-03-25',10),(51,16,'uploads_51.wav','2021-03-25',9),(52,23,'uploads_52.wav','2021-03-25',9),(53,23,'uploads_53.wav','2021-03-25',9),(54,16,'uploads_54.wav','2021-03-25',11),(55,16,'uploads_55.wav','2021-03-25',10),(56,23,'uploads_56.wav','2021-04-03',9),(57,23,'uploads_57.wav','2021-04-03',9),(58,23,'uploads_58.wav','2021-04-03',9),(59,23,'uploads_59.wav','2021-04-03',9),(60,16,'','2021-04-04',0),(61,16,'uploads_61.wav','2021-04-04',10),(62,16,'uploads_62.wav','2021-04-04',10),(63,23,'uploads_63.wav','2021-04-04',9),(64,23,'uploads_64.wav','2021-04-04',9),(65,23,'uploads_65.wav','2021-04-04',9),(66,23,'uploads_66.wav','2021-04-04',9),(67,23,'uploads_67.wav','2021-04-04',9),(68,23,'uploads_68.wav','2021-04-04',9),(69,16,'uploads_69.wav','2021-04-04',15),(70,16,'uploads_70.wav','2021-04-04',14),(71,16,'uploads_71.wav','2021-04-04',15),(72,16,'uploads_72.wav','2021-04-04',15),(73,16,'uploads_73.wav','2021-04-04',10),(74,16,'uploads_74.wav','2021-04-04',10),(75,16,'uploads_75.wav','2021-04-04',10),(76,16,'uploads_76.wav','2021-04-04',15),(77,16,'uploads_77.wav','2021-04-07',15),(78,23,'uploads_78.wav','2021-04-07',9),(79,33,'','2021-04-12',0),(80,33,'','2021-04-12',0),(81,33,'','2021-04-12',0),(82,33,'','2021-04-12',0),(83,33,'','2021-04-12',0),(84,33,'','2021-04-12',0),(85,33,'','2021-04-15',0),(86,33,'','2021-04-15',0),(87,26,'uploads_87.wav','2021-04-15',14),(88,26,'uploads_88.wav','2021-04-15',14),(89,33,'','2021-04-15',0),(90,33,'','2021-04-15',0),(91,33,'uploads_91.wav','2021-04-15',10),(92,33,'uploads_92.wav','2021-04-15',14),(93,29,'uploads_93.wav','2021-04-15',10),(94,29,'','2021-04-15',0),(95,29,'','2021-04-15',0),(96,29,'','2021-04-15',0);

/*Table structure for table `user_data` */

DROP TABLE IF EXISTS `user_data`;

CREATE TABLE `user_data` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `login_id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `place` varchar(100) DEFAULT NULL,
  `phone` varchar(10) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `photo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;

/*Data for the table `user_data` */

insert  into `user_data`(`user_id`,`login_id`,`name`,`place`,`phone`,`email`,`photo`) values (15,22,'vimal','Kozhikode','9685489769','vimalvyas@gmail.com','20210415-163627.jpg'),(16,23,'Aswin cp','Moozhikal','9961714468','aswin.p1800@gmail.com','20210415-163304.jpg'),(21,28,'Damu','Karaparamba','7387843789','damu@gmail.com','user_21.jpg'),(22,29,'Adhi','Karaparamba','9048036076','adithyamanojv@gmail.com',NULL),(24,31,'athira','','4864684667','athira@gmail.com',NULL),(25,32,'demo','Kollam','9961714468','demo@gmail.com',NULL),(26,33,'Akshay','chevarambalam','7736629883','akshaysajeev68@gmail.com','20210415-151128.jpg');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
