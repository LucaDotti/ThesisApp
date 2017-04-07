package usi.justmove.UI.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
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
import usi.justmove.UI.views.HomeView;
import usi.justmove.UI.views.RegistrationView;
import usi.justmove.gathering.surveys.config.SurveyConfig;
import usi.justmove.gathering.surveys.config.SurveyConfigFactory;
import usi.justmove.gathering.surveys.config.SurveyType;
import usi.justmove.gathering.surveys.schedulation.DailyScheduler;
import usi.justmove.gathering.surveys.schedulation.Scheduler;
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

public class HomeFragment extends Fragment implements RegistrationView.OnUserRegisteredCallback {
    private OnRegistrationSurveyChoice callback;
    private LocalStorageController localController;

    private RegistrationView registrationView;
    private HomeView homeView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        localController = SQLiteController.getInstance(getContext());
        View root = inflater.inflate(R.layout.home_layoutt, null);

        registrationView = (RegistrationView) root.findViewById(R.id.homeRegistrationView);
        registrationView.setOnUserRegisteredCallback(this);
        homeView = (HomeView) root.findViewById(R.id.homeHomeView);

        if(checkUserAgreed()) {
            registrationView.setVisibility(View.GONE);
            homeView.setVisibility(View.VISIBLE);
        } else {
            registrationView.setVisibility(View.VISIBLE);
            homeView.setVisibility(View.GONE);
        }

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



    @Override
    public void onUserRegisteredCallback() {
        Log.d("REGISTRATION", "Registered");
        createSurveyDialog();
        registrationView.setVisibility(View.GONE);
        homeView.setVisibility(View.VISIBLE);
    }

    private void createSurveyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.registration_survey_question)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.onRegistrationSurveyChoice(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.onRegistrationSurveyChoice(false);
                    }
                });
        builder.show();
    }

    private String[] getTermSurveyDates() {
        SurveyConfig c = SurveyConfigFactory.getConfig(SurveyType.GROUPED_SSPP, getContext());

        String[] dates = new String[c.dayCount];

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 1);

        Calendar end = Calendar.getInstance();
        String endString = getContext().getString(R.string.term_end_study_date);
        String[] dateElems = endString.split("-");
        end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateElems[0]));
        end.set(Calendar.MONTH, Integer.parseInt(dateElems[1])-1);
        end.set(Calendar.YEAR, Integer.parseInt(dateElems[2]));
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 1);

        System.out.println("END " + formatter.format(end.getTime()));
        System.out.println("START " + formatter.format(start.getTime()));
        long diff = end.getTimeInMillis() - start.getTimeInMillis();

        //check this
        long interval = diff/(c.dayCount-1);
        int days = (int) (interval/(24 * 60 * 60 * 1000));

        dates[0] = formatter.format(start.getTime());

        int i;
        for(i = 1; i < dates.length-1; i++) {
            start.add(Calendar.DAY_OF_MONTH, days);
            dates[i] = formatter.format(start.getTime());
        }

        dates[i] = formatter.format(end.getTime());

        return dates;
    }

    public interface OnRegistrationSurveyChoice {
        void onRegistrationSurveyChoice(boolean now);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnRegistrationSurveyChoice) {
            callback = (OnRegistrationSurveyChoice) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegistrationSurveyChoice");
        }
    }
}