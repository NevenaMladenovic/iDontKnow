package com.nevena.idontknow.newActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nevena.idontknow.Adapters.RankAdapter;
import com.nevena.idontknow.Controllers.Constants;
import com.nevena.idontknow.GlideApp;
import com.nevena.idontknow.Models.User;
import com.nevena.idontknow.ProfileActivity;
import com.nevena.idontknow.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private List<User> usersList;
    private RankAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private BottomNavigationView navView;

    AVLoadingIndicatorView avi;
    ImageView imgProfile;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        Constants.rankActivity = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        avi =  findViewById(R.id.indicator);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        usersList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        mAdapter = new RankAdapter(this, usersList, userID);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        imgProfile = (ImageView) findViewById(R.id.profile_img);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RankActivity.this, ProfileActivity.class));
            }
        });
    }

    public void onResume(){
        super.onResume();
        getUsersList();
        navView.getMenu().findItem(R.id.navigation_rank).setChecked(true);
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
                    intent = new Intent(RankActivity.this, PlacesListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_maps:
                    intent = new Intent(RankActivity.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_friends:
                    intent = new Intent(RankActivity.this, FriendsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_rank:
                    return true;
            }
            return false;
        }
    };

    public void getUsersList()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = databaseReference.child("users");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null)
                {
                    usersList = new ArrayList<>();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        User user = singleSnapshot.getValue(User.class);
                        if(user!=null)
                        {
                            usersList.add(user);
                        }
                    }
                    Collections.sort(usersList);
                    mAdapter.setUsersList(usersList);
                    avi.hide();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Error occured when reading database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
