package usi.justmove.gathering.surveys.config;

import org.joda.time.Period;

import static android.R.attr.name;

/**
 * Created by usi on 15/02/17.
 */

public enum Frequency {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    ONCE("once"),
    FIXED_DATES("fixed_dates");

    public String name;

    Frequency(String name) {
        this.name = name;
    }

    public static long getMilliseconds(Frequency period) {
        switch (period) {
            case DAILY:
                return 1000*60*60*24;
            case WEEKLY:
                return 1000*60*60*24*7;
            case MONTHLY:
                return 1000*60*60*24*7*30;
            default:
                return 0;
        }
    }

    public static Frequency getFrequency(String name) {
        for(Frequency frequency: Frequency.values()) {
            if(name.equals(frequency.name)) {
                return frequency;
            }
        }

        throw new IllegalArgumentException("Frequency not found");
    }
}