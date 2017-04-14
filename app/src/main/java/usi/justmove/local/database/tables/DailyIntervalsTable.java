package usi.justmove.local.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import usi.justmove.MyApplication;
import usi.justmove.R;
import usi.justmove.gathering.surveys.config.SurveyType;

/**
 * Created by usi on 13/04/17.
 */

public class DailyIntervalsTable {
    public static final String TABLE_DAILY_INTERVALS_TABLE = "daily_intervals";
    public static final String KEY_DAILY_INTERVALS_ID = "id_daily_intervals";
    public static final String KEY_DAILY_INTERVALS_CONFIG_ID = "config_id";
    public static final String KEY_DAILY_INTERVALS_TIME_START = "time_start";
    public static final String KEY_DAILY_INTERVALS_TIME_END = "time_end";

    public static String getCreateQuery() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_DAILY_INTERVALS_TABLE +
                "(" +
                KEY_DAILY_INTERVALS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_DAILY_INTERVALS_CONFIG_ID + " INTEGER NOT NULL, " +
                KEY_DAILY_INTERVALS_TIME_START + " TEXT, " +
                KEY_DAILY_INTERVALS_TIME_END + " TEXT, " +
                "FOREIGN KEY (" + KEY_DAILY_INTERVALS_CONFIG_ID + ") REFERENCES " + SurveyConfigTable.TABLE_SURVEY_CONFIG +  " (" + SurveyConfigTable.KEY_SURVEY_CONFIG_ID + ")"
                + ")";
    }

    public static String[] getColumns() {
        String[] columns = {
                KEY_DAILY_INTERVALS_ID,
                KEY_DAILY_INTERVALS_CONFIG_ID,
                KEY_DAILY_INTERVALS_TIME_START,
                KEY_DAILY_INTERVALS_TIME_END
        };

        return columns;
    }

    public static List<ContentValues> getRecords() {
        Context context = MyApplication.getContext();
        List<ContentValues> records = new ArrayList<>();

        String[] intervals;
        List<ContentValues> currentRecords = new ArrayList<>();
        for(SurveyType surveyType: SurveyType.values()) {
            switch(surveyType) {
                case PAM:
                    intervals = context.getResources().getStringArray(R.array.PAM_daily_times);
                    break;
                case PWB:
                    intervals = context.getResources().getStringArray(R.array.PWB_daily_times);
                    break;
                case GROUPED_SSPP:
                    intervals = context.getResources().getStringArray(R.array.term_daily_times);
                    break;
                default:
                    intervals = null;
            }
            if(intervals != null) {
                currentRecords = getRecordsFromIntervals(intervals, surveyType.getId());

                if(currentRecords != null) {
                    records.addAll(currentRecords);
                }
            }


        }

        return records;
    }

    private static List<ContentValues> getRecordsFromIntervals(String[] intervals, int configId) {
        if(intervals.length == 0) {
            return null;
        }

        List<ContentValues> records = new ArrayList<>();
        ContentValues current = new ContentValues();
        String[] split;
        for(String interval: intervals) {
            split = interval.split(",");
            current.put(KEY_DAILY_INTERVALS_CONFIG_ID, configId);
            current.put(KEY_DAILY_INTERVALS_TIME_START, split[0]);
            current.put(KEY_DAILY_INTERVALS_TIME_END, split[1]);

            records.add(current);

            current = new ContentValues();
        }

        return records;
    }
}
