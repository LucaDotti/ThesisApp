package usi.justmove.gathering.surveys;

import android.content.Context;

import usi.justmove.gathering.surveys.handle.SurveysHandleSystem;
import usi.justmove.gathering.surveys.inspection.SurveysInspectionSystem;
import usi.justmove.gathering.surveys.schedulation.SurveysScheduleSystem;

/**
 * Created by usi on 15/02/17.
 */

public class SurveysSystem {
    private SurveysScheduleSystem scheduleSystem;
    private SurveysInspectionSystem inspectionSystem;
    private SurveysHandleSystem handleSystem;

    private Thread inspectionSystemThread;

    public SurveysSystem(Context context) {
        scheduleSystem = new SurveysScheduleSystem(context);
        inspectionSystem = new SurveysInspectionSystem(1000, context);
        handleSystem = new SurveysHandleSystem(context);
        inspectionSystem.addHandler(handleSystem);
    }

    public void start() {
        scheduleSystem.start();
        inspectionSystemThread = new Thread(inspectionSystem);
        inspectionSystemThread.start();
    }

    public void addSurvey(Surveys survey) {
        scheduleSystem.addScheduler(survey);
        inspectionSystem.addInspector(survey);
        handleSystem.addHandler(survey);
    }
}
