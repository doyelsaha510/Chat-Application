package com.example.mychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputEditText emailId, passwd, name;
    MaterialButton btnSignUp;
    private FirebaseAuth mAuth;

    ProgressDialog progress;
    DatabaseReference uDatabase;

    Animation upanimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailId = findViewById(R.id.input_email);
        passwd = findViewById(R.id.input_password);
        name = findViewById(R.id.input_name);
        btnSignUp = findViewById(R.id.btn_signup);
        progress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        passwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 10) {
                    passwd.setError("Max character length is " + 10);

                }
                else
                    passwd.setError(null);

            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] token = new String[1];

                FirebaseMessaging.getInstance().subscribeToTopic("request_notification");
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    //Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                // Get new Instance ID token
                                token[0] = task.getResult().getToken().toString();

                                // Log and toast
                                //String msg = getString(R.string.msg_token_fmt, token);
                                Log.i("Token", token[0]);
                                //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                String uemailID = emailId.getText().toString();
                String upaswd = passwd.getText().toString();
                String uname = name.getText().toString();
                if (!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(uemailID) && (!TextUtils.isEmpty(upaswd)&& upaswd.length()<=10)) {

                    progress.setMessage("Registering... ");
                    progress.setIndeterminate(true);
                    progress.setIcon(R.drawable.icon);
                    progress.setIndeterminateDrawable(getDrawable(R.drawable.progress_icon));
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    register_user(uemailID, upaswd, uname,token);

                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "All fields are necessary or check password limit", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }

    private void register_user(String uemailID, String upaswd, final String uname, final String[] token) {
        mAuth.createUserWithEmailAndPassword(uemailID, upaswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                                    String uid=current_user.getUid();

                                    uDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                    HashMap<String,String> userMap=new HashMap<>();
                                    userMap.put("id",uid);
                                    userMap.put("name",uname);
                                    userMap.put("status","hello!!my name is "+uname);
                                    userMap.put("image","default");
                                    userMap.put("token", Arrays.toString(token));
                                    uDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(RegisterActivity.this, "WELCOME", Toast.LENGTH_SHORT).show();
                                                progress.dismiss();
                                                // Sign in success, update UI with the signed-in user's information
                                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);

                                                finish();

                                            }
                                        }
                                    });

                                } else {
                                    progress.hide();
                                    // If sign in fails, display a message to the user.
                                    Log.w("tag", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "error!cant sign in!!"+"\n"+"password must be six characters\n"+"or invalid emailid", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
    }


