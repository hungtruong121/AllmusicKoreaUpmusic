/*
***************************************************************************
com.upmusic.web
***************************************************************************
Name: Blockchain Copyright Platform
Version 20180510
***************************************************************************
*/

CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



------------------------------------------------------------------------------------------------------------------------------ MEMBER


--
-- Table structure for table `role`
--
CREATE TABLE IF NOT EXISTS `role` (
  `id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `name` enum('ADMIN','FAMILY','MEMBER') NOT NULL DEFAULT 'MEMBER',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `role`(id, name) VALUES (1,'ADMIN'),(8,'FAMILY'),(9,'MEMBER');
--
-- Table structure for table `member`
--
CREATE TABLE IF NOT EXISTS `member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `uid` varchar(50) NOT NULL,
  `upw` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `profile_image` varchar(50) DEFAULT NULL,
  `role_id` smallint unsigned NOT NULL DEFAULT '9',
  `cookie` varchar(40) DEFAULT NULL,
  `login_cnt` int(11) NOT NULL DEFAULT '0',
  `last_login` datetime,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `member_detail`
--
CREATE TABLE IF NOT EXISTS `member_detail` (
  `member_id` int(10) unsigned NOT NULL,
  `introduction` text DEFAULT NULL,
  `gender` enum('MALE','FEMALE') NOT NULL DEFAULT 'MALE',
  `album_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `video_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `is_copyright_memeber` tinyint(1) NOT NULL DEFAULT '0',
  `is_copyright_trust_memeber` tinyint(1) NOT NULL DEFAULT '0',
-- `following_cnt` smallint unsigned NOT NULL DEFAULT '0',
-- `follower_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `updated_at` datetime,
  PRIMARY KEY (`member_id`),
  FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `member_follow`
--
-- CREATE TABLE IF NOT EXISTS `member_follow` (
--   `member_id` int(10) unsigned NOT NULL,
--   `follower_id` int(10) unsigned NOT NULL,
--   KEY `member_id` (`member_id`),
--   KEY `follower_id` (`follower_id`),
--   CONSTRAINT `member_follow_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
--   CONSTRAINT `member_follow_ibfk_2` FOREIGN KEY (`follower_id`) REFERENCES `member` (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


------------------------------------------------------------------------------------------------------------------------------ Category


--
-- Table structure for table `music_category`
--
CREATE TABLE IF NOT EXISTS `music_category` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` enum('BALLADE','DANCE','HIP_HOP','ROCK','TROT') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `music_category`(id, name) VALUES (1,'BALLADE'),(2,'DANCE'),(3,'HIP_HOP'),(4,'ROCK'),(5,'TROT');
--
-- Table structure for table `music_theme_category`
--
CREATE TABLE IF NOT EXISTS `music_theme_category` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` enum('DRIVE','SPORTS','MEDITATION','PARTY','CAFE','COOKING') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `music_theme_category`(id, name) VALUES (1,'DRIVE'),(2,'SPORTS'),(3,'MEDITATION'),(4,'PARTY'),(5,'CAFE'),(6,'COOKING');


------------------------------------------------------------------------------------------------------------------------------ MUSIC ALBUM & TRACK


--
-- Table structure for table `music_album`
--
CREATE TABLE IF NOT EXISTS `music_album` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `ap` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `cover_filename` varchar(255),
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `released_at` datetime,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_album_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `music_album_comment`
--
CREATE TABLE IF NOT EXISTS `music_album_comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_album_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `music_album_id` (`music_album_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_album_comment_ibfk_1` FOREIGN KEY (`music_album_id`) REFERENCES `music_album` (`id`),
  CONSTRAINT `music_album_comment_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `music_album_heart_record`
--
CREATE TABLE IF NOT EXISTS `music_album_heart_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_album_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `music_album_id` (`music_album_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_album_heart_record_ibfk_1` FOREIGN KEY (`music_album_id`) REFERENCES `music_album` (`id`),
  CONSTRAINT `music_album_heart_record_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `music_track`
--
CREATE TABLE IF NOT EXISTS `music_track` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_category_id` int(10) unsigned NOT NULL,
  `music_theme_category_id` int(10) unsigned DEFAULT NULL,
  `music_album_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `lyric` text,
  `filename` varchar(50),
  `duration` smallint unsigned NOT NULL DEFAULT '0',
  `play_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `for_league` tinyint(1) NOT NULL DEFAULT '1',
  `for_sale` tinyint(1) NOT NULL DEFAULT '1',
  `price` mediumint unsigned NOT NULL DEFAULT '0',
  `sort` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `upload_state` enum('BEFORE_EXAM','UNDER_EXAM', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'BEFORE_EXAM',
  `record_type` enum('AR','MR', 'VOCAL_CASTING') NOT NULL DEFAULT 'AR',
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `music_category_id` (`music_category_id`),
  KEY `music_theme_category_id` (`music_theme_category_id`),
  KEY `music_album_id` (`music_album_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_track_ibfk_1` FOREIGN KEY (`music_category_id`) REFERENCES `music_category` (`id`),
  CONSTRAINT `music_track_ibfk_2` FOREIGN KEY (`music_theme_category_id`) REFERENCES `music_theme_category` (`id`),
  CONSTRAINT `music_track_ibfk_3` FOREIGN KEY (`music_album_id`) REFERENCES `music_album` (`id`),
  CONSTRAINT `music_track_ibfk_4` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `music_track_heart_record`
--
CREATE TABLE IF NOT EXISTS `music_track_heart_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_track_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_track_heart_record_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`),
  CONSTRAINT `music_track_heart_record_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `music_request`
--
CREATE TABLE IF NOT EXISTS `music_request` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `ap` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `price` mediumint unsigned NOT NULL DEFAULT '0',
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_request_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `music_request_comment`
--
CREATE TABLE IF NOT EXISTS `music_request_comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_request_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `music_request_id` (`music_request_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_request_comment_ibfk_1` FOREIGN KEY (`music_request_id`) REFERENCES `music_request` (`id`),
  CONSTRAINT `music_request_comment_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

------------------------------------------------------------------------------------------------------------------------------ LEAGUE

--
-- Table structure for table `league_daily_chart`
-- 랭킹에 사용될 테이블로 리그 일별 순위
-- 크론잡으로 매일 백업 후 truncate 
--
CREATE TABLE IF NOT EXISTS `league_daily_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_track_id` int(10) unsigned NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_daily_chart_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_weekly_chart`
-- 랭킹에 사용될 테이블로 리그 주별 순위
-- 크론잡으로 매주 백업 후 truncate 
--
CREATE TABLE IF NOT EXISTS `league_weekly_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_track_id` int(10) unsigned NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_weekly_chart_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_monthly_chart`
-- 랭킹에 사용될 테이블로 리그 월별 순위
-- 크론잡으로 매월 백업 후 truncate 
--
CREATE TABLE IF NOT EXISTS `league_monthly_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_track_id` int(10) unsigned NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_monthly_chart_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `league_daily_chart_backup`
-- 일단위의 리그 결과 백업
-- 크론잡으로 매일 추가
--
CREATE TABLE IF NOT EXISTS `league_daily_chart_backup` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `backup_date` date NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_daily_chart_backup_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_weekly_chart_backup`
-- 주단위의 리그 결과 백업
-- 크론잡으로 매주 추가
--
CREATE TABLE IF NOT EXISTS `league_weekly_chart_backup` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `backup_date` date NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_weekly_chart_backup_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_monthly_chart_backup`
-- 월단위의 리그 결과 백업
-- 크론잡으로 매월 추가
--
CREATE TABLE IF NOT EXISTS `league_monthly_chart_backup` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `backup_date` date NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_monthly_chart_backup_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `league_season`
--
CREATE TABLE IF NOT EXISTS `league_season` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject` varchar(255) NOT NULL,
  `ap` varchar(255) NOT NULL,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_season_track`
--
CREATE TABLE IF NOT EXISTS `league_season_track` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `league_season_id` int(10) unsigned NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `league_season_id` (`league_season_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_season_track_ibfk_1` FOREIGN KEY (`league_season_id`) REFERENCES `league_season` (`id`),
  CONSTRAINT `league_season_track_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


------------------------------------------------------------------------------------------------------------------------------ VOACAL CASTING


--
-- Table structure for table `vocal_casting`
-- 담기 기능으로 인해 music_track으로 처리하고 부가 컬럼을 추가
--
CREATE TABLE IF NOT EXISTS `vocal_casting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `ap` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `member_id` (`member_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `vocal_casting_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`),
  CONSTRAINT `vocal_casting_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


------------------------------------------------------------------------------------------------------------------------------ VIDEO


--
-- Table structure for table `video`
--
CREATE TABLE IF NOT EXISTS `video` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_category_id` int(10) unsigned,
  `music_track_id` int(10) unsigned,
  `video_category` enum('MV','GV') NOT NULL DEFAULT 'MV',
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `file_name` varchar(50),
  `file_originalname` varchar(200),
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `music_category_id` (`music_category_id`),
  KEY `music_track_id` (`music_track_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `video_ibfk_1` FOREIGN KEY (`music_category_id`) REFERENCES `music_category` (`id`),
  CONSTRAINT `video_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`),
  CONSTRAINT `video_ibfk_3` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `video_comment`
--
CREATE TABLE IF NOT EXISTS `video_comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `video_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `video_id` (`video_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `video_comment_ibfk_1` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`),
  CONSTRAINT `video_comment_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `video_heart_record`
--
CREATE TABLE IF NOT EXISTS `video_heart_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `video_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `video_id` (`video_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `video_heart_record_ibfk_1` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`),
  CONSTRAINT `video_heart_record_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



------------------------------------------------------------------------------------------------------------------------------ COLLECTION

--
-- Table structure for table `collection`
--
CREATE TABLE IF NOT EXISTS `collection` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `ap` varchar(255) NOT NULL,
  `track_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `collection_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `collection_track`
--
CREATE TABLE IF NOT EXISTS `collection_track` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `collection_id` int(10) unsigned NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `sort` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `collection_id` (`collection_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `collection_track_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `collection` (`id`),
  CONSTRAINT `collection_track_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
