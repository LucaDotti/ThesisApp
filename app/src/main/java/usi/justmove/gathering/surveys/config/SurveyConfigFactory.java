package usi.justmove.gathering.surveys.config;

import android.content.Context;
import android.util.Pair;

import usi.justmove.MyApplication;
import usi.justmove.R;
import usi.justmove.local.database.tableHandlers.User;

/**
 * Created by usi on 16/02/17.
 */

public class SurveyConfigFactory {

    public static SurveyConfig getConfig(SurveyType survey, Context context) {
        SurveyConfig config = new SurveyConfig();
        switch(survey) {
            case PAM:
                config.survey = SurveyType.PAM;
                config.frequency = new Frequency(FrequencyUnit.getFrequency(context.getString(R.string.PAM_frequency)), Integer.parseInt(context.getString(R.string.PAM_frequency_multiplier)));
                config.surveysCount = Integer.parseInt(context.getString(R.string.PAM_daily_count));
                config.dailyTimes = parseTimes(context.getResources().getStringArray(R.array.PAM_daily_times));
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PAM_max_elapse_time_for_completion));
                config.minElapseTimeBetweenSurveys = Long.parseLong(context.getString(R.string.PAM_min_elapse_time_between_surveys));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.PAM_notifications_count));
                config.immediate = Boolean.parseBoolean(context.getString(R.string.PAM_immediate));
                config.dayCount = Integer.parseInt(context.getString(R.string.PAM_count));
                break;
            case PWB:
                config.survey = SurveyType.PWB;
                config.frequency = new Frequency(FrequencyUnit.getFrequency(context.getString(R.string.PWB_frequency)), Integer.parseInt(context.getString(R.string.PWB_frequency_multiplier)));
                config.surveysCount = Integer.parseInt(context.getString(R.string.PWB_daily_count));
                config.dailyTimes = parseTimes(context.getResources().getStringArray(R.array.PWB_daily_times));
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PWB_max_elapse_time_for_completion));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.PWB_notifications_count));
                config.period = Period.getPeriod(context.getString(R.string.PWB_week_period));
                config.dayCount = Integer.parseInt(context.getString(R.string.PWB_count));
                config.immediate = Boolean.parseBoolean(context.getString(R.string.PWB_immediate));
                break;
            case GROUPED_SSPP:
                config.survey = SurveyType.GROUPED_SSPP;
                config.frequency = new Frequency(FrequencyUnit.getFrequency(context.getString(R.string.term_frequency)), -1);
                config.surveysCount = Integer.parseInt(context.getString(R.string.term_daily_count));
                config.dailyTimes = parseTimes(context.getResources().getStringArray(R.array.term_daily_times));
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.term_max_elapse_time_for_completion));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.term_notifications_count));
                config.dayCount = Integer.parseInt(context.getString(R.string.term_count));
                config.grouped = true;
                config.endStudy = context.getString(R.string.term_end_study_date);
                config.startStudy = User.getCreationDate();
                config.immediate = Boolean.parseBoolean(context.getString(R.string.term_immediate));
                break;
            default:

        }

        return config;

    }

    private static Pair<String, String>[] parseTimes(String[] times) {
        if(times.length == 0) {
            return null;
        }
        Pair<String, String>[] pairs = new Pair[times.length];

        String[] split;
        for (int i = 0; i < times.length; i++) {
            split = times[i].split(",");
            pairs[i] =  new Pair<>(split[0], split[1]);
        }

        return pairs;
    }

}
