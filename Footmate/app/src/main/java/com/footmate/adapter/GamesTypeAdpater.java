package com.footmate.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.footmate.R;


import java.util.ArrayList;

import com.footmate.model.GamesTypeModal;

/**
 * Created by patas tech on 3/14/2016.
 */
public class GamesTypeAdpater extends BaseAdapter {
    public Context context;
    public ArrayList<GamesTypeModal> modal_List;


    public GamesTypeAdpater(Context context, ArrayList<GamesTypeModal> modal_List) {
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
        TextView text_Numbers;
        TextView text_Type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Typeface Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
        Typeface Ubuntu_Medium = Typeface.createFromAsset(context.getAssets(), "Ubuntu-M.ttf");
        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_horizontalgames, null);
            holder.text_Numbers = (TextView) convertView.findViewById(R.id.text_Numbers);
            holder.text_Type = (TextView) convertView.findViewById(R.id.text_Type);
            holder.text_Numbers.setTypeface(Ubuntu_Medium);
            holder.text_Type.setTypeface(Ubuntu_Regular);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GamesTypeModal modal = (GamesTypeModal) getItem(position);
        holder.text_Numbers.setText(modal.getGameNumbers());
        holder.text_Type.setText(modal.getGameType());
        return convertView;
    }
}
