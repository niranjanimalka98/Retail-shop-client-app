package com.jkdesigns.stsproject;

import android.app.DatePickerDialog;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Newitem extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout iName, batchNo, qty, expdate, mrp, sp, cName, code, cp, desc, unit;
    EditText p3, p2, p10, p5, p7, p8, p4, p1, p6, p11, p9;
    private DatabaseReference du,dup,dut,dugr;
    int dutString;
    private FirebaseAuth mAuth;
    String uid, p18, p19, p20, p21;
    private int mYear, mMonth, mDay;
    private AdView mAdView1, mAdView2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_info);

        MobileAds.initialize(this, String.valueOf(R.string.app_id));
        mAdView1 = (AdView) findViewById(R.id.adView1);
        mAdView2 = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest);
        mAdView2.loadAd(adRequest);

        setTitle("Add Item");

        iName = (TextInputLayout) findViewById(R.id.nameLayout);
        batchNo = (TextInputLayout) findViewById(R.id.batchLayout);
        qty = (TextInputLayout) findViewById(R.id.qtyLayout);
        expdate = (TextInputLayout) findViewById(R.id.expLayout);
        mrp = (TextInputLayout) findViewById(R.id.mrpLayout);
        sp = (TextInputLayout) findViewById(R.id.spLayout);
        cName = (TextInputLayout) findViewById(R.id.cNameLayout);
        code = (TextInputLayout) findViewById(R.id.codeLayout);
        cp = (TextInputLayout) findViewById(R.id.cpLayout);
        desc = (TextInputLayout) findViewById(R.id.descLayout);
        unit = (TextInputLayout) findViewById(R.id.unitLayout);

        p3 = (EditText) findViewById(R.id.nameText);
        p2 = (EditText) findViewById(R.id.batchText);
        p10 = (EditText) findViewById(R.id.qtyText);
        p5 = (EditText) findViewById(R.id.expText);
        p7 = (EditText) findViewById(R.id.mrpText);
        p8 = (EditText) findViewById(R.id.spText);
        p4 = (EditText) findViewById(R.id.cNameText);
        p1 = (EditText) findViewById(R.id.codeText);
        p6 = (EditText) findViewById(R.id.cpText);
        p11 = (EditText) findViewById(R.id.descText);
        p9 = (EditText) findViewById(R.id.unitText);

        p7.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
        p8.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });
        p6.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

        p5.setInputType(InputType.TYPE_NULL);
        p5.setFocusable(false);
        p5.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Inventory");
        //du.keepSynced(true);
        dup = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("products");
        dugr = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("GR");
        //dup.keepSynced(true);
    }

    public void addInfo(View v) {
        String siname = p3.getText().toString();
        String sbatchNo = p2.getText().toString();
        String sqty = p10.getText().toString();
        String sexpDate = p5.getText().toString();
        String smrp = p7.getText().toString();
        String ssp = p8.getText().toString();
        String scName = p4.getText().toString();
        final String scode = p1.getText().toString();
        String scp = p6.getText().toString();
        String sdesc = p11.getText().toString();
        String sunit = p9.getText().toString();
        String vc = "0";

        double d = (Double.valueOf(scp) / 100) * Double.valueOf(smrp);
        double d1 = d + Double.valueOf(scp);
        double d2 = (d1 / 100) * Double.valueOf(sdesc);
        double d3 = d2 + d1;
        d = roundOff(d3);
        //Toast.makeText(this, String.valueOf(d), Toast.LENGTH_SHORT).show();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());

        final String padd = "[{\"name\":\"p01\",\"value\":\""+scode+"\"}," +
                "{\"name\":\"p02\",\"value\":\""+sbatchNo+"\"}," +
                "{\"name\":\"p03\",\"value\":\""+siname+"\"}," +
                "{\"name\":\"p04\",\"value\":\""+scName+"\"}," +
                "{\"name\":\"p05\",\"value\":\""+sexpDate+"\"}," +
                "{\"name\":\"p06\",\"value\":\""+scp+"\"}," +
                "{\"name\":\"p07\",\"value\":\""+smrp+"\"}," +
                "{\"name\":\"p08\",\"value\":\""+d+"\"}," +
                "{\"name\":\"p09\",\"value\":\""+sunit+"\"}," +
                "{\"name\":\"p10\",\"value\":\""+sqty+"\"}," +
                "{\"name\":\"p11\",\"value\":\""+sdesc+"\"}," +
                "{\"name\":\"p17\",\"value\":\"0\"}]";

        final String gr = "[{\"name\":\"p01\",\"value\":\""+scode+"\"}," +
                "{\"name\":\"p10\",\"value\":\""+sqty+"\"}," +
                "{\"name\":\"p06\",\"value\":\""+scp+"\"}," +
                "{\"name\":\"p07\",\"value\":\""+smrp+"\"}," +
                "{\"name\":\"p11\",\"value\":\""+sdesc+"\"}," +
                "{\"name\":\"p09\",\"value\":\""+sunit+"\"}," +
                "{\"name\":\"p16\",\"value\":\""+currentDateandTime+"\"}]";

        if (validate()) {
            //String k = du.child("Items").push().getKey();
            Model m = new Model(siname, sbatchNo, sqty, sexpDate, smrp, String.valueOf(d), scName, scode, scp, sdesc, /*k,*/ sunit, vc, p18, p19, p20, p21); // not defined correctly p18,p19,p20
            du.child("Items").child(scode).setValue(m);

            // add to products
            dup.getParent().child("products").child(scode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dup.getParent().child("products").child(scode)
                            .setValue(padd);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            // add to gr
            dugr.getParent().child("GR").child(scode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dugr.getParent().child("GR").child(scode)
                            .setValue(gr);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            dut = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Inventory");
            //dut.keepSynced(true);
            dut.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                    dutString = Integer.parseInt(map.get("count"));

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            dup.getParent().child("tables").child("Inventory").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dup.getParent().child("tables").child("Inventory").child("count")
                            .setValue(String.valueOf(++dutString));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            // log
            final String item = siname;
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
                                + fm.format(calendar.getTime()) + "] " + item + " Item Added\n" + l);
                    } else {
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " Item Added\n\n" + l);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            Toast.makeText(this, "Your Item is saved", Toast.LENGTH_SHORT).show();
            m_clear();
        } else {
            Toast.makeText(this, "Sorry.. Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        int flag = 0;
        if (p3.getText().toString().trim().isEmpty()) {
            flag = 1;
            iName.setError("Cannot be Empty");
        } else
            iName.setErrorEnabled(false);
        if (p10.getText().toString().trim().isEmpty()) {
            flag = 1;
            qty.setError("Cannot be Empty");
        } else
            qty.setErrorEnabled(false);
        if (p2.getText().toString().trim().isEmpty()) {
            flag = 1;
            batchNo.setError("Cannot be Empty");
        } else
            batchNo.setErrorEnabled(false);
        if (p5.getText().toString().trim().isEmpty()) {
            flag = 1;
            expdate.setError("Cannot be Empty");
        } else
            expdate.setErrorEnabled(false);
        if (p7.getText().toString().trim().isEmpty()) {
            flag = 1;
            mrp.setError("Cannot be Empty");
        } else
            mrp.setErrorEnabled(false);
        if (p11.getText().toString().trim().isEmpty()) {
            flag = 1;
            sp.setError("Cannot be Empty");
        } else
            sp.setErrorEnabled(false);
        if (p4.getText().toString().trim().isEmpty()) {
            flag = 1;
            cName.setError("Cannot be Empty");
        } else
            cName.setErrorEnabled(false);
        if (p1.getText().toString().trim().isEmpty()) {
            flag = 1;
            code.setError("Cannot be Empty");
        } else
            code.setErrorEnabled(false);
        if (p6.getText().toString().trim().isEmpty()) {
            flag = 1;
            cp.setError("Cannot be Empty");
        } else
            cp.setErrorEnabled(false);

        if (p9.getText().toString().trim().isEmpty()) {
            flag = 1;
            unit.setError("Cannot be Empty");
        } else
            unit.setErrorEnabled(false);
        if (flag == 1)
            return false;
        else
            return true;
    }

    public void clear(View v) {
        m_clear();
    }

    public void m_clear() {
        p3.setText("");
        p2.setText("");
        p7.setText("");
        p8.setText("");
        p5.setText("");
        p10.setText("");
        p4.setText("");
        p1.setText("");
        p6.setText("");
        p11.setText("");
        p9.setText("");
    }

    @Override
    public void onClick(View v) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                p5.setText(format.format(calendar.getTime()));
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public double roundOff(double d) {
        d = Math.round(d * 100.00);
        d = d / 100.00;
        return d;
    }
}
