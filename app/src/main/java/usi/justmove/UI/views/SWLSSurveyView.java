package usi.justmove.UI.views;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Calendar;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.UI.ExpandableLayout;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.handle.SurveyEventReceiver;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.tableHandlers.TableHandler;
import usi.justmove.local.database.tables.PWBTable;
import usi.justmove.local.database.tables.SHSTable;
import usi.justmove.local.database.tables.SWLSTable;

/**
 * Created by usi on 07/03/17.
 */

public class SWLSSurveyView extends LinearLayout{
    private OnSwlsSurveyCompletedCallback callback;

    private Context context;

    private View titleView;
    private LinearLayout questionsLayout;
    private ExpandableLayout expandableLayout;

    private SeekBar q1Seekbar;
    private SeekBar q2Seekbar;
    private SeekBar q3Seekbar;
    private SeekBar q4Seekbar;
    private SeekBar q5Seekbar;
    private Button submiButton;

    private Survey currentSurvey;

    public SWLSSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.swls_layout, this, true);

        expandableLayout = (ExpandableLayout) findViewById(R.id.swlsViewExpandableLayout);
        titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        questionsLayout = (LinearLayout) inflater.inflate(R.layout.swls_questions_layout, null);

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

    private Survey getCurrentSurvey() {
        Survey survey = Survey.getAvailableSurvey(SurveyType.SWLS);

        if(survey != null) {
            return survey;
        }

        survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);

        if(survey != null) {
            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);

            if(children.containsKey(SurveyType.SWLS)) {
                return survey;
            }
        }

        return null;
    }

    private void init() {
        expandableLayout.getBodyView().removeAllViews();
        expandableLayout.getTitleView().removeAllViews();
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "SWLS");

        currentSurvey = getCurrentSurvey();

        if(currentSurvey != null) {
            q1Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysSwlsQ1SeekBar);
            q1Seekbar.setMax(6);
            q2Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysSwlsQ2SeekBar);
            q2Seekbar.setMax(6);
            q3Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysSwlsQ3SeekBar);
            q3Seekbar.setMax(6);
            q4Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysSwlsQ4SeekBar);
            q4Seekbar.setMax(6);
            q5Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysSwlsQ5SeekBar);
            q5Seekbar.setMax(6);

            submiButton = (Button) questionsLayout.findViewById(R.id.swlsSubmitButton);

            submiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveSwlsSurvey();
                    expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
                    expandableLayout.setNoContentMsg("No SWLS survey available");
                    expandableLayout.showNoContentMsg();
                    expandableLayout.collapse();
                    callback.onSwlsSurveyCompletedCallback();

                    if(!currentSurvey.grouped) {
                        notifySurveyCompleted();
                    }

                    Toast.makeText(getContext(), "SWLS survey completed", Toast.LENGTH_SHORT).show();
                }
            });

            expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
            expandableLayout.setBodyView(questionsLayout);
            expandableLayout.showBody();
//            hasSurvey = true;
        } else {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No SWLS survey available");
            expandableLayout.showNoContentMsg();
        }
    }

    private void saveSwlsSurvey() {
        ContentValues attributes = new ContentValues();
        attributes.put(SWLSTable.KEY_SWLS_PARENT_SURVEY_ID, currentSurvey.id);
        attributes.put(SWLSTable.KEY_SWLS_COMPLETED, true);
        attributes.put(SWLSTable.KEY_SWLS_Q1, q1Seekbar.getProgress());
        attributes.put(SWLSTable.KEY_SWLS_Q2, q2Seekbar.getProgress());
        attributes.put(SWLSTable.KEY_SWLS_Q3, q3Seekbar.getProgress());
        attributes.put(SWLSTable.KEY_SWLS_Q4, q4Seekbar.getProgress());
        attributes.put(SWLSTable.KEY_SWLS_Q5, q5Seekbar.getProgress());

        Survey survey = (Survey) Survey.findByPk(currentSurvey.id);
        survey.getSurveys().get(SurveyType.SWLS).setAttributes(attributes);

        if(!survey.grouped) {
            survey.completed = true;
            survey.ts = System.currentTimeMillis();
        }

        survey.save();
    }

    public interface OnSwlsSurveyCompletedCallback {
        void onSwlsSurveyCompletedCallback();
    }

    public void setCallback(OnSwlsSurveyCompletedCallback callback) {
        this.callback = callback;
    }

    public void expand() {
        expand();
        expandableLayout.showBody();
    }

    public void reInit() {
        init();
    }
}
