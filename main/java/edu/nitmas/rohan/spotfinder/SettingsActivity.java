package edu.nitmas.rohan.spotfinder;

import android.content.Intent;
import android.os.Bundle;


public class SettingsActivity extends AppCompatPreferenceActivity {




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_headers);
    }

    @Override
    public void onBackPressed() {


        startActivity(new Intent(this, MainActivity.class));
            finish();

    }


}