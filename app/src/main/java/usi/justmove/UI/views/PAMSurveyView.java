package usi.justmove.UI.views;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.shawnlin.numberpicker.NumberPicker;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import usi.justmove.R;
import usi.justmove.UI.fragments.SurveysFragment;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.PAMTable;

/**
 * Created by usi on 20/02/17.
 */

public class PAMSurveyView extends LinearLayout {
    private OnPamSurveyCompletedCallback callback;
    private final int PAM_MORNING = 0;
    private final int PAM_AFTERNOON = 1;
    private LocalTables pamTable;

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

    private ImageView[][] images;
    private int selectedImageId;
    private ImageView selectedImage;

    private int currentPeriod;
    private int currentSurveyId;

    private LocalStorageController localController;

    //morning questions
    private LinearLayout morningQuestions;
    private SeekBar morningStressSeekBar;
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
    private RadioButton afternoonWorkloadSelectedRButton;
    private NumberPicker afternoonPeoplePicker;
    private List<RadioButton> afternoonLocationRadioGroup;
    private RadioButton afternoonLocationSelectedRButton;

    private Button submitButton;

    private Context context;

    private LinearLayout imagesContainer;
    private LinearLayout questions;

    public PAMSurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.pam_layout, this, true);
        this.context = context;

        pamTable = LocalTables.TABLE_PAM;
        images =  new ImageView[4][4];
        init();
    }

    private void init() {
        localController = SQLiteController.getInstance(context);
        imagesContainer = (LinearLayout) findViewById(R.id.pamSurveyImagesContainer);
        questions = (LinearLayout) findViewById(R.id.pamSurveyQuestions);
        morningQuestions = (LinearLayout) findViewById(R.id.pamSurveyMorningQuestions);
        afternoonQuestions = (LinearLayout) findViewById(R.id.pamSurveyAfternoonQuestions);

        Cursor surveys = getTodayPams();

        if(surveys.getCount() > 0) {
            surveys.moveToFirst();
            determineSurveyPeriod(surveys);
            currentSurveyId = surveys.getInt(0);
            initPamImages();
            if(currentPeriod == PAM_MORNING) {
                initMorningQuestions();
                afternoonQuestions.setVisibility(GONE);
            } else {
                initAfternoonQuestions();
                morningQuestions.setVisibility(GONE);
            }

            submitButton = (Button) findViewById(R.id.pamSubmitButton);
            submitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentPeriod == PAM_MORNING) {
                        saveMorningPam();
                    } else {
                        saveAfternoonPam();
                    }
                    callback.onPamSurveyCompletedCallback();
                }
            });
            surveys.close();
        } else {
            imagesContainer.setVisibility(GONE);
            questions.setVisibility(GONE);
        }
        invalidate();
    }

    private Cursor getTodayPams() {
        String tableName = LocalDbUtility.getTableName(pamTable);
        String indexColumn = LocalDbUtility.getTableColumns(pamTable)[0];
        String columnSchedule = LocalDbUtility.getTableColumns(pamTable)[2];
        String columnCompleted = LocalDbUtility.getTableColumns(pamTable)[3];
        String columnNotified = LocalDbUtility.getTableColumns(pamTable)[4];
        String columnExpired = LocalDbUtility.getTableColumns(pamTable)[5];

        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;
        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnSchedule + " >= " + startMillis + " AND " + columnSchedule + " <= " + endMillis
                + " AND " + columnCompleted + " = " + 0 + " AND " + columnNotified + " > " + 0 + " AND " + columnExpired + " = " + 0 +
                " ORDER BY " + indexColumn + " ASC LIMIT 1", null);

        return c;
    }

    private void determineSurveyPeriod(Cursor surveys) {
        surveys.moveToFirst();
        String period = surveys.getString(6);

        if (period.equals("morning")) {
            currentPeriod = PAM_MORNING;
        } else {
            currentPeriod = PAM_AFTERNOON;
        }
    }

    private void initMorningQuestions() {
        morningStressSeekBar = (SeekBar) findViewById(R.id.pamSurveyMorningStressSeekBar);
        morningStressSeekBar.setMax(4);
        morningStressSeekBar.setProgress(0);

        morningSleepRadioGroup = new ArrayList<>();
        RadioButton current;
        for(int i = 0; i < morningSleepRButtons.length; i++) {
            current = (RadioButton) findViewById(morningSleepRButtons[i]);
            current.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(morningSleepSelectedRButton != null) {
                        Log.d("FANCULO", "CAZZO");
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
            current = (RadioButton) findViewById(morningLocationRButtons[i]);
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
            morningTransportationCheckboxes.add((CheckBox) findViewById(checkboxIDs[i]));
        }
    }

    private void initAfternoonQuestions() {
        afternoonSportRadioGroup =  new ArrayList<>();
        RadioButton current;
        for(int i = 0; i < afternoonSportRButtons.length; i++) {
            current = (RadioButton) findViewById(afternoonSportRButtons[i]);
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
            current = (RadioButton) findViewById(afternoonLocationRButtons[i]);
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

        afternoonPeoplePicker = (NumberPicker) findViewById(R.id.pamSurveyAfternoonPeoplePicker);
        afternoonPeoplePicker.setMinValue(0);
        afternoonPeoplePicker.setMaxValue(20);

        afternoonWorkloadRadioGroup = new ArrayList<>();
        for(int i = 0; i < afternoonWorkloadRButtons.length; i++) {
            current = (RadioButton) findViewById(afternoonWorkloadRButtons[i]);
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

    private void initPamImages() {
        int imageViewId;
        Random r = new Random();
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 4; j++) {
                imageViewId = getResources().getIdentifier("pamSurveyImages_" + (i+1) + "_" + (j+1), "id", getContext().getPackageName());
                images[i][j] = (ImageView) findViewById(imageViewId);
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
        ContentValues record = new ContentValues();
        record.put(PAMTable.KEY_PAM_TS, timestamp);
        record.put(PAMTable.KEY_PAM_COMPLETED, completed);
        record.put(PAMTable.KEY_PAM_STRESS, stress);
        record.put(PAMTable.KEY_PAM_SLEEP, sleep);
        record.put(PAMTable.KEY_PAM_LOCATION, location);
        record.put(PAMTable.KEY_PAM_TRANSPORTATION, transportation);
        record.put(PAMTable.KEY_PAM_IMAGE_ID, imageId);
        localController.update(PAMTable.TABLE_PAM, record, PAMTable.KEY_PAM_ID + " = " + currentSurveyId);
        Log.d("Survey fragment", "Completed: Survey id" + currentSurveyId);
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

        int people = afternoonPeoplePicker.getValue();

        String location;
        if(afternoonLocationSelectedRButton != null) {
            location = parseStringChoice(afternoonLocationSelectedRButton.getId());
        } else {
            location = "no answer";
        }

        ContentValues record = new ContentValues();
        record.put(PAMTable.KEY_PAM_TS, timestamp);
        record.put(PAMTable.KEY_PAM_COMPLETED, completed);
        record.put(PAMTable.KEY_PAM_ACTIVITIES, sport);
        record.put(PAMTable.KEY_PAM_WORKLOAD, workload);
        record.put(PAMTable.KEY_PAM_LOCATION, location);
        record.put(PAMTable.KEY_PAM_SOCIAL, people);
        record.put(PAMTable.KEY_PAM_IMAGE_ID, imageId);
        localController.update(PAMTable.TABLE_PAM, record, PAMTable.KEY_PAM_ID + " = " + currentSurveyId);
        Log.d("Survey fragment", "Completed: Survey id" + currentSurveyId);
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
}
