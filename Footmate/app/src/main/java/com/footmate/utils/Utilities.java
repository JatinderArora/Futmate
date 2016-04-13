package com.footmate.utils;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.footmate.model.SelectListModal;
import com.footmate.model.TimerModel;

/**
 * Created by d_RaMaN on 12/18/2015.
 */
public class Utilities {
    public static ArrayList<SelectListModal> locationlist = new ArrayList<>();
    public static ArrayList<TimerModel> timer_Arraylist = new ArrayList<TimerModel>();

    // seserializeObject
    public static byte[] serializeObject(Object o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();

            // Get the bytes of the serialized object
            byte[] buf = bos.toByteArray();

            return buf;
        } catch (IOException ioe) {
            Log.e("serializeObject", "error", ioe);

            return null;
        }
    }
        //deserializeObject
    public static Object deserializeObject(byte[] b) {
        try {
            Log.e("", "value of b" + b);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
            Object object = in.readObject();
            in.close();

            return object;
        } catch (ClassNotFoundException cnfe) {
            Log.e("deserializeObject", "class not found error", cnfe);

            return null;
        } catch (IOException ioe) {
            Log.e("deserializeObject", "io error", ioe);

            return null;
        }
    }

    public static String getMonthString(String month){
        String mon = null;
        if(month.equals("1")){
            mon = "Jan";
        }else if(month.equals("2")){
            mon = "Feb";
        }else if(month.equals("3")){
            mon = "Mar";
        }else if(month.equals("4")){
            mon = "Apr";
        }else if(month.equals("5")){
            mon = "May";
        }else if(month.equals("6")){
            mon = "Jun";
        }else if(month.equals("7")){
            mon = "Jul";
        }else if(month.equals("8")){
            mon = "Aug";
        }else if(month.equals("9")){
            mon = "Sep";
        }else if(month.equals("10")){
            mon = "Oct";
        }else if(month.equals("11")){
            mon = "Nov";
        }else if(month.equals("12")){
            mon = "Dec";
        }
        return mon;
    }

    public static String getDay(String dateStr) throws ParseException {
        SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy");
        Date dateObj = curFormater.parse(dateStr);
        int d = dateObj.getDay();
        String day = null;
        if(d == 0){
            day = "Sunday";
        }else if(d == 1){
            day = "Monday";
        }else if(d == 2){
            day = "Tuesday";
        }else if(d == 3){
            day = "Wednesday";
        }else if(d == 4){
            day = "Thursday";
        }else if(d == 5){
            day = "Friday";
        }else if(d == 6){
            day = "Saturday";
        }
        return day;
    }

    public static String getDayFeed(String dateStr) throws ParseException {
        SimpleDateFormat curFormater = new SimpleDateFormat("MM/dd/yyyy");
        Date dateObj = curFormater.parse(dateStr);
        int d = dateObj.getDay();
        String day = null;
        if(d == 0){
            day = "Sun";
        }else if(d == 1){
            day = "Mon";
        }else if(d == 2){
            day = "Tue";
        }else if(d == 3){
            day = "Wed";
        }else if(d == 4){
            day = "Thu";
        }else if(d == 5){
            day = "Fri";
        }else if(d == 6){
            day = "Sat";
        }
        return day;
    }


}
