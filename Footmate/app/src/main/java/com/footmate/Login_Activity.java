package com.footmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.ConnectivityNetwork;
import com.footmate.utils.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class Login_Activity extends ActionBarActivity  implements
        GoogleApiClient.OnConnectionFailedListener{
    private Activity activity = Login_Activity.this;
    private Context context = Login_Activity.this;
    private ConnectivityNetwork internetAvialable;
    private CheckAlertDialog alertDialogManager;
    private boolean isInternet = false;
    private static String TAG = "Login_Activity";
    private ImageView img_login_fb, img_login_gmail;
    String android_id;
    public String username, email_id, image_url, about = "", LoginType= "";
    CallbackManager callbackManager;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        getSupportActionBar().hide();
        // check internet connection....
        setInternetConnction();
        //set Widget ids...
        setWidgetIDS();
        // setClick listner....
        setCLickListner();

        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, MODE_PRIVATE);
        editor = prefs.edit();

        android_id = prefs.getString(MyPreferenceManager.DEVICE_ID, null);

        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        // Application code
                                        Log.e(TAG, "Object Values.." + object);
                                        try {
                                            username = object.getString("name");
                                            email_id = object.getString("email");
                                            image_url = "http://graph.facebook.com/" + object.getString("id") + "/picture?type=large";
                                            about = "";
                                            LoginType = "Facebook";
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        LoginAsyn obj = new LoginAsyn();
                                        obj.execute();
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,email,bio");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e(TAG, "onError FACEBOOK"+ exception);
                    }
                });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e("GMAIL",result.getStatus()+"");
            handleSignInResult(result);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            Log.e(TAG, "Data FACEBOOK onActivityResult..." + data.getData());
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            username = acct.getDisplayName();
            email_id = acct.getEmail();
            about = "";
            image_url = acct.getPhotoUrl()+"";
            LoginType = "Gmail";
            Log.e("GOOGLE profile info", acct.getDisplayName() + acct.getIdToken() + acct.getPhotoUrl() + acct.getId() + acct.getEmail());
            LoginAsyn obj = new LoginAsyn();
            obj.execute();
        } else {
            // Signed out, show unauthenticated UI.
            Log.e("GMAIL", "RESULT NOT SUCCESS");
        }
    }

    // set CLick LIstner....
    private void setCLickListner() {
        img_login_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "user_friends", "email", "user_about_me"));
                }
        });
        img_login_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void setWidgetIDS() {
        img_login_fb = (ImageView) findViewById(R.id.img_login_fb);
        img_login_gmail = (ImageView) findViewById(R.id.img_login_gmail);

    }

    private void setInternetConnction() {
        alertDialogManager = new CheckAlertDialog();
        internetAvialable = new ConnectivityNetwork();

        isInternet = internetAvialable.isNetworkAvailable(context);
        if (!isInternet) {
            alertDialogManager.showcheckAlert3(Login_Activity.this, "Internet connection error.",
                    "Please connect the valid internet connection.",
                    true);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
    }

    // implement login webservice ...
    public class LoginAsyn extends AsyncTask<String, String, String> {
        String url, data;
        ProgressDialog p = new ProgressDialog(Login_Activity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.setMessage("Please Wait...");
            p.setCancelable(false);
            p.show();
        }
        @Override
        protected String doInBackground(String... params) {
            url = Constants.LoginUrl;
            try{
                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();

                json.put("UserName", username);
                json.put("EmailId", email_id);
                json.put("About", about);
                json.put("ImageUrl", image_url);
                json.put("LoginType", LoginType);
                json.put("DeviceId",android_id);
                json.put("DeviceType","Android");

                Log.e("", "LOGIN json" + json.toString());
                // HttpPost only accepsts arguments as string, so we turn the
                // json into a string.
                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e(TAG, "Login Code" + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e(TAG, "Login response" + data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null){
                p.dismiss();
                return;
            }
            try {
                JSONObject obj = new JSONObject(result);
//                JSONArray trackdetailarr = obj.getJSONArray("listOfTrackNotificationDetail");
                JSONObject logindetailobj = obj.getJSONObject("loginResponceDetail");
                editor.putString(MyPreferenceManager.LOGIN_ID, logindetailobj.getString("LoginId"));
                editor.putString(MyPreferenceManager.ABOUT, logindetailobj.getString("About"));
                editor.putString(MyPreferenceManager.IMAGE_URL, logindetailobj.getString("ImageUrl"));
                editor.putString(MyPreferenceManager.USERNAME, logindetailobj.getString("UserName"));
                editor.putString(MyPreferenceManager.EMAIL_ID, logindetailobj.getString("EmailId"));
                editor.putString(MyPreferenceManager.LOGIN_TYPE, logindetailobj.getString("LoginType"));
                editor.commit();
                p.dismiss();
                Intent i = new Intent(context,Home_Activity.class);
                startActivity(i);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }
}
