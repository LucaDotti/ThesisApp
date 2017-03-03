package usi.justmove.gathering.surveys.config;

import org.joda.time.DateTimeConstants;

import java.util.Date;

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

    public static int toDateTimeConstants(Days day) {
        switch(day) {
            case MONDAY:
                return DateTimeConstants.MONDAY;
            case TUESDAY:
                return DateTimeConstants.TUESDAY;
            case WEDNESDAY:
                return DateTimeConstants.WEDNESDAY;
            case THURSDAY:
                return DateTimeConstants.THURSDAY;
            case FRIDAY:
                return DateTimeConstants.FRIDAY;
            case SATURDAY:
                return DateTimeConstants.SATURDAY;
            case SUNDAY:
                return DateTimeConstants.SUNDAY;
            default:
                throw new IllegalArgumentException("Day not found");
        }
    }
}
