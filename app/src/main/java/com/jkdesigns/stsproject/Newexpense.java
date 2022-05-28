package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
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

public class Newexpense extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout iName, iaddr, iphone, ibalance, idate;
    EditText eName, p15, ephone, ebalance, p18;
    private DatabaseReference du,dup,dut,dbr, dbs, zinfotsv, dus;
    int dutString;
    private FirebaseAuth mAuth;
    String uid,starttsv,sbalance,startcash,startcheque,startcredit;
    private int mYear, mMonth, mDay;
    NativeExpressAdView NAdView;
    Button add;
    Calendar csummary;
    String time = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expense);

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

        setTitle("Add Expense");

        iName = (TextInputLayout) findViewById(R.id.nameLayout);
        iaddr = (TextInputLayout) findViewById(R.id.addrLayout);
        iphone = (TextInputLayout) findViewById(R.id.phoneLayout);
        ibalance = (TextInputLayout) findViewById(R.id.balanceLayout);
        idate = (TextInputLayout) findViewById(R.id.dateLayout);
        add = (Button) findViewById(R.id.btn_add);

        eName = (EditText) findViewById(R.id.nameText);
        p15 = (EditText) findViewById(R.id.addrText);
        ephone = (EditText) findViewById(R.id.phoneText);
        ebalance = (EditText) findViewById(R.id.balanceText);
        p18 = (EditText) findViewById(R.id.dateText);

        /*Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        p18.setText(format.format(c.getTime()));*/

        ebalance.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        //route = (EditText) findViewById(R.id.route);
        /*route.setText(prefs.getString("autoSaveroute", ""));
        route.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count)
            {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                prefs.edit().putString("autoSaveroute", s.toString()).commit();
            }
        });*/
        //act4 = (AutoCompleteTextView) findViewById(R.id.route);

        /*p18.setInputType(InputType.TYPE_NULL);
        p18.setFocusable(false);
        p18.setOnClickListener(this);*/
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Expense");
        dup = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("expenses");
        dbs = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Summary");
        dus = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        csummary = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformatday = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");

        // get tsv
        zinfotsv = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
        zinfotsv.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                starttsv = map.get("tsv");
                startcash = map.get("cash");
                startcheque = map.get("cheque");
                startcredit = map.get("credit");
                String todate = map.get("day");

                assert todate != null;
                if(!todate.equals(dayformatday.format(csummary.getTime()))){
                    startthemonth();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void startthemonth(){
        csummary = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformatday = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformatm = new SimpleDateFormat("MM/yyyy");

        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String date = (String) dataSnapshot.child(dayformat.format(csummary.getTime())).getValue();
                String z01 = (String) dataSnapshot.child("z01").getValue();
                String z02 = (String) dataSnapshot.child("z02").getValue();
                String z03 = (String) dataSnapshot.child("z03").getValue();
                String z04 = (String) dataSnapshot.child("z04").getValue();
                String z05 = (String) dataSnapshot.child("z05").getValue();
                String z06 = (String) dataSnapshot.child("z06").getValue();
                String z07 = (String) dataSnapshot.child("z07").getValue();
                String z08 = (String) dataSnapshot.child("z08").getValue();
                String z09 = (String) dataSnapshot.child("z09").getValue();

                if (!dataSnapshot.hasChild("z00")) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(Newexpense.this);
                    alert.setCancelable(false);
                    alert.setTitle(R.string.startday);
                    alert.setMessage(R.string.startdaymsg);
                    alert.setPositiveButton(R.string.trans_yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            zinfotsv.addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                    };
                                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                                    String todate = map.get("day");
                                    assert todate != null;
                                    if(!todate.equals(dayformatday.format(csummary.getTime()))){
                                        zinfotsv.child("day").setValue(dayformatday.format(csummary.getTime()));
                                        zinfotsv.child("newprice").setValue("001");
                                        time = "month";
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z00").setValue(monthformatm.format(csummary.getTime()));
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z01").setValue(starttsv);
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z02").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z03").setValue("0");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z04").setValue("0");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z05").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z06").setValue("0");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z07").setValue("0");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z08").setValue("0");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z09").setValue(starttsv);

                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z10").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z00").setValue(startcash);
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(startcash);
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z04").setValue(startcheque);
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z05").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(startcheque);
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z07").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z08").setValue("0.00");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");

                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("d02").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z00").setValue(dayformatday.format(csummary.getTime()));
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z01").setValue(starttsv);
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z02").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z03").setValue("0");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z04").setValue("0");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z05").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z06").setValue("0");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z07").setValue("0");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z08").setValue("0");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z09").setValue(starttsv);

                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z10").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z00").setValue(startcash);
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(startcash);
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z04").setValue(startcheque);
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(startcheque);
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z07").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue("0.00");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");

                                        //add expense 0.00
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z01").setValue("Expense");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z02").setValue("~");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z03").setValue("0.00");
                                        String summary = "[{\"name\":\"z00\",\"value\":\""+dayformatday.format(csummary.getTime())+"\"}," +
                                                "{\"name\":\"z01\",\"value\":\""+starttsv+"\"}," +
                                                "{\"name\":\"z02\",\"value\":\"0.00\"}," +
                                                "{\"name\":\"z03\",\"value\":\"0\"}," +
                                                "{\"name\":\"z04\",\"value\":\"0\"}," +
                                                "{\"name\":\"z05\",\"value\":\"0.00\"}," +
                                                "{\"name\":\"z06\",\"value\":\"0\"}," +
                                                "{\"name\":\"z07\",\"value\":\"0\"}," +
                                                "{\"name\":\"z08\",\"value\":\"0\"}," +
                                                "{\"name\":\"z09\",\"value\":\""+starttsv+"\"}]";
                                        dus.child("summary").child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).setValue(summary);
                                        add.setText("Add");
                                        //addtosummary();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                }
                            });
                        }
                    });

                    alert.setNegativeButton(R.string.trans_no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            add.setText("START");
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }else{
                    starttheday();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void starttheday(){
        csummary = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformatday = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");

        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String date = (String) dataSnapshot.child(dayformat.format(csummary.getTime())).getValue();
                String z01 = (String) dataSnapshot.child("z01").getValue();
                String z02 = (String) dataSnapshot.child("z02").getValue();
                String z03 = (String) dataSnapshot.child("z03").getValue();
                String z04 = (String) dataSnapshot.child("z04").getValue();
                String z05 = (String) dataSnapshot.child("z05").getValue();
                String z06 = (String) dataSnapshot.child("z06").getValue();
                String z07 = (String) dataSnapshot.child("z07").getValue();
                String z08 = (String) dataSnapshot.child("z08").getValue();
                //String z081 = (String) dataSnapshot.child("z081").getValue();
                String z09 = (String) dataSnapshot.child("z09").getValue();

                if (!dataSnapshot.hasChild("z00")) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(Newexpense.this);
                    alert.setCancelable(false);
                    alert.setTitle(R.string.startday);
                    alert.setMessage(R.string.startdaymsg);
                    alert.setPositiveButton(R.string.trans_yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            zinfotsv.addListenerForSingleValueEvent(new ValueEventListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                    };
                                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                                    String todate = map.get("day");
                                    assert todate != null;
                                    if(!todate.equals(dayformatday.format(csummary.getTime()))){
                            zinfotsv.child("day").setValue(dayformatday.format(csummary.getTime()));
                            zinfotsv.child("newprice").setValue("001");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z00").setValue(dayformatday.format(csummary.getTime()));
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z01").setValue(starttsv);
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z02").setValue("0.00");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z03").setValue("0");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z04").setValue("0");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z05").setValue("0.00");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z06").setValue("0");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z07").setValue("0");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z08").setValue("0");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z09").setValue(starttsv);

                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z10").setValue("0.00");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z00").setValue(startcash);
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue("0.00");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue("0.00");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(startcash);
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z04").setValue(startcheque);
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue("0.00");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(startcheque);
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z07").setValue(startcredit);
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue("0.00");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(startcredit);

                            //add expense 0.00
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z01").setValue("Expense");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z02").setValue("~");
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z03").setValue("0.00");
                            String summary = "[{\"name\":\"z00\",\"value\":\""+dayformatday.format(csummary.getTime())+"\"}," +
                                    "{\"name\":\"z01\",\"value\":\""+starttsv+"\"}," +
                                    "{\"name\":\"z02\",\"value\":\"0.00\"}," +
                                    "{\"name\":\"z03\",\"value\":\"0\"}," +
                                    "{\"name\":\"z04\",\"value\":\"0\"}," +
                                    "{\"name\":\"z05\",\"value\":\"0.00\"}," +
                                    "{\"name\":\"z06\",\"value\":\"0\"}," +
                                    "{\"name\":\"z07\",\"value\":\"0\"}," +
                                    "{\"name\":\"z08\",\"value\":\"0\"}," +
                                    "{\"name\":\"z09\",\"value\":\""+starttsv+"\"}]";
                            dus.child("summary").child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).setValue(summary);
                            add.setText("Add");
                            //addtosummary();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                }
                            });
                        }
                    });

                    alert.setNegativeButton(R.string.trans_no, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            add.setText("START");
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

   /*public void addtosummary(){
        dus.child("Inventory").child("Items").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectItems((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
    private void collectItems(Map<String,Object> users) {
        //ArrayList<String> phoneNumbers = new ArrayList<>();
        //iterate through each user, ignoring their UID
        final Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayform = new SimpleDateFormat("ddMMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");

        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            if(time.equals("month")) {
                dbs.child("m").child(monthformat.format(csummary.getTime())).child((String) singleUser.get("p01")).child("z00").setValue(singleUser.get("p08"));
                dbs.child("m").child(monthformat.format(csummary.getTime())).child((String) singleUser.get("p01")).child("z01").setValue(singleUser.get("p03"));
                dbs.child("m").child(monthformat.format(csummary.getTime())).child((String) singleUser.get("p01")).child("z06").setValue(singleUser.get("p10"));
                dbs.child("m").child(monthformat.format(csummary.getTime())).child((String) singleUser.get("p01")).child("z07").setValue(singleUser.get("p10"));
                dbs.child("m").child(monthformat.format(csummary.getTime())).child((String) singleUser.get("p01")).child("z08").setValue(singleUser.get("p17"));
                dbs.child("m").child(monthformat.format(csummary.getTime())).child((String) singleUser.get("p01")).child("z09").setValue(singleUser.get("p03t"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z00").setValue(singleUser.get("p08"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z01").setValue(singleUser.get("p03"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z06").setValue(singleUser.get("p10"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z07").setValue(singleUser.get("p10"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z08").setValue(singleUser.get("p17"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z09").setValue(singleUser.get("p03t"));
            }else{
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z00").setValue(singleUser.get("p08"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z01").setValue(singleUser.get("p03"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z06").setValue(singleUser.get("p10"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z07").setValue(singleUser.get("p10"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z08").setValue(singleUser.get("p17"));
                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayform.format(c.getTime())).child((String) singleUser.get("p01")).child("z09").setValue(singleUser.get("p03t"));
            }
        }
    }*/

    public void addInfo(View v) {

        if (add.getText().equals("START")){

            startthemonth();
        }

        final String sName = eName.getText().toString();
        //String saddr = p15.getText().toString();
        //String sphone = ephone.getText().toString();
        sbalance = ebalance.getText().toString();

        int jk = ebalance.getText().toString().lastIndexOf('.');
        if(jk != -1 && ebalance.getText().toString().substring(jk + 1).length() == 2) {
            sbalance = ebalance.getText().toString();
        }else if(jk != -1 && ebalance.getText().toString().substring(jk + 1).length() == 1) {
            sbalance = ebalance.getText().toString()+"0";
        }else if(jk != -1 && ebalance.getText().toString().substring(jk + 1).length() == 0){
            sbalance = ebalance.getText().toString()+"00";
        }else{
            sbalance = ebalance.getText().toString()+".00";
        }

        if (validate()) {
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
            String sdate = format.format(calendar.getTime());
            final String k = du.child(monthformat.format(csummary.getTime())).child("E").push().getKey();
            Modelexpense m = new Modelexpense(sName, sbalance, sdate, k);
            du.child(monthformat.format(csummary.getTime())).child("E").child(k).setValue(m);

            du.child(monthformat.format(csummary.getTime())).child("E").child(k).child("log").setValue(
                    "[" + format.format(calendar.getTime()) + "] " + "Account created. Balance:" + sbalance + "\n");

            final String padd = "[{\"name\":\"p18\",\"value\":\""+format.format(calendar.getTime())+"\"}," + //
                    "{\"name\":\"p03\",\"value\":\""+sName+"\"}," +
                    "{\"name\":\"b07\",\"value\":\""+sbalance+"\"}," +
                    "{\"name\":\"key\",\"value\":\""+k+"\"}," +
                    "{\"name\":\"log\",\"value\":\""+"[" + format.format(calendar.getTime()) + "] " + "Expense "+sName+" created. Amount:" + sbalance+"\"}]";

            // add to customers
            dup.getParent().child("expenses").child(monthformat.format(csummary.getTime())).child(k).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dup.getParent().child("expenses").child(monthformat.format(csummary.getTime())).child(k)
                            .setValue(padd);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            dut = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Expense");
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

            dup.getParent().child("tables").child("Expense").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dup.getParent().child("tables").child("Expense").child("count")
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
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                    if (format.format(calendar.getTime()).equals(date)) {
                        l = l.substring(11);
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " New Expense Created\n" + l);
                    } else {
                        du.getParent().child("Log").child("log").setValue(format.format(calendar.getTime()) + "\n["
                                + fm.format(calendar.getTime()) + "] " + item + " New Expense Created\n\n" + l);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            //month money
            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String z02 = (String) dataSnapshot.child("z02").getValue();
                    //String z09 = (String) dataSnapshot.child("z09").getValue();

                    if (dataSnapshot.hasChild("z02")) {

                        double z081sum = Double.parseDouble(z02) + Double.parseDouble(sbalance);
                        //double z09sum = Double.parseDouble(z09) - Double.parseDouble(sbalance);

                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue(String.valueOf(z081sum+"0"));//amount
                        //dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z09").setValue(String.valueOf(z09sum+"0"));//amount
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //remove sample expense
            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("z01")) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").removeValue();
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("Expense").child("z02").setValue(sName);
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("Expense").child("z03").setValue(sbalance);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //day expense
            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild("z04")) {

                        //dbs.child("d").child(dayformat.format(csummary.getTime())).child("e-"+k).child("z00").setValue("");
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z01").setValue("Expense");
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z02").setValue(sName);
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z03").setValue(sbalance);
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z04").setValue("");
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z05").setValue("");
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z06").setValue("");
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z07").setValue("");
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z08").setValue("");
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-"+k).child("z09").setValue("");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //day money
            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String z02 = (String) dataSnapshot.child("z02").getValue();
                    //String z09 = (String) dataSnapshot.child("z09").getValue();

                    if (dataSnapshot.hasChild("z02")) {
                        double z081sum = Double.parseDouble(z02) + Double.parseDouble(sbalance);
                        //double z09sum = Double.parseDouble(z09) - Double.parseDouble(sbalance);

                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue(String.valueOf(z081sum+"0"));//amount
                        //dbs.child("d").child(dayformat.format(csummary.getTime())).child("00").child("z09").setValue(String.valueOf(z09sum+"0"));//amount
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //Zinfo
            zinfotsv.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //String tsvvalue = (String) dataSnapshot.child("tsv").getValue();
                    String cash = (String) dataSnapshot.child("cash").getValue();

                    if (dataSnapshot.hasChild("cash")) {
                        //double tsvsum = Double.parseDouble(tsvvalue) - Double.parseDouble(sbalance);
                        //zinfotsv.child("tsv").setValue(String.valueOf(tsvsum+"0"));//tsv

                        double cashsum = Double.parseDouble(cash) - Double.parseDouble(sbalance);
                        zinfotsv.child("cash").setValue(String.valueOf(cashsum+"0"));//cash
                        //zinfotsv.child("us").setValue("1");//need update
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //reduce from month cash
            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String z02 = (String) dataSnapshot.child("z02").getValue();
                    String z03 = (String) dataSnapshot.child("z03").getValue();

                    if (dataSnapshot.hasChild("z03")) {

                        if(z03.contains("-")){
                            z03 = z03.replace("-","");
                            double z03sum = Double.parseDouble(z03) + Double.parseDouble(sbalance);
                            //double z02sum = Double.parseDouble(z02) + Double.parseDouble(sbalance);
                            //dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue(String.valueOf("-"+z02sum+"0"));
                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(String.valueOf("-"+z03sum+"0"));
                        }else{
                            double z03sum = Double.parseDouble(z03) - Double.parseDouble(sbalance);
                            //double z02sum = Double.parseDouble(z02) - Double.parseDouble(sbalance);
                            //dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue(String.valueOf(z02sum+"0"));
                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(String.valueOf(z03sum+"0"));//tsv
                        }

                    }/*else{
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue(String.valueOf("-"+sbalance));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(String.valueOf("-"+sbalance));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");
                    }*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //reduce from day cash
            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String z02 = (String) dataSnapshot.child("z02").getValue();
                    String z03 = (String) dataSnapshot.child("z03").getValue();

                    if (dataSnapshot.hasChild("z03")) {
                        if(z03.contains("-")){
                            z03 = z03.replace("-","");
                            double z03sum = Double.parseDouble(z03) + Double.parseDouble(sbalance);
                            //double z02sum = Double.parseDouble(z02) + Double.parseDouble(sbalance);
                            //dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue(String.valueOf("-"+z02sum+"0"));
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(String.valueOf("-"+z03sum+"0"));
                        }else{
                            double z03sum = Double.parseDouble(z03) - Double.parseDouble(sbalance);
                            //double z02sum = Double.parseDouble(z02) - Double.parseDouble(sbalance);
                            //dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue(String.valueOf(z02sum+"0"));
                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(String.valueOf(z03sum+"0"));//tsv
                        }
                    }/*else{
                        dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z04").setValue(String.valueOf("-"+sbalance));
                        dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue("0.00");
                        dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                    }*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            Toast.makeText(this, "Expense is saved", Toast.LENGTH_SHORT).show();
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

        /*if (ephone.getText().toString().trim().isEmpty()) {
            flag = 1;
            iphone.setError("Cannot be Empty");
        } else
            iphone.setErrorEnabled(false);*/
        /*if (p18.getText().toString().trim().isEmpty()) {
            flag = 1;
            idate.setError("Cannot be Empty");
        } else
            idate.setErrorEnabled(false);*/
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
        //p15.setText("");
        //ephone.setText("");
        ebalance.setText("");

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
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                p18.setText(format.format(calendar.getTime()));

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }
}
