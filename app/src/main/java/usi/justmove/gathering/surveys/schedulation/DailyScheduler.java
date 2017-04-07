package usi.justmove.gathering.surveys.schedulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import usi.justmove.MyApplication;
import usi.justmove.gathering.surveys.config.Frequency;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.handle.SurveyEventReceiver;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.LocalTables;
import usi.justmove.local.database.tables.SurveyTable;

import static android.R.attr.id;
import static usi.justmove.R.array.surveys;

/**
 * Created by usi on 14/03/17.
 */

public class DailyScheduler {
    private AlarmManager alarmMgr;
    private Context context;
    private SurveyConfig currConfig;
    private boolean isImmediate;
    private boolean restore;

    public DailyScheduler() {
        this.context = MyApplication.getContext();
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(SurveyType surveyType, boolean immediate) {
        isImmediate = immediate;

        currConfig = SurveyConfigFactory.getConfig(surveyType, context);

//        int count = Survey.getAvailableSurveyCount(surveyType);
        int count = Survey.getTodaySurveyCount(surveyType);
        Log.d("COUNT", surveyType.getSurveyName() + "" + count);

        long[] times = getAlarmsTimes();


        Survey[] surveys = new Survey[currConfig.surveysCount];

        if(count < currConfig.surveysCount) {
            for (int i = 0; i < currConfig.surveysCount; i++) {
                if(times[i] >= 0) {
                    surveys[i] = insertSurveyRecord(times[i]);
                } else {
                    Log.d("Daily scheduler", "Too late for that survey");
                }
            }
        } else {
            surveys = Survey.getTodaySurveys(surveyType);

            int notified;
            for(Survey s: surveys) {
                notified = s.getAttributes().getAsInteger("notified");
                notified--;
                ContentValues attr = new ContentValues();
                attr.put("notified", notified);
                s.setAttribute("notified", attr);
                s.save();
            }
        }

        for (int i = 0; i < currConfig.surveysCount; i++) {
            if(times[i] >= 0) {
                setAlarms(surveys[i], isImmediate);
            }
            isImmediate = false;
        }

        restore = false;
    }

//    private void schedule(SurveyConfig config, boolean immediate, long[] times) {
//        int count = Survey.getTodaySurveyCount(config.survey);
//
//        Survey s;
//
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
//
//        for (int i = 0; i < config.surveysCount; i++) {
//            s = (Survey) LocalTables.getTableHandler(LocalTables.TABLE_SURVEY);
//
//            if(count == 0) {
//                insertSurveyRecord(config, times[i]);
//            }
//
//            if(times[i] >= 0) {
//
//                if(i == 0 && immediate) {
//                    Intent intent = new Intent(context, SurveyEventReceiver.class);
//                    intent.putExtra("survey_id", s.id);
//                    intent.setAction(SurveyEventReceiver.SURVEY_SCHEDULED_INTENT);
//                    Calendar c = Calendar.getInstance();
//                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) c.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                    try {
//                        alarmIntent.send();
//                    } catch (PendingIntent.CanceledException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                config.notificationsCount--;
//
//                int[] timeEnd = parseTime(config.dailyTimes[0].second);
//                Calendar end = Calendar.getInstance();
//                end.set(Calendar.HOUR_OF_DAY, timeEnd[0]);
//                end.set(Calendar.MINUTE, timeEnd[1]);
//
//                if(times[i] > end.getTimeInMillis() - config.maxElapseTimeForCompletion) {
//                    end.set(Calendar.HOUR_OF_DAY, 19);
//                    end.set(Calendar.MINUTE, 0);
//                    end.add(Calendar.DAY_OF_MONTH, 1);
//                    times[i] = end.getTimeInMillis();
//                }
//            } else {
//                Log.d("Scheduler", "Too late for this survey.");
//            }
//        }
//    }

    private Survey insertSurveyRecord(long scheduleTime) {
        Survey survey = (Survey) LocalTables.getTableHandler(LocalTables.TABLE_SURVEY);
        ContentValues attributes = new ContentValues();

        //CARE HERE
        if(scheduleTime < 0) {
            attributes.put(SurveyTable.KEY_SURVEY_SCHEDULED_AT, scheduleTime);
            attributes.put(SurveyTable.KEY_SURVEY_EXPIRED, true);
        } else {
            attributes.put(SurveyTable.KEY_SURVEY_SCHEDULED_AT, scheduleTime/1000);
            attributes.put(SurveyTable.KEY_SURVEY_EXPIRED, false);
        }

        attributes.put(SurveyTable.KEY_SURVEY_NOTIFIED, 0);
        attributes.put(SurveyTable.KEY_SURVEY_COMPLETED, false);
        attributes.put(SurveyTable.KEY_SURVEY_TYPE, currConfig.survey.getSurveyName());
        attributes.put(SurveyTable.KEY_SURVEY_GROUPED, currConfig.grouped);
        survey.setAttributes(attributes);
        survey.save();

        return survey;
    }

    private void setAlarms(Survey survey, boolean immediate) {
        Intent intent = new Intent(context, SurveyEventReceiver.class);
        intent.putExtra("survey_id", survey.id);
        intent.setAction(SurveyEventReceiver.SURVEY_SCHEDULED_INTENT);


        long notificationInterval = currConfig.maxElapseTimeForCompletion / currConfig.notificationsCount;

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        Calendar now = Calendar.getInstance();

        int notificationStart = 0;

        if(immediate) {
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) now.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                alarmIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            Log.d("DailyScheduler", "Scheduled immediate survey " + currConfig.survey.getSurveyName() + " notification at " + format.format(now.getTime()));

            notificationStart = 1;
        }

        long oldSchedule = survey.scheduledAt;
        if(notificationStart == 1) {
            Calendar startDay = Calendar.getInstance();
            startDay.set(Calendar.HOUR_OF_DAY, 7);
            startDay.set(Calendar.MINUTE, 0);
            startDay.set(Calendar.SECOND, 0);

            Calendar endDay = Calendar.getInstance();
            endDay.set(Calendar.HOUR_OF_DAY, 20);
            endDay.set(Calendar.MINUTE, 0);
            endDay.set(Calendar.SECOND, 0);

            if(survey.scheduledAt*1000 < startDay.getTimeInMillis() && survey.scheduledAt*1000 > endDay.getTimeInMillis()) {
                Random r = new Random();

                long newSchedule = r.nextInt((int) (endDay.getTimeInMillis() - startDay.getTimeInMillis()));
                survey.scheduledAt = newSchedule;

            }
        }

        for (int i = notificationStart; i < currConfig.notificationsCount; i++) {
            if(now.getTimeInMillis() <= (survey.scheduledAt*1000) + i * notificationInterval) {
                setAlarm(intent, survey.scheduledAt*1000 + i * notificationInterval);
                Log.d("DailyScheduler", "Scheduled survey " + currConfig.survey.getSurveyName() + " notification at " + format.format(survey.scheduledAt*1000 + i * notificationInterval));
            }
        }

        survey.scheduledAt = oldSchedule;
        Log.d("DailyScheduler", "-----------------------------");
    }

    private void setAlarm(Intent i, long time) {
        Calendar c = Calendar.getInstance();
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) c.getTimeInMillis(), i, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent);
    }

    private long[] getAlarmsTimes() {
        long[] alarmsTimes;

        if(currConfig.dailyTimes == null) {
            alarmsTimes  = new long[1];
            Calendar c = Calendar.getInstance();
//            c.add(Calendar.MINUTE, 2);
            alarmsTimes[0] = c.getTimeInMillis();

            return alarmsTimes;
        }


        alarmsTimes = new long[currConfig.surveysCount];

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

        for (int i = surveyStart; i < currConfig.surveysCount; i++) {
            timeStart = parseTime(currConfig.dailyTimes[i].first);
            timeEnd = parseTime(currConfig.dailyTimes[i].second);

            start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, timeStart[0]);
            start.set(Calendar.MINUTE, timeStart[1]);
            start.set(Calendar.SECOND, 0);

            end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, timeEnd[0]);
            end.set(Calendar.MINUTE, timeEnd[1]);
            end.set(Calendar.SECOND, 0);
            end.add(Calendar.MILLISECOND, (int) -currConfig.maxElapseTimeForCompletion);

            if(now.getTimeInMillis() > end.getTimeInMillis()) {
                alarmsTimes[i] = -1;
                if(currConfig.survey == SurveyType.GROUPED_SSPP) {
                    alarmsTimes[i] = now.getTimeInMillis();
                }
            } else {
                diff = end.getTimeInMillis() - start.getTimeInMillis();
                scheduleOffset = r.nextInt((int) diff);
                if (schedule != null) {
                    tempSchedule = Calendar.getInstance();
                    tempSchedule.setTimeInMillis(start.getTimeInMillis() + scheduleOffset);
                    while (Math.abs(tempSchedule.getTimeInMillis() - schedule.getTimeInMillis()) < currConfig.minElapseTimeBetweenSurveys) {
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
