package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class reset_password extends AppCompatActivity {
    Toolbar mToolBar;
    MaterialButton resetbtn;
    TextInputEditText email;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetbtn=findViewById(R.id.btn_reset);
        email=findViewById(R.id.input_email);
        mToolBar = findViewById(R.id.resetappbar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Reset Password");

        firebaseAuth=FirebaseAuth.getInstance();
        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailid=email.getText().toString();
                if(emailid.equals(""))
                {
                    Toast.makeText(reset_password.this, "All fields are required", Toast.LENGTH_SHORT).show();

                }else
                {
                    firebaseAuth.sendPasswordResetEmail(emailid).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(reset_password.this, "please check your email", Toast.LENGTH_SHORT).show();
                                resetbtn.setText("Email sent");
                                resetbtn.setIconResource(R.drawable.ic_email_black_24dp);
                                startActivity(new Intent(reset_password.this,LoginActivity.class));
                            }
                            else
                            {
                                String error=task.getException().getMessage();
                                Toast.makeText(reset_password.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
