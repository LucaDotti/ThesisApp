package usi.justmove.gathering.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import usi.justmove.gathering.base.FrequencyProvider;
import usi.justmove.gathering.strategies.timebased.TimeBasedSamplingFrequencyProvider;

/**
 * Created by usi on 27/12/16.
 */

public class SamplingFrequencyTunerService extends IntentService {
    private FrequencyProvider provider;
    private double currentFrequency;
    private Thread providerThread;

    public SamplingFrequencyTunerService() {
        super("SamplingFrequencyService");
        provider = new TimeBasedSamplingFrequencyProvider("07:00:00", "23:00:00");
        currentFrequency = 0.0;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        providerThread = new Thread(provider);
        providerThread.start();
        while(true) {
            try {
                synchronized(provider) {
                    provider.wait();
                }
                currentFrequency = provider.getFrequency();
                Log.d("FREQUENCY", Double.toString(currentFrequency));
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
