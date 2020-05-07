package com.upmusic.web.helper;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;

import com.upmusic.web.config.UPMusicConstants;


public class UPMusicHelper {
	
	public static byte[] _ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	

	/**
	 * 이메일주소 정규식 검사
	 * @param email
	 * @return boolean
	 */
	public static boolean validateEmail(String email) {
		Matcher matcher = UPMusicConstants.REGEX_EMAIL_ADDRESS.matcher(email);
		return matcher.find();
	}
	
	/**
	 * 파일명 등을 url로 읽을 수 있도록 변환
	 * @param name
	 * @return String
	 */
	public static String makeReadableUrl(String name) {
		StringBuffer sb = new StringBuffer();
		for (int i=0 ; i< name.length(); i++) {
			if (Character.isLetterOrDigit(name.charAt(i)) || name.charAt(i) == '.') {
				sb.append(name.charAt(i));
			} else {
				sb.append("_");
			}
		}
		return sb.toString();
//		String name_url = name.trim().replaceAll("[^a-zA-Zㄱ-ㅎ가-힣0-9\\-\\s\\.]", "");
//		name_url = name_url.replaceAll("[\\-| |\\.]+", "-");
//		return name_url.toLowerCase();
	}
	
	/**
	 * 초 단위의 재생시간을 분과 초로 변환
	 * @param seconds
	 * @return time
	 */
	public static String secondsToTime(int seconds) {
		if (0 >= seconds) return "0";
		return String.format("%02d:%02d", seconds / 60, seconds % 60);
	}
	public static String secondsToTime(float seconds) {
		if (0 >= seconds) return "0";
		return String.format("%02d:%02d", (int) seconds / 60, (int) seconds % 60);
	}
	public static String secondsToTime(Long seconds) {
		if (0 >= seconds) return "0";
		return String.format("%02d:%02d", seconds.intValue() / 60, seconds.intValue() % 60);
	}
	
	/**
	 * 일시를 일 형식으로 반환 
	 * @param time
	 * @return string
	 */
	public static String formattedTimeDay(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(time);
	}
	public static String formattedTimeDotDay(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		return sdf.format(time);
	}
	
	/**
	 * 일시를 일 시:분 형식으로 반환 
	 * @param time
	 * @return string
	 */
	public static String formattedTimeHourMin(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(time);
	}
	
	/**
	 * 가격 입력값을 콤마를 포함한 형식으로 반환
	 * @param price
	 * @return price
	 */
	public static String intToPrice(int price) {
		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(price);
	}
	public static String longToPrice(long price) {
		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(price);
	}
	public static String floatToPrice(float price) {
		DecimalFormat df = new DecimalFormat("###,###.###");
		return df.format(price);
	}
	public static String decimalToPrice(BigDecimal price) {
		DecimalFormat df = new DecimalFormat("###,###.###");
		return df.format(price);
	}
	
	public static String baseUrl(HttpServletRequest request) throws MalformedURLException {
		URL requestURL = new URL(request.getRequestURL().toString());
	    String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
	    // load-blancer 문제
    	// return requestURL.getProtocol() + "://" + requestURL.getHost() + port;
	    return "https://" + requestURL.getHost() + port;
	}
	
	/**
	 * AES 암호화된 텍스트를 반환
	 * @param txt
	 * @param key : 길이에 따라 128 / 192 / 256bit 처리
	 * @return encrypted string
	 * @throws java.io.UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String encryptAES(String txt, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,	IllegalBlockSizeException, BadPaddingException {
		byte[] txtBytes = txt.getBytes(StandardCharsets.UTF_8);
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(_ivBytes);
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
		return new String(Base64.encodeBase64(cipher.doFinal(txtBytes)));
	}

	/**
	 * AES 복호화된 텍스트를 반환
	 * @param txt
	 * @param key : 길이에 따라 128 / 192 / 256bit 처리
	 * @return decrypted string
	 * @throws java.io.UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String decryptAES(String txt, String key)	throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] txtBytes = Base64.decodeBase64(txt.getBytes(StandardCharsets.UTF_8));
		AlgorithmParameterSpec ivSpec = new IvParameterSpec(_ivBytes);
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
		return new String(cipher.doFinal(txtBytes), "UTF-8");
	}
	
	/**
	 * 현재 타임스탬프를 반환
	 * @return time stamp
	 */
	public static String getTimeStamp() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(dt);
	}
	
	/**
	 * 오늘 0시 0분 0초
	 * @return Date
	 */
	public static Date getTodayStart() {
    	Calendar now = Calendar.getInstance();
    	now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
    	return now.getTime();
    }

    public static void imageResizer(){

	}

}
