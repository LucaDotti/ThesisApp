package usi.memotion.surveys.handle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import usi.memotion.surveys.config.SurveyType;
import usi.memotion.surveys.schedulation.DailyScheduler;
import usi.memotion.surveys.schedulation.Scheduler;

/**
 * Created by usi on 14/03/17.
 */

public class SchedulerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Scheduler receiver", "Got schedule");

        DailyScheduler dailyScheduler = new DailyScheduler();
        Scheduler scheduler = Scheduler.getInstance();

        SurveyType survey = SurveyType.getSurvey(intent.getStringExtra("survey"));

        dailyScheduler.schedule(intent);
        scheduler.schedule(survey, false);
    }
}
