package com.ekm.youtubevr3dvideosprod;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import io.topvpn.vpn_api.api;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import com.yunju.vr360videoplayer.SimpleVrVideoActivity;
import com.yunju.vr360videoplayer.VrVideoActivity;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import io.display.sdk.Controller;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tools.ConnectivityReceiver;
import tools.EndlessRecyclerViewScrollListener;
import tools.InfiniteScrollListener;
import tools.ItemClickSupport;
import tools.StackAdapterNoSelect;
import tools.data;

public class SearchActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, BillingProcessor.IBillingHandler, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 1004;
    private static final String TAG = "SearchActivity";
    BillingProcessor bp;
    private String force = "Force";
    BottomBar extraBar;
    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Object> playlistsKey;
    private MaterialDialog pdial;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private boolean isConnected;
    private String idrecord = "id";
    private CheckBox auto;
    private static final int MY_PERMISSIONS_REQUEST_READ = 2564 ;

    // IBillingHandler implementation

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    @Override
    public void onBillingInitialized() {
        //
        //* Called when BillingProcessor was initialized and it's ready to purchase
        //
        if (bp.isPurchased(Settings.adremove)) {
            editor = pref.edit();
            editor.putBoolean("ad", true);
            editor.commit();  //  editor.putInt("zoomer", 40);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(force)
                .setAction("New Purchase")
                .setLabel(bp.getPurchaseListingDetails(productId).title + " with lock of " + MainStarterActivity.limitry)
                .build());
        // runHelper();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(force)
                .setAction("Purchase Failure")
                .setLabel("Error: " + errorCode)
                .build());
        editor.putInt("nohappy", pref.getInt("nohappy", 0) + 5);
        editor.commit();
        rate(5);
//        if (MainStarterActivity.newAdds) {
//            promptPurchaseWithVideo();
//        } else if (MainStarterActivity.earnCoin) {
//            promptPurchaseWithAddsr();
//        }
        //q.setText("");
        //runHelper();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    private static final int ACCOUNT_PICKER = 25452;
    private static final int USER_RECOVERABLE_AUTH = 5656516;
    AutoCompleteTextView q;
//    ListView myList;
    ArrayList<String> pages;
    private boolean scroll;
    private SharedPreferences pref;
    private Tracker mTracker;
    private ListAdapter mAdapter;

    private
    void start_luminati_sdk(final Context ctx){
        api.set_dialog_type(api.DIALOG_TYPE.PEER1);
        api.set_tos_link("http://www.futureappsllc.us/#privacy-policy");
        api.set_btn_peer_txt(api.BTN_PEER_TXT.I_AGREE);
        api.set_btn_not_peer_txt(api.BTN_NOT_PEER_TXT.I_DISAGREE);
        api.set_selection_listener(new api.on_selection_listener(){
            public void on_user_selection(int choice){
                // Psuedocode for handling user selection (not mandatory, can
                // use api.get_user_selection directly instead)
                System.out.println(choice+" __________________________");
                System.out.println( api.get_user_selection(ctx)+" -----------------------------------");
                switch (choice){
                    case api.CHOICE_PEER:
                        editor.putBoolean(Settings.luminatiId, true);
                        editor.commit();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Luminati")
                                .setAction("PEER")
                                .setValue(1)
                                .build());
                        break;
                    case api.CHOICE_NOT_PEER:
                        api.clear_selection(SearchActivity.this);
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Luminati")
                                .setAction("NOT PEER")
                                .setValue(1)
                                .build());
                      //  luminati.setChecked(false);
                        break;
                    case api.CHOICE_NONE:
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Luminati")
                                .setAction("NONE")
                                .setValue(1)
                                .build());
                       // api.clear_selection(SearchActivity.this);
                     //   luminati.setChecked(false);
                        break;

                }
            }
        });
        api.init(SearchActivity.this, false);
    }

    public void search3d(View v) {
        System.out.println("Search 3D");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_READ);
        }else {
            System.out.println("else permission");
            start2d3d();
        }
    }
    public void settings(View v) {
        Intent id = new Intent(SearchActivity.this,
                Settings.class);
//        ids.putExtra(vidId, videos.get(location));
        startActivity(id);
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
                                Intent intent = new Intent(SearchActivity.this, SimpleVrVideoActivity.class);
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
                Intent intent = new Intent(SearchActivity.this, Floating2D3DStarted.class);
//                                        intent.putExtra(MainStarterActivity.vidId,vidid2);
//                                intent.putExtra("lloop", time2);
                startActivity(intent);
                chosen = false;
            }}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        // System.out.println(">>>>>>>>>>OOOO>>>>;>>>>>>>>>   -  "+MainStarterActivity.limitry+"   -  "+MainStarterActivity.earnCoin+"  -  "+MainStarterActivity.amountToEarn);

        mRecyclerView = (RecyclerView) findViewById(R.id.mlist);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        // ...
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    DeveloperKey.userid = user.getUid();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("type");
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    signInAnonymously();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setDefaults(R.xml.mydefs);
//        getAndSetDefaults();
        System.out.println("=======================  " + MainStarterActivity.limitRand + "  -  " + MainStarterActivity.limitry + "   -  " + MainStarterActivity.earnCoin);

        bp = new BillingProcessor(this, DeveloperKey.BILLING_KEY, this);
        bp.loadOwnedPurchasesFromGoogle();
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
        AppRate.showRateDialogIfMeetsConditions(this);



        if (MainStarterActivity.newAdds)
            Controller.getInstance().init(this, "5191");
        // ...
        bp = new BillingProcessor(this, DeveloperKey.BILLING_KEY, this);
        bp.loadOwnedPurchasesFromGoogle();
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        playlistsKey = new ArrayList<>();
        playlistsKey.add("likes");
        playlistsKey.add("favorites");
        playlistsKey.add("uploads");
        playlistsKey.add("watchHistory");
        playlistsKey.add("watchLater");

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
        extraBar = (BottomBar) findViewById(R.id.bottomBar);

        // Obtain the shared Tracker instance.
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();
//        MyAcessTokenData = "access_token=" + pref.getString(authutil.acess_token, "nono"); }
//        else {
//        MyAcessTokenData = "access_token=nono";
//        }
        pages = new ArrayList<String>();
        //  pages.add("");
//        page=0;
        editor = pref.edit();
        myDataAll = new ArrayList<data>();
//        ids = new ArrayList<String>();
//        idsInt = new ArrayList<Integer>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.dropdown, SUGGESTION);
        q = (AutoCompleteTextView)
                findViewById(R.id.playtxt);
        q.setAdapter(adapter);
        q.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isConnected) {
                    try {
                        new GetSuggestions().execute("http://suggestqueries.google.com/complete/search?client=youtube&ds=yt&client=firefox&q=" + URLEncoder.encode(q.getText().toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
//        q = (EditText) findViewById(R.id.playtxt);
        q.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                run(null);
            }
        });
        q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                q.setEnabled(true);
                q.setCursorVisible(true);
                q.setBackgroundResource(R.drawable.picb46);

            }
        });
        q.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    run(null);
                    handled = true;
                    q.dismissDropDown();
                }
                return handled;
            }
        });
        q.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (q.getRight() - q.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        run(null);
                        return true;
                    }
                }
                return false;
            }
        });
        //myList = (ListView) findViewById(R.id.listView);

//        myList.setEmptyView(findViewById(R.id.emptyView));
//--------------------------------
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(DeveloperKey.ClientID)
                .requestServerAuthCode(DeveloperKey.ClientID)
                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
                .requestEmail()
                .build();

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        //----------------------------------

        checkConnection();

      emptyView = (RelativeLayout) findViewById(R.id.emptyView);

        extraBar.selectTabWithId(R.id.tab_search);
        extraBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_likes) {
                 likes(null);
                } else if (tabId == R.id.tab_search) {
                        mRecyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                         q.setVisibility(View.VISIBLE);
                } else   if (tabId == R.id.tab_user) {
                   signOut(null);
                    q.setVisibility(View.VISIBLE);
                    extraBar.selectTabWithId(R.id.tab_search);
                }
                else if (tabId == R.id.tab_upload) {
                 uploads(null);
                }
            }
        });

    }
    RelativeLayout emptyView;
    private String[] SUGGESTION = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };



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
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // System.out.println("failed connection");
    }

    public void resetPages() {
        myDataAll = new ArrayList<>();
        scroll = false;
        pages = new ArrayList<>();
        pages.add("");
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result, boolean x) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            /* Signed in successfully, show authenticated UI. */
            GoogleSignInAccount acct = result.getSignInAccount();
            if (x) {
                if (user != null) {
                    if (user.getDisplayName() == null)
                        convertUser(acct.getIdToken());
                }
            }
            editor.putString(DeveloperKey.TokenID, acct.getServerAuthCode());
            editor.commit();
            try {
                getToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);


        } else {

            // Signed out, show unauthenticated UI.
            //   updateUI(false);
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            //    System.out.println("Signed out");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Signin")
                .setAction("Signin user")
                .build());
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    OkHttpClient client = new OkHttpClient();
    // [END handleSignInResult]
    String getJson(String url) throws IOException {
        if (isConnected) {
            //   System.out.println(url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.code() == 401) {
                editor.putString(DeveloperKey.AcessToken, "none");
                editor.commit();
                getJson(url(0));
            }
            // System.out.println(response.body().toString());

            return response.body().string();
        } else {
            return "";
        }
    }




    public void history(View v) {
        runHelperP(playlists.get(playlistsKey.indexOf("watchHistory")));
        //ew RunTaskPlaylist().execute(url);
    }

    public void likes(View v) {
        runHelperP(playlists.get(playlistsKey.indexOf("likes")));
        q.setVisibility(View.GONE);
//        new RunTaskPlaylist().execute(url);
    }

    public void later(View v) {
        runHelperP(playlists.get(playlistsKey.indexOf("watchLater")));
//        new RunTaskPlaylist().execute(url);
    }

    public void favs() {
        runHelperP(playlists.get(playlistsKey.indexOf("favorites")));
//        new RunTaskPlaylist().execute(url);
    }

    public void uploads(View v) {
        runHelperP(playlists.get(playlistsKey.indexOf("uploads")));
        q.setVisibility(View.GONE);
//        new RunTaskPlaylist().execute(url);
    }


    public void coaster(View v) {
        q.setText("roller Coaster vr");
        run(v);
    }

    public void movies360(View v) {
        q.setText("360 video");
        run(v);
    }

    public void vryt3d(View v) {
        q.setText("vr yt3d");
        run(v);
    }

    public void movies(View v) {
        q.setText("movies yt3d");
        run(v);

    }

    String url(int page) {
        String url = "";
        try {
            int pg = page;
            if (pages.size() >= page) {
                pg = pages.size() - 1;
            }


            //System.out.println(page + "  ----   ");
            url = searchUrl + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + part + and + pageToken + pages.get(pg) + and + max + and + term + URLEncoder.encode(q.getText().toString(), "UTF-8") + and + key;
//            //System.out.println(pages.get(page) + "   Step " + page);
            //System.out.println(url);
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Queries")
                    .setAction("Search")
                    .setLabel(q.getText().toString())
                    .build());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //  System.out.println(url);
        return url;
    }

    //String url;
    final int maxInt = 30;
    final String max = "maxResults=" + maxInt;
    final String searchUrl = "https://www.googleapis.com/youtube/v3/search?";
    final String videoUrl = "https://www.googleapis.com/youtube/v3/videos?";
    //    final String channelsUrl = "https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.channels.list?" +
//            "        part=contentDetails" +
//            "        &mine=true";
    final String channelsUrl = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true";
    final String playlistItem = "https://www.googleapis.com/youtube/v3/playlistItems?";
    final String and = "&";
    final String term = "q=";
    final String iDplay = "id=";
    static String MyAcessTokenData = "access_token=";
    final String key = "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
    final String part = "part=snippet";
    final String partDetail = "part=contentDetails,statistics";
    final String channelId = "channelId=";
    final String playlistId = "playlistId=";
    final String pageToken = "pageToken=";
    final String extras = "videoDimension=3d&type=video,channel,playlist";
    //    int page=0;
//int page=0;
//    ArrayList<String> ids;
//    ArrayList<Integer> idsInt;

    boolean contain(String n, String n2) {
        return StringUtils.lowerCase(n).contains(StringUtils.lowerCase(n2));
    }

    public void run(View v) {
        checkConnection();
        // System.out.println(isConnected);

        if (isConnected) {
            // try {
            // url = searchUrl+key+and+part+and+pageToken+pages.get(page)+and+max+and+term+ URLEncoder.encode(q.getText().toString(), "UTF-8");
            scroll = false;
            pages = new ArrayList<String>();
            pages.add("");
//        page=0;
            myDataAll = new ArrayList<data>();
//        ids = new ArrayList<String>();
//        idsInt = new ArrayList<Integer>();
            String data = q.getText().toString();
            if (contain(data, "sex") || contain(data, "porn") || contain(data, "bikini") || contain(data, "nude") || contain(data, "vagina") || contain(data, "pussy") || contain(data, "model")) {
                if (bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2) || bp.isPurchased(Settings.adremove) || api.get_user_selection(this)==1) {
                    runHelper();

                } else {
                    promptPurchase();
                }
            } else {
                runHelper();
            }
        } else {
            notConnected();
        }
    }

    void runHelper() {
        // url(0);
        checkConnection();
        if (isConnected) {
            new RunTask().execute(url(0), "0");
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(q.getWindowToken(), 0);
            //  q.setEnabled(false);
            q.setCursorVisible(false);
            q.setBackgroundColor(Color.TRANSPARENT);

            EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (scroll) {
                        new RunTask().execute(url(page), (page + 1) + "");
                    }
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);
        } else {
            notConnected();
        }
    }

    void runHelperP(final String id) {
        System.out.println(id+"  "+isConnected);
        checkConnection();
        if (isConnected) {
            final String url = playlistItem + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + part + and + max + and + playlistId + id + and + key;
//        System.out.println(pages.get(0));
            System.out.println(url);
            new RunTaskPlaylist().execute(url, "0");
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(q.getWindowToken(), 0);
            //  q.setEnabled(false);
            q.setCursorVisible(false);
            q.setBackgroundColor(Color.TRANSPARENT);

            EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (scroll) {
                        int pg = page;
                        if (pages.size() >= page) {
                            pg = pages.size() - 1;
                        }
                        final String url = playlistItem + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + part + and + max + and + playlistId + id + and + key + and + pageToken + pages.get(pg);

                        new RunTaskPlaylist().execute(url, page + "");

                    }
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);
        } else {
            notConnected();
        }
    }

    void runHelperC(final String id) {
        if (isConnected) {
            final String url = searchUrl + MyAcessTokenData + and + part + and + max + and + channelId + id + and + key;
            new RunTaskChannel().execute(url, "0");
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(q.getWindowToken(), 0);
            //  q.setEnabled(false);
            q.setCursorVisible(false);
            q.setBackgroundColor(Color.TRANSPARENT);

            EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (scroll) {
                        int pg = page;
                        if (pages.size() >= page) {
                            pg = pages.size() - 1;
                        }
                        final String url = searchUrl + MyAcessTokenData + and + part + and + max + and + channelId + id + and + key + and + pageToken + pages.get(pg);
                        new RunTaskChannel().execute(url, page + "");
                    }
                }
            };
            mRecyclerView.addOnScrollListener(scrollListener);
        } else {
            notConnected();
        }
    }

    //// TODO: 6/1/2016  add id and page on the fly and scroll
    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result, false);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            // showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //  hideProgressDialog();
                    handleSignInResult(googleSignInResult, false);
                }
            });
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());

        getAndSetDefaults();
        MainStarterActivity.limitRand = mFirebaseRemoteConfig.getLong("unlock");
        MainStarterActivity.limitry = mFirebaseRemoteConfig.getLong("try");
        MainStarterActivity.amountToEarn = mFirebaseRemoteConfig.getLong("earn");
        MainStarterActivity.amountToEarn = 0;
        MainStarterActivity.newAdds = mFirebaseRemoteConfig.getBoolean("newAdd");
        MainStarterActivity.earnCoin = mFirebaseRemoteConfig.getBoolean("earncoin");
        MainStarterActivity.adFrequency = mFirebaseRemoteConfig.getBoolean("adf");
        MainStarterActivity.cost = mFirebaseRemoteConfig.getBoolean("cost");
        MainStarterActivity.show = mFirebaseRemoteConfig.getBoolean("show");
        MainStarterActivity.exitshow = mFirebaseRemoteConfig.getBoolean("exitshow");
//        info.setText(limitRand+"  -  "+limitry+"   -  "+earnCoin+"  -  "+amountToEarn+" *"+recheckTime);
        startHelper("POST CONFIG2");

    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return StringUtils.capitalize(model);
        }
        return StringUtils.capitalize(manufacturer) + " " + model;
    }
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
                        .setValue(MainStarterActivity.limitRand)
                        .build());
                editor.putBoolean("firsttime", true);
                editor.commit();
            } else {
                MainStarterActivity.limitRand = 500;
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
            Intent id = new Intent(this,
                    intro.class);
            startActivity(id);
        }
    }

    private  void getAndSetDefaults() {
        System.out.println(">>>>>>>>>>>>>>>>>>TRIED TO RUN DEFAULT");
        mFirebaseRemoteConfig.fetch(200)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Fetch Succeeded");
                            mFirebaseRemoteConfig.activateFetched();

                            MainStarterActivity.limitRand = mFirebaseRemoteConfig.getLong("unlock");
                            MainStarterActivity.limitry = mFirebaseRemoteConfig.getLong("try");
                            MainStarterActivity.amountToEarn = mFirebaseRemoteConfig.getLong("earn");
                            MainStarterActivity.newAdds = mFirebaseRemoteConfig.getBoolean("newAdd");
                            MainStarterActivity.earnCoin = mFirebaseRemoteConfig.getBoolean("earncoin");
                            MainStarterActivity.adFrequency = mFirebaseRemoteConfig.getBoolean("adf");
                            MainStarterActivity.cost = mFirebaseRemoteConfig.getBoolean("cost");
                            MainStarterActivity. show = mFirebaseRemoteConfig.getBoolean("show");
                            MainStarterActivity.exitshow = mFirebaseRemoteConfig.getBoolean("exitshow");
//                            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>  "+limitRand+"  -  "+limitry+"   -  "+earnCoin+"  -  "+amountToEarn+" *"+recheckTime);
                            //   info.setText(limitRand+"  -  "+limitry+"   -  "+earnCoin+"  -  "+amountToEarn+" *"+recheckTime);
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Config ok")
                                    .setAction("New")
                                    .setLabel("Lock:"+MainStarterActivity.limitry+"   -  To earn:"+MainStarterActivity.amountToEarn)
                                    .build());
//                            startHelper("POST CONFIG");
                        } else {
                            Log.d(TAG, "Config failed");
                            System.out.println(">>>>>>>>>>>>>>>>>>FAILED RUN DEFAULT");
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Config")
                                    .setAction("failed")
                                    .setLabel("Lock:"+MainStarterActivity.limitry+"   -  To earn:"+MainStarterActivity.amountToEarn)
                                    .build());
                            // startHelper("POST CONFIG");
                        }

                    }
                });
    }


    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.disconnect();
    }







    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }

    // Method to manually check connection status
    private void checkConnection() {

        isConnected = ConnectivityReceiver.isConnected();
        // System.out.println(isConnected);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Search Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private class GetSuggestions extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if (isConnected) {
                String getData = null;
                try {
                    getData = getJson(params[0]);
                    //  System.out.println(getData+"  ------");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // getting JSON Object
                JSONArray mObj = null;

                try {
                    if (getData != null)
                        mObj = new JSONArray(getData);

                    //    //System.out.println(pages.get(pages.size()-1)+"   Step+2   "+mObj.getString("nextPageToken"));

                    JSONArray mainArray = mObj.getJSONArray(1);
                    if (mainArray != null) {
                        SUGGESTION = new String[mainArray.length()];
                        for (int i = 0; i < mainArray.length(); i++) {
                            SUGGESTION[i] = mainArray.getString(i);
                            //  System.out.println(SUGGESTION[i]);
                        }
                    } else {
                        SUGGESTION = new String[]{
                                "Belgium", "France", "Italy", "Germany", "Spain"
                        };
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("SUGGESTION ERROR")
                            .setAction(e.getMessage())
                            .build());
                } catch (NullPointerException er) {

                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (isSafe && isConnected) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivity.this,
                        android.R.layout.simple_dropdown_item_1line, SUGGESTION);
                q.setAdapter(adapter);
            }
        }
    }

    private class RunTask extends AsyncTask<String, String, List<data>> {
        List<data> myData = new ArrayList<>();

        @Override
        protected void onPreExecute() {

            extraBar.selectTabWithId(R.id.tab_search);
            super.onPreExecute();
        }

        boolean prescroll = false;

        @Override
        protected List<data> doInBackground(String... params) {
            //System.out.println(params[1]);
            if (params[1].equals("0")) {
                resetPages();
            }
            int maxResult = 0;
            String getData = null;
            try {
                getData = getJson(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            }
            // getting JSON Object
            JSONObject mObj;
            String idDuration = "";
            try {
                mObj = new JSONObject(getData);

                if (!mObj.isNull("nextPageToken")) {
                    pages.add(mObj.getString("nextPageToken"));
                    prescroll = true;
                } else {
                    scroll = false;
                }
                //    //System.out.println(pages.get(pages.size()-1)+"   Step+2   "+mObj.getString("nextPageToken"));
                JSONArray mainArray = mObj.getJSONArray("items");


                JSONObject opage = mObj.getJSONObject("pageInfo");

                maxResult = Math.min(opage.getInt("totalResults"), mainArray.length());
                // System.out.println(maxResult);
                for (int i = 0; i < maxResult; i++) {

                    JSONObject mainObject0 = mainArray.getJSONObject(i);
                    JSONObject sousObject1 = mainObject0.getJSONObject("snippet");
                    String title = sousObject1.getString("title");
                    String channeltitle = sousObject1.getString("channelTitle");
                    JSONObject sousObject2 = sousObject1.getJSONObject("thumbnails");
                    JSONObject sousObject3 = sousObject2.getJSONObject("medium");
                    String imageUrl = sousObject3.getString("url");
                    String dates = sousObject1.getString("publishedAt");
                    //print(imageUrl);


                    JSONObject idOBject1 = mainObject0.getJSONObject("id");
                    String kind = idOBject1.getString("kind");

                    if (kind.equals("youtube#video")) {
                        String id = idOBject1.getString("videoId");
                        myData.add(new data(imageUrl, title, channeltitle, dates, 0, id));
                        idDuration += id + ",";
                    } else if (kind.equals("youtube#channel")) {
                        String idchan = idOBject1.getString("channelId");
                        myData.add(new data(imageUrl, title, channeltitle, dates, 1, idchan));
                    } else if (kind.equals("youtube#playlist")) {
                        String idchan = idOBject1.getString("playlistId");
                        myData.add(new data(imageUrl, title, channeltitle, dates, 2, idchan));
                    }

                }


            } catch (JSONException e) {
            } catch (NullPointerException e) {

            }
            String url = videoUrl + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + partDetail + and + "id=" + idDuration + and + key;

            try {
                getData = getJson(url);

                mObj = new JSONObject(getData);
                JSONArray items = mObj.getJSONArray("items");
                int kk = 0;
                for (int i = 0; i < myData.size(); i++) {

                    if (myData.get(i).getType() == 0) {
                        JSONObject child = items.getJSONObject(kk);
                        JSONObject innerChild = child.getJSONObject("contentDetails");
                        String duration = innerChild.getString("duration");
                        myData.get(i).setDuration(duration);

                        JSONObject innerChild2 = child.getJSONObject("statistics");
                        String Count = innerChild2.getString("viewCount");
                        myData.get(i).setCount(Long.parseLong(Count));

                        kk++;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {

            }
            return myData;
        }

        @Override
        protected void onPostExecute(List<data> myData) {
            if (isSafe && isConnected) {

                myDataAll.addAll(myData);
                if (prescroll && pages.size() > 1)
                    scroll = true;
                if (myDataAll.size() > 30) {
                    //       System.out.println(myDataAll.size() + "Notify");
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter = new ListAdapter(myDataAll);
                    mRecyclerView.setAdapter(mAdapter);
                    if (!myDataAll.isEmpty()) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }
                try {
                    ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                if ((pref.getBoolean("noluck", true) && pref.getInt("inc", 0) >= MainStarterActivity.limitry)) {
                                    if (bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2) || bp.isPurchased(Settings.adremove) || myDataAll.get(position).getType() == 1 || api.get_user_selection(SearchActivity.this)==1) {
                                        if (myDataAll.get(position).getType() == 0) {
                                            mTracker.send(new HitBuilders.EventBuilder()
                                                    .setCategory("Queries")
                                                    .setAction("NEW CHOSEN 2D ID")
                                                    .setLabel(myDataAll.get(position).getId())
                                                    .build());
                                            if (lower(myDataAll.get(position).getTitle()).contains("sbs") ||
                                                    lower(myDataAll.get(position).getTitle()).contains("yt3d")) {
                                                runMain(myDataAll.get(position).getId());
                                                mTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory("FORCE PLAY")
                                                        .setAction("SBS")
                                                        .build());
                                            } else {
                                                getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                            }
                                        } else if (myDataAll.get(position).getType() == 1) {
                                            scroll = false;
                                            try {

                                                //System.out.println(url);
                                                mTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory("Queries")
                                                        .setAction("Chosen 2D")
                                                        .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                        .build());
                                                runHelperC(myDataAll.get(position).getId());
                                                //  new RunTaskChannel().execute(url);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        } else if (myDataAll.get(position).getType() == 2) {
                                            scroll = false;
                                            try {

                                                String url = playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                                //System.out.println(url);
                                                mTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory("Queries")
                                                        .setAction("Chosen 2D")
                                                        .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                        .build());
                                                runHelperP(myDataAll.get(position).getId());
                                                //  new RunTaskPlaylist().execute(url);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    } else {
                                        promptPurchase();
                                        idrecord = myDataAll.get(position).getId();
                                    }
                                } else {
                                    try {
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory(force)
                                                .setAction("Free Play")
                                                .setValue(1)
                                                .build());
                                        if (myDataAll.get(position).getType() == 0) {
                                            mTracker.send(new HitBuilders.EventBuilder()
                                                    .setCategory("Queries")
                                                    .setAction("NEW CHOSEN 2D ID")
                                                    .setLabel(myDataAll.get(position).getId())
                                                    .build());
                                            if (lower(myDataAll.get(position).getTitle()).contains("sbs") ||
                                                    lower(myDataAll.get(position).getTitle()).contains("yt3d")) {
                                                runMain(myDataAll.get(position).getId());
                                                mTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory("FORCE PLAY")
                                                        .setAction("SBS")
                                                        .build());
                                            } else {
                                                getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                            }
                                        } else if (myDataAll.get(position).getType() == 1) {
                                            scroll = false;
                                            try {

                                                //   String url = searchUrl + MyAcessTokenData + and + part + and + max + and + channelId + URLEncoder.encode(myDataAll.get(position).getId().toString(), "UTF-8") + and + key;
                                                //System.out.println(url);
                                                mTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory("Queries")
                                                        .setAction("Chosen 2D")
                                                        .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                        .build());
                                                runHelperC(myDataAll.get(position).getId());
//                                    new RunTaskChannel().execute(url);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        } else if (myDataAll.get(position).getType() == 2) {
                                            scroll = false;
                                            try {

                                                String url = playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                                //System.out.println(url);
                                                mTracker.send(new HitBuilders.EventBuilder()
                                                        .setCategory("Queries")
                                                        .setAction("Chosen 2D")
                                                        .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                        .build());
                                                runHelperP(myDataAll.get(position).getId());
//                                    new RunTaskPlaylist().execute(url);
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    } catch (IndexOutOfBoundsException e) {

                                    }
                                }

                        }
                    });

                    ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                            if (pref.getBoolean("noluck", true) && pref.getInt("inc", 0) >= MainStarterActivity.limitry) {
                                if (bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2) || bp.isPurchased(Settings.adremove) || myDataAll.get(position).getType() == 1 || api.get_user_selection(SearchActivity.this)==1) {
                                    if (myDataAll.get(position).getType() == 0) {
                                        if (contain(myDataAll.get(position).getTitle(), "sbs")) {
                                            getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                        } else {
                                            runMain(myDataAll.get(position).getId());
                                            mTracker.send(new HitBuilders.EventBuilder()
                                                    .setCategory("Queries")
                                                    .setAction("New Chosen 2D LONG")
                                                    .setLabel(myDataAll.get(position).getId())
                                                    .build());
                                        }
                                    } else if (myDataAll.get(position).getType() == 1) {
                                        scroll = false;
                                        try {
                                            String url = searchUrl + MyAcessTokenData + and + part + and + max + and + channelId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                            //System.out.println(url);
                                            mTracker.send(new HitBuilders.EventBuilder()
                                                    .setCategory("Queries")
                                                    .setAction("Chosen 2D")
                                                    .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                    .build());
                                            runHelperC(myDataAll.get(position).getId());
//                                        new RunTaskChannel().execute(url);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                            output("Please try again. If you keep receiving this message, email ekm10evans@gmail.com for support.");
                                        }

                                    }

                                } else {
                                    idrecord = myDataAll.get(position).getId();
                                    promptPurchase();
                                }
                            } else {

                                if (myDataAll.get(position).getType() == 0) {
                                    if (contain(myDataAll.get(position).getTitle(), "sbs")) {
                                        getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                    } else {
                                        runMain(myDataAll.get(position).getId());
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("New Chosen 2D LONG")
                                                .setLabel(myDataAll.get(position).getId())
                                                .build());
                                    }
                                } else if (myDataAll.get(position).getType() == 1) {
                                    scroll = false;
                                    try {
                                        String url = searchUrl + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + part + and + max + and + channelId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                        //System.out.println(url);
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("Chosen 2D")
                                                .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                .build());
                                        runHelperC(myDataAll.get(position).getId());
//                                    new RunTaskChannel().execute(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }


                            }
                            return false;
                        }
                    });


                } catch (IndexOutOfBoundsException e) {
                }
            }
            //   pdial.hide();

        }

    }

    void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                        }

                        // ...
                    }
                });

    }

    void output(String data) {
        new MaterialDialog.Builder(SearchActivity.this)
                .title(getResources().getString(R.string.error))
                .titleColorRes(R.color.fab_color_emerald)
                .content(data)
                .positiveText(getResources().getString(R.string.ok))
                .backgroundColor(Color.parseColor("#ffffff"))
                .contentColor(Color.parseColor("#000000"))
                .show();
    }

    private void promptPurchase() {
        if (user == null) {
            signInAnonymously();
        }
        {
            new MaterialDialog.Builder(SearchActivity.this)
                    .title(getResources().getString(R.string.unlockit))
                    .titleColorRes(R.color.fab_color_emerald)
                    .content(getResources().getString(R.string.unlockmessage))
                    .positiveText(getResources().getString(R.string.purchase))
                    .backgroundColor(Color.parseColor("#ffffff"))
                    .contentColor(Color.parseColor("#000000"))
//                    .negativeText(getResources().getString(R.string.useLuminati))
                    .negativeText(getResources().getString(R.string.disagree))
                    .negativeColorRes(R.color.fab_color_alizarin)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory(force)
                                    .setAction("Prompt to purchase")
                                    .setLabel("NO")
                                    .setValue(1)
                                    .build());
                        }
                    })
                    .positiveColorRes(R.color.fab_color_green_sea)
//                    .onNegative(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(MaterialDialog dialog, DialogAction which) {
////                            if (MainStarterActivity.newAdds) {
////                                promptPurchaseWithVideo();
////                            } else if (MainStarterActivity.earnCoin) {
//                                promptPurchaseWithAddsr();
////                            }
//                            editor.putInt("nohappy", pref.getInt("nohappy", 0) + 1);
//                            editor.commit();
////                            System.out.println(pref.getInt("nohappy", 0));
//                            mTracker.send(new HitBuilders.EventBuilder()
//                                    .setCategory(force)
//                                    .setAction("Prompt to purchase")
//                                    .setLabel("No")
//                                    .setValue(1)
//                                    .build());
//                        }
//                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            if (MainStarterActivity.cost) {
                                bp.purchase(SearchActivity.this, Settings.unlock);
                            } else {
                                bp.purchase(SearchActivity.this, Settings.unlock2);
                            }
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory(force)
                                    .setAction("Prompt to purchase")
                                    .setLabel("Yes")
                                    .setValue(1)
                                    .build());
                        }
                    })
                    .canceledOnTouchOutside(false)
                    .show();
        }
    }

    private void promptPurchaseWithAddsr() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Luminati")
                .setAction("Opt in message displayed")
                .build());

        if (true) {
                       //--------------------

            new MaterialDialog.Builder(SearchActivity.this)
                    .title(getResources().getString(R.string.Luminati))
                    .titleColorRes(R.color.fab_color_emerald)
                    .content(getResources().getString(R.string.partLuminati))
                    .positiveText(getResources().getString(R.string.continued))
                    .backgroundColor(Color.parseColor("#ffffff"))
                    .contentColor(Color.parseColor("#000000"))
                    //.negativeText(getResources().getString(R.string.md_cancel_label))
                    .negativeColorRes(R.color.fab_color_wet_asphalt)
                    .positiveColorRes(R.color.fab_color_emerald)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            // System.out.println(p.isContentReady());
                            {
                                //   System.out.println("not ready for ad");
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Luminati")
                                        .setAction("Cancelled")
                                        .setValue(1)
                                        .build());
                                //handle situation where there is no content to show, or it has not yet downloaded.
                            }

                        }
                    })
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                           {
                               // start_luminati_sdk(SearchActivity.this);
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Luminati")
                                        .setAction("Accept Part 1")
                                        .setValue(1)
                                        .build());
                            }
                        }
                    })
                    .show();


            //--------------------

        }
    }




    @Override
    public void onBackPressed() {
      rate(3);
        super.onBackPressed();
    }

void rate(int r) {
    if (pref.getInt("nohappy", 0) > r && !pref.getBoolean("willrate", false)) {
        editor.putBoolean("willrate", true);
        editor.commit();
        Intent intent = new Intent(SearchActivity.this, rater.class);
        startActivity(intent);
    }

    if ((bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2)) && !pref.getBoolean("willrate", false)) {
        editor.putBoolean("willrate", true);
        editor.commit();
        Intent intent = new Intent(SearchActivity.this, rater.class);
        startActivity(intent);
    }
}
    private void promptPurchaseWithVideo() {

        if (true) {


            //--------------------
            new MaterialDialog.Builder(SearchActivity.this)
                    .title(getResources().getString(R.string.qbreakt))
                    .titleColorRes(R.color.fab_color_emerald)
                    .content(getResources().getString(R.string.adbreak))
                    .positiveText(getResources().getString(R.string.continued))
                    .backgroundColor(Color.parseColor("#ffffff"))
                    .contentColor(Color.parseColor("#000000"))
                    .negativeColorRes(R.color.fab_color_sun_flower)
                    .positiveColorRes(R.color.fab_color_emerald)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            {
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Chartboost")
                                        .setAction("rewarded")
                                        .setLabel("Trying")
                                        .setValue(1)
                                        .build());
                            }
                        }
                    }).cancelable(false)
                    .show();


            //--------------------

        }
    }


    private String lower(String title) {
        return StringUtils.lowerCase(title);
    }


    //StackAdapterNoSelect adapter;

    private class RunTaskChannel extends AsyncTask<String, String, List<data>> {
        List<data> myData = new ArrayList<>();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        boolean prescroll = false;

        @Override
        protected List<data> doInBackground(String... params) {
            if (params[1].equals("0")) {
                resetPages();
            }
            int max = 0;
            String idDuration = "";
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
                    prescroll = true;
                } else {
                    scroll = false;
                }
                JSONArray mainArray = mObj.getJSONArray("items");
//                myData = new ArrayList<data>();
                JSONObject opage = mObj.getJSONObject("pageInfo");
                max = Math.min(opage.getInt("totalResults"), mainArray.length());
                for (int i = 0; i < max; i++) {

                    JSONObject mainObject0 = mainArray.getJSONObject(i);
                    JSONObject sousObject1 = mainObject0.getJSONObject("snippet");
                    String title = sousObject1.getString("title");
                    String channeltitle = sousObject1.getString("channelTitle");
                    JSONObject sousObject2 = sousObject1.getJSONObject("thumbnails");
                    JSONObject sousObject3 = sousObject2.getJSONObject("medium");
                    String imageUrl = sousObject3.getString("url");
                    String dates = sousObject1.getString("publishedAt");
                    //print(imageUrl);


                    JSONObject idOBject1 = mainObject0.getJSONObject("id");
                    String kind = idOBject1.getString("kind");

                    if (kind.equals("youtube#video")) {
                        String id = idOBject1.getString("videoId");
//                        idsInt.add(0);
//                        ids.add(id);

                        myData.add(new data(imageUrl, title, channeltitle, dates, 0, id));
                        idDuration += id + ",";
                    } else if (kind.equals("youtube#channel")) {
                        scroll = false;
                        String idchan = idOBject1.getString("channelId");
//                        ids.add(idchan);
//                        idsInt.add(1);
                        //System.out.println(idchan);
                        myData.add(new data(imageUrl, title, channeltitle, dates, 1, idchan));
                    } else if (kind.equals("youtube#playlist")) {
                        scroll = false;
//                        String idchan = sousObject1.getString("channelId");
                        String idchan = idOBject1.getString("playlistId");
                        //System.out.println("First one: "+idchan);
//                        ids.add(idchan);
//                        idsInt.add(2);
                        myData.add(new data(imageUrl, title, channeltitle, dates, 2, idchan));
                    } else {
                        continue;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = videoUrl + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + partDetail + and + "id=" + idDuration + and + key;

            try {
                getData = getJson(url);

                mObj = new JSONObject(getData);
                JSONArray items = mObj.getJSONArray("items");
                int kk = 0;
                for (int i = 0; i < myData.size(); i++) {

                    if (myData.get(i).getType() == 0) {
                        JSONObject child = items.getJSONObject(kk);
                        JSONObject innerChild = child.getJSONObject("contentDetails");
                        String duration = innerChild.getString("duration");
                        myData.get(i).setDuration(duration);


                        JSONObject innerChild2 = child.getJSONObject("statistics");
                        String Count = innerChild2.getString("viewCount");
                        myData.get(i).setCount(Long.parseLong(Count));

                        kk++;
                    } else {
                        continue;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return myData;
        }

        @Override
        protected void onPostExecute(List<data> myData) {
            if (isSafe) {
                myDataAll.addAll(myData);
                if (prescroll && pages.size() > 1)
                    scroll = true;
                if (myDataAll.size() > 30) {
                    //       System.out.println(myDataAll.size() + "Notify");
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter = new ListAdapter(myDataAll);
                    mRecyclerView.setAdapter(mAdapter);
                    if (!myDataAll.isEmpty()) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }

                ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if ((pref.getBoolean("noluck", true) && pref.getInt("inc", 0) >= MainStarterActivity.limitry)) {
                            if (bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2) || bp.isPurchased(Settings.adremove) || myDataAll.get(position).getType() == 1|| api.get_user_selection(SearchActivity.this)==1) {
                                if (myDataAll.get(position).getType() == 0) {
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Queries")
                                            .setAction("NEW CHOSEN 2D ID")
                                            .setLabel(myDataAll.get(position).getId())
                                            .build());
                                    if (lower(myDataAll.get(position).getTitle()).contains("sbs") ||
                                            lower(myDataAll.get(position).getTitle()).contains("yt3d")) {
                                        runMain(myDataAll.get(position).getId());
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("FORCE PLAY")
                                                .setAction("SBS")
                                                .build());
                                    } else {
                                        getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                    }
                                } else if (myDataAll.get(position).getType() == 1) {
                                    scroll = false;
                                    try {

                                        //System.out.println(url);
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("Chosen 2D")
                                                .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                .build());
                                        runHelperC(myDataAll.get(position).getId());
                                        //  new RunTaskChannel().execute(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                } else if (myDataAll.get(position).getType() == 2) {
                                    scroll = false;
                                    try {

                                        String url = playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                        //System.out.println(url);
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("Chosen 2D")
                                                .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                .build());
                                        runHelperP(myDataAll.get(position).getId());
                                        //  new RunTaskPlaylist().execute(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } else {
                                promptPurchase();
                                idrecord = myDataAll.get(position).getId();
                            }
                        } else {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory(force)
                                    .setAction("Free Play")
                                    .setValue(1)
                                    .build());
                            if (myDataAll.get(position).getType() == 0) {
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Queries")
                                        .setAction("NEW CHOSEN 2D ID")
                                        .setLabel(myDataAll.get(position).getId())
                                        .build());
                                if (lower(myDataAll.get(position).getTitle()).contains("sbs") ||
                                        lower(myDataAll.get(position).getTitle()).contains("yt3d")) {
                                    runMain(myDataAll.get(position).getId());
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("FORCE PLAY")
                                            .setAction("SBS")
                                            .build());
                                } else {
                                    getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                }
                            } else if (myDataAll.get(position).getType() == 1) {
                                scroll = false;
                                try {

                                    String url = searchUrl + MyAcessTokenData + and + part + and + max + and + channelId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                    //System.out.println(url);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Queries")
                                            .setAction("Chosen 2D")
                                            .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                            .build());
                                    runHelperC(myDataAll.get(position).getId());
//                                    new RunTaskChannel().execute(url);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            } else if (myDataAll.get(position).getType() == 2) {
                                scroll = false;
                                try {

                                    String url = playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                    //System.out.println(url);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Queries")
                                            .setAction("Chosen 2D")
                                            .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                            .build());
                                    runHelperP(myDataAll.get(position).getId());
//                                    new RunTaskPlaylist().execute(url);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                });

                ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        if (pref.getBoolean("noluck", true) && pref.getInt("inc", 0) >= MainStarterActivity.limitry) {
                            if (bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2) || bp.isPurchased(Settings.adremove) || myDataAll.get(position).getType() == 1|| api.get_user_selection(SearchActivity.this)==1) {
                                if (myDataAll.get(position).getType() == 0) {
                                    if (contain(myDataAll.get(position).getTitle(), "sbs")) {
                                        getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                    } else {
                                        runMain(myDataAll.get(position).getId());
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("New Chosen 2D LONG")
                                                .setLabel(myDataAll.get(position).getId())
                                                .build());
                                    }
                                } else if (myDataAll.get(position).getType() == 1) {
                                    scroll = false;
                                    try {
                                        String url = searchUrl + MyAcessTokenData + and + part + and + max + and + channelId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                        //System.out.println(url);
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("Chosen 2D")
                                                .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                .build());
                                        runHelperC(myDataAll.get(position).getId());
//                                        new RunTaskChannel().execute(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }

                            } else {

                                promptPurchase();
                                idrecord = myDataAll.get(position).getId();
                            }
                        } else {

                            if (myDataAll.get(position).getType() == 0) {
                                if (contain(myDataAll.get(position).getTitle(), "sbs")) {
                                    getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                } else {
                                    runMain(myDataAll.get(position).getId());
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Queries")
                                            .setAction("New Chosen 2D LONG")
                                            .setLabel(myDataAll.get(position).getId())
                                            .build());
                                }
                            } else if (myDataAll.get(position).getType() == 1) {
                                scroll = false;
                                try {
                                    String url = searchUrl + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + part + and + max + and + channelId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                    //System.out.println(url);
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Queries")
                                            .setAction("Chosen 2D")
                                            .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                            .build());
                                    runHelperC(myDataAll.get(position).getId());
//                                    new RunTaskChannel().execute(url);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }


                        }
                        return false;
                    }
                });



            }
            // pdial.hide();

        }


    }

    private class RunTaskPlaylist extends AsyncTask<String, String, List<data>> {
        List<data> myData = new ArrayList<>();

        @Override
        protected void onPreExecute() {
          //  pdial.show();
            super.onPreExecute();
        }

        boolean prescroll = false;

        @Override
        protected List<data> doInBackground(String... params) {

            if (params[1].equals("0")) {
                resetPages();
            }
            String getData = null;
            String idDuration = "";
            int max = 0;
            try {
                getData = getJson(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject mObj;

            try {
                mObj = new JSONObject(getData);
                if (!mObj.isNull("nextPageToken")) {
                    pages.add(mObj.getString("nextPageToken"));
                    // System.out.println("adding "+mObj.getString("nextPageToken"));
                    prescroll = true;
                } else {
                    scroll = false;
                }
                JSONArray mainArray = mObj.getJSONArray("items");
//                myData = new ArrayList<data>();
                JSONObject opage = mObj.getJSONObject("pageInfo");
                max = Math.min(opage.getInt("totalResults"), mainArray.length());
                for (int i = 0; i < max; i++) {

                    JSONObject mainObject0 = mainArray.getJSONObject(i);
                    JSONObject sousObject1 = mainObject0.getJSONObject("snippet");
                    String title = sousObject1.getString("title");
                    String channeltitle = sousObject1.getString("channelTitle");
                    JSONObject resource = sousObject1.getJSONObject("resourceId");
                    JSONObject sousObject2 = sousObject1.getJSONObject("thumbnails");
                    JSONObject sousObject3 = sousObject2.getJSONObject("medium");
                    String imageUrl = sousObject3.getString("url");
                    String dates = sousObject1.getString("publishedAt");
                    //print(imageUrl);


//                    JSONObject idOBject1 = mainObject0.getJSONObject("id");
                    String kind = mainObject0.getString("kind");

                    if (kind.equals("youtube#playlistItem")) {
                        scroll = false;
//                        String idchan = sousObject1.getString("channelId");
                        String idchan = resource.getString("videoId");
                        //System.out.println("First one: "+idchan);
//                        ids.add(idchan);
//                        idsInt.add(0);
                        idDuration += idchan + ",";
                        myData.add(new data(imageUrl, title, channeltitle, dates, 0, idchan));
                    } else {
                        continue;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = videoUrl + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none") + and + partDetail + and + "id=" + idDuration + and + key;

            try {
                getData = getJson(url);

                mObj = new JSONObject(getData);
                JSONArray items = mObj.getJSONArray("items");
                int kk = 0;
                for (int i = 0; i < myData.size(); i++) {

                    if (myData.get(i).getType() == 0) {
                        JSONObject child = items.getJSONObject(kk);
                        JSONObject innerChild = child.getJSONObject("contentDetails");
                        String duration = innerChild.getString("duration");
                        myData.get(i).setDuration(duration);

                        JSONObject innerChild2 = child.getJSONObject("statistics");
                        String Count = innerChild2.getString("viewCount");
                        myData.get(i).setCount(Long.parseLong(Count));

                        kk++;
                    } else {
                        continue;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return myData;
        }

        @Override
        protected void onPostExecute(List<data> myData) {
            if (isSafe) {
                myDataAll.addAll(myData);
                if (prescroll && pages.size() > 1)
                    scroll = true;
                if (myDataAll.size() > 30) {
                    //       System.out.println(myDataAll.size() + "Notify");
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter = new ListAdapter(myDataAll);
                    mRecyclerView.setAdapter(mAdapter);
                    if (!myDataAll.isEmpty()) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }

                ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if ((pref.getBoolean("noluck", true) && pref.getInt("inc", 0) >= MainStarterActivity.limitry)) {
                            if (bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2) || bp.isPurchased(Settings.adremove) || myDataAll.get(position).getType() == 1|| api.get_user_selection(SearchActivity.this)==1) {
                                if (myDataAll.get(position).getType() == 0) {
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Queries")
                                            .setAction("NEW CHOSEN 2D ID")
                                            .setLabel(myDataAll.get(position).getId())
                                            .build());
                                    if (lower(myDataAll.get(position).getTitle()).contains("sbs") ||
                                            lower(myDataAll.get(position).getTitle()).contains("yt3d")) {
                                        runMain(myDataAll.get(position).getId());
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("FORCE PLAY")
                                                .setAction("SBS")
                                                .build());
                                    } else {
                                        getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                    }
                                } else if (myDataAll.get(position).getType() == 1) {
                                    scroll = false;
                                    try {

                                        //System.out.println(url);
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("Chosen 2D")
                                                .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                .build());
                                        runHelperC(myDataAll.get(position).getId());
                                        //  new RunTaskChannel().execute(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                } else if (myDataAll.get(position).getType() == 2) {
                                    scroll = false;
                                    try {

                                        String url = playlistItem + MyAcessTokenData + and + part + and + max + and + playlistId + URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8") + and + key;
                                        //System.out.println(url);
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("Chosen 2D")
                                                .setLabel(URLEncoder.encode(myDataAll.get(position).getId(), "UTF-8"))
                                                .build());
                                        runHelperP(myDataAll.get(position).getId());
                                        //  new RunTaskPlaylist().execute(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } else {
                                promptPurchase();
                                idrecord = myDataAll.get(position).getId();
                            }
                        } else {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory(force)
                                    .setAction("Free Play")
                                    .setValue(1)
                                    .build());
                            if (myDataAll.get(position).getType() == 0) {
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Queries")
                                        .setAction("NEW CHOSEN 2D ID")
                                        .setLabel(myDataAll.get(position).getId())
                                        .build());
                                if (lower(myDataAll.get(position).getTitle()).contains("sbs") ||
                                        lower(myDataAll.get(position).getTitle()).contains("yt3d")) {
                                    runMain(myDataAll.get(position).getId());
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("FORCE PLAY")
                                            .setAction("SBS")
                                            .build());
                                } else {
                                    getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                }
                            }

                        }
                    }
                });

                ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                        if (pref.getBoolean("noluck", true) && pref.getInt("inc", 0) >= MainStarterActivity.limitry) {
                            if (bp.isPurchased(Settings.unlock) || bp.isPurchased(Settings.unlock2) || bp.isPurchased(Settings.adremove) || myDataAll.get(position).getType() == 1|| api.get_user_selection(SearchActivity.this)==1) {
                                if (myDataAll.get(position).getType() == 0) {
                                    if (contain(myDataAll.get(position).getTitle(), "sbs")) {
                                        getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                    } else {
                                        runMain(myDataAll.get(position).getId());
                                        mTracker.send(new HitBuilders.EventBuilder()
                                                .setCategory("Queries")
                                                .setAction("New Chosen 2D LONG")
                                                .setLabel(myDataAll.get(position).getId())
                                                .build());
                                    }
                                }

                            } else {
                                idrecord = myDataAll.get(position).getId();
                                promptPurchase();
                            }
                        } else {

                            if (myDataAll.get(position).getType() == 0) {
                                if (contain(myDataAll.get(position).getTitle(), "sbs")) {
                                    getURI(myDataAll.get(position).getId(), is360(myDataAll.get(position).getTitle(), myDataAll.get(position).getChannel()));
                                } else {
                                    runMain(myDataAll.get(position).getId());
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("Queries")
                                            .setAction("New Chosen 2D LONG")
                                            .setLabel(myDataAll.get(position).getId())
                                            .build());
                                }
                            }


                        }
                        return false;
                    }
                });
            }
//            pdial.hide();

        }

    }

    Boolean isSafe;

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {

        app.getInstance().setConnectivityListener(this);
        chosen = false;
        isSafe = true;
        mTracker.setScreenName("List search");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        pdial = new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.loading))
                .backgroundColor(Color.parseColor("#d9b38c"))
                .content(getResources().getString(R.string.please_wait))
                .progress(true, 0)
                .build();
        super.onResume();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        isSafe = false;
        if (bp != null) {
            bp.release();
        }
    }

    // List<data> myData = new ArrayList<data>();
    List<data> myDataAll;

    String accountName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result, true);
        }
    }

    void convertUser(String googleIdToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleIdToken, null);
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "linkWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //     System.out.println("Failed to convert");
                        }

                        // ...
                    }
                });

    }

    SharedPreferences.Editor editor;

    boolean chosen;

    void getURI(final String id, final boolean is360) {
        if (!chosen) {


            final DateTime dt = new DateTime();
            final long time = dt.getDayOfMonth();
            //    System.out.println(time);
            boolean istime = false;
            String youtubeLink;
            youtubeLink = "http://youtube.com/watch?v=" + id;
            // }
            chosen = true;
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
                                    .setCategory("itag")
                                    .setAction("chosen")
                                    .setLabel("22")
                                    .build());
                        } else if (key.contains(18)) {
                            downloadUrl = ytFiles.get(18).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itag")
                                    .setAction("defaulted to")
                                    .setLabel("18")
                                    .build());
                        } else if (key.contains(22)) {
                            downloadUrl = ytFiles.get(22).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itag")
                                    .setAction("defaulted to")
                                    .setLabel("22")
                                    .build());
                        } else if (key.contains(36)) {
                            downloadUrl = ytFiles.get(36).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itag")
                                    .setAction("defaulted to")
                                    .setLabel("36")
                                    .build());
                        } else if (key.contains(17)) {
                            downloadUrl = ytFiles.get(17).getUrl();
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("itag")
                                    .setAction("defaulted to")
                                    .setLabel("17")
                                    .build());
                        } else {
                            chosen = false;
                        }
                        System.out.println("Url------------>>> :" + downloadUrl);
                        if (downloadUrl != null && !downloadUrl.isEmpty()) {

//                            Intent intent = new Intent(SearchActivity.this, VideoPlayerActivity.class);
//                            intent.putExtra(MainStarterActivity.vidId, downloadUrl);
//                            startActivity(intent);


                            if (false) {
                                editor.putLong("vrlooptime", time);
                                editor.commit();
                                //   System.out.println("time is committed "+time);
                                Intent intent = new Intent(SearchActivity.this, SimpleVrVideoActivity.class);
                                if (id.equals("id")) {
                                    intent.putExtra(MainStarterActivity.vidId, idrecord);
                                } else {
                                    intent.putExtra(MainStarterActivity.vidId, id);
                                }
                                intent.putExtra(MainStarterActivity.vidIdbool, is360);
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
                            } else {
                                if (is360) {
                                    play360(downloadUrl);
                                } else {
                                    Intent intent = new Intent(SearchActivity.this, VideoPlayerActivity.class);
                                    intent.putExtra(MainStarterActivity.vidId, downloadUrl);
                                    startActivity(intent);
                                }
                            }
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

    // [START signOut]
    public void signOut(View v) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//                        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
                        extraBar.setVisibility(View.GONE);
                        // [END_EXCLUDE]
                    }
                });
    }

    // [END signOut]
    public void picex(View v) {
//        Intent id = new Intent(MainStarterActivity.this,
//                ShowAd.class);
//        startActivity(id);https://play.google.com/store/apps/details?id=fart.ekm.com.vrimgviewerreal3d
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.ekm.flapbird"));
        startActivity(intent);


        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(force)
                .setAction("Other")
                .setLabel("Flaps")
                .build());

    }

    private class getDataWithToken extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            playlists = new ArrayList<>();

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
            JSONObject mObj;

            try {
                mObj = new JSONObject(getData);
                JSONArray mainArray = mObj.getJSONArray("items");
                JSONObject mainObject0 = mainArray.getJSONObject(0);
                JSONObject sousObject1 = mainObject0.getJSONObject("contentDetails");
                JSONObject sousObject2 = sousObject1.getJSONObject("relatedPlaylists");

                playlists.add(sousObject2.getString("likes"));

                playlists.add(sousObject2.getString("favorites"));

                playlists.add(sousObject2.getString("uploads"));

                playlists.add(sousObject2.getString("watchHistory"));

                playlists.add(sousObject2.getString("watchLater"));

                //  System.out.println(sousObject2.getString("watchLater"));


            } catch (JSONException e) {
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            if (playlists.size() > 3) {
                extraBar.setVisibility(View.VISIBLE);
                extraBar.selectTabWithId(R.id.tab_search);
            }


        }

    }

    void getToken() throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", DeveloperKey.ClientID)
                .add("client_secret", DeveloperKey.Secret)
                .add("redirect_uri", "")
                .add("code", pref.getString(DeveloperKey.TokenID, "none"))
                .build();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                pdial.hide();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    editor.putString(DeveloperKey.AcessToken, jsonObject.getString("access_token"));
                    editor.commit();

                    String url = channelsUrl + and + MyAcessTokenData + pref.getString(DeveloperKey.AcessToken, "none");
                    System.out.println(url);
                    new getDataWithToken().execute(url);

                    // final String message = jsonObject.toString(5);
                    // Log.i(TAG, message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    void runMain(final String id) {

        {

//                            Intent intent = new Intent(SearchActivity.this, VideoPlayerActivity.class);
//                            intent.putExtra(MainStarterActivity.vidId, downloadUrl);
//                            startActivity(intent);
            final DateTime dt = new DateTime();
            final long time = dt.getDayOfMonth();
//            long time = 7;

            //    System.out.println("timefactor: "+time+ "|"+pref.getLong("vrlooptime", 0));
            boolean istime = false;
//                            System.out.println(time);
//                            System.out.println(pref.getLong("vrlooptime", time));
//                            System.out.println(time-pref.getLong("vrlooptime", time));
//            if (time <30 && pref.getLong("vrlooptime", 0)>=30 ) {
//                istime = true;
//            }
//           else  if (time>pref.getLong("vrlooptime", 0) || pref.getLong("vrlooptime", 0)==0 || pref.getLong("vrlooptime", 0)>31) {
//                istime=true;
//            }

            if (false) {
                String youtubeLink = "http://youtube.com/watch?v=" + "QEidwK1oiM0";
                YouTubeUriExtractor ytEx = new YouTubeUriExtractor(this) {
                    @Override
                    public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                        String downloadUrl = "";
                        if (ytFiles != null) {
                            ArrayList<Integer> key = new ArrayList<>();
                            for (int i = 0; i < ytFiles.size(); i++) {
                                key.add(ytFiles.keyAt(i));
                                //  System.out.println(key.get(i));
                            }
                            if (key.contains(22)) {
                                downloadUrl = ytFiles.get(22).getUrl();
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("itag")
                                        .setAction("defaulted to")
                                        .setLabel("22")
                                        .build());
                            } else {
                                chosen = false;
                            }
                            //   System.out.println("Url------------>>> :" + downloadUrl);
                            if (downloadUrl != null && !downloadUrl.isEmpty()) {

                                editor.putLong("vrlooptime", time);
                                editor.commit();
//                                System.out.println("time is committed "+time);
                                Intent intent = new Intent(SearchActivity.this, SimpleVrVideoActivity.class);
                                intent.putExtra(MainStarterActivity.vidId, id);
//                                intent.putExtra(SimpleVrVideoActivity.TAGVIDEO_URL, MainStarterActivity.strVideoDownloadPath);
                                intent.putExtra(SimpleVrVideoActivity.TAG_VIDEO_URL, downloadUrl);
                                startActivity(intent);

                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("FruitLoop")
                                        .setAction("Running AD")
                                        .setLabel("3D")
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
//                                System.out.println("Download urL is null");
                                chosen = false;
                            }

                        } else {
//                            System.out.println("YTFILES is null");
                            chosen = false;
                            // BACKUP Plan
                        }
                    }
                };
                ytEx.setIncludeWebM(false);
                ytEx.execute(youtubeLink);

            } else {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.putExtra(MainStarterActivity.vidId, id);
                startActivity(intent);
            }
        }
    }

    ArrayList<String> playlists;

    private void play360(String url) {
        Intent intent = new Intent(this, VrVideoActivity.class);
        intent.putExtra(VrVideoActivity.TAG_VIDEO_URL, url);
        startActivity(intent);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("New 360")
                .setAction("played")
                .setLabel(url)
                .setValue(1)
                .build());
    }

    private boolean is360(String title2, String title) {
        if (title2.contains("(360 Video)") ||
                title2.contains("360° Video") ||
                title2.contains("360° VR") ||
                title2.contains("360° Virtual Reality") ||
                title2.contains("360° DUBSTEP") ||
                title2.contains("360 video") ||
                title2.contains("360° Video Experience") ||
                title2.contains("360° Music Video") ||
                title2.contains("360 VIDEO") ||
                title2.contains("360 CAM") ||
                title2.contains("360-degree") ||
                title2.contains("360°") ||
                title2.contains("360") ||


                title.contains("(360 Video)") ||
                title.contains("360° Video") ||
                title.contains("360° VR") ||
                title.contains("360° Virtual Reality") ||
                title.contains("360° DUBSTEP") ||
                title.contains("360 video") ||
                title.contains("360° Video Experience") ||
                title.contains("360° Music Video") ||
                title.contains("360 VIDEO") ||
                title.contains("360 CAM") ||
                title.contains("360-degree") ||
                title.contains("360°") ||
                title.contains("360")) {


            return true;
        } else {
            return false;
        }
    }

    void notConnected() {
        new MaterialDialog.Builder(SearchActivity.this)
                .title("No connection found")
                .titleColorRes(R.color.fab_color_emerald)
                .content("App must be connected to the internet in order to function")
                .positiveText("Ok")
                .backgroundColor(Color.parseColor("#ffffff"))
                .contentColor(Color.parseColor("#000000"))
                .negativeColorRes(R.color.fab_color_wet_asphalt)
                .positiveColorRes(R.color.fab_color_emerald)
                .show();
    }

}
