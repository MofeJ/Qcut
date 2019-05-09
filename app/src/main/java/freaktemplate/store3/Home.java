package freaktemplate.store3;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import freaktemplate.adapter.CustomHomeAdapter;
import freaktemplate.getset.Storegetset;
import freaktemplate.utils.ConnectionDetector;
import freaktemplate.utils.GPSTracker;


public class Home extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static final String MY_PREFS_NAME = "Store";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static ArrayList<Storegetset> rest;
    private String foodname;
    private int from;
    private String prodel = "";
    private View layout12;
    private int to;
    private String search;
    private String userloginid;
    private RecyclerView list_detail;
    private RelativeLayout rl_dialoguser;
    private double latitude;
    private double longitude;
    private int isSearch = -1;
    private final int Load_For_DATA = 0;
    private final int Load_For_CATEGORY = 1;
    private final int Load_For_SEARCH = 2;
    private CustomHomeAdapter adapter;
    private ProgressBar progressBar;

    // SET CLICK EVENTS

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // SET ADVERTISE
        loadAd();

        // GET SHARED PREFERENCE
        getPreference();

        // SET CLICK EVENTS
        initViews();

        //load data from server
        checkinternet();
    }


    private void loadAd() {
        // TODO Auto-generated method stub
        if (getString(R.string.bannerads).equals("yes")) {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else if (getString(R.string.bannerads).equals("no")) {

            AdView mAdView = findViewById(R.id.adView);
            mAdView.setVisibility(View.GONE);

        }
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void getPreference() {
        // TODO Auto-generated method stub
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (prefs.getString("score", null) != null) {
            userloginid = prefs.getString("score", null);
        }
        if (prefs.getString("delete", null) != null) {
            prodel = prefs.getString("delete", null);
        }
    }


    private void initViews() {
        // TODO Auto-generated method stub

        //initialization
        Toolbar mToolbar = findViewById(R.id.toolbar);
        Button btn_map = findViewById(R.id.btn_map);
        progressBar = findViewById(R.id.progressBar);
        list_detail = findViewById(R.id.list_detail);
        EditText edit_search = findViewById(R.id.edit_search);
        layout12 = getLayoutInflater().inflate(R.layout.footer_layout, rl_dialoguser, false);


        //setting toolbar and Drawer
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        //map button click go to map class

        btn_map.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent iv = new Intent(Home.this, Gmap.class);
                iv.putExtra("map", "yes");
                startActivity(iv);
            }
        });

        //search store

        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    isSearch = Load_For_SEARCH;
                    getDataFromServer();
                    return true;
                }
                return false;
            }
        });
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                search = arg0.toString();
                if (search.length() == 0) {
                    isSearch = Load_For_DATA;
                    getDataFromServer();
                }

            }
        });

    }

    private void checkinternet() {
        // TODO Auto-generated method stub
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            // check if GPS enabled
            if (checkLocationPermission()) {

                //get data after getting location
                gpsgetlocation();
            }
        } else {

            //error dialog
            rl_dialoguser = findViewById(R.id.rl_infodialog);
            try {
                layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
            } catch (InflateException e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            rl_dialoguser.addView(layout12);
            rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Home.this, R.anim.popup));
            TextView txt_error_discription = layout12.findViewById(R.id.txt_dia);
            txt_error_discription.setText(getString(R.string.internet_error));
            Button btn_yes = layout12.findViewById(R.id.btn_yes);
            btn_yes.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialoguser.setVisibility(View.GONE);
                }
            });
        }
    }


    // GET SHARED PREFRANCE

    private void gpsgetlocation() {
        GPSTracker gps = new GPSTracker(Home.this);
        if (gps.canGetLocation()) {
            try {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Intent iv = getIntent();
                foodname = iv.getStringExtra("foodname");

                //set the url condition to load  /////////////Important Step////
                if (foodname != null)
                    isSearch = Load_For_CATEGORY;
                else
                    isSearch = Load_For_DATA;

                getDataFromServer();

            } catch (NullPointerException e) {
                // TODO: handle exception
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        } else {

            gps.showSettingsAlert();
        }
    }

    private void getDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);

        String url = "";
        from = 0;
        to = 7;

        switch (isSearch) {
            case Load_For_DATA:
                url = getString(R.string.link) + "rest/nearbystore.php?lat=" + latitude + "&&long=" + longitude + "&from=" + from + "&to=" + to;
                break;
            case Load_For_SEARCH:
                url = getString(R.string.link) + "rest/nearbystore.php?lat=" + latitude + "&&long=" + longitude + "&from=" + from + "&to=" + to + "&search=" + search;
                break;
            case Load_For_CATEGORY:
                url = getString(R.string.link) + "rest/nearbystore.php?lat=" + latitude + "&&long=" + longitude + "&from=" + from + "&to=" + to + "&search=category&cat_id=" + foodname;
                break;
        }
        Log.e(getLocalClassName(), url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(getLocalClassName(), response);
                progressBar.setVisibility(View.GONE);

                try {
                    handleResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void handleResponse(String response) throws JSONException {
        JSONObject jObject = new JSONObject(response);
        ArrayList<Storegetset> dat = new ArrayList<>();
        rest = new ArrayList<>();
        JSONArray j = jObject.optJSONArray("Stores");
        for (int i = 0; i < j.length(); i++) {
            JSONObject Obj;
            Obj = j.getJSONObject(i);
            Storegetset temp = new Storegetset();
            temp.setStore_id(Obj.optString("store_id"));
            temp.setName(Obj.optString("name"));
            temp.setAddress(Obj.optString("address"));
            temp.setDistance(Obj.optString("distance"));
            temp.setLat(Obj.optString("latitude"));
            temp.setLongi(Obj.optString("longitude"));
            temp.setFeatured(Obj.optString("featured"));
            temp.setRatting(Obj.optString("ratting"));
            temp.setThumbnail(Obj.optString("thumbnail"));
            dat.add(temp);
            rest.add(temp);
        }
        updateUI(dat);
    }

    private void updateUI(ArrayList<Storegetset> dat) {

        list_detail.setVisibility(View.VISIBLE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        list_detail.setLayoutManager(mLayoutManager);
        list_detail.setItemAnimator(new DefaultItemAnimator());
        adapter = new CustomHomeAdapter(dat, list_detail);
        adapter.setOnLoadMoreListener(new CustomHomeAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                from = to + 1;
                to = from + 7;
                //Load more data for recyclerView
                loadMoreDataFromServer();
            }
        });
        list_detail.setAdapter(adapter);

        adapter.setOnClickListnerR(new CustomHomeAdapter.onClickItem() {
            @Override
            public void onItemClicked(int position, View view) {
                Intent iv = new Intent(Home.this, Detailpage.class);
                iv.putExtra("rating", "" + adapter.getItem(position).getRatting());
                iv.putExtra("name", "" + adapter.getItem(position).getName());
                iv.putExtra("id", "" + adapter.getItem(position).getStore_id());
                iv.putExtra("distance", "" + adapter.getItem(position).getDistance());
                startActivity(iv);
            }
        });


    }

    private void loadMoreDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);

        String url = "";

        switch (isSearch) {
            case Load_For_DATA:
                url = getString(R.string.link) + "rest/nearbystore.php?lat=" + latitude + "&&long=" + longitude + "&from=" + from + "&to=" + to;
                break;
            case Load_For_SEARCH:
                url = getString(R.string.link) + "rest/nearbystore.php?lat=" + latitude + "&&long=" + longitude + "&from=" + from + "&to=" + to + "&search=" + search;
                break;
            case Load_For_CATEGORY:
                url = getString(R.string.link) + "rest/nearbystore.php?lat=" + latitude + "&&long=" + longitude + "&from=" + from + "&to=" + to + "&search=category&cat_id=" + foodname;
                break;
        }
        Log.e(getLocalClassName(), url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(getLocalClassName(), response);
                progressBar.setVisibility(View.GONE);

                try {
                    handleMoreResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void handleMoreResponse(String response) throws JSONException {

        JSONObject jObject = new JSONObject(response);
        ArrayList<Storegetset> dat = new ArrayList<>();
        JSONArray j = jObject.optJSONArray("Stores");
        for (int i = 0; i < j.length(); i++) {
            JSONObject Obj;
            Obj = j.getJSONObject(i);
            Storegetset temp = new Storegetset();
            temp.setStore_id(Obj.optString("store_id"));
            temp.setName(Obj.optString("name"));
            temp.setAddress(Obj.optString("address"));
            temp.setDistance(Obj.optString("distance"));
            temp.setLat(Obj.optString("latitude"));
            temp.setLongi(Obj.optString("longitude"));
            temp.setFeatured(Obj.optString("featured"));
            temp.setRatting(Obj.optString("ratting"));
            temp.setThumbnail(Obj.optString("thumbnail"));
            dat.add(temp);
            rest.add(temp);

        }
        if (dat.size() > 0) {
            adapter.setLoaded();
            adapter.addItem(dat, adapter.getItemCount());

        } else {
            Toast.makeText(this, "No More Stores Found!", Toast.LENGTH_SHORT).show();
        }
    }
    // SET ADVERTISE


    // CLASS FOR GET STORE RESULTS FROM SERVER

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location")
                        .setMessage("Need permission to retrieve your location")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Home.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();


        AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
        builder1.setTitle("Quit?");
        builder1.setMessage("Are you sure you want to Quit?");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Home.this.finish();
                finishAffinity();

            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();


    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title;
        switch (position) {
            case 0:
                Intent iv = new Intent(Home.this, Home.class);
                startActivity(iv);

                break;
            case 1:
                Intent iv1 = new Intent(Home.this, Category.class);
                startActivity(iv1);

                break;
            case 2:
                Intent iv2 = new Intent(Home.this, Favourite.class);
                startActivity(iv2);

                break;
            case 3:
                Intent iv4 = new Intent(Home.this, Featured.class);
                startActivity(iv4);

                break;
            case 4:
                Intent iv5 = new Intent(Home.this, News.class);
                startActivity(iv5);

                break;
            case 5:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.link) + "appuserlogin.php"));
                try {
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Home.this, "Please try later!", Toast.LENGTH_SHORT).show();
                }

                break;

            case 6:
                Intent iv7 = new Intent(Home.this, About.class);
                startActivity(iv7);

                break;
            case 7:
                Intent iv8 = new Intent(Home.this, Terms.class);
                startActivity(iv8);

                break;
            case 8:

                switch (prodel) {
                    case "delete": {
                        Intent iv9 = new Intent(Home.this, Login.class);
                        iv9.putExtra("key", "home");
                        startActivity(iv9);
                        break;
                    }
                    case "new":
                        if (userloginid != null) {
                            Intent iv9 = new Intent(Home.this, Profile.class);
                            startActivity(iv9);
                        } else {
                            Intent iv9 = new Intent(Home.this, Login.class);
                            startActivity(iv9);
                        }
                        break;
                    case "": {
                        Intent iv9 = new Intent(Home.this, Login.class);
                        iv9.putExtra("key", "home");
                        startActivity(iv9);
                        break;
                    }
                }
                //fragment = new MessagesFragment();
                //title = getString(R.string.title_messages);
                break;


            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            title = "";
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    //load more store from json class
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        gpsgetlocation();
                    }
                } else {
                    checkLocationPermission();

                }
            }
        }
    }
}
