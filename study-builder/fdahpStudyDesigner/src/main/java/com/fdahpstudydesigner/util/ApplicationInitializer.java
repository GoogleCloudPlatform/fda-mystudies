/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ApplicationInitializer implements WebApplicationInitializer {

  private static XLogger logger = XLoggerFactory.getXLogger(ApplicationInitializer.class.getName());

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    File[] defaultImageFiles =
        new File(servletContext.getRealPath("/") + "/images/study/").listFiles();
    try {
      uplaodImage(defaultImageFiles);
    } catch (IOException e) {
      logger.error("Study Default Image upload On Startup failed - onStartup()", e);
    }
  }

  public void uplaodImage(File[] defaultImagefiles) throws IOException {
    MultipartFile multipartFile = null;
    InputStream input = null;
    OutputStream os = null;
    for (int i = 0; i < defaultImagefiles.length; i++) {
      DiskFileItem fileItem =
          new DiskFileItem(
              "file",
              "image/png",
              true,
              defaultImagefiles[i].getName(),
              (int) defaultImagefiles[i].length(),
              defaultImagefiles[i].getParentFile());
      try {
        input = new FileInputStream(defaultImagefiles[i]);
        os = fileItem.getOutputStream();
        int ret = input.read();
        while (ret != -1) {
          os.write(ret);
          ret = input.read();
        }
        os.flush();
      } catch (Exception ex) {
        logger.error("Upload Study Default image failed - uplaodImage()", ex);
      } finally {
        if (input != null) {
          input.close();
        }
        if (os != null) {
          os.close();
        }
      }
      multipartFile = new CommonsMultipartFile(fileItem);
      FdahpStudyDesignerUtil.saveDefaultImageToCloudStorage(
          multipartFile,
          defaultImagefiles[i].getName(),
          FdahpStudyDesignerConstants.DEFAULT_IMAGES);
    }
  }
}
