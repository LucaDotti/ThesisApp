package usi.justmove.gathering.strategies.timebased;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import usi.justmove.gathering.base.FrequencyProvider;
import usi.justmove.gathering.base.StateMachine;

/**
 * Created by usi on 28/12/16.
 */

public class TimeBasedSamplingFrequencyProvider extends FrequencyProvider {
    private LocalTime dayStartTime;
    private LocalTime nightStartTime;
    private double dayFrequency;
    private double nightFrequency;
    private StateMachine<TimeBasedSMState, TimeBasedSMSymbol> sm;
    private TimeBasedSMState currentState;

    public TimeBasedSamplingFrequencyProvider(String dayStartTime, String nightStartTime) {
        sm = new TimeBasedGatheringStateMachine();
        currentState = TimeBasedSMState.START;

        dayFrequency = 1;
        nightFrequency = 0;

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

    private DateTime getDayStartDateTime() {
        DateTime sDay = new DateTime();
        sDay.withHourOfDay(dayStartTime.getHourOfDay());
        sDay.withMinuteOfHour(dayStartTime.getMinuteOfHour());
        sDay.withSecondOfMinute(dayStartTime.getSecondOfMinute());
        return sDay;
    }

    private DateTime getNightStartDateTime() {
        DateTime sNight = new DateTime()
            .withHourOfDay(nightStartTime.getHourOfDay())
            .withMinuteOfHour(nightStartTime.getMinuteOfHour())
            .withSecondOfMinute(nightStartTime.getSecondOfMinute());
        return sNight;
    }

    @Override
    public double getFrequency() {
        switch(currentState) {
            case DAY: return dayFrequency;
            case NIGHT: return nightFrequency;
            default: return 0.0;
        }
    }

    private TimeBasedSMSymbol getInput() {
        DateTime now = new DateTime();
        System.out.println(DateTimeFormat.forPattern("HH:mm:ss").print(now));
        if(now.isBefore(getNightStartDateTime())) {
            return TimeBasedSMSymbol.IS_DAY;
        } else {
            return TimeBasedSMSymbol.IS_NIGHT;
        }
    }

    @Override
    public void run() {
        while(running) {
            sm.transition(getInput());
            if(sm.currentState() != currentState) {
                currentState = sm.currentState();
                Log.d("FREQUENCY", currentState.toString());
                synchronized(this) {
                    notify();
                }
            }

            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
