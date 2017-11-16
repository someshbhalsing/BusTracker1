package com.google.bustracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Button view;
    long startTime = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {     // TO RUN TIMER ON BACKGROUND THREAD
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            seconds = seconds % 6;
            if(seconds == 5){
                setCurrentLocation();
            }
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                recreate();
            }else{
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",getPackageName(),null)));
                Toast.makeText(this, "Cannot start service without the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        view = (Button)findViewById(R.id.textView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatabaseHelper(DriverMapActivity.this).signOut();
                startActivity(new Intent(DriverMapActivity.this,MainActivity.class));
                finish();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                }else{
                    ActivityCompat.requestPermissions(DriverMapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
                    return;
                }
            Toast.makeText(this, "Cannot start service without the location permission", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",getPackageName(),null)));
            return;
       }
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setCurrentLocation(){
        GPSTracker tracker = new GPSTracker(DriverMapActivity.this);
        if(tracker.canGetLocation()){
            Location location = tracker.getLocation();
            if(location==null) {
                Toast.makeText(this, "location null", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("ABC",""+location.getAccuracy());
            FirebaseDatabase.getInstance().getReference("drivers/"+new DatabaseHelper(DriverMapActivity.this).currentUserEmail()+"/lastLocation").setValue(location);
            mMap.clear();
            mMap.setTrafficEnabled(true);
            LatLng latiLongi = new LatLng(location.getLatitude(),location.getLongitude());
            CircleOptions options = new CircleOptions();
            options.center(latiLongi);
            options.radius(location.getAccuracy());
            options.strokeColor(Color.BLUE);
            options.strokeWidth(3);
            options.fillColor(Color.argb(119,91,113,209));
            mMap.addCircle(options);
            mMap.addMarker(new MarkerOptions().position(latiLongi).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latiLongi,15));
            return;
        }
        Log.d("ABC","Cannot get location");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        Log.d("XYZ","Destroyed");
    }
}
