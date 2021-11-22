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
import android.media.AudioManager;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.data;

/**
 * A Cardboard sample application.
 */
public class Floating3DSearch extends CardboardActivity implements CardboardView.StereoRenderer {

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 100.0f;

    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.3f;

    private static final float YAW_LIMIT = 0.212f;
    private static final float PITCH_LIMIT = 0.212f;

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

    //  private Billboard[] floatingImages;
    ArrayList<BillboardURL> floatingImages;
    private int score = 0;
    private float objectDistance = 12f;
    private float floorDepth = 20f;


    private Vibrator vibrator;
    private CardboardOverlayView overlayView;


    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     */
    List<data> dataCopy;
    AudioManager amanager;
    int page;
    private String searchQuery;
    private Tracker mTracker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);
        Bundle extras = getIntent().getExtras();
        //Reset data holder
        stat.myData.clear();
        stat.myDataAll.clear();
        pages = new ArrayList<>();
        pages.add("");
        page = 0;
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        floatingImages = new ArrayList<BillboardURL>();
        amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());
        var v = new var();
        floatingImages.add(new BillboardURL(v.coords[2][0]+3, v.coords[2][1], v.coords[2][2], 2.5f, 2.5f, null, R.drawable.mic));
        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];
        modelFloor = new float[16];
        headView = new float[16];
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        overlayView.show3DToast("Select the mic, and speak your command after the tone!");
        speak(null);
        // Obtain the shamied Tracker instance.
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();
    }

    class listener implements RecognitionListener {

        public void onReadyForSpeech(Bundle params) {
//            Log.d(TAG, "onReadyForSpeech");

//            voice.setVisibility(View.VISIBLE);
        }

        public void onBeginningOfSpeech() {
//            Log.d(TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            //       Log.d(TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            //       Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
//            Log.d(TAG, "onEndofSpeech");

//            voice.setVisibility(View.GONE);
        }

        public void onError(int error) {
//            Log.d(TAG, "error " + error);
//            playit(null);
//            voice.setVisibility(View.GONE);
            if (pref.getBoolean(var.autochoose, false)) {
            speak(null);
        }}


        public void onResults(Bundle results) {

            ArrayList<String> textMatchList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


            if (!textMatchList.isEmpty()) {
                // If first Match contains the 'search' word
                // Then start web search.
                if (true) {

                    searchQuery = textMatchList.get(0);

                    String idForList="PLcQCsePRL4jd6WMU_MP5xUJhcvOW8pqfX";
                    String url= playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + idForList +and+key+and+"mine=true"+and+pageToken + pages.get(page);
                    try {
                        url = searchUrl + and + part + and + type+and+ pageToken + pages.get(0) + and + max + and + term + URLEncoder.encode(searchQuery+" yt3d", "UTF-8") + and + MyAcessTokenData + and+key;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //System.out.println(url);
                    new LoadVideos().execute(url, "1");

                }

            }
            //Result code for various error.
        }

        public void onPartialResults(Bundle partialResults) {

//            Log.d(TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {

//            Log.d(TAG, "onEvent " + eventType);
        }
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
//        drawFloor();

        for (int i = 0; i < floatingImages.size(); i++) {
         //   floatingImages.get(i).setHighlighting(floatingImages.get(i).isLookingAtObject(headView, PITCH_LIMIT, YAW_LIMIT));
            floatingImages.get(i).draw(view, perspective, lightPosInEyeSpace,var.color);
        }
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
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

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(Utils.TAG, "onCardboardTrigger");
        boolean win = false;

        if (floatingImages.get(0).isLookingAtObject(headView, PITCH_LIMIT, YAW_LIMIT)) {
           speak(null);
            win = true;
        }
//
        if (!win) {
            String idForList="PLcQCsePRL4jd6WMU_MP5xUJhcvOW8pqfX";
            String url= playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + idForList +and+key+and+"mine=true"+and+pageToken + pages.get(page);
            try {
                url = searchUrl + and + part + and + type+and+ pageToken + pages.get(0) + and + max + and + term + URLEncoder.encode("birds"+" yt3d", "UTF-8") + and + MyAcessTokenData + and+key;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //System.out.println(url);
            new LoadVideos().execute(url, "1");
//            finish();
        }
        // Always give user feedback.
        vibrator.vibrate(50);
    }
    @Override
    protected void onStart() {
        super.onStart();
     //   EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    protected void onResume() {
        mTracker.setScreenName("Immersive search 3D");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
    public void speak(View view) {
        //advert = true;
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//
//        // Specify the calling package to identify your application
//        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
//                .getPackage().getName());
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

        int noOfMatches = 5;
        // Specify how many results you want to receive. The results will be
        // sorted where the first result is the one with higher confidence.
        //    amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        // if// (isSafe) {
        // pauseit(null);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noOfMatches);
        sr.startListening(intent);
        //}//

    }

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private SpeechRecognizer sr;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            //If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                if (!textMatchList.isEmpty()) {


                }
                //Result code for various error.
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }





    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }   OkHttpClient client = new OkHttpClient();
    String getJson(String url) throws IOException {
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private class LoadVideos extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            //  myBar.setVisibility(View.VISIBLE);
            scroll = false;
//            pages = new ArrayList<String>();
//            pages.add("");
            myData = new ArrayList<data>();

            ids = new ArrayList<String>();
            idsInt = new ArrayList<Integer>();

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
                    //prescroll = true;
                } else {
                    scroll = false;
                }
                //    ////System.out.println(pages.get(pages.size()-1)+"   Step+2   "+mObj.getString("nextPageToken"));
                JSONArray mainArray = mObj.getJSONArray("items");


                JSONObject opage = mObj.getJSONObject("pageInfo");

                int max = Math.min(opage.getInt("totalResults"), stat.max);
                for (int i = 0; i < max; i++) {

                    JSONObject mainObject0 = mainArray.getJSONObject(i);
                    JSONObject sousObject1 = mainObject0.getJSONObject("snippet");
                    String title = sousObject1.getString("title");
                    String channeltitle = sousObject1.getString("channelTitle");
                    JSONObject sousObject2 = sousObject1.getJSONObject("thumbnails");
                    JSONObject sousObject3 = sousObject2.getJSONObject("medium");
                    String imageUrl = sousObject3.getString("url");
                    //print(imageUrl);


                    JSONObject idOBject1 = mainObject0.getJSONObject("ids");
                    String kind = idOBject1.getString("kind");

                    if (kind.equals("youtube#video")) {
                        String id = idOBject1.getString("videoId");
                        idsInt.add(0);
                        ids.add(id);
                        myData.add(new data(imageUrl, title, channeltitle,0,Floating3DSearch.this, id));
                        //System.out.println(myData.get(i));
                    }


                }

            } catch (JSONException e) {
                if (pref.getBoolean("loggedin", false)) {
//                    getAccountNames();
                }else {
                    MyAcessTokenData="nono";
                }
                e.printStackTrace();
            }

            return params[1];
        }

        @Override
        protected void onPostExecute(String result) {
            stat.myDataAll.clear();
            stat.myDataAll.addAll(myData);
                //System.out.println(stat.myDataAll.size());
             //   for (int i=0; i<stat.myDataAll.size(); i++)
                    //System.out.println(stat.myDataAll.get(i).imageId);

            if (result.equals("1"))
            {
                page++;
                Intent id = new Intent(Floating3DSearch.this, Floating3DVideos.class);
                id.putExtra("page", page);
                id.putExtra("pages", pages);
                id.putExtra("search", searchQuery);
                id.putExtra("vidBools", true);
                //System.out.println(page+" "+pages.size());
                startActivity(id);
            }

            finish();
//                if (myDataAll.size() > 30) {
//                    ////System.out.println(myDataAll.size() + "Notify");
////                    adapter.notifyDataSetChanged();
//                } else {
//                    ////System.out.println(myDataAll.size() + "New Adapter");
////                    adapter = new StackAdapterNoSelect(
////                            SearchActivity.this, R.layout.stack_list, myDataAll);
////                    myList.setAdapter(adapter);
//                }



            super.onPostExecute(result);
        }

    }

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
    final String type = "type=video";
    final String videoDimension ="videoDimension=";
    final String channelId = "channelId=";
    final String playlistId = "playlistId=";
    final String pageToken = "pageToken=";
    final String extras = "videoDimension=3d&type=video,channel,playlist";


    private boolean scroll;
    ArrayList<String> pages;
    List<data> myData;

    ArrayList<String> ids;
    ArrayList<Integer> idsInt;
    String accountName;
    private SharedPreferences pref;

    Boolean isSafe;

}