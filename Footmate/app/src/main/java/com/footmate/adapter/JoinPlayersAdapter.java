package com.footmate.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.footmate.model.JoinPlayersModal;
import com.footmate.helper.MyPreferenceManager;


/**
 * Created by patas tech on 4/6/2016.
 */
public class JoinPlayersAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<JoinPlayersModal> arr;
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;
    String PlayerId, FollowerId;
    int pos;
    SharedPreferences prefs;
    ViewHolder holder = null;

    public JoinPlayersAdapter(ArrayList<JoinPlayersModal> arr, Context context) {
        this.context = context;
        this.arr = arr;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    class ViewHolder {
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

        final JoinPlayersModal modal = (JoinPlayersModal) getItem(position);

        if (modal.getImageUrl().equals("null") || modal.getImageUrl().equals("http://wuayo.seekarcctv.com/TGImages/NoImage")){
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
        holder.about.setText("");

        holder.follow_unfollow.setImageResource(R.drawable.search_following);


        holder.follow_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return convertView;
    }
}
