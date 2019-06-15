package com.example.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, passwd;
    Button btnlogin;
    TextView forgot_pass;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference userreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        userreference = FirebaseDatabase.getInstance().getReference().child("Users");


        forgot_pass = findViewById(R.id.forget);
        emailId = findViewById(R.id.input_email);
        passwd = findViewById(R.id.input_password);
        btnlogin = findViewById(R.id.btn_login);
        progressDialog = new ProgressDialog(this);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uemailID = emailId.getText().toString();
                String upaswd = passwd.getText().toString();

                if (!TextUtils.isEmpty(uemailID) && !TextUtils.isEmpty(upaswd)) {
                   // progressDialog.setTitle("Login user");
                    progressDialog.setMessage("Login... ");
                    progressDialog.setIndeterminate(true);
                    progressDialog.setIcon(R.drawable.icon);
                    progressDialog.setIndeterminateDrawable(getDrawable(R.drawable.progress_icon));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    loginuser(uemailID, upaswd);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loginuser(String uemailID, String upaswd) {
        mAuth.signInWithEmailAndPassword(uemailID, upaswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            String online_user_id = mAuth.getCurrentUser().getUid();
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.hide();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
}
