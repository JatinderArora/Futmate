package com.footmate;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.footmate.adapter.SearchPlayerListAdapter;
import com.footmate.model.SearchPlayersModal;


public class SearchPlayerActivity extends ActionBarActivity {
    ListView playerslistview;
    TextView title;
    ImageView back;
    SearchPlayerListAdapter adapter;
    ArrayList<SearchPlayersModal> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_player);
        setWidgets();
        setTypeFace();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        arrayList = (ArrayList<SearchPlayersModal>) getIntent().getSerializableExtra("SearchArray");
        adapter = new SearchPlayerListAdapter(arrayList, this);
        playerslistview.setAdapter(adapter);

    }

    public void setWidgets(){
        playerslistview = (ListView)findViewById(R.id.playerslistview);
        title = (TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);

        arrayList = new ArrayList<>();
    }

    public void setTypeFace(){
        Typeface Ubuntu_Medium = Typeface.createFromAsset(getAssets(), "Ubuntu-M.ttf");
        Typeface Ubuntu_Bold = Typeface.createFromAsset(getAssets(), "Ubuntu-B.ttf");
        Typeface Ubuntu_Regular = Typeface.createFromAsset(getAssets(), "Ubuntu-R.ttf");
        title.setTypeface(Ubuntu_Bold);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
