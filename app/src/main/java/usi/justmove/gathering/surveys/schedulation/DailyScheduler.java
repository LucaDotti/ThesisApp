package usi.justmove.gathering.surveys.schedulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import usi.justmove.MyApplication;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.handle.SurveyEventReceiver;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.LocalTables;
import usi.justmove.local.database.tables.SurveyTable;

/**
 * Created by usi on 14/03/17.
 */

public class DailyScheduler {
    private AlarmManager alarmMgr;
    private Context context;

    public DailyScheduler() {
        this.context = MyApplication.getContext();
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(SurveyType surveyType) {
        SurveyConfig config = SurveyConfigFactory.getConfig(surveyType, context);

        schedule(config);
    }

    private void schedule(SurveyConfig config) {
        Survey s;

        long[] times = getAlarmsTimes(config);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");

        for (int i = 0; i < config.surveysCount; i++) {
            s = (Survey) LocalTables.getTableHandler(LocalTables.TABLE_SURVEY);
            insertSurveyRecord(s, config, times[i]);

            if(times[i] >= 0) {
                setAlarms(s, config, times[i]);

            } else {
                Log.d("Scheduler", "Too late for this survey.");
            }
        }
    }

    private void insertSurveyRecord(Survey survey, SurveyConfig config, long scheduleTime) {
        ContentValues attributes = new ContentValues();
        attributes.put(SurveyTable.KEY_SURVEY_SCHEDULED_AT, scheduleTime/1000);
        attributes.put(SurveyTable.KEY_SURVEY_NOTIFIED, 0);
        attributes.put(SurveyTable.KEY_SURVEY_EXPIRED, false);
        attributes.put(SurveyTable.KEY_SURVEY_COMPLETED, false);
        attributes.put(SurveyTable.KEY_SURVEY_TYPE, config.survey.getSurveyName());
        attributes.put(SurveyTable.KEY_SURVEY_GROUPED, config.grouped);
        survey.setAttributes(attributes);
        survey.save();
    }

    private void setAlarms(Survey survey, SurveyConfig config, long scheduleTime) {
        Intent intent = new Intent(context, SurveyEventReceiver.class);
        intent.putExtra("survey_id", survey.id);
        intent.setAction(SurveyEventReceiver.SURVEY_SCHEDULED_INTENT);


        long notificationInterval = config.maxElapseTimeForCompletion / config.notificationsCount;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        Calendar c;
        PendingIntent alarmIntent;
        for (int i = 0; i < config.notificationsCount; i++) {
            c = Calendar.getInstance();
            alarmIntent = PendingIntent.getBroadcast(context, (int) c.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, scheduleTime + i * notificationInterval, alarmIntent);
            Log.d("DailyScheduler", "Scheduled survey notification at " + format.format(scheduleTime + i * notificationInterval));
        }
        Log.d("DailyScheduler", "-----------------------------");
    }

    private long[] getAlarmsTimes(SurveyConfig config) {
        long[] alarmsTimes;

        if(config.times == null) {
            alarmsTimes  = new long[1];
            Calendar c = Calendar.getInstance();
//            c.add(Calendar.MINUTE, 2);
            alarmsTimes[0] = c.getTimeInMillis();

            return alarmsTimes;
        }

        alarmsTimes = new long[config.surveysCount];

        DateTime now = new DateTime();
        DateTime start;
        DateTime end;
        int[] timeStart;
        int[] timeEnd;
        long scheduleOffset;
        long diff;
        DateTime schedule = null;
        DateTime tempSchedule;
        Random r = new Random();

        for (int i = 0; i < config.surveysCount; i++) {
            timeStart = parseTime(config.times[i].first);
            timeEnd = parseTime(config.times[i].second);

            start = new DateTime().withTime(timeStart[0], timeStart[1], 0, 0);
            end = new DateTime().withTime(timeEnd[0], timeEnd[1], 0, 0).minusMillis((int) config.maxElapseTimeForCompletion);

            if(now.isAfter(end)) {
                alarmsTimes[i] = -1;
            } else {
                diff = end.getMillis() - start.getMillis();
                scheduleOffset = r.nextInt((int) diff);
                if (schedule != null) {
                    tempSchedule = new DateTime(start.getMillis() + scheduleOffset).plusHours(1);
                    while (Math.abs(tempSchedule.getMillis() - schedule.getMillis()) < config.minElapseTimeBetweenSurveys) {
                        scheduleOffset = r.nextInt((int) scheduleOffset);
                        tempSchedule = new DateTime(start.getMillis() + scheduleOffset).plusHours(1);
                    }
                    schedule = tempSchedule;
                } else {
                    schedule = new DateTime(start.getMillis() + scheduleOffset).plusHours(1);
                }

                alarmsTimes[i] = schedule.getMillis();
            }
        }

        return alarmsTimes;
    }


    private int[] parseTime(String time) {
        String[] split = time.split(":");
        int[] timeInt = {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        return timeInt;
    }

}
