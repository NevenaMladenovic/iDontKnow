package com.nevena.idontknow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.nevena.idontknow.Models.*;


public class RegisterActivity extends AppCompatActivity
{
    private Button btnLogin, btnRegister;
    private EditText inputName, inputSurname, inputNickname, inputEmail, inputPassword, inputPasswordCheck;
    private String name, surname, nickname, userId, email, password, passwordCheck;

    private FirebaseAuth auth;
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth.AuthStateListener authListener;
    private Context mContext;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private static final String TAG = "";
    private String append = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = RegisterActivity.this;

        inputName = (EditText) findViewById(R.id.register_etxt_name);
        inputSurname = (EditText) findViewById(R.id.register_etxt_surname);
        inputNickname = (EditText) findViewById(R.id.register_etxt_nickname);
        inputEmail = (EditText) findViewById(R.id.register_etxt_email);
        inputPassword = (EditText) findViewById(R.id.register_etxt_password);
        inputPasswordCheck = (EditText) findViewById(R.id.register_etxt_password_check);

        btnLogin = (Button) findViewById(R.id.register_btn_login);
        btnRegister = (Button) findViewById(R.id.register_btn_register);

        auth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        init();
        setupFirebaseAuth();

    }


    private void init()
    {

        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                name = inputName.getText().toString();
                surname = inputSurname.getText().toString();
                nickname = inputNickname.getText().toString();
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                passwordCheck = inputPasswordCheck.getText().toString();

                //Check if all * fiels are filled
                if (checkInputs(nickname, email, password, passwordCheck))
                {
                    firebaseMethods.registerNewEmail(email, password, "");

                }

            }
        });
    }

    private boolean checkInputs(String nickname, String email, String password, String passwordCheck){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if (TextUtils.isEmpty(nickname))
        {
            Toast.makeText(mContext, "Please enter your nickname", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(email))
        {
            Toast.makeText(mContext, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(mContext, "Please enter your password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6)
        {
            Toast.makeText(mContext, "Your password must contain at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!TextUtils.equals(password, passwordCheck))
        {
            Toast.makeText(mContext, "Your passwords don't match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //If user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "onComplete: Successful signup");
                            firebaseMethods.addNewUser(name, surname, nickname, email);
                            Toast.makeText(mContext, "Successful signup!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    finish();

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    @Override
    public void onStart() {
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
}
