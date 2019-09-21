package com.nevena.idontknow;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nevena.idontknow.Models.User;
import com.wang.avi.AVLoadingIndicatorView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private EditText inputNickname, inputPassword;
    private Button btnLogin, btnRegister;
    private ImageView img;

    SignInButton btnLoginGoogle;
    private final static int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;
    public String userID;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private Context mContext;
    private String currentUserID;
    boolean isUserLoggedIn;
    private static final String TAG = "LoginActivity";

    View loadingHolder;
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("users");
        mContext = LoginActivity.this;
        firebaseMethods = new FirebaseMethods(mContext);


        currentUserID = "";
        isUserLoggedIn = false;
        new CountDownTimer(3000,100)
        {

            @Override
            public void onTick(long l) {   }

            @Override
            public void onFinish()
            {
                if (auth.getCurrentUser() != null)
                {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
//            userID = auth.getCurrentUser().getUid();
//            isUserLoggedIn = true;
//            checkUser(auth.getCurrentUser().getUid());
                    //   startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    //   finish();
                }
                else
                    loadingHolder.setVisibility(View.GONE);
            }
        }.start();

        initLayout();
        initListeners();

        avi.show();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setupFirebaseAuth();
//        // Check if email or password are empty
//        btnLogin.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                String email = inputEmail.getText().toString();
//                final String password = inputPassword.getText().toString();
//
//                if (TextUtils.isEmpty(email))
//                {
//                    Toast.makeText(getApplicationContext(), "Please enter your email address.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(password))
//                {
//                    Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                //avi.show();
//
//                // Authenticate user
//                mAuth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
//                        {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task)
//                            {
//                               // avi.hide();
//                                if (task.isSuccessful())
//                                {
//                                    Log.d(TAG, "signInWithEmail:success");
//                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Log.d(TAG, "singInWithEmail:Fail");
//                                    Toast.makeText(LoginActivity.this, getString(R.string.failed_login), Toast.LENGTH_LONG).show();
//                                }
//                            }
//                        });
//            }
//        });
    }

    private void initLayout()
    {
        inputNickname = (EditText) findViewById(R.id.login_etxt_nickname);
        inputPassword = (EditText) findViewById(R.id.login_etxt_password);

        btnRegister = (Button) findViewById(R.id.login_btn_register);
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        btnLoginGoogle = (SignInButton) findViewById(R.id.login_btn_login_google);

        loadingHolder =  findViewById(R.id.loadingHolder);
        avi =  findViewById(R.id.indicator);
    }

    private void initListeners()
    {
        btnLoginGoogle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nickname = inputNickname.getText().toString();
                final String password = inputPassword.getText().toString();

                if(checkInputs(nickname, password))
                {
//                    DatabaseReference databaseReference = myRef.child(auth.getUid());
//                    Query mQueryRef =  myRef.child("users").orderByChild("nickname").equalTo(nickname);
//                    mQueryRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            if(dataSnapshot!=null){
////                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
////                                    User u = childSnapshot.getValue(User.class);
////                                    String email = u.getEmail();
////
////                                }
//                                User u =  new User();
//                                u.setEmail(dataSnapshot.getValue(User.class).getEmail());
//                                String email = u.getEmail();
//
//                                auth.signInWithEmailAndPassword(email, password)
//                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                                FirebaseUser user = auth.getCurrentUser();
//
//                                                if (!task.isSuccessful()) {
//                                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
//                                                            Toast.LENGTH_SHORT).show();
//                                                }
//                                                else{
//                                                    try{
//                                                        if(user.isEmailVerified()){
//                                                            Log.d(TAG, "onComplete: success. email is verified.");
//                                                            checkUser(user.getUid());
//                                                            // checkUser(nickname);
//
//                                                        }else{
//                                                            Toast.makeText(mContext, "Email is not verified \ncheck your email inbox.", Toast.LENGTH_SHORT).show();
//                                                            auth.signOut();
//                                                        }
//
//                                                    }catch (NullPointerException e){
//                                                        Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
//                                                    }
//                                                }
//                                            }
//                                        });
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//
//                    });
//                    auth.signInWithEmailAndPassword(nickname, password)
//                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                    FirebaseUser user = auth.getCurrentUser();
//
//                                    if (!task.isSuccessful()) {
//                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                    else{
//                                        try{
//                                            if(user.isEmailVerified()){
//                                                Log.d(TAG, "onComplete: success. email is verified.");
//                                                checkUser(user.getUid());
//                                            }else{
//                                                Toast.makeText(mContext, "Email is not verified \ncheck your email inbox.", Toast.LENGTH_SHORT).show();
//                                                auth.signOut();
//                                            }
//
//                                        }catch (NullPointerException e){
//                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
//                                        }
//                                    }
//                                }
//                            });
                    auth.signInWithEmailAndPassword(nickname, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    FirebaseUser user = auth.getCurrentUser();

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        try{
                                            if(user!= null && user.isEmailVerified()){
                                                Log.d(TAG, "onComplete: success. email is verified.");
                                                checkUser(user.getUid());
                                                // checkUser(nickname);

                                            }else{
                                                Toast.makeText(mContext, "Email is not verified \ncheck your email inbox.", Toast.LENGTH_SHORT).show();
                                                auth.signOut();
                                            }

                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    private void checkUser(final String userID)
    {

        Query queryUser = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .orderByChild("userID")
                .equalTo(userID);


        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    User user = new User();
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();
                    user.setUserID(objectMap.get("userID").toString());
                    currentUserID = user.getUserID();
                    if(!currentUserID.isEmpty()) {
                        if(isUserLoggedIn) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean checkInputs(String email, String password){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(getApplicationContext(), "Please enter your email address.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupFirebaseAuth()
    {
        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {}
                else {}
            }
        };
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    private void login()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            //updateUI(user);

                            if(user != null)
                            {
////                                String name = user.getDisplayName();
////                                String surname = user.getDisplayName();
////                                String nickname = user.getDisplayName();
////                                String email = user.getEmail();
////                                String uid = user.getUid();
//
//                                for (UserInfo profile : user.getProviderData()) {
//                                    // Id of the provider (ex: google.com)
//                                    String providerId = profile.getProviderId();
//
//                                    // UID specific to the provider
//                                    String uid = profile.getUid();
//
//                                    // Name, email address, and profile photo Url
//                                    String name = profile.getDisplayName();
//                                    String email = profile.getEmail();
//                                }
                            }

                            if (auth.getCurrentUser() != null)
                            {
                                isUserLoggedIn = true;
                                checkUser(auth.getCurrentUser().getUid());
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Aut Fail", Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

}

