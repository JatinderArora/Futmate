package com.footmate.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.footmate.R;

public class InviteFriends_Fragment extends Fragment {
    private Typeface Ubuntu_Medium;
    private Typeface Ubuntu_Bold;
    private Typeface Ubuntu_Regular;
    public TextView text_top_text,_textimg_number,circle_below_text,text_invitefriends;
    public InviteFriends_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.footmate.fragment
        View rootView = inflater.inflate(R.layout.fragment_invite_friends_, container, false);
        //set typeface...for external font families...
        setTypeface();
        text_top_text = (TextView)rootView.findViewById(R.id.text_top_text);
        _textimg_number = (TextView)rootView.findViewById(R.id._textimg_number);
        circle_below_text = (TextView)rootView.findViewById(R.id.circle_below_text);
        text_invitefriends = (TextView)rootView.findViewById(R.id.text_invitefriends);
        text_top_text.setTypeface(Ubuntu_Bold);
        _textimg_number.setTypeface(Ubuntu_Medium);
        circle_below_text.setTypeface(Ubuntu_Medium);
        text_invitefriends.setTypeface(Ubuntu_Regular);

        // set click listner......
        setClickListner();




        return rootView;

    }

    private void setClickListner() {
        text_invitefriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Futmate Invite the Friends");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    private void setTypeface() {
        Ubuntu_Medium = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-M.ttf");
        Ubuntu_Bold = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-B.ttf");
        Ubuntu_Regular = Typeface.createFromAsset(getActivity().getAssets(), "Ubuntu-R.ttf");
    }


}
