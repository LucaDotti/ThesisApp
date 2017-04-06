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

/**
 * Created by usi on 20/02/17.
 */

public class PWBSurveyView extends LinearLayout {
    private OnPwbSurveyCompletedCallback callback;

    private Context context;

    private Survey currentSurvey;

    private LinearLayout questionsLayout;
    private ExpandableLayout expandableLayout;
    private View titleView;

    private DiscreteSeekBar q1Seekbar;
    private DiscreteSeekBar q2Seekbar;
    private DiscreteSeekBar q3Seekbar;
    private DiscreteSeekBar q4Seekbar;
    private DiscreteSeekBar q5Seekbar;
    private DiscreteSeekBar q6Seekbar;
    private DiscreteSeekBar q7Seekbar;
    private DiscreteSeekBar q8Seekbar;
    private Button submitButton;

    private boolean hasSurvey;

    public PWBSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pwb_layout, this, true);
        expandableLayout = (ExpandableLayout) findViewById(R.id.pwbViewExpandableLayout);
        titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        questionsLayout = (LinearLayout) inflater.inflate(R.layout.pwb_questions_layout, null);

        this.context = context;

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
        Survey survey = Survey.getAvailableSurvey(SurveyType.PWB);

        if(survey != null) {
            return survey;
        }

        survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);

        if(survey != null) {
            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);

            if(children.containsKey(SurveyType.PWB)) {
                return survey;
            }
        }

        return null;
    }

    private void init() {
        expandableLayout.getTitleView().removeAllViews();
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "PWB");

        currentSurvey = getCurrentSurvey();

        if(currentSurvey != null) {
            initQuestions();

            submitButton = (Button) questionsLayout.findViewById(R.id.pwbSubmitButton);
            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePwbSurvey();
                    expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
                    expandableLayout.setNoContentMsg("No PWB survey available");
                    expandableLayout.showNoContentMsg();
                    expandableLayout.collapse();
                    callback.onPwbSurveyCompletedCallback();

                    if(!currentSurvey.grouped) {
                        notifySurveyCompleted();
                    }

                    Toast.makeText(getContext(), "PWB survey completed", Toast.LENGTH_SHORT).show();
                }
            });

            expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
            expandableLayout.setBodyView(questionsLayout);
            expandableLayout.showBody();
            hasSurvey = true;
        } else {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No PWB survey available");
            expandableLayout.showNoContentMsg();
        }
    }

    private void initQuestions() {
        q1Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ1SeekBar);
        q2Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ2SeekBar);
        q3Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ3SeekBar);
        q4Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ4SeekBar);
        q5Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ5SeekBar);
        q6Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ6SeekBar);
        q7Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ7SeekBar);
        q8Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPwbQ8SeekBar);
    }


    private void savePwbSurvey() {
        ContentValues attributes = new ContentValues();
        attributes.put(PWBTable.KEY_PWB_PARENT_SURVEY_ID, currentSurvey.id);
        attributes.put(PWBTable.KEY_PWB_COMPLETED, true);
        attributes.put(PWBTable.KEY_PWB_Q1, q1Seekbar.getProgress());
        attributes.put(PWBTable.KEY_PWB_Q2, q2Seekbar.getProgress());
        attributes.put(PWBTable.KEY_PWB_Q3, q3Seekbar.getProgress());
        attributes.put(PWBTable.KEY_PWB_Q4, q4Seekbar.getProgress());
        attributes.put(PWBTable.KEY_PWB_Q5, q5Seekbar.getProgress());
        attributes.put(PWBTable.KEY_PWB_Q6, q6Seekbar.getProgress());
        attributes.put(PWBTable.KEY_PWB_Q7, q7Seekbar.getProgress());
        attributes.put(PWBTable.KEY_PWB_Q8, q8Seekbar.getProgress());

        currentSurvey.getSurveys().get(SurveyType.PWB).setAttributes(attributes);

        if(!currentSurvey.grouped) {
            currentSurvey.completed = true;
            currentSurvey.ts = System.currentTimeMillis();
        }

        currentSurvey.save();
    }

    public interface OnPwbSurveyCompletedCallback {
        void onPwbSurveyCompletedCallback();
    }

    public void setCallback(OnPwbSurveyCompletedCallback callback) {
        this.callback = callback;
    }

    public boolean hasSurvey() {
        return hasSurvey;
    }

    public void expand() {
        expandableLayout.expand();
        expandableLayout.showBody();
    }

    public void reInit() {
        init();
    }
}
