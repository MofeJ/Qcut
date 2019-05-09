package freaktemplate.store3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import freaktemplate.utils.AlertDialogManager;
import freaktemplate.utils.ConnectionDetector;
import freaktemplate.utils.ImageLoader;
import freaktemplate.utils.Reviewgetset;
import freaktemplate.utils.User;

public class Review extends Activity {
    private String id;
    private String key;
    private String Error;
    private static final String MY_PREFS_NAME = "Store";
    private String uservalue;
    private String pic;
    private String usercomment;
    private String userrate;
    private ProgressDialog progressDialog;
    private ArrayList<Reviewgetset> rest;
    private ArrayList<User> rest1;
    private ListView list_review;
    private Button btn_add;
    private View layout12;
    RelativeLayout rl_home;
    private RelativeLayout rl_back;
    private ImageView img_back;
    private EditText edt_comment;
    private RatingBar rb1234;

    private InterstitialAd mInterstitialAd;
    private boolean interstitialCanceled;
    private ConnectionDetector cd;
    private AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        adsview();
        init();

        new getreviewdetail().execute();
    }

    private void init() {
        // TODO Auto-generated method stub
        Typeface tf = Typeface.createFromAsset(Review.this.getAssets(), "fonts/Roboto-Light.ttf");

        TextView txt_head = findViewById(R.id.textView1);
        txt_head.setTypeface(tf);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        // check user is created or not
        if (prefs.getString("myfbpic", null) != null) {
            pic = prefs.getString("myfbpic", null);
        }
        rl_back = findViewById(R.id.rl_back);
        rest = new ArrayList<>();
        rest1 = new ArrayList<>();
        try {
            Intent iv = getIntent();
            id = iv.getStringExtra("id");
        } catch (NullPointerException e) {
            // TODO: handle exception
        }

        btn_add = findViewById(R.id.btn_add);


        btn_add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                layout12 = v;


                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                if (prefs.getString("score", null) != null) {
                    uservalue = prefs.getString("score", null);
                    //list_review.setEnabled(false);
                    if (uservalue.equals("delete")) {

                        RelativeLayout rl_back = findViewById(R.id.rl_back);
                        if (rl_back == null) {
                            final RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

                            try {
                                layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
                            } catch (InflateException e) {
                                // TODO: handle exception
                            }


                            rl_dialoguser.addView(layout12);
                            rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Review.this, android.R.anim.slide_in_left));

                            TextView txt_dia = layout12.findViewById(R.id.txt_dia);
                            txt_dia.setText(getString(R.string.error_required_registration));

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

                    } else {

                        if (rl_back == null) {
                            RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);
                            layout12 = getLayoutInflater().inflate(R.layout.ratedialog, rl_dialoguser, false);
                            rl_dialoguser.addView(layout12);

                            edt_comment = layout12.findViewById(R.id.txt_description);
                            rb1234 = layout12.findViewById(R.id.rate1234);
                            Button btn_submit = layout12.findViewById(R.id.btn_submit);
                            btn_submit.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    // rl_adddialog.setVisibility(View.GONE);
                                    layout12 = v;
                                    // list_review.setEnabled(true);
                                    // rl_home.setAlpha(1.0f);

                                    try {
                                        usercomment = edt_comment.getText().toString().replace(" ", "%20");
                                        userrate = String.valueOf(rb1234.getRating());
                                        if (usercomment.equals(null)) {
                                            usercomment = "";
                                        }
                                    } catch (NullPointerException e) {
                                        // TODO: handle exception
                                    }
                                    if (usercomment.equals("")) {
                                        edt_comment.setError("Review Please");
                                    } else {
                                        new getratedetail().execute();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(Review.this);
                                        builder.setMessage(getString(R.string.dialog_description))
                                                .setTitle(getString(R.string.dialog_title));

                                        builder.setNeutralButton(android.R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                        new getreviewdetail().execute();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                        View myView = findViewById(R.id.rl_back);
                                        ViewGroup parent = (ViewGroup) myView.getParent();
                                        parent.removeView(myView);
                                        img_back = findViewById(R.id.img_back);
                                    }
                                }
                            });

                            Button btn_cancel = layout12.findViewById(R.id.btn_cancel);
                            btn_cancel.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    list_review.setEnabled(true);
                                    View myView = findViewById(R.id.rl_back);
                                    ViewGroup parent = (ViewGroup) myView.getParent();
                                    parent.removeView(myView);
                                    img_back = findViewById(R.id.img_back);

                                }
                            });

                        }
                    }
                } else {

                    RelativeLayout rl_back = findViewById(R.id.rl_back);
                    if (rl_back == null) {
                        final RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

                        try {
                            layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);

                        } catch (InflateException e) {
                            // TODO: handle exception
                        }

                        rl_dialoguser.addView(layout12);
                        rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Review.this, R.anim.popup));

                        TextView txt_dia = layout12.findViewById(R.id.txt_dia);
                        txt_dia.setText(getString(R.string.error_required_registration));

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

    class getreviewdetail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Review.this);
            progressDialog.setMessage("Loading");
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
                if (key.equals("status")) {
                    Toast.makeText(Review.this, getString(R.string.error_add_review), Toast.LENGTH_LONG).show();

                } else if (key.equals("user")) {

                    list_review = findViewById(R.id.list_review);
                    LazyAdapter lazy = new LazyAdapter(Review.this, rest);
                    list_review.setAdapter(lazy);

                    list_review.startAnimation(AnimationUtils.loadAnimation(Review.this, R.anim.lefttoright));
                    list_review.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            // TODO Auto-generated method stub
                            //	final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);

                            list_review.setEnabled(false);
                            if (rl_back == null) {
                                final RelativeLayout rl_dialog = findViewById(R.id.rl_infodialog);
                                layout12 = getLayoutInflater().inflate(R.layout.reviewclick_dialog, rl_dialog, false);
                                rl_dialog.addView(layout12);
                                rl_dialog.startAnimation(AnimationUtils.loadAnimation(Review.this, android.R.anim.fade_in));
                                TextView txt_name_comment = layout12.findViewById(R.id.txt_nameuser);
                                txt_name_comment.setText("" + Html.fromHtml(rest.get(position).getUsername()));
                                try {
                                    RatingBar rb = layout12.findViewById(R.id.rate1234);
                                    rb.setRating(Float.parseFloat(rest.get(position).getRatting()));
                                } catch (NumberFormatException e) {
                                    // TODO: handle exception
                                }

                                try {
                                    TextView txt_comment_desc = layout12.findViewById(R.id.txt_desc);
                                    txt_comment_desc.setText(URLDecoder.decode(rest.get(position).getReview(), "UTF-8"));
                                } catch (UnsupportedEncodingException e) {

                                }
                                String image = rest.get(position).getImage().replace("\\", "");

                                ImageView img_user = layout12.findViewById(R.id.img_my);
                                img_user.setImageResource(R.drawable.default_img);
                                if (image != null) {
                                    new DownloadImageTask(img_user).execute(image);
                                } else {
                                    img_user.setImageResource(R.drawable.default_img);
                                }

                                Button btn_ok = layout12.findViewById(R.id.btn_ok);
                                btn_ok.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        list_review.setEnabled(true);
                                        rl_dialog.startAnimation(AnimationUtils.loadAnimation(Review.this, android.R.anim.fade_out));
                                        View myView = findViewById(R.id.rl_back);
                                        ViewGroup parent = (ViewGroup) myView.getParent();
                                        parent.removeView(myView);
                                    }
                                });
                            }
                        }
                    });
                }

            }
        }

    }

    // get review data from url
    private void getdetailforNearMe() {
        // TODO Auto-generated method stub
        URL hp = null;
        try {
            rest.clear();
            hp = new URL(getString(R.string.link) + "rest/get_ratting_review.php?store_id=" + id);
            Log.e("url", String.valueOf(hp));
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
            if (currentKey.equals("Status")) {
                key = "status";
                JSONArray j1 = jObject.getJSONArray("Status");
                for (int i = 0; i < j1.length(); i++) {
                    JSONObject Obj;
                    Obj = j1.getJSONObject(i);
                    Reviewgetset temp = new Reviewgetset();
                    temp.setId(Obj.getString("id"));
                    rest.add(temp);

                }
            } else if (currentKey.equals("Review")) {
                key = "user";
                JSONArray j = jObject.getJSONArray("Review");
                // JSONArray j = new JSONArray(total);
                for (int i = 0; i < j.length(); i++) {

                    JSONObject Obj;
                    Obj = j.getJSONObject(i);

                    Reviewgetset temp = new Reviewgetset();

                    temp.setReview_id(Obj.getString("review_id"));
                    temp.setReview(Obj.getString("review"));

                    temp.setStore_id(Obj.getString("store_id"));
                    temp.setRatting(Obj.getString("ratting"));
                    temp.setUsername(Obj.getString("username"));
                    temp.setImage(Obj.getString("image"));
                    Log.e("imagereview", temp.getImage() + "no");
                    rest.add(temp);

                }

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

    // bind data in listview

    class LazyAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<Reviewgetset> data;
        private LayoutInflater inflater = null;
        Typeface tf = Typeface.createFromAsset(Review.this.getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface tf1 = Typeface.createFromAsset(Review.this.getAssets(), "fonts/Roboto-Light.ttf");

        String s;

        LazyAdapter(Activity a, ArrayList<Reviewgetset> str) {
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


                vi = inflater.inflate(R.layout.reviewcell, null);
            }


            try {
                TextView txt_name = vi.findViewById(R.id.text_name);
                txt_name.setText(Html.fromHtml(data.get(position).getUsername()));
                txt_name.setTypeface(tf);

                TextView txt_comment = vi.findViewById(R.id.txt_review);
                txt_comment.setText(URLDecoder.decode(data.get(position).getReview(), "UTF-8"));
                txt_comment.setTypeface(tf1);
                String image = "";

                image = data.get(position).getImage().replace("\\", "");
                Log.e("image",image);
                ImageView img_user = vi.findViewById(R.id.img_user);
                img_user.setImageResource(R.drawable.default_img);
                if (image != null) {
                    Picasso.get().load(image).placeholder(R.drawable.default_img).into(img_user);
                } else {
                    img_user.setImageResource(R.drawable.default_img);
                }
                RatingBar rb = vi.findViewById(R.id.rate1);
                rb.setRating(Float.parseFloat(data.get(position).getRatting()));
            } catch (NumberFormatException e) {
                // TODO: handle exception
            } catch (NullPointerException e) {
                // TODO: handle exception
            } catch (UnsupportedEncodingException e) {

            }

            return vi;
        }
    }

    // post review on server

    class getratedetail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            URL hp = null;
            try {

                hp = new URL(getString(R.string.link) + "rest/post_ratting_review.php?user_id=" + uservalue
                        + "&&store_id=" + id + "&&ratting=" + userrate + "&review=" + URLEncoder.encode(usercomment, "UTF-8"));

                URLConnection hpCon = hp.openConnection();
                hpCon.connect();
                InputStream input = hpCon.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(input));

                String x = "";
                // x = r.readLine();
                String total = "";

                while (x != null) {
                    total += x;
                    x = r.readLine();
                }
                JSONObject j = new JSONObject("Status");
                for (int i = 0; i < j.length(); i++) {
                    JSONObject Obj;
                    Obj = j.getJSONObject(String.valueOf(i));
                    User temp = new User();
                    temp.setStatus(Obj.getString("Status"));
                    rest1.add(temp);
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NullPointerException e) {
                // TODO: handle exception
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

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


                    }
                }

            }
        }, 5000);
    }

    private void CallNewInsertial() {
        cd = new ConnectionDetector(Review.this);

        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(Review.this, getString(R.string.internet), getString(R.string.internettext), false);
        } else {
            // AdView mAdView = (AdView) findViewById(R.id.adView);
            // AdRequest adRequest = new AdRequest.Builder().build();
            // mAdView.loadAd(adRequest);
            mInterstitialAd = new InterstitialAd(Review.this);
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

    // download image from url

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Bitmap mIcon11;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
