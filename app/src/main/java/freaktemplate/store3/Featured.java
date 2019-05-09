package freaktemplate.store3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import freaktemplate.getset.Storegetset;
import freaktemplate.utils.AlertDialogManager;
import freaktemplate.utils.ConnectionDetector;
import freaktemplate.utils.GPSTracker;
import freaktemplate.utils.ImageLoader;

public class Featured extends Activity {
    private ProgressDialog progressDialog;
    private ArrayList<Storegetset> rest;
    private ArrayList<Storegetset> rest2;
    private String Error;
    private int from = 0;
    private int to = 3;
    private Boolean isInternetPresent = false;
    private double latitude;
    private double longitude;
    private String checknull = "";
    private ListView list_news;
    private Button btn_load;
    private View layout12;
    private RelativeLayout rl_dialoguser;
    private AlertDialogManager alert = new AlertDialogManager();
    private ConnectionDetector cd;
    private GPSTracker gps;
    private Typeface tf1;
    private InterstitialAd mInterstitialAd;
    private boolean interstitialCanceled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured);

        adsview();
        initialise();

    }

    private void initialise() {
        // TODO Auto-generated method stub

        tf1 = Typeface.createFromAsset(Featured.this.getAssets(), "fonts/Roboto-Medium.ttf");

        TextView txt_header = findViewById(R.id.txt_header);
        txt_header.setTypeface(tf1);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            gps = new GPSTracker(Featured.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                try {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                } catch (NumberFormatException e) {
                    // TODO: handle exception
                }
            } else {
                gps.showSettingsAlert();
            }
        }

        rest = new ArrayList<>();
        rest2 = new ArrayList<>();

        new getfeatured().execute();

        try {
            layout12 = getLayoutInflater().inflate(R.layout.footer_layout, rl_dialoguser, false);

        } catch (InflateException e) {
            // TODO: handle exception
        }

        // load more button click

        btn_load = layout12.findViewById(R.id.btn_load);
        btn_load.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                new getstoreloaddetail().execute();
            }
        });
    }

    private void adsview() {
        // TODO Auto-generated method stub
        if (getString(R.string.bannerads).equals("yes")) {
            AdView mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else if (getString(R.string.bannerads).equals("no")) {

            AdView mAdView = findViewById(R.id.adView);
            mAdView.setVisibility(View.GONE);

        }

        if (getString(R.string.insertialads).equals("yes")) {
            interstitialCanceled = false;
            CallNewInsertial();
        } else if (getString(R.string.insertialads).equals("no")) {

        }
    }

    // get featured store from json

    class getfeatured extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Featured.this);
            progressDialog.setMessage(getString(R.string.progress_dialog));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getdetailforstore();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();

                try {
                    if (checknull.equals("notnull")) {
                        list_news = findViewById(R.id.list_news);
                        list_news.addFooterView(layout12);
                        LazyAdapter lazy = new LazyAdapter(Featured.this, rest);
                        lazy.notifyDataSetChanged();
                        list_news.setAdapter(lazy);
                        list_news.setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                // TODO Auto-generated method stub
                                Intent iv = new Intent(Featured.this, Detailpage.class);
                                iv.putExtra("rating", "" + rest.get(arg2).getRatting());
                                iv.putExtra("name", "" + rest.get(arg2).getName());
                                iv.putExtra("id", "" + rest.get(arg2).getStore_id());
                                iv.putExtra("distance", "" + rest.get(arg2).getDistance());
                                startActivity(iv);
                            }
                        });
                    } else {

                        RelativeLayout rl_back = findViewById(R.id.rl_back);
                        if (rl_back == null) {
                            RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);
                            layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
                            rl_dialoguser.addView(layout12);
                            rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Featured.this, R.anim.popup));
                            Button btn_yes = layout12.findViewById(R.id.btn_yes);
                            btn_yes.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    // finish();
                                    // rl_dialoguser.setVisibility(View.GONE);
                                    View myView = findViewById(R.id.rl_back);
                                    ViewGroup parent = (ViewGroup) myView.getParent();
                                    parent.removeView(myView);
                                }
                            });
                        }

                    }
                } catch (NullPointerException e) {
                    // TODO: handle exception
                }

            }

        }

    }

    // binding data in listview

    class LazyAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<Storegetset> data;
        private LayoutInflater inflater = null;
        Typeface tf = Typeface.createFromAsset(Featured.this.getAssets(), "fonts/Roboto-Medium.ttf");

        private int lastPosition = -1;

        LazyAdapter(Activity a, ArrayList<Storegetset> str) {
            activity = a;
            data = str;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void addItems(ArrayList<Storegetset> fileList) {
            // TODO Auto-generated method stub
            if (data != null) {
                data.addAll(fileList);
            } else {
                data = fileList;
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;

            if (convertView == null) {

                vi = inflater.inflate(R.layout.home_cell, null);
            }

            try {

                TextView txt_rname = vi.findViewById(R.id.txt_name);
                txt_rname.setText(Html.fromHtml(data.get(position).getName()));
                txt_rname.setTypeface(tf);

                TextView txt_add = vi.findViewById(R.id.txt_address);
                txt_add.setText(Html.fromHtml(data.get(position).getAddress()));
                txt_add.setTypeface(tf);

                TextView txt_distance = vi.findViewById(R.id.txt_distance);
                txt_distance.setText(data.get(position).getDistance() + "km");

                ImageView img_featured = vi.findViewById(R.id.img_featured);
                String featured = data.get(position).getFeatured();

                if (featured.equals("1")) {
                    img_featured.setImageResource(R.drawable.feature_text_bg);
                } else if (featured.equals("0")) {
                    // img_featured.setImageResource(R.drawable.feature_text_bg);
                }

                String image = data.get(position).getThumbnail().replace(" ", "%20");

                ImageView programImage = vi.findViewById(R.id.img_storediff);
                programImage.setImageResource(R.drawable.homepage_load_img);
                Picasso.get()
                        .load(getString(R.string.link) + "uploads/store/full/" + image)
                        .into(programImage);
                ImageLoader imgLoader = new ImageLoader(Featured.this);

                //imgLoader.DisplayImage(getString(R.string.link) + "uploads/store/full/" + image, programImage);
                RatingBar ratb = vi.findViewById(R.id.rate1);
                ratb.setFocusable(false);
                ratb.setRating(Float.parseFloat(data.get(position).getRatting()));
                ratb.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) { // TODO

                    }

                });
            } catch (NullPointerException e) {
                // TODO: handle exception
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }


            return vi;
        }
    }

    // get featured store from json url

    private void getdetailforstore() {
        // TODO Auto-generated method stub

        URL hp = null;
        try {
            rest.clear();

            hp = new URL(getString(R.string.link) + "rest/featuredstores.php?lat=" + latitude + "&&long=" + longitude
                    + "&from=" + from + "&to=" + to);
            URLConnection hpCon = hp.openConnection();
            hpCon.connect();

            InputStream input = hpCon.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(input));

            String x = "";
            x = r.readLine();
            String total = "";

            while (x != null) {
                total += x;
                x = r.readLine();
            }
            JSONObject jObject = new JSONObject(total);
            String currentKey = "";
            Iterator<String> iterator = jObject.keys();
            while (iterator.hasNext()) {
                currentKey = iterator.next();
            }
            if (currentKey.equals("Stores")) {
                checknull = "notnull";
                JSONArray j = jObject.getJSONArray("Stores");
                // JSONArray j = new JSONArray(total);
                for (int i = 0; i < j.length(); i++) {

                    JSONObject Obj;
                    Obj = j.getJSONObject(i);
                    // JSONArray jarr = Obj.getJSONArray("images");
                    Storegetset temp = new Storegetset();
                    temp.setDistance(Obj.getString("distance"));
                    temp.setStore_id(Obj.getString("store_id"));
                    temp.setName(Obj.getString("name"));
                    temp.setAddress(Obj.getString("address"));
                    temp.setLat(Obj.getString("latitude"));
                    temp.setLongi(Obj.getString("longitude"));
                    temp.setFeatured(Obj.getString("featured"));
                    temp.setThumbnail(Obj.getString("thumbnail"));
                    temp.setRatting(Obj.getString("ratting"));
                    rest.add(temp);

                }
            } else if (currentKey.equals("id")) {
                checknull = "null";
                JSONObject Obj;
                Obj = jObject.getJSONObject("id");
                Storegetset temp = new Storegetset();
                temp.setStore_id(Obj.getString("id"));
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

    // load more store on click of load more button

    class getstoreloaddetail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Featured.this);
            progressDialog.setMessage(getString(R.string.progress_dialog));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getdetailforNearMeload();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            list_news = findViewById(R.id.list_news);
            if (Error != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                RelativeLayout rl_back = findViewById(R.id.rl_back);
                if (rl_back == null) {
                    rl_dialoguser = findViewById(R.id.rl_infodialog);
                    layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
                    rl_dialoguser.addView(layout12);
                    rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Featured.this, R.anim.popup));
                    Button btn_yes = layout12.findViewById(R.id.btn_yes);
                    btn_yes.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub

                            View myView = findViewById(R.id.rl_back);
                            ViewGroup parent = (ViewGroup) myView.getParent();
                            parent.removeView(myView);
                        }
                    });
                }
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();


                    try {
                        int currentPosition = list_news.getFirstVisiblePosition();

                        LazyAdapter lazy = new LazyAdapter(Featured.this, rest2);

                        LazyAdapter myExistingAdapter = null;
                        lazy.notifyDataSetChanged();

                        if (list_news.getAdapter() instanceof WrapperListAdapter) {
                            myExistingAdapter = (LazyAdapter) ((WrapperListAdapter) list_news.getAdapter())
                                    .getWrappedAdapter();
                        } else if (list_news.getAdapter() instanceof LazyAdapter) {
                            myExistingAdapter = (LazyAdapter) list_news.getAdapter();
                        }
                        myExistingAdapter.addItems(rest2);
                        myExistingAdapter.notifyDataSetChanged();

                        // Setting new scroll position
                        list_news.setSelectionFromTop(currentPosition + 1, 0);
                        // list_detail.setAdapter(lazy);
                    } catch (NullPointerException e) {
                        // TODO: handle exception
                    } catch (NumberFormatException e) {
                        // TODO: handle exception
                    }


                }

            }

        }

    }

    // load more store from json url

    private void getdetailforNearMeload() {
        // TODO Auto-generated method stub

        URL hp = null;
        try {

            from = from + 3;


            hp = new URL(getString(R.string.link) + "rest/featuredstores.php?lat=" + latitude + "&&long=" + longitude
                    + "&from=" + from + "&to=" + to);

            URLConnection hpCon = hp.openConnection();
            hpCon.connect();
            InputStream input = hpCon.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input));

            String x = "";
            x = r.readLine();
            String total = "";

            while (x != null) {
                total += x;
                x = r.readLine();
            }
            JSONObject jObject = new JSONObject(total);
            String currentKey = "";
            Iterator<String> iterator = jObject.keys();
            while (iterator.hasNext()) {
                currentKey = iterator.next();
            }

            JSONArray j = jObject.getJSONArray("Stores");
            for (int i = 0; i < j.length(); i++) {

                JSONObject Obj;
                Obj = j.getJSONObject(i);


                Storegetset temp = new Storegetset();
                temp.setStore_id(Obj.getString("store_id"));
                temp.setName(Obj.getString("name"));
                temp.setAddress(Obj.getString("address"));
                temp.setDistance(Obj.getString("distance"));
                temp.setFeatured(Obj.getString("featured"));
                temp.setRatting(Obj.getString("ratting"));
                temp.setThumbnail(Obj.getString("thumbnail"));
                rest2.add(temp);
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

    // show InterstitialAd

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
        cd = new ConnectionDetector(Featured.this);

        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(Featured.this, getString(R.string.internet), getString(R.string.internettext), false);
        } else {
            mInterstitialAd = new InterstitialAd(Featured.this);
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
        final AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

    }

    @Override
    public void onPause() {
        mInterstitialAd = null;
        interstitialCanceled = true;
        super.onPause();
    }

}
