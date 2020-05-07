INSERT INTO `role`(id, name) VALUES (1,'ROLE_ADMIN'),(6,'ROLE_FAMILY'),(7,'ROLE_GUIDE'),(8,'ROLE_ARTIST'),(9,'ROLE_MEMBER');
INSERT INTO `genre`(id, name) VALUES (1,'BALLADE'),(2,'DANCE'),(3,'HIP_HOP'),(4,'ELECTRONIC'),(5,'ROCK_METAL'),(6,'JAZZ'),(7,'INDIE'),(8,'ADULT'),(9,'BGM'),(10,'NEWAGE'),(11,'RELIGION'),(12,'CHILD'),(13,'PRENATAL'),(14,'COMIC'),(15,'POP'),(16,'RNB_SOUL'),(17,'FOLK'),(18,'BLUES'),(19,'COUNTRY'),(20,'ETC');
INSERT INTO `theme`(id, name) VALUES (1,'SHOP'),(2,'DRIVE'),(3,'CLUB'),(4,'SPORTS'),(5,'CAFE'),(6,'ETC');
INSERT INTO `store_license`(id, name, price, track_type, description) VALUES (1,'임대 라이센스',0,'MP3,WAV,ZIP','<li>1. 수령하게 되는 파일: MP3, WAV, TRACK OUT, 계약서</li><li>2. 상업적인 발매: 가능</li><li>3. 배포 횟수: 무제한</li><li>4. 수익성 공연, 방송: 무제한 가능</li><li>5. 리믹스, 재편곡, 특정 멜로디 제거, 드럼 재 프로그래밍 등의 재구성: 가능</li>');
INSERT INTO `store_license`(id, name, price, track_type, description) VALUES (2,'독점 라이센스',0,'MP3,WAV,ZIP','<li>1. 수령하게 되는 파일: MP3, WAV, TRACK OUT, 계약서</li><li>2. 상업적인 발매: 가능</li><li>3. 배포 횟수: 무제한</li><li>4. 수익성 공연, 방송: 무제한 가능</li><li>5. 리믹스, 재편곡, 특정 멜로디 제거, 드럼 재 프로그래밍 등의 재구성: 가능</li><li>6. 음원 소유권을 완전히 가지는 독점 라이센스. 구매하게 되면 결제가 이루어지고 음원 전송이 끝난 기점으로 판매가 중단되므로, 구매 후 다른 고객들은 더 이상 구매할 수 없음.</li>');
#INSERT INTO `store_order_license`(id, buyer_id, code, music_track_id, paid_at, price, seller_id, order_status, store_license_id, created_at, updated_at) VALUES (1,3,'20180606112345354',29,'2018-06-06 11:23:23',100000,11,3,3,'2018-06-06 11:23:08','2018-06-06 11:23:08');
INSERT INTO `reward`(id, name, payments, playtime) VALUES (1,'1단계',3000,240),(2,'2단계',2000,360),(3,'3단계',1000,60);

update upmusic.member set copyright_membership=0 where id>0;
alter table upmusic.music_request modify column price varchar(10) NOT NULL DEFAULT '0';

INSERT INTO `point_reward_condition`(id, listen_first_step_point, listen_first_step_limit, listen_second_step_point, listen_second_step_limit, listen_third_step_point, listen_third_step_limit, listen_artist_point, upload_point, share_point, listen_artist_self_point, listen_artist_self_limit) VALUES (1,0.1,180,0.05,120,0.01,0,0.02,1000,200,0.02,0);
UPDATE upmusic.member set upm_point=0.000, funding_point=0.000, hyc=0 where id>0;

INSERT INTO `guide_vocal_scope`(id, name) VALUES (1,'GUIDE'),(2,'CHORUS'),(3,'RAP'); 

UPDATE upmusic.music_track set in_recent=1 where id>0;

UPDATE upmusic.point_transaction set removed=0 where id>0;

# Dummy
INSERT INTO `recommended_video`(id, video_id, created_at) VALUES (1,497,'2018-06-07 21:29:18'),(2,503,'2018-06-08 21:29:18'),(3,519,'2018-06-09 21:29:18');
INSERT INTO `recommended_music_track`(id, music_track_id, created_at) VALUES (1,192,'2018-06-07 21:29:18'),(2,204,'2018-06-08 21:29:18'),(3,216,'2018-06-09 21:29:18');

INSERT INTO `terms`(id, subject, content, hit_cnt, created_at, updated_at) VALUES (1,'이용약관','내용',0,'2018-09-27 01:29:18','2018-09-27 01:29:18');
INSERT INTO `terms`(id, subject, content, hit_cnt, created_at, updated_at) VALUES (2,'개인정보처리방침','내용',0,'2018-09-27 01:29:18','2018-09-27 01:29:18');

## Use Video URL
# DROP TABLE `video_play_record`;
# DROP TABLE `video_heart_record`;
# DROP TABLE `video_comment`;
# DROP TABLE `recommended_video`;
# DROP TABLE `video`;
# UPDATE upmusic.member set video_cnt=0 where id>0;