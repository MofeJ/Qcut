package freaktemplate.store3;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import freaktemplate.getset.Logingetset;

public class Profile extends Activity {
	private static final String MY_PREFS_NAME = "Store";
	private String score;
	private String fullname;
	private String mail;
	private String username;
	private String picturepath;
	private String responseStr;
	private String key;
	private String userloginid;
	private String user_name1;
	private String Full_name;
	private String Email;
	private String imageprofile;
	private String user_name;
	private String full_name;
	private String user2;
	String loginimage;
	private String emailid;
	private String Error;
	private EditText edt_username;
	private EditText edt_fullname;
	private EditText edt_mailid;
	private ImageView img_profile;
	private Button btn_profile;
	private Button btn_delete;

	private static int RESULT_LOAD_IMAGE = 1;

	private ProgressDialog progressDialog;
	private ArrayList<Logingetset> login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		init();
		getpreferences();
		buttonclick();

	}

	private void buttonclick() {
		// TODO Auto-generated method stub

		//update profile button click
		btn_profile = findViewById(R.id.btn_update);
		btn_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				fullname = edt_fullname.getText().toString();
				mail = edt_mailid.getText().toString();
				username = edt_username.getText().toString();
				new PostDataAsyncTask().execute();
			}
		});

		//logout profile click

		btn_delete = findViewById(R.id.btn_delete);
		btn_delete.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				AlertDialog alertDialog = new AlertDialog.Builder(Profile.this).create(); // Read
																							// Update
				alertDialog.setTitle("Logout?");
				alertDialog.setMessage("Are you sure you want to logout?");

				alertDialog.setButton("Yes, Continue.", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// here you can add functions
						boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
						if(!loggedIn)
						{
							LoginManager.getInstance().logOut();
						}
						score = "delete";
						String prodel = "delete";
						SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
						editor.clear();
						editor.putString("delete", "" + prodel);
						editor.putString("score", "" + score);
						editor.apply();
						Intent iv = new Intent(Profile.this, Home.class);
						startActivity(iv);
					}
				});

				alertDialog.show();

			}
		});

		//choose image from gallery

		img_profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
	}

	private void init() {
		FacebookSdk.sdkInitialize(this);
		// TODO Auto-generated method stub
		login = new ArrayList<>();
		edt_fullname = findViewById(R.id.edt_fullname);
		edt_username = findViewById(R.id.edt_username);
		edt_mailid = findViewById(R.id.edt_malid);

		img_profile = findViewById(R.id.img_profile);

		//img_profile.setImageBitmap(decodeFile(picturepath));

	}

	private void getpreferences() {
		// TODO Auto-generated method stub


		SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
		picturepath = prefs.getString("picturepath", null);
		userloginid = prefs.getString("score", null);
		Full_name = prefs.getString("fullname", null);
		user_name1 = prefs.getString("username", null);
		Email = prefs.getString("emilid", null);
		imageprofile = prefs.getString("picturepath", null);

		String s = "";
		try {
			if(Full_name!=null)
			{	s = new String(Full_name.getBytes(), "UTF-8");}
		} catch (UnsupportedEncodingException e) {
			Log.e("utf8", "conversion", e);
		}

		String lang = Locale.getDefault().getLanguage();
		if (lang.equals("en")) {
			edt_mailid.setText(Email);
			edt_fullname.setText(Full_name);
			edt_username.setText(user_name1);
		} else if (lang.equals("ar")) {
			String text = "";
			String[] str = Full_name.split(" ");
			for (int k = 0; k < str.length; k++) {
				str[k] = str[k].replace("\\", "");
				String[] arr = str[k].split("u");
				text += " ";
				for (int i = 1; i < arr.length; i++) {
					try {
						int hexVal = Integer.parseInt(arr[i], 16);
						text += (char) hexVal;
					} catch (NumberFormatException e) {
            e.printStackTrace();
					}

				}
			}

			String text1 = "";
			String[] str1 = user_name1.split(" ");
			for (int k = 0; k < str1.length; k++) {
				str1[k] = str1[k].replace("\\", "");
				String[] arr1 = str1[k].split("u");
				text1 += " ";
				for (int i = 1; i < arr1.length; i++) {
					int hexVal = Integer.parseInt(arr1[i], 16);
					text1 += (char) hexVal;
				}
			}

			edt_mailid.setText(Email);
			edt_fullname.setText(text);
			edt_username.setText(text1);
		}

		if (picturepath != null) {

			Log.e("loginimage12", "" + picturepath);

			picturepath = picturepath.replace("\\", "");
			Picasso.get()
					.load(picturepath).fit()
					.into(img_profile);

		}
	}

	//download image from url


	//post updated data on server class

	class PostDataAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// do stuff before posting data
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
			Log.d("successful", "successful");

			new getlogin().execute();
		}
	}

	private void postdata() {
		// TODO Auto-generated method stub

		HttpClient httpClient = new DefaultHttpClient();
		HttpEntity entity;
		if (picturepath != null) {
			entity = MultipartEntityBuilder.create().addTextBody("email", mail)
					.addTextBody("fullname", fullname).addBinaryBody("file", new File(picturepath),
							ContentType.create("application/octet-stream"), "filename")
					.addTextBody("username", username).addTextBody("user_id", userloginid).build();
		} else {
			entity = MultipartEntityBuilder.create().addTextBody("email", mail).addTextBody("fullname", fullname)
					.addTextBody("username", username).addTextBody("user_id", userloginid).build();
		}

		HttpPost httpPost = new HttpPost(getString(R.string.link) + "rest/update_user.php");
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
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.v("Response", "Response: " + responseStr);


		}
	}

	//after selected image from gallery return data in this method

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaColumns.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturepath = cursor.getString(columnIndex);
			SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
			editor.apply();
			cursor.close();
			img_profile.setImageBitmap(BitmapFactory.decodeFile(picturepath));

		}

	}

	//after successfully post data on server this class is called

	class getlogin extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Profile.this);
			progressDialog.setMessage("Loading..");
			progressDialog.setCancelable(true);
			progressDialog.show();
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
						temp.setImage(Obj.getString("image"));
						user2 = Obj.getString("user_id");
						user_name = Obj.getString("username");
						full_name = Obj.getString("name");
						emailid = Obj.getString("email");
						imageprofile = Obj.getString("image");
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

			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			if (key.equals("user")) {
				SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
				editor.putString("score", "" + user2);
				editor.putString("username", "" + user_name);
				editor.putString("fullname", "" + full_name);
				editor.putString("emilid", "" + emailid);
				editor.putString("imageprofile", "" + imageprofile);
				editor.apply();
				Intent iv = new Intent(Profile.this, Home.class);
				startActivity(iv);
			} else if (key.equals("status")) {
				Toast.makeText(Profile.this, "try different one", Toast.LENGTH_LONG).show();
			}
		}
	}

}
