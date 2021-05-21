/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.util;

import com.fdahpstudydesigner.bean.FormulaInfoBean;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPermissions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.multipart.MultipartFile;

public class FdahpStudyDesignerUtil {

  /* Read Properties file */
  private static XLogger logger = XLoggerFactory.getXLogger(FdahpStudyDesignerUtil.class.getName());

  protected static final Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();

  private static final String PATH_SEPARATOR = "/";

  public static Date addDaysToDate(Date date, int days) {
    logger.entry("begin addDaysToDate()");
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.DATE, days);
      date = cal.getTime();
    } catch (Exception e) {
      logger.error("ERROR: FdahpStudyDesignerUtil.addDaysToDate() ::", e);
    }
    logger.exit("addDaysToDate() :: Ends");
    return date;
  }

  public static String addHours(String dtStr, int hours) {
    String newdateStr = "";
    try {
      Date dt = new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.HOUR, hours);
      Date newDate = cal.getTime();
      newdateStr =
          new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).format(newDate);
    } catch (Exception e) {
      logger.error("ERROR: FdahpStudyDesignerUtil.addHours()", e);
    }
    return newdateStr;
  }

  public static String addMinutes(String dtStr, int minutes) {
    logger.entry("begin addMinutes()");
    String newdateStr = "";
    try {
      Date dt = new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.MINUTE, minutes);
      Date newDate = cal.getTime();
      newdateStr =
          new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).format(newDate);
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - addMinutes() : ", e);
    }
    logger.exit("addMinutes() - Ends");
    return newdateStr;
  }

  public static List<GrantedAuthority> buildUserAuthority(Set<UserPermissions> userRoles) {

    Set<GrantedAuthority> setAuths = new HashSet<>();

    // Build user's authorities
    for (UserPermissions userRole : userRoles) {
      setAuths.add(new SimpleGrantedAuthority(userRole.getPermissions()));
    }

    return new ArrayList<>(setAuths);
  }

  public static User buildUserForAuthentication(UserBO user, List<GrantedAuthority> authorities) {
    return new User(
        user.getUserEmail(),
        user.getUserPassword(),
        user.isEnabled(),
        user.isAccountNonExpired(),
        user.isCredentialsNonExpired(),
        user.isAccountNonLocked(),
        authorities);
  }

  public static boolean compareDateWithCurrentDateResource(String inputDate, String inputFormat) {
    boolean flag = false;
    final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
    try {
      if (new Date().before(sdf.parse(inputDate))) {
        flag = true;
      }
    } catch (ParseException e) {
      logger.error("FdahpStudyDesignerUtil - compareDateWithCurrentDateTime() : ", e);
    }
    return flag;
  }

  public static boolean compareDateWithCurrentDateTime(String inputDate, String inputFormat) {
    boolean flag = false;
    final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
    try {
      if (new Date().before(sdf.parse(inputDate))) {
        flag = true;
      }
      if (new Date().equals(sdf.parse(inputDate))) {
        flag = true;
      }
    } catch (ParseException e) {
      logger.error("FdahpStudyDesignerUtil - compareDateWithCurrentDateTime() : ", e);
    }
    return flag;
  }

  public static Boolean compareEncryptedPassword(String dbEncryptPassword, String uiPassword) {
    Boolean isMatch = false;
    logger.entry("begin getEncryptedString()");
    try {
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      isMatch = passwordEncoder.matches(uiPassword, dbEncryptPassword);
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - compairEncryptedPassword() - ERROR", e);
    }
    logger.exit("getEncryptedString() end");
    return isMatch;
  }

  public static boolean fieldsValidation(String... fields) {
    logger.entry("begin formValidation() - " + " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
    List<String> fieldsList = new ArrayList<>();
    boolean result = true;
    try {
      for (String field : fields) {
        fieldsList.add(field);
      }
      for (int i = 0; i < fieldsList.size(); i++) {
        String tempField = fieldsList.get(i);
        tempField = StringUtils.isEmpty(tempField) != true ? tempField.trim() : "";
        if (tempField.length() < 1) {
          return false;
        }
      }
    } catch (Exception e) {
      logger.error("ERROR: FdahpStudyDesignerUtil: formValidation(): ", e);
    }
    logger.exit(
        "Exit Point: formValidation() - " + " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
    return result;
  }

  public static String formatTime(String inputTime, String inputFormat, String outputFormat) {
    logger.entry("begin formatTime()");
    String finalTime = "";
    SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
    SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat);
    if ((inputTime != null) && !"".equals(inputTime) && !"null".equalsIgnoreCase(inputTime)) {
      try {
        finalTime = outputSDF.format(inputSDF.parse(inputTime)).toLowerCase();
      } catch (Exception e) {
        logger.error("FdahpStudyDesignerUtil.formatTime() ::", e);
      }
    }
    logger.exit("formatTime() :: Ends");
    return finalTime;
  }

  public static String genarateEmailContent(String emailContentName, Map<String, String> keyValue) {

    String dynamicContent = configMap.get(emailContentName);

    if (FdahpStudyDesignerUtil.isNotEmpty(dynamicContent)) {
      for (Map.Entry<String, String> entry : keyValue.entrySet()) {
        dynamicContent =
            dynamicContent.replace(
                entry.getKey(), StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue());
      }
    }
    return dynamicContent;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static Map<String, String> getAppProperties() {
    HashMap hm = new HashMap<String, String>();
    logger.entry("begin getAppProperties() :: Properties Initialization");
    Enumeration<String> keys = null;
    Enumeration<Object> objectKeys = null;
    Resource resource = null;
    try {
      ResourceBundle rb = ResourceBundle.getBundle("messageResource");
      keys = rb.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        String value = rb.getString(key);
        hm.put(key, value);
      }
      ServletContext context = ServletContextHolder.getServletContext();
      Properties prop =
          PropertiesUtil.makePropertiesWithEnvironmentVariables("application.properties");
      objectKeys = prop.keys();
      while (objectKeys.hasMoreElements()) {
        String key = (String) objectKeys.nextElement();
        String value = prop.getProperty(key);
        hm.put(key, value);
      }

    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - getAppProperties() - ERROR ", e);
    }
    logger.exit("getAppProperties() - ends");
    return hm;
  }

  public static FormulaInfoBean getConditionalFormulaResult(
      String lhs, String rhs, String operator, String trialInput) {
    FormulaInfoBean formulaInfoBean = new FormulaInfoBean();
    String operand1 = "";
    String operand2 = "";
    BigDecimal result = null;
    BigDecimal oprandResult = null;
    if (lhs.contains("x")) {
      if (lhs.contains("!=")
          || lhs.contains("==")
          || lhs.contains(">")
          || lhs.contains("<")
          || lhs.contains("&&")
          || lhs.contains("||")) {
        oprandResult = null;
        try {
          oprandResult = new com.udojava.evalex.Expression(lhs).with("x", trialInput).eval();
          if (oprandResult.intValue() == 1) {
            operand1 = "true";
          } else {
            operand1 = "false";
          }
        } catch (Exception e) {
          logger.error("FdahpStudyDesignerUtil - getConditionalFormulaResult() : ", e);
          formulaInfoBean.setStatusMessage("Error in LHS");
        }
      } else {
        try {
          net.objecthunter.exp4j.Expression e;
          if (trialInput.contains(".")) {
            e =
                new ExpressionBuilder(lhs)
                    .variables("x")
                    .build()
                    .setVariable("x", Float.parseFloat(trialInput));
          } else {
            e =
                new ExpressionBuilder(lhs)
                    .variables("x")
                    .build()
                    .setVariable("x", Integer.parseInt(trialInput));
          }
          double op = e.evaluate();
          operand1 = Double.toString(Math.round(op * 100.0) / 100.0);
        } catch (Exception e) {
          logger.error("FdahpStudyDesignerUtil - getConditionalFormulaResult() : ", e);
          formulaInfoBean.setStatusMessage("Error in LHS");
        }
      }
    } else {
      try {
        double op = new ExpressionBuilder(lhs).build().evaluate();
        operand1 = Double.toString(Math.round(op * 100.0) / 100.0);
      } catch (Exception e) {
        logger.error("FdahpStudyDesignerUtil - getConditionalFormulaResult() : ", e);
        formulaInfoBean.setStatusMessage("Error in RHS");
      }
    }
    if (rhs.contains("x")) {
      if (rhs.contains("!=")
          || rhs.contains("==")
          || rhs.contains(">")
          || rhs.contains("<")
          || rhs.contains("&&")
          || rhs.contains("||")) {
        oprandResult = null;
        try {
          oprandResult = new com.udojava.evalex.Expression(rhs).with("x", trialInput).eval();
          if (oprandResult.intValue() == 1) {
            operand2 = "true";
          } else {
            operand2 = "false";
          }
        } catch (Exception e) {
          logger.error("FdahpStudyDesignerUtil - getConditionalFormulaResult() : ", e);
          formulaInfoBean.setStatusMessage("Error in LHS");
        }
      } else {
        try {
          net.objecthunter.exp4j.Expression e;
          if (trialInput.contains(".")) {
            e =
                new ExpressionBuilder(rhs)
                    .variables("x")
                    .build()
                    .setVariable("x", Float.parseFloat(trialInput));
          } else {
            e =
                new ExpressionBuilder(rhs)
                    .variables("x")
                    .build()
                    .setVariable("x", Integer.parseInt(trialInput));
          }

          double op = e.evaluate();
          operand2 = Double.toString(Math.round(op * 100.0) / 100.0);
        } catch (Exception e) {
          logger.error("FdahpStudyDesignerUtil - getConditionalFormulaResult() : ", e);
          formulaInfoBean.setStatusMessage("Error in RHS");
        }
      }
    } else {
      try {
        double op = new ExpressionBuilder(rhs).build().evaluate();
        operand2 = Double.toString(Math.round(op * 100.0) / 100.0);
      } catch (Exception e) {
        logger.error("FdahpStudyDesignerUtil - getConditionalFormulaResult() : ", e);
        formulaInfoBean.setStatusMessage("Error in RHS");
      }
    }
    if (formulaInfoBean.getStatusMessage().isEmpty()) {
      try {
        result =
            new com.udojava.evalex.Expression("x " + operator + " y")
                .with("x", operand1)
                .with("y", operand2)
                .eval();
      } catch (Exception e) {
        logger.error("FdahpStudyDesignerUtil - getConditionalFormulaResult() : ", e);
        formulaInfoBean.setStatusMessage("Error in Result");
      }
      if (result != null) {
        if (result.intValue() == 1) {
          formulaInfoBean.setOutPutData("True");
        } else {
          formulaInfoBean.setOutPutData("False");
        }
        formulaInfoBean.setLhsData(operand1);
        formulaInfoBean.setRhsData(operand2);
        formulaInfoBean.setMessage(FdahpStudyDesignerConstants.SUCCESS);
      }
    }
    return formulaInfoBean;
  }

  public static String getCurrentDate() {
    Calendar currentDate = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    return formatter.format(currentDate.getTime());
  }

  public static String getCurrentDateTime() {
    Calendar currentDate = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME);
    return formatter.format(currentDate.getTime());
  }

  public static Date getCurrentDateTimeAsDate() {
    logger.entry(
        "begin getCurrentDateTimeAsDate() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    Date dateNow = null;
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String timeZone = "UTC";
    try {
      String strDate = new Date() + "";
      if (strDate.indexOf("IST") != -1) {
        timeZone = "IST";
      }
      sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
      dateNow = sdf.parse(sdf.format(new Date()));
    } catch (Exception e) {
      logger.error("ERROR: getCurrentDateTimeAsDate(): ", e);
    }
    logger.exit(
        "Exit Point: getCurrentDateTimeAsDate() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    return dateNow;
  }

  public static String getCurrentTime() {
    Calendar currentDate = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    return formatter.format(currentDate.getTime());
  }

  public static Date getCurrentUtilDateTime() {
    logger.entry(
        "begin getCurrentUtilDateTime() - " + " : " + FdahpStudyDesignerUtil.getCurrentDateTime());
    Date utilDate = new Date();
    Calendar currentDate = Calendar.getInstance();
    String dateNow =
        new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
            .format(currentDate.getTime());
    try {
      utilDate = new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).parse(dateNow);
    } catch (ParseException e) {
      logger.error("FdahpStudyDesignerUtil - getCurrentUtilDateTime() : ", e);
    }
    logger.exit(
        "Exit Point: getCurrentUtilDateTime() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    return utilDate;
  }

  public static String getDate(String date, SimpleDateFormat sdf) {
    logger.entry("begin getDate()");
    String postedDate = sdf.format(date);
    logger.exit("getDate() :: Ends");
    return postedDate;
  }

  public static String getDateAndTimeBasedOnTimeZone(String timeZone, String dateTime) {
    String actualDateTime = null;
    Date fromDate = null;
    try {
      if (StringUtils.isNotEmpty(timeZone)) {
        SimpleDateFormat toDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fromDate = toDateFormatter.parse(dateTime);
        toDateFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        logger.info(" Date Time in seconds : " + fromDate.getTime());
        actualDateTime = toDateFormatter.format(fromDate.getTime());
      } else {
        actualDateTime = timeZone;
      }
    } catch (ParseException e) {
      logger.error("FdahpStudyDesignerUtil - getDateAndTimeBasedOnTimeZone() : ", e);
    }
    logger.exit(" User Date and Time based on the Time Zone : " + actualDateTime);
    return actualDateTime;
  }

  public static String getDecodedStringByBase64(String encodedText) {
    logger.entry(
        "begin getDecodedStringByBase64() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    try {
      // Decrypt data on other side, by processing encoded data
      byte[] valueDecoded = Base64.getDecoder().decode(encodedText);
      return new String(valueDecoded);

    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - getDecodedStringByBase64() : ", e);
    }
    logger.exit(
        "Exit Point: getDecodedStringByBase64() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    return "";
  }

  public static boolean getEDTdatetimeAsStringCompare(
      String timeZone, String inputDate, String inputFormat) {
    final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
    boolean flag = false;
    try {
      if (sdf.parse(inputDate).before(new Date())) {
        flag = false;
      }
      if (sdf.parse(inputDate).after(new Date())) {
        flag = true;
      }
      if (sdf.parse(inputDate).equals(new Date())) {
        flag = true;
      }
    } catch (Exception e) {
      logger.error("ERROR: FdahpStudyDesignerUtil.getEDTdatetimeAsStringCompare()", e);
    }
    return flag;
  }

  public static String getEncodedStringByBase64(String plainText) {
    logger.entry(
        "begin getEncodedStringByBase64() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    try {
      // encrypt data on your side using BASE64
      byte[] bytesEncoded = Base64.getEncoder().encode(plainText.getBytes());
      return new String(bytesEncoded);
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - getEncodedStringByBase64() : ", e);
    }
    logger.exit(
        "Exit Point: getEncodedStringByBase64() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    return "";
  }

  public static String getEncryptedFormat(String input) {
    StringBuffer sb = new StringBuffer();
    logger.entry("begin Password Encryption method==start");
    if (input != null) {
      /* Add the password salt to input parameter */
      input = input + FdahpStudyDesignerConstants.ENCRYPT_SALT;
      try {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(input.getBytes("UTF-8"));
        byte[] digestBytes = messageDigest.digest();
        String hex = null;
        for (int i = 0; i < 8; i++) {
          hex = Integer.toHexString(0xFF & digestBytes[i]);
          if (hex.length() < 2) {
            sb.append("0");
          }
          sb.append(hex);
        }
      } catch (Exception e) {
        logger.error("FdahpStudyDesignerUtil - getEncryptedFormat() - ERROR", e);
      }
    }
    logger.exit("Password Encryption method==end");
    return sb.toString();
  }

  /* getEncodedString(String test) method returns Encoded String */
  public static String getEncryptedPassword(String input) {
    String hashedPassword = null;
    logger.entry("begin getEncryptedPassword()");
    if (input != null) {
      try {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        hashedPassword = passwordEncoder.encode(input);
      } catch (Exception e) {
        logger.error("FdahpStudyDesignerUtil - getEncryptedPassword() - ERROR", e);
      }
    }
    logger.exit("getEncryptedPassword() end");
    return hashedPassword;
  }

  /* getEncodedString(String test) method returns Encoded String */
  public static String getEncryptedString(String input) {
    StringBuffer sb = new StringBuffer();
    logger.entry("begin getEncryptedString()");
    if (input != null) {
      /** Add the password salt to input parameter */
      input = input + FdahpStudyDesignerConstants.FDA_SALT;
      try {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(input.getBytes("UTF-8"));
        byte[] digestBytes = messageDigest.digest();
        String hex = null;
        for (int i = 0; i < 8; i++) {
          hex = Integer.toHexString(0xFF & digestBytes[i]);
          if (hex.length() < 2) {
            sb.append("0");
          }
          sb.append(hex);
        }
      } catch (Exception e) {
        logger.error("FdahpStudyDesignerUtil - getEncryptedString() - ERROR", e);
      }
    }
    logger.exit("getEncryptedString() end");
    return sb.toString();
  }

  public static String getErrorMessage(HttpServletRequest request, String key) {

    Exception exception = (Exception) request.getSession().getAttribute(key);
    String userLoginFailure = configMap.get("user.login.failure");
    String userInactiveMsg = configMap.get("user.inactive.msg");
    String alreadyLoginMsg = configMap.get("user.alreadylogin.msg");
    String credentialExpiredException = configMap.get("user.admin.forcepassword.msg");
    String error = "";
    if (exception instanceof BadCredentialsException) {
      error = userLoginFailure;
    } else if (exception instanceof LockedException) {
      error = exception.getMessage();
    } else if (exception instanceof DisabledException) {
      error = userInactiveMsg;
    } else if (exception instanceof CredentialsExpiredException) {
      error = credentialExpiredException;
    } else if (exception instanceof SessionAuthenticationException) {
      error = alreadyLoginMsg;
    } else if (exception instanceof AccountStatusException) {
      error = exception.getMessage() + "!";
    } else {
      error = userLoginFailure;
    }

    return error;
  }

  public static String getFormattedDate(String inputDate, String inputFormat, String outputFormat) {
    String finalDate = "";
    java.sql.Date formattedDate;
    if ((inputDate != null) && !"".equals(inputDate) && !"null".equalsIgnoreCase(inputDate)) {
      try {
        SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
        formattedDate = new java.sql.Date(formatter.parse(inputDate).getTime());
        formatter = new SimpleDateFormat(outputFormat);
        finalDate = formatter.format(formattedDate);
      } catch (Exception e) {
        logger.error("Exception in getFormattedDate(): " + e);
      }
    }
    return finalDate;
  }

  public static String getRegExpression(
      String validCondition, String validCharacters, String exceptCharacters) {
    String regEx = "";
    if (((validCharacters != null) && StringUtils.isNotEmpty(validCharacters))
        && ((validCondition != null) && StringUtils.isNotEmpty(validCondition))) {
      if (validCondition.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLOW)) {
        regEx += "[";
        if (validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLCHARACTERS)) {
          regEx += "^.";
        } else if (validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.ALPHABETS)) {
          regEx += "a-zA-Z ";
        } else if (validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.NUMBERS)) {
          regEx += "0-9 ";
        } else if (validCharacters.equalsIgnoreCase(
            FdahpStudyDesignerConstants.ALPHABETSANDNUMBERS)) {
          regEx += "a-zA-Z0-9 ";
        } else if (validCharacters.equalsIgnoreCase(
            FdahpStudyDesignerConstants.SPECIALCHARACTERS)) {
          regEx += "^A-Za-z0-9";
        }
        if ((exceptCharacters != null) && StringUtils.isNotEmpty(exceptCharacters)) {
          String[] exceptChar = exceptCharacters.split("\\|");
          StringBuilder except = new StringBuilder();
          for (String element : exceptChar) {
            except.append("^(?!.*" + element.trim().replace(" ", "") + ")");
          }
          regEx = except + regEx + "]+";
        } else {
          regEx += "]+";
        }
      } else {
        if (validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLCHARACTERS)) {
          if ((exceptCharacters != null) && StringUtils.isNotEmpty(exceptCharacters)) {
            regEx += "^(?:" + exceptCharacters.trim().replace(" ", "") + ")$";
          } else {
            regEx += "[.]";
          }

        } else if (validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.ALPHABETS)) {
          regEx += "^([^a-zA-Z]";
        } else if (validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.NUMBERS)) {
          regEx += "^([^0-9]";
        } else if (validCharacters.equalsIgnoreCase(
            FdahpStudyDesignerConstants.ALPHABETSANDNUMBERS)) {
          regEx += "^([^a-zA-Z0-9]";
        } else if (validCharacters.equalsIgnoreCase(
            FdahpStudyDesignerConstants.SPECIALCHARACTERS)) {
          regEx += "^([A-Za-z0-9 ]";
        }
        if (!validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.ALLCHARACTERS)) {
          if ((exceptCharacters != null) && StringUtils.isNotEmpty(exceptCharacters)) {
            String[] exceptChar = exceptCharacters.split("\\|");
            StringBuilder except = new StringBuilder();
            if (validCharacters.equalsIgnoreCase(FdahpStudyDesignerConstants.SPECIALCHARACTERS)) {
              for (String element : exceptChar) {
                except.append(element.trim().replace(" ", ""));
              }
              regEx += "|[" + except + "]*)+$";
            } else {
              for (String element : exceptChar) {
                except.append("|\\b(\\b" + element.trim().replace(" ", "") + "\\b)");
              }
              regEx += except + "*)+$";
            }

          } else {
            regEx += "*)+$";
          }
        }
      }
    }
    return regEx;
  }

  public static String getSessionUserRole() {
    logger.entry("begin getSessionUserRole()");
    String userRoles = "";
    try {
      SecurityContext securityContext = SecurityContextHolder.getContext();
      Authentication authentication = securityContext.getAuthentication();
      if (authentication != null) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        userRoles = StringUtils.join(authorities.iterator(), ",");
      }
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - getSessionUser() - ERROR ", e);
    }
    logger.exit("getSessionUserRole() :: Ends");
    return userRoles;
  }

  public static String getStandardFileName(
      String actualFileName, String userFirstName, String userLastName) {
    String intial = userFirstName.charAt(0) + "" + userLastName.charAt(0);
    String dateTime =
        new SimpleDateFormat(FdahpStudyDesignerConstants.SDF_FILE_NAME_TIMESTAMP)
            .format(new Date());
    return actualFileName + "_" + intial + "_" + dateTime;
  }

  public static String getTimeDiffInDaysHoursMins(Date dateOne, Date dateTwo) {
    String diff = "";
    long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
    diff =
        String.format(
            "%d Day(s) %d hour(s) %d min(s)",
            TimeUnit.MILLISECONDS.toDays(timeDiff),
            TimeUnit.MILLISECONDS.toHours(timeDiff)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeDiff)),
            TimeUnit.MILLISECONDS.toMinutes(timeDiff)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
    return diff;
  }

  public static String getTimeDiffInMins(Date dateOne, Date dateTwo) {
    String diff = "";
    long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
    diff = String.format("%d", TimeUnit.MILLISECONDS.toMinutes(timeDiff));
    return diff;
  }

  public static Integer getTimeDiffToCurrentTimeInHr(Date date) {
    logger.entry(
        "begin getTimeDiffToCurrentTimeInHr() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime());
    Integer diffHours = null;
    float diff = 0.0f;
    try {
      Date dt2 = new Date();
      diff = (float) dt2.getTime() - date.getTime();
      diffHours = Math.round(diff / (60 * 60 * 1000));
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - getTimeDiffToCurrentTimeInHr() : ", e);
    }
    logger.exit("getTimeDiffToCurrentTimeInHr() - Ends");
    return diffHours;
  }

  public static List<String> getTimeRangeList(String frequency) {
    List<String> timeRangeList = new ArrayList<>();
    if (StringUtils.isNotEmpty(frequency)) {
      switch (frequency) {
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME:
          timeRangeList.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_WEEK);
          timeRangeList.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_MONTH);
          break;
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY:
          timeRangeList.add(FdahpStudyDesignerConstants.MULTIPLE_TIMES_A_DAY);
          break;

        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_WEEKLY:
          timeRangeList.add(FdahpStudyDesignerConstants.WEEKS_OF_THE_CURRENT_MONTH);
          break;

        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MONTHLY:
          timeRangeList.add(FdahpStudyDesignerConstants.MONTHS_OF_THE_CURRENT_YEAR);
          break;

        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
          timeRangeList.add(FdahpStudyDesignerConstants.RUN_BASED);
          break;
      }
    } else {
      timeRangeList.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_WEEK);
      timeRangeList.add(FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_MONTH);
    }
    return timeRangeList;
  }

  public static String[] getTimeRangeString(String frequency) {
    if (StringUtils.isNotEmpty(frequency)) {
      switch (frequency) {
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_WITHIN_A_DAY:
          return new String[] {
            FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_MONTH,
            FdahpStudyDesignerConstants.DAYS_OF_THE_CURRENT_WEEK
          };
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY:
          return new String[] {FdahpStudyDesignerConstants.MULTIPLE_TIMES_A_DAY};
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_WEEKLY:
          return new String[] {FdahpStudyDesignerConstants.WEEKS_OF_THE_CURRENT_MONTH};
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MONTHLY:
          return new String[] {FdahpStudyDesignerConstants.MONTHS_OF_THE_CURRENT_YEAR};
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE:
          return new String[] {FdahpStudyDesignerConstants.RUN_BASED};
        case FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME:
          return new String[] {""};
      }
    }
    return new String[] {""};
  }

  public static boolean isEmpty(String str) {
    logger.entry("begin isEmpty()");
    boolean flag = false;
    if ((null == str) || "".equals(str)) {
      flag = true;
    }
    logger.exit("isEmpty() :: Ends");
    return flag;
  }

  public static boolean isNotEmpty(String str) {
    logger.entry("begin isNotEmpty()");
    boolean flag = !isEmpty(str);
    logger.exit("isNotEmpty() :: Ends");
    return flag;
  }

  public static boolean isSession(HttpServletRequest request) {
    logger.entry("begin isSession()");
    boolean flag = false;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        flag = true;
      }
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil - isSession() - ERROR ", e);
    }
    logger.exit("FdahpStudyDesignerUtil - isSession() :: Ends");
    return flag;
  }

  public static String privMinDateTime(String sysDateTime, String format, int min) {
    String newSysDateTime = null;
    try {
      SimpleDateFormat formatter = new SimpleDateFormat(format);
      Calendar cal = Calendar.getInstance();
      Date actualDateTime = formatter.parse(sysDateTime);
      cal.setTime(actualDateTime);
      cal.add(Calendar.MINUTE, -min);
      Date modDate = cal.getTime();
      newSysDateTime = formatter.format(modDate);
    } catch (ParseException e) {
      logger.error("FdahpStudyDesignerUtil - privMinDateTime : ", e);
    }
    return newSysDateTime;
  }

  public static String removeLastCommaFromString(String str) {
    if ((str.trim().length() > 0) && str.trim().endsWith(",")) {
      str = str.trim();
      str = str.substring(0, str.length() - 1);
      return str;
    } else {
      return str;
    }
  }

  public static String round(double value) {
    logger.entry("begin round()");
    String rounded = "0";
    try {
      rounded = String.valueOf(Math.round(value));
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil: double round() :: ERROR: ", e);
    }
    logger.exit("round() :: Ends");
    return rounded;
  }

  public static String round(float value) {
    logger.entry("begin float round()");
    String rounded = "0";
    try {
      rounded = String.valueOf(Math.round(value));
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil: float round() :: ERROR: ", e);
    }
    logger.exit("float round() :: Ends");
    return rounded;
  }

  public static String round(String value) {
    logger.entry("begin String round()");
    String rounded = "0";
    try {
      if (StringUtils.isNotEmpty(value)) {
        rounded = String.valueOf(Math.round(Double.parseDouble(value)));
      }
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerUtil: String round() :: ERROR: ", e);
    }
    logger.exit("String round() :: Ends");
    return rounded;
  }

  public static boolean validateUserSession(HttpServletRequest request) {
    boolean flag = false;
    SessionObject sesObj =
        (SessionObject)
            request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
    if (null != sesObj) {
      flag = true;
    }
    return flag;
  }

  public static String getHashedValue(String secretToHash) {
    logger.entry("begin getHashedValue()");
    String generatedHash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(secretToHash.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedHash = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.info("FdahpStudyDesignerUtil getHashedValue() - error() ", e);
    }
    logger.exit("getHashedValue() - ends");
    return generatedHash;
  }

  public static String saveImage(
      MultipartFile fileStream, String fileName, String underDirectory, String customStudyId) {
    String fileNameWithExtension =
        fileName + "." + FilenameUtils.getExtension(fileStream.getOriginalFilename());
    String absoluteFileName =
        FdahpStudyDesignerConstants.STUDIES
            + PATH_SEPARATOR
            + customStudyId
            + PATH_SEPARATOR
            + underDirectory
            + PATH_SEPARATOR
            + fileNameWithExtension;
    BlobInfo blobInfo =
        BlobInfo.newBuilder(configMap.get("cloud.bucket.name"), absoluteFileName).build();

    try {
      Storage storage = StorageOptions.getDefaultInstance().getService();
      storage.create(blobInfo, fileStream.getBytes());

    } catch (Exception e) {
      logger.error("Save Image in cloud storage failed", e);
    }
    return fileNameWithExtension;
  }

  public static String getSignedUrl(String filePath, int signedUrlDurationInHours) {
    try {
      BlobInfo blobInfo = BlobInfo.newBuilder(configMap.get("cloud.bucket.name"), filePath).build();
      Storage storage = StorageOptions.getDefaultInstance().getService();
      return storage.signUrl(blobInfo, signedUrlDurationInHours, TimeUnit.HOURS).toString();
    } catch (Exception e) {
      logger.error("Unable to generate signed url", e);
    }
    return null;
  }

  public static void saveDefaultImageToCloudStorage(
      MultipartFile fileStream, String fileName, String underDirectory) {
    String absoluteFileName = underDirectory + PATH_SEPARATOR + fileName;
    BlobInfo blobInfo =
        BlobInfo.newBuilder(configMap.get("cloud.bucket.name"), absoluteFileName).build();
    try {
      Storage storage = StorageOptions.getDefaultInstance().getService();
      storage.create(blobInfo, fileStream.getBytes());
    } catch (Exception e) {
      logger.error("Save Default Image to cloud storage failed", e);
    }
  }

  public static String getTimeStamp(String inputDate, String inputTime) {

    String timestampInString = inputDate + " " + inputTime;
    try {
      System.out.println("timestamp of notification " + timestampInString);
      return timestampInString;
    } catch (Exception e) {
      logger.error("Exception in getTimeStamp(): " + e);
    }
    return null;
  }

  public static void copyImage(String fileName, String underDirectory, String customStudyId) {
    String newFilePath =
        FdahpStudyDesignerConstants.STUDIES
            + PATH_SEPARATOR
            + customStudyId
            + PATH_SEPARATOR
            + underDirectory
            + PATH_SEPARATOR
            + fileName;

    String oldFilePath = underDirectory + PATH_SEPARATOR + fileName;
    try {
      Storage storage = StorageOptions.getDefaultInstance().getService();
      Blob blob = storage.get(BlobId.of(configMap.get("cloud.bucket.name"), oldFilePath));

      if (blob != null) {
        blob.copyTo(configMap.get("cloud.bucket.name"), newFilePath);

        // Delete the original blob now that we've copied to where we want it, finishing the "move"
        // operation
        blob.delete();
      }

    } catch (Exception e) {
      logger.error("Save Image in cloud storage failed", e);
    }
  }
}
