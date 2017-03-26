package usi.justmove.gathering.surveys.config;

import android.content.Context;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import usi.justmove.R;

/**
 * Created by usi on 16/02/17.
 */

public class SurveyConfigFactory {
    private static List<SurveyConfig> configs = new ArrayList<>();
    public static SurveyConfig getConfig(SurveyType survey, Context context) {
        SurveyConfig config = new SurveyConfig();
        switch(survey) {
            case PAM:
                config.survey = SurveyType.PAM;
                config.frequency = Frequency.getFrequency(context.getString(R.string.PAM_frequency));
                config.surveysCount = Integer.parseInt(context.getString(R.string.PAM_daily_count));
                config.times = parseTimes(context.getResources().getStringArray(R.array.PAM_daily_times));
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PAM_max_elapse_time_for_completion));
                config.minElapseTimeBetweenSurveys = Long.parseLong(context.getString(R.string.PAM_min_elapse_time_between_surveys));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.PAM_notifications_count));
                break;
            case PWB:
                config.survey = SurveyType.PWB;
                config.frequency = Frequency.getFrequency(context.getString(R.string.PWB_frequency));
                config.surveysCount = Integer.parseInt(context.getString(R.string.PWB_daily_count));
                config.times = parseTimes(context.getResources().getStringArray(R.array.PWB_daily_times));
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PWB_max_elapse_time_for_completion));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.PWB_notifications_count));
                config.period = Period.getPeriod(context.getString(R.string.PWB_week_period));
                config.dayCount = Integer.parseInt(context.getString(R.string.PWB_count));
                break;
            case GROUPED_SSPP:
                config.survey = SurveyType.GROUPED_SSPP;
                config.frequency = Frequency.getFrequency(context.getString(R.string.term_frequency));
                config.surveysCount = Integer.parseInt(context.getString(R.string.term_daily_count));
                config.times = parseTimes(context.getResources().getStringArray(R.array.term_daily_times));
                config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.term_max_elapse_time_for_completion));
                config.notificationsCount = Integer.parseInt(context.getString(R.string.term_notifications_count));
                config.period = Period.getPeriod(context.getString(R.string.term_week_period));
                config.dayCount = Integer.parseInt(context.getString(R.string.term_count));
                config.grouped = true;
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
