CREATE DATABASE  IF NOT EXISTS `gestion_turnos_hospital` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `gestion_turnos_hospital`;
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: gestion_turnos_hospital
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comunicacion`
--

DROP TABLE IF EXISTS `comunicacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicacion` (
  `id_comunicacion` int NOT NULL AUTO_INCREMENT,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo` varchar(30) NOT NULL,
  `contenido` varchar(500) NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id_comunicacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicacion`
--

LOCK TABLES `comunicacion` WRITE;
/*!40000 ALTER TABLE `comunicacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comunicacion_destinatario`
--

DROP TABLE IF EXISTS `comunicacion_destinatario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comunicacion_destinatario` (
  `id_comunicacion` int NOT NULL,
  `id_paciente` int NOT NULL,
  `estado_envio` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id_comunicacion`,`id_paciente`),
  KEY `id_paciente` (`id_paciente`),
  CONSTRAINT `comunicacion_destinatario_ibfk_1` FOREIGN KEY (`id_comunicacion`) REFERENCES `comunicacion` (`id_comunicacion`),
  CONSTRAINT `comunicacion_destinatario_ibfk_2` FOREIGN KEY (`id_paciente`) REFERENCES `paciente` (`id_paciente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comunicacion_destinatario`
--

LOCK TABLES `comunicacion_destinatario` WRITE;
/*!40000 ALTER TABLE `comunicacion_destinatario` DISABLE KEYS */;
/*!40000 ALTER TABLE `comunicacion_destinatario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `disponibilidad`
--

DROP TABLE IF EXISTS `disponibilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `disponibilidad` (
  `id_disponibilidad` int NOT NULL AUTO_INCREMENT,
  `fecha` date NOT NULL,
  `horario` time NOT NULL,
  `estado` varchar(20) NOT NULL DEFAULT 'LIBRE',
  `id_profesional` int NOT NULL,
  `id_lugar` int NOT NULL,
  PRIMARY KEY (`id_disponibilidad`),
  UNIQUE KEY `id_profesional` (`id_profesional`,`fecha`,`horario`),
  KEY `id_lugar` (`id_lugar`),
  CONSTRAINT `disponibilidad_ibfk_1` FOREIGN KEY (`id_profesional`) REFERENCES `profesional` (`id_profesional`),
  CONSTRAINT `disponibilidad_ibfk_2` FOREIGN KEY (`id_lugar`) REFERENCES `lugar_atencion` (`id_lugar`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `disponibilidad`
--

LOCK TABLES `disponibilidad` WRITE;
/*!40000 ALTER TABLE `disponibilidad` DISABLE KEYS */;
INSERT INTO `disponibilidad` VALUES (1,'2026-10-15','09:00:00','OCUPADA',1,1),(2,'2026-10-15','10:00:00','BLOQUEADA',1,1),(3,'2026-10-20','09:00:00','OCUPADA',1,1),(4,'2026-10-25','08:00:00','LIBRE',1,1),(5,'2026-10-25','08:30:00','LIBRE',1,1),(6,'2026-10-25','09:00:00','LIBRE',1,1),(7,'2026-10-25','09:30:00','OCUPADA',1,1),(8,'2026-10-25','10:00:00','OCUPADA',1,1),(9,'2026-10-25','10:30:00','LIBRE',1,1),(10,'2026-10-25','11:00:00','LIBRE',1,1),(11,'2026-10-25','11:30:00','LIBRE',1,1),(12,'2026-11-02','08:00:00','LIBRE',2,1),(13,'2026-11-02','08:30:00','LIBRE',2,1),(14,'2026-11-02','09:00:00','LIBRE',2,1),(15,'2026-11-02','09:30:00','LIBRE',2,1),(16,'2026-11-02','10:00:00','LIBRE',2,1),(17,'2026-11-02','10:30:00','LIBRE',2,1),(18,'2026-11-02','11:00:00','LIBRE',2,1),(19,'2026-11-02','11:30:00','LIBRE',2,1),(20,'2026-11-10','08:00:00','LIBRE',4,1),(21,'2026-11-10','08:30:00','LIBRE',4,1),(22,'2026-11-10','09:00:00','LIBRE',4,1),(23,'2026-11-10','09:30:00','LIBRE',4,1),(24,'2026-11-10','10:00:00','LIBRE',4,1),(25,'2026-11-10','10:30:00','LIBRE',4,1),(26,'2026-11-10','11:00:00','OCUPADA',4,1),(27,'2026-11-10','11:30:00','LIBRE',4,1),(28,'2026-11-20','08:00:00','LIBRE',1,1),(29,'2026-11-20','08:30:00','LIBRE',1,1),(30,'2026-11-20','09:00:00','LIBRE',1,1),(31,'2026-11-20','09:30:00','LIBRE',1,1),(32,'2026-11-20','10:00:00','LIBRE',1,1),(33,'2026-11-20','10:30:00','LIBRE',1,1),(34,'2026-11-20','11:00:00','LIBRE',1,1),(35,'2026-11-20','11:30:00','LIBRE',1,1),(36,'2026-11-15','08:00:00','LIBRE',3,1),(37,'2026-11-15','08:30:00','LIBRE',3,1),(38,'2026-11-15','09:00:00','LIBRE',3,1),(39,'2026-11-15','09:30:00','LIBRE',3,1),(40,'2026-11-15','10:00:00','LIBRE',3,1),(41,'2026-11-15','10:30:00','LIBRE',3,1),(42,'2026-11-15','11:00:00','LIBRE',3,1),(43,'2026-11-15','11:30:00','LIBRE',3,1),(44,'2026-11-20','08:00:00','LIBRE',3,1),(45,'2026-11-20','08:30:00','LIBRE',3,1),(46,'2026-11-20','09:00:00','LIBRE',3,1),(47,'2026-11-20','09:30:00','LIBRE',3,1),(48,'2026-11-20','10:00:00','LIBRE',3,1),(49,'2026-11-20','10:30:00','LIBRE',3,1),(50,'2026-11-20','11:00:00','LIBRE',3,1),(51,'2026-11-20','11:30:00','LIBRE',3,1),(60,'2026-11-09','08:00:00','LIBRE',3,1),(61,'2026-11-22','08:00:00','LIBRE',4,1),(62,'2026-11-25','08:00:00','LIBRE',5,1),(63,'2026-11-25','08:45:00','LIBRE',5,1),(64,'2026-11-25','09:30:00','LIBRE',5,1),(65,'2026-11-25','10:15:00','LIBRE',5,1),(66,'2026-11-25','11:00:00','LIBRE',5,1);
/*!40000 ALTER TABLE `disponibilidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `especialidad`
--

DROP TABLE IF EXISTS `especialidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `especialidad` (
  `id_especialidad` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_especialidad`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `especialidad`
--

LOCK TABLES `especialidad` WRITE;
/*!40000 ALTER TABLE `especialidad` DISABLE KEYS */;
INSERT INTO `especialidad` VALUES (1,'Cardiología'),(7,'Dermatología'),(5,'Ginecología'),(9,'Infectología'),(6,'Pediatría'),(8,'Traumatología');
/*!40000 ALTER TABLE `especialidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lista_espera`
--

DROP TABLE IF EXISTS `lista_espera`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lista_espera` (
  `id_lista_espera` int NOT NULL AUTO_INCREMENT,
  `fecha_solicitud` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  `id_paciente` int NOT NULL,
  `id_especialidad` int NOT NULL,
  PRIMARY KEY (`id_lista_espera`),
  KEY `id_paciente` (`id_paciente`),
  KEY `id_especialidad` (`id_especialidad`),
  CONSTRAINT `lista_espera_ibfk_1` FOREIGN KEY (`id_paciente`) REFERENCES `paciente` (`id_paciente`),
  CONSTRAINT `lista_espera_ibfk_2` FOREIGN KEY (`id_especialidad`) REFERENCES `especialidad` (`id_especialidad`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lista_espera`
--

LOCK TABLES `lista_espera` WRITE;
/*!40000 ALTER TABLE `lista_espera` DISABLE KEYS */;
/*!40000 ALTER TABLE `lista_espera` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lugar_atencion`
--

DROP TABLE IF EXISTS `lugar_atencion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lugar_atencion` (
  `id_lugar` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `direccion` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_lugar`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lugar_atencion`
--

LOCK TABLES `lugar_atencion` WRITE;
/*!40000 ALTER TABLE `lugar_atencion` DISABLE KEYS */;
INSERT INTO `lugar_atencion` VALUES (1,'Hospital Dr. Fernando Rocha','Luis Beltrán, Río Negro'),(2,'CAPS Jorge D. Guenchenen','Barrio 40 Viviendas, Luis Beltrán'),(3,'CAPS Esperanza (CIC)','Luis Beltrán, Río Negro');
/*!40000 ALTER TABLE `lugar_atencion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `paciente`
--

DROP TABLE IF EXISTS `paciente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `paciente` (
  `id_paciente` int NOT NULL AUTO_INCREMENT,
  `documento` varchar(20) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_paciente`),
  UNIQUE KEY `documento` (`documento`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `paciente`
--

LOCK TABLES `paciente` WRITE;
/*!40000 ALTER TABLE `paciente` DISABLE KEYS */;
INSERT INTO `paciente` VALUES (1,'30111222','Ana','Pérez','2984123456','ana.perez@email.com',1),(3,'35669887','Claudina','Bini','29845556030','Claudibin@gmail.com',1),(4,'32666148','Marilú','Fofiña','211405405','fofilu@email.com',1),(5,'38666155','Carlos','Victorino','2334552039','cvic@gmail.com',1),(6,'70000001','Martino','Alvanio','29845556060','Mal@gmail.com',1),(7,'40555666','Pauli','Polens','2984555222','Polens@gmail.com',1),(9,'60000001','Piero','Zaso','222111444','PZ@gmail.com',1);
/*!40000 ALTER TABLE `paciente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `personal_administrativo`
--

DROP TABLE IF EXISTS `personal_administrativo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `personal_administrativo` (
  `id_personal` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_personal`),
  UNIQUE KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `personal_administrativo_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personal_administrativo`
--

LOCK TABLES `personal_administrativo` WRITE;
/*!40000 ALTER TABLE `personal_administrativo` DISABLE KEYS */;
/*!40000 ALTER TABLE `personal_administrativo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profesional`
--

DROP TABLE IF EXISTS `profesional`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `profesional` (
  `id_profesional` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `apellido` varchar(50) NOT NULL,
  `id_especialidad` int NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_profesional`),
  KEY `id_especialidad` (`id_especialidad`),
  CONSTRAINT `profesional_ibfk_1` FOREIGN KEY (`id_especialidad`) REFERENCES `especialidad` (`id_especialidad`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profesional`
--

LOCK TABLES `profesional` WRITE;
/*!40000 ALTER TABLE `profesional` DISABLE KEYS */;
INSERT INTO `profesional` VALUES (1,'Laura','Gómez',1,1),(2,'Susana María','Briher',5,1),(3,'Fabricio','Calvi',1,1),(4,'Marcia Lina','Dellapiel',7,1),(5,'Ignacio','Verrugo',7,1),(6,'Carmela','Asprela',6,1);
/*!40000 ALTER TABLE `profesional` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turno`
--

DROP TABLE IF EXISTS `turno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turno` (
  `id_turno` int NOT NULL AUTO_INCREMENT,
  `fecha_asignacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(20) NOT NULL DEFAULT 'ACTIVO',
  `id_paciente` int NOT NULL,
  `id_disponibilidad` int NOT NULL,
  PRIMARY KEY (`id_turno`),
  KEY `id_paciente` (`id_paciente`),
  KEY `id_disponibilidad` (`id_disponibilidad`),
  CONSTRAINT `turno_ibfk_1` FOREIGN KEY (`id_paciente`) REFERENCES `paciente` (`id_paciente`),
  CONSTRAINT `turno_ibfk_2` FOREIGN KEY (`id_disponibilidad`) REFERENCES `disponibilidad` (`id_disponibilidad`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turno`
--

LOCK TABLES `turno` WRITE;
/*!40000 ALTER TABLE `turno` DISABLE KEYS */;
INSERT INTO `turno` VALUES (1,'2026-09-23 23:50:32','ACTIVO',1,1),(2,'2026-10-18 11:24:08','ACTIVO',3,3),(3,'2026-10-18 11:53:52','ACTIVO',1,8),(4,'2026-10-18 11:56:56','ACTIVO',4,7),(5,'2026-11-07 12:27:30','CANCELADO',7,36),(6,'2026-11-07 12:29:42','CANCELADO',7,37),(7,'2026-11-08 11:04:47','ACTIVO',7,26);
/*!40000 ALTER TABLE `turno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre_usuario` varchar(50) NOT NULL,
  `clave_hash` varchar(255) NOT NULL,
  `rol` varchar(30) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `nombre_usuario` (`nombre_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-08 13:34:54
