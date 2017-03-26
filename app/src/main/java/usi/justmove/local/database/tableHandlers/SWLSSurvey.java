package usi.justmove.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;

import usi.justmove.local.database.LocalDbUtility;
import usi.justmove.local.database.LocalTables;
import usi.justmove.local.database.tables.SWLSTable;

/**
 * Created by usi on 10/03/17.
 */

public class SWLSSurvey extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_SWLS;
    public long parentId;
    public boolean completed;
    public int q1;
    public int q2;
    public int q3;
    public int q4;
    public int q5;

    public SWLSSurvey(boolean isNewRecord) {
        super(isNewRecord);
        columns = LocalDbUtility.getTableColumns(table);
    }

    public static TableHandler findByPk(long pk) {
        String idColumn = LocalDbUtility.getTableColumns(table)[0];
        Cursor surveyRecord = localController.rawQuery("SELECT * FROM " + LocalDbUtility.getTableName(table) + " WHERE " + idColumn + " = " + pk + " LIMIT " + 1, null);

        if(surveyRecord.getCount() == 0) {
            return null;
        }

        Survey survey = new Survey(false);
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

        if(attributes.containsKey(columns[7])) {
            q5 = attributes.getAsInteger(columns[7]);
        }

    }

    @Override
    public ContentValues getAttributes() {
        ContentValues attributes = new ContentValues();
        attributes.put(columns[0], id);
        attributes.put(columns[1], parentId);
        attributes.put(columns[2], completed);
        attributes.put(columns[3], q1);
        attributes.put(columns[4], q2);
        attributes.put(columns[5], q3);
        attributes.put(columns[6], q4);
        attributes.put(columns[7], q5);

        return attributes;
    }

    public static TableHandler[] findAll(String select, String clause) {
        Cursor surveyRecords = localController.rawQuery("SELECT " + select + " FROM " + LocalDbUtility.getTableName(table) + " WHERE " + clause, null);
        TableHandler[] surveys =  new TableHandler[surveyRecords.getCount()];

        if(surveyRecords.getCount() == 0) {
            return surveys;
        }

        int i = 0;
        Survey survey;
        while(surveyRecords.moveToNext()) {
            survey = new Survey(false);

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

        SWLSSurvey survey = new SWLSSurvey(false);
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
        attributes.put(columns[7], cursor.getInt(7));

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
            case SWLSTable.KEY_SWLS_ID:
                id = attribute.getAsLong(SWLSTable.KEY_SWLS_ID);
                break;
            case SWLSTable.KEY_SWLS_PARENT_SURVEY_ID:
                parentId = attribute.getAsLong(SWLSTable.KEY_SWLS_PARENT_SURVEY_ID);
                break;
            case SWLSTable.KEY_SWLS_COMPLETED:
                completed = attribute.getAsBoolean(SWLSTable.KEY_SWLS_COMPLETED);
                break;
            case SWLSTable.KEY_SWLS_Q1:
                q1 = attribute.getAsInteger(SWLSTable.KEY_SWLS_Q1);
                break;
            case SWLSTable.KEY_SWLS_Q2:
                q2 = attribute.getAsInteger(SWLSTable.KEY_SWLS_Q2);
                break;
            case SWLSTable.KEY_SWLS_Q3:
                q3 = attribute.getAsInteger(SWLSTable.KEY_SWLS_Q3);
                break;
            case SWLSTable.KEY_SWLS_Q4:
                q4 = attribute.getAsInteger(SWLSTable.KEY_SWLS_Q4);
                break;
            case SWLSTable.KEY_SWLS_Q5:
                q5 = attribute.getAsInteger(SWLSTable.KEY_SWLS_Q5);
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
