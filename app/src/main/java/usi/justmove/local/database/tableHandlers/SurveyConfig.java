package usi.justmove.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Pair;

import java.util.List;

import usi.justmove.gathering.surveys.config.Frequency;
import usi.justmove.gathering.surveys.config.FrequencyUnit;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.local.database.LocalDbUtility;
import usi.justmove.local.database.LocalTables;
import usi.justmove.gathering.surveys.config.Period;
import usi.justmove.local.database.tables.DailyIntervalsTable;
import usi.justmove.local.database.tables.SurveyConfigTable;

/**
 * Created by usi on 14/04/17.
 */

public class SurveyConfig extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_SURVEY_CONFIG;

    public SurveyType surveyType;
    public boolean grouped;
    public boolean immediate;
    public FrequencyUnit frequencyUnit;
    public int frequencyMultiplier;
    public int surveysCount;
    public Period period;
    public int dailySurveysCount;
    public int maxCompletionTime;
    public int minTimeBetweenSurveys;
    public int notificationsCount;
    public int issuedSurveyCount;

    public Pair<String, String>[] dailyIntervals;
    public Frequency frequency;

    public SurveyConfig(boolean isNewRecord) {
        super(isNewRecord);
        columns = LocalDbUtility.getTableColumns(table);
        id = -1;
    }

    @Override
    public void setAttributes(ContentValues attributes) {
        if(attributes.containsKey(columns[0])) {
            id = attributes.getAsLong(columns[0]);
        }

        if(attributes.containsKey(columns[1])) {
            surveyType = SurveyType.getSurvey(attributes.getAsString(columns[1]));
        }

        if(attributes.containsKey(columns[2])) {
            grouped = attributes.getAsBoolean(columns[2]);
        }

        if(attributes.containsKey(columns[3])) {
            immediate = attributes.getAsBoolean(columns[3]);
        }

        if(attributes.containsKey(columns[4])) {
            frequencyUnit = FrequencyUnit.getFrequency(attributes.getAsString(columns[4]));
        }

        if(attributes.containsKey(columns[5])) {
            frequencyMultiplier= attributes.getAsInteger(columns[5]);
        }

        frequency = new Frequency(frequencyUnit, frequencyMultiplier);

        if(attributes.containsKey(columns[6])) {
            surveysCount = attributes.getAsInteger(columns[6]);
        }

        if(attributes.containsKey(columns[7])) {
            period = Period.getPeriod(attributes.getAsString(columns[7]));
        }

        if(attributes.containsKey(columns[8])) {
            dailySurveysCount = attributes.getAsInteger(columns[8]);
        }

        if(attributes.containsKey(columns[9])) {
            maxCompletionTime = attributes.getAsInteger(columns[9]);
        }

        if(attributes.containsKey(columns[10])) {
            minTimeBetweenSurveys = attributes.getAsInteger(columns[10]);
        }

        if(attributes.containsKey(columns[11])) {
            notificationsCount = attributes.getAsInteger(columns[11]);
        }

        if(attributes.containsKey(columns[12])) {
            issuedSurveyCount = attributes.getAsInteger(columns[12]);
        }

        dailyIntervals = getDailyIntervals();
    }

    private Pair<String, String>[] getDailyIntervals() {
        Cursor c = localController.rawQuery("SELECT * FROM " + DailyIntervalsTable.TABLE_DAILY_INTERVALS_TABLE + " WHERE " + DailyIntervalsTable.KEY_DAILY_INTERVALS_CONFIG_ID + " = " + id, null);

        Pair<String, String>[] intervals = new Pair[c.getCount()];

        if(intervals.length == 0) {
            return intervals;
        }

        int i = 0;
        while(c.moveToNext()) {
            intervals[i] = new Pair<>(c.getString(2), c.getString(3));
            i++;
        }

        return intervals;
    }

    @Override
    public ContentValues getAttributes() {
        ContentValues attributes = new ContentValues();
        if(id >= 0) {
            attributes.put(columns[0], id);
            attributes.put(columns[1], surveyType.getSurveyName());
            attributes.put(columns[2], grouped);
            attributes.put(columns[3], immediate);
            attributes.put(columns[4], frequencyUnit.getFrequencyName());
            attributes.put(columns[5], frequencyMultiplier);
            attributes.put(columns[6], surveysCount);

            if(period != null) {
                attributes.put(columns[7], period.getPeriodName());
            }

            attributes.put(columns[8], dailySurveysCount);
            attributes.put(columns[9], maxCompletionTime);
            attributes.put(columns[10], minTimeBetweenSurveys);
            attributes.put(columns[11], notificationsCount);
            attributes.put(columns[12], issuedSurveyCount);
        }

        return attributes;
    }

    @Override
    public ContentValues getAttributesFromCursor(Cursor cursor) {
        ContentValues attributes = new ContentValues();
        attributes.put(columns[0], cursor.getLong(0));
        attributes.put(columns[1], cursor.getString(1));
        attributes.put(columns[2], cursor.getInt(2));
        attributes.put(columns[3], cursor.getInt(3));
        attributes.put(columns[4], cursor.getString(4));
        attributes.put(columns[5], cursor.getInt(5));
        attributes.put(columns[6], cursor.getInt(6));
        attributes.put(columns[7], cursor.getString(7));
        attributes.put(columns[8], cursor.getInt(8));
        attributes.put(columns[9], cursor.getInt(9));
        attributes.put(columns[10], cursor.getInt(10));
        attributes.put(columns[11], cursor.getInt(11));
        attributes.put(columns[12], cursor.getInt(12));

        return attributes;
    }

    @Override
    public void save() {
        String tableName = LocalDbUtility.getTableName(table);

        if(isNewRecord) {
            localController.insertRecord(tableName, getAttributes());
        } else {
            String columnId = columns[0];
            localController.update(tableName, getAttributes(), columnId + " = " + id);
        }
    }

    @Override
    public String[] getColumns() {
        return columns;
    }

    @Override
    public String getTableName() {
        return table.getTableName();
    }

    @Override
    public void delete() {
        localController.delete(table.getTableName(), columns[0] + " = " + id);
    }

    public static SurveyConfig getConfig(SurveyType surveyType) {
        return (SurveyConfig) find("*", SurveyConfigTable.KEY_SURVEY_CONFIG_SURVEY_TYPE + " = \"" + surveyType.getSurveyName() + "\"");
    }

    public static TableHandler find(String select, String clause) {
        Cursor surveyRecord = localController.rawQuery("SELECT " + select + " FROM " + table.getTableName() + " WHERE " + clause + " LIMIT " + 1, null);

        if(surveyRecord.getCount() == 0) {
            return null;
        }

        SurveyConfig config = new SurveyConfig(false);
        surveyRecord.moveToFirst();

        config.setAttributes(config.getAttributesFromCursor(surveyRecord));
        surveyRecord.close();
        return config;
    }
}
