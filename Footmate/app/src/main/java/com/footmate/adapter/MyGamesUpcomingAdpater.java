package com.footmate.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.footmate.FeedDetailsActivity;
import com.footmate.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import com.footmate.model.MyGamesUpcomingModal;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Constants;
import com.footmate.utils.Utilities;

/**
 * Created by patas tech on 2/22/2016.
 */
public class MyGamesUpcomingAdpater  extends BaseAdapter {
    public Context context;
    public ArrayList<MyGamesUpcomingModal> modal_List;
    SharedPreferences prefs;
    String PlayerId, MatchId;

    public MyGamesUpcomingAdpater(Context context, ArrayList<MyGamesUpcomingModal> modal_List) {
        this.context = context;
        this.modal_List = modal_List;
        prefs = context.getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
    }


    @Override
    public int getCount() {
        return modal_List.size();
    }

    @Override
    public Object getItem(int position) {
        return modal_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView txt_matchdate;
        TextView txt_one;
        TextView txt_two;
        TextView txt_three;
        ImageView img_ok_cancel,img_logo_exchange;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Typeface Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
        Typeface Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Typeface Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        PlayerId = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_upcominglist, null);

            holder.txt_matchdate = (TextView) convertView.findViewById(R.id.txt_matchdate);
            holder.img_logo_exchange = (ImageView) convertView.findViewById(R.id.img_logo_exchange);
            holder.txt_one = (TextView) convertView.findViewById(R.id.txt_one);
            holder.txt_two = (TextView) convertView.findViewById(R.id.txt_two);
            holder.txt_three = (TextView) convertView.findViewById(R.id.txt_three);
            holder.img_ok_cancel = (ImageView) convertView.findViewById(R.id.img_ok_cancel);


            holder.txt_one.setTypeface(Ubuntu_Regular);
            holder.txt_two.setTypeface(Ubuntu_Regular);
            holder.txt_three.setTypeface(Ubuntu_Regular);
            holder.txt_matchdate.setTypeface(Ubuntu_Medium);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MyGamesUpcomingModal modal = (MyGamesUpcomingModal) getItem(position);
        String[] str_array = modal.getDateTimeOfMatch().split(" ");
        String date = str_array[0];
        String time = str_array[1];

        String[] date_array = date.split("/");
        String month = date_array[0];
        String day = date_array[1];
        String year = date_array[2];

        String dayOfTheWeek = null;
        try {
            dayOfTheWeek = Utilities.getDay(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.txt_matchdate.setText(dayOfTheWeek+","+Utilities.getMonthString(month).toUpperCase() + " " + day);


        holder.txt_one.setText(time+" "+ str_array[2]);

        holder.txt_two.setText("@ "+modal.getVenue());

        if (modal.getMatchType().equals("Match")) {
            holder.img_logo_exchange.setImageResource(R.drawable.findgames_match);
        } else if (modal.getMatchType().equals("Freeplay")) {
            holder.img_logo_exchange.setImageResource(R.drawable.findgames_freeplay);
        } else if (modal.getMatchType().equals("Practice")) {
            holder.img_logo_exchange.setImageResource(R.drawable.findgames_practice);
        }

        if(modal.getOrgniserMode().equals("True")){
            if (modal.getJoinStatus().equals("NotJoin")){
                holder.img_ok_cancel.setImageResource(R.drawable.soccer_cross);
                holder.txt_three.setTextColor(context.getResources().getColor(R.color.cancelred));
                holder.txt_three.setText("Cancelled");
            }else if (modal.getJoinStatus().equals("Join")){
                holder.img_ok_cancel.setImageResource(R.drawable.findgames_check);
                holder.txt_three.setText("");
            }
        }else if(modal.getOrgniserMode().equals("False")){
            holder.img_ok_cancel.setImageResource(R.drawable.soccer_cross);
            holder.txt_three.setTextColor(context.getResources().getColor(R.color.openyourgamegreen));
            holder.txt_three.setText("Open your Game");
        }


        final ViewHolder finalHolder = holder;
        holder.img_ok_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prefs.getString(MyPreferenceManager.MYGAME_TYPE, null).equals("UPCOMING")) {
                    MatchId = modal.getMatchId();
                    if (modal.getOrgniserMode().equals("False")){

                    }else if(modal.getOrgniserMode().equals("True")){
                        if (finalHolder.txt_three.getText().toString().equals("Cancelled")){
                            new JoinAsync().execute();
                            finalHolder.img_ok_cancel.setImageResource(R.drawable.findgames_check);
                            finalHolder.txt_three.setText("");
                        }else {
                           new UNJoinAsync().execute();
                            finalHolder.img_ok_cancel.setImageResource(R.drawable.soccer_cross);
                            finalHolder.txt_three.setTextColor(context.getResources().getColor(R.color.cancelred));
                            finalHolder.txt_three.setText("Cancelled");
                        }
                    }
                }
            }
        });

        final ViewHolder finalHolder1 = holder;
        holder.txt_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalHolder1.txt_three.getText().toString().equals("Open your Game")){
                    String newFormat;
                        String[] str_array = modal.getDateTimeOfMatch().split(" ");
                        String date = str_array[0];
                        String time = str_array[1];

                        String[] date_array = date.split("/");
                        String month = date_array[0];
                        String day = date_array[1];

                        String dayOfTheWeek = null;
                        try {
                            dayOfTheWeek = Utilities.getDayFeed(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        newFormat = dayOfTheWeek.toUpperCase() + " " + Utilities.getMonthString(month).toUpperCase() + " " + day + " // " + time + " " + str_array[2];

                    Intent intent = new Intent(context, FeedDetailsActivity.class);
                    intent.putExtra("USERNAME", prefs.getString(MyPreferenceManager.USERNAME, null));
                    intent.putExtra("TITLE", modal.getTitle());
                    intent.putExtra("VANUE", modal.getVenue());
                    intent.putExtra("DESCRIPTION", modal.getDescription());
                    intent.putExtra("MATCHTYPE", modal.getMatchType());
                    intent.putExtra("DATETIME", newFormat);
                    intent.putExtra("SPOTS", modal.getNoOfPlayerToCompleteTeam());
                    intent.putExtra("GAMELEVEL", modal.getGameLevel());
                    intent.putExtra("GENDER", modal.getGender());
                    intent.putExtra("PICTURE", prefs.getString(MyPreferenceManager.IMAGE_URL, null));
                    intent.putExtra("JOINSTATUS",modal.getJoinStatus());
                    intent.putExtra("ORGANISERMODE",modal.getOrgniserMode());
                    intent.putExtra("MATCHID",modal.getMatchId());
                    intent.putExtra("LATITUDE",modal.getLatitude());
                    intent.putExtra("LONGITUDE",modal.getLongitude());
                    intent.putExtra("TYPEOFPOST", "game");
                    intent.putExtra("ADDRESS","null");
                    intent.putExtra("DATETIMEOFMATCH", modal.getDateTimeOfMatch());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }
    /*.............UnJoin the Match.............*/
    public class UNJoinAsync extends AsyncTask<String, String, String> {
        ProgressDialog pdial = new ProgressDialog(context);
        String url = Constants.UnjoinMatch;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdial.setMessage("Loading...");
            pdial.setCancelable(false);
//            pdial.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("MatchId",MatchId);
                json.put("PlayerId",PlayerId);

                Log.e("My Games Adapter", "UNJoin json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("My Games Adapter", "UNJoin API DATA" + data.toString());
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
                    Log.e("My Games Adapter", "UNJoin Plan success");
                } else {
                    Log.e("My Games Adapter", "UNJoin Plan UnSuccess");
                }
                pdial.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*.............Join the Match.............*/
    public class JoinAsync extends AsyncTask<String, String, String> {
        ProgressDialog pdial = new ProgressDialog(context);
        String url = Constants.JoinMatch;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdial.setMessage("Loading...");
            pdial.setCancelable(false);
//            pdial.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("MatchId",MatchId);
                json.put("PlayerId",PlayerId);

                Log.e("My Games Adapter", "Join json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("My Games Adapter", "Join" + data.toString());
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


}
