package freaktemplate.store3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import freaktemplate.getset.Getsetfav;
import freaktemplate.getset.Storegetset;
import freaktemplate.utils.AlertDialogManager;
import freaktemplate.utils.ConnectionDetector;
import freaktemplate.utils.CustomMarker;
import freaktemplate.utils.GPSTracker;
import freaktemplate.utils.sqlitehelper;

public class Detailpage extends FragmentActivity implements OnMapReadyCallback {
    String name;
    private String id;
    private String Error;
    private String text;
    private String urlToShare;
    private String imageurl;
    private String store_id;
    private String name1;
    private String address;
    private String distance;
    private String homedistance;
    private String email;
    private TextView txt_header;
    private TextView txt_description;
    private RatingBar rb;
    private Float number;
    private int start = 0;
    private boolean fvtr;
    private ProgressDialog progressDialog;
    Runnable runnable;
    private ArrayList<Storegetset> rest;
    private ArrayList<Getsetfav> FileList;
    private View layout12;
    private ImageView imageView;
    private String[] separated = null;
    private Button btn_fvrt;
    private Button btn_fvrt1;
    private SQLiteDatabase db;
    private Cursor cur = null;
    private CustomMarker customMarkerOne;
    private HashMap<CustomMarker, Marker> markersHashMap;
    private GoogleMap googleMap;
    private GPSTracker gps;
    private double latitudecur;
    private double longitudecur;
    private Typeface tfh;
    private InterstitialAd mInterstitialAd;
    private boolean interstitialCanceled;
    private ConnectionDetector cd;
    private AlertDialogManager alert = new AlertDialogManager();
    private MaterialFavoriteButton fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailpage);
        SupportMapFragment sup = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment));
        sup.getMapAsync(this);
        adsview();
        checkgps();
        initialise();
        fav = findViewById(R.id.fav);

    }

    private void initialise() {
        // TODO Auto-generated method stub
        tfh = Typeface.createFromAsset(Detailpage.this.getAssets(), "fonts/Roboto-Medium.ttf");
        FileList = new ArrayList<>();
        rest = new ArrayList<>();
        Intent iv = getIntent();
        try {
            id = iv.getStringExtra("id");
            homedistance = iv.getStringExtra("distance");
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
        btn_fvrt = findViewById(R.id.btn_fvrt);
        btn_fvrt1 = findViewById(R.id.btn_fvrt1);
        txt_header = findViewById(R.id.txt_header);
        txt_header.setTypeface(tfh);
        new getList().execute();
        new getstorefulldetail().execute();
    }

    private void checkgps() {
        // TODO Auto-generated method stub
        gps = new GPSTracker(Detailpage.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitudecur = gps.getLatitude();
            longitudecur = gps.getLongitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private void adsview() {
        // TODO Auto-generated method stub
        if (getString(R.string.insertialads).equals("yes")) {
            interstitialCanceled = false;
            CallNewInsertial();
        } else if (getString(R.string.insertialads).equals("no")) {

        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        // Enabling MyLocation Layer of Google Map
        //googleMap.setMyLocationEnabled(true);
    }

    // GET STORE DETAIL FROM SERVER

    class getstorefulldetail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Detailpage.this);
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getdetailforNearMe();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();

            }
            if (Error != null) {

                RelativeLayout rl_back = findViewById(R.id.rl_back);
                if (rl_back == null) {
                    Log.d("second", "second");
                    RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

                    try {
                        layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
                    } catch (InflateException e) {
                        // TODO: handle exception
                    }

                    rl_dialoguser.addView(layout12);
                    rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Detailpage.this, R.anim.popup));
                    Button btn_yes = layout12.findViewById(R.id.btn_yes);
                    btn_yes.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            finish();
                        }
                    });
                }

            } else {
                //if (progressDialog.isShowing()) {
                //	progressDialog.dismiss();
                setdata();
                //}
            }
        }
    }

    // set data on detail page

    private void setdata() {
        // TODO Auto-generated method stub
        Typeface tf = Typeface.createFromAsset(Detailpage.this.getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface tf1 = Typeface.createFromAsset(Detailpage.this.getAssets(), "fonts/Roboto-Light.ttf");
        final Storegetset temp_Obj3 = rest.get(start);
        // display store position on google map
        double latitude = 0, longitude = 0;
        try {
            latitude = Double.parseDouble(temp_Obj3.getLat());
            longitude = Double.parseDouble(temp_Obj3.getLongi());
            Log.d("latitude", "" + latitude + longitude);
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }


        final LatLng position = new LatLng(latitude, longitude);
        customMarkerOne = new CustomMarker("markerOne", latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions().position(
                new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                .title(temp_Obj3.getName() + temp_Obj3.getAddress());
        Marker newMark = googleMap.addMarker(markerOption);
        addMarkerToHashMap(customMarkerOne, newMark);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        try {
            // total review of store
            TextView txt_totalreview = findViewById(R.id.txt_totalreview);
            txt_totalreview.setText("Total Review :" + temp_Obj3.getTotal_review());
            txt_totalreview.setTypeface(tf1);

            // address of store

            TextView txt_add = findViewById(R.id.txt_add);
            txt_add.setText(Html.fromHtml(temp_Obj3.getAddress()));
            txt_add.setTypeface(tf);

            // phone no of store

            TextView txt_phone = findViewById(R.id.txt_phone);
            txt_phone.setText(temp_Obj3.getPhoneno());
            txt_phone.setTypeface(tf);

            // description of store

            txt_description = findViewById(R.id.txt_description);
            txt_description.setText(Html.fromHtml(temp_Obj3.getDescription()));

            // name of store

            txt_header.setText("" + Html.fromHtml(temp_Obj3.getName()));

            String tempimg = temp_Obj3.getImages();
            Log.d("tempimage", "" + tempimg);

            separated = tempimg.split(",");
            Log.d("sep", "" + separated[0]);
            Log.d("sep", "" + separated.length);

            // rating of store

            rb = findViewById(R.id.rate1);
            number = Float.parseFloat(temp_Obj3.getRatting());
            Log.d("number", "" + number);
            rb.setRating(number);


            text = Html.fromHtml(temp_Obj3.getName()) + "\n" + Html.fromHtml(temp_Obj3.getAddress()) + "\n" + "Email: " + Html.fromHtml(temp_Obj3.getEmail()) + "\n"
                    + "Contact: " + Html.fromHtml(temp_Obj3.getPhoneno()) + "\n" +
                    "https://play.google.com/store/apps/details?id=" + Detailpage.this.getPackageName();
            // image view pager class

            CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(Detailpage.this);
            ViewPager mViewPager = findViewById(R.id.pager);
            mViewPager.setAdapter(mCustomPagerAdapter);
        } catch (NullPointerException e) {
            // TODO: handle exception
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        // call button click

        Button btn_call = findViewById(R.id.btn_video);
        btn_call.setAnimation(AnimationUtils.loadAnimation(Detailpage.this, R.anim.lefttoright));
        btn_call.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String uri = "tel:" + temp_Obj3.getPhoneno();
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse(uri));
                startActivity(i);
            }
        });

        // share button click(on social)

        Button btn_share = findViewById(R.id.btn_share);
        btn_share.setAnimation(AnimationUtils.loadAnimation(Detailpage.this, R.anim.lefttoright));
        btn_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                AlertDialog alertDialog = new AlertDialog.Builder(Detailpage.this).create();
                alertDialog.setTitle(getString(R.string.share));
                alertDialog.setMessage(getString(R.string.sharetitle));
                // share on gmail,hike etc
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.more),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                // ...
                                try {
                                    text = Html.fromHtml(temp_Obj3.getName()) + "\n" + Html.fromHtml(temp_Obj3.getAddress()) + "\n" +
                                            "https://play.google.com/store/apps/details?id=" + Detailpage.this.getPackageName();
                                    urlToShare = "https://play.google.com/store/apps/details?id="
                                            + Detailpage.this.getPackageName();
                                    Uri bmpUri = getLocalBitmapUri(imageView);
                                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.setType("image/*");
                                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                                    share.putExtra(Intent.EXTRA_SUBJECT, "Store Finder");
                                    share.putExtra("android.intent.extra.TEXT", text);
                                    share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    startActivity(Intent.createChooser(share, "Share"));
                                } catch (NullPointerException e) {
                                    // TODO: handle exception
                                }
                            }
                        });

                // share on whatsapp

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.whatsapp),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                // ...
                                try {
                                    Uri bmpUri = getLocalBitmapUri(imageView);
                                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                                    whatsappIntent.setType("text/plain");
                                    whatsappIntent.setType("image/*");
                                    whatsappIntent.setPackage("com.whatsapp");
                                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, text);
                                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                                    Detailpage.this.startActivity(whatsappIntent);
                                } catch (NullPointerException e) {
                                    // TODO: handle exception
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(Detailpage.this, "Whatsapp have not been installed.", Toast.LENGTH_LONG)
                                            .show();
                                }

                            }
                        });

                final String image = rest.get(0).getImages().replace(" ", "%20");
                // share on facebook

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.facebook),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {




                                text = rest.get(0).getName()+"\n"+Html.fromHtml(temp_Obj3.getAddress()) + "\n" + "Email: " + Html.fromHtml(temp_Obj3.getEmail()) + "\n"
                                        + "Contact: " + Html.fromHtml(temp_Obj3.getPhoneno());
                                String link = "https://play.google.com/store/apps/details?id=" + Detailpage.this.getPackageName();



                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(link))
                                        .setQuote(text)
                                        .build();

                                FacebookSdk.sdkInitialize(getApplicationContext());
                                ShareDialog shareDialog = new ShareDialog(Detailpage.this);
                                if (ShareDialog.canShow(ShareLinkContent.class)) {
//                                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                                            .setContentTitle(rest.get(0).getName())
//                                            .setContentDescription(text)
//                                            .setContentUrl(Uri.parse(link))
//                                            .setImageUrl(Uri.parse(imageurl))
//                                            .build();
                                    shareDialog.show(content);
                                }


                            }
                        });
                alertDialog.show();
            }
        });

        // on click of map button redirect to your device map to show route

        Log.e("test", temp_Obj3.getLat() + " " + temp_Obj3.getLongi() + " " + latitudecur + " " + longitudecur);

        Button btn_map = findViewById(R.id.btn_map);
        btn_map.setAnimation(AnimationUtils.loadAnimation(Detailpage.this, R.anim.lefttoright));
        btn_map.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + temp_Obj3.getLat() + "," + temp_Obj3.getLongi()
                                + "&saddr=" + latitudecur + "," + longitudecur));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

        // on click of website button(open particular store website)

        Button btn_web = findViewById(R.id.btn_web);
        btn_web.setAnimation(AnimationUtils.loadAnimation(Detailpage.this, R.anim.lefttoright));
        btn_web.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Uri temp = Uri.parse("http://" + temp_Obj3.getWebsite());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, temp);
                try {
                    startActivity(browserIntent);
                } catch (ActivityNotFoundException e) {
                    Log.w("errors", e.getMessage());
                    Toast.makeText(Detailpage.this, "Please try later!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // on click of mail button mail to store

        Button btn_mail = findViewById(R.id.btn_book);
        btn_mail.setAnimation(AnimationUtils.loadAnimation(Detailpage.this, R.anim.lefttoright));
        btn_mail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                email = temp_Obj3.getEmail();
                try {
                    openGmail(temp_Obj3);
                } catch (Exception e) {
                    Log.w("Erroe", e.getMessage());
                    sendEmail();
                }
            }
        });

        // on click of review button

        Button btn_review = findViewById(R.id.btn_review);
        btn_review.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent iv = new Intent(getApplicationContext(), Review.class);
                iv.putExtra("id", "" + temp_Obj3.getStore_id());
                startActivity(iv);


            }
        });

        // on click of this favourite button store will be favourite list
        fav.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener() {
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                        //

                        if (fvtr) {
                            fvtr = false;
                            fav.setImageResource(R.drawable.favorite_btn);
                            Log.e("fvrt", "yes");
//                            myDbHelpel = new DBAdapter(Detailpage.this);
//                            try {
//                                myDbHelpel.createDataBase();
//                            } catch (IOException io) {
//                                throw new Error("Unable TO Create DataBase");
//                            }
//                            try {
//                                myDbHelpel.openDataBase();
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                            db = myDbHelpel.getWritableDatabase();
                            sqlitehelper myDbHelpel=new sqlitehelper(Detailpage.this);
                            SQLiteDatabase db = myDbHelpel.getWritableDatabase();
                            ContentValues values = new ContentValues();

                            values.put("store_id", temp_Obj3.getStore_id());
                            values.put("name", temp_Obj3.getName());
                            values.put("address", temp_Obj3.getAddress());

                            values.put("distance", homedistance);

                            db.insert("favourite", null, values);

                            myDbHelpel.close();
                        } else if (!fvtr) {
                            fvtr = true;
                            fav.setImageResource(R.drawable.unfavorite_btn);
                            Log.e("fvrt", "no");

//                            DBAdapter myDbHelper = new DBAdapter(Detailpage.this);
//                            myDbHelper = new DBAdapter(Detailpage.this);
//                            try {
//                                myDbHelper.createDataBase();
//                            } catch (IOException e) {
//
//                                e.printStackTrace();
//                            }
//
//                            try {
//
//                                myDbHelper.openDataBase();
//
//                            } catch (SQLException sqle) {
//                                sqle.printStackTrace();
//                            }
//
//                            int i = 1;
//                            db = myDbHelper.getWritableDatabase();

                            sqlitehelper myDbHelper=new sqlitehelper(Detailpage.this);
                            SQLiteDatabase db = myDbHelper.getWritableDatabase();

                            cur = db.rawQuery("Delete from favourite where store_id =" + temp_Obj3.getStore_id() + ";", null);
                            if (cur.getCount() != 0) {
                                if (cur.moveToFirst()) {
                                    do {
                                        Getsetfav obj = new Getsetfav();

                                        store_id = cur.getString(cur.getColumnIndex("store_id"));
                                        name1 = cur.getString(cur.getColumnIndex("name"));
                                        address = cur.getString(cur.getColumnIndex("address"));

                                        distance = cur.getString(cur.getColumnIndex("distance"));

                                        obj.setName(name1);
                                        obj.setAddress(address);
                                        obj.setStore_id(store_id);
                                        obj.setDistance(distance);

                                        FileList.add(obj);

                                    } while (cur.moveToNext());
                                }
                            }
                            cur.close();
                            db.close();
                            myDbHelper.close();
                        }
                    }
                });
        btn_fvrt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Animation a = AnimationUtils.loadAnimation(Detailpage.this, R.anim.bounce);
                btn_fvrt1.startAnimation(a);

                btn_fvrt1.setVisibility(View.VISIBLE);
                btn_fvrt.setVisibility(View.INVISIBLE);

//                // data store in database of favorite store
//                myDbHelpel = new DBAdapter(Detailpage.this);
//                try {
//                    myDbHelpel.createDataBase();
//                } catch (IOException io) {
//                    throw new Error("Unable TO Create DataBase");
//                }
//                try {
//                    myDbHelpel.openDataBase();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                db = myDbHelpel.getWritableDatabase();

                sqlitehelper myDbHelpel=new sqlitehelper(Detailpage.this);
                SQLiteDatabase db = myDbHelpel.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("store_id", temp_Obj3.getStore_id());
                values.put("name", temp_Obj3.getName());
                values.put("address", temp_Obj3.getAddress());
                values.put("distance", homedistance);
                db.insert("favourite", null, values);
                myDbHelpel.close();
            }
        });

        // on click of this favourite button store will be unfavourite

        btn_fvrt1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                btn_fvrt.setVisibility(View.VISIBLE);
                btn_fvrt1.setVisibility(View.INVISIBLE);
                Animation a = AnimationUtils.loadAnimation(Detailpage.this, R.anim.bounce);
                btn_fvrt.startAnimation(a);
                // remove record of store from database to unfavourite
//                DBAdapter myDbHelper = new DBAdapter(Detailpage.this);
//                myDbHelper = new DBAdapter(Detailpage.this);
//                try {
//                    myDbHelper.createDataBase();
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }
//                try {
//                    myDbHelper.openDataBase();
//
//                } catch (SQLException sqle) {
//                    sqle.printStackTrace();
//                }
//                int i = 1;
//                db = myDbHelper.getWritableDatabase();
                sqlitehelper myDbHelper=new sqlitehelper(Detailpage.this);
                SQLiteDatabase db = myDbHelper.getWritableDatabase();
                cur = db.rawQuery("Delete from favourite where store_id =" + temp_Obj3.getStore_id() + ";", null);
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            Getsetfav obj = new Getsetfav();

                            store_id = cur.getString(cur.getColumnIndex("store_id"));
                            name1 = cur.getString(cur.getColumnIndex("name"));
                            address = cur.getString(cur.getColumnIndex("address"));

                            distance = cur.getString(cur.getColumnIndex("distance"));

                            obj.setName(name1);
                            obj.setAddress(address);
                            obj.setStore_id(store_id);
                            obj.setDistance(distance);

                            FileList.add(obj);

                        } while (cur.moveToNext());
                    }
                }
                cur.close();
                db.close();
                myDbHelper.close();
            }
        });
    }

    private void openGmail(Storegetset temp) {
        // TODO Auto-generated method stub
        String email = temp.getEmail();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Objects.equals(temp.getEmail(), "")) {
                email = "nocompanyemail@gmail.com";
            } else if (temp.getEmail().equals("")) {
                email = "nocompanyemail@gmail.com";
            }
        }
        String body = "Name: " + Html.fromHtml(temp.getName()) + "\n" + "Mail id: " + email + "\n" + "Address: " + Html.fromHtml(temp.getAddress()) + "\n" + "Contact no: " + Html.fromHtml(temp.getPhoneno());
//        EmailIntentBuilder.from(this)
//                .to(email)
//                .subject("Store Finder")
//                .body("Name: " + Html.fromHtml(temp.getName()) + "\n" + "Mail id: " + email + "\n" + "Address: " + Html.fromHtml(temp.getAddress()) + "\n" + "Contact no: " + Html.fromHtml(temp.getPhoneno()))
//                .start();
        composeEmail(email, "Store Finder", body);

    }

    public void composeEmail(String address, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, address);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void sendEmail() {
        // TODO Auto-generated method stub
        String recipient = email;
        String subject = "store finder";
        @SuppressWarnings("unused")
        String body = "";

        String[] recipients = {recipient};
        Intent email = new Intent(Intent.ACTION_SEND);

        email.setType("message/rfc822");

        email.putExtra(Intent.EXTRA_EMAIL, recipients);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);

        try {

            startActivity(Intent.createChooser(email, getString(R.string.email_choose_from_client)));

        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(Detailpage.this, getString(R.string.email_no_client), Toast.LENGTH_LONG).show();

        }
    }

    // SHOW MULTIPLE IMAGE IN SLIDER

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return separated.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.image_pager, container, false);

            try {

                imageView = itemView.findViewById(R.id.image_page_fliper);

                Log.d("position", "" + separated[position].replace("[", "").replace("]", "").replace("\"", ""));
                imageurl = getString(R.string.link) + "uploads/store/full/"
                        + separated[position].replace("[", "").replace("]", "").replace("\"", "");
//
//


                Picasso.get()
                        .load(getString(R.string.link) + "uploads/store/full/"
                                + separated[position].replace("[", "").replace("]", "").replace("\"", ""))
                        .into(imageView);
                container.addView(itemView);

            } catch (NullPointerException e) {
                // TODO: handle exception
            }

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    // GET STORE DATA FROM JSON URL

    private void getdetailforNearMe() {
        // TODO Auto-generated method stub

        URL hp = null;
        try {

            hp = new URL(getString(R.string.link) + "rest/detail_store.php?store_id=" + id);

            Log.d("URL", "" + hp);
            URLConnection hpCon = hp.openConnection();
            hpCon.connect();
            InputStream input = hpCon.getInputStream();
            Log.d("input", "" + input);

            BufferedReader r = new BufferedReader(new InputStreamReader(input));

            String x = "";
            x = r.readLine();
            String total = "";

            while (x != null) {
                total += x;
                x = r.readLine();
            }
            Log.d("URLTOTAL", "" + total);
            JSONObject jObject = new JSONObject(total);
            JSONArray j = jObject.getJSONArray("Stores");

            Log.d("URL1", "" + j);
            for (int i = 0; i < j.length(); i++) {

                JSONObject Obj;
                Obj = j.getJSONObject(i);

                Storegetset temp = new Storegetset();

                temp.setName(Obj.getString("name"));
                temp.setStore_id(Obj.getString("store_id"));
                temp.setAddress(Obj.getString("address"));
                temp.setDescription(Obj.getString("description"));
                temp.setLat(Obj.getString("lat"));
                temp.setTotal_review(Obj.getString("total_review"));
                temp.setLongi(Obj.getString("long"));
                temp.setRatting(Obj.getString("ratting"));
                temp.setSmsno(Obj.getString("sms no"));
                temp.setEmail(Obj.getString("email"));
                temp.setWebsite(Obj.getString("website"));
                temp.setCategory_id(Obj.getString("categoery_id"));
                temp.setPhoneno(Obj.getString("phone no"));
                temp.setImages(Obj.getString("images"));
                Log.d("tempimage", "" + Obj.getString("images"));
                temp.setFeatured(Obj.getString("featured"));
                temp.setCreated_at(Obj.getString("created_at"));
                temp.setUpdated_at(Obj.getString("updated_at"));
                rest.add(temp);
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Error = e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Error = e.getMessage();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Error = e.getMessage();
        } catch (NullPointerException e) {
            // TODO: handle exception
            Error = e.getMessage();
        }
    }

    // check store is already favorite or not

    private class getList extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            FileList.clear();
//            DBAdapter myDbHelper = new DBAdapter(Detailpage.this);
//            myDbHelper = new DBAdapter(Detailpage.this);
//            try {
//                myDbHelper.createDataBase();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//
//            try {
//
//                myDbHelper.openDataBase();
//
//            } catch (SQLException sqle) {
//                sqle.printStackTrace();
//            }
//
//            int i = 1;
//            db = myDbHelper.getReadableDatabase();
            sqlitehelper myDbHelper=new sqlitehelper(Detailpage.this);
            SQLiteDatabase db = myDbHelper.getReadableDatabase();

            try {
                cur = db.rawQuery("select * from favourite where store_id=" + id + ";", null);
                Log.d("SIZWA", "" + cur.getCount());
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            Getsetfav obj = new Getsetfav();
                            store_id = cur.getString(cur.getColumnIndex("store_id"));
                            name1 = cur.getString(cur.getColumnIndex("name"));
                            address = cur.getString(cur.getColumnIndex("address"));
                            distance = cur.getString(cur.getColumnIndex("distance"));
                            obj.setName(name1);
                            obj.setAddress(address);
                            obj.setStore_id(store_id);
                            obj.setDistance(distance);
                            FileList.add(obj);

                        } while (cur.moveToNext());

                    }

                }

                cur.close();
                db.close();
                myDbHelper.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (FileList.size() == 0) {
                Log.e("favrt", "no");
                fvtr = true;
                fav.setImageResource(R.drawable.unfavorite_btn);

            } else {
                Log.e("favrt", "yes");
                fvtr = false;
                fav.setImageResource(R.drawable.favorite_btn);


            }

        }
    }

    private void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
        setUpMarkersHashMap();
        markersHashMap.put(customMarker, marker);
    }

    private void setUpMarkersHashMap() {
        if (markersHashMap == null) {
            markersHashMap = new HashMap<>();
        }
    }

    public void zoomToMarkers(View v) {
        zoomAnimateLevelToFitMarkers(120);
    }

    private void zoomAnimateLevelToFitMarkers(int padding) {
        Iterator<Entry<CustomMarker, Marker>> iter = markersHashMap.entrySet().iterator();
        LatLngBounds.Builder b = new LatLngBounds.Builder();

        LatLng ll = null;
        while (iter.hasNext()) {
            Map.Entry mEntry = iter.next();
            CustomMarker key = (CustomMarker) mEntry.getKey();
            ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());

            b.include(ll);
        }
        LatLngBounds bounds = b.build();
        Log.d("bounds", "" + bounds);

        // Change the padding as per needed
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 400, 17);
        googleMap.animateCamera(cu);

    }

    // Show InterstitialAd

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Your code to show add

                if (interstitialCanceled) {

                } else {

                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();

                    } else {

                        // ContinueIntent();
                    }
                }

            }
        }, 5000);
    }

    private void CallNewInsertial() {
        cd = new ConnectionDetector(Detailpage.this);

        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(Detailpage.this, getString(R.string.internet), getString(R.string.internettext),
                    false);
        } else {

            Log.d("call", "call");

            mInterstitialAd = new InterstitialAd(Detailpage.this);
            mInterstitialAd.setAdUnitId(getString(R.string.insertial_ad_key));
            requestNewInterstitial();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {

                }

            });

        }
    }

    private void requestNewInterstitial() {
        Log.d("request", "request");
        final AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

    }

    @Override
    public void onPause() {
        mInterstitialAd = null;
        interstitialCanceled = true;
        super.onPause();
    }

    // for image sharing on social sites

    private Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
//            bmpUri = Uri.fromFile(file);
            bmpUri = FileProvider.getUriForFile(Detailpage.this, getResources().getString(R.string.providerid), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


}
