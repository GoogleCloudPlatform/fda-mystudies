package org.sagebionetworks.bridge.researchstack;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Preferences specific to Crf.
 */
public class CrfPrefs {
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Statics
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private static final String KEY_CUSTOM_SURVEY_QUESTION = "custom_survey_question";
    private static final String KEY_CUSTOM_SURVEY_COUNTER  = "custom_survey_counter";
    private static CrfPrefs instance;

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Field Vars
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private final SharedPreferences prefs;

    CrfPrefs(Context context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
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

    public void clear() {
        prefs.edit().clear().commit();
    }

}