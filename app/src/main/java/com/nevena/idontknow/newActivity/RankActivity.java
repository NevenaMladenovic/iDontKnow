package com.nevena.idontknow.newActivity;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nevena.idontknow.Adapters.PlacesAdapter;
import com.nevena.idontknow.Adapters.UsersAdapter;
import com.nevena.idontknow.FirebaseMethods;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.Models.User;
import com.nevena.idontknow.ProfileActivity;
import com.nevena.idontknow.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private Context mContext;

    private RecyclerView recyclerView;
    private List<User> usersList;
    private UsersAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private  BottomNavigationView navView;

    AVLoadingIndicatorView avi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        mContext = RankActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        avi =  findViewById(R.id.indicator);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        usersList = new ArrayList<>();
        mAdapter = new UsersAdapter(this, usersList);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getUsersList();

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ImageView profile = (ImageView) findViewById(R.id.profile_img);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RankActivity.this, ProfileActivity.class));
            }
        });
    }

    public void onResume(){
        super.onResume();
        navView.getMenu().findItem(R.id.navigation_rank).setChecked(true);
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
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        User user = singleSnapshot.getValue(User.class);
                        if(user!=null)
                        {
                            usersList.add(user);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    new CountDownTimer(600,600)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {

                        }

                        @Override
                        public void onFinish()
                        {
                            avi.hide();

                        }
                    }.start();
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
}
