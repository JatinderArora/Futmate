package com.footmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Constants;
import com.footmate.views.HorizontalView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.footmate.adapter.FollowerList_Adapter;
import com.footmate.adapter.FollowingListAdapter;
import com.footmate.adapter.GamesTypeAdpater;
import de.hdodenhof.circleimageview.CircleImageView;
import com.footmate.model.FollowerListModal;
import com.footmate.model.FollowingListModal;
import com.footmate.model.GamesTypeModal;


public class Profile_Activity extends AppCompatActivity {
    private Context context = Profile_Activity.this;
    private String TAG = "NewPlan_Activity";

    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    private Toolbar toolbar;
    private TextView text_top;
    private ImageView img_toolbar_back;
    private TextView text_about, text_followers, text_followings, text_games,text_followersNum,text_followingsNum,text_gamesNum;
    private CircleImageView profile_pic;
    private LinearLayout layout_threebutton;
    LinearLayout followerslayout, followingslayout, gameslayout;
    HorizontalView commonListView;


    String imageUrl = "";
    String username = "";
    String about = "";

    SharedPreferences myprefs;

    private ArrayList<FollowerListModal> followersList;
    private ArrayList<FollowingListModal> followingList;
    private ArrayList<GamesTypeModal> gamesList;
    private FollowerList_Adapter mAdp_Followers;
    private FollowingListAdapter mAdp_Followings;
    private GamesTypeAdpater mAdp_Games;

    ImageLoader imageLoader = null;
    private DisplayImageOptions options;
    public String gamesType[] = {"Matches","Freeplay","Practice","Average Goals","Average Distance","Average Speed"};
    public String gamesNumber[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        myprefs = context.getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
        imageUrl = myprefs.getString(MyPreferenceManager.IMAGE_URL, null);
        username = myprefs.getString(MyPreferenceManager.USERNAME, null);
        about = myprefs.getString(MyPreferenceManager.ABOUT, null);
        // initialize the arrayList...
        followersList = new ArrayList<FollowerListModal>();
        followingList = new ArrayList<FollowingListModal>();
        gamesList = new ArrayList<GamesTypeModal>();


        //set typeface...for external font families...
        setTypeface();
        // set toolbar..with text and navigation back button..
        setToolBar();
        // set the Widget ids to the Views......
        setWidgetIDswithTypeface();
        // set the click listner...
        setCLickListner();
        // set the profile pic that is getting from prefs....and About info...
        setProfilePicWithAbout();

        // Followers List
        FollowersAsyn objFollowers = new FollowersAsyn();
        objFollowers.execute();

        // Followings List:-
        FollowingAsyn objFollowings = new FollowingAsyn();
        objFollowings.execute();

        // Games List
        GamesAsyn objgames = new GamesAsyn();
        objgames.execute();

    }

    private void setProfilePicWithAbout() {
        profile_pic = (CircleImageView)findViewById(R.id.profile_pic);
        text_about.setText("About");

        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        if(imageUrl.equals("null")){
            profile_pic.setImageResource(R.drawable.placeholder);
        }else {
            imageLoader.displayImage(imageUrl, profile_pic, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    profile_pic.setImageResource(R.drawable.user_img);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

    }

    private void setCLickListner() {
        followerslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_threebutton.setBackgroundResource(R.drawable.followers);
                //followers listview setAdapter
                mAdp_Followers = new FollowerList_Adapter(context,followersList);
                commonListView.setAdapter(mAdp_Followers);

                FollowersAsyn obj1 = new FollowersAsyn();
                obj1.execute();
            }
        });
        followingslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_threebutton.setBackgroundResource(R.drawable.following);

                //followings listview Adapter
                mAdp_Followings = new FollowingListAdapter(context,followingList);
                commonListView.setAdapter(mAdp_Followings);

                FollowingAsyn obj3 = new FollowingAsyn();
                obj3.execute();
            }
        });

        gameslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_threebutton.setBackgroundResource(R.drawable.games);

                // games listview Adapter
                mAdp_Games = new GamesTypeAdpater(context,gamesList);
                commonListView.setAdapter(mAdp_Games);

                // Games List
                GamesAsyn objgames = new GamesAsyn();
                objgames.execute();

            }
        });


    }

    private void setWidgetIDswithTypeface() {
        layout_threebutton = (LinearLayout)findViewById(R.id.layout_threebutton);
        followerslayout = (LinearLayout)findViewById(R.id.followerslayout);
        followingslayout = (LinearLayout)findViewById(R.id.followingslayout);
        gameslayout = (LinearLayout)findViewById(R.id.gameslayout);
        text_about = (TextView) findViewById(R.id.text_about);
        text_followers = (TextView) findViewById(R.id.text_followers);
        text_followings = (TextView) findViewById(R.id.text_followings);
        text_games = (TextView) findViewById(R.id.text_games);

        text_followersNum = (TextView)findViewById(R.id.text_followersNum);
        text_followingsNum = (TextView)findViewById(R.id.text_followingsNum);
        text_gamesNum = (TextView)findViewById(R.id.text_gamesNum);

        commonListView = (HorizontalView)findViewById(R.id.commonListView);

        text_followersNum.setTypeface(Ubuntu_Medium);
        text_followingsNum.setTypeface(Ubuntu_Medium);
        text_gamesNum.setTypeface(Ubuntu_Medium);

        // set typeface...
        text_about.setTypeface(Ubuntu_Regular);
        text_followers.setTypeface(Ubuntu_Regular);
        text_followings.setTypeface(Ubuntu_Regular);
        text_games.setTypeface(Ubuntu_Regular);

    }


    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // toolbar text....
        text_top = (TextView) toolbar.findViewById(R.id.text_top);
        img_toolbar_back = (ImageView) toolbar.findViewById(R.id.img_toolbar_back);
        text_top.setText(username);
        text_top.setTypeface(Ubuntu_Regular);
        img_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setTypeface() {
        Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }

    /*Followers List....*/
    public class FollowersAsyn extends AsyncTask<Void, String, String> {
        String data;
        ProgressDialog p = new ProgressDialog(context);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.setMessage("Please Wait...");
            p.setCancelable(true);
//            p.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            try {
                HttpPost post = new HttpPost(Constants.FollowerList);
                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("PlayerId", myprefs.getString(MyPreferenceManager.LOGIN_ID, null));

                Log.e("Followers JSON", json.toString());

                StringEntity entity = new StringEntity(json.toString());
                post.setEntity(entity);

                HttpResponse response = defaultHttpClient.execute(post);
                HttpEntity httpEntity = response.getEntity();
                data = EntityUtils.toString(httpEntity);

                Log.e("Followers API DATA", data);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            p.dismiss();
            if (s == null){
                p.dismiss();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                String res = jsonObject.getString("Responce");
                if(res.equals("1"))
                {
                    JSONArray array = jsonObject.getJSONArray("listOfFollower");
                    //followers size..
                    int followerSize = array.length();
                    text_followersNum.setText(followerSize+"");
                    followersList.clear();
                    for (int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        FollowerListModal modal = new FollowerListModal();
                        modal.setAbout(obj.getString("About"));
                        modal.setEmailId(obj.getString("EmailId"));
                        modal.setId(obj.getString("Id"));
                        modal.setImageUrl(obj.getString("ImageUrl"));
                        modal.setLoginType(obj.getString("LoginType"));
                        modal.setUserName(obj.getString("UserName"));
                        followersList.add(modal);
                        Log.e(TAG,"FollowersList"+followersList.get(i).toString());
                    }
                    //followers listview setAdapter
                    mAdp_Followers = new FollowerList_Adapter(context,followersList);
                    commonListView.setAdapter(mAdp_Followers);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*Following List....*/
    public class FollowingAsyn extends AsyncTask<Void, String, String> {
        String data;
        ProgressDialog p = new ProgressDialog(context);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.setMessage("Please Wait...");
            p.setCancelable(true);
//            p.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            try {
                HttpPost post = new HttpPost(Constants.ListOfPlayersAcordingToFollowerPlayerId);
                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("PlayerId", myprefs.getString(MyPreferenceManager.LOGIN_ID, null));

                Log.e("Following JSON", json.toString());

                StringEntity entity = new StringEntity(json.toString());
                post.setEntity(entity);

                HttpResponse response = defaultHttpClient.execute(post);
                HttpEntity httpEntity = response.getEntity();
                data = EntityUtils.toString(httpEntity);

                Log.e("Following API DATA", data);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            p.dismiss();
            if (s == null){
                p.dismiss();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                String res = jsonObject.getString("Responce");
                if(res.equals("1"))
                {
                    JSONArray array = jsonObject.getJSONArray("listOfFollower");

                    //followings size..
                    int followingsSize = array.length();
                    text_followingsNum.setText(followingsSize+"");
                    followingList.clear();
                    for (int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        FollowingListModal modal = new FollowingListModal();
                        modal.setAbout(obj.getString("About"));
                        modal.setEmailId(obj.getString("EmailId"));
                        modal.setId(obj.getString("Id"));
                        modal.setImageUrl(obj.getString("ImageUrl"));
                        modal.setLoginType(obj.getString("LoginType"));
                        modal.setUserName(obj.getString("UserName"));

                        followingList.add(modal);
                        Log.e(TAG,"FollowingList"+followingList.get(i).toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class GamesAsyn extends AsyncTask<Void, String, String>{
        String data;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            try {
                HttpPost post = new HttpPost(Constants.GetPlayerDetail);
                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("PlayerId", myprefs.getString(MyPreferenceManager.LOGIN_ID, null));

                Log.e("Total Games JSON", json.toString());

                StringEntity entity = new StringEntity(json.toString());
                post.setEntity(entity);

                HttpResponse response = defaultHttpClient.execute(post);
                HttpEntity httpEntity = response.getEntity();
                data = EntityUtils.toString(httpEntity);

                Log.e("Total Games API DATA", data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null){
                return;
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                String res = jsonObject.getString("Response");

                    //games size..
                    int gamessSize = Integer.parseInt(jsonObject.getString("NoOfMatchPlay")) + Integer.parseInt(jsonObject.getString("NoOfFreeplay"))
                    + Integer.parseInt(jsonObject.getString("NoOfPractice"));
                    text_gamesNum.setText(gamessSize+"");
                    gamesNumber = new String[]{jsonObject.getString("NoOfMatchPlay"), jsonObject.getString("NoOfFreeplay"), jsonObject.getString("NoOfPractice"),
                            jsonObject.getString("AverageGoals"), jsonObject.getString("AverageDistance"), jsonObject.getString("AverageSpeed")};
                    gamesList.clear();
                    for(int i = 0; i < gamesType.length; i++){
                        GamesTypeModal modal = new GamesTypeModal();
                        modal.setGameNumbers(gamesNumber[i]);
                        modal.setGameType(gamesType[i]);
                        gamesList.add(modal);
                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
