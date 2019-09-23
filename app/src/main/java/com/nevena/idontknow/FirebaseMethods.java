package com.nevena.idontknow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.Models.Review;
import com.nevena.idontknow.Models.User;


public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";
    public FirebaseAuth auth;
    public FirebaseDatabase firebaseDatabase;
    public FirebaseAuth.AuthStateListener authListener;
    public DatabaseReference myRef;
    public Context mContext;
    public String userID;

    public FirebaseMethods(Context context) {
        auth = FirebaseAuth.getInstance();
        mContext = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        if (auth.getCurrentUser() != null) {
            userID = auth.getCurrentUser().getUid();
        }
    }

    public void registerNewEmail(final String email, String password, final String index) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();

                        } else if (task.isSuccessful()) {
                            sendVerificationEmail();
                            userID = auth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }




    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(mContext, "We have sent an email with a confirmation link to your email address!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext, "Couldn't send verification email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void addNewUser (String name, String surname, String nickname,String email) {

        User user = new User(name, surname, nickname, email, userID);

        myRef.child("users")
                .child(userID)
        //        .child(nickname)
                .setValue(user);
        //  .setValue(email);

    }


    public void addNewPlace (String name, String type, String thumbnailUrl, String address, String workingHours, double rate, double latitude, double longitude) {

        Place place = new Place(name, type, thumbnailUrl, address, workingHours, rate, latitude, longitude);

        myRef.child("places")
                .child(name)
                .setValue(place);
    }

    public void addNewPlace (Place p) {

        //String id = myRef.push().getKey();
        Place place = new Place(p);

        myRef.child("places")
                .child(place.getName())
                .setValue(place);
    }

    public void addNewReview(String place, Review r)
    {
        Review review = new Review(r);
        myRef.child("places")
                .child(place)
                .child("review")
                .child(review.getUserID())
                .setValue(review);
    }

}





















