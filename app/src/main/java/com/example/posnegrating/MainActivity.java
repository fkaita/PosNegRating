package com.example.posnegrating;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_WRITE_EX_STR = 1;
//    private SharedPreferences title;
//    private String title;
//    private EditText text;

    ActivityResultLauncher resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent resultData  = result.getData();
                    if (resultData  != null) {
                        Uri uri = resultData.getData();
                        try(OutputStream outputStream =
                                    getContentResolver().openOutputStream(uri)) {
                            if(outputStream != null){
                                OpenHelper helper = new OpenHelper(getApplicationContext());
                                SQLiteDatabase db = helper.getReadableDatabase();
                                Log.d("debug","**********Cursor");
                                Cursor cursor = db.query(
                                        "rating_db",
                                        new String[] { "time", "value" },
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                );
                                cursor.moveToFirst();
                                for (int i=0; i < cursor.getCount() ;i++){
                                    StringBuilder sbuilder = new StringBuilder();
                                    sbuilder.append(cursor.getString(0));
                                    sbuilder.append(", ");
                                    sbuilder.append(cursor.getString(1));
                                    sbuilder.append("\n");
                                    outputStream.write(sbuilder.toString().getBytes());
                                    cursor.moveToNext();
                                }
                                cursor.close();
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get permission
        if (Build.VERSION.SDK_INT >= 23) {
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_CONTACTS
                        },
                        PERMISSION_WRITE_EX_STR);
            }
        }

        //Set title
        EditText text = (EditText) findViewById(R.id.txtTitle);

//        Intent i = new Intent(this, RatingActivity.class);

//        startActivity(i);

        //        SharedPreferences pref = this.getSharedPreferences("Title", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("title", txt);
//        editor.apply();

        //Set starting time
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = text.getText().toString();
                Intent intent = new Intent(MainActivity.this, RatingActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        Button btnExport = findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String time = formatter.format(date);
                // Export data into txt file.
                String fileName = "rating_" + time + ".txt";
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, fileName);
                resultLauncher.launch(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length <= 0) {
            return;
        }
        switch (requestCode) {
            case PERMISSION_WRITE_EX_STR: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /// 許可が取れた場合・・・
                } else {
                    /// 許可が取れなかった場合・・・
                    Toast.makeText(this,
                            "Cannot launch the app", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            return;
        }
    }

}