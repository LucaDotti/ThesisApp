package usi.justmove.utils;

/**
 * Helper class to perform any kind of frequency transformation. It is assumed that the given
 * frequency is dailyTimes/minute
 *
 * Created by Luca Dotti on 07/01/17.
 */
public class FrequencyHelper {

    /**
     * This method returns the elapsed time in milliseconds between every tick.
     *
     * @param freq
     * @return
     */
    public static long getElapseTimeMillis(double freq) {
        return (long) (60/freq)*1000;
    }
}
