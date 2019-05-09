package freaktemplate.store3;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.onesignal.OneSignal;

/**
 * Created by Redixbit on 16-09-2016.
 */
class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        OneSignal.startInit(this).init();
    }
  /* @Override
   protected void attachBaseContext(Context base) {
       super.attachBaseContext(base);
       MultiDex.install(this);
   }*/
}
