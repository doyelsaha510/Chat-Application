package com.example.mychatapp;

import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class statusactivity extends AppCompatActivity {
    MaterialButton newstatusbtn;
    TextInputEditText statustext;
    Toolbar toolbar;
    DatabaseReference datareference;
    FirebaseUser currentUser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusactivity);
        newstatusbtn = findViewById(R.id.changestatus);
        statustext = findViewById(R.id.statusvalue);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = currentUser.getUid();

        datareference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        toolbar = findViewById(R.id.statuappbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status");

        statustext.setText(status_value);
        newstatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //progress
                progressDialog = new ProgressDialog(statusactivity.this);
                progressDialog.setTitle("Saving Changes");
                progressDialog.setMessage("Pls wait while we save the changes");
                progressDialog.show();

                String status = statustext.getText().toString();

                datareference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                        }
                    }
                });


            }
        });


    }
}
