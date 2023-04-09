package com.example.venuerecommendation.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.venuerecommendation.R;
import com.example.venuerecommendation.model.Group;


import java.util.List;

/**
 * Created by Belal on 11/9/2015.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private List<Group> groupList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);

        }
    }


    public CardAdapter(List<Group> groupList) {
        this.groupList = groupList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.textView.setText(group.getGname());

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}