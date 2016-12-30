package usi.justmove;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import usi.justmove.gathering.services.SamplingFrequencyTunerService;
import usi.justmove.gathering.services.WifiGatheringService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startService(new Intent(this, SamplingFrequencyTunerService.class));
        startService(new Intent(this, WifiGatheringService.class));
    }
}
