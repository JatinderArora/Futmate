package com.footmate.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;

import com.footmate.Home_Activity;

public class CheckAlertDialog {
	// Dialog with Message with one button"
	public void showcheckAlert1(final Activity context, final String message,
			final boolean status) {
		// define alert...
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		// set message...
		dialog.setMessage(message);
		// set button status..onclick
		dialog.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (status == true) {
                    dialog.dismiss();
                }
            }
        });
		AlertDialog alert = dialog.create();
		alert.setCanceledOnTouchOutside(false);
		alert.setCancelable(false);
		alert.show();
	}

	// Dialog with Message with Two button
	public void showcheckAlert2(final Activity context, final String message,
			final boolean yesstatus, final boolean nostatus) {
		// define alert...
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		// set message...
		dialog.setMessage(message);
		// set yes button status..onclick
		dialog.setPositiveButton("OK", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (yesstatus == true) {
                    dialog.dismiss();
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });
		// set no button status ... onclick...
		dialog.setNegativeButton("NO", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (nostatus == true) {
                    dialog.dismiss();
                }
            }
        });
		AlertDialog alert = dialog.create();
		alert.setCanceledOnTouchOutside(false);
		alert.setCancelable(false);
		alert.show();

	}

	// Dialog with Title,Message with one button
	public void showcheckAlert3(Activity context, String title,
			String message, final boolean yesstatus) {
//		// define alert...
//		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//		// set title...
//		dialog.setTitle(title);
//		// set message...
//		dialog.setMessage(message);
//		// set yes button status..onclick
//        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                if (yesstatus == true) {
//                    dialog.dismiss();
//                }
//            }
//        });
//		//AlertDialog alert = dialog.create();
//        dialog.setCancelable(false);
//        dialog.show();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();

	}




}
