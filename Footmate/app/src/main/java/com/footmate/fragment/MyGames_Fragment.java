package com.footmate.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.footmate.adapter.MyGamesUpcomingAdpater;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.model.MyGamesUpcomingModal;
import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.Constants;


public class MyGames_Fragment extends Fragment{

    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    public ImageView img_plus, img_upcoming, img_past;
    ListView list_ofupcoming;
    private FloatingActionButton btn_floataction;

    SharedPreferences myprefs;
    SharedPreferences.Editor editor;
    String PlayerID = "";
    CheckAlertDialog checkalert;
    private ArrayList<MyGamesUpcomingModal> modal_List, upcoming_list, past_list;
    private MyGamesUpcomingAdpater mAdp;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    Date strDate = null;
    public MyGames_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.footmate.fragment
        View rootView =inflater.inflate(R.layout.fragment_mygames_, container, false); // alert Dialog

        checkalert = new CheckAlertDialog();
        modal_List = new ArrayList<MyGamesUpcomingModal>();
        upcoming_list = new ArrayList<>();
        past_list = new ArrayList<>();

        // get the playerid from shared prefraces...
        myprefs = getActivity().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, getActivity().MODE_PRIVATE);
        editor = myprefs.edit();
        PlayerID = myprefs.getString(MyPreferenceManager.LOGIN_ID, null);

        // set widg3ets ids....
        setWidgets(rootView);

        setTypeFace();

        // set click list listner of widgets....
        setClickLIstner();

        // hit the NearByPlaces Api.........
        GamesNearByApi obj1 = new GamesNearByApi();
        obj1.execute();

        return rootView;
    }

    public void setWidgets(View rootView){
        img_upcoming = (ImageView)rootView.findViewById(R.id.img_upcoming);
        img_past = (ImageView)rootView.findViewById(R.id.img_past);
        img_plus = (ImageView)rootView.findViewById(R.id.img_plus);
        list_ofupcoming  = (ListView)rootView.findViewById(R.id.list_ofupcoming);
        btn_floataction = (FloatingActionButton) rootView.findViewById(R.id.btn_floataction);
    }

    private void setClickLIstner() {
        img_upcoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(MyPreferenceManager.MYGAME_TYPE, "UPCOMING").commit();
                img_upcoming.setImageResource(R.drawable.green_upcoming);
                img_past.setImageResource(R.drawable.white_past);
                mAdp = new MyGamesUpcomingAdpater(getActivity(), upcoming_list);
                list_ofupcoming.setAdapter(mAdp);

            }
        });
        img_past.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString(MyPreferenceManager.MYGAME_TYPE, "PAST").commit();
                img_upcoming.setImageResource(R.drawable.white_upcoming);
                img_past.setImageResource(R.drawable.green_past);
                mAdp = new MyGamesUpcomingAdpater(getActivity(), past_list);
                list_ofupcoming.setAdapter(mAdp);

            }
        });
        img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Start_New_PlanActivity.class);
                startActivity(i);
            }
        });
        btn_floataction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindow();
            }
        });

    }

    private void setTypeFace() {
        Ubuntu_Medium = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");
    }

    // apply NearByApi webservice/.......
    public class GamesNearByApi extends AsyncTask<Void, String, String> {
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        String url = Constants.MyGames;
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
                Log.e("MY GAMES Screen..", "json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("MyGames Fragment", "data" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                pDialog.dismiss();
                return;
            }
            try {
                String response = new JSONObject(result).getString("Responce");
                Log.e("MyGamesFragment", "MyGamesFragment......................." + response);
                if(response.equals("1")){
                String listofmatches = new JSONObject(result).getString("listOfMatchesCreatedByPlayer");
                    JSONArray jsonArray = new JSONArray(listofmatches);
                    modal_List.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        MyGamesUpcomingModal modal = new MyGamesUpcomingModal();
                        modal.setCreatedDate(object.getString("CreatedDate"));
                        modal.setDateTimeOfMatch(object.getString("DateTimeOfMatch"));
                        modal.setDescription(object.getString("Description"));
                        modal.setGameLevel(object.getString("GameLevel"));
                        modal.setGender(object.getString("Gender"));
                        modal.setJoinStatus(object.getString("JoinStatus"));
                        modal.setLatitude(object.getString("Latitude"));
                        modal.setLongitude(object.getString("Longitude"));
                        modal.setMatchId(object.getString("MatchId"));
                        modal.setMatchType(object.getString("MatchType"));
                        modal.setNoOfPlayerToCompleteTeam(object.getString("NoOfPlayerToCompleteTeam"));
                        modal.setOrgniserMode(object.getString("OrgniserMode"));
                        modal.setPrivacyType(object.getString("PrivacyType"));
                        modal.setTeamSize(object.getString("TeamSize"));
                        modal.setTitle(object.getString("Title"));
                        modal.setVenue(object.getString("Venue"));
                        modal_List.add(modal);
                    }
                    past_list.clear();
                    upcoming_list.clear();
                    for(int j = 0;j < modal_List.size();j++) {
                        if (!modal_List.get(j).getDateTimeOfMatch().equals("")) {
                            String[] str_array = modal_List.get(j).getDateTimeOfMatch().split(" ");
                            String date = str_array[0];
                            try {
                                strDate = sdf.parse(date);
                                if (System.currentTimeMillis() > strDate.getTime()) {
                                    Log.e("PAST ", "PAST DATE" + strDate);
                                    past_list.add(modal_List.get(j));
                                } else if (System.currentTimeMillis() < strDate.getTime() || System.currentTimeMillis() == strDate.getTime()) {
                                    Log.e("UPCOMING ", "UPCOMING DATE" + strDate);
                                    upcoming_list.add(modal_List.get(j));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        editor.putString(MyPreferenceManager.MYGAME_TYPE, "UPCOMING").commit();
                        mAdp = new MyGamesUpcomingAdpater(getActivity(), upcoming_list);
                        list_ofupcoming.setAdapter(mAdp);
                    }
                    pDialog.dismiss();

                } else {
                    pDialog.dismiss();
                    Toast.makeText(getActivity(), "No Match Plan created near your location", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onResume() {
        super.onResume();
        new GamesNearByApi().execute();
    }
}
