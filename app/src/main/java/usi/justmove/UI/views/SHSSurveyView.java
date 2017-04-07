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

/**
 * Created by usi on 07/03/17.
 */

public class SHSSurveyView extends LinearLayout {
    private OnShsSurveyCompletedCallback callback;
    private Context context;

    private View titleView;
    private LinearLayout questionsLayout;
    private ExpandableLayout expandableLayout;

    private SeekBar q1Seekbar;
    private SeekBar q2Seekbar;
    private SeekBar q3Seekbar;
    private SeekBar q4Seekbar;
    private Button submiButton;

    private Survey currentSurvey;

    public SHSSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.shs_layout, this, true);

        expandableLayout = (ExpandableLayout) findViewById(R.id.shsViewExpandableLayout);
        titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        questionsLayout = (LinearLayout) inflater.inflate(R.layout.shs_questions_layout, null);

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
        Survey survey = Survey.getAvailableSurvey(SurveyType.SHS);

        if(survey != null) {
            return survey;
        }

        survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);

        if(survey != null) {
            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);

            if(children.containsKey(SurveyType.SHS)) {
                return survey;
            }
        }

        return null;
    }

    private void init() {
        expandableLayout.getBodyView().removeAllViews();
        expandableLayout.getTitleView().removeAllViews();
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "SHS");

        currentSurvey = getCurrentSurvey();

        if(currentSurvey != null) {
            q1Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysShsQ1SeekBar);
            q1Seekbar.setMax(4);
            q2Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysShsQ2SeekBar);
            q2Seekbar.setMax(4);
            q3Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysShsQ3SeekBar);
            q3Seekbar.setMax(4);
            q4Seekbar = (SeekBar) questionsLayout.findViewById(R.id.surveysShsQ4SeekBar);
            q4Seekbar.setMax(4);

            submiButton = (Button) questionsLayout.findViewById(R.id.shsSubmitButton);

            submiButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveSHSSurvey();
                    expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
                    expandableLayout.setNoContentMsg("No SHS survey available");
                    expandableLayout.showNoContentMsg();
                    expandableLayout.collapse();
                    callback.onShsSurveyCompletedCallback();
                    if(!currentSurvey.grouped) {
                        notifySurveyCompleted();
                    }

                    Toast.makeText(getContext(), "SHS survey completed", Toast.LENGTH_SHORT).show();
                }
            });

            expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
            expandableLayout.setBodyView(questionsLayout);
            expandableLayout.showBody();
//            hasSurvey = true;
        } else {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No SHS survey available");
            expandableLayout.showNoContentMsg();
        }

    }

    private void saveSHSSurvey() {
        ContentValues attributes = new ContentValues();
        attributes.put(SHSTable.KEY_SHS_PARENT_SURVEY_ID, currentSurvey.id);
        attributes.put(SHSTable.KEY_SHS_COMPLETED, true);
        attributes.put(SHSTable.KEY_SHS_Q1, q1Seekbar.getProgress());
        attributes.put(SHSTable.KEY_SHS_Q2, q2Seekbar.getProgress());
        attributes.put(SHSTable.KEY_SHS_Q3, q3Seekbar.getProgress());
        attributes.put(SHSTable.KEY_SHS_Q4, q4Seekbar.getProgress());

        Survey survey = (Survey) Survey.findByPk(currentSurvey.id);
        survey.getSurveys().get(SurveyType.SHS).setAttributes(attributes);

        if(!survey.grouped) {
            survey.completed = true;
            survey.ts = System.currentTimeMillis();
        }

        survey.save();
    }

    public interface OnShsSurveyCompletedCallback {
        void onShsSurveyCompletedCallback();
    }

    public void setCallback(OnShsSurveyCompletedCallback callback) {
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
