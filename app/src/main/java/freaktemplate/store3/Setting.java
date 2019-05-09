package freaktemplate.store3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

public class Setting extends PreferenceActivity {

	private SharedPreferences sharedPreferences;
	Context context = this;
	private String email;
    private String name;
    private String phone;
    private String address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.setting);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		getintent();

		openGmail();

	}

	private void getintent() {
		// TODO Auto-generated method stub
		Intent iv = getIntent();
		email = iv.getStringExtra("email");
		Log.d("email", "" + email);
		name = iv.getStringExtra("namec");
		Log.d("name", "" + name);

		phone = iv.getStringExtra("phone");
		Log.d("name", "" + phone);
		address=iv.getStringExtra("address");

	}

	private void openGmail() {
		// TODO Auto-generated method stub
		try {

			Intent gmail = new Intent(Intent.ACTION_VIEW);
			gmail.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
			gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
			gmail.setData(Uri.parse(email));
			gmail.putExtra(Intent.EXTRA_SUBJECT, "Store Finder");
			gmail.setType("text/plain");
			gmail.putExtra(Intent.EXTRA_TEXT,
					"Name: " + Html.fromHtml(name) + "\n" + "Mail id: " + Html.fromHtml(email) + "\n" +"Address: "+Html.fromHtml(address)+"\n"+ "Contact no: " +Html.fromHtml(phone) );
			startActivity(gmail);
		} catch (Exception e) {
			sendEmail();
		}
	}

	private void sendEmail() {
		// TODO Auto-generated method stub
		String recipient = email;
		String subject = "store finder";
		@SuppressWarnings("unused")
		String body = "";

		String[] recipients = { recipient };
		Intent email = new Intent(Intent.ACTION_SEND);

		email.setType("message/rfc822");
		email.putExtra(Intent.EXTRA_EMAIL, recipients);
		email.putExtra(Intent.EXTRA_SUBJECT, subject);

		try {

			startActivity(Intent.createChooser(email, getString(R.string.email_choose_from_client)));

		} catch (android.content.ActivityNotFoundException ex) {

			Toast.makeText(Setting.this, getString(R.string.email_no_client), Toast.LENGTH_LONG).show();

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

	}
}
