package usi.justmove.gathering.surveys.schedulation;

import usi.justmove.gathering.surveys.config.SurveyConfig;

/**
 * Created by usi on 07/02/17.
 */

public interface SurveyScheduler {
    void schedule();
    SurveyConfig getConfig();
}
