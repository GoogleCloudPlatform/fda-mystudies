/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.SplashActivity;
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.utils.realm.RealmEncryptionHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

public class AppController {

  private static SharedPreferenceHelper sharedPreferenceHelper;
  private static JsonFormatHelper jsonFormatHelper;
  private static SetDialogHelper setDialogHelper;
  private static ProgressDialogHelper progressDialogHelper;
  private static RealmConfiguration config;
  private static KeyStore keyStore;
  private static final String TAG = "FDAKeystore";
  private static String keystoreValue = null;
  public static String loginCallback = "login_callback";

  public static SharedPreferenceHelper getHelperSharedPreference() {
    if (sharedPreferenceHelper == null) {
      sharedPreferenceHelper = new SharedPreferenceHelper();
    }
    return sharedPreferenceHelper;
  }

  public static JsonFormatHelper getHelperJsonFormat() {
    if (jsonFormatHelper == null) {
      jsonFormatHelper = new JsonFormatHelper();
    }
    return jsonFormatHelper;
  }

  public static ProgressDialogHelper getHelperProgressDialog() {
    if (progressDialogHelper == null) {
      progressDialogHelper = new ProgressDialogHelper();
    }
    return progressDialogHelper;
  }

  public static SetDialogHelper getHelperSetDialog() {
    if (setDialogHelper == null) {
      setDialogHelper = new SetDialogHelper();
    }
    return setDialogHelper;
  }

  public static boolean getHelperIsValidEmail(String target) {
    if (target == null) {
      return false;
    } else {
      return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
  }

  public static void getHelperSessionExpired(Context context, String msg) {
    SharedPreferences settings = SharedPreferenceHelper.getPreferences(context);
    settings.edit().clear().apply();
    // delete passcode from keystore
    String pass = AppController.refreshKeys("passcode");
    if (pass != null) {
      AppController.deleteKey("passcode_" + pass);
    }
    DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
    Realm realm = getRealmobj(context);
    dbServiceSubscriber.deleteDb(context);
    try {
      NotificationModuleSubscriber notificationModuleSubscriber =
          new NotificationModuleSubscriber(dbServiceSubscriber, realm);
      notificationModuleSubscriber.cancleActivityLocalNotification(context);
      notificationModuleSubscriber.cancleResourcesLocalNotification(context);
    } catch (Exception e) {
      Logger.log(e);
    }
    NotificationModuleSubscriber notificationModuleSubscriber =
        new NotificationModuleSubscriber(dbServiceSubscriber, realm);
    notificationModuleSubscriber.cancelNotificationTurnOffNotification(context);
    dbServiceSubscriber.closeRealmObj(realm);

    // clear notifications from notification tray
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.cancelAll();

    if (AppConfig.AppType.equalsIgnoreCase(context.getString(R.string.app_gateway))) {
      Intent intent = new Intent(context, StudyActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      context.startActivity(mainIntent);
      ((Activity) context).finish();
    } else {
      Intent intent = new Intent(context, StandaloneActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      context.startActivity(mainIntent);
      ((Activity) context).finish();
    }
  }

  public static void getHelperHideKeyboard(Activity activity) {
    InputMethodManager imm =
        (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    View view = activity.getCurrentFocus();
    if (view == null) {
      view = new View(activity);
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static void getHelperHideKeyboardContext(Context context, View view) {
    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
  }

  public static final int getColor(Context context, int id) {
    final int version = Build.VERSION.SDK_INT;
    if (version >= 23) {
      return ContextCompat.getColor(context, id);
    } else {
      return context.getResources().getColor(id);
    }
  }

  public static void blockscreenshot(Activity activity) {
    activity
        .getWindow()
        .setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
  }

  public static Typeface getTypeface(Context context, String whichType) {
    // whichTyp ====> bold/light/medium/regular/thin
    Typeface retTypeface = null;
    try {
      if (whichType.equalsIgnoreCase("bold")) {
        retTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
      } else if (whichType.equalsIgnoreCase("light")) {
        retTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
      } else if (whichType.equalsIgnoreCase("medium")) {
        retTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
      } else if (whichType.equalsIgnoreCase("regular")) {
        retTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
      } else if (whichType.equalsIgnoreCase("thin")) {
        retTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
      } else {
        return retTypeface;
      }
    } catch (Exception e) {
      Logger.log(e);
    }
    return retTypeface;
  }

  public static Realm getRealmobj(Context context) {
    if (config == null) {
      RealmEncryptionHelper realmEncryptionHelper =
          RealmEncryptionHelper.initHelper(context, context.getString(R.string.app_name));
      byte[] key = realmEncryptionHelper.getEncryptKey();
      config =
          new RealmConfiguration.Builder()
              .encryptionKey(key)
              .deleteRealmIfMigrationNeeded()
              .build();
    }
    return Realm.getInstance(config);
  }

  public static void clearDBfile() {
    if (config != null) {
      Realm.compactRealm(config);
    }
  }

  public static SimpleDateFormat getNotificationDateFormat() {
    return new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
  }

  public static SimpleDateFormat getDateFormatForApi() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  }


  public static SimpleDateFormat getDateFormatUtcNoZone() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    return simpleDateFormat;
  }

  public static SimpleDateFormat getDateFormatForNotification() {
    return new SimpleDateFormat("MMM dd yyyy");
  }

  public static SimpleDateFormat getDateFormatForActivityList() {
    return new SimpleDateFormat("MMM dd, yyyy");
  }

  public static SimpleDateFormat getDateFormatForDailyRun() {
    return new SimpleDateFormat("yyyy-MM-dd");
  }

  public static String getDateFormatForConsentPdf() {
    Date date = new Date();
    return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(date);
  }

  public static SimpleDateFormat getDateFormatForOtherFreq() {
    return new SimpleDateFormat("hh:mma, MMM dd, yyyy");
  }

  public static SimpleDateFormat getDateFormatForResourceAvailability() {
    return new SimpleDateFormat("yyyy-MM-dd");
  }

  public static SimpleDateFormat getDateFormatForDailyRunStartAndEnd() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  }

  public static SimpleDateFormat getDateFormatForChartAndStat() {
    return new SimpleDateFormat("MMM yyyy");
  }

  public static SimpleDateFormat getDateFormatForDashboardCurrentDay() {
    return new SimpleDateFormat("dd MM yyyy");
  }

  public static SimpleDateFormat getDateFormatForDashboardAndChartCurrentDayOut() {
    return new SimpleDateFormat("dd, MMM yyyy");
  }

  public static SimpleDateFormat getDateFormatYearFormat() {
    return new SimpleDateFormat("yyyy");
  }

  public static SimpleDateFormat getHourMinuteSecondFormat() {
    return new SimpleDateFormat("HH:mm:ss");
  }

  public static SimpleDateFormat getLabkeyDateFormat() {
    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ"); // "2017/06/15 08:27:07"
  }

  public static SimpleDateFormat getHourAmPmFormat() {
    return new SimpleDateFormat("hhaa");
  }

  public static SimpleDateFormat getHourAmPmFormat1() {
    return new SimpleDateFormat("hh:mmaa");
  }

  public static SimpleDateFormat getDdFormat() {
    return new SimpleDateFormat("dd");
  }

  public static SimpleDateFormat getEeFormat() {
    return new SimpleDateFormat("EE");
  }

  public static SimpleDateFormat getHourAmPmMonthDayYearFormat() {
    return new SimpleDateFormat("hh:mmaa, MMM dd, yyyy");
  }

  public static void showPasscodeActivity(Context context1, Class context2) {
    Intent intent = new Intent(context1, context2);
    context1.startActivity(intent);
  }

  public static AlertDialog upgrade(
      int resultCode, boolean forceUpgrade, Context context, String version, String message) {
    AlertDialog alertDialog;
    if (forceUpgrade) {
      alertDialog =
          setNeutralDialog(
              resultCode,
              forceUpgrade,
              context,
              context.getResources().getString(R.string.force_upgrade),
              false,
              context.getResources().getString(R.string.upgrade),
              context.getResources().getString(R.string.upgrade),
              version,
              message);
    } else {
      alertDialog =
          setNeutralDialog(
              resultCode,
              forceUpgrade,
              context,
              context.getResources().getString(R.string.normal_upgrade),
              false,
              context.getResources().getString(R.string.upgrade),
              context.getResources().getString(R.string.upgrade),
              version,
              message);
    }
    return alertDialog;
  }

  public static AlertDialog setNeutralDialog(
      final int resultCode,
      final boolean forceUpgrade,
      final Context context,
      String message,
      final boolean finish,
      String positiveButton,
      String title,
      String version,
      String versionMessage) {
    if (!forceUpgrade) {
      AlertDialog.Builder alertDialogBuilder =
          new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
      alertDialogBuilder.setTitle(title);
      alertDialogBuilder
          .setMessage(message)
          .setCancelable(false)
          .setPositiveButton(
              positiveButton,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {}
              })
          .setNegativeButton(
              context.getResources().getString(R.string.cancel),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  dialog.dismiss();
                  if (finish) {
                    ((Activity) context).finish();
                  }
                }
              });

      final AlertDialog alertDialog = alertDialogBuilder.create();
      alertDialog.show();
      alertDialog
          .getButton(AlertDialog.BUTTON_POSITIVE)
          .setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  // Do stuff, possibly set wantToCloseDialog to true then...
                  final String appPackageName = context.getPackageName();
                  try {
                    ((Activity) context)
                        .startActivityForResult(
                            new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName)),
                            resultCode);
                  } catch (android.content.ActivityNotFoundException anfe) {
                    ((Activity) context)
                        .startActivityForResult(
                            new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                    "https://play.google.com/store/apps/details?id="
                                        + appPackageName)),
                            resultCode);
                  }
                  if (finish) {
                    ((Activity) context).finish();
                  }
                  if (!forceUpgrade) {
                    alertDialog.dismiss();
                  }
                  // else dialog stays open. Make sure you have an obvious way to close the dialog
                  // especially if you set cancellable to false.
                }
              });
      alertDialog
          .getButton(AlertDialog.BUTTON_NEGATIVE)
          .setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  alertDialog.dismiss();
                  ((SplashActivity) context).loadsplash();
                }
              });
      return alertDialog;
    } else {
      AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
      dialogBuilder.setCancelable(false);

      // ...Irrelevant code for customizing the buttons and title
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      View dialogView = inflater.inflate(R.layout.force_upgrade_lay, null);
      TextView upgrade = (TextView) dialogView.findViewById(R.id.upgrade);
      TextView desc = (TextView) dialogView.findViewById(R.id.desc);
      desc.setText(versionMessage);
      upgrade.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final String appPackageName = context.getPackageName();
              try {
                ((Activity) context)
                    .startActivityForResult(
                        new Intent(
                            Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),
                        resultCode);
              } catch (android.content.ActivityNotFoundException anfe) {
                ((Activity) context)
                    .startActivityForResult(
                        new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "https://play.google.com/store/apps/details?id=" + appPackageName)),
                        resultCode);
              }
            }
          });
      dialogBuilder.setView(dialogView);
      TextView title1 = (TextView) dialogView.findViewById(R.id.title);
      title1.setText(version);

      AlertDialog alertDialog = dialogBuilder.create();
      alertDialog.show();
      return alertDialog;
    }
  }

  // executes a command on the system
  private static boolean canExecuteCommand(String command) {
    boolean executedSuccesfully;
    try {
      Runtime.getRuntime().exec(command);
      executedSuccesfully = true;
    } catch (Exception e) {
      executedSuccesfully = false;
    }

    return executedSuccesfully;
  }

  /** KEYSTORE RELATED CODES HERE. */
  public static void keystoreInitilize(Context context) {
    try {
      keyStore = KeyStore.getInstance("AndroidKeyStore");
      keyStore.load(null);
    } catch (Exception e) {
      Logger.log(e);
    }
    String secretKeyVal = refreshKeys("key");
    if (secretKeyVal == null) {
      // to identify specific key and get that using key_
      String alias1 = "key_" + secretKeyToString();
      // genarate random iv and to get using iv_
      // keystore, store the secretkey; differentiate by key_
      createNewKeys(context, alias1);
    }
    String ivBytesVal = refreshKeys("iv");
    if (ivBytesVal == null) {
      // genarate random iv and to get using iv_
      String alias2 = "iv_" + ivBytesToString();
      // keystore, store the ivBytes string; differentiate by iv_
      createNewKeys(context, alias2);
    }
  }

  // get the stored keystore values
  public static String refreshKeys(String concatedString) {

    try {
      Enumeration<String> aliases;
      if (keyStore == null) {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
      }
      aliases = keyStore.aliases();
      String val;
      while (aliases.hasMoreElements()) {
        val = aliases.nextElement();
        if (val.contains(concatedString)) {
          String[] splitString = val.split("_");
          return splitString[1];
        }
      }
    } catch (Exception e) {
      Logger.log(e);
    }
    return null;
  }

  // first time create new keystore value
  public static void createNewKeys(Context context, String alias) {
    try {
      // Create new key if needed
      if (!keyStore.containsAlias(alias)) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);
        KeyPairGeneratorSpec spec =
            new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        generator.initialize(spec);
        generator.generateKeyPair();
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  // delete the keystore value
  public static void deleteKey(String key) {
    try {
      keyStore.deleteEntry(key);
    } catch (KeyStoreException e) {
      Logger.log(e);
    }
  }

  // encrypt the string
  public static void encryptString(Context context, String passwordString) {
    try {
      refreshKeys("key");
      KeyStore.PrivateKeyEntry privateKeyEntry =
          (KeyStore.PrivateKeyEntry) keyStore.getEntry(keystoreValue, null);
      RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

      Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
      inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inCipher);
      cipherOutputStream.write(passwordString.getBytes("UTF-8"));
      cipherOutputStream.close();

      byte[] vals = outputStream.toByteArray();
      String abc = Base64.encodeToString(vals, Base64.DEFAULT);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  // decrypt the string
  public static void decryptString(Context context) {
    try {
      refreshKeys("key");
      KeyStore.PrivateKeyEntry privateKeyEntry =
          (KeyStore.PrivateKeyEntry) keyStore.getEntry(keystoreValue, null);
      RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

      Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
      output.init(Cipher.DECRYPT_MODE, privateKey);

      String cipherText =
          "FRxCp9t99YTZvWEVewZ3PRMN3Xfc4kV7mlO9dPy8BBNSRz/y89BC+wLIq3/HTN9zKCFcNkO7Xg2h\n"
              + "eCUULE9MWQb7Sj1pjQR2A81N/kBgHNZjOzsfLLzKYPSMHVHy85xXnXwkY/5Jc5Jo4d8S65DeLY/7\n"
              + "6bDIhqanYzSAJr4IaQI6tC3mU+SMqA6GyyadNk3R9EwqIjTaXSj4aj/5hDCl37aW807Q3jfbX0XK\n"
              + "d8dY8zY+w/H3PazNah6/MyQYbN0y1buxIZRyN2C2rZv6F1UA3kb0/u+G/TusZy1fV38kkOC+/pbV\n"
              + "xh9ouDOLEjkR0yLSgkJKqsFE1PiLs+AS9/C0iQ==";
      CipherInputStream cipherInputStream =
          new CipherInputStream(
              new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
      ArrayList<Byte> values = new ArrayList<>();
      int nextByte;
      while ((nextByte = cipherInputStream.read()) != -1) {
        values.add((byte) nextByte);
      }

      byte[] bytes = new byte[values.size()];
      for (int i = 0; i < bytes.length; i++) {
        bytes[i] = values.get(i).byteValue();
      }

      String finalText = new String(bytes, 0, bytes.length, "UTF-8");
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  // genarate random string
  private static String getRandomString() {
    Random generator = new Random();
    StringBuilder randomStringBuilder = new StringBuilder();
    // sometime random val coming null so legth 7 hard-coded
    int randomLength = 7;
    char tempChar;
    for (int i = 0; i < randomLength; i++) {
      tempChar = (char) (generator.nextInt(96) + 32);
      randomStringBuilder.append(tempChar);
    }
    return randomStringBuilder.toString();
  }

  // convert  String to SecretKey
  public static SecretKey stringToSecretKey(String abc) {
    byte[] encodedKey = Base64.decode(abc, Base64.DEFAULT);
    SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    return originalKey;
  }

  // convert SecretKey to String
  public static String secretKeyToString() {
    String keyConvertString = "";
    try {
      KeyGenerator kgen = KeyGenerator.getInstance("AES");
      kgen.init(256);
      SecretKey skey = kgen.generateKey();
      keyConvertString = Base64.encodeToString(skey.getEncoded(), Base64.DEFAULT);
    } catch (NoSuchAlgorithmException e) {
      Logger.log(e);
    }
    return keyConvertString;
  }

  // convert ivBytes to String
  public static String ivBytesToString() {
    String ivBytesConvertString = "";
    try {
      SecureRandom rand = new SecureRandom();
      byte[] ivBytes = new byte[16];
      rand.nextBytes(ivBytes);
      ivBytesConvertString = new String(Base64.encode(ivBytes, Base64.DEFAULT), "UTF-8");
    } catch (Exception e) {
      Logger.log(e);
    }
    return ivBytesConvertString;
  }

  // convert  String to ivBytes
  public static byte[] stringToIvBytes(String val) {
    byte[] ivBytes = null;
    try {
      ivBytes = val.getBytes("UTF-8");
    } catch (Exception e) {
      Logger.log(e);
    }
    return ivBytes;
  }

  // encrypt the pdf file and return File
  public static File generateEncryptedConsentPdf(String filePath, String timeStamp) {
    try {
      FileInputStream fis = new FileInputStream(new File(filePath + timeStamp + ".pdf"));
      File encryptFile = new File(filePath + File.separator + timeStamp + ".txt");
      int read;
      if (!encryptFile.exists()) {
        encryptFile.createNewFile();
      }
      FileOutputStream fos = new FileOutputStream(encryptFile);
      Cipher encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

      String val = AppController.refreshKeys("key");
      SecretKey skey = AppController.stringToSecretKey(val);

      String val2 = AppController.refreshKeys("iv");
      byte[] ivBytes = AppController.stringToIvBytes(val2);

      try {
        encipher.init(
            Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(Base64.decode(ivBytes, Base64.DEFAULT)));
      } catch (InvalidAlgorithmParameterException e) {
        Logger.log(e);
      }
      CipherInputStream cis = new CipherInputStream(fis, encipher);
      while ((read = cis.read()) != -1) {
        fos.write((char) read);
        fos.flush();
      }
      fos.close();
      return encryptFile;

    } catch (FileNotFoundException e) {
      Logger.log(e);
    } catch (NoSuchPaddingException e) {
      Logger.log(e);
    } catch (NoSuchAlgorithmException e) {
      Logger.log(e);
    } catch (IOException e) {
      Logger.log(e);
    } catch (InvalidKeyException e) {
      Logger.log(e);
    }
    return null;
  }

  // decrypt the pdf file and return CipherInputStream
  public static CipherInputStream generateDecryptedConsentPdf(String filePath) {
    try {
      FileInputStream fis = new FileInputStream(new File(filePath));
      Cipher encipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      String getStringTypeKey = AppController.refreshKeys("key");
      SecretKey skey = AppController.stringToSecretKey(getStringTypeKey);
      String getStringIvByte = AppController.refreshKeys("iv");
      byte[] ivBytes = AppController.stringToIvBytes(getStringIvByte);
      try {
        encipher.init(
            Cipher.DECRYPT_MODE, skey, new IvParameterSpec(Base64.decode(ivBytes, Base64.DEFAULT)));
      } catch (InvalidAlgorithmParameterException e) {
        Logger.log(e);
      }
      CipherInputStream cis = new CipherInputStream(fis, encipher);
      return cis;
    } catch (FileNotFoundException
        | NoSuchPaddingException
        | NoSuchAlgorithmException
        | InvalidKeyException e) {
      Logger.log(e);
    }
    return null;
  }

  // convert cipherInputStreamConvertToByte to byte []
  public static byte[] cipherInputStreamConvertToByte(CipherInputStream cis) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int len;
      byte[] buffer = new byte[4096];
      while ((len = cis.read(buffer, 0, buffer.length)) != -1) {
        baos.write(buffer, 0, len);
      }
      baos.flush();
      byte[] cipherByteArray = baos.toByteArray();
      return cipherByteArray;

    } catch (IOException e) {
      Logger.log(e);
    }
    return null;
  }

  /////////// offline data storing
  /*
  number------- > auto increment
  httpMethod--- > post/get
  url---------- > server url
  serverType--- > registraion/response/studyBuilder
  userProfileId-> userId (only for profile scenario handling other case pass "" )
  studyId-------> handling bookmark duplicate(only using the class studyinfo other class pass it "")
  activityId----> handling SurveyActivitiesFragment duplication other class pass it ""
   */
  public static void pendingService(
      Context context,
      int number,
      String httpMethod,
      String url,
      String normalParam,
      String jsonParam,
      String serverType,
      String userProfileId,
      String studyId,
      String activityId) {

    try {
      OfflineData offlineData = new OfflineData();

      offlineData.setNumber(number);
      offlineData.setDate(new Date());
      offlineData.setHttpMethod(httpMethod);
      offlineData.setUrl(url);
      offlineData.setNormalParam(normalParam);
      offlineData.setJsonParam(jsonParam);
      offlineData.setServerType(serverType);
      offlineData.setUserProfileId(userProfileId);
      offlineData.setStudyId(studyId);
      offlineData.setActivityId(activityId);
      offlineData.setStatus(false);
      DbServiceSubscriber db = new DbServiceSubscriber();
      db.saveOfflineData(context, offlineData);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public static void forceSignout(Context context) {
    SharedPreferences settings = SharedPreferenceHelper.getPreferences(context);
    settings.edit().clear().apply();
    // delete passcode from keystore
    String pass = AppController.refreshKeys("passcode");
    if (pass != null) {
      AppController.deleteKey("passcode_" + pass);
    }
    DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
    Realm realm = getRealmobj(context);
    dbServiceSubscriber.deleteDb(context);
    try {
      NotificationModuleSubscriber notificationModuleSubscriber =
          new NotificationModuleSubscriber(dbServiceSubscriber, realm);
      notificationModuleSubscriber.cancleActivityLocalNotification(context);
      notificationModuleSubscriber.cancleResourcesLocalNotification(context);
    } catch (Exception e) {
      Logger.log(e);
    }
    NotificationModuleSubscriber notificationModuleSubscriber =
        new NotificationModuleSubscriber(dbServiceSubscriber, realm);
    notificationModuleSubscriber.cancelNotificationTurnOffNotification(context);
    dbServiceSubscriber.closeRealmObj(realm);

    // clear notifications from notification tray
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
    notificationManager.cancelAll();

    if (AppConfig.AppType.equalsIgnoreCase(context.getString(R.string.app_gateway))) {
      Intent intent = new Intent(context, StudyActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      mainIntent.putExtra("action", loginCallback);
      context.startActivity(mainIntent);
      ((Activity) context).finish();
    } else {
      Intent intent = new Intent(context, StandaloneActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      mainIntent.putExtra("action", loginCallback);
      context.startActivity(mainIntent);
      ((Activity) context).finish();
    }
  }

  public static String getHashedValue(String valueToHash) {
    String generatedHash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(valueToHash.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedHash = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      Logger.log(e);
    }
    return generatedHash;
  }

  public static boolean checkafter(Date starttime) {
    return starttime.after(new Date());
  }

  public static boolean isWithinRange(Date starttime, Date endtime) {
    if (endtime == null) {
      return (new Date().after(starttime) || new Date().equals(starttime));
    } else {
      return (new Date().after(starttime) || new Date().equals(starttime))
              && new Date().before(endtime);
    }
  }

  public static String getSourceActivityId(Resource resource) {
    return resource.getAvailability().getSourceActivityId();
  }

  public static String getSourceKey(Resource resource) {
    return resource.getAvailability().getSourceKey();
  }
}
