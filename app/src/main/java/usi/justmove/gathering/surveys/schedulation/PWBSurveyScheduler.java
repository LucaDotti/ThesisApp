package usi.justmove.gathering.surveys.schedulation;

import android.database.Cursor;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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
import usi.justmove.gathering.surveys.config.Days;

/**
 * Created by usi on 15/02/17.
 */

public class PWBSurveyScheduler implements SurveyScheduler {
    private SurveyConfig surveyConfig;
    private LocalTables pwbTable;
    private LocalStorageController localController;

    public PWBSurveyScheduler(SurveyConfig surveyConfig, LocalTables pwbTable, LocalStorageController localController) {
        this.surveyConfig = surveyConfig;
        this.pwbTable = pwbTable;
        this.localController = localController;
    }

    @Override
    public void schedule() {
        Cursor c = getWeekSurveys();

        if(c.getCount() == 0) {
            insertSurveys();
        }

        c.close();
    }

    @Override
    public SurveyConfig getConfig() {
        return surveyConfig;
    }

    private Cursor getWeekSurveys() {
        String tableName = LocalDbUtility.getTableName(pwbTable);
        String columnSchedule = LocalDbUtility.getTableColumns(pwbTable)[2];

        LocalDateTime startDateTime = new LocalDateTime().withDayOfWeek(DateTimeConstants.MONDAY).withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withDayOfWeek(DateTimeConstants.SUNDAY).withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;

        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis, null);

        return c;
    }

    private void insertSurveys() {
        Days[] days = surveyConfig.period.days;

        Random r = new Random();
        Random r2 = new Random();
        int scheduleDay;
        int day = -1;
        DateTime now = new DateTime();
        int i;
        for(i = 0; i < days.length; i++) {

            if(now.getDayOfWeek() == Days.toDateTimeConstants(days[i])) {
                day = days[i].ordinal();
                break;
            }
        }

        if(day < 0) {
            scheduleDay = days[r.nextInt(days.length)].ordinal();
        } else {
            scheduleDay = days[i + r.nextInt(days.length-i)].ordinal();


        }


        int[] startTime = parseTime(surveyConfig.startTime);
        int[] endTime = parseTime(surveyConfig.endTime);
        DateTime start = new DateTime().withDayOfWeek(scheduleDay+1).withTime(startTime[0], startTime[1], 0, 0).plusHours(1);
        DateTime end = new DateTime().withDayOfWeek(scheduleDay+1).withTime(endTime[0], endTime[1], 0, 0).minusMillis((int) surveyConfig.maxElapseTimeForCompletion).plusHours(1);

        long diff = end.getMillis() - start.getMillis();

        long scheduleOffset = r2.nextInt( (int) diff);

        DateTime scheduleTime = new DateTime(start.getMillis() + scheduleOffset);

        insertSurveyRecord(scheduleTime.getMillis());
    }

    private int[] parseTime(String time) {
        String[] split = time.split(":");
        int[] timeInt = {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        return timeInt;
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
        record.put(columns[5], Integer.toString(0));
        record.put(columns[6], null);
        record.put(columns[7], null);
        record.put(columns[8], null);
        record.put(columns[9], null);
        record.put(columns[10], null);
        record.put(columns[11], null);
        record.put(columns[12], null);
        record.put(columns[13], null);

        records.add(record);

        localController.insertRecords(LocalDbUtility.getTableName(pwbTable), records);
        Log.d("SURVEY SERVICE", "Added pwb record: schedule_at( " + record.get(columns[2]) + " )");
    }
}
