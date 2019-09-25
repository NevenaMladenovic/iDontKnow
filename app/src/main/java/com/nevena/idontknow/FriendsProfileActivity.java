package com.nevena.idontknow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendsProfileActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_profile);

        String userName, userSurName, userNickname;
        int userPoens;
        TextView userNameTV, userSurNameTV, userNicknameTV, userPoensTV;

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userSurName = intent.getStringExtra("userSurName");
        userNickname = intent.getStringExtra("userNickname");
        userPoens = intent.getIntExtra("userPoens", 0);

        userNicknameTV = findViewById(R.id.friend_nickname);
        userNameTV = findViewById(R.id.friend_name);
        userSurNameTV = findViewById(R.id.friend_surname);
        userPoensTV = findViewById(R.id.friend_poens);


        userNameTV.setText(userName);
        userNicknameTV.setText(userNickname);
        userSurNameTV.setText(userSurName);
        userPoensTV.setText(String.valueOf(userPoens));

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
