package com.nevena.idontknow;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nevena.idontknow.Adapters.ReviewsAdapter;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.Models.Review;
import com.nevena.idontknow.Models.User;

import java.util.ArrayList;
import java.util.List;

public class PlaceActivity extends AppCompatActivity
{
    private Button writeReview, save;

    private ImageView logo;
    private TextView name, rate, workingHours, address;

    int inputRate;
    public String inputComment, userID, placeID;

    private Context mContext;

    private RecyclerView recyclerView;
    public static List<Review> reviewsList;
    public static List<User> usersList;
    public ReviewsAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseMethods firebaseMethods;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mContext = PlaceActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
       // firebaseMethods = new FirebaseMethods(this);

        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        reviewsList = new ArrayList<>();
        usersList = new ArrayList<>();
        mAdapter = new ReviewsAdapter(this, reviewsList, usersList, userID);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        initLayout();

        //what place is selected
        Intent i = getIntent();
        placeID = i.getStringExtra("name");
        boolean reviewValid = i.getBooleanExtra("reviewValid", false);
        if(writeReview != null && reviewValid)
        {
            writeReview.setVisibility(View.VISIBLE);
            initListeners();
        }

        findPlace(placeID);




    }


    public void getReviewsList()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref = databaseReference.child("places").child(placeID).child("review");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null)
                {
                    reviewsList = new ArrayList<>();
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                        Review review = singleSnapshot.getValue(Review.class);
                        if(review!=null)
                        {
                            reviewsList.add(review);
                        }
                    }
                    if(reviewsList != null && reviewsList.size() > 0)
                        getUsersList();
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

    public void getUsersList()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot != null)
                    {
                        usersList = new ArrayList<>();
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren())
                        { User user = singleSnapshot.getValue(User.class);
                            if(user!=null)
                            {
                                for (int i=0; i < reviewsList.size(); i++)
                                {
                                    if(user.getUserID().equalsIgnoreCase(reviewsList.get(i).getUserID()))
                                    {
                                        usersList.add(user);
                                    }
                                }
                            }
                        }
                        if(usersList != null && usersList.size() > 0)
                            mAdapter.setLists(reviewsList, usersList);

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

    private void initLayout()
    {
        writeReview = findViewById(R.id.btn_writeReview);
        save = findViewById(R.id.btn_save);

        name = findViewById(R.id.place_txt_name);
        rate = findViewById(R.id.place_txt_rate);
        workingHours = findViewById(R.id.place_txt_workingHours);
        address = findViewById(R.id.place_txt_address);

        logo = findViewById(R.id.place_img_logo);
    }

    private void initListeners()
    {
        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PlaceActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_write_review, null);



                final EditText review =  mView.findViewById(R.id.etxt_review);

                //Spinner menu for users rate
                final Spinner spinner = mView.findViewById(R.id.spinner);
                String[] items = new String[]{"-select-", "5", "4", "3", "2", "1"};
                ArrayAdapter<String>adapter = new ArrayAdapter<String>(PlaceActivity.this,
                        android.R.layout.simple_spinner_item,items);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        Log.v("item", (String) parent.getItemAtPosition(position));
                        if(position > 0)
                        {
                            inputRate = Integer.parseInt(spinner.getSelectedItem().toString());
                        }
                        else{
                            // show toast select gender
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

                save = mView.findViewById(R.id.btn_save);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if(!review.getText().toString().isEmpty() || spinner.getSelectedItemPosition() != 0)
                        {
                            inputComment = review.getText().toString();

                            Review r = new Review(inputComment, inputRate, userID);
                            firebaseMethods.addNewReview(placeID, r);

                            //calculating places rate
                            updateRate(inputRate);
                            int tmp = 0;
                            if(!review.getText().toString().isEmpty())
                            {
                                tmp++;
                            }
                            if(spinner.getSelectedItemPosition() != 0)
                            {
                                tmp++;
                            }
                            if(tmp == 2)
                            {
                                tmp = 3; //bonus 1p
                            }

                            //update users poens for given review
                            findUser(tmp);


                            Toast.makeText(PlaceActivity.this, getString(R.string.error_review), Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else
                        {
                            Toast.makeText(PlaceActivity.this, getString(R.string.success_review), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                dialog.show();

            }
        });
    }

    public void findUser(int newPoens)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference Ref = reference.child(auth.getUid());


        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                updatePoens(newPoens, user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public void  updatePoens(int newPoens, User u)
    {
        int poens = u.getPoens();
        poens += newPoens;
        myRef.child("users")
                .child(userID)
                .child("poens")
                .setValue(poens);
        findPlace(placeID);
    }

    public void updateRate(int inputRate)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("places");
        DatabaseReference ref = databaseReference.child(placeID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Place place = dataSnapshot.getValue(Place.class);
                double oldRate = place.getRate();
                double newRate;
                if(oldRate != 0)
                {
                    newRate = (oldRate + inputRate) / 2;

                    //update rate on activity after users given rate
                    rate.setText(String.valueOf(newRate));
                }
                else
                {
                    newRate = inputRate;
                }

                myRef.child("places")
                        .child(placeID)
                        .child("rate")
                        .setValue(newRate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void findPlace(String placeID)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("places");
        DatabaseReference ref = databaseReference.child(placeID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                    Place place = dataSnapshot.getValue(Place.class);
                    setPlaceData(place);
                    getReviewsList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setPlaceData(Place place){
        name.setText(place.getName());
        rate.setText(String.valueOf(place.getRate()));
        workingHours.setText(place.getWorkingHours());
        address.setText(place.getAddress());

        Glide.with(this)
                .load(place.getThumbnailUrl())
                .into(logo);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
