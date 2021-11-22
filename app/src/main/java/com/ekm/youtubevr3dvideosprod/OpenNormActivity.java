package com.ekm.youtubevr3dvideosprod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardDeviceParams;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;


public class OpenNormActivity extends CardboardActivity {

    long theme;
    Vibrator mVibrator;
    ArrayList<Bitmap> vidThumbnailId;
    ArrayList<String> vidPath;
    boolean returnto = false;
    ZoomControls zoom;
    TextView zoo;
    static int location;
    static final String vidId = "ekmvideoid";
    static final String vidId2 = "ekmvideoid2";
    YouTubeThumbnailView youTubeThumbnailView;
    SharedPreferences pref;
    YouTubeThumbnailLoader youTubeThumbnailLoader;

    int zoomint = 0;
    SharedPreferences.Editor editor;
    private Tracker mTracker;
    private boolean chosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
//        MainStarterActivity.videos = new ArrayList<String>();
//        MainStarterActivity.videonorm = new ArrayList<String>();


        // Get the intent that started this activity
        Intent intent = getIntent();
        Uri data = intent.getData();

        // Figure out what to do based on the intent type
       // System.out.println(intent.getDataString()+"");
        playNormal(intent.getDataString()+"");

        // txt.setText(intent.getDataString()+"");

    }

    public void playNormal(String url) {

        mVibrator.vibrate(50);
        if (!url.isEmpty()) {
            String org = url;
            String texid = StringUtils.substringAfter(org, "?v=");
            if (texid.isEmpty()) {
                if (org.length() >= 11)
                    texid = StringUtils.substring(org, org.length() - 11, org.length());
                else
                    texid = org;
//				System.out.println(texid);
            }
			getURI(texid);
        } else {
            Intent id = new Intent(this, MainStarterActivity.class);
            startActivity(id);
        }
    }



    void getURI(String id) {
        if (!chosen) {
            chosen = true;
            String youtubeLink = "http://youtube.com/watch?v=" + id;
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
                        if (key.contains(MainStarterActivity.itag)) {
                            downloadUrl = ytFiles.get(MainStarterActivity.itag).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itagUR")
                                    .setAction("chosen")
                                    .setLabel("22")
                                    .build());
                        } else if (key.contains(18)) {
                            downloadUrl = ytFiles.get(18).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itagUR")
                                    .setAction("defaulted to")
                                    .setLabel("18")
                                    .build());
                        } else if (key.contains(22)) {
                            downloadUrl = ytFiles.get(22).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itagUR")
                                    .setAction("defaulted to")
                                    .setLabel("22")
                                    .build());
                        } else if (key.contains(36)) {
                            downloadUrl = ytFiles.get(36).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itagUR")
                                    .setAction("defaulted to")
                                    .setLabel("36")
                                    .build());
                        } else if (key.contains(17)) {
                            downloadUrl = ytFiles.get(17).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itagUR")
                                    .setAction("defaulted to")
                                    .setLabel("17")
                                    .build());
                        } else {
                            chosen = false;

                        }
                        System.out.println("Url------------>>> :" + downloadUrl);
                        if (downloadUrl != null && !downloadUrl.isEmpty()) {

                            Intent intent = new Intent(OpenNormActivity.this, VideoPlayerActivity.class);
                            intent.putExtra(MainStarterActivity.vidId, downloadUrl);
                            startActivity(intent);
                        } else {
                            System.out.println("Download urL is null");
                            chosen = false;
                            Intent id = new Intent(OpenNormActivity.this, MainStarterActivity.class);
                            startActivity(id);
                        }
                        finish();
                    } else {
                        System.out.println("YTFILES is null");
                        chosen = false;
                        Intent id = new Intent(OpenNormActivity.this, MainStarterActivity.class);
                        startActivity(id);
                        finish();
                        // BACKUP Plan
                    }
                }
            };
            ytEx.setIncludeWebM(false);
            ytEx.execute(youtubeLink);

        }
    }


    @Override
    public void onCardboardTrigger() {
        mVibrator.vibrate(50);
//        Intent ids = new Intent(OpenNormActivity.this, MainActivity.class);
//        ids.putExtra(vidId, SelectActivity.videos.get(0));
//        startActivity(ids);
        super.onCardboardTrigger();
    }

    @Override
    public void onInsertedIntoCardboard(CardboardDeviceParams deviceParams) {
        // TODO Auto-generated method stub
        // setContentView(R.layout.activity_select2);
        // super.onInsertedIntoCardboard(deviceParams);
    }

    @Override
    public void onRemovedFromCardboard() {
        // TODO Auto-generated method stub
        super.onRemovedFromCardboard();
    }
}
