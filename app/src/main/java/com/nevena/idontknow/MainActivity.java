package com.nevena.idontknow;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.nevena.idontknow.Fragments.FriendsFragment;
import com.nevena.idontknow.Fragments.MapsFragment;
import com.nevena.idontknow.Fragments.PlacesFragment;
import com.nevena.idontknow.Fragments.RankFragment;
import com.nevena.idontknow.Adapters.CustomListAdapter;
import com.nevena.idontknow.Controllers.AppController;
import com.nevena.idontknow.Models.Place;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toolbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.nevena.idontknow.Utils.BottomNavigationBehavior;

public class MainActivity extends AppCompatActivity
{
    ImageView imgProfile;

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Places json url
    private static final String url = "https://api.androidhive.info/json/places.json";
    private ProgressDialog pDialog;
    private List<Place> placeList = new ArrayList<Place>();
    private ListView listView;
    private CustomListAdapter adapter;

    private ActionBar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            Fragment fragment;
            switch (item.getItemId())
            {
                case R.id.navigation_places:
//                    toolbar.setTitle("Places");
                    fragment = new PlacesFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_maps:
//                    toolbar.setTitle("Maps");
                    fragment = new MapsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_friends:
//                    toolbar.setTitle("Friends");
                    fragment = new FriendsFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_rank:
//                    toolbar.setTitle("Rank");
                    fragment = new RankFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.customToolbar));
      //  android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.customToolbar);
        ImageView profile = (ImageView) findViewById(R.id.profile_img);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });





        //imgProfile = (ImageView) findViewById(R.id.customToolbar);
//        imgProfile.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//            }
//        });

//        toolbar = getSupportActionBar();

        // load the store fragment by default
//        toolbar.setTitle("Places");

    loadFragment(new PlacesFragment());

    BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    // attaching bottom sheet behaviour - hide / show on scroll
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navView.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());




//        listView = (ListView) findViewById(R.id.list);
//        adapter = new CustomListAdapter(this, placeList);
//        listView.setAdapter(adapter);
//
//        pDialog = new ProgressDialog(this);
//        // Showing progress dialog before making http request
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//
//        // changing action bar color
//        getActionBar().setBackgroundDrawable(
//                new ColorDrawable(Color.parseColor("#1b1b1b")));
//
//        // Creating volley request obj
//        JsonArrayRequest placeReq = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d(TAG, response.toString());
//                        hidePDialog();
//
//                        // Parsing json
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//
//                                JSONObject obj = response.getJSONObject(i);
//                                Place place = new Place();
//                                place.setName(obj.getString("name"));
//                                place.setThumbnailUrl(obj.getString("image"));
//                                place.setRating(((Number) obj.get("rating"))
//                                        .doubleValue());
//                                place.setWorkingHours(obj.getString("workingHours"));
//                                place.setAddress(obj.getString("address"));
//
//                                // adding place to places array
//                                placeList.add(place);
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//
//                        // notifying list adapter about data changes
//                        // so that it renders the list view with updated data
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                hidePDialog();
//
//            }
//        });
//
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(placeReq);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        hidePDialog();
//    }
//
//    private void hidePDialog() {
//        if (pDialog != null) {
//            pDialog.dismiss();
//            pDialog = null;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
