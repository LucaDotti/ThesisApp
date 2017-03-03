package usi.justmove.gathering.surveys.config;

import android.content.Context;

import usi.justmove.R;
import usi.justmove.gathering.surveys.Surveys;

/**
 * Created by usi on 16/02/17.
 */

public class SurveyConfigFactory {
    public static SurveyConfig getConfig(Surveys survey, Context context) {
        SurveyConfig config = new SurveyConfig();
        switch(survey) {
            case PAM:
                config.survey = Surveys.PAM;
                config.frequency = Frequency.getFrequency(context.getString(R.string.PAM_frequency));
                config.surveysCount = Integer.parseInt(context.getString(R.string.PAM_count));
                config.startTime = context.getString(R.string.PAM_start);
                config.scheduleTime = context.getString(R.string.PAM_schedule_time);
                String[] pamMidTimes = {context.getString(R.string.PAM_afternoon)};
                config.midTimes = pamMidTimes;
                config.endTime = context.getString(R.string.PAM_end);
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PAM_max_elapse_time_for_completition));
                config.minElapseTimeBetweenSurveys = Long.parseLong(context.getString(R.string.PAM_min_elapse_time_between_surveys));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.PAM_notifications_count));
                return config;
            case PWB:
                config.survey = Surveys.PWB;
                config.frequency = Frequency.getFrequency(context.getString(R.string.PWB_frequency));
                config.surveysCount = Integer.parseInt(context.getString(R.string.PWB_count));
                config.scheduleTime = context.getString(R.string.PWB_schedule_time);
                config.startTime = context.getString(R.string.PWB_start);
                config.endTime = context.getString(R.string.PWB_end);
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PWB_max_elapse_time_for_completition));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.PWB_notifications_count));
                config.period = Period.getPeriod(context.getString(R.string.PWB_week_period));
                return config;
            default:
                return config;
        }
    }
}
