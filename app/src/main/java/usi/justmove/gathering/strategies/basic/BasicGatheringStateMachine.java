package usi.justmove.gathering.strategies.basic;

import usi.justmove.gathering.base.StateMachine;

/**
 * Basic gathering strategy: get the location everytime, no matter what is the current activity.
 *
 * Created by Luca Dotti on 27/12/16.
 */
public class BasicGatheringStateMachine implements StateMachine<BasicSMState, BasicSMSymbol> {
    //current state
    private BasicSMState currentState;
    //transition function
    private BasicSMState[][] transitionFunction;

    /**
     * Constructor. Initialize the start state as current state and the transition function.
     */
    public BasicGatheringStateMachine() {
        currentState = BasicSMState.MOVING;
        transitionFunction = new BasicSMState[1][1];
        //transition: [MOVING, *] -> MOVING
        transitionFunction[BasicSMState.MOVING.ordinal()][BasicSMSymbol.STAR.ordinal()] = BasicSMState.MOVING;
    }

    @Override
    public void transition(BasicSMSymbol symbol) {
        currentState = transitionFunction[currentState.ordinal()][symbol.ordinal()];
    }

    @Override
    public BasicSMState currentState() {
        return currentState;
    }
}

enum BasicSMState {
    //moving state
    MOVING
}

enum BasicSMSymbol {
    //any input, *
    STAR
}
