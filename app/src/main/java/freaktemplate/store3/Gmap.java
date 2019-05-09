package freaktemplate.store3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.InflateException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import freaktemplate.getset.Storegetset;
import freaktemplate.utils.ConnectionDetector;
import freaktemplate.utils.CustomMarker;
import freaktemplate.utils.GPSTracker;
import freaktemplate.utils.LatLngInterpolator;
import freaktemplate.utils.LatLngInterpolator.LinearFixed;
import freaktemplate.utils.MarkerAnimation;
import freaktemplate.utils.v2GetRouteDirection;

public class Gmap extends FragmentActivity implements OnMapReadyCallback /*implements OnMapReadyCallback*/ {
	private GoogleMap googleMap;
	private HashMap<CustomMarker, Marker> markersHashMap;
	private Iterator<Entry<CustomMarker, Marker>> iter;
	private CameraUpdate cu;
    private Button btn_map;
    private Button btn_detail;
	private Button btn_circle;
    private Button btn_zoom;
    private Button btn_arrow;
    private Button btn_current;
	private String lat;
    private String lng;
    private String map;
    private String nm;
    private String ad;
    private String id;
    private String rate;
    private String latitude;
    private String longitude;
	private int locationCount = 0;
	private String key;
	private String Error;
	private Button btn_anim;
	private boolean anim = true;
	private String name;
    private String address;
    private String idf;
    private String ratting;
	private SharedPreferences sharedPreferences;
	PopupWindow popup;
	private ArrayList<Storegetset> rest1;
	ProgressDialog progressDialog;
	private String[] separateddata;
	private View layout12;
	private Boolean isInternetPresent = false;
	private ConnectionDetector cd;
	private GPSTracker gps;
	private double latitudecur;
	private double longitudecur;
	private v2GetRouteDirection v2GetRouteDirection;
	private Document document;
	private LatLng fromPosition;
	private LatLng toPosition;
	private double destlat;
    private double destlng;
	private MarkerOptions markerOptions;
	private Marker newMark;

    // PolylineOptions polylineOptions;
	public static final String MY_PREFS_NAME = "Store";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gmap);


		v2GetRouteDirection = new v2GetRouteDirection();
		cd = new ConnectionDetector(getApplicationContext());

		isInternetPresent = cd.isConnectingToInternet();
		// check for Internet status
		if (isInternetPresent) {

            ArrayList<LatLng> arrayPoints = new ArrayList<>();
			markersHashMap = new HashMap<>();
			gps = new GPSTracker(Gmap.this);
			// check if GPS enabled
			if (gps.canGetLocation()) {
				try {
					latitudecur = gps.getLatitude();
					longitudecur = gps.getLongitude();

				} catch (NumberFormatException e) {
					// TODO: handle exception
				}


			} else {

				gps.showSettingsAlert();
			}
			getintent();

			initialize();

			try {
				rest1 = Home.rest;

				try {
					// Loading map
					initilizeMap();
				} catch (Exception e) {
					e.printStackTrace();
				}
				fromPosition = new LatLng(latitudecur, longitudecur);
				toPosition = new LatLng(destlat, destlng);
			} catch (NullPointerException e) {
				// TODO: handle exception
			} catch (NumberFormatException e) {
				// TODO: handle exception
			}

			buttonmethod();

		} else {

			RelativeLayout rl_back = findViewById(R.id.rl_back);
			if (rl_back == null) {
				RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

				try {
					layout12 = getLayoutInflater().inflate(R.layout.connectiondialog, rl_dialoguser, false);

				} catch (InflateException e) {
					// TODO: handle exception
				}

				rl_dialoguser.addView(layout12);
				rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Gmap.this, R.anim.popup));
				Button btn_yes = layout12.findViewById(R.id.btn_yes);
				btn_yes.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
					}
				});
			}


		}
	}

	private void getintent() {
		// TODO Auto-generated method stub

		try {
			Intent iv = getIntent();
			lat = iv.getStringExtra("lat");
			lng = iv.getStringExtra("lng");
			map = iv.getStringExtra("map");
			nm = iv.getStringExtra("nm");
			ad = iv.getStringExtra("ad");
			id = iv.getStringExtra("id");
			rate = iv.getStringExtra("rate");
			latitude = iv.getStringExtra("latitude");
			longitude = iv.getStringExtra("longitude");
			MapsInitializer.initialize(getApplicationContext());

		} catch (NullPointerException e) {
			// TODO: handle exception
		}


		//MapsInitializer.initialize(getApplicationContext());
		btn_map = findViewById(R.id.btn_map);
		btn_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				Gmap.this.finish();

			}
		});

	}

	private void initialize() {
		// TODO Auto-generated method stub
		// rest1 = Home.rest;

		rest1 = new ArrayList<>();

		btn_detail = findViewById(R.id.btn_detail);
		btn_detail.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.googleMap = googleMap;
		try {

			initializeUiSettings();
			initializeMapLocationSettings();
			initializeMapTraffic();
			initializeMapType();
			initializeMapViewSettings();
			setCustomMarkerOnePosition();
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}



	// class call for latitude longitude and id of particular restaurant
    class getstoredetail extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			getdetailforNearMe();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			setCustomMarkerOnePosition();

		}

	}

	private void getdetailforNearMe() {
		// TODO Auto-generated method stub

		URL hp = null;
		try {
			rest1.clear();

			hp = new URL(getString(R.string.link) + "rest/nearbystore.php?lat=" + latitudecur + "&&long=" + longitudecur
					+ "&from=0&to=10");
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
			JSONArray j = jObject.getJSONArray("Stores");
			for (int i = 0; i < j.length(); i++) {
				JSONObject Obj;
				Obj = j.getJSONObject(i);
				Storegetset temp = new Storegetset();
				temp.setStore_id(Obj.getString("store_id"));
				temp.setName(Obj.getString("name"));
				temp.setAddress(Obj.getString("address"));
				temp.setDistance(Obj.getString("distance"));
				temp.setLat(Obj.getString("latitude"));
				temp.setLongi(Obj.getString("longitude"));
				temp.setFeatured(Obj.getString("featured"));
				temp.setRatting(Obj.getString("ratting"));
				temp.setThumbnail(Obj.getString("thumbnail"));
				String lat12 = Obj.getString("latitude");
				String lng12 = Obj.getString("longitude");

				rest1.add(temp);

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

	private void initilizeMap() {

		SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment));
		supportMapFragment.getMapAsync(this);

		try {

			locationCount = sharedPreferences.getInt("locationCount", 0);
		} catch (NumberFormatException e) {
			// TODO: handle exception
		}

		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		}

		(findViewById(R.id.mapFragment)).getViewTreeObserver()
				.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						if (android.os.Build.VERSION.SDK_INT >= 16) {
							(findViewById(R.id.mapFragment)).getViewTreeObserver().removeOnGlobalLayoutListener(this);
						} else {
							(findViewById(R.id.mapFragment)).getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}

					}
				});
	}

	// set position in google map from latitude and longitude
    private void setCustomMarkerOnePosition() {

        CustomMarker customMarkerOne;
        if (map.equals("yes")) {
			for (int i = 0; i < rest1.size(); i++) {

				try {
					String lat1 = rest1.get(i).getLat();
					String lng1 = rest1.get(i).getLongi();
					name = rest1.get(i).getName() + ":" + rest1.get(i).getStore_id() + ":" + rest1.get(i).getRatting();
					address = rest1.get(i).getAddress();
					idf = rest1.get(i).getStore_id();
					ratting = rest1.get(i).getRatting();
					customMarkerOne = new CustomMarker("markerOne", Double.parseDouble(lat1), Double.parseDouble(lng1));

					// addMarker(customMarkerOne);
					markerOptions = new MarkerOptions().position(
							new LatLng(customMarkerOne.getCustomMarkerLatitude(), customMarkerOne.getCustomMarkerLongitude()))
							.icon(BitmapDescriptorFactory.defaultMarker())
							.snippet(rest1.get(i).getName() + "\n" + rest1.get(i).getAddress())
							.title(rest1.get(i).getName() + ":" + rest1.get(i).getStore_id() + ":"
									+ rest1.get(i).getRatting() + ":" + rest1.get(i).getAddress() + ":"
									+ rest1.get(i).getLat() + ":" + rest1.get(i).getLongi());

					final Marker newMark = googleMap.addMarker(markerOptions);
					addMarkerToHashMap(customMarkerOne, newMark);
					zoomToMarkers(btn_detail);
				} catch (NullPointerException e) {
					// TODO: handle exception
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}

				googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

					@Override
					public View getInfoWindow(Marker arg0) {
						return null;
					}

					@Override
					public View getInfoContents(Marker marker) {

						LinearLayout info = new LinearLayout(Gmap.this);
						info.setOrientation(LinearLayout.VERTICAL);

						try {
							TextView snippet = new TextView(Gmap.this);
							snippet.setTextColor(Color.BLACK);
							snippet.setText(marker.getSnippet());
							snippet.setTypeface(null, Typeface.BOLD);
							snippet.setGravity(Gravity.CENTER);

							// info.addView(title);
							info.addView(snippet);
						} catch (NullPointerException e) {
							// TODO: handle exception
						}


						return info;
					}
				});

				// on click of google map marker
				googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick(Marker marker) {

						try {
							String title = marker.getTitle();
							separateddata = title.split(":");
							String addressmap = separateddata[3].toLowerCase();
							destlat = Double.parseDouble(separateddata[4]);
							destlng = Double.parseDouble(separateddata[5]);
							Intent intent = new Intent(Gmap.this, Detailpage.class);
							intent.putExtra("id", "" + separateddata[1]);
							intent.putExtra("name", "" + separateddata[0]);
							intent.putExtra("rating", "" + separateddata[2]);
							startActivity(intent);
						} catch (NullPointerException e) {
							// TODO: handle exception
						} catch (NumberFormatException e) {
							// TODO: handle exception
						}

					}
				});
				googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker arg0) {
						// TODO Auto-generated method stub

						try {
							String title = arg0.getTitle();
							separateddata = title.split(":");
							destlat = Double.parseDouble(separateddata[4]);
							destlng = Double.parseDouble(separateddata[5]);
							arg0.showInfoWindow();
						} catch (NullPointerException e) {
							// TODO: handle exception
						} catch (NumberFormatException e) {
							// TODO: handle exception
						}

						return true;
					}
				});


				// on click of particular restaurant full detail
				btn_detail.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Intent intent = new Intent(Gmap.this, Detailpage.class);
						intent.putExtra("id", "" + separateddata[1]);
						intent.putExtra("name", "" + separateddata[0]);
						intent.putExtra("rating", "" + separateddata[2]);
						startActivity(intent);
					}
				});
			}

		} else {
			customMarkerOne = new CustomMarker("markerOne", Double.parseDouble(lat), Double.parseDouble(lng));

			addMarker(customMarkerOne);

		}
	}

	public void showDirections(View view) {
		final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
				"http://maps.google.com/maps?" + "saddr=" + latitude + "," + longitude + "&daddr=" + lat + "," + lng));
		intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(intent);
	}


	private void zoomToMarkers(View v) {
		zoomAnimateLevelToFitMarkers(120);
	}


	private void initializeUiSettings() {
		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(false);
		googleMap.getUiSettings().setTiltGesturesEnabled(true);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
	}

	private void initializeMapLocationSettings() {
		if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		googleMap.setMyLocationEnabled(true);
	}

	private void initializeMapTraffic() {
		googleMap.setTrafficEnabled(true);
	}

	private void initializeMapType() {
		googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
	}

	private void initializeMapViewSettings() {
		googleMap.setIndoorEnabled(true);
		googleMap.setBuildingsEnabled(false);
	}


	private void setUpMarkersHashMap() {
		if (markersHashMap == null) {
			markersHashMap = new HashMap<>();
		}
	}


	private void addMarkerToHashMap(CustomMarker customMarker, Marker marker) {
		setUpMarkersHashMap();
		markersHashMap.put(customMarker, marker);
	}

	// this is method to help us find a Marker that is stored into the hashmap
    private Marker findMarker(CustomMarker customMarker) {
		iter = markersHashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry mEntry = iter.next();
			CustomMarker key = (CustomMarker) mEntry.getKey();
			if (customMarker.getCustomMarkerId().equals(key.getCustomMarkerId())) {
				Marker value = (Marker) mEntry.getValue();
				return value;
			}
		}
		return null;
	}

	// this is method to help us add a Marker to the map
    private void addMarker(CustomMarker customMarker) {

		for (int i = 0; i < rest1.size(); i++) {
			Storegetset tempobj = rest1.get(i);
			MarkerOptions markerOption = new MarkerOptions().position(
			new LatLng(customMarker.getCustomMarkerLatitude(), customMarker.getCustomMarkerLongitude()))
					.icon(BitmapDescriptorFactory.defaultMarker()).title(tempobj.getName());
			Marker newMark = googleMap.addMarker(markerOption);
			addMarkerToHashMap(customMarker, newMark);
		}

	}

	// this is method to help us remove a Marker
	public void removeMarker(CustomMarker customMarker) {
		if (markersHashMap != null) {
			if (findMarker(customMarker) != null) {
				findMarker(customMarker).remove();
				markersHashMap.remove(customMarker);
			}
		}
	}

	// this is method to help us fit the Markers into specific bounds for camera
	// position
    private void zoomAnimateLevelToFitMarkers(int padding) {
		iter = markersHashMap.entrySet().iterator();
		LatLngBounds.Builder b = new LatLngBounds.Builder();

		LatLng ll = null;
		while (iter.hasNext()) {
			Entry mEntry = iter.next();
			CustomMarker key = (CustomMarker) mEntry.getKey();
			ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());
			b.include(ll);
		}
		LatLngBounds bounds = b.build();
		// Change the padding as per needed
		cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 400, 12);
		googleMap.animateCamera(cu);
		googleMap.moveCamera(cu);
	}

	// this is method to help us move a Marker.
	public void moveMarker(CustomMarker customMarker, LatLng latlng) {
		if (findMarker(customMarker) != null) {
			findMarker(customMarker).setPosition(latlng);
			customMarker.setCustomMarkerLatitude(latlng.latitude);
			customMarker.setCustomMarkerLongitude(latlng.longitude);
		}
	}

	// this is method to animate the Marker. There are flavours for all Android
	// versions
	public void animateMarker(CustomMarker customMarker, LatLng latlng) {
		if (findMarker(customMarker) != null) {

			LatLngInterpolator latlonInter = new LinearFixed();
			latlonInter.interpolate(20,
					new LatLng(customMarker.getCustomMarkerLatitude(), customMarker.getCustomMarkerLongitude()),
					latlng);

			customMarker.setCustomMarkerLatitude(latlng.latitude);
			customMarker.setCustomMarkerLongitude(latlng.longitude);

			if (android.os.Build.VERSION.SDK_INT >= 14) {
				MarkerAnimation.animateMarkerToICS(findMarker(customMarker),
						new LatLng(customMarker.getCustomMarkerLatitude(), customMarker.getCustomMarkerLongitude()),
						latlonInter);
			} else if (android.os.Build.VERSION.SDK_INT >= 11) {
				MarkerAnimation.animateMarkerToHC(findMarker(customMarker),
						new LatLng(customMarker.getCustomMarkerLatitude(), customMarker.getCustomMarkerLongitude()),
						latlonInter);
			} else {
				MarkerAnimation.animateMarkerToGB(findMarker(customMarker),
						new LatLng(customMarker.getCustomMarkerLatitude(), customMarker.getCustomMarkerLongitude()),
						latlonInter);
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

	}

	private void buttonmethod() {
		// TODO Auto-generated method stub

		btn_arrow = findViewById(R.id.btn_arrow);
		btn_arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				try {
					key = "route";
					fromPosition = new LatLng(latitudecur, longitudecur);
					toPosition = new LatLng(destlat, destlng);
				} catch (NullPointerException e) {
					// TODO: handle exception
				} catch (NumberFormatException e) {
					// TODO: handle exception
				}
			
				if (String.valueOf(destlat).equals("0.0")) {
					Toast.makeText(Gmap.this, "select your destination point marker to find your route",
							Toast.LENGTH_LONG).show();
				} else {

					GetRouteTask getRoute = new GetRouteTask();
					getRoute.execute();
				}

			}
		});

		btn_current = findViewById(R.id.btn_current);
		btn_current.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LatLng latLng = new LatLng(latitudecur, longitudecur);
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
			}
		});

		btn_circle = findViewById(R.id.btn_circle);
		btn_circle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				key = "remove";
				GetRouteTask getRoute = new GetRouteTask();
				getRoute.execute();

			}
		});

		btn_zoom = findViewById(R.id.btn_zoom);
		btn_zoom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new getstoredetail().execute();
			}
		});

		btn_arrow.setVisibility(View.INVISIBLE);
		btn_circle.setVisibility(View.INVISIBLE);
		btn_current.setVisibility(View.INVISIBLE);
		btn_zoom.setVisibility(View.INVISIBLE);
		btn_anim = findViewById(R.id.btn_anim);

		btn_anim.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (anim) {
					Animation shake = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_down);
					Animation shake1 = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_down1);
					Animation shake2 = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_sown2);
					Animation shake3 = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_down3);
					btn_zoom.setVisibility(View.VISIBLE);
					btn_zoom.startAnimation(shake);
					btn_arrow.setVisibility(View.VISIBLE);
					btn_arrow.startAnimation(shake1);
					btn_current.setVisibility(View.VISIBLE);
					btn_current.startAnimation(shake2);
					btn_circle.setVisibility(View.VISIBLE);
					btn_circle.startAnimation(shake3);
					anim = false;
				} else {
					Animation shake = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_up);
					Animation shake1 = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_up1);
					Animation shake2 = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_up2);
					Animation shake3 = AnimationUtils.loadAnimation(Gmap.this, R.anim.slide_up3);

					btn_arrow.startAnimation(shake1);
					btn_circle.startAnimation(shake3);
					btn_current.startAnimation(shake2);
					btn_zoom.startAnimation(shake);
					btn_arrow.setVisibility(View.INVISIBLE);
					btn_circle.setVisibility(View.INVISIBLE);
					btn_current.setVisibility(View.INVISIBLE);
					btn_zoom.setVisibility(View.INVISIBLE);
					anim = true;
				}

			}
		});
	}

	private class GetRouteTask extends AsyncTask<String, Void, String> {

		private ProgressDialog Dialog;
		String response = "";

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(Gmap.this);
			Dialog.setMessage("Loading");
			Dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			// Get All Route values
			document = v2GetRouteDirection.getDocument(fromPosition, toPosition,
					freaktemplate.utils.v2GetRouteDirection.MODE_DRIVING);
			response = "Success";
			return response;

		}

		@Override
		protected void onPostExecute(String result) {
			googleMap.clear();
			if (response.equalsIgnoreCase("Success")) {
				ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
				PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.RED);

				for (int i = 0; i < directionPoint.size(); i++) {
					rectLine.add(directionPoint.get(i));
				}
				// Adding route on the map
                CustomMarker customMarkerdest;
                CustomMarker customMarkersrc;
                if (key.equals("route")) {
					googleMap.addPolyline(rectLine);
					markerOptions.position(toPosition);
					markerOptions.draggable(true);
					// changes
					customMarkersrc = new CustomMarker("markerOne", latitudecur, longitudecur);
					customMarkerdest = new CustomMarker("markerOne", destlat, destlng);
					newMark = googleMap.addMarker(markerOptions);
					// mGoogleMap.addMarker(markerOptions);

					addMarkerToHashMap(customMarkersrc, newMark);
					addMarkerToHashMap(customMarkerdest, newMark);
					zoomToMarkers1(btn_detail);
				} else if (key.equals("remove")) {
					markerOptions.position(toPosition);
					markerOptions.draggable(true);
					// changes
					customMarkersrc = new CustomMarker("markerOne", latitudecur, longitudecur);
					customMarkerdest = new CustomMarker("markerOne", destlat, destlng);
					newMark = googleMap.addMarker(markerOptions);
					addMarkerToHashMap(customMarkersrc, newMark);
					addMarkerToHashMap(customMarkerdest, newMark);
					zoomToMarkers1(btn_detail);
				}
				
			}

			Dialog.dismiss();
		}
	}

	private void zoomToMarkers1(View v) {
		zoomAnimateLevelToFitMarkers1(120);
	}

	private void zoomAnimateLevelToFitMarkers1(int padding) {
		iter = markersHashMap.entrySet().iterator();
		LatLngBounds.Builder b = new LatLngBounds.Builder();

		LatLng ll = null;
		while (iter.hasNext()) {
			Entry mEntry = iter.next();
			CustomMarker key = (CustomMarker) mEntry.getKey();
			
			ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());
			b.include(ll);
		}
		try {
			LatLngBounds bounds = b.build();
			// Change the padding as per needed
			cu = CameraUpdateFactory.newLatLngBounds(bounds, 25);
			googleMap.animateCamera(cu);

			
		} catch (IllegalStateException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
