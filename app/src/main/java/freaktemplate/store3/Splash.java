package freaktemplate.store3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.onesignal.OneSignal;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        OneSignal.startInit(this).init();
        Thread th = new Thread() {
            @Override
            public void run() {
                try {

                    sleep(2000);
                    Intent i = new Intent(getBaseContext(), Home.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        th.start();
    }
}
