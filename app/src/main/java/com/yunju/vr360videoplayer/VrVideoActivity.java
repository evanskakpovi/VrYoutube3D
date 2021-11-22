package com.yunju.vr360videoplayer;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;

import com.ekm.youtubevr3dvideosprod.DeveloperKey;
import com.ekm.youtubevr3dvideosprod.MainStarterActivity;
import com.ekm.youtubevr3dvideosprod.Settings;
import com.ekm.youtubevr3dvideosprod.app;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.joda.time.DateTime;
import org.rajawali3d.util.RajLog;
import org.rajawali3d.vr.RajawaliVRActivity;


public class VrVideoActivity extends RajawaliVRActivity {
  private static final String TAG = VrVideoActivity.class.getSimpleName();
  public static final String TAG_VIDEO_URL = "tag_video_url";
  private String strVideoDownloadPath;
  VR360Renderer mRenderer;
  private Tracker mTracker;
  long time;
  private String vidid3;
  PackageManager manager;
  SharedPreferences.Editor editor;
  private SharedPreferences pref;
  boolean hasAccelerometer, hasGyro;
  private boolean chosen;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    super.onCreate(savedInstanceState);

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    strVideoDownloadPath = getIntent().getStringExtra(TAG_VIDEO_URL);
    mRenderer = new VR360Renderer(this);
    mRenderer.setVideoPath(strVideoDownloadPath);

    setRenderer(mRenderer);
    setConvertTapIntoTrigger(true);
    app application = (app) getApplication();
    mTracker = application.getDefaultTracker();

    pref = PreferenceManager
            .getDefaultSharedPreferences(this);
    editor = pref.edit();
    manager = getPackageManager();
    hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    hasGyro = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
  }
  /**
   * Called when the Cardboard trigger is pulled.
   */
  @Override
  public void onCardboardTrigger() {
    RajLog.i("onCardboardTrigger");
  }

  @Override
  public void onPause() {
    if(mRenderer != null)
      mRenderer.pauseVideo();
    super.onPause();
  }

  @Override
  public void onResume() {

    mTracker.setScreenName("VR 360)");


    super.onResume();
//    if(mRenderer != null)
//      mRenderer.resumeVideo();

//    showAd(true);
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
      mRenderer = null;

  }
//  final DateTime dt = new DateTime();
  //                            long time =8;
//  final long time = dt.getDayOfMonth();
  //            editor.putLong("vrlooptime", time);



  boolean isFirstToday() {
if (!MainStarterActivity.adFrequency) {
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
