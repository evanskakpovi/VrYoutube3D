package com.ekm.youtubevr3dvideosprod;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import com.github.paolorotolo.appintro.AppIntro;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.yunju.vr360videoplayer.SimpleVrVideoActivity;
import com.yunju.vr360videoplayer.VrVideoActivity;

import java.util.ArrayList;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import tools.SampleSlide;

public class intro extends AppIntro {
    SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private boolean chosen;
    private Tracker mTracker;

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(SampleSlide.newInstance(R.layout.slidetwo));
        addSlide(SampleSlide.newInstance(R.layout.slide4));
        addSlide(SampleSlide.newInstance(R.layout.slideone));
        addSlide(SampleSlide.newInstance(R.layout.slide5));
        addSlide(SampleSlide.newInstance(R.layout.slidethree));

        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        editor = pref.edit();
        editor.putInt("zoomer", 40);
        editor.putBoolean("ad", true);
//        editor.putLong("vrlooptime", 999999);
        editor.commit();

        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
//        addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));

        // OPTIONAL METHODS

// Override bar/separator color.
        setBarColor(Color.parseColor("#d9b38c"));
        setSeparatorColor(Color.parseColor("#d9b38c"));
        // Hide Skip/Done button.
        showSkipButton(true);

        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

//        mTracker.send(new HitBuilders.EventBuilder()
//                .setCategory("FruitLoop")
//                .setAction("Running AD")
//                .setLabel("2D")
//                .setValue(1)
//                .build());
    }

    @Override
    public void onSkipPressed() {
        Intent id = new Intent(intro.this,
                    MainActivity.class);
            id.putExtra(MainStarterActivity.vidId, "ff693IW6O4w");
            startActivity(id);
        finish();
      //  play360();
    }

    @Override
    public void onDonePressed() {
        Intent id = new Intent(intro.this,
                MainActivity.class);
//        ids.putExtra(MainStarterActivity.vidId, "_lau2wHRFxs");
        id.putExtra(MainStarterActivity.vidId, "ff693IW6O4w");
        startActivity(id);
        finish();
      ///  play360();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }

    void play360() {
        String youtubeLink = "http://youtube.com/watch?v=" +"dKj4PDldebc";
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
                        Intent intent = new Intent(intro.this, VrVideoActivity.class);
//                        intent.putExtra(MainStarterActivity.vidId3, "notnull");
//                                intent.putExtra(SimpleVrVideoActivity.TAG_VIDEO_URL, MainStarterActivity.strVideoDownloadPath);
                        intent.putExtra(SimpleVrVideoActivity.TAG_VIDEO_URL, downloadUrl);
                        startActivity(intent);

                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Intro")
                                .setAction("VID")
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

    }
}
