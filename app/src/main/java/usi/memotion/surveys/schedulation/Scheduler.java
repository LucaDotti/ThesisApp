package usi.memotion.surveys.schedulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

import usi.memotion.MyApplication;
import usi.memotion.R;
import usi.memotion.surveys.config.SurveyType;
import usi.memotion.surveys.handle.SchedulerAlarmReceiver;
import usi.memotion.surveys.config.Days;
import usi.memotion.local.database.db.LocalTables;
import usi.memotion.local.database.tableHandlers.SurveyAlarmSurvey;
import usi.memotion.local.database.tableHandlers.SurveyAlarms;
import usi.memotion.local.database.tableHandlers.TableHandler;
import usi.memotion.local.database.tableHandlers.User;
import usi.memotion.local.database.tables.SurveyAlarmSurveyTable;
import usi.memotion.local.database.tables.SurveyAlarmsTable;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;


import usi.memotion.local.database.tableHandlers.SurveyConfig;
/**
 * Created by usi on 12/03/17.
 */

public class Scheduler {
    private static Scheduler instance;
    private int[] scheduleTime;
    private AlarmManager alarmMgr;
    private Context context;
    private SurveyConfig currConfig;
    private TableHandler[] currentAlarms;

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

        schedule(survey, true);
    }

    private SurveyAlarms insertSurveyAlarmsRecord(long ts, boolean current) {
        SurveyAlarms alarm = (SurveyAlarms) LocalTables.getTableHandler(LocalTables.TABLE_SURVEY_ALARMS);

        ContentValues attributes = new ContentValues();

        attributes.put("current", current);
        attributes.put("ts", ts);
        attributes.put("type", currConfig.surveyType.getSurveyName());

        alarm.setAttributes(attributes);
        alarm.save();
        Log.d("Scheduler", "\t Inserted " + alarm.type.toString() + alarm.toString());
        return alarm;
    }

    private void processInit() {
        if(currentAlarms.length == 0) {
            Log.d("Scheduler", "Init state: no " + currConfig.surveyType.getSurveyName() + " alarms found.");
            processSchedule();
        } else {
            checkAlarms();
        }
    }

    private void checkAlarms() {
        Log.d("Scheduler", "Checking existing " + currConfig.surveyType.getSurveyName() + " alarms.");
        SurveyAlarms alarm;
        for(TableHandler t: currentAlarms) {
            alarm = (SurveyAlarms) t;
            Log.d("Scheduler", "\t Checking " + currConfig.surveyType.getSurveyName() + " " + alarm.toString());

            if(!checkAlarmExists((int) alarm.id)) {
                Log.d("Scheduler", "\t" + currConfig.surveyType.getSurveyName() + " alarm does not exists " + alarm.toString());
                createAlarm((int) alarm.id, alarm.ts);
            }
        }
    }

    private void processSchedule() {
        Log.d("Scheduler", "Scheduling alarm " + currConfig.surveyType.getSurveyName());

        if(currentAlarms.length == 0) {
            processInitSchedule();
        } else if(currentAlarms.length == 1) {
            processScheduleNext();
        } else {
            Log.d("Scheduler", "\t Next alarm already set!");

            SurveyAlarms alarm;
            for(TableHandler t: currentAlarms) {
                alarm = (SurveyAlarms) t;
                Log.d("Scheduler", "\t\t" + alarm.toString());
            }

        }
    }

    private void processScheduleNext() {
        Log.d("Scheduler", "\t Scheduling next alarm");
        SurveyAlarms alarm = (SurveyAlarms) currentAlarms[0];
        long nextScheduleTime = getSurveyScheduleTime(true);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        if(alarm.current) {
            SurveyAlarms next = insertSurveyAlarmsRecord(nextScheduleTime, false);
            createAlarm((int) next.id, nextScheduleTime);

        }
    }

    private void processInitSchedule() {
        long currentScheduleTime = getSurveyScheduleTime(false);
//        Log.d("Scheduler debug", "" + currentScheduleTime);
        if(currConfig.immediate) {
            Calendar now = Calendar.getInstance();
            currentScheduleTime = now.getTimeInMillis();
        }

        SurveyAlarms current = insertSurveyAlarmsRecord(currentScheduleTime, true);

        if(currConfig.immediate) {
            createImmediateAlarm((int) current.id);
        } else {
            createAlarm((int) current.id, currentScheduleTime);
        }
    }

    public void schedule(SurveyType survey, boolean init) {
        currConfig = SurveyConfig.getConfig(survey);
        currentAlarms = SurveyAlarms.findAll("*", SurveyAlarmsTable.KEY_SURVEY_ALARM_SURVEY_TYPE + " = \"" + survey.getSurveyName() + "\" ORDER BY " + SurveyAlarmsTable.KEY_SURVEY_ALARM_ID + " ASC");

        if(init) {
            processInit();
        } else {
            processSchedule();
        }
    }

    private long getSurveyScheduleTime(boolean next) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, scheduleTime[0]);
        c.set(Calendar.MINUTE, scheduleTime[1]);
        c.set(Calendar.SECOND, 0);

        switch(currConfig.frequency.unit) {
            case MINUTE:
                c = Calendar.getInstance();
                if(next) {
                    c.add(Calendar.MINUTE, currConfig.frequency.multiplier);
                }
                return c.getTimeInMillis();
            case DAILY:
                if(next) {
                    c.add(Calendar.DAY_OF_MONTH, currConfig.frequency.multiplier);
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
                int surveyCount = currConfig.issuedSurveyCount;

                if(next) {
                    surveyCount++;
                }

                if(surveyCount <= currConfig.surveysCount) {
                    String startDate = User.getCreationDate();
                    Calendar start = Calendar.getInstance();
                    String[] split = startDate.split("-");

                    start.set(Calendar.HOUR_OF_DAY, 1);
                    start.set(Calendar.MINUTE, 0);
                    start.set(Calendar.SECOND, 0);
                    start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[0]));
                    start.set(Calendar.MONTH, Integer.parseInt(split[1])-1);


                    String endDate = User.getEndStudyDate();
                    Calendar end = Calendar.getInstance();
                    split = endDate.split("-");

                    end.set(Calendar.HOUR_OF_DAY, 1);
                    end.set(Calendar.MINUTE, 0);
                    end.set(Calendar.SECOND, 0);
                    end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[0]));
                    end.set(Calendar.MONTH, Integer.parseInt(split[1])-1);

                    long interval = (end.getTimeInMillis() - start.getTimeInMillis())/(currConfig.surveysCount-1);
                    int daysInterval = (int) (interval/(24 * 60 * 60 * 1000));

                    start.add(Calendar.DAY_OF_MONTH, surveyCount * daysInterval);
                    start.set(Calendar.HOUR_OF_DAY, scheduleTime[0]);
                    start.set(Calendar.MINUTE, scheduleTime[1]);
                    start.set(Calendar.SECOND, 0);
                    return start.getTimeInMillis();
                } else {
                    return -1;
                }
            case ONCE:
                return -1;
            default:
                throw new IllegalArgumentException("Unknown frequency");
        }
    }

    private void createImmediateAlarm(int id) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", currConfig.surveyType.getSurveyName());
        intent.putExtra("immediate", true);
        intent.putExtra("survey_id", id);
        intent.putExtra("alarm_id", id);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            alarmIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        Log.d("Scheduler", "\t Scheduled immediate " + currConfig.surveyType.getSurveyName());
    }

    private void createAlarm(int id, long scheduleTime) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", currConfig.surveyType.getSurveyName());
        intent.putExtra("alarm_id", id);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, scheduleTime, alarmIntent);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        Log.d("Scheduler", "\t Scheduled " + currConfig.surveyType.getSurveyName() + " scheduler at " + format.format(scheduleTime) + ", with alarm id " + id);
    }

    private boolean checkAlarmExists(int id) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", currConfig.surveyType.getSurveyName());
        intent.putExtra("alarm_id", id);

        PendingIntent pIntent = PendingIntent.getBroadcast(context, id,
                intent,
                PendingIntent.FLAG_NO_CREATE);
        return pIntent != null;
    }

    public void addSurvey(SurveyType survey) {
        surveys.put(survey, false);
    }

    public void deleteAlarm(int id) {
        SurveyAlarms alarm = (SurveyAlarms) SurveyAlarms.findByPk(id);

        Log.d("Scheduler", "Deleting " + alarm.type.getSurveyName() + " " + alarm.toString());
        //remove alarm
        alarm.delete();

        SurveyConfig config = SurveyConfig.getConfig(alarm.type);
        config.issuedSurveyCount++;
        config.save();

        //remove surveyAlarm_survey record
        SurveyAlarmSurvey[] records = SurveyAlarmSurvey.findAllByAttribute(SurveyAlarmSurveyTable.KEY_SURVEY_ALARMS_SURVEY_SURVEY_ALARMS_ID, Integer.toString(id));
        for(SurveyAlarmSurvey r: records) {
            r.delete();
        }


//        SurveyAlarms nextAlarm = (SurveyAlarms) SurveyAlarms.find("*", SurveyAlarmsTable.KEY_SURVEY_ALARM_SURVEY_TYPE + " = \"" + alarm.type + "\" ORDER BY " + SurveyAlarmsTable.KEY_SURVEY_ALARM_ID + " ASC");
        SurveyAlarms nextAlarm = (SurveyAlarms) SurveyAlarms.find("*", SurveyAlarmsTable.KEY_SURVEY_ALARM_SURVEY_TYPE + " = \"" + alarm.type.getSurveyName() + "\"");
        Log.d("Scheduler", "Setting current " + nextAlarm.toString());
        nextAlarm.current = true;
        nextAlarm.save();
    }

    public void removeCurrentAlarms() {
        TableHandler[] alarms = SurveyAlarms.findAll("*", "");

        SurveyAlarms s;
        for(TableHandler t: alarms) {
            s = (SurveyAlarms) t;
            cancelAlarm(s.id, s.type);
        }
    }

    private void cancelAlarm(long id, SurveyType survey) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", survey.getSurveyName());
        intent.putExtra("alarm_id", id);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(alarmIntent);
        alarmIntent.cancel();
    }
}


