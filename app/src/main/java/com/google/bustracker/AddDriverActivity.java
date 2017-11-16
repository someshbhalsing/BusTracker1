package com.google.bustracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDriverActivity extends AppCompatActivity {

    private EditText name;
    private EditText email;
    private EditText mobile;
    private EditText password;
    private EditText busNo;
    private Button button;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);
        name = (EditText)findViewById(R.id.d_name);
        email = (EditText)findViewById(R.id.d_email);
        mobile = (EditText)findViewById(R.id.d_mobile);
        password = (EditText)findViewById(R.id.d_password);
        busNo = (EditText)findViewById(R.id.d_bus);
        button = (Button)findViewById(R.id.d_submit);
        intent = getIntent();
        if(intent.hasExtra("updateDriver")){
            button.setText("Update");
            setTitle("Update Driver");
            email.setText(intent.getStringExtra("uname"));
            email.setEnabled(false);
        }
        button.setOnClickListener(new View.OnClickListener() {
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
                if(TextUtils.isEmpty(mobile.getText())||mobile.getText().length()!=10){
                    mobile.setError("Invaild Data");
                    return;
                }

                if(TextUtils.isEmpty(busNo.getText())){
                    busNo.setError("Mandatory Field");
                    return;
                }
                if(TextUtils.isEmpty(password.getText())||password.getText().length()<6){
                    password.setError("Invalid data");
                    return;
                }
                if(email.getText().toString().contains(".")||email.getText().toString().contains("$")||email.getText().toString().contains("#")||email.getText().toString().contains("[")||email.getText().toString().contains("]")){
                    email.setError("Cannot contain . $ # [ ]");
                    return;
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("drivers/"+email.getText().toString());
                reference.child("name").setValue(name.getText().toString());
                reference.child("mobile").setValue(mobile.getText().toString());
                reference.child("password").setValue(password.getText().toString());
                reference.child("bus").setValue(busNo.getText().toString());
                if(!intent.hasExtra("updateDriver")) {
                    Toast temp = Toast.makeText(AddDriverActivity.this, "Driver added Successfully", Toast.LENGTH_SHORT);
                    temp.show();
                }else{
                    Toast.makeText(AddDriverActivity.this, "Driver updated successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}