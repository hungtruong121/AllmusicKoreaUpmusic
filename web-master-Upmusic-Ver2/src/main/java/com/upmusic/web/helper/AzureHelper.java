package com.upmusic.web.helper;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

import com.upmusic.web.services.AzureStorageService;
import org.springframework.util.StringUtils;

import com.upmusic.web.config.UPMusicConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class AzureHelper {

	private static String storageResourceUrl;
	private static String storageAlbumUrl;
	
	
	/**
	 * Azure 리소스 버킷 url 반환
	 * @return url
	 */
	public static String getStorageResourceUrl() {
		if (StringUtils.isEmpty(storageResourceUrl)) {
			try {
				storageResourceUrl = UPMusicConstants.loadPropertyValue("upm.azure.storage.resource.url");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return storageResourceUrl;
	}
	
	/**
	 * Azure 앨범 버킷 url 반환
	 * @return url
	 */
	public static String getStorageAlbumUrl() {
		if (StringUtils.isEmpty(storageAlbumUrl)) {
			try {
				storageAlbumUrl = UPMusicConstants.loadPropertyValue("upm.azure.storage.album.url");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return storageAlbumUrl;
	}

	/**
	 * 업로드 된 파일 크기 조정(용량 압축)
	 * @return InputStream
	 */
	public static InputStream scale(MultipartFile file, double fWidth, double fHeight) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			BufferedImage sbi = ImageIO.read(file.getInputStream());
			BufferedImage dbi = null;
			int dWidth = (int)(double)(sbi.getWidth()*fWidth);
			int dHeight = (int)(double)(sbi.getHeight()*fHeight);
			if(sbi != null) {
				dbi = new BufferedImage(dWidth, dHeight, sbi.getType());
				Graphics2D g = dbi.createGraphics();
				AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
				g.drawRenderedImage(sbi, at);
			}
			ImageIO.write(dbi, "png", os);
		}catch (Exception e){
			e.printStackTrace();
		}
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		return is;
	}
}
