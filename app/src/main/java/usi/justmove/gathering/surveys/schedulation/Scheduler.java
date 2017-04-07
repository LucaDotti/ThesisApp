package usi.justmove.gathering.surveys.schedulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import usi.justmove.MyApplication;
import usi.justmove.R;
import usi.justmove.gathering.surveys.config.Frequency;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.handle.SchedulerAlarmReceiver;
import usi.justmove.gathering.surveys.config.Days;
import usi.justmove.gathering.surveys.handle.SurveyEventReceiver;
import usi.justmove.local.database.LocalTables;
import usi.justmove.local.database.tableHandlers.PAMSurvey;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.tableHandlers.SurveyAlarms;
import usi.justmove.local.database.tableHandlers.TableHandler;
import usi.justmove.local.database.tables.SurveyAlarmsTable;
import usi.justmove.local.database.tables.SurveyTable;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;

import static android.R.attr.id;
import static usi.justmove.gathering.surveys.config.Frequency.DAILY;
import static usi.justmove.gathering.surveys.config.Frequency.ONCE;
import static usi.justmove.gathering.surveys.config.Frequency.WEEKLY;

/**
 * Created by usi on 12/03/17.
 */

public class Scheduler {
    private static Scheduler instance;
    public static final String SURVEY_SCHEDULED_ACTION = "survey_scheduled";
    public static final String WAKEUP_SURVEY_SCHEDULER_ACTION = "wake_up_survey_scheduler";
    private int[] scheduleTime;
    private AlarmManager alarmMgr;
    private Context context;
    private SurveyConfig currConfig;
    private int currAlarmsCount;
    public String test;
    private boolean init;

    private Map<SurveyType, Boolean> surveys;

    public static Scheduler getInstance() {
        if(instance == null) {
            instance = new Scheduler();
        }

        return instance;
    }


    private Scheduler() {
        context = MyApplication.getContext();
        String schedule = context.getString(R.string.schedule_time);
        scheduleTime = parseTime(schedule);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        surveys = new HashMap<>();
    }

    private int[] parseTime(String time) {
        String[] split = time.split(":");
        int[] t = {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        return t;
    }

    public void initSchedulers() {
        Log.d("SCHEDULER", "COUNT " + surveys.size());
        for(Map.Entry<SurveyType, Boolean> entry: surveys.entrySet()) {
            if(!entry.getValue()) {
                surveys.put(entry.getKey(), true);
                initScheduler(entry.getKey());
            }

        }
    }

    public void initScheduler(SurveyType survey) {
        if(!surveys.containsKey(survey)) {
            surveys.put(survey, true);
        }

        schedule(survey);
    }

    private SurveyAlarms insertSurveyAlarmsRecord(SurveyType survey, long ts, boolean current) {
//        Log.d("Alarm", "Inserting " + survey.getSurveyName());
        SurveyAlarms alarm = (SurveyAlarms) LocalTables.getTableHandler(LocalTables.TABLE_SURVEY_ALARMS);

        ContentValues attributes = new ContentValues();

        attributes.put("current", current);
        attributes.put("ts", ts);
        attributes.put("type", survey.getSurveyName());

        alarm.setAttributes(attributes);
        alarm.save();

        return alarm;
    }

    public void schedule(SurveyType survey) {
        currConfig = SurveyConfigFactory.getConfig(survey, context);
        TableHandler[] alarms = SurveyAlarms.findAll("*", SurveyAlarmsTable.KEY_SURVEY_ALARM_SURVEY_TYPE + " = \"" + survey.getSurveyName() + "\" ORDER BY " + SurveyAlarmsTable.KEY_SURVEY_ALARM_ID + " ASC");

        currAlarmsCount = alarms.length;

        Calendar scheduleTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        if(alarms.length == 0) {
            long currentScheduleTime = getSurveyScheduleTime(false);
            if(currConfig.immediate) {
                currentScheduleTime = now.getTimeInMillis();
            }
            SurveyAlarms current = insertSurveyAlarmsRecord(survey, currentScheduleTime, true);

//            long nextScheduleTime = getSurveyScheduleTime(true);
//            SurveyAlarms next = insertSurveyAlarmsRecord(survey, nextScheduleTime, false);


            if(currConfig.immediate) {
                createImmediateAlarm((int) current.id);
            } else {
                createAlarm((int) current.id, current.ts);
            }

//            createAlarm((int) next.id, next.ts);
        } else if (alarms.length == 1) {
            long nextScheduleTime = getSurveyScheduleTime(true);

            SurveyAlarms next = insertSurveyAlarmsRecord(survey, nextScheduleTime, false);

            createAlarm((int) next.id, next.ts);
        } else {
            SurveyAlarms alarm;
            int i = 0;
            for(TableHandler a: alarms) {
                alarm = (SurveyAlarms) a;
                scheduleTime.setTimeInMillis(alarm.ts * 1000);

                if(now.getTimeInMillis() <= scheduleTime.getTimeInMillis() + currConfig.maxElapseTimeForCompletion) {
                    Log.d("Alarm", "Checking alarm with id " + alarm.id + " " + alarm.type);
                    if(!checkAlarmExists((int) alarm.id)) {
                        if(i == 0 && currConfig.immediate) {
                            createImmediateAlarm((int) alarm.id);
                        } else {
                            createAlarm((int) alarm.id, alarm.ts);
                        }

                    }
                } else {
                    alarm.delete();
                }

                i++;
            }
        }
    }

    private long getSurveyScheduleTime(boolean next) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, scheduleTime[0]);
        c.set(Calendar.MINUTE, scheduleTime[1]);
        c.set(Calendar.SECOND, 0);

        switch(currConfig.frequency) {
            case DAILY:
                if(next) {
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }
                return c.getTimeInMillis();
            case WEEKLY:
                Days[] days = currConfig.period.days;
                Random r = new Random();
                c.set(Calendar.DAY_OF_WEEK, Days.toTimeConstants(days[r.nextInt(days.length)]));

                if(next) {
                    c.add(Calendar.DAY_OF_MONTH, 7);
                }

                return c.getTimeInMillis();
            case FIXED_DATES:
                int surveyCount = Survey.getDoneSurveysCount(SurveyType.GROUPED_SSPP);

                if(next) {
                    surveyCount++;
                }

                String startDate = currConfig.startStudy;
                Calendar start = Calendar.getInstance();
                String[] split = startDate.split("-");

                start.set(Calendar.HOUR_OF_DAY, 1);
                start.set(Calendar.MINUTE, 0);
                start.set(Calendar.SECOND, 0);
                start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[0]));
                start.set(Calendar.MONTH, Integer.parseInt(split[1])-1);


                String endDate = currConfig.endStudy;
                Calendar end = Calendar.getInstance();
                split = endDate.split("-");

                end.set(Calendar.HOUR_OF_DAY, 1);
                end.set(Calendar.MINUTE, 0);
                end.set(Calendar.SECOND, 0);
                end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[0]));
                end.set(Calendar.MONTH, Integer.parseInt(split[1])-1);

                long interval = (end.getTimeInMillis() - start.getTimeInMillis())/(currConfig.dayCount-1);
                int daysInterval = (int) (interval/(24 * 60 * 60 * 1000));

                start.add(Calendar.DAY_OF_MONTH, surveyCount * daysInterval);
                start.set(Calendar.HOUR_OF_DAY, scheduleTime[0]);
                start.set(Calendar.MINUTE, scheduleTime[1]);
                start.set(Calendar.SECOND, 0);
                return start.getTimeInMillis();
            case ONCE:
                return -1;
            default:
                throw new IllegalArgumentException("Unknown frequency");
        }
    }

    private void createImmediateAlarm(int id) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", currConfig.survey.getSurveyName());
        intent.putExtra("immediate", true);
        intent.putExtra("survey_id", id);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        try {
            alarmIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        Log.d("Scheduler", "Scheduled immediate " + currConfig.survey.getSurveyName());
    }

    private void createAlarm(int id, long scheduleTime) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", currConfig.survey.getSurveyName());
        intent.putExtra("alarm_id", id);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, scheduleTime, alarmIntent);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        Log.d("Scheduler", "Scheduled " + currConfig.survey.getSurveyName() + " scheduler at " + format.format(scheduleTime) + ", with alarm id " + id);
    }

    private boolean checkAlarmExists(int id) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", currConfig.survey.getSurveyName());

        return PendingIntent.getBroadcast(context, id,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null;
    }

    public void addSurvey(SurveyType survey) {
        surveys.put(survey, false);
    }

    public void deleteAlarm(int id) {
        TableHandler[] alarms = SurveyAlarms.findAll("*", "");

        for(TableHandler handler: alarms) {
            Log.d("SCHEDULER", handler.toString());
        }

        SurveyAlarms alarm = (SurveyAlarms) SurveyAlarms.findByPk(id);

        alarm.delete();

        SurveyAlarms nextAlarm = (SurveyAlarms) SurveyAlarms.find("*", "");

        if(nextAlarm != null) {
            nextAlarm.current = true;
            nextAlarm.save();
            Log.d("ALARM COUNT", "" + SurveyAlarms.getCount(SurveyType.PAM));
        }
    }
}


