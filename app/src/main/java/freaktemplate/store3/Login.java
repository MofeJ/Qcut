package freaktemplate.store3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import freaktemplate.getset.Logingetset;

public class Login extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener {
    private EditText edt_user;
    private EditText edt_pwd;
    private static final String TAG = "MainActivity";
    private String user2;
    private String key;
    private String id;
    private String method;
    private String username;
    private String password;
    private String value;
    private String personname;
    private String personemail;
    private String personPhotoUrl;
    private String ppic;
    private String fullname;
    private String user_name;
    private String fullimage;
    private String email_id;
    private ProgressDialog mProgressDialog;
    private ArrayList<Logingetset> login;
    private static final String MY_PREFS_NAME = "Store";
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private static final String[] PERMISSIONS = new String[]{"publish_actions"};
    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    String name;
    private String email;
    private static String APP_ID = "2039278182984090";
    private SharedPreferences mPrefs;
    View v;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //get data from preference

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        if (prefs.getString("score", null) != null) {
            String userloginid = prefs.getString("score", null);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        init();

    }


    private void init() {
        // TODO Auto-generated method stub
        FacebookSdk.sdkInitialize(this);
        try {
            Intent iv = getIntent();
            value = iv.getStringExtra("key");
            id = iv.getStringExtra("id");
        } catch (NullPointerException e) {
            // TODO: handle exception
        }

        login = new ArrayList<>();
        edt_user = findViewById(R.id.edit_user);
        edt_pwd = findViewById(R.id.edit_pwd);

        //register button click

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                String prodel = "new";
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("delete", "" + prodel);
                editor.apply();
                Intent iv = new Intent(Login.this, Register.class);
                startActivity(iv);

            }
        });

        //login button click

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String prodel = "new";
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("delete", "" + prodel);
                editor.apply();
                method = "login";
                username = edt_user.getText().toString();
                password = edt_pwd.getText().toString();

                if (username != null) {
                    new getlogin1().execute();
                } else {
                    edt_user.setError("enter username");
                }
            }
        });

        //facebook button click
        //facebook login
        final Button loginButton = findViewById(R.id.btn_fb);

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };
        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final List<String> permissionNeeds = Arrays.asList("email");
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetail(loginResult);
            }

            @Override
            public void onCancel() {
                Log.e("check1", "cancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("check1", error.getMessage());

            }
        });
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String prodel = "new";
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("delete", "" + prodel);
                editor.apply();
                LoginManager.getInstance().logInWithReadPermissions(Login.this, permissionNeeds);

            }
        });


        Button btn_logingoogle = findViewById(R.id.btn_google);
        btn_logingoogle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //mGoogleApiClient.connect();
                String prodel = "new";
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("delete", "" + prodel);
                editor.apply();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    //class for send data to server from simple login,facebook and google plus
    class getlogin extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login.this);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            URL hp = null;
            try {
                switch (method) {
                    case "login":
                        hp = new URL(
                                getString(R.string.link) + "rest/login.php?username=" + username + "&password=" + password);
                        break;
                    case "google":
                        personname = personname.replace(" ", "%20");
                        hp = new URL(getString(R.string.link) + "rest/user_register.php?google_user&name=" + personname
                                + "&email=" + personemail + "&photo_url=" + personPhotoUrl);
                        break;
                    case "facebook":
                        user_name = user_name.replace(" ", "");
                        hp = new URL(getString(R.string.link) + "rest/user_register.php?facebook_user&name=" + user_name
                                + "&facebook_id=" + email + "&photo_url=" + fullimage);
                        break;
                }

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
                        temp.setImage(Obj.getString("fullimage"));
                        // temp.setLat(Obj.getString("lat"));
                        user2 = Obj.getString("user_id");
                        fullname = Obj.getString("name");
                        user_name = Obj.getString("username");
                        email_id = Obj.getString("email");
                        fullimage = Obj.getString("fullimage");
                        login.add(temp);
                    }

                }


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            } catch (NullPointerException e) {
                // TODO: handle exception
                Log.e("Error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            switch (method) {
                case "login":
                    if (key.equals("user")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("score", "" + user2);
                        editor.putString("username", "" + user_name);
                        editor.putString("emilid", "" + email_id);
                        editor.putString("fullname", "" + fullname);
                        editor.putString("picturepath", "" + fullimage);
                        Log.e("fullimage",fullimage+"no");
                        editor.apply();
                        if (value.equals("home")) {
                            Intent iv = new Intent(Login.this, Home.class);
                            startActivity(iv);
                            Toast.makeText(Login.this, "Login Successful..", Toast.LENGTH_LONG).show();
                        } else if (value.equals("review")) {
                            Intent iv = new Intent(Login.this, Review.class);
                            iv.putExtra("id", "" + id);
                            startActivity(iv);
                        }
                    } else if (key.equals("status")) {
                        Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG).show();
                    }
                    break;

                case "facebook":
                    if (key.equals("user")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("score", "" + user2);
                        editor.putString("username", "" + user_name);
                        editor.putString("emilid", "" + email_id);
                        editor.putString("fullname", "" + fullname);
                        editor.putString("picturepath", "" + fullimage);
                        editor.apply();
                        if (value.equals("home")) {
                            Intent iv = new Intent(Login.this, Home.class);
                            startActivity(iv);
                            Toast.makeText(Login.this, "Login Successful with Facebook", Toast.LENGTH_LONG).show();
                        } else if (value.equals("review")) {
                            Intent iv = new Intent(Login.this, Review.class);
                            iv.putExtra("id", "" + id);
                            startActivity(iv);
                        }

                    } else if (key.equals("status")) {
                        Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG).show();
                    }
                    break;

                case "google":
                    if (key.equals("user")) {
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("score", "" + user2);
                        editor.putString("username", "" + user_name);
                        editor.putString("emilid", "" + email_id);
                        editor.putString("fullname", "" + fullname);
                        editor.putString("picturepath", "" + fullimage);
                        editor.apply();
                        if (value.equals("home")) {
                            Intent iv = new Intent(Login.this, Home.class);
                            startActivity(iv);
                            Toast.makeText(Login.this, "Login Successful with Google+", Toast.LENGTH_LONG).show();
                        } else if (value.equals("review")) {
                            Intent iv = new Intent(Login.this, Review.class);
                            iv.putExtra("id", "" + id);
                            startActivity(iv);
                        }

                    } else if (key.equals("status")) {
                        Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG).show();
                    }
                    break;

            }

        }

    }

    class getlogin1 extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Login.this);
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            URL hp = null;
            try {
                // login.clear();

                if (method.equals("login")) {
                    hp = new URL(
                            getString(R.string.link) + "rest/login.php?username=" + username + "&password=" + password);
                }

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
                        temp.setImage(Obj.getString("fullimage"));
                        // temp.setLat(Obj.getString("lat"));
                        user2 = Obj.getString("user_id");
                        fullname = Obj.getString("name");
                        user_name = Obj.getString("username");
                        email_id = Obj.getString("email");
                        fullimage = Obj.getString("fullimage");
                        login.add(temp);

                    }

                }


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e("Error", e.getMessage());
            } catch (NullPointerException e) {
                // TODO: handle exception
                Log.e("Error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (method.equals("login")) {
                if (key.equals("user")) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("score", "" + user2);
                    editor.putString("username", "" + user_name);
                    editor.putString("emilid", "" + email_id);
                    editor.putString("fullname", "" + fullname);
                    editor.putString("picturepath", "" + fullimage);
                    editor.apply();
                    if (value.equals("home")) {
                        Intent iv = new Intent(Login.this, Home.class);
                        startActivity(iv);
                        Toast.makeText(Login.this, "Login Successful..", Toast.LENGTH_LONG).show();
                    } else if (value.equals("review")) {
                        Intent iv = new Intent(Login.this, Review.class);
                        iv.putExtra("id", "" + id);
                        startActivity(iv);
                    }

                } else if (key.equals("status")) {
                    Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG)
                            .show();
                }

            }

        }

    }

    private void getUserDetail(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        Log.e("User Data", json_object.toString());
                        String json = json_object.toString();
                        try {
                            JSONObject profile = new JSONObject(json);

                            // getting name of the user
                            user_name = profile.getString("name");
                            fullname = profile.getString("name");
                            // getting email of the user
                            user2 = profile.getString("id");
                            email_id = profile.getString("email");
                            JSONObject picture = profile.getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");
                            ppic = data.getString("url");
                            Log.d("ppic", "" + ppic);
                            Log.d("fbname", "" + user_name);
                            if (user_name != null) {
                                if (ppic != null) {
//                                    fullimage = "https://graph.facebook.com/" + user2 + "/picture?type=large";
                                    fullimage = "graph.facebook.com/" + user2 + "/picture?type=large";
                                    Log.d("fbimage", "" + fullimage);
                                    email = email_id.replace(" ", "%20");
                                }
                            }

//
                            key = "user";
                            method = "facebook";

                            new getlogin().execute();


                        } catch (JSONException e) {
                            Log.e("Error", e.getMessage());
                        }

                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture");
        data_request.setParameters(permission_param);
        data_request.executeAsync();
    }

    @Override
    protected void onStart() {
        super.onStart();


        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();

        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    //handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {


    }

    @Override
    public void onConnected(Bundle arg0) {
        boolean mSignInClicked = false;
        Toast.makeText(this, "User is Connect", Toast.LENGTH_LONG).show();


        // Update the UI after signin
        updateUI(true);

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
        updateUI(false);
    }


    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {

        } else {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            int statusCode = result.getStatus().getStatusCode();
            Log.d("statuscode", "" + statusCode);
            handleSignInResult(result);
//			new getlogin().execute();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("checkAccept", "" + acct);

            personname = acct.getDisplayName();
            personemail = acct.getEmail().replace(" ", "%20");
            Log.e(TAG, "display name: " + acct.getEmail() + "email" + personemail);


            if (acct.getPhotoUrl() != null) {
                personPhotoUrl = acct.getPhotoUrl().toString().replace(" ", "%20").replace("//", "\\//");
                Log.e(TAG, "photourl" + personPhotoUrl);
            }
            user_name = personname;
            fullname = acct.getGivenName();
            email_id = personemail;
            fullimage = personPhotoUrl;

            key = "user";
            method = "google";
            new getlogin().execute();


            updateUI(true);
        } else {


            updateUI(false);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
//	private class postData extends AsyncTask<String, Integer, Integer> {
//		HttpEntity httpEntity;
//		String responseStr;
//		ProgressDialog progressDialog;
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//             progressDialog = new ProgressDialog(Login.this);
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//		}
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			// TODO Auto-generated method stub
//			HttpClient httpClient=new DefaultHttpClient();
//
//			Log.e("FaceBookAll",""+name+"::"+email+"::"+imagefb+"::"+fid);
//			Log.e("GoogleAll",""+personname+"::"+personemail+"::"+personPhotoUrl+"::"+googleid);
//			if (key.equals("facebook")) {
//
//				httpEntity = MultipartEntityBuilder.create().addTextBody("username", "" + name, ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("email", "" +email, ContentType.create("text/plain", MIME.UTF8_CHARSET))
//						.addTextBody("pwd", "", ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("image", "" + imagefb, ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("phone", "").
//								addTextBody("acc_id", "" + fid).addTextBody("type", "facebook").build();
//
//			}
//
//			else if (key.equals("google")){
//				httpEntity = MultipartEntityBuilder.create().addTextBody("username", "" + personname, ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("email", "" + personemail, ContentType.create("text/plain", MIME.UTF8_CHARSET))
//						.addTextBody("pwd", "", ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("image", "" + personPhotoUrl, ContentType.create("text/plain", MIME.UTF8_CHARSET)).addTextBody("phone", "").
//								addTextBody("acc_id", "" + googleid).addTextBody("type", "google").build();
//
//			}
//
//			HttpPost httpPost=new HttpPost(getString(R.string.socialLoginservice)+"UserValidation");
//			Log.e("httpPost",""+httpPost);
//			httpPost.setEntity(httpEntity);
//			// Log.e("httpPost",""+httpPost);
//			HttpResponse response=null;
//			try {
//				response=httpClient.execute(httpPost);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			HttpEntity result=response.getEntity();
//			Log.e("result",""+result);
//			if(result!=null)
//			{
//				try {
//					responseStr= EntityUtils.toString(result).trim();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//			Log.e("Response1",""+responseStr);
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//
//            if (progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }
//			Log.e("Response2",""+responseStr);
//			new getlogin().execute();
//
//
//}
//
//}

}
