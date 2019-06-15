package com.example.mychatapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class startActivity extends AppCompatActivity {
    Button mRegbtn,mlgnbtn;
    RelativeLayout relativeLayout;
    ImageView icon;
    Animation banimation,ianimation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        relativeLayout=findViewById(R.id.btns);
        icon=findViewById(R.id.icon);
        banimation= AnimationUtils.loadAnimation(this,R.anim.frombutton);
        ianimation= AnimationUtils.loadAnimation(this,R.anim.fromicon);
        relativeLayout.setAnimation(banimation);
        icon.setAnimation(ianimation);

        mRegbtn=findViewById(R.id.btnnewacc);
        mlgnbtn=findViewById(R.id.btnlogin);
        mRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent=new Intent(startActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        mlgnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent=new Intent(startActivity.this,LoginActivity.class);
                startActivity(reg_intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


    }
}
