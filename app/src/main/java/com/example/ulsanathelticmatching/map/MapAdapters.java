package com.example.ulsanathelticmatching.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.GymModel;

import java.util.ArrayList;

public class MapAdapters extends BaseAdapter {
    //울산 체육관 정보를 담은 어뎁터
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<GymModel> sample;

    public MapAdapters(Context context, ArrayList<GymModel> data) {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public GymModel getItem(int position) {
        return sample.get(position);
    }

    @Override//리스트뷰의 자식 플래그먼트에 어레이리스트의 요소를 세팅
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.activity_map_items, null);

        TextView tv_gymName = (TextView)view.findViewById(R.id.gymName);
        TextView tv_gymPhoneNumber = (TextView)view.findViewById(R.id.gymPhoneNumber);
        TextView tv_gymAdress = (TextView)view.findViewById(R.id.gymAdress);

        tv_gymName.setText(sample.get(position).name);
        tv_gymAdress.setText(sample.get(position).address);

        return view;
    }
}