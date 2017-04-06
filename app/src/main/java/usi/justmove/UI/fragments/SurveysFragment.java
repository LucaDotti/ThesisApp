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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SurveyType fragment", "onCreateView");
        View root = inflater.inflate(R.layout.surveys_layout, container, false);

        initPam(root);
        initPwb(root);
        initTerm(root);

        if(termView.hasSurvey()) {
            termView.expand();
        } else if(pwbView.hasSurvey()) {
            pwbView.expand();
        } else if (pamView.hasSurvey()) {
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
            termView.expand();
        } else if(pwbView.hasSurvey()) {
            pwbView.expand();
        }
    }

    @Override
    public void onPwbSurveyCompletedCallback() {
        callback.onSurveyCompletedCallback();

        if(termView.hasSurvey()) {
            termView.expand();
        } else if(pamView.hasSurvey()) {
            pamView.expand();
        }
    }


    @Override
    public void onTermSurveyCompleted() {
        callback.onSurveyCompletedCallback();
        if(pamView.hasSurvey()) {
            pamView.expand();
        } else if(pwbView.hasSurvey()) {
            pwbView.expand();
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
            case PAM:
                pamView.reInit();
                pamView.blink();
//                pamView.expand();
                break;
            case PWB:
                pwbView.reInit();
//                pwbView.expand();
                break;
            case GROUPED_SSPP:
                termView.reInit();
//                termView.expand();
                break;
        }
    }
}
