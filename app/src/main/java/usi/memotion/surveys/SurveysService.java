package usi.memotion.surveys;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import usi.memotion.MainActivity;
import usi.memotion.surveys.config.SurveyType;
import usi.memotion.surveys.schedulation.DailyScheduler;
import usi.memotion.surveys.schedulation.Scheduler;

/**
 * Created by usi on 06/02/17.
 */

public class SurveysService extends Service {
    Scheduler scheduler;

    @Override
    public void onCreate() {
        super.onCreate();

        scheduler = Scheduler.getInstance();
        scheduler.addSurvey(SurveyType.PAM);
        scheduler.addSurvey(SurveyType.PWB);
        scheduler.addSurvey(SurveyType.GROUPED_SSPP);

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
        scheduler.removeCurrentAlarms();
        new DailyScheduler().removeCurrentAlarms();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
