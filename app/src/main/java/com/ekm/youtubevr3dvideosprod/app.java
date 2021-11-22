package com.ekm.youtubevr3dvideosprod;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;


import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import tools.ConnectivityReceiver;

/**
 * Created by Evans Kakpovi on 9/14/2014.
 */
public class app extends MultiDexApplication {
    private Tracker mTracker;

    SharedPreferences pref;
//    private Tracker mTracker;
private static app mInstance;
    @Override
    public void onCreate() {
     //   mTracker =
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ids);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
//        mTracker.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
//                .applicationId("VwcU464sfCFOWL048VpKA1tDL6y2P9axd6zhowRR")
//                .clientKey("GZfnEjFVuWFj56jtrUFIIqrwjkpgovHE7HfhhiaQ")
//                .server("https://parseapi.back4app.com")
//        .build()
//        );

        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        mInstance = this;
        super.onCreate();
      //  JodaTimeAndroid.init(this);
    }
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public static synchronized app getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    }
//}
