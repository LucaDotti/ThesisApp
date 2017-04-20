package usi.memotion.UI.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usi.memotion.R;
import usi.memotion.local.database.controllers.LocalStorageController;

/**
 * Created by usi on 08/02/17.
 */

public class PamSurveyView extends LinearLayout {
    static int[][] pamImages = {
        {R.drawable.afraid_1_1, R.drawable.afraid_1_2, R.drawable.afraid_1_3},
        {R.drawable.tense_2_1, R.drawable.tense_2_2, R.drawable.tense_2_1},
        {R.drawable.excited_3_1, R.drawable.excited_3_2, R.drawable.excited_3_3},
        {R.drawable.delighted_4_1, R.drawable.delighted_4_2, R.drawable.delighted_4_3},
        {R.drawable.frustrated_5_1, R.drawable.frustrated_5_2, R.drawable.frustrated_5_3},
        {R.drawable.angry_6_1, R.drawable.angry_6_2, R.drawable.angry_6_3},
        {R.drawable.happy_7_1, R.drawable.happy_7_2, R.drawable.happy_7_3},
        {R.drawable.glad_8_1, R.drawable.glad_8_2, R.drawable.glad_8_3},
        {R.drawable.miserable_9_1, R.drawable.miserable_9_2, R.drawable.miserable_9_3},
        {R.drawable.sad_10_1, R.drawable.sad_10_2, R.drawable.sad_10_3},
        {R.drawable.calm_11_1, R.drawable.calm_11_2, R.drawable.calm_11_3},
        {R.drawable.satisfied_12_1, R.drawable.satisfied_12_2, R.drawable.satisfied_12_3},
        {R.drawable.gloomy_13_1, R.drawable.gloomy_13_2, R.drawable.gloomy_13_3},
        {R.drawable.tired_14_1, R.drawable.tired_14_2, R.drawable.tired_14_3},
        {R.drawable.sleepy_15_1, R.drawable.sleepy_15_2, R.drawable.sleepy_15_3},
        {R.drawable.serene_16_1, R.drawable.serene_16_2, R.drawable.serene_16_3}
    };

    private Context context;
    private LocalStorageController localController;
    private GridView imageGrid;
    private Button submitButton;

    private LinearLayout morningQuestions;
    private SeekBar morningStressSeekBar;
    private Spinner morningSpleepSpinner;
    private Spinner morningLocationSpinner;
    private Spinner morningTransportationSpinner;

    private LinearLayout afternoonQuestions;
    private Spinner afternoonSportSpinner;
    private Spinner afternoonWorkloadSpinner;
    private Spinner afternoonPeopleSpinner;
    private Spinner afternoonLocationSpinner;



    public PamSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }


    private void init() {
//        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = li.inflate(R.layout.surveys_pam_layout, this, true);
//        imageGrid = (GridView) root.findViewById(R.id.pamImageGrid);
        imageGrid.setVerticalScrollBarEnabled(false);
        submitButton = (Button) root.findViewById(R.id.pamSubmitButton);

        morningQuestions = (LinearLayout) root.findViewById(R.id.pamSurveyMorningQuestions);

        morningStressSeekBar = (SeekBar) root.findViewById(R.id.pamSurveyMorningStressSeekBar);
        morningStressSeekBar.setMax(4);
        morningStressSeekBar.setProgress(0);

        morningSpleepSpinner = (Spinner) root.findViewById(R.id.pamSurveyMorningSleepSpinner);
        List<Integer> sleepHours = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            sleepHours.add(i);
        }
        ArrayAdapter<Integer> sleepHoursAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_dropdown_item, sleepHours);
        morningSpleepSpinner.setAdapter(sleepHoursAdapter);

        morningLocationSpinner = (Spinner) root.findViewById(R.id.pamSurveyMorningLocationSpinner);
        ArrayAdapter<CharSequence> locationsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.locations, android.R.layout.simple_spinner_item);
        morningLocationSpinner.setAdapter(locationsAdapter);

        morningTransportationSpinner = (Spinner) root.findViewById(R.id.pamSurveyMorningTransportationSpinner);
        ArrayAdapter<CharSequence> transportationsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.transportations, android.R.layout.simple_spinner_item);
        morningTransportationSpinner.setAdapter(transportationsAdapter);


        afternoonQuestions = (LinearLayout) root.findViewById(R.id.pamSurveyAfternoonQuestions);

        afternoonSportSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonSportSpinner);
        List<Integer> sportHours = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            sleepHours.add(i);
        }
//        ArrayAdapter<Integer> sleepHoursAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_dropdown_item, sleepHours);

        afternoonWorkloadSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonWorkloadSpinner);
        afternoonPeopleSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonPeopleSpinner);
        afternoonLocationSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonLocationSpinner);
        ArrayAdapter<CharSequence> afternoonLocationsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.locations, android.R.layout.simple_spinner_item);
        afternoonLocationSpinner.setAdapter(afternoonLocationsAdapter);
        initSurvey("a");
    }

    public void initSurvey(String dayPeriod) {
        Random r = new Random();
        int[] images = new int[16];

        for(int i = 0; i < images.length; i++) {
            images[i] = pamImages[i][r.nextInt(3)];
        }

        imageGrid.setAdapter(new PamImageAdapter(images, context));
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
////        int desiredWidth = 100;
////        int desiredHeight = 100;
////
////        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
////        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
////        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
////        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
////
////        int width;
////        int height;
////
////        //Measure Width
////        if (widthMode == MeasureSpec.EXACTLY) {
////            //Must be this size
////            width = widthSize;
////        } else if (widthMode == MeasureSpec.AT_MOST) {
////            //Can't be bigger than...
////            width = Math.min(desiredWidth, widthSize);
////        } else {
////            //Be whatever you want
////            width = desiredWidth;
////        }
////
////        //Measure Height
////        if (heightMode == MeasureSpec.EXACTLY) {
////            //Must be this size
////            height = heightSize;
////        } else if (heightMode == MeasureSpec.AT_MOST) {
////            //Can't be bigger than...
////            height = Math.min(desiredHeight, heightSize);
////        } else {
////            //Be whatever you want
////            height = desiredHeight;
////        }
//
//        //MUST CALL THIS
//        setMeasuredDimension(, heightMeasureSpec);
//    }


}
