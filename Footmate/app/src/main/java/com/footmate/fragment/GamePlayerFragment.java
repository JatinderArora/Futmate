package com.footmate.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.footmate.Home_Activity;
import com.footmate.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.footmate.adapter.JoinPlayersAdapter;
import com.footmate.model.JoinPlayersModal;
import com.footmate.utils.Constants;


public class GamePlayerFragment extends Fragment {
    String matchid;
    ArrayList<JoinPlayersModal> arjoinplayer;
    JoinPlayersAdapter adapter;
    ListView list_ofplayers;
    ImageView img_cross;
    public GamePlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    // Inflate the layout for this com.footmate.fragment
        View rootView =inflater.inflate(R.layout.fragment_game_player, container, false);
        list_ofplayers = (ListView)rootView.findViewById(R.id.list_ofplayers);
        img_cross = (ImageView)rootView.findViewById(R.id.img_cross);
        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Home_Activity.class);
                startActivity(i);
                getActivity().finish();
            }
        });
        if (getArguments() != null){
            Bundle bundle = getArguments();
            matchid = bundle.getString("MATCHID");
        }
        arjoinplayer = new ArrayList<>();
        new GetListOfJoinPlayersAsyn().execute();
        return rootView;
}
    /*.............GetListOfJoinPlayers.............*/
    public class GetListOfJoinPlayersAsyn extends AsyncTask<String, String, String> {
        ProgressDialog pdial = new ProgressDialog(getActivity());
        String url = Constants.GetListOfJoinPlayers;
        String data = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdial.setMessage("Loading...");
            pdial.setCancelable(false);
            pdial.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                DefaultHttpClient client = new DefaultHttpClient();
                // Setup of the post call to the server
                HttpPost post = new HttpPost(url);

                post.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("MatchId",matchid);

                Log.e("GamePlayer Fragment", json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("GamePlayer Fragment",data.toString());
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
                    String listOfJoinMatchPlayers = new JSONObject(result).getString("listOfJoinMatchPlayers");
                    JSONArray arr = new JSONArray(listOfJoinMatchPlayers);
                    for (int i = 0;i < arr.length();i++){
                        JSONObject jsonObject = arr.getJSONObject(i);
                        JoinPlayersModal modal = new JoinPlayersModal();
                        modal.setDeviceId(jsonObject.getString("DeviceId"));
                        modal.setImageUrl(jsonObject.getString("ImageUrl"));
                        modal.setMatchId(jsonObject.getString("MatchId"));
                        modal.setPlayerId(jsonObject.getString("PlayerId"));
                        modal.setUserName(jsonObject.getString("UserName"));
                        arjoinplayer.add(modal);
                    }
                    adapter = new JoinPlayersAdapter(arjoinplayer, getActivity());
                    list_ofplayers.setAdapter(adapter);
                    Log.e("GamePlayer Fragment", response);
                } else {
                    Log.e("GamePlayer Fragment", response);
                }
                pdial.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
