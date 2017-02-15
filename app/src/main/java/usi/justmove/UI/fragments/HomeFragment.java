package usi.justmove.UI.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.SimpleMoodTable;
import usi.justmove.local.database.tables.UserTable;
import usi.justmove.remote.database.upload.DataUploadService;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.R.attr.onClick;

/**
 * Created by usi on 04/02/17.
 */

public class HomeFragment extends Fragment {

    private LocalStorageController localController;
    private View root;
    private OnUserRegisteredCallback callback;
    //-------- home_layout_form ----------
    private Spinner ageSpinner;
    private Spinner facultySpinner;
    private Spinner statusSpinner;
    private CheckBox agreeCheckBox;
    private ViewGroup registrationForm;
    private Button closeButton;
    private Button registerButton;
    //-------- home_layout ----------
    private View currentFace;
    private Button submitButton;
    private View simpleMoodForm;
    private LineChart simpleMoodChart;
    //-------- chart ----------
    private Cursor currentChartData;
    private int nbRecords;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HOME FRAGMENT", "ON CREATE VIEW");
        localController = new SQLiteController(getContext());
//        return initHomePage(inflater, container);
        if(checkUserAgreed()) {
            return initHomePage(inflater, container);
        } else {
            return initFormPage(inflater, container);
        }
    }

    private boolean checkDisplaySimpleMood() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String today = df.format(c.getTime());

        Cursor record = localController.rawQuery("SELECT * FROM " + SimpleMoodTable.TABLE_SIMPLE_MOOD +
                " ORDER BY " + SimpleMoodTable.KEY_SIMPLE_MOOD_ID + " DESC " +
                "LIMIT 1", null);

        if(record.getCount() == 0) {
            return true;
        }


        record.moveToFirst();
        long ts = record.getLong(1);
        String date = df.format(new Date(ts*1000));
        return !today.equals(date);
    }

    private View initHomePage(LayoutInflater inflater, @Nullable ViewGroup container) {
        root = inflater.inflate(R.layout.home_layout, container, false);
        simpleMoodForm = root.findViewById(R.id.simpleMoodForm);
        if(checkDisplaySimpleMood()) {
            submitButton = (Button) root.findViewById(R.id.submitButton);
            ImageView face = (ImageView) root.findViewById(R.id.moodFace_0);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) root.findViewById(R.id.moodFace_1);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) root.findViewById(R.id.moodFace_2);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) root.findViewById(R.id.moodFace_3);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) root.findViewById(R.id.moodFace_4);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) root.findViewById(R.id.moodFace_5);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) root.findViewById(R.id.moodFace_6);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });


            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = currentFace.getId();

                    switch(id) {
                        case R.id.moodFace_0:
                            saveMoodStatus(0);
                            break;
                        case R.id.moodFace_1:
                            saveMoodStatus(1);
                            break;
                        case R.id.moodFace_2:
                            saveMoodStatus(2);
                            break;
                        case R.id.moodFace_3:
                            saveMoodStatus(3);
                            break;
                        case R.id.moodFace_4:
                            saveMoodStatus(4);
                            break;
                        case R.id.moodFace_5:
                            saveMoodStatus(5);
                            break;
                        case R.id.moodFace_6:
                            saveMoodStatus(6);
                            break;
                    }
                    simpleMoodForm.setVisibility(View.INVISIBLE);
                }

            });
        } else {
            simpleMoodForm.setVisibility(View.INVISIBLE);
        }

        initSimpleMoodChart();

        return root;
    }

    private void initSimpleMoodChart() {
        simpleMoodChart = (LineChart) root.findViewById(R.id.simpleMoodChart);


        List<Entry> data = getSimpleMoodChartData();
        if(data.size() > 0) {
            setChartData(data);
        }

        // GENERAL
        simpleMoodChart.setDrawGridBackground(false);
        simpleMoodChart.getAxisRight().setEnabled(false);
        simpleMoodChart.setDragEnabled(true);
        simpleMoodChart.setScaleEnabled(true);
        simpleMoodChart.setScaleXEnabled(true);
        simpleMoodChart.setExtraOffsets(20,0,20,0);
        simpleMoodChart.setDrawGridBackground(false);
        simpleMoodChart.setNoDataText("No mood data available");
        simpleMoodChart.setNoDataTextColor(Color.WHITE);
        simpleMoodChart.getLegend().setEnabled(false);
        simpleMoodChart.getDescription().setEnabled(false);
        simpleMoodChart.setVisibleXRangeMaximum(7);
        simpleMoodChart.moveViewToX(nbRecords);

        // Y AXIS
        simpleMoodChart.getAxisLeft().setAxisMinimum(0);
        simpleMoodChart.getAxisLeft().setAxisMaximum(6);
        simpleMoodChart.getAxisLeft().setDrawGridLines(false);
        simpleMoodChart.getAxisLeft().setAxisLineColor(Color.WHITE);
        simpleMoodChart.getAxisLeft().setAxisLineWidth(2);
        simpleMoodChart.getAxisLeft().setTextColor(Color.WHITE);

        // X AXIS
        simpleMoodChart.getXAxis().setLabelRotationAngle(-45);
        simpleMoodChart.getXAxis().setTextColor(Color.WHITE);
        simpleMoodChart.getXAxis().setAxisMinimum(0);
        simpleMoodChart.getXAxis().setGranularity(1f);
        simpleMoodChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        simpleMoodChart.getXAxis().setDrawAxisLine(true);
        simpleMoodChart.getXAxis().setDrawGridLines(false);
        simpleMoodChart.getXAxis().setAxisLineColor(Color.WHITE);
        simpleMoodChart.getXAxis().setAxisLineWidth(2);
        simpleMoodChart.getXAxis().setAxisMaximum(nbRecords);
        simpleMoodChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int id = (int) value;
                Log.d("HOME FRAGMENT", "id " + id);
                DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM-dd-yy");
                if(id >= 0) {
                    if(id < nbRecords) {
                        Cursor c = localController.rawQuery("SELECT * FROM " + SimpleMoodTable.TABLE_SIMPLE_MOOD +
                                " WHERE " + SimpleMoodTable.KEY_SIMPLE_MOOD_ID + " = " + Integer.toString(id+1), null);
                        c.moveToFirst();
                        DateTime date = new DateTime(c.getLong(1)*1000);
                        return dtfOut.print(date);
                    }

                    if(id >= nbRecords) {
//                    Log.d("HOME FRAGMENT", "id " + id);
                        Cursor c = localController.rawQuery("SELECT * FROM " + SimpleMoodTable.TABLE_SIMPLE_MOOD +
                                " WHERE " + SimpleMoodTable.KEY_SIMPLE_MOOD_ID + " = " + Integer.toString(id), null);
                        c.moveToFirst();
                        DateTime date = new DateTime(c.getLong(1)*1000);
                        date = date.plusDays(1);
                        return dtfOut.print(date);
                    }
                }

                return "";

            }
        });



//        simpleMoodChart.setMaxVisibleValueCount(nbRecords+1);

    }

    private void setChartData(List<Entry> data) {
        LineDataSet dataSet = new LineDataSet(data, "Mood");
        dataSet.setColor(Color.WHITE);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.WHITE);
        LineData lineData = new LineData(dataSet);
        lineData.setValueTextColor(Color.WHITE);
        simpleMoodChart.setData(lineData);
        simpleMoodChart.invalidate();
    }

    private List<Entry> getSimpleMoodChartData() {
        List<Entry> data = new ArrayList<>();
        Cursor c = localController.rawQuery("SELECT * FROM " + SimpleMoodTable.TABLE_SIMPLE_MOOD, null);

        if(c.getCount() == 0) {
            return data;
        }

        nbRecords = c.getCount();
        while(c.moveToNext()) {
            DateTime date = new DateTime(c.getLong(1)*1000);
            DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM-dd-yy");
            data.add(new Entry(c.getInt(0)-1, c.getInt(2)));
        }

        return data;
    }

    private void saveMoodStatus(int status) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();
        record.put(SimpleMoodTable.KEY_SIMPLE_MOOD_ID, null);
        record.put(SimpleMoodTable.KEY_SIMPLE_MOOD_TIMESTAMP, Long.toString(System.currentTimeMillis()/1000L));
        record.put(SimpleMoodTable.KEY_SIMPLE_MOOD_STATUS, Integer.toString(status));
        records.add(record);
        localController.insertRecords(SimpleMoodTable.TABLE_SIMPLE_MOOD, records);
        Toast.makeText(getContext(), "Mood saved", Toast.LENGTH_SHORT).show();
        initSimpleMoodChart();
    }

    private View initFormPage(LayoutInflater inflater, @Nullable ViewGroup container) {
        View root = inflater.inflate(R.layout.home_layout_form, container, false);
        TextView consentForm = (TextView) root.findViewById(R.id.consentForm);
        consentForm.setText(getContext().getString(R.string.consent_form));

        closeButton = (Button) root.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        registerButton = (Button) root.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                callback.onUserRegisteredCallback();
            }
        });

        registrationForm = (ViewGroup) root.findViewById(R.id.registrationForm);
        agreeCheckBox = (CheckBox) root.findViewById(R.id.agreeCheckbox);
        agreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    registrationForm.setVisibility(View.VISIBLE);
                    registerButton.setEnabled(true);
                } else {
                    registrationForm.setVisibility(View.INVISIBLE);
                    registerButton.setEnabled(false);
                }
            }
        });

        ageSpinner = (Spinner) root.findViewById(R.id.age_spinner);
        List<Integer> ages = new ArrayList<>();
        for(int i = 18; i < 60; i++) {
            ages.add(i);
        }

        ArrayAdapter<Integer> ageAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_dropdown_item, ages);
        ageSpinner.setAdapter(ageAdapter);

        facultySpinner = (Spinner) root.findViewById(R.id.faculty_spinner);
        List<String> faculties = new ArrayList<>();
        faculties.add("Communication sciences");
        faculties.add("Biomedical sciences");
        faculties.add("Architecture");
        faculties.add("Informatics");
        faculties.add("Economics");

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, faculties);
        facultySpinner.setAdapter(facultyAdapter);

        statusSpinner = (Spinner) root.findViewById(R.id.status_spinner);
        List<String> status = new ArrayList<>();
        status.add("Bachelor");
        status.add("Master");
        status.add("PhD");
        status.add("Professor");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, status);
        statusSpinner.setAdapter(statusAdapter);

        return root;
    }

    private boolean checkUserAgreed() {
        Cursor c = localController.rawQuery("SELECT * FROM " + UserTable.TABLE_USER, null);

        if(c.getCount() == 0) {
            return false;
        }

        c.moveToFirst();
        boolean agreed = c.getInt(2) == 0 ? false : true;
        return agreed;
    }

    private void registerUser() {
        long time = System.currentTimeMillis();
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();
        record.put(UserTable.KEY_USER_ID, null);
        record.put(UserTable.KEY_USER_UID, Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        record.put(UserTable.KEY_USER_AGE, ageSpinner.getSelectedItem().toString());
        record.put(UserTable.KEY_USER_AGREED, agreeCheckBox.isChecked() ? "1" : "0");
        record.put(UserTable.KEY_USER_FACULTY, facultySpinner.getSelectedItem().toString());
        record.put(UserTable.KEY_USER_ACADEMIC_STATUS, statusSpinner.getSelectedItem().toString());
        record.put(UserTable.KEY_USER_EMAIL, "");
        record.put(UserTable.KEY_USER_CREATION_TS, Long.toString(time));
        record.put(UserTable.KEY_USER_UPDATE_TS, Long.toString(time));
        records.add(record);
        localController.insertRecords(UserTable.TABLE_USER, records);
    }

    public void onMoodFaceClick(View v) {
        if(currentFace != null) {
            currentFace.setBackgroundResource(0);
        }
        v.setBackgroundResource(R.drawable.face_circle);
        currentFace = v;
        submitButton.setEnabled(true);
    }

    public interface OnUserRegisteredCallback {
        void onUserRegisteredCallback();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserRegisteredCallback) {
            callback = (OnUserRegisteredCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserRegisteredCallback");
        }
    }
}