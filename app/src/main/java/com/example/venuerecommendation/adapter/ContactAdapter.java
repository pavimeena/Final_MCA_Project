package com.example.venuerecommendation.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.venuerecommendation.R;
import com.example.venuerecommendation.model.Contact;


import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private List<Contact> contactList;

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public TextView cname,cnumber;
       // public CheckBox checkbox;

        public MyViewHolder(View view) {
            super(view);
            cname = (TextView) view.findViewById(R.id.textView3);
            cnumber = (TextView) view.findViewById(R.id.textView5);
           // checkbox = (CheckBox)view.findViewById(R.id.checkBox4);


        }
    }


    public ContactAdapter(List<Contact> contactList) {
        this.contactList = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.cname.setText(contact.getName());
        holder.cnumber.setText(contact.getNumber());
       // holder.checkbox.setChecked(contact.isCheck());

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}