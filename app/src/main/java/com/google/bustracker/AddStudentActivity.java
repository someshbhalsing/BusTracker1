package com.google.bustracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText password;
    private Spinner spinner;
    private Button submit;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        name = (EditText)findViewById(R.id.s_name);
        email = (EditText)findViewById(R.id.s_email);
        password = (EditText)findViewById(R.id.s_password);
        spinner = (Spinner)findViewById(R.id.s_spinner);
        submit = (Button)findViewById(R.id.s_submit);
        intent = getIntent();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("drivers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    list.add(snapshot.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddStudentActivity.this,android.R.layout.simple_spinner_item,list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(intent.hasExtra("updateStudent")){
            submit.setText("Update");
            setTitle("Update Student");
            email.setText(intent.getStringExtra("uname"));
            email.setEnabled(false);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(name.getText())){
                    name.setError("Mandatory Field");
                    return;
                }
                if(TextUtils.isEmpty(email.getText())){
                    email.setError("Mandatory Field");
                    return;
                }
                if(TextUtils.isEmpty(password.getText())||password.getText().length()<6){
                    password.setError("Invalid data");
                    return;
                }
                if(TextUtils.isEmpty((String)spinner.getSelectedItem())){
                    Toast.makeText(AddStudentActivity.this, "Select a valid driver email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(email.getText().toString().contains(".")||email.getText().toString().contains("$")||email.getText().toString().contains("#")||email.getText().toString().contains("[")||email.getText().toString().contains("]")){
                    email.setError("Cannot contain . $ # [ ]");
                    return;
                }
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("students/"+email.getText().toString());
                ref.child("name").setValue(name.getText().toString());
                ref.child("password").setValue(password.getText().toString());
                ref.child("driver").setValue(spinner.getSelectedItem());
                if(!intent.hasExtra("updateStudent")) {
                    Toast temp = Toast.makeText(AddStudentActivity.this, "Student added Successfully", Toast.LENGTH_SHORT);
                    temp.show();
                }else{
                    Toast.makeText(AddStudentActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
