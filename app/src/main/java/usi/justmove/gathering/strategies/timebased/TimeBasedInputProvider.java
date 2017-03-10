package usi.justmove.gathering.strategies.timebased;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import usi.justmove.gathering.base.StateMachineInputProvider;

/**
 * Created by usi on 05/01/17.
 */

public class TimeBasedInputProvider implements StateMachineInputProvider<TimeBasedSMSymbol> {
    private LocalTime dayStartTime;
    private LocalTime nightStartTime;

    public TimeBasedInputProvider(String dayStartTime, String nightStartTime) {

        String[] splitted = dayStartTime.split(":");
        this.dayStartTime = new LocalTime()
                .withHourOfDay(Integer.parseInt(splitted[0]))
                .withMinuteOfHour(Integer.parseInt(splitted[1]))
                .withSecondOfMinute(Integer.parseInt(splitted[2]));

        splitted = nightStartTime.split(":");
        this.nightStartTime = new LocalTime()
                .withHourOfDay(Integer.parseInt(splitted[0]))
                .withMinuteOfHour(Integer.parseInt(splitted[1]))
                .withSecondOfMinute(Integer.parseInt(splitted[2]));
    }

    private DateTime getNightStartDateTime() {
        DateTime sNight = new DateTime()
                .withHourOfDay(nightStartTime.getHourOfDay())
                .withMinuteOfHour(nightStartTime.getMinuteOfHour())
                .withSecondOfMinute(nightStartTime.getSecondOfMinute());
        return sNight;
    }

    @Override
    public TimeBasedSMSymbol getInput() {

        DateTime now = new DateTime();
        if(now.isBefore(getNightStartDateTime())) {
            return TimeBasedSMSymbol.IS_DAY;
        } else {
            return TimeBasedSMSymbol.IS_NIGHT;
        }
    }
}
