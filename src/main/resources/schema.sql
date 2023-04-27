-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server Version:               11.1.0-MariaDB - mariadb.org binary distribution
-- Server Betriebssystem:        Win64
-- HeidiSQL Version:             12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Exportiere Struktur von Tabelle bansys2.active_global_bans
CREATE TABLE IF NOT EXISTS `active_global_bans` (
  `uuid` varchar(90) NOT NULL DEFAULT '',
  `reason` varchar(255) DEFAULT '"Banned by a Operator"',
  `banby` varchar(90) DEFAULT NULL,
  `banuntil` bigint(20) DEFAULT NULL,
  `banforid` int(11) DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Daten Export vom Benutzer nicht ausgewählt

-- Exportiere Struktur von Tabelle bansys2.active_server_bans
CREATE TABLE IF NOT EXISTS `active_server_bans` (
  `sbanid` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(90) NOT NULL DEFAULT '0',
  `sbanreason` varchar(90) NOT NULL DEFAULT '0',
  `servername` varchar(90) NOT NULL DEFAULT '0',
  `sbanby` varchar(90) NOT NULL DEFAULT '0',
  `sbanuntil` bigint(20) NOT NULL DEFAULT 0,
  `sbanforid` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`sbanid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Daten Export vom Benutzer nicht ausgewählt

-- Exportiere Struktur von Tabelle bansys2.command_logs
CREATE TABLE IF NOT EXISTS `command_logs` (
  `logid` int(11) NOT NULL AUTO_INCREMENT,
  `perfby` varchar(90) NOT NULL,
  `perfto` varchar(90) NOT NULL,
  `command` varchar(90) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`logid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Daten Export vom Benutzer nicht ausgewählt

-- Exportiere Struktur von Tabelle bansys2.player_data
CREATE TABLE IF NOT EXISTS `player_data` (
  `uuid` varchar(90) NOT NULL,
  `playername` varchar(90) NOT NULL,
  `protected` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Daten Export vom Benutzer nicht ausgewählt

-- Exportiere Struktur von Tabelle bansys2.punishment_history
CREATE TABLE IF NOT EXISTS `punishment_history` (
  `punishid` int(11) NOT NULL AUTO_INCREMENT,
  `punishuuid` varchar(90) NOT NULL,
  `punishby` varchar(90) NOT NULL,
  `punishtype` varchar(50) NOT NULL,
  `punishat` bigint(20) DEFAULT current_timestamp(),
  `punishuntil` bigint(20) NOT NULL,
  `punishforid` int(11) NOT NULL,
  PRIMARY KEY (`punishid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Daten Export vom Benutzer nicht ausgewählt

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
