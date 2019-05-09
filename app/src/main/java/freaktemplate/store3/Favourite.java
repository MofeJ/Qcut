package freaktemplate.store3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

import freaktemplate.getset.Getsetfav;
import freaktemplate.utils.AlertDialogManager;
import freaktemplate.utils.ConnectionDetector;
import freaktemplate.utils.JazzyHelper;
import freaktemplate.utils.JazzyListView;
import freaktemplate.utils.sqlitehelper;


public class Favourite extends Activity {
    private JazzyListView list_fav;
    private ArrayList<Getsetfav> FileList;
    private View layout12;
    private SQLiteDatabase db;
    private Cursor cur = null;
    private ProgressDialog progressDialog;
    private String store_id;
    private String name;
    private String address;
    private String distance;
    private int start = 0;
    private InterstitialAd mInterstitialAd;
    private boolean interstitialCanceled;
    private ConnectionDetector cd;
    private AlertDialogManager alert = new AlertDialogManager();
    private static final String KEY_TRANSITION_EFFECT = "transition_effect";
    private int mCurrentTransitionEffect = JazzyHelper.CURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        adsview();
        initialise();

        if (savedInstanceState != null) {
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT, JazzyHelper.CURL);
            setupJazziness(mCurrentTransitionEffect);
        }
    }

    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        list_fav.setTransitionEffect(mCurrentTransitionEffect);
    }

    private void initialise() {
        // TODO Auto-generated method stub
        Typeface tf = Typeface.createFromAsset(Favourite.this.getAssets(), "fonts/Roboto-Light.ttf");
        TextView txt_header = findViewById(R.id.txt_header);
        txt_header.setTypeface(tf);
        FileList = new ArrayList<>();
        new getList().execute();
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

    //getting data from database of favorite stores

    private class getList extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(Favourite.this);
            progressDialog.setMessage(getString(R.string.progress_dialog));
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            // TODO Auto-generated method stub

            FileList.clear();
//            DBAdapter myDbHelper = new DBAdapter(Favourite.this);
//            myDbHelper = new DBAdapter(Favourite.this);
//            try {
//                myDbHelper.createDataBase();
//            } catch (IOException e) {
//
//                e.printStackTrace();
//            }
//
//            try {
//                myDbHelper.openDataBase();
//            } catch (SQLException sqle) {
//                sqle.printStackTrace();
//            }
//
//            int i = 1;
//            db = myDbHelper.getReadableDatabase();

            sqlitehelper myDbHelper = new sqlitehelper(Favourite.this);
            SQLiteDatabase db = myDbHelper.getReadableDatabase();

            try {
                cur = db.rawQuery("select * from favourite;", null);
                if (cur.getCount() != 0) {
                    if (cur.moveToFirst()) {
                        do {
                            Getsetfav obj = new Getsetfav();
                            store_id = cur.getString(cur.getColumnIndex("store_id"));
                            name = cur.getString(cur.getColumnIndex("name"));
                            address = cur.getString(cur.getColumnIndex("address"));
                            distance = cur.getString(cur.getColumnIndex("distance"));
                            obj.setName(name);
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

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                list_fav = findViewById(R.id.list_detail);

                if (FileList.size() == 0) {
                    RelativeLayout rl_back = findViewById(R.id.rl_back);
                    if (rl_back == null) {
                        RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

                        layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);

                        rl_dialoguser.addView(layout12);
                        rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Favourite.this, R.anim.popup));
                        Button btn_yes = layout12.findViewById(R.id.btn_yes);
                        btn_yes.setOnClickListener(new View.OnClickListener() {

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
                    list_fav.setVisibility(View.INVISIBLE);
                } else {

                    final Getsetfav tempobj = FileList.get(start);

                    list_fav = findViewById(R.id.list_detail);
                    list_fav.setVisibility(View.VISIBLE);

                    LazyAdapter lazy = new LazyAdapter(Favourite.this, FileList);
                    lazy.notifyDataSetChanged();
                    list_fav.setAdapter(lazy);

                    list_fav.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // TODO Auto-generated method stub

                            Intent iv = new Intent(Favourite.this, Detailpage.class);
                            iv.putExtra("id", "" + FileList.get(position).getStore_id());
                            startActivity(iv);
                        }
                    });
                }

            }
        }
    }

    // bind data in listview

    class LazyAdapter extends BaseAdapter {

        private Activity activity;
        private ArrayList<Getsetfav> data;
        private LayoutInflater inflater = null;
        Typeface tf1 = Typeface.createFromAsset(Favourite.this.getAssets(), "fonts/Roboto-Medium.ttf");
        Typeface tf2 = Typeface.createFromAsset(Favourite.this.getAssets(), "fonts/Roboto-Light.ttf");

        LazyAdapter(Activity a, ArrayList<Getsetfav> d) {
            activity = a;
            data = d;
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
                if (position % 2 == 0) {
                    vi = inflater.inflate(R.layout.cellfav, null);
                } else {
                    vi = inflater.inflate(R.layout.cellfav1, null);
                }


            }
            try {
                Spanned namefirst = Html.fromHtml(data.get(position).getName());
                String s = String.valueOf(namefirst).substring(0, 1).toUpperCase();

                TextView txt_first = vi.findViewById(R.id.txt_first);
                txt_first.setText("" + Html.fromHtml(s));

                TextView txt_name = vi.findViewById(R.id.txt_rest_name);
                txt_name.setText(Html.fromHtml(data.get(position).getName()));
                txt_name.setTypeface(tf1);

                TextView txt_address = vi.findViewById(R.id.txt_address);
                txt_address.setText(Html.fromHtml(data.get(position).getAddress()));
                txt_address.setTypeface(tf2);

                TextView txt_km = vi.findViewById(R.id.txt_km);
                txt_km.setText(data.get(position).getDistance() + getString(R.string.km_sufix));
                txt_km.setTypeface(tf2);
            } catch (StringIndexOutOfBoundsException e) {
                // TODO: handle exception
                e.printStackTrace();
            } catch (NullPointerException e) {
                // TODO: handle exception
            }


            return vi;
        }
    }

    //if any store will be unfavorite then back to again this page will be refresh

    @Override
    public void onRestart() {
        super.onRestart();
        // When BACK BUTTON is pressed, the activity on the stack is restarted

        new getList().execute();
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
        cd = new ConnectionDetector(Favourite.this);

        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(Favourite.this, getString(R.string.internet),
                    getString(R.string.internettext), false);
        } else {

            mInterstitialAd = new InterstitialAd(Favourite.this);
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
