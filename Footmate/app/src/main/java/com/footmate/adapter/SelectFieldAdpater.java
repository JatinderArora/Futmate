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

import com.footmate.model.SelectListModal;

/**
 * Created by sahil on 08-02-2016.
 */
public class SelectFieldAdpater extends BaseAdapter {
    public Context context;
    public ArrayList<SelectListModal> modal_List;

    public SelectFieldAdpater(Context context, ArrayList<SelectListModal> modal_List) {
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
        TextView texttop;
        TextView textbottom;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Typeface Ubuntu_Regular = Typeface.createFromAsset(context.getAssets(), "Ubuntu-R.ttf");
        Typeface Ubuntu_Bold = Typeface.createFromAsset(context.getAssets(), "Ubuntu-B.ttf");

        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_selectfield_list, null);

            holder.texttop = (TextView) convertView.findViewById(R.id.txt_vanue);
            holder.textbottom = (TextView) convertView.findViewById(R.id.txt_address);
            holder.texttop.setTypeface(Ubuntu_Bold);
            holder.textbottom.setTypeface(Ubuntu_Regular);



            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SelectListModal modal = (SelectListModal) getItem(position);

        holder.texttop.setText(modal.getVanue());
        holder.textbottom.setText(modal.getAddress());


        return convertView;
    }


}
