package org.sagebionetworks.bridge.researchstack;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Preferences specific to Crf.
 */
public class CrfPrefs {
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Statics
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private static final String KEY_CUSTOM_SURVEY_QUESTION = "custom_survey_question";
    private static final String KEY_CUSTOM_SURVEY_COUNTER  = "custom_survey_counter";

    private static final String KEY_FIRST_SIGN_IN_DATE_TIME = "first_sign_in_date_time";

    private static final String KEY_REMINDER_DATES = "reminder_dates";
    private static final String KEY_REMINDER_TIME_HOUR = "reminder_time_hour";
    private static final String KEY_REMINDER_TIME_MINUTE = "reminder_time_minute";

    private static CrfPrefs instance;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Field Vars
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private final SharedPreferences prefs;

    public static final DateTimeFormatter FORMATTER = ISODateTimeFormat.dateTime().withOffsetParsed();

    CrfPrefs(Context context)
    {
        prefs = createPrefs(context);
    }

    @VisibleForTesting
    SharedPreferences createPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        instance = new CrfPrefs(context);
    }

    public static CrfPrefs getInstance()
    {
        if(instance == null) {
            throw new RuntimeException(
                    "CrfPrefs instance is null. Make sure it is initialized in ResearchStack before calling.");
        }
        return instance;
    }

    public String getCustomSurveyQuestion() {
        String question = prefs.getString(KEY_CUSTOM_SURVEY_QUESTION, null);
        return question;
    }

    public void setCustomSurveyQuestion(String question) {
        prefs.edit().putString(KEY_CUSTOM_SURVEY_QUESTION, question).apply();
    }

    public int getCustomSurveyCounter() {
        return prefs.getInt(KEY_CUSTOM_SURVEY_COUNTER, 0);
    }

    public void setCustomSurveyCounter(int counter) {
        prefs.edit().putInt(KEY_CUSTOM_SURVEY_COUNTER, counter).apply();
    }

    public boolean hasClinicDate() {
        return getClinicDate() != null;
    }

    @Nullable
    public DateTime getClinicDate() {
        String jsonString = prefs.getString(KEY_FIRST_SIGN_IN_DATE_TIME, null);
        if (jsonString == null) {
            return null;
        }
        return FORMATTER.parseDateTime(jsonString);
    }

    public void setClinicDate(DateTime dateTime) {
        // We need commit() instead of apply() to have this happen immediately
        if (dateTime == null) {
            prefs.edit().remove(KEY_FIRST_SIGN_IN_DATE_TIME).commit();
        } else {
            String jsonString = FORMATTER.print(dateTime);
            prefs.edit().putString(KEY_FIRST_SIGN_IN_DATE_TIME, jsonString).commit();
        }
    }

    public void setReminderDates(List<Date> reminderDates) {
        Set<String> reminderDateStrings = new HashSet<>();
        for (Date date : reminderDates) {
            reminderDateStrings.add(FORMATTER.print(new DateTime(date)));
        }
        prefs.edit().putStringSet(KEY_REMINDER_DATES, reminderDateStrings).apply();
    }

    public @Nullable List<Date> getReminderDates() {
        Set<String> reminderDateStrings = prefs.getStringSet(KEY_REMINDER_DATES, null);
        if (reminderDateStrings == null) {
            return null;
        }
        List<Date> reminderDates = new ArrayList<>();
        for (String reminderString : reminderDateStrings) {
            reminderDates.add(FORMATTER.parseDateTime(reminderString).toDate());
        }
        return reminderDates;
    }

    public void setKeyReminderTimeHour(int hour) {
        prefs.edit().putInt(KEY_REMINDER_TIME_HOUR, hour).commit();
    }

    public int getReminderTimeHour(int defaultValue) {
        return prefs.getInt(KEY_REMINDER_TIME_HOUR, defaultValue);
    }

    public void setKeyReminderTimeMinute(int minute) {
        prefs.edit().putInt(KEY_REMINDER_TIME_MINUTE, minute).commit();
    }

    public int getReminderTimeMinute(int defaultValue) {
        return prefs.getInt(KEY_REMINDER_TIME_MINUTE, defaultValue);
    }

    public void clear() {
        prefs.edit().clear().commit();
    }
}