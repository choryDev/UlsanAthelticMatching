package com.example.ulsanathelticmatching.board;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.ulsanathelticmatching.R;
import com.example.ulsanathelticmatching.model.BoardItem;

import java.util.List;

public class BoardAdapters extends BaseAdapter { //게시글을 연결하는 어뎁터
    private Context mContext = null; //액티비티로 부터 받아온 객체를 담을 변수
    private int layout = 0;
    private List<BoardItem> boardItemslist = null; //firebase Db로 부터 받아온 객체를 어레이 리스트에 담는다
    private LayoutInflater inflater = null; //아이템을 인플리먼트로 띄우기 위한 변수
    private String area,sport;

    public BoardAdapters(Context c, int l, List<BoardItem> list) {
        this.boardItemslist = list;
        this.mContext = c;
        this.layout = l;
        this.inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void selectSports(List<BoardItem> list) {
        this.boardItemslist = list;
            notifyDataSetChanged();
    }

    public void selectArea(List<BoardItem> list) {
        this.boardItemslist = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return boardItemslist.size();
    }

    @Override
    public BoardItem getItem(int position) {
        // TODO Auto-generated method stub
        return boardItemslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //리스트 뷰 안의 플래그먼트를 세팅하는 함수
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