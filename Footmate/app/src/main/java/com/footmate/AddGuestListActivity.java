package com.footmate;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;




public class AddGuestListActivity extends ActionBarActivity {
    LinearLayout layout_creategame;
    ImageView img_myfriends, img_friendsofplayer, img_anyone, img_male, img_female, img_coed, img_spots_up, img_spots_down, img_team_up,
            img_team_down, img_level_up, img_level_down;
    TextView text_show_game, text_show_gener, text_spots_number, text_team_number, text_level_number, text_spots, text_team, text_level,
            text_title, text_gender, text_gameopen, te_male, text_female, text_coed, text_myfriends, text_friendsofplayer, text_anyone;
    EditText edit_description, edit_title;
    private Toolbar toolbar;
    TextView cancel, save;
    int spotsTest = 1, TeamSize = 1, numTest = 1;
    private Typeface Ubuntu_Medium, Ubuntu_Bold, Ubuntu_Regular;
    String TAG = "AddGuestListActivity";
    String PlayerId, latitude, longitude, typeofpost, address, joinstatus, datetimeofmatch, matchid;
    SharedPreferences prefs;
    private CheckAlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guest_list);

        Ubuntu_Medium = Typeface.createFromAsset(getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(getAssets(), "Ubuntu-R.ttf");
        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, MODE_PRIVATE);
        PlayerId = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
        alertDialog = new CheckAlertDialog();

        latitude = getIntent().getStringExtra("LATITUDE");
        longitude = getIntent().getStringExtra("LONGITUDE");
        typeofpost = getIntent().getStringExtra("TYPEOFPOST");
        address = getIntent().getStringExtra("ADDRESS");
        joinstatus = getIntent().getStringExtra("JOINSTATUS");
        datetimeofmatch = getIntent().getStringExtra("DATETIMEOFMATCH");
        matchid = getIntent().getStringExtra("MATCHID");

        setToolBar();

        setWidgets();

        setOnClickListeners();

        layout_creategame.setVisibility(View.GONE);
    }

    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar text....
        cancel = (TextView) toolbar.findViewById(R.id.cancel);
        save = (TextView) toolbar.findViewById(R.id.save);
        cancel.setTypeface(Ubuntu_Medium);
        save.setTypeface(Ubuntu_Medium);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_title.getText().toString().equals("")) {
                    alertDialog.showcheckAlert3(AddGuestListActivity.this, "Futmate!", "Please Enter the title.", true);
                } else if (text_show_gener.getText().toString().equals("")) {
                    alertDialog.showcheckAlert3(AddGuestListActivity.this, "Futmate!", "Please select the  gender.", true);
                } else if (text_show_game.getText().toString().equals("")) {
                    alertDialog.showcheckAlert3(AddGuestListActivity.this, "Futmate!", "Please select one from My Friends,Friends of Players,Anyone.", true);
                } else {
                    new UpdateMatchAsyn().execute();
                }
            }
        });

    }

    public void setWidgets(){
        layout_creategame = (LinearLayout)findViewById(R.id.layout_creategame);
        img_myfriends = (ImageView)findViewById(R.id.img_myfriends);
        img_friendsofplayer = (ImageView)findViewById(R.id.img_friendsofplayer);
        img_anyone = (ImageView)findViewById(R.id.img_anyone);
        text_show_game = (TextView)findViewById(R.id.text_show_game);
        img_male = (ImageView)findViewById(R.id.img_male);
        img_female = (ImageView)findViewById(R.id.img_female);
        img_coed = (ImageView)findViewById(R.id.img_coed);
        img_spots_up = (ImageView)findViewById(R.id.img_spots_up);
        img_spots_down = (ImageView)findViewById(R.id.img_spots_down);
        img_team_up = (ImageView)findViewById(R.id.img_team_up);
        img_team_down = (ImageView)findViewById(R.id.img_team_down);
        img_level_up = (ImageView)findViewById(R.id.img_level_up);
        img_level_down = (ImageView)findViewById(R.id.img_level_down);
        text_show_gener = (TextView)findViewById(R.id.text_show_gener);
        text_spots_number = (TextView)findViewById(R.id.text_spots_number);
        text_team_number = (TextView)findViewById(R.id.text_team_number);
        text_level_number = (TextView)findViewById(R.id.text_level_number);
        edit_description = (EditText)findViewById(R.id.edit_description);
        edit_title = (EditText)findViewById(R.id.edit_title);
        text_spots = (TextView)findViewById(R.id.text_spots);
        text_team = (TextView)findViewById(R.id.text_team);
        text_level = (TextView)findViewById(R.id.text_level);
        text_title = (TextView)findViewById(R.id.text_title);
        text_gender = (TextView)findViewById(R.id.text_gender);
        text_gameopen = (TextView)findViewById(R.id.text_gameopen);
        te_male = (TextView)findViewById(R.id.te_male);
        text_female = (TextView)findViewById(R.id.text_female);
        text_coed = (TextView)findViewById(R.id.text_coed);
        text_myfriends = (TextView)findViewById(R.id.text_myfriends);
        text_friendsofplayer = (TextView)findViewById(R.id.text_friendsofplayer);
        text_anyone = (TextView)findViewById(R.id.text_anyone);

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
        edit_title.setTypeface(Ubuntu_Medium);
        edit_description.setTypeface(Ubuntu_Medium);
    }

    public void setOnClickListeners(){
        img_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_male.setImageResource(R.drawable.white_male);
                img_female.setImageResource(R.drawable.feemail);
                img_coed.setImageResource(R.drawable.co_ed);
                text_show_gener.setText("Male");
            }
        });
        img_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_male.setImageResource(R.drawable.male);
                img_female.setImageResource(R.drawable.white_feemail);
                img_coed.setImageResource(R.drawable.co_ed);
                text_show_gener.setText("Female");
            }
        });
        img_coed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_male.setImageResource(R.drawable.male);
                img_female.setImageResource(R.drawable.feemail);
                img_coed.setImageResource(R.drawable.white_co_ed);
                text_show_gener.setText("Co Ed");
            }
        });

        img_myfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_myfriends.setImageResource(R.drawable.white_myfriends);
                img_friendsofplayer.setImageResource(R.drawable.friends_of_players);
                img_anyone.setImageResource(R.drawable.anyone);
                text_show_game.setText("My Friends");
            }
        });
        img_friendsofplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_myfriends.setImageResource(R.drawable.my_riends);
                img_friendsofplayer.setImageResource(R.drawable.white_friendsofplayer);
                img_anyone.setImageResource(R.drawable.anyone);
                text_show_game.setText("Friends of Players");
            }
        });
        img_anyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img_myfriends.setImageResource(R.drawable.my_riends);
                img_friendsofplayer.setImageResource(R.drawable.friends_of_players);
                img_anyone.setImageResource(R.drawable.white_anyone);
                text_show_game.setText("Anyone");
            }
        });

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
    }

    // create game webservice executed here....if the Organizer Mode is False......
    public class UpdateMatchAsyn extends AsyncTask<String, String, String> {
        final ProgressDialog pDialog = new ProgressDialog(AddGuestListActivity.this);
        String url = Constants.UpdateMatch;
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
                json.put("Title", edit_title.getText().toString());
                json.put("Description", edit_description.getText().toString());
                json.put("TeamSize", TeamSize);
                json.put("NoOfPlayerToCompleteTeam", text_spots_number.getText().toString());
                json.put("GameLevel", text_level_number.getText().toString());
                json.put("Gender", text_show_gener.getText().toString());
                json.put("PrivacyType", text_show_game.getText().toString());
                json.put("MatchPlanCreatedBy", PlayerId);
                json.put("Latitude", latitude);
                json.put("Longitude", longitude);
                json.put("OrganizarMode","True");
                json.put("TypeOfPost",typeofpost);
                Log.e("ADDRESS ADDGUESTLIST", address);
                if (address.equals("null")){

                }else {
                    json.put("Address", address);
                }
                json.put("JoinStatus",joinstatus);
                json.put("DateTimeOfMatch", datetimeofmatch);
                json.put("MatchId", matchid);

                Log.e("JSON", String.valueOf(json));

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e(TAG, "UpDAte MAtch Response Code = " + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e(TAG, "data of UpDAte MAtch" + data);
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
