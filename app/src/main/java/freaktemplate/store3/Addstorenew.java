package freaktemplate.store3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import freaktemplate.getset.Logingetset;
import freaktemplate.getset.catgetset;

public class Addstorenew extends FragmentActivity implements OnMapReadyCallback {
    private static final String MY_PREFS_NAME = "Store";
    private final static int RESULT_CODE = 999;
    private final static int FILECHOOSER_RESULTCODE = 1;
    View v;
    private LinearLayout ll_step1;
    private LinearLayout ll_step2;
    private LinearLayout ll_step3;
    private RelativeLayout rel_step1;
    private RelativeLayout rel_step2;
    private RelativeLayout rel_step3;
    private double latitude;
    private double longitude;
    private EditText edt_lat;
    private EditText edt_long;
    private EditText edt_search;
    private String search;
    private String Error;
    private EditText edt_name;
    private EditText edt_add;
    private EditText edt_email;
    private EditText edt_phone;
    private EditText edt_sms;
    private EditText edt_website;
    private EditText edt_desc;
    private String user2;
    private String key;
    private String user3;
    private String spid;
    private String[] imagesPath;
    private ArrayList<Logingetset> login;
    private String responseStr;
    private String[] country_array;
    private ProgressDialog progressDialog;
    private ArrayList<catgetset> cat;
    private GoogleMap googleMap;
    private TextView txt_step1;
    private TextView txt_step2;
    private TextView txt_step3;
    private ArrayList<String> imagesPathList;
    private String FilePath;
    private String name;
    private String address;
    private String email;
    private String phoneno;
    private String sms;
    private String website;
    private String description;
    private String latitudedata;
    private String longitudedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstorenew);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        // check user is created or not
        if (prefs.getString("score", null) != null) {
            String uservalue = prefs.getString("score", null);
            createmethod();

        } else {


            RelativeLayout rl_back = findViewById(R.id.rl_back);
            if (rl_back == null) {
                final RelativeLayout rl_dialoguser = findViewById(R.id.rl_infodialog);

                View layout12 = getLayoutInflater().inflate(R.layout.json_dilaog, rl_dialoguser, false);

                rl_dialoguser.addView(layout12);
                rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(Addstorenew.this, R.anim.popup));

                TextView txt_dia = layout12.findViewById(R.id.txt_dia);
                txt_dia.setText(getString(R.string.error_required_registrationstore));

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
        }

    }

    private void createmethod() {
        // TODO Auto-generated method stub
        initialise();

        rel_step1.setVisibility(View.VISIBLE);
        rel_step2.setVisibility(View.INVISIBLE);
        rel_step3.setVisibility(View.INVISIBLE);
        ll_step1.setBackgroundColor(Color.parseColor("#ffffff"));
        ll_step2.setBackgroundColor(Color.parseColor("#000000"));
        ll_step3.setBackgroundColor(Color.parseColor("#000000"));

        txt_step1.setTextColor(Color.parseColor("#000000"));
        txt_step2.setTextColor(Color.parseColor("#ffffff"));
        txt_step3.setTextColor(Color.parseColor("#ffffff"));

        SupportMapFragment sup = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment));
        sup.getMapAsync(this);

        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                search = arg0.toString().replace(" ", "%20");
                // if(location!=null && !location.equals("")){
                new GeocoderTask().execute(search);
                // }
            }
        });
        googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                latitude = arg0.latitude;
                longitude = arg0.longitude;
                edt_lat.setText("" + latitude);
                edt_long.setText("" + longitude);
            }
        });
        step3();
        step2();
        Button btn_next_step1 = findViewById(R.id.btn_next1);
        btn_next_step1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                name = edt_name.getText().toString();
                address = edt_add.getText().toString();
                email = edt_email.getText().toString();
                phoneno = edt_phone.getText().toString();
                sms = edt_sms.getText().toString();
                website = edt_website.getText().toString();
                if (name.equals("")) {
                    edt_name.setError("Enter Name");
                } else if (address.equals("")) {
                    edt_add.setError("Enter Address");
                } else if (email.equals("")) {
                    edt_email.setError("Enter Email Id");
                } else if (phoneno.equals("")) {
                    edt_phone.setError("Enter Phone No");
                } else if (sms.equals("")) {
                    edt_sms.setError("Enter Sms No");
                } else if (website.equals("")) {
                    edt_website.setError("Enter Website");
                } else {
                    key = "step1";
                    new PostDataAsyncTask().execute();
                }
            }
        });

        Button btn_next_step2 = findViewById(R.id.btn_next2);
        btn_next_step2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                description = edt_desc.getText().toString();
                latitudedata = edt_lat.getText().toString();
                longitudedata = edt_long.getText().toString();

                if (description.equals("")) {
                    edt_desc.setError("Enter Description");
                } else if (latitudedata.equals("")) {
                    edt_lat.setError("Enter Latitude");
                } else if (longitudedata.equals("")) {
                    edt_long.setError("Enter Longitude");
                } else {
                    key = "step2";
                    new PostDataAsyncTask().execute();
                }
            }
        });

        Button btn_next3 = findViewById(R.id.btn_next3);
        btn_next3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                key = "step3";
                new PostDataAsyncTask1().execute();
            }
        });
    }

    private void initialise() {
        // TODO Auto-generated method stub

        login = new ArrayList<>();
        edt_desc = findViewById(R.id.edt_description);
        edt_search = findViewById(R.id.edt_search);
        edt_lat = findViewById(R.id.edt_latitude);
        edt_long = findViewById(R.id.edt_longitude);
        ll_step1 = findViewById(R.id.ll_step1);
        ll_step2 = findViewById(R.id.ll_step2);
        ll_step3 = findViewById(R.id.ll_step3);
        txt_step1 = findViewById(R.id.txt_step1);
        txt_step2 = findViewById(R.id.txt_step2);
        txt_step3 = findViewById(R.id.txt_step3);
        rel_step1 = findViewById(R.id.rel_step1);
        rel_step2 = findViewById(R.id.rel_step2);
        rel_step3 = findViewById(R.id.rel_step3);
        edt_name = findViewById(R.id.edt_name);
        edt_add = findViewById(R.id.edt_address);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_sms = findViewById(R.id.edt_sms);
        edt_website = findViewById(R.id.edt_website);
    }

    private void step2() {
        // TODO Auto-generated method stub
        cat = new ArrayList<>();
        new getcategorydetail().execute();
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;
        // Enabling MyLocation Layer of Google Map
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
        Map.setMyLocationEnabled(true);

    }

    // getting data from category json url
    private void getdetailforNearMe() {
        // TODO Auto-generated method stub

        URL hp;
        try {

            hp = new URL("http://192.168.1.109/store/rest/storecategory.php");


            URLConnection hpCon = hp.openConnection();
            hpCon.connect();
            InputStream input = hpCon.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(input));

            String x;
            x = r.readLine();
            String total = "";

            while (x != null) {
                total += x;
                x = r.readLine();
            }

            JSONObject jObject = new JSONObject(total);
            JSONArray j = jObject.getJSONArray("Category");
            country_array = new String[j.length()];
            for (int i = 0; i < j.length(); i++) {

                JSONObject Obj;
                Obj = j.getJSONObject(i);

                catgetset temp = new catgetset();
                temp.setId(Obj.getString("id"));
                temp.setCategory_name(Obj.getString("category_name"));
                country_array[i] = Obj.getString("category_name");
                cat.add(temp);
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

    private void step3() {
        // TODO Auto-generated method stub
        Button btn_thumbnail = findViewById(R.id.btn_thumbnail);
        Button btn_multiple = findViewById(R.id.btn_multiple);

        btn_thumbnail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, FILECHOOSER_RESULTCODE);

            }
        });

        btn_multiple.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Addstorenew.this, CustomPhotoGalleryActivity.class);
                startActivityForResult(intent, RESULT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case FILECHOOSER_RESULTCODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaColumns.DATA};

                    assert selectedImage != null;
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    assert cursor != null;
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    FilePath = cursor.getString(columnIndex);
                    // FilePath = intent.getData().getPath();

                    String FileName = intent.getData().getLastPathSegment();
                    int lastPos = FilePath.length() - FileName.length();
                    String Folder = FilePath.substring(0, lastPos);
                    File f = new File(FilePath);
                    String imageName = f.getName();
                    TextView txt_thumbname = findViewById(R.id.txt_thumbname);
                    txt_thumbname.setText("" + imageName);
                    cursor.close();

                }
                break;

            case RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    imagesPathList = new ArrayList<>();
                    // String[] imagesPath =
                    // intent.getStringExtra("data").split("\\|");
                    imagesPath = intent.getStringExtra("data").split("\\|");
                    new getpost().execute();

                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addstorenew, menu);
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

    private void postdata() {
        // TODO Auto-generated method stub
        HttpClient httpClient = new DefaultHttpClient();
        HttpEntity entity;

        entity = MultipartEntityBuilder.create().addTextBody("sname", name).addTextBody("address", address)
                .addTextBody("email", email).addTextBody("phone", phoneno).addTextBody("sms", sms)
                .addTextBody("url", website).build();

        HttpPost httpPost = new HttpPost("http://192.168.1.109/store/rest/addstoredetail.php");
        httpPost.setEntity(entity);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert response != null;
        HttpEntity result = response.getEntity();
        if (result != null) {


            try {
                responseStr = EntityUtils.toString(result).trim();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void postdata1() {
        // TODO Auto-generated method stub
        HttpClient httpClient = new DefaultHttpClient();
        HttpEntity entity;

        entity = MultipartEntityBuilder.create().addTextBody("id", user2).addTextBody("category", spid)
                .addTextBody("desc", description).addTextBody("lat", latitudedata).addTextBody("long", longitudedata)
                .build();

        HttpPost httpPost = new HttpPost("http://192.168.1.109/store/rest/addstoredetail.php");
        httpPost.setEntity(entity);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert response != null;
        HttpEntity result = response.getEntity();
        if (result != null) {


            try {
                responseStr = EntityUtils.toString(result).trim();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // you can add an if statement here and do other actions based
            // on the response
        }
    }

    @SuppressWarnings("deprecation")
    private void postdata2() {
        // TODO Auto-generated method stub
        HttpClient httpClient = new DefaultHttpClient();
        HttpEntity entity;

        for (String element : imagesPathList) {
            File bin = new File(element);
            FileBody bin1 = new FileBody(bin);
            entity = MultipartEntityBuilder.create().addTextBody("id", user2).addBinaryBody("file", new File(FilePath),
                    ContentType.create("application/octet-stream"), "filename")

                    .build();

            HttpPost httpPost = new HttpPost("http://192.168.1.109/store/rest/addstoredetail.php");
            httpPost.setEntity(entity);
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpPost);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            assert response != null;
            HttpEntity result = response.getEntity();
            if (result != null) {


                try {
                    responseStr = EntityUtils.toString(result).trim();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    class getcategorydetail extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Addstorenew.this);
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
                Toast.makeText(Addstorenew.this, "Try again Later", Toast.LENGTH_LONG).show();
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Spinner sp_cat = findViewById(R.id.category);
                ArrayAdapter<String> category = new ArrayAdapter<>(Addstorenew.this,
                        android.R.layout.simple_spinner_dropdown_item, country_array);
                sp_cat.setAdapter(category);
                sp_cat.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        spid = cat.get(arg2).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        }
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            googleMap.clear();

            // Adding Markers on Google Map for each matching address
            assert addresses != null;
            for (int i = 0; i < addresses.size(); i++) {

                Address address = addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
                edt_lat.setText("" + address.getLatitude());
                edt_long.setText("" + address.getLongitude());
                googleMap.addMarker(markerOptions);
                if (i == 0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
    }

    class PostDataAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
            progressDialog = new ProgressDialog(Addstorenew.this);
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (key.equals("step1")) {
                    postdata();
                } else if (key.equals("step2")) {
                    postdata1();
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                new getId().execute();
            }
        }
    }

    class getId extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            // TODO Auto-generated method stub

            URL hp = null;
            try {
                login.clear();

                JSONObject jObject = new JSONObject(responseStr);
                String currentKey = "";

                JSONArray j = jObject.getJSONArray("AddStore");
                // JSONArray j = new JSONArray(total);
                for (int i = 0; i < j.length(); i++) {
                    JSONObject Obj;
                    Obj = j.getJSONObject(i);
                    // JSONArray jarr = Obj.getJSONArray("images");
                    Logingetset temp = new Logingetset();
                    temp.setUser_id(Obj.getString("id"));
                    if (key.equals("step1")) {
                        user2 = Obj.getString("id");
                    } else if (key.equals("step2")) {
                        user3 = Obj.getString("id");
                    } else if (key.equals("step3")) {
                        user3 = Obj.getString("id");
                    }
                    login.add(temp);

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Error = e.getMessage();
            } catch (NullPointerException e) {
                // TODO: handle exception
                Error = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (key.equals("step1")) {
                if (user2.equals("False")) {
                    Toast.makeText(Addstorenew.this, "Something Wrong", Toast.LENGTH_LONG).show();
                } else {
                    txt_step1.setTextColor(Color.parseColor("#ffffff"));
                    txt_step2.setTextColor(Color.parseColor("#000000"));
                    txt_step3.setTextColor(Color.parseColor("#ffffff"));
                    ll_step1.setBackgroundColor(Color.parseColor("#000000"));
                    ll_step2.setBackgroundColor(Color.parseColor("#ffffff"));
                    ll_step3.setBackgroundColor(Color.parseColor("#000000"));
                    rel_step1.setVisibility(View.INVISIBLE);
                    rel_step2.setVisibility(View.VISIBLE);
                    rel_step3.setVisibility(View.INVISIBLE);
                }
            } else if (key.equals("step2")) {
                if (user3.equals("False")) {
                    Toast.makeText(Addstorenew.this, "Something Wrong", Toast.LENGTH_LONG).show();
                } else if (user3.equals("True")) {
                    txt_step1.setTextColor(Color.parseColor("#ffffff"));
                    txt_step2.setTextColor(Color.parseColor("#ffffff"));
                    txt_step3.setTextColor(Color.parseColor("#000000"));
                    ll_step1.setBackgroundColor(Color.parseColor("#000000"));
                    ll_step2.setBackgroundColor(Color.parseColor("#000000"));
                    ll_step3.setBackgroundColor(Color.parseColor("#ffffff"));
                    rel_step1.setVisibility(View.INVISIBLE);
                    rel_step2.setVisibility(View.INVISIBLE);
                    rel_step3.setVisibility(View.VISIBLE);
                }
            } else if (key.equals("step3")) {
                if (user3.equals("False")) {
                    Toast.makeText(Addstorenew.this, "Something Wrong", Toast.LENGTH_LONG).show();
                } else if (user3.equals("True")) {

                    Intent iv = new Intent(Addstorenew.this, Home.class);
                    startActivity(iv);
                    Toast.makeText(Addstorenew.this, "Store added Successfully..", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    class PostDataAsyncTask1 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
            progressDialog = new ProgressDialog(Addstorenew.this);
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                postdata2();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                new getId().execute();

            }

        }
    }

    class getpost extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                HttpClient httpClient = new DefaultHttpClient();
                HttpEntity entity;
                for (int i = 0; i < imagesPath.length; i++) {

                    String imageName = imagesPath[i];
                    // Log.d("imageName", "" + imageName);
                    // TODO Auto-generated method stub

                    imagesPathList.add(imageName);
                    String name = imagesPath[imagesPath.length - 1];
                    entity = MultipartEntityBuilder.create().addTextBody("id", user2).addBinaryBody("file1",
                            new File(imageName), ContentType.create("application/octet-stream"), "filename")

                            .build();
                    HttpPost httpPost = new HttpPost("http://192.168.1.109/store/rest/multiimage_upload.php");
                    httpPost.setEntity(entity);
                    HttpResponse response = null;
                    try {
                        response = httpClient.execute(httpPost);
                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    assert response != null;
                    HttpEntity result = response.getEntity();
                    if (result != null) {

                        // String responseStr = "";
                        try {
                            responseStr = EntityUtils.toString(result).trim();
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String lenghtOfFile) {
            // do stuff after posting data
            TextView txt_mulname = findViewById(R.id.txt_multiplename);
            txt_mulname.setText("" + imagesPathList.size() + " images selected");

        }
    }
}
