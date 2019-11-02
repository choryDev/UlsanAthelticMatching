package com.example.ulsanathelticmatching.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ulsanathelticmatching.R;

import java.util.ArrayList;

public class MainMenuItemAdapters extends BaseAdapter {
    private Context mContext = null;
    private int layout = 0;
    private ArrayList<MainMenuItem> data = null;
    private LayoutInflater inflater = null;

    public MainMenuItemAdapters(Context c, int l, ArrayList<MainMenuItem> d) {
        this.mContext = c;
        this.layout = l;
        this.data = d;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if(convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.Human_image);
        TextView name = (TextView) convertView.findViewById(R.id.Human_name);


        image.setImageBitmap(data.get(position).image);
        name.setText(data.get(position).name);

        return convertView;
    }
}