package com.yunju.vr360videoplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ekm.youtubevr3dvideosprod.R;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.FieldOfView;

import org.rajawali3d.cameras.Camera;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.math.Matrix4;
import org.rajawali3d.math.Quaternion;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.Sphere;
import org.rajawali3d.vr.renderer.RajawaliVRRenderer;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by WORYA on 8/7/2016.
 */
public class VR360Renderer extends RajawaliVRRenderer {
    protected Matrix4 eyeMatrix;
    protected Quaternion eyeOrientation;
    protected Vector3 eyePosition;
    public MediaPlayer mediaPlayer;
    private  StreamingTexture videoTexture;
    private Context mContext;
    private String mStrVideoPath;
    public VR360Renderer(Context context) {
        super(context);
        mContext = context;
    }
    @Override
    public void initScene() {

        // setup sphere
        Sphere sphere = new Sphere(1, 100, 100);
        sphere.setPosition(0, 0, 0);
        // invert the sphere normals
        // factor "1" is two small and result in rendering glitches
        sphere.setScaleX(11.15);
        sphere.setScaleY(11.15);
        sphere.setScaleZ(-11.15);
        // eyeMatrix
        eyeMatrix  = new Matrix4();
        eyeOrientation = new Quaternion();
        eyePosition = new Vector3();
        //initiate MediaPlayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false);
        try {
            Uri videoUrl = Uri.parse(mStrVideoPath);
            mediaPlayer.setDataSource(getContext(), videoUrl);
            mediaPlayer.prepareAsync();    //prepare the player (asynchronous)
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start(); //start the player only when it is prepared
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(mContext != null)
                        ((Activity) mContext).finish();

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
 catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {

        }

        // create texture from media player video
        videoTexture = new StreamingTexture("video", mediaPlayer);

        // set material with video texture
        Material material = new Material();
        material.setColorInfluence(0f);
        try {
            material.addTexture(videoTexture);
        } catch (ATexture.TextureException e) {
            throw new RuntimeException(e);
        }
        sphere.setMaterial(material);

        // add sphere to scene
        getCurrentScene().addChild(sphere);

        super.initScene();
    }
    @Override
    public void onRender(long elapsedRealTime, double deltaTime) {
        super.onRender(elapsedRealTime, deltaTime);

        if (videoTexture != null) {
            // update texture from video content
            videoTexture.update();
        }
    }
    @Override
    public void onDrawEye(Eye eye) {
        // Rajawali camera
        Camera currentCamera = getCurrentCamera();

        // cardboard field of view
        FieldOfView fov = eye.getFov();

        // update Rajawali camera from cardboard sdk
        currentCamera.updatePerspective(fov.getLeft(), fov.getRight(), fov.getBottom(), fov.getTop());
        eyeMatrix.setAll(eye.getEyeView());
        // orientation
        eyeOrientation.fromMatrix(eyeMatrix);
        currentCamera.setOrientation(eyeOrientation);
        // position
        eyePosition = eyeMatrix.getTranslation().inverse();
        currentCamera.setPosition(eyePosition);

        // render with Rajawali
        super.onRenderFrame(null);
    }
    @Override
    public void onSurfaceChanged(int width, int height) {
        // tell Rajawali that cardboard sdk detected a size change
        try {
        super.onRenderSurfaceSizeChanged(null, width, height); }
        catch (IllegalStateException e) {

        }
    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        // pass opengl config to Rajawali
        super.onRenderSurfaceCreated(eglConfig, null, -1, -1);
    }

    @Override
    public void onRenderSurfaceDestroyed(SurfaceTexture surface) {
        super.onRenderSurfaceDestroyed(surface);
        mediaPlayer.release();
    }
    public void pauseVideo() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    public void resumeVideo() {
        if(mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    public void setVideoPath(String strPath){
        mStrVideoPath = strPath;
    }

    void output(String data) {
        new MaterialDialog.Builder(getContext())
                .title("Something went wrong")
                .titleColorRes(R.color.fab_color_emerald)
                .content(data)
                .positiveText(R.string.ok)
                .backgroundColor(Color.parseColor("#ffffff"))
                .contentColor(Color.parseColor("#000000"))
                .show();
    }
}