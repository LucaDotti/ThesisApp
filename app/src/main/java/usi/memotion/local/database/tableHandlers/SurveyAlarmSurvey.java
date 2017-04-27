package usi.memotion.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;

import usi.memotion.local.database.db.LocalDbUtility;
import usi.memotion.local.database.db.LocalTables;
import usi.memotion.local.database.tables.SurveyAlarmSurveyTable;

/**
 * Created by usi on 10/04/17.
 */

public class SurveyAlarmSurvey extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_SURVEY_ALARMS_SURVEY;

    public long surveyId;
    public long surveyAlarmId;

    public SurveyAlarmSurvey(boolean isNewRecord) {
        super(isNewRecord);
        id = -1;
        columns = LocalDbUtility.getTableColumns(table);
    }

    public static TableHandler findByPk(long pk) {
        String idColumn = LocalDbUtility.getTableColumns(table)[0];
        Cursor record = localController.rawQuery("SELECT * FROM " + LocalDbUtility.getTableName(table) + " WHERE " + idColumn + " = " + pk + " LIMIT " + 1, null);

        if(record.getCount() == 0) {
            return null;
        }

        SurveyAlarmSurvey alarm = new SurveyAlarmSurvey(false);
        record.moveToFirst();


        alarm.setAttributes(alarm.getAttributesFromCursor(record));

        record.close();
        return alarm;
    }

    @Override
    public void setAttributes(ContentValues attributes) {
        if(attributes.containsKey(columns[0])) {
            id = attributes.getAsLong(columns[0]);
        }

        if(attributes.containsKey(columns[1])) {
            surveyId = attributes.getAsLong(columns[1]);
        }

        if(attributes.containsKey(columns[2])) {
            surveyAlarmId = attributes.getAsLong(columns[2]);
        }

    }

    @Override
    public ContentValues getAttributes() {
        ContentValues attributes = new ContentValues();
        if(id >= 0) {
            attributes.put(columns[0], id);
        }

        attributes.put(columns[1], surveyId);
        attributes.put(columns[2], surveyAlarmId);

        return attributes;
    }

    @Override
    public ContentValues getAttributesFromCursor(Cursor cursor) {
        ContentValues attributes = new ContentValues();
        attributes.put(columns[0], cursor.getLong(0));
        attributes.put(columns[1], cursor.getLong(1));
        attributes.put(columns[2], cursor.getLong(2));

        return attributes;
    }

    @Override
    public void save() {
        String tableName = LocalDbUtility.getTableName(table);

        if(isNewRecord) {
            id = localController.insertRecord(tableName, getAttributes());
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

    public static Survey[] getSurveys(long alarmId) {
        Cursor c = localController.rawQuery("SELECT * FROM " + table.getTableName()
                + " WHERE " + SurveyAlarmSurveyTable.KEY_SURVEY_ALARMS_SURVEY_SURVEY_ALARMS_ID + " = " + alarmId, null);

        Survey[] surveys = new Survey[c.getCount()];

        int i = 0;
        while(c.moveToNext()) {
            surveys[i] = (Survey) Survey.findByPk(c.getInt(0));
            i++;
        }

        c.close();
        return surveys;
    }

    public static SurveyAlarms getAlarm(long surveyId) {
        Cursor c = localController.rawQuery("SELECT * FROM " + table.getTableName()
                + " WHERE " + SurveyAlarmSurveyTable.KEY_SURVEY_ALARMS_SURVEY_SURVEY_ID + " = " + surveyId, null);

        if(c.getCount() > 0) {
            c.moveToFirst();

            SurveyAlarms a = (SurveyAlarms) SurveyAlarms.findByPk(c.getInt(2));
            c.close();
            return a;
        } else {
            c.close();
            return null;
        }
    }

    public static Survey[] getAllSurveys() {
        Cursor c =localController.rawQuery("SELECT * FROM  " + table.getTableName(), null);

        Survey[] surveys = new Survey[c.getCount()];
        if(c.getCount() > 0) {
            int i = 0;
            while(c.moveToNext()) {
                surveys[i] = (Survey) Survey.findByPk(c.getInt(1));
            }
        }

        c.close();
        return surveys;
    }

    public static SurveyAlarmSurvey[] findAllByAttribute(String attribute, String value) {
        Cursor c = localController.rawQuery("SELECT * FROM " + table.getTableName() + " WHERE " + attribute + " = " + value, null);

        SurveyAlarmSurvey[] records = new SurveyAlarmSurvey[c.getCount()];

        int i = 0;

        SurveyAlarmSurvey current;
        while(c.moveToNext()) {
            current = new SurveyAlarmSurvey(false);
            current.setAttributes(current.getAttributesFromCursor(c));
            records[i] = current;
            i++;
        }

        c.close();
        return records;
    }
}
