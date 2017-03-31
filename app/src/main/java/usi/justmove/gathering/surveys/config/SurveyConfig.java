package usi.justmove.gathering.surveys.config;

import android.util.Pair;

import java.util.List;

/**
 * Created by usi on 15/02/17.
 */

public class SurveyConfig {

    public SurveyType survey;

    //+--------------------- DAY -------------------+
    //| info to schedule the survey(s) during a day |
    //+---------------------------------------------+
    //number of surveys during a day
    public int surveysCount;
    //contains surveyCount intervals which specify the intervals during which the survey can be submitted
    public Pair<String, String>[] dailyTimes;
    //max time the user can complete the survey
    public long maxElapseTimeForCompletion;
    //min time between two consecutive surveys
    public long minElapseTimeBetweenSurveys;
    //number of notifications for a survey
    public int notificationsCount;
    //whether the first should be fired immediately. It may happens that the survey its triggered
    //by an event and not by the alarm. In that case we want the survey to be fired immediately, without
    //waiting the alarm
    public boolean immediate;

    //+------------------- SCHEDULE ------------------+
    //| info to schedule the survey day               |
    //+-----------------------------------------------+
    //the frequency of the survey(s)
    public Frequency frequency;

    //used by WEEKLY frequency
    //the period of the week the survey should be submitted
    public Period period;
    //used by WEEKLY, FIXED_DATES frequencies
    //how many survey days per frequency unit (2 times per week: frequency -> weekly, dayCount -> 2)
    public int dayCount;
    //used by FIXED_DATES frequency
    //the dates where the surveys should be submitted
    public String startStudy;
    public String endStudy;

    //whether the survey is a composition of other surveys
    public boolean grouped;

    //MAYBE ADD CHECK OF CONFIG
}


