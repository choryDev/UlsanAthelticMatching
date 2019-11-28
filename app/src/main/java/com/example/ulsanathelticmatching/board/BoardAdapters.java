package com.example.ulsanathelticmatching.board;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ulsanathelticmatching.R;

import java.util.List;

public class BoardAdapters extends BaseAdapter {
    private Context mContext = null;
    private int layout = 0;
    private List<BoardItem> boardItemslist = null;
    private LayoutInflater inflater = null;

    public BoardAdapters(Context c, int l, List<BoardItem> boardItemslist) {

        this.boardItemslist = boardItemslist;
        this.mContext = c;
        this.layout = l;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return boardItemslist.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return boardItemslist;
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
        ImageView image = (ImageView) convertView.findViewById(R.id.iv_avatar);
        TextView sport = (TextView) convertView.findViewById(R.id.tv_sport);
        TextView area = (TextView) convertView.findViewById(R.id.tv_area);
        TextView title = (TextView) convertView.findViewById(R.id.tv_title);
        TextView date = (TextView) convertView.findViewById(R.id.tv_date);
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);

        Glide
                .with(convertView)
                .load(Uri.parse(boardItemslist.get(position).img))
                .circleCrop()
                .placeholder(R.drawable.logo)
                .into(image);
        sport.setText(boardItemslist.get(position).sports);
        area.setText(boardItemslist.get(position).area);
        title.setText(boardItemslist.get(position).title);
        date.setText(boardItemslist.get(position).date);
        name.setText(boardItemslist.get(position).name);

        return convertView;
    }
}