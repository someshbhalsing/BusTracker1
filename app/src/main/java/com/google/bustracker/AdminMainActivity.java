package com.google.bustracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AdminMainActivity extends AppCompatActivity {

    Button student;
    Button driver;
    Button viewStudents;
    Button viewDrivers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        viewDrivers = (Button)findViewById(R.id.view_driver);
        viewStudents = (Button)findViewById(R.id.view_student);
        student = (Button) findViewById(R.id.add_student);
        driver = (Button) findViewById(R.id.add_driver);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this,AddStudentActivity.class));
            }
        });
        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this,AddDriverActivity.class));
            }
        });
        viewStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this,StudentViewActivity.class));
            }
        });
        viewDrivers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this,DriverViewActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                new DatabaseHelper(this).signOut();
                startActivity(new Intent(AdminMainActivity.this,MainActivity.class));
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
