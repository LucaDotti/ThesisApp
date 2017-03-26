package usi.justmove.gathering.surveys.handle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.schedulation.DailyScheduler;
import usi.justmove.gathering.surveys.schedulation.Scheduler;

/**
 * Created by usi on 14/03/17.
 */

public class SchedulerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Scheduler receiver", "Got schedule");
        DailyScheduler scheduler = new DailyScheduler();
        SurveyType survey = SurveyType.getSurvey(intent.getStringExtra("survey"));
        scheduler.schedule(survey);
        new Scheduler().scheduleNext(survey);
    }
}
