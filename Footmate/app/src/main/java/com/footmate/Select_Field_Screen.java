package com.footmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.footmate.adapter.SelectFieldAdpater;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.model.SelectListModal;

import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.ConnectivityNetwork;
import com.footmate.utils.Constants;
import com.footmate.utils.ListViewMeasureHeight;
import com.footmate.utils.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;


public class Select_Field_Screen extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>{


    private Context context = Select_Field_Screen.this;
    private String TAG = "Select_Field_Screen";

    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    private Toolbar toolbar;
    private EditText search_view;
    private ImageView img_map;
    private TextView recent_places, text_nearbyfields, addnewfield_greay, text_addnewfield_click;
    private ListView listView_Recent, list_NearBy;

    SharedPreferences myprefs;
    String PlayerID = "";
    private ArrayList<SelectListModal> modal_List;
    private ArrayList<SelectListModal> recent_modal_List;
    private SelectFieldAdpater mAdapter;
    private ListViewMeasureHeight list_Hight;


    CheckAlertDialog checkalert;


    ConnectivityNetwork connectivityNetwork;
    boolean isInternet;
    private TextView text_internetconnection;




//    --------------------------Google Location params.........................

    /**
     * Constant used in the location settings dialog.
     */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select__field__screen);

        text_internetconnection = (TextView)findViewById(R.id.text_internetconnection);
        connectivityNetwork = new ConnectivityNetwork();
        isInternet = connectivityNetwork.isNetworkAvailable(context);
        if (!(isInternet)){
            text_internetconnection.setVisibility(View.VISIBLE);
        }else{
            text_internetconnection.setVisibility(View.GONE);
        }


        myprefs = context.getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
        PlayerID = myprefs.getString(MyPreferenceManager.LOGIN_ID, null);
        Log.e(TAG, "PlayerID......" + PlayerID);
        // alert Dialog
        checkalert = new CheckAlertDialog();

        // arraylists of NearByPlaces and RecentPlaces....
        modal_List = new ArrayList<SelectListModal>();
        recent_modal_List = new ArrayList<SelectListModal>();

        //set typeface...for external font families...
        setTypeface();
        // set toolbar..with text and navigation back button..
        setToolBar();
        //set custom listviews....
        setWidgetIDs();
        // set Click listner....
        setclickListner();

        // the Recently used venues list by Api......
        RecentVenueListAsyn object = new RecentVenueListAsyn();
        object.execute();


        // getting google location as latitude and longitude........
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building the GoogleApiClient, LocationRequest, and
        // LocationSettingsRequest objects.
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLocationSettings();
            }
        },1000);

    }

    private void setclickListner() {
        listView_Recent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String select_venue = recent_modal_List.get(position).getVanue();
                String select_address = recent_modal_List.get(position).getAddress();
                String select_city = recent_modal_List.get(position).getCity();
                String lat = recent_modal_List.get(position).getLatitude();
                String longi = recent_modal_List.get(position).getLongitude();

                Intent intent = new Intent();
                intent.putExtra("VANUE", select_venue);
                intent.putExtra("ADDRESS", select_address);
                intent.putExtra("CITY",select_city);
                intent.putExtra("LAT", lat);
                intent.putExtra("LONG", longi);
                setResult(2, intent);
                finish();//finishing activity
            }
        });
        list_NearBy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String select_venue = modal_List.get(i).getVanue();
                String select_address = modal_List.get(i).getAddress();
                String select_city = modal_List.get(i).getCity();
                String lat = modal_List.get(i).getLatitude();
                String longi = modal_List.get(i).getLongitude();

                Intent intent = new Intent();
                intent.putExtra("VANUE", select_venue);
                intent.putExtra("ADDRESS", select_address);
                intent.putExtra("CITY",select_city);
                intent.putExtra("LAT", lat);
                intent.putExtra("LONG", longi);
                setResult(2, intent);
                finish();//finishing activity

            }
        });

        text_addnewfield_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Utilities.locationlist", Utilities.locationlist.size()+"");
                Intent intent = new Intent(Select_Field_Screen.this, Map_Activity.class);
                intent.putExtra("Nearby_Places_Array", Utilities.locationlist);
                startActivity(intent);
                finish();
            }
        });


    }

    private void setWidgetIDs() {
        recent_places = (TextView) findViewById(R.id.recent_places);
        text_nearbyfields = (TextView) findViewById(R.id.text_nearbyfields);
        addnewfield_greay = (TextView) findViewById(R.id.addnewfield_greay);
        text_addnewfield_click = (TextView) findViewById(R.id.text_addnewfield_click);
        listView_Recent = (ListView) findViewById(R.id.listView_Recent);
        list_NearBy = (ListView) findViewById(R.id.list_NearBy);

        recent_places.setTypeface(Ubuntu_Medium);
        text_nearbyfields.setTypeface(Ubuntu_Medium);
        addnewfield_greay.setTypeface(Ubuntu_Medium);
        text_addnewfield_click.setTypeface(Ubuntu_Medium);
    }

    private void setToolBar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // set the actionbar attributes for toolbar...
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        // toolbar text....
        search_view = (EditText) toolbar.findViewById(R.id.search_view);
        search_view.setTypeface(Ubuntu_Regular);

        // toolbar map button...and cross buttons with click event...
        toolbar.setNavigationIcon(R.drawable.organizer_cross);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        img_map = (ImageView) toolbar.findViewById(R.id.img_map);
        img_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Utilities.locationlist Size",Utilities.locationlist.size()+"");
                Intent intent = new Intent(Select_Field_Screen.this, Map_Activity.class);
                intent.putExtra("Nearby_Places_Array", Utilities.locationlist);
                startActivity(intent);
                finish();
            }
        });


    }

    private void setTypeface() {
        Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }



    // apply NearByApi webservice/...................................................................................
    public class NearByApi extends AsyncTask<Void, String, String> {
        final ProgressDialog pDialog = new ProgressDialog(context);
        String url = Constants.ListOfMatchsAcordingToPlayerLocation;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();

                json.put("PlayerId", PlayerID);
                json.put("Latitude", mCurrentLocation.getLatitude() + "");
                json.put("Longitude", mCurrentLocation.getLongitude() + "");
                Log.e("Select Field Screen..","json......."+json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e(TAG, " Response Code For NearBy Places............. = " + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e(TAG, "data" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if (result == null) {
                pDialog.dismiss();
                return;
            }
            try {
                String response = new JSONObject(result).getString("Responce");
                if (response.equals("1")) {
                    String listofmatches = new JSONObject(result).getString("listOfMatches");
                    JSONArray jsonArray = new JSONArray(listofmatches);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        SelectListModal modal = new SelectListModal();
                        modal.setVanue(object.getString("Venue"));
                        modal.setAddress(object.getString("Address"));
                        modal.setLatitude(object.getString("Latitude"));
                        modal.setLongitude(object.getString("Longitude"));
                        modal.setDistance(object.getString("Distance"));
                        modal.setCity(object.getString("City"));

                        modal_List.add(modal);
                        Utilities.locationlist.add(modal);


                    }
                    mAdapter = new SelectFieldAdpater(context, modal_List);
                    list_NearBy.setAdapter(mAdapter);

                    // getting the list com.footmate.adapter Height and show
                    // the all items of the ListViews..
                    // and disable the scroll property of the ListViews...
                    list_Hight = new ListViewMeasureHeight();
                    list_Hight.setListViewHeightBasedOnChildren(list_NearBy);


                } else {
                    Toast.makeText(getApplicationContext(), "No Match Plan created near your location", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
//........................complete nearby api...............................................................................................................
//......................Recently Used Fields...................................................................................................................
    public class RecentVenueListAsyn extends AsyncTask<Void, String, String> {
    final ProgressDialog pDialog = new ProgressDialog(context);
    String url = Constants.ListOfRecentVenueAccordingToPlayerId;
    String data = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            DefaultHttpClient client = new DefaultHttpClient();
            // Setup of the post call to the server
            HttpPost post = new HttpPost(url);

            post.setHeader("Content-type", "application/json");

            JSONObject json = new JSONObject();

            json.put("PlayerId", PlayerID);

            StringEntity se = new StringEntity(json.toString());
            post.setEntity(se);

            // Here we'll receive the response.
            HttpResponse response;
            response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            Log.e(TAG, " Recently Used Venues LIst code............. = " + code);
            HttpEntity entity = response.getEntity();
            data = EntityUtils.toString(entity);
            Log.e(TAG, "data" + data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        pDialog.dismiss();
        if (result == null) {
            pDialog.dismiss();
            return;
        }
        try {
            String response = new JSONObject(result).getString("Responce");
            String listofmatches = new JSONObject(result).getString("listOfRecentVenue");
            if (response.equals("1")) {
                JSONArray jsonArray = new JSONArray(listofmatches);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    SelectListModal modal = new SelectListModal();
                    // here set the playerid in the match id modal method for recent places
                    modal.setVanue(object.getString("Venue"));
                    modal.setAddress(object.getString("Address"));
                    modal.setLatitude(object.getString("Latitude"));
                    modal.setLongitude(object.getString("Longitude"));
                    modal.setCity(object.getString("City"));
                    recent_modal_List.add(modal);
                    Utilities.locationlist.add(modal);
                }
                mAdapter = new SelectFieldAdpater(context, recent_modal_List);
                listView_Recent.setAdapter(mAdapter);

                // getting the list com.footmate.adapter Height and show
                // the all items of the ListViews..
                // and disable the scroll property of the ListViews...
                list_Hight = new ListViewMeasureHeight();
                list_Hight.setListViewHeightBasedOnChildren(listView_Recent);


            } else {
                Toast.makeText(getApplicationContext(), "No,Recently Fields are Created.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

//..............................complete recently used api............................................................
    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
//            updateUI();
        }
    }
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
//        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest).setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }
    /**
     * Check if the device's location settings are adequate for the com.footmate.app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }
    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(Select_Field_Screen.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
            default:
                finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                    default:
                        finish();
                }
                break;
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
//                setButtonsEnabledState();
            }
        });

    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
//                setButtonsEnabledState();
            }
        });
    }


    @Override
protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
}

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }
    }
    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
        NearByApi obj1 = new NearByApi();
        obj1.execute();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }





}





