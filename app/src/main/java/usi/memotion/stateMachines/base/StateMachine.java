package usi.memotion.stateMachines.base;

import android.util.Log;

import java.util.Observable;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by Luca Dotti on 27/12/16.
 */
public abstract class StateMachine extends Observable {
    private Enum[][] transitions;
    private Enum currentState;

    public StateMachine(Enum[][] transitions, Enum startState) {
        this.transitions = transitions;
        currentState = startState;
    }

    protected void transition() {
        Enum symbol = getSymbol();
        String msg = "(" + currentState.toString() + ", " + symbol.toString() + ") -> ";

        Enum newState = transitions[currentState.ordinal()][symbol.ordinal()];
        currentState = newState;
        setChanged();
        Log.d("STATE MACHINE", "Transition " + msg + currentState.toString());
        notifyObservers(currentState);
    }

    public abstract Enum getSymbol();

}