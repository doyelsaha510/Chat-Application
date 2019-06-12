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
import android.widget.LinearLayout;
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

import java.net.FileNameMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Request extends Fragment {
    private View requestView;
    private RecyclerView friendrequestList;
    private DatabaseReference friendrequestRef, userRef;
    private FirebaseAuth mauth;
    private String current_userid;
    LinearLayout linearLayout;


    public Request() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        requestView = inflater.inflate(R.layout.fragment_request, container, false);
        friendrequestList = requestView.findViewById(R.id.friendrequestlist);
        friendrequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayout = requestView.findViewById(R.id.norequest);
        mauth = FirebaseAuth.getInstance();
        current_userid = Objects.requireNonNull(mauth.getCurrentUser()).getUid();

        friendrequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return requestView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(friendrequestRef.child(current_userid), Users.class)
                .build();

        FirebaseRecyclerAdapter<Users, RequestViewHolder> adapter = new
                FirebaseRecyclerAdapter<Users, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder requestViewHolder, final int i, @NonNull final Users users) {

                        final String list_user_id = getRef(i).getKey();
                        Log.i("list_userid", list_user_id);


                        DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("received")) {
                                        assert list_user_id != null;
                                        userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("image")) {
                                                    final String Profilename = dataSnapshot.child("name").getValue().toString();
                                                    String userImage = dataSnapshot.child("image").getValue().toString();
                                                    requestViewHolder.userName.setText("New request from " + Profilename + " click to see profile");
                                                    Picasso.get().load(userImage).placeholder(R.drawable.defaultimage).into(requestViewHolder.CircleImageView);
                                                    linearLayout.setVisibility(View.INVISIBLE);
                                                }
                                                // else
                                                //{
                                                //  String profilename = dataSnapshot.child("name").getValue().toString();
                                                // requestViewHolder.userName.setText(profilename);

                                                //}
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else {
                                        friendrequestList.setVisibility(View.INVISIBLE);
                                        //linearLayout.setVisibility(View.VISIBLE);
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
                                String visit_userid = getRef(i).getKey();
                                Intent profileintent = new Intent(getContext(), ProfileActivity.class);
                                profileintent.putExtra("visit_userid", visit_userid);
                                startActivity(profileintent);
                            }
                        });


                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_user_single_layout, parent, false);
                        RequestViewHolder holder = new RequestViewHolder(view);
                        return holder;


                    }
                };
        friendrequestList.setAdapter(adapter);
        adapter.startListening();
        //
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView CircleImageView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            CircleImageView = itemView.findViewById(R.id.profile_image);
        }
    }
}
