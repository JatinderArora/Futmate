package com.footmate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import com.footmate.fragment.Home_Fragment;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.listeners.ExceptionListener;
import com.footmate.listeners.ResponseListener;
import com.footmate.uploadimage.ImageHelper2;
import com.footmate.utils.Constants;
import com.footmate.utils.HttpFileUpload;


public class TrackGameOrganiserActivity extends ActionBarActivity {
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    public String Path, path;
    public Uri selectedImageUri;

    private Context context = TrackGameOrganiserActivity.this;
    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    Toolbar toolbar;
    private TextView text_top;
    private ImageView img_toolbar_back;
    Bitmap bitmap;
    String ImageName = "";
    ProgressDialog dialogprogress = null;
    public int teamTest = 1;
    public int opponentTest = 1;
    public int goalsTest = 1;
    public int assistTest = 1;


    private TextView txtMatch, txtFieldName, text_Teamnumber, text_TeamBelowtxt, text_vs, text_Opponentnumber,
            text_OpponentBelowtxt, txtTimer, text_Goalsnumber, text_GoalsBelowtxt, text_Assistsnumber, text_AssistsBelowtxt,
            text_Distancenumber, text_Distance, text_Averageenumber, text_Average, text_MaxSpeednumber, text_MaxSpeed, text_GameSummary;
    private EditText edit_GameSummary;
    private ImageView img_TeamUpArrow, img_TeamDownArrow, img_OpponentUpArrow, img_OpponentDownArrow, img_GoalsUpArrow, img_GoalsDownArrow,
            img_AssistsUpArrow, img_AssistsDownArrow, img_Camra, img_Delete, img_Post;

    String PlayerID;
    SharedPreferences prefs;
    Intent intent;
    String latitude, longitude, time, tracktype, fieldname, address, teamsize, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_game_organiser);
        setTypeface();

        setToolBar();

        setWidgets();

        setClickListeners();
        dialogprogress = new ProgressDialog(TrackGameOrganiserActivity.this);
        prefs = getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, MODE_PRIVATE);
        PlayerID = prefs.getString(MyPreferenceManager.LOGIN_ID, null);

        intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        time = intent.getStringExtra("time");
        tracktype = intent.getStringExtra("tracktype");
        fieldname = intent.getStringExtra("fieldname");
        address = intent.getStringExtra("address");
        teamsize = intent.getStringExtra("teamsize");
        city = intent.getStringExtra("city");

        txtMatch.setText(tracktype+" #"+teamsize);
        txtFieldName.setText(fieldname);
        txtTimer.setText(time);
    }

    private void setTypeface() {
        Ubuntu_Medium = Typeface.createFromAsset(getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(getAssets(), "Ubuntu-R.ttf");
    }

    private void setToolBar() {

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        img_toolbar_back = (ImageView) toolbar.findViewById(R.id.img_toolbar_back);
        text_top = (TextView) toolbar.findViewById(R.id.text_top);
        text_top.setTypeface(Ubuntu_Medium);
        img_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Do you want to delete?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        Home_Fragment.setPostion(0);
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = dialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(false);
                alert.show();
            }
        });
    }

    private void setWidgets() {
        txtMatch = (TextView) findViewById(R.id.txtMatch);
        txtFieldName = (TextView) findViewById(R.id.txtFieldName);
        text_Teamnumber = (TextView) findViewById(R.id.text_Teamnumber);
        text_TeamBelowtxt = (TextView) findViewById(R.id.text_TeamBelowtxt);
        text_vs = (TextView) findViewById(R.id.text_vs);
        text_Opponentnumber = (TextView) findViewById(R.id.text_Opponentnumber);
        text_OpponentBelowtxt = (TextView) findViewById(R.id.text_OpponentBelowtxt);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        text_Goalsnumber = (TextView) findViewById(R.id.text_Goalsnumber);
        text_GoalsBelowtxt = (TextView) findViewById(R.id.text_GoalsBelowtxt);
        text_Assistsnumber = (TextView) findViewById(R.id.text_Assistsnumber);
        text_AssistsBelowtxt = (TextView) findViewById(R.id.text_AssistsBelowtxt);
        text_Distancenumber = (TextView) findViewById(R.id.text_Distancenumber);
        text_Distance = (TextView) findViewById(R.id.text_Distance);
        text_Averageenumber = (TextView) findViewById(R.id.text_Averageenumber);
        text_Average = (TextView) findViewById(R.id.text_Average);
        text_MaxSpeednumber = (TextView) findViewById(R.id.text_MaxSpeednumber);
        text_MaxSpeed = (TextView) findViewById(R.id.text_MaxSpeed);
        text_GameSummary = (TextView) findViewById(R.id.text_GameSummary);

        edit_GameSummary = (EditText) findViewById(R.id.edit_GameSummary);

        img_Camra = (ImageView) findViewById(R.id.img_Camra);

        img_TeamUpArrow = (ImageView) findViewById(R.id.img_TeamUpArrow);
        img_TeamDownArrow = (ImageView) findViewById(R.id.img_TeamDownArrow);

        img_OpponentUpArrow = (ImageView) findViewById(R.id.img_OpponentUpArrow);
        img_OpponentDownArrow = (ImageView) findViewById(R.id.img_OpponentDownArrow);

        img_GoalsUpArrow = (ImageView) findViewById(R.id.img_GoalsUpArrow);
        img_GoalsDownArrow = (ImageView) findViewById(R.id.img_GoalsDownArrow);

        img_AssistsUpArrow = (ImageView) findViewById(R.id.img_AssistsUpArrow);
        img_AssistsDownArrow = (ImageView) findViewById(R.id.img_AssistsDownArrow);

        img_Delete = (ImageView) findViewById(R.id.img_Delete);
        img_Post = (ImageView) findViewById(R.id.img_Post);


        txtMatch.setTypeface(Ubuntu_Medium);
        txtFieldName.setTypeface(Ubuntu_Medium);
        text_Teamnumber.setTypeface(Ubuntu_Medium);
        text_TeamBelowtxt.setTypeface(Ubuntu_Medium);
        text_vs.setTypeface(Ubuntu_Medium);
        text_Opponentnumber.setTypeface(Ubuntu_Medium);
        text_OpponentBelowtxt.setTypeface(Ubuntu_Medium);
        txtTimer.setTypeface(Ubuntu_Medium);
        text_Goalsnumber.setTypeface(Ubuntu_Medium);
        text_GoalsBelowtxt.setTypeface(Ubuntu_Medium);
        text_Assistsnumber.setTypeface(Ubuntu_Medium);
        text_AssistsBelowtxt.setTypeface(Ubuntu_Medium);
        text_Distancenumber.setTypeface(Ubuntu_Medium);
        text_Distance.setTypeface(Ubuntu_Regular);
        text_Averageenumber.setTypeface(Ubuntu_Medium);
        text_Average.setTypeface(Ubuntu_Regular);
        text_MaxSpeednumber.setTypeface(Ubuntu_Medium);
        text_MaxSpeed.setTypeface(Ubuntu_Regular);
        edit_GameSummary.setTypeface(Ubuntu_Medium);
        text_GameSummary.setTypeface(Ubuntu_Medium);
    }

    public void setClickListeners() {

        img_TeamUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamTest += 1;
                if (teamTest > 50) {
                    teamTest = 1;
                }
                String str = Integer.toString(teamTest);
                text_Teamnumber.setText(str);
            }
        });
        img_TeamDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamTest -= 1;
                if (teamTest < 1) {
                    teamTest = 50;
                }
                String str = Integer.toString(teamTest);
                text_Teamnumber.setText(str);
            }
        });
        img_OpponentUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentTest += 1;
                if (opponentTest > 50) {
                    opponentTest = 1;
                }
                String str = Integer.toString(opponentTest);
                text_Opponentnumber.setText(str);
            }
        });
        img_OpponentDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opponentTest -= 1;
                if (opponentTest < 1) {
                    opponentTest = 50;
                }
                String str = Integer.toString(opponentTest);
                text_Opponentnumber.setText(str);
            }
        });
        img_GoalsUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalsTest += 1;
                if (goalsTest > 50) {
                    goalsTest = 1;
                }
                String str = Integer.toString(goalsTest);
                text_Goalsnumber.setText(str);
            }
        });
        img_GoalsDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalsTest -= 1;
                if (goalsTest < 1) {
                    goalsTest = 50;
                }
                String str = Integer.toString(goalsTest);
                text_Goalsnumber.setText(str);
            }
        });
        img_AssistsUpArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assistTest += 1;
                if (assistTest > 50) {
                    assistTest = 1;
                }
                String str = Integer.toString(assistTest);
                text_Assistsnumber.setText(str);
            }
        });
        img_AssistsDownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assistTest -= 1;
                if (assistTest < 1) {
                    assistTest = 50;
                }
                String str = Integer.toString(assistTest);
                text_Assistsnumber.setText(str);
            }
        });

        img_Camra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });
        img_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Path.length() == 0) {
                    new PostTrackData().execute();
                }else {
                    dialogprogress.setMessage("Please Wait..");
                    dialogprogress.setCancelable(false);
                    dialogprogress.show();
                    uploadImage();
                }
            }
        });
        img_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Do you want to delete?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        Home_Fragment.setPostion(0);
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = dialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.setCancelable(false);
                alert.show();
            }
        });

    }

    public class PostTrackData extends AsyncTask<Void, String, String> {
        String data;

        @Override
        protected void onPreExecute() {
            dialogprogress.setMessage("Please Wait...");
            dialogprogress.setCancelable(false);
            dialogprogress.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(Constants.TrackGames);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();

                json.put("PlayerId", PlayerID);
                json.put("FieldName",txtFieldName.getText().toString() );
                json.put("MatchType",tracktype);
                json.put("Distance", text_Distancenumber.getText().toString());
                json.put("Goal", text_Goalsnumber.getText().toString());
                json.put("Time", txtTimer.getText().toString());
                json.put("Assists", text_Assistsnumber.getText().toString());
                json.put("Nutmegs", "-1");
                json.put("AvgSpeed", text_Averageenumber.getText().toString());
                json.put("MaxSpeed", text_MaxSpeednumber.getText().toString());
                json.put("Latitude", latitude);
                json.put("Longitude", longitude);
                json.put("MyTeam", text_Teamnumber.getText().toString());
                json.put("Opponent", text_Opponentnumber.getText().toString());
                json.put("OrganizarMode", "true");
                json.put("ImageName", ImageName);
                json.put("TypeOfPost", "track");
                json.put("TeamSize", teamsize);
                json.put("Address", address);
                json.put("City",city);
                json.put("Description", edit_GameSummary.getText().toString());
                Log.e("POST Screen..", "json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("POST SCREEN", "data" + data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                dialogprogress.dismiss();
                return;
            }
            try {
                String Response = new JSONObject(s).getString("Response");
                if (Response.equals("1")) {
                    dialogprogress.dismiss();
                    onBackPressed();
                    Home_Fragment.setPostion(0);
                } else {
                    Toast.makeText(getApplicationContext(), "Server Error! Repost data", Toast.LENGTH_LONG).show();
                    dialogprogress.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQUEST) {
            try {
                selectedImageUri = data.getData();
                Log.e("selectedImageUri" + selectedImageUri, "c" + this);
                if (selectedImageUri == null)
                    return;
                Path = ImageHelper2.compressImage(selectedImageUri, this);
                Log.e("", "Path........." + Path);
                img_Camra.setAdjustViewBounds(true);
                img_Camra.setImageBitmap(ImageHelper2.decodeSampledBitmapFromFile(Path, 1024, 1024));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST) {
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                selectedImageUri = getImageUri(this, bitmap);
                Log.e("selectedImageUri" + selectedImageUri, "c" + this);
                if (selectedImageUri == null)
                    return;
                Path = ImageHelper2.compressImage(selectedImageUri, this);;
                Log.e("", "Path........." + Path);
                img_Camra.setAdjustViewBounds(true);
                img_Camra.setImageBitmap(ImageHelper2.decodeSampledBitmapFromFile(Path,1024, 1024));
            } catch (Exception e) {

            }
        }
    }

    // get the path/....
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Log.e("", "path............" + path);
        return Uri.parse(path);
    }

    // Custom Dialog Camra,Gallery()......
    public void showImageDialog() {
        final Dialog dialog = new Dialog(TrackGameOrganiserActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.setContentView(R.layout.dialog_image);
        TextView textView1 = (TextView) dialog.findViewById(R.id.text_gallery);
        TextView textView2 = (TextView) dialog.findViewById(R.id.text_camra);
        TextView textView3 = (TextView) dialog.findViewById(R.id.text_cancel1);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openGallery();
            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openCamra();
            }
        });
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void openGallery() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_REQUEST);
    }

    public void openCamra() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:{
                    new PostTrackData().execute();
                    break;
                }
                default: {
                    break;
                }
            }
        }

    };

    private void uploadImage(){
        new AsyncTask<Void,Void,Void>(){


            @Override
            protected Void doInBackground(Void... params) {
                // TODO Auto-generated method stub
                ImageName = System.currentTimeMillis()+".jpg";
                Log.e("", "Path"+Path);
                HttpFileUpload hfu = new HttpFileUpload(Constants.Image_Upload_Url, ImageName,"Description",Path,TrackGameOrganiserActivity.this,imageUplaodResponseListener,imageUploadExceptionListener);
                return null;
            }
        }.execute();
    }

    ResponseListener imageUplaodResponseListener = new ResponseListener(){

        @Override
        public void handleResponse(String res) {
            // TODO Auto-generated method stub
            Log.i("", "res"+res);
            if (res != null) {
                handler.sendEmptyMessage(3);
            }
        }

    };


    ExceptionListener imageUploadExceptionListener = new ExceptionListener(){

        @Override
        public void handleException(String exception) {
            // TODO Auto-generated method stub
            Log.i("", "exception"+exception);
        }

    };

}
