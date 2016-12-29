package usi.justmove.gathering.base;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by usi on 29/12/16.
 */

public interface FrequencyProvider extends Runnable {
    double getFrequency();
}
