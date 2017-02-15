package usi.justmove.UI.fragments;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.PWBTable;

import static usi.justmove.R.id.submitButton;
import static usi.justmove.R.id.surveysPamNotificationImage;

/**
 * Created by usi on 04/02/17.
 */

public class SurveysFragment extends Fragment {
    private final int PAM_MORNING = 0;
    private final int PAM_AFTERNOON = 1;

    private LocalStorageController localController;

    private LinearLayout pamMorningQuestions;
    private LinearLayout pamAfternoonQuestions;

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


    //------------- PAM SURVEY --------------
    private int pamRotationAngle;
    private ImageView[][] images;
    private int selectedImageId;
    private ImageView selectedImage;
    private int pamCurrentPeriod;
    private int pamSurveyId;

    private CardView pamCard;
    private ImageView pamExpandableIcon;
    private LinearLayout pamBody;
    private RelativeLayout pamTitle;
    private LinearLayout pamSurvey;
    private TextView pamNoSurveyMsg;
    private ImageView pamNotificationImage;

    //morning questions
    private LinearLayout morningQuestions;
    private SeekBar morningStressSeekBar;
    private Spinner morningSleepSpinner;
    private Spinner morningLocationSpinner;
    private Spinner morningTransportationSpinner;
    //afternoon questions
    private LinearLayout afternoonQuestions;
    private Spinner afternoonSportSpinner;
    private Spinner afternoonWorkloadSpinner;
    private Spinner afternoonPeopleSpinner;
    private Spinner afternoonLocationSpinner;
    private Button pamSubmitButton;


    private boolean isPamExpanded;

    //------------- PWB SURVEY --------------
    private int pwbRotationAngle;
    private int pwbSurveyId;

    private CardView pwbCard;
    private LinearLayout pwbBody;
    private ImageView pwbExpandableIcon;
    private LinearLayout pwbSurvey;
    private TextView pwbNoSurveyMsg;
    private RelativeLayout pwbTitle;
    private ImageView pwbNotificationImage;

    private SeekBar pwbQ1SeekBar;
    private SeekBar pwbQ2SeekBar;
    private SeekBar pwbQ3SeekBar;
    private SeekBar pwbQ4SeekBar;
    private SeekBar pwbQ5SeekBar;
    private SeekBar pwbQ6SeekBar;
    private SeekBar pwbQ7SeekBar;
    private SeekBar pwbQ8SeekBar;
    private Button pwbSubmitButton;

    private boolean isPwbExpanded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        localController = new SQLiteController(getContext());

        View root = inflater.inflate(R.layout.surveys_layout, container, false);

        images = new ImageView[4][4];
        initPamSurvey(root);
        initPwbSurvey(root);
        return root;
    }

    private Cursor getTodayPwb() {
        String tableName = LocalDbUtility.getTableName(LocalTables.TABLE_PWB);
        String columnTS = LocalDbUtility.getTableColumns(LocalTables.TABLE_PWB)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(LocalTables.TABLE_PWB)[3];
        String columnNotified = LocalDbUtility.getTableColumns(LocalTables.TABLE_PWB)[4];
        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnTS + " >= " + startMillis + " AND " + columnTS + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " = " + 1, null);

        return c;
    }

    private void initPwbSurvey(View root) {
        Cursor surveys = getTodayPwb();
        surveys.moveToNext();

        pwbCard = (CardView) root.findViewById(R.id.surveysPwbCard);
        pwbBody = (LinearLayout) root.findViewById(R.id.surveysPwbBody);
        pwbExpandableIcon = (ImageView) root.findViewById(R.id.surveyPwbExpandableIcon);
        pwbSurvey = (LinearLayout) root.findViewById((R.id.surveysPwbSurvey));
        pwbNoSurveyMsg = (TextView) root.findViewById(R.id.surveysPwbNoSurvey);
        pwbTitle = (RelativeLayout) root.findViewById(R.id.surveysPwbExpandableTitle);
        pwbNotificationImage = (ImageView) root.findViewById(R.id.surveysPwbNotificationImage);

        pwbTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPwbExpanded) {
                    pwbExpandableIcon.setImageResource(R.drawable.collapse_arrow);
                    animateCollapse(pwbBody);
                    isPwbExpanded = false;
                    ObjectAnimator anim = ObjectAnimator.ofFloat(pwbExpandableIcon, "rotation", pwbRotationAngle, pwbRotationAngle + 90);
                    anim.setDuration(500);
                    anim.start();
                    pwbRotationAngle += 90;
                    pwbRotationAngle = pwbRotationAngle%90;
                } else {
                    pwbExpandableIcon.setImageResource(R.drawable.expand_arrow);
                    isPwbExpanded = true;
                    animateExpansion(pwbBody);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(pwbExpandableIcon, "rotation", -pwbRotationAngle, -1*(pwbRotationAngle + 90));
                    anim.setDuration(500);
                    anim.start();
                    pwbRotationAngle += 90;
                    pwbRotationAngle = pwbRotationAngle%90;
                }
            }
        });

        pwbRotationAngle = 0;

        if(surveys.getCount() > 0) {
            initPwbQuestions(root);
            isPwbExpanded = true;
            pwbNoSurveyMsg.setVisibility(View.GONE);
            pwbSurvey.setVisibility(View.VISIBLE);
            pwbExpandableIcon.setImageResource(R.drawable.collapse_arrow);
            pwbNotificationImage.setVisibility(View.VISIBLE);
//            pwbSurveyId = surveys.getInt(0);
        } else {
            pwbBody.setVisibility(View.GONE);
            pwbNoSurveyMsg.setVisibility(View.VISIBLE);
            pwbSurvey.setVisibility(View.GONE);
            isPwbExpanded = false;

            pwbExpandableIcon.setImageResource(R.drawable.expand_arrow);
            pwbNotificationImage.setVisibility(View.GONE);
        }

    }

    private void initPwbQuestions(View root) {
        pwbQ1SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ1SeekBar);
        pwbQ1SeekBar.setMax(7);
        pwbQ1SeekBar.setProgress(0);

        pwbQ2SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ2SeekBar);
        pwbQ2SeekBar.setMax(7);
        pwbQ2SeekBar.setProgress(0);

        pwbQ3SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ3SeekBar);
        pwbQ3SeekBar.setMax(7);
        pwbQ3SeekBar.setProgress(0);

        pwbQ4SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ4SeekBar);
        pwbQ4SeekBar.setMax(7);
        pwbQ4SeekBar.setProgress(0);

        pwbQ5SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ5SeekBar);
        pwbQ5SeekBar.setMax(7);
        pwbQ5SeekBar.setProgress(0);

        pwbQ6SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ6SeekBar);
        pwbQ6SeekBar.setMax(7);
        pwbQ6SeekBar.setProgress(0);

        pwbQ7SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ7SeekBar);
        pwbQ7SeekBar.setMax(7);
        pwbQ7SeekBar.setProgress(0);

        pwbQ8SeekBar = (SeekBar) root.findViewById(R.id.surveysPamQ8SeekBar);
        pwbQ8SeekBar.setMax(7);
        pwbQ8SeekBar.setProgress(0);

        pwbSubmitButton = (Button) root.findViewById(R.id.pwbSubmitButton);
        pwbSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePwbSurvey();
                clearPwbSurvey();
                Toast.makeText(getContext(), "Pwb survey completed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePwbSurvey() {
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        ContentValues record = new ContentValues();
        record.put(PWBTable.KEY_PWB_TS, timestamp);
        record.put(PWBTable.KEY_PWB_COMPLETED, completed);
        record.put(PWBTable.KEY_PWB_Q1, pwbQ1SeekBar.getProgress());
        record.put(PWBTable.KEY_PWB_Q2, pwbQ2SeekBar.getProgress());
        record.put(PWBTable.KEY_PWB_Q3, pwbQ3SeekBar.getProgress());
        record.put(PWBTable.KEY_PWB_Q4, pwbQ4SeekBar.getProgress());
        record.put(PWBTable.KEY_PWB_Q5, pwbQ5SeekBar.getProgress());
        record.put(PWBTable.KEY_PWB_Q6, pwbQ6SeekBar.getProgress());
        record.put(PWBTable.KEY_PWB_Q7, pwbQ7SeekBar.getProgress());
        record.put(PWBTable.KEY_PWB_Q8, pwbQ8SeekBar.getProgress());
        localController.update(PWBTable.TABLE_PWB, record, PWBTable.KEY_PWB_ID + " = " + pwbSurveyId);
    }

    private void clearPwbSurvey() {
        pwbSurvey.setVisibility(View.GONE);
        pwbNoSurveyMsg.setVisibility(View.VISIBLE);
        animateCollapse(pwbBody);
        isPwbExpanded = false;
        ObjectAnimator anim = ObjectAnimator.ofFloat(pwbExpandableIcon, "rotation", pwbRotationAngle, pwbRotationAngle + 90);
        anim.setDuration(500);
        anim.start();
        pwbRotationAngle += 90;
        pwbRotationAngle = pwbRotationAngle%90;
    }

    private Cursor getTodayPams() {
        String tableName = LocalDbUtility.getTableName(LocalTables.TABLE_PAM);
        String columnTS = LocalDbUtility.getTableColumns(LocalTables.TABLE_PAM)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(LocalTables.TABLE_PAM)[3];
        String columnNotified = LocalDbUtility.getTableColumns(LocalTables.TABLE_PAM)[13];
        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnTS + " >= " + startMillis + " AND " + columnTS + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " = " + 1, null);

        return c;
    }

    private void determinePamSurveyDayPeriod(Cursor surveys) {
        int completed;
        if(surveys.getCount() < 0) {
            while(surveys.moveToNext()) {
                completed = surveys.getInt(3);
                if(completed == 0) {
                    if(surveys.getString(12).equals("morning")) {
                        pamCurrentPeriod = PAM_MORNING;
                    } else {
                        pamCurrentPeriod = PAM_AFTERNOON;
                    }
                    pamSurveyId = surveys.getInt(0);
                    break;
                }
            }
        }
    }

    private void initPamSurvey(View root) {
        Cursor surveys = getTodayPams();

        pamCard = (CardView) root.findViewById(R.id.surveysPamCard);
        pamBody = (LinearLayout) root.findViewById(R.id.surveysPamBody);
        pamExpandableIcon = (ImageView) root.findViewById(R.id.surveyPamExpandableIcon);
        pamSurvey = (LinearLayout) root.findViewById((R.id.surveysPamSurvey));
        pamNoSurveyMsg = (TextView) root.findViewById(R.id.surveysPamNoSurvey);
        pamTitle = (RelativeLayout) root.findViewById(R.id.surveysPamExpandableTitle);
        pamNotificationImage = (ImageView) root.findViewById(R.id.surveysPamNotificationImage);

        pamTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPamExpanded) {
                    pamExpandableIcon.setImageResource(R.drawable.collapse_arrow);
                    animateCollapse(pamBody);
                    isPamExpanded = false;
                    ObjectAnimator anim = ObjectAnimator.ofFloat(pamExpandableIcon, "rotation", pamRotationAngle, pamRotationAngle + 90);
                    anim.setDuration(500);
                    anim.start();
                    pamRotationAngle += 90;
                    pamRotationAngle = pamRotationAngle%90;
                } else {
                    pamExpandableIcon.setImageResource(R.drawable.expand_arrow);
                    isPamExpanded = true;
                    animateExpansion(pamBody);
                    ObjectAnimator anim = ObjectAnimator.ofFloat(pamExpandableIcon, "rotation", -pamRotationAngle, -1*(pamRotationAngle + 90));
                    anim.setDuration(500);
                    anim.start();
                    pamRotationAngle += 90;
                    pamRotationAngle = pamRotationAngle%90;
                }
            }
        });

        pamRotationAngle = 0;
        if(surveys.getCount() > 0) {
            surveys.moveToNext();
            determinePamSurveyDayPeriod(surveys);
            initPamImages(root);
            initPamQuestions(root);
            isPamExpanded = true;
            pamNoSurveyMsg.setVisibility(View.GONE);
            pamSurvey.setVisibility(View.VISIBLE);
            pamExpandableIcon.setImageResource(R.drawable.collapse_arrow);
            pamNotificationImage.setVisibility(View.VISIBLE);
        } else {
            pamBody.setVisibility(View.GONE);
            pamNoSurveyMsg.setVisibility(View.VISIBLE);
            pamSurvey.setVisibility(View.GONE);
            isPamExpanded = false;

            pamExpandableIcon.setImageResource(R.drawable.expand_arrow);
            pamNotificationImage.setVisibility(View.GONE);
        }
    }

    private void initPamQuestions(View root) {
        morningQuestions = (LinearLayout) root.findViewById(R.id.pamSurveyMorningQuestions);
        afternoonQuestions = (LinearLayout) root.findViewById(R.id.pamSurveyAfternoonQuestions);

        if(pamCurrentPeriod == PAM_MORNING) {
            afternoonQuestions.setVisibility(View.GONE);
        } else {
            morningQuestions.setVisibility(View.GONE);
        }

        morningStressSeekBar = (SeekBar) root.findViewById(R.id.pamSurveyMorningStressSeekBar);
        morningStressSeekBar.setMax(4);
        morningStressSeekBar.setProgress(0);

        morningSleepSpinner = (Spinner) root.findViewById(R.id.pamSurveyMorningSleepSpinner);
        List<Integer> sleepHours = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            sleepHours.add(i);
        }
        ArrayAdapter<CharSequence> sleepHoursAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sleepTimePeriods, android.R.layout.simple_spinner_item);
        morningSleepSpinner.setAdapter(sleepHoursAdapter);

        morningLocationSpinner = (Spinner) root.findViewById(R.id.pamSurveyMorningLocationSpinner);
        ArrayAdapter<CharSequence> locationsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.locations, android.R.layout.simple_spinner_item);
        morningLocationSpinner.setAdapter(locationsAdapter);

        morningTransportationSpinner = (Spinner) root.findViewById(R.id.pamSurveyMorningTransportationSpinner);
        ArrayAdapter<CharSequence> transportationsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.transportations, android.R.layout.simple_spinner_item);
        morningTransportationSpinner.setAdapter(transportationsAdapter);


        afternoonQuestions = (LinearLayout) root.findViewById(R.id.pamSurveyAfternoonQuestions);

        afternoonSportSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonSportSpinner);
        ArrayAdapter<CharSequence> sportHoursAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sportTimePeriods, android.R.layout.simple_spinner_item);
        afternoonSportSpinner.setAdapter(sportHoursAdapter);

        afternoonWorkloadSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonWorkloadSpinner);
        ArrayAdapter<CharSequence> workloadHoursAdapter = ArrayAdapter.createFromResource(getContext(), R.array.workloadTimePeriods, android.R.layout.simple_spinner_item);
        afternoonWorkloadSpinner.setAdapter(workloadHoursAdapter);

        afternoonPeopleSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonPeopleSpinner);
        List<String> people = new ArrayList<>();
        int i;
        for(i = 0; i <= 10; i++) {
            people.add(Integer.toString(i));
        }
        for(i = i - 1 + 5; i <= 30; i += 5) {
            people.add(Integer.toString(i));
        }
        ArrayAdapter<String> peopleAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, people);
        afternoonPeopleSpinner.setAdapter(peopleAdapter);

        afternoonLocationSpinner = (Spinner) root.findViewById(R.id.pamSurveyAfternoonLocationSpinner);
        ArrayAdapter<CharSequence> afternoonLocationsAdapter = ArrayAdapter.createFromResource(getContext(), R.array.locations, android.R.layout.simple_spinner_item);
        afternoonLocationSpinner.setAdapter(afternoonLocationsAdapter);

        pamSubmitButton = (Button) root.findViewById(R.id.pamSubmitButton);
        pamSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pamCurrentPeriod == PAM_MORNING) {
                    saveMorningPam();
                } else {
                    saveAfternoonPam();
                }
                clearPamSurvey();
                Toast.makeText(getContext(), "Pam survey completed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPamImages(View root) {
        int imageViewId;
        Random r = new Random();
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                imageViewId = getResources().getIdentifier("pamSurveyImages_" + (i+1) + "_" + (j+1), "id", getContext().getPackageName());
                images[i][j] = (ImageView) root.findViewById(imageViewId);
                final int imagejId = r.nextInt(3);
                final int imageiId = (i*images[i].length)+j;
                images[i][j].setImageResource(pamImages[imageiId][imagejId]);
                images[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlePamImageClick(v, imageiId, imagejId);
                    }
                });
            }
        }
    }

    private void clearPamSurvey() {
        pamNoSurveyMsg.setVisibility(View.VISIBLE);
        pamSurvey.setVisibility(View.GONE);
        animateCollapse(pamBody);
        isPamExpanded = false;
        ObjectAnimator anim = ObjectAnimator.ofFloat(pamExpandableIcon, "rotation", pamRotationAngle, pamRotationAngle + 90);
        anim.setDuration(500);
        anim.start();
        pamRotationAngle += 90;
        pamRotationAngle = pamRotationAngle%90;
    }

    private void saveMorningPam() {
        int imageId = selectedImageId;
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        int stress = morningStressSeekBar.getProgress();
        float sleep = parseTimePeriod(morningSleepSpinner.getSelectedItem().toString());
        String location = morningLocationSpinner.getSelectedItem().toString();
        String transportation = morningLocationSpinner.getSelectedItem().toString();
        ContentValues record = new ContentValues();
        record.put(PAMTable.KEY_PAM_TS, timestamp);
        record.put(PAMTable.KEY_PAM_COMPLETED, completed);
        record.put(PAMTable.KEY_PAM_STRESS, stress);
        record.put(PAMTable.KEY_PAM_SLEEP, sleep);
        record.put(PAMTable.KEY_PAM_LOCATION, location);
        record.put(PAMTable.KEY_PAM_TRANSPORTATION, transportation);
        record.put(PAMTable.KEY_PAM_IMAGE_ID, imageId);
        localController.update(PAMTable.TABLE_PAM, record, PAMTable.KEY_PAM_ID + " = " + pamSurveyId);
    }

    private void saveAfternoonPam() {
        int imageId = selectedImageId;
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        float sport = parseTimePeriod(afternoonSportSpinner.getSelectedItem().toString());
        float workload = parseTimePeriod(afternoonWorkloadSpinner.getSelectedItem().toString());
        float people = parseTimePeriod(afternoonPeopleSpinner.getSelectedItem().toString());
        String location = morningLocationSpinner.getSelectedItem().toString();
        ContentValues record = new ContentValues();
        record.put(PAMTable.KEY_PAM_TS, timestamp);
        record.put(PAMTable.KEY_PAM_COMPLETED, completed);
        record.put(PAMTable.KEY_PAM_ACTIVITIES, sport);
        record.put(PAMTable.KEY_PAM_WORKLOAD, workload);
        record.put(PAMTable.KEY_PAM_LOCATION, location);
        record.put(PAMTable.KEY_PAM_SOCIAL, people);
        record.put(PAMTable.KEY_PAM_IMAGE_ID, imageId);
        localController.update(PAMTable.TABLE_PAM, record, PAMTable.KEY_PAM_ID + " = " + pamSurveyId);
    }

    private float parseTimePeriod(String period) {
        if(period.equals("None")) {
            return 0;
        }
        float hours = 0;
        float mins = 0;

        String[] split = period.split(" ");

        if(split.length == 2) {
            if(split[1].equals("hour") || split[1].equals("hours")) {
                hours = Float.parseFloat(split[0]);
            } else {
                mins = Float.parseFloat(split[0]);
            }
        } else {
            hours = Float.parseFloat(split[0]);
            mins = Float.parseFloat(split[2]);
        }
        return hours + mins/60;
    }



    private void handlePamImageClick(View v, int i, int j) {
        selectedImageId = i;
        if(selectedImage != null) {
            selectedImage.setBackgroundResource(0);
        }

        selectedImage = (ImageView) v;
        v.setBackgroundResource(R.drawable.pam_image_border);
    }

    private void animateCollapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void animateExpansion(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
