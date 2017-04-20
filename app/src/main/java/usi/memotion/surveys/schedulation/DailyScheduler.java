package usi.memotion.surveys.schedulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import usi.memotion.MyApplication;
import usi.memotion.surveys.config.SurveyType;
import usi.memotion.local.database.tableHandlers.SurveyConfig;
import usi.memotion.surveys.handle.SurveyEventReceiver;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tableHandlers.Survey;
import usi.memotion.local.database.db.LocalTables;
import usi.memotion.local.database.tableHandlers.SurveyAlarmSurvey;
import usi.memotion.local.database.tables.SurveyAlarmSurveyTable;
import usi.memotion.local.database.tables.SurveyTable;

/**
 * Created by usi on 14/03/17.
 */

public class DailyScheduler {
    private AlarmManager alarmMgr;
    private Context context;
    private SurveyConfig currConfig;
    private boolean isImmediate;
    private int currAlarmId;

    public DailyScheduler() {
        this.context = MyApplication.getContext();
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(Intent intent) {
        Log.d("Daily Scheduler", "-----------------------------");
        SurveyType surveyType = SurveyType.getSurvey(intent.getStringExtra("survey"));
        isImmediate = intent.getBooleanExtra("immediate", false);
        currAlarmId = intent.getIntExtra("alarm_id", -1);

        currConfig = SurveyConfig.getConfig(surveyType);

        Survey[] currSurveys = SurveyAlarmSurvey.getSurveys(currAlarmId);

        long[] times = getAlarmsTimes();

        Survey[] surveys = new Survey[currConfig.dailySurveysCount];

        if(currSurveys.length == 0) {
            Log.d("Daily scheduler", "No notification alarms found for alarm " + currAlarmId);
            for (int i = 0; i < surveys.length; i++) {
                if(times[i] >= 0) {
                    surveys[i] = insertSurveyRecord(times[i]);
                } else {
                    Log.d("Daily scheduler", "\tToo late for that survey");
                }
            }

            long[] notificationTimes;
            for (int i = 0; i < surveys.length; i++) {
                if(times[i] >= 0) {
                    notificationTimes = getNotificationAlarmTimes(surveys[i], isImmediate);
                    setAlarms(surveys[i], notificationTimes, isImmediate);
                }
                isImmediate = false;
            }
        } else {
            Log.d("Daily scheduler", "Notification alarms found for alarm " + currAlarmId);
            long[] notificationTimes;
            for(Survey s: currSurveys) {
                Log.d("Daily scheduler", "\tChecking alarm for " + s.toString());
                notificationTimes = getNotificationAlarmTimes(s, isImmediate);
                checkNotificationAlarms(s, notificationTimes);
            }
        }

        Log.d("Daily Scheduler", "-----------------------------");
    }

    private void checkNotificationAlarms(Survey s, long[] times) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        Intent intent = new Intent(context, SurveyEventReceiver.class);
        intent.putExtra("survey_id", s.id);
        intent.setAction(SurveyEventReceiver.SURVEY_NOTIFICATION_INTENT);

        for(int i = s.notified; i < times.length; i++) {
            if(!checkAlarmExists((int) times[i], intent)) {
                Log.d("Daily scheduler", "\t Alarm " + times[i] + " does not exists. Resetting alarm...");
                Log.d("Daily Scheduler", "\t Scheduled survey " + currConfig.surveyType.getSurveyName() + " notification at " + format.format(times[i]));
                setAlarm(intent, times[i]);
            } else {
                Log.d("Daily scheduler", "\t Alarm " + times[i] + " already exists");
            }
        }
    }

    private boolean checkAlarmExists(int alarmId, Intent i) {
        PendingIntent pIntent = PendingIntent.getBroadcast(context, alarmId,
                i,
                PendingIntent.FLAG_NO_CREATE);
        return pIntent != null;
    }

    private Survey insertSurveyRecord(long scheduleTime) {
        Survey survey = (Survey) LocalTables.getTableHandler(LocalTables.TABLE_SURVEY);
        ContentValues attributes = new ContentValues();

        if(scheduleTime < 0) {
            attributes.put(SurveyTable.KEY_SURVEY_SCHEDULED_AT, scheduleTime);
            attributes.put(SurveyTable.KEY_SURVEY_EXPIRED, true);
        } else {
            attributes.put(SurveyTable.KEY_SURVEY_SCHEDULED_AT, scheduleTime);
            attributes.put(SurveyTable.KEY_SURVEY_EXPIRED, false);
        }

        attributes.put(SurveyTable.KEY_SURVEY_NOTIFIED, 0);
        attributes.put(SurveyTable.KEY_SURVEY_COMPLETED, false);
        attributes.put(SurveyTable.KEY_SURVEY_TYPE, currConfig.surveyType.getSurveyName());
        attributes.put(SurveyTable.KEY_SURVEY_GROUPED, currConfig.grouped);
        survey.setAttributes(attributes);
        survey.save();

        insertSurveyAlarmsSurveyRecord(survey.id);
        Log.d("Daily Scheduler", "\tInserting " + survey.toString());
        return survey;
    }

    private void insertSurveyAlarmsSurveyRecord(long surveyId) {
        SQLiteController c = SQLiteController.getInstance(context);
        ContentValues record = new ContentValues();
        record.put(SurveyAlarmSurveyTable.KEY_SURVEY_ALARMS_SURVEY_SURVEY_ALARMS_ID, currAlarmId);
        record.put(SurveyAlarmSurveyTable.KEY_SURVEY_ALARMS_SURVEY_SURVEY_ID, surveyId);

        c.insertRecord(SurveyAlarmSurveyTable.TABLE_SURVEY_ALARMS_SURVEY_TABLE, record);
    }

    private void setAlarms(Survey survey, long[] notificationTimes, boolean isImmediate) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");

        Intent intent = new Intent(context, SurveyEventReceiver.class);
        intent.putExtra("survey_id", survey.id);
        intent.setAction(SurveyEventReceiver.SURVEY_NOTIFICATION_INTENT);

        for(long t: notificationTimes) {
            if(isImmediate) {
                setImmediateAlarm(intent, t);
                isImmediate = false;
            } else {
                setAlarm(intent, t);
            }

            Log.d("Daily Scheduler", "\tScheduled survey " + currConfig.surveyType.getSurveyName() + " notification at " + format.format(t));
        }
    }

    private void setImmediateAlarm(Intent intent, long t) {
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) t, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            alarmIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private long[] getNotificationAlarmTimes(Survey survey, boolean immediate) {
        long notificationInterval = currConfig.maxCompletionTime / currConfig.notificationsCount;

        int notificationStart = 1;

        long[] times = new long[currConfig.notificationsCount+1];
        times[0] = survey.scheduledAt;

        if(immediate) {
            Calendar startDay = Calendar.getInstance();
            startDay.set(Calendar.HOUR_OF_DAY, 7);
            startDay.set(Calendar.MINUTE, 0);
            startDay.set(Calendar.SECOND, 0);

            Calendar endDay = Calendar.getInstance();
            endDay.set(Calendar.HOUR_OF_DAY, 20);
            endDay.set(Calendar.MINUTE, 0);
            endDay.set(Calendar.SECOND, 0);

            if(survey.scheduledAt < startDay.getTimeInMillis() && survey.scheduledAt > endDay.getTimeInMillis()) {
                Random r = new Random();
                notificationStart = 2;
                times[1] = r.nextInt((int) (endDay.getTimeInMillis() - startDay.getTimeInMillis()));
            }
        }

        for(int i = notificationStart; i < times.length; i++) {
            times[i] = times[i-1]+notificationInterval;
        }

        return times;
    }

    private void setAlarm(Intent i, long time) {
        Calendar c = Calendar.getInstance();
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) time, i, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
    }

    private long[] getAlarmsTimes() {
        long[] alarmsTimes;

        if(currConfig.dailyIntervals.length == 0) {
            alarmsTimes  = new long[1];
            Calendar c = Calendar.getInstance();
            alarmsTimes[0] = c.getTimeInMillis();

            return alarmsTimes;
        }


        alarmsTimes = new long[currConfig.dailySurveysCount];

        int surveyStart = 0;

        if(isImmediate) {
            Calendar now = Calendar.getInstance();
            alarmsTimes[0] = now.getTimeInMillis();
            surveyStart = 1;
        }

        Calendar now = Calendar.getInstance();
        Calendar start;
        Calendar end;
        int[] timeStart;
        int[] timeEnd;
        long scheduleOffset;
        long diff;
        Calendar schedule = null;
        Calendar tempSchedule;
        Random r = new Random();

        for (int i = surveyStart; i < currConfig.dailySurveysCount; i++) {
            timeStart = parseTime(currConfig.dailyIntervals[i].first);
            timeEnd = parseTime(currConfig.dailyIntervals[i].second);

            start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, timeStart[0]);
            start.set(Calendar.MINUTE, timeStart[1]);
            start.set(Calendar.SECOND, 0);

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String s = format.format(start.getTime());
            end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, timeEnd[0]);
            end.set(Calendar.MINUTE, timeEnd[1]);
            end.set(Calendar.SECOND, 0);

            int complTimeInMin = (currConfig.maxCompletionTime/(1000*60));
            end.add(Calendar.MINUTE,  -complTimeInMin);
            String e = format.format(end.getTime());
            if(now.getTimeInMillis() > end.getTimeInMillis()) {
                alarmsTimes[i] = -1;
                if(currConfig.surveyType == SurveyType.GROUPED_SSPP) {
                    alarmsTimes[i] = now.getTimeInMillis();
                }
            } else {

                if(now.getTimeInMillis() > start.getTimeInMillis()) {
                    start = now;
                }

                diff = end.getTimeInMillis() - start.getTimeInMillis();
                scheduleOffset = r.nextInt((int) diff);
                if (schedule != null) {
                    tempSchedule = Calendar.getInstance();
                    tempSchedule.setTimeInMillis(start.getTimeInMillis() + scheduleOffset);
                    while (Math.abs(tempSchedule.getTimeInMillis() - schedule.getTimeInMillis()) < currConfig.minTimeBetweenSurveys) {
                        scheduleOffset = r.nextInt((int) scheduleOffset);
                        tempSchedule = Calendar.getInstance();
                        tempSchedule.setTimeInMillis(start.getTimeInMillis() + scheduleOffset);
                    }
                    schedule = tempSchedule;
                } else {
                    schedule = Calendar.getInstance();
                    schedule.setTimeInMillis(start.getTimeInMillis() + scheduleOffset);
                }

                alarmsTimes[i] = schedule.getTimeInMillis();
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
