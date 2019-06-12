package com.example.mychatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.Viewholder> {
    private Context mcontext;
    private List<Users> mUsers;

    public userAdapter(Context mcontext, List<Users> mUsers) {
        this.mcontext = mcontext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_single_layout, parent, false);
        return new userAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Users users = mUsers.get(position);
        holder.username.setText(users.getName());
        if(users.getImage().equals("default"))
        {
            holder.circleImageView.setImageResource(R.drawable.defaultimage);
        }
        else
        {
            Picasso.get().load(users.getImage()).placeholder(R.drawable.defaultimage).into(holder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {
        TextView username;
        CircleImageView circleImageView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            circleImageView = itemView.findViewById(R.id.profile_image);
        }
    }
}
