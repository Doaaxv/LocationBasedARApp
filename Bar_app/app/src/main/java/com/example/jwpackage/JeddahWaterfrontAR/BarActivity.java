package com.example.jwpackage.JeddahWaterfrontAR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.json.*;

import java.text.*;
import java.util.*;


public class BarActivity extends AppCompatActivity implements RatingDialogListener {

    private FrameLayout fragment_container;
    public static DrawerLayout drawer;
    final int REQ_CODE = 5600;
    public static TextView temp, userNameET;
    public static ImageView wcondition;
    public static String userName;
    RelativeLayout container;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);

        fragment_container = findViewById(R.id.fragment_container);
        container = findViewById(R.id.container);
        context = getBaseContext();

        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, REQ_CODE);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView navigation = findViewById(R.id.navigation);

        ///////////////////// Bottom Menu
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                                                           @Override
                                                           public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                                                               switch (item.getItemId()) {
                                                                   case R.id.navigation_ar:
                                                                       loadFragment(new ARFragment());
                                                                       break;

                                                                   case R.id.navigation_map:
                                                                       loadFragment(new MapFragment());
                                                                       break;

                                                                   case R.id.navigation_event:
                                                                       loadFragment(new event_Fragment());
                                                                       break;
                                                               }
                                                               return true;
                                                           }
                                                       }
        );

        navigation.setSelectedItemId(R.id.navigation_map);
        loadFragment(new MapFragment());


        /////////////////////  Drawer Menu
        NavigationView navigationView = findViewById(R.id.nav_view);


        String userEm = common.sp.getString(ConstantFields.userEmail, "").toString();

        if (userEm == "") {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_favorit).setVisible(false);
            nav_Menu.findItem(R.id.logOut).setVisible(false);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.call) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new callsFragment())
                            .commit();
                } else if (id == R.id.nav_favorit) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new favoritesFragment())
                            .commit();

                } else if (id == R.id.nav_share) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    Double latitude = MapFragment.currentLocation.latitude;
                    Double longitude = MapFragment.currentLocation.longitude;

                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "\n" + "https://www.google.com/maps/?q=" + latitude + "," + longitude);
                    if (share.resolveActivity(getPackageManager()) != null) {
                        startActivity(Intent.createChooser(share, "Share Location"));
                    }
                } else if (id == R.id.nav_send) {

                    String[] to = {"jeddahwaterfront.app@gmail.com"};
                    Intent sendemail = new Intent(Intent.ACTION_SEND);
                    sendemail.putExtra(Intent.EXTRA_EMAIL, to);
                    sendemail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    sendemail.setType("message/rfc822");
                    if (sendemail.resolveActivity(getPackageManager()) != null) {
                        startActivity(Intent.createChooser(sendemail, "Send Email"));
                    }
                } else if (id == R.id.nav_exit) {
                    Exit();
                } else if (id == R.id.logOut) {
                    logOut();
                } else if (id == R.id.nav_home) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new MapFragment())
                            .commit();
                }else if(id == R.id.nav_help){
                    help();
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }


        });

        /// Drawer Menu Header
        View headView = navigationView.getHeaderView(0);
        temp = headView.findViewById(R.id.temp);
        userNameET = headView.findViewById(R.id.userEmail);
        wcondition = headView.findViewById(R.id.wcondition);
        setWeather();

    }

    ///////////////////// Load Fragment
    public void loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    ///////////////////// Weather method for the drawer header
    public void setWeather() {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=jeddah,sa&appid=47e1a53f2b2db3be6a3c085fb3c41463&units=Metric";

        JsonObjectRequest jR = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_obj = response.getJSONObject("main");
                    String tempr = main_obj.getString("temp");
                    JSONArray arr = response.getJSONArray(("weather"));
                    JSONObject object = arr.getJSONObject(0);
                    String description = object.getString("description");
                    String conditionIcon = description.replace(" ", "");
                    Date currentTime = Calendar.getInstance().getTime();
                    DateFormat df = new SimpleDateFormat("HH:mm:ss");
                    String time = df.format(currentTime);
                    String timeString = time.substring(0, 2);
                    int timeInt = Integer.parseInt(timeString);
                    temp.setText(tempr);
                    common.sp = getSharedPreferences(ConstantFields.userInfo, Context.MODE_PRIVATE);
                    common.editor = common.sp.edit();
                    userName = common.sp.getString(ConstantFields.userName, "").toString();
                    userNameET.setText(userName);
                    //set the weather condition image based on the time of the day (day or night)
                    if (timeInt > 18 || timeInt < 6) {
                        conditionIcon = conditionIcon + "n";
                    } else {
                        conditionIcon = conditionIcon + "m";
                    }
                    int infowindowImage = getResources().getIdentifier(conditionIcon, "drawable", "com.example.jwpackage.JeddahWaterfrontAR");
                    wcondition.setImageResource(infowindowImage);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jR);
    }

    ///exit from the app method
    public void Exit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    //logout of the app method
    public void logOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                common.editor.putBoolean(ConstantFields.userIsLoggedIn, false);
                common.editor.clear();
                common.editor.commit();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    //help methods
    public void help(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("Jeddah Waterfront AR application facilitate the access to various locations in Jeddah waterfront.\nThere are important points that we would like to clarify to the user so please click next");

        builder.setNegativeButton("NEXT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Guesthelp();
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    public void Guesthelp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guest");
        builder.setMessage("When entering the application as a guest special features such as rating, add to favorite list and parking place will be hidden.");

        builder.setNegativeButton("Previous", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                help();
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ARhelp();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    public void ARhelp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Live View Pointer");
        builder.setMessage("After clicking on the pointer in AR view the arrow will be displayed when camera is not in the marker scope.");

        builder.setNegativeButton("Previous", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Guesthelp();
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    /// AR Rating Method
    @Override
    public void onPositiveButtonClicked(int value, String comment) {
        if (comment.length() <= 200) {
            String evaluation = String.valueOf(value);
            String type = "UserRatingService";
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, String.valueOf(ARFragment.x), String.valueOf(ARFragment.y), evaluation, comment);
        } else {
            Toast.makeText(this, "Error!! Comment can't be more than 200 characters", Toast.LENGTH_LONG);
        }
    }
    @Override
    public void onNegativeButtonClicked() {

    }

    /// Request permission
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                //MapFragment.mMap.setMyLocationEnabled(true);
                MapFragment.mf.LocationStuff();
        }

    }




}