package com.ekm.youtubevr3dvideosprod;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ekm.youtubevr3dvideosprod.SimpleGestureFilter.SimpleGestureListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.vrtoolkit.cardboard.CardboardActivity;

import org.joda.time.DateTime;


public class MainActivity extends CardboardActivity implements
        OnInitializedListener, SimpleGestureListener {
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private SimpleGestureFilter detector;
    View decorView;
    String path;
    Vibrator mVibrator;
    int zoomer;
    RelativeLayout rel;
    YouTubePlayerFragment youTubePlayerFragment;
    private SharedPreferences pref;
//    private InterstitialAd interstitial;
//    AdRequest adRequest;
    FrameLayout yfrag;
    private Tracker mTracker;
    private SharedPreferences.Editor editor;
    private long lloop;
//    private PackageManager manager;
//    private boolean hasAccelerometer, hasGyro;

    PackageManager manager;
    boolean hasAccelerometer, hasGyro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);

     hideSystemUI();
        setFrame();
        editor = pref.edit();
        editor.putInt("inc",  pref.getInt("inc",0)+1);
        editor.commit();
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();
        zoomer = pref.getInt("zoomer", 0);
        rel = (RelativeLayout) findViewById(R.id.rel);
        rel.setPadding(zoomer, zoomer, zoomer, zoomer);
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Bundle extras = getIntent().getExtras();
        path = extras.getString(MainStarterActivity.vidId);
        lloop = extras.getLong("lloop");
    System.out.println(path+"            true");
        youTubePlayerFragment = YouTubePlayerFragment.newInstance();

        youTubePlayerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);



        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.yfrag, youTubePlayerFragment);
        fragmentTransaction.commit();

        manager = getPackageManager();
        hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        hasGyro = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Immerse")
                .setAction("totals")
                .setLabel("VideoMain" +
                        "")
                .setValue(1)
                .build());


    }
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    YouTubePlayer myPlayer;

    @Override
    public void onInitializationSuccess(Provider arg0, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            myPlayer = player;
            player.loadVideo(path);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
            hideSystemUI();

            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {
                }

                @Override
                public void onVideoStarted() {
                    mHandler.postDelayed(mUpdateTimeTask, 50);
                }

                @Override
                public void onVideoEnded() {
                    showAd(true);
                    finish();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {
                }
            });



        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }


    private boolean ready;
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {


try {
    if (myPlayer != null && myPlayer.isPlaying()) {


//                System.out.println("trying");
        long currentDuration = myPlayer.getCurrentTimeMillis();

        if (currentDuration > 25000 && !ready) {
            ready = true;
            System.out.println(ready);
        }

    }
    //set delay for next update
    mHandler.postDelayed(this, 50);
}
catch (IllegalStateException e){

}
        }
    };

    private android.os.Handler mHandler = new android.os.Handler();
    @Override
    public void onSingleTapUp() {
        if (myPlayer!=null){
        if (myPlayer.isPlaying())
myPlayer.pause();
        else {
            myPlayer.play();
        }}
    }
    @Override
    public void onInitializationFailure(Provider arg0,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = "ll";
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCardboardTrigger() {
        //next();
//       finish();
//        System.out.println("okoko");
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);

        return super.dispatchTouchEvent(me);
    }

    private void setFrame() {

        int border = pref.getInt("border", Settings.borderDefault);
        yfrag = (FrameLayout) findViewById(R.id.yfrag);
        yfrag.setBackgroundResource(border);
        //frame2.setBackgroundResource(border);

//        System.out.println(border);
    }
    @Override
    public void onSwipe(int direction) {

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                zoomer += 10;
                rel.setPadding(zoomer, zoomer, zoomer, zoomer);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                if (zoomer >= 0)
                    zoomer -= 10;

                rel.setPadding(zoomer, zoomer, zoomer, zoomer);
                break;

        }

    }

    @Override
    public void onStart() {
        super.onStart();

      //  EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();

        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    @Override
    public void onResume() {
        super.onResume();

        mTracker.setScreenName("3D Player Mainactivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onDoubleTap() {
        // TODO Auto-generated method stub

    }



    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("DESTroyed");

        if (mHandler != null) {
            //Make sure handler remove when activity destroyed.
            mHandler.removeCallbacks(mUpdateTimeTask);
        }

    }

    void showAd(boolean exit) {
 }

    @Override
    public void onBackPressed() {
        if (ready ) {
        showAd(MainStarterActivity.exitshow); }
        super.onBackPressed();
    }



    boolean isFirstToday() {
if(!MainStarterActivity.adFrequency) {
    final DateTime dt = new DateTime();
    final long time = dt.getDayOfMonth();
    //    System.out.println(time);
    boolean istime = false;

    if (time < 30 && pref.getLong("oncetoday", 0) >= 30) {
        istime = true;
    } else if (time > pref.getLong("oncetoday", 0) || pref.getLong("oncetoday", 0) == 0 || pref.getLong("oncetoday", 0) > 31) {
        istime = true;
    }
    //  System.out.println("Will play "+istime);
    editor.putLong("oncetoday", time);
    editor.commit();
    return istime;
}else {
    return true;
}
    }
}
