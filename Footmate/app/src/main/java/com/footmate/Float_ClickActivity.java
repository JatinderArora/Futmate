package com.footmate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Float_ClickActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = Float_ClickActivity.this;
    private String TAG = "Float_ClickActivity";

    private Typeface Ubuntu_Regular;

    private TextView text_game, text_findgame, text_newplan;
    private ImageView top_button;
    private LinearLayout layout_trackgame, layout_newplan, layout_findgame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float__click);
        getSupportActionBar().hide();

        //set typeface...for external font families...
        setTypeface();
        //set widget ids and their typeface....
        setWidgetIDSWithTypeface();
        // click listner of views
        setOnCLickListner();

    }

    private void setTypeface() {
        Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
    }

    // all views click listner......
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.top_button) {
            Intent intent = new Intent(context,Home_Activity.class);
            startActivity(intent);
//            overridePendingTransition(R.xml.top_bottom
//                    ,0);
        }
        if (view.getId() == R.id.layout_trackgame) {

        }
        if (view.getId() == R.id.layout_newplan) {
            Intent intent = new Intent(context, Start_New_PlanActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.layout_findgame) {

        }
    }


    private void setWidgetIDSWithTypeface() {
        top_button = (ImageView) findViewById(R.id.top_button);
        text_game = (TextView) findViewById(R.id.text_game);
        text_findgame = (TextView) findViewById(R.id.text_findgame);
        text_newplan = (TextView) findViewById(R.id.text_newplan);

        // set setTypeface
        text_game.setTypeface(Ubuntu_Regular);
        text_findgame.setTypeface(Ubuntu_Regular);
        text_newplan.setTypeface(Ubuntu_Regular);

        // linear layout ids...

        layout_trackgame = (LinearLayout) findViewById(R.id.layout_trackgame);
        layout_newplan = (LinearLayout) findViewById(R.id.layout_newplan);
        layout_findgame = (LinearLayout) findViewById(R.id.layout_findgame);


    }

    private void setOnCLickListner() {
        layout_trackgame.setOnClickListener(this);
        layout_newplan.setOnClickListener(this);
        text_findgame.setOnClickListener(this);
        top_button.setOnClickListener(this);
    }

}
