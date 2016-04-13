package com.footmate.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.footmate.R;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

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

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.footmate.adapter.FindGamesAdapter;
import com.footmate.model.FindGamesModel;


public class FindGames_Fragment extends Fragment {
    MapView mapView;
    GoogleMap map;
    ImageView distance, type;
    ListView findgamelistview;
    double Latitude, Longitude;
    SharedPreferences prefs;
    LatLng center;
    TextView cityname;
    double centerlat, centerlongi;
    String Adddress;
    ArrayList<FindGamesModel> arrayList, typearrlist;
    public FindGames_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.footmate.fragment
        View rootView = inflater.inflate(R.layout.fragment_findgames_, container, false);

        // setting views
        setView(rootView);

        prefs = getActivity().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, getActivity().MODE_PRIVATE);
        // user's current latitude and longitude
        Latitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LATITUDE, null));
        Longitude = Double.parseDouble(prefs.getString(MyPreferenceManager.HOME_LONGITUDE, null));
        arrayList = new ArrayList<>();
        typearrlist = new ArrayList<>();

        // setting map
        setMap(savedInstanceState);

        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance.setImageResource(R.drawable.distance);
                type.setImageResource(R.drawable.white_type);
                FindGamesAdapter adapter = new FindGamesAdapter(getActivity(), arrayList);
                findgamelistview.setAdapter(adapter);
            }
        });
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type.setImageResource(R.drawable.type);
                distance.setImageResource(R.drawable.white_distance);
                typearrlist.clear();
                for(int i = 0; i < arrayList.size(); i++){
                    if (arrayList.get(i).getMatchType().equals("Match")) {
                        typearrlist.add(arrayList.get(i));
                    }
                }
                for(int i = 0; i < arrayList.size(); i++){
                    if (arrayList.get(i).getMatchType().equals("Freeplay")) {
                        typearrlist.add(arrayList.get(i));
                    }
                }
                for(int i = 0; i < arrayList.size(); i++){
                    if (arrayList.get(i).getMatchType().equals("Practice")) {
                        typearrlist.add(arrayList.get(i));
                    }
                }
                Log.e("type array size",typearrlist.size() + "array size" + arrayList.size());
                FindGamesAdapter adapter = new FindGamesAdapter(getActivity(), typearrlist);
                findgamelistview.setAdapter(adapter);

            }
        });

        // executing find games API
        new GetFindGamesData().execute();
        return rootView;
    }

    public void setView(View rootView){
        // Gets the MapView from the XML layout
        mapView = (MapView) rootView.findViewById(R.id.mapview);
        findgamelistview = (ListView) rootView.findViewById(R.id.findgamelistview);
        distance = (ImageView) rootView.findViewById(R.id.distance);
        type = (ImageView) rootView.findViewById(R.id.type);
        cityname = (TextView) rootView.findViewById(R.id.cityname);
    }

    public void setMap(Bundle savedInstanceState){
        //   creates mapview
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitude), 12f);
        map.animateCamera(cameraUpdate);
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public synchronized void onCameraChange(CameraPosition cameraPosition) {
                center = map.getCameraPosition().target;
                Log.d("", "center Position of camera" + center.latitude + "," + center.longitude);
                centerlat = center.latitude;
                centerlongi = center.longitude;

                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(centerlat, centerlongi, 1);
                    String cityName = addresses.get(0).getAddressLine(0);
                    String stateName = addresses.get(0).getAddressLine(1);
                    String countryName = addresses.get(0).getAddressLine(2);
                    if(addresses.get(0).getLocality().equals("null")){
                        cityname.setText(addresses.get(0).getAddressLine(0));
                    }else {
                        cityname.setText(addresses.get(0).getLocality());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public class GetFindGamesData extends AsyncTask<Void, String, String>{
        String data;
        ProgressDialog p = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p.setMessage("Please Wait...");
            p.setCancelable(true);
            p.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            try {
                HttpPost post = new HttpPost(Constants.FindGames);
                post.setHeader("Content-type", "application/json");
                 JSONObject json = new JSONObject();
                json.put("PlayerId", prefs.getString(MyPreferenceManager.LOGIN_ID, null));
                json.put("Latitude", Latitude);
                json.put("Longitude", Longitude);

                Log.e("FIND GAMES JSON", json.toString());

                StringEntity entity = new StringEntity(json.toString());
                post.setEntity(entity);

                HttpResponse response = defaultHttpClient.execute(post);
                HttpEntity httpEntity = response.getEntity();
                data = EntityUtils.toString(httpEntity);

                Log.e("FIND GAMES API DATA", data);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (HttpException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null){
                p.dismiss();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(s);
                String res = jsonObject.getString("Responce");
                if(res.equals("1"))
                {
                    JSONArray array = jsonObject.getJSONArray("listOfMatches");
                    for (int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        FindGamesModel model = new FindGamesModel();
                        model.setAddress(obj.getString("Address"));
                        model.setCreatedDate(obj.getString("CreatedDate"));
                        model.setDateTimeOfMatch(obj.getString("DateTimeOfMatch"));
                        model.setDescription(obj.getString("Description"));
                        model.setDistance(obj.getString("Distance"));
                        model.setGameLevel(obj.getString("GameLevel"));
                        model.setGender(obj.getString("Gender"));
                        model.setImageUrl(obj.getString("ImageUrl"));
                        model.setLatitude(obj.getString("Latitude"));
                        model.setLongitude(obj.getString("Longitude"));
                        model.setMatchId(obj.getString("MatchId"));
                        model.setMatchType(obj.getString("MatchType"));
                        model.setNoOfPlayerToCompleteTeam("NoOfPlayerToCompleteTeam");
                        model.setPrivacyType(obj.getString("PrivacyType"));
                        model.setTeamSize(obj.getString("TeamSize"));
                        model.setTitle(obj.getString("Title"));
                        model.setUserName(obj.getString("UserName"));
                        model.setVenue(obj.getString("Venue"));
                        arrayList.add(model);
                    }
                    FindGamesAdapter adapter = new FindGamesAdapter(getActivity(), arrayList);
                    findgamelistview.setAdapter(adapter);
                    p.dismiss();
                }else {
                    p.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
