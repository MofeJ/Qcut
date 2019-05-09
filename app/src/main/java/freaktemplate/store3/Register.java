package freaktemplate.store3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import freaktemplate.getset.Logingetset;

public class Register extends Activity {
    private EditText edt_fullname;
    private EditText edt_mailid;
    private EditText edt_username;
    private EditText edt_password;
    private Button btn_register;
    private ImageView img_profile;
    private String emailpattern;
    private String fullname;
    private String mail;
    private String username;
    private String password;
    private String loginimage;
    private String Error;
    private String user2;
    private String user_name;
    private String full_name;
    private String email_id;
    private String imageprofile;
    private String key;
    private String picturepath;
    String file;
    private String encodedString;
    private String Full_name;
    private String User_name;
    private String emailid;
    private String responseStr;
    private String prodel;
    private ArrayList<Logingetset> login;
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String MY_PREFS_NAME = "Store";
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ProgressDialog progressDialog;
    private Typeface tf1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //get preference data

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (prefs.getString("fullname", null) != null) {
            Full_name = prefs.getString("fullname", null);
        } else {
            Full_name = "";
        }

        if (prefs.getString("delete", null) != null) {
            prodel = prefs.getString("delete", null);
        } else {
            prodel = "";
        }

        if (prefs.getString("emilid", null) != null) {
            email_id = prefs.getString("emilid", null);
        } else {
            email_id = "";
        }

        if (prefs.getString("username", null) != null) {
            User_name = prefs.getString("username", null);
        } else {
            User_name = "";
        }

        tf1 = Typeface.createFromAsset(Register.this.getAssets(), "fonts/Roboto-Light.ttf");
        TextView textview1 = findViewById(R.id.txt_header);
        textview1.setTypeface(tf1);

        login = new ArrayList<>();

        // img_profile.setImageResource(R.drawable.default_circle_img);

        edt_fullname = findViewById(R.id.edt_fullname);

        edt_mailid = findViewById(R.id.edt_mailid);

        edt_username = findViewById(R.id.edt_username);

        edt_password = findViewById(R.id.edt_password);
        img_profile = findViewById(R.id.img_profile);

        //check profile is delete or not


        try {
            if (prodel.equals("new")) {
                edt_fullname.setText("");
                edt_mailid.setText("");
                edt_password.setText("");
                edt_username.setText("");
                img_profile.setImageResource(R.drawable.default_circle_img);
            } else {
                edt_fullname.setText("" + Full_name);
                edt_mailid.setText("" + email_id);
                edt_username.setText("" + User_name);

                if (prefs.getString("picturepath", null) != null) {
                    picturepath = prefs.getString("picturepath", null);
                }
                if (picturepath != null) {
                    img_profile.setImageBitmap(decodeFile(picturepath));

                } else {
                    if (prefs.getString("picture", null) != null) {
                        loginimage = prefs.getString("picture", null);
                        new DownloadImageTask(img_profile)
                                .execute(loginimage);
                    }
                }
            }
        } catch (NullPointerException e) {
            // TODO: handle exception
        }


        //select image from gallery

        img_profile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                verifyStoragePermissions(Register.this);

                // TODO Auto-generated method stub
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        //register button click

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                fullname = edt_fullname.getText().toString();
                mail = edt_mailid.getText().toString();
                username = edt_username.getText().toString();
                password = edt_password.getText().toString();

                if (mail.matches(emailpattern)) {
                    if (fullname.equals("")) {
                        edt_fullname.setError("Enter Fullname");
                    } else {
                        if (username.equals("")) {
                            edt_username.setError("Enter Username");
                        } else {
                            if (password.equals("")) {
                                edt_password.setError("Enter Password");
                            } else {

                                new PostDataAsyncTask().execute();

                            }
                        }
                    }
                } else {
                    edt_mailid.setError("Enter Valid Email Address");
                }
            }

        });
    }

    private void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //after selecting image from gallery image path receive in this method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaColumns.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturepath = cursor.getString(columnIndex);
            cursor.close();
            img_profile.setImageBitmap(decodeFile(picturepath));

        }
        if (requestCode == REQUEST_EXTERNAL_STORAGE && resultCode == RESULT_OK) {
            img_profile.setImageBitmap(decodeFile(picturepath));
        }

    }


    private Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

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


    class getlogin extends AsyncTask<Void, Void, Void> {
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
                        // JSONArray jarr = Obj.getJSONArray("images");
                        Logingetset temp = new Logingetset();


                        temp.setId(Obj.getString("id"));


                        login.add(temp);

                    }
                } else if (currentKey.equals("User Info")) {
                    key = "user";
                    JSONArray j = jObject.getJSONArray("User Info");
                    for (int i = 0; i < j.length(); i++) {

                        JSONObject Obj;
                        Obj = j.getJSONObject(i);
                        Logingetset temp = new Logingetset();
                        temp.setUser_id(Obj.getString("user_id"));
                        temp.setName(Obj.getString("name"));
                        temp.setUsername(Obj.getString("username"));
                        temp.setEmail(Obj.getString("email"));
                        temp.setFullimage(Obj.getString("fullimage"));
                        user2 = Obj.getString("user_id");
                        user_name = Obj.getString("username");
                        full_name = Obj.getString("name");
                        emailid = Obj.getString("email");
                        imageprofile = Obj.getString("fullimage");
                        login.add(temp);

                    }

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
            if (key == null) {
                Toast.makeText(Register.this, "Something went wrong!Please retry again!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (key.equals("user")) {

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("score", "" + user2);
                editor.putString("username", "" + user_name);
                editor.putString("fullname", "" + full_name);
                editor.putString("emilid", "" + emailid);
                editor.putString("picture", "" + imageprofile);

                editor.apply();
                Intent iv = new Intent(Register.this, Home.class);
                startActivity(iv);
                Toast.makeText(Register.this, "Register Successful..", Toast.LENGTH_LONG).show();
            } else if (key.equals("status")) {
                Toast.makeText(Register.this, getString(R.string.error_duplicate_email), Toast.LENGTH_LONG).show();
            }

        }
    }

    class PostDataAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // do stuff before posting data
            progressDialog = new ProgressDialog(Register.this);
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                postdata();
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
                new getlogin().execute();
            }

        }
    }

    // this will post our text data
    private void postText() {
        try {

            String postReceiverUrl = getString(R.string.link) + "/rest/user_register.php";
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(postReceiverUrl);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("email", mail));
            nameValuePairs.add(new BasicNameValuePair("password", password));
            nameValuePairs.add(new BasicNameValuePair("name", fullname));
            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("file", encodedString));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {

                String responseStr = EntityUtils.toString(resEntity).trim();
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postdata() {
        // TODO Auto-generated method stub
        HttpClient httpClient = new DefaultHttpClient();
        HttpEntity entity;
        //try {
        if (picturepath != null)

        {

            entity = MultipartEntityBuilder.create().addTextBody("email", "" + mail, ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("name", "" + fullname, ContentType.create("text/plain", MIME.UTF8_CHARSET))
                    .addTextBody("password", "" + password
                            , ContentType.create("text/plain", MIME.UTF8_CHARSET)).addBinaryBody("file", new File(picturepath),
                            ContentType.create("application/octet-stream"), "filename")
                    .addTextBody("username", "" + username, ContentType.create("text/plain", MIME.UTF8_CHARSET)).build();
        } else {
            entity = MultipartEntityBuilder.create().addTextBody("email", mail, ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("name", fullname, ContentType.create("text/plain", MIME.UTF8_CHARSET))
                    .addTextBody("password", password, ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("username", username, ContentType.create("text/plain", MIME.UTF8_CHARSET)).build();
        }


        HttpPost httpPost = new HttpPost(getString(R.string.link) + "/rest/user_register.php");

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
        HttpEntity result = response.getEntity();
        if (result != null) {

            // String responseStr = "";
            try {
                responseStr = EntityUtils.toString(result).trim();
                //responseStr= URLEncoder.encode(responseStr,"UTF-8");
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
