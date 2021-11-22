package com.yunju.vr360videoplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Window;
import android.view.WindowManager;

import com.ekm.youtubevr3dvideosprod.Floating2D3DStarted;
import com.ekm.youtubevr3dvideosprod.MainActivity;
import com.ekm.youtubevr3dvideosprod.MainStarterActivity;
import com.ekm.youtubevr3dvideosprod.VideoPlayerActivity;

import org.joda.time.DateTime;
import org.rajawali3d.util.RajLog;
import org.rajawali3d.vr.RajawaliVRActivity;

import java.io.File;
import java.util.ArrayList;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class SimpleVrVideoActivity extends RajawaliVRActivity {
  private static final String TAG = SimpleVrVideoActivity.class.getSimpleName();
  public static final String TAG_VIDEO_URL = "tag_video_url";
  private String strVideoDownloadPath, vidid, vidid2;
  VR360Renderer mRenderer;
  long time;
  private String vidid3;
  private String vidid4;
  private boolean chosen, vidbool;

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
      vidid = getIntent().getStringExtra(MainStarterActivity.vidId);
      vidid2 = getIntent().getStringExtra(MainStarterActivity.vidId2);
      vidid3 = getIntent().getStringExtra(MainStarterActivity.vidId3);
      vidid3 = getIntent().getStringExtra(MainStarterActivity.vidId4);
      vidbool = getIntent().getBooleanExtra(MainStarterActivity.vidIdbool, false);

    mRenderer = new VR360Renderer(this);
    mRenderer.setVideoPath(strVideoDownloadPath);
    setRenderer(mRenderer);
    setConvertTapIntoTrigger(true);
    DateTime dt = new DateTime();
   time = dt.getMillis();

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
    super.onResume();
   // if(mRenderer != null)
      //mRenderer.resumeVideo();
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
      mRenderer = null;
    DateTime dt = new DateTime();
    long time2 = dt.getMillis()-time;

                                      if (vidid!=null)   {

                                        getURI(vidid, vidbool) ;
                                      }
    else if(vidid2!=null) {
                                        Intent intent = new Intent(SimpleVrVideoActivity.this, MainActivity.class);
                                        intent.putExtra(MainStarterActivity.vidId,vidid2);
//                                        intent.putExtra("lloop", time2);
                                        startActivity(intent);
                                      }
                                      else if(vidid3!=null) {
                                        Intent intent = new Intent(SimpleVrVideoActivity.this, Floating2D3DStarted.class);
//                                        intent.putExtra(MainStarterActivity.vidId,vidid2);
//                                        intent.putExtra("lloop", time2);
                                        startActivity(intent);
                                      }else if(vidid4!=null) {
                                        Intent intent = new Intent(SimpleVrVideoActivity.this, Floating2D3DStarted.class);
//                                        intent.putExtra(MainStarterActivity.vidId,vidid2);
//                                        intent.putExtra("lloop", time2);
                                        startActivity(intent);
                                      }

    //  deleteFile(strVideoDownloadPath);
  }


  public  boolean deleteFile(String strPath)
  {
    boolean retVal = false;
    if(strPath != null && !strPath.isEmpty()) {
      try {
        File f = new File(strPath);
        if (f.exists())
          f.delete();
        retVal = true;
      }catch (Exception e){

      }
    }
    return retVal;
  }

  @Override
  public void onBackPressed() {
return;
  //  super.onBackPressed();
  }

  void getURI(String id, final boolean is360) {
    if (!chosen) {
       String youtubeLink = "http://youtube.com/watch?v=" + id;
      System.out.println(youtubeLink+" ()()");
      chosen = true;

      YouTubeUriExtractor ytEx = new YouTubeUriExtractor(this) {
        @Override
        public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
          String downloadUrl = "";
          if (ytFiles != null) {
            ArrayList<Integer> key = new ArrayList<>();
            for (int i = 0; i < ytFiles.size(); i++) {
              key.add(ytFiles.keyAt(i));
              //   System.out.println(key.get(i));
            }
            if (key.contains(MainStarterActivity.itag)) {
              downloadUrl = ytFiles.get(MainStarterActivity.itag).getUrl();

            } else if (key.contains(18)) {
              downloadUrl = ytFiles.get(18).getUrl();

            } else if (key.contains(22)) {
              downloadUrl = ytFiles.get(22).getUrl();

            } else if (key.contains(36)) {
              downloadUrl = ytFiles.get(36).getUrl();

            } else if (key.contains(17)) {
              downloadUrl = ytFiles.get(17).getUrl();

            } else {
              chosen = false;
            }
            System.out.println("Url------------>>> :" + downloadUrl);
            if (downloadUrl != null && !downloadUrl.isEmpty()) {

//                            Intent intent = new Intent(SearchActivity.this, VideoPlayerActivity.class);
//                            intent.putExtra(MainStarterActivity.vidId, downloadUrl);
//                            startActivity(intent);



              {
                if (is360) {
                  play360(downloadUrl);
                }else {
                  Intent intent = new Intent(SimpleVrVideoActivity.this, VideoPlayerActivity.class);
                  intent.putExtra(MainStarterActivity.vidId, downloadUrl);
                  startActivity(intent);
                }}
            } else {
              //   System.out.println("Download urL is null");
              chosen = false;
            }

          } else {
            //  System.out.println("YTFILES is null");
            chosen = false;
            // BACKUP Plan
          }
        }
      };
      ytEx.setIncludeWebM(false);
      ytEx.execute(youtubeLink);

    }
  }

  private void play360(String url){
    Intent intent = new Intent(this, VrVideoActivity.class);
    intent.putExtra(VrVideoActivity.TAG_VIDEO_URL, url );
    startActivity(intent);

  }

}
