package usi.memotion.stateMachines.strategies.timeBased;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import usi.memotion.stateMachines.base.ActiveStateMachine;

/**
 * Created by usi on 20/04/17.
 */

public class TBStateMachine extends ActiveStateMachine {
    private LocalTime dayStartTime;
    private LocalTime nightStartTime;

    public TBStateMachine(TBSMState[][] transitions, TBSMState startState, String dayStartTime, String nightStartTime) {
        super(transitions, startState);

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
    public TBSMSymbol getSymbol() {
        DateTime now = new DateTime();
        if(now.isBefore(getNightStartDateTime())) {
            return TBSMSymbol.IS_DAY;
        } else {
            return TBSMSymbol.IS_NIGHT;
        }
    }
}
