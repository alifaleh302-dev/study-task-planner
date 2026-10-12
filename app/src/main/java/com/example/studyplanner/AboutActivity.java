package com.example.studyplanner;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Static information screen. It needs no database and holds no logic beyond
 * showing the application version.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ((TextView) findViewById(R.id.textVersion))
                .setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));
    }
}
