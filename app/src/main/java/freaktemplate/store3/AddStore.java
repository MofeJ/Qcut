package freaktemplate.store3;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class AddStore extends Activity {
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstore);
        //open add store link in browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link) + "appuserlogin.php"));
        try {
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(AddStore.this, "Please try Later!", Toast.LENGTH_SHORT).show();

        }
    }

}
