package usi.memotion.stateMachines.strategies.timeBased;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Calendar;

import usi.memotion.stateMachines.base.ActiveStateMachine;

/**
 * Created by usi on 20/04/17.
 */

public class TBStateMachine extends ActiveStateMachine {
    private Calendar dayStartTime;
    private Calendar nightStartTime;
    private String nightTimeS;

    public TBStateMachine(TBSMState[][] transitions, TBSMState startState, String dayStartTime, String nightStartTime) {
        super(transitions, startState);
        this.nightTimeS = nightStartTime;
        String[] splitted = nightStartTime.split(":");
        this.nightStartTime = Calendar.getInstance();
        this.nightStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitted[0]));
        this.nightStartTime.set(Calendar.MINUTE, Integer.parseInt(splitted[1]));
        this.nightStartTime.set(Calendar.SECOND, Integer.parseInt(splitted[2]));
    }

    private Calendar getNightStartDateTime() {
        String[] splitted = nightTimeS.split(":");
        Calendar sNight = Calendar.getInstance();
        sNight.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitted[0]));
        sNight.set(Calendar.MINUTE, Integer.parseInt(splitted[1]));
        sNight.set(Calendar.SECOND, Integer.parseInt(splitted[2]));
        return sNight;
    }

    @Override
    public TBSMSymbol getSymbol() {
        Calendar now = Calendar.getInstance();
        if(now.getTimeInMillis() < getNightStartDateTime().getTimeInMillis()) {
            return TBSMSymbol.IS_DAY;
        } else {
            return TBSMSymbol.IS_NIGHT;
        }
    }
}
