package com.footmate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGameActivity extends Activity {
    Activity activity = CreateGameActivity.this;
    Context context = CreateGameActivity.this;
    public static final String TAG = "CreateGameActivity";
    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;

    private LinearLayout top_layout;
    private ImageView img_left_right_swipe, img_addprofile, img_run_logo, circle_img_gender, img_applogo;
    private CircleImageView profile_pic;
    private TextView match_date_time, circle_text_spots, below_text_spot, circle_text_teamsize, below_text_gender,
            text_organizer, text_name, below_text_teamsize,text_description,text_title,txtAreYouPlay, venue;


    private ImageView img_cross, img_right, img_top;
    SharedPreferences myPrefs;
    String username = "";
    String imageurl = "";
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;

    private String OrganizerMode, Matchdate_time,new_DateTime_withFormat, Vanue, Address, City, Match_Type, Title,
            Description, TeamSize,TypeOfPost, NumSpotCom, GameLevel, Gender, PrivacyPolicy, Match_Plan_CreateBy, JoinStatus,
            Latitude, Longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        // getting the intents....
        getIntentFromBack();
        //set typeface...for external font families...
        setTypeface();
        //set widget ids and their typeface....
        setWidgetIDSWithTypeface();
        // getting prefrances and set in widgets....
        setPefrancesandGetIntentInWidgets();
        // set logo and layout background.......
        setLogoWithLayoutBackground();
        //setClick listner...
        setClickListner();

    }

    private void setLogoWithLayoutBackground() {
        if (Match_Type.equals("Match")) {
            img_run_logo.setImageResource(R.drawable.feed_ground_icon);
            top_layout.setBackground(getResources().getDrawable(R.drawable.match_bg));
        } else if (Match_Type.equals("Freeplay")) {
            img_run_logo.setImageResource(R.drawable.post_match_ico);
            top_layout.setBackground(getResources().getDrawable(R.drawable.freeplay_match_bg));
        } else if (Match_Type.equals("Practice")) {
            img_run_logo.setImageResource(R.drawable.run_user);
            top_layout.setBackground(getResources().getDrawable(R.drawable.match_img3));
        }

        if (Gender.equals("Male")) {
            circle_img_gender.setImageResource(R.drawable.male_s);
        } else if (Gender.equals("Female")) {
            circle_img_gender.setImageResource(R.drawable.female_s);
        } else if (Gender.equals("Co Ed")) {
            circle_img_gender.setImageResource(R.drawable.co_ed_s);
        }


    }

    private void getIntentFromBack() {
        // getting the intent.......
        TypeOfPost  = getIntent().getStringExtra("TYPEOFPOST");
        OrganizerMode = getIntent().getStringExtra("ORGANIZER_MODE");
        Matchdate_time = getIntent().getStringExtra("DATE_TIME");
        Log.e(TAG,"" + Matchdate_time);

        // convert the one datetime format to another......
        Calendar cal= Calendar.getInstance();/*1 Mar 2016,11 : 13 AM*/
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy,HH : mm a");
        Date testDate = null;
        try {
            testDate = sdf.parse(Matchdate_time);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd");
        String[] date_time = Matchdate_time.split(",");
        String date = date_time[0];
        String time = date_time[1];
        new_DateTime_withFormat = formatter.format(testDate) +" // "+ time;


        Vanue = getIntent().getStringExtra("VANUE");
        Address = getIntent().getStringExtra("ADDRESS");
        City = getIntent().getStringExtra("CITY");
        Match_Type = getIntent().getStringExtra("MATCH_TYPE");
        Title = getIntent().getStringExtra("TITLE");
        Description = getIntent().getStringExtra("DESCRIPTION");
        Match_Plan_CreateBy = getIntent().getStringExtra("MATCH_PLAN_CREATED_BY");
        Latitude = getIntent().getStringExtra("LATITUDE");
        Longitude = getIntent().getStringExtra("LONGITUDES");
        TeamSize = getIntent().getStringExtra("TEAM_SIZE");
        NumSpotCom = getIntent().getStringExtra("NUMBERS_SPOT_COMPLETE");
        GameLevel = getIntent().getStringExtra("GAME_LEVEL");
        Gender = getIntent().getStringExtra("GENDER");
        PrivacyPolicy = getIntent().getStringExtra("PRIVACY_TYPE");
    }

    private void setClickListner() {
        img_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               activity.finish();
            }
        });
        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinStatus = "NotJoin";
                CreateGameAsyn obj = new CreateGameAsyn();
                obj.execute();
            }
        });
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinStatus = "Join";
                CreateGameAsyn obj = new CreateGameAsyn();
                obj.execute();
            }
        });

    }

    private void setPefrancesandGetIntentInWidgets() {
        myPrefs = context.getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
        imageurl = myPrefs.getString(MyPreferenceManager.IMAGE_URL, null);
        username = myPrefs.getString(MyPreferenceManager.USERNAME, null);

        text_name.setText(username);
        match_date_time.setText(new_DateTime_withFormat);
        circle_text_spots.setText(NumSpotCom);
        circle_text_teamsize.setText(TeamSize);
        text_name.setText(username);
        text_title.setText(Title);
        text_description.setText(Description);
        venue.setText(Vanue);

        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        imageLoader.displayImage(imageurl, profile_pic, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                //profile_pic.setImageResource(R.drawable.user_img);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

    }

    private void setWidgetIDSWithTypeface() {
        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        text_organizer = (TextView) findViewById(R.id.text_organizer);
        text_name = (TextView) findViewById(R.id.text_name);
        img_top = (ImageView) findViewById(R.id.img_top);
        img_cross = (ImageView) findViewById(R.id.img_cross);
        img_right = (ImageView) findViewById(R.id.img_right);
        text_organizer.setTypeface(Ubuntu_Medium);
        text_name.setTypeface(Ubuntu_Regular);
        top_layout = (LinearLayout) findViewById(R.id.top_layout);
        img_cross = (ImageView) findViewById(R.id.img_cross);
        img_left_right_swipe = (ImageView) findViewById(R.id.img_left_right_swipe);
        img_addprofile = (ImageView) findViewById(R.id.img_addprofile);
        img_run_logo = (ImageView) findViewById(R.id.img_run_logo);
        circle_img_gender = (ImageView) findViewById(R.id.circle_img_gender);
        img_applogo = (ImageView) findViewById(R.id.img_applogo);
        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        match_date_time = (TextView) findViewById(R.id.match_date_time);
        circle_text_spots = (TextView) findViewById(R.id.circle_text_spots);
        below_text_spot = (TextView) findViewById(R.id.below_text_spot);
        circle_text_teamsize = (TextView) findViewById(R.id.circle_text_teamsize);
        below_text_gender = (TextView) findViewById(R.id.below_text_gender);
        below_text_teamsize = (TextView) findViewById(R.id.below_text_teamsize);
        text_organizer = (TextView) findViewById(R.id.text_organizer);
        text_name = (TextView) findViewById(R.id.text_name);
        text_description = (TextView) findViewById(R.id.text_description);
        text_title = (TextView) findViewById(R.id.text_title);
        txtAreYouPlay = (TextView)findViewById(R.id.txtAreYouPlay);
        venue = (TextView)findViewById(R.id.venue);

        match_date_time.setTypeface(Ubuntu_Medium);
        circle_text_spots.setTypeface(Ubuntu_Regular);
        below_text_spot.setTypeface(Ubuntu_Medium);
        circle_text_teamsize.setTypeface(Ubuntu_Regular);
        below_text_gender.setTypeface(Ubuntu_Medium);
        below_text_teamsize.setTypeface(Ubuntu_Medium);
        text_organizer.setTypeface(Ubuntu_Medium);
        text_name.setTypeface(Ubuntu_Medium);
        text_description.setTypeface(Ubuntu_Regular);
        text_title.setTypeface(Ubuntu_Medium);
        txtAreYouPlay.setTypeface(Ubuntu_Bold);
        venue.setTypeface(Ubuntu_Medium);

    }

    private void setTypeface() {
        Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }


    // create game webservice executed here....//.....................................  create if organizer mode ======= True False
    public class CreateGameAsyn extends AsyncTask<String, String, String> {
        final ProgressDialog pDialog = new ProgressDialog(context);
        String url = Constants.CreateNewPlanForMatch;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("DateTimeOfMatch", Matchdate_time);
                json.put("Venue", Vanue);
                json.put("Address",Address);
                json.put("City",City);
                json.put("MatchType", Match_Type);
                json.put("Title", Title);
                json.put("Description", Description);
                json.put("TeamSize", TeamSize);
                json.put("NoOfPlayerToCompleteTeam", NumSpotCom);
                json.put("GameLevel", GameLevel);
                json.put("Gender", Gender);
                json.put("PrivacyType", PrivacyPolicy);
                json.put("MatchPlanCreatedBy", Match_Plan_CreateBy);
                json.put("Latitude", Latitude);
                json.put("Longitude", Longitude);
                json.put("TypeOfPost",TypeOfPost);
                json.put("OrganizarMode",OrganizerMode);
                json.put("JoinStatus",JoinStatus);

                Log.e("JSON", String.valueOf(json));

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e(TAG, " CreateNewGAmePlan Response Code = " + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e(TAG, "data of Create Game" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            try {
                String response = new JSONObject(result).getString("Responce");
                Log.e(TAG, "CreateNewGAmePlan response......................." + response);
                if (response.equals("1")) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Game not Created", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
