package usi.justmove.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;

import usi.justmove.local.database.LocalDbUtility;
import usi.justmove.local.database.LocalTables;
import usi.justmove.local.database.tables.PAMTable;

/**
 * Created by usi on 10/03/17.
 */

public class PAMSurvey extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_PAM;
    public long parentId;
    public boolean completed;
    public String period;
    public int imageId;
    public int stress;
    public String sleep;
    public String location;
    public String transportation;
    public String activities;
    public String workload;
    public int social;

    public PAMSurvey(boolean isNewRecord) {
        super(isNewRecord);
        columns = LocalDbUtility.getTableColumns(table);
    }

    public static TableHandler findByPk(long pk) {
        String idColumn = LocalDbUtility.getTableColumns(table)[0];
        Cursor surveyRecord = localController.rawQuery("SELECT * FROM " + LocalDbUtility.getTableName(table) + " WHERE " + idColumn + " = " + pk + " LIMIT " + 1, null);

        if(surveyRecord.getCount() == 0) {
            return null;
        }

        PAMSurvey survey = new PAMSurvey(false);
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
            period = attributes.getAsString(columns[3]);
        }

        if(attributes.containsKey(columns[4])) {
            imageId = attributes.getAsInteger(columns[4]);
        }

        if(attributes.containsKey(columns[5])) {
            stress = attributes.getAsInteger(columns[5]);
        }

        if(attributes.containsKey(columns[6])) {
            sleep = attributes.getAsString(columns[6]);
        }


        if(attributes.containsKey(columns[7])) {
            location = attributes.getAsString(columns[7]);
        }

        if(attributes.containsKey(columns[8])) {
            transportation = attributes.getAsString(columns[8]);
        }

        if(attributes.containsKey(columns[9])) {
            activities = attributes.getAsString(columns[9]);
        }

        if(attributes.containsKey(columns[10])) {
            workload = attributes.getAsString(columns[10]);
        }

        if(attributes.containsKey(columns[11])) {
            social = attributes.getAsInteger(columns[11]);
        }
    }

    @Override
    public ContentValues getAttributes() {
        ContentValues attributes = new ContentValues();
        attributes.put(columns[0], id);
        attributes.put(columns[1], parentId);
        attributes.put(columns[2], completed);
        attributes.put(columns[3], period);
        attributes.put(columns[4], imageId);
        attributes.put(columns[5], stress);
        attributes.put(columns[6], sleep);
        attributes.put(columns[7], location);
        attributes.put(columns[8], transportation);
        attributes.put(columns[9], activities);
        attributes.put(columns[10], workload);
        attributes.put(columns[11], social);

        return attributes;
    }

    public static TableHandler[] findAll(String select, String clause) {
        Cursor surveyRecords = localController.rawQuery("SELECT " + select + " FROM " + LocalDbUtility.getTableName(table) + " WHERE " + clause, null);
        TableHandler[] surveys =  new TableHandler[surveyRecords.getCount()];

        if(surveyRecords.getCount() == 0) {
            return surveys;
        }

        int i = 0;
        PAMSurvey survey;
        while(surveyRecords.moveToNext()) {
            survey = new PAMSurvey(false);

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

        PAMSurvey survey = new PAMSurvey(false);
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
        attributes.put(columns[3], cursor.getString(3));
        attributes.put(columns[4], cursor.getInt(4));
        attributes.put(columns[5], cursor.getInt(5));
        attributes.put(columns[6], cursor.getFloat(6));
        attributes.put(columns[7], cursor.getString(7));
        attributes.put(columns[8], cursor.getString(8));
        attributes.put(columns[9], cursor.getFloat(9));
        attributes.put(columns[10], cursor.getFloat(10));
        attributes.put(columns[11], cursor.getInt(11));

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
    public void setAttribute(String attributeName, ContentValues attribute) {
        super.setAttribute(attributeName, attribute);

        switch(attributeName) {
            case PAMTable.KEY_PAM_ID:
                id = attribute.getAsLong(PAMTable.KEY_PAM_ID);
                break;
            case PAMTable.KEY_PAM_PARENT_SURVEY_ID:
                parentId = attribute.getAsLong(PAMTable.KEY_PAM_PARENT_SURVEY_ID);
                break;
            case PAMTable.KEY_PAM_COMPLETED:
                completed = attribute.getAsBoolean(PAMTable.KEY_PAM_COMPLETED);
                break;
            case PAMTable.KEY_PAM_PERIOD:
                period = attribute.getAsString(PAMTable.KEY_PAM_PERIOD);
                break;
            case PAMTable.KEY_PAM_IMAGE_ID:
                imageId = attribute.getAsInteger(PAMTable.KEY_PAM_IMAGE_ID);
                break;
            case PAMTable.KEY_PAM_STRESS:
                stress = attribute.getAsInteger(PAMTable.KEY_PAM_STRESS);
                break;
            case PAMTable.KEY_PAM_SLEEP:
                sleep = attribute.getAsString(PAMTable.KEY_PAM_SLEEP);
                break;
            case PAMTable.KEY_PAM_LOCATION:
                location = attribute.getAsString(PAMTable.KEY_PAM_LOCATION);
                break;
            case PAMTable.KEY_PAM_TRANSPORTATION:
                transportation = attribute.getAsString(PAMTable.KEY_PAM_TRANSPORTATION);
                break;
            case PAMTable.KEY_PAM_ACTIVITIES:
                transportation = attribute.getAsString(PAMTable.KEY_PAM_ACTIVITIES);
                break;
            case PAMTable.KEY_PAM_WORKLOAD:
                workload = attribute.getAsString(PAMTable.KEY_PAM_WORKLOAD);
                break;
            case PAMTable.KEY_PAM_SOCIAL:
                social = attribute.getAsInteger(PAMTable.KEY_PAM_SOCIAL);
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
