package usi.justmove.UI.views;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.UI.ExpandableLayout;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.handle.SurveyEventReceiver;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.tableHandlers.TableHandler;
import usi.justmove.local.database.tables.PHQ8Table;
import usi.justmove.local.database.tables.PSSTable;
import usi.justmove.local.database.tables.SHSTable;
import usi.justmove.local.database.tables.SWLSTable;

import static usi.justmove.R.array.surveys;

/**
 * Created by usi on 21/03/17.
 */

public class TermSurveyView extends LinearLayout implements SHSSurveyView.OnShsSurveyCompletedCallback, PSSSurveyView.OnPssSurveyCompletedCallback, PHQ8SurveyView.OnPhq8SurveyCompletedCallback, SWLSSurveyView.OnSwlsSurveyCompletedCallback{
    private OnTermSurveyCompletedCallback callback;

    private Context context;

    private View titleView;
    private LinearLayout questionsLayout;
    private ExpandableLayout expandableLayout;

    private SurveyType[] surveys;

    private SHSSurveyView shsView;
    private SWLSSurveyView swlsView;
    private PHQ8SurveyView phq8View;
    private PSSSurveyView pssView;

    private Survey currentSurvey;

    private boolean hasSurvey;

    public TermSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.term_survey_layout, this, true);

        expandableLayout = (ExpandableLayout) findViewById(R.id.termViewExpandableLayout);
        titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        questionsLayout = (LinearLayout) inflater.inflate(R.layout.term_questions_layout, null);

        init();
    }

    private void notifySurveyCompleted() {
        Intent intent = new Intent(context, SurveyEventReceiver.class);
        intent.putExtra("survey_id", currentSurvey.id);
        intent.setAction(SurveyEventReceiver.SURVEY_COMPLETED_INTENT);

        Calendar c = Calendar.getInstance();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) c.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        surveys = new SurveyType[4];
        surveys[0] = SurveyType.SWLS;
        surveys[1] = SurveyType.SHS;
        surveys[2] = SurveyType.PHQ8;
        surveys[3] = SurveyType.PSS;

        expandableLayout.getTitleView().removeAllViews();
        expandableLayout.getBodyView().removeAllViews();
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "Term survey");

        currentSurvey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);

        if(currentSurvey != null) {

            shsView = (SHSSurveyView) questionsLayout.findViewById(R.id.termSurvey_SHS);
            shsView.setVisibility(GONE);
            shsView.setCallback(this);
            swlsView = (SWLSSurveyView) questionsLayout.findViewById(R.id.termSurvey_SWLS);
            swlsView.setVisibility(GONE);
            swlsView.setCallback(this);
            pssView = (PSSSurveyView) questionsLayout.findViewById(R.id.termSurvey_PSS);
            pssView.setVisibility(GONE);
            pssView.setCallback(this);
            phq8View = (PHQ8SurveyView) questionsLayout.findViewById(R.id.termSurvey_PHQ8);
            phq8View.setVisibility(GONE);
            phq8View.setCallback(this);


            showSurveys(currentSurvey);
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
            expandableLayout.setBodyView(questionsLayout);
            expandableLayout.showBody();
            hasSurvey = true;
        } else {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No term survey available");
            expandableLayout.showNoContentMsg();
        }


    }

    private void showSurveys(Survey survey) {
        for(Map.Entry<SurveyType, TableHandler> entry: survey.getSurveys().entrySet()) {
            if(!entry.getValue().getAttributes().getAsBoolean("completed")) {
                switch (entry.getKey()) {
                    case SHS:
                        shsView.setVisibility(VISIBLE);
//                        shsView.expand();
                        break;
                    case SWLS:
                        swlsView.setVisibility(VISIBLE);
//                        swlsView.expand();
                        break;
                    case PHQ8:
                        phq8View.setVisibility(VISIBLE);
//                        phq8View.expand();
                        break;
                    case PSS:
                        pssView.setVisibility(VISIBLE);
//                        pssView.expand();
                        break;
                }

                break;
            }


        }
    }

    @Override
    public void onPhq8SurveyCompletedCallback() {
        processSurveyCompleted();
    }

    @Override
    public void onPssSurveyCompletedCallback() {
        processSurveyCompleted();
    }

    @Override
    public void onShsSurveyCompletedCallback() {
        processSurveyCompleted();
    }

    @Override
    public void onSwlsSurveyCompletedCallback() {
        processSurveyCompleted();
    }

    public interface OnTermSurveyCompletedCallback {
        void onTermSurveyCompleted();
    }

    private void printSurvey(Cursor c, String name) {
        c.moveToFirst();
        Log.d("SURVEY", name + " : completed -> " + c.getInt(2));
    }

    private void processSurveyCompleted() {
        Survey s = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);
        showSurveys(s);
        if(checkCompleted(s)) {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No term survey available");
            expandableLayout.showNoContentMsg();
            expandableLayout.collapse();
            callback.onTermSurveyCompleted();
            s.completed = true;
            s.ts = System.currentTimeMillis();
            s.save();

            notifySurveyCompleted();
            Toast.makeText(getContext(), "Term survey completed", Toast.LENGTH_SHORT).show();

            hasSurvey = false;
        }
    }

    private boolean checkCompleted(Survey s) {
        for(Map.Entry<SurveyType, TableHandler> childSurvey: s.getSurveys().entrySet()) {
            if(!childSurvey.getValue().getAttributes().getAsBoolean("completed")) {
                return false;
            }
        }

        return true;
    }

    public void setCallback(OnTermSurveyCompletedCallback callback) {
        this.callback = callback;
    }

    public boolean hasSurvey() {
        return hasSurvey;
    }

    public void reInit() {
        init();
        shsView.reInit();
        swlsView.reInit();
        phq8View.reInit();
        pssView.reInit();
    }

    public void blink() {
        expandableLayout.startBlink();
    }

    public void stopBlink() {
        expandableLayout.stopBlink();
        expandableLayout.getTitleView().setAlpha(1);
//        expandableLayout.getTitleView().findViewById(R.id.surveysTitle).setAlpha(1);
    }
}
