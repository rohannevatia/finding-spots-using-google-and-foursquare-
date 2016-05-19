package edu.nitmas.rohan.spotfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


public class SplashActivity extends Activity {
ProgressBar progress;
    private static int SPLASH_TIME_OUT = 3000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progress=(ProgressBar)findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (isInternetAvailable(SplashActivity.this)) {
                    Intent i = new Intent(SplashActivity.this,
                            MainActivity.class);
                    startActivity(i);

                    progress.setVisibility(View.GONE);
                    finish();
                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("No Internet Connection");
                    builder.setMessage("OOPS..!!!It seems there is no Internet Connection,Please Check");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info == null) {
            return false;
        } else {
            return true;
        }
    }

}


