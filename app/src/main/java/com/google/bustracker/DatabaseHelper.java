package com.google.bustracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by somesh on 08/10/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "Bustracker.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE CURRENT_USER(EMAIL TEXT PRIMARY KEY,FLAG INT)");   //1:admin   2:student   3:driver
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addCurrentUser(String email,int flag){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("CURRENT_USER",null,null);
        ContentValues cv = new ContentValues();
        cv.put("EMAIL",email);
        cv.put("FLAG",flag);
        db.insert("CURRENT_USER",null,cv);
        db.close();
    }
    public int currentUserFlag(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("CURRENT_USER",new String[]{"FLAG"},null,null,null,null,null);
        if(cursor.getCount()==0){
            return 0;
        }
        cursor.moveToFirst();
        int flag = cursor.getInt(cursor.getColumnIndex("FLAG"));
        cursor.close();
        db.close();
        return flag;
    }
    public String currentUserEmail(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("CURRENT_USER",new String[]{"EMAIL"},null,null,null,null,null);
        cursor.moveToFirst();
        String email = cursor.getString(cursor.getColumnIndex("EMAIL"));
        cursor.close();
        db.close();
        return email;
    }
    public void signOut(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("CURRENT_USER",null,null);
        db.close();
    }
}
