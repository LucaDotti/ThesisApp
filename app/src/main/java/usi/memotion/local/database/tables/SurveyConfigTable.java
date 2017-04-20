package usi.memotion.local.database.tables;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import usi.memotion.MyApplication;
import usi.memotion.R;
import usi.memotion.surveys.config.SurveyType;

import static usi.memotion.surveys.config.SurveyType.GROUPED_SSPP;
import static usi.memotion.surveys.config.SurveyType.PAM;
import static usi.memotion.surveys.config.SurveyType.PWB;

/**
 * Created by usi on 13/04/17.
 */

public class SurveyConfigTable {
    public static final String TABLE_SURVEY_CONFIG = "survey_config";
    public static final String KEY_SURVEY_CONFIG_ID = "id_survey_config";
    public static final String KEY_SURVEY_CONFIG_SURVEY_TYPE = "survey_type";
    public static final String KEY_SURVEY_CONFIG_GROUPED = "grouped";
    public static final String KEY_SURVEY_CONFIG_IMMEDIATE = "immediate";

    public static final String KEY_SURVEY_CONFIG_FREQUENCY_UNIT = "frequency";
    public static final String KEY_SURVEY_CONFIG_FREQUENCY_MULTIPLIER = "frequency_multiplier";
    public static final String KEY_SURVEY_CONFIG_SURVEYS_COUNT = "surveys_count";
    public static final String KEY_SURVEY_CONFIG_PERIOD = "period";

    public static final String KEY_SURVEY_CONFIG_DAILY_SURVEYS_COUNT = "daily_surveys_count";
    public static final String KEY_SURVEY_CONFIG_MAX_COMPLETION_TIME = "max_completion_time";
    public static final String KEY_SURVEY_CONFIG_MIN_TIME_BETWEEN_SURVEYS = "min_time_between_surveys";
    public static final String KEY_SURVEY_CONFIG_NOTIFICATIONS_COUNT = "notifications_count";

    public static final String KEY_SURVEY_CONFIG_ISSUED_SURVEY_COUNT = "issued_survey_count";


    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_SURVEY_CONFIG +
                "(" +
                KEY_SURVEY_CONFIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_SURVEY_CONFIG_SURVEY_TYPE + " TEXT, " +
                KEY_SURVEY_CONFIG_GROUPED + " INTEGER, " +
                KEY_SURVEY_CONFIG_IMMEDIATE + " INTEGER, " +
                KEY_SURVEY_CONFIG_FREQUENCY_UNIT + " TEXT, " +
                KEY_SURVEY_CONFIG_FREQUENCY_MULTIPLIER + " INTEGER, " +
                KEY_SURVEY_CONFIG_SURVEYS_COUNT + " INTEGER, " +
                KEY_SURVEY_CONFIG_PERIOD + " TEXT, " +
                KEY_SURVEY_CONFIG_DAILY_SURVEYS_COUNT + " INTEGER, " +
                KEY_SURVEY_CONFIG_MAX_COMPLETION_TIME + " INTEGER, " +
                KEY_SURVEY_CONFIG_MIN_TIME_BETWEEN_SURVEYS + " INTEGER, " +
                KEY_SURVEY_CONFIG_NOTIFICATIONS_COUNT + " INTEGER, " +
                KEY_SURVEY_CONFIG_ISSUED_SURVEY_COUNT + " INTEGER"
                + ")";
    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_SURVEY_CONFIG_ID,
                KEY_SURVEY_CONFIG_SURVEY_TYPE,
                KEY_SURVEY_CONFIG_GROUPED,
                KEY_SURVEY_CONFIG_IMMEDIATE,
                KEY_SURVEY_CONFIG_FREQUENCY_UNIT,
                KEY_SURVEY_CONFIG_FREQUENCY_MULTIPLIER,
                KEY_SURVEY_CONFIG_SURVEYS_COUNT,
                KEY_SURVEY_CONFIG_PERIOD,
                KEY_SURVEY_CONFIG_DAILY_SURVEYS_COUNT,
                KEY_SURVEY_CONFIG_MAX_COMPLETION_TIME,
                KEY_SURVEY_CONFIG_MIN_TIME_BETWEEN_SURVEYS,
                KEY_SURVEY_CONFIG_NOTIFICATIONS_COUNT,
                KEY_SURVEY_CONFIG_ISSUED_SURVEY_COUNT
        };

        return columns;
    }

    public static List<ContentValues> getRecords() {
        Context context = MyApplication.getContext();
        List<ContentValues> records = new ArrayList<>();
        ContentValues current = new ContentValues();

        for(SurveyType surveyType: SurveyType.values()) {
            switch(surveyType) {
                case PAM:
                    current.put(KEY_SURVEY_CONFIG_ID, PAM.getId());
                    current.put(KEY_SURVEY_CONFIG_SURVEY_TYPE, PAM.getSurveyName());
                    current.put(KEY_SURVEY_CONFIG_GROUPED, Integer.parseInt(context.getString(R.string.PAM_grouped)));
                    current.put(KEY_SURVEY_CONFIG_IMMEDIATE, Integer.parseInt(context.getString(R.string.PAM_immediate)));
                    current.put(KEY_SURVEY_CONFIG_FREQUENCY_UNIT, context.getString(R.string.PAM_frequency));
                    current.put(KEY_SURVEY_CONFIG_FREQUENCY_MULTIPLIER, Integer.parseInt(context.getString(R.string.PAM_frequency_multiplier)));
                    current.put(KEY_SURVEY_CONFIG_SURVEYS_COUNT, Integer.parseInt(context.getString(R.string.PAM_count)));
                    current.put(KEY_SURVEY_CONFIG_DAILY_SURVEYS_COUNT, Integer.parseInt(context.getString(R.string.PAM_daily_count)));
                    current.put(KEY_SURVEY_CONFIG_MAX_COMPLETION_TIME, Integer.parseInt(context.getString(R.string.PAM_max_elapse_time_for_completion)));
                    current.put(KEY_SURVEY_CONFIG_MIN_TIME_BETWEEN_SURVEYS, Integer.parseInt(context.getString(R.string.PAM_min_elapse_time_between_surveys)));
                    current.put(KEY_SURVEY_CONFIG_NOTIFICATIONS_COUNT, Integer.parseInt(context.getString(R.string.PAM_notifications_count)));
                    break;
                case PWB:
                    current.put(KEY_SURVEY_CONFIG_ID, PWB.getId());
                    current.put(KEY_SURVEY_CONFIG_SURVEY_TYPE, PWB.getSurveyName());
                    current.put(KEY_SURVEY_CONFIG_GROUPED, Integer.parseInt(context.getString(R.string.PWB_grouped)));
                    current.put(KEY_SURVEY_CONFIG_IMMEDIATE, Integer.parseInt(context.getString(R.string.PWB_immediate)));
                    current.put(KEY_SURVEY_CONFIG_FREQUENCY_UNIT, context.getString(R.string.PWB_frequency));
                    current.put(KEY_SURVEY_CONFIG_FREQUENCY_MULTIPLIER, Integer.parseInt(context.getString(R.string.PWB_frequency_multiplier)));
                    current.put(KEY_SURVEY_CONFIG_SURVEYS_COUNT, Integer.parseInt(context.getString(R.string.PWB_count)));
                    current.put(KEY_SURVEY_CONFIG_DAILY_SURVEYS_COUNT, Integer.parseInt(context.getString(R.string.PWB_daily_count)));
                    current.put(KEY_SURVEY_CONFIG_MAX_COMPLETION_TIME, Integer.parseInt(context.getString(R.string.PWB_max_elapse_time_for_completion)));
                    current.put(KEY_SURVEY_CONFIG_MIN_TIME_BETWEEN_SURVEYS, Integer.parseInt(context.getString(R.string.PWB_min_elapse_time_between_surveys)));
                    current.put(KEY_SURVEY_CONFIG_NOTIFICATIONS_COUNT, Integer.parseInt(context.getString(R.string.PWB_notifications_count)));
                    current.put(KEY_SURVEY_CONFIG_PERIOD, context.getString(R.string.PWB_week_period));
                    break;
                case GROUPED_SSPP:
                    current.put(KEY_SURVEY_CONFIG_ID, GROUPED_SSPP.getId());
                    current.put(KEY_SURVEY_CONFIG_SURVEY_TYPE, GROUPED_SSPP.getSurveyName());
                    current.put(KEY_SURVEY_CONFIG_GROUPED, Integer.parseInt(context.getString(R.string.term_grouped)));
                    current.put(KEY_SURVEY_CONFIG_IMMEDIATE, Integer.parseInt(context.getString(R.string.term_immediate)));
                    current.put(KEY_SURVEY_CONFIG_FREQUENCY_UNIT, context.getString(R.string.term_frequency));
                    current.put(KEY_SURVEY_CONFIG_FREQUENCY_MULTIPLIER, Integer.parseInt(context.getString(R.string.term_frequency_multiplier)));
                    current.put(KEY_SURVEY_CONFIG_SURVEYS_COUNT, Integer.parseInt(context.getString(R.string.term_count)));
                    current.put(KEY_SURVEY_CONFIG_DAILY_SURVEYS_COUNT, Integer.parseInt(context.getString(R.string.term_daily_count)));
                    current.put(KEY_SURVEY_CONFIG_MAX_COMPLETION_TIME, Integer.parseInt(context.getString(R.string.term_max_elapse_time_for_completion)));
                    current.put(KEY_SURVEY_CONFIG_MIN_TIME_BETWEEN_SURVEYS, Integer.parseInt(context.getString(R.string.term_min_elapse_time_between_surveys)));
                    current.put(KEY_SURVEY_CONFIG_NOTIFICATIONS_COUNT, Integer.parseInt(context.getString(R.string.term_notifications_count)));
                    break;
                default:
                    current = null;
            }

            if(current != null) {
                records.add(current);
            }
            current = new ContentValues();
        }

        return records;
    }
}
