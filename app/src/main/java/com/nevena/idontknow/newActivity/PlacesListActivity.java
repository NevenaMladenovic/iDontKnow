package com.nevena.idontknow.newActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.nevena.idontknow.FirebaseMethods;
import com.nevena.idontknow.Fragments.FriendsFragment;
import com.nevena.idontknow.Fragments.MapsFragment;
import com.nevena.idontknow.Fragments.PlacesFragment;
import com.nevena.idontknow.Fragments.RankFragment;
import com.nevena.idontknow.LoginActivity;
import com.nevena.idontknow.MainActivity;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.Models.Review;
import com.nevena.idontknow.ProfileActivity;
import com.nevena.idontknow.R;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PlacesListActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private static final String TAG = "";
    private Context mContext;


    private RecyclerView recyclerView;
    private List<Place> placesList;
    private PlacesAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private  BottomNavigationView navView;

    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        mContext = PlacesListActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);

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


        ImageView profile = (ImageView) findViewById(R.id.profile_img);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlacesListActivity.this, ProfileActivity.class));
            }
        });


        //setupFirebaseData();

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


    public void onResume(){
        super.onResume();
        navView.getMenu().findItem(R.id.navigation_places).setChecked(true);
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

    private Boolean readSP()
    {
        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "FirstLogin", MODE_PRIVATE);
        Boolean isFirstLogin = sharedPreferencesA.getBoolean("IsFirstLogin", true);

        return isFirstLogin;
    }

    private void writeSp(Boolean val)
    {
        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "FirstLogin", MODE_PRIVATE);
        SharedPreferences.Editor editorA = sharedPreferencesA.edit();
        editorA.putBoolean("IsFirstLogin", val); //key, value
        editorA.apply();
    }

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
