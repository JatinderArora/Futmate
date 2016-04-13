package com.footmate.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.footmate.R;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import com.footmate.model.ChatMessagesModal;
import com.footmate.helper.MyPreferenceManager;


/**
 * Created by patas tech on 4/8/2016.
 */
public class ChatMessagesAdapter extends BaseAdapter{
    ArrayList<ChatMessagesModal> arraylist;
    LayoutInflater inflater;
    Context context;
    ViewHolder holder = null;
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;
    String PlayerId;
    SharedPreferences prefs;
    public ChatMessagesAdapter(ArrayList<ChatMessagesModal> arraylist, Context context){
        this.arraylist = arraylist;
        this.context = context;
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
    }
    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder{
        LinearLayout othermsgboxlayout;
        CircleImageView user_pic;
        TextView username, user_msg;
        RelativeTimeTextView user_msg_time_past;
        RelativeLayout mymsgboxlayout;
        TextView my_msg;
        RelativeTimeTextView my_msg_time_past;
        LinearLayout joinstatuslayout;
        TextView date, time_joinstatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_messages_listitem, null);
            holder.othermsgboxlayout = (LinearLayout) convertView.findViewById(R.id.othermsgboxlayout);
            holder.user_pic = (CircleImageView) convertView.findViewById(R.id.user_pic);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.user_msg = (TextView) convertView.findViewById(R.id.user_msg);
            holder.user_msg_time_past = (RelativeTimeTextView) convertView.findViewById(R.id.user_msg_time_past);
            holder.mymsgboxlayout = (RelativeLayout) convertView.findViewById(R.id.mymsgboxlayout);
            holder.my_msg = (TextView) convertView.findViewById(R.id.my_msg);
            holder.my_msg_time_past = (RelativeTimeTextView) convertView.findViewById(R.id.my_msg_time_past);
            holder.joinstatuslayout = (LinearLayout) convertView.findViewById(R.id.joinstatuslayout);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.time_joinstatus = (TextView) convertView.findViewById(R.id.time_joinstatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChatMessagesModal modal = (ChatMessagesModal) getItem(position);
        PlayerId = prefs.getString(MyPreferenceManager.LOGIN_ID, null);
        if (modal.getMessage().contains("Join Match ") || modal.getMessage().contains("Left Match ")){
            holder.joinstatuslayout.setVisibility(View.VISIBLE);
            holder.mymsgboxlayout.setVisibility(View.GONE);
            holder.othermsgboxlayout.setVisibility(View.GONE);

            String[] str_array = modal.getDateTimeOfMessage().split(" ");
            String date = str_array[0];
            String time = str_array[1];
            String ampm = str_array[2];

            if(modal.getMessage().contains("Join Match ")){
                holder.time_joinstatus.setText(time+" "+ampm+" @"+modal.getPlayerName()+" is IN");
            }else if (modal.getMessage().contains("Left Match ")){
                holder.time_joinstatus.setText(time+" "+ampm+" @"+modal.getPlayerName()+" Left");
            }

            holder.date.setText(date);
        }else {
            if (modal.getPlayerId().equals(PlayerId)){
                holder.joinstatuslayout.setVisibility(View.GONE);
                holder.mymsgboxlayout.setVisibility(View.VISIBLE);
                holder.othermsgboxlayout.setVisibility(View.GONE);

                holder.my_msg.setText(modal.getMessage());

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date convertedDate = new Date();
                try {
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    convertedDate = dateFormat.parse(modal.getDateTimeOfMessage());
                    holder.my_msg_time_past.setReferenceTime(convertedDate.getTime());
                    Log.e("Chat" + convertedDate.getDate(), "convertedDate" + modal.getDateTimeOfMessage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                holder.joinstatuslayout.setVisibility(View.GONE);
                holder.mymsgboxlayout.setVisibility(View.GONE);
                holder.othermsgboxlayout.setVisibility(View.VISIBLE);

                if(modal.getImageUrl().equals("null") || modal.getImageUrl().equals("http://wuayo.seekarcctv.com/TGImages/NoImage")){
                    holder.user_pic.setImageResource(R.drawable.placeholder);
                }else {
                    imageLoader.displayImage(modal.getImageUrl(), holder.user_pic, options, new ImageLoadingListener() {
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

                holder.user_msg.setText(modal.getMessage());
                holder.username.setText(modal.getPlayerName());

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date convertedDate = new Date();
                try {
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    convertedDate = dateFormat.parse(modal.getDateTimeOfMessage());
                    holder.user_msg_time_past.setReferenceTime(convertedDate.getTime());
                    Log.e("Chat" + convertedDate.getDate(), "convertedDate" + modal.getDateTimeOfMessage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return convertView;
    }
}
