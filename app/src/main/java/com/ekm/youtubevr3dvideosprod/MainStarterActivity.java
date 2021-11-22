package com.ekm.youtubevr3dvideosprod;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.yunju.vr360videoplayer.SimpleVrVideoActivity;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import io.topvpn.vpn_api.api;

public class MainStarterActivity extends CardboardActivity implements
	 OnConnectionFailedListener, BillingProcessor.IBillingHandler{



    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private static final String TAG = "TAGGER";
    private static final int MY_PERMISSIONS_REQUEST_READ = 2564 ;
    public static  boolean noAdd;
    public static boolean newAdds;
    public static String vidId3="start2d3d";
    public static String vidId4="startsearch";
    public static String vidIdbool= "vidbool";

    Vibrator mVibrator;
//	ArrayList<Bitmap> vidThumbnailId;
//	ArrayList<String> vidPath;
//	boolean returnto = false;
    //ZoomControls zoom;
   // TextView zoo;
	//static ArrayList<String> videos, videonorm;
	//static int location;
	public static final String vidId = "ekmvideoid";
    static public final String vidId2 = "ekmvideoid2";

    int zoomint=0;
    SharedPreferences.Editor editor;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
  //  private boolean mIntentInProgress;
    BillingProcessor bp;
//    private View decorView;
//    private static final int PLUS_ONE_REQUEST_CODE = 0;
//    private String mAccountName;
    private Tracker mTracker;
    private RadioGroup rad;
    public static int itag;

    public static boolean earnCoin=true;
    public static boolean cost=true;
    public static boolean show=true;
    public static boolean exitshow=true;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static long amountToEarn=25;
    private long recheckTime = 200;
  //  public static String loopReady = "loopready";
   // public static String looppath = "looppath";
    private boolean chosen=false;
    public static boolean adReady;
    public static boolean adFrequency = true;


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection has failed");

    }

    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int i, Throwable throwable) {

    }

    @Override
    public void onBillingInitialized() {

    }


    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }



    public void settings(View v) {
        Intent id = new Intent(MainStarterActivity.this,
               Settings.class);
//        ids.putExtra(vidId, videos.get(location));
        startActivity(id);
    }

    String url;
//    final int maxInt = 30;
final int maxInt = stat.max;
    final String max = "maxResults="+maxInt;
    final String searchUrl = "https://www.googleapis.com/youtube/v3/search?";
    final String and = "&";
    final String term = "q=";
    final String key = "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
    final String part = "part=snippet";
//    ArrayList<String> ids;



    final String channelsUrl = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true";
    final String playlistItem = "https://www.googleapis.com/youtube/v3/playlistItems?";
    static String MyAcessTokenData = "access_token";
    final String playlistId = "playlistId=";
    final String pageToken = "pageToken=";
    final String extras = "videoDimension=3d&type=video,channel,playlist";
    private boolean scroll;
    public ArrayList<String> pages;
    ArrayList<String> ids;
    ArrayList<Integer> idsInt;
    private SharedPreferences pref;
    private CheckBox auto;
    TextView info;
    PackageManager manager;
    boolean hasAccelerometer, hasGyro;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // VrPanoramaView
        setContentView(R.layout.activity_select2);
        //videos = new ArrayList<String>();
        pages = new ArrayList<String>();

        manager = getPackageManager();
        hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        hasGyro = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setDeveloperModeEnabled(BuildConfig.DEBUG)
//                .build();
//        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.mydefs);
        info = (TextView) findViewById(R.id.info);
//        getAndSetDefaults();
        System.out.println("=======================  " + limitRand + "  -  " + limitry + "   -  " + earnCoin);
        // Obtain the shared Tracker instance.
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();

        pages.add("");
        checkVoiceRecognition();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        bp = new BillingProcessor(this, DeveloperKey.BILLING_KEY, this);
        bp.loadOwnedPurchasesFromGoogle();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (!pref.getBoolean("firsttime", false)) {
            recheckTime = 0;
        }
        txt = (EditText) findViewById(R.id.editText1);

        //videonorm = new ArrayList<String>();
        editor = pref.edit();
        //  AppRate.with(this).clearAgreeShowDialog();
        AppRate.with(this)
                .setInstallDays(0) // default 10, 0 means install day.
                .setLaunchTimes(10) // default 10
                .setRemindInterval(1) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);

        auto = (CheckBox) findViewById(R.id.auto);
        auto.setChecked(pref.getBoolean("autochoose", false));
        auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("autochoose", isChecked);
                editor.commit();
                System.out.println(isChecked);
            }
        });
        editor = pref.edit();
        rad = (RadioGroup) findViewById(R.id.radg);
        rad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.hd:
                        // System.out.println("HD");
                        itag = 22;
                        editor.putInt(quality, checkedId);
                        editor.commit();
                        break;
                    case R.id.sd:
                        itag = 18;
                        editor.putInt(quality, checkedId);
                        editor.commit();
                        break;
                    case R.id.gp:
                        itag = 36;
                        editor.putInt(quality, checkedId);
                        editor.commit();
                        break;
                    case R.id.gps:
                        itag = 17;
                        editor.putInt(quality, checkedId);
                        editor.commit();
                        break;
                }
            }
        });
    }

    public void picex(View v) {
//        Intent ids = new Intent(MainStarterActivity.this,
//                ShowAd.class);
//        startActivity(ids);https://play.google.com/store/apps/details?id=fart.ekm.com.vrimgviewerreal3d
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.ekm.flapbird")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.ekm.flapbird")));
        }

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(force)
                .setAction("Other")
                .setLabel("Flapm")
                .build());

    }

    void zoomDialog() {

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Zoom")
                .build());


        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_selectzoom);
        dialog.setTitle("Adjust default zoom");

        zoomint = pref.getInt("zoomer",150);
//        zoom = (ZoomControls) dialog.findViewById(R.ids.zoomControls);
//        zoo = (TextView) dialog.findViewById(R.ids.zoo);
//        zoo.setText(zoomint+"");
//
//
//        zoom.setOnZoomInClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                zoomint += 1;
//                editor.putInt("zoomer", zoomint);
//                editor.commit();
//                zoo.setText(zoomint + "");
//            }
//        });
//
//        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//                if (zoomint > 0)
//                    zoomint -= 1;
//                editor.putInt("zoomer", zoomint);
//                editor.commit();
//                zoo.setText(zoomint + "");
//            }
//        });



        dialog.show();
    }

    static long limitRand = 0;
   public static long limitry = 0;
    
    @Override
    public void onStart() {
       // startHelper("Network");
        //System.out.println("ON START");
         getAndSetDefaults();
        limitRand = mFirebaseRemoteConfig.getLong("unlock");
        limitry = mFirebaseRemoteConfig.getLong("try");
        amountToEarn = mFirebaseRemoteConfig.getLong("earn");
        amountToEarn = 0;
        newAdds = mFirebaseRemoteConfig.getBoolean("newAdd");
        earnCoin = mFirebaseRemoteConfig.getBoolean("earncoin");
        adFrequency = mFirebaseRemoteConfig.getBoolean("adf");
        cost = mFirebaseRemoteConfig.getBoolean("cost");
        show = mFirebaseRemoteConfig.getBoolean("show");
        exitshow = mFirebaseRemoteConfig.getBoolean("exitshow");
//        info.setText(limitRand+"  -  "+limitry+"   -  "+earnCoin+"  -  "+amountToEarn+" *"+recheckTime);
        startHelper("POST CONFIG2");
  //      trial();
//        Tapjoy.onActivityStart(this);
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("unlock");
//        query.getInBackground("QgO4BdIJhI", new GetCallback<ParseObject>() {
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                   limitRand = object.getInt("unlock");
//                   limitry = object.getInt("try");
//                   earnCoin = object.getBoolean("earncoin");
//                   System.out.println("Print "+limitRand+"---"+limitry);
//                   startHelper("Network");
//                } else {
//                   startHelper("No Network");
//                    System.out.println("PrintN "+limitRand+"---"+limitry);
//                }
//            }
//        });


        super.onStart();


       // EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    private  void getAndSetDefaults() {
        System.out.println(">>>>>>>>>>>>>>>>>>TRIED TO RUN DEFAULT");
        mFirebaseRemoteConfig.fetch(recheckTime)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Fetch Succeeded");
                            mFirebaseRemoteConfig.activateFetched();

                            limitRand = mFirebaseRemoteConfig.getLong("unlock");
                            limitry = mFirebaseRemoteConfig.getLong("try");
                            amountToEarn = mFirebaseRemoteConfig.getLong("earn");
                            newAdds = mFirebaseRemoteConfig.getBoolean("newAdd");
                            earnCoin = mFirebaseRemoteConfig.getBoolean("earncoin");
                            adFrequency = mFirebaseRemoteConfig.getBoolean("adf");
                            cost = mFirebaseRemoteConfig.getBoolean("cost");
                            show = mFirebaseRemoteConfig.getBoolean("show");
                            exitshow = mFirebaseRemoteConfig.getBoolean("exitshow");
//                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>  "+limitRand+"  -  "+limitry+"   -  "+earnCoin+"  -  "+amountToEarn+" *"+recheckTime);
                        //   info.setText(limitRand+"  -  "+limitry+"   -  "+earnCoin+"  -  "+amountToEarn+" *"+recheckTime);
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Config ok")
                                    .setAction("New")
                                    .setLabel("Lock:"+limitry+"   -  To earn:"+amountToEarn)
                                    .build());
//                            startHelper("POST CONFIG");
                        } else {
                            Log.d(TAG, "Config failed");
                            System.out.println(">>>>>>>>>>>>>>>>>>FAILED RUN DEFAULT");
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Config")
                                    .setAction("failed")
                                    .setLabel("Lock:"+limitry+"   -  To earn:"+amountToEarn)
                                    .build());
                           // startHelper("POST CONFIG");
                        }

                    }
                });
    }

    void trial() {
        editor.putBoolean("noluck", true);
        editor.commit();
    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return StringUtils.capitalize(model);
        }
        return StringUtils.capitalize(manufacturer) + " " + model;
    }
    final String force = "Force";
    void startHelper(String queried) {
//        editor.putBoolean("noluck", false);
//        editor.commit();
        if (!pref.getBoolean("firsttime", false) ) {
          //  recheckTime = 0;
            if (BillingProcessor.isIabServiceAvailable(this)){
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(force)
                        .setAction("New user can pay me - "+queried)
                        .setLabel(getDeviceName())
                        .setValue(limitRand)
                        .build());
                editor.putBoolean("firsttime", true);
                editor.commit();
            } else {
                limitRand = 500;
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(force)
                        .setAction("Free Pass")
                        .build());
            }

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(force)
                        .setAction("Unlucky User - No choice")
                        .build());
                    editor.putBoolean("noluck", true);
                    editor.commit();

            editor = pref.edit();
//            editor.putBoolean("autochoose", true);
//            editor.commit();

            editor.putInt("zoomer", 100);
            editor.commit();

            editor.putBoolean("autochoose", false);
            editor.commit();

            editor.putBoolean(Settings.effect3did, false);
            editor.commit();
            Intent id = new Intent(MainStarterActivity.this,
                    intro.class);
            startActivity(id);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
     //   Tapjoy.onActivityStop(this);
        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }



    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
//        if (activities.size() == 0) {
//            mbtSpeak.setEnabled(false);
//            mbtSpeak.setText("Voice recognizer not present")
//            Toast.makeText(this, "Voice recognizer not present",
//                    Toast.LENGTH_SHORT).show();
//        }
    }
    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "ok");

        // Given an hint to the recognizer about what the user is going to say
        //There are two form of language model available
        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        // If number of Matches is not selected then return show toast message
//        if (msTextMatches.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
//            Toast.makeText(this, "Please select No. of Matches from spinner",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }

        int noOfMatches = 5;
        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
        //Start the Voice recognizer activity for the result.
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    private static final int REQ_SIGN_IN_REQUIRED = 55664;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {




        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)  {

            //If Voice recognition is successful then it returns RESULT_OK
            if(resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    if (textMatchList.get(0).contains("ok")) {

                        String searchQuery = textMatchList.get(0);
                        searchQuery = searchQuery.replace("ok","");
                        System.out.println(searchQuery);
                        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                        search.putExtra(SearchManager.QUERY, searchQuery);
                        startActivity(search);
                    } else {
                        // populate the Matches
//                        mlvTextMatches
//                                .setAdapter(new ArrayAdapter<string>(this,
//                                        android.R.layout.simple_list_item_1,
//                                        textMatchList));
                        System.out.println("We got a match");
                    }

                }
                //Result code for various error.
            }else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                showToastMessage("Audio Error");
            }else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                showToastMessage("Client Error");
            }else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                showToastMessage("Network Error");
            }else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                showToastMessage("No Match");
            }else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                showToastMessage("Server Error");
            } }
        super.onActivityResult(requestCode, resultCode, data);
    }
    /**
     * Helper method to show the toast message
     **/
    void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void search(View v) {

            Intent id = new Intent(MainStarterActivity.this,
                    SearchActivity.class);
            startActivity(id);
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("SearchActivity")
                       .setLabel("search")
                        .setValue(1)
                        .build());
    }
      //  speak(v);

    public void search3d(View v) {
        System.out.println("Search 3D");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_READ);
        }else {
            System.out.println("else permission");
            start2d3d();
        }
    }
    void p(String g) {
        System.out.println(g);
    }
String m = ">>>>>>>>>>>>>>>>>>>>>>>>>||||||||||||||||||||";
    EditText txt;

	public void play(View v) {

		mVibrator.vibrate(50);

		if (!txt.getText().toString().isEmpty()) {
			String org = txt.getText().toString();
			String texid = StringUtils.substringAfter(org, "?v=");
			if (texid.isEmpty()) {
				if (org.length()>=11)
				texid = StringUtils.substring(org, org.length()-11, org.length());
				else
				texid=org;
//				System.out.println(texid);
			}
//			System.out.println(texid);
			Intent id = new Intent(MainStarterActivity.this, MainActivity.class);
			//videos.add(texid);
			id.putExtra(vidId, texid);
			startActivity(id);
		} else {

			Intent id = new Intent(MainStarterActivity.this, MainActivity.class);
			//videos.add(txt.getText().toString());
			id.putExtra(vidId, txt.getText().toString());
			startActivity(id);
		}
	}
final public static String quality  = "vrqualitiesints";
public static Boolean ads = false;
    @Override
    protected void onResume() {
       // preroll=true;
        mFirebaseRemoteConfig.activateFetched();

        Log.i(TAG, "Setting screen name: " + "");
        mTracker.setScreenName("Starting Activity Starter");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (bp.isPurchased(Settings.adremove) || bp.isPurchased(Settings.unlock)|| bp.isPurchased(Settings.unlock2)|| api.get_user_selection(MainStarterActivity.this)==1){
            editor = pref.edit();
            editor.putBoolean("ad", false);
            editor.commit();
            Log.d("ADSTATUS", "WILL NOT SHOW ADS");
        }

        rad.check(pref.getInt(quality, R.id.hd));


//        mPlusOneButton.initialize("https://play.google.com/store/apps/details?id=com.ekm.youtubevr3dvideos", PLUS_ONE_REQUEST_CODE);
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    private void resettutor(boolean todo) {
        for (int i =0; i<var.run1.length; i++) {
            editor.putBoolean(var.run1[i], todo);
            editor.commit();
        }
    }


//	@Override
//	public void onCardboardTrigger() {
//		mVibrator.vibrate(50);
//		Intent ids = new Intent(SelectActivity.this, MainActivity.class);
//		ids.putExtra(vidId, videos.get(0));
//		startActivity(ids);
//		super.onCardboardTrigger();
//	}

//	@Override
//	public void onInsertedIntoCardboard(CardboardDeviceParams deviceParams) {
//		// TODO Auto-generated method stub
//		// setContentView(R.layout.activity_select2);
//		// super.onInsertedIntoCardboard(deviceParams);
//	}
//
//	@Override
//	public void onRemovedFromCardboard() {
//		// TODO Auto-generated method stub
//		super.onRemovedFromCardboard();
//	}


    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;

        getMenuInflater().inflate(R.menu.search, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.zoom_settings:
                zoomDialog();
                break;
            case R.id.immersive:
                ImmerseDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void ImmerseDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_immerse);
        dialog.setTitle("Immersive mode settings");

        Switch sw = (Switch) dialog.findViewById(R.id.switch1);
        sw.setChecked(pref.getBoolean(var.autochoose,true));
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                 editor.putBoolean(var.autochoose,true);
                    editor.commit();

                }else {
                    editor.putBoolean(var.autochoose,false);
                    editor.commit();
                }
            }
        });

        Switch sw2 = (Switch) dialog.findViewById(R.id.switch2);
        sw2.setChecked(false);
        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                  resettutor(true);

                }else {
          resettutor(false);
                }
            }
        });


        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 start2d3d();

                } else {

                   start2d3d();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




    void start2d3d() {
        getURI();
    }



    void getURI() {
        if (!chosen) {
            chosen = true;
            final DateTime dt = new DateTime();
//                            long time =8;
            final long time = dt.getDayOfMonth();
            boolean istime=false;
//            System.out.println("timefactor: "+time+ "|"+pref.getLong("vrlooptime", 0));
//            if (time <30 && pref.getLong("vrlooptime", 0)>=30 ) {
//                istime = true;
//            }
//            else if (time>pref.getLong("vrlooptime", 0) || pref.getLong("vrlooptime", 0)==0  || pref.getLong("vrlooptime", 0)>31) {
//                istime=true;
//            }
            if (false) {
            String youtubeLink = "http://youtube.com/watch?v=" +"QEidwK1oiM0";
            YouTubeUriExtractor ytEx = new YouTubeUriExtractor(this) {
                @Override
                public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                    String downloadUrl = "";
                    if (ytFiles != null) {
                        ArrayList<Integer> key = new ArrayList<>();
                        for (int i = 0; i < ytFiles.size(); i++) {
                            key.add(ytFiles.keyAt(i));
                            System.out.println(key.get(i));
                        }
                       if (key.contains(22)) {
                            downloadUrl = ytFiles.get(22).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itag")
                                    .setAction("defaulted to")
                                    .setLabel("22")
                                    .build());
                        }else {
                            chosen = false;
                        }
                        System.out.println("Url------------>>> :" + downloadUrl);
                        if (downloadUrl != null && !downloadUrl.isEmpty()) {

                                editor.putLong("vrlooptime", time);
                                editor.commit();
                                System.out.println("time is committed "+time);
                                Intent intent = new Intent(MainStarterActivity.this, SimpleVrVideoActivity.class);
                                intent.putExtra(MainStarterActivity.vidId, "notnull");
//                                intent.putExtra(SimpleVrVideoActivity.TAG_VIDEO_URL, MainStarterActivity.strVideoDownloadPath);
                                intent.putExtra(SimpleVrVideoActivity.TAG_VIDEO_URL, downloadUrl);
                                startActivity(intent);

                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("FruitLoop")
                                        .setAction("Running AD")
                                        .setLabel("2D")
                                        .setValue(1)
                                        .build());

                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("FruitLoopDay")
                                        .setAction(dt.monthOfYear().getAsText())
                                        .setLabel(dt.dayOfWeek().getAsText())
                                        .setValue(1)
                                        .build());
                                chosen = false;

                        } else {
                            System.out.println("Download urL is null");
                            chosen = false;
                        }

                    } else {
                        System.out.println("YTFILES is null");
                        chosen = false;
                        // BACKUP Plan
                    }
                }
            };
            ytEx.setIncludeWebM(false);
            ytEx.execute(youtubeLink);

        } else {
            Intent intent = new Intent(MainStarterActivity.this, Floating2D3DStarted.class);
//                                        intent.putExtra(MainStarterActivity.vidId,vidid2);
//                                intent.putExtra("lloop", time2);
            startActivity(intent);
            chosen = false;
        }}
    }}


