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
import usi.justmove.gathering.surveys.schedulation.PeriodSurveysScheduler;
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
        Log.d("HOME FRAGMENT", "ON CREATE VIEW");
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
        createSurveyDialog();
        registrationView.setVisibility(View.GONE);
        homeView.setVisibility(View.VISIBLE);
    }

    private void createSurveyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.registration_survey_question)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PeriodSurveysScheduler(true).schedule();
                        callback.onRegistrationSurveyChoice(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PeriodSurveysScheduler(false).schedule();
                        callback.onRegistrationSurveyChoice(false);
                    }
                });
    }

    public interface OnRegistrationSurveyChoice {
        void onRegistrationSurveyChoice(boolean now);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SurveysFragment.OnSurveyCompletedCallback) {
            callback = (OnRegistrationSurveyChoice) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRegistrationSurveyChoice");
        }
    }
}