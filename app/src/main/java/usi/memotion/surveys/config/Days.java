package usi.memotion.surveys.config;

import java.util.Calendar;

/**
 * Created by usi on 16/02/17.
 */

public enum Days {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static int toTimeConstants(Days day) {
        switch(day) {
            case MONDAY:
                return Calendar.MONDAY;
            case TUESDAY:
                return Calendar.TUESDAY;
            case WEDNESDAY:
                return Calendar.WEDNESDAY;
            case THURSDAY:
                return Calendar.THURSDAY;
            case FRIDAY:
                return Calendar.FRIDAY;
            case SATURDAY:
                return Calendar.SATURDAY;
            case SUNDAY:
                return Calendar.SUNDAY;
            default:
                throw new IllegalArgumentException("Day not found");
        }
    }
}
