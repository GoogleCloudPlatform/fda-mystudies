/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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
 *
 */

package com.harvard.utils.realm;

import com.harvard.utils.version.Android;
import com.harvard.utils.version.VersionModel;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigrationHelper implements RealmMigration {

  @Override
  public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
    RealmSchema schema = realm.getSchema();

    if (oldVersion == 0) {
      // time separated into start and end time
      RealmObjectSchema anchorRuns = schema.get("AnchorRuns");
      anchorRuns
          .addField("startTime", String.class)
          .addField("endTime", String.class)
          .transform(
              new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                  obj.set("startTime", obj.getString("time"));
                  obj.set("endTime", obj.getString("time"));
                }
              })
          .removeField("time");

      // Added enroll field
      RealmObjectSchema consentDocumentData = schema.get("ConsentDocumentData");
      consentDocumentData
          .addField("enrollAgain", boolean.class)
          .transform(
              new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                  obj.set("enrollAgain", false);
                }
              });

      RealmObjectSchema studyUpdate = schema.get("StudyUpdate");
      studyUpdate
          .addField("enrollAgain", boolean.class)
          .transform(
              new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                  obj.set("enrollAgain", false);
                }
              });

      oldVersion++;
    } else if (oldVersion == 1) {
      // Added verificationTime field
      RealmObjectSchema profile = schema.get("Profile");
      profile
          .addField("verificationTime", String.class)
          .transform(
              new RealmObjectSchema.Function() {
                @Override
                public void apply(DynamicRealmObject obj) {
                  obj.set("verificationTime", "");
                }
              });

      // Added Apps
      schema.create("Android")
          .addField("latestVersion", String.class)
          .addField("forceUpdate", String.class);

      schema.create("VersionModel")
          .addField("android", Android.class);

      schema.create("Apps")
          .addField("message", String.class)
          .addField("appName", String.class)
          .addField("appId", String.class)
          .addField("fromEmail", String.class)
          .addField("contactUsEmail", String.class)
          .addField("supportEmail", String.class)
          .addField("status", int.class)
          .addField("code", String.class)
          .addField("termsUrl", String.class)
          .addField("appWebsite", String.class)
          .addField("version", VersionModel.class);

      oldVersion++;
    }
  }
}
