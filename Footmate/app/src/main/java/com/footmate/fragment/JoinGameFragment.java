package com.footmate.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.footmate.AddGuestListActivity;
import com.footmate.FeedDetailsActivity;
import com.footmate.Home_Activity;
import com.footmate.InvitePlayersActivity;
import com.footmate.R;
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

import de.hdodenhof.circleimageview.CircleImageView;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Constants;


public class JoinGameFragment extends Fragment {

    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;

    private LinearLayout top_layout, layoutgender;
    private ImageView img_cross, img_left_right_swipe, img_addprofile, img_run_logo, circle_img_gender, img_applogo, img_threedot;
    private CircleImageView profile_pic;
    private TextView match_date_time, circle_text_spots, below_text_spot, circle_text_teamsize, below_text_gender,
            text_organizer, text_name,text_title,text_description, text_joingame,below_text_teamsize, venue;
    String title,username = "",vanue = "",description = "",matchtype = "",datetime = "",spots = "",gamelevel = "",
            gender = "",profilepic ="", joinstatus = "", organisermode = "", matchid = "", PlayerId = "", latitude = "",
    longitude = "", typeofpost = "", address = "", datetimeofmatch = "";
    ImageLoader imageLoader = null;
    SharedPreferences prefs;
    RelativeLayout layout_field;
    ImageView up_arrow;
    private DisplayImageOptions options;
    private PopupWindow pwindo;

    public JoinGameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.footmate.fragment
        View rootView = inflater.inflate(R.layout.fragment_join_game, container, false);
        prefs = getActivity().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, getActivity().MODE_PRIVATE);
        if (getArguments() != null){
            Bundle bundle = getArguments();
            username =  bundle.getString("USERNAME");
            title = bundle.getString("TITLE");
            vanue =  bundle.getString("VANUE");
            description =  bundle.getString("DESCRIPTION");
            matchtype =  bundle.getString("MATCHTYPE");
            datetime =  bundle.getString("DATETIME");
            spots =  bundle.getString("SPOTS");
            gamelevel =  bundle.getString("GAMELEVEL");
            gender =  bundle.getString("GENDER");
            profilepic =  bundle.getString("PICTURE");
            joinstatus = bundle.getString("JOINSTATUS");
            organisermode = bundle.getString("ORGANISERMODE");
            matchid = bundle.getString("MATCHID");
            latitude = bundle.getString("LATITUDE");
            longitude = bundle.getString("LONGITUDE");
            typeofpost = bundle.getString("TYPEOFPOST");
            address = bundle.getString("ADDRESS");
            datetimeofmatch = bundle.getString("DATETIMEOFMATCH");
        }
        PlayerId = prefs.getString(MyPreferenceManager.LOGIN_ID, null);

        // set the widget ids...
        top_layout = (LinearLayout) rootView.findViewById(R.id.top_layout);
        layoutgender = (LinearLayout) rootView.findViewById(R.id.layoutgender);
        img_cross = (ImageView) rootView.findViewById(R.id.img_cross);
        img_left_right_swipe = (ImageView) rootView.findViewById(R.id.img_left_right_swipe);
        img_addprofile = (ImageView) rootView.findViewById(R.id.img_addprofile);
        img_run_logo = (ImageView) rootView.findViewById(R.id.img_run_logo);
        circle_img_gender = (ImageView) rootView.findViewById(R.id.circle_img_gender);
        img_applogo = (ImageView) rootView.findViewById(R.id.img_applogo);
        img_threedot = (ImageView) rootView.findViewById(R.id.img_threedot);
        profile_pic = (CircleImageView) rootView.findViewById(R.id.profile_pic);
        match_date_time = (TextView) rootView.findViewById(R.id.match_date_time);
        circle_text_spots = (TextView) rootView.findViewById(R.id.circle_text_spots);
        below_text_spot = (TextView) rootView.findViewById(R.id.below_text_spot);
        circle_text_teamsize = (TextView) rootView.findViewById(R.id.circle_text_teamsize);
        below_text_gender = (TextView) rootView.findViewById(R.id.below_text_gender);
        below_text_teamsize = (TextView) rootView.findViewById(R.id.below_text_teamsize);
        text_organizer = (TextView) rootView.findViewById(R.id.text_organizer);
        text_name = (TextView) rootView.findViewById(R.id.text_name);
        text_title = (TextView) rootView.findViewById(R.id.text_title);
        text_description = (TextView) rootView.findViewById(R.id.text_description);
        text_joingame = (TextView) rootView.findViewById(R.id.text_joingame);
        venue = (TextView) rootView.findViewById(R.id.venue);
        layout_field = (RelativeLayout) rootView.findViewById(R.id.layout_field);
        up_arrow = (ImageView) rootView.findViewById(R.id.up_arrow);

        Ubuntu_Medium = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");

        // set the widget ids...
        setTypeface();

        // set the text in widgets.....
        setDataInWidgets();

        // setClick listner.....
        setClickListner();

        return rootView;
    }

    private void setClickListner() {

        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Home_Activity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        text_joingame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text_joingame.getText().toString().equals("JOIN GAME")) {
                    new JoinAsync().execute();
                } else if (text_joingame.getText().toString().equals("ADD GUEST LIST")) {
                    if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))) {
                        Intent i = new Intent(getActivity(), AddGuestListActivity.class);
                        i.putExtra("LATITUDE", latitude);
                        i.putExtra("LONGITUDE", longitude);
                        i.putExtra("TYPEOFPOST", typeofpost);
                        i.putExtra("ADDRESS", address);
                        i.putExtra("JOINSTATUS", joinstatus);
                        i.putExtra("DATETIMEOFMATCH", datetimeofmatch);
                        i.putExtra("MATCHID", matchid);
                        startActivity(i);
                        getActivity().finish();
                    }
                }
            }
        });
        up_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_field.setVisibility(View.GONE);
                initiatePopupWindow();
            }
        });

        img_addprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (organisermode.equals("True")) {
                    if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))) {
                        Intent i = new Intent(getActivity(), InvitePlayersActivity.class);
                        startActivity(i);
                    } else {
                        if (joinstatus.equals("Join")) {
                            Intent i = new Intent(getActivity(), InvitePlayersActivity.class);
                            startActivity(i);
                        }
                    }
                }
            }
        });

        img_threedot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindowThreeDot();
            }
        });
    }

    private void setDataInWidgets() {

        //set the image....
        setProfilePicWithAbout();
        text_name.setText(username);
        text_title.setText(title);
        text_description.setText(description);
        match_date_time.setText(datetime);
        venue.setText(vanue);

        if (matchtype.equals("Match")){
            img_run_logo.setImageResource(R.drawable.feed_ground_icon);
            top_layout.setBackground(getResources().getDrawable(R.drawable.match_bg));
        }else if (matchtype.equals("Freeplay")){
            img_run_logo.setImageResource(R.drawable.post_match_ico);
            top_layout.setBackground(getResources().getDrawable(R.drawable.freeplay_match_bg));
        }else if (matchtype.equals("Practice")){
            img_run_logo.setImageResource(R.drawable.run_user);
            top_layout.setBackground(getResources().getDrawable(R.drawable.match_img3));
        }

        if(organisermode.equals("False")){
            circle_text_spots.setVisibility(View.INVISIBLE);
            below_text_spot.setVisibility(View.INVISIBLE);
            circle_text_teamsize.setVisibility(View.INVISIBLE);
            below_text_teamsize.setVisibility(View.INVISIBLE);
            layoutgender.setVisibility(View.INVISIBLE);
            below_text_gender.setVisibility(View.INVISIBLE);

            text_joingame.setText("ADD GUEST LIST");
        }else if(organisermode.equals("True")){
            circle_text_spots.setVisibility(View.VISIBLE);
            below_text_spot.setVisibility(View.VISIBLE);
            circle_text_teamsize.setVisibility(View.VISIBLE);
            below_text_teamsize.setVisibility(View.VISIBLE);
            layoutgender.setVisibility(View.VISIBLE);
            below_text_gender.setVisibility(View.VISIBLE);

            circle_text_spots.setText(spots);
            circle_text_teamsize.setText(gamelevel);
            if (gender.equalsIgnoreCase("Male")){
                circle_img_gender.setImageResource(R.drawable.male_s);
            } else if (gender.equalsIgnoreCase("Female")){
                circle_img_gender.setImageResource(R.drawable.female_s);
            }else if (gender.equalsIgnoreCase("Co Ed")){
                circle_img_gender.setImageResource(R.drawable.co_ed_s);
            }

            if (joinstatus.equals("Join")){
                text_joingame.setText("I'M IN");
                up_arrow.setVisibility(View.VISIBLE);
            }else if (joinstatus.equals("NotJoin")){
                text_joingame.setText("JOIN GAME");
            }
        }
    }

    private void setProfilePicWithAbout() {

        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

        if(profilepic.equals("null")){
            profile_pic.setImageResource(R.drawable.placeholder);
        }else {
            imageLoader.displayImage(profilepic, profile_pic, options, new ImageLoadingListener() {
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

    private void setTypeface() {
        match_date_time.setTypeface(Ubuntu_Medium);
        circle_text_spots.setTypeface(Ubuntu_Regular);
        below_text_spot.setTypeface(Ubuntu_Medium);
        circle_text_teamsize.setTypeface(Ubuntu_Regular);
        below_text_gender.setTypeface(Ubuntu_Medium);
        below_text_teamsize.setTypeface(Ubuntu_Medium);
        text_organizer.setTypeface(Ubuntu_Medium);
        text_name.setTypeface(Ubuntu_Regular);
        text_title.setTypeface(Ubuntu_Medium);
        text_description.setTypeface(Ubuntu_Regular);
        text_joingame.setTypeface(Ubuntu_Bold);
        venue.setTypeface(Ubuntu_Medium);
    }


    /*.............UnJoin the Match.............*/
    public class UNJoinAsync extends AsyncTask<String, String, String> {
        ProgressDialog pdial = new ProgressDialog(getActivity());
        String url = Constants.UnjoinMatch;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdial.setMessage("Loading...");
            pdial.setCancelable(false);
            pdial.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("MatchId",matchid);
                json.put("PlayerId", PlayerId);

                Log.e("Join Game Fragment", "UNJoin json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("Join Game Fragment", "UNJoin API DATA" + data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                pdial.dismiss();
                return;
            }
            try {
                String response = new JSONObject(result).getString("Response");
                if (response.equals("1")) {
                    Log.e("Join Game Fragment", "UNJoin Plan success");
                    pwindo.dismiss();
                    layout_field.setVisibility(View.VISIBLE);
                    text_joingame.setText("JOIN GAME");
                    up_arrow.setVisibility(View.GONE);
                    joinstatus = "NotJoin";
                    if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))){

                    }else {
                        Intent intent = new Intent(getActivity(), FeedDetailsActivity.class);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("TITLE", title);
                        intent.putExtra("VANUE", vanue);
                        intent.putExtra("DESCRIPTION", description);
                        intent.putExtra("MATCHTYPE", matchtype);
                        intent.putExtra("DATETIME", datetime);
                        intent.putExtra("SPOTS", spots);
                        intent.putExtra("GAMELEVEL", gamelevel);
                        intent.putExtra("GENDER", gender);
                        intent.putExtra("PICTURE", profilepic);
                        intent.putExtra("JOINSTATUS", joinstatus);
                        intent.putExtra("ORGANISERMODE", organisermode);
                        intent.putExtra("MATCHID", matchid);
                        intent.putExtra("LATITUDE", latitude);
                        intent.putExtra("LONGITUDE", longitude);
                        intent.putExtra("TYPEOFPOST", typeofpost);
                        intent.putExtra("ADDRESS", address);
                        intent.putExtra("DATETIMEOFMATCH", datetimeofmatch);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                } else {
                    Log.e("Join Game Fragment", "UNJoin Plan UnSuccess");
                }
                pdial.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*.............Join the Match.............*/
    public class JoinAsync extends AsyncTask<String, String, String> {
        ProgressDialog pdial = new ProgressDialog(getActivity());
        String url = Constants.JoinMatch;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdial.setMessage("Loading...");
            pdial.setCancelable(false);
            pdial.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("MatchId",matchid);
                json.put("PlayerId", PlayerId);

                Log.e("Join Game Fragment", "Join json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("Join Game Fragment", "Join" + data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                pdial.dismiss();
                return;
            }
            try {
                String response = new JSONObject(result).getString("Response");
                if (response.equals("1")) {
                    up_arrow.setVisibility(View.VISIBLE);
                    text_joingame.setText("I'M IN");
                    joinstatus = "Join";
                    if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))){

                    }else {
                        Intent intent = new Intent(getActivity(), FeedDetailsActivity.class);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("TITLE", title);
                        intent.putExtra("VANUE", vanue);
                        intent.putExtra("DESCRIPTION", description);
                        intent.putExtra("MATCHTYPE", matchtype);
                        intent.putExtra("DATETIME", datetime);
                        intent.putExtra("SPOTS", spots);
                        intent.putExtra("GAMELEVEL", gamelevel);
                        intent.putExtra("GENDER", gender);
                        intent.putExtra("PICTURE", profilepic);
                        intent.putExtra("JOINSTATUS", joinstatus);
                        intent.putExtra("ORGANISERMODE", organisermode);
                        intent.putExtra("MATCHID", matchid);
                        intent.putExtra("LATITUDE", latitude);
                        intent.putExtra("LONGITUDE", longitude);
                        intent.putExtra("TYPEOFPOST", typeofpost);
                        intent.putExtra("ADDRESS", address);
                        intent.putExtra("DATETIMEOFMATCH", datetimeofmatch);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }
                    Log.e("My Games Adapter", "Join Plan success");
                } else {
                    Log.e("My Games Adapter", "Join Plan UnSuccess");
                }
                pdial.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //.......................create and display the window popup................on Fragment........................
    private void initiatePopupWindow() {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.join_game_click,
                    (ViewGroup) getActivity().findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pwindo.setAnimationStyle(R.anim.slide_in_top);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ImageView down_arrow = (ImageView) layout.findViewById(R.id.down_arrow);
            TextView iamin = (TextView) layout.findViewById(R.id.iamin);
            TextView wontgotext = (TextView) layout.findViewById(R.id.wontgotext);
            ImageView iamout = (ImageView) layout.findViewById(R.id.iamout);

            iamin.setTypeface(Ubuntu_Bold);
            wontgotext.setTypeface(Ubuntu_Medium);

            iamout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UNJoinAsync().execute();
                }
            });

            down_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                    layout_field.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initiatePopupWindowThreeDot() {
        try {
            // We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.three_dot_click,
                    (ViewGroup) getActivity().findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            pwindo.setAnimationStyle(R.anim.slide_in_top);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ImageView cancel = (ImageView) layout.findViewById(R.id.cancel);
            ImageView invitefutmatesimg = (ImageView) layout.findViewById(R.id.invitefutmatesimg);
            ImageView inviteviaurlimg = (ImageView) layout.findViewById(R.id.inviteviaurlimg);
            ImageView gamesettingsimg = (ImageView) layout.findViewById(R.id.gamesettingsimg);
            TextView invitefutmatestext = (TextView) layout.findViewById(R.id.invitefutmatestext);
            TextView inviteviaurltext = (TextView) layout.findViewById(R.id.inviteviaurltext);
            TextView gamesettingstext = (TextView) layout.findViewById(R.id.gamesettingstext);


            invitefutmatestext.setTypeface(Ubuntu_Medium);
            inviteviaurltext.setTypeface(Ubuntu_Medium);
            gamesettingstext.setTypeface(Ubuntu_Medium);
            if(organisermode.equals("True")) {
                if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))) {
                    invitefutmatesimg.setImageResource(R.drawable.invite_futmates);
                    inviteviaurlimg.setImageResource(R.drawable.invite_via_url);
                    gamesettingsimg.setImageResource(R.drawable.game_setting);

                    invitefutmatestext.setTextColor(getResources().getColor(R.color.white));
                    inviteviaurltext.setTextColor(getResources().getColor(R.color.white));
                    gamesettingstext.setTextColor(getResources().getColor(R.color.white));
                }else {
                    if (joinstatus.equals("Join")){
                        invitefutmatesimg.setImageResource(R.drawable.invite_futmates);
                        inviteviaurlimg.setImageResource(R.drawable.invite_via_url);
                        gamesettingsimg.setImageResource(R.drawable.game_setting_del);

                        invitefutmatestext.setTextColor(getResources().getColor(R.color.white));
                        inviteviaurltext.setTextColor(getResources().getColor(R.color.white));
                        gamesettingstext.setTextColor(getResources().getColor(R.color.gr));
                    }else {
                        invitefutmatesimg.setImageResource(R.drawable.invite_futmates_del);
                        inviteviaurlimg.setImageResource(R.drawable.invite_via_url_del);
                        gamesettingsimg.setImageResource(R.drawable.game_setting_del);

                        invitefutmatestext.setTextColor(getResources().getColor(R.color.gr));
                        inviteviaurltext.setTextColor(getResources().getColor(R.color.gr));
                        gamesettingstext.setTextColor(getResources().getColor(R.color.gr));
                    }
                }
            }else {
                invitefutmatesimg.setImageResource(R.drawable.invite_futmates_del);
                inviteviaurlimg.setImageResource(R.drawable.invite_via_url_del);
                gamesettingsimg.setImageResource(R.drawable.game_setting_del);

                invitefutmatestext.setTextColor(getResources().getColor(R.color.gr));
                inviteviaurltext.setTextColor(getResources().getColor(R.color.gr));
                gamesettingstext.setTextColor(getResources().getColor(R.color.gr));
            }
            invitefutmatesimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (organisermode.equals("True")) {
                        if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))) {
                            Intent i = new Intent(getActivity(), InvitePlayersActivity.class);
                            startActivity(i);
                        } else {
                            if (joinstatus.equals("Join")) {
                                Intent i = new Intent(getActivity(), InvitePlayersActivity.class);
                                startActivity(i);
                            }
                        }
                    }
                }
            });
            inviteviaurlimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (organisermode.equals("True")) {
                        if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, "Futmate Invite the Friends");
                            sendIntent.setType("text/plain");
                            startActivity(sendIntent);
                        }else {
                            if (joinstatus.equals("Join")) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Futmate Invite the Friends");
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                            }
                        }
                    }
                }
            });

            gamesettingsimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if (organisermode.equals("True")) {
                            if (username.equals(prefs.getString(MyPreferenceManager.USERNAME, null))) {
                                Intent i = new Intent(getActivity(), AddGuestListActivity.class);
                                i.putExtra("LATITUDE", latitude);
                                i.putExtra("LONGITUDE", longitude);
                                i.putExtra("TYPEOFPOST", typeofpost);
                                i.putExtra("ADDRESS", address);
                                i.putExtra("JOINSTATUS", joinstatus);
                                i.putExtra("DATETIMEOFMATCH", datetimeofmatch);
                                i.putExtra("MATCHID", matchid);
                                startActivity(i);
                            }
                        }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
