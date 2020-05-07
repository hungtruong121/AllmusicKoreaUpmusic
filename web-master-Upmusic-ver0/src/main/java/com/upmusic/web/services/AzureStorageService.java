package com.upmusic.web.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AzureStorageService {
	
	void uploadTrackToTranscode(MultipartFile multipartFile, String prefix);

	void uploadTrackToTranscode(MultipartFile multipartFile, String fileName, String prefix);

	void uploadResource(MultipartFile multipartFile, String prefix);

	void uploadResource(InputStream inputStream, String originalFileName, String prefix);

	String uploadResource(String url, String prefix);
	
	void uploadVideoToTranscode(MultipartFile multipartFile, String prefix);
	
	ResponseEntity<byte[]> downloadMusicFile(String prefix, String filename) throws IOException;
	
	ResponseEntity<byte[]> downloadVocalGuideFile(String prefix, String filename) throws IOException;
	
	void uploadToResource(InputStream inputStream, String uploadKey);
	
	void uploadToTrack(InputStream inputStream, String uploadKey);

	Map<String, byte[]> getBlob(String imgName, String fileName);

}
