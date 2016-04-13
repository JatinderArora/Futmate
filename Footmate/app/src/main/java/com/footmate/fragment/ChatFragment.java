package com.footmate.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.footmate.Home_Activity;
import com.footmate.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.footmate.adapter.ChatMessagesAdapter;
import com.footmate.app.MyApplication;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.model.ChatMessagesModal;
import com.footmate.utils.Constants;


public class ChatFragment extends Fragment {
    ImageView img_cross;
    static ListView chatlistview;
    static String matchid;
    String playerId;
    EditText msg_edittext;
    ImageView img_sendmsg;
    static ArrayList<ChatMessagesModal> chatmessagearrlist;
    static ChatMessagesAdapter adapter;
    SharedPreferences prefs;
    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.footmate.fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        prefs = getActivity().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, getActivity().MODE_PRIVATE);
        playerId = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
        img_cross = (ImageView)rootView.findViewById(R.id.img_cross);
        chatlistview = (ListView)rootView.findViewById(R.id.chatlistview);
        img_sendmsg = (ImageView)rootView.findViewById(R.id.img_sendmsg);
        msg_edittext = (EditText)rootView.findViewById(R.id.msg_edittext);
        chatmessagearrlist = new ArrayList<>();
        if (getArguments() != null){
            Bundle bundle = getArguments();
            matchid = bundle.getString("MATCHID");
        }

        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Home_Activity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        img_sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg_edittext.getText().toString().equals("")) {
                }else {
                    new SendMessagesAsyn().execute();
                }
            }
        });
        new GetAllChatMessagesAsyn().execute();
        return rootView;
    }

    static class GetAllChatMessagesAsyn extends AsyncTask<String, Void, String>{
        String data;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            try {
                HttpPost httpPost = new HttpPost(Constants.ListOfMessagesInChat);
                httpPost.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("MatchId", matchid);
                Log.e("ChatFragment", "GEt ALL Chat LIst " + json.toString());

                StringEntity stringEntity = new StringEntity(json.toString());
                httpPost.setEntity(stringEntity);

                HttpResponse response = defaultHttpClient.execute(httpPost);
                int code = response.getStatusLine().getStatusCode();

                Log.e("Chat Fragment","GEt ALL Chat LIst response code "+code);

                HttpEntity httpEntity = response.getEntity();
                data = EntityUtils.toString(httpEntity);
                Log.e("GEt ALL Chat LIst", "data" + data);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null){
                return;
            }
            try {
                JSONObject object = new JSONObject(s);
                String responce = object.getString("Responce");
                if (responce.equals("1")){
                    JSONArray arr = object.getJSONArray("ListOfMessages");
                    chatmessagearrlist.clear();
                    for (int i = 0; i < arr.length(); i++){
                        JSONObject listobj = arr.getJSONObject(i);
                        ChatMessagesModal modal = new ChatMessagesModal();
                        modal.setDateTimeOfMessage(listobj.getString("DateTimeOfMessage"));
                        modal.setDeviceId(listobj.getString("DeviceId"));
                        modal.setImageUrl(listobj.getString("ImageUrl"));
                        modal.setMatchId(listobj.getString("MatchId"));
                        modal.setMessage(listobj.getString("Message"));
                        modal.setPlayerId(listobj.getString("PlayerId"));
                        modal.setPlayerName(listobj.getString("PlayerName"));
                        chatmessagearrlist.add(modal);
                    }
                    adapter = new ChatMessagesAdapter(chatmessagearrlist, MyApplication.getInstance());
                    chatlistview.setAdapter(adapter);
                    chatlistview.setSelection(adapter.getCount() - 1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class SendMessagesAsyn extends AsyncTask<String, Void, String>{
        String data;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            try {
                HttpPost httpPost = new HttpPost(Constants.SendMessageInChat);
                httpPost.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("MatchId", matchid);
                json.put("PlayerId",playerId);
                json.put("Message",msg_edittext.getText().toString());
                Log.e("ChatFragment", "Send message " + json.toString());

                StringEntity stringEntity = new StringEntity(json.toString());
                httpPost.setEntity(stringEntity);

                HttpResponse response = defaultHttpClient.execute(httpPost);
                int code = response.getStatusLine().getStatusCode();

                Log.e("Chat Fragment","Send message "+code);

                HttpEntity httpEntity = response.getEntity();
                data = EntityUtils.toString(httpEntity);
                Log.e("Send chat message", "data" + data);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null){
                return;
            }
            try {
                JSONObject object = new JSONObject(s);
                String responce = object.getString("Responce");
                if (responce.equals("1")){
                    msg_edittext.setText("");
                    new GetAllChatMessagesAsyn().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public static void refreshChat(){
        new GetAllChatMessagesAsyn().execute();
    }

}