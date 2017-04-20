package usi.memotion.surveys.config;

/**
 * Created by usi on 15/02/17.
 */

public enum FrequencyUnit {
    MINUTE("minute", 1000*60),
    HOURLY("hourly", 1000*60*60),
    DAILY("daily", 1000*60*60*24),
    WEEKLY("weekly", 1000*60*60*24*7),
    MONTHLY("monthly", 1000*60*60*24*7*30),
    ONCE("once", 0),
    FIXED_DATES("fixed_dates", 0);

    public String name;
    public long millis;

    FrequencyUnit(String name, long millis) {
        this.name = name;
        this.millis = millis;
    }

    public static FrequencyUnit getFrequency(String name) {
        for(FrequencyUnit frequency: FrequencyUnit.values()) {
            if(name.equals(frequency.name)) {
                return frequency;
            }
        }

        throw new IllegalArgumentException("FrequencyUnit not found");
    }

    public String getFrequencyName() {
        return name;
    }
}