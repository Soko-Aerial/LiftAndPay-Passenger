package com.example.liftandpay_passenger.AVR_Activities.Chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_passenger.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.messageViewHolder>{

    private Context context;
    private int theViewtype;
    private ArrayList<MessageModel> messageModels;

    public MessageAdapter(ArrayList<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }


    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(theViewtype == 2)
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_layout_chatsent,parent,false);
        else
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_layout_chatreceived,parent,false);

        return new messageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.messageViewHolder holder, int position) {
        theViewtype = messageModels.get(position).getvType();

        TextView msgTxt = holder.itemView.findViewById(R.id.MsgTxt);
        msgTxt.setText(messageModels.get(position).getMessage());
    }



    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public static class messageViewHolder extends RecyclerView.ViewHolder {
        public messageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}