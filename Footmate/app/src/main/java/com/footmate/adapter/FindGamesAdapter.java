package com.footmate.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.footmate.model.FindGamesModel;

/**
 * Created by patas tech on 3/4/2016.
 */
public class FindGamesAdapter extends BaseAdapter {
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;
    Context context;
    LayoutInflater inflater;
    ArrayList<FindGamesModel> arrList;

    public FindGamesAdapter(Context context, ArrayList<FindGamesModel> arrList){
        this.context = context;
        this.arrList = arrList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    @Override
    public int getCount() {
        return arrList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        CircleImageView profilepic;
        ImageView typeimg;
        TextView username, matchtime, fieldname, distance;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_findgames, null);
            holder.profilepic = (CircleImageView) convertView.findViewById(R.id.profilepic);
            holder.typeimg = (ImageView) convertView.findViewById(R.id.typeimg);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.matchtime = (TextView) convertView.findViewById(R.id.matchtime);
            holder.fieldname = (TextView) convertView.findViewById(R.id.fieldname);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FindGamesModel item = (FindGamesModel) getItem(position);

        holder.username.setText(item.getUserName());
        holder.fieldname.setText("@ "+item.getVenue());
        holder.distance.setText(item.getDistance() + " km");
        if(!item.getDateTimeOfMatch().equals("null")) {
            String[] separated = item.getDateTimeOfMatch().split(" ");
            holder.matchtime.setText(separated[1] + " " + separated[2]);
        }else{
            holder.matchtime.setText("");
        }
        if(item.getImageUrl().equals("null")){
            holder.profilepic.setImageResource(R.drawable.placeholder);
        }else {
            // show The Image in a ImageView
            imageLoader.displayImage(item.getImageUrl(), holder.profilepic, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    ///holder.person_img.setImageResource(R.drawable.user_img);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }

        if (item.getMatchType().equals("Match")) {
            holder.typeimg.setImageResource(R.drawable.findgames_match);
        } else if (item.getMatchType().equals("Freeplay")) {
            holder.typeimg.setImageResource(R.drawable.findgames_freeplay);
        } else if (item.getMatchType().equals("Practice")) {
            holder.typeimg.setImageResource(R.drawable.findgames_practice);
        }

        return convertView;
    }
}
