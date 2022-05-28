package com.jkdesigns.stsproject;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class UpdateInfo extends AppCompatActivity {

    TextInputLayout name, storename, tin, b3, ph1, ph2, more, gst;
    EditText ename, estorename, etin, p15, eph1, eph2, emore, egst;
    private DatabaseReference du;
    private FirebaseAuth mAuth;
    String uid;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateinfo);

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
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
        //du.keepSynced(true);
        du.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                ename.setText(map.get("i01"));
                estorename.setText(map.get("i02"));
                etin.setText(map.get("i03"));
                p15.setText(map.get("i04"));
                eph1.setText(map.get("i05"));
                eph2.setText(map.get("i06"));
                emore.setText(map.get("i07"));
                //egst.setText(map.get("gst"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void startsetup(View view) {

        String sname = ename.getText().toString();
        String sstorename = estorename.getText().toString();
        String stin = etin.getText().toString();
        String saddr = p15.getText().toString();
        String sph1 = eph1.getText().toString();
        String sph2 = eph2.getText().toString();
        String smore = emore.getText().toString();
        //String sgst = egst.getText().toString();
        if (validate()) {
            du.child("i01").setValue(sname);
            du.child("i02").setValue(sstorename);
            du.child("i03").setValue(stin);
            du.child("i04").setValue(saddr);
            du.child("i05").setValue(sph1);
            du.child("i06").setValue(sph2);
            du.child("i07").setValue(smore);
            //du.child("gst").setValue(sgst);

            du.getParent().child("Log").child("log").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String l = (String) dataSnapshot.getValue();
                    String date = l.substring(0, 10);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                    if (format.format(calendar.getTime()).equals(date)) {
                        l = l.substring(11);
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] Account Updated\n" + l);
                    } else {
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] Account Updated\n\n" + l);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
        }else
            gst.setErrorEnabled(false);*/
        if (flag == 1)
            return false;
        else
            return true;
    }

}
