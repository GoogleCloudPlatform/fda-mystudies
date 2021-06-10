package com.fdahpstudydesigner.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

  private byte[] imgContent;
  private String fileName;
  private String ext;

  public CustomMultipartFile(byte[] imageContent, String fileName, String ext) {
    this.imgContent = imageContent;
    this.fileName = fileName;
    this.ext = ext;
  }

  public String getExt() {
    return ext;
  }

  @Override
  public String getName() {
    return fileName;
  }

  @Override
  public String getOriginalFilename() {
    return fileName;
  }

  @Override
  public String getContentType() {
    if (getExt() == null) {
      return null;
    }
    return FilenameUtils.getExtension(getExt());
  }

  @Override
  public boolean isEmpty() {
    return imgContent == null || imgContent.length == 0;
  }

  @Override
  public long getSize() {
    return imgContent.length;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return imgContent;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(imgContent);
  }

  @Override
  public void transferTo(File dest) throws IOException {
    try (FileOutputStream f = new FileOutputStream(dest)) {
      f.write(imgContent);
    }
  }
}
