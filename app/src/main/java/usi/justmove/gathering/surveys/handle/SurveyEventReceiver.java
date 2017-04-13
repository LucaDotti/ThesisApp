package usi.justmove.gathering.surveys.handle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by usi on 12/03/17.
 */

public class SurveyEventReceiver extends BroadcastReceiver {
    public final static String SURVEY_NOTIFICATION_INTENT = "survey_notification_intent";
    public final static String SURVEY_EXPIRED_INTENT = "survey_expired_intent";
    public final static String SURVEY_COMPLETED_INTENT = "survey_completed_intent";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SurveyEventReceiver", "Got event");
        Log.d("SurveyEventReceiver", intent.getAction());
        long surveyId = intent.getLongExtra("survey_id", -1);
        new SurveyNotifier().notify(surveyId, intent.getAction());
    }
}
