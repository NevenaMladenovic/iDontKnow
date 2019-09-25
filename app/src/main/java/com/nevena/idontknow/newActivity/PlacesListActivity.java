package com.nevena.idontknow.newActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nevena.idontknow.Adapters.PlacesAdapter;
import com.nevena.idontknow.Controllers.Constants;
import com.nevena.idontknow.FirebaseMethods;
import com.nevena.idontknow.GlideApp;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.ProfileActivity;
import com.nevena.idontknow.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesListActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private static final String TAG = "";
    private Context mContext;

    private RecyclerView recyclerView;
    public static List<Place> placesList;
    public static boolean reloadPlaces;
    private PlacesAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private  BottomNavigationView navView;

    @BindView(R.id.profile_img)
    ImageView imgProfile;

    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        reloadPlaces = true;
        mContext = PlacesListActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        ButterKnife.bind(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        avi =  findViewById(R.id.indicator);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        placesList = new ArrayList<>();
        mAdapter = new PlacesAdapter(this, placesList);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

  //      new ReadJSONFILE().execute();
        getPlacesList();

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //setupFirebaseData();


    }
    @OnClick({R.id.profile_img})
    void onProfileImageClick()
    {
        startActivity(new Intent(PlacesListActivity.this, ProfileActivity.class));
    }
    private String readSP()
    {
        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "Images", MODE_PRIVATE);
        return sharedPreferencesA.getString("ImagePath2", "");
    }
    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);
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

    public void getPlacesList()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = databaseReference.child("places");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null)
                {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Place place = singleSnapshot.getValue(Place.class);
                        if(place!=null)
                        {
                            placesList.add(place);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
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


    public void onResume(){
        super.onResume();
        navView.getMenu().findItem(R.id.navigation_places).setChecked(true);
        if (!readSP().equalsIgnoreCase(""))
        {
            loadProfile(readSP());
        }
        else
        {
            loadProfileDefault();
        }
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
                    return true;
                case R.id.navigation_maps:
                    intent = new Intent(PlacesListActivity.this, MapsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_friends:
                    intent = new Intent(PlacesListActivity.this, FriendsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_rank:
                    intent = new Intent(PlacesListActivity.this, RankActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            if(Constants.mapsActivity != null)
                Constants.mapsActivity.finish();
            if(Constants.rankActivity != null)
                Constants.rankActivity.finish();
            if(Constants.friendsActivity != null)
                Constants.friendsActivity.finish();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please touch BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

//    private class ReadJSONFILE extends AsyncTask<Void, Void, Void>
//    {
//
//        @Override
//        protected Void doInBackground(Void... voids)
//        {
//            readJSon();
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute()
//        {
//            avi.show();
//        }
//
//        @Override
//        protected void onPostExecute(Void result)
//        {
//
//            new CountDownTimer(1000,1000)
//            {
//                @Override
//                public void onTick(long millisUntilFinished)
//                {
//
//                }
//
//                @Override
//                public void onFinish()
//                {
//                    avi.hide();
//                    recyclerView.setVerticalScrollbarPosition(placesList.size() - 2);
//                }
//            }.start();
//        }
//    }
//    private Boolean readSP()
//    {
//        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "FirstLogin", MODE_PRIVATE);
//        Boolean isFirstLogin = sharedPreferencesA.getBoolean("IsFirstLogin", true);
//
//        return isFirstLogin;
//    }
//
//    private void writeSp(Boolean val)
//    {
//        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "FirstLogin", MODE_PRIVATE);
//        SharedPreferences.Editor editorA = sharedPreferencesA.edit();
//        editorA.putBoolean("IsFirstLogin", val); //key, value
//        editorA.apply();
//    }
//    private void readJSon()
//    {
//        try
//        {
//            JSONArray response = new JSONArray(loadJSONFromAsset());
//            for (int i = 0; i < response.length(); i++) {
//
//
//                JSONObject obj = response.getJSONObject(i);
//                Place place = new Place();
//                place.setName(obj.getString("name"));
//                place.setType(obj.getString("type"));
//                place.setThumbnailUrl(obj.getString("image"));
//                place.setRate(((Number) obj.get("rate"))
//                        .doubleValue());
//                place.setWorkingHours(obj.getString("workingHours"));
//                place.setAddress(obj.getString("address"));
//                place.setLatitude(obj.getDouble("latitude"));
//                place.setLongitude(obj.getDouble("longitude"));
//
//                // adding place to places array
//                placesList.add(place);
//
//                 if(readSP())
//                {
//                    firebaseMethods.addNewPlace(place);
//                }
//
//            }
//
//            if(readSP())
//            {
//                writeSp(false);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


//    public String loadJSONFromAsset() {
//        String json = null;
//        try {
//            InputStream is = getAssets().open("places.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }
}
