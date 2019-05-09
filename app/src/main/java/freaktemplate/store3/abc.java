package freaktemplate.store3;

import android.app.Application;

import com.onesignal.OneSignal;

/**
 * Created by Redixbit on 21-12-2016.
 */
class abc extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this).init();

        // Sync hashed email if you have a login system or collect it.
        //   Will be used to reach the user at the most optimal time of day.
        // OneSignal.syncHashedEmail(userEmail);
    }
}
