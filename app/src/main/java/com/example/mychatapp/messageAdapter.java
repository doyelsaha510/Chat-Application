package com.example.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private Context mContext;
    private List<Chat> mchat;
    private String imageUrl;
    FirebaseUser firebaseUser;

    public messageAdapter(Context mContext, List<Chat> mchat, String imageUrl) {
        this.mContext = mContext;
        this.mchat = mchat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public messageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==MSG_TYPE_RIGHT)
        {
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new messageAdapter.ViewHolder(view);
        }
        else
        {
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new messageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull messageAdapter.ViewHolder holder, int position) {

        Chat chat=mchat.get(position);
        holder.show_message.setText(chat.getMessage());

        if(imageUrl.equals("default"))
        {
            holder.profile_image.setImageResource(R.drawable.defaultimage);
        }
        else
        {
            Picasso.get().load(imageUrl).placeholder(R.drawable.defaultimage).into(holder.profile_image);
        }



    }


    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mchat.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
