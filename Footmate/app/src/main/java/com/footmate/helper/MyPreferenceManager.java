package com.footmate.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by patas tech on 2/10/2016.
 */
public class MyPreferenceManager {
    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;



    public static String FOOTMATE_PREFS = "prefs";

    public static String LOGIN_ID = "login_id";
    public static String IMAGE_URL = "image_url";
    public static String USERNAME = "username";
    public static String ABOUT = "about";
    public static String EMAIL_ID = "email_id";
    public static String LOGIN_TYPE = "login_type";
    public static String DEVICE_ID = "device_id";

    public static String VANUE_NAME = "vanue_name";
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String VANUE_ADDRESS = "vanue_address";
    public static String VANUE_CITY = "vanue_city";

    /*HomeActivity Lat..Long...*/
    public static String  HOME_LATITUDE = "home_latitude";
    public static String  HOME_LONGITUDE = "home_longitude";

    public static String TRACK_TYPE = "track_type";
    public static String TRACK_LATITUDE = "track_latitude";
    public static String TRACK_LONGITUDE = "track_longitude";
    public static String TRACK_FIELDNAME = "track_fieldname";
    public static String TRACK_ORGANISERMODE = "track_organisermode";
    public static String TRACK_ADDRESS = "track_address";
    public static String TIMER_STATUS = "start_timer";
    public static String TRACK_TEAM_SIZE = "track_teamsize";
    public static String TRACK_CITY = "track_city";

    public static String COMMENT_COUNT = "comment_count";
    public static String POSITION = "position";

    public static String MYGAME_TYPE = "mygame_type";




    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(FOOTMATE_PREFS, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
