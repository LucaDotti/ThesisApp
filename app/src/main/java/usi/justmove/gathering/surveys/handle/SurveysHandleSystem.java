package usi.justmove.gathering.surveys.handle;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import usi.justmove.gathering.surveys.Surveys;
import usi.justmove.gathering.surveys.inspection.SurveyEvent;
import usi.justmove.gathering.surveys.inspection.SurveysInspectionSystem;

/**
 * Created by usi on 15/02/17.
 */

public class SurveysHandleSystem implements SurveysInspectionSystem.SurveyEventHandler {
    public final static String NOTIFICATION_INTENT = "notification_event";
    private Map<Surveys, SurveyHandler> handlers;
    private Context context;

    public SurveysHandleSystem(Context context) {
        handlers = new HashMap<>();
        this.context = context;
    }

    public void addHandler(Surveys survey) {
        switch(survey) {
            case PAM:
                handlers.put(survey, new PAMSurveyHandler(context));
                break;
            case PWB:
                handlers.put(survey, new PWBSurveyHandler(context));
                break;
            default:
                Log.d("ScheduleSystem", "Survey not found.");
        }

    }

    @Override
    public void onSurveyEvent(SurveyEvent event) {
        switch(event.getSurvey()) {
            case PAM:
                handlers.get(Surveys.PAM).handle(event);
                break;
            case PWB:
                handlers.get(Surveys.PWB).handle(event);
                break;
            default:
                Log.d("ScheduleSystem", "Survey not found.");
        }
    }
}
