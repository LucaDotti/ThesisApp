package usi.justmove.gathering.surveys.schedulation;

import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;

/**
 * Created by usi on 15/02/17.
 */

public class PAMSurveyScheduler implements SurveyScheduler {
    private SurveyConfig surveyConfig;
    private LocalTables pamTable;
    private LocalStorageController localController;

    public PAMSurveyScheduler(SurveyConfig surveyConfig, LocalTables pamTable, LocalStorageController localController) {
        this.surveyConfig = surveyConfig;
        this.pamTable = pamTable;
        this.localController = localController;
    }

    @Override
    public void schedule() {
        Cursor c = getTodaySurveys();

        if(c.getCount() == 0) {
            insertSurveys();
        }

        c.close();
    }

    @Override
    public SurveyConfig getConfig() {
        return surveyConfig;
    }

    private int[] parseTime(String time) {
        String[] split = time.split(":");
        int[] timeInt = {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        return timeInt;
    }

    private void insertSurveys() {
        int[] startTime = parseTime(surveyConfig.startTime);
        int[] afternoonTime = parseTime(surveyConfig.midTimes[0]);
        int[] endTime = parseTime(surveyConfig.endTime);

        DateTime start = new DateTime().withTime(startTime[0], startTime[1], 0, 0);
        DateTime afternoon = new DateTime().withTime(afternoonTime[0], afternoonTime[1], 0, 0);
        DateTime end = new DateTime().withTime(endTime[0], endTime[1], 0, 0).minusMillis((int) surveyConfig.maxElapseTimeForCompletion);

        long morningDiff = afternoon.getMillis() - start.getMillis();
        long afternoonDiff = end.getMillis() - afternoon.getMillis();

        Random r = new Random();
        Random r2 = new Random();

        long morningScheduleOffset = r.nextInt((int) morningDiff);
        long afternoonScheduleOffset = r2.nextInt((int) afternoonDiff);

        DateTime morningSchedule = new DateTime(start.getMillis()+morningScheduleOffset).plusHours(1);
        DateTime afternoonSchedule = new DateTime(afternoon.getMillis()+afternoonScheduleOffset).plusHours(1);

        while(Math.abs(afternoonSchedule.getMillis() - morningSchedule.getMillis()) < surveyConfig.minElapseTimeBetweenSurveys) {
            morningScheduleOffset = r.nextInt((int) morningDiff);
            afternoonScheduleOffset = (int) r2.nextInt((int) afternoonDiff);
            morningSchedule = new DateTime(start.getMillis()+morningScheduleOffset).plusHours(1);
            afternoonSchedule = new DateTime(afternoon.getMillis()+afternoonScheduleOffset).plusHours(1);
        }

        insertSurveyRecord(morningSchedule.getMillis(), "morning");
        insertSurveyRecord(afternoonSchedule.getMillis(), "afternoon");

    }

    private void insertSurveyRecord(long scheduleTime, String period) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();
        String[] columns = LocalDbUtility.getTableColumns(pamTable);
        record.put(columns[0], null);
        record.put(columns[1], Long.toString(scheduleTime/1000));
        record.put(columns[2], Long.toString(scheduleTime/1000));
        record.put(columns[3], Integer.toString(0));
        record.put(columns[4], Integer.toString(0));
        record.put(columns[5], Integer.toString(0));
        record.put(columns[6], period);
        record.put(columns[7], null);
        record.put(columns[8], null);
        record.put(columns[9], null);
        record.put(columns[10], null);
        record.put(columns[11], null);
        record.put(columns[12], null);
        record.put(columns[13], null);
        record.put(columns[14], null);

        records.add(record);

        localController.insertRecords(PAMTable.TABLE_PAM, records);
        Log.d("SURVEY SERVICE", "Added pam record: schedule_at( " + record.get(columns[2]) + " ), completed( " + record.get(columns[3]) + " ), period( " + record.get(columns[6]) + " )");
    }


    private Cursor getTodaySurveys() {
        String tableName = LocalDbUtility.getTableName(pamTable);
        String columnSchedule = LocalDbUtility.getTableColumns(pamTable)[2];

        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;

        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis, null);

        return c;
    }
}
