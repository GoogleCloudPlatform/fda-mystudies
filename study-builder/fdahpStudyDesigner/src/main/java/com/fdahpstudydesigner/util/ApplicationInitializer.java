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
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class ApplicationInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    File[] defaultImageFiles =
        new File(servletContext.getRealPath("/") + "/images/defaultimage/").listFiles();
    uplaodImage(defaultImageFiles);
  }

  public void uplaodImage(File[] defaultImagefiles) {
    MultipartFile multipartFile = null;
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
        InputStream input = new FileInputStream(defaultImagefiles[i]);
        OutputStream os = fileItem.getOutputStream();
        int ret = input.read();
        while (ret != -1) {
          os.write(ret);
          ret = input.read();
        }
        os.flush();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      multipartFile = new CommonsMultipartFile(fileItem);
      FdahpStudyDesignerUtil.saveDefaultImageToCloudStorage(
          multipartFile, defaultImagefiles[i].getName(), FdahpStudyDesignerConstants.STUDTYLOGO);
    }
  }
}
