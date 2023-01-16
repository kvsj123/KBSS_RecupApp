package com.example.kbss_recupapp;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;




import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.types.Formatted;
import com.mapbox.mapboxsdk.style.types.FormattedSection;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements PermissionsListener,OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private Button button_cam;
    private TextView showLatLng;

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";

    private boolean markerSelected = false;
    private ValueAnimator markerAnimator;

    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private String currentStyle;



    public static MapsActivity inflate(LayoutInflater layoutInflater) {
        return null;
    }

    private void openCamera(){
        Intent intent3 =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivity(intent3);
    }

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

// This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_maps);

        //button_cam = (Button) findViewById(R.id.button_cam);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);









        Activity context = this;



      /*  button_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    openCamera();
                } else {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                            Manifest.permission.CAMERA);
                }


            }


        });

       */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_markerList:
             Intent intent = new Intent(MapsActivity.this,MarkerList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
             startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }

    public void GetMarkersFromDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://kbssrecupapp-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference ref = database.getReference("Markers");



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot markerSnapshot : snapshot.getChildren()) {

                    double lat = markerSnapshot.child("lat").getValue(Double.class);
                    double lng = markerSnapshot.child("lng").getValue(Double.class);

                    String titel = getIntent().getStringExtra("titelMarker");

                    LatLng latLng = new LatLng(lng, lat);
                    MakeMarker(latLng,titel);
                    SetMarkerStyle();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("tag", "Failed to read value.", error.toException());
            }
        });
    }



    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        symbolLayerIconFeatureList.clear();

        MapsActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);
                mapboxMap.addOnMapClickListener(MapsActivity.this);

                GetMarkersFromDatabase();




            }


        });

    }





    private void addDestinationIconSymbolLayer(Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(MapsActivity.this.getResources(),
                R.drawable.red_marker));

        GeoJsonSource geoJsonSource = new GeoJsonSource(SOURCE_ID);
        loadedMapStyle.addSource(geoJsonSource);

        SymbolLayer destinationSymbolLayer = new SymbolLayer(LAYER_ID,SOURCE_ID);
        destinationSymbolLayer.withProperties(iconImage(ICON_ID),
                iconAllowOverlap(true),
                iconIgnorePlacement(true));
        loadedMapStyle.addLayer(destinationSymbolLayer);

    }




    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        LatLng pos = new LatLng(point.getLongitude(),point.getLatitude());


        Intent intent = new Intent(this, MarkerFormActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("latitude_key", pos.getLatitude());
        intent.putExtra("longitude_key", pos.getLongitude());
        startActivity(intent);

        String titel = getIntent().getStringExtra("titelMarker");

        MakeMarker(point,titel);
        SetMarkerStyle();

        return false;
    }

    public void MakeMarker(@NonNull LatLng point, String titel){
        Feature markerFeature = Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
        markerFeature.addStringProperty("titelo", titel);
        markerFeature.addNumberProperty("lng", point.getLongitude());
        markerFeature.addNumberProperty("lat", point.getLatitude());
        symbolLayerIconFeatureList.add(markerFeature);
    }

    public void SetMarkerStyle(){

        if (!mapView.isDestroyed()) {

            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                    // Add the SymbolLayer icon image to the map style
                    .withImage(ICON_ID, BitmapFactory.decodeResource(
                            MapsActivity.this.getResources(), R.drawable.red_marker))

                    // Adding a GeoJson source for the SymbolLayer icons.
                    .withSource(new GeoJsonSource(SOURCE_ID,
                            FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

                    // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                    // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                    // the coordinate point. This is offset is not always needed and is dependent on the image
                    // that you use for the SymbolLayer icon.
                    .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                            .withProperties(
                                    iconImage(ICON_ID),
                                    textColor(Color.GREEN),
                                    textField(Expression.concat(
                                            Expression.literal("longitude: "),
                                            Expression.get("lng"),
                                            Expression.literal("            latitude: "),
                                            Expression.get("lat"))),
                                    iconAllowOverlap(true),
                                    iconIgnorePlacement(true)
                            )
                    ));
        }
    }







    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Enable the most basic pulsing styling by ONLY using
// the `.pulseEnabled()` method
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .pulseEnabled(true)
                    .build();

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                // proceed with your code
                enableLocationComponent(mapboxMap.getStyle());
            } else {
                // permission denied
                // show an explanation or disable the feature that requires this permission
            }
        }
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mapView.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    public View getImageCaptureButton() {
        return null;
    }

    public int getRoot() {
        return 0;
    }



}