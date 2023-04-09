package com.example.venuerecommendation.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.venuerecommendation.R;
import com.example.venuerecommendation.model.events;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<events> Events;

    public RvAdapter(Context ctx, ArrayList<events> Events){

        inflater = LayoutInflater.from(ctx);
        this.Events = Events;
    }

    @Override
    public RvAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rv_one, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RvAdapter.MyViewHolder holder, int position) {

        Picasso.get().load(Events.get(position).getEventimage()).into(holder.iv);
        holder.date.setText(Events.get(position).getName());
        holder.eventname.setText(Events.get(position).getEventname());
        holder.category.setText(Events.get(position).getCategory());
        holder.suitablefor.setText(Events.get(position).getSuitablefor());
    }

    @Override
    public int getItemCount() {
        return Events.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView eventname, date, category,suitablefor ;
        ImageView iv;

        public MyViewHolder(View itemView) {
            super(itemView);

            eventname = (TextView) itemView.findViewById(R.id.textView14);
            date = (TextView) itemView.findViewById(R.id.textView18);
            category = (TextView) itemView.findViewById(R.id.textView19);
            suitablefor = (TextView) itemView.findViewById(R.id.textView22);
            iv = (ImageView) itemView.findViewById(R.id.iv);
        }

    }
}
