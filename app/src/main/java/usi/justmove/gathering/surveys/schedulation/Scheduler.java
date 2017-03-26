package usi.justmove.gathering.surveys.schedulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import usi.justmove.MyApplication;
import usi.justmove.R;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.handle.SchedulerAlarmReceiver;
import usi.justmove.gathering.surveys.config.Days;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by usi on 12/03/17.
 */

public class Scheduler {
    public static final String SURVEY_SCHEDULED_ACTION = "survey_scheduled";
    public static final String WAKEUP_SURVEY_SCHEDULER_ACTION = "wake_up_survey_scheduler";
    private int[] scheduleTime;
    private AlarmManager alarmMgr;
    private Context context;

    private List<SurveyType> surveys;

    public Scheduler() {
        context = MyApplication.getContext();
        String schedule = context.getString(R.string.schedule_time);
        scheduleTime = parseTime(schedule);
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        surveys = new ArrayList<>();
    }

    private int[] parseTime(String time) {
        String[] split = time.split(":");
        int[] t = {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        return t;
    }

    public void initSchedulers() {
        for(SurveyType s: surveys) {
            initScheduler(s);
        }
    }

    private void initScheduler(SurveyType survey) {
        SurveyConfig config = SurveyConfigFactory.getConfig(survey, context);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, scheduleTime[0]);
        c.set(Calendar.MINUTE, scheduleTime[1]);
        c.set(Calendar.SECOND, 0);

        switch (config.frequency) {
            case DAILY:
                schedule(survey, c.getTimeInMillis());
                break;
            case WEEKLY:
                Days[] days = config.period.days;
                Random r = new Random();

                Days scheduleDay = days[r.nextInt(days.length)];
                c.set(Calendar.DAY_OF_WEEK, Days.toTimeConstants(scheduleDay));
                schedule(survey, c.getTimeInMillis());
                break;
            case ONCE:
                break;
        }
    }

    public void scheduleNext(SurveyType survey) {
        Log.d("Scheduler", "Next schedule:");
        SurveyConfig config = SurveyConfigFactory.getConfig(survey, context);
        Calendar c = Calendar.getInstance();
        switch(config.frequency) {
            case DAILY:
                c.set(Calendar.HOUR_OF_DAY, scheduleTime[0]);
                c.set(Calendar.MINUTE, scheduleTime[1]);
                c.set(Calendar.SECOND, 0);
                c.add(Calendar.DAY_OF_MONTH, 1);
                schedule(survey, c.getTimeInMillis());
                break;
            case WEEKLY:
                Days[] days = config.period.days;
                Random r = new Random();

                Days scheduleDay = days[r.nextInt(days.length)];

                c.set(Calendar.HOUR_OF_DAY, scheduleTime[0]);
                c.set(Calendar.MINUTE, scheduleTime[1]);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.DAY_OF_WEEK, Days.toTimeConstants(scheduleDay));
                c.add(Calendar.DAY_OF_MONTH, 7);
                schedule(survey, c.getTimeInMillis());
                break;
            case ONCE:
                break;
        }
    }

    public void schedule(SurveyType survey, long scheduleTime) {
        Intent intent = new Intent(context, SchedulerAlarmReceiver.class);
        intent.putExtra("survey", survey.getSurveyName());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, scheduleTime, alarmIntent);

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy  HH:mm:ss");
        Log.d("Scheduler", "Scheduled scheduler at " + format.format(scheduleTime));
    }

    public void addSurvey(SurveyType survey) {
        surveys.add(survey);
    }

}


