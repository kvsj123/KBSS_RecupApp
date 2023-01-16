package com.example.kbss_recupapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MarkerFormActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int REUEST_CODE = 22;


    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};


    private ActivityResultLauncher<Intent> CameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), isGranted -> {
                return;
            });


    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                   // dispatchTakePictureIntent();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });



    private FirebaseDatabase database;



    EditText afvalNaam, afvalBeschrijving;
    ImageView fotoUpload;
    Button afvalOpslaan;
    Button butt1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_form);

        database = FirebaseDatabase.getInstance("https://kbssrecupapp-default-rtdb.europe-west1.firebasedatabase.app");



        afvalNaam = (EditText) findViewById(R.id.afvalNaam);
        afvalBeschrijving = (EditText) findViewById(R.id.afvalBeschrijving);
        fotoUpload = (ImageView) findViewById(R.id.fotoUpload);

        butt1 = (Button) findViewById(R.id.butt1);

        butt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (ContextCompat.checkSelfPermission(
                        MarkerFormActivity.this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   startActivityForResult(cameraIntent,REUEST_CODE);
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.CAMERA);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String afvalN = afvalNaam.getText().toString();
        String afvalB = afvalBeschrijving.getText().toString();


        double latitude = getIntent().getDoubleExtra("latitude_key",0.0);
        double longitude = getIntent().getDoubleExtra("longitude_key",0.0);

        if (requestCode == REUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                fotoUpload.setImageBitmap(photo);

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://kbssrecupapp.appspot.com/");

                String fileName = UUID.randomUUID().toString();

                StorageReference storageRef = storage.getReference().child("images/" + fileName + ".jpg");

                Bitmap bitmap = ((BitmapDrawable) fotoUpload.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();


                UploadTask uploadTask = storageRef.putBytes(data1);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://kbssrecupapp-default-rtdb.europe-west1.firebasedatabase.app");
                        DatabaseReference ref = database.getReference("Markers");
                        DatabaseReference ref2 = ref.child(fileName);



                                  // Get the URL of the uploaded image


    Marker marker = new Marker(afvalN, afvalB, "", latitude, longitude);
    ref.child(fileName).setValue(marker);



    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            String uurl = uri.toString();

            Map<String, Object> updates = new HashMap<>();
            updates.put("foto", uurl);

            ref2.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MarkerFormActivity.this, "Toegevoegd", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MarkerFormActivity.this, MapsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("titelMarker", afvalN);
                        finish();
                        startActivity(intent);

                    } else {
                        Toast.makeText(MarkerFormActivity.this, "baaad", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    });



                            }


                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        });

                    }

            } else {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                super.onActivityResult(requestCode, resultCode, data);
            }


    }


    @Override
    public void onClick(View view) {

    }
}