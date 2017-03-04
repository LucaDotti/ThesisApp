package usi.justmove.gathering.surveys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import usi.justmove.R;
import usi.justmove.gathering.surveys.config.Frequency;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.inspection.SurveyEvent;

/**
 * Created by usi on 06/02/17.
 */

public class SurveysService extends Service {
    private final static String NOTIFICATION_INTENT = "notification";

    SurveysSystem surveysSystem;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("SURVEYS SERVICE", "STARTED");

        surveysSystem = new SurveysSystem(getApplicationContext());
        SurveyConfig pamConfig = new SurveyConfig();
        pamConfig.survey = Surveys.PAM;
        pamConfig.frequency = Frequency.getFrequency(getApplicationContext().getString(R.string.PAM_frequency));
        pamConfig.surveysCount = Integer.parseInt(getApplicationContext().getString(R.string.PAM_count));
        pamConfig.startTime = getApplicationContext().getString(R.string.PAM_start);
        pamConfig.scheduleTime = getApplicationContext().getString(R.string.PAM_schedule_time);
        String[] pamMidTimes = {getApplicationContext().getString(R.string.PAM_afternoon)};
        pamConfig.midTimes = pamMidTimes;
        pamConfig.endTime = getApplicationContext().getString(R.string.PAM_end);
        pamConfig.maxElapseTimeForCompletion = Long.parseLong(getApplicationContext().getString(R.string.PAM_max_elapse_time_for_completition));
        pamConfig.minElapseTimeBetweenSurveys = Long.parseLong(getApplicationContext().getString(R.string.PAM_min_elapse_time_between_surveys));
        pamConfig.notificationsCount = Integer.parseInt(getApplicationContext().getString(R.string.PAM_notifications_count));

        SurveyConfig pwbConfig = new SurveyConfig();
        pwbConfig.survey = Surveys.PWB;
        pwbConfig.frequency = Frequency.getFrequency(getApplicationContext().getString(R.string.PWB_frequency));
        pwbConfig.surveysCount = Integer.parseInt(getApplicationContext().getString(R.string.PWB_count));
        pwbConfig.startTime = getApplicationContext().getString(R.string.PWB_start);
        pwbConfig.endTime = getApplicationContext().getString(R.string.PWB_end);
        pwbConfig.maxElapseTimeForCompletion = Long.parseLong(getApplicationContext().getString(R.string.PWB_max_elapse_time_for_completition));
        pwbConfig.notificationsCount = Integer.parseInt(getApplicationContext().getString(R.string.PWB_notifications_count));

        surveysSystem.addSurvey(Surveys.PAM);
        surveysSystem.addSurvey(Surveys.PWB);
        surveysSystem.start();

//        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(SurveyEvent event) {
//
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}