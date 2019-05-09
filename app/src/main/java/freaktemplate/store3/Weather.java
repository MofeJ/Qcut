package freaktemplate.store3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import freaktemplate.getset.Weathergetset;
import freaktemplate.utils.GPSTracker;

public class Weather extends Activity {
	private Boolean isInternetPresent = false;
	private ConnectionDetector cd;
	private GPSTracker gps;
	private double latitude;
	private double longitude;
	private String addr;
    private String name;
	private ArrayList<Weathergetset> rest;
	private String Error;
    private String cel;
	private TextView txt_cel;
    private TextView txt_addr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		rest = new ArrayList<>();
		cd = new ConnectionDetector(getApplicationContext());
		txt_addr = findViewById(R.id.txt_address);
		txt_cel = findViewById(R.id.txt_weather);
		isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent) {
			// Internet Connection is Present
			// make HTTP requests
			// showAlertDialog(getApplicationContext(), "Internet Connection",
			// "You have internet connection", true);

			gps = new GPSTracker(Weather.this);
			// check if GPS enabled
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				Log.d("latlng", "" + latitude + " hi" + longitude);
				// \n is for new line

			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}
		}
		addr = ConvertPointToLocation(latitude, longitude);

		new getWeather().execute();

	}

	class getWeather extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {
			getdetailforstore();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			double celci = Double.parseDouble(cel) - 273.15;
			txt_cel.setText("" + String.format("%.2f", celci));
			txt_addr.setText("" + name);
		}

	}

	private void getdetailforstore() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {

			hp = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude
					+ "&appid=44db6a862fba0b067b1930da0d769e98");
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
			Log.d("URL", "" + total);
			JSONObject jObject = new JSONObject(total);
			Log.d("URL1", "" + jObject);
			// JSONArray j = jObject.getJSONObject("main");
			// JSONArray j = new JSONArray(total);

			Log.d("URL1", "" + jObject);
			JSONObject jobj = jObject.getJSONObject("main");
			Log.d("jobj", "" + jobj);
			// for (int i = 0; i < jobj.length(); i++) {

			// JSONArray jarr = Obj.getJSONArray("images");
			Weathergetset temp = new Weathergetset();

			temp.setTemp(jobj.getString("temp"));
			temp.setName(jObject.getString("name"));
			Log.d("temperatute", jobj.getString("temp"));
			cel = jobj.getString("temp");
			name = jObject.getString("name");
			rest.add(temp);

			// }

			// sorting data from miles wise in home page list
			/*
			 * Collections.sort(rest, new Comparator<Restgetset>() {
			 * 
			 * @Override public int compare(Restgetset lhs, Restgetset rhs) { //
			 * TODO Auto-generated method stub return
			 * Double.compare(lhs.getMiles(), rhs.getMiles()); } });
			 */
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

	private String ConvertPointToLocation(double pointlat, double pointlog) {

		String address = "";
		Geocoder geoCoder = new Geocoder(Weather.this, Locale.getDefault());
		try {
			List<Address> addresses = geoCoder.getFromLocation(pointlat, pointlog, 1);
			if (addresses.size() > 0) {
				for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
					address += addresses.get(0).getAddressLine(index) + " ";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return id == R.id.action_settings || super.onOptionsItemSelected(item);
	}
}
