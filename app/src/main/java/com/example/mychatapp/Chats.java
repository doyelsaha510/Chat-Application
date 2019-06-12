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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chats extends Fragment {

    private RecyclerView recyclerView;

    private View view;

    DatabaseReference databaseReference, userRef;

    String current_uid;


    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.chat_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        current_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Request").child(current_uid);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(databaseReference, Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, DataViewHolder> adapter = new FirebaseRecyclerAdapter<Users, DataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DataViewHolder dataViewHolder, final int i, @NonNull Users users) {
                final String userIDs = getRef(i).getKey();
                Log.i("userIds", userIDs);
                assert userIDs != null;
                userRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists())
                        {
                            if (dataSnapshot.hasChild("image")) {
                                String userImage = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(userImage).placeholder(R.drawable.defaultimage).into(dataViewHolder.circleImageView);

                            }
                            String profilename = dataSnapshot.child("name").getValue().toString();
                            String profilestatus = dataSnapshot.child("status").getValue().toString();
                            dataViewHolder.username.setText(profilename);
                            dataViewHolder.userstatus.setText("Last seen: "+"\n"+"Date"+"Time");

                            if(dataSnapshot.child("userState").hasChild("state"))
                            {
                                String state=dataSnapshot.child("userState").child("state").getValue().toString();
                                String date=dataSnapshot.child("userState").child("date").getValue().toString();
                                String time=dataSnapshot.child("userState").child("time").getValue().toString();

                                if(state.equals("online"))
                                {
                                    dataViewHolder.userstatus.setText("online");
                                }
                                else if(state.equals("offline"))
                                {
                                    dataViewHolder.userstatus.setText("Last seen: "+date+" "+time);
                                }
                            }
                            else
                            {
                                dataViewHolder.userstatus.setText("offline");
                            }

                            dataViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent=new Intent(getContext(),chatActivity.class);
                                    intent.putExtra("visit_userid",userIDs);
                                    startActivity(intent);

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_single_layout, parent, false);
                return new DataViewHolder(view);


            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView username, userstatus;
        CircleImageView circleImageView;


        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            userstatus = itemView.findViewById(R.id.userstatus);
            circleImageView = itemView.findViewById(R.id.profile_image);
        }
    }
}
