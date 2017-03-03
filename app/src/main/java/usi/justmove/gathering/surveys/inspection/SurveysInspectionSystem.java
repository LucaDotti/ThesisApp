package usi.justmove.gathering.surveys.inspection;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.gathering.surveys.Surveys;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalTables;

/**
 * Created by usi on 15/02/17.
 */

public class SurveysInspectionSystem implements Runnable {
    private Map<Surveys, SurveyInspector> inspectors;
    private List<SurveyEventHandler> handlers;
    private long frequency;
    private boolean running;
    private Context context;

    public SurveysInspectionSystem(long frequency, Context context) {
        inspectors = new HashMap<>();
        handlers = new ArrayList<>();
        running = true;
        this.frequency = frequency;
        this.context = context;
    }

    public void addInspector(Surveys survey) {
        switch(survey) {
            case PAM:
                inspectors.put(Surveys.PAM, new PAMSurveyInspector(LocalTables.TABLE_PAM, SQLiteController.getInstance(context), context));
                break;
            case PWB:
                inspectors.put(Surveys.PWB, new PWBSurveyInspector(LocalTables.TABLE_PWB, SQLiteController.getInstance(context), context));
                break;
            default:
                Log.d("ScheduleSystem", "Survey not found.");
        }
    }

    public void addHandler(SurveyEventHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void run() {
        SurveyEvent event;
        while(running) {
            for(Map.Entry<Surveys, SurveyInspector> entry: inspectors.entrySet()) {
                event = entry.getValue().inspect();

                if(event != null) {
                    if(handlers.size() > 0) {
                        for(SurveyEventHandler handler: handlers) {
                            handler.onSurveyEvent(event);
                        }
                    }
                }
            }

            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public interface SurveyEventHandler {
        void onSurveyEvent(SurveyEvent event);
    }

    public void stop() {
        running = false;
    }
}
