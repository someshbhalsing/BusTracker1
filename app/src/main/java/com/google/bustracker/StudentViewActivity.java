package com.google.bustracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentViewActivity extends AppCompatActivity {

    private RecyclerView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view);
        view = (RecyclerView)findViewById(R.id.recycler);
        view.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("students");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Person> list = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Person person = new Person((String) snapshot.child("name").getValue(),snapshot.getKey(),(String)snapshot.child("password").getValue(),(String)snapshot.child("driver").getValue());
                    list.add(person);
                }
                if(list.size()==0){
                    return;
                }
                view.setLayoutManager(new LinearLayoutManager(StudentViewActivity.this));
                view.setAdapter(new Adapter(list,StudentViewActivity.this,0));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
