package com.ekm.youtubevr3dvideosprod;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ekm.youtubevr3dvideosprod.SimpleGestureFilter.SimpleGestureListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.vrtoolkit.cardboard.CardboardActivity;

import org.joda.time.DateTime;

import java.io.IOException;


public class VideoPlayerActivity extends CardboardActivity implements TextureView.SurfaceTextureListener, SeekBar.OnSeekBarChangeListener, SimpleGestureListener {

    private SimpleGestureFilter detector;
    //  the main TextureView which show the video
    public TextureView videoView;
    private Tracker mTracker;
    //Mediya player which play video
    public MediaPlayer mMediaPlayer;

    //for position change
    ViewGroup.LayoutParams params;

    public int old_duration=0;

    public boolean container = false;
    public ImageView framImageView;

    public RelativeLayout layoutContainAllView;
    public LinearLayout  menuBar,main_Conteinar, liner;
    RelativeLayout image_layout, texture_Layout;

    //progressbar for initial loading.
    private ProgressBar  bar1, bar2;
    //seekbar which show the video progress and move to seek position
    SeekBar movieProgress;
    //play button
    ImageButton playBtn;
//    public boolean canShowAd;
    //duration, total time and info textview
    TextView elapseedTime, totalTime;


    //custom class for time and other calculation
    private Utilities utils;

    //width/heith ratio can set by this variable
    static final float sreenRatio = 1.5f;


    //this value for change screen size minimum 2 which is 50-50 part or screen
    float scale = 2;

    public float fistPostion = 0;
    public float secondPosition = 0;

    public int mainLayoutWidth;
    SharedPreferences.Editor editor;

    public float xDifference = 0;

    public Bitmap backgroundSnap;

    private LinearLayout frame, frame2;

    private android.os.Handler mHandler = new android.os.Handler();

    Uri videoUri;
    Long lloop;
   private String path;    private Vibrator mVibrator;
    SharedPreferences pref;
    private View decorView;
    private int zoomer;
    PackageManager manager;
    boolean hasAccelerometer, hasGyro;
    private boolean ready;
    private boolean track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (pref.getBoolean(Settings.effect3did,true)){
        setContentView(R.layout.activity_mains);}
        else {
            setContentView(R.layout.activity_mainsfalse);
        }
        //hideSystemUI();
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Bundle extras = this.getIntent().getExtras();
        detector = new SimpleGestureFilter(this, this);
        path = extras.getString(MainStarterActivity.vidId);
        lloop = extras.getLong("lloop");
//        manager = getPackageManager();
//        hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
//         hasGyro = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);



        //All initialization
        utils = new Utilities();
        mMediaPlayer = new MediaPlayer();
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        bar1 = (ProgressBar) findViewById(R.id.progressBar6);
        bar2 = (ProgressBar) findViewById(R.id.progressBar7);
        bar1.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
        bar2.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);
        liner = (LinearLayout) findViewById(R.id.liner2);
        layoutContainAllView = (RelativeLayout) findViewById(R.id.ll_main);
        elapseedTime = (TextView) findViewById(R.id.tv_elapsed_time);
        totalTime = (TextView) findViewById(R.id.tv_totol_time);
        movieProgress = (SeekBar) findViewById(R.id.movie_progress);
        playBtn = (ImageButton) findViewById(R.id.play_btn);
        menuBar = (LinearLayout) findViewById(R.id.menuBar);

        main_Conteinar = (LinearLayout) findViewById(R.id.main_container);
        framImageView = (ImageView) findViewById(R.id.imageView);
        texture_Layout = (RelativeLayout) findViewById(R.id.texturelayout);
        image_layout = (RelativeLayout) findViewById(R.id.imagelayout);
        videoView = (TextureView) findViewById(R.id.videoView);

        //set initial progress and max progress of seekbar
        movieProgress.setProgress(0);
        movieProgress.setMax(100);

        //create seekbar listener
        movieProgress.setOnSeekBarChangeListener(this);

        // set uri from your link
        try {
            videoUri = Uri.parse(path);
        } catch (Exception e) {

        }
        editor = pref.edit();
        editor.putInt("inc",  pref.getInt("inc",0)+1);
        editor.commit();
        //check internet connection
        if (connectionAvailable()) {
            videoView.setSurfaceTextureListener(this);
        } else {
            System.out.println("Error in internet connection. Check the connection and reopen the apps");

        }

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Immerse")
                .setAction("totals")
                .setLabel("VideoPlayer")
                .setValue(1)
                .build());
    }


    private boolean loadads=true;
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {



            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {


                long totalDuration = mMediaPlayer.getDuration();
                long currentDuration = mMediaPlayer.getCurrentPosition();

                if (old_duration == currentDuration) {
                    bar1.setVisibility(View.VISIBLE);
                    bar2.setVisibility(View.VISIBLE);
                } else {
                    bar1.setVisibility(View.GONE);
                    bar2.setVisibility(View.GONE);
                }
                old_duration = (int)currentDuration;

                if (currentDuration>25000 && !ready) {
                    ready = true;
//                    System.out.println(ready);
                }

                elapseedTime.setText("" + utils.milliSecondsToTimer(currentDuration));
                totalTime.setText("" + utils.milliSecondsToTimer(totalDuration));

                int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                movieProgress.setProgress(progress);

            }
            //set delay for next update
            mHandler.postDelayed(this, 50);
        }
    };


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Surface surface1 = new Surface(surface);

        try {

            //set media player data source
            mMediaPlayer.setDataSource(this, videoUri);

            //set surface
            mMediaPlayer.setSurface(surface1);

            //set repeating
            mMediaPlayer.setLooping(false);
            //set screen on while playing
            mMediaPlayer.setScreenOnWhilePlaying(true);

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();
            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    //start media player on prepared
                    mediaPlayer.start();

                    bar1.setVisibility(View.GONE);
                    bar2.setVisibility(View.GONE);


                    //plaState flag for video playing

                    playBtn.setImageResource(R.drawable.pause_btn);

                    //menuOpen flag true for menubar is showing
                    menuBar.setVisibility(View.GONE);
                    //info text gone

                    //set frame to video
                    //texture_Layout.setBackgroundResource(R.color.fbutton_color_transparent);
                    //image_layout.setBackgroundResource(R.color.fbutton_color_transparent);
                    setFrame();
                    //enable handler to update menu
                    mHandler.postDelayed(mUpdateTimeTask, 50);

                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Immerse")
                            .setAction("Showadcall")
                            .setLabel("VideoPlayerActivity: Showad on complete called")
                            .setValue(1)
                            .build());

                    finish();
                }
            });
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                                    player2.pause();
                            bar1.setVisibility(View.VISIBLE);
                            bar2.setVisibility(View.VISIBLE);;
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            bar1.setVisibility(View.GONE);
                            bar2.setVisibility(View.GONE);
                            break;
                    }
                    return false;
                }

            });
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    if (mp != null) {
                        movieProgress.setSecondaryProgress(percent);
                    }

                }
            });






        } catch (IllegalArgumentException e) {
            // Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            // Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            // Log.d(TAG, e.getMessage());

        } catch (IOException e) {
            //e.printStackTrace();
        }

    }

            @Override
            public void onSurfaceTextureSizeChanged (SurfaceTexture surface,int width, int height){

            }

            @Override
            public boolean onSurfaceTextureDestroyed (SurfaceTexture surface){
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated (SurfaceTexture surface){

                //take layout param for resize screen
                params = texture_Layout.getLayoutParams();

                //take main screen width which is have 10dp margin, if u want you can remove margin from xml
                mainLayoutWidth = layoutContainAllView.getWidth();

                //taking screen size changing parameter
                xDifference = (mainLayoutWidth / 2) - (mainLayoutWidth / scale);

                //change the height and witdh acording to scale
                params.width = (int) (mainLayoutWidth / scale);
                params.height = (int) ((mainLayoutWidth / scale) / sreenRatio);

                //set height and width to imageview and video
                texture_Layout.setLayoutParams(params);
                image_layout.setLayoutParams(params);

                if (mMediaPlayer.isPlaying()) {

                    //set first screen position and second screen position
//                    fistPostion = 0 + xDifference;
//                    secondPosition = texture_Layout.getWidth() + xDifference;

                    //get the video current image for continuty
                    backgroundSnap = videoView.getBitmap();

                    //set video current fram to imageview
                    if (backgroundSnap != null) {
                        framImageView.setBackgroundDrawable(new BitmapDrawable(backgroundSnap));
                    }

//                    //here exchanging position of imageview and textureview acording to framrate of the video
//
//                    if (container) {
//                        container = false;
//                        image_layout.setX(secondPosition);
//                        texture_Layout.setX(fistPostion);
//
//
//                    } else {
//                        container = true;
//                        image_layout.setX(fistPostion);
//                        texture_Layout.setX(secondPosition);
//
//                    }
                }

            }


        private boolean connectionAvailable () {
            boolean connected = false;
            //checking wireless connection
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            return connected;
        }

        @Override
        public void onProgressChanged (SeekBar seekBar,int progress, boolean fromUser){

        }

        @Override
        public void onStartTrackingTouch (SeekBar seekBar){
                track = true;
        }

    /*
    if anyone change progress than change the mediaplayer to that position
     */
        @Override
        public void onStopTrackingTouch (SeekBar seekBar){
track  = false;
            if (mMediaPlayer.isPlaying()) {
                mHandler.removeCallbacks(mUpdateTimeTask);

                int totalDuration = (int) mMediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                mMediaPlayer.seekTo(currentPosition);
                // update timer progress again
                mHandler.postDelayed(mUpdateTimeTask, 50);
            } else {
//               bar1.setProgress(0);
//                bar2.setProgress(0);
                int totalDuration = (int) mMediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
                // forward or backward to certain seconds
                mMediaPlayer.seekTo(currentPosition);

                //get the video current image for continuty
                backgroundSnap = videoView.getBitmap();

                //set video current fram to imageview
                if (backgroundSnap != null) {
                    framImageView.setBackgroundDrawable(new BitmapDrawable(backgroundSnap));
                }
            }
        }


        @Override
        protected void onDestroy () {
            super.onDestroy();
            // timer.cancel();
            if (mMediaPlayer != null) {
                // Make sure we stop video and release resources when activity is destroyed.
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            if (mHandler != null) {
                //Make sure handler remove when activity destroyed.
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

        }

    @Override
    public void onSwipe(int direction) {

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
showHideBar();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
showHideBar();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                mVibrator.vibrate(50);
                zoomout();

                break;
            case SimpleGestureFilter.SWIPE_UP:
                mVibrator.vibrate(50);
                zoomin();

                break;
        }

    }

    @Override
    public void onDoubleTap() {
showHideBar();
    }

    @Override
    public void onSingleTapUp() {
       // showHideBar();
pausePlay();
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    void pausePlay() {
        {

            if (mMediaPlayer != null && !track) {

                if (mMediaPlayer.isPlaying()) {
                    playBtn.setImageResource(R.drawable.play_btn);
                    mMediaPlayer.pause();

                } else {
                    playBtn.setImageResource(R.drawable.pause_btn);
                    mMediaPlayer.start();

                }
            }
        }
    }

    void zoomin() {
        if (zoomer >= -100)
            zoomer -= 10;
    }

    void zoomout() {
        zoomer += 10;
    }
    
//
//    public void zoomout() {
//        {
//            // decrease size
//            if (scale > 3) {
//                scale = 3;
//            } else {
//                scale = scale + 0.1f;
//                if (scale > 3) {
//                    scale = 3;
//                }
//            }
//        }
//    }
//
//    public void zoomin() {
//        {
//            //increase video size
//            if (scale < 2) {
//                scale = 2;
//            } else {
//                scale = scale - 0.1f;
//                if (scale < 2) {
//                    scale = 2;
//                }
//            }
//        }
//    }

    void showHideBar() {
        if (!track) {
        System.out.println("----------------"+menuBar.getVisibility());
        if (menuBar.getVisibility()==View.VISIBLE) {
            menuBar.setVisibility(View.GONE);
//            menuOpen = false;
        } else {
            menuBar.setVisibility(View.VISIBLE);
//            menuOpen = true;
        }}
    }

    private void setFrame() {
//        frame = (RelativeLayout) findViewById(R.ids.texturelayout);
//        frame2 = (RelativeLayout) findViewById(R.ids.imagelayout);
        int border = pref.getInt("border", Settings.borderDefault);

        image_layout.setBackgroundResource(border);
        texture_Layout.setBackgroundResource(border);


        //   System.out.println(border);
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
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }



    public void zoom() {
        int zoomer = pref.getInt("zoomer", 0);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(zoomer, zoomer, zoomer, zoomer);
//        liner.setLayoutParams(lp);
        liner.setPadding(zoomer, zoomer, zoomer, zoomer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        zoomer = pref.getInt("zoomer", 30);

        mTracker.setScreenName("Video2D");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (mMediaPlayer!=null)
        mMediaPlayer.start();



        zoom();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer!=null) {
            mMediaPlayer.pause();
        }

    }

    @Override
    public void onBackPressed() {


        super.onBackPressed();
    }




    @Override
    protected void onStart() {

        super.onStart();
    }



    boolean isFirstToday() {

        if (!MainStarterActivity.adFrequency) {
        final DateTime dt = new DateTime();
        final long time = dt.getDayOfMonth();
        //    System.out.println(time);
        boolean istime=false;

        if (time <30 && pref.getLong("oncetoday", 0)>=30 ) {
            istime = true;
        }
        else if (time>pref.getLong("oncetoday", 0) || pref.getLong("oncetoday", 0)==0  || pref.getLong("oncetoday", 0)>31) {
            istime=true;
        }
      //  System.out.println("Will play "+istime);
        editor.putLong("oncetoday", time);
        editor.commit();


        return istime;}
        else {
            return true;
        }

    }
}

