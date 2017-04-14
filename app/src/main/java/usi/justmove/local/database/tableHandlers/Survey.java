package usi.justmove.local.database.tableHandlers;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.HashMap;
import java.util.Map;

import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.local.database.LocalDbUtility;
import usi.justmove.local.database.LocalTables;
import usi.justmove.local.database.tables.SurveyTable;

import static usi.justmove.gathering.surveys.config.SurveyType.GROUPED_SSPP;
import static usi.justmove.gathering.surveys.config.SurveyType.PAM;
import static usi.justmove.gathering.surveys.config.SurveyType.PHQ8;
import static usi.justmove.gathering.surveys.config.SurveyType.PSS;
import static usi.justmove.gathering.surveys.config.SurveyType.PWB;
import static usi.justmove.gathering.surveys.config.SurveyType.SHS;
import static usi.justmove.gathering.surveys.config.SurveyType.SWLS;

/**
 * Created by usi on 10/03/17.
 */

public class Survey extends TableHandler {
    private static LocalTables table = LocalTables.TABLE_SURVEY;

    public long ts;
    public long scheduledAt;
    public boolean completed;
    public int notified;
    public boolean expired;
    public boolean grouped;
    public SurveyType surveyType;

    private Map<SurveyType, TableHandler> surveys;

    public Survey(boolean isNewRecord) {
        super(isNewRecord);
        columns = LocalDbUtility.getTableColumns(table);
        surveys = new HashMap<>();
        id = -1;
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


        survey.surveys = survey.getChildSurveys(true);

        surveyRecord.close();
        return survey;
    }

    public Map<SurveyType, TableHandler> getChildSurveys(boolean all) {
        Map<SurveyType, TableHandler> surveyList = new HashMap<>();

        String childClause = "";


        LocalTables[] surveyTables;
        TableHandler s = null;
        String clause = "";
        s = null;
        surveyTables = SurveyType.getSurveyTables(surveyType);
        SurveyType childType = null;
        for (int i = 0; i < surveyTables.length; i++) {
            clause = "";
            childClause = "";
            s = null;
            childType = null;
            switch(surveyTables[i]) {
                case TABLE_PAM:
                    childType = PAM;
                    clause += LocalDbUtility.getTableColumns(surveyTables[i])[1] + " = " + id;

                    if(!all) {
                        childClause += " AND " + LocalDbUtility.getTableColumns(surveyTables[i])[2] + " = " + 0;
                        clause += childClause;
                    }

                    s = PAMSurvey.find("*", clause);

                    if(s == null) {
                        PAMSurvey ps = new PAMSurvey(true);
                        ps.parentId = id;
                        s = ps;
                    }
                    break;
                case TABLE_PWB:
                    childType = PWB;
                    clause += LocalDbUtility.getTableColumns(surveyTables[i])[1] + " = " + id;

                    if(!all) {
                        childClause += " AND " + LocalDbUtility.getTableColumns(surveyTables[i])[2] + " = " + 0;
                        clause += childClause;
                    }

                    s = PWBSurvey.find("*", clause);
                    if(s == null) {
                        PWBSurvey ps = new PWBSurvey(true);
                        ps.parentId = id;
                        s = ps;
                    }
                    break;
                case TABLE_PSS:
                    childType = PSS;
                    clause += LocalDbUtility.getTableColumns(surveyTables[i])[1] + " = " + id;

                    if(!all) {
                        childClause += " AND " + LocalDbUtility.getTableColumns(surveyTables[i])[2] + " = " + 0;
                        clause += childClause;
                    }

                    s = PSSSurvey.find("*", clause);
                    if(s == null) {
                        PSSSurvey ps = new PSSSurvey(true);
                        ps.parentId = id;
                        s = ps;
                    }
                    break;
                case TABLE_SHS:
                    childType = SHS;
                    clause += LocalDbUtility.getTableColumns(surveyTables[i])[1] + " = " + id;

                    if(!all) {
                        childClause += " AND " + LocalDbUtility.getTableColumns(surveyTables[i])[2] + " = " + 0;
                        clause += childClause;
                    }

                    s = SHSSurvey.find("*", clause);
                    if(s == null) {
                        SHSSurvey ps = new SHSSurvey(true);
                        ps.parentId = id;
                        s = ps;
                    }
                    break;
                case TABLE_PHQ8:
                    childType = PHQ8;
                    clause += LocalDbUtility.getTableColumns(surveyTables[i])[1] + " = " + id;

                    if(!all) {
                        childClause += " AND " + LocalDbUtility.getTableColumns(surveyTables[i])[2] + " = " + 0;
                        clause += childClause;
                    }

                    s = PHQ8Survey.find("*", clause);
                    if(s == null) {
                        PHQ8Survey ps = new PHQ8Survey(true);
                        ps.parentId = id;
                        s = ps;
                    }
                    break;
                case TABLE_SWLS:
                    childType = SWLS;
                    clause += LocalDbUtility.getTableColumns(surveyTables[i])[1] + " = " + id;

                    if(!all) {
                        childClause += " AND " + LocalDbUtility.getTableColumns(surveyTables[i])[2] + " = " + 0;
                        clause += childClause;
                    }

                    s = SWLSSurvey.find("*", clause);
                    if(s == null) {
                        SWLSSurvey ps = new SWLSSurvey(true);
                        ps.parentId = id;
                        s = ps;
                    }

                    break;
            }

            if(s != null) {
                surveyList.put(childType, s);
            }

        }

        return surveyList;
    }

    public static TableHandler[] findAll(String select, String clause) {
        String query = "SELECT " + select + " FROM " + LocalDbUtility.getTableName(table);

        if(clause != null && !clause.equals("")) {
            query += " WHERE " + clause;
        }

        Cursor surveyRecords = localController.rawQuery(query, null);
        TableHandler[] surveys =  new TableHandler[surveyRecords.getCount()];

        if(surveyRecords.getCount() == 0) {
            return null;
        }

        int i = 0;
        Survey survey;
        while(surveyRecords.moveToNext()) {
            survey = new Survey(false);


            survey.setAttributes(survey.getAttributesFromCursor(surveyRecords));
            survey.surveys = survey.getChildSurveys(true);
            surveys[i] = survey;
            i++;
        }
        surveyRecords.close();
        return surveys;
    }

    @Override
    public void save() {
        String tableName = LocalDbUtility.getTableName(table);
        long sId = -1;
        if(isNewRecord) {
            sId = localController.insertRecord(tableName, getAttributes());
            id = sId;
        } else {
            String columnId = columns[0];
            localController.update(tableName, getAttributes(), columnId + " = " + id);
        }

//        ContentValues attrs = new ContentValues();
        for(Map.Entry<SurveyType, TableHandler> surveyRecord: surveys.entrySet()) {
            surveyRecord.getValue().save();
        }
    }

    @Override
    public void setAttribute(String attributeName, ContentValues attribute) {
        super.setAttribute(attributeName, attribute);

        switch(attributeName) {
            case SurveyTable.KEY_SURVEY_ID:
                id = attribute.getAsLong(SurveyTable.KEY_SURVEY_ID);
                break;
            case SurveyTable.KEY_SURVEY_COMPLETED:
                completed = attribute.getAsBoolean(SurveyTable.KEY_SURVEY_COMPLETED);
                break;
            case SurveyTable.KEY_SURVEY_EXPIRED:
                expired = attribute.getAsBoolean(SurveyTable.KEY_SURVEY_EXPIRED);
                break;
            case SurveyTable.KEY_SURVEY_GROUPED:
                grouped = attribute.getAsBoolean(SurveyTable.KEY_SURVEY_GROUPED);
                break;
            case SurveyTable.KEY_SURVEY_NOTIFIED:
                notified = attribute.getAsInteger(SurveyTable.KEY_SURVEY_NOTIFIED);
                break;
            case SurveyTable.KEY_SURVEY_SCHEDULED_AT:
                scheduledAt = attribute.getAsLong(SurveyTable.KEY_SURVEY_SCHEDULED_AT);
                break;
            case SurveyTable.KEY_SURVEY_TS:
                ts = attribute.getAsLong(SurveyTable.KEY_SURVEY_TS);
                break;
            case SurveyTable.KEY_SURVEY_TYPE:
                surveyType = SurveyType.getSurvey(attribute.getAsString(SurveyTable.KEY_SURVEY_TYPE));
                break;
        }
    }

    @Override
    public String[] getColumns() {
        return columns;
    }

    @Override
    public String getTableName() {
        return surveyType.getSurveyName();
    }


    public static TableHandler find(String select, String clause, String order) {
        String query = "SELECT " + select + " FROM " + LocalDbUtility.getTableName(table);

        if(clause != null && !clause.equals("")) {
            query += " WHERE " + clause;
        }

        query += " " + order;
        query += " LIMIT " + 1;
        Cursor surveyRecord = localController.rawQuery(query, null);

        if(surveyRecord.getCount() == 0) {
            return null;
        }

        Survey survey = new Survey(false);

        surveyRecord.moveToFirst();

        survey.setAttributes(survey.getAttributesFromCursor(surveyRecord));

        survey.surveys = survey.getChildSurveys(true);

        surveyRecord.close();
        return survey;
    }

    public Map<SurveyType, TableHandler> getSurveys() {
        return surveys;
    }

    public void setSurveys(Map<SurveyType, TableHandler> surveys) {
        this.surveys = surveys;
    }

    @Override
    public ContentValues getAttributesFromCursor(Cursor cursor) {
        ContentValues attributes = new ContentValues();
        attributes.put(columns[0], cursor.getLong(0));
        attributes.put(columns[1], cursor.getLong(1));
        attributes.put(columns[2], cursor.getLong(2));
        attributes.put(columns[3], cursor.getInt(3));
        attributes.put(columns[4], cursor.getInt(4));
        attributes.put(columns[5], cursor.getInt(5));
        attributes.put(columns[6], cursor.getInt(6));
        attributes.put(columns[7], cursor.getString(7));

        return attributes;
    }

    @Override
    public void setAttributes(ContentValues attributes) {
//        id = attributes.getAsLong(columns[0]);
        if(attributes.containsKey(columns[0])) {
            id = attributes.getAsLong(columns[0]);
        }

        if(attributes.containsKey(columns[1])) {
            ts = attributes.getAsLong(columns[1]);
        }

        scheduledAt = attributes.getAsLong(columns[2]);
        completed = attributes.getAsBoolean(columns[3]);
        notified = attributes.getAsInteger(columns[4]);
        expired = attributes.getAsBoolean(columns[5]);
        grouped = attributes.getAsBoolean(columns[6]);
        surveyType = SurveyType.getSurvey(attributes.getAsString(columns[7]));
    }

    @Override
    public ContentValues getAttributes() {
        ContentValues attributes = new ContentValues();
        if(id >= 0) {
            attributes.put(columns[0], id);
        }

        attributes.put(columns[1], ts);
        attributes.put(columns[2], scheduledAt);
        attributes.put(columns[3], completed);
        attributes.put(columns[4], notified);
        attributes.put(columns[5], expired);
        attributes.put(columns[6], grouped);
        attributes.put(columns[7], surveyType.getSurveyName());

        return attributes;
    }

    public static int getAllAvailableSurveysCount() {
        String tableName = LocalDbUtility.getTableName(table);
        String columnSchedule = LocalDbUtility.getTableColumns(table)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(table)[3];
        String columnNotified = LocalDbUtility.getTableColumns(table)[4];
        String columnExpired = LocalDbUtility.getTableColumns(table)[5];

//        Calendar startDateTime = Calendar.getInstance();
//        startDateTime.set(Calendar.HOUR_OF_DAY, 0);
//        startDateTime.set(Calendar.MINUTE, 0);
//        startDateTime.set(Calendar.SECOND, 1);
//
//        Calendar endDateTime = Calendar.getInstance();
//        endDateTime.set(Calendar.HOUR_OF_DAY, 23);
//        endDateTime.set(Calendar.MINUTE, 59);
//        endDateTime.set(Calendar.SECOND, 59);
//        long startMillis = startDateTime.getTimeInMillis();
//        long endMillis = endDateTime.getTimeInMillis();
//
//        Log.d("SSSSSSSS", "" + startMillis);
//        Log.d("SSSSSSSS", "" + endMillis);
//        String query = "SELECT COUNT(*) FROM " + tableName +
//                " WHERE " +
//                columnSchedule + " >= " + startMillis + " AND " +
//                columnSchedule + " <= " + endMillis + " AND " +
//                columnCompleted + " = " + 0 + " AND " +
//                columnNotified + " > " + 0 + " AND " +
//                columnExpired + " = " + 0;

        String query = "SELECT COUNT(*) FROM " + tableName
                + " WHERE " +
                columnCompleted + " = " + 0 + " AND " +
                columnExpired + " = " + 0 + " AND " +
                columnNotified + " > " + 0;

        Cursor c = localController.rawQuery(query, null);
//        Cursor c = localController.rawQuery("SELECT * FROM " + tableName, null);

        Survey grouped = Survey.getAvailableSurvey(GROUPED_SSPP);

        int count = 0;

//        if(grouped != null) {
//            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//            if(!format.format(grouped.scheduledAt).equals(format.format(startDateTime.getTime()))) {
//                count = 1;
//            }
//        }

        if(c.getCount() > 0) {
            c.moveToFirst();
//            Log.d("RECORD", "ID: " + c.getInt(0) + ", TS: " + c.getLong(1) + ", SCHEDULED_AT: " + c.getLong(2) + ", COMPLETED: " + c.getInt(3) +  ", NOTIFIED: " + c.getInt(4));
            count += c.getInt(0);
        }

        c.close();

        return count;

    }

    public static Survey getAvailableSurvey(SurveyType survey) {
        String columnCompleted = LocalDbUtility.getTableColumns(table)[3];
        String columnNotified = LocalDbUtility.getTableColumns(table)[4];
        String columnType = LocalDbUtility.getTableColumns(table)[7];
        String columnExpired = LocalDbUtility.getTableColumns(table)[5];

        String query = columnCompleted + " = " + 0 + " AND " +
                columnExpired + " = " + 0 + " AND " +
                columnNotified + " > " + 0 + " AND " +
                columnType + " = \"" + survey.getSurveyName() + "\"";

        return (Survey) find("*", query, "");
    }

    @Override
    public void delete() {
        localController.delete(table.getTableName(), columns[0] + " = " + id);

        for(Map.Entry<SurveyType, TableHandler> entry: surveys.entrySet()) {
            entry.getValue().delete();
        }
    }

    public static int getCount(SurveyType survey) {
        String columnType = LocalDbUtility.getTableColumns(table)[7];
        Cursor c = localController.rawQuery("SELECT COUNT(*) FROM " + LocalDbUtility.getTableName(table) +
                " WHERE " + columnType + " = \"" + survey.getSurveyName() + "\"", null);

        c.moveToFirst();

        int a = c.getInt(0);
        c.close();
        return a;
    }

    @Override
    public String toString() {
        String ret = "Survey(id: " + id +  ", newRecord: " + isNewRecord + ", ts: " + ts + ", scheduledAt: " + scheduledAt + ", completed: " + completed + ", notified: " + notified + ", expired: " + expired + ", grouped: " + grouped + ", ts: " + surveyType.getSurveyName() + ")\n";

        for(Map.Entry<SurveyType, TableHandler> record: surveys.entrySet()) {
            ret += "\t" + record.getValue().toString() + "\n";
        }

        return ret;
    }
}