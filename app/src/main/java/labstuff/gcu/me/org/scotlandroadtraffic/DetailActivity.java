package labstuff.gcu.me.org.scotlandroadtraffic;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

/**
 * Created by Andre Queen on 12/03/2018.
 * Matric Number ; S1635221
 */

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        //set the back (up) button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //find all our view components
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);
        TextView urllink = (TextView) findViewById(R.id.urllink);
        TextView location = (TextView) findViewById(R.id.location);
//        TextView authors = (TextView) findViewById(R.id.authors);
//        TextView comments = (TextView) findViewById(R.id.comments);
        TextView startTime = (TextView) findViewById(R.id.startTime);
        TextView endTime = (TextView) findViewById(R.id.endTime);


        //collect our intent and populate our layout
        Intent intent = getIntent();

        String titleH = intent.getStringExtra("title");
        String information = intent.getStringExtra("description");
        String urlLink = intent.getStringExtra("urlLink");
        String locations = intent.getStringExtra("location");
        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");
        String datetime = intent.getStringExtra("datetime");
        String start = intent.getStringExtra("start");
        String end = intent.getStringExtra("end");

        if (end == null){
            endTime.setText("");
        }else{
            endTime.setText("Ends: " + end );
        }

        //set elements
        title.setText(titleH);
        description.setText(information);
        urllink.setText("Link: " + urlLink);
        location.setText(longitude + " " + latitude);
//        authors.setText(author);
//        comments.setText(comment);
        startTime.setText("Starts: " + start );

        //set the title of this activity to be the street name
        getSupportActionBar().setTitle(titleH + "(S1635221)");

        getLocationPermission();

        double LongTd = Double.parseDouble(longitude);
        double LatTd = Double.parseDouble(latitude);
        LatLng positioning = new LatLng(LongTd, LatTd);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


//        moveCamera(new LatLng(LongTd, LatTd), DEFAULT_ZOOM);

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    //vars
    private Boolean mLocationPermissionsGranted = false;

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(DetailActivity.this);
        //collect our intent and populate our layout
        Intent intent = getIntent();

        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");
        double LongTd = Double.parseDouble(longitude);
        double LatTd = Double.parseDouble(latitude);
        LatLng positioning = new LatLng(LongTd, LatTd);

        mMap.addMarker(new MarkerOptions().position(positioning));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(positioning));
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }



}