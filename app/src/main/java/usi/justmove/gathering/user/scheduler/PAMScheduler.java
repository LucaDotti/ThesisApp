package usi.justmove.gathering.user.scheduler;

import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;

import static android.R.attr.max;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by usi on 07/02/17.
 */

public class PAMScheduler extends Scheduler {
    private LocalTables pamTable;
    private LocalStorageController localController;
    private String startTime;
    private String afternoon;
    private String endTime;
    private long minElapseTimeBetweenSurveys;
    private long maxElapseTimeForCompletition;

    public PAMScheduler(LocalTables pamTable, LocalStorageController localController, String startTime, String afternoon, String endTime, long minElapseTimeBetweenSurveys, long maxElapseTimeForCompletition) {
        this.pamTable = pamTable;
        this.localController = localController;
        this.startTime = startTime;
        this.afternoon = afternoon;
        this.endTime = endTime;
        this.minElapseTimeBetweenSurveys = minElapseTimeBetweenSurveys;
        this.maxElapseTimeForCompletition = maxElapseTimeForCompletition;
    }

    private void insertSurveys() {
        String[] startSplit = startTime.split(":");
        String[] afternoonSplit = afternoon.split(":");
        String[] endSplit = endTime.split(":");
        DateTime now = new DateTime();
        DateTime start = new DateTime().withTime(Integer.parseInt(startSplit[0]), Integer.parseInt(startSplit[1]), 0, 0);
        DateTime afternoon = new DateTime().withTime(Integer.parseInt(afternoonSplit[0]), Integer.parseInt(afternoonSplit[1]), 0, 0);
        DateTime end = new DateTime().withTime(Integer.parseInt(endSplit[0]), Integer.parseInt(endSplit[1]), 0, 0).minusMillis((int) maxElapseTimeForCompletition);

        long morningDiff = afternoon.getMillis() - start.getMillis();
        long afternoonDiff = end.getMillis() - afternoon.getMillis();

        Random r = new Random();
        Random r2 = new Random();

        long morningScheduleOffset = r.nextInt((int) morningDiff);
        long afternoonScheduleOffset = r2.nextInt((int) afternoonDiff);

        while(Math.abs(afternoonScheduleOffset - morningScheduleOffset) < minElapseTimeBetweenSurveys) {
            morningScheduleOffset = r.nextInt((int) morningDiff);
            afternoonScheduleOffset = (int) r2.nextInt((int) afternoonDiff);
        }

        DateTime morningSchedule = new DateTime(start.getMillis()+morningScheduleOffset).plusHours(1);
        DateTime afternoonSchedule = new DateTime(afternoon.getMillis()+afternoonScheduleOffset).plusHours(1);

//        if(Math.abs(now.getMillis() - afternoon.getMillis()) <= afternoon.getMillis()) {
//            insertSurveyRecord(morningSchedule.getMillis(), "morning");
//        }
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
        record.put(columns[4], null);
        record.put(columns[5], null);
        record.put(columns[6], null);
        record.put(columns[7], null);
        record.put(columns[8], null);
        record.put(columns[9], null);
        record.put(columns[10], null);
        record.put(columns[11], null);
        record.put(columns[12], period);
        record.put(columns[13], Integer.toString(0));

        records.add(record);

        localController.insertRecords(PAMTable.TABLE_PAM, records);
        Log.d("SURVEY SERVICE", "Added pam record: schedule_at( " + record.get(columns[2]) + " ), completed( " + record.get(columns[3]) + " ), period( " + record.get(columns[12]) + " )");
    }

    private Cursor getTodaySurveys() {
        String tableName = LocalDbUtility.getTableName(pamTable);
        String columnTS = LocalDbUtility.getTableColumns(pamTable)[2];
        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnTS + " >= " + startMillis + " AND " + columnTS + " <= " + endMillis, null);

        return c;
    }

    @Override
    protected void schedule() {
        Cursor c = getTodaySurveys();

        if(c.getCount() == 0) {
            insertSurveys();
        }
    }

}
