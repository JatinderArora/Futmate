package com.footmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.footmate.helper.MyPreferenceManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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

public class Map_Activity extends AppCompatActivity {
    private String TAG = "Map_Activity";
    private Context context = Map_Activity.this;
    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    GoogleMap mMap;
    double latitude, centerlat, centerlongi;
    double longitude;
    TextView address, txt_AddField, field_name, addressedit;
    String venue, add;
    LinearLayout pickupaddress;
    RelativeLayout bottomtabedit, bottomtab;
    String Adddress;
    LatLng center;
    String flag;
    int radious;
    Circle myCircle;
    MarkerOptions markerOptions;
    public static final int SELECT_LOCATION_CODE = 201;
    ImageView main_marker;
    Toolbar toolbar,save_toolbar;
    // save tooolbar text...
    private ImageView img_toolbar_back;
    private TextView text_top,text_save;
    public String slatitude, slongitude, city;
    // sharedprefs
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    ImageView img_menu_button;
    EditText search_view,field_name_edittext;
    private ArrayList<SelectListModal> nearby_places_arraylist;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<TempDistanceModal> tempdistanceArraylist = new ArrayList<>();
     Marker marker;
    Marker LastMarker = null;
    public static boolean mMapIsTouched = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_);
        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS,MODE_PRIVATE);
        editor = prefs.edit();
        // set Typeface....
        setTypeFace();
        // set teh top toolbar above of the map...
        setTopToolBar();
        // set the widgets ids...
        setWidgetIDSWithTypeFace();
        // set click listner....
        setClickListner();
        pickupaddress.setVisibility(View.GONE);
        nearby_places_arraylist = new ArrayList<SelectListModal>();
        nearby_places_arraylist = (ArrayList<SelectListModal>)getIntent().getSerializableExtra("Nearby_Places_Array");

        txt_AddField.setVisibility(View.GONE);
        setMap();

        txt_AddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_AddField.getText().toString().equals("ADD FIELD HERE?")) {
                    save_toolbar.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.GONE);
                    bottomtabedit.setVisibility(View.VISIBLE);
                    bottomtab.setVisibility(View.GONE);
                    field_name_edittext.requestFocus();
                    field_name_edittext.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                    field_name_edittext.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));

                } else if (txt_AddField.getText().toString().equals("PICK THIS FIELD")) {
                    editor.putString(MyPreferenceManager.LATITUDE, slatitude);
                    editor.putString(MyPreferenceManager.LONGITUDE, slongitude);
                    editor.putString(MyPreferenceManager.VANUE_NAME, field_name.getText().toString());
                    editor.putString(MyPreferenceManager.VANUE_ADDRESS, address.getText().toString());
                    editor.putString(MyPreferenceManager.VANUE_CITY, city);
                    editor.commit();
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

            Log.d("lat long",""+latitude+","+longitude);

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
                            pickupaddress.setVisibility(View.GONE);
                            txt_AddField.setText("ADD FIELD HERE?");
                            txt_AddField.setVisibility(View.VISIBLE);
                            main_marker.setImageResource(R.drawable.mark);
                            main_marker.setVisibility(View.VISIBLE);
                        }
                    }
                    tempdistanceArraylist.clear();

                    if(nearby_places_arraylist.size() > 0){
                        marker =  getMarker(new LatLng(centerlat, centerlongi));
                        if (marker != null) {
                            Map_Activity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2));
                                    main_marker.setImageResource(R.drawable.pin_3);
                                    LastMarker = marker ;
                                    txt_AddField.setVisibility(View.VISIBLE);
                                    txt_AddField.setText("PICK THIS FIELD");
                                    pickupaddress.setVisibility(View.VISIBLE);
                                    address.setText(add);
                                    field_name.setText(venue);
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
                        Map_Activity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Geocoder geocoder = new Geocoder(Map_Activity.this, Locale.getDefault());
                                List<Address> addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(centerlat, centerlongi, 1);
                                    String cityName = addresses.get(0).getAddressLine(0);
                                    String stateName = addresses.get(0).getAddressLine(1);
                                    String countryName = addresses.get(0).getAddressLine(2);
                                    slatitude = centerlat+"";
                                    slongitude = centerlongi+"";
                                    txt_AddField.setVisibility(View.VISIBLE);
                                    if(addresses.get(0).getLocality().equals("null")){
                                        city = addresses.get(0).getAddressLine(0);
                                    }else {
                                        city = addresses.get(0).getLocality();
                                    }
                                    addressedit.setText(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
                                    Log.e("ADDRES",addresses.get(0).getAddressLine(0)+",kkkkkkkkkkkkkk "+addresses.get(0).getAddressLine(1)+",kkkkkk "+addresses.get(0).getAddressLine(2)+" City "+city);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                }
            });

        }else if (location == null) {
                latitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LATITUDE, null));
                longitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LONGITUDE, null));
                Log.d("lat long",""+latitude+","+longitude);

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
                                pickupaddress.setVisibility(View.GONE);
                                txt_AddField.setText("ADD FIELD HERE?");
                                txt_AddField.setVisibility(View.VISIBLE);
                                main_marker.setImageResource(R.drawable.mark);
                                main_marker.setVisibility(View.VISIBLE);
                            }
                        }
                        tempdistanceArraylist.clear();

                        if(nearby_places_arraylist.size() > 0){
                            marker =  getMarker(new LatLng(centerlat, centerlongi));
                            if (marker != null) {
                                Map_Activity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_2));
                                        main_marker.setImageResource(R.drawable.pin_3);
                                        LastMarker = marker ;
                                        txt_AddField.setVisibility(View.VISIBLE);
                                        txt_AddField.setText("PICK THIS FIELD");
                                        pickupaddress.setVisibility(View.VISIBLE);
                                        address.setText(add);
                                        field_name.setText(venue);
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
                            Map_Activity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Geocoder geocoder = new Geocoder(Map_Activity.this, Locale.getDefault());
                                    List<Address> addresses = null;
                                    try {
                                        addresses = geocoder.getFromLocation(centerlat, centerlongi, 1);
                                        String cityName = addresses.get(0).getAddressLine(0);
                                        String stateName = addresses.get(0).getAddressLine(1);
                                        String countryName = addresses.get(0).getAddressLine(2);
                                        slatitude = centerlat+"";
                                        slongitude = centerlongi+"";
                                        txt_AddField.setVisibility(View.VISIBLE);
                                        if(addresses.get(0).getLocality().equals("null")){
                                            city = addresses.get(0).getAddressLine(0);
                                        }else {
                                            city = addresses.get(0).getLocality();
                                        }
                                        addressedit.setText(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
                                        Log.e("ADDRES",addresses.get(0).getAddressLine(0)+",kkkkkkkkkkkkkk "+addresses.get(0).getAddressLine(1)+",kkkkkk "+addresses.get(0).getAddressLine(2)+" City "+city);
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
        for(int i = 0; i < nearby_places_arraylist.size(); i++){
            double latitude = Double.parseDouble(nearby_places_arraylist.get(i).getLatitude());
            double longitude = Double.parseDouble(nearby_places_arraylist.get(i).getLongitude());
            final LatLng MELBOURNE = new LatLng(latitude, longitude);
            Marker melbourne = mMap.addMarker(new MarkerOptions()
                    .position(MELBOURNE)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_1)));
            markers.add(melbourne);
        }
    }

    private void setClickListner() {

        field_name_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (field_name_edittext.getText().toString().equals("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Map_Activity.this);
                        builder.setTitle("Futmate!");
                        builder.setMessage("Field Name is empty...");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }else {
                        editor.putString(MyPreferenceManager.LATITUDE,slatitude);
                        editor.putString(MyPreferenceManager.LONGITUDE,slongitude);
                        editor.putString(MyPreferenceManager.VANUE_NAME,field_name_edittext.getText().toString());
                        editor.putString(MyPreferenceManager.VANUE_ADDRESS,addressedit.getText().toString());
                        editor.putString(MyPreferenceManager.VANUE_CITY, city);
                        editor.commit();
                        finish();

                    }
                }
                return false;
            }
        });
    }

    private void setTopToolBar() {
        // default toolbar firsttime....
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        img_menu_button = (ImageView)toolbar.findViewById(R.id.img_menu_button);
        search_view = (EditText)toolbar.findViewById(R.id.search_view);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.organizer_cross);
        // default toolbar click listner....
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        img_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(Map_Activity.this,Select_Field_Screen.class);
                startActivity(i);
            }
        });

        search_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))|| (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    String Searchdata = search_view.getText().toString();
                    Log.d("searchdata", Searchdata);
                    if (Searchdata != null && !Searchdata.equals("")) {
                        new GeocoderTask().execute(Searchdata);
                    }
                    return true;
                }
                return false;
            }
        });
        //// default toolbar save toolbar.....
        save_toolbar = (Toolbar)findViewById(R.id.save_toolbar);
        img_toolbar_back = (ImageView)save_toolbar.findViewById(R.id.img_toolbar_back);
        text_top = (TextView)save_toolbar.findViewById(R.id.text_top);
        text_save = (TextView)save_toolbar.findViewById(R.id.text_save);
        // save toolbar click listner....
        img_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Map_Activity.this,Start_New_PlanActivity.class);
                startActivity(intent);
                finish();
            }
        });
        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (field_name_edittext.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Map_Activity.this);
                    builder.setTitle("Futmate!");
                    builder.setMessage("Field Name is empty...");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                        }
                    });
                    builder.show();
                }else {
                    editor.putString(MyPreferenceManager.LATITUDE,slatitude);
                    editor.putString(MyPreferenceManager.LONGITUDE,slongitude);
                    editor.putString(MyPreferenceManager.VANUE_NAME,field_name_edittext.getText().toString());
                    editor.putString(MyPreferenceManager.VANUE_ADDRESS,addressedit.getText().toString());
                    editor.putString(MyPreferenceManager.VANUE_CITY, city);
                    editor.commit();
                    finish();

                }
            }
        });


    }

    private void setTypeFace() {
        Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }

    private void setWidgetIDSWithTypeFace() {
        address = (TextView)findViewById(R.id.addressTV);
        addressedit = (TextView)findViewById(R.id.addressTV1);
        txt_AddField = (TextView)findViewById(R.id.txt_AddField);
        field_name = (TextView)findViewById(R.id.field_name);
        pickupaddress = (LinearLayout)findViewById(R.id.pickupaddress);
        bottomtabedit = (RelativeLayout)findViewById(R.id.bottomtabedit);
        bottomtab = (RelativeLayout)findViewById(R.id.bottomtab);
        field_name_edittext = (EditText)findViewById(R.id.field_name_edittext);
        main_marker = (ImageView)findViewById(R.id.main_marker);

        address.setTypeface(Ubuntu_Medium);
        addressedit.setTypeface(Ubuntu_Medium);
        txt_AddField.setTypeface(Ubuntu_Medium);
        field_name.setTypeface(Ubuntu_Medium);
        field_name_edittext.setTypeface(Ubuntu_Medium);

    }

    private void GenerateCirle(int r) {
        center = mMap.getCameraPosition().target;
        if(myCircle!=null){
            myCircle.remove();
        }
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(center.latitude, center.longitude))
                .radius(r)
                .fillColor(0x4034aadc)
                .strokeColor(0x34aadc)
                .strokeWidth(5);

        myCircle = mMap.addCircle(circleOptions);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
                Log.d("addresses",""+addresses);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Clears all the existing markers on the map
//            mMap.clear();

            // Adding Markers on Google Map for each matching address
//            for(int i=0;i<addresses.size();i++){

            Address address = (Address) addresses.get(0);

            // Creating an instance of GeoPoint, to display in Google Map
            center = new LatLng(address.getLatitude(), address.getLongitude());

            String addressText = String.format("%s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getCountryName());

            markerOptions = new MarkerOptions();
            markerOptions.position(center);
            markerOptions.title(addressText);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

            mMap.animateCamera(CameraUpdateFactory.newLatLng(center));

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
            model.setVenue(nearby_places_arraylist.get(i).getVanue());
            model.setAddres(nearby_places_arraylist.get(i).getAddress());
            model.setCity(nearby_places_arraylist.get(i).getCity());
            tempdistanceArraylist.add(model);

        }
        return   getNearbyMarker(tempdistanceArraylist);
    }

    private synchronized  Marker getNearbyMarker(ArrayList<TempDistanceModal> arr){

        synchronized (arr){
            // Now sort by address instead of name (default).
            Collections.sort(arr, new Comparator<TempDistanceModal>() {
                public int compare(TempDistanceModal one, TempDistanceModal other) {
                    return (int)(one.getDistance() - other.getDistance());

                }
            });
        }
        if (arr.get(0).getDistance() < 500.00){
            slatitude = arr.get(0).getMarker().getPosition().latitude+"";
            slongitude = arr.get(0).getMarker().getPosition().longitude+"";
            venue = arr.get(0).getVenue();
            add = arr.get(0).getAddres();
            city = arr.get(0).getCity();
            return arr.get(0).getMarker();
        }else{
            return null;
        }

    }

    public void setUI(){

    }

    @Override
    public void onBackPressed() {
        if(bottomtabedit.getVisibility() == View.VISIBLE)
        {
            bottomtabedit.setVisibility(View.GONE);
            bottomtab.setVisibility(View.VISIBLE);
            save_toolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }
}
