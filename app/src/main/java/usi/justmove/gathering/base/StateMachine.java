package usi.justmove.gathering.base;

import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by usi on 27/12/16.
 */

public interface StateMachine<S, T> {
    void transition(T symbol);
    S currentState();
}