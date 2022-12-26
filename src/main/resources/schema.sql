-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server Version:               10.6.7-MariaDB - mariadb.org binary distribution
-- Server Betriebssystem:        Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Exportiere Datenbank Struktur für bansystem
CREATE DATABASE IF NOT EXISTS `bansystem` /*!40100 DEFAULT CHARACTER SET utf8mb3 */;
USE `bansystem`;

-- Exportiere Struktur von Tabelle bansystem.active_bans
CREATE TABLE IF NOT EXISTS `active_bans` (
  `uuid` varchar(40) NOT NULL,
  `playername` varchar(25) NOT NULL,
  `reason` varchar(255) NOT NULL,
  `bannedby` varchar(25) NOT NULL,
  `banneduntil` bigint(20) NOT NULL DEFAULT -1,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Daten Export vom Benutzer nicht ausgewählt

-- Exportiere Struktur von Tabelle bansystem.ban_history
CREATE TABLE IF NOT EXISTS `ban_history` (
  `banhistoryid` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(40) NOT NULL,
  `playername` varchar(25) NOT NULL,
  `reason` varchar(255) NOT NULL,
  `bannedby` varchar(25) NOT NULL,
  `bannedat` bigint(20) NOT NULL,
  `banneduntil` bigint(20) NOT NULL,
  PRIMARY KEY (`banhistoryid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Daten Export vom Benutzer nicht ausgewählt

-- Exportiere Struktur von Tabelle bansystem.player_data
CREATE TABLE IF NOT EXISTS `player_data` (
  `uuid` varchar(40) NOT NULL,
  `playername` varchar(25) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- Daten Export vom Benutzer nicht ausgewählt

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
