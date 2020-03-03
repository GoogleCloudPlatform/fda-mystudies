package com.google.cloud.healthcare.fdamystudies.service;

import java.io.OutputStream;
import java.util.List;

public interface FileStorageService {

	List<String> listFiles(String underDirectory, boolean recursive);
	
	String saveFile(String fileName, String content, String underDirectory);
	
	void downloadFileTo(String fileName, OutputStream outputStream);
	
	void printMetadata();
}
