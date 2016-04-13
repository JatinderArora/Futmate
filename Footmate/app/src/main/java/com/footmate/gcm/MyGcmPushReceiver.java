package com.footmate.gcm;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.footmate.FeedDetailsActivity;
import com.footmate.Home_Activity;
import com.footmate.utils.Utilities;
import com.google.android.gms.gcm.GcmListenerService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.footmate.app.Config;
import com.footmate.fragment.ChatFragment;


/**
 * Created by patas tech on 4/11/2016.
 */
public class MyGcmPushReceiver extends GcmListenerService {

    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;
    String title = "Futmate";
    String image = "";
    String timestamp;
    String message, typeofnotification, username, posttitle, venue, description, matchtype, newFormat, spots, gamelevel, gender,
    picture, organisermode, matchid, latitude, longitude, typeofpost, address, datetimeofmatch;
    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        Log.e("DATA ", bundle.toString());
        try {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormatter.setLenient(false);
            Date today = new Date();
            timestamp = dateFormatter.format(today);
            typeofnotification = bundle.getString("TypeOfNotification");
            message = bundle.getString("Message");
            if (typeofnotification.equals("Message")) {
                username = bundle.getString("UserName");
                posttitle = bundle.getString("Title");
                venue = bundle.getString("Venue");
                description = bundle.getString("Description");
                matchtype = bundle.getString("MatchType");
                spots = bundle.getString("NoOfPlayerToCompleteTeam");
                gamelevel = bundle.getString("GameLevel");
                gender = bundle.getString("Gender");
                picture = bundle.getString("ImageUrl");
                organisermode = bundle.getString("OrganizarMode");
                matchid = bundle.getString("MatchId");
                latitude = bundle.getString("Latitude");
                longitude = bundle.getString("Longitude");
                typeofpost = bundle.getString("TypeOfPost");
                address = bundle.getString("Address");
                datetimeofmatch = bundle.getString("DateTimeOfMatch");

                String[] str_array = datetimeofmatch.split(" ");
                String date = str_array[0];
                String time = str_array[1];

                String[] date_array = date.split("/");
                String month = date_array[0];
                String day = date_array[1];

                String dayOfTheWeek = null;
                try {
                    dayOfTheWeek = Utilities.getDayFeed(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                newFormat = dayOfTheWeek.toUpperCase() + " " + Utilities.getMonthString(month).toUpperCase() + " " +
                        day + " // " + time + " " + str_array[2];
            }else{

            }
            Log.e(TAG, "From: " + from);
            Log.e(TAG, "Title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "image: " + image);
            Log.e(TAG, "typeofnotification: " + typeofnotification);
            Log.e(TAG, "timestamp: " + timestamp);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            // com.footmate.app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            if (typeofnotification.equals("Message")) {
                ChatFragment.refreshChat();
            }
            // play notification sound
//            NotificationUtils notificationUtils = new NotificationUtils();
//            notificationUtils.playNotificationSound();
        } else {
            Intent resultIntent;
            if (typeofnotification.equals("Message")){
                resultIntent = new Intent(getApplicationContext(), FeedDetailsActivity.class);
                resultIntent.putExtra("USERNAME", username);
                resultIntent.putExtra("TITLE", posttitle);
                resultIntent.putExtra("VANUE", venue);
                resultIntent.putExtra("DESCRIPTION", description);
                resultIntent.putExtra("MATCHTYPE", matchtype);
                resultIntent.putExtra("DATETIME", newFormat);
                resultIntent.putExtra("SPOTS", spots);
                resultIntent.putExtra("GAMELEVEL", gamelevel);
                resultIntent.putExtra("GENDER", gender);
                resultIntent.putExtra("PICTURE", picture);
                resultIntent.putExtra("JOINSTATUS","Join");
                resultIntent.putExtra("ORGANISERMODE", organisermode);
                resultIntent.putExtra("MATCHID", matchid);
                resultIntent.putExtra("LATITUDE", latitude);
                resultIntent.putExtra("LONGITUDE", longitude);
                resultIntent.putExtra("TYPEOFPOST", typeofpost);
                resultIntent.putExtra("ADDRESS", address);
                resultIntent.putExtra("DATETIMEOFMATCH", datetimeofmatch);
                resultIntent.putExtra("Chat", "chat");
            }else{
                resultIntent = new Intent(getApplicationContext(), Home_Activity.class);
                resultIntent.putExtra("NotiState","Active");
            }

            if (TextUtils.isEmpty(image)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            } else {
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, image);
            }
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
