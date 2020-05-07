package com.strawberryswing.upmusic.activity;

import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import org.json.JSONArray;

/**
 * Blog Scheme를 사용하기 위한 예시 class
 * version=1에서는 write만 존재합니다.
 * 차후 다양한 Scheme가 추가될 예정입니다.
 */
public class NaverBlog {
	public static final String APP_NAVER_APPSTORE = "com.nhn.android.appstore";
	public static final String BLOG_INSTALL_URL = "market://details?id=com.nhn.android.blog";
	public static final String BLOG_INSTALL_URL_NAVER = "appstore://store?version=7&action=view&packageName=com.nhn.android.blog";

	public static final String SCHEME_NAVERBLOG = "naverblog";
	public static final String AUTHORITY_WRITE = "write";
	public static final String QUERY_VERSION = "version";
	public static final String QUERY_TITLE = "title";
	public static final String QUERY_CONTENT = "content";
	public static final String QUERY_IMAGEURLS = "imageUrls";
	public static final String QUERY_VIDEOURLS = "videoUrls";
	public static final String QUERY_OGTAGURLS = "ogTagUrls";
	public static final String QUERY_TAGS = "tags";

	private Context context;

	public NaverBlog(Context context) {
		this.context = context;
	}

	/**
	 * 첨부 글쓰기 스키마
	 * @param version 버전
	 * @param title 제목
	 * @param content 내용
	 * @param imageUrls 이미지url
	 * @param videoUrls 동영상url
	 * @param ogTagUrls 오지링크url
	 * @param tags 태그
	 */
	public void write(
		int version,
		String title,
		String content,
		List<String> imageUrls,
		List<String> videoUrls,
		List<String> ogTagUrls,
		List<String> tags) {

		Uri writeUri = BlogUriBuilder.write(version, title, content, imageUrls, videoUrls, ogTagUrls, tags);
		Intent writeIntent = new Intent();
		writeIntent.setData(writeUri);
		try {
			context.startActivity(writeIntent);
		} catch (ActivityNotFoundException e) {
			gotoMarket();
		}
	}

	/**
	 * 마켓으로 이동
	 */
	public void gotoMarket() {
		// 네이버 앱스토어로 갈 수 없으면, 플레이 스토어로 이동
		if (!gotoNaverAppStore()) {
			gotoPlayStore();
		}
	}

	/**
	 * 네이버 앱스토어 이동
	 */
	public boolean gotoNaverAppStore() {
		if (isAppInstalled(APP_NAVER_APPSTORE)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(BLOG_INSTALL_URL_NAVER));
			context.startActivity(intent);
			return true;
		}

		return false;
	}

	/**
	 * 구글 플레이스토어 이동
	 */
	public void gotoPlayStore() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(BLOG_INSTALL_URL));
		context.startActivity(intent);
	}

	/**
	 * 앱설치 확인
	 */
	public boolean isAppInstalled(String packname) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
				packname, 0);
			if (info != null) {
				return true;
			}
		} catch (PackageManager.NameNotFoundException e) {
		}
		return false;
	}

	/**
	 * write Url Scheme 생성
	 * (List를 받아 jsonArray로 파싱하는 방식을 사용하였지만 처음부터 jsonArray로 만들어서 보내는 방법도 있습니다.)
	 */
	public static class BlogUriBuilder {
		public static Uri write(
			int version,
			String title,
			String content,
			List<String> imageUrls,
			List<String> videoUrls,
			List<String> ogTagUrls,
			List<String> tags) {

			Uri.Builder uriBuilder = new Uri.Builder();
			uriBuilder.scheme(SCHEME_NAVERBLOG);
			uriBuilder.authority(AUTHORITY_WRITE);
			uriBuilder.appendQueryParameter(QUERY_VERSION, String.valueOf(version));
			if (title != null && !title.isEmpty()) {
				uriBuilder.appendQueryParameter(QUERY_TITLE, title);
			}
			if (content != null && !content.isEmpty()) {
				uriBuilder.appendQueryParameter(QUERY_CONTENT, content);
			}

			appendArrayQueryParameter(uriBuilder, QUERY_IMAGEURLS, imageUrls);
			appendArrayQueryParameter(uriBuilder, QUERY_VIDEOURLS, videoUrls);
			appendArrayQueryParameter(uriBuilder, QUERY_OGTAGURLS, ogTagUrls);
			appendArrayQueryParameter(uriBuilder, QUERY_TAGS, tags);

			return uriBuilder.build();
		}

		public static void appendArrayQueryParameter(Uri.Builder uriBuilder, String queryString, List<String> list) {
			if (list == null) {
				return;
			}

			JSONArray jsArray = new JSONArray(list);
			uriBuilder.appendQueryParameter(queryString, jsArray.toString());
		}
	}
}
