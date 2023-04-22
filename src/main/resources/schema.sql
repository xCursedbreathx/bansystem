-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server Version:               11.1.0-MariaDB - mariadb.org binary distribution
-- Server Betriebssystem:        Win64
-- HeidiSQL Version:             12.3.0.6589
-- --------------------------------------------------------

-- Exportiere Struktur von Tabelle bansys2.active_bans
CREATE TABLE IF NOT EXISTS `active_bans` (
  `UUID` varchar(90) NOT NULL DEFAULT '',
  `REASON` varchar(255) DEFAULT '"Banned by a Operator"',
  `BANBY` varchar(90) DEFAULT NULL,
  `BANTYPE` varchar(90) DEFAULT 'global',
  `BANSERVER` varchar(90) DEFAULT NULL,
  `BANUNTIL` bigint(20) DEFAULT NULL,
  `BANFORID` int(11) DEFAULT NULL,
  PRIMARY KEY (`UUID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Struktur von Tabelle bansys2.command_logs
CREATE TABLE IF NOT EXISTS `command_logs` (
  `LOGID` int(11) NOT NULL AUTO_INCREMENT,
  `PERFBY` varchar(90) NOT NULL,
  `PERFTO` varchar(90) NOT NULL,
  `COMMAND` varchar(90) NOT NULL,
  `TIMESTAMP` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`LOGID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Struktur von Tabelle bansys2.player_data
CREATE TABLE IF NOT EXISTS `player_data` (
  `PLAYERID` varchar(90) NOT NULL DEFAULT '"PlayerUUID"',
  `PLAYERNAME` varchar(90) NOT NULL,
  `GLOBALBANNED` tinyint(4) DEFAULT 0,
  PRIMARY KEY (`PLAYERID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Struktur von Tabelle bansys2.punishment_history
CREATE TABLE IF NOT EXISTS `punishment_history` (
  `PUNISHID` int(11) NOT NULL AUTO_INCREMENT,
  `PUNISHUUID` varchar(90) NOT NULL,
  `PUNISHBY` varchar(90) NOT NULL,
  `PUNISHTYPE` varchar(50) NOT NULL,
  `PUNISHAT` bigint(20) DEFAULT current_timestamp(),
  `PUNISHUNTIL` bigint(20) NOT NULL,
  `PUNISHFORID` int(11) NOT NULL,
  PRIMARY KEY (`PUNISHID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
