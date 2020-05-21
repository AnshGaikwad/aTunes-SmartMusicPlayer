package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
//import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
//import android.widget.ImageView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.widget.Toolbar;

public class Main2Activity extends AppCompatActivity {



   // private NavigationView navigationView;

    Toolbar mToolbar;
    TabLayout mTabLayout;
    TabItem mTab1;
    TabItem mTab2;
    TabItem mTab3;
    ViewPager mPager;
    PagerController mPagerController;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private DrawerLayout drawerlayout;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");



        drawerlayout = (DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(Main2Activity.this, drawerlayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerlayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState(); //to sync
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Here we are storing the navigation header in the navView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.nav_header);



        mTabLayout = findViewById(R.id.tabLayout);

        mTab1 = findViewById(R.id.tab1);
        mTab2 = findViewById(R.id.tab2);
        mTab3 = findViewById(R.id.tab3);
        mPager = findViewById(R.id.viewPager);

        mPagerController = new PagerController(getSupportFragmentManager(), mTabLayout.getTabCount());
        mPager.setAdapter(mPagerController);

        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);

                return false;
            }
        });


    }

       @Override
       public boolean onOptionsItemSelected(@NonNull MenuItem item) {
           if(actionBarDrawerToggle.onOptionsItemSelected(item)){
               return true;
           }

           return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        // Here if the user click home then he will be redirected home, settings then redirected to settings
        switch(item.getItemId()){
            case R.id.online1:
                Intent intent = new Intent(Main2Activity.this, ShowSongsActivity.class);
                startActivity(intent);
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.features:
                Intent i1 = new Intent(Main2Activity.this,Features.class);
                startActivity(i1);
                break;
            case R.id.online2:
                Intent i = new Intent(Main2Activity.this,Main3Activity.class);
                startActivity(i);
                break;
            case R.id.logout:
                mAuth.signOut(); //to signout from the firebase
                SendUserToLoginActivity();  // to send user from mainactivity to login activity
                break;
            case R.id.credits:
                Intent i2 = new Intent(Main2Activity.this,Credits.class);
                startActivity(i2);


        }
    }

    //This method will be called directly when the user opens the phone
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser(); //getting the current user before if statement
        if(currentUser == null){ //means user  is not authenticated
            SendUserToLoginActivity();
        }
    }

    private void SendUserToLoginActivity() {

        Intent loginIntent = new Intent(Main2Activity.this, LoginActivity.class); //sending user from main activity to login activity
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //this line is to prevent user from pressing back and going to the main activity
        startActivity(loginIntent);
        finish();
    }

}