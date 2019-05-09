package freaktemplate.store3;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import freaktemplate.utils.AlertDialogManager;
import freaktemplate.utils.ConnectionDetector;

public class About extends Activity {
    private final AlertDialogManager alert = new AlertDialogManager();
    private InterstitialAd mInterstitialAd;
    private boolean interstitialCanceled;
    private ProgressDialog progressDialog;
    private String text = "";
    private View layout12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        adsview();
        initialize();
    }
    private void initialize() {
        // TODO Auto-generated method stub
        new getabout().execute();
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
        }
    }
    private void getdetailforNearMe() {
        // TODO Auto-generated method stub

        URL hp;
        String error;
        try {
            hp = new URL(getString(R.string.link) + "rest/aboutus.php");
            URLConnection hpCon = hp.openConnection();
            hpCon.connect();
            InputStream input = hpCon.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input));
            String x;
            x = r.readLine();
            StringBuilder total = new StringBuilder();
            while (x != null) {
                total.append(x);
                x = r.readLine();
            }
            JSONObject jObject = new JSONObject(total.toString());
            text = jObject.getString("about_us");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            error = e.getMessage();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            error = e.getMessage();
            e.printStackTrace();
        } catch (NullPointerException e) {
            // TODO: handle exception
            error = e.getMessage();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Your code to show add
                if (!interstitialCanceled) {
                    if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                }

            }
        }, 5000);
    }
    private void CallNewInsertial() {
        ConnectionDetector cd = new ConnectionDetector(About.this);
        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(About.this, getString(R.string.internet), getString(R.string.internettext), false);
        } else {
            mInterstitialAd = new InterstitialAd(About.this);
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
    class getabout extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(About.this);
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
            if (text.isEmpty()) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                RelativeLayout rl_back = findViewById(R.id.rl_back);
                if (rl_back == null) {
                    RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);
                    try {
                        layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
                    } catch (InflateException e) {
                        // TODO: handle exception
                    }
                    rl_dialoguser.addView(layout12);
                    rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(About.this, R.anim.popup));
                    Button btn_yes = layout12.findViewById(R.id.btn_yes);
                    btn_yes.setOnClickListener(new View.OnClickListener() {

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
                }
                String htmltxt = "<!DOCTYPE html><head><meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=iso-8859-1\\\"></head><body>" + text + "</body></html>";
                WebView wv = findViewById(R.id.web);
                wv.getSettings().setJavaScriptEnabled(true);
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                wv.loadData("" + Html.fromHtml(text), "text/html", "UTF-8");
                TextView te = findViewById(R.id.text);
                te.setText(Html.fromHtml(text));
                te.setVisibility(View.VISIBLE);
            }
        }
    }
}
