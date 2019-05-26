package com.nevena.idontknow;

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
import com.wang.avi.AVLoadingIndicatorView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnRegister;
    private Button btnLogin;
    private ImageView img;

    private static final String TAG = "";
    private FirebaseAuth mAuth;
    //    private ProgressBar progressBar;
    SignInButton btnLoginGoogle;
    private final static int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListner;

    View loadingHolder;
    AVLoadingIndicatorView avi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        //Check the current user
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        loadingHolder =  findViewById(R.id.loadingHolder);
        inputEmail = (EditText) findViewById(R.id.login_etxt_email);
        inputPassword = (EditText) findViewById(R.id.login_etxt_password);

        btnRegister = (Button) findViewById(R.id.login_btn_register);
        btnLogin = (Button) findViewById(R.id.login_btn_login);
        btnLoginGoogle = (SignInButton) findViewById(R.id.login_btn_login_google);

        //Loading screen
        avi =  findViewById(R.id.indicator);
        avi.show();

        new CountDownTimer(3000,100)
        {

            @Override
            public void onTick(long l)
            {

            }

            @Override
            public void onFinish()
            {
                loadingHolder.setVisibility(View.GONE);
            }
        }.start();

        // Check if email or password are empty
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(), "Please enter your email address.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(), "Please enter your password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //avi.show();

                // Authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                               // avi.hide();
                                if (task.isSuccessful())
                                {
                                    Log.d(TAG, "signInWithEmail:success");
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d(TAG, "singInWithEmail:Fail");
                                    Toast.makeText(LoginActivity.this, getString(R.string.failed_login), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

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

        mAuth = FirebaseAuth.getInstance();

        //If user is already singed in
        mAuthListner = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                if (firebaseAuth.getCurrentUser() != null)
                {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
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

