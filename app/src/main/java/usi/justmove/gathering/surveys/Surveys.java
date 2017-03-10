package usi.justmove.gathering.surveys;

import usi.justmove.gathering.surveys.schedulation.SurveyScheduler;

/**
 * Created by usi on 07/02/17.
 */

public enum Surveys {
    PAM(0),
    PWB(1),
    SWLS(2),
    SHS(3),
    PHQ8(4),
    PSS(5);

    private int surveyIndex;

    Surveys(int surveyIndex) { this.surveyIndex = surveyIndex; }

    public static Surveys getSurvey(int surveyIndex) {
        for (Surveys s : Surveys.values()) {
            if (s.surveyIndex == surveyIndex) return s;
        }
        throw new IllegalArgumentException("Survey not found");
    }
}
