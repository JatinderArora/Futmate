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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.footmate.fragment.FragmentDrawer;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.ConnectivityNetwork;
import com.footmate.utils.Constants;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.footmate.adapter.SearchPlayerListAdapter;

import com.footmate.fragment.Home_Fragment;

import com.footmate.model.MyGamesUpcomingModal;
import com.footmate.model.SearchPlayersModal;
import com.footmate.model.SelectListModal;


public class Home_Activity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        ResultCallback<LocationSettingsResult>{

    // internet connetion .....with AlertDialog
    private ConnectivityNetwork internetAvialable;
    private CheckAlertDialog alertDialogManager;
    private boolean isInternet = false;

    private static String TAG = Home_Activity.class.getSimpleName();
    private Context context = Home_Activity.this;
    private Typeface Ubuntu_Regular;

    private FragmentDrawer drawerFragment;
    private Toolbar mToolbar;
    private EditText search_view;
    TextView cancel;
    private static ImageView img_bell;
    public static TextView timerText;
    public static RelativeLayout timer_ring_layout;
    private String currentFragment = "" ;


    private ArrayList<MyGamesUpcomingModal> modal_List;
    ArrayList<SearchPlayersModal> searchPlayerArray;
    ListView playerslistview;
    FrameLayout frameLayout;
    SearchPlayerListAdapter adapter;

    //.....................................google Location.........................

    public SharedPreferences prefs;
    SharedPreferences.Editor editor;

    String PlayerID = "";
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date strDate = null;
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

    public String Latitude = "";
    public String Longitude = "";
    RelativeLayout active_noti_layout;

    //..............................................................................


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        //........................google location........
        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, MODE_PRIVATE);
        editor = prefs.edit();
        PlayerID = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
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
        }, 1000);


        //...............................................

        // check internet connetion and show dialog is not persent..
        setInternetConnction();
        // set widget ids to view..
        setWidgetIDs();
        // set actionbar ....
        setActionBar();
        // setup the drawerFragment...
        // setDrawerFragment();
        modal_List = new ArrayList<MyGamesUpcomingModal>();
        searchPlayerArray = new ArrayList<>();

        // the Recently used venues list by Api......
        RecentVenueListAsyn object = new RecentVenueListAsyn();
        object.execute();

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        // display the first navigation drawer view on com.footmate.app launch
        displayView(0);
        // working with img_chat...
        img_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "" + search_view.getText().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        if(getIntent().getStringExtra("NotiState") != null){
            if(getIntent().getStringExtra("NotiState").equals("Active")){
                showActiveNoti();
            }
        }
        active_noti_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home_Activity.this, NotificationActivity.class);
                startActivity(i);
                active_noti_layout.setVisibility(View.GONE);
                img_bell.setVisibility(View.VISIBLE);
            }
        });

        if (prefs.getString(MyPreferenceManager.TIMER_STATUS, null) != null) {
            showGreenRing();
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerslistview.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
                search_view.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search_view.getWindowToken(), 0);
                cancel.setVisibility(View.GONE);
            }
        });

        search_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Your piece of code on keyboard search click
                    playerslistview.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.VISIBLE);
                    search_view.setText("");
                    cancel.setVisibility(View.GONE);
                    Intent i = new Intent(Home_Activity.this, SearchPlayerActivity.class);
                    i.putExtra("SearchArray", searchPlayerArray);
                    startActivityForResult(i, 10);
                    return true;
                }
                return false;
            }
        });

        search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cancel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    Log.e("Char", s.toString() + " " + s);
                    new SearchPlayers().execute(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setWidgetIDs() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        search_view = (EditText)mToolbar.findViewById(R.id.search_view);
        img_bell = (ImageView)mToolbar.findViewById(R.id.img_bell);
        timerText = (TextView)mToolbar.findViewById(R.id.timerText);
        timer_ring_layout = (RelativeLayout)mToolbar.findViewById(R.id.timer_ring_layout);
        active_noti_layout = (RelativeLayout)mToolbar.findViewById(R.id.active_noti_layout);
        cancel = (TextView)mToolbar.findViewById(R.id.cancel);
        playerslistview = (ListView) findViewById(R.id.playerslistview);
        frameLayout = (FrameLayout)findViewById(R.id.container_body);

        // SET TYPEFACE FOR THE SEARCH eDIT vIEW....
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
        search_view.setTypeface(Ubuntu_Regular);
    }

    public static void showGreenRing(){
        timer_ring_layout.setVisibility(View.VISIBLE);
        img_bell.setVisibility(View.GONE);
    }
    public static void hideGreenRing(){
        timer_ring_layout.setVisibility(View.GONE);
        img_bell.setVisibility(View.VISIBLE);
    }

    public  void showActiveNoti(){
        active_noti_layout.setVisibility(View.VISIBLE);
        img_bell.setVisibility(View.GONE);
    }

    public void setActionBar(){
        setSupportActionBar(mToolbar);
        // set the actionbar attributes for toolbar...
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                if (!currentFragment.equals("Home"))
                fragment = new Home_Fragment();

                break;
//            case 2:
//                if (currentFragment.equals("Home"))
//                    com.footmate.fragment = new Home_Fragment();
//                    Bundle b = new Bundle();
//                    b.putInt("position", position);
//                    com.footmate.fragment.setArguments(b);
//                break;
//            case 3:
//                if (currentFragment.equals("Home"))
//                    com.footmate.fragment = new Home_Fragment();
//                    b = new Bundle();
//                    b.putInt("position", position);
//                    com.footmate.fragment.setArguments(b);
//
//                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private void setInternetConnction() {
        alertDialogManager = new CheckAlertDialog();
        internetAvialable = new ConnectivityNetwork();

        isInternet = internetAvialable.isNetworkAvailable(context);
        if (!isInternet){
            alertDialogManager.showcheckAlert3(Home_Activity.this, "Internet connection error.",
                    "Please connect the valid internet connection.",
                    true);
        }

    }
    public void currentFragment(String currentFragment){
        this.currentFragment = currentFragment;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Home_Activity.this.finish();
    }

    // ......................................google location.................................

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
                .addConnectionCallbacks(Home_Activity.this)
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
                    status.startResolutionForResult(Home_Activity.this, REQUEST_CHECK_SETTINGS);
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
        Log.e("requestCode",requestCode + "resultCode" + resultCode);
        if(requestCode == 10){
            playerslistview.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);
            search_view.setText("");
            cancel.setVisibility(View.GONE);
        }
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
//        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
//                Toast.LENGTH_SHORT).show();
        Latitude = ""+mCurrentLocation.getLatitude();
        Longitude = ""+mCurrentLocation.getLongitude();
        Log.e("HOmeActivity","My Current Location.....Latitude...."+Latitude);
        Log.e("HOmeActivity", "My Current Location.....Longitude...." + Longitude);

        editor.putString(MyPreferenceManager.HOME_LATITUDE, Latitude);
        editor.putString(MyPreferenceManager.HOME_LONGITUDE, Longitude);
        editor.commit();
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
//            pDialog.show();
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
                        Utilities.locationlist.add(modal);
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "No,Recently Fields are Created.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    // apply ListOfPlayerAcordingToSearch webservice/.......
    public class SearchPlayers extends AsyncTask<String, String, String> {
        String url = Constants.ListOfPlayerAcordingToSearch;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.e("PARAMS",params[0]);
                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();

                json.put("SearchByPlayerId", PlayerID);
                json.put("PlayerName",params[0]);
                Log.e("Search Players", "json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("Search Players", "data" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                return;
            }
            try {
                String response = new JSONObject(result).getString("Responce");
                if(response.equals("1")){
                    String listOfPlayers = new JSONObject(result).getString("listOfPlayers");
                    JSONArray playersarr = new JSONArray(listOfPlayers);
                    searchPlayerArray.clear();
                    for (int i = 0;i < playersarr.length();i++){
                        JSONObject obj = playersarr.getJSONObject(i);
                        SearchPlayersModal smodal = new SearchPlayersModal();
                        smodal.setAbout(obj.getString("About"));
                        smodal.setEmailId(obj.getString("EmailId"));
                        smodal.setFollowStatus(obj.getString("FollowStatus"));
                        smodal.setId(obj.getString("Id"));
                        smodal.setImageUrl(obj.getString("ImageUrl"));
                        smodal.setLoginType(obj.getString("LoginType"));
                        smodal.setUserName(obj.getString("UserName"));
                        searchPlayerArray.add(smodal);
                    }
                    playerslistview.setVisibility(View.VISIBLE);
                    frameLayout.setVisibility(View.GONE);
                    adapter = new SearchPlayerListAdapter(searchPlayerArray, Home_Activity.this);
                    playerslistview.setAdapter(adapter);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

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
//            pDialog.show();
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
                        Utilities.locationlist.add(modal);


                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "No Match Plan created near your location", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("DESTROy", "DESTROy");
        editor.putString(MyPreferenceManager.TRACK_TYPE, null);
        editor.putString(MyPreferenceManager.TRACK_LATITUDE, null);
        editor.putString(MyPreferenceManager.TRACK_LONGITUDE, null);
        editor.putString(MyPreferenceManager.TRACK_FIELDNAME, null);
        editor.putString(MyPreferenceManager.TRACK_ORGANISERMODE, null);
        editor.putString(MyPreferenceManager.TRACK_ADDRESS, null);
        editor.putString(MyPreferenceManager.TIMER_STATUS, null);
        editor.putString(MyPreferenceManager.TRACK_CITY, null);
        editor.commit();
    }
}
