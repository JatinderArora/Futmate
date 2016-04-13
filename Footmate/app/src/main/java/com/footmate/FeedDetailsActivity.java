package com.footmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.footmate.adapter.DetailsFeedAdapter;
import com.footmate.fragment.ChatFragment;
import com.footmate.fragment.GamePlayerFragment;
import com.footmate.fragment.JoinGameFragment;
import com.footmate.helper.MyPreferenceManager;

/**
 * The <code>ViewPagerFragmentActivity</code> class is the com.footmate.fragment activity hosting the ViewPager
 *
 * @author mwho
 */
public class FeedDetailsActivity extends FragmentActivity {
    public TabLayout tab_layout;
    public ViewPager viewpager;
    JoinGameFragment fragobj;
    GamePlayerFragment fraggameobj;
    ChatFragment fragchatobj;
    SharedPreferences prefs;
    String username,vanue,description,matchtype,datetime,spots,gamelevel,gender,profilepic,title, joinstatus, organisermode, matchid,
    latitude, longitude, typeofpost, address, datetimeofmatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_details);
        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, MODE_PRIVATE);
        //get the job_id from intent.....
        username = getIntent().getExtras().getString("USERNAME");
        title = getIntent().getExtras().getString("TITLE");
        vanue = getIntent().getExtras().getString("VANUE");
        description = getIntent().getExtras().getString("DESCRIPTION");
        matchtype = getIntent().getExtras().getString("MATCHTYPE");
        datetime = getIntent().getExtras().getString("DATETIME");
        spots = getIntent().getExtras().getString("SPOTS");
        gamelevel = getIntent().getExtras().getString("GAMELEVEL");
        gender = getIntent().getExtras().getString("GENDER");
        profilepic = getIntent().getExtras().getString("PICTURE");
        joinstatus = getIntent().getExtras().getString("JOINSTATUS");
        organisermode = getIntent().getExtras().getString("ORGANISERMODE");
        matchid = getIntent().getExtras().getString("MATCHID");
        latitude = getIntent().getExtras().getString("LATITUDE");
        longitude = getIntent().getExtras().getString("LONGITUDE");
        typeofpost = getIntent().getExtras().getString("TYPEOFPOST");
        address = getIntent().getExtras().getString("ADDRESS");
        datetimeofmatch = getIntent().getExtras().getString("DATETIMEOFMATCH");

        Log.e("....................", "username///...." + username + "\n");
        Bundle bundle = new Bundle();
        bundle.putString("USERNAME", username);
        bundle.putString("TITLE", title);
        bundle.putString("VANUE",vanue);
        bundle.putString("DESCRIPTION",description );
        bundle.putString("MATCHTYPE", matchtype);
        bundle.putString("DATETIME", datetime);
        bundle.putString("SPOTS", spots);
        bundle.putString("GAMELEVEL", gamelevel);
        bundle.putString("GENDER", gender);
        bundle.putString("PICTURE", profilepic);
        bundle.putString("JOINSTATUS",joinstatus);
        bundle.putString("ORGANISERMODE",organisermode);
        bundle.putString("MATCHID",matchid);
        bundle.putString("LATITUDE",latitude);
        bundle.putString("LONGITUDE",longitude);
        bundle.putString("TYPEOFPOST",typeofpost);
        bundle.putString("ADDRESS",address);
        bundle.putString("DATETIMEOFMATCH",datetimeofmatch);
        // set Fragmentclass Arguments
        fragobj = new JoinGameFragment();
        fraggameobj = new GamePlayerFragment();
        fragchatobj = new ChatFragment();
        fragobj.setArguments(bundle);
        fraggameobj.setArguments(bundle);
        fragchatobj.setArguments(bundle);

        viewpager = (ViewPager)findViewById(R.id.viewpager);
        setupViewPager(viewpager);


    }


    private void setupViewPager(ViewPager viewPager) {
        DetailsFeedAdapter adapter = new DetailsFeedAdapter(getSupportFragmentManager());
        adapter.addFrag(fragobj);
        if(organisermode.equals("True")) {
            if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))){
                adapter.addFrag(fragchatobj);
                adapter.addFrag(fraggameobj);
            }else {
                if (joinstatus.equals("Join")) {
                    adapter.addFrag(fragchatobj);
                    adapter.addFrag(fraggameobj);
                }
            }
        }

        viewPager.setAdapter(adapter);
        if(getIntent().getStringExtra("Chat") == null) {

        }else if(getIntent().getStringExtra("Chat").equals("chat")) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, Home_Activity.class);
        startActivity(i);
        finish();
    }
}
