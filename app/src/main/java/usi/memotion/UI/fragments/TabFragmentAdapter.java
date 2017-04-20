package usi.memotion.UI.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import usi.memotion.UI.fragments.HomeFragment;
import usi.memotion.UI.fragments.MapFragment;
import usi.memotion.UI.fragments.SurveysFragment;

/**
 * Created by usi on 04/02/17.
 */

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private String[] fragments = {"Home", "Survey", "Map"};

    public TabFragmentAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SurveysFragment();
            case 2:
                return new MapFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof HomeFragment || object instanceof  SurveysFragment) {
            return POSITION_NONE;
        } else {
            return super.getItemPosition(object);
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position];
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
