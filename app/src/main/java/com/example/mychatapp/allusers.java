package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class allusers extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView userlist;
private  DatabaseReference databaseReference;
    FirebaseRecyclerOptions<Users> options;
    FirebaseRecyclerAdapter<Users,UsersViewHolder>adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusers);
        toolbar=findViewById(R.id.alluser_toolbar);
        userlist=findViewById(R.id.userlist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(databaseReference, Users.class)
                        .build();
        adapter=new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, final int i, @NonNull Users users) {
                usersViewHolder.setDisplayName(users.getName());
                usersViewHolder.setStatus(users.getStatus());
          //      usersViewHolder.setuserid(users.getId());
                usersViewHolder.setImage(getApplicationContext(),users.getImage());

                usersViewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_userid=getRef(i).getKey();
                        Intent profileintent=new Intent(allusers.this,ProfileActivity.class);
                        profileintent.putExtra("visit_userid",visit_userid);
                        startActivity(profileintent);
                    }
                });

            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);

                return new UsersViewHolder(view);
            }
        };
        userlist.setLayoutManager(new LinearLayoutManager(allusers.this));
        adapter.startListening();
        userlist.setAdapter(adapter);

    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mview=itemView;
        }
        public void setDisplayName(String name)
        {
            TextView usernameView=(TextView)mview.findViewById(R.id.username);
            usernameView.setText(name);
        }
        public void setStatus(String status)
        {
            TextView userStatusView=(TextView)mview.findViewById(R.id.userstatus);
            userStatusView.setText(status);
        }
        public void setImage(Context context, final String image)
        {
            final CircleImageView circleImageView=mview.findViewById(R.id.profile_image);
            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.defaultimage).into(circleImageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(image).placeholder(R.drawable.defaultimage).into(circleImageView);
                }
            });
        }
        //public void setuserid(String id)
        //{

        //}
    }
}
