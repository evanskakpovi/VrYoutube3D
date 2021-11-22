package com.ekm.youtubevr3dvideosprod;

import android.media.MediaPlayer;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by work on 3/11/15.
 */
public class var {

    public static String[] run1 = {
            "run1","run3d","run2d"
    };
    float inc = 8;
    final float start = -3;
    private float od = 12f;
    private float ods = 20f;
    public var() {

    }
    public var(float inc) {
        this.inc = inc;
    }
    public float coords[][] = {

            {-od+4,0,start},
            {od-4,0,start},

//            {-od,0,start},
//            {od,0,start},

            {start,0,-od},
            {start+inc,0,-od},
            {start,-5,-od},
            {start+inc,-5,-od},

            {start,5,-od},
            {start+inc,5,-od},

            {od,5,start},
            {-od,5,start},
            {od,-5,start},
            {-od,-5,start},

            {od,0,start+inc},
            {-od,0,start+inc},
            {od,5,start+inc},
            {-od,5,start+inc},
            {od,-5,start+inc},
            {-od,-5,start+inc},

            {start,0,od},
            {start+inc,0,od},
            {start,5,od},
            {start+inc,5,od},
            {start,-5,od},
            {start+inc,-5,od},

    };

    public static final String autochoose="autochoose";
public static float color = 0.3f;
    public float waitcoords[][] = {

            {-ods,0,0},
            {ods,0,0},

            {-ods,0,0},
            {ods,0,0},

            {0,0,-ods},
//            {0+inc,0,-od},





//            {od,0,start+inc},
//            {-od,0,start+inc},


            {0,0,ods},
//            {start+inc,0,od},


    };
    static MediaPlayer mdp;

    /* Play the sound */
    public static void speak(InputStream in) throws IllegalArgumentException,
            IllegalStateException, IOException {
        try {

            File root = new File(Environment.getExternalStorageDirectory(),
                    "FlipItAud");
            if (!root.exists()) {
                root.mkdirs();
            }

             mdp = new MediaPlayer();
            File f = new File(root, "flptmid");
            OutputStream out = new FileOutputStream(f);

            byte buf[] = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();

            FileInputStream fis;

            fis = new FileInputStream(f);

            mdp.setDataSource(fis.getFD());
            fis.close();
            mdp.prepare();

            // mdp.setOnCompletionListener(new OnCompletionListener() {
            //
            // @Override
            // public void onCompletion(MediaPlayer mdp) {
            // // TODO Auto-generated method stub
            // mdp.release();
            // }
            //
            // });
            mdp.start();

        } catch (FileNotFoundException e) {
            // //System.out.println("FILE NOT FOUND");
            e.printStackTrace();
        }
    }
}
