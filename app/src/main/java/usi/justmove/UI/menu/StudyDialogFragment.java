package usi.justmove.UI.menu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import usi.justmove.R;

import static android.R.attr.button;

/**
 * Created by usi on 06/02/17.
 */

public class StudyDialogFragment extends AppCompatDialogFragment {
    private Button button;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.about_this_study_layout, null);

        button = (Button) root.findViewById(R.id.study_okButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(root);

        TextView text = (TextView) root.findViewById(R.id.aboutThisStudyDesription);
        text.setText(Html.fromHtml(getContext().getString(R.string.study_description)));
        return builder.create();
    }
}
