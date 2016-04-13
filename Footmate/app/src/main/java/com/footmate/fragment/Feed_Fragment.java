package com.footmate.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.footmate.FindYourFriendsActivity;
import com.footmate.R;
import com.footmate.Start_New_PlanActivity;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import com.footmate.adapter.FeedAdapter;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.model.FeedListModal;
import com.footmate.utils.ConnectivityNetwork;
import com.footmate.utils.Constants;


public class Feed_Fragment extends Fragment{
    private static ListView myListView;
    private FloatingActionButton btn_floataction;
    private FeedAdapter mAdp;
    private ArrayList<FeedListModal> list;
    LinearLayout searchforfutmatelayout;
    ImageView searchforfutmates;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    ConnectivityNetwork connectivityNetwork;
    boolean isInternet;
    private TextView text_internetconnection;

    String username,vanue,description,matchtype,datetime,spots,teamsize,gender,profilepic;

    public Feed_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.footmate.fragment
        View rootView = inflater.inflate(R.layout.fragment_feed_, container, false);

        // internet connection error message...
        connectivityNetwork = new ConnectivityNetwork();
        isInternet = connectivityNetwork.isNetworkAvailable(getActivity());
        text_internetconnection = (TextView)rootView.findViewById(R.id.text_internetconnection);
        if (!(isInternet)){
            text_internetconnection.setVisibility(View.VISIBLE);
        }else{
            text_internetconnection.setVisibility(View.GONE);
        }


        list = new ArrayList<FeedListModal>();
        myListView = (ListView) rootView.findViewById(R.id.feed_listview);
        searchforfutmatelayout = (LinearLayout) rootView.findViewById(R.id.searchforfutmatelayout);
        searchforfutmates = (ImageView) rootView.findViewById(R.id.searchforfutmates);
        btn_floataction = (FloatingActionButton) rootView.findViewById(R.id.btn_floataction);
        prefs = getActivity().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, getActivity().MODE_PRIVATE);
        editor = prefs.edit();

        // set click event on btn_floataction...
        setClickFAB();
        // set listview click listner.....
        setClickListeners();
        Feed_Asyn object = new Feed_Asyn();
        object.execute();
        return rootView;
    }


    private void setClickListeners() {
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

        searchforfutmates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FindYourFriendsActivity.class);
                startActivity(i);
            }
        });
    }
    private void setClickFAB() {
        btn_floataction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });

    }

    // to create class to post the web service....
    public class Feed_Asyn extends AsyncTask<String, Void, String> {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        String url = Constants.ListOfAllMatchsAcordingToPlayerId;
        String PlayerID = prefs.getString(MyPreferenceManager.LOGIN_ID, null);

        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("PlayerId", PlayerID);
                json.put("PageNo","1");
                json.put("PageSize", "10");

                Log.e("Feed Fragment", "json......." + json.toString());
                
                // HttpPost only accepsts arguments as string, so we turn the
                // json into a string.
                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e("Feed Fragment", " Response Code = " + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("Feed Fragment", "data" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if (result == null) {
                return;
            }
            try {
                String Response = new JSONObject(result).getString("Responce");
                String ListOfMatchtes = new JSONObject(result).getString("listOfMatchs");
                Log.e("Feed Fragment", "Response...................." + Response);
                Log.e("Feed Fragment", "ListOfMatchtes...................." + ListOfMatchtes);

                if (Response.equals("1")) {
                    JSONArray array = new JSONArray(ListOfMatchtes);
                    list.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        FeedListModal modal = new FeedListModal();
                        Log.e("LikeStatus", object.getString("LikeStatus") + " MatchId " + object.getString("MatchId"));
                        modal.setVenue(object.getString("Venue"));
                        modal.setCity(object.getString("City"));
                        modal.setUserName(object.getString("UserName"));
                        modal.setTypeOfPost(object.getString("TypeOfPost"));
                        modal.setTotalLike(object.getString("TotalLike"));
                        modal.setTotalComment(object.getString("TotalComment"));
                        modal.setTitle(object.getString("Title"));
                        modal.setTime(object.getString("Time"));
                        modal.setTeamSize(object.getString("TeamSize"));
                        modal.setPrivacyType(object.getString("PrivacyType"));
                        modal.setOrganizarMode(object.getString("OrganizarMode"));
                        modal.setOpponent(object.getString("Opponent"));
                        modal.setNutmegs(object.getString("Nutmegs"));
                        modal.setNoOfPlayerToCompleteTeam(object.getString("NoOfPlayerToCompleteTeam"));
                        modal.setMyTeam(object.getString("MyTeam"));
                        modal.setMaxSpeed(object.getString("MaxSpeed"));
                        modal.setMatchType(object.getString("MatchType"));
                        modal.setMatchId(object.getString("MatchId"));
                        modal.setLongitude(object.getString("Longitude"));
                        modal.setLikeStatus(object.getString("LikeStatus"));
                        modal.setLatitude(object.getString("Latitude"));
                        modal.setJoinStatus(object.getString("JoinStatus"));
                        modal.setImageUrl(object.getString("ImageUrl"));
                        modal.setImageName(object.getString("ImageName"));
                        modal.setGoal(object.getString("Goal"));
                        modal.setGender(object.getString("Gender"));
                        modal.setGameLevel(object.getString("GameLevel"));
                        modal.setFieldName(object.getString("FieldName"));
                        modal.setDistance(object.getString("Distance"));
                        modal.setDeviceId(object.getString("DeviceId"));
                        modal.setDescription(object.getString("Description"));
                        modal.setDateTimeOfMatch(object.getString("DateTimeOfMatch"));
                        modal.setCreatedDate(object.getString("CreatedDate"));
                        modal.setCreatedBy(object.getString("CreatedBy"));
                        modal.setAvgSpeed(object.getString("AvgSpeed"));
                        modal.setAssists(object.getString("Assists"));
                        modal.setAddress(object.getString("Address"));
                        list.add(modal);
                    }
                    Collections.reverse(list);
                    mAdp = new FeedAdapter(getActivity(), list);
                    myListView.setAdapter(mAdp);
                } else {
                    myListView.setVisibility(View.GONE);
                    searchforfutmatelayout.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    //.......................create and display the window popup................on Fragment........................
    private PopupWindow pwindo;
    private void initiatePopupWindow() {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_float__click,
                    (ViewGroup) getActivity().findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pwindo.setAnimationStyle(R.anim.slide_in_top);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            LinearLayout layout_trackgame = (LinearLayout) layout.findViewById(R.id.layout_trackgame);
            LinearLayout layout_newplan = (LinearLayout) layout.findViewById(R.id.layout_newplan);
            LinearLayout layout_findgame = (LinearLayout) layout.findViewById(R.id.layout_findgame);
            ImageView top_button = (ImageView) layout.findViewById(R.id.top_button);

            layout_trackgame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.setAnimationStyle(R.anim.slide_in_top);
                    pwindo.dismiss();
                    Home_Fragment.setPostion(4);
                }
            });

            layout_newplan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.setAnimationStyle(R.anim.slide_in_top);
                    pwindo.dismiss();
                    Intent intent = new Intent(getActivity(), Start_New_PlanActivity.class);
                    startActivity(intent);
                }
            });

            layout_findgame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.setAnimationStyle(R.anim.slide_in_top);
                    pwindo.dismiss();
                    Home_Fragment.setPostion(3);
                }
            });

            top_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.setAnimationStyle(R.anim.slide_in_top);
                    pwindo.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //.........................................................................................................
    // call to onResume method called..
    @Override
    public void onResume() {
        super.onResume();
        if(prefs.getString(MyPreferenceManager.COMMENT_COUNT, null) != null) {
            list.get(prefs.getInt(MyPreferenceManager.POSITION, 0)).setTotalComment(prefs.getString(MyPreferenceManager.COMMENT_COUNT, null));
            mAdp.notifyDataSetChanged();
            editor.putString(MyPreferenceManager.COMMENT_COUNT, null).commit();
        }else {
                    Feed_Asyn object = new Feed_Asyn();
                    object.execute();
        }
    }
    //.........................................................................................................

}

