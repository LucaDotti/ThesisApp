package usi.justmove.gathering.user.scheduler;

import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;

/**
 * Created by usi on 07/02/17.
 */

public class PWBScheduler extends Scheduler {
    private LocalTables pwbTable;
    private LocalStorageController localController;
    private long maxElapseTimeForCompletion;
    private String startTime;
    private String endTime;
    private String period;

    public PWBScheduler(LocalTables pwbTable, LocalStorageController localController, String startTime, String endTime, long maxElapseTimeForCompletion, String period) {
        this.pwbTable = pwbTable;
        this.localController = localController;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxElapseTimeForCompletion = maxElapseTimeForCompletion;
        this.period = period;
    }

    private Cursor getWeekSurveys() {
        String tableName = LocalDbUtility.getTableName(pwbTable);
        String columnTS = LocalDbUtility.getTableColumns(pwbTable)[2];
        LocalDateTime startDateTime = new LocalDateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withDayOfWeek(DateTimeConstants.SUNDAY).withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnTS + " >= " + startMillis + " AND " + columnTS + " <= " + endMillis, null);

        return c;
    }

    private void insertSurveys() {
        int day;
        int days;
        Random r = new Random();
        Random r2 = new Random();
        if(period.equals("weekend")) {
            DateTime now = new DateTime();
            if(now.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                days = 2;
            } else if(now.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                days = 1;
            } else {
                days = 3;
            }

            switch (r.nextInt(days)) {
                case 0:
                    day = DateTimeConstants.SUNDAY;
                    break;
                case 1:
                    day = DateTimeConstants.SATURDAY;
                    break;
                case 2:
                    day = DateTimeConstants.FRIDAY;
                    break;
                default:
                    day = 0;
            }
        } else {
            switch (period) {
                case "monday":
                    day = DateTimeConstants.MONDAY;
                    break;
                case "tuesday":
                    day = DateTimeConstants.TUESDAY;
                    break;
                case "wednesday":
                    day = DateTimeConstants.WEDNESDAY;
                    break;
                case "thursday":
                    day = DateTimeConstants.THURSDAY;
                    break;
                case "friday":
                    day = DateTimeConstants.FRIDAY;
                    break;
                case "saturday":
                    day = DateTimeConstants.SATURDAY;
                    break;
                case "sunday":
                    day = DateTimeConstants.SUNDAY;
                    break;
                default:
                    day = 0;
            }
        }



        String[] startSplit = startTime.split(":");
        String[] endSplit = endTime.split(":");
        DateTime start = new DateTime().withDayOfWeek(day).withTime(Integer.parseInt(startSplit[0]), Integer.parseInt(startSplit[1]), 0, 0);
        DateTime end = new DateTime().withDayOfWeek(day).withTime(Integer.parseInt(endSplit[0]), Integer.parseInt(endSplit[1]), 0, 0).minusMillis((int) maxElapseTimeForCompletion);

        long diff = end.getMillis() - start.getMillis();

        long scheduleOffset = r2.nextInt( (int) diff);

        DateTime scheduleTime = new DateTime(start.getMillis() + scheduleOffset).plusHours(1);

        insertSurveyRecord(scheduleTime.getMillis());

    }

    private void insertSurveyRecord(long scheduleTime) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();
        String[] columns = LocalDbUtility.getTableColumns(pwbTable);
        record.put(columns[0], null);
        record.put(columns[1], Long.toString(scheduleTime/1000));
        record.put(columns[2], Long.toString(scheduleTime/1000));
        record.put(columns[3], Integer.toString(0));
        record.put(columns[4], Integer.toString(0));
        record.put(columns[5], null);
        record.put(columns[6], null);
        record.put(columns[7], null);
        record.put(columns[8], null);
        record.put(columns[9], null);
        record.put(columns[10], null);
        record.put(columns[11], null);

        records.add(record);

        localController.insertRecords(LocalDbUtility.getTableName(pwbTable), records);
        Log.d("SURVEY SERVICE", "Added pwb record: schedule_at( " + record.get(columns[1]) + " ), completed( " + record.get(columns[2]) + " )");
    }

    @Override
    protected void schedule() {
        Cursor c = getWeekSurveys();

        if(c.getCount() == 0) {
            insertSurveys();
        }
    }
}
