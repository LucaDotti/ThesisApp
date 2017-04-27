package usi.memotion.UI.views;

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

import java.util.Calendar;
import java.util.Map;

import usi.memotion.R;
import usi.memotion.surveys.config.SurveyType;
import usi.memotion.surveys.handle.SurveyEventReceiver;
import usi.memotion.local.database.tableHandlers.Survey;
import usi.memotion.local.database.tableHandlers.TableHandler;
import usi.memotion.local.database.tables.PHQ8Table;

/**
 * Created by usi on 07/03/17.
 */

public class PHQ8SurveyView extends LinearLayout {
    private OnPhq8SurveyCompletedCallback callback;

    private Context context;

    private View titleView;
    private LinearLayout questionsLayout;
    private ExpandableLayout expandableLayout;

    private SeekBar q1Seekbar;
    private SeekBar q2Seekbar;
    private SeekBar q3Seekbar;
    private SeekBar q4Seekbar;
    private SeekBar q5Seekbar;
    private SeekBar q6Seekbar;
    private SeekBar q7Seekbar;
    private SeekBar q8Seekbar;
    private Button submiButton;

    private Survey currentSurvey;

    public PHQ8SurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.phq8_layout, this, true);

        expandableLayout = (ExpandableLayout) findViewById(R.id.phq8ViewExpandableLayout);
        titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        questionsLayout = (LinearLayout) inflater.inflate(R.layout.phq8_questions_layout, null);

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
        Survey survey = Survey.getAvailableSurvey(SurveyType.PHQ8);

        if(survey != null) {
            return survey;
        }

        survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);

        if(survey != null) {
            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);

            if(children.containsKey(SurveyType.PHQ8)) {
                return survey;
            }
        }

        return null;
    }

    private void init() {
        expandableLayout.getBodyView().removeAllViews();
        expandableLayout.getTitleView().removeAllViews();
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "Personal Health Questionnaire Depression Scale");

        currentSurvey = getCurrentSurvey();


        if(currentSurvey != null) {
            q1Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q1SeekBar);
            q1Seekbar.setMax(3);
            q2Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q2SeekBar);
            q2Seekbar.setMax(3);
            q3Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q3SeekBar);
            q3Seekbar.setMax(3);
            q4Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q4SeekBar);
            q4Seekbar.setMax(3);
            q5Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q5SeekBar);
            q5Seekbar.setMax(3);
            q6Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q6SeekBar);
            q6Seekbar.setMax(3);
            q7Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q7SeekBar);
            q7Seekbar.setMax(3);
            q8Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPhq8Q8SeekBar);
            q8Seekbar.setMax(3);

            submiButton = (Button) questionsLayout.findViewById(R.id.phq8SubmitButton);

            submiButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePhq8Survey();
                    expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
                    expandableLayout.setNoContentMsg("No PHQ8 survey available");
                    expandableLayout.showNoContentMsg();
                    expandableLayout.collapse();
                    callback.onPhq8SurveyCompletedCallback();

                    if(!currentSurvey.grouped) {
                        notifySurveyCompleted();
                    }

                    Toast.makeText(getContext(), "PHQ8 survey completed", Toast.LENGTH_SHORT).show();
                }
            });

            expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
            expandableLayout.setBodyView(questionsLayout);
            expandableLayout.showBody();


//            hasSurvey = true;
        } else {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No PHQ8 survey available");
            expandableLayout.showNoContentMsg();
        }
    }

    private void savePhq8Survey() {
        ContentValues attributes = new ContentValues();
        attributes.put(PHQ8Table.KEY_PHQ8_PARENT_SURVEY_ID, currentSurvey.id);
        attributes.put(PHQ8Table.KEY_PHQ8_COMPLETED, true);
        attributes.put(PHQ8Table.KEY_PHQ8_Q1, q1Seekbar.getProgress());
        attributes.put(PHQ8Table.KEY_PHQ8_Q2, q2Seekbar.getProgress());
        attributes.put(PHQ8Table.KEY_PHQ8_Q3, q3Seekbar.getProgress());
        attributes.put(PHQ8Table.KEY_PHQ8_Q4, q4Seekbar.getProgress());
        attributes.put(PHQ8Table.KEY_PHQ8_Q5, q5Seekbar.getProgress());
        attributes.put(PHQ8Table.KEY_PHQ8_Q6, q6Seekbar.getProgress());
        attributes.put(PHQ8Table.KEY_PHQ8_Q7, q7Seekbar.getProgress());
        attributes.put(PHQ8Table.KEY_PHQ8_Q8, q8Seekbar.getProgress());

        Survey survey = (Survey) Survey.findByPk(currentSurvey.id);
        survey.getSurveys().get(SurveyType.PHQ8).setAttributes(attributes);


        if(!currentSurvey.grouped) {
            survey.completed = true;
            survey.ts = System.currentTimeMillis();
        }

        survey.save();
    }

    public interface OnPhq8SurveyCompletedCallback {
        void onPhq8SurveyCompletedCallback();
    }

    public void setCallback(OnPhq8SurveyCompletedCallback callback) {
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
