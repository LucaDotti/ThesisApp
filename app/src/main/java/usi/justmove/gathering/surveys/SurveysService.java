package usi.justmove.gathering.surveys;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import usi.justmove.MainActivity;
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
        Log.d("START", "SURVEY " + MainActivity.running);
//        if(checkActivityIsRunning()) {
//
//        }
        scheduler = Scheduler.getInstance();
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
