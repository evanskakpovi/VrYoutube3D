/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ekm.youtubevr3dvideosprod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.AccountPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.data;

/**
 * A Cardboard sample application.
 */
public class Floating2D3DStarted extends CardboardActivity implements CardboardView.StereoRenderer {

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.3f;

    private static final float YAW_LIMIT = 0.150f;
    private static final float PITCH_LIMIT = 0.150f;

    private static final int COORDS_PER_VERTEX = 3;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 2.0f, 0.0f, 1.0f};

    private final float[] lightPosInEyeSpace = new float[4];

    private FloatBuffer floorVertices;
    private FloatBuffer floorColors;
    private FloatBuffer floorNormals;


    private int floorProgram;

    private int floorPositionParam;
    private int floorNormalParam;
    private int floorColorParam;
    private int floorModelParam;
    private int floorModelViewParam;
    private int floorModelViewProjectionParam;
    private int floorLightPosParam;

    private float[] camera;
    private float[] view;
    private float[] headView;
    private float[] modelViewProjection;
    private float[] modelView;
    private float[] modelFloor;

    SharedPreferences.Editor editor;
    //  private Billboard[] floatingImages;
    ArrayList<BillboardURL> floatingImages;
    ArrayList<BillboardURL> floatingImagesBorder;
    private int score = 0;
    private float objectDistance = 12f;
    private float floorDepth = 20f;

    private Vibrator vibrator;
    private CardboardOverlayView overlayView;

    String url;
    final int maxInt = stat.max;
    final String max = "maxResults=" + maxInt;
    final String searchUrl = "https://www.googleapis.com/youtube/v3/search?";
    final String channelsUrl = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true";
    final String playlistItem = "https://www.googleapis.com/youtube/v3/playlistItems?";
    final String and = "&";
    final String term = "q=";
    final String iDplay = "ids=";
    static String MyAcessTokenData = "access_token";
    final String key = "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
    final String part = "part=snippet";
    final String channelId = "channelId=";
    final String playlistId = "playlistId=";
    final String pageToken = "pageToken=";
    final String extras = "videoDimension=3d&type=video,channel,playlist";


    private boolean scroll;
    public ArrayList<String> pages;


    ArrayList<String> ids;
    ArrayList<Integer> idsInt;
    String accountName;
    private SharedPreferences pref;

    Boolean isSafe;

    private static final int ACCOUNT_PICKER = 25452;
    private static final int USER_RECOVERABLE_AUTH = 5656516;
    private float colordefault = 0.3f;
    private Tracker mTracker;


    private void getAccountNames() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false, null, null, null, null);
        startActivityForResult(intent, ACCOUNT_PICKER);

    }

    @Override
    protected void onResume() {

        isSafe = true;

//        mTracker.setScreenName("Immersive search");
//        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // var.mdp = new MediaPlayer();
        if (pref.getBoolean(var.run1[0], true)) {
            //  new var.voice().execute(getString(R.string.hello));
            editor.putBoolean(var.run1[0], false);
            editor.commit();
        }
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        //var.mdp.release();
    }

    @Override
    protected void onDestroy() {
        isSafe = false;
        super.onDestroy();
    }

    ArrayList<BillboardURL> waitingImages;


    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);
//        Speak.setClientId("flipit");
//        Speak.setClientSecret("9czxZuzgpRDLM7DryIuxFWlivlsreAlG+Jqoc+dvltA=");
//
//
//        imageLoader = ImageLoader.getInstance();
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
//                // You can pass your own memory cache implementation
//
//                .build();
//// Initialize ImageLoader with created configuration. Do it once.
//        imageLoader.init(config);
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (pref.getBoolean("loggedin", false)) {
            getAccountNames();
        }

        pages = new ArrayList<String>();
        pages.add("");
        floatingImages = new ArrayList<BillboardURL>();
        waitingImages = new ArrayList<>();
        floatingImagesBorder = new ArrayList<BillboardURL>();

        var v = new var();
//      for (int i=0; i<dataCopy.size(); i++)
//          //System.out.println(dataCopy.get(i).myBitmap);
        for (int i = 0; i < 6; i++) {
            waitingImages.add(new BillboardURL(v.waitcoords[i][0], v.waitcoords[i][1], v.waitcoords[i][2], 0.0f, 0.0f, null, R.drawable.wait));
        }

        floatingImages.add(new BillboardURL(-3, 0, -objectDistance, 2f, 2f, null, R.drawable.twod));
        floatingImages.add(new BillboardURL(3, 0, -objectDistance, 2f, 2f, null, R.drawable.threed));
//      floatingImages.add( new BillboardURL(12, 0, 0, 2f, 2f, null,R.drawable.wait));

//        floatingImagesBorder.add(new BillboardURL(-3, 0, -objectDistance, 2.1f, 2.1f, null, R.drawable.bordo11));
//        floatingImagesBorder.add(new BillboardURL(3, 0, -objectDistance, 2.1f, 2.4f, null, R.drawable.bordo));

//   floatingImages.add( new Billboard(5, 0, -objectDistance, 2f, 2f, R.drawable.my_texture));

        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];
        modelFloor = new float[16];
        headView = new float[16];
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Bitmap imagenAndroid = BitmapFactory.decodeResource(getResources(), R.drawable.twod);
        overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        overlayView.show3DToast(getString(R.string.look));
//        overlayView.show3DPic(imagenAndroid);
        editor = pref.edit();

        // Obtain the shared Tracker instance.
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    public void onRendererShutdown() {
        Log.i(Utils.TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(Utils.TAG, "onSurfaceChanged");
    }

    /**
     * Creates the buffers we use to store information about the 3D world.
     * <p/>
     * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
     * Hence we use ByteBuffers.
     *
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(Utils.TAG, "onSurfaceCreated");
        BillboardURL.whenSurfaceCreated(getApplicationContext());//NEW: Static initialization
        for (int i = 0; i < floatingImages.size(); i++) {
            floatingImages.get(i).init(getApplicationContext());
//            floatingImagesBorder.get(i).init(getApplicationContext());
        }
        for (int i = 0; i < 6; i++) {
            waitingImages.get(i).init(getApplicationContext());
        }

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Dark background so text shows up well.


        // make a floor
        ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COORDS.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        floorVertices = bbFloorVertices.asFloatBuffer();
        floorVertices.put(WorldLayoutData.FLOOR_COORDS);
        floorVertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_NORMALS.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        floorNormals = bbFloorNormals.asFloatBuffer();
        floorNormals.put(WorldLayoutData.FLOOR_NORMALS);
        floorNormals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COLORS.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        floorColors = bbFloorColors.asFloatBuffer();
        floorColors.put(WorldLayoutData.FLOOR_COLORS);
        floorColors.position(0);

        int vertexShader = Utils.loadGLShader(getBaseContext(), GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int gridShader = Utils.loadGLShader(getBaseContext(), GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        int passthroughShader = Utils.loadGLShader(getBaseContext(), GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);


        floorProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(floorProgram, vertexShader);
        GLES20.glAttachShader(floorProgram, gridShader);
        GLES20.glLinkProgram(floorProgram);
        GLES20.glUseProgram(floorProgram);

        Utils.checkGLError("Floor program");

        floorModelParam = GLES20.glGetUniformLocation(floorProgram, "u_Model");
        floorModelViewParam = GLES20.glGetUniformLocation(floorProgram, "u_MVMatrix");
        floorModelViewProjectionParam = GLES20.glGetUniformLocation(floorProgram, "u_MVP");
        floorLightPosParam = GLES20.glGetUniformLocation(floorProgram, "u_LightPos");

        floorPositionParam = GLES20.glGetAttribLocation(floorProgram, "a_Position");
        floorNormalParam = GLES20.glGetAttribLocation(floorProgram, "a_Normal");
        floorColorParam = GLES20.glGetAttribLocation(floorProgram, "a_Color");

        GLES20.glEnableVertexAttribArray(floorPositionParam);
        GLES20.glEnableVertexAttribArray(floorNormalParam);
        GLES20.glEnableVertexAttribArray(floorColorParam);

        Utils.checkGLError("Floor program params");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        Matrix.setIdentityM(modelFloor, 0);
        Matrix.translateM(modelFloor, 0, 0, -floorDepth, 0); // Floor appears below user.

        Utils.checkGLError("onSurfaceCreated");
    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

        headTransform.getHeadView(headView, 0);

        Utils.checkGLError("onReadyToDraw");
    }


    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */
    @Override
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Utils.checkGLError("colorParam");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        //It's for debugging on device without gyroscope
        //Matrix.rotateM(view, 0, (System.currentTimeMillis()%3600000)*0.01f , 0, 1, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, modelFloor, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0,
                modelView, 0);
        drawFloor();

        for (int i = 0; i < floatingImages.size(); i++) {
            floatingImages.get(i).setHighlighting(floatingImages.get(i).isLookingAtObject(headView, PITCH_LIMIT, YAW_LIMIT));
            floatingImages.get(i).draw(view, perspective, lightPosInEyeSpace, colordefault);

//        floatingImages.get(i).setHighlighting(floatingImages.get(i).isLookingAtObject(headView, PITCH_LIMIT, YAW_LIMIT));
            //floatingImagesBorder.get(i).draw(view, perspective, lightPosInEyeSpace);
        }
        for (int i = 0; i < 6; i++) {
            waitingImages.get(i).draw(view, perspective, lightPosInEyeSpace, var.color);
        }


        if (pref.getBoolean("autochoose", false)) {
            for (int i = 0; i < floatingImages.size(); i++) {
                if (floatingImages.get(i).isLookingAtObject(headView, PITCH_LIMIT, YAW_LIMIT)) {
//                  //System.out.println("york");
                    final int x = i;
                    colordefault += 0.001f;
                    if (!isLooking && isLookingDigit != i) {
//                      //System.out.println("opening");

                        t.schedule(new TimerTask() {

                            @Override
                            public void run() {

                                if (isLookingDigit == x) {
//                                  //System.out.println("YES SIR+ ");
                                    long p[] = {0, 50, 500, 50, 500, 50};
                                    vibrator.vibrate(p, -1);
                                }

                            }
                        }, 2000);


                        t.schedule(new TimerTask() {

                            @Override
                            public void run() {

                                if (isLookingDigit == x) {
//                              //System.out.println("YES SIR+ ");
                                    //     colordefault = 0.6f;
                                    vibrator.vibrate(300);
                                    ActionToPerform();
                                }

                            }
                        }, 4500);
                        isLooking = true;
                        isLookingDigit = i;
                    } else if (isLooking && isLookingDigit != i) {
                        isLooking = false;
                        t.cancel();
                        t = new Timer();
                        vibrator.cancel();
                        //System.out.println("Resetting");
                        isLookingDigit = -1;
                        colordefault = 0.3f;
                    }
                } else if (!floatingImages.get(i).isLookingAtObject(headView, PITCH_LIMIT, YAW_LIMIT) && isLooking && isLookingDigit == i) {
                    isLookingDigit = -1;
                    //System.out.println("salty");
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onFinishFrame(Viewport viewport) {
    }

    //     public static ImageLoader imageLoader;
    OkHttpClient client = new OkHttpClient();
    String getJson(String url) throws IOException {
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    private class LoadPlayList extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            //  myBar.setVisibility(View.VISIBLE);
            scroll = false;
            pages = new ArrayList<String>();
            pages.add("");
            stat.myData = new ArrayList<data>();
            stat.myDataAll = new ArrayList<data>();
            ids = new ArrayList<String>();
            idsInt = new ArrayList<Integer>();
            stat.myData = new ArrayList<data>();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {


            String getData = null;
            try {
                getData = getJson(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // getting JSON Object
            JSONObject mObj;

            try {
                mObj = new JSONObject(getData);
                if (!mObj.isNull("nextPageToken")) {
                    pages.add(mObj.getString("nextPageToken"));
                    scroll = true;
                } else {
                    scroll = false;
                }
                //    ////System.out.println(pages.get(pages.size()-1)+"   Step+2   "+mObj.getString("nextPageToken"));
                JSONArray mainArray = mObj.getJSONArray("items");


                JSONObject opage = mObj.getJSONObject("pageInfo");

                int max = Math.min(opage.getInt("totalResults"), stat.max);
//                stat.myData.add(null);
//                stat.myData.add(null);
                for (int i = 0; i < max - 1; i++) {

                    JSONObject mainObject0 = mainArray.getJSONObject(i);
                    JSONObject sousObject1 = mainObject0.getJSONObject("snippet");
                    String title = sousObject1.getString("title");
                    String channeltitle = sousObject1.getString("channelTitle");
                    JSONObject sousObject2 = sousObject1.getJSONObject("thumbnails");
                    JSONObject sousObject3 = sousObject2.getJSONObject("medium");
                    String imageUrl = sousObject3.getString("url");
//                    imageUrl = "/storage/emulated/0/DCIM/Camera/gren.png";
                    //print(imageUrl);


                    JSONObject idOBject1 = sousObject1.getJSONObject("resourceId");
                    String kind = idOBject1.getString("kind");

                    if (kind.equals("youtube#video")) {
                        String id = idOBject1.getString("videoId");
                        idsInt.add(0);
                        ids.add(id);
                        stat.myData.add(new data(imageUrl, title, channeltitle, 0, Floating2D3DStarted.this, id));
                    } else if (kind.equals("youtube#channel")) {
                        String idchan = idOBject1.getString("channelId");
                        ids.add(idchan);
                        idsInt.add(1);
                        ////System.out.println(idchan);
                        stat.myData.add(new data(imageUrl, title, channeltitle, 0, Floating2D3DStarted.this, idchan));
                    } else if (kind.equals("youtube#playlist")) {
//                        String idchan = sousObject1.getString("channelId");
                        String idchan = idOBject1.getString("playlistId");
                        ////System.out.println("FIrst One Pre: "+idchan);
                        ids.add(idchan);
                        idsInt.add(1);
                        stat.myData.add(new data(imageUrl, title, channeltitle, 1, Floating2D3DStarted.this, idchan));
                    } else {
                        continue;
                    }


                }

            } catch (JSONException e) {
                if (pref.getBoolean("loggedin", false)) {
                    getAccountNames();
                } else {
                    MyAcessTokenData = "nono";
                }
                e.printStackTrace();
            } catch (NullPointerException e) {

            }catch (RuntimeException w) {

            }

            return params[1];
        }

        @Override
        protected void onPostExecute(String result) {

            int inc = 6;
            if (isSafe) {
                stat.myDataAll.addAll(stat.myData);
                //System.out.println(stat.myDataAll.size());
//                for (int i=0; i<stat.myDataAll.size(); i++)
//                    //System.out.println(stat.myDataAll.get(i).myBitmap);

                if (result.equals("1")) {
//                    page++;
                    Intent id = new Intent(Floating2D3DStarted.this, Floating3DVideos.class);
                    id.putExtra("page", 1);
                    id.putExtra("pages", pages);
                    startActivity(id);
                } else if (result.equals("2")) {
                    Intent id = new Intent(Floating2D3DStarted.this, Floating2DVideos.class);
                    id.putExtra("page", 1);
                    id.putExtra("pages", pages);
                    startActivity(id);
                }
                finish();


            }

            super.onPostExecute(result);
        }

    }


    /**
     * Draw the floor.
     * <p/>
     * <p>This feeds in data for the floor into the shader. Note that this doesn't feed in data about
     * position of the light, so if we rewrite our code to draw the floor first, the lighting might
     * look strange.
     */
    public void drawFloor() {
        GLES20.glUseProgram(floorProgram);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(floorLightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(floorModelParam, 1, false, modelFloor, 0);
        GLES20.glUniformMatrix4fv(floorModelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(floorModelViewProjectionParam, 1, false,
                modelViewProjection, 0);

        GLES20.glVertexAttribPointer(floorPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, floorVertices);
        GLES20.glVertexAttribPointer(floorNormalParam, 3, GLES20.GL_FLOAT, false, 0,
                floorNormals);
        GLES20.glVertexAttribPointer(floorColorParam, 4, GLES20.GL_FLOAT, false, 0, floorColors);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);


        Utils.checkGLError("drawing floor");

    }

    protected static int isLookingDigit = -1;
    boolean isLooking = false;
    boolean win = false;
    Timer t = new Timer();

    private void ActionToPerform() {
        {
            Log.i(Utils.TAG, "onCardboardTrigger");
            boolean win = false;

            for (int i = 0; i < floatingImages.size(); i++) {
                if (floatingImages.get(i).isLookingAtObject(headView, PITCH_LIMIT, YAW_LIMIT)) {
                    switch (i) {
                        case 1:
                            String idForList = "PLcQCsePRL4jd6WMU_MP5xUJhcvOW8pqfX";
                            String url = playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + idForList + and + key + and + "mine=true" + and + pageToken + pages.get(0);
                            //System.out.println(url);
                            new LoadPlayList().execute(url, "1");
                            //System.out.println("2d");
                            win = true;
                            break;

                        case 0:
                            String idForList2 = "PLcQCsePRL4jcLU_mhIsejZ9qaPumkbgUv";
                            String url2 = playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + idForList2 + and + key + and + "mine=true" + and + pageToken + pages.get(0);
                            new LoadPlayList().execute(url2, "2");
                            //System.out.println("3d");
                            win = true;
                            break;
                    }
                }
            }
            if (!win) {
                overlayView.show3DToast(getString(R.string.lookand));

            } else if (win) {
                for (int i = 0; i < floatingImages.size(); i++) {
                    floatingImages.get(i).hide();
                }
                for (int i = 0; i < 6; i++) {
                    waitingImages.get(i).showWait();
                }
            }
            // Always give user feedback.
            vibrator.vibrate(50);
        }
    }

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        ActionToPerform();
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}