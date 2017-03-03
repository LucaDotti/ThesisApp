package usi.justmove.gathering.surveys.inspection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDateTime;

import usi.justmove.gathering.surveys.Surveys;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.PWBTable;

/**
 * Created by usi on 15/02/17.
 */

public class PWBSurveyInspector implements SurveyInspector {
    private LocalTables pwbTable;
    private LocalStorageController localController;
    private SurveyConfig config;

    public PWBSurveyInspector(LocalTables pwbTable, LocalStorageController localController, Context context) {
        this.pwbTable = pwbTable;
        this.localController = localController;
        config = SurveyConfigFactory.getConfig(Surveys.PAM, context);
    }

    @Override
    public SurveyEvent inspect() {
        Cursor surveys = getWeekSurveys();

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
                if(notificationsCount < config.notificationsCount - 1) {
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
            return new SurveyEvent(Surveys.PWB, surveyId, surveyStatus);
        } else {
            return null;
        }
    }

    private Cursor getWeekSurveys() {
        String tableName = LocalDbUtility.getTableName(pwbTable);
        String columnSchedule = LocalDbUtility.getTableColumns(pwbTable)[2];

        DateTime startDateTime = new DateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTime(0, 0, 0, 999).plusHours(1);
        DateTime endDateTime = new DateTime().withDayOfWeek(DateTimeConstants.SUNDAY).withTime(23, 59, 59, 999).plusHours(1);
        long startMillis = startDateTime.getMillis()/1000;
        long endMillis = endDateTime.getMillis()/1000;

        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis, null);
        return c;
    }

    private boolean checkNeedNotify(long scheduleTime, int notificationsCount) {

        long elapseTimeBetweenPersistentNotifications = (config.maxElapseTimeForCompletion/config.notificationsCount)/1000;
        DateTime now = new DateTime().plusHours(1);
        long nowMillis = now.getMillis()/1000;
        long a = Math.abs((scheduleTime + notificationsCount*elapseTimeBetweenPersistentNotifications) - nowMillis);
        return Math.abs((scheduleTime + notificationsCount*elapseTimeBetweenPersistentNotifications) - nowMillis)*1000 <= 1000;
    }

    private void incrementNotified(int surveyId, int notificationsCount) {
        ContentValues record = new ContentValues();
        record.put(PWBTable.KEY_PWB_NOTIFIED, notificationsCount+1);

        localController.update(LocalDbUtility.getTableName(pwbTable), record, PWBTable.KEY_PWB_ID + " = " + surveyId);
    }

    private void updateExpired(int surveyId) {
        ContentValues record = new ContentValues();
        record.put(PWBTable.KEY_PWB_EXPIRED, 1);

        localController.update(LocalDbUtility.getTableName(pwbTable), record, PWBTable.KEY_PWB_ID + " = " + surveyId);
    }
}
