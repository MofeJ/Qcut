package freaktemplate.store3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import freaktemplate.getset.Newsgetset;
import freaktemplate.utils.AlertDialogManager;
import freaktemplate.utils.ConnectionDetector;

public class News extends Activity {
	private ProgressDialog progressDialog;
	private ArrayList<Newsgetset> rest;
	private String Error;
	private String checknull="";
	private ListView list_news;
	private View layout12;
	private Typeface tf1;
	private InterstitialAd mInterstitialAd;
	private boolean interstitialCanceled;
	private ConnectionDetector cd;
	private AlertDialogManager alert = new AlertDialogManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		adsview();
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		tf1 = Typeface.createFromAsset(News.this.getAssets(), "fonts/Roboto-Light.ttf");
		TextView txt_header = findViewById(R.id.txt_header);
		txt_header.setTypeface(tf1);
		rest = new ArrayList<>();
		new getnews().execute();
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



	class getnews extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(News.this);
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

				if (checknull.equals("notnull")) {
					list_news = findViewById(R.id.list_news);

					LazyAdapter lazy = new LazyAdapter(News.this, rest);
					lazy.notifyDataSetChanged();
					list_news.setAdapter(lazy);

					// onclick of list news particular news open in browser
					final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
					list_news.startAnimation(buttonClick);
					list_news.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
							// TODO Auto-generated method stub

							Intent browserIntent = new Intent(Intent.ACTION_VIEW,
									Uri.parse(rest.get(arg2).getNews_url()));
							startActivity(browserIntent);
						}
					});
				} else  {

					RelativeLayout rl_back = findViewById(R.id.rl_back);
					if (rl_back == null) {
						RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);
						try {
							layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);
						} catch (InflateException e) {
							// TODO: handle exception
						}

						rl_dialoguser.addView(layout12);
						rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(News.this, R.anim.popup));
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

	// binding data in listview

	class LazyAdapter extends BaseAdapter {

		private Activity activity;
		private ArrayList<Newsgetset> data;
		private LayoutInflater inflater = null;
		Typeface tf = Typeface.createFromAsset(News.this.getAssets(), "fonts/Roboto-Medium.ttf");
		private int lastPosition = -1;

		LazyAdapter(Activity a, ArrayList<Newsgetset> str) {
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
				vi = inflater.inflate(R.layout.news_cell, null);
			}
			try {
				TextView txt_rname = vi.findViewById(R.id.txt_name);
				txt_rname.setText(Html.fromHtml(data.get(position).getNews_title()));
				txt_rname.setTypeface(tf);
				TextView txt_add = vi.findViewById(R.id.txt_address);
				txt_add.setText(Html.fromHtml(data.get(position).getNews_content()));
				txt_add.setTypeface(tf);
				String date = data.get(position).getCreated_at();
				long timestamp = Long.parseLong(date) * 1000;
				TextView txt_distance = vi.findViewById(R.id.txt_distance);
				txt_distance.setText(getDate(timestamp));
				txt_distance.setTypeface(tf);
				String image = data.get(position).getNews_image().replace(" ","%20");
				// new changes
				ImageView programImage = vi.findViewById(R.id.img_storediff);
				Picasso.get()
						.load(getString(R.string.link)+"uploads/news/" + image)
						.into(programImage);
			} catch (NullPointerException e) {
				// TODO: handle exception
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}
			return vi;
		}
	}

	// get date from timestamp

	private String getDate(long timeStamp) {

		try {
			DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date netDate = (new Date(timeStamp));
			return sdf.format(netDate);
		} catch (Exception ex) {
			return "xx";
		}
	}


	// get news data from json url

	private void getdetailforstore() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {
			rest.clear();

			hp = new URL(getString(R.string.link) + "rest/news.php");
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

			if (currentKey.equals("News")) {
				checknull = "notnull";
				JSONArray j = jObject.getJSONArray("News");


				for (int i = 0; i < j.length(); i++) {
					JSONObject Obj;
					Obj = j.getJSONObject(i);
					Newsgetset temp = new Newsgetset();
					temp.setNews_content(Obj.getString("news_content"));
					temp.setNews_title(Obj.getString("news_title"));
					temp.setNews_image(Obj.getString("news_image"));
					temp.setCreated_at(Obj.getString("created_at"));
					temp.setNews_url(Obj.getString("news_url"));
					temp.setUpdated_at(Obj.getString("updated_at"));

					rest.add(temp);

				}

			} else if (currentKey.equals("Massage")) {
				checknull = "null";
				JSONArray j1 = jObject.getJSONArray("Massage");
				JSONObject Obj;
				Obj = j1.getJSONObject(0);
				Newsgetset temp = new Newsgetset();
				temp.setNews_title(Obj.getString("id"));
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
		cd = new ConnectionDetector(News.this);

		if (!cd.isConnectingToInternet()) {
			alert.showAlertDialog(News.this, getString(R.string.internet), getString(R.string.internettext), false);
        } else {
			// AdView mAdView = (AdView) findViewById(R.id.adView);
			// AdRequest adRequest = new AdRequest.Builder().build();
			// mAdView.loadAd(adRequest);
			mInterstitialAd = new InterstitialAd(News.this);
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
