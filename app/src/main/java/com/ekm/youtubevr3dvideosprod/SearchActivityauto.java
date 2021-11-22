package com.ekm.youtubevr3dvideosprod;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tools.data;

//import com.google.analytics.tracking.android.EasyTracker;

public class SearchActivityauto extends Activity {
    EditText q;
    ListView myList;
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_search);

        Intent intent = getIntent();

         data = intent.getStringExtra("term");

        run(null);

//        q = (EditText) findViewById(R.ids.editText);
//        q.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                q.setEnabled(true);
//                q.setCursorVisible(true);
//                q.setBackgroundResource(R.drawable.picb46);
//
//            }
//        });
//        q.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    run(null);
//                    handled = true;
//                }
//                return handled;
//            }
//        });
//        myList = (ListView) findViewById(R.ids.listView);
       // t = (TextView) findViewById(R.ids.textView);
      //  aq = new AQuery(this);

    }

    @Override
    public void onStart() {
        super.onStart();

      //  EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();

        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    String url;
final int maxInt = 30;
final String max = "maxResults="+maxInt;
final String searchUrl = "https://www.googleapis.com/youtube/v3/search?";
final String playlistUrl = "https://www.googleapis.com/youtube/v3/playlists?";
final String and = "&";
final String term = "q=";
final String iDplay = "ids=";
final String key = "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
final String part = "part=snippet";
final String channelId = "channelId=";
public static ArrayList<String> ids;
ArrayList<Integer> idsInt;
    public void run(View v) {
        try {
            url = searchUrl+key+and+part+and+max+and+term+ URLEncoder.encode(data, "UTF-8");
            System.out.println(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new RunTask().execute("");
     //   MainStarterActivity.videonorm = new ArrayList<String>();
//        InputMethodManager imm = (InputMethodManager)getSystemService(
//                Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(q.getWindowToken(), 0);
//      //  q.setEnabled(false);
//        q.setCursorVisible(false);
//        q.setBackgroundColor(Color.TRANSPARENT);

//        // Get tracker.
//        Tracker t = ((app) this.getApplication()).getTracker(
//                app.TrackerName.APP_TRACKER);
//
//        t.setScreenName("Searching "+q.getText());
//
//
//
//        // Send a screen view.
//        t.send(new HitBuilders.AppViewBuilder().build());


    }

//void print(String p) {
//    System.out.println(p);
//}
        // ////////////////////////////////////////////////////////////////
String getJson(String url) throws IOException {
    //System.out.println(url);
    Request request = new Request.Builder()
            .url(url)
            .build();

    Response response = client.newCall(request).execute();
    return response.body().string();


}


    OkHttpClient client = new OkHttpClient();
        private class RunTask extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
              //  myBar.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {


                String getData = null;
                try {
                    getData = getJson(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // print(getData);


                // getting JSON Object
                JSONObject mObj;
                ids = new ArrayList<String>();
                idsInt = new ArrayList<Integer>();
                try {
                    mObj = new JSONObject(getData);

                JSONArray mainArray = mObj.getJSONArray("items");
                    myData = new ArrayList<data>();
                  JSONObject opage = mObj.getJSONObject("pageInfo");

                    int max = Math.min(opage.getInt("totalResults"), 30);
                    for (int i=0; i<max; i++) {

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
                            //MainStarterActivity.videonorm.add(ids);
                            myData.add(new data(imageUrl, title, channeltitle,0));
                        }   else if (kind.equals("youtube#channel")) {
                            String idchan = idOBject1.getString("channelId");
                            myData.add(new data(imageUrl, title, channeltitle,1));
                            ids.add(idchan);
                            idsInt.add(1);
                            System.out.println(idchan);
                        }
                        else if (kind.equals("youtube#playlist")) {
                            String idchan = sousObject1.getString("channelId");
                            myData.add(new data(imageUrl, title, channeltitle,1));
                            System.out.println("next move");
                            ids.add(idchan);
                            idsInt.add(1);
                        } else {
                            continue;
                        }


                    }

            } catch (JSONException e) {
                e.printStackTrace();
            }

                return "-";
            }

            @Override
            protected void onPostExecute(String result) {
                if (isSafe) {
//                    StackAdapterNoSelect adapter = new StackAdapterNoSelect(
//                            SearchActivityauto.this, R.layout.stack_list, myData);

                  //  myList.setAdapter(adapter);

//                    myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        public void onItemClick(AdapterView<?> parent,
//                                                View view, int position, long ids) {
                            if(idsInt.get(0)==0) {
                            Intent intent = new Intent(SearchActivityauto.this, VideoPlayerActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.putExtra(MainStarterActivity.vidId2, ids.get(0));
                            startActivity(intent);}
                            else if (idsInt.get(0)==1) {
                                try {
                                    url = searchUrl+key+and+part+and+max+and+channelId+ URLEncoder.encode(ids.get(0).toString(), "UTF-8");
                                    System.out.println(url);
                                    new RunTaskChannel().execute("");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
//                    });
//                }
                super.onPostExecute(result);
            }

        }

    private class RunTaskChannel extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            //  myBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {


            String getData = null;
            try {
                getData = getJson(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // getting JSON Object
            JSONObject mObj;
            ids = new ArrayList<String>();
            idsInt = new ArrayList<Integer>();
            try {
                mObj = new JSONObject(getData);

                JSONArray mainArray = mObj.getJSONArray("items");
                myData = new ArrayList<data>();
                JSONObject opage = mObj.getJSONObject("pageInfo");
                int max = Math.min(opage.getInt("totalResults"), 30);
                for (int i=1; i<max; i++) {

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
                     //   MainStarterActivity.videonorm.add(ids);
                        ids.add(id);
                        myData.add(new data(imageUrl, title, channeltitle,0));
                    }   else if (kind.equals("youtube#channel")) {
                        String idchan = idOBject1.getString("channelId");

                        ids.add(idchan);
                        idsInt.add(1);
                        System.out.println(idchan);
                        myData.add(new data(imageUrl, title, channeltitle,1));
                    }
                    else if (kind.equals("youtube#playlist")) {
                        String idchan = sousObject1.getString("channelId");
                        ids.add(idchan);
                        myData.add(new data(imageUrl, title, channeltitle,1));
                        idsInt.add(1);
                    } else {
                        continue;
                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "-";
        }

        @Override
        protected void onPostExecute(String result) {
            if (isSafe) {
//                StackAdapterNoSelect adapter = new StackAdapterNoSelect(
//                        SearchActivityauto.this, R.layout.stack_list, myData);

          //      myList.setAdapter(adapter);

//                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> parent,
//                                            View view, int position, long ids) {
                        if(idsInt.get(0)==0) {
                            Intent intent = new Intent(SearchActivityauto.this, VideoPlayerActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.putExtra(MainStarterActivity.vidId2, ids.get(0));
                            startActivity(intent);}
                        else if (idsInt.get(0)==1) {
                            try {
                                url = searchUrl+key+and+part+and+max+and+channelId+ URLEncoder.encode(ids.get(0).toString(), "UTF-8");
                                System.out.println(url);
                                new RunTaskChannel().execute("");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
//                    }
//                });
            }
            super.onPostExecute(result);
        }

    }


    Boolean isSafe;

    @Override
    protected void onResume() {
        isSafe = true;

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        isSafe = false;
        super.onDestroy();
    }

    List<data> myData;
}
