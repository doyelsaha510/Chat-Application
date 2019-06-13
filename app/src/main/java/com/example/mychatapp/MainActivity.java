package com.example.mychatapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Toolbar mToolBar;
     ViewPager viewPager;
     SectionPageAdapter sectionPageAdapter;
     TabLayout tabLayout;
    private DatabaseReference RootRef;
    private String currentUserID;

    public static final String CHANNEL_ID="MyChatApp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
       // currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        mToolBar = findViewById(R.id.mainpage_toolbar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("MyChatApp");


        viewPager = findViewById(R.id.viewpager);
        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPageAdapter);


        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
        if (currentUser == null) {
            sendTostart();
        }
        else
        {
            updateUserStatus("online");
        }
    }

    private void sendTostart() {
        Intent intent = new Intent(MainActivity.this, startActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logout) {
            updateUserStatus("offline");
            FirebaseAuth.getInstance().signOut();
            sendTostart();
        } else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, settings.class);
            startActivity(intent);
        }
        else
        {
            Intent uintent=new Intent(MainActivity.this,allusers.class);
            startActivity(uintent);
        }
        return true;
    }

    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        RootRef.child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("userState")
                .updateChildren(onlineStateMap);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            updateUserStatus("online");
        }
    }

}
