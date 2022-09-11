package com.example.cygoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class activity_user_details extends AppCompatActivity {

    private ImageView profileImage,editImage;
    private Button btnSave;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        profileImage = findViewById(R.id.profileImage);
        editImage = findViewById(R.id.editImage);

        btnSave = findViewById(R.id.btnSave);

        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("CustomerProPics");

        Uri uri = firebaseUser.getPhotoUrl();

        if(uri == null) {
            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(activity_user_details.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup1, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.addImg:
                                    Intent intent = new Intent();
                                    intent.setType("image/**");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        if(uri != null){

            Picasso.get().load(uri).into(profileImage);

            editImage.setVisibility(View.VISIBLE);
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(activity_user_details.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.addImg:
                                    Intent intent = new Intent();
                                    intent.setType("image/**");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                                    return true;
                                case R.id.remImg:
                                    Toast.makeText(activity_user_details.this, "Remove Image", Toast.LENGTH_LONG).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popupMenu.show();
                }
            });

        }



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_user_details.this,activity_home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            progressBar.setVisibility(View.VISIBLE);

            uriImage = data.getData();

            InputImage img;

            try {
                img = InputImage.fromFilePath(activity_user_details.this, uriImage);
                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

                labeler.process(img)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                int found=0;

                                for (ImageLabel label : labels) {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();

                                    if(text.equals("Smile") && confidence>0.5){
                                        found=1;
                                        profileImage.setImageURI(uriImage);
                                        uploadPicFirebase();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }

                                if(found==0){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(activity_user_details.this, "Upload a image of you with clear face and smile", Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity_user_details.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity_user_details.this, "Error", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void uploadPicFirebase() {
        if(uriImage != null){
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "." + getFileExt(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();

                            firebaseUser.updateProfile(profileUpdates);

                            Toast.makeText(activity_user_details.this, "Upload Successful", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity_user_details.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(activity_user_details.this, "No File Selected", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileExt(Uri uriImage) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uriImage));
    }
}