package com.footmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Utilities;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.footmate.model.SelectListModal;
import com.footmate.model.TempDistanceModal;


public class TrackMainActivity extends ActionBarActivity {
    GoogleMap mMap;
    double latitude,longitude, centerlat, centerlongi;
    ImageView check_in, main_marker;
    LatLng center;
    TextView fieldname_text, save_field;
    EditText enter_field_name_edittext;
    private ArrayList<SelectListModal> Location_arraylist;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<TempDistanceModal> tempdistanceArraylist = new ArrayList<>();
    Marker LastMarker = null;
    Marker marker;
    public static boolean mMapIsTouched = false;
    String trackType, trackfieldname, trackOrganiserMode, trackAddress, trackCity, trackTeamSize = "";
    String tracklongitude, tracklatitude;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_main);

        setWidgets();

        setClickListeners();
        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, MODE_PRIVATE);
        Location_arraylist = new ArrayList<SelectListModal>();
        Location_arraylist = Utilities.locationlist;
        setMap();


    }

    public void setWidgets(){
        check_in = (ImageView)findViewById(R.id.check_in);
        main_marker = (ImageView)findViewById(R.id.main_marker);
        fieldname_text = (TextView)findViewById(R.id.fieldname_text);
        enter_field_name_edittext = (EditText)findViewById(R.id.enter_field_name_edittext);
        save_field = (TextView)findViewById(R.id.save_field);
    }

    public void setClickListeners(){
        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(save_field.getVisibility() == View.GONE) {
                    trackType = getIntent().getStringExtra("Type");
                    trackOrganiserMode = "False";
                    Intent i = new Intent(TrackMainActivity.this, TrackTimerActivity.class);
                    i.putExtra("TrackType", trackType);
                    i.putExtra("TrackLatitude", tracklatitude);
                    i.putExtra("TrackLongitude", tracklongitude);
                    i.putExtra("TrackFieldname", trackfieldname);
                    i.putExtra("TrackOrganisermode", trackOrganiserMode);
                    i.putExtra("TrackAddress", trackAddress);
                    i.putExtra("TrackTeamSize", trackTeamSize);
                    i.putExtra("TrackCity", trackCity);
                    startActivity(i);
                    finish();
                }
            }
        });

        save_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enter_field_name_edittext.getText().toString().equals("")){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(TrackMainActivity.this);
                    dialog.setMessage("Please enter Field Name");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = dialog.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.setCancelable(false);
                    alert.show();
                }else {
                    trackType = getIntent().getStringExtra("Type");
                    Log.e("track game type", trackType);
                    trackfieldname = enter_field_name_edittext.getText().toString();
                    trackOrganiserMode = "False";
                    Intent i = new Intent(TrackMainActivity.this, TrackTimerActivity.class);
                    i.putExtra("TrackType", trackType);
                    i.putExtra("TrackLatitude", tracklatitude);
                    i.putExtra("TrackLongitude", tracklongitude);
                    i.putExtra("TrackFieldname", trackfieldname);
                    i.putExtra("TrackOrganisermode", trackOrganiserMode);
                    i.putExtra("TrackAddress", trackAddress);
                    i.putExtra("TrackTeamSize", trackTeamSize);
                    i.putExtra("TrackCity", trackCity);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    public void setMap(){
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = supportMapFragment.getMap();
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);


        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.d("lat long", "" + latitude + "," + longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(15)
                    .build();


            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public  synchronized void  onCameraChange(CameraPosition cameraPosition) {
                    center = mMap.getCameraPosition().target;
                    Log.d("", "center Position of camera" + center.latitude + "," + center.longitude);
                    centerlat = center.latitude;
                    centerlongi = center.longitude;
                    if (LastMarker != null){
                        Location locationA = new Location("point A");
                        locationA.setLatitude(LastMarker.getPosition().latitude);
                        locationA.setLongitude(LastMarker.getPosition().longitude);



                        Location locationB = new Location("point B");
                        locationB.setLatitude(centerlat);
                        locationB.setLongitude(centerlongi);

                        float distance = locationA.distanceTo(locationB);
                        if (distance > 500.00){
                            LastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1));
                            enter_field_name_edittext.setVisibility(View.VISIBLE);
                            fieldname_text.setVisibility(View.VISIBLE);
                            save_field.setVisibility(View.VISIBLE);
                            main_marker.setImageResource(R.drawable.mark);
                            main_marker.setVisibility(View.VISIBLE);
                        }
                    }
                    tempdistanceArraylist.clear();
                    if(Location_arraylist.size() > 0){
                        marker =  getMarker(new LatLng(centerlat, centerlongi));
                        if (marker != null) {
                            TrackMainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    main_marker.setImageResource(R.drawable.pin_3);
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2));
                                    LastMarker = marker ;
                                    enter_field_name_edittext.setVisibility(View.GONE);
                                    fieldname_text.setVisibility(View.GONE);
                                    save_field.setVisibility(View.GONE);
                                }
                            });
                            if(!mMapIsTouched) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3));
                                            main_marker.setVisibility(View.GONE);
                                        }catch (Exception e){

                                        }


                                    }
                                }, 500);

                            }
                        }
                    }
                    if(!mMapIsTouched){
                        TrackMainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Geocoder geocoder = new Geocoder(TrackMainActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(centerlat, centerlongi, 1);
                                    String cityName = addresses.get(0).getAddressLine(0);
                                    String stateName = addresses.get(0).getAddressLine(1);
                                    String countryName = addresses.get(0).getAddressLine(2);
                                    tracklatitude = centerlat+"";
                                    tracklongitude = centerlongi+"";
                                    trackAddress = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2);
                                    fieldname_text.setText(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
                                    if(addresses.get(0).getLocality().equals("null")){
                                        trackCity = addresses.get(0).getAddressLine(0);
                                    }else {
                                        trackCity = addresses.get(0).getLocality();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                }
                        });

                    }

                }
            });

        }else if(location == null){
            latitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LATITUDE, null));
            longitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LONGITUDE, null));

            Log.d("lat long", "" + latitude + "," + longitude);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(15)
                    .build();


            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public  synchronized void  onCameraChange(CameraPosition cameraPosition) {
                    center = mMap.getCameraPosition().target;
                    Log.d("", "center Position of camera" + center.latitude + "," + center.longitude);
                    centerlat = center.latitude;
                    centerlongi = center.longitude;
                    if (LastMarker != null){
                        Location locationA = new Location("point A");
                        locationA.setLatitude(LastMarker.getPosition().latitude);
                        locationA.setLongitude(LastMarker.getPosition().longitude);



                        Location locationB = new Location("point B");
                        locationB.setLatitude(centerlat);
                        locationB.setLongitude(centerlongi);

                        float distance = locationA.distanceTo(locationB);
                        if (distance > 500.00){
                            LastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1));
                            enter_field_name_edittext.setVisibility(View.VISIBLE);
                            fieldname_text.setVisibility(View.VISIBLE);
                            save_field.setVisibility(View.VISIBLE);
                            main_marker.setImageResource(R.drawable.mark);
                            main_marker.setVisibility(View.VISIBLE);
                        }
                    }
                    tempdistanceArraylist.clear();
                    if(Location_arraylist.size() > 0){
                        marker =  getMarker(new LatLng(centerlat, centerlongi));
                        if (marker != null) {
                            TrackMainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    main_marker.setImageResource(R.drawable.pin_3);
                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2));
                                    LastMarker = marker ;
                                    enter_field_name_edittext.setVisibility(View.GONE);
                                    fieldname_text.setVisibility(View.GONE);
                                    save_field.setVisibility(View.GONE);
                                }
                            });
                            if(!mMapIsTouched) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_3));
                                            main_marker.setVisibility(View.GONE);
                                        }catch (Exception e){

                                        }


                                    }
                                }, 500);

                            }
                        }
                    }
                    if(!mMapIsTouched){
                        TrackMainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Geocoder geocoder = new Geocoder(TrackMainActivity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(centerlat, centerlongi, 1);
                                    String cityName = addresses.get(0).getAddressLine(0);
                                    String stateName = addresses.get(0).getAddressLine(1);
                                    String countryName = addresses.get(0).getAddressLine(2);
                                    tracklatitude = centerlat+"";
                                    tracklongitude = centerlongi+"";
                                    trackAddress = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2);
                                    fieldname_text.setText(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
                                    if(addresses.get(0).getLocality().equals("null")){
                                        trackCity = addresses.get(0).getAddressLine(0);
                                    }else {
                                        trackCity = addresses.get(0).getLocality();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }

                }
            });
        }

        markers.clear();
        for(int i = 0; i < Location_arraylist.size(); i++){
            double latitude = Double.parseDouble(Location_arraylist.get(i).getLatitude());
            double longitude = Double.parseDouble(Location_arraylist.get(i).getLongitude());
            final LatLng MELBOURNE = new LatLng(latitude, longitude);
            Marker melbourne = mMap.addMarker(new MarkerOptions()
                    .position(MELBOURNE)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1)));
            markers.add(melbourne);
        }
    }

    private synchronized  Marker getMarker(LatLng latLng){

        for ( int i = 0 ; i < markers.size() ; i++){
            Location locationA = new Location("point A");
            locationA.setLatitude(latLng.latitude);
            locationA.setLongitude(latLng.longitude);

            LatLng ltlng = markers.get(i).getPosition();

            Location locationB = new Location("point B");
            locationB.setLatitude(ltlng.latitude);
            locationB.setLongitude(ltlng.longitude);

            float distance = locationA.distanceTo(locationB);



            TempDistanceModal model = new TempDistanceModal();
            model.setDistance(distance);
            model.setMarker(markers.get(i));
            model.setVenue(Location_arraylist.get(i).getVanue());
            model.setAddres(Location_arraylist.get(i).getAddress());
            model.setCity(Location_arraylist.get(i).getCity());
            tempdistanceArraylist.add(model);

        }
        return   getNearbyMarker(tempdistanceArraylist);
    }

    private synchronized  Marker getNearbyMarker(ArrayList<TempDistanceModal> arr){

        synchronized (arr){
            // Now sort by address instead of name (default).
            Collections.sort(arr, new Comparator<TempDistanceModal>() {
                public int compare(TempDistanceModal one, TempDistanceModal other) {
                    return (int) (one.getDistance() - other.getDistance());

                }
            });
        }
        if (arr.get(0).getDistance() < 500.00){
            trackfieldname = arr.get(0).getVenue();
            tracklatitude = arr.get(0).getMarker().getPosition().latitude+"";
            tracklongitude = arr.get(0).getMarker().getPosition().longitude+"";
            trackAddress = arr.get(0).getAddres();
            trackCity = arr.get(0).getCity();
            return arr.get(0).getMarker();
        }else{
            return null;
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_track_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
