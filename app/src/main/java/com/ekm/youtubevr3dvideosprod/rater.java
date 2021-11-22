package com.ekm.youtubevr3dvideosprod;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.ekm.youtubevr3dvideosprod.MainStarterActivity.adFrequency;


public class rater extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView txtRatingValue;
    private Button btnSubmit, btnsubmit2;
    private RadioGroup rad;
    private EditText comment;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rater);
        mDatabase = FirebaseDatabase.getInstance().getReference();

      //  addListenerOnRatingBar();
        addListenerOnButton();

        rad = (RadioGroup) findViewById(R.id.radg);
        rad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.hd:
                        commentFree();
                        break;
                    case R.id.sd:
                        comment();
                        break;
                    case R.id.gp:
                        comment();
                        break;
                }
            }
        });

    }
    public void addListenerOnButton() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.button3);
        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if (ratingBar.getRating()>3.0f) {
    startActivity(new Intent(Intent.ACTION_VIEW,
            Uri.parse("http://play.google.com/store/apps/details?id=" + Uri.parse("market://details?ids=com.ekm.youtubevr3dvideos"))));
//    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?ids=com.ekm.youtubevr3dvideos")));
} else {
    btnSubmit.setVisibility(View.GONE);
    rad.setVisibility(View.VISIBLE);
}
            }

        });

    }
    private DatabaseReference mDatabase;
    private  void comment() {
//        rad.setEnabled(false);
        rad.setEnabled(false);
        btnsubmit2 = (Button) findViewById(R.id.button6);
        ratingBar.setEnabled(false);
        btnSubmit.setVisibility(View.GONE);
        comment = (EditText) findViewById(R.id.comment);
        comment.setVisibility(View.VISIBLE);

        btnsubmit2.setVisibility(View.VISIBLE);
        addListenerOnButton2();
    }
    private  void commentFree() {
//        rad.setEnabled(false);
        rad.setEnabled(false);
        btnsubmit2 = (Button) findViewById(R.id.button6);
        ratingBar.setEnabled(false);
        btnSubmit.setVisibility(View.GONE);
        comment = (EditText) findViewById(R.id.comment);
        comment.setVisibility(View.VISIBLE);

        btnsubmit2.setVisibility(View.VISIBLE);
//        addListenerOnButtonFree();
addListenerOnButton2();
    }

    public void addListenerOnButton2() {
        btnsubmit2 = (Button) findViewById(R.id.button6);
        //if click on me, then display the current rating value.
        btnsubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getText().toString().trim().length() > 0)
                mDatabase.child("complaints").child(DeveloperKey.userid).setValue(comment.getText()+"");


                new MaterialDialog.Builder(rater.this)
                        .title("Thanks")
                        .titleColorRes(R.color.fab_color_emerald)
                        .content("Your input is valuable to us. Thank you for rating this app. ")
                        .positiveText(R.string.ok)
                        .backgroundColor(Color.parseColor("#ffffff"))
                        .contentColor(Color.parseColor("#000000"))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .show();
            }

        });

    }
    public void addListenerOnButtonFree() {
        pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        editor = pref.edit();
        editor.putBoolean("noluck", false);
        editor.commit();
        MainStarterActivity.adFrequency = true;
        btnsubmit2 = (Button) findViewById(R.id.button6);
        //if click on me, then display the current rating value.
        btnsubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getText().toString().trim().length() > 0)
                mDatabase.child("complaints").child(DeveloperKey.userid).setValue(comment.getText()+"");

                mDatabase.child("submitted").child(DeveloperKey.userid).setValue("Free app");


                new MaterialDialog.Builder(rater.this)
                        .title("App is now free!")
                        .titleColorRes(R.color.fab_color_emerald)
                        .content("This app is now free of charge. Your input is valuable to us. Thank you for your support.")
                        .positiveText(R.string.ok)
                        .backgroundColor(Color.parseColor("#ffffff"))
                        .contentColor(Color.parseColor("#000000"))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                                Intent id = new Intent(rater.this,
                                        SearchActivity.class);
                                startActivity(id);
                            }
                        })
                        .show();
            }

        });

    }
}
