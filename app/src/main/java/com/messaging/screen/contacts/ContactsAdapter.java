package com.messaging.screen.contacts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messaging.anushka.R;
import com.messaging.screen.chatWindow.ChatActivity;
import com.messaging.screen.common.AppConstants;
import com.messaging.screen.contacts.model.MessagingUser;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    private ArrayList<MessagingUser> contactsList;
    private LayoutInflater inflater;
    private Context context;

    public ContactsAdapter(ArrayList<MessagingUser> contactsList, Activity activity) {
        this.contactsList = contactsList;
        this.context=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater=LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.contact_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if(contactsList!=null)
        {
            holder.contactName.setText(contactsList.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, ChatActivity.class);
                    intent.putExtra(AppConstants.CHAT_PERSON_NUMBER,contactsList.get(position).getPhoneNumber());
                    intent.putExtra(AppConstants.CHAT_PERSON_NAME,contactsList.get(position).getName());
                    context.startActivity(intent);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return contactsList!=null?contactsList.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView contactName;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName=itemView.findViewById(R.id.contact_name);
        }
    }
}
