package com.google.bustracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DriverViewActivity extends AppCompatActivity {
    private RecyclerView view;
    private ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_view);
        bar = (ProgressBar)findViewById(R.id.progressBar2);
        view = (RecyclerView)findViewById(R.id.driver_recycler);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("drivers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Person> list = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Person person = new Person((String) snapshot.child("name").getValue(),snapshot.getKey(),(String)snapshot.child("password").getValue(),(String)snapshot.child("bus").getValue());
                    list.add(person);
                }
                bar.setVisibility(View.GONE);
                if(list.size()==0){
                    Toast.makeText(DriverViewActivity.this, "No Drivers Added yet", Toast.LENGTH_SHORT).show();
                    return;
                }
                view.setLayoutManager(new LinearLayoutManager(DriverViewActivity.this));
                view.setAdapter(new Adapter(list,DriverViewActivity.this,1));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
