package com.example.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class settings extends AppCompatActivity {
    TextView uname, ustatus;
    Button btnstatus, btnimage;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    private static int GALLERY_PICKDP = 1;
    private StorageReference userprofileImageRef;
    String current_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        uname = findViewById(R.id.textname);
        ustatus = findViewById(R.id.textstatus);
        btnstatus = findViewById(R.id.btnstatus);
        btnimage = findViewById(R.id.btnimage);
        userprofileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        final CircularImageView circularImageView = (CircularImageView) findViewById(R.id.circular_image);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                uname.setText(name);
                ustatus.setText(status);
                if (!image.equals("default")) {
                    Picasso.get().load(image).into(circularImageView);
                } else {
                    Picasso.get().load(R.drawable.defaultimage).into(circularImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settings.this, statusactivity.class);
                String putstatus = ustatus.getText().toString();
                intent.putExtra("status", putstatus);
                startActivity(intent);
            }
        });
        btnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryintent, "Select image"), GALLERY_PICKDP);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICKDP && resultCode == RESULT_OK && data != null) {
            Uri imageuri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final StorageReference filepath = userprofileImageRef.child(current_uid + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(settings.this, "profile image upload successfully", Toast.LENGTH_SHORT).show();
                            Task<Uri> uri = filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String Imageurl = task.getResult().toString();
                                    //Log.i("url",Imageurl);
                                    databaseReference.child("image").setValue(Imageurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(settings.this, "done", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(settings.this, "error"+task.getException().toString(), Toast.LENGTH_SHORT).show();

                                            }
                                        }

                                    });
                                }
                            });


                        } else {
                            String msg = task.getException().toString();
                            Toast.makeText(settings.this, "error:" + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}

