package com.example.posnegrating;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RatingActivity extends AppCompatActivity {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // Initialize db
        OpenHelper helper = new OpenHelper(getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onUpgrade(db, 0, 1);
        // insert title
//        SharedPreferences pref = this.getSharedPreferences("Title", Context.MODE_PRIVATE);
//        String text = pref.getString("title", "");
//
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        insertData(db, "title", title);
        // insert start time
        Date date = new Date();
        String time = formatter.format(date);
        insertData(db, time, "start");
        // set buttons
        Button btnNegative = findViewById(R.id.btnNegative);
        Button btnPositive = findViewById(R.id.btnPositive);
        Button btnEnd = findViewById(R.id.btnEnd);

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String time = formatter.format(date);
                insertData(db, time,"negative");
            }
        });

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String time = formatter.format(date);
                insertData(db, time,"positive");
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                String time = formatter.format(date);
                insertData(db, time,"end");
                finish();
            }
        });
    }

    private void insertData(SQLiteDatabase db, String time, String value) {
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("value", value);
        db.insert("rating_db", null, values);
    }
}