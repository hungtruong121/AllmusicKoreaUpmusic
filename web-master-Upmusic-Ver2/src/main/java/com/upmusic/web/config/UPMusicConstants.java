package com.upmusic.web.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

/**
 * upmusic web에 사용되는 상수 지정
 */
@Slf4j
@Configuration
public class UPMusicConstants {
	
	// Limit
	public static final int PAGE_ARTIST_DEFAULT_LIMIT = 16;
	public static final int PAGE_ARTIST_NEW_LIMIT = 24;
	public static final int PAGE_COLLECTION_DEFAULT_LIMIT = 10;
	public static final int PAGE_COLLECTION_TRACK_LIMIT = 50;
	public static final int PAGE_COMMENT_DEFAULT_LIMIT = 10;
	public static final int PAGE_HOME_MEDIA_LIST_LIMIT = 10;
	public static final int PAGE_MEMBER_DEFAULT_LIMIT = 20;
	public static final int PAGE_MUSIC_DEFAULT_LIMIT = 50;
	public static final int PAGE_MUSIC_ALBUM_LIMIT = 24;
	public static final int PAGE_REQUEST_DEFAULT_LIMIT = 10;
	public static final int PAGE_TRANSACTION_DEFAULT_LIMIT = 20;
	public static final int PAGE_VIDEO_DEFAULT_LIMIT = 10;
	public static final int PAGE_VOCAL_CASTING_DEFAULT_LIMIT = 20;
	public static final String FORMULA_DEFAULT_POPULARITY = "heart_cnt * 7 + hit_cnt * 3";
	public static final String FORMULA_TRACK_POPULARITY = "heart_cnt * 7 + play_cnt * 3";
	public static final int TRACK_IN_LIST_LIMIT = 200;
	
	// Role
	public static final String ROLE_ADMIN = "ADMIN";
	public static final String ROLE_FAMILY = "FAMILY";
	public static final String ROLE_ARTIST = "ARTIST";
	public static final String ROLE_GUIDE = "GUIDE";
	public static final String ROLE_MEMBER = "MEMBER";
	
	// Message
	public static final String PUBLIC_OK = "OK";
	
	// Enum
	public static enum Gender { MALE, FEMALE }
	public static enum MusicAlbumType { SA, GA }
	public static enum MusicTrackType { MR, AR, AR_GUIDE }
	public static enum MusicTrackStatus { BEFORE_EXAM, UNDER_EXAM, ACCEPTED, REJECTED }
	public static enum MusicCooperatorRole { COMPOSER, LYRICIST, ARRANGER, VOCALIST, MUSICIAN }
	public static enum VideoType { MV, GV }
	public static enum LeagueSeasonPeriod { ALL, DAILY, WEEKLY, MONTHLY }
	public static enum StoreOrderStatus { PREPARE, CANCELED, PAY, COMPLETED, REFUNDED }
	public static enum PointTransactionType { LISTEN, LISTEN_ARTIST, UPLOAD, SHARE, EVENT, HYC, FUNDING, LISTEN_ARTIST_SELF, CHARGE }
	public static enum ChargeType { CREDIT, MOBILE, ACCOUNT }

	public static int DEFAULT_GENRE = 0; // ALL
	public static int DEFAULT_THEME = 0; // EXCITING
	public static LeagueSeasonPeriod DEFAULT_PERIOD = LeagueSeasonPeriod.ALL;
	
	// Regex
	public static final Pattern REGEX_EMAIL_ADDRESS =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	// Encryption
	public static final String TOKEN_AES128 = "u5mus1cw2bs1t2g5";
	
	// Cookie prefix
	public static final String COOKIE_ALBUM = "upm_album_";
	public static final String COOKIE_CASTING = "upm_casting_";
	public static final String COOKIE_NOTICE = "upm_notice_";
	public static final String COOKIE_REQUEST = "upm_request_";
	public static final String COOKIE_TERMS = "upm_terms_";
	public static final String COOKIE_TRACK = "upm_track_";
	public static final String COOKIE_VIDEO = "upm_video_";
	public static final String COOKIE_CROWDFUNDING = "crowdFunding_";
	
	
	// Social type
	public static final String SOCIAL_FACEBOOK = "facebook";
	public static final String SOCIAL_GOOGLE = "google";
	public static final String SOCIAL_KAKAO = "kakao";
	public static final String SOCIAL_NAVER = "naver";
	
	// FAQ 분류
	public static final String FAQ_UPLOAD = "1";
	public static final String FAQ_COPYRIGHT = "2";
	public static final String FAQ_UPLEAGUE = "3";
	public static final String FAQ_MUSICSTORE = "4";
	public static final String FAQ_ARTIST = "5";
	public static final String FAQ_PAYMENT = "6";
	public static final String FAQ_CALCULATE = "7";
	public static final String FAQ_REWARD = "8";
	public static final String FAQ_POINT = "9";
	public static final String FAQ_OTHER = "0";
	
	// Point
	public static final float FUNDING_POINT_RATE = 10f; // 전체 포인트의 10%
	
	// Azure
	public static final String AZURE_STORAGE_CONTAINER = "upm-container-resource";
	public static final String AZURE_STORAGE_CONTAINER_ALBUM = "upm-container-album";
	public static final String AZURE_STORAGE_CONTAINER_BULK = "upm-container-bulk";
	public static final String AZURE_STORAGE_CONTAINER_UPLOAD = "upm-container-resource";
	public static String AZURE_STORAGE_CONNECT_STRING_ALBUM = "DefaultEndpointsProtocol=https;AccountName=upmalbum;AccountKey=gZMxlj8zqvz6uoqnIR8+tLFvaTj0F7Oegj66vbuhUDCJvzdbwht8tsgu+x0fniC0AGK5x0qpz4bp3JKyzDDnZA==;EndpointSuffix=core.windows.net";
	
	public static final String AZURE_MEDIA_SERVICES_TENANT = "upmusic.onmicrosoft.com";
	public static final String AZURE_MEDIA_SERVICES_CLIENT_ID = "4978fd78-6285-4eb1-ad04-84daf07c7ab7";
	public static final String AZURE_MEDIA_SERVICES_CLIENT_KEY = "5r4Wnt0Q24VerxQEeXTodb2jPzUCckqzR/o0jXise1Y=";
	public static final String AZURE_MEDIA_SERVICES_REST_API_ENDPOINT = "https://upmmediaservice.restv2.japanwest.media.azure.net/api/";

	@Autowired
	Environment env;


	public static String configFileName = "application.properties";

	@PostConstruct
	public void setProperties() {

		log.debug("setProperties started !!!!!!!!!!!!");

		if (Arrays.asList(env.getActiveProfiles()).contains("dev")) {
			configFileName = "application-dev.properties";
		}

		if (Arrays.asList(env.getActiveProfiles()).contains("prod")) {
			configFileName = "application-prod.properties";
			AZURE_STORAGE_CONNECT_STRING_ALBUM = "DefaultEndpointsProtocol=https;AccountName=upmresourceprod;AccountKey=UI3cuPN4NaTZAUEse92Cesy4k/A/VqkFL06EAu5FiNS6Ium0uXrGEqu26HEcduQQuAsfqILdJwmyvKSOkILkiw==;EndpointSuffix=core.windows.net";

		}

		log.debug("configFileName : " +configFileName);
	}


	// Properties
	public static String loadPropertyValue(String key) throws IOException {
        Properties configuration = new Properties();
        InputStream inputStream = UPMusicConstants.class.getClassLoader().getResourceAsStream(configFileName);
        configuration.load(inputStream);
        inputStream.close();
        return configuration.getProperty(key);
    }
	
}
