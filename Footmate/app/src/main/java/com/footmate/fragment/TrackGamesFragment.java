package com.footmate.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.footmate.R;
import com.footmate.TrackMainActivity;
import com.footmate.TrackTimerActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.footmate.helper.MyPreferenceManager;


public class TrackGamesFragment extends Fragment {
    MapView mapView;
    GoogleMap map;
    double latitude, longitude;
    ImageView check_in;
    LinearLayout layout_selectType;
    String Type;
    TextView text_match, text_freeplay, text_practice;
    SharedPreferences prefs;
    public TrackGamesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.footmate.fragment
        View rootView = inflater.inflate(R.layout.fragment_track_games, container, false);
        prefs = getActivity().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, getActivity().MODE_PRIVATE);
        Type = "Match";
        // setting views
        setView(rootView);

        // setting map
        setMap(savedInstanceState);

        setClickListeners();

        return rootView;
    }

    public void setView(View rootView){
        // Gets the MapView from the XML layout
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        check_in = (ImageView) rootView.findViewById(R.id.check_in);
        text_match = (TextView) rootView.findViewById(R.id.text_match);
        text_freeplay = (TextView) rootView.findViewById(R.id.text_freeplay);
        text_practice = (TextView) rootView.findViewById(R.id.text_practice);
        layout_selectType = (LinearLayout) rootView.findViewById(R.id.layout_selectType);
    }

    public void setMap(Bundle savedInstanceState){
        //   creates mapview
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getActivity().getApplication().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);


        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.d("lat long", "" + latitude + "," + longitude);
            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(12f)
                    .build();


            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else if (location == null) {
            latitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LATITUDE, null));
            longitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LONGITUDE, null));
            Log.d("lat long", "" + latitude + "," + longitude);
            // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
            MapsInitializer.initialize(this.getActivity());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(12f)
                    .build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void setClickListeners(){
        text_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Type = "Match";
                layout_selectType.setBackgroundResource(R.drawable.track_match);
            }
        });
        text_freeplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Type = "Freeplay";
                layout_selectType.setBackgroundResource(R.drawable.track_freeplay);
            }
        });

        text_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Type = "Practice";
                layout_selectType.setBackgroundResource(R.drawable.track_practice);
            }
        });

        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getString(MyPreferenceManager.TRACK_TYPE, null) == null) {
                    Intent i = new Intent(getActivity(), TrackMainActivity.class);
                    i.putExtra("Type",Type);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getActivity(), TrackTimerActivity.class);
                    startActivity(i);
                }


            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
