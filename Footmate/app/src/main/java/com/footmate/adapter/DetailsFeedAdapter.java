package com.footmate.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d_RaMaN on 2/14/2016.
 */
public class DetailsFeedAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
    public DetailsFeedAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFrag(Fragment fragment) {
        mFragmentList.add(fragment);
    }


}
