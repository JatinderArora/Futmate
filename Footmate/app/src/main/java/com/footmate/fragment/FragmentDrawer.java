package com.footmate.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.footmate.HelpAndSupportActivity;
import com.footmate.NotificationActivity;
import com.footmate.Profile_Activity;
import com.footmate.R;
import com.footmate.SettingsActivity;
import com.footmate.helper.MyPreferenceManager;
import com.footmate.model.NavDrawerItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import com.footmate.adapter.NavigationDrawerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();
    private RecyclerView recyclerView;
    CircleImageView drawerUserImage;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] Titles = null;
    private static int[] Icons = {};
    private FragmentDrawerListener drawerListener;
    SharedPreferences prefs;
    String url;
    List<NavDrawerItem> data = new ArrayList<NavDrawerItem>();
    ImageLoader imageLoader = null;
    private DisplayImageOptions options;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // drawer titiles and icons
        Titles = getResources().getStringArray(R.array.drawer_titles);
        Icons = getResources().getIntArray(R.array.nav_drawer_icons);
        prefs = getActivity().getSharedPreferences(MyPreferenceManager.FOOTMATE_PREFS, getActivity().MODE_PRIVATE);
        url = prefs.getString(MyPreferenceManager.IMAGE_URL, null);
        imageLoader = ImageLoader.getInstance(); // Get singleton instance
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));

        for ( int i = 0 ; i < Icons.length ; i++){
            Resources res = getResources();
            NavDrawerItem navItem = new NavDrawerItem();
            // getting icons from the String.xml
            TypedArray icons = res.obtainTypedArray(R.array.nav_drawer_icons);
            Drawable drawable = icons.getDrawable(i);
            navItem.setTitle(Titles[i]);
            navItem.setIcons(drawable);
            data.add(navItem);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

       // txt = (TextView) layout.findViewById(R.id.txt);
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        drawerUserImage = (CircleImageView) layout.findViewById(R.id.profile_pic);
        Log.e("Image Url FROM PREFS", url);
        if(url.equals("null")){
            drawerUserImage.setImageResource(R.drawable.placeholder);
        }else {
            imageLoader.displayImage(url, drawerUserImage, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    drawerUserImage.setImageResource(R.drawable.user_img);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
        recyclerView.setHasFixedSize(true);
        adapter = new NavigationDrawerAdapter(getActivity(), data);
        recyclerView.setAdapter(adapter);

        // set the divider to recyclarview....
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    mDrawerLayout.closeDrawer(containerView);
                    Home_Fragment.setPostion(0);
                } else if (position == 1) {
                    mDrawerLayout.closeDrawer(containerView);
                    Intent intent = new Intent(getActivity(),Profile_Activity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    mDrawerLayout.closeDrawer(containerView);
//                    ((Home_Activity)getActivity()).displayView(2);
                    Home_Fragment.setPostion(2);
                } else if (position == 3) {
                    mDrawerLayout.closeDrawer(containerView);
                    Home_Fragment.setPostion(1);
//                    ((Home_Activity)getActivity()).displayView(1);
                } else if (position == 4) {
                    mDrawerLayout.closeDrawer(containerView);
                    Intent intent = new Intent(getActivity(),NotificationActivity.class);
                    startActivity(intent);
                } else if (position == 5) {
                    mDrawerLayout.closeDrawer(containerView);
                    Intent intent = new Intent(getActivity(),HelpAndSupportActivity.class);
                    startActivity(intent);
                } else if (position == 6) {
                    mDrawerLayout.closeDrawer(containerView);
                    Intent intent = new Intent(getActivity(),SettingsActivity.class);
                    startActivity(intent);
                } else {

                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;


        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        // change the toolbar mDrawerToggle button........
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.drawable.menu_button);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
    //.............Set the Divider for RecyclerView ...............
    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
