package org.sagebionetworks.bridge.researchstack;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

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

    public boolean hasFirstSignInDate() {
        return getFirstSignInDate() != null;
    }

    public DateTime getFirstSignInDate() {
        String jsonString = prefs.getString(KEY_FIRST_SIGN_IN_DATE_TIME, null);
        if (jsonString == null) {
            return null;
        }
        return FORMATTER.parseDateTime(jsonString);
    }

    public void setFirstSignInDate(DateTime dateTime) {
        // We need commit() instead of apply() to have this happen immediately
        if (dateTime == null) {
            prefs.edit().remove(KEY_FIRST_SIGN_IN_DATE_TIME).commit();
        } else {
            String jsonString = FORMATTER.print(dateTime);
            prefs.edit().putString(KEY_FIRST_SIGN_IN_DATE_TIME, jsonString).commit();
        }
    }

    public void clear() {
        prefs.edit().clear().commit();
    }
}