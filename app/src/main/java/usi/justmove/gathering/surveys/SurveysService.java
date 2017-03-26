package usi.justmove.gathering.surveys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.schedulation.Scheduler;

/**
 * Created by usi on 06/02/17.
 */

public class SurveysService extends Service {
    Scheduler scheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        scheduler = new Scheduler();
        scheduler.addSurvey(SurveyType.PAM);
//        scheduler.addSurvey(SurveyType.PWB);
//        scheduler.addSurvey(SurveyType.GROUPED_SSPP);
        scheduler.initSchedulers();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}