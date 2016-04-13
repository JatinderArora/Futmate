package com.footmate;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.Constants;
import com.footmate.views.SwitchButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Start_New_PlanActivity extends ActionBarActivity {
    private Activity activity = Start_New_PlanActivity.this;
    private Context context = Start_New_PlanActivity.this;
    private String TAG = "Start_New_PlanActivity";

    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    private Toolbar toolbar;
    private TextView text_top;
    private ImageView img_toolbar_back, img_next;

    private TextView text_datetime, text_selectfield,text_organizer, text_match, text_freeeplay, text_practice;
    private RelativeLayout layout_datetime,layout_field;
//    private Switch switch_organizer;
    private SwitchButton switch_organizer;
    private ImageView img_field,img_select_datetime;

    private LinearLayout layout_threebutton;
    // sharedprefs..
    private SharedPreferences prefs;
    SharedPreferences.Editor editor;


    public String format = "";
    public String PlayerID = "";
    boolean hasFocus = true;
    public String select_Field = "";
    public String vanue, address, lat, longi, city;
    public String Match_Type = "Match";
    public String OrganizerMode = "True";
    private CheckAlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start__new__plan);
        alertDialog = new CheckAlertDialog();
        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS,MODE_PRIVATE);
        editor = prefs.edit();

        PlayerID = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
        Log.e(TAG, "PlayerID......" + PlayerID);
        //set typeface...for external font families...
        setTypeface();
        // set toolbar..with text and navigation back button..
        setToolBar();
        //set widget ids and their typeface....
        setWidgetIDSWithTypeface();
        // click listner of views
        setOnCLickListner();

        editor.putString(MyPreferenceManager.LATITUDE, null);
        editor.putString(MyPreferenceManager.LONGITUDE, null);
        editor.putString(MyPreferenceManager.VANUE_NAME, null);
        editor.putString(MyPreferenceManager.VANUE_ADDRESS, null);
        editor.commit();


    }

    // all views click listner......
    private void setOnCLickListner() {
       // select the date time.......
        img_select_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDateTimeDialog();
                showDateTimeCustomDialog();
            }
        });
        text_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDateTimeDialog();
                showDateTimeCustomDialog();
            }
        });
        // select a  field..... img_field,img_select_datetime
        img_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Select_Field_Screen.class);
                startActivityForResult(intent, 2);// Activity is started with requestCode 2
            }
        });
        text_selectfield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Select_Field_Screen.class);
                startActivityForResult(intent, 2);// Activity is started with requestCode 2
            }
        });
        // select organizer mode.....
        switch_organizer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OrganizerMode = "True";
                } else {
                    OrganizerMode = "false";
                }
            }
        });
        // click on match.....
        text_match.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_threebutton.setBackgroundResource(R.drawable.match);
                Match_Type = "Match";
            }
        });
        // click on freeplay.....
        text_freeeplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_threebutton.setBackgroundResource(R.drawable.freeplay);
                Match_Type = "Freeplay";
            }
        });
        // click on practice....
        text_practice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_threebutton.setBackgroundResource(R.drawable.practice);
                Match_Type = "Practice";
            }
        });
        // click on next button....img
        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text_datetime.equals("") || text_datetime.getText().toString().equals("Select Date and Time")) {
                    alertDialog.showcheckAlert3(activity, "Futmate!", "Please select the date and time.", true);
                } else if (text_selectfield.equals("") || text_selectfield.getText().toString().equals("Select a Field")) {
                    alertDialog.showcheckAlert3(activity, "Futmate!", "Please select the field.", true);
                } else {
                    Intent intent = new Intent(context, NewPlan_Activity.class);
                    intent.putExtra("ORGANIGER_MODE", OrganizerMode);
                    intent.putExtra("DATETIME", text_datetime.getText().toString());
                    intent.putExtra("VANUE", vanue);
                    intent.putExtra("ADDRESS", address);
                    intent.putExtra("CITY",city);
                    intent.putExtra("MATCH_TYPE", Match_Type);
                    intent.putExtra("LATITUDE", lat);
                    intent.putExtra("LONGITUDE", longi);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }


    private void setWidgetIDSWithTypeface() {
        text_datetime = (TextView) findViewById(R.id.text_datetime);
        text_selectfield = (TextView) findViewById(R.id.text_field);
        text_organizer = (TextView) findViewById(R.id.text_organizer);
        text_match = (TextView) findViewById(R.id.text_match);
        text_freeeplay = (TextView) findViewById(R.id.text_freeeplay);
        text_practice = (TextView) findViewById(R.id.text_practice);


        layout_datetime = (RelativeLayout)findViewById(R.id.layout_datetime);
        layout_field  = (RelativeLayout)findViewById(R.id.layout_field);

        layout_threebutton = (LinearLayout) findViewById(R.id.layout_threebutton);
        img_next = (ImageView) findViewById(R.id.img_next);

        img_select_datetime= (ImageView) findViewById(R.id.img_select_datetime);
        img_field= (ImageView) findViewById(R.id.img_field);

        switch_organizer = (SwitchButton) findViewById(R.id.switch_organizer);

        text_datetime.setTypeface(Ubuntu_Regular);
        text_selectfield.setTypeface(Ubuntu_Regular);
        text_organizer.setTypeface(Ubuntu_Regular);
        // set setTypeface of threebuttons.....
        text_match.setTypeface(Ubuntu_Regular);
        text_freeeplay.setTypeface(Ubuntu_Regular);
        text_practice.setTypeface(Ubuntu_Regular);


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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start__new__plan, menu);
        return true;
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            if(data != null) {
                vanue = data.getStringExtra("VANUE");
                address = data.getStringExtra("ADDRESS");
                lat = data.getStringExtra("LAT");
                longi = data.getStringExtra("LONG");
                city = data.getStringExtra("CITY");

                text_selectfield.setText(vanue);

                // insert Recentvanues............
                InsertRecentVenueAsyn obj1 = new InsertRecentVenueAsyn();
                obj1.execute();

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void showDateTimeDialog(){
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.date_time_dialog);
//
//        final TextView text_title = (TextView)dialog.findViewById(R.id.text_title);
//        final TextView text_date = (TextView)dialog.findViewById(R.id.text_date);
//        final TextView text_time = (TextView)dialog.findViewById(R.id.text_time);
//        final DatePicker select_date = (DatePicker) dialog.findViewById(R.id.select_date);
//        final TimePicker select_time = (TimePicker) dialog.findViewById(R.id.select_time);
//        final TextView text_ok = (TextView) dialog.findViewById(R.id.text_ok);
//        final TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
//        text_ok.setTypeface(Ubuntu_Medium);
//        text_cancel.setTypeface(Ubuntu_Medium);
//        text_title.setTypeface(Ubuntu_Medium);
//        text_date.setTypeface(Ubuntu_Medium);
//        text_time.setTypeface(Ubuntu_Medium);
//
//
//        text_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                Date selectedDate = null;
//                Calendar calendar = Calendar.getInstance();
//
//                int day = select_date.getDayOfMonth();
//                int month = select_date.getMonth();
//
//                int month1 = month + 1;
//
//
//                int year = select_date.getYear();
//                calendar.set(Calendar.MONTH, month);
//                String month_name = new DateFormatSymbols().getShortMonths()[month];
//                Log.e(TAG, "month number.............." + month);
//                Log.e(TAG, "month_name.............." + month_name);
//
//
//
//                int hour = select_time.getCurrentHour();
//                int minute = select_time.getCurrentMinute();
//
//                if (hour == 0) {
//                    hour += 12;
//                    format = "AM";
//                } else if (hour == 12) {
//                    format = "PM";
//                } else if (hour > 12) {
//                    hour -= 12;
//                    format = "PM";
//                } else {
//                    format = "AM";
//                }
//                Log.e("date time.....","hour"+hour +"minut.." + minute);
//                String setonlbl = day + " " + month_name + " " + year;
//
//
//                // convert the datepicker and timepicker datetime to milliseconds.....
//                String dateformat = year + "-"+month1+"-"+day+" "+hour+":"+minute;
//                Log.e("dateformat","dateformat"+dateformat);
//                long selectedDateTime = 0;
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                try {
//                    Date mDate = sdf.parse(dateformat);
//                    selectedDateTime = mDate.getTime();
//                    Log.e("selectedDateTime","selectedDateTime" + selectedDateTime);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//
//                // getting the system current datetime....
//                long myCurrentTime = System.currentTimeMillis();
//                Log.e("myCurrentTime", "myCurrentTime" + myCurrentTime);
//
//                // set the check..
//                if (myCurrentTime < selectedDateTime){
//
//                    text_datetime.setText(new StringBuilder().append(setonlbl).append(",").append(hour).append(" : ").append(minute)
//                            .append(" ").append(format));
//                }else{
//                    alertDialog.showcheckAlert3(activity, "Futmate!", "You cannot add past time, Please use greater than current time.", true);
//                }
//
//
//            }
//        });
//        text_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        dialog.setCanceledOnTouchOutside(false);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        // recent field api
        // getting lat,long,vanuename,address from sharedpref...

        if (prefs.getString(MyPreferenceManager.VANUE_NAME,null) != null) {
            text_selectfield.setText(prefs.getString(MyPreferenceManager.VANUE_NAME, null));
            vanue = prefs.getString(MyPreferenceManager.VANUE_NAME, null);
            address = prefs.getString(MyPreferenceManager.VANUE_ADDRESS, null);
            lat = prefs.getString(MyPreferenceManager.LATITUDE, null);
            longi = prefs.getString(MyPreferenceManager.LONGITUDE, null);
            city = prefs.getString(MyPreferenceManager.VANUE_CITY, null);
            // insert Recentvanues............
            InsertRecentVenueAsyn obj = new InsertRecentVenueAsyn();
            obj.execute();
        }
    }
    public class InsertRecentVenueAsyn extends AsyncTask<Void, String, String> {
        final ProgressDialog pDialog = new ProgressDialog(context);
        String url = Constants.InsertRecentPlaces;
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
                json.put("Venue",vanue);
                json.put("Address",address);
                json.put("Latitude",lat);
                json.put("Longitude",longi);
                json.put("City", city);
                Log.e("InsertRecentPlaces...","json......."+json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e(TAG, " Response Code InsertRecentPlaces............. = " + code);
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
                if (response.equals("1")){
                    Toast.makeText(context,"Succes ! Insert Recent Venue.",Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    /*Show the New DAte time Dialog*/

    private void showDateTimeCustomDialog() {
        final Dialog dialog = new Dialog(Start_New_PlanActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_datetime);
        dialog.setCancelable(false);


        final TextView text_date = (TextView)dialog.findViewById(R.id.text_date);
        final TextView text_time = (TextView)dialog.findViewById(R.id.text_time);
        final DatePicker select_date = (DatePicker) dialog.findViewById(R.id.select_date);
        final TimePicker select_time = (TimePicker) dialog.findViewById(R.id.select_time);
        final TextView text_ok = (TextView) dialog.findViewById(R.id.text_ok);
        final TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);

        select_time.setVisibility(View.GONE);

        text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_date.setVisibility(View.VISIBLE);
                select_time.setVisibility(View.GONE);
            }
        });
        text_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_date.setVisibility(View.GONE);
                select_time.setVisibility(View.VISIBLE);
            }
        });



        text_ok.setTypeface(Ubuntu_Medium);
        text_cancel.setTypeface(Ubuntu_Medium);
        text_date.setTypeface(Ubuntu_Medium);
        text_time.setTypeface(Ubuntu_Medium);


        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Date selectedDate = null;
                Calendar calendar = Calendar.getInstance();

                int day = select_date.getDayOfMonth();
                int month = select_date.getMonth();

                int month1 = month + 1;


                int year = select_date.getYear();
                calendar.set(Calendar.MONTH, month);
                String month_name = new DateFormatSymbols().getShortMonths()[month];
                Log.e(TAG, "month number.............." + month);
                Log.e(TAG, "month_name.............." + month_name);



                int hour = select_time.getCurrentHour();
                int minute = select_time.getCurrentMinute();

                if (hour == 0) {
                    hour += 12;
                    format = "AM";
                } else if (hour == 12) {
                    format = "PM";
                } else if (hour > 12) {
                    hour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }
                Log.e("date time.....","hour"+hour +"minut.." + minute);
                String setonlbl = day + " " + month_name + " " + year;


                // convert the datepicker and timepicker datetime to milliseconds.....
                String dateformat = year + "-"+month1+"-"+day+" "+hour+":"+minute+" "+format;
                Log.e("dateformat","dateformat"+dateformat);
                long selectedDateTime = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
                try {
                    Date mDate = sdf.parse(dateformat);
                    selectedDateTime = mDate.getTime();
                    Log.e("selectedDateTime","selectedDateTime" + selectedDateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                // getting the system current datetime....
                long myCurrentTime = System.currentTimeMillis();
                Log.e("myCurrentTime", "myCurrentTime" + myCurrentTime);

                // set the check..
                if (myCurrentTime < selectedDateTime){

                    text_datetime.setText(new StringBuilder().append(setonlbl).append(", ").append(String.format("%02d",hour)).append(" : ").append(String.format("%02d",minute))
                            .append(" ").append(format));
                }else{
                    alertDialog.showcheckAlert3(activity, "Futmate!", "You cannot add past time, Please use greater than current time.", true);
                }


            }
        });
        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }
}
