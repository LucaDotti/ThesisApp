package usi.justmove.gathering.surveys.handle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import usi.justmove.gathering.surveys.config.Frequency;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.schedulation.DailyScheduler;
import usi.justmove.gathering.surveys.schedulation.Scheduler;
import usi.justmove.local.database.tableHandlers.SurveyAlarms;
import usi.justmove.local.database.tables.SurveyAlarmsTable;

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


        boolean immediate = intent.getBooleanExtra("immediate", false);

        dailyScheduler.schedule(survey, immediate);
        scheduler.schedule(survey);
    }
}
