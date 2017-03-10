package usi.justmove.UI.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.LocalDbUtility;
import usi.justmove.local.database.tables.LocalTables;
import usi.justmove.local.database.tables.SimpleMoodTable;

/**
 * Created by usi on 20/02/17.
 */

public class HomeView extends LinearLayout{
    private LocalStorageController localController;
    private Context context;

    private View currentFace;
    private Button submitButton;
    private View simpleMoodForm;
    private LineChart simpleMoodChart;
    private int nbRecords;

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.home_layout, this, true);

        init();
    }

    private void init() {
        localController = SQLiteController.getInstance(context);
        simpleMoodForm = findViewById(R.id.simpleMoodForm);

        if(checkDisplaySimpleMood()) {
            submitButton = (Button) findViewById(R.id.submitButton);
            ImageView face = (ImageView) findViewById(R.id.moodFace_0);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) findViewById(R.id.moodFace_1);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) findViewById(R.id.moodFace_2);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) findViewById(R.id.moodFace_3);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) findViewById(R.id.moodFace_4);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) findViewById(R.id.moodFace_5);
            face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMoodFaceClick(v);
                }
            });

            face = (ImageView) findViewById(R.id.moodFace_6);
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
//        initSimpleMoodChart();
    }

    private boolean checkDisplaySimpleMood() {
        String tableName = LocalDbUtility.getTableName(LocalTables.TABLE_SIMPLE_MOOD);
        String columnTs = LocalDbUtility.getTableColumns(LocalTables.TABLE_SIMPLE_MOOD)[1];

        LocalDateTime startDateTime = new LocalDateTime().withTime(0, 0, 0, 0);
        LocalDateTime endDateTime = new LocalDateTime().withTime(23, 59, 59, 999);
        long startMillis = startDateTime.toDateTime().getMillis()/1000;
        long endMillis = endDateTime.toDateTime().getMillis()/1000;

        Cursor c = localController.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + columnTs + " >= " + startMillis + " AND " + columnTs + " <= " + endMillis
                + " LIMIT " + 1, null);

        if(c.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void onMoodFaceClick(View v) {
        if(currentFace != null) {
            currentFace.setBackgroundResource(0);
        }
        v.setBackgroundResource(R.drawable.face_circle);
        currentFace = v;
        submitButton.setEnabled(true);
    }

    private void saveMoodStatus(int status) {
        List<Map<String, String>> records = new ArrayList<>();
        Map<String, String> record = new HashMap<>();
        record.put(SimpleMoodTable.KEY_SIMPLE_MOOD_ID, null);
        record.put(SimpleMoodTable.KEY_SIMPLE_MOOD_TIMESTAMP, Long.toString(System.currentTimeMillis()));
        record.put(SimpleMoodTable.KEY_SIMPLE_MOOD_STATUS, Integer.toString(status));
        records.add(record);
        localController.insertRecords(SimpleMoodTable.TABLE_SIMPLE_MOOD, records);
        Toast.makeText(getContext(), "Mood saved", Toast.LENGTH_SHORT).show();
        initSimpleMoodChart();
    }

    private void initSimpleMoodChart() {
        simpleMoodChart = (LineChart) findViewById(R.id.simpleMoodChart);

        List<Entry> data = getSimpleMoodChartData();
        if(data.size() > 0) {
            setChartData(data);
        }

        // GENERAL
        simpleMoodChart.setDrawGridBackground(false);
        simpleMoodChart.getAxisRight().setEnabled(false);
        simpleMoodChart.setDragEnabled(true);
        simpleMoodChart.setDrawGridBackground(false);
        simpleMoodChart.setNoDataText("No mood data available");
        simpleMoodChart.setNoDataTextColor(Color.WHITE);
        simpleMoodChart.getLegend().setEnabled(false);
        simpleMoodChart.getDescription().setEnabled(false);
        simpleMoodChart.setExtraBottomOffset(30);
        simpleMoodChart.getData().setHighlightEnabled(false);

        // Y AXIS
        simpleMoodChart.getAxisLeft().setAxisMinimum(0);
        simpleMoodChart.getAxisLeft().setAxisMaximum(6);
        simpleMoodChart.getAxisLeft().setDrawLabels(false);
        simpleMoodChart.getAxisLeft().setDrawGridLines(false);
        simpleMoodChart.getAxisLeft().setAxisLineColor(Color.WHITE);
        simpleMoodChart.getAxisLeft().setAxisLineWidth(2);
        simpleMoodChart.getAxisLeft().setDrawAxisLine(false);
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
        simpleMoodChart.getXAxis().setTextSize(8);
        simpleMoodChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int id = (int) value;
                DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM-dd-yy");
                if(id >= 0) {
                    if(id < nbRecords) {
                        Cursor c = localController.rawQuery("SELECT * FROM " + SimpleMoodTable.TABLE_SIMPLE_MOOD +
                                " WHERE " + SimpleMoodTable.KEY_SIMPLE_MOOD_ID + " = " + Integer.toString(id+1), null);
                        c.moveToFirst();
                        DateTime date = new DateTime(c.getLong(1));
                        c.close();
                        return dtfOut.print(date);
                    }

                    if(id >= nbRecords) {
                        Cursor c = localController.rawQuery("SELECT * FROM " + SimpleMoodTable.TABLE_SIMPLE_MOOD +
                                " WHERE " + SimpleMoodTable.KEY_SIMPLE_MOOD_ID + " = " + Integer.toString(id), null);
                        c.moveToFirst();
                        DateTime date = new DateTime(c.getLong(1));
                        date = date.plusDays(1);
                        c.close();
                        return dtfOut.print(date);
                    }
                }

                return "";

            }
        });
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

        c.close();
        return data;
    }
}
