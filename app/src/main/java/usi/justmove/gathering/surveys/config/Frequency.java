package usi.justmove.gathering.surveys.config;

/**
 * Created by usi on 12/04/17.
 */

public class Frequency {
    public FrequencyUnit unit;
    public int multiplier;

    public Frequency(FrequencyUnit unit, int multiplier) {
        this.unit = unit;
        this.multiplier = multiplier;
    }
}
