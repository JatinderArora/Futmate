package com.footmate.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.footmate.CommentActivity;
import com.footmate.FeedDetailsActivity;
import com.footmate.R;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.utils.Constants;
import com.footmate.utils.Utilities;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import com.footmate.model.FeedListModal;


/**
 * Created by patas tech on 3/8/2016.
 */
public class FeedAdapter extends BaseAdapter {
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;
    LayoutInflater inflater;
    public ArrayList<FeedListModal> modal_List;
    public Context context;
    public String newFormat;
    public String MatchID;
    int pos;
    ViewHolder holder = null;
    public String LikeID;
    SharedPreferences myPrefs;
    SharedPreferences.Editor editor;
    public String TeamSizeArray[] = {"1v1", "2v2", "3v3", "4v4", "5v5", "6v6", "7v7", "8v8", "9v9", "10v10", "11v11"};
    public FeedAdapter(Context context, ArrayList<FeedListModal> modal_List) {
        this.context = context;
        this.modal_List = modal_List;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myPrefs = context.getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, context.MODE_PRIVATE);
        editor = myPrefs.edit();
        LikeID = myPrefs.getString(MyPreferenceManager.LOGIN_ID, null);
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

    public class ViewHolder{
        /*Create New Plan Widgets*/
        RelativeLayout layout_match_back, backtypeimg;
        ImageView img_logo_exchange;
        CircleImageView person_img;
        TextView text_username;
        TextView text_playstation;
        TextView text_matchtype;
        TextView match_date_time;
        TextView circle_text_spots;
        TextView below_text_spot;
        TextView circle_text_teamsize;
        TextView below_text_teamsize;
        ImageView circle_img_gender;
        TextView below_text_gender;
        TextView like_num;
        TextView like_text;
        TextView comment_num;
        TextView comment_text;
        ImageView img_like;
        TextView img_text_like;
        ImageView img_comment;
        TextView img_text_comment;
        ImageView img_share;
        TextView img_text_share;
        TextView text_soccer;
        LinearLayout layout_like, layout_comment, laout_share,layoutgender;
        RelativeTimeTextView text_total_time;

        /*Track Game Widgets.....*/
        TextView track_matchType,trackFieldName,track_GoalCount,txtGoal,track_AssistCount,txt_Assist,
                track_DistanceCount,txt_Distance,track_AverageCount,txt_Average,txt_Description,
                txt_myteam,txt_opponent,txt_circleBottom,track_Time;
        ImageView img_LogoFutmate;
        LinearLayout layout_Circle,layoutLikeCount,layoutCommentCount;
        RelativeLayout layout_track;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Typeface Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        Typeface Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");
        Typeface Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_feedlistview, null);
            holder = new ViewHolder();
            /*Initialize the Create New Plan widgets......*/
            holder.layout_match_back = (RelativeLayout) convertView.findViewById(R.id.layout_match_back);
            holder.backtypeimg = (RelativeLayout) convertView.findViewById(R.id.backtypeimg);
            holder.person_img = (CircleImageView) convertView.findViewById(R.id.person_img);
            holder.img_logo_exchange = (ImageView) convertView.findViewById(R.id.img_logo_exchange);

            holder.text_username = (TextView) convertView.findViewById(R.id.text_username);
            holder.text_playstation = (TextView) convertView.findViewById(R.id.text_playstation);
            holder.text_total_time = (RelativeTimeTextView) convertView.findViewById(R.id.text_total_time);
            holder.text_matchtype = (TextView) convertView.findViewById(R.id.text_matchtype);
            holder.match_date_time = (TextView) convertView.findViewById(R.id.match_date_time);
            holder.circle_text_spots = (TextView) convertView.findViewById(R.id.circle_text_spots);
            holder.circle_text_teamsize = (TextView) convertView.findViewById(R.id.circle_text_teamsize);
            holder.circle_img_gender = (ImageView) convertView.findViewById(R.id.circle_img_gender);
            holder.below_text_spot = (TextView) convertView.findViewById(R.id.below_text_spot);
            holder.below_text_teamsize = (TextView) convertView.findViewById(R.id.below_text_teamsize);
            holder.below_text_gender = (TextView) convertView.findViewById(R.id.below_text_gender);

            holder.like_num = (TextView) convertView.findViewById(R.id.like_num);//.................
            holder.like_text = (TextView) convertView.findViewById(R.id.like_text);

            holder.comment_num = (TextView) convertView.findViewById(R.id.comment_num);//...................
            holder.comment_text = (TextView) convertView.findViewById(R.id.comment_text);

            holder.img_like = (ImageView) convertView.findViewById(R.id.img_like);
            holder.img_text_like = (TextView) convertView.findViewById(R.id.img_text_like);

            holder.img_comment = (ImageView) convertView.findViewById(R.id.img_comment);
            holder.img_text_comment = (TextView) convertView.findViewById(R.id.img_text_comment);

            holder.img_share = (ImageView) convertView.findViewById(R.id.img_share);
            holder.img_text_share = (TextView) convertView.findViewById(R.id.img_text_share);

            holder.layout_like = (LinearLayout) convertView.findViewById(R.id.layout_like);
            holder.layoutLikeCount = (LinearLayout) convertView.findViewById(R.id.layoutLikeCount);
            holder.layout_comment = (LinearLayout) convertView.findViewById(R.id.layout_comment);
            holder.layoutCommentCount = (LinearLayout) convertView.findViewById(R.id.layoutCommentCount);
            holder.laout_share = (LinearLayout) convertView.findViewById(R.id.laout_share);

            holder.layoutgender = (LinearLayout) convertView.findViewById(R.id.layoutgender);

            holder.text_soccer = (TextView) convertView.findViewById(R.id.text_soccer);

           /*Initialize the Track Game Plan widgets......*/
            holder.track_matchType = (TextView) convertView.findViewById(R.id.track_matchType);
            holder.trackFieldName = (TextView) convertView.findViewById(R.id.trackFieldName);
            holder.track_GoalCount = (TextView) convertView.findViewById(R.id.track_GoalCount);
            holder.txtGoal = (TextView) convertView.findViewById(R.id.txtGoal);
            holder.track_AssistCount = (TextView) convertView.findViewById(R.id.track_AssistCount);
            holder.txt_Assist = (TextView) convertView.findViewById(R.id.txt_Assist);
            holder.track_DistanceCount = (TextView) convertView.findViewById(R.id.track_DistanceCount);
            holder.txt_Distance = (TextView) convertView.findViewById(R.id.txt_Distance);
            holder.track_AverageCount = (TextView) convertView.findViewById(R.id.track_AverageCount);
            holder.txt_Average = (TextView) convertView.findViewById(R.id.txt_Average);
            holder.txt_Description = (TextView) convertView.findViewById(R.id.txt_Description);
            holder.txt_myteam = (TextView) convertView.findViewById(R.id.txt_myteam);
            holder.txt_opponent = (TextView) convertView.findViewById(R.id.txt_opponent);
            holder.txt_circleBottom = (TextView) convertView.findViewById(R.id.txt_circleBottom);
            holder.track_Time = (TextView)convertView.findViewById(R.id.track_Time);

            holder.layout_Circle = (LinearLayout)convertView.findViewById(R.id.layout_Circle);
            holder.layout_track = (RelativeLayout)convertView.findViewById(R.id.layout_track);
            holder.img_LogoFutmate = (ImageView)convertView.findViewById(R.id.img_LogoFutmate);

            /*Set Create New Plan  widgets..TypeFace....*/
            holder.text_username.setTypeface(Ubuntu_Regular);
            holder.text_playstation.setTypeface(Ubuntu_Regular);
            holder.text_total_time.setTypeface(Ubuntu_Regular);
            holder.text_matchtype.setTypeface(Ubuntu_Medium);
            holder.match_date_time.setTypeface(Ubuntu_Bold);
            holder.circle_text_spots.setTypeface(Ubuntu_Regular);
            holder.circle_text_teamsize.setTypeface(Ubuntu_Regular);
            holder.text_soccer.setTypeface(Ubuntu_Medium);

            holder.like_num.setTypeface(Ubuntu_Regular);
            holder.like_text.setTypeface(Ubuntu_Regular);
            holder.comment_num.setTypeface(Ubuntu_Regular);
            holder.comment_text.setTypeface(Ubuntu_Regular);

            holder.below_text_spot.setTypeface(Ubuntu_Medium);
            holder.below_text_teamsize.setTypeface(Ubuntu_Medium);
            holder.below_text_gender.setTypeface(Ubuntu_Medium);

            holder.img_text_like.setTypeface(Ubuntu_Regular);
            holder.img_text_comment.setTypeface(Ubuntu_Regular);
            holder.img_text_share.setTypeface(Ubuntu_Regular);


           /*Set Track Game Plan  widgets..TypeFace....*/
            holder.track_matchType.setTypeface(Ubuntu_Medium);
            holder.trackFieldName.setTypeface(Ubuntu_Regular);
            holder.track_GoalCount.setTypeface(Ubuntu_Regular);
            holder.txtGoal.setTypeface(Ubuntu_Regular);
            holder.track_AssistCount.setTypeface(Ubuntu_Regular);
            holder.txt_Assist.setTypeface(Ubuntu_Regular);
            holder.track_DistanceCount.setTypeface(Ubuntu_Regular);
            holder.txt_Distance.setTypeface(Ubuntu_Regular);
            holder.track_AverageCount.setTypeface(Ubuntu_Regular);
            holder.txt_Average.setTypeface(Ubuntu_Regular);
            holder.txt_Description.setTypeface(Ubuntu_Regular);
            holder.txt_myteam.setTypeface(Ubuntu_Regular);
            holder.txt_opponent.setTypeface(Ubuntu_Regular);
            holder.txt_circleBottom.setTypeface(Ubuntu_Regular);
            holder.track_Time.setTypeface(Ubuntu_Regular);


            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        // set the Modal data into views .... and set all checks......
        final FeedListModal modal = (FeedListModal) getItem(position);

        //set all common text in views.......................................
        // show The Image in a ImageView
        setProfilePicWithAbout();
        if(modal.getImageUrl().equals("null") || modal.getImageUrl().equals("http://wuayo.seekarcctv.com/TGImages/NoImage")){
            holder.person_img.setImageResource(R.drawable.placeholder);
        }else {
            imageLoader.displayImage(modal.getImageUrl(), holder.person_img, options, new ImageLoadingListener() {
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
        if(!modal.getDateTimeOfMatch().equals("")) {
            String[] str_array = modal.getDateTimeOfMatch().split(" ");
            String date = str_array[0];
            String time = str_array[1];

//            String[] time_split = time.split(":");

            String[] date_array = date.split("/");
            String month = date_array[0];
            String day = date_array[1];

            String dayOfTheWeek = null;
            try {
                dayOfTheWeek = Utilities.getDayFeed(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.match_date_time.setText(dayOfTheWeek.toUpperCase() + " " + Utilities.getMonthString(month).toUpperCase() + " " + day + " // " + time + " " + str_array[2]);
        }else{
            holder.match_date_time.setText("");
        }
        // set the vanue below the date and time....
        holder.text_soccer.setText(modal.getVenue());

        // set the total time in widget .t..............
        // get the server time..........
        // get the the system current time.....
        // and at the last subtract both the time ....
        // convert into hours ...............and show the textview .........
        // using the UTC .....Time Format.....
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date convertedDate = new Date();
        try {
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            convertedDate = dateFormat.parse(modal.getCreatedDate());
            holder.text_total_time.setReferenceTime(convertedDate.getTime());
            Log.e("---------" + convertedDate.getDate(), "convertedDate" + modal.getCreatedDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // set user name
        holder.text_username.setText(modal.getUserName());

        // setting total like and total comment count
        holder.like_num.setText(modal.getTotalLike());
        holder.comment_num.setText(modal.getTotalComment());

        // setting like, unlike status
        if (modal.getLikeStatus().equals("Like")) {
            holder.img_text_like.setText("Unlike");
        } else if (modal.getLikeStatus().equals("Unlike")) {
            holder.img_text_like.setText("Like");
        }

        // like unlike apis.......

        holder.layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatchID = modal.getMatchId();
                pos = position;
                if (modal.getLikeStatus().equals("Like")) {
                    UNLikeAsync unLikeObj = new UNLikeAsync();
                    unLikeObj.execute();
                }
                if (modal.getLikeStatus().equals("Unlike")) {
                    LikeAsync likeObj = new LikeAsync();
                    likeObj.execute();
                }

            }
        });
        // LikeCount.....
        holder.layoutLikeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatchID = modal.getMatchId();
                pos = position;
                if (holder.img_text_like.getText().toString().equals("Like")) {
                    LikeAsync likeObj = new LikeAsync();
                    likeObj.execute();
                }
                if (holder.img_text_like.getText().toString().equals("Unlike")) {
                    UNLikeAsync unLikeObj = new UNLikeAsync();
                    unLikeObj.execute();
                }
            }
        });

        holder.layout_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(MyPreferenceManager.POSITION, position);
                editor.commit();
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("MATCHID", modal.getMatchId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        // comment count...
        holder.layoutCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt(MyPreferenceManager.POSITION, position);
                editor.commit();
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("MATCHID", modal.getMatchId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        final ViewHolder finalHolder2 = holder;
        holder.laout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Futmate Demo");
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });


       /*................................................................................................................*/
        // set the check......according to gameType....
        if (modal.getTypeOfPost().equals("game")){
            holder.layout_track.setVisibility(View.GONE);
            holder.layout_match_back.setVisibility(View.VISIBLE);

            // set the click event on listitems....................
            // click event onLayout.....................
            final ViewHolder finalHolder1 = holder;
            holder.layout_match_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newFormat = finalHolder1.match_date_time.getText().toString();
                    Intent intent = new Intent(context, FeedDetailsActivity.class);
                    intent.putExtra("USERNAME", modal.getUserName());
                    intent.putExtra("TITLE", modal.getTitle());
                    intent.putExtra("VANUE", modal.getVenue());
                    intent.putExtra("DESCRIPTION", modal.getDescription());
                    intent.putExtra("MATCHTYPE", modal.getMatchType());
                    intent.putExtra("DATETIME", newFormat);
                    intent.putExtra("SPOTS", modal.getNoOfPlayerToCompleteTeam());
                    intent.putExtra("GAMELEVEL", modal.getGameLevel());
                    intent.putExtra("GENDER", modal.getGender());
                    intent.putExtra("PICTURE", modal.getImageUrl());
                    intent.putExtra("JOINSTATUS",modal.getJoinStatus());
                    intent.putExtra("ORGANISERMODE",modal.getOrganizarMode());
                    intent.putExtra("MATCHID",modal.getMatchId());
                    intent.putExtra("LATITUDE",modal.getLatitude());
                    intent.putExtra("LONGITUDE",modal.getLongitude());
                    intent.putExtra("TYPEOFPOST", modal.getTypeOfPost());
                    intent.putExtra("ADDRESS",modal.getAddress());
                    intent.putExtra("DATETIMEOFMATCH", modal.getDateTimeOfMatch());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
            });

            if (modal.getOrganizarMode().equals("True")){
                /*set the match type text.......*/
                if (modal.getTeamSize().equals("1")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[0]);
                } else if (modal.getTeamSize().equals("2")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[1]);
                } else if (modal.getTeamSize().equals("3")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[2]);
                } else if (modal.getTeamSize().equals("4")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[3]);
                } else if (modal.getTeamSize().equals("5")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[4]);
                } else if (modal.getTeamSize().equals("6")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[5]);
                } else if (modal.getTeamSize().equals("7")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[6]);
                } else if (modal.getTeamSize().equals("8")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[7]);
                } else if (modal.getTeamSize().equals("9")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[8]);
                } else if (modal.getTeamSize().equals("10")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[9]);
                } else if (modal.getTeamSize().equals("11")) {
                    holder.text_matchtype.setText(modal.getMatchType() + " " + TeamSizeArray[10]);
                }

                // set the Logo and Backgroud Image According to Match type.....
                holder.img_logo_exchange.setVisibility(View.VISIBLE);
                if (modal.getMatchType().equals("Match")) {
                    holder.img_logo_exchange.setImageResource(R.drawable.feed_ground_icon);
                    holder.backtypeimg.setBackgroundResource(R.drawable.match_bg);
                } else if (modal.getMatchType().equals("Freeplay")) {
                    holder.img_logo_exchange.setImageResource(R.drawable.post_match_ico);
                    holder.backtypeimg.setBackgroundResource(R.drawable.freeplay_match_bg);
                } else if (modal.getMatchType().equals("Practice")) {
                    holder.img_logo_exchange.setImageResource(R.drawable.run_user);
                    holder.backtypeimg.setBackgroundResource(R.drawable.match_img3);
                }

                // set the Gender According to the Apis Data......
                holder.circle_img_gender.setVisibility(View.VISIBLE);
                holder.layoutgender.setVisibility(View.VISIBLE);
                if (modal.getGender().equalsIgnoreCase("Male") ) {
                    holder.circle_img_gender.setImageResource(R.drawable.male_s);
                } else if (modal.getGender().equalsIgnoreCase("Female")) {
                    holder.circle_img_gender.setImageResource(R.drawable.female_s);
                } else if (modal.getGender().equalsIgnoreCase("Co Ed")) {
                    holder.circle_img_gender.setImageResource(R.drawable.co_ed_s);
                }

                holder.below_text_gender.setVisibility(View.VISIBLE);

                // setting no. of spots
                holder.below_text_spot.setVisibility(View.VISIBLE);
                holder.circle_text_spots.setVisibility(View.VISIBLE);
                holder.circle_text_spots.setText(modal.getNoOfPlayerToCompleteTeam());

                // setting team size
                holder.circle_text_teamsize.setVisibility(View.VISIBLE);
                holder.below_text_teamsize.setVisibility(View.VISIBLE);
                holder.circle_text_teamsize.setText(modal.getGameLevel());

                holder.text_playstation.setText("Shared a plan in " + modal.getCity());

            }else if(modal.getOrganizarMode().equals("False")) {
                holder.text_matchtype.setText(modal.getMatchType());
                holder.backtypeimg.setBackgroundResource(R.color.app_color);
                holder.img_logo_exchange.setVisibility(View.GONE);
                holder.layoutgender.setVisibility(View.GONE);
                holder.circle_text_spots.setVisibility(View.GONE);
                holder.circle_text_teamsize.setVisibility(View.GONE);
                holder.below_text_gender.setVisibility(View.GONE);
                holder.below_text_spot.setVisibility(View.GONE);
                holder.below_text_teamsize.setVisibility(View.GONE);

                holder.text_playstation.setText("Shared a plan in " + modal.getCity());
            }
        }else if (modal.getTypeOfPost().equals("track")){
            holder.layout_track.setVisibility(View.VISIBLE);
            holder.layout_match_back.setVisibility(View.GONE);

            // set the click event on listitems....................
            // click event onLayout.....................
            holder.layout_track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            holder.text_playstation.setText("Played in " + modal.getCity());

            if (modal.getImageName().equals("") || modal.getImageName().equals("false"))
            {
                holder.img_LogoFutmate.setVisibility(View.GONE);
                holder.layout_track.setBackgroundColor(context.getResources().getColor(R.color.app_color));
            }else{
                holder.img_LogoFutmate.setVisibility(View.VISIBLE);
                imageLoader.displayImage(Constants.Load_Image_Url + modal.getImageName(), holder.img_LogoFutmate, options, new ImageLoadingListener() {
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

            /*set the match type text.......*/
            if (modal.getTeamSize().equals("1")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[0]);
            } else if (modal.getTeamSize().equals("2")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[1]);
            } else if (modal.getTeamSize().equals("3")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[2]);
            } else if (modal.getTeamSize().equals("4")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[3]);
            } else if (modal.getTeamSize().equals("5")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[4]);
            } else if (modal.getTeamSize().equals("6")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[5]);
            } else if (modal.getTeamSize().equals("7")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[6]);
            } else if (modal.getTeamSize().equals("8")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[7]);
            } else if (modal.getTeamSize().equals("9")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[8]);
            } else if (modal.getTeamSize().equals("10")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[9]);
            } else if (modal.getTeamSize().equals("11")) {
                holder.track_matchType.setText(modal.getMatchType() + " " + TeamSizeArray[10]);
            }

            holder.trackFieldName.setText(modal.getFieldName());
            holder.track_GoalCount.setText(modal.getGoal());
            holder.track_AssistCount.setText(modal.getAssists());
            Double dist = Double.parseDouble(modal.getDistance());
            Double avgspeed = Double.parseDouble(modal.getAvgSpeed());
            holder.track_DistanceCount.setText(roundTwoDecimals(dist)+" km");
            holder.track_AverageCount.setText(roundTwoDecimals(avgspeed)+ " km/h");
            holder.txt_Description.setText(modal.getDescription());


            if (modal.getOrganizarMode().equals("True")){
                holder.layout_Circle.setVisibility(View.VISIBLE);
                holder.track_Time.setVisibility(View.GONE);
                holder.txt_myteam.setText(modal.getMyTeam());
                holder.txt_opponent.setText(modal.getOpponent());
            }else if(modal.getOrganizarMode().equals("False")){
                holder.track_Time.setVisibility(View.VISIBLE);
                holder.layout_Circle.setVisibility(View.GONE);
                holder.track_Time.setText(modal.getTime());
            }
        }


        return convertView;
    }

    // set the Post Profile Image......
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

    /*.............Like the Match.............*/
    public class LikeAsync extends AsyncTask<String, String, String> {
        ProgressDialog pdial = new ProgressDialog(context);
        String url = Constants.LikeMatch;
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
                json.put("MatchId", MatchID);
                json.put("LikerId", LikeID);

                Log.e("FeedAspate", "Like json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e("Feed Adpater", " Response Code Feed Adpater Like............. = " + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("Feed Adapter", "Like...." + data.toString());
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
                String response = new JSONObject(result).getString("Responce");
                    int li = Integer.parseInt(modal_List.get(pos).getTotalLike()) + 1;
                    modal_List.get(pos).setTotalLike(li+"");
                    modal_List.get(pos).setLikeStatus("Like");
                    notifyDataSetChanged();
                    Log.e("FeedAdpater....", "Like Plan success");

                pdial.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*.............UnLike the Match.............*/
    public class UNLikeAsync extends AsyncTask<String, String, String> {
        ProgressDialog pdial = new ProgressDialog(context);
        String url = Constants.UnLikeMatch;
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
                json.put("MatchId", MatchID);
                json.put("UnLikerId", LikeID);

                Log.e("FeedAspate", "UNLike json......." + json.toString());

                StringEntity se = new StringEntity(json.toString());
                post.setEntity(se);

                // Here we'll receive the response.
                HttpResponse response;
                response = client.execute(post);
                int code = response.getStatusLine().getStatusCode();
                Log.e("Feed Adpater", " Response Code Feed Adpater UNLike............. = " + code);
                HttpEntity entity = response.getEntity();
                data = EntityUtils.toString(entity);
                Log.e("Feed Adapter", "UNLike...." + data.toString());
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
                String response = new JSONObject(result).getString("Responce");
                    int unli = Integer.parseInt(modal_List.get(pos).getTotalLike()) - 1;
                    modal_List.get(pos).setTotalLike(unli+"");
                    modal_List.get(pos).setLikeStatus("UnLike");
                    notifyDataSetChanged();
                    Log.e("FeedAdpater....", "UNLike Plan success");

                pdial.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

}
