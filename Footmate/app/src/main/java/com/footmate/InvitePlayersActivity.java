package com.footmate;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class InvitePlayersActivity extends ActionBarActivity {
    Toolbar toolbar;
    ImageView img_toolbar_back;
    TextView inviteplayerstitle;
    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_players);
        Ubuntu_Medium = Typeface.createFromAsset(getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(getAssets(), "Ubuntu-R.ttf");
        // set the Toolbar....
        setToolBar();
    }
    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        img_toolbar_back = (ImageView) toolbar.findViewById(R.id.img_toolbar_back);
        inviteplayerstitle = (TextView) toolbar.findViewById(R.id.inviteplayerstitle);
        inviteplayerstitle.setTypeface(Ubuntu_Medium);
        img_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
