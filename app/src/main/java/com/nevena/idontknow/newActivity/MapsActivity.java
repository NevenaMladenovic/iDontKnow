package com.nevena.idontknow.newActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nevena.idontknow.Adapters.ValidPlacesAdapter;
import com.nevena.idontknow.Controllers.Constants;
import com.nevena.idontknow.FriendsProfileActivity;
import com.nevena.idontknow.GlideApp;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.Models.Review;
import com.nevena.idontknow.Models.User;
import com.nevena.idontknow.PlaceActivity;
import com.nevena.idontknow.ProfileActivity;
import com.nevena.idontknow.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import static com.nevena.idontknow.newActivity.PlacesListActivity.placesList;
import static com.nevena.idontknow.newActivity.PlacesListActivity.reloadPlaces;

public class MapsActivity extends AppCompatActivity implements LocationListener
{
    private int MAX_RADIUS = 3000;
    private int REVIEW_RADIUS = 100;
    MapView mapView = null;
    BottomNavigationView navView;
    IMapController mapController;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location locationObject;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 5000;

    protected LocationManager locationManager;
    GeoPoint myGeoPoint;
    OverlayItem myLocationMarker;
    ItemizedOverlayWithFocus<OverlayItem> mOverlayMyLocationMarker;
    ArrayList<OverlayItem> markersList;
    List<Place> placesListMaps, placesListMapsSort;
    List<User> userList, userListSort;
    boolean onCreate;
    Drawable newUserMarkerIcon, placesMarkerIcon, userMarkerIcon;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseReference;
    public String userID;
    ImageView imgProfile;

    SeekBar distanceSeekBar;
    TextView seekBarText;
    int distancePicked, typePlacePickedPosition, typePlacePickedPositionTemp;
    String[] items;
    float zoomLlv;
    boolean flagZoomLvl;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_maps);

        Constants.mapsActivity = this;
        distancePicked = MAX_RADIUS;
        typePlacePickedPosition = 0;
        zoomLlv = 17.5f;
        flagZoomLvl = false;
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        databaseReference = mDatabase.child(auth.getUid());
        userListSort = new ArrayList<>();
        placesListMapsSort = new ArrayList<>();
        items = getResources().getStringArray(R.array.places_type);
        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        userList = new ArrayList<>();
        onCreate = true;
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        imgProfile = (ImageView) findViewById(R.id.profile_img);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
            }
        });

        placesListMaps = placesList;

        placesMarkerIcon = getResources().getDrawable(R.drawable.places_marker);
        userMarkerIcon = getResources().getDrawable(R.drawable.users_marker);
        mapView = (MapView) findViewById(R.id.mapview);
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        Context ctx = getApplicationContext();
                        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


                        // Inflate the layout for this fragment

                        mapView.setTileSource(TileSourceFactory.MAPNIK);

                        mapView.setBuiltInZoomControls(false);
                        mapView.setMultiTouchControls(true);

                        mapController = mapView.getController();
                        mapController.setZoom(zoomLlv);

                        GeoPoint startPoint = new GeoPoint(43.32472, 21.90333);
                        myLocationMarker = new OverlayItem("My loc", "", startPoint);
                        newUserMarkerIcon = getResources().getDrawable(R.drawable.avatar_marker);
                        myLocationMarker.setMarker(newUserMarkerIcon);
                        if(markersList == null)
                            markersList = new ArrayList<OverlayItem>();
                        markersList.add(myLocationMarker);
                        mapController.setCenter(startPoint);
                        try {
                            getLocation();
                        }
                        catch (SecurityException e)
                        {
                            //
                        }
//                        mapView.getOverlays().add(mOverlayMyLocationMarker);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        markersList = new ArrayList<OverlayItem>();

        setMarkerOverLayListener();

        getUsersLocation();
        addReview();
        findViewById(R.id.filterImg).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.filter_dialog, null);

                //Spinner menu for users rate
                final Spinner spinner = mView.findViewById(R.id.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MapsActivity.this,
                        android.R.layout.simple_spinner_item,items);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(typePlacePickedPosition);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id)
                    {
                        typePlacePickedPositionTemp = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

                distanceSeekBar = mView.findViewById(R.id.seekBar);
                seekBarText = mView.findViewById(R.id.seekBarText);
                seekBarText.setText(String.valueOf(distancePicked));
                distanceSeekBar.setProgress((int)((float)distancePicked / MAX_RADIUS * 100));
                distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
                    {
                        progress = (int)(((float)progress/100) * MAX_RADIUS);
                        seekBarText.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                Button findFilterDialogBtn = mView.findViewById(R.id.btnFind);
                findFilterDialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        distancePicked = Integer.valueOf(String.valueOf(seekBarText.getText()));
                        typePlacePickedPosition = typePlacePickedPositionTemp;
                        reloadPlaces = true;
                        loadPlacesMarker();
                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                });

                Button btnRestore = mView.findViewById(R.id.btnRestore);

                btnRestore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        distancePicked = MAX_RADIUS;
                        typePlacePickedPosition = typePlacePickedPositionTemp = 0;
                        reloadPlaces = true;
                        loadPlacesMarker();
                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();                    }
                });
                Button btnCancel = mView.findViewById(R.id.btnCancel);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }

    private void getLocation() throws SecurityException
    {
        locationManager = (LocationManager) MapsActivity.this.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(locationManager != null) {
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    locationObject = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (locationObject != null) {
                        myGeoPoint = new GeoPoint(locationObject.getLatitude(), locationObject.getLongitude());
                    }
                }
            }
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null)
                {
                    if(isGPSEnabled && locationObject != null) {
                        Location locationTemp = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (locationTemp != null) {
                            locationObject.setLatitude((locationObject.getLatitude() + locationTemp.getLatitude()) / 2);
                            locationObject.setLongitude((locationObject.getLongitude() + locationTemp.getLongitude()) / 2);
                        }
                    }
                    if (locationObject != null) {
                        myGeoPoint = new GeoPoint(locationObject.getLatitude(), locationObject.getLongitude());
                    }
                }
            }
        }
    }

    private void setGeoPointOnMap()
    {
        if(mapController!= null && myGeoPoint != null)
        {
            mapController.setCenter(myGeoPoint);
            databaseReference.child("latitude").setValue(myGeoPoint.getLatitude());
            databaseReference.child("longitude").setValue(myGeoPoint.getLongitude());

        }
        if(onCreate)
        {
            onCreate = false;
            reloadPlaces = true;
            loadPlacesMarker();
        }
    }

    private void getUsersLocation()
    {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList = new ArrayList<>();
                markersList = new ArrayList<>();

                markersList.add(myLocationMarker);
                try {
                    for (int i = 0; i < placesListMaps.size(); i++) {
                        Place place = placesListMaps.get(i);
                        OverlayItem overlayItem = new OverlayItem(place.getName(), place.getType(), new GeoPoint(place.getLatitude(), place.getLongitude()));
                        overlayItem.setMarker(placesMarkerIcon);
                        markersList.add(overlayItem);
                    }
                    for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                        User user = dataSnap.getValue(User.class);
                        if (user != null && !user.getUserID().equalsIgnoreCase(userID)) {
                            userList.add(user);
                            OverlayItem overlayItem = new OverlayItem(user.getName(), user.getSurname(), new GeoPoint(user.getLatitude(), user.getLongitude()));
                            overlayItem.setMarker(userMarkerIcon);
                            markersList.add(overlayItem);
                        }
                    }
                    zoomLlv += (flagZoomLvl)? 0.1f : -0.1f;
                    flagZoomLvl = !flagZoomLvl;
                    mapController.setZoom(zoomLlv);
                    setMarkerOverLayListener();
                }
                catch (Exception e)
                {
                    //
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.v("onLocationChanged", "lat = " + location.getLatitude() + "  ; long = " + location.getLongitude());
        myGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        myLocationMarker = new OverlayItem("My loc", "", myGeoPoint);
        myLocationMarker.setMarker(newUserMarkerIcon);
        if(markersList.size() == 0)
            markersList.add(myLocationMarker);
        else
            markersList.set(0, myLocationMarker);

        if(mapView.getOverlays().isEmpty())
            mapView.getOverlays().add(mOverlayMyLocationMarker);
        else
            mapView.getOverlays().set(0, mOverlayMyLocationMarker);

        setMarkerOverLayListener();

        setGeoPointOnMap();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void setMarkerOverLayListener()
    {

        mapView.getOverlays().clear();

        mOverlayMyLocationMarker = new ItemizedOverlayWithFocus<OverlayItem>( markersList,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        if(index == 0)
                        {
                            startActivity(new Intent(MapsActivity.this, ProfileActivity.class));
                        }
                        else
                        {
                            if(index > 0 && index <= placesListMapsSort.size())
                            {
                                Intent i = new Intent(MapsActivity.this, PlaceActivity.class);
                                int tempI = index - 1;
                                Place place = placesListMapsSort.get(tempI);
                                i.putExtra("name", place.getName());
                                i.putExtra("reviewValid", false);
                                startActivity(i);
                            }
                            else
                            {
                                Intent userIntent = new Intent(MapsActivity.this, FriendsProfileActivity.class);
                                User user = userList.get(index % (1 + placesListMapsSort.size()));
                                userIntent.putExtra("userName", user.getName());
                                userIntent.putExtra("userSurName", user.getSurname());
                                userIntent.putExtra("userNickname", user.getNickname());
                                userIntent.putExtra("userPoens", user.getPoens());
                                startActivity(userIntent);
                            }
                        }
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, this);
        mapView.getOverlays().add(mOverlayMyLocationMarker);
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
                    intent = new Intent(MapsActivity.this, PlacesListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_maps:
                    return true;
                case R.id.navigation_friends:
                    intent = new Intent(MapsActivity.this, FriendsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.navigation_rank:
                    intent = new Intent(MapsActivity.this, RankActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    public void onResume(){
        super.onResume();
        navView.getMenu().findItem(R.id.navigation_maps).setChecked(true);
        try {
            if (mapView != null)
                mapView.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        }
        catch (Exception e)
        {
            //
        }
       loadPlacesMarker();
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

    private void loadPlacesMarker()
    {
        if(markersList.size() > 0 && reloadPlaces)
        {
            reloadPlaces = false;
            placesListMaps = new ArrayList<>();
            placesListMaps = placesList;
            if(markersList.size() > 1) {
                myLocationMarker = markersList.get(0);
                markersList = new ArrayList<>();
                markersList.add(myLocationMarker);
            }
            userListSort = new ArrayList<>();
            placesListMapsSort = new ArrayList<>();
            for (int i = 0; i < placesListMaps.size(); i++)
            {
                Place place = placesListMaps.get(i);
                if(distancePicked >= distance(myGeoPoint.getLatitude(), myGeoPoint.getLongitude(), place.getLatitude(), place.getLongitude()))
                {
                    if(typePlacePickedPosition > 0)
                    {
                        if(place.getType().equalsIgnoreCase(items[typePlacePickedPosition])) {
                            OverlayItem overlayItem = new OverlayItem(place.getName(), place.getType(), new GeoPoint(place.getLatitude(), place.getLongitude()));
                            overlayItem.setMarker(placesMarkerIcon);
                            markersList.add(overlayItem);
                            placesListMapsSort.add(place);
                        }
                    }
                    else
                    {
                        OverlayItem overlayItem = new OverlayItem(place.getName(), place.getType(), new GeoPoint(place.getLatitude(), place.getLongitude()));
                        overlayItem.setMarker(placesMarkerIcon);
                        markersList.add(overlayItem);
                        placesListMapsSort.add(place);
                    }
                }
            }
            if(userList != null && userList.size() > 0)
            {
                for (int i = 0; i < userList.size(); i++)
                {
                    User user = userList.get(i);
                    if(distancePicked >= distance(myGeoPoint.getLatitude(), myGeoPoint.getLongitude(), user.getLatitude(), user.getLongitude()))
                    {
                        OverlayItem overlayItem = new OverlayItem(user.getName(), user.getSurname(), new GeoPoint(user.getLatitude(), user.getLongitude()));
                        overlayItem.setMarker(userMarkerIcon);
                        markersList.add(overlayItem);
                        userListSort.add(user);
                    }
                }
            }
            zoomLlv += (flagZoomLvl)? 0.1f : -0.1f;
            flagZoomLvl = !flagZoomLvl;
            mapController.setZoom(zoomLlv);
            setMarkerOverLayListener();
        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344; //km
        dist *= 1000; //m

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void onPause(){
        super.onPause();
        try {
            if (mapView != null)
                mapView.onPause();
        }
        catch (Exception e)
        {
            //
        }
    }

    private void addReview()
    {
        findViewById(R.id.addValidReviewBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.pick_place_dialog, null);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                List<Place> tempList = new ArrayList<>();
                for (int i = 0; i < placesListMaps.size(); i++) {
                    Place place = placesListMaps.get(i);
                    if (REVIEW_RADIUS >= distance(myGeoPoint.getLatitude(), myGeoPoint.getLongitude(), place.getLatitude(), place.getLongitude())) {
                        tempList.add(place);
                    }
                }
                RecyclerView recyclerViewValidPlaces;
                ValidPlacesAdapter validPlacesAdapter;
                RecyclerView.LayoutManager validPlacesLayoutManager;
                recyclerViewValidPlaces = (RecyclerView) mView.findViewById(R.id.placesRV);
                validPlacesLayoutManager = new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.VERTICAL, false);
                recyclerViewValidPlaces.setLayoutManager(validPlacesLayoutManager);
                validPlacesAdapter = new ValidPlacesAdapter(MapsActivity.this, tempList, dialog);
                recyclerViewValidPlaces.setItemAnimator(new DefaultItemAnimator());
                recyclerViewValidPlaces.setAdapter(validPlacesAdapter);

                Button btnCancel = mView.findViewById(R.id.btnCancel);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if(dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

}
