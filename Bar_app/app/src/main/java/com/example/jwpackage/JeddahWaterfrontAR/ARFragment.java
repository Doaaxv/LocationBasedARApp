package com.example.jwpackage.JeddahWaterfrontAR;



import android.annotation.SuppressLint;;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.apprating.AppRatingDialog;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.architect.ArchitectView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ARFragment extends Fragment {

    public static ArchitectView architectView;
    FusedLocationProviderClient fusedLocProv;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    public static Context ctx;
    public static BackgroundWorker bw, bw2;
    public static Double x, y;
    public static ArrayList<String> servicesX = new ArrayList<>();
    public static ArrayList<String> servicesY = new ArrayList<>();
    public static String id;

    public ARFragment() {
        // Required empty public constructor
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ctx = getContext();
        View v = inflater.inflate(R.layout.fragment_ar, container, false);
        super.onCreateView(inflater, container, savedInstanceState);

        bw = new BackgroundWorker(ctx);
        bw.execute("ARFragment");

        bw2 = new BackgroundWorker(ctx);
        bw2.execute("FavoritesList");


        this.architectView = v.findViewById(R.id.architectView);
        final ArchitectStartupConfiguration config = new ArchitectStartupConfiguration();
        config.setLicenseKey(getResources().getString(R.string.WikitudeKey));
        this.architectView.onCreate(config);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        architectView.onPostCreate();
        try {
            //check each service if it was in the user's favorite list, to change the heart icon in the marker's information page in AR view
            isfav();
            this.architectView.load("file:///android_asset/ARfolder/index.html");
            //get the markers' information from the database then send it to the JavaScript files
            ArData();
            //to get JSON object from Javascript
            architectView.addArchitectJavaScriptInterfaceListener(new ArchitectJavaScriptInterfaceListener() {
                @Override
                public void onJSONObjectReceived(JSONObject jsonObject) {
                    try {
                        String type = (String) jsonObject.get("type");
                        x = (Double) jsonObject.get("x");
                        y = (Double) jsonObject.get("y");

                        if (type.equalsIgnoreCase("Rate")) {
                            AppRatingDialog.Builder a = new AppRatingDialog.Builder();
                            a.setPositiveButtonText("Submit")
                                    .setNegativeButtonText("Cancel")
                                    .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite ok", "very Good", "Excellent"))
                                    .setDefaultRating(1)
                                    .setTitle("Rate the Service")
                                    .setDescription("Please select some stars and give your feedback")
                                    .setTitleTextColor(R.color.colorPrimary)
                                    .setDescriptionTextColor(R.color.colorPrimary)
                                    .setHint("Please write your comment here...")
                                    .setHintTextColor(R.color.colorAccent)
                                    .setCommentTextColor(android.R.color.white)
                                    .setCommentBackgroundColor(R.color.colorPrimaryDark)
                                    .setWindowAnimation(R.style.RatingDialogFadeAnim)
                                    .create(getActivity())
                                    .show();

                        } else if (type.equalsIgnoreCase("favorite")) {
                            Looper.prepare();
                            String choice = (String) jsonObject.get("choice");
                            if (choice.equalsIgnoreCase("1")) {
                                choice = "2";
                            } else {
                                choice = "1";
                            }
                            addDeleteFromFav(x, y, choice);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {

        }
    }

    //// Send AR data to JavaScript
    public void ArData() throws ExecutionException, InterruptedException {
        String result = bw.get();
        String[] resultArr = result.split("-");

        final JSONArray pois = new JSONArray();
        final String ATTR_LOGED = "isloged";
        final String ATTR_ID = "id";
        final String ATTR_NAME = "name";
        final String ATTR_DESCRIPTION = "description";
        final String ATTR_LATITUDE = "latitude";
        final String ATTR_LONGITUDE = "longitude";
        final String ATTR_ALTITUDE = "altitude";
        final String ATTR_IMAGE = "Image";
        final String ISFAVE = "isfav";
        final float UNKNOWN_ALTITUDE = -32768f;

        String isloged;
        String email = common.sp.getString(ConstantFields.userName, "").toString();

        if(email==""){
            isloged = "guest";
        }else{
            isloged="signed";
        }

        int flag = 0;
        int j = 0;
        for (int i = 0; i <= resultArr.length - 5; i = i + 5) {
            final HashMap<String, String> poiInformation = new HashMap<String, String>();

            for (int d = 0; d < servicesX.size(); d++) {
                if (servicesX.get(d).equalsIgnoreCase(resultArr[i]) && servicesY.get(d).equalsIgnoreCase(resultArr[i + 1])) {
                    flag = 2;
                    break;
                } else {
                    flag = 1;
                }
            }

            if (flag == 2) {
                poiInformation.put(ISFAVE, "2");
            } else {
                poiInformation.put(ISFAVE, "1");
            }
            poiInformation.put(ATTR_LOGED,isloged);
            poiInformation.put(ATTR_LATITUDE, resultArr[i]);
            poiInformation.put(ATTR_LONGITUDE, resultArr[i + 1]);
            poiInformation.put(ATTR_IMAGE, resultArr[i + 2]);
            poiInformation.put(ATTR_NAME, resultArr[i + 3]);
            poiInformation.put(ATTR_DESCRIPTION, resultArr[i + 4]);
            poiInformation.put(ATTR_ID, Integer.toString(j));
            poiInformation.put(ATTR_ALTITUDE, String.valueOf(UNKNOWN_ALTITUDE));
            pois.put(new JSONObject(poiInformation));
            j++;
        }

        String methodname = "World.dbData";
        final String js = (methodname + "( " + pois.toString() + " );");
        this.architectView.callJavascript(js);
    }

    //favorite methods
    private void addDeleteFromFav(Double x, Double y, String choice) {
        String type = "Favorites";
        BackgroundWorker backgroundWorker = new BackgroundWorker(ctx);
        backgroundWorker.execute(type, String.valueOf(x), String.valueOf(y), choice);
    }

    //check if a service is in the user's favorite list
    public void isfav() throws ExecutionException, InterruptedException {
        String result = bw2.get();
        if (!result.equalsIgnoreCase("SOMETHING WRONG")) {
            String[] resArr = result.split("-");
            for (int i = 0; i <= resArr.length - 5; i = i + 5) {
                servicesX.add(resArr[i + 3]);
                servicesY.add(resArr[i + 4]);
            }
        }
    }

    //location stuff
    @SuppressLint("MissingPermission")
    public void LocationStuff() {
        buildLocationRequest();
        buildLocationCallback();

        //create fusedlocation
        fusedLocProv = LocationServices.getFusedLocationProviderClient(ctx);
        fusedLocProv.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());

    }

    private void buildLocationCallback() {
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    architectView.setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getAccuracy());
                }
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(5);

    }

    //////////////////////
    @Override
    public void onResume() {
        super.onResume();
        architectView.onResume();
        LocationStuff();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.architectView != null) {
            this.architectView.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        architectView.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
