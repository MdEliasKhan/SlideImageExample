package com.currentaffairs21.elias.slideimageexample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadHotOfferActivity extends AppCompatActivity {
    private ImageView add_offerPhotoIV;
    private TextView offerDetailsTV;
    private Button btn_addOffer;
    private Uri photo_Uri = null;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private String details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_hot_offer);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        add_offerPhotoIV = findViewById(R.id.add_offerPhotoIV_id);
        offerDetailsTV = findViewById(R.id.offerDetailsTV_id);
        btn_addOffer = findViewById(R.id.btn_addOffer_id);


        //ADD HOT OFFER BUTTON
        btn_addOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                uploadPhoto();
            }
        });

        // ADD IMAGE BUTTON
        add_offerPhotoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(UploadHotOfferActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(UploadHotOfferActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(UploadHotOfferActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        ActivityCompat.requestPermissions(UploadHotOfferActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }else{
                        BringImagePicker();
                    }
                }else{
                    BringImagePicker();
                }
            }
        });

    }

    private void uploadPhoto() {
        details = offerDetailsTV.getText().toString().trim();
        if(add_offerPhotoIV == null){
            Toast.makeText(UploadHotOfferActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
        }else if(details.isEmpty()){
            Toast.makeText(UploadHotOfferActivity.this, "Enter Details", Toast.LENGTH_SHORT).show();
        }else{
            final String randomName = UUID.randomUUID().toString();
            StorageReference imagePath = storageReference.child("HotPost_images").child(randomName + "jpg");
            imagePath.putFile(photo_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(UploadHotOfferActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                        uploadDetails(task);
                    }else{
                        Toast.makeText(UploadHotOfferActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void uploadDetails(Task<UploadTask.TaskSnapshot> task) {
        final View view = findViewById(android.R.id.content);
        String download_uri = task.getResult().getDownloadUrl().toString();
        Map<String,Object>hotPostInfo = new HashMap<>();
        hotPostInfo.put("hotPost_Image",download_uri);
        hotPostInfo.put("postDetails",details);
        firebaseFirestore.collection("Hot_Posts").add(hotPostInfo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(UploadHotOfferActivity.this, "Successfully Posted", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view,"Successfully Posted",Snackbar.LENGTH_LONG).show();
                }else{
                    Toast.makeText(UploadHotOfferActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                photo_Uri = result.getUri();
                add_offerPhotoIV.setImageURI(photo_Uri);
//                isChanged = true;
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(2,2)
                .start(UploadHotOfferActivity.this);
    }
}
