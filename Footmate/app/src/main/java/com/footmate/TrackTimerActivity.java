package com.footmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.PlayGifView;
import com.footmate.utils.TrackTimerService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class TrackTimerActivity extends ActionBarActivity {
    private Context context = TrackTimerActivity.this;
    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    private Toolbar toolbar;
    private ImageView img_toolbar_back,finish;
    private TextView text_top,txtMatch,txtFieldName;
    public static TextView txtTimer;
    PlayGifView viewGif;
    GoogleMap mMap;
    Intent service;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_timer);

        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, MODE_PRIVATE);
        editor = prefs.edit();

        if(prefs.getString(MyPreferenceManager.TRACK_TYPE, null) == null) {
            editor.putString(MyPreferenceManager.TRACK_TYPE, getIntent().getStringExtra("TrackType"));
            editor.putString(MyPreferenceManager.TRACK_LATITUDE, getIntent().getStringExtra("TrackLatitude"));
            editor.putString(MyPreferenceManager.TRACK_LONGITUDE, getIntent().getStringExtra("TrackLongitude"));
            editor.putString(MyPreferenceManager.TRACK_FIELDNAME, getIntent().getStringExtra("TrackFieldname"));
            editor.putString(MyPreferenceManager.TRACK_ORGANISERMODE, getIntent().getStringExtra("TrackOrganisermode"));
            editor.putString(MyPreferenceManager.TRACK_ADDRESS, getIntent().getStringExtra("TrackAddress"));
            editor.putString(MyPreferenceManager.TRACK_TEAM_SIZE, getIntent().getStringExtra("TrackTeamSize"));
            editor.putString(MyPreferenceManager.TRACK_CITY, getIntent().getStringExtra("TrackCity"));
            editor.commit();
        }

        //set TypeFace....
        setTypeface();
        // set the Toolbar....
        setToolBar();
        // setwidgets....
        setWidgets();
        //set clickListner...
        setClickListner();
        // set map...

        setMap();

        service = new Intent(this, TrackTimerService.class);
        if(prefs.getString(MyPreferenceManager.TIMER_STATUS, null) == null) {
            startService(service);
            Home_Activity.showGreenRing();
            editor.putString(MyPreferenceManager.TIMER_STATUS, "ON");
            editor.commit();
        }
        if(!prefs.getString(MyPreferenceManager.TRACK_TEAM_SIZE, null).equals("")) {
            txtMatch.setText(prefs.getString(MyPreferenceManager.TRACK_TYPE, null) + " #" + prefs.getString(MyPreferenceManager.TRACK_TEAM_SIZE, null));
        }else {
            txtMatch.setText(prefs.getString(MyPreferenceManager.TRACK_TYPE, null));
        }
        txtFieldName.setText(prefs.getString(MyPreferenceManager.TRACK_FIELDNAME, null));
        viewGif.setImageResource(R.drawable.gifcircle);
    }
    private void setWidgets(){
        txtMatch = (TextView)findViewById(R.id.txtMatch);
        txtFieldName = (TextView)findViewById(R.id.txtFieldName);
        txtTimer = (TextView)findViewById(R.id.txtTimer);
        finish = (ImageView)findViewById(R.id.finish);
        viewGif = (PlayGifView) findViewById(R.id.viewGif);

        txtMatch.setTypeface(Ubuntu_Medium);
        txtFieldName.setTypeface(Ubuntu_Regular);
        txtTimer.setTypeface(Ubuntu_Bold);
    }

    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        img_toolbar_back = (ImageView) toolbar.findViewById(R.id.img_toolbar_back);
        text_top = (TextView) toolbar.findViewById(R.id.text_top);
        text_top.setTypeface(Ubuntu_Medium);
        img_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setMap(){
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = supportMapFragment.getMap();
        mMap.setMyLocationEnabled(true);

        double latitude = Double.parseDouble(prefs.getString(MyPreferenceManager.TRACK_LATITUDE, null));
        double longitude = Double.parseDouble(prefs.getString(MyPreferenceManager.TRACK_LONGITUDE, null));
        final LatLng MELBOURNE = new LatLng(latitude, longitude);
        Marker melbourne = mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        markers.add(melbourne);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude,longitude))
                .zoom(12)
                .build();


        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setTypeface() {
        Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }
    private void setClickListner(){
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(service);
                Home_Activity.hideGreenRing();

                if(prefs.getString(MyPreferenceManager.TRACK_ORGANISERMODE, null).equals("True")){
                    Intent i = new Intent(TrackTimerActivity.this,TrackGameOrganiserActivity.class);
                    i.putExtra("latitude",prefs.getString(MyPreferenceManager.TRACK_LATITUDE, null));
                    i.putExtra("longitude",prefs.getString(MyPreferenceManager.TRACK_LONGITUDE, null));
                    i.putExtra("time",txtTimer.getText().toString());
                    i.putExtra("tracktype",prefs.getString(MyPreferenceManager.TRACK_TYPE, null));
                    i.putExtra("fieldname",prefs.getString(MyPreferenceManager.TRACK_FIELDNAME, null));
                    i.putExtra("address",prefs.getString(MyPreferenceManager.TRACK_ADDRESS, null));
                    i.putExtra("teamsize",prefs.getString(MyPreferenceManager.TRACK_TEAM_SIZE, null));
                    i.putExtra("city",prefs.getString(MyPreferenceManager.TRACK_CITY, null));
                    startActivity(i);
                    finish();
                    if(prefs.getString(MyPreferenceManager.TRACK_TYPE, null) != null) {
                        editor.putString(MyPreferenceManager.TRACK_TYPE, null);
                        editor.putString(MyPreferenceManager.TRACK_LATITUDE, null);
                        editor.putString(MyPreferenceManager.TRACK_LONGITUDE, null);
                        editor.putString(MyPreferenceManager.TRACK_FIELDNAME, null);
                        editor.putString(MyPreferenceManager.TRACK_ORGANISERMODE, null);
                        editor.putString(MyPreferenceManager.TRACK_ADDRESS, null);
                        editor.putString(MyPreferenceManager.TIMER_STATUS,null);
                        editor.putString(MyPreferenceManager.TRACK_TEAM_SIZE, null);
                        editor.putString(MyPreferenceManager.TRACK_CITY, null);
                        editor.commit();
                    }
                }else {
                    Intent i = new Intent(TrackTimerActivity.this,TrackGameNoOrganiserActivity.class);
                    i.putExtra("latitude",prefs.getString(MyPreferenceManager.TRACK_LATITUDE, null));
                    i.putExtra("longitude",prefs.getString(MyPreferenceManager.TRACK_LONGITUDE, null));
                    i.putExtra("time",txtTimer.getText().toString());
                    i.putExtra("tracktype",prefs.getString(MyPreferenceManager.TRACK_TYPE, null));
                    i.putExtra("fieldname",prefs.getString(MyPreferenceManager.TRACK_FIELDNAME, null));
                    i.putExtra("address",prefs.getString(MyPreferenceManager.TRACK_ADDRESS, null));
                    i.putExtra("city",prefs.getString(MyPreferenceManager.TRACK_CITY, null));
                    startActivity(i);
                    finish();
                    if(prefs.getString(MyPreferenceManager.TRACK_TYPE, null) != null) {
                        editor.putString(MyPreferenceManager.TRACK_TYPE, null);
                        editor.putString(MyPreferenceManager.TRACK_LATITUDE, null);
                        editor.putString(MyPreferenceManager.TRACK_LONGITUDE, null);
                        editor.putString(MyPreferenceManager.TRACK_FIELDNAME, null);
                        editor.putString(MyPreferenceManager.TRACK_ORGANISERMODE, null);
                        editor.putString(MyPreferenceManager.TRACK_ADDRESS, null);
                        editor.putString(MyPreferenceManager.TIMER_STATUS,null);
                        editor.putString(MyPreferenceManager.TRACK_CITY, null);
                        editor.commit();
                    }
                }

            }
        });
    }


}
