package usi.justmove.gathering.surveys.config;

import android.content.Context;
import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

import usi.justmove.MyApplication;
import usi.justmove.R;
import usi.justmove.local.database.tableHandlers.User;

/**
 * Created by usi on 16/02/17.
 */

public class SurveyConfigFactory {
    private static Map<SurveyType, SurveyConfig> instances = new HashMap<>();

    public static SurveyConfig getConfig(SurveyType survey, Context context) {

        if(instances.containsKey(survey)) {
            return instances.get(survey);
        } else {
            SurveyConfig config = new SurveyConfig();
            switch(survey) {
                case PAM:
                    config.survey = SurveyType.PAM;
                    config.frequency = Frequency.getFrequency(context.getString(R.string.PAM_frequency));
                    config.surveysCount = Integer.parseInt(context.getString(R.string.PAM_daily_count));
                    config.dailyTimes = parseTimes(context.getResources().getStringArray(R.array.PAM_daily_times));
                    config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PAM_max_elapse_time_for_completion));
                    config.minElapseTimeBetweenSurveys = Long.parseLong(context.getString(R.string.PAM_min_elapse_time_between_surveys));
                    config.notificationsCount = Integer.parseInt(context.getString(R.string.PAM_notifications_count));
                    config.immediate = Boolean.parseBoolean(context.getString(R.string.PAM_immediate));
                    instances.put(SurveyType.PAM, config);
                    break;
                case PWB:
                    config.survey = SurveyType.PWB;
                    config.frequency = Frequency.getFrequency(context.getString(R.string.PWB_frequency));
                    config.surveysCount = Integer.parseInt(context.getString(R.string.PWB_daily_count));
                    config.dailyTimes = parseTimes(context.getResources().getStringArray(R.array.PWB_daily_times));
                    config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.PWB_max_elapse_time_for_completion));
                    config.notificationsCount = Integer.parseInt(context.getString(R.string.PWB_notifications_count));
                    config.period = Period.getPeriod(context.getString(R.string.PWB_week_period));
                    config.dayCount = Integer.parseInt(context.getString(R.string.PWB_count));
                    config.immediate = Boolean.parseBoolean(context.getString(R.string.PWB_immediate));
                    instances.put(SurveyType.PWB, config);
                    break;
                case GROUPED_SSPP:
                    config.survey = SurveyType.GROUPED_SSPP;
                    config.frequency = Frequency.getFrequency(context.getString(R.string.term_frequency));
                    config.surveysCount = Integer.parseInt(context.getString(R.string.term_daily_count));
                    config.dailyTimes = parseTimes(context.getResources().getStringArray(R.array.term_daily_times));
                    config.maxElapseTimeForCompletion = Long.parseLong(context.getString(R.string.term_max_elapse_time_for_completion));
                    config.notificationsCount = Integer.parseInt(context.getString(R.string.term_notifications_count));
//                    config.period = Period.getPeriod(context.getString(R.string.term_week_period));
                    config.dayCount = Integer.parseInt(context.getString(R.string.term_count));
                    config.grouped = true;
                    config.endStudy = context.getString(R.string.term_end_study_date);
                    config.startStudy = User.getCreationDate();
                    config.immediate = Boolean.parseBoolean(context.getString(R.string.term_immediate));
                    instances.put(SurveyType.GROUPED_SSPP, config);
                    break;
                default:

            }
            return config;
        }

    }

    public static void alterConfig(SurveyType survey, String attribute, String... value) {
        SurveyConfig config = getConfig(survey, MyApplication.getContext());

        switch(attribute) {
            case "surveysCount":
                config.surveysCount = Integer.parseInt(value[0]);
                break;
            case "maxElapseTimeForCompletion":
                config.maxElapseTimeForCompletion = Long.parseLong(value[0]);
                break;
            case "minElapseTimeBetweenSurveys":
                config.minElapseTimeBetweenSurveys = Long.parseLong(value[0]);
                break;
            case "notificationsCount":
                config.notificationsCount = Integer.parseInt(value[0]);
                break;
            case "frequency":
                config.frequency = Frequency.getFrequency(value[0]);
                break;
            case "period":
                config.period = Period.getPeriod(value[0]);
                break;
            case "dayCount":
                config.dayCount = Integer.parseInt(value[0]);
                break;
            case "grouped":
                config.grouped = Boolean.parseBoolean(value[0]);
                break;
            case "dailyTimes":
                config.dailyTimes = parseTimes(value);
                break;
        }
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
