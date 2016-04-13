package com.footmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

import com.footmate.helper.MyPreferenceManager;
import com.footmate.model.SelectListModal;
import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.Constants;

public class NewPlan_Activity extends AppCompatActivity {
    private Context context = NewPlan_Activity.this;
    private String TAG = "NewPlan_Activity";

    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    private Toolbar toolbar;
    private TextView text_top;
    private ImageView img_toolbar_back,
            img_spots_up,
            img_spots_down,
            img_team_up,
            img_team_down,
            img_level_up,
            img_level_down,
            img_male,
            img_female,
            img_coed,
            img_myfriends,
            img_friendsofplayer,
            img_anyone,
            img_check;
    private TextView text_title,
            text_spots,
            text_spots_number,
            text_team,
            text_team_number,
            text_level,
            text_level_number,
            text_gender,
            text_show_gener,
            te_male,
            text_female,
            text_coed,
            text_gameopen,
            text_show_game,
            text_myfriends,
            text_friendsofplayer,
            text_anyone,
            text_creategames;
    private EditText edit_title, edit_description;
    private LinearLayout layout4, layout5, layout6, layout7, layout8;

    int numTest = 1;
    int spotsTest = 1;
    boolean check_Gender = false;
    boolean check_OpenGame = false;
    SharedPreferences myprefs;

    public String PlayerID, Title, Description, NoOfPlayerSpot,GameLevel, Gender, PrivacyType, TeamSize_Num;
    int TeamSize = 1;
    String OrganizerMode = "";
    public String matchdate_time, vanue, address, city, latitude, longitude, match_type;
    public String typeofpost = "game";
    private CheckAlertDialog alertDialog;
    ArrayList<SelectListModal> placelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan_);

        alertDialog = new CheckAlertDialog();
        myprefs = context.getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
        PlayerID = myprefs.getString(MyPreferenceManager.LOGIN_ID, null);
        //set typeface...for external font families...
        setTypeface();
        // set toolbar..with text and navigation back button..
        setToolBar();
        // set the Widget ids to the Views......
        setWidgetIDswithTypeface();
        // getting the intent as organizermode....
        OrganizerMode = getIntent().getStringExtra("ORGANIGER_MODE");
        matchdate_time = getIntent().getStringExtra("DATETIME");
        vanue = getIntent().getStringExtra("VANUE");
        address = getIntent().getStringExtra("ADDRESS");
        city = getIntent().getStringExtra("CITY");
        latitude = getIntent().getStringExtra("LATITUDE");
        longitude = getIntent().getStringExtra("LONGITUDE");
        match_type = getIntent().getStringExtra("MATCH_TYPE");

        placelist = new ArrayList<>();

        if (OrganizerMode.equals("false")) {
            layout4.setVisibility(View.GONE);
            layout5.setVisibility(View.GONE);
            layout6.setVisibility(View.GONE);
            layout7.setVisibility(View.GONE);
            layout8.setVisibility(View.GONE);
        }

        // set the Widget Click listner....
        setWidgetClickListner();

    }


    private void setWidgetIDswithTypeface() {
        te_male = (TextView) findViewById(R.id.te_male);
        text_title = (TextView) findViewById(R.id.text_title);
        text_spots = (TextView) findViewById(R.id.text_spots);
        text_spots_number = (TextView) findViewById(R.id.text_spots_number);
        text_team = (TextView) findViewById(R.id.text_team);
        text_team_number = (TextView) findViewById(R.id.text_team_number);
        text_level = (TextView) findViewById(R.id.text_level);
        text_level_number = (TextView) findViewById(R.id.text_level_number);
        text_gender = (TextView) findViewById(R.id.text_gender);
        text_show_gener = (TextView) findViewById(R.id.text_show_gener);
        text_female = (TextView) findViewById(R.id.text_female);
        text_coed = (TextView) findViewById(R.id.text_coed);
        text_gameopen = (TextView) findViewById(R.id.text_gameopen);
        text_show_game = (TextView) findViewById(R.id.text_show_game);
        text_myfriends = (TextView) findViewById(R.id.text_myfriends);
        text_friendsofplayer = (TextView) findViewById(R.id.text_friendsofplayer);
        text_anyone = (TextView) findViewById(R.id.text_anyone);
        text_creategames = (TextView) findViewById(R.id.text_creategames);

        edit_title = (EditText) findViewById(R.id.edit_title);
        edit_description = (EditText) findViewById(R.id.edit_description);


        img_spots_up = (ImageView) findViewById(R.id.img_spots_up);
        img_spots_down = (ImageView) findViewById(R.id.img_spots_down);

        img_team_up = (ImageView) findViewById(R.id.img_team_up);
        img_team_down = (ImageView) findViewById(R.id.img_team_down);

        img_level_up = (ImageView) findViewById(R.id.img_level_up);
        img_level_down = (ImageView) findViewById(R.id.img_level_down);

        img_male = (ImageView) findViewById(R.id.img_male);
        img_female = (ImageView) findViewById(R.id.img_female);
        img_coed = (ImageView) findViewById(R.id.img_coed);

        img_myfriends = (ImageView) findViewById(R.id.img_myfriends);
        img_friendsofplayer = (ImageView) findViewById(R.id.img_friendsofplayer);
        img_anyone = (ImageView) findViewById(R.id.img_anyone);

        img_check = (ImageView) findViewById(R.id.img_check);


        layout4 = (LinearLayout) findViewById(R.id.layout4);
        layout5 = (LinearLayout) findViewById(R.id.layout5);
        layout6 = (LinearLayout) findViewById(R.id.layout6);
        layout7 = (LinearLayout) findViewById(R.id.layout7);
        layout8 = (LinearLayout) findViewById(R.id.layout8);

        te_male.setTypeface(Ubuntu_Medium);
        text_title.setTypeface(Ubuntu_Medium);
        text_spots.setTypeface(Ubuntu_Medium);
        text_spots_number.setTypeface(Ubuntu_Medium);
        text_team.setTypeface(Ubuntu_Medium);
        text_team_number.setTypeface(Ubuntu_Medium);
        text_level.setTypeface(Ubuntu_Medium);
        text_level_number.setTypeface(Ubuntu_Medium);
        text_gender.setTypeface(Ubuntu_Medium);
        text_show_gener.setTypeface(Ubuntu_Medium);
        text_female.setTypeface(Ubuntu_Medium);
        text_coed.setTypeface(Ubuntu_Medium);
        text_gameopen.setTypeface(Ubuntu_Medium);
        text_show_game.setTypeface(Ubuntu_Medium);
        text_myfriends.setTypeface(Ubuntu_Medium);
        text_friendsofplayer.setTypeface(Ubuntu_Medium);
        text_anyone.setTypeface(Ubuntu_Medium);
        text_creategames.setTypeface(Ubuntu_Medium);

        edit_title.setTypeface(Ubuntu_Medium);
        edit_description.setTypeface(Ubuntu_Medium);


    }


    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar text....
        text_top = (TextView) toolbar.findViewById(R.id.text_top);
        img_toolbar_back = (ImageView) toolbar.findViewById(R.id.img_toolbar_back);
        text_top.setTypeface(Ubuntu_Medium);
        img_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

    }

    private void setTypeface() {
        Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }

    private void setWidgetClickListner() {

        img_spots_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spotsTest += 1;
                if (spotsTest > 50) {
                    spotsTest = 1;
                }
                String str = Integer.toString(spotsTest);
                text_spots_number.setText(str);
            }
        });
        img_spots_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spotsTest -= 1;
                if (spotsTest < 1) {
                    spotsTest = 50;
                }
                String str = Integer.toString(spotsTest);
                text_spots_number.setText(str);

            }
        });
        img_team_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamSize = TeamSize + 1;
                if (TeamSize > 11) {
                    TeamSize = 1;
                }
                text_team_number.setText(TeamSize + "v" + TeamSize);
            }
        });
        img_team_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeamSize = TeamSize - 1;
                if (TeamSize < 1) {
                    TeamSize = 11;
                }
                text_team_number.setText(TeamSize + "v" + TeamSize);
            }
        });
        img_level_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numTest += 1;
                if (numTest > 5) {
                    numTest = 1;
                }
                String str = Integer.toString(numTest);
                text_level_number.setText(str);

            }
        });
        img_level_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numTest -= 1;
                if (numTest < 1) {
                    numTest = 5;
                }
                String str = Integer.toString(numTest);
                text_level_number.setText(str);
            }
        });
        img_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_male.setImageResource(R.drawable.white_male);
                img_female.setImageResource(R.drawable.feemail);
                img_coed.setImageResource(R.drawable.co_ed);
                text_show_gener.setText("Male");
                Gender = "Male";
                check_Gender = true;


            }
        });
        img_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_male.setImageResource(R.drawable.male);
                img_female.setImageResource(R.drawable.white_feemail);
                img_coed.setImageResource(R.drawable.co_ed);
                text_show_gener.setText("Female");
                Gender = "Female";
                check_Gender = true;

            }
        });
        img_coed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_male.setImageResource(R.drawable.male);
                img_female.setImageResource(R.drawable.feemail);
                img_coed.setImageResource(R.drawable.white_co_ed);
                text_show_gener.setText("Co Ed");
                Gender = "Co Ed";
                check_Gender = true;

            }
        });
        img_myfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_myfriends.setImageResource(R.drawable.white_myfriends);
                img_friendsofplayer.setImageResource(R.drawable.friends_of_players);
                img_anyone.setImageResource(R.drawable.anyone);
                text_show_game.setText("My Friends");
                PrivacyType = "My Friends";
                check_OpenGame = true;

            }
        });
        img_friendsofplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_myfriends.setImageResource(R.drawable.my_riends);
                img_friendsofplayer.setImageResource(R.drawable.white_friendsofplayer);
                img_anyone.setImageResource(R.drawable.anyone);
                text_show_game.setText("Friends of Players");
                PrivacyType = "Friends of Players";
                check_OpenGame = true;

            }
        });
        img_anyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_myfriends.setImageResource(R.drawable.my_riends);
                img_friendsofplayer.setImageResource(R.drawable.friends_of_players);
                img_anyone.setImageResource(R.drawable.white_anyone);
                text_show_game.setText("Anyone");
                PrivacyType = "Anyone";
                check_OpenGame = true;
            }
        });

        // create the game..................
        text_creategames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OrganizerMode.equals("True")){
                    if (edit_title.getText().toString().equals("")) {
                        alertDialog.showcheckAlert3(NewPlan_Activity.this, "Futmate!", "Please Enter the title.", true);
                    } else if (check_Gender == false) {
                        alertDialog.showcheckAlert3(NewPlan_Activity.this, "Futmate!", "Please select the  gender.", true);
                    } else if (check_OpenGame == false) {
                        alertDialog.showcheckAlert3(NewPlan_Activity.this, "Futmate!", "Please select one from My Friends,Friends of Players,Anyone.", true);
                    } else {


                        Title = edit_title.getText().toString();
                        Description = edit_description.getText().toString();
                        NoOfPlayerSpot = text_spots_number.getText().toString();
                        TeamSize_Num = "" + TeamSize;
                        GameLevel = text_level_number.getText().toString();

                        Intent intent = new Intent(context, CreateGameActivity.class);
                        intent.putExtra("TYPEOFPOST",typeofpost);
                        intent.putExtra("ORGANIZER_MODE", OrganizerMode);
                        intent.putExtra("DATE_TIME", matchdate_time);
                        intent.putExtra("VANUE", vanue);
                        intent.putExtra("ADDRESS",address);
                        intent.putExtra("CITY",city);
                        intent.putExtra("MATCH_TYPE", match_type);
                        intent.putExtra("TITLE", Title);
                        intent.putExtra("DESCRIPTION", Description);
                        intent.putExtra("TEAM_SIZE", TeamSize_Num);
                        intent.putExtra("NUMBERS_SPOT_COMPLETE", NoOfPlayerSpot);
                        intent.putExtra("GAME_LEVEL", GameLevel);
                        intent.putExtra("GENDER", Gender);
                        intent.putExtra("PRIVACY_TYPE", PrivacyType);
                        intent.putExtra("MATCH_PLAN_CREATED_BY", PlayerID);
                        intent.putExtra("LATITUDE", latitude);
                        intent.putExtra("LONGITUDES", longitude);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    if (edit_title.getText().toString().equals("")) {
                        alertDialog.showcheckAlert3(NewPlan_Activity.this, "Futmate!", "Please Enter the title.", true);
                    } else if (edit_description.getText().toString().equals("")) {
                        alertDialog.showcheckAlert3(NewPlan_Activity.this, "Futmate!", "Please Enter the description.", true);
                    }else{
                        Title = edit_title.getText().toString();
                        Description = edit_description.getText().toString();
                        NoOfPlayerSpot = "-1";
                        TeamSize_Num = "-1";
                        GameLevel = "-1";
                        PrivacyType = "-1";
                        Gender = "-1";

                        CreateGameWithOFF object = new CreateGameWithOFF();
                        object.execute();
                    }
                }

            }
        });


    }

    // create game webservice executed here....if the Organizer Mode is False......
    public class CreateGameWithOFF extends AsyncTask<String, String, String> {
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
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("DateTimeOfMatch", matchdate_time);
                json.put("Venue", vanue);
                json.put("Address",address);
                json.put("City",city);
                json.put("MatchType", match_type);
                json.put("Title", Title);
                json.put("Description", Description);
                json.put("TeamSize", TeamSize_Num);
                json.put("NoOfPlayerToCompleteTeam", NoOfPlayerSpot);
                json.put("GameLevel", GameLevel);
                json.put("Gender", Gender);
                json.put("PrivacyType", PrivacyType);
                json.put("MatchPlanCreatedBy", PlayerID);
                json.put("Latitude", latitude);
                json.put("Longitude", longitude);
                json.put("TypeOfPost",typeofpost);
                json.put("OrganizarMode",OrganizerMode);
                json.put("JoinStatus","NotJoin");

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
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//                    dialog.setTitle("Futmate!");
//                    dialog.setMessage("Game Create Successful.");
//                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(context, Home_Activity.class);
//                            context.startActivity(intent);
//                            finish();
//                        }
//
//                    });
//                    AlertDialog alert = dialog.create();
//                    alert.setCanceledOnTouchOutside(false);
//                    alert.setCancelable(false);
//                    alert.show();
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
