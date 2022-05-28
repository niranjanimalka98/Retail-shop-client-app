package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Register extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    TextInputLayout name, storename, tin, b3, ph1, ph2, more, gst;
    EditText ename, estorename, etin, p15, eph1, eph2, emore, egst;
    private DatabaseReference du;
    private FirebaseAuth mAuth;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        setTitle("Register");

        MobileAds.initialize(this, String.valueOf(R.string.app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        name = (TextInputLayout) findViewById(R.id.input_layout_name);
        storename = (TextInputLayout) findViewById(R.id.input_layout_store_name);
        tin = (TextInputLayout) findViewById(R.id.input_layout_tin);
        b3 = (TextInputLayout) findViewById(R.id.input_layout_addr);
        ph1 = (TextInputLayout) findViewById(R.id.input_layout_ph1);
        ph2 = (TextInputLayout) findViewById(R.id.input_layout_ph2);
        more = (TextInputLayout) findViewById(R.id.input_layout_more);
        gst = (TextInputLayout) findViewById(R.id.input_layout_gst);

        ename = (EditText) findViewById(R.id.input_name);
        estorename = (EditText) findViewById(R.id.input_store_name);
        etin = (EditText) findViewById(R.id.input_tin);
        p15 = (EditText) findViewById(R.id.input_addr);
        eph1 = (EditText) findViewById(R.id.input_ph1);
        eph2 = (EditText) findViewById(R.id.input_ph2);
        emore = (EditText) findViewById(R.id.input_more);
        egst = (EditText) findViewById(R.id.input_gst);

        mAuth = FirebaseAuth.getInstance();
        du = FirebaseDatabase.getInstance().getReference().child("Users");
        //du.keepSynced(true);
    }

    public void startsetup(View view) {
        String uid = mAuth.getCurrentUser().getUid();
        String sname = ename.getText().toString();
        String sstorename = estorename.getText().toString();
        String stin = etin.getText().toString();
        String saddr = p15.getText().toString();
        String sph1 = eph1.getText().toString();
        String sph2 = eph2.getText().toString();
        String smore = emore.getText().toString();
        //String sgst = egst.getText().toString();
        if (validate()) {

            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat fm = new SimpleDateFormat("HH:mm");

            du.child(uid).child("zinfo").child("i01").setValue(sname);
            du.child(uid).child("zinfo").child("i02").setValue(sstorename);
            du.child(uid).child("zinfo").child("i03").setValue(stin);
            du.child(uid).child("zinfo").child("i04").setValue(saddr);
            du.child(uid).child("zinfo").child("i05").setValue(sph1);
            du.child(uid).child("zinfo").child("i06").setValue(sph2);
            du.child(uid).child("zinfo").child("i07").setValue(smore);
            du.child(uid).child("zinfo").child("cash").setValue("0.00");
            du.child(uid).child("zinfo").child("cheque").setValue("0.00");
            du.child(uid).child("zinfo").child("credit").setValue("0.00");
            du.child(uid).child("zinfo").child("day").setValue("0");
            du.child(uid).child("zinfo").child("lan").setValue("en");
            du.child(uid).child("zinfo").child("mlan").setValue("en");
            du.child(uid).child("zinfo").child("newprice").setValue("001");
            du.child(uid).child("zinfo").child("tsv").setValue("0.00");
            du.child(uid).child("zinfo").child("us").setValue("0");
            du.child(uid).child("Log").child("log").setValue(
                    format.format(calendar.getTime()) + "\n[" + fm.format(calendar.getTime()) + "] Account Created");

            Intent i = new Intent(this, MainActivity.class);
            finish();
            startActivity(i);
        }

    }

    public boolean validate() {
        int flag = 0;
        if (ename.getText().toString().trim().isEmpty()) {
            flag = 1;
            name.setError("Cannot be Empty");
        } else
            name.setErrorEnabled(false);
        if (estorename.getText().toString().trim().isEmpty()) {
            flag = 1;
            storename.setError("Cannot be Empty");
        } else
            storename.setErrorEnabled(false);
        if (etin.getText().toString().trim().isEmpty()) {
            flag = 1;
            tin.setError("Cannot be Empty");
        } else
            tin.setErrorEnabled(false);
        if (p15.getText().toString().trim().isEmpty()) {
            flag = 1;
            b3.setError("Cannot be Empty");
        } else
            b3.setErrorEnabled(false);
        if (eph1.getText().toString().trim().isEmpty()) {
            flag = 1;
            ph1.setError("Cannot be Empty");
        } else
            ph1.setErrorEnabled(false);
        if (eph2.getText().toString().trim().isEmpty()) {
            flag = 1;
            ph2.setError("Cannot be Empty");
        } else
            ph2.setErrorEnabled(false);
        if (emore.getText().toString().trim().isEmpty()) {
            emore.setText("-");
        }
        /*if (egst.getText().toString().trim().isEmpty()) {
            flag = 1;
            gst.setError("Cannot be Empty");
        } else
            gst.setErrorEnabled(false);*/
        if (flag == 1)
            return false;
        else
            return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
        super.onBackPressed();
    }

    public void logoutreg(View view) {
        signOut();
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(Register.this, Login.class);
                finish();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mGoogleApiClient.connect();
        super.onStart();
    }
}
