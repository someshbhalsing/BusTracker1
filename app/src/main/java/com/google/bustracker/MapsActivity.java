
package com.google.bustracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        logOut = (Button)findViewById(R.id.button2);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatabaseHelper(MapsActivity.this).signOut();
                startActivity(new Intent(MapsActivity.this,MainActivity.class));
                finish();
            }
        });
        mapFragment.getMapAsync(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){

            }else{
                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},303);
                return;
            }
            Toast.makeText(this, "Cannot start service without the location permission", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",getPackageName(),null)));
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("students/"+new DatabaseHelper(this).currentUserEmail());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setCurrentLocation();
                String driver = (String) dataSnapshot.child("driver").getValue();
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("drivers/"+driver+"/lastLocation");
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        double accuracy = 10;
                        try {
                            accuracy= (double) dataSnapshot.child("accuracy").getValue();
                        }catch (ClassCastException e){

                        }
                        setLocation(new LatLng((double)dataSnapshot.child("latitude").getValue(),(double)dataSnapshot.child("longitude").getValue()),accuracy);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setLocation(LatLng latlng, double accuracy) {
        if(latlng!=null){
            mMap.clear();
            CircleOptions options = new CircleOptions();
            options.center(latlng);
            options.radius(accuracy);
            options.strokeColor(Color.BLUE);
            options.strokeWidth(3);
            options.fillColor(Color.argb(119,91,113,209));
            mMap.addCircle(options);
            setCurrentLocation();
            mMap.addMarker(new MarkerOptions().position(latlng).title("Driver Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,14));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==303){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                recreate();
            }else{
                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",getPackageName(),null)));
                Toast.makeText(this, "Cannot start service without the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void setCurrentLocation(){
        GPSTracker tracker = new GPSTracker(MapsActivity.this);
        if(tracker.canGetLocation()){
            Location location = tracker.getLocation();
            if(location==null) {
                Toast.makeText(this, "location null", Toast.LENGTH_SHORT).show();
                return;
            }
            LatLng latiLongi = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latiLongi).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latiLongi,15));
            return;
        }
        Log.d("ABC","Cannot get location");
    }
}
