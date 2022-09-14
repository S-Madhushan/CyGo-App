package com.example.cygoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cygoapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class activity_home extends AppCompatActivity {

    String name;
    boolean complete;
    ImageView profileImage;
    TextView txtName, txtGreeting;
    RatingBar ratingBar;
    Button btnDrive, btnGo, btnBooked, btnRides, btnSettings,btnLogout;
    ProgressBar progressBar;
    FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profileImage=findViewById(R.id.imgUserAcc);
        txtName=findViewById(R.id.txtName);
        txtGreeting=findViewById(R.id.txtNote);
        ratingBar=findViewById(R.id.ratingBar);
        btnDrive=findViewById(R.id.btnDrivewithCyGoins);
        btnDrive.setEnabled(false);
        btnGo=findViewById(R.id.btnGowithCyGoins);
        btnGo.setEnabled(false);
        btnBooked=findViewById(R.id.BookedRides);
        btnBooked.setEnabled(false);
        btnRides=findViewById(R.id.OfferedRides);
        btnRides.setEnabled(false);
        btnSettings=findViewById(R.id.settings);
        btnLogout=findViewById(R.id.logout);
        progressBar=findViewById(R.id.progressBar);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(activity_home.this, "Something went wrong",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(activity_home.this,activity_sign_in.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Uri uri = firebaseUser.getPhotoUrl();
            if(uri != null) {
                Picasso.get().load(uri).into(profileImage);
            }
            checkEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserDetails(firebaseUser);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProfile.signOut();
                Toast.makeText(activity_home.this, "Logged Out",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(activity_home.this,activity_sign_in.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_home.this,activity_user_details.class));
            }
        });

        btnDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(activity_home.this,activity_drive.class));
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivity(new Intent(activity_home.this,activity_go.class));
            }
        });

        btnBooked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void checkEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showUserDetails(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("customers");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =  snapshot.getValue(User.class);
                if(user != null ){
                    name = firebaseUser.getDisplayName();
                    txtName.setText(name);
                    complete = user.isProfileCreated();
                    if(complete == true){
                        btnDrive.setEnabled(true);
                        btnGo.setEnabled(true);
                        btnBooked.setEnabled(true);
                        btnRides.setEnabled(true);
                        txtGreeting.setText("Hi "+name.split(" ")[0]+"!");
                    }else{
                        btnDrive.setEnabled(false);
                        btnGo.setEnabled(false);
                        btnBooked.setEnabled(false);
                        btnRides.setEnabled(false);
                        txtGreeting.setText("Please Complete Your Profile in Settings");
                    }

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity_home.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without verifying");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }
}