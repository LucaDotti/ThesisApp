package usi.memotion.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;

import usi.memotion.R;
import usi.memotion.UI.views.HomeView;
import usi.memotion.UI.views.RegistrationView;
import usi.memotion.local.database.controllers.LocalStorageController;
import usi.memotion.local.database.controllers.SQLiteController;
import usi.memotion.local.database.tables.UserTable;

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


        return true;
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