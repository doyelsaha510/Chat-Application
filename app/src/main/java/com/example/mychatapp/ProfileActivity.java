package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    //CircularImageView dpimage;
    CircleImageView dpimage;
    MaterialButton sendfq, declinefq;
    TextView display_name, display_status;
    DatabaseReference UsersReference, FriendRequestReference, FriendsReference, NotificationsReference;
    ProgressDialog progressDialog;
    String current_State;
    private FirebaseAuth mAuth;
    MaterialCardView cardViewbtn;
    FirebaseUser firebaseCurrUser;
    String sender_userid;
    String receiver_user_id;
    LinearLayout linearLayout;
    Context contextInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiver_user_id = getIntent().getStringExtra("visit_userid").toString();
        FriendsReference = FirebaseDatabase.getInstance().getReference()
                .child("Request");
        FriendsReference.keepSynced(true);


        FriendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        FriendRequestReference.keepSynced(true);

        NotificationsReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        NotificationsReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        sender_userid = mAuth.getCurrentUser().getUid();

        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        dpimage = findViewById(R.id.profile);
        sendfq = findViewById(R.id.rightbtn);
        declinefq = findViewById(R.id.crossbtn);
        display_name = findViewById(R.id.name);
        display_status = findViewById(R.id.status);
        cardViewbtn = findViewById(R.id.buttons);
        current_State = "not_friends";

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading user data");
        progressDialog.setMessage("pls wait while we load the user data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        UsersReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();


                display_name.setText(name);
                display_status.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.defaultimage).into(dpimage);
                progressDialog.dismiss();

                FriendRequestReference.child(sender_userid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild(receiver_user_id)) {
                                        String req_type = dataSnapshot.child(receiver_user_id)
                                                .child("request_type").getValue().toString();
                                        if (req_type.equals("sent")) {
                                            current_State = "request_sent";
                                            sendfq.setText("Cancel Friend Request");
                                            sendfq.setIconResource(R.drawable.warning);
                                            declinefq.setVisibility(View.INVISIBLE);
                                            declinefq.setEnabled(false);


                                        } else if (req_type.equals("received")) {
                                            current_State = "request_received";
                                            sendfq.setText("Accept Friend Friend");
                                            declinefq.setVisibility(View.VISIBLE);
                                            declinefq.setEnabled(true);

                                            declinefq.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DeclineFriendRequest();
                                                }
                                            });

                                        }

                                    }

                                } else {
                                    FriendsReference.child(sender_userid)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(receiver_user_id)) {
                                                        current_State = "friends";
                                                        sendfq.setText("Unfriend This Person");
                                                        sendfq.setIconResource(R.drawable.warning);
                                                        declinefq.setVisibility(View.INVISIBLE);
                                                        declinefq.setEnabled(false);

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        declinefq.setVisibility(View.INVISIBLE);
        declinefq.setEnabled(false);
//        mcross.setVisibility(View.INVISIBLE);


        if (!sender_userid.equals(receiver_user_id)) {
            sendfq.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    sendfq.setEnabled(false);

                    if (current_State.equals("not_friends")) {
                        SendFriendRequestToaFriend();
                    }

                    if (current_State.equals("request_sent")) {
                        CancelFriendRequest();
                    }

                    if (current_State.equals("request_received")) {
                        AcceptFriendRequest();
                    }
                    if (current_State.equals("friends")) {
                        UnFriendsFriend();
                    }
                }
            });
        } else {
            cardViewbtn.setVisibility(View.INVISIBLE);
            declinefq.setVisibility(View.INVISIBLE);
            sendfq.setVisibility(View.INVISIBLE);
        }

    }


    private void DeclineFriendRequest() {
        FriendRequestReference.child(sender_userid).child(receiver_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendRequestReference.child(receiver_user_id).child(sender_userid)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendfq.setEnabled(true);
                                current_State = "not_friend";
                                sendfq.setText("Send Friend Request");
                                sendfq.setIconResource(R.drawable.right);
                                declinefq.setVisibility(View.INVISIBLE);
                                declinefq.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void UnFriendsFriend() {
        FriendsReference.child(sender_userid).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendsReference.child(receiver_user_id).child(sender_userid).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendfq.setEnabled(true);
                                                current_State = "not_friends";
                                                sendfq.setText("Send Friend Request");
                                                sendfq.setIconResource(R.drawable.right);
                                                declinefq.setVisibility(View.INVISIBLE);
                                                declinefq.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void AcceptFriendRequest() {
        Calendar calFordAte = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calFordAte.getTime());

        FriendsReference.child(sender_userid)
                .child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FriendsReference.child(receiver_user_id)
                                .child(sender_userid)
                                .child("date")
                                .setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FriendRequestReference.child(sender_userid).child(receiver_user_id)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FriendRequestReference.child(receiver_user_id).child(sender_userid)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                sendfq.setEnabled(true);
                                                                current_State = "friends";
                                                                sendfq.setText("Unfriend this person");
                                                                sendfq.setIconResource(R.drawable.warning);
                                                                declinefq.setVisibility(View.INVISIBLE);
                                                                declinefq.setEnabled(false);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                    }
                });
    }

    private void CancelFriendRequest() {
        FriendRequestReference.child(sender_userid).child(receiver_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FriendRequestReference.child(receiver_user_id).child(sender_userid)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendfq.setEnabled(true);
                                current_State = "not_friend";
                                sendfq.setText("Send Friend Request");
                                sendfq.setIconResource(R.drawable.right);
                                declinefq.setVisibility(View.INVISIBLE);
                                declinefq.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendFriendRequestToaFriend() {
        FriendRequestReference.child(sender_userid)
                .child(receiver_user_id).child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FriendRequestReference.child(receiver_user_id)
                                    .child(sender_userid).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                HashMap<String, String> notificationData = new HashMap<String, String>();
                                                notificationData.put("from", sender_userid);
                                                notificationData.put("type", "request");

                                                NotificationsReference.child(receiver_user_id).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    sendfq.setEnabled(true);
                                                                    current_State = "request_sent";
                                                                    sendfq.setText("Cancel Friend Request");
                                                                    sendfq.setIconResource(R.drawable.warning);
                                                                    declinefq.setVisibility(View.INVISIBLE);
                                                                    declinefq.setEnabled(false);
                                                                }
                                                            }
                                                        });


                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
