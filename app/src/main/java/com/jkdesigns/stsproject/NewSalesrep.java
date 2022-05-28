package com.jkdesigns.stsproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
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

public class NewSalesrep extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout iName, iaddr, iphone, ibalance, idate;
    EditText eName, p15, ephone, ebalance, p13;
    private DatabaseReference du,dup,dut;
    int dutString;
    private FirebaseAuth mAuth;
    String uid;
    private int mYear, mMonth, mDay;
    NativeExpressAdView NAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_salesrep);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ad_layout);
        // Create a native express ad. The ad size and ad unit ID must be set before calling
        // loadAd.
        NAdView = new NativeExpressAdView(this);
        NAdView.setAdSize(new AdSize(320, 300));
        NAdView.setAdUnitId(String.valueOf(R.string.sma_banner_2));

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        // Add the NativeExpressAdView to the view hierarchy.
        linearLayout.addView(NAdView);

        // Start loading the ad.
        NAdView.loadAd(adRequestBuilder.build());

        setTitle("Add Sales Rep");

        iName = (TextInputLayout) findViewById(R.id.nameLayout);
        iaddr = (TextInputLayout) findViewById(R.id.addrLayout);
        iphone = (TextInputLayout) findViewById(R.id.phoneLayout);
        //ibalance = (TextInputLayout) findViewById(R.id.balanceLayout);
        idate = (TextInputLayout) findViewById(R.id.dateLayout);

        eName = (EditText) findViewById(R.id.nameText);
        p15 = (EditText) findViewById(R.id.addrText);
        ephone = (EditText) findViewById(R.id.phoneText);
        ebalance = (EditText) findViewById(R.id.balanceText);
        p13 = (EditText) findViewById(R.id.dateText);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        p13.setText(format.format(c.getTime()));

        ebalance.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

        p13.setInputType(InputType.TYPE_NULL);
        p13.setFocusable(false);
        p13.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("SalesRep");
        dup = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("salesreps");
        du.keepSynced(true);
    }

    public void addInfo(View v) {
        String sName = eName.getText().toString();
        String saddr = p15.getText().toString();
        String sphone = ephone.getText().toString();
        String sbalance = "0";
        String sdate = p13.getText().toString();

        if (validate()) {
            final String k = du.child("Person").push().getKey();
            Modelcustomer m = new Modelcustomer(sName, sphone, sdate, sbalance, saddr, "", k);
            du.child("Person").child(k).setValue(m);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            du.child("Person").child(k).child("log").setValue(
                    "[" + format.format(calendar.getTime()) + "] " + "Sales rep " + sName +" added." + "\n");

            final String padd = "[{\"name\":\"p03\",\"value\":\""+sName+"\"}," +
                    "{\"name\":\"p12\",\"value\":\""+sphone+"\"}," +
                    "{\"name\":\"p13\",\"value\":\""+sdate+"\"}," +
                    "{\"name\":\"p14\",\"value\":\""+sbalance+"\"}," +
                    "{\"name\":\"p15\",\"value\":\""+saddr+"\"}," +
                    "{\"name\":\"key\",\"value\":\""+k+"\"}," +
                    "{\"name\":\"log\",\"value\":\""+"[" + format.format(calendar.getTime()) + "] " + "Account "+sName+" created. Balance:" + sbalance+"\"}]";

            // add to customers
            dup.getParent().child("salesreps").child(k).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dup.getParent().child("salesreps").child(k)
                            .setValue(padd);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            dut = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("SalesRep");
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

            dup.getParent().child("tables").child("SalesRep").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dup.getParent().child("tables").child("SalesRep").child("count")
                            .setValue(String.valueOf(++dutString));
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            // log
            final String item = sName;
            du.getParent().child("Log").child("log").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String l = (String) dataSnapshot.getValue();
                    String date = l.substring(0, 10);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                    if (format.format(calendar.getTime()).equals(date)) {
                        l = l.substring(11);
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " Sales Rep Account Created\n" + l);
                    } else {
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " Sales Rep Account Created\n\n" + l);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            Toast.makeText(this, "Sales Rep is saved", Toast.LENGTH_SHORT).show();
            m_clear();
        } else {
            Toast.makeText(this, "Sorry.. Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        int flag = 0;
        if (eName.getText().toString().trim().isEmpty()) {
            flag = 1;
            iName.setError("Cannot be Empty");
        } else
            iName.setErrorEnabled(false);

        if (ephone.getText().toString().trim().isEmpty()) {
            flag = 1;
            iphone.setError("Cannot be Empty");
        } else
            iphone.setErrorEnabled(false);
        if (p13.getText().toString().trim().isEmpty()) {
            flag = 1;
            idate.setError("Cannot be Empty");
        } else
            idate.setErrorEnabled(false);
        /*if (ebalance.getText().toString().trim().isEmpty()) {
            flag = 1;
            ibalance.setError("Cannot be Empty");
        } else
            ibalance.setErrorEnabled(false);*/

        if (flag == 1)
            return false;
        else
            return true;
    }

    public void clear(View v) {
        m_clear();
    }

    public void m_clear() {
        eName.setText("");
        p15.setText("");
        ephone.setText("");
        ebalance.setText("");
        p13.setText("");

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
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                p13.setText(format.format(calendar.getTime()));

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }
}
