package com.footmate;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


public class FindYourFriendsActivity extends ActionBarActivity {
    Toolbar toolbar;
    EditText search_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_your_friends);
        setTopToolBar();
    }

    private void setTopToolBar() {
        // default toolbar firsttime....
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        search_view = (EditText)toolbar.findViewById(R.id.search_view);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.organizer_cross);
        // default toolbar click listner....
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        search_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))|| (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    String Searchdata = search_view.getText().toString();
                    Log.d("searchdata", Searchdata);
                    if (Searchdata != null && !Searchdata.equals("")) {

                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_your_friends, menu);
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
