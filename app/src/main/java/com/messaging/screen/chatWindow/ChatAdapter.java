package com.messaging.screen.chatWindow;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.messaging.anushka.R;
import com.messaging.screen.chatWindow.model.Chat;
import com.messaging.screen.common.AppConstants;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private ArrayList<Chat> chatArrayList;
    private Context context;
    private LayoutInflater inflater;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public ChatAdapter(ArrayList<Chat> chatArrayList) {
        this.chatArrayList = chatArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        inflater= LayoutInflater.from(parent.getContext());
        if(viewType==VIEW_TYPE_MESSAGE_RECEIVED)
        return new ViewHolder(inflater.inflate(R.layout.chat_list_item,parent,false));
        else
            return new ViewHolder(inflater.inflate(R.layout.chat_list_item_sent,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        if(chatArrayList!=null && chatArrayList.size()>0)
        {

            holder.chatMessage.setText(chatArrayList.get(position).getMyMessage());
            holder.msgTime.setText(chatArrayList.get(position).getTimeInFormat());
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(chatArrayList.get(position).getStatus().equalsIgnoreCase(AppConstants.RECEIVED))
        {
            return VIEW_TYPE_MESSAGE_RECEIVED;

        }
        else
        {
            return VIEW_TYPE_MESSAGE_SENT;
        }
    }

    @Override
    public int getItemCount() {
        return chatArrayList!=null?chatArrayList.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout chat_view;
        TextView chatMessage;
        TextView msgTime;

        public ViewHolder(View itemView) {
            super(itemView);
            chat_view=itemView.findViewById(R.id.chat_view);
            chatMessage=itemView.findViewById(R.id.chatMsg);
            msgTime=itemView.findViewById(R.id.chatTime);

        }
    }
}
