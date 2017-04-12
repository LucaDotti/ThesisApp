package usi.justmove.UI.views;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usi.justmove.R;
import usi.justmove.UI.ExpandableLayout;
import usi.justmove.UI.fragments.HomeFragment;
import usi.justmove.local.database.LocalStorageController;
import usi.justmove.local.database.controllers.SQLiteController;
import usi.justmove.local.database.tables.UserTable;

/**
 * Created by usi on 19/02/17.
 */

public class RegistrationView extends LinearLayout {
    public static String[] academicStatusValues = {"BSc", "MSc", "PhD", "Prof", "Other"};
    private ExpandableLayout consentLayout;
    private ExpandableLayout formLayout;

    private Button closeButton;
    private Button registerButton;
    private RadioButton currentSelectedRadioButton;

    private LocalStorageController localController;

    private OnUserRegisteredCallback callback;


    public RegistrationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        localController = SQLiteController.getInstance(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.registration_layout, this, true);

        closeButton = (Button) this.findViewById(R.id.registrationCloseButton);
        registerButton = (Button) this.findViewById(R.id.registrationRegisterButton);

        consentLayout = (ExpandableLayout) this.findViewById(R.id.consentExpandableLayout);
        View consentTitleView = inflater.inflate(R.layout.registration_consent_title, null);
        consentLayout.setTitleView(consentTitleView);

        View consentBody = inflater.inflate(R.layout.registration_consent_layout, null);
        consentLayout.setBodyView(consentBody);

        initConsent(consentLayout.getBodyView());

        formLayout = (ExpandableLayout) this.findViewById(R.id.formExpandableLayout);
        View formTitleView = inflater.inflate(R.layout.registration_form_title, null);
        formLayout.setTitleView(formTitleView);

        View formBody = inflater.inflate(R.layout.registration_form_layout, null);
        formLayout.setBodyView(formBody);

        initForm(formLayout.getBodyView());

        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView consentText = (TextView) consentBody.findViewById(R.id.consentForm);
        consentText.setText(Html.fromHtml(context.getString(R.string.consent_form)));

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
                callback.onUserRegisteredCallback();
                formLayout.stopBlink();
            }
        });

        registerButton.setEnabled(false);

        consentLayout.startBlink();
    }

    public void setOnUserRegisteredCallback(OnUserRegisteredCallback callback) {
        this.callback = callback;
    }

    private void registerUser() {
        View bodyView = formLayout.getBodyView();
        NumberPicker agePicker = (NumberPicker) bodyView.findViewById(R.id.age_picker);
        CheckBox agreeCheckbox = (CheckBox) bodyView.findViewById(R.id.agreeCheckbox);
        NumberPicker statusPicker = (NumberPicker) bodyView.findViewById(R.id.status_picker);
        RadioGroup group = (RadioGroup) bodyView.findViewById(R.id.genderRadioGroup);

        long time = System.currentTimeMillis()/1000;
        ContentValues record = new ContentValues();

        record.put(UserTable.KEY_USER_UID, Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));
        record.put(UserTable.KEY_USER_AGE, Integer.toString(agePicker.getValue()));
        record.put(UserTable.KEY_USER_GENDER, currentSelectedRadioButton.getId() == R.id.genderFemaleRadioButton ? "female" : "male");
        record.put(UserTable.KEY_USER_AGREED, "1");
        record.put(UserTable.KEY_USER_FACULTY, "Informatics");
        record.put(UserTable.KEY_USER_ACADEMIC_STATUS, academicStatusValues[statusPicker.getValue()]);
        record.put(UserTable.KEY_USER_EMAIL, "");
        record.put(UserTable.KEY_USER_CREATION_TS, time);
        record.put(UserTable.KEY_USER_UPDATE_TS, time);
        localController.insertRecord(UserTable.TABLE_USER, record);
    }

    private void initConsent(View view) {
        final TextView consentForm = (TextView) view.findViewById(R.id.consentForm);
        consentForm.setText(getContext().getString(R.string.consent_form));

        CheckBox agreeCheckbox = (CheckBox) view.findViewById(R.id.agreeCheckbox);

        agreeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    consentLayout.collapse();
                    registerButton.setEnabled(true);
                    formLayout.expand();
                    consentLayout.stopBlink();
                    formLayout.startBlink();
                }
            }
        });
    }

    private void initForm(View view) {
        NumberPicker agePicker = (NumberPicker) view.findViewById(R.id.age_picker);
        agePicker.setMinValue(18);
        agePicker.setMaxValue(60);

        RadioButton maleGenderRadioButton = (RadioButton) view.findViewById(R.id.genderMaleRadioButton);
        maleGenderRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSelectedRadioButton != null) {
                    currentSelectedRadioButton.setChecked(false);
                }

                currentSelectedRadioButton = (RadioButton) v;

            }
        });

        RadioButton femaleGenderRadioButton = (RadioButton) view.findViewById(R.id.genderFemaleRadioButton);
        femaleGenderRadioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentSelectedRadioButton != null) {
                    currentSelectedRadioButton.setChecked(false);
                }

                currentSelectedRadioButton = (RadioButton) v;

            }
        });

        maleGenderRadioButton.setChecked(true);
        currentSelectedRadioButton = maleGenderRadioButton;

        NumberPicker statusPicker = (NumberPicker) view.findViewById(R.id.status_picker);
        statusPicker.setMinValue(0);
        statusPicker.setMaxValue(4);
        statusPicker.setDisplayedValues(academicStatusValues);
    }

    public interface OnUserRegisteredCallback {
        void onUserRegisteredCallback();
    }
}
