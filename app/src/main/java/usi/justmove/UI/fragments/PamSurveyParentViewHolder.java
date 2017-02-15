package usi.justmove.UI.fragments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bignerdranch.expandablerecyclerview.ParentViewHolder;

import usi.justmove.R;

/**
 * Created by usi on 09/02/17.
 */

public class PamSurveyParentViewHolder extends ParentViewHolder {

    public TextView surveyName;
    public ImageView warning;

    public PamSurveyParentViewHolder(View itemView) {
        super(itemView);

        surveyName = (TextView)itemView.findViewById(R.id.pamSurveyParentTextView);
        warning = (ImageView) itemView.findViewById(R.id.pamSurveyParentImageView);
    }
}
