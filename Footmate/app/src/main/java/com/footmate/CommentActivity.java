package com.footmate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.footmate.adapter.CommetListAdapter;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.model.CommentListModal;
import com.footmate.utils.CheckAlertDialog;
import com.footmate.utils.Constants;


public class CommentActivity extends AppCompatActivity {
    private Context context = CommentActivity.this;
    private String TAG = "CommentActivity";
    private CheckAlertDialog checkDailog;
    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    private Toolbar toolbar;
    private TextView text_top;
    private ImageView img_toolbar_back, img_clear;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private EditText edit_writecomment;
    private ImageView img_postcomment;
    private ListView list_comments;
    private CommetListAdapter mAdapter;
    private ArrayList<CommentListModal> modalList;
    public String CommentText = "";
    public String Match_ID = "";
    public String Player_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        checkDailog = new CheckAlertDialog();
        prefs = getApplicationContext().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
        editor = prefs.edit();
        Player_ID = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
        Match_ID = getIntent().getExtras().getString("MATCHID");
        Log.e(TAG, "Player_ID..." + Player_ID);
        Log.e(TAG, "Match_ID..." + Match_ID);


        // set Typeface...
        setTypeface();
        // set Toolbar....
        setToolBar();
        // setWidgetIDS...
        setWidgetIDs();
        // setListViewAdapter...with.... All Comments...
        modalList = new ArrayList<CommentListModal>();
        AllComments objT = new AllComments();
        objT.execute();
        // setClickListner...
        setClickListNer();
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

    private void setWidgetIDs() {
        edit_writecomment = (EditText) findViewById(R.id.edit_writecomment);
        img_postcomment = (ImageView) findViewById(R.id.img_postcomment);
        img_clear = (ImageView) findViewById(R.id.img_clear);
        list_comments = (ListView) findViewById(R.id.list_comments);
        edit_writecomment.setTypeface(Ubuntu_Regular);
    }


    private void setClickListNer() {
        img_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_writecomment.setText("");
            }
        });

        img_postcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentText = edit_writecomment.getText().toString();
                if (CommentText.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Futmate!");
                    dialog.setMessage("Write Comment on comment box.");
                    // set yes button status..onclick
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    });

                    AlertDialog alert = dialog.create();
                    alert.setCanceledOnTouchOutside(false);
                    alert.setCancelable(false);
                    alert.show();
                } else {
                    CommentAsync obj = new CommentAsync();
                    obj.execute();
                }
            }
        });
    }
    // getting the list of comments according to match id........
    public class AllComments extends AsyncTask<String, String, String> {
        String url = Constants.ListOfCommentsAcordingToMatch;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();

                json.put("MatchId", Match_ID);

                Log.e("Comment Screen", "All comment json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e(TAG, " Response Code For AllComment............. = " + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e(TAG, "all Comment....list" + data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("")) {
                return;
            }
            try {
                String response = new JSONObject(result).getString("Responce");
                String ListOfComments = new JSONObject(result).getString("ListOfComments");
                if (response.equals("1")) {
                    JSONArray jsonArray = new JSONArray(ListOfComments);
                    modalList.clear();
                    editor.putString(MyPreferenceManager.COMMENT_COUNT, jsonArray.length()+"");
                    editor.commit();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CommentListModal modal = new CommentListModal();
                        modal.setComment(jsonObject.getString("Comment"));
                        modal.setDateTimeOfComment(jsonObject.getString("DateTimeOfComment"));
                        modal.setImageUrl(jsonObject.getString("ImageUrl"));
                        modal.setMatchId(jsonObject.getString("MatchId"));
                        modal.setPlayerId(jsonObject.getString("PlayerId"));
                        modal.setPlayerName(jsonObject.getString("PlayerName"));
                        modalList.add(modal);
                    }

//                    Collections.reverse(modalList);
                    mAdapter = new CommetListAdapter(context, modalList);
                    list_comments.setAdapter(mAdapter);
                    list_comments.setSelection(mAdapter.getCount() - 1);

                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    // comment api implemented......
    public class CommentAsync extends AsyncTask<String, String, String> {
        String url = Constants.CommentMatch;
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();

                json.put("MatchId", Match_ID);
                json.put("PlayerId", Player_ID);
                json.put("Comment", CommentText);

                Log.e("Comment Screen", "json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e(TAG, " Response Code For Comment............. = " + code);
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity);
                Log.e(TAG, "Comment...." + result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("")) {
                return;
            }
            try {
                String response = new JSONObject(result).getString("Response");
                Log.e(TAG, "Put Comment Response......................." + response);
                if (response.equals("1")) {
                    edit_writecomment.setText("");
                    AllComments objectL = new AllComments();
                    objectL.execute();
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}



