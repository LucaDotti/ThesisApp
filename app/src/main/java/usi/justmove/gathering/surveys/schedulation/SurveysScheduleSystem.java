package usi.justmove.gathering.surveys.schedulation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.text.SimpleDateFormat;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;

import usi.justmove.gathering.surveys.config.Frequency;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.Surveys;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by usi on 07/02/17.
 */
public class SurveysScheduleSystem {
    private final static String SURVEY_SCHEDULE_ACTION = "survey_schedule_action";
    private List<Surveys> surveys;
    private AlarmManager alarmMgr;
    private Context context;
    private boolean debug;


    public SurveysScheduleSystem(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        surveys = new ArrayList<>();
        this.context = context;
        debug = true;
    }

    public void start() {
        Intent intent;
        PendingIntent alarmIntent;

        for(Surveys survey: surveys) {
            intent = new Intent(context, SurveyScheduleActionReceiver.class);
            intent.setAction(SURVEY_SCHEDULE_ACTION);
            intent.putExtra("survey", survey.ordinal());
            alarmIntent = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, 0);

            SurveyConfig config = SurveyConfigFactory.getConfig(survey, context);
            int[] time = parseTime(config.scheduleTime);

//            DateTime scheduleTime = new DateTime().withTime(time[0], time[1], 0, 0).plusHours(1);
//            long m = scheduleTime.getMillis();

            if(!debug) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, time[0]);
                calendar.set(Calendar.MINUTE, time[1]);
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Frequency.getMilliseconds(config.frequency), alarmIntent);
                Log.d("ScheduleSystem", survey + "alarm scheduled at " + format1.format(calendar.getTime()));
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Frequency.getMilliseconds(config.frequency), alarmIntent);
                Log.d("ScheduleSystem", survey + "alarm scheduled at " + format1.format(calendar.getTime()));
            }

        }
    }

    private int[] parseTime(String time) {
        String[] split = time.split(":");
        int[] timeInt = {Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        return timeInt;
    }

    public void addScheduler(Surveys survey) {
        switch(survey) {
            case PAM:
                surveys.add(Surveys.PAM);
                break;
            case PWB:
                surveys.add(Surveys.PWB);
                break;
            default:
                Log.d("ScheduleSystem", "Survey not found.");
                return;
        }
    }

    public static class SurveyScheduleActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ScheduleSystem", "Got alarm intent");
            int surveyExtra = intent.getIntExtra("survey", -1);
            if(surveyExtra >= 0) {
                Surveys survey = Surveys.getSurvey(surveyExtra);
                SurveyScheduler scheduler;

                switch(survey) {
                    case PAM:
                        scheduler = new PAMSurveyScheduler(SurveyConfigFactory.getConfig(survey, context), LocalTables.TABLE_PAM, SQLiteController.getInstance(context));
                        break;
                    case PWB:
                        scheduler = new PWBSurveyScheduler(SurveyConfigFactory.getConfig(survey, context), LocalTables.TABLE_PWB, SQLiteController.getInstance(context));
                        break;
                    default:
                        throw new IllegalArgumentException("Survey not found.");
                }
                scheduler.schedule();
            }
        }
    }
}


