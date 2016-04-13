package com.footmate.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.footmate.Home_Activity;
import com.footmate.TrackTimerActivity;

public class TrackTimerService extends Service {
    String TAG = "TrackTimerService";
    private long startTime = 0L;

    Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    String timerValue;

//    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//    double longitude = location.getLongitude();
//    double latitude = location.getLatitude();

    public TrackTimerService() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 1000);
            }
        }).start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            try {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

                updatedTime = timeSwapBuff + timeInMilliseconds;

                int seconds = (int) (updatedTime / 1000) % 60 ;
                int minutes = (int) ((updatedTime / (1000*60)) % 60);
                int hours   = (int) ((updatedTime / (1000*60*60)) % 24);

                timerValue =String.format("%02d:%02d:%02d",hours,minutes,seconds);
                Log.e("timerValue", timerValue);
                TrackTimerActivity.txtTimer.setText(timerValue);
                if(minutes == 0) {
                    Home_Activity.timerText.setText(String.format("%d", seconds) + "s");
                }else if (hours == 0 && minutes > 0){
                    Home_Activity.timerText.setText(String.format("%d", minutes) + "m");
                }else if(hours > 0){
                    Home_Activity.timerText.setText(String.format("%d", hours) + "h");
                }

            //            TimerModel com.footmate.model = new TimerModel();
            //            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, locationListener);
            //            com.footmate.model.setTime(updatedTime + "");
            //            com.footmate.model.setLatitude(latitude);
            //            com.footmate.model.setLongitude(longitude);
            //            Utilities.timer_Arraylist.add(com.footmate.model);

                customHandler.postDelayed(this, 1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    };

//    private final LocationListener locationListener = new LocationListener() {
//        public void onLocationChanged(Location location) {
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();
//        }

//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };
//
//
//    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
