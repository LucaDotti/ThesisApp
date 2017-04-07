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
import usi.justmove.local.database.tables.PSSTable;
import usi.justmove.local.database.tables.PWBTable;
import usi.justmove.local.database.tables.SHSTable;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by usi on 07/03/17.
 */

public class PSSSurveyView extends LinearLayout {
    private OnPssSurveyCompletedCallback callback;

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
    private SeekBar q9Seekbar;
    private SeekBar q10Seekbar;
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

    private Survey getCurrentSurvey() {
        Survey survey = Survey.getAvailableSurvey(SurveyType.PSS);

        if(survey != null) {
            return survey;
        }

        survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);

        if(survey != null) {
            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);

            if(children.containsKey(SurveyType.PSS)) {
                return survey;
            }
        }

        return null;
    }

    private void init() {
        expandableLayout.getBodyView().removeAllViews();
        expandableLayout.getTitleView().removeAllViews();
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "PSS");

        currentSurvey = getCurrentSurvey();

        if(currentSurvey != null) {
            q1Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ1SeekBar);
            q1Seekbar.setMax(4);
            q2Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ2SeekBar);
            q2Seekbar.setMax(4);
            q3Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ3SeekBar);
            q3Seekbar.setMax(4);
            q4Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ4SeekBar);
            q4Seekbar.setMax(4);
            q5Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ5SeekBar);
            q5Seekbar.setMax(4);
            q6Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ6SeekBar);
            q6Seekbar.setMax(4);
            q7Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ7SeekBar);
            q7Seekbar.setMax(4);
            q8Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ8SeekBar);
            q8Seekbar.setMax(4);
            q9Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ9SeekBar);
            q9Seekbar.setMax(4);
            q10Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysPssQ10SeekBar);
            q10Seekbar.setMax(4);

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

            expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
            expandableLayout.setBodyView(questionsLayout);
            expandableLayout.showBody();
//            hasSurvey = true;
        } else {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No PSS survey available");
            expandableLayout.showNoContentMsg();
        }
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

    public void expand() {
        expand();
        expandableLayout.showBody();
    }

    public void reInit() {
        init();
    }
}
