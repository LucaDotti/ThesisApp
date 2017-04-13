package usi.justmove.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;

import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.local.database.LocalDbUtility;
import usi.justmove.local.database.LocalTables;

import static com.sun.mail.imap.SortTerm.FROM;
import static usi.justmove.R.array.surveys;

/**
 * Created by usi on 30/03/17.
 */

public class SurveyAlarms extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_SURVEY_ALARMS;

    public boolean current;
    public long ts;
    public SurveyType type;

    public SurveyAlarms(boolean isNewRecord) {
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

        SurveyAlarms alarm = new SurveyAlarms(false);
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
            current = attributes.getAsBoolean(columns[1]);
        }

        if(attributes.containsKey(columns[2])) {
            ts = attributes.getAsLong(columns[2]);
        }

        if(attributes.containsKey(columns[3])) {
            type = SurveyType.getSurvey(attributes.getAsString(columns[3]));
        }
    }

    @Override
    public ContentValues getAttributes() {
        ContentValues attributes = new ContentValues();
        if(id >= 0) {
            attributes.put(columns[0], id);
        }

        attributes.put(columns[1], current);
        attributes.put(columns[2], ts);
        attributes.put(columns[3], type.getSurveyName());

        return attributes;
    }

    @Override
    public ContentValues getAttributesFromCursor(Cursor cursor) {
        ContentValues attributes = new ContentValues();
        attributes.put(columns[0], cursor.getLong(0));
        attributes.put(columns[1], cursor.getInt(1));
        attributes.put(columns[2], cursor.getLong(2));
        attributes.put(columns[3], cursor.getString(3));

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

    public static void dropTable() {
        localController.truncate(table.getTableName());
    }

    public static TableHandler[] findAll(String select, String clause) {
        String query = "SELECT " + select + "FROM " + table.getTableName();

        if(!clause.equals("") && clause != null) {
            query += " WHERE " + clause;
        }

        Cursor records = localController.rawQuery(query, null);

        TableHandler[] alarms =  new TableHandler[records.getCount()];

        if(records.getCount() == 0) {
            return alarms;
        }



        int i = 0;
        SurveyAlarms alarm;
        while(records.moveToNext()) {
            alarm = new SurveyAlarms(false);

            alarm.setAttributes(alarm.getAttributesFromCursor(records));
            alarms[i] = alarm;
            i++;
        }
        records.close();
        return alarms;
    }

    public static SurveyAlarms getCurrentAlarm(SurveyType survey) {
        String columnCurrent = LocalDbUtility.getTableColumns(table)[1];
        String columnType = LocalDbUtility.getTableColumns(table)[3];
        String columnId = LocalDbUtility.getTableColumns(table)[0];



        Cursor record = localController.rawQuery("SELECT * FROM " + table.getTableName() + " WHERE " + columnType + " = \"" + survey.getSurveyName() + "\" ORDER BY " + columnId + " ASC", null);

        if(record.getCount() == 0) {
            return null;
        }

        SurveyAlarms alarm = new SurveyAlarms(false);
        record.moveToFirst();


        alarm.setAttributes(alarm.getAttributesFromCursor(record));

        record.close();

        return alarm;
    }

    public static TableHandler find(String select, String clause) {

        String query = "SELECT " + select + " FROM " + table.getTableName();

        if(!clause.equals("") && clause != null) {
            query += " WHERE " + clause;
        }

        query += " LIMIT " + 1;

        Cursor surveyRecord = localController.rawQuery(query, null);

        if(surveyRecord.getCount() == 0) {
            return null;
        }

        SurveyAlarms survey = new SurveyAlarms(false);

        surveyRecord.moveToFirst();

        survey.setAttributes(survey.getAttributesFromCursor(surveyRecord));


        surveyRecord.close();
        return survey;
    }

    public static int getCount(SurveyType survey) {
        String columnType = LocalDbUtility.getTableColumns(table)[3];

        Cursor c = localController.rawQuery("SELECT COUNT(*) FROM " + table.getTableName() + " WHERE " + columnType + " = \"" + survey.getSurveyName() + "\"", null);
        c.moveToFirst();
        return c.getInt(0);
    }

    @Override
    public String toString() {
        return "SurveyAlarms(id: " + id + ", current: " + current + ", ts: " + ts + ", type: " + type;
    }
}
