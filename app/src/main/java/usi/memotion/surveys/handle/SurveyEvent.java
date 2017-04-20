package usi.memotion.surveys.handle;

/**
 * Created by usi on 16/02/17.
 */

public class SurveyEvent {
    private long recordId;
    private boolean scheduled;

    public SurveyEvent(long recordId, boolean scheduled) {
        this.recordId = recordId;
        this.scheduled = scheduled;
    }

    public long getRecordId() {
        return recordId;
    }

    public boolean isScheduled() {
        return scheduled;
    }
}
