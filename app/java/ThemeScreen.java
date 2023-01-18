package com.pixhunter.friendsbills;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Theme screen with logo image - appears before app is running
 */
public class ThemeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // after theme mainActivity will runs
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        try {
            // how long logo will appears
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finish();
    }

}