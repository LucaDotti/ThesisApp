package usi.justmove.UI.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import usi.justmove.R;

/**
 * Created by usi on 08/02/17.
 */

public class ExpandableSurveysListViewAdapter extends BaseExpandableListAdapter {
    private List<String> groups;
    private Map<String, List<String>> childs;
    private Context context;

    public ExpandableSurveysListViewAdapter(Context context, List<String> surveys) {
        this.context = context;
        this.groups = surveys;
        childs = new HashMap<>();
        List<String> pam = new ArrayList<>();
        pam.add("Pam");
        childs.put(groups.get(0), pam);

        List<String> pwb = new ArrayList<>();
        pam.add("Pwb");
        childs.put(groups.get(1), pwb);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition*groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.surveys_pam_parent_layout, null);
        }

        String surveyName = groups.get(groupPosition);
        TextView surveyNameTextView = (TextView) convertView.findViewById(R.id.pamSurveyParentTextView);
        surveyNameTextView.setText(surveyName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(getChildView(childPosition), null);
            convertView = inflater.inflate(R.layout.surveys_pam_child_layout, null);
        }

//        String surveyName = groups.get(groupPosition);
//        TextView surveyNameTextView = (TextView) convertView.findViewById(R.id.surveysGroupItemText);
//        surveyNameTextView.setText(surveyName);
//        convertView = new PamSurveyView(context);
//        convertView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        TextView v = new TextView(context);
//        v.setText("AAAAAAAAA");
        return convertView;
    }

    private int getChildView(int childPosition) {
        switch(childPosition) {
            case 0:
                return R.layout.surveys_pam_layout;
            case 1:
                return R.layout.surveys_list_item_pwb;
            default:
                return -1;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
