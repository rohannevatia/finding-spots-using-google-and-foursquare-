  package edu.nitmas.rohan.spotfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;



  public class MainActivity extends AppCompatActivity {

      private TextView tv1;
      private int radius;
      private Button btngoto, btntourist;



      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
          setSupportActionBar(toolbar);
          checkFirstRun();
          loadpreferences();
          tv1 = (TextView) findViewById(R.id.main_tv1);
          tv1.setText(" Set Radius Limit in Preferences");
          blink();
          btngoto = (Button) findViewById(R.id.btn_goto);
          btntourist = (Button) findViewById(R.id.btn_tourist);
          btngoto.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(MainActivity.this, MapsActivity.class));


              }
          });
          btntourist.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  startActivity(new Intent(MainActivity.this, FsquareActivity.class));
              }
          });
      }


      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.menu_main, menu);
          return true;
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
          int id = item.getItemId();

          if (id == R.id.action_settings) {
              startActivity(new Intent(this, SettingsActivity.class));
              finish();

          }
          if (id == R.id.action_about) {
              startActivity(new Intent(MainActivity.this, AboutUs.class));
              finish();

          }
          if (id == R.id.action_exit) {
              onBackPressed();
          }

          return super.onOptionsItemSelected(item);
      }

      public void onBackPressed() {
          AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
          builder.setTitle("Exit");
          builder.setMessage("Do you want to exit?");
          builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                  finish();
              }
          });
          builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
              }
          });
          AlertDialog alert1 = builder.create();
          alert1.show();
      }

      void loadpreferences() {
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
          Log.i("MainActivity", ""+prefs.getString("radius", "5000"));
          if(prefs.getString("radius", "5000").isEmpty()) {
              Log.i("MainActivity", "radius is empty");
          }else
              radius = Integer.parseInt(prefs.getString("radius", "5000"));
          String rad=prefs.getString("radius", "5000");
          Log.i("MainActivity", "radius: " + radius + " : " + rad);
          if(radius==0){
              AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
              builder.setTitle("WARNING");
              builder.setIcon(android.R.drawable.ic_dialog_alert);
              builder.setMessage("Radius limit can't be set zero or left blank. Setting back to default radius!!");
              builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }
              });
              AlertDialog alert3 = builder.create();
              alert3.show();
              SharedPreferences.Editor editor = prefs.edit();
              editor.putString("radius", "5000");
              editor.commit();
          }
          if(radius>40000){
              AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
              builder.setTitle("WARNING");
              builder.setIcon(android.R.drawable.ic_dialog_alert);
              builder.setMessage("Outside permissible radius limit. Search within radius limit up to 40000 metres. Setting back to default radius!!");
              builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }
              });
              AlertDialog alert4 = builder.create();
              alert4.show();
              SharedPreferences.Editor editor = prefs.edit();
              editor.putString("radius", "5000");
              editor.commit();
          }
      }

      private void blink() {
          final Handler handler = new Handler();
          new Thread(new Runnable() {
              @Override
              public void run() {
                  int timeToBlink = 500;
                  try {
                      Thread.sleep(timeToBlink);
                  } catch (Exception e) {
                  }
                  handler.post(new Runnable() {
                      @Override
                      public void run() {
                          TextView txt = (TextView) findViewById(R.id.main_tv1);
                          if (txt.getVisibility() == View.VISIBLE) {
                              txt.setVisibility(View.INVISIBLE);
                          } else {
                              txt.setVisibility(View.VISIBLE);
                          }
                          blink();
                      }
                  });
              }
          }).start();
      }
      public void checkFirstRun() {
          boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
          if (isFirstRun){
              AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
              builder.setTitle("INSTRUCTIONS");
              builder.setMessage("The App has two tabs Basic utilities for commonly used utilities like ATM, Bank, Airport etc and the other tab contains Tourist spots like Museum, Aquarium etc. Set the radius limit, choose the tab as per your need locate and reach easily in fingertips!!");
              builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }
              });
              AlertDialog alert2 = builder.create();
              alert2.show();
              builder.setTitle("INSTRUCTIONS");
              builder.setMessage("Spot Finder helps you find new and fun places near your location, for you and your loved ones to spend some quality time together. Either it be dinner, coffee or even drinks, Spot Finder will provide you with all the available places near you, with details for each and every one of them. " +
                      "It is also a great navigation tool to take on a vacation as you can find your way around an unfamiliar city.");
              builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }
              });
              AlertDialog alert1 = builder.create();
              alert1.show();

              getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                      .edit()
                      .putBoolean("isFirstRun", false)
                      .apply();
          }
      }
  }
