package com.example.jwpackage.JeddahWaterfrontAR;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter,
                                                RoutingListener, GoogleMap.OnInfoWindowClickListener {

    //map stuff
    public static SupportMapFragment MapFragment;
    public static GoogleMap mMap;
    public static MapFragment mf;
    public static Context ctx;

    //infowindow and marker stuff
    public static View infoBoxV;
    public static TextView infoBoxTitle,infoBoxDes;
    public static ImageView infoBoxImage;
    public static LatLng markerposition;
    public static List<String> pointersImages;
    public static List<Integer>markerCategory;
    public static List<Marker> markersList;

    //parking marker
    public static Marker parkingMarker;

    //distance and duration stuff
    public static TextView distanceTxt,durationTxt;
    public static ImageView distanceIV,durationIV;

    //route stuff
    public static Polygon AlNawrawsPol, AlAsdafPol, AltawheedPol, AlRemalPol, Allo2lo2Pol, AlsaiadPol, AlKaleejPol;
    public static LatLng currentLocation;
    private List<Polyline> polylines;

    //current location stuff
    private static FusedLocationProviderClient fusedLocProv;
    private static LocationRequest locationRequest;
    private static LocationCallback locationCallBack;

    //Buttons
    public static Button parkCarBtn;
    public static ImageView overlayIV;
    //Button's function keys
    public static Boolean parkCarKey = false;

    //for shared refrence checking
    public static String userEmail;

    //change map settings layout
    public static ImageView mapSat, mapTerr, mapNorm, exitimg, maptraff, mapdef,ximg;
    public static LinearLayout linlayout;

    //Bar stuff
    private TabLayout tabLayout;

    public MapFragment() {
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       //MapFragment object declared to be used in other classes.
        mf = new MapFragment();
        ctx = getContext();

        //declaring arrays to be used in the fragment
        polylines = new ArrayList<>();
        pointersImages = new ArrayList<>();
        markersList = new ArrayList<>();
        markerCategory = new ArrayList<>();

        //shared refrence declaring
        common.sp = ctx.getSharedPreferences(ConstantFields.userInfo, Context.MODE_PRIVATE);
        common.editor = common.sp.edit();
        userEmail = common.sp.getString(ConstantFields.userEmail, "").toString();

        //infowindow box declarations.
        infoBoxV = inflater.inflate(R.layout.infobox, container, false);
        infoBoxTitle = infoBoxV.findViewById(R.id.markertitle);
        infoBoxDes = infoBoxV.findViewById(R.id.markerDesc);
        infoBoxImage = infoBoxV.findViewById(R.id.markerImage);

        //map fragment initilising.
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        MapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (MapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            MapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, MapFragment).commit();
        }
        MapFragment.getMapAsync(this);

        //Declare fragment's buttons,imageviews,textviews
        overlayIV = v.findViewById(R.id.overlayIV);
        parkCarBtn = v.findViewById(R.id.parkCarBtn);
        distanceTxt = v.findViewById(R.id.markerdistt);
        durationTxt = v.findViewById(R.id.markerDurr);
        ximg = v.findViewById(R.id.ximg);
        mapSat = v.findViewById(R.id.mapSat);
        mapTerr = v.findViewById(R.id.mapTerr);
        mapNorm = v.findViewById(R.id.mapNorm);
        exitimg = v.findViewById(R.id.exitimg);
        linlayout = v.findViewById(R.id.mapset);
        maptraff = v.findViewById(R.id.maptraff);
        mapdef = v.findViewById(R.id.mapdef);
        distanceIV= v.findViewById(R.id.distanceIV);
        durationIV=v.findViewById(R.id.durationIV);

        //tab stuff
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.all));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.toilet));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.sledge));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_battery_charging_full_black));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_restaurant));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.sunbed));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.jed));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.fountainss));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position==0){
                    for(int i=0;i<markersList.size();i++){
                        markersList.get(i).setVisible(true);
                    }
                }else{
                    for(int i=0;i<markersList.size();i++){
                        if(markerCategory.get(i)==position){
                            markersList.get(i).setVisible(true);
                        }else{
                            markersList.get(i).setVisible(false);
                        }
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //if user is guest hide the parking button.
        if(userEmail==""){
            parkCarBtn.setVisibility(View.INVISIBLE);
        }

        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //connect to database to pin the markers
        BackgroundWorker bw = new BackgroundWorker(ctx);
        bw.execute("fragment_map");

        //show blue dot on current location
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // Move the map camera to Jeddah Waterfront
        LatLng JeddahWater = new LatLng(21.621406, 39.107177);
        float zoomLevel = 16.0f;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(JeddahWater, zoomLevel));
    //Calling the map styling method to draw polygones on the JeddahWaterfront Areas
        mapStyle();

        //Set the custom inforwindow
        mMap.setOnInfoWindowClickListener(this);

        //Click listeners for the map setting buttons.
        mapSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(mMap.MAP_TYPE_SATELLITE);
            }
        });
        mapTerr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(mMap.MAP_TYPE_TERRAIN);
            }
        });
        mapNorm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(mMap.MAP_TYPE_NORMAL);
            }
        });
        exitimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linlayout.setVisibility(View.GONE);
            }
        });
        maptraff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setTrafficEnabled(true);
            }
        });
        mapdef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setTrafficEnabled(false);
            }
        });
        overlayIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linlayout.setVisibility(View.VISIBLE);
            }
        });

        //Click listener for the parking button
        parkCarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userEmail != null) {
                    if (!parkCarKey) {
                        LocationStuff();
                        float zoomLevel = 16.0f;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
                        parkingMarker = mMap.addMarker(new MarkerOptions()
                                .title("Parking Place")
                                .snippet("parking location " + currentLocation)
                                .position(currentLocation)
                                .icon(BitmapDescriptorFactory.fromResource(ctx.getResources().getIdentifier("parking", "drawable", "com.example.jwpackage.JeddahWaterfrontAR"))));

                        int id = Integer.parseInt(parkingMarker.getId().replace("m", "").trim());

                        pointersImages.add(id, "parkingsec");
                        BackgroundWorker bw = new BackgroundWorker(ctx);
             bw.execute("parkingPlace", userEmail, "2", Double.toString(currentLocation.latitude), Double.toString(currentLocation.longitude));

                        parkCarKey = true;
                    } else {
                        BackgroundWorker bw = new BackgroundWorker(ctx);
                        parkingMarker.remove();
                        bw.execute("parkingPlace", userEmail, "3");

                        parkCarKey = false;
                    }
                }
            }
        });

        //Click listener for the route
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerposition = marker.getPosition();
                getRouteToMarker(markerposition);
                marker.showInfoWindow();
                return true;
            }
        });

        ximg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (polylines.size() > 0) {
                    for (Polyline poly : polylines) {
                        poly.remove();
                    }
                }
                distanceIV.setVisibility(View.GONE);
                durationIV.setVisibility(View.GONE);
                ximg.setVisibility(View.GONE);
                distanceTxt.setVisibility(View.GONE);
                durationTxt.setVisibility(View.GONE);

            }
        });

    }

    public void mapStyle() {

        //AlNawraws
        PolygonOptions AlNawraws = new PolygonOptions()
                .add(new LatLng(21.59144, 39.10622), new LatLng(21.59095, 39.1067), new LatLng(21.59068, 39.10735),
                        new LatLng(21.59058, 39.10833), new LatLng(21.59074, 39.10869), new LatLng(21.59114, 39.10924),
                        new LatLng(21.59106, 39.11023), new LatLng(21.59077, 39.11045), new LatLng(21.59044, 39.11042),
                        new LatLng(21.5901, 39.10972), new LatLng(21.58986, 39.10885), new LatLng(21.58886, 39.10747),
                        new LatLng(21.58859, 39.10664), new LatLng(21.58835, 39.10605), new LatLng(21.58923, 39.10546),
                        new LatLng(21.5895, 39.10538), new LatLng(21.58969, 39.10559), new LatLng(21.59132, 39.10578));

        AlNawrawsPol = mMap.addPolygon(AlNawraws.fillColor(0x55078095)
                .strokeColor(0x99078095).strokeWidth(5));


        //AlAsdaf
        PolygonOptions AlAsdaf = new PolygonOptions()
                .add(new LatLng(21.59134, 39.10579), new LatLng(21.59145, 39.10621), new LatLng(21.59185, 39.10611),
                        new LatLng(21.59205, 39.10608), new LatLng(21.59224, 39.10605), new LatLng(21.59297, 39.10601),
                        new LatLng(21.59588, 39.10606), new LatLng(21.59597, 39.10579), new LatLng(21.59575, 39.10561),
                        new LatLng(21.59565, 39.1055), new LatLng(21.59558, 39.10535), new LatLng(21.59555, 39.1053),
                        new LatLng(21.59547, 39.10521), new LatLng(21.59529, 39.10526), new LatLng(21.59523, 39.10534),
                        new LatLng(21.5952, 39.10544), new LatLng(21.59518, 39.10555), new LatLng(21.59511, 39.10558),
                        new LatLng(21.5948, 39.10563), new LatLng(21.59439, 39.10574), new LatLng(21.59431, 39.10565),
                        new LatLng(21.5942, 39.10559), new LatLng(21.59409, 39.10554), new LatLng(21.59396, 39.10556),
                        new LatLng(21.59387, 39.10562), new LatLng(21.59374, 39.10576), new LatLng(21.59293, 39.10559),
                        new LatLng(21.59284, 39.10549), new LatLng(21.59279, 39.10543), new LatLng(21.59266, 39.10535),
                        new LatLng(21.59253, 39.1054), new LatLng(21.59246, 39.10547), new LatLng(21.5924, 39.10555),
                        new LatLng(21.59191, 39.10563));

        AlAsdafPol = mMap.addPolygon(AlAsdaf.fillColor(0x55C24871)
                .strokeColor(0x99C24871).strokeWidth(5));

        //AlTawheed
        PolygonOptions Altawheed = new PolygonOptions()
                .add(new LatLng(21.596, 39.1058), new LatLng(21.59591, 39.10605), new LatLng(21.59629, 39.10606),
                        new LatLng(21.59668, 39.10611), new LatLng(21.59742, 39.10626), new LatLng(21.59876, 39.10643),
                        new LatLng(21.59956, 39.10665), new LatLng(21.60001, 39.10684), new LatLng(21.60025, 39.10633),
                        new LatLng(21.59974, 39.10614), new LatLng(21.59945, 39.10605), new LatLng(21.59915, 39.10604),
                        new LatLng(21.59862, 39.10572), new LatLng(21.59855, 39.10558), new LatLng(21.59815, 39.10536),
                        new LatLng(21.5978, 39.10537), new LatLng(21.59757, 39.10536), new LatLng(21.59737, 39.10524),
                        new LatLng(21.59734, 39.10515), new LatLng(21.59725, 39.10512), new LatLng(21.59708, 39.10507),
                        new LatLng(21.59693, 39.10512), new LatLng(21.59672, 39.10551), new LatLng(21.59656, 39.10567),
                        new LatLng(21.59631, 39.10581));

        AltawheedPol = mMap.addPolygon(Altawheed.fillColor(0x55DD6B0E)
                .strokeColor(0x99DD6B0E).strokeWidth(5));

        //AlRemal
        PolygonOptions AlRemal = new PolygonOptions()
                .add(new LatLng(21.60026, 39.10633), new LatLng(21.60002, 39.10687), new LatLng(21.60137, 39.10728),
                        new LatLng(21.60206, 39.10747), new LatLng(21.60279, 39.10764), new LatLng(21.60325, 39.10769),
                        new LatLng(21.60373, 39.10774), new LatLng(21.60479, 39.10769), new LatLng(21.60546, 39.10763),
                        new LatLng(21.60567, 39.10762), new LatLng(21.60587, 39.10763), new LatLng(21.60608, 39.10766),
                        new LatLng(21.60622, 39.10727), new LatLng(21.60652, 39.10632), new LatLng(21.60631, 39.10631),
                        new LatLng(21.60619, 39.10624), new LatLng(21.60615, 39.10619), new LatLng(21.60608, 39.10619),
                        new LatLng(21.60599, 39.1062), new LatLng(21.60587, 39.10616), new LatLng(21.60571, 39.10613),
                        new LatLng(21.60559, 39.10604), new LatLng(21.60554, 39.10607), new LatLng(21.60552, 39.10613),
                        new LatLng(21.60552, 39.10622), new LatLng(21.60547, 39.10641), new LatLng(21.60553, 39.10647),
                        new LatLng(21.60555, 39.10654), new LatLng(21.60551, 39.10663), new LatLng(21.60546, 39.1067),
                        new LatLng(21.60529, 39.10685), new LatLng(21.6051, 39.10695), new LatLng(21.60421, 39.10692),
                        new LatLng(21.60392, 39.10701), new LatLng(21.60374, 39.10703), new LatLng(21.60357, 39.10708),
                        new LatLng(21.60346, 39.1071), new LatLng(21.60338, 39.10707), new LatLng(21.60325, 39.1069),
                        new LatLng(21.60322, 39.10668), new LatLng(21.60325, 39.10655), new LatLng(21.60334, 39.10646),
                        new LatLng(21.60338, 39.10634), new LatLng(21.60327, 39.10622), new LatLng(21.60319, 39.10626),
                        new LatLng(21.60314, 39.1063), new LatLng(21.60313, 39.10635), new LatLng(21.60277, 39.10643),
                        new LatLng(21.60217, 39.10672), new LatLng(21.60173, 39.10673), new LatLng(21.60135, 39.10677),
                        new LatLng(21.60092, 39.10675), new LatLng(21.60059, 39.10656));

        AlRemalPol = mMap.addPolygon(AlRemal.fillColor(0x55DDA84B)
                .strokeColor(0x99DDA84B).strokeWidth(5));

        //Allo2lo2
        PolygonOptions Allo2lo2 = new PolygonOptions()
                .add(new LatLng(21.60656, 39.10627), new LatLng(21.60609, 39.10765), new LatLng(21.60709, 39.10788),
                        new LatLng(21.6077, 39.10793), new LatLng(21.60841, 39.10796), new LatLng(21.60927, 39.10799),
                        new LatLng(21.61033, 39.10803), new LatLng(21.61109, 39.10812), new LatLng(21.61145, 39.10815),
                        new LatLng(21.61182, 39.10811), new LatLng(21.61185, 39.10691), new LatLng(21.61168, 39.10698),
                        new LatLng(21.61161, 39.10701), new LatLng(21.61129, 39.10697), new LatLng(21.61097, 39.10693),
                        new LatLng(21.61104, 39.10678), new LatLng(21.61109, 39.10679), new LatLng(21.6111, 39.10669),
                        new LatLng(21.61106, 39.10668), new LatLng(21.61113, 39.10651), new LatLng(21.61097, 39.10626),
                        new LatLng(21.61099, 39.10623), new LatLng(21.61096, 39.10619), new LatLng(21.61089, 39.10623),
                        new LatLng(21.61096, 39.1066), new LatLng(21.61086, 39.10691), new LatLng(21.61052, 39.10687),
                        new LatLng(21.6104, 39.10675), new LatLng(21.6105, 39.10652), new LatLng(21.61067, 39.10632),
                        new LatLng(21.61065, 39.10629), new LatLng(21.61043, 39.10637), new LatLng(21.61037, 39.10657),
                        new LatLng(21.61032, 39.10655), new LatLng(21.61032, 39.10662), new LatLng(21.61033, 39.10667),
                        new LatLng(21.61029, 39.10664), new LatLng(21.61009, 39.10668), new LatLng(21.60984, 39.10663),
                        new LatLng(21.60967, 39.10664), new LatLng(21.60943, 39.10653), new LatLng(21.60914, 39.10664),
                        new LatLng(21.60908, 39.10659), new LatLng(21.60875, 39.10654), new LatLng(21.60864, 39.10652),
                        new LatLng(21.6086, 39.10651), new LatLng(21.60855, 39.10648), new LatLng(21.60852, 39.10634),
                        new LatLng(21.60852, 39.10623), new LatLng(21.60845, 39.10615), new LatLng(21.60833, 39.10614),
                        new LatLng(21.60827, 39.10619), new LatLng(21.60824, 39.10626), new LatLng(21.60807, 39.10631),
                        new LatLng(21.6079, 39.10634), new LatLng(21.60778, 39.10634), new LatLng(21.60766, 39.1063),
                        new LatLng(21.6075, 39.10636), new LatLng(21.60725, 39.10632), new LatLng(21.60695, 39.10636),
                        new LatLng(21.60681, 39.10632), new LatLng(21.60675, 39.10635), new LatLng(21.60656, 39.10627));

        Allo2lo2Pol = mMap.addPolygon(Allo2lo2.fillColor(0x55AB2325)
                .strokeColor(0x99AB2325).strokeWidth(5));

        //Alsaiad
        PolygonOptions Alsaiad = new PolygonOptions()
                .add(new LatLng(21.61186, 39.10692), new LatLng(21.61183, 39.1081), new LatLng(21.61255, 39.10795),
                        new LatLng(21.61325, 39.10779), new LatLng(21.61382, 39.10769), new LatLng(21.61433, 39.107689),
                        new LatLng(21.61544, 39.10764), new LatLng(21.61613, 39.10747), new LatLng(21.61648, 39.10736),
                        new LatLng(21.61666, 39.10727), new LatLng(21.61661, 39.10706), new LatLng(21.61647, 39.10599),
                        new LatLng(21.61617, 39.10644), new LatLng(21.61597, 39.10658), new LatLng(21.61574, 39.10649),
                        new LatLng(21.61559, 39.10647), new LatLng(21.61559, 39.10623), new LatLng(21.61567, 39.10624),
                        new LatLng(21.61564, 39.10599), new LatLng(21.61557, 39.10599), new LatLng(21.61554, 39.10563),
                        new LatLng(21.6156, 39.1056), new LatLng(21.61559, 39.10537), new LatLng(21.61555, 39.10536),
                        new LatLng(21.61556, 39.10528), new LatLng(21.61563, 39.10528), new LatLng(21.61563, 39.1053),
                        new LatLng(21.6157, 39.1053), new LatLng(21.61569, 39.1052), new LatLng(21.61525, 39.10522),
                        new LatLng(21.61529, 39.1053), new LatLng(21.6154, 39.10529), new LatLng(21.61541, 39.10534),
                        new LatLng(21.61541, 39.10538), new LatLng(21.61537, 39.10541), new LatLng(21.61539, 39.10564),
                        new LatLng(21.61544, 39.10563), new LatLng(21.61546, 39.10601), new LatLng(21.61537, 39.10602),
                        new LatLng(21.61537, 39.10615), new LatLng(21.61539, 39.10625), new LatLng(21.61547, 39.10625),
                        new LatLng(21.61548, 39.10646), new LatLng(21.61536, 39.10652), new LatLng(21.61527, 39.1066),
                        new LatLng(21.61516, 39.10679), new LatLng(21.615, 39.10681), new LatLng(21.61482, 39.10683),
                        new LatLng(21.61467, 39.10661), new LatLng(21.6146, 39.10644), new LatLng(21.61468, 39.10629),
                        new LatLng(21.6148, 39.10621), new LatLng(21.61483, 39.10602), new LatLng(21.61468, 39.10566),
                        new LatLng(21.61435, 39.10544), new LatLng(21.61417, 39.10539), new LatLng(21.61397, 39.10538),
                        new LatLng(21.61382, 39.10539), new LatLng(21.61367, 39.10545), new LatLng(21.61353, 39.10552),
                        new LatLng(21.61341, 39.10569), new LatLng(21.61332, 39.10589), new LatLng(21.61318, 39.10609),
                        new LatLng(21.61314, 39.10617), new LatLng(21.61306, 39.10627), new LatLng(21.6131, 39.10645),
                        new LatLng(21.61302, 39.10654), new LatLng(21.61297, 39.10665), new LatLng(21.61285, 39.10664),
                        new LatLng(21.61273, 39.10665), new LatLng(21.61265, 39.10671), new LatLng(21.61259, 39.10678),
                        new LatLng(21.61235, 39.1068), new LatLng(21.61214, 39.10695), new LatLng(21.61186, 39.10692));

        AlsaiadPol = mMap.addPolygon(Alsaiad.fillColor(0x551476BC)
                .strokeColor(0x991476BC).strokeWidth(5));

        //AlKaleej
        PolygonOptions AlKaleej = new PolygonOptions()
                .add(new LatLng(21.61648, 39.10593), new LatLng(21.61655, 39.10651), new LatLng(21.61663, 39.10702),
                        new LatLng(21.61668, 39.10725), new LatLng(21.61717, 39.10726), new LatLng(21.61772, 39.10727),
                        new LatLng(21.61874, 39.10739), new LatLng(21.61955, 39.10752), new LatLng(21.62052, 39.10752),
                        new LatLng(21.6211, 39.10752), new LatLng(21.62173, 39.10741), new LatLng(21.62231, 39.10726),
                        new LatLng(21.62307, 39.10704), new LatLng(21.62357, 39.10692), new LatLng(21.62387, 39.10682),
                        new LatLng(21.62385, 39.10672), new LatLng(21.62386, 39.10645), new LatLng(21.62382, 39.10627),
                        new LatLng(21.62378, 39.10617), new LatLng(21.62358, 39.10622), new LatLng(21.62354, 39.10611),
                        new LatLng(21.62353, 39.106), new LatLng(21.62347, 39.10589), new LatLng(21.62338, 39.10586),
                        new LatLng(21.62331, 39.10592), new LatLng(21.62334, 39.10601), new LatLng(21.62335, 39.10608),
                        new LatLng(21.62334, 39.10614), new LatLng(21.62331, 39.1062), new LatLng(21.62322, 39.10624),
                        new LatLng(21.62313, 39.1062), new LatLng(21.62308, 39.10612), new LatLng(21.62304, 39.10611),
                        new LatLng(21.62299, 39.10611), new LatLng(21.62293, 39.10624), new LatLng(21.62305, 39.10634),
                        new LatLng(21.62322, 39.1064), new LatLng(21.62333, 39.10641), new LatLng(21.6234, 39.10645),
                        new LatLng(21.62341, 39.10651), new LatLng(21.62336, 39.10658), new LatLng(21.62329, 39.10662),
                        new LatLng(21.62301, 39.10669), new LatLng(21.62272, 39.10679), new LatLng(21.62253, 39.10684),
                        new LatLng(21.62225, 39.10688), new LatLng(21.62208, 39.10681), new LatLng(21.62186, 39.10678),
                        new LatLng(21.62166, 39.10677), new LatLng(21.62148, 39.10678), new LatLng(21.62134, 39.10682),
                        new LatLng(21.6211, 39.10691), new LatLng(21.62086, 39.10698), new LatLng(21.62078, 39.10706),
                        new LatLng(21.62064, 39.10706), new LatLng(21.6205, 39.10698), new LatLng(21.6204, 39.10695),
                        new LatLng(21.62029, 39.10697), new LatLng(21.62025, 39.10707), new LatLng(21.61985, 39.10703),
                        new LatLng(21.61988, 39.10694), new LatLng(21.61987, 39.10685), new LatLng(21.61983, 39.10676),
                        new LatLng(21.61969, 39.10673), new LatLng(21.6196, 39.10681), new LatLng(21.61941, 39.10679),
                        new LatLng(21.61941, 39.10662), new LatLng(21.61939, 39.10656), new LatLng(21.61936, 39.10652),
                        new LatLng(21.6192, 39.10638), new LatLng(21.61903, 39.10628), new LatLng(21.61896, 39.10628),
                        new LatLng(21.61888, 39.10631), new LatLng(21.61879, 39.10639), new LatLng(21.6186, 39.106479),
                        new LatLng(21.61853, 39.10645), new LatLng(21.6185, 39.10637), new LatLng(21.61849, 39.10632),
                        new LatLng(21.61854, 39.10626), new LatLng(21.61875, 39.10619), new LatLng(21.61901, 39.10599),
                        new LatLng(21.61949, 39.10572), new LatLng(21.61958, 39.10572), new LatLng(21.61966, 39.10575),
                        new LatLng(21.61966, 39.10575), new LatLng(21.61967, 39.10588), new LatLng(21.61963, 39.10596),
                        new LatLng(21.61958, 39.10608), new LatLng(21.6196, 39.10618), new LatLng(21.61965, 39.1063),
                        new LatLng(21.61976, 39.10637), new LatLng(21.61988, 39.10636), new LatLng(21.61999, 39.10633),
                        new LatLng(21.62008, 39.1062), new LatLng(21.62009, 39.10605), new LatLng(21.61997, 39.10591),
                        new LatLng(21.61978, 39.10582), new LatLng(21.61978, 39.10564), new LatLng(21.61983, 39.10546),
                        new LatLng(21.62005, 39.10539), new LatLng(21.62046, 39.10528), new LatLng(21.62116, 39.10519),
                        new LatLng(21.6212, 39.10527), new LatLng(21.6213, 39.10547), new LatLng(21.62145, 39.1056),
                        new LatLng(21.62163, 39.10564), new LatLng(21.62187, 39.10556), new LatLng(21.62204, 39.10526),
                        new LatLng(21.62221, 39.10527), new LatLng(21.6223, 39.1054), new LatLng(21.62256, 39.10551),
                        new LatLng(21.62269, 39.1054), new LatLng(21.62282, 39.10538), new LatLng(21.62298, 39.10553),
                        new LatLng(21.62315, 39.10546), new LatLng(21.62329, 39.10553), new LatLng(21.62343, 39.10546),
                        new LatLng(21.62379, 39.10549), new LatLng(21.62383, 39.10514), new LatLng(21.62373, 39.10511),
                        new LatLng(21.62358, 39.10509), new LatLng(21.62344, 39.10507), new LatLng(21.62337, 39.10479),
                        new LatLng(21.62325, 39.10468), new LatLng(21.62309, 39.10461), new LatLng(21.62284, 39.10468),
                        new LatLng(21.62264, 39.10499), new LatLng(21.62162, 39.10482), new LatLng(21.62009, 39.10494),
                        new LatLng(21.61901, 39.10534), new LatLng(21.61845, 39.10573), new LatLng(21.61842, 39.10558),
                        new LatLng(21.61838, 39.10543), new LatLng(21.61829, 39.1053), new LatLng(21.61822, 39.10529),
                        new LatLng(21.61815, 39.1053), new LatLng(21.61811, 39.10535), new LatLng(21.61776, 39.10535),
                        new LatLng(21.61724, 39.10552), new LatLng(21.61666, 39.10574), new LatLng(21.61648, 39.10593));

        AlKaleejPol = mMap.addPolygon(AlKaleej.fillColor(0x55689B32)
                .strokeColor(0x99689B32).strokeWidth(5));
    }

    public void mapPointers(String result) {
        //result from backgroundworker
        String[] pointerInfo = result.split("-");
        LatLng Pointer;

        for (int i = 0; i <= (pointerInfo.length - 7); i = i + 7) {
            Pointer = new LatLng(Double.parseDouble(pointerInfo[i]), Double.parseDouble(pointerInfo[i + 1]));

            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(Pointer)
                    .snippet(pointerInfo[i + 4])
                    .title(pointerInfo[i + 3])
                    .icon(BitmapDescriptorFactory.fromResource(ctx.getResources().getIdentifier(pointerInfo[i + 2], "drawable", "com.example.jwpackage.JeddahWaterfrontAR"))));

            int id = Integer.parseInt(m.getId().replace("m", "").trim());

            //save marker info in arraylists using marker's id as index
            markersList.add(id,m);
            pointersImages.add(id, pointerInfo[i + 5]);
            markerCategory.add(id,Integer.parseInt(pointerInfo[i+6]));
            mMap.setInfoWindowAdapter(this);
        }
        //check if shared refrence email is not empty to get the parking information
         if (userEmail != "") {
            BackgroundWorker bw2 = new BackgroundWorker(ctx);
            bw2.execute("parkingPlace", userEmail, "1");
        }
    }

    public void parkingInfo(double x, double y) {
        LatLng parkingPosition = new LatLng(x, y);

        parkingMarker = mMap.addMarker(new MarkerOptions()
                .title("Parking Place")
                .snippet("parking location " +x+","+y)
                .position(parkingPosition)
                .icon(BitmapDescriptorFactory.fromResource(ctx.getResources().getIdentifier("parking", "drawable", "com.example.jwpackage.JeddahWaterfrontAR"))));

        parkCarKey = true;
    }

  @Override
    public void onResume() {
        super.onResume();
        LocationStuff();

    }

///////////////////////////////////////infowindow stuff

    @Override
    public View getInfoWindow(Marker marker) {
        infoBoxTitle.setText(marker.getTitle());
        infoBoxDes.setText(marker.getSnippet());

        //get marker Id and convert it to int.
        int id = Integer.parseInt(marker.getId().replace("m", "").trim());

        //set inforwindow image
        int infowindowImage = ctx.getResources().getIdentifier(pointersImages.get(id), "drawable", "com.example.jwpackage.JeddahWaterfrontAR");
        infoBoxImage.setImageResource(infowindowImage);

        return infoBoxV;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return infoBoxV;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    ///////////////////////Routing stuff
    public void getRouteToMarker(LatLng destinationMarker) {
        Routing routing;
        //check if user is in or out of the waterfront to set his travel mode
        if (PolyUtil.containsLocation(currentLocation, AlKaleejPol.getPoints(), false) ||
                PolyUtil.containsLocation(currentLocation, AlNawrawsPol.getPoints(), false) ||
                PolyUtil.containsLocation(currentLocation, AlAsdafPol.getPoints(), false) ||
                PolyUtil.containsLocation(currentLocation, AlsaiadPol.getPoints(), false) ||
                PolyUtil.containsLocation(currentLocation, Allo2lo2Pol.getPoints(), false) ||
                PolyUtil.containsLocation(currentLocation, AlRemalPol.getPoints(), false) ||
                PolyUtil.containsLocation(currentLocation, AltawheedPol.getPoints(), false)) {
            routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.WALKING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(currentLocation, destinationMarker)
                    .build();
            routing.execute();
        } else {
            routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(currentLocation, destinationMarker)
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        AlertDialog ad = new AlertDialog.Builder(ctx)
                .create();
        if (e != null) {
            ad.setMessage(e.getMessage());
        } else {
            ad.setMessage("Error");
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        //clear previos route
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.rgb(0, 255, 255));
        polyOptions.width(5);
        polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
        Polyline polyline = mMap.addPolyline(polyOptions);
        polylines.add(polyline);

        int distance = route.get(shortestRouteIndex).getDistanceValue();
        int duration = route.get(shortestRouteIndex).getDurationValue();
        duration /= 60;

        String distanceUnite = "m";
        String durationUnite = "min";

        if (distance >= 1000) {
            distance /= 1000;
            distanceUnite = "km";
        }
        if (duration >= 60) {
            duration /= 60;
            durationUnite = "hrs";
        }
        distanceIV.setVisibility(View.VISIBLE);
        durationIV.setVisibility(View.VISIBLE);
        ximg.setVisibility(View.VISIBLE);
        distanceTxt.setVisibility(View.VISIBLE);
        durationTxt.setVisibility(View.VISIBLE);
        distanceTxt.setText(Math.round(distance) + distanceUnite);
        durationTxt.setText(Math.round(duration) + durationUnite);

    }

    @Override
    public void onRoutingCancelled() {

    }

    ///////////////////////////// fused loc stuff
    public void LocationStuff() {
        buildLocationRequest();
        buildLocationCallback();

        //create fusedlocation
        fusedLocProv = LocationServices.getFusedLocationProviderClient(ctx);

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocProv.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());


        }
    }

    private void buildLocationCallback() {
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations())
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10);

    }

}