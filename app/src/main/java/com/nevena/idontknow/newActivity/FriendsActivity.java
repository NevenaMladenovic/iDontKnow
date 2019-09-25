package com.nevena.idontknow.newActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;
import com.nevena.idontknow.Controllers.Constants;
import com.nevena.idontknow.GlideApp;
import com.nevena.idontknow.ProfileActivity;
import com.nevena.idontknow.R;

public class FriendsActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    ImageView imgProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Constants.friendsActivity = this;
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        imgProfile = (ImageView) findViewById(R.id.profile_img);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FriendsActivity.this, ProfileActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    public void onResume(){
        super.onResume();
        navView.getMenu().findItem(R.id.navigation_friends).setChecked(true);
        if (!readSP().equalsIgnoreCase(""))
        {
            loadProfile(readSP());
        }
        else
        {
            loadProfileDefault();
        }
    }

    private String readSP()
    {
        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "Images", MODE_PRIVATE);
        return sharedPreferencesA.getString("ImagePath2", "");
    }
    private void loadProfile(String url) {
        Key glideKey = new ObjectKey(new Object());
        GlideApp.with(this).load(url)
                .signature(glideKey)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault() {
        GlideApp.with(this).load(R.drawable.ic_avatar)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Intent intent;
            switch (item.getItemId())
            {
                case R.id.navigation_places:
                    intent = new Intent(FriendsActivity.this, PlacesListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_maps:
                    intent = new Intent(FriendsActivity.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_friends:
                    return true;
                case R.id.navigation_rank:
                    intent = new Intent(FriendsActivity.this, RankActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };


}
