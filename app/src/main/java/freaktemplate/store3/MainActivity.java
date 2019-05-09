package freaktemplate.store3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import freaktemplate.utils.ConnectionDetector;
import freaktemplate.utils.CustomMarker;
import freaktemplate.utils.GPSTracker;
import freaktemplate.utils.MapWrapperLayout;
import freaktemplate.utils.MySupportMapFragment;
import freaktemplate.utils.v2GetRouteDirection;

public class MainActivity extends FragmentActivity {

	LocationManager locManager;
	Drawable drawable;
	private Document document;
	private v2GetRouteDirection v2GetRouteDirection;
	private LatLng fromPosition;
	private LatLng toPosition;
	private GoogleMap mGoogleMap;
	private MarkerOptions markerOptions;
	Location location;
	private String lat;
    private String lng;
    private String map;
    private String nm;
    private String ad;
    private String id;
    private String rate;
	private double destlat;
    private double destlng;
	private GPSTracker gps;
	private double latitude;
	private double longitude;
	private Button btn_detail;
	private HashMap<CustomMarker, Marker> markersHashMap;

	private Boolean isInternetPresent = false;
	private ConnectionDetector cd;
	private View layout12;
	String longitudereplace;
	private Button btn_root;
    private Button btn_circle;
    Button btn_zoom;
    private Button btn_arrow;
    private Button btn_current;
	private String key;
	Boolean Is_MAP_Moveable = false;
	SupportMapFragment supportMapFragment;
	private RelativeLayout rel_map;
	public static boolean mMapIsTouched = false;
	private MySupportMapFragment customMapFragment;
	private Projection projection;
	private ArrayList<LatLng> arrayPoints = null;
	private PolylineOptions polylineOptions;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		arrayPoints = new ArrayList<>();
		rel_map = findViewById(R.id.rel_map);
		cd = new ConnectionDetector(getApplicationContext());


		MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

		isInternetPresent = cd.isConnectingToInternet();
		// check for Internet status
		if (isInternetPresent) {
			// Internet Connection is Present
			// make HTTP requests
			// showAlertDialog(getApplicationContext(), "Internet Connection",
			// "You have internet connection", true);

			btn_detail = findViewById(R.id.btn_detail);
			btn_detail.setVisibility(View.GONE);
			markersHashMap = new HashMap<>();
			MapsInitializer.initialize(getApplicationContext());

			gps = new GPSTracker(MainActivity.this);
			// check if GPS enabled
			if (gps.canGetLocation()) {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();

				// \n is for new line

			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}
			Intent iv = getIntent();
			lat = iv.getStringExtra("lat");
			lng = iv.getStringExtra("lng");
			// longitudereplace=lng.replace("-", "");
			map = iv.getStringExtra("map");
			nm = iv.getStringExtra("nm");
			ad = iv.getStringExtra("ad");
			id = iv.getStringExtra("id");
			rate = iv.getStringExtra("rate");

			try {
				destlat = Double.parseDouble(lat);
				destlng = Double.parseDouble(lng);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			v2GetRouteDirection = new v2GetRouteDirection();
			// supportMapFragment = (SupportMapFragment)
			// getSupportFragmentManager().findFragmentById(R.id.map);
			// mGoogleMap = supportMapFragment.getMap();
			customMapFragment = ((MySupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map));
//			mGoogleMap = customMapFragment.getMap();
			// Enabling MyLocation in Google Map

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
			mGoogleMap.setMyLocationEnabled(true);
			mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
			mGoogleMap.getUiSettings().setCompassEnabled(true);
			mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
			mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
			mGoogleMap.setTrafficEnabled(true);
			mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(-5));
			markerOptions = new MarkerOptions();
			// current location
			fromPosition = new LatLng(latitude, longitude);
			// fromPosition = new LatLng(40.7127, -74.006086);
			// toPosition = new LatLng(destlat, destlng);
			toPosition = new LatLng(destlat, destlng);
			buttonmethod();
			
			// GetRouteTask getRoute = new GetRouteTask();

			// draw a route between current location and particular destination
			// getRoute.execute();

		} else {
			// Internet connection is not present
			// Ask user to connect to Internet
			RelativeLayout rl_back = findViewById(R.id.rl_back);
			if (rl_back == null) {
				RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);
				layout12 = getLayoutInflater().inflate(R.layout.connectiondialog, rl_dialoguser, false);
				rl_dialoguser.addView(layout12);
				rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.popup));
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

	private void buttonmethod() {
		// TODO Auto-generated method stub
		btn_root = findViewById(R.id.btn_root);
		btn_root.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				drawshape();
			}

			

		});

		rel_map.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				LatLng markerLocation = new LatLng(destlat, destlng);
				Point markerScreenPosition = mGoogleMap.getProjection().toScreenLocation(markerLocation);
				return false;
			}
		});

		btn_arrow = findViewById(R.id.btn_arrow);
		btn_arrow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				key = "route";
				GetRouteTask getRoute = new GetRouteTask();

				// draw a route between current location and particular
				// destination
				getRoute.execute();
			}
		});

		btn_current = findViewById(R.id.btn_current);
		btn_current.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LatLng latLng = new LatLng(latitude, longitude);
				mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
				mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
			}
		});

		btn_circle = findViewById(R.id.btn_circle);
		btn_circle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				key = "remove";
				GetRouteTask getRoute = new GetRouteTask();

				// draw a route between current location and particular
				// destination
				getRoute.execute();

			}
		});
	}

	private class GetRouteTask extends AsyncTask<String, Void, String> {

		private ProgressDialog Dialog;
		String response = "";

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(MainActivity.this);
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
			mGoogleMap.clear();
			if (response.equalsIgnoreCase("Success")) {
				ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
				PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.RED);

				for (int i = 0; i < directionPoint.size(); i++) {
					rectLine.add(directionPoint.get(i));
				}
				// Adding route on the map
				if (key.equals("route")) {
					mGoogleMap.addPolyline(rectLine);
				} else if (key.equals("remove")) {

				}
				// mGoogleMap.addPolyline(rectLine);
				
				markerOptions.position(toPosition);
				markerOptions.draggable(true);
				// changes
				CustomMarker customMarkerOne = new CustomMarker("markerOne", destlat, destlng);
				CustomMarker customMarkerTwo = new CustomMarker("markerOne", latitude, longitude);
				Marker newMark = mGoogleMap.addMarker(markerOptions);
				// mGoogleMap.addMarker(markerOptions);

				addMarkerToHashMap(customMarkerOne, newMark);
				addMarkerToHashMap(customMarkerTwo, newMark);
				zoomToMarkers(btn_detail);
				mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker arg0) {
						// TODO Auto-generated method stub
						Button btn_detail = findViewById(R.id.btn_detail);
						btn_detail.setVisibility(View.GONE);
						btn_detail.setText("" + arg0.getPosition() + "\n" + "Name: " + nm + "\n" + "Address: " + ad);
						return false;
					}
				});
			}

			Dialog.dismiss();
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

	private void zoomToMarkers(View v) {
		zoomAnimateLevelToFitMarkers(120);
	}

	private void zoomAnimateLevelToFitMarkers(int padding) {
		Iterator<Entry<CustomMarker, Marker>> iter = markersHashMap.entrySet().iterator();
		LatLngBounds.Builder b = new LatLngBounds.Builder();

		LatLng ll = null;
		while (iter.hasNext()) {
			Map.Entry mEntry = iter.next();
			CustomMarker key = (CustomMarker) mEntry.getKey();
			// ll = new LatLng(destlat,destlng);
			// ll = new LatLng(21.2049, 72.8406);
			ll = new LatLng(key.getCustomMarkerLatitude(), key.getCustomMarkerLongitude());
			b.include(ll);
		}
		try {
			LatLngBounds bounds = b.build();
			// Change the padding as per needed
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 25);
			mGoogleMap.animateCamera(cu);
		} catch (IllegalStateException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	private void drawshape() {
		// TODO Auto-generated method stub
		 customMapFragment.setOnDragListener(new MapWrapperLayout.OnDragListener() {               @Override
	            public void onDrag(MotionEvent motionEvent) {
	                float x = motionEvent.getX();
	                float y = motionEvent.getY();

	                int x_co = Integer.parseInt(String.valueOf(Math.round(x)));
	                int y_co = Integer.parseInt(String.valueOf(Math.round(y)));

	                projection = mGoogleMap.getProjection();
	                Point x_y_points = new Point(x_co, y_co);
	                LatLng latLng = mGoogleMap.getProjection().fromScreenLocation(x_y_points);
	                latitude = latLng.latitude;
	                longitude = latLng.longitude;
	                // Handle motion event:
	                	                MarkerOptions marker=new MarkerOptions();
	                marker.position(latLng); 
	               // mGoogleMap.addMarker(marker); // settin polyline in the map 
	                polylineOptions = new PolylineOptions(); 
	                polylineOptions.color(Color.BLACK); 
	                polylineOptions.width(5); 
	                arrayPoints.add(latLng); 
	                polylineOptions.addAll(arrayPoints); 
	                mGoogleMap.addPolyline(polylineOptions);
	            }
	        });
	}
}