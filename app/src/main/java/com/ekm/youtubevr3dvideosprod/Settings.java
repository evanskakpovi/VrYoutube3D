package com.ekm.youtubevr3dvideosprod;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.vrtoolkit.cardboard.CardboardActivity;

import java.util.ArrayList;

import io.topvpn.vpn_api.api;

public class Settings extends CardboardActivity implements BillingProcessor.IBillingHandler {
        BillingProcessor bp;

    private SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<RadioButton> rad;
    RadioGroup rdg1;

    int pos = 0;
    public static final String unlock = "unlock";
    public static final String unlock2 = "unlocker2";
    final String blueborder="borderblue";
    final String redborder="borderred";
    final String greenborder="bordergreen";
    final String tvborder="bordertv";
    final String metborder="bordermet";
    final String woodborder="borderwood";
    final String realtvborder="borderrealtv";
    final String holborder="borderhol";
    final String yellowborder = "borderyellow";
    final String orangeborder = "borderorange";
    final String purpleborder = "borderpurple";
    final String whiteborder = "borderwhite";
    final String noborder = "borderno";
    final String bonusborder = "abonus";
   public static final String adremove="removead";

    public static boolean ad = true;

    public static ArrayList<Integer> colors;


    static final int bordergreen = R.drawable.bordo;
    static final int borderred = R.drawable.bordo2;
    static final int borderblue = R.drawable.bordo3;
    static final int bordertv = R.drawable.bordo4;
    static final int bordermet = R.drawable.bordo5;
    static final int borderwood = R.drawable.bordo9;
    static final int borderreatv = R.drawable.bordo11;
    static final int borderhol = R.drawable.bordo12;
    static final int borderyellow = R.drawable.bordoyellow;
    static final int borderorange = R.drawable.bordoorange;
    static final int borderpurple = R.drawable.bordopurple;
    static final int borderwhite = R.drawable.bordowhite;
    static final int borderno = R.drawable.noborder;
    public static final int borderDefault = borderreatv;
    private Tracker mTracker;
    private EditText q;
    public static String isVrBlock;
    MaterialDialog codeMsg, errMsg, acceptMsg;
    Switch effect3d, luminati;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        bp = new BillingProcessor(this, DeveloperKey.BILLING_KEY, this);


        runeffect3d();
        runLuminati();
        // Obtain the shared Tracker instance.
        app application = (app) getApplication();
        mTracker = application.getDefaultTracker();

        rdg1 = (RadioGroup) findViewById(R.id.radioGroup1);

        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        editor = pref.edit();
       pos = pref.getInt("border", borderDefault);

        bp.loadOwnedPurchasesFromGoogle();

        SeekBar mBar = (SeekBar) findViewById(R.id.seekBar2);
        mBar.setMax(400);
        mBar.setProgress(pref.getInt("zoomer", 30));

        codeMsg=  new MaterialDialog.Builder(Settings.this)
                .title("Code status")
                .titleColorRes(R.color.fab_color_emerald)
                .content("Invalid entry. Please check your code.")
                .positiveText("Ok")
                .backgroundColor(Color.parseColor("#ffffff"))
                .contentColor(Color.parseColor("#000000"))
                .build();

        errMsg =  new MaterialDialog.Builder(Settings.this)
                .title("Code status")
                .titleColorRes(R.color.fab_color_emerald)
                .content("Failed to check. Please check your data connection.")
                .positiveText("Ok")
                .backgroundColor(Color.parseColor("#ffffff"))
                .contentColor(Color.parseColor("#000000"))
                .build();

        acceptMsg =  new MaterialDialog.Builder(Settings.this)
                .title("Code status")
                .titleColorRes(R.color.fab_color_emerald)
                .content("Code accepted")
                .positiveText("Done")
                .backgroundColor(Color.parseColor("#ffffff"))
                .contentColor(Color.parseColor("#000000"))
                .build();

        mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int zoom, boolean fromUser) {

                editor.putInt("zoomer", zoom);
                editor.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Zoom New")
                        .setValue(pref.getInt("zoomer", 50))
                        .build());
            }
        });
//       gridview = (GridView) findViewById(R.ids.gridView);
//        gridview.setAdapter(new ImageAdapter2(this));
//        gridview.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long ids) {
//                setFrame(colors.get(position));
////                gridview.setBackgroundResource(R.drawable.check2);
//            }
//        });

        rad = new ArrayList<RadioButton>();
        rad.add((RadioButton) findViewById(R.id.radioButton));
        rad.add((RadioButton) findViewById(R.id.radioButton2));
        rad.add((RadioButton) findViewById(R.id.radioButton3));
        rad.add((RadioButton) findViewById(R.id.radioButton4));
        rad.add((RadioButton) findViewById(R.id.radioButton5));
        rad.add((RadioButton) findViewById(R.id.radioButton6));
        rad.add((RadioButton) findViewById(R.id.radioButton7));
        rad.add((RadioButton) findViewById(R.id.radioButton1));
        rad.add((RadioButton) findViewById(R.id.radioButton8));
        rad.add((RadioButton) findViewById(R.id.radioButton9));
        rad.add((RadioButton) findViewById(R.id.radioButton10));
        rad.add((RadioButton) findViewById(R.id.radioButton11));
        rad.add((RadioButton) findViewById(R.id.radioButton12));
        rad.add((RadioButton) findViewById(R.id.radioButton13));

        q = (EditText) findViewById(R.id.code);
        q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                q.setEnabled(true);
                q.setCursorVisible(true);
//                q.setBackgroundResource(R.drawable.picb46);

            }
        });
        q.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    checkCode(q.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        q.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (q.getRight() - q.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkCode(q.getText().toString());
                        return true;
                    }
                }
                return false;
            }
        });

    }
    public static String effect3did = "effect3d";
    public static String luminatiId = "luminatioptin";
    private void runeffect3d() {
        effect3d = (Switch) findViewById(R.id.effect3d);
        effect3d.setChecked(pref.getBoolean(effect3did,true));
        effect3d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if (ischecked){
                    editor.putBoolean(effect3did, true);
                    editor.commit();
                }else {
                    editor.putBoolean(effect3did,false);
                    editor.commit();
                }
            }
        });
    }

    private void runLuminati() {
        luminati = (Switch) findViewById(R.id.luminati);
        luminati.setChecked(pref.getBoolean(luminatiId,false));
        luminati.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if (ischecked){
                    start_luminati_sdk(Settings.this);

                }else {

                    mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Luminati")
                        .setAction("NOT PEER in Settings")
                        .setValue(1)
                        .build());
                    api.clear_selection(Settings.this);
                    editor.putBoolean(luminatiId,false);
                    editor.commit();
                }
            }
        });
    }
    void start_luminati_sdk(final Context ctx){
        api.set_dialog_type(api.DIALOG_TYPE.PEER1);
        api.set_tos_link("http://futureappsllc.000webhostapp.com/#privacy-policy");
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
                        editor.putBoolean(luminatiId, true);
                        editor.commit();
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Luminati")
                                .setAction("PEER")
                                .setValue(1)
                                .build());
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Luminati")
                                .setAction("PEER in Settings")
                                .setValue(1)
                                .build());
                        break;
//                    case api.CHOICE_FREE:
//                        myapp.options.set_luminati_sdk_enabled();
//                        break;
                    case api.CHOICE_NOT_PEER:
                        luminati.setChecked(false);
                        break;
                    case api.CHOICE_NONE:
                        luminati.setChecked(false);
                        break;
//                        myapp.options.set_luminati_sdk_disabled();
                }
            }
        });
        api.init(Settings.this, false);
    }

    //    public void fun1() {
//        rdg1.setOnCheckedChangeListener(null);
//        rdg1.clearCheck();
//        rdg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                fun2();
//                Log.v("Inside fun1", "fun2");
//            }
//        });
//    }
//    public void fun2() {
////        rdg2.setOnCheckedChangeListener(null);
////        rdg2.clearCheck();
////        rdg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
////
////            @Override
////            public void onCheckedChanged(RadioGroup group, int checkedId) {
////                fun1();
////                Log.v("Inside fun2", "fun1");
////            }
////        });
//    }
    public  void adremove(View v) {
        if (!bp.isPurchased(adremove)){
            bp.purchase( this, adremove);
        } else {
            Button ad = (Button) findViewById(R.id.button);
            ad.setVisibility(View.GONE);
            MainStarterActivity.ads = false;
        }
    }

    void mEvent(String n ) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Buy Attempt")
                .setAction(n+"")
                .build());
    }

    void checkCode(final String code) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("code");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("Settings", "Value is: " + value);
                if (code.equals(value)) {
                   acceptMsg.show();

                    editor = pref.edit();
                    editor.putBoolean(isVrBlock, true);
                    editor.commit();

                    if (code.equals("ekmvr")) {
                        editor = pref.edit();
                        editor.putInt("inc",  -9999);
                        editor.commit();
                        System.out.println("okkkkkkkkkkkkkkkkkkkkk");
                    }
                } else {
                   codeMsg.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Settings", "Failed to read value.", error.toException());
               errMsg.show();
            }
        });
    }
    @Override
    protected void onResume() {


//        gridview.requestFocus();
//        gridview.setSelection(colors.indexOf(pos));
//       gridview.getSelectedView().setSelected(true);
        // gridview.setItemChecked(1, false);
        mTracker.setScreenName("Spice Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        for (int i=0; i<rad.size(); i++) {

            final int fi = i;
            rad.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bp.isPurchased(bonusborder)) {
                        switch (fi) {
                            case 0:
                                setFrame(bordergreen);
                                break;
                            case 1:
                                setFrame(borderred);
                                break;
                            case 2:
                                setFrame(borderblue);
                                break;
                            case 3:
                                setFrame(bordertv);
                                break;
                            case 4:
                                setFrame(bordermet);
                                break;
                            case 5:
                                setFrame(borderwood);
                                break;
                            case 6:
                                setFrame(borderreatv);
                                break;
                            case 7:
                                setFrame(borderhol);
                                break;
                            case 8:
                                setFrame(borderyellow);
                                break;
                            case 9:
                                setFrame(borderorange);
                                break;
                            case 10:
                                setFrame(borderpurple);
                                break;
                            case 11:
                                setFrame(borderwhite);
                                break;
                            case 13:
                                setFrame(borderno);
                                break;
                            case 12:
                                setFrame(borderno);
                                break;
                        }


                    } else

                    {
                        switch (fi) {
                            case 0:
                                if (bp.isPurchased(greenborder)) {
                                    setFrame(bordergreen);

                                } else {
                                    bp.purchase(Settings.this, greenborder);
                                    mEvent(greenborder);
                                }

                                break;
                            case 1:
                                if (bp.isPurchased(redborder)) {
                                    setFrame(borderred);
                                } else {
                                    bp.purchase(Settings.this, redborder);
                                    mEvent(redborder);
                                }
                                break;
                            case 2:
                                if (bp.isPurchased(blueborder)) {
                                    setFrame(borderblue);
                                } else {
                                    bp.purchase(Settings.this, blueborder);
                                    mEvent(blueborder);
                                }
                                break;
                            case 3:
                                if (bp.isPurchased(tvborder)) {
                                    setFrame(bordertv);
                                } else {
                                    bp.purchase(Settings.this, tvborder);
                                    mEvent(tvborder);
                                }
                                break;
                            case 4:
                                if (bp.isPurchased(metborder)) {
                                    setFrame(bordermet);
                                } else {
                                    bp.purchase(Settings.this, metborder);
                                    mEvent(metborder);
                                }
                                break;
                            case 5:
//                            if (/*bp.isPurchased(woodborder)*/true) {
                                setFrame(borderwood);
//
                                break;
                            case 6:
                                if (bp.isPurchased(realtvborder)) {
                                    setFrame(borderreatv);
                                } else {
                                    bp.purchase(Settings.this, realtvborder);
                                    mEvent(realtvborder);
                                }
                                break;
                            case 7:
                                if (bp.isPurchased(holborder)) {
                                    setFrame(borderhol);
                                } else {
                                    bp.purchase(Settings.this, holborder);
                                    mEvent(holborder);
                                }
                                break;
                            case 8:
                                if (bp.isPurchased(yellowborder)) {
                                    setFrame(borderyellow);
                                } else {
                                    bp.purchase(Settings.this, yellowborder);
                                    mEvent(yellowborder);
                                }
                                break;
                            case 9:
                                if (bp.isPurchased(orangeborder)) {
                                    setFrame(borderorange);
                                } else {
                                    bp.purchase(Settings.this, orangeborder);
                                    mEvent(orangeborder);
                                }
                                break;
                            case 10:
                                if (bp.isPurchased(purpleborder)) {
                                    setFrame(borderpurple);
                                } else {
                                    bp.purchase(Settings.this, purpleborder);
                                    mEvent(purpleborder);
                                }
                                break;
                            case 11:
                                if (bp.isPurchased(whiteborder)) {
                                    setFrame(borderwhite);
                                } else {
                                    bp.purchase(Settings.this, whiteborder);
                                    mEvent(whiteborder);
                                }
                                break;
                            case 13:
                                if (bp.isPurchased(bonusborder)) {
                                    setFrame(borderno);
                                } else {
                                    bp.purchase(Settings.this, bonusborder);
                                    mEvent(bonusborder);
                                }
                                break;
                            case 12:
                                if (bp.isPurchased(noborder)) {
                                    setFrame(borderno);
                                } else {
                                    bp.purchase(Settings.this, noborder);
                                    mEvent(noborder);
                                }
                                break;
                        }

                    }
                }
            });
        }

        if (bp.isPurchased(adremove)){
            Button ad = (Button) findViewById(R.id.button);
            ad.setVisibility(View.GONE);
            editor = pref.edit();
            editor.putBoolean("ad", false);
            editor.commit();
        }

        restorer();
//System.out.println(pos+"---"+borderblue+"---"+bordergreen+"---"+borderred);


        switch (pos) {
            case borderblue:

                rad.get(2).setChecked(true);
                break;
            case bordergreen:

                rad.get(0).setChecked(true);
                break;
            case borderred:

                rad.get(1).setChecked(true);
                break;
            case bordertv:

                rad.get(3).setChecked(true);
                break;
            case bordermet:

                rad.get(4).setChecked(true);
                break;
            case borderwood:

                rad.get(5).setChecked(true);
                break;
            case borderreatv:

                rad.get(6).setChecked(true);
                break;
            case borderhol:

                rad.get(7).setChecked(true);
                break;
            case borderyellow:

                rad.get(8).setChecked(true);
                break;
            case borderorange:

                rad.get(9).setChecked(true);
                break;
            case borderpurple:

                rad.get(10).setChecked(true);
                break;
            case borderwhite:

                rad.get(11).setChecked(true);
                break;
            case borderno:

                rad.get(12).setChecked(true);
                break;
        }
        super.onResume();
    }


    public void consume(View v) {
//        bp.consumePurchase(redborder);
//        bp.consumePurchase(greenborder);
//        bp.consumePurchase(blueborder);
//        bp.consumePurchase(tvborder);


    }

    private void setFrame(int border) {
        editor = pref.edit();
        editor.putInt("border", border);
        editor.commit();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Set Frame")
                .setAction("Frame")
                .setLabel(border + "")
                .build());
        System.out.println(border);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.settings, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // The rest of your onStart() code.
        //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        // The rest of your onStop() code.
        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
boolean allfree = false;
    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {
        SkuDetails nd = bp.getPurchaseListingDetails(transactionDetails.productId);
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Bought")
                .setAction("Product")
                .setLabel(nd.title)
                .build());


        if (bp.isPurchased(adremove)){
            Button ad = (Button) findViewById(R.id.button);
            ad.setVisibility(View.GONE);
            editor = pref.edit();
            editor.putBoolean("ad", false);
            editor.commit();
            MainStarterActivity.ads = false;
        }
         if (bp.isPurchased(bonusborder)) {
             allfree = true;
             editor = pref.edit();
             editor.putBoolean("allfree", true);
             editor.commit();
         }

        restorer();

    }

    @Override
    public void onPurchaseHistoryRestored() {
       restorer();
    }

    void restorer() {
        if (bp.isPurchased(bonusborder)) {
            rad.get(0).setButtonDrawable(R.drawable.selectord);
            rad.get(1).setButtonDrawable(R.drawable.selectord);
            rad.get(2).setButtonDrawable(R.drawable.selectord);
            rad.get(3).setButtonDrawable(R.drawable.selectord);
            rad.get(4).setButtonDrawable(R.drawable.selectord);
            rad.get(5).setButtonDrawable(R.drawable.selectord);
            rad.get(6).setButtonDrawable(R.drawable.selectord);
            rad.get(7).setButtonDrawable(R.drawable.selectord);
            rad.get(8).setButtonDrawable(R.drawable.selectord);
            rad.get(9).setButtonDrawable(R.drawable.selectord);
            rad.get(10).setButtonDrawable(R.drawable.selectord);
            rad.get(11).setButtonDrawable(R.drawable.selectord);
            rad.get(12).setButtonDrawable(R.drawable.selectord);
            rad.get(13).setVisibility(View.GONE);
        } else {
        if (bp.isPurchased(blueborder)) {
            rad.get(2).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(redborder)) {
            rad.get(1).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(greenborder)) {
            rad.get(0).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(tvborder)) {
            rad.get(3).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(metborder)) {
            rad.get(4).setButtonDrawable(R.drawable.selectord);}
        if (/*bp.isPurchased(woodborder)*/true) {
            rad.get(5).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(realtvborder)) {
            rad.get(6).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(holborder)) {
            rad.get(7).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(yellowborder)) {
            rad.get(8).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(orangeborder)) {
            rad.get(9).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(purpleborder)) {
            rad.get(10).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(whiteborder)) {
            rad.get(11).setButtonDrawable(R.drawable.selectord);}
        if (bp.isPurchased(noborder)) {
            rad.get(12).setButtonDrawable(R.drawable.selectord);}}
    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Error buying")
                .setAction("Failed")
                .build());
    }

    @Override
    public void onBillingInitialized() {
        System.out.println("BUying");
    }
    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
}
