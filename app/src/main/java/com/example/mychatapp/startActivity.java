package com.example.mychatapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class startActivity extends AppCompatActivity {
    Button mRegbtn,mlgnbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegbtn=findViewById(R.id.btnnewacc);
        mlgnbtn=findViewById(R.id.btnlogin);
        mRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent=new Intent(startActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
        mlgnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent=new Intent(startActivity.this,LoginActivity.class);
                startActivity(reg_intent);
            }
        });


    }
}
