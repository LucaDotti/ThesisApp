package usi.justmove.gathering.base;

import usi.justmove.gathering.strategies.timebased.TimeBasedSMState;
import usi.justmove.gathering.strategies.timebased.TimeBasedSMSymbol;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * This base class represents a sampling frequency provider. It is a Runnable object, because it
 * needs to periodically provide inputs to the StateMachine object in order to change its state
 * and determine the sampling frequency.
 *
 * Created by Luca Dotti on 29/12/16.
 */
public abstract class FrequencyProvider implements Runnable {
    protected boolean running = true;

    public void terminate() {
        running = false;
    }
    public abstract double getFrequency();
}
