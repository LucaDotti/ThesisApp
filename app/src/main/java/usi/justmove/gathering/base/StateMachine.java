package usi.justmove.gathering.base;

import android.util.Log;

import java.util.Observable;

/**
 * Created by Luca Dotti on 27/12/16.
 */
public class StateMachine<S extends Enum, T extends Enum> extends Observable implements Runnable {
    private StateMachineInputProvider<T> inputProvider;
    private S[][] transitions;
    private S currentState;
    private boolean running;
    private long triggerFreq;

    public StateMachine(StateMachineInputProvider<T> inputProvider, S[][] transitions, S startState,long triggerFreq) {
        this.inputProvider = inputProvider;
        this.transitions = transitions;
        this.triggerFreq = triggerFreq;
        currentState = startState;
        running = true;
    }

    private S transition(T symbol) {
        return transitions[currentState.ordinal()][symbol.ordinal()];
    }

    @Override
    public void run() {
//        Log.d("SM", "STARTED");
        while(running) {
//            Log.d("SM", "TRANSITION");
            S newState = transition(inputProvider.getInput());
            if(newState != currentState) {
//                Log.d("SM", "NOTIFY");
                currentState = newState;
                setChanged();
                notifyObservers(currentState);
            }

            try {
                Thread.sleep(triggerFreq);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        running = false;
    }
}