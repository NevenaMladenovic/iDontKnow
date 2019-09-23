package com.nevena.idontknow.newActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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
import com.nevena.idontknow.Models.Place;
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
    List<Place> placesListMaps;
    List<User> userList;
    boolean onCreate;
    Drawable newUserMarkerIcon, placesMarkerIcon, userMarkerIcon;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseReference;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_maps);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        databaseReference = mDatabase.child(auth.getUid());

        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        userList = new ArrayList<>();
        onCreate = true;
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        placesListMaps = placesList;

        placesMarkerIcon = getResources().getDrawable(R.drawable.places_marker);
        userMarkerIcon = getResources().getDrawable(R.drawable.users_marker);

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        Context ctx = getApplicationContext();
                        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

                        mapView = (MapView) findViewById(R.id.mapview);
                        // Inflate the layout for this fragment

                        mapView.setTileSource(TileSourceFactory.MAPNIK);

                        mapView.setBuiltInZoomControls(false);
                        mapView.setMultiTouchControls(true);

                        mapController = mapView.getController();
                        mapController.setZoom(17.5);

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
                        mapView.getOverlays().add(mOverlayMyLocationMarker);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        markersList = new ArrayList<OverlayItem>();

        setMarkerOverLayListener();

//        mapView.getOverlays().add(mOverlayMyLocationMarker);
        getUsersLocation();
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

                for (int i = 0; i < placesListMaps.size(); i++)
                {
                    Place place = placesListMaps.get(i);
                    OverlayItem overlayItem = new OverlayItem(place.getName(), place.getType(), new GeoPoint(place.getLatitude(), place.getLongitude()));
                    overlayItem.setMarker(placesMarkerIcon);
                    markersList.add(overlayItem);
                }
                for (DataSnapshot dataSnap : dataSnapshot.getChildren())
                {
                    User user = dataSnap.getValue(User.class);
                    if(user != null && !user.getUserID().equalsIgnoreCase(userID)) {
                        userList.add(user);
                        OverlayItem overlayItem = new OverlayItem(user.getName(), user.getSurname(), new GeoPoint(user.getLatitude(), user.getLongitude()));
                        overlayItem.setMarker(userMarkerIcon);
                        markersList.add(overlayItem);
                    }
                }
                setMarkerOverLayListener();

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
                            if(index > 0 && index <= placesListMaps.size())
                            {
                                Intent i = new Intent(MapsActivity.this, PlaceActivity.class);
                                int tempI = index - 1;
                                Place place = placesListMaps.get(tempI);
                                i.putExtra("name", place.getName());
                                startActivity(i);
                            }
                            else
                            {
                                //todo user
                            }
                        }
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, this);
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
            for (int i = 0; i < placesListMaps.size(); i++)
            {
                Place place = placesListMaps.get(i);
                OverlayItem overlayItem = new OverlayItem(place.getName(), place.getType(), new GeoPoint(place.getLatitude(), place.getLongitude()));
                overlayItem.setMarker(placesMarkerIcon);
                markersList.add(overlayItem);
            }
            if(userList != null && userList.size() > 0)
            {
                for (int i = 0; i < userList.size(); i++)
                {
                    User user = userList.get(i);
                    OverlayItem overlayItem = new OverlayItem(user.getName(), user.getSurname(), new GeoPoint(user.getLatitude(), user.getLongitude()));
                    overlayItem.setMarker(userMarkerIcon);
                    markersList.add(overlayItem);
                }
            }
            setMarkerOverLayListener();
        }
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

}
