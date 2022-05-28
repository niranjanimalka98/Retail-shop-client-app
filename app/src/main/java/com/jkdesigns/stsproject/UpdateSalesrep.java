package com.jkdesigns.stsproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

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

public class UpdateSalesrep extends AppCompatActivity implements View.OnClickListener {
    TextInputLayout iName, iaddr, iphone, ibalance, idate;
    EditText eName, p15, ephone, ebalance, p13;
    private int mYear, mMonth, mDay;
    private DatabaseReference du,dup;
    String s;
    private FirebaseAuth mAuth;
    String uid;
    NativeExpressAdView NAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_salesrep);

        setTitle("Update Sales Rep");
        s = getIntent().getStringExtra("Keys");

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ad_layout);
        // Create a native express ad. The ad size and ad unit ID must be set before calling
        // loadAd.
        NAdView = new NativeExpressAdView(this);
        NAdView.setAdSize(new AdSize(320, 300));
        NAdView.setAdUnitId(String.valueOf(R.string.sma_banner_1));

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        // Add the NativeExpressAdView to the view hierarchy.
        linearLayout.addView(NAdView);

        // Start loading the ad.
        NAdView.loadAd(adRequestBuilder.build());

        iName = (TextInputLayout) findViewById(R.id.nameLayout);
        iaddr = (TextInputLayout) findViewById(R.id.addrLayout);
        iphone = (TextInputLayout) findViewById(R.id.phoneLayout);
        ibalance = (TextInputLayout) findViewById(R.id.balanceLayout);
        idate = (TextInputLayout) findViewById(R.id.dateLayout);

        eName = (EditText) findViewById(R.id.nameText);
        p15 = (EditText) findViewById(R.id.addrText);
        ephone = (EditText) findViewById(R.id.phoneText);
        ebalance = (EditText) findViewById(R.id.balanceText);
        p13 = (EditText) findViewById(R.id.dateText);

        p13.setInputType(InputType.TYPE_NULL);
        p13.setFocusable(false);
        p13.setOnClickListener(this);

        ebalance.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("SalesRep").child("Person")
                .child(s);
        du.keepSynced(true);
        du.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                eName.setText(map.get("p03"));
                p15.setText(map.get("p15"));
                ephone.setText(map.get("p12"));
                ebalance.setText(map.get("p14"));
                p13.setText(map.get("p13"));

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

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
                p13.setText(format.format(calendar.getTime()));
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.del_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.del) {

            du.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(UpdateSalesrep.this);
                    alert.setTitle("Alert!!");
                    alert.setMessage("Are you sure to delete this Sales Rep?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dataSnapshot.getRef().removeValue();
                            dialog.dismiss();
                            finish();

                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateInfo(View view) {
        String sName = eName.getText().toString();
        String saddr = p15.getText().toString();
        String sphone = ephone.getText().toString();
        final String sbalance = ebalance.getText().toString();
        String sdate = p13.getText().toString();

        if (validate()) {

            du.child("p03").setValue(sName);
            du.child("p12").setValue(sphone);
            du.child("p13").setValue(sdate);
            du.child("p14").setValue(sbalance);
            du.child("p15").setValue(saddr);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            final String padd = "[{\"name\":\"p03\",\"value\":\""+sName+"\"}," +
                    "{\"name\":\"p12\",\"value\":\""+sphone+"\"}," +
                    "{\"name\":\"p13\",\"value\":\""+sdate+"\"}," +
                    "{\"name\":\"p14\",\"value\":\""+sbalance+"\"}," +
                    "{\"name\":\"p15\",\"value\":\""+saddr+"\"}," +
                    "{\"name\":\"key\",\"value\":\""+s+"\"}," +
                    "{\"name\":\"log\",\"value\":\""+"[" + format.format(calendar.getTime()) + "] " + "Account "+sName+" updated. Balance:" + sbalance+"\"}]";

            // add to salesreps
            dup = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("salesreps");
            dup.getParent().child("salesreps").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dup.getParent().child("salesreps").child(s)
                            .setValue(padd);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            du.child("log").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = (String) dataSnapshot.getValue();
                    // do your stuff here with value
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    du.child("log").setValue(value + "[" + format.format(calendar.getTime()) + "] "
                            + "Account updated. Balance :" + sbalance + "\n");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            // log
            final String item = sName;
            du.getParent().getParent().getParent().child("Log").child("log")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String l = (String) dataSnapshot.getValue();
                            String date = l.substring(0, 10);
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                            if (format.format(calendar.getTime()).equals(date)) {
                                l = l.substring(11);
                                du.getParent().getParent().getParent().child("Log").child("log")
                                        .setValue(format.format(calendar.getTime()) + "\n["
                                                + fm.format(calendar.getTime()) + "] " + item + " Sales Rep Updated\n"
                                                + l);
                            } else {
                                du.getParent().getParent().getParent().child("Log").child("log")
                                        .setValue(format.format(calendar.getTime()) + "\n["
                                                + fm.format(calendar.getTime()) + "] " + item + " Sales Rep Updated\n\n"
                                                + l);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });

            finish();
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
        if (ebalance.getText().toString().trim().isEmpty()) {
            flag = 1;
            ibalance.setError("Cannot be Empty");
        } else
            ibalance.setErrorEnabled(false);
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
}
