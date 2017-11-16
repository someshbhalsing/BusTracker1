package com.google.bustracker;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class afterParent extends AppCompatActivity {


    private Button trackbus;
    private Button call;
    private Button log_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_parent);
        trackbus = (Button)findViewById(R.id.bus_track);
        call = (Button)findViewById(R.id.call_driver);
        log_out = (Button)findViewById(R.id.log_out_parent);
        trackbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(afterParent.this,MapsActivity.class));
            }
        });
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("students/"+new DatabaseHelper(this).currentUserEmail());
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String driver = (String) dataSnapshot.child("driver").getValue();
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("drivers/"+driver+"/mobile");
                        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String mobile = (String) dataSnapshot.getValue();
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mobile)));
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
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatabaseHelper(afterParent.this).signOut();
                startActivity(new Intent(afterParent.this,MainActivity.class));
                finish();
            }
        });
    }
    public void fun5(View view){
        Intent i = new Intent(afterParent.this, MapsActivity.class);

        startActivity(i);
    }
}
