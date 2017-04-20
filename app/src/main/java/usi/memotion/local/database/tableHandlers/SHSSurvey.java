package usi.memotion.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;

import usi.memotion.local.database.db.LocalDbUtility;
import usi.memotion.local.database.db.LocalTables;
import usi.memotion.local.database.tables.SHSTable;

/**
 * Created by usi on 10/03/17.
 */

public class SHSSurvey extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_SHS;
    public long parentId;
    public boolean completed;
    public int q1;
    public int q2;
    public int q3;
    public int q4;

    public SHSSurvey(boolean isNewRecord) {
        super(isNewRecord);
        id = -1;
        columns = LocalDbUtility.getTableColumns(table);
    }

    public static TableHandler findByPk(long pk) {
        String idColumn = LocalDbUtility.getTableColumns(table)[0];
        Cursor surveyRecord = localController.rawQuery("SELECT * FROM " + LocalDbUtility.getTableName(table) + " WHERE " + idColumn + " = " + pk + " LIMIT " + 1, null);

        if(surveyRecord.getCount() == 0) {
            return null;
        }

        SHSSurvey survey = new SHSSurvey(false);
        surveyRecord.moveToFirst();

        survey.setAttributes(survey.getAttributesFromCursor(surveyRecord));

        surveyRecord.close();
        return survey;
    }

    @Override
    public void setAttributes(ContentValues attributes) {
        if(attributes.containsKey(columns[0])) {
            id = attributes.getAsLong(columns[0]);
        }

        if(attributes.containsKey(columns[1])) {
            parentId = attributes.getAsLong(columns[1]);
        }

        if(attributes.containsKey(columns[2])) {
            completed = attributes.getAsBoolean(columns[2]);
        }

        if(attributes.containsKey(columns[3])) {
            q1 = attributes.getAsInteger(columns[3]);
        }

        if(attributes.containsKey(columns[4])) {
            q2 = attributes.getAsInteger(columns[4]);
        }

        if(attributes.containsKey(columns[5])) {
            q3 = attributes.getAsInteger(columns[5]);
        }

        if(attributes.containsKey(columns[6])) {
            q4 = attributes.getAsInteger(columns[6]);
        }

    }

    @Override
    public ContentValues getAttributes() {
        ContentValues attributes = new ContentValues();
        if(id >= 0) {
            attributes.put(columns[0], id);
        }
        attributes.put(columns[1], parentId);
        attributes.put(columns[2], completed);
        attributes.put(columns[3], q1);
        attributes.put(columns[4], q2);
        attributes.put(columns[5], q3);
        attributes.put(columns[6], q4);

        return attributes;
    }

    public static TableHandler[] findAll(String select, String clause) {
        Cursor surveyRecords = localController.rawQuery("SELECT " + select + " FROM " + LocalDbUtility.getTableName(table) + " WHERE " + clause, null);
        TableHandler[] surveys =  new TableHandler[surveyRecords.getCount()];

        if(surveyRecords.getCount() == 0) {
            return surveys;
        }

        int i = 0;
        SHSSurvey survey;
        while(surveyRecords.moveToNext()) {
            survey = new SHSSurvey(false);

            survey.setAttributes(survey.getAttributesFromCursor(surveyRecords));
            surveys[i] = survey;
            i++;
        }
        surveyRecords.close();
        return surveys;
    }

    public static TableHandler find(String select, String clause) {
        Cursor surveyRecord = localController.rawQuery("SELECT " + select + " FROM " + LocalDbUtility.getTableName(table) + " WHERE " + clause + " LIMIT " + 1, null);

        if(surveyRecord.getCount() == 0) {
            return null;
        }

        SHSSurvey survey = new SHSSurvey(false);
        surveyRecord.moveToFirst();

        survey.setAttributes(survey.getAttributesFromCursor(surveyRecord));
        surveyRecord.close();
        return survey;
    }

    @Override
    public ContentValues getAttributesFromCursor(Cursor cursor) {
        ContentValues attributes = new ContentValues();
        attributes.put(columns[0], cursor.getLong(0));
        attributes.put(columns[1], cursor.getLong(1));
        attributes.put(columns[2], cursor.getInt(2));
        attributes.put(columns[3], cursor.getInt(3));
        attributes.put(columns[4], cursor.getInt(4));
        attributes.put(columns[5], cursor.getInt(5));
        attributes.put(columns[6], cursor.getInt(6));

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
    public void setAttribute(String attributeName, ContentValues attribute) {
        super.setAttribute(attributeName, attribute);

        switch(attributeName) {
            case SHSTable.KEY_SHS_ID:
                id = attribute.getAsLong(SHSTable.KEY_SHS_ID);
                break;
            case SHSTable.KEY_SHS_PARENT_SURVEY_ID:
                parentId = attribute.getAsLong(SHSTable.KEY_SHS_PARENT_SURVEY_ID);
                break;
            case SHSTable.KEY_SHS_COMPLETED:
                completed = attribute.getAsBoolean(SHSTable.KEY_SHS_COMPLETED);
                break;
            case SHSTable.KEY_SHS_Q1:
                q1 = attribute.getAsInteger(SHSTable.KEY_SHS_Q1);
                break;
            case SHSTable.KEY_SHS_Q2:
                q2 = attribute.getAsInteger(SHSTable.KEY_SHS_Q2);
                break;
            case SHSTable.KEY_SHS_Q3:
                q3 = attribute.getAsInteger(SHSTable.KEY_SHS_Q3);
                break;
            case SHSTable.KEY_SHS_Q4:
                q4 = attribute.getAsInteger(SHSTable.KEY_SHS_Q4);
                break;
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
}
