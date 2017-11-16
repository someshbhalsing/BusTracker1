package com.google.bustracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private RadioButton student;
    private RadioButton administrator;
    private RadioButton driver;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText)findViewById(R.id.login_email);
        password = (EditText)findViewById(R.id.login_password);
        student = (RadioButton)findViewById(R.id.login_student);
        administrator = (RadioButton)findViewById(R.id.login_administrator);
        driver = (RadioButton)findViewById(R.id.login_driver);
        login = (Button)findViewById(R.id.login_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString();
                String Password = password.getText().toString();
                if(TextUtils.isEmpty(Email))
                    email.setError("Cannot be left empty");
                else if(TextUtils.isEmpty(Password))
                    password.setError("Cannot be left empty");
                else{
                    if(administrator.isChecked()){
                        if(Email.contentEquals("admin")&&Password.contentEquals("admin@123")) {
                            startActivity(new Intent(MainActivity.this,AdminMainActivity.class));
                            finish();
                            new DatabaseHelper(MainActivity.this).addCurrentUser(Email,1);
                        }
                        else{
                            password.setError("Invalid Credentials");
                        }
                    }
                    else if(student.isChecked()){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("students");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(email.getText().toString())){
                                    String pass = (String)  dataSnapshot.child(email.getText().toString()).child("password").getValue();
                                    if(pass.contentEquals(password.getText().toString())){
                                        new DatabaseHelper(MainActivity.this).addCurrentUser(email.getText().toString(),2);
                                        startActivity(new Intent(MainActivity.this,afterParent.class));
                                        finish();                                    }
                                    else{
                                        password.setError("Password mismatch");
                                        return;
                                    }
                                }
                                else{
                                    email.setError("Invalid Email");
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    else if(driver.isChecked()){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("drivers");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(email.getText().toString())){
                                    String pass = (String) dataSnapshot.child(email.getText().toString()).child("password").getValue();
                                    if(pass.contentEquals(password.getText().toString())){
                                        new DatabaseHelper(MainActivity.this).addCurrentUser(email.getText().toString(),3);
                                        startActivity(new Intent(MainActivity.this,DriverMapActivity.class));
                                        finish();
                                    }
                                    else{
                                        password.setError("Password mismatch");
                                        return;
                                    }
                                }
                                else{
                                    email.setError("Invalid Email");
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int flag = new DatabaseHelper(this).currentUserFlag();
        switch (flag){
            case 1:
                startActivity(new Intent(MainActivity.this,AdminMainActivity.class));
                finish();
                break;
            case 2:
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
                finish();
                break;
            case 3:
                startActivity(new Intent(MainActivity.this,DriverMapActivity.class));
                finish();
                break;
        }
    }
}
