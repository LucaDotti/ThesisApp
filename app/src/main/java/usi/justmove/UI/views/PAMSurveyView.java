package usi.justmove.UI.views;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shawnlin.numberpicker.NumberPicker;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import usi.justmove.R;
import usi.justmove.UI.ExpandableLayout;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.handle.SurveyEventReceiver;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tableHandlers.PAMSurvey;
import usi.justmove.local.database.tableHandlers.Survey;
import usi.justmove.local.database.tableHandlers.TableHandler;
import usi.justmove.local.database.LocalTables;
import usi.justmove.local.database.tables.PAMTable;
import usi.justmove.local.database.tables.UserTable;

/**
 * Created by usi on 20/02/17.
 */

public class PAMSurveyView extends LinearLayout {
    private OnPamSurveyCompletedCallback callback;
    private final int PAM_MORNING = 0;
    private final int PAM_AFTERNOON = 1;
    private LocalTables pamTable;
    private boolean hasSurvey;

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

    static int[] checkboxIDs = {
            R.id.pamSurveyTransp_checkbox_0,
            R.id.pamSurveyTransp_checkbox_1,
            R.id.pamSurveyTransp_checkbox_2,
            R.id.pamSurveyTransp_checkbox_3,
            R.id.pamSurveyTransp_checkbox_4,
            R.id.pamSurveyTransp_checkbox_5,
    };

    static int[] morningSleepRButtons = {
            R.id.pamSurveySleep_none_radioButton,
            R.id.pamSurveySleep_1_3_radioButton,
            R.id.pamSurveySleep_4_6_radioButton,
            R.id.pamSurveySleep_7_9_radioButton
    };

    static int[] morningLocationRButtons = {
        R.id.pamSurveyLocation_uni_radioButton,
        R.id.pamSurveyLocation_pub_radioButton,
        R.id.pamSurveyLocation_other_radioButton,
        R.id.pamSurveyLocation_restaurant_radioButton,
        R.id.pamSurveyLocation_gym_radioButton,
        R.id.pamSurveyLocation_other_radioButton
    };

    static int[] afternoonSportRButtons = {
        R.id.pamSurveySport_none_radioButton,
        R.id.pamSurveySport_10_30_radioButton,
        R.id.pamSurveySport_1_2_radioButton,
        R.id.pamSurveySport_2_p_radioButton
    };

    static int[] afternoonWorkloadRButtons = {
        R.id.pamSurveyUni_none_radioButton,
        R.id.pamSurveyUni_1_2_radioButton,
        R.id.pamSurveyUni_3_4_radioButton,
        R.id.pamSurveyUni_5_6_radioButton,
        R.id.pamSurveyUni_7_8_radioButton,
        R.id.pamSurveyUni_8_p_radioButton
    };

    static int[] afternoonLocationRButtons = {
            R.id.pamSurveyLocationA_uni_radioButton,
            R.id.pamSurveyLocationA_pub_radioButton,
            R.id.pamSurveyLocationA_other_radioButton,
            R.id.pamSurveyLocationA_restaurant_radioButton,
            R.id.pamSurveyLocationA_gym_radioButton,
            R.id.pamSurveyLocationA_other_radioButton
    };

    static int[] afternoonPeopleRButtons = {
            R.id.pamSurveyPeople_none_radioButton,
            R.id.pamSurveyPeople_1_5_radioButton,
            R.id.pamSurveyPeople_6_10_radioButton,
            R.id.pamSurveyPeople_10_p_radioButton
    };

    private ImageView[][] images;
    private int selectedImageId;
    private ImageView selectedImage;

    private int currentPeriod;
    private Survey currentSurvey;

    private LocalStorageController localController;

    //morning questions
    private LinearLayout morningQuestions;
    private DiscreteSeekBar morningStressSeekBar;
    private List<RadioButton> morningSleepRadioGroup;
    private RadioButton morningSleepSelectedRButton;
    private List<RadioButton> morningLocationRadioGroup;
    private RadioButton morningLocationSelectedRButton;
    private List<CheckBox> morningTransportationCheckboxes;
    //afternoon questions
    private LinearLayout afternoonQuestions;
    private List<RadioButton> afternoonSportRadioGroup;
    private RadioButton afternoonSportSelectedRButton;
    private List<RadioButton> afternoonWorkloadRadioGroup;
    private RadioButton afternoonPeopleSelectedRButton;
    private List<RadioButton> afternoonPeopleRadioGroup;
    private RadioButton afternoonWorkloadSelectedRButton;
    private List<RadioButton> afternoonLocationRadioGroup;
    private RadioButton afternoonLocationSelectedRButton;

    private Button submitButton;

    private Context context;

    private LinearLayout imagesContainer;
    private LinearLayout questions;

    private LinearLayout questionsLayout;
    private ExpandableLayout expandableLayout;
    private View titleView;

    private View animation;

    public PAMSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        localController = SQLiteController.getInstance(context);

        this.context = context;
        pamTable = LocalTables.TABLE_PAM;
        images =  new ImageView[4][4];

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pam_layout, this, true);

        expandableLayout = (ExpandableLayout) findViewById(R.id.pamViewExpandableLayout);
        titleView = inflater.inflate(R.layout.expandable_layout_title, null);
        questionsLayout = (LinearLayout) inflater.inflate(R.layout.pam_questions_layout, null);
        morningQuestions = (LinearLayout) inflater.inflate(R.layout.pam_morning_questions_layout, null);
        afternoonQuestions = (LinearLayout) inflater.inflate(R.layout.pam_afternoon_questions_layout, null);
        initView();
    }

    private void notifySurveyCompleted() {
        Intent intent = new Intent(context, SurveyEventReceiver.class);
        intent.putExtra("survey_id", currentSurvey.id);
        intent.setAction(SurveyEventReceiver.SURVEY_COMPLETED_INTENT);

        Calendar c = Calendar.getInstance();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) c.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private Survey getCurrentSurvey() {
        Survey survey = Survey.getAvailableSurvey(SurveyType.PAM);

        if(survey != null) {
            return survey;
        }

        survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);

        if(survey != null) {
            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);

            if(children.containsKey(SurveyType.PAM)) {
                return survey;
            }
        }

        return null;
    }

    private void initView() {
        selectedImageId = -1;

        expandableLayout.getBodyView().removeAllViews();
        expandableLayout.getTitleView().removeAllViews();
        expandableLayout.setTitleView(titleView);
        expandableLayout.setTitleText(R.id.surveysTitle, "PAM");

        imagesContainer = (LinearLayout) questionsLayout.findViewById(R.id.pamSurveyImagesContainer);
        questions = (LinearLayout) questionsLayout.findViewById(R.id.pamSurveyQuestions);

        currentSurvey = getCurrentSurvey();

        if(currentSurvey != null) {
            determineSurveyPeriod(currentSurvey);
            initPamImages();
            initQuestions();

            submitButton = (Button) questionsLayout.findViewById(R.id.pamSubmitButton);
            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentPeriod == PAM_MORNING) {
                        saveMorningPam();
                    } else {
                        saveAfternoonPam();
                    }

                    expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
                    expandableLayout.setNoContentMsg("No PAM survey available");
                    expandableLayout.showNoContentMsg();
                    expandableLayout.collapse();
                    callback.onPamSurveyCompletedCallback();

                    if(!currentSurvey.grouped) {
                        notifySurveyCompleted();
                    }

                    expandableLayout.stopBlink();

                    Toast.makeText(getContext(), "PAM survey completed", Toast.LENGTH_SHORT).show();
                }
            });

            expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
            expandableLayout.setBodyView(questionsLayout);
            expandableLayout.showBody();
            hasSurvey = true;
        } else {
            expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
            expandableLayout.setNoContentMsg("No PAM survey available");
            expandableLayout.showNoContentMsg();
        }
    }

    private void initQuestions() {
        questions.removeAllViews();
        if(currentPeriod == PAM_MORNING) {
            initMorningQuestions();
            questions.addView(morningQuestions);
        } else {
            initAfternoonQuestions();
            questions.addView(afternoonQuestions);
        }
    }

    private void initPamImages() {
        int imageViewId;
        Random r = new Random();
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                imageViewId = getResources().getIdentifier("pamSurveyImages_" + (i+1) + "_" + (j+1), "id", getContext().getPackageName());
                images[i][j] = (ImageView) questionsLayout.findViewById(imageViewId);
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

//    private void init() {
//        selectedImageId = -1;
//        expandableLayout.setTitleView(titleView);
//        expandableLayout.setTitleText(R.id.surveysTitle, "PAM");
//
//        imagesContainer = (LinearLayout) questionsLayout.findViewById(R.id.pamSurveyImagesContainer);
//        questions = (LinearLayout) questionsLayout.findViewById(R.id.pamSurveyQuestions);
//
//        Survey survey = Survey.getAvailableSurvey(SurveyType.PAM);
//
//        if(survey == null) {
//            survey = Survey.getAvailableSurvey(SurveyType.GROUPED_SSPP);
//        }
//
//        if(survey != null) {
//
//            Map<SurveyType, TableHandler> children =  survey.getChildSurveys(false);
//
//            if(children.containsKey(SurveyType.PAM)) {
//                determineSurveyPeriod(survey);
//                currentSurvey = survey;
//                initPamImages();
//                if(currentPeriod == PAM_MORNING) {
//                    initMorningQuestions();
//                    afternoonQuestions.setVisibility(GONE);
//                } else {
//                    initAfternoonQuestions();
//                    morningQuestions.setVisibility(GONE);
//                }
//
//
//
//                expandableLayout.setBodyView(questionsLayout);
////                expandableLayout.expand();
////                expandableLayout.showBody();
//                hasSurvey = true;
//
//                return;
//            }
//
//        }
//
//        imagesContainer.setVisibility(GONE);
//        questions.setVisibility(GONE);
//        expandableLayout.setTitleImage(R.id.surveysNotificationImage, 0);
//        expandableLayout.setNoContentMsg("No PAM survey available");
//        expandableLayout.showNoContentMsg();
//    }



    private void determineSurveyPeriod(Survey survey) {
        DateTime scheduleTime = new DateTime(survey.scheduledAt * 1000);
        DateTime afternoon = new DateTime().withTime(13, 0, 0, 0);
        if(scheduleTime.isBefore(afternoon)) {
            currentPeriod = PAM_MORNING;
        } else {
            currentPeriod = PAM_AFTERNOON;
        }
    }

    private void initMorningQuestions() {
        morningStressSeekBar = (DiscreteSeekBar) morningQuestions.findViewById(R.id.pamSurveyMorningStressSeekBar);

        morningSleepRadioGroup = new ArrayList<>();
        RadioButton current;
        for(int i = 0; i < morningSleepRButtons.length; i++) {
            current = (RadioButton) morningQuestions.findViewById(morningSleepRButtons[i]);
            current.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(morningSleepSelectedRButton != null) {
                        morningSleepSelectedRButton.setChecked(false);

                    }

                    morningSleepSelectedRButton = (RadioButton) v;
                    morningSleepSelectedRButton.setChecked(true);
                }
            });
            morningSleepRadioGroup.add(current);
        }

        morningLocationRadioGroup = new ArrayList<>();
        for(int i = 0; i < morningLocationRButtons.length; i++) {
            current = (RadioButton) morningQuestions.findViewById(morningLocationRButtons[i]);
            current.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(morningLocationSelectedRButton != null) {
                        morningLocationSelectedRButton.setChecked(false);
                    }
                    morningLocationSelectedRButton = (RadioButton) v;
                    morningLocationSelectedRButton.setChecked(true);
                }
            });
            morningSleepRadioGroup.add(current);
        }

        morningTransportationCheckboxes = new ArrayList<>();

        for(int i = 0; i < checkboxIDs.length; i++) {
            morningTransportationCheckboxes.add((CheckBox) morningQuestions.findViewById(checkboxIDs[i]));
        }
    }

    private void initAfternoonQuestions() {
        afternoonSportRadioGroup =  new ArrayList<>();
        RadioButton current;
        for(int i = 0; i < afternoonSportRButtons.length; i++) {
            current = (RadioButton) afternoonQuestions.findViewById(afternoonSportRButtons[i]);
            current.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(afternoonSportSelectedRButton != null) {
                        afternoonSportSelectedRButton.setChecked(false);
                    }
                    afternoonSportSelectedRButton = (RadioButton) v;
                    afternoonSportSelectedRButton.setChecked(true);
                }
            });
            afternoonSportRadioGroup.add(current);
        }

        afternoonLocationRadioGroup =  new ArrayList<>();
        for(int i = 0; i < afternoonLocationRButtons.length; i++) {
            current = (RadioButton) afternoonQuestions.findViewById(afternoonLocationRButtons[i]);
            current.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(afternoonLocationSelectedRButton != null) {
                        afternoonLocationSelectedRButton.setChecked(false);
                    }
                    afternoonLocationSelectedRButton = (RadioButton) v;
                    afternoonLocationSelectedRButton.setChecked(true);
                }
            });
            afternoonLocationRadioGroup.add(current);
        }

        afternoonPeopleRadioGroup =  new ArrayList<>();
        for(int i = 0; i < afternoonPeopleRButtons.length; i++) {
            current = (RadioButton) afternoonQuestions.findViewById(afternoonPeopleRButtons[i]);
            current.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(afternoonPeopleSelectedRButton != null) {
                        afternoonPeopleSelectedRButton.setChecked(false);
                    }
                    afternoonPeopleSelectedRButton = (RadioButton) v;
                    afternoonPeopleSelectedRButton.setChecked(true);
                }
            });
            afternoonPeopleRadioGroup.add(current);
        }

        if(isUserStudent()) {
            TextView uniQ = (TextView) afternoonQuestions.findViewById(R.id.pamSurveyAfternoonUniQ);
            uniQ.setText(context.getString(R.string.pam_q2_afternoon_2));
        }

        afternoonWorkloadRadioGroup = new ArrayList<>();
        for(int i = 0; i < afternoonWorkloadRButtons.length; i++) {
            current = (RadioButton) afternoonQuestions.findViewById(afternoonWorkloadRButtons[i]);
            current.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(afternoonWorkloadSelectedRButton != null) {
                        afternoonWorkloadSelectedRButton.setChecked(false);
                    }
                    afternoonWorkloadSelectedRButton = (RadioButton) v;
                    afternoonWorkloadSelectedRButton.setChecked(true);
                }
            });
            afternoonWorkloadRadioGroup.add(current);
        }
    }

    private boolean isUserStudent() {
        Cursor c = localController.rawQuery("SELECT * FROM " + UserTable.TABLE_USER, null);

        if(c.getCount() > 0) {
            c.moveToFirst();

            if(c.getString(7).equals("Other")) {
                return false;
            } else {
                return true;
            }
        }

        return true;

    }

    private void handlePamImageClick(View v, int i, int j) {
        selectedImageId = i;
        if(selectedImage != null) {
            selectedImage.setBackgroundResource(0);
        }

        selectedImage = (ImageView) v;
        v.setBackgroundResource(R.drawable.pam_image_border);
    }

    private void saveMorningPam() {
        int imageId = selectedImageId;
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        int stress = morningStressSeekBar.getProgress();
        String sleep;
        if(morningSleepSelectedRButton != null) {
            sleep = parseStringChoice(morningSleepSelectedRButton.getId());
        } else {
            sleep = "no answer";
        }

        String location;
        if(morningLocationSelectedRButton != null) {
            location = parseStringChoice(morningLocationSelectedRButton.getId());
        } else {
            location = "no answer";
        }

        String transportation = getTransportationList();
        ContentValues pamAttributes = new ContentValues();
        pamAttributes.put(PAMTable.KEY_PAM_PARENT_SURVEY_ID, currentSurvey.id);
        pamAttributes.put(PAMTable.KEY_PAM_COMPLETED, true);
        pamAttributes.put(PAMTable.KEY_PAM_PERIOD, "morning");
        pamAttributes.put(PAMTable.KEY_PAM_STRESS, stress);
        pamAttributes.put(PAMTable.KEY_PAM_SLEEP, sleep);
        pamAttributes.put(PAMTable.KEY_PAM_LOCATION, location);
        pamAttributes.put(PAMTable.KEY_PAM_TRANSPORTATION, transportation);
        pamAttributes.put(PAMTable.KEY_PAM_IMAGE_ID, imageId);


//        PAMSurvey pamSurvey = new PAMSurvey(true);
//        pamSurvey.setAttributes(pamAttributes);

        Survey survey = (Survey) Survey.findByPk(currentSurvey.id);
        survey.getSurveys().get(SurveyType.PAM).setAttributes(pamAttributes);


        if(!currentSurvey.grouped) {
            survey.completed = true;
            survey.ts = System.currentTimeMillis();
        }

        survey.save();
    }

    private void saveAfternoonPam() {
        int imageId = selectedImageId;
        long timestamp = System.currentTimeMillis();
        int completed = 1;
        String sport;
        if(afternoonSportSelectedRButton != null) {
            sport = parseStringChoice(afternoonSportSelectedRButton.getId());
        } else {
            sport = "no answer";
        }

        String workload;
        if(afternoonWorkloadSelectedRButton != null) {
            workload = parseStringChoice(afternoonWorkloadSelectedRButton.getId());
        } else {
            workload = "no answer";
        }

        String people;
        if(afternoonPeopleSelectedRButton != null) {
            people = parseStringChoice(afternoonPeopleSelectedRButton.getId());
        } else {
            people = "no answer";
        }

        String location;
        if(afternoonLocationSelectedRButton != null) {
            location = parseStringChoice(afternoonLocationSelectedRButton.getId());
        } else {
            location = "no answer";
        }

        ContentValues pamAttributes = new ContentValues();
        pamAttributes.put(PAMTable.KEY_PAM_PARENT_SURVEY_ID, currentSurvey.id);
        pamAttributes.put(PAMTable.KEY_PAM_COMPLETED, true);
        pamAttributes.put(PAMTable.KEY_PAM_ACTIVITIES, sport);
        pamAttributes.put(PAMTable.KEY_PAM_PERIOD, "afternoon");
        pamAttributes.put(PAMTable.KEY_PAM_WORKLOAD, workload);
        pamAttributes.put(PAMTable.KEY_PAM_LOCATION, location);
        pamAttributes.put(PAMTable.KEY_PAM_SOCIAL, people);
        pamAttributes.put(PAMTable.KEY_PAM_IMAGE_ID, imageId);

        PAMSurvey pamSurvey = new PAMSurvey(true);
        pamSurvey.setAttributes(pamAttributes);

        Survey survey = (Survey) Survey.findByPk(currentSurvey.id);
        survey.getSurveys().get(SurveyType.PAM).setAttributes(pamAttributes);


        if(!currentSurvey.grouped) {
            survey.completed = true;
            survey.ts = System.currentTimeMillis();
        }

        survey.save();
    }

    private String parseStringChoice(int id) {
        switch(id) {
            case R.id.pamSurveyLocation_gym_radioButton: return "Gym";
            case R.id.pamSurveyLocation_home_radioButton: return "Home";
            case R.id.pamSurveyLocation_other_radioButton: return "Other";
            case R.id.pamSurveyLocation_pub_radioButton: return "pub";
            case R.id.pamSurveyLocation_restaurant_radioButton: return "restaurant";
            case R.id.pamSurveyLocation_uni_radioButton: return "Uni";
            case R.id.pamSurveyLocationA_gym_radioButton: return "Gym";
            case R.id.pamSurveyLocationA_home_radioButton: return "Home";
            case R.id.pamSurveyLocationA_other_radioButton: return "Other";
            case R.id.pamSurveyLocationA_pub_radioButton: return "pub";
            case R.id.pamSurveyLocationA_restaurant_radioButton: return "restaurant";
            case R.id.pamSurveyLocationA_uni_radioButton: return "Uni";
            case R.id.pamSurveyTransp_checkbox_0: return "Walking";
            case R.id.pamSurveyTransp_checkbox_1: return "Bus";
            case R.id.pamSurveyTransp_checkbox_2: return "Train";
            case R.id.pamSurveyTransp_checkbox_3: return "Bicycle";
            case R.id.pamSurveyTransp_checkbox_4: return "Car";
            case R.id.pamSurveyTransp_checkbox_5: return "Other";
            case R.id.pamSurveySleep_none_radioButton:
            case R.id.pamSurveySport_none_radioButton:
            case R.id.pamSurveyUni_none_radioButton:
            case R.id.pamSurveyPeople_none_radioButton:
                return "None";
            case R.id.pamSurveySleep_1_3_radioButton: return "1 - 3 h";
            case R.id.pamSurveySleep_4_6_radioButton: return "4 - 6 h";
            case R.id.pamSurveySleep_7_9_radioButton: return "7 - 9 h";
            case R.id.pamSurveySport_10_30_radioButton: return "10 - 30 min";
            case R.id.pamSurveySport_1_2_radioButton:
            case R.id.pamSurveyUni_1_2_radioButton:
                return "1 -2 h";
            case R.id.pamSurveySport_2_p_radioButton: return "2+ h";
            case R.id.pamSurveyUni_3_4_radioButton: return "3 - 4 h";
            case R.id.pamSurveyUni_5_6_radioButton: return "5 - 6 h";
            case R.id.pamSurveyUni_7_8_radioButton: return "7 - 8 h";
            case R.id.pamSurveyUni_8_p_radioButton: return "8+ h";
            case R.id.pamSurveyPeople_1_5_radioButton: return "1 - 5";
            case R.id.pamSurveyPeople_6_10_radioButton: return "6 - 10";
            case R.id.pamSurveyPeople_10_p_radioButton: return "10 +";
            default: return "undefined";
        }
    }

    private String getTransportationList() {
        String transps= "";

        for(CheckBox c: morningTransportationCheckboxes) {
            if(c.isChecked()) {
                transps += parseStringChoice(c.getId()) + ", ";
            }
        }

        return transps.substring(0, transps.length()-2);
    }

    public interface OnPamSurveyCompletedCallback {
        void onPamSurveyCompletedCallback();
    }

    public void setCallback(OnPamSurveyCompletedCallback callback) {
        this.callback = callback;
    }

    public boolean hasSurvey() {
        return hasSurvey;
    }

    public void expand() {
        expandableLayout.expand();
        expandableLayout.showBody();
    }

    public void reInit() {
//        expandableLayout.removeAllViews();
//        expandableLayout.removeContent();
//        expandableLayout.removeBody();
//        expandableLayout.setTitleImage(R.id.surveysNotificationImage, R.drawable.notification_1);
//        expandableLayout.removeBody();
//        expandableLayout.setBodyView(questionsLayout);
//        expandableLayout.showBody();
//        expandableLayout.setBodyView(questionsLayout);
//        init();
//        expandableLayout.invalidate();
//        invalidate();

        initView();

    }

    public void blink() {
        expandableLayout.startBlink();
    }

    public void stopBlink() {
        expandableLayout.stopBlink();
    }
}
