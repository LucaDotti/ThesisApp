package usi.justmove.UI.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import usi.justmove.R;
import usi.justmove.UI.ExpandableLayout;
import usi.justmove.UI.views.PAMSurveyView;
import usi.justmove.UI.views.PWBSurveyView;
import usi.justmove.UI.views.TermSurveyView;
import usi.justmove.gathering.surveys.config.SurveyType;

/**
 * Created by usi on 04/02/17.
 */

public class SurveysFragment extends Fragment implements PAMSurveyView.OnPamSurveyCompletedCallback, PWBSurveyView.OnPwbSurveyCompletedCallback, TermSurveyView.OnTermSurveyCompletedCallback {
    private OnSurveyCompletedCallback callback;

    private PAMSurveyView pamView;
    private PWBSurveyView pwbView;
    private TermSurveyView termView;
    private View currentVisible;
    private View currSurvey;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SurveyType fragment", "onCreateView");
        View root = inflater.inflate(R.layout.surveys_layout, container, false);

        initPam(root);
        initPwb(root);
        initTerm(root);

        if(termView.hasSurvey()) {
            currSurvey = termView;
            termView.blink();
        } else if(pwbView.hasSurvey()) {
            currSurvey = pwbView;
            pwbView.blink();
        } else if (pamView.hasSurvey()) {
            currSurvey = pamView;
            pamView.blink();
        }

        return root;
    }

    private void initPam(View root) {
        pamView = (PAMSurveyView) root.findViewById(R.id.surveyFragment_pamView);
        pamView.setCallback(this);
    }

    private void initPwb(View root) {
        pwbView = (PWBSurveyView) root.findViewById(R.id.surveyFragment_pwbView);
        pwbView.setCallback(this);
    }

    private void initTerm(View root) {
        termView = (TermSurveyView) root.findViewById(R.id.surveyFragment_termView);
        termView.setCallback(this);
    }

    @Override
    public void onPamSurveyCompletedCallback() {
        callback.onSurveyCompletedCallback();
        pamView.stopBlink();
        if(termView.hasSurvey()) {
            termView.blink();
            currSurvey = termView;
            Log.d("SurveyFrag", "Start blink term");
        } else if(pwbView.hasSurvey()) {
            currSurvey = pwbView;
            pwbView.blink();
            Log.d("SurveyFrag", "Start blink pwb");
        }
    }

    @Override
    public void onPwbSurveyCompletedCallback() {
        callback.onSurveyCompletedCallback();
        pwbView.stopBlink();
        if(termView.hasSurvey()) {
            currSurvey = termView;
            termView.blink();
            Log.d("SurveyFrag", "Start blink term");
        } else if(pamView.hasSurvey()) {
            currSurvey = pamView;
            pamView.blink();
            Log.d("SurveyFrag", "Start blink pam");
        }
    }


    @Override
    public void onTermSurveyCompleted() {
        callback.onSurveyCompletedCallback();
        termView.stopBlink();
        if(pwbView.hasSurvey()) {
            currSurvey = pwbView;
            pwbView.blink();
            Log.d("SurveyFrag", "Start blink pwb");
        } else if(pamView.hasSurvey()) {
            currSurvey = pamView;
            pamView.blink();
            Log.d("SurveyFrag", "Start blink pam");
        }
    }


    public interface OnSurveyCompletedCallback {
        void onSurveyCompletedCallback();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSurveyCompletedCallback) {
            callback = (SurveysFragment.OnSurveyCompletedCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserRegisteredCallback");
        }
    }

    public void resetSurvey(SurveyType survey) {
        switch (survey) {
            case GROUPED_SSPP:
                termView.reInit();
                if(currSurvey == null || currSurvey.equals(termView)) {
                    currSurvey = termView;
                    Log.d("SurveyFrag", "Start blink term");
                    termView.blink();
                }
                break;
            case PAM:
                pamView.reInit();
                if(currSurvey == null || currSurvey.equals(pamView)) {
                    currSurvey = pamView;
                    pamView.blink();
                    Log.d("SurveyFrag", "Start blink pam");
                }
                break;
            case PWB:
                pwbView.reInit();
                if(currSurvey == null || currSurvey.equals(pwbView)) {
                    currSurvey = pwbView;
                    pwbView.blink();
                    Log.d("SurveyFrag", "Start blink pwb");
                }
                break;

        }
    }
}
