package edu.nitmas.rohan.spotfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class AboutUs extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }
    public void onBackPressed() {
        startActivity(new Intent(AboutUs.this, MainActivity.class));
        finish();
    }

}

