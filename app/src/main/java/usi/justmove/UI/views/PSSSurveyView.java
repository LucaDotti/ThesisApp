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
import usi.justmove.local.database.tables.PSSTable;
import usi.justmove.local.database.tables.PWBTable;
import usi.justmove.local.database.tables.SHSTable;

/**
 * Created by usi on 07/03/17.
 */

public class PSSSurveyView extends LinearLayout {
    private OnPssSurveyCompletedCallback callback;

    private Context context;

    private View titleView;
    private LinearLayout questionsLayout;
    private ExpandableLayout expandableLayout;

    private DiscreteSeekBar q1Seekbar;
    private DiscreteSeekBar q2Seekbar;
    private DiscreteSeekBar q3Seekbar;
    private DiscreteSeekBar q4Seekbar;
    private DiscreteSeekBar q5Seekbar;
    private DiscreteSeekBar q6Seekbar;
    private DiscreteSeekBar q7Seekbar;
    private DiscreteSeekBar q8Seekbar;
    private DiscreteSeekBar q9Seekbar;
    private DiscreteSeekBar q10Seekbar;
    private Button submiButton;

    private Survey currentSurvey;

    public PSSSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pss_layout, this, true);

        expandableLayout = (ExpandableLayout) findViewById(R.id.pssViewExpandableLayout);
        titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        questionsLayout = (LinearLayout) inflater.inflate(R.layout.pss_questions_layout, null);

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
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "PSS");

        Survey survey = Survey.getAvailableSurvey(SurveyType.PSS);

        if(survey == null) {
            survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);
        }

        if(survey != null) {
            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);

            if(children.containsKey(SurveyType.PSS)) {
                currentSurvey = survey;
                q1Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ1SeekBar);
                q2Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ2SeekBar);
                q3Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ3SeekBar);
                q4Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ4SeekBar);
                q5Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ5SeekBar);
                q6Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ6SeekBar);
                q7Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ7SeekBar);
                q8Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ8SeekBar);
                q9Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ9SeekBar);
                q10Seekbar = (DiscreteSeekBar) questionsLayout.findViewById(R.id.surveysPssQ10SeekBar);

                submiButton = (Button) questionsLayout.findViewById(R.id.pssSubmitButton);

                submiButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePssSurvey();
                        expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
                        expandableLayout.setNoContentMsg("No PSS survey available");
                        expandableLayout.showNoContentMsg();
                        expandableLayout.collapse();
                        callback.onPssSurveyCompletedCallback();

                        if(!currentSurvey.grouped) {
                            notifySurveyCompleted();
                        }

                        Toast.makeText(getContext(), "PSS survey completed", Toast.LENGTH_SHORT).show();
                    }
                });

                expandableLayout.setBodyView(questionsLayout);
                expandableLayout.showBody();

                return;
            }
        }

        questionsLayout.setVisibility(GONE);
        expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
        expandableLayout.setNoContentMsg("No PSS survey available");
        expandableLayout.showNoContentMsg();
    }

    private void savePssSurvey() {
        ContentValues attributes = new ContentValues();
        attributes.put(PSSTable.KEY_PSS_PARENT_SURVEY_ID, currentSurvey.id);
        attributes.put(PSSTable.KEY_PSS_COMPLETED, true);
        attributes.put(PSSTable.KEY_PSS_Q1, q1Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q2, q2Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q3, q3Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q4, q4Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q5, q5Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q6, q6Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q7, q7Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q8, q8Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q9, q9Seekbar.getProgress());
        attributes.put(PSSTable.KEY_PSS_Q10, q10Seekbar.getProgress());

        Survey survey = (Survey) Survey.findByPk(currentSurvey.id);
        survey.getSurveys().get(SurveyType.PSS).setAttributes(attributes);

        if(!survey.grouped) {
            survey.completed = true;
            survey.ts = System.currentTimeMillis();
        }

        survey.save();
    }

    public interface OnPssSurveyCompletedCallback {
        void onPssSurveyCompletedCallback();
    }

    public void setCallback(OnPssSurveyCompletedCallback callback) {
        this.callback = callback;
    }
}
