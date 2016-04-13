package com.footmate.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.footmate.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.footmate.model.CommentListModal;

/**
 * Created by d_RaMaN on 2/16/2016.
 */
public class CommetListAdapter extends BaseAdapter {
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;
    Context context;
    ViewHolder holder = null;
    ArrayList<CommentListModal> modalList;
    public CommetListAdapter(Context context, ArrayList<CommentListModal> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public int getCount() {
        return modalList.size();
    }

    @Override
    public Object getItem(int position) {
        return modalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public class ViewHolder{
        CircleImageView profilepic;
        TextView username;
        TextView comment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Typeface Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
        Typeface Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_commentlist, null);
            holder.profilepic = (CircleImageView) convertView.findViewById(R.id.profilepic);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            holder.username.setTypeface(Ubuntu_Bold);
            holder.comment.setTypeface(Ubuntu_Regular);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CommentListModal modal = (CommentListModal) getItem(position);

        holder.username.setText(modal.getPlayerName());
        holder.comment.setText(modal.getComment());

        // show The Image in a ImageView
        setProfilePicWithAbout();
        if(modal.getImageUrl().equals("null") || modal.getImageUrl().equals("http://wuayo.seekarcctv.com/TGImages/NoImage")){
            holder.profilepic.setImageResource(R.drawable.placeholder);
        }else {
            imageLoader.displayImage(modal.getImageUrl(), holder.profilepic, options, new ImageLoadingListener() {
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


        return convertView;
    }
    private void setProfilePicWithAbout() {
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

}
