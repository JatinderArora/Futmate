package com.footmate.adapter;

import android.content.Context;
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

import com.footmate.model.FollowerListModal;

/**
 * Created by sahil on 05-02-2016.
 */
public class FollowerList_Adapter extends BaseAdapter {
    public Context context;
    public ArrayList<FollowerListModal> modal_List;
    /*image upload*/
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;

    public FollowerList_Adapter(Context context, ArrayList<FollowerListModal> modal_List) {
        this.context = context;
        this.modal_List = modal_List;
    }

    @Override
    public int getCount() {
        return modal_List.size();
    }

    @Override
    public Object getItem(int position) {
        return modal_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        ImageView img_User;
        TextView text_UserName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Typeface Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_horizontalscroll, null);
            holder.img_User = (ImageView) convertView.findViewById(R.id.img_User);
            holder.text_UserName = (TextView) convertView.findViewById(R.id.text_UserName);
            holder.text_UserName.setTypeface(Ubuntu_Regular);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FollowerListModal modal = (FollowerListModal) getItem(position);
        /*set the followers image....*/
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

        if(modal.getImageUrl().equals("null")){
            holder.img_User.setImageResource(R.drawable.placeholder);
        }else {
            imageLoader.displayImage(modal.getImageUrl(), holder.img_User, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
//                profile_pic.setImageResource(R.drawable.user_img);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
        holder.text_UserName.setText(modal.getUserName());
        return convertView;
    }
}
