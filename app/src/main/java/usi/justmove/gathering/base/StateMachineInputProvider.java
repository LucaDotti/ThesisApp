package usi.justmove.gathering.base;

import java.util.Observable;

/**
 * Created by usi on 05/01/17.
 */

public interface StateMachineInputProvider<S> {
    S getInput();
}
