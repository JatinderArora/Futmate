package com.footmate.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.footmate.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.footmate.model.SearchPlayersModal;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Constants;

/**
 * Created by patas tech on 3/31/2016.
 */
public class SearchPlayerListAdapter extends BaseAdapter{
    LayoutInflater inflater;
    Context context;
    ArrayList<SearchPlayersModal> arr;
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;
    String PlayerId, FollowerId;
    int pos;
    SharedPreferences prefs;
    ViewHolder holder = null;
    public SearchPlayerListAdapter(ArrayList<SearchPlayersModal> arr, Context context){
        this.context = context;
        this.arr = arr;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        prefs = context.getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
        FollowerId = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
    }
    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder{
        CircleImageView user_pic;
        ImageView follow_unfollow;
        TextView username, about;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Typeface Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.searchplayer_listitem, null);
            holder.user_pic = (CircleImageView) convertView.findViewById(R.id.user_pic);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.about = (TextView) convertView.findViewById(R.id.about);
            holder.follow_unfollow = (ImageView) convertView.findViewById(R.id.follow_unfollow);
            holder.username.setTypeface(Ubuntu_Medium);
            holder.about.setTypeface(Ubuntu_Medium);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SearchPlayersModal modal = (SearchPlayersModal) getItem(position);
        if(modal.getImageUrl().equals("null")){
            holder.user_pic.setImageResource(R.drawable.placeholder);
        }else {
            imageLoader.displayImage(modal.getImageUrl(), holder.user_pic, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

        holder.username.setText(modal.getUserName());
        holder.about.setText(modal.getAbout());

        if (modal.getFollowStatus().equals("UnFollow")){
            holder.follow_unfollow.setImageResource(R.drawable.search_follow);
        }else {
            holder.follow_unfollow.setImageResource(R.drawable.search_following);
        }

        holder.follow_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerId = modal.getId();
                pos = position;
                if(modal.getFollowStatus().equals("UnFollow")){
                    new FollowAsync().execute();
                }else {
                    new UnFollowAsync().execute();
                }
            }
        });

        return convertView;
    }

    class FollowAsync extends AsyncTask<Void, String, String>{
        ProgressDialog progressDialog = new ProgressDialog(context);
        String res;
        String url = Constants.FollowAPI;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("PlayerId",PlayerId);
                json.put("FollowerId", FollowerId);

                Log.e("Follow API json", json.toString());

                StringEntity stringEntity = new StringEntity(json.toString());
                httpPost.setEntity(stringEntity);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                int code = httpResponse.getStatusLine().getStatusCode();
                Log.e("Follow response code", code+"");

                HttpEntity entity = httpResponse.getEntity();
                res = EntityUtils.toString(entity);
                Log.e("Follow response data", res);
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

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null){
                return;
            }
            try {
                String response = new JSONObject(s).getString("Responce");
                if (response.equals("1")){
                    arr.get(pos).setFollowStatus("Follow");
                    notifyDataSetChanged();
                }else {

                }
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class UnFollowAsync extends AsyncTask<Void, String, String>{
        ProgressDialog progressDialog = new ProgressDialog(context);
        String res;
        String url = Constants.UnFollowAPI;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Content-type", "application/json");

                JSONObject json = new JSONObject();
                json.put("PlayerId",PlayerId);
                json.put("FollowerId", FollowerId);

                Log.e("UnFollow API json", json.toString());

                StringEntity stringEntity = new StringEntity(json.toString());
                httpPost.setEntity(stringEntity);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                int code = httpResponse.getStatusLine().getStatusCode();
                Log.e("UnFollow response code", code+"");

                HttpEntity entity = httpResponse.getEntity();
                res = EntityUtils.toString(entity);
                Log.e("UnFollow response data", res);
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

            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s == null){
                return;
            }
            try {
                String response = new JSONObject(s).getString("Responce");
                if (response.equals("1")){
                    arr.get(pos).setFollowStatus("UnFollow");
                    notifyDataSetChanged();
                }else {

                }
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
