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
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import freaktemplate.utils.JazzyHelper;
import freaktemplate.utils.JazzyListView;

public class Category extends Activity {

    private ArrayList<Storegetset> rest;
    private String Error;
    private String checknull;
    private JazzyListView list_cat;
    private ProgressDialog progressDialog;
    private View layout12;
    private InterstitialAd mInterstitialAd;
    private boolean interstitialCanceled;
    private final AlertDialogManager alert = new AlertDialogManager();
    private static final String KEY_TRANSITION_EFFECT = "transition_effect";
    private int mCurrentTransitionEffect = JazzyHelper.SLIDE_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        adsview();
        initialise();

        if (savedInstanceState != null) {
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT, JazzyHelper.SLIDE_IN);
            setupJazziness(mCurrentTransitionEffect);
        }
    }

    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        list_cat.setTransitionEffect(mCurrentTransitionEffect);
    }

    private void initialise() {
        // TODO Auto-generated method stub
        Typeface tf = Typeface.createFromAsset(Category.this.getAssets(), "fonts/Roboto-Light.ttf");

        TextView txt_header = findViewById(R.id.txt_header);
        txt_header.setTypeface(tf);

        rest = new ArrayList<>();
        new getcategorydetail().execute();
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

    //get category page data from json

    class getcategorydetail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Category.this);
            progressDialog.setMessage(getString(R.string.progress_dialog));
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

            if (Error != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                RelativeLayout rl_back = findViewById(R.id.rl_back);
                if (rl_back == null) {
                    final RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

                    try {
                        layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
                    } catch (InflateException e) {
                        // TODO: handle exception
                    }


                    rl_dialoguser.addView(layout12);
                    rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Category.this, R.anim.popup));
                    Button btn_yes = layout12.findViewById(R.id.btn_yes);
                    btn_yes.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            // finish();
                            rl_dialoguser.setVisibility(View.GONE);

                        }
                    });
                }
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (checknull.equals("notnull")) {
                    list_cat = findViewById(R.id.list_cat);

                    LazyAdapter lazy = new LazyAdapter(Category.this, rest);
                    list_cat.setAdapter(lazy);
                    list_cat.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Intent iv = new Intent(Category.this, Home.class);
                            iv.putExtra("foodname", "" + rest.get(position).getCategory_id());
                            iv.putExtra("foodtype", "category");
                            startActivity(iv);
                        }
                    });
                } else if (checknull.equals("null")) {
                    RelativeLayout rl_back = findViewById(R.id.rl_back);
                    if (rl_back == null) {
                        RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

                        try {
                            layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
                        } catch (InflateException e) {
                            // TODO: handle exception
                        }


                        rl_dialoguser.addView(layout12);
                        rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Category.this, R.anim.popup));
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
            }
        }
    }

    // getting data from category json url

    private void getdetailforNearMe() {
        // TODO Auto-generated method stub

        URL hp = null;
        try {

            hp = new URL(getString(R.string.link) + "rest/category.php");

            // hp = new URL("http://192.168.1.106/restourant/foodcategory.php");

            URLConnection hpCon = hp.openConnection();
            hpCon.connect();
            InputStream input = hpCon.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input));

            String x = "";
            x = r.readLine();
            StringBuilder total = new StringBuilder();

            while (x != null) {
                total.append(x);
                x = r.readLine();
            }
            JSONObject jObject = new JSONObject(total.toString());
            String currentKey = "";
            Iterator<String> iterator = jObject.keys();
            while (iterator.hasNext()) {
                currentKey = iterator.next();
            }
            if (currentKey.equals("category")) {
                checknull = "notnull";
                JSONArray j = jObject.getJSONArray("category");
                for (int i = 0; i < j.length(); i++) {
                    JSONObject Obj;
                    Obj = j.getJSONObject(i);
                    Storegetset temp = new Storegetset();
                    temp.setName(Obj.getString("name"));
                    temp.setThumbnail(Obj.getString("image"));
                    temp.setCategory_id(Obj.getString("category_id"));
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
            Error = e.getMessage();
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO: handle exception
            Error = e.getMessage();
        }
    }

    //binding data in listview of category page

    class LazyAdapter extends BaseAdapter {

        private final Activity activity;
        private final ArrayList<Storegetset> data;
        private LayoutInflater inflater = null;
        final Typeface tf = Typeface.createFromAsset(Category.this.getAssets(), "fonts/Roboto-Light.ttf");

        LazyAdapter(Activity a, ArrayList<Storegetset> str) {
            activity = a;
            data = str;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                vi = inflater.inflate(R.layout.category_cell, null);
            }

            try {
                TextView txt_name = vi.findViewById(R.id.txt_cat);
                txt_name.setText(Html.fromHtml(data.get(position).getName()));
                txt_name.setTypeface(tf);

                String image = data.get(position).getThumbnail().replace(" ", "%20");
                ImageView img_cat = vi.findViewById(R.id.img_cat);
                img_cat.setImageResource(R.drawable.categories_img_load);
                Picasso.get()
                        .load(getString(R.string.link) + "uploads/category/" + image)
                        .into(img_cat);
            } catch (NullPointerException e) {
                // TODO: handle exception
            }


            return vi;
        }
    }


    // to show InterstitialAd

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
        ConnectionDetector cd = new ConnectionDetector(Category.this);

        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(Category.this, getString(R.string.internet),
                    getString(R.string.internettext), false);
        } else {
            mInterstitialAd = new InterstitialAd(Category.this);
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
