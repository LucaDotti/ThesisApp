package usi.justmove.gathering.surveys.inspection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import usi.justmove.gathering.surveys.Surveys;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;

/**
 * Created by usi on 15/02/17.
 */

public class PAMSurveyInspector implements SurveyInspector {
    private LocalTables pamTable;
    private LocalStorageController localController;
    private SurveyConfig config;
    private Context context;

    public PAMSurveyInspector(LocalTables pamTable, LocalStorageController localController, Context context) {
        this.pamTable = pamTable;
        this.localController = localController;
        this.context = context;
        config = SurveyConfigFactory.getConfig(Surveys.PAM, context);
    }

    @Override
    public SurveyEvent inspect() {
        Cursor surveys = getTodaySurveys();

        if(surveys.getCount() == 0) {
            return null;
        }

        int surveyId = -1;
        int currSurveyId;
        int surveyStatus = -1;
        long scheduleTime;
        int notificationsCount;
        int expired;
        while(surveys.moveToNext()) {
            currSurveyId = surveys.getInt(0);
            scheduleTime = surveys.getLong(2);
            notificationsCount = surveys.getInt(4);
            expired = surveys.getInt(5);

            if(expired == 0) {
                if(notificationsCount < config.notificationsCount) {
                    if(checkNeedNotify(scheduleTime, notificationsCount)) {
                        incrementNotified(currSurveyId, notificationsCount);
                        surveyId = currSurveyId;
                        surveyStatus = SurveyEvent.SCHEDULED;
                    }
                } else {
                    surveyId = currSurveyId;
                    surveyStatus = SurveyEvent.EXPIRED;
                    updateExpired(surveyId);
                }
            }
        }

        surveys.close();

        if(surveyId >= 0) {
            return new SurveyEvent(Surveys.PAM, surveyId, surveyStatus);
        } else {
            return null;
        }
    }

//    private boolean isExpired(long scheduleTime) {
//        DateTime now = new DateTime();
//        return Math.abs((now.getMillis()/1000)-scheduleTime) >= config.maxElapseTimeForCompletion;
//    }

    private Cursor getTodaySurveys() {
        String tableName = LocalDbUtility.getTableName(pamTable);
        String columnSchedule = LocalDbUtility.getTableColumns(pamTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(pamTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(pamTable)[4];

        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;

        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0, null);

        return c;
    }

    private boolean checkNeedNotify(long scheduleTime, int notificationsCount) {
        long elapseTimeBetweenPersistentNotifications = (config.maxElapseTimeForCompletion/config.notificationsCount)/1000;
        DateTime now = new DateTime().plusHours(1);
        long nowMillis = now.getMillis()/1000;
        return Math.abs((scheduleTime + notificationsCount*elapseTimeBetweenPersistentNotifications) - nowMillis)*1000 <= 1000;
    }

    private void incrementNotified(int surveyId, int notificationsCount) {
        ContentValues record = new ContentValues();
        record.put(PAMTable.KEY_PAM_NOTIFIED, notificationsCount+1);

        localController.update(LocalDbUtility.getTableName(pamTable), record, PAMTable.KEY_PAM_ID + " = " + surveyId);
    }

    private void updateExpired(int surveyId) {
        ContentValues record = new ContentValues();
        record.put(PAMTable.KEY_PAM_EXPIRED, 1);

        localController.update(LocalDbUtility.getTableName(pamTable), record, PAMTable.KEY_PAM_ID + " = " + surveyId);
    }
}
