package com.example.mychatapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Friend extends Fragment {

    private View friendsView;
    private RecyclerView requestList;

    DatabaseReference friendsRef, userRef;
    FirebaseAuth mauth;
    String current_userid;


    public Friend() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        friendsView = inflater.inflate(R.layout.fragment_friend, container, false);

        requestList = friendsView.findViewById(R.id.requestlist);
        requestList.setLayoutManager(new LinearLayoutManager(getContext()));
        mauth = FirebaseAuth.getInstance();
        current_userid = mauth.getCurrentUser().getUid();

        friendsRef = FirebaseDatabase.getInstance().getReference().child("Request").child(current_userid);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return friendsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(friendsRef, Users.class)
                        .build();
        final FirebaseRecyclerAdapter<Users, FriendViewHolder> adapter = new FirebaseRecyclerAdapter<Users, FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendViewHolder requestViewHolder, final int i, @NonNull Users users) {

                String userIDs = getRef(i).getKey();
                Log.i("userIds",userIDs);
                assert userIDs != null;
                userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       if(dataSnapshot.exists())

                       {

                           if(dataSnapshot.child("userState").hasChild("state")) {
                               String state = dataSnapshot.child("userState").child("state").getValue().toString();
                               String date = dataSnapshot.child("userState").child("date").getValue().toString();
                               String time = dataSnapshot.child("userState").child("time").getValue().toString();

                               if (state.equals("online")) {

                                   requestViewHolder.img_on.setVisibility(View.VISIBLE);
                               } else {
                                   requestViewHolder.img_off.setVisibility(View.VISIBLE);
                               }
                           }
                           if (dataSnapshot.hasChild("image")) {
                               String userImage = dataSnapshot.child("image").getValue().toString();
                               String profilename = dataSnapshot.child("name").getValue().toString();
                               String profilestatus = dataSnapshot.child("status").getValue().toString();

                               requestViewHolder.username.setText(profilename);
                               requestViewHolder.userstatus.setText(profilestatus);
                               Picasso.get().load(userImage).placeholder(R.drawable.defaultimage).into(requestViewHolder.circleImageView);

                           }
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                requestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_userid=getRef(i).getKey();
                        Intent profileintent=new Intent(getContext(),chatActivity.class);
                        profileintent.putExtra("visit_userid",visit_userid);
                        startActivity(profileintent);
                    }
                });


            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout, parent, false);
                FriendViewHolder viewHolder = new FriendViewHolder(view);
                return viewHolder;
            }
        };
        requestList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView username, userstatus;
        CircleImageView circleImageView;
        CircleImageView img_on,img_off;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
            circleImageView = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);


        }
    }

}