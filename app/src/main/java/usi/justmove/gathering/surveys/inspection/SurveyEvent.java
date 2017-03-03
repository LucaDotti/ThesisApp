package usi.justmove.gathering.surveys.inspection;

import usi.justmove.gathering.surveys.Surveys;

/**
 * Created by usi on 16/02/17.
 */

public class SurveyEvent {
    public final static int SCHEDULED = 0;
    public final static int EXPIRED = 1;

    private Surveys survey;
    private int recordId;
    private int status;

    public SurveyEvent(Surveys survey, int recordId, int status) {
        this.survey = survey;
        this.recordId = recordId;
        this.status = status;
    }

    public int getRecordId() {
        return recordId;
    }

    public Surveys getSurvey() {
        return survey;
    }

    public int getStatus() {
        return status;
    }
}
