package usi.justmove.gathering.surveys.config;

import android.util.Pair;

/**
 * Created by usi on 15/02/17.
 */

public class SurveyConfig {

    public SurveyType survey;

    //+------------------- DAY ------------------+
    //| info to schedule the survey during a day |
    //+------------------------------------------+
    //number of surveys during a day
    public int surveysCount;
    //contains surveyCount intervals which specify the intervals during which the survey can be submitted
    public Pair<String, String>[] times;
    public long maxElapseTimeForCompletion;
    public long minElapseTimeBetweenSurveys;
    public int notificationsCount;

    //+------------------- SCHEDULE ------------------+
    //| info to schedule the survey day               |
    //+-----------------------------------------------+
    public Frequency frequency;
    public Period period;
    public int dayCount;



    public boolean grouped;

    //MAYBE ADD CHECK OF CONFIG
}


