package usi.justmove.gathering.surveys.config;

import usi.justmove.gathering.surveys.config.Days;

import java.util.List;

import usi.justmove.gathering.surveys.Surveys;

/**
 * Created by usi on 15/02/17.
 */

public class SurveyConfig {
    public Surveys survey;
    public Frequency frequency;
    public int surveysCount;
    public String startTime;
    public String[] midTimes;
    public String endTime;
    public long maxElapseTimeForCompletion;
    public long minElapseTimeBetweenSurveys;
    public int notificationsCount;
    public String scheduleTime;
    public Period period;

}


