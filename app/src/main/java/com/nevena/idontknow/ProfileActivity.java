package com.nevena.idontknow;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nevena.idontknow.Models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = ProfileActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;

    private EditText nickname, name, surname, email, poens;

    private Button btn_save, btn_logout;
    public String userID, userNickname;
    private User updateUser, newUser;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;

    @BindView(R.id.profile_img_profile)
    ImageView imgProfile;

    ImageView imgPlus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();


        FirebaseUser user = auth.getCurrentUser();
        userID = user.getUid();


        initLayout();
        initListeners();



        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        //    ImagePickerActivity.clearCache(this);

        if (!readSP().equalsIgnoreCase(""))
        {
            loadProfile(readSP());
        }
        else
        {
            loadProfileDefault();
        }


        findUser();

    }

    public void findUser()
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference Ref = reference.child(auth.getUid());
//        Query query = reference
////                .child("users")
////                .orderByChild("userID")
////                .equalTo(userID);

        Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //subjectID = singleSnapshot.getKey();
                setUserData(user);
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                    User user = singleSnapshot.getValue(User.class);
//                    //subjectID = singleSnapshot.getKey();
//                    setUserData(user);
//                }
//                if (!dataSnapshot.exists()){
//                  //  Toast.makeText(ProfileActivity.this, "Ne postoji user sa ovim imenom!", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setUserData(User user){
        updateUser = user;
        nickname.setText(user.getNickname());
        name.setText(user.getName());
        surname.setText(user.getSurname());
        email.setText(user.getEmail());
        poens.setText(String.valueOf(user.getPoens()));
    }

    private void initLayout()
    {
        imgPlus = (ImageView) findViewById(R.id.img_plus);

        nickname = findViewById(R.id.profile_etxt_nickname);
        name = findViewById(R.id.profile_etxt_name);
        surname = findViewById(R.id.profile_etxt_surname);
        email = findViewById(R.id.profile_etxt_email);
        poens = findViewById(R.id.profile_etxt_poens);

        btn_save = findViewById(R.id.profile_btn_save);
        btn_logout = findViewById(R.id.profile_btn_logout);

    }

    private void initListeners()
    {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newNickname = nickname.getText().toString();
                String newName = name.getText().toString();
                String newSurname = surname.getText().toString();
                String newEmail = email.getText().toString();

                if(!updateUser.getNickname().equals(newNickname)){

                    myRef.child("users")
                            .child(userID)
                            .child("nickname")
                            .setValue(newNickname);

                }

                if(!updateUser.getName().equals(newName)){

                    myRef.child("users")
                            .child(userID)
                            .child("name")
                            .setValue(newName);
                }

                if(!updateUser.getSurname().equals(newSurname)){

                    myRef.child("users")
                           // .child("nena")
                            .child(userID)
                            .child("surname")
                            .setValue(newSurname);
                }

                if(!updateUser.getEmail().equals(newEmail)){

                    myRef.child("users")
                        //    .child("nena")
                            .child(userID)
                            .child("email")
                            .setValue(newEmail);
                }

                newUser = new User(newName, newNickname, newNickname, newEmail, userID);


                finish();
            }
        });


        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                auth.signOut();
                startActivity(intent);
            }
        });

    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        GlideApp.with(this).load(url)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        imgPlus.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark));    }

    private void loadProfileDefault() {
        GlideApp.with(this).load(R.drawable.ic_avatar)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        imgPlus.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    @OnClick({R.id.img_plus, R.id.profile_img_profile})
    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(ProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(ProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    Uri uri = data.getParcelableExtra("path");
                    // You can update this bitmap to your server
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    //saveToInternalStorage(bitmap);
                    // loading profile image from local cache
                    writeSp(uri.toString());
                    loadProfile(uri.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writeSp(directory.getAbsolutePath());
        // return directory.getAbsolutePath();
    }

    private String readSP()
    {
        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "Images", MODE_PRIVATE);
        String imagePath = sharedPreferencesA.getString("ImagePath", "");

        return imagePath;
    }

    private void writeSp(String path)
    {
        SharedPreferences sharedPreferencesA = getSharedPreferences(getPackageName() + "Images", MODE_PRIVATE);
        SharedPreferences.Editor editorA = sharedPreferencesA.edit();
        editorA.putString("ImagePath", path); //key, value
        editorA.apply();
    }
}

