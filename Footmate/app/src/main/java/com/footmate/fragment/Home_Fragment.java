package com.footmate.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.footmate.Home_Activity;
import com.footmate.R;

import com.footmate.adapter.TabWithPagerAdapter;


public class Home_Fragment extends Fragment {
    public static TabLayout tab_layout;
    public static ViewPager viewpager;
    static int tabposition = 0;

    public Home_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this com.footmate.fragment
        View rootView = inflater.inflate(R.layout.fragment_home_, container, false);
        tab_layout = (TabLayout)rootView.findViewById(R.id.tab_layout);
        viewpager = (ViewPager)rootView.findViewById(R.id.viewpager);

        setupViewPager(viewpager);
        tab_layout.setupWithViewPager(viewpager);
        tab_layout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tab_layout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupTabIcons();

        setTabCLick();
        if (getArguments() != null){
            Bundle b = getArguments();
            tabposition = b.getInt("position");
            viewpager.setCurrentItem(tabposition);
        }
        return rootView;
    }

    public static void setPostion(int pos){
        TabLayout.Tab tab = tab_layout.getTabAt(pos);
        tab.select();
    }

    private void setTabCLick() {
        tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
                tabposition = tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_feed, 0, 0);
        tab_layout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_invite_friends, 0, 0);
        tab_layout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_my_games, 0, 0);
        tab_layout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_find_games, 0, 0);
        tab_layout.getTabAt(3).setCustomView(tabFour);

        TextView tabFive = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabFive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_track_game, 0, 0);
        tab_layout.getTabAt(4).setCustomView(tabFive);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((Home_Activity)getActivity()).currentFragment("Home");
    }

    private void setupViewPager(ViewPager viewPager) {

        TabWithPagerAdapter adapter = new TabWithPagerAdapter(getFragmentManager());
        adapter.addFrag(new Feed_Fragment());
        adapter.addFrag(new InviteFriends_Fragment());
        adapter.addFrag(new MyGames_Fragment());
        adapter.addFrag(new FindGames_Fragment());
        adapter.addFrag(new TrackGamesFragment());

        viewPager.setAdapter(adapter);
    }
    // get the current com.footmate.fragment position...
    public static void getPosition(int index) {
        viewpager.setCurrentItem(index, true);
    }

}
