package com.example.ulsanathelticmatching.board;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class BoardAdapters extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
    private ArrayList<BoardItem> boardItems = new ArrayList<>();
    @Override
    public int getCount() {
        return boardItems.size();
    }

    public ArrayList<BoardItem> getBoardItems() {
        return boardItems;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
