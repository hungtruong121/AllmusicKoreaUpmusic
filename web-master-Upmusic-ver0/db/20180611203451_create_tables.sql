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
  `name` enum('ADMIN','FAMILY','ARTIST','GUIDE','MEMBER') NOT NULL DEFAULT 'MEMBER',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `role`(id, name) VALUES (1,'ADMIN'),(6,'FAMILY'),(7,'ARTIST'),(8,'GUIDE'),(9,'MEMBER');
--
-- Table structure for table `member`
--
CREATE TABLE IF NOT EXISTS `member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `phone_number` varchar(50) NOT NULL,
  `nick` varchar(50) NOT NULL,
  `birthday` varchar(8) NOT NULL,
  `profile_image` varchar(50) DEFAULT NULL,
  `gender` enum('MALE','FEMALE') NOT NULL DEFAULT 'MALE',
  `role_id` smallint unsigned NOT NULL DEFAULT '9',
  `receive_email` tinyint(1) NOT NULL DEFAULT '1',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
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
-- Table structure for table `member_playtime`
--
CREATE TABLE IF NOT EXISTS `member_playtime` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `playtime` text DEFAULT NULL,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`)
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



------------------------------------------------------------------------------------------------------------------------------ Genre & Theme

--
-- Table structure for table `genre`
--
CREATE TABLE IF NOT EXISTS `genre` (
  `id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `name` enum('BALLADE', 'DANCE', 'HIP_HOP', 'ROCK_METAL', 'JAZZ', 'INDIE', 'ADULT', 'BGM', 'NEWAGE', 'RELIGION', 'CHILD', 'PRENATAL', 'COMIC', 'ETC'),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `genre`(id, name) VALUES (1,'BALLADE'), (2, 'DANCE'), (3, 'HIP_HOP'), (4, 'ROCK_METAL'), (5, 'JAZZ'), (6, 'INDIE'), (7, 'ADULT'), (8, 'BGM'), (9, 'NEWAGE'), (10, 'RELIGION'), (11, 'CHILD'), (12, 'PRENATAL'), (13, 'COMIC'), (14, 'ETC');


--
-- Table structure for table `theme`
--
CREATE TABLE IF NOT EXISTS `theme` (
  `id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `name` enum('EXCITING', 'SEDATE', 'PELLUCID', 'GRANDIOSO', 'MOODY', 'DOLEFUL', 'SEXY', 'TRIPPY', 'UNIQUE', 'ACOUSTIC', 'SWEET', 'LOVELY', 'NOISY', 'CUTE', 'SCARY', 'SOB', 'FUNNY', 'SOMNIFIC', 'PENETRATING', 'LONGING', 'LONELY', 'DEPRESSING'),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `theme`(id, name) VALUES (1,'EXCITING'),(2,'SEDATE'),(3,'PELLUCID'),(4,'GRANDIOSO'),(5,'MOODY'),(6,'DOLEFUL'),(7,'SEXY'),(8,'TRIPPY'),(9,'UNIQUE'),(10,'ACOUSTIC'),(11,'SWEET'),(12,'LOVELY'),(13,'NOISY'),(14,'CUTE'),(15,'SCARY'),(16,'SOB'),(17,'FUNNY'),(18,'SOMNIFIC'),(19,'PENETRATING'),(20,'LONGING'),(21,'LONELY'),(22,'DEPRESSING');


------------------------------------------------------------------------------------------------------------------------------ MUSIC ALBUM & TRACK


--
-- Table structure for table `music_album`
--
CREATE TABLE IF NOT EXISTS `music_album` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `description` text,
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
  `in_league` tinyint(1) NOT NULL DEFAULT '1',
  `in_store` tinyint(1) NOT NULL DEFAULT '1',
  `price` mediumint unsigned NOT NULL DEFAULT '0',
  `sort_no` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `track_status` enum('BEFORE_EXAM','UNDER_EXAM', 'ACCEPTED', 'REJECTED') NOT NULL DEFAULT 'BEFORE_EXAM',
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
-- Table structure for table `music_track_cooperator`
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
-- Table structure for table `music_track_play_record`
--
CREATE TABLE IF NOT EXISTS `music_track_play_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_track_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `played_at` datetime,
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `music_track_id` (`music_track_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `music_track_play_record_ibfk_1` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`),
  CONSTRAINT `music_track_play_record_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `music_request`
--
CREATE TABLE IF NOT EXISTS `music_request` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
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
-- Table structure for table `league_time_chart`
-- 랭킹에 사용될 테이블로 리그 전체 순위
-- 크론잡으로 매시간 업데이트
--
CREATE TABLE IF NOT EXISTS `league_time_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_track_id` int(10) unsigned NOT NULL,
  `play_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `league_season_id` (`league_season_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_time_chart_ibfk_1` FOREIGN KEY (`league_season_id`) REFERENCES `league_season` (`id`),
  CONSTRAINT `league_time_chart_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_daily_chart`
-- 랭킹에 사용될 테이블로 리그 일별 순위
-- 크론잡으로 저장 
--
CREATE TABLE IF NOT EXISTS `league_daily_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `league_season_id` int(10) unsigned NOT NULL,
  `backup_date` date NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `play_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `league_season_id` (`league_season_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_daily_chart_ibfk_1` FOREIGN KEY (`league_season_id`) REFERENCES `league_season` (`id`),
  CONSTRAINT `league_daily_chart_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_weekly_chart`
-- 랭킹에 사용될 테이블로 리그 주별 순위
-- 크론잡으로 저장 
--
CREATE TABLE IF NOT EXISTS `league_weekly_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `league_season_id` int(10) unsigned NOT NULL,
  `backup_date` date NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `play_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `league_season_id` (`league_season_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_weekly_chart_ibfk_1` FOREIGN KEY (`league_season_id`) REFERENCES `league_season` (`id`),
  CONSTRAINT `league_weekly_chart_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_monthly_chart`
-- 랭킹에 사용될 테이블로 리그 월별 순위
-- 크론잡으로 저장 
--
CREATE TABLE IF NOT EXISTS `league_monthly_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `league_season_id` int(10) unsigned NOT NULL,
  `backup_date` date NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `play_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `league_season_id` (`league_season_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_monthly_chart_ibfk_1` FOREIGN KEY (`league_season_id`) REFERENCES `league_season` (`id`),
  CONSTRAINT `league_monthly_chart_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_season_chart`
-- 랭킹에 사용될 테이블로 리그 시즌별 순위
--
CREATE TABLE IF NOT EXISTS `league_season_chart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `league_season_id` int(10) unsigned NOT NULL,
  `backup_date` date NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `play_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `league_season_id` (`league_season_id`),
  KEY `music_track_id` (`music_track_id`),
  CONSTRAINT `league_season_chart_ibfk_1` FOREIGN KEY (`league_season_id`) REFERENCES `league_season` (`id`),
  CONSTRAINT `league_season_chart_ibfk_2` FOREIGN KEY (`music_track_id`) REFERENCES `music_track` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_season`
--
CREATE TABLE IF NOT EXISTS `league_season` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subject` varchar(255) NOT NULL,
  `open_date` date,
  `close_date` date,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `league_season_track`
--
CREATE TABLE IF NOT EXISTS `league_season_track` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_track_id` int(10) unsigned NOT NULL,
  `play_cnt` int unsigned NOT NULL DEFAULT '0',
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
--
CREATE TABLE IF NOT EXISTS `vocal_casting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `description` text,
  `filename` varchar(255),
  `hit_cnt` int unsigned NOT NULL DEFAULT '0',
  `heart_cnt` smallint unsigned NOT NULL DEFAULT '0',
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `vocal_casting_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `vocal_casting_comment`
--
CREATE TABLE IF NOT EXISTS `vocal_casting_comment` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `vocal_casting_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `ip` varchar(20) DEFAULT NULL,
  `content` text NOT NULL,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `vocal_casting_id` (`vocal_casting_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `vocal_casting_comment_ibfk_1` FOREIGN KEY (`vocal_casting_id`) REFERENCES `vocal_casting` (`id`),
  CONSTRAINT `vocal_casting_comment_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- Table structure for table `vocal_casting_heart_record`
--
CREATE TABLE IF NOT EXISTS `vocal_casting_heart_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `vocal_casting_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `vocal_casting_id` (`vocal_casting_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `vocal_casting_heart_record_ibfk_1` FOREIGN KEY (`vocal_casting_id`) REFERENCES `vocal_casting` (`id`),
  CONSTRAINT `vocal_casting_heart_record_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


------------------------------------------------------------------------------------------------------------------------------ VIDEO


--
-- Table structure for table `video`
--
CREATE TABLE IF NOT EXISTS `video` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `music_category_id` int(10) unsigned,
  `music_track_id` int(10) unsigned,
  `video_type` enum('MV','GV') NOT NULL DEFAULT 'MV',
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
  `file_name` varchar(50),
  `file_originalname` varchar(200),
  `description` text,
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
--
-- Table structure for table `video_play_record`
--
CREATE TABLE IF NOT EXISTS `video_play_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `video_id` int(10) unsigned NOT NULL,
  `member_id` int(10) unsigned NOT NULL,
  `played_at` datetime,
  `created_at` datetime,
  PRIMARY KEY (`id`),
  KEY `video_id` (`video_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `video_play_record_ibfk_1` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`),
  CONSTRAINT `video_play_record_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



------------------------------------------------------------------------------------------------------------------------------ COLLECTION

--
-- Table structure for table `collection`
--
CREATE TABLE IF NOT EXISTS `collection` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` int(10) unsigned NOT NULL,
  `subject` varchar(255) NOT NULL,
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

------------------------------------------------------------------------------------------------------------------------------ STORE

--
-- Table structure for table `store_license`
--
CREATE TABLE IF NOT EXISTS `store_license` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `price` mediumint unsigned NOT NULL DEFAULT '0',
  `track_type` varchar(255),
  `description` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `store_license`(id, name, price, track_type, description) VALUES (1,'브론즈 라이센스',20000,'MP3','1. 수령하게 되는 파일: MP3\n2. 상업적인 발매: 가능\n3. 배포 횟수: 3,000건\n4. 수익성 공연, 방송: 불가능');
INSERT INTO `store_license`(id, name, price, track_type, description) VALUES (2,'실버 라이센스',50000,'MP3,WAV','1. 수령하게 되는 파일: MP3, WAV\n2. 상업적인 발매: 가능\n3. 배포 횟수: 5,000건\n4. 수익성 공연, 방송: 가능 (단, 유튜브를 통한 수익 활동 – 불가능)');
INSERT INTO `store_license`(id, name, price, track_type, description) VALUES (3,'골드 라이센스',80000,'MP3,WAV,ZIP','1. 수령하게 되는 파일: MP3, WAV, TRACK OUT (설명: 각 트랙 별로 분리된 멀티 소스)\n2. 상업적인 발매: 가능\n3. 배포 횟수: 10,000건\n4. 수익성 공연, 방송: 가능 (단, 유튜브를 통한 수익 활동 – 불가능)\n5. 리믹스, 재편곡, 특정 멜로디 제거, 드럼 재 프로그래밍 등의 재구성: 가능');
INSERT INTO `store_license`(id, name, price, track_type, description) VALUES (4,'플래티넘 라이센스',120000,'MP3,WAV,ZIP','1. 수령하게 되는 파일: MP3, WAV, TRACK OUT, 계약서\n2. 상업적인 발매: 가능\n3. 배포 횟수: 무제한\n4. 수익성 공연, 방송: 무제한 가능\n5. 리믹스, 재편곡, 특정 멜로디 제거, 드럼 재 프로그래밍 등의 재구성: 가능');
INSERT INTO `store_license`(id, name, price, track_type, description) VALUES (5,'독점 라이센스',0,'MP3','1. 수령하게 되는 파일: MP3, WAV, TRACK OUT, 계약서\n2. 상업적인 발매: 가능\n3. 배포 횟수: 무제한\n4. 수익성 공연, 방송: 무제한 가능\n5. 리믹스, 재편곡, 특정 멜로디 제거, 드럼 재 프로그래밍 등의 재구성: 가능\n6. 음원 소유권을 완전히 가지는 독점 라이센스. 구매하게 되면 결제가 이루어지고 음원 전송이 끝난 기점으로 판매가 중단되므로, 구매 후 다른 고객들은 더 이상 구매할 수 없음.');

--
-- Table structure for table `store_order_license`
--
CREATE TABLE IF NOT EXISTS `store_order_license` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `code` varchar(17) NOT NULL,
  `music_track_id` int(10) unsigned NOT NULL,
  `store_license_id` int(10) unsigned NOT NULL,
  `seller_id` int(10) unsigned NOT NULL,
  `buyer_id` int(10) unsigned NOT NULL,
  `price` decimal(10,2) unsigned NOT NULL DEFAULT '0.00',
  `status` enum('PREPARE','CANCELED','PAY','COMPLETED','REFUNDED') NOT NULL DEFAULT 'PREPARE',
  `paid_at` datetime,
  `created_at` datetime,
  `updated_at` datetime,
  PRIMARY KEY (`id`),
  KEY `store_license_id` (`store_license_id`),
  CONSTRAINT `store_order_license_ibfk_1` FOREIGN KEY (`store_license_id`) REFERENCES `store_license` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;