package freaktemplate.store3;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.InflateException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

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


public class Terms extends Activity {
	Typeface tf1;
	private InterstitialAd mInterstitialAd;
	private boolean interstitialCanceled;
	private ConnectionDetector cd;
	private AlertDialogManager alert = new AlertDialogManager();
	private Button btn_home;
	private ProgressDialog progressDialog;
	private String text="";
	private String Error;
	private View layout12;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms);
		
		adview();
		init();
		
	}

	private void init() {
		// TODO Auto-generated method stub
		btn_home= findViewById(R.id.btn_home);
		btn_home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent iv=new Intent(Terms.this,Home.class);
				startActivity(iv);
			}
		});
		new getabout().execute();

	}

	class getabout extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Terms.this);
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


			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if(!text.equals("")) {
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
				//wv.getSettings().setPluginState(WebSettings.PluginState.ON);
				//webSetting.setBuiltInZoomControls(true);
				//wv.getSettings().setAllowContentAccess(true);
				//	wv.loadDataWithBaseURL("", text, "text/html", "UTF-}
			}
			else {
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
					rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Terms.this, R.anim.popup));
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

	private void getdetailforNearMe() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {

			hp = new URL(getString(R.string.link) + "rest/termsandconditions.php");

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
			text=jObject.getString("terms_and_conditions");
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

	private void adview() {
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

	//show InterstitialAd
	
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
		cd = new ConnectionDetector(Terms.this);

		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(Terms.this, getString(R.string.internet),
					getString(R.string.internettext), false);
        } else {

			mInterstitialAd = new InterstitialAd(Terms.this);
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
}
