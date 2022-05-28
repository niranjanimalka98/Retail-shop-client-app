package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.util.Date;

public class Trans extends AppCompatActivity implements View.OnClickListener {

    View v;
    String uid, cusname, cusaddr, pay, repname, repmob, repkey, current, starttsv, startcash, padd,startcheque,startcredit;
    CheckBox cashc, chequec, creditc;
    int dutString, keypush;
    double sum2, b8, d;
    private DatabaseReference db, dbi, dbc, dbs, dbsr, dbr, dbsum, dbin, dup, dupush, dbzsum, zinfotsv, dbcre, du, billdb;
    private FirebaseAuth mAuth;
    static AutoCompleteTextView act, act2, act3, act4;
    AutoAdapter adapter;
    AutoCustAdapter autoCustAdapter;
    AutoRepAdapter autoRepAdapter;
    AutoRouteAdapter autoRouteAdapter;
    ListAdapter listadapter;
    ArrayList<Model> models = new ArrayList<>();
    TextInputLayout date, pt, b3;
    EditText p13,ept,eage,loctext;
    @SuppressLint("StaticFieldLeak")
    static EditText due;
    private int mYear, mMonth, mDay;
    ArrayList<ListModel> listmodel = new ArrayList<>();
    ArrayList<Modelcustomer> modelcustomers = new ArrayList<>();
    ArrayList<Modelroute> modelroute = new ArrayList<>();
    ListView lv;
    Button btn, catbtn, custbtn;
    @SuppressLint("StaticFieldLeak")
    static Button gen;
    boolean set = false;
    @SuppressLint("StaticFieldLeak")
    static TextView totamt, SList, discount;
    static double totamount, damount, d3;
    NativeExpressAdView NAdView;

    Double sums = (double) 0;
    Double disc = (double) 0;
    String items = "";
    //String itemsb6t = "";
    String itemswebs = "";
    //String itemswebsb6t = "";

    private static final String TAG = "Check";
    // declare the variable needed in activity
    MyExpandableAdapter expandableadapter;
    ExpandableListView expandableListView;
    List<String> headersList;
    HashMap<String, List<String>> itemsMap;
    //My Implimentation
    private DatabaseReference mTasksDatabaseReference;
    List<String> tasks;
    private int previousGroup = -1;
    int height = 0;
    DecimalFormat dc;
    double totalcash;
    Calendar csummary;
    static LinearLayout actLayout;
    String time = "";

    static RecyclerView recyclerView;
    ArrayList<String> personNames = new ArrayList<>();
    ArrayList<String> personImages = new ArrayList<String>();

    //private static final String TAG = MainActivity.class.getSimpleName();

    // location last updated time
    private String mLastUpdateTime;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.m_trans);
        setTitle(getString(R.string.nav_drawer_trans));

        dc = new DecimalFormat(".00");

        // initialize the necessary libraries
        init();
        // restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);
        
        MobileAds.initialize(this, String.valueOf(R.string.app_id));
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ad_layout);
        actLayout = (LinearLayout) findViewById(R.id.actlayout);
        // Create a native express ad. The ad size and ad unit ID must be set before calling
        // loadAd.
        NAdView = new NativeExpressAdView(this);
        NAdView.setAdSize(new AdSize(320, 300));
        NAdView.setAdUnitId(String.valueOf(R.string.sma_banner_3));

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        // Add the NativeExpressAdView to the view hierarchy.
        linearLayout.addView(NAdView);

        // Start loading the ad.
        NAdView.loadAd(adRequestBuilder.build());

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        LocaleUtils.updateConfig(this,prefs.getString("My_Lang", ""));
        current = prefs.getString("My_Lang", "");//Locale.getDefault().getDisplayLanguage();
        //if (current.toLowerCase().contains("en")) {}

        Objects.requireNonNull(getSupportActionBar()).hide();

        date = (TextInputLayout) findViewById(R.id.dateLayout);
        pt = (TextInputLayout) findViewById(R.id.ptLayout);
        totamount = 0.00;
        damount = 0.00;
        d3 = 0.00;

        cashc = (CheckBox) findViewById(R.id.cashc);
        cashc.setChecked(true);
        chequec = (CheckBox) findViewById(R.id.chequec);
        creditc = (CheckBox) findViewById(R.id.creditc);
        p13 = (EditText) findViewById(R.id.dateText);
        ept = (EditText) findViewById(R.id.ptText);
        act3 = (AutoCompleteTextView) findViewById(R.id.docText);
        act3.setText(prefs.getString("autoSave", ""));
        act4 = (AutoCompleteTextView) findViewById(R.id.route);
        act4.setText(prefs.getString("autoSaveroute", ""));
        eage = (EditText) findViewById(R.id.ageText);
        SList = (TextView) findViewById(R.id.SList);
        loctext = (EditText) findViewById(R.id.loctext);
        SList.setText("jk-");

        act3.addTextChangedListener(new TextWatcher() {
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

            @SuppressLint("ApplySharedPref")
            @Override
            public void afterTextChanged(Editable s)
            {
                prefs.edit().putString("autoSave", s.toString()).apply();
            }
        });
        act3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                act3.setFocusable(false);
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(act3.getWindowToken(), 0);
            }
        });

        act2 = (AutoCompleteTextView) findViewById(R.id.actcust);
        act2.setFocusable(true);
        act2.requestFocus();
        ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInputFromInputMethod(act2.getWindowToken(), 0);
        /*act2.setText(prefs.getString("autoSaveCustomer", ""));
        act2.addTextChangedListener(new TextWatcher() {
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

            @SuppressLint("ApplySharedPref")
            @Override
            public void afterTextChanged(Editable s)
            {
                prefs.edit().putString("autoSaveCustomer", s.toString()).apply();
            }
        });*/

        act2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                act2.setFocusable(false);
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(act2.getWindowToken(), 0);
            }
        });

        act4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                textchange();
                act4.setFocusable(false);
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(act4.getWindowToken(), 0);
            }
        });

        //act2.setFocusable(true);
        //act2.setFocusableInTouchMode(true);
        //act2.setInputType(InputType.TYPE_CLASS_TEXT);

        act3.setLongClickable(false);
        act3.setClickable(false);
        act3.setFocusableInTouchMode(false);
        act3.setInputType(InputType.TYPE_NULL);
        act3.setEnabled(false);

        act4.setLongClickable(false);
        act4.setClickable(false);
        act4.setFocusableInTouchMode(false);
        act4.setInputType(InputType.TYPE_NULL);
        act4.setEnabled(false);

        act4.addTextChangedListener(new TextWatcher() {
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

            @SuppressLint("ApplySharedPref")
            @Override
            public void afterTextChanged(Editable s)
            {
                prefs.edit().putString("autoSaveroute", s.toString()).apply();
            }
        });

        due = (EditText) findViewById(R.id.amtdue);
        totamt = (TextView) findViewById(R.id.totamt);
        discount = (TextView) findViewById(R.id.discount);

        p13.setInputType(InputType.TYPE_NULL);
        p13.setFocusable(false);
        p13.setOnClickListener(this);
        act = (AutoCompleteTextView) findViewById(R.id.act);
        act.setInputType(InputType.TYPE_NULL);
        final Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        p13.setText(format.format(c.getTime()));

        mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Inventory");
        //dbzsum = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("ZSummary");
        dbi = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Invoice");
        dbin = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("invoices");
        dbc = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer");
        dbcre = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Credit");
        dbsr = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("SalesRep");
        dbs = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Summary");
        dbsum = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("summary");
        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Route");
        zinfotsv = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        billdb = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        btn = (Button) findViewById(R.id.btnadd);
        gen = (Button) findViewById(R.id.genbtn);
        custbtn = (Button) findViewById(R.id.regcust);
        catbtn = (Button) findViewById(R.id.btn_cat);
        linearLayout = (LinearLayout) findViewById(R.id.cust);

        // get the reference of RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a GridLayoutManager with 2 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        CustomAdapter customAdapter = new CustomAdapter(Trans.this, personNames,personImages);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
        
        catbtn.setText("LIST");
        catbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(catbtn.getText().toString().equals("LIST")) {
                    act.setInputType(InputType.TYPE_NULL);
                    recyclerView.setVisibility(View.GONE);
                    expandableListView.setVisibility(View.VISIBLE);
                    catbtn.setText("CAT");
                }else if(catbtn.getText().toString().equals("CAT")){
                    act.setInputType(InputType.TYPE_CLASS_TEXT);
                    expandableListView.setVisibility(View.GONE);
                    catbtn.setText("C");
                }else if(catbtn.getText().toString().equals("C")){
                    act.setInputType(InputType.TYPE_NULL);
                    recyclerView.setVisibility(View.VISIBLE);
                    catbtn.setText("LIST");
                }
            }
        });
        
        final SharedPreferences rep = PreferenceManager
                .getDefaultSharedPreferences(this);
        //get rep
        du.child("SalesRep").child("Person").child(rep.getString("SRep", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("de01").exists()) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                    act3.setText(map.get("p03")+"-"+map.get("p12")+" "+map.get("key"));
                }else{
                    act3.setText("");
                    Toast.makeText(Trans.this,"SalesRep not paired", Toast.LENGTH_LONG).show();
                    /*Intent k = new Intent(Trans.this, MainActivity.class);
                    startActivity(k);*/
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        File folder = new File(Environment.getExternalStorageDirectory(), "products/");
        for (File file : folder.listFiles()) {
            String filename = file.getName().toLowerCase();
            if ( filename.endsWith("jpeg")) {
                personImages.add(filename);
            }
        }

        //For Reading Tasks(Not Using For Now)
        if (current.toLowerCase().contains("en")) {
            mTasksDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Category").child("C");
        }else{
            mTasksDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Category").child("Ct");
        }
        //Now Saving Tasks(from firebase nodes:task1,task2,task3) to ArrayList and then adding data to HashMap
        itemsMap = new HashMap<String, List<String>>();
        headersList = new ArrayList<>();
        tasks = new ArrayList<>();

        createData();
        //get expandable listview
        expandableListView = (ExpandableListView) findViewById(R.id.ExlistView);

        // get expandable list adapter
        expandableadapter = new MyExpandableAdapter(this, headersList, itemsMap);
        //set list adapter to list
        expandableListView.setAdapter(expandableadapter);

        //Refreshing Adapter
        expandableadapter.notifyDataSetChanged();

        // Listview Group click listener
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                /*Toast.makeText(Trans.this,
                        "Group Clicked " + headersList.get(groupPosition),
                        Toast.LENGTH_SHORT).show();*/

                // only one group is populate using this
                if (expandableListView.isGroupExpanded(groupPosition)) {
                    expandableListView.collapseGroup(groupPosition);
                    previousGroup=-1;
                    expandableListView.getLayoutParams().height = (height)*2;
                } else {
                    expandableListView.expandGroup(groupPosition);
                    if(previousGroup!=-1){
                        expandableListView.collapseGroup(previousGroup);
                    }
                    previousGroup=groupPosition;
                }
                return true;
            }
        });

        //get the expand of headersList
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < expandableListView.getChildCount(); i++) {
                    height += expandableListView.getChildAt(i).getMeasuredHeight();
                    height += expandableListView.getDividerHeight();
                }
                expandableListView.getLayoutParams().height = (height)*5;
                //expandableListView.setMinimumHeight(expandableListView.getHeaderViewsCount());

                //Toast.makeText(Trans.this, headersList.get(groupPosition), Toast.LENGTH_SHORT).show();
            }
        });
        //get the collapse of headersList
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

                //height -= expandableListView.getChildCount();
                //height -= expandableListView.getDividerHeight();

                //expandableListView.getLayoutParams().height = (height)*2;
                //height = 0;
                //expandableListView.setMinimumHeight(expandableListView.getHeaderViewsCount());
                //Toast.makeText(Trans.this, headersList.get(groupPosition), Toast.LENGTH_SHORT).show();
            }
        });

        //handling the header items click
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Toast.makeText(Returns.this, headersList.get(groupPosition) + "--" + itemsMap.get(headersList.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                String finals = Objects.requireNonNull(itemsMap.get(headersList.get(groupPosition))).get(childPosition);

                actLayout.setVisibility(View.VISIBLE);
                ((AutoCompleteTextView) findViewById(R.id.act)).requestFocus();
                act.setInputType(InputType.TYPE_NULL); // disable soft input
                act.setFocusable(true);
                act.setPressed(true);
                act.setActivated(true);
                act.setText(finals);
                act.setSelection(act.getText().length());
                return false;
            }
        });

        act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                addbtn();
                actLayout.setVisibility(View.INVISIBLE);
                //Toast.makeText(Returns.this," selected", Toast.LENGTH_LONG).show();
            }
        });

        // get count
        dupush = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Invoice");
        dupush.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                keypush = Integer.parseInt(Objects.requireNonNull(map.get("id")));
                dutString = Integer.parseInt(Objects.requireNonNull(map.get("count")));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

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
                if(!todate.equals(format.format(c.getTime()))){
                    startthemonth();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        // Requesting ACCESS_FINE_LOCATION using Dexter library
        // start location
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


        gen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (gen.getText().equals("START") && !act3.getText().toString().isEmpty()){

                    startthemonth();
                }

                if(creditc.isChecked()) {
                    if (p13.getText().toString().equals("12/12/2012")) {
                        act3.setLongClickable(true);
                        act3.setClickable(true);
                        act3.setFocusableInTouchMode(true);
                        act3.setInputType(InputType.TYPE_CLASS_TEXT);
                        act3.setEnabled(true);
                        creditc.setChecked(false);
                        Toast.makeText(Trans.this, R.string.trans_assignrep, Toast.LENGTH_SHORT).show();
                    }
                }

                if (validate()) {
                    if (custValidate()) {
                        zinfotsv = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
                        zinfotsv.addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                                };
                                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                                String todate = map.get("day");

                        if(todate.equals(format.format(c.getTime()))) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(Trans.this);
                            alert.setTitle(R.string.trans_confirm);

                            double totalpay = Double.parseDouble(totamt.getText().toString().trim()) - Double.parseDouble(discount.getText().toString().trim()) - Double.parseDouble(due.getText().toString().trim());
                            double totalamount = Double.parseDouble(totamt.getText().toString().trim()) - Double.parseDouble(discount.getText().toString().trim());
                            if (cashc.isChecked() && chequec.isChecked()) {
                                pay = "Cash & Cheque";
                                alert.setMessage("INVOICE BILL" + "\n\n" + "Total: " + totamt.getText().toString() + "\n" + "Discount: " + discount.getText().toString()
                                        + "\n" + "Cash: " + due.getText().toString() + "\n" + "Cheque: " + totalpay + "\n\n" + "Total Amount: " + totalamount + "\n" + "Payment Method: " + pay);
                            } else if (cashc.isChecked() && creditc.isChecked()) {
                                pay = "Cash & Credit";
                                alert.setMessage("INVOICE BILL" + "\n\n" + "Total: " + totamt.getText().toString() + "\n" + "Discount: " + discount.getText().toString()
                                        + "Cash: " + due.getText().toString() + "\n" + "Credit: " + totalpay + "\n\n" + "Total Amount: " + totalamount  + "Payment Method: " + pay);
                            } else if (chequec.isChecked() && creditc.isChecked()) {
                                pay = "Cheque & Credit";
                                alert.setMessage("INVOICE BILL" + "\n\n" + "Total: " + totamt.getText().toString() + "\n" + "Discount: " + discount.getText().toString()
                                        + "Cheque: " + due.getText().toString() + "\n" + "Credit: " + totalpay + "\n\n" + "Total Amount: " + totalamount + "Payment Method: " + pay);
                            } else if (chequec.isChecked()) {
                                pay = "Cheque";
                                alert.setMessage("INVOICE BILL" + "\n\n" + "Total: " + totamt.getText().toString() + "\n"+ "Discount: " + discount.getText().toString()
                                        + "\n" + "Cheque: " + due.getText().toString() +"\n\n" + "Total Amount: " + totalamount + "\n" + "Payment Method: " + pay);
                            } else if (creditc.isChecked()) {
                                pay = "Credit";
                                alert.setMessage("INVOICE BILL" + "\n\n" + "Total: " + totamt.getText().toString() + "\n"+ "Discount: " + discount.getText().toString()
                                        + "\n" + "Credit: " + due.getText().toString() + "\n\n" + "Total Amount: " + totalamount + "Payment Method: " + pay);
                            } else if (cashc.isChecked()) {
                                pay = "Cash";
                                alert.setMessage("INVOICE BILL" + "\n\n" + "Total: " + totamt.getText().toString() + "\n" + "Discount: " + discount.getText().toString()
                                        + "\n" + "Cash: " + due.getText().toString() + "\n\n" + "Total Amount: " + totalamount + "Payment Method: " + pay);
                            }
                            final String payment = pay;

                            alert.setPositiveButton(R.string.trans_yes, new DialogInterface.OnClickListener() {

                            /*final ProgressDialog loadingDialog = ProgressDialog.show(Trans.this,
                                    "","Please wait...",false,false);*/

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final int size = listmodel.size();
                                    final String b4 = p13.getText().toString().trim();
                                    final String b5 = act3.getText().toString().trim();
                                    final String b2 = cusname;
                                    final String gage = autoCustAdapter.detcust()[3];

                                    int index = b5.lastIndexOf(" ");
                                    final String reponly = b5.substring(0, index);
                                    repkey = b5.substring(index + 1);

                                    billdb.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.hasChild("Bill")) {
                                                Toast.makeText(Trans.this, "Another bill in process", Toast.LENGTH_SHORT).show();
                                            } else {
                                                billdb.child("Bill").child("Rep").setValue(repkey);

                                                // Updating customer detail
                                                if (set) {
                                                    updateCust(autoCustAdapter.detcust()[0], due.getText().toString(), autoCustAdapter.detcust()[1],
                                                            autoCustAdapter.detcust()[2], autoCustAdapter.detcust()[3], autoCustAdapter.detcust()[4],
                                                            autoCustAdapter.detcust()[5]);
                                                }

                                                if (!b5.contains("-")) {
                                                    StringTokenizer tokens = new StringTokenizer(b5, "-");
                                                    repname = tokens.nextToken();// this will contain "Name"
                                                    repmob = "";
                                                    set = false;
                                                } else {
                                                    StringTokenizer tokens = new StringTokenizer(b5, "-");
                                                    repname = tokens.nextToken();// this will contain "Name"
                                                    repmob = tokens.nextToken();// this will contain "Mob"
                                                }

                                                Double[] sumlist = new Double[size];
                                                Double[] discounts = new Double[size];
                                                String[] itemlist = new String[size];
                                                //String[] itemlistb6t = new String[size];

                                                int itemcnt = 0;
                                                for (int i = 0; i < size; i++) {
                                                    String getP21 = listmodel.get(i).getP21();
                                                    String getP21w = "";
                                                    if (getP21.equals("") || getP21.isEmpty() || Double.parseDouble(getP21) < 1) {
                                                        getP21 = "";
                                                    } else {
                                                        getP21 = listmodel.get(i).getP21();
                                                        getP21w = "<br>" + "Discount: " + listmodel.get(i).getP21();
                                                    }
                                                    itemlist[i] = listmodel.get(i).getMed() + "©" + listmodel.get(i).getUnit() + "©"
                                                            /*+ listmodel.get(i).getBatch() + "©"*/ + listmodel.get(i).getQty() + "©"
                                                            + listmodel.get(i).getPr() + "©" /*+ listmodel.get(i).getExpdate() + "©"*/
                                                            + listmodel.get(i).getAmount() + "©" + getP21;
                                                    //itemlistb6t[i] = listmodel.get(i).getP03() + "©" + listmodel.get(i).getUnit() + "©"
                                                    ///*+ listmodel.get(i).getBatch() + "©"*/ + listmodel.get(i).getQty() + "©"
                                                    //+ listmodel.get(i).getPr() + "©" /*+ listmodel.get(i).getExpdate() + "©"*/
                                                    //+ listmodel.get(i).getAmount() + "©" + getP21;

                                                    if (items.equals("")) {
                                                        items = items + itemlist[i];
                                                        /*itemsb6t = itemsb6t + itemlistb6t[i];*/
                                                        itemswebs = itemswebs + listmodel.get(i).getMed() + " - " + listmodel.get(i).getUnit() + " x "
                                                                + listmodel.get(i).getQty() + " x " + listmodel.get(i).getPr() + " = "
                                                                + listmodel.get(i).getAmount() + getP21w;
                                                    /*itemswebsb6t = itemswebsb6t + listmodel.get(i).getP03() + " - " + listmodel.get(i).getUnit() + " x "
                                                            + listmodel.get(i).getQty() + " x " + listmodel.get(i).getPr() + " = "
                                                            + listmodel.get(i).getAmount() + getP21w;*/
                                                    } else {
                                                        items = items + "®" + itemlist[i];
                                                        /*itemsb6t = itemsb6t + "®" + itemlistb6t[i];*/
                                                        itemswebs = itemswebs + "<br>" + listmodel.get(i).getMed() + " - " + listmodel.get(i).getUnit() + " x "
                                                                + listmodel.get(i).getQty() + " x " + listmodel.get(i).getPr() + " = "
                                                                + listmodel.get(i).getAmount() + getP21w;
                                                    /*itemswebsb6t = itemswebsb6t + "<br>" + listmodel.get(i).getP03() + " - " + listmodel.get(i).getUnit() + " x "
                                                            + listmodel.get(i).getQty() + " x " + listmodel.get(i).getPr() + " = "
                                                            + listmodel.get(i).getAmount() + getP21w;*/
                                                    }

                                                    itemcnt += Integer.parseInt(listmodel.get(i).getQty());

                                                    // updating itemquantity
                                                    updateQty(listmodel.get(i).getKey(), listmodel.get(i).getMed(), listmodel.get(i).getP03(), listmodel.get(i).getQty(), listmodel.get(i).getOrgqty(),
                                                            listmodel.get(i).getP06(), listmodel.get(i).getP07(), listmodel.get(i).getExpdate(), listmodel.get(i).getBatch(),
                                                            listmodel.get(i).getPr(), listmodel.get(i).getUnit(), listmodel.get(i).getP11(), listmodel.get(i).getP04(), listmodel.get(i).getP18(),
                                                            listmodel.get(i).getP19(), listmodel.get(i).getP20(), listmodel.get(i).getP21());

                                                    //Toast.makeText(Trans.this, itemswebs, Toast.LENGTH_SHORT).show();
                                                    double sum = Double.parseDouble(listmodel.get(i).getP06()) / 100 * Double.parseDouble(listmodel.get(i).getP07());
                                                    double sum1 = Double.parseDouble(listmodel.get(i).getP06()) + sum;
                                                    sum2 = Double.parseDouble(listmodel.get(i).getQty()) * sum1;
                                                    sumlist[i] = Double.parseDouble(String.valueOf(sum2));
                                                    sums = sums + sumlist[i];

                                                    discounts[i] = Double.parseDouble(String.valueOf(listmodel.get(i).getP21()));
                                                    disc = disc + discounts[i];
                                                    //Toast.makeText(Trans.this, String.valueOf(sums), Toast.LENGTH_SHORT).show();
                                                }

                                                final String b7 = dc.format(totamount);
                                                keypush = keypush + 1;
                                                @SuppressLint("DefaultLocale") String BillNo = String.format("%06d", keypush);

                                                // save to firebase  //inside MMyyyy=b4
                                                csummary = Calendar.getInstance();
                                                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformatday = new SimpleDateFormat("dd/MM/yyyy");
                                                @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");
                                                @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformatm = new SimpleDateFormat("MM/yyyy");
                                                @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
                                                final String mmyyy = monthformat.format(csummary.getTime()).toString().replace("/", "");
                                                //final String mmyyy = mmyyyy.substring(2);

                                                final String k = dbi.child(mmyyy).child("Bill").push().getKey();
                                                dbi.child(mmyyy).child("Bill").child(k).child("b01").setValue(repkey + "-" + BillNo);
                                                dbi.child(mmyyy).child("Bill").child(k).child("b04").setValue(b4);
                                                dbi.child(mmyyy).child("Bill").child(k).child("b05").setValue(reponly);
                                                dbi.child(mmyyy).child("Bill").child(k).child("b02").setValue(b2 + "-" + gage);
                                                dbi.child(mmyyy).child("Bill").child(k).child("b06").setValue(items);
                                                //dbi.child(mmyyy).child("Bill").child(k).child("b06t").setValue(itemsb6t);
                                                dbi.child(mmyyy).child("Bill").child(k).child("b07").setValue(b7);

                                                int jk = due.getText().toString().lastIndexOf('.');
                                                if (jk != -1 && due.getText().toString().substring(jk + 1).length() == 2) {
                                                    dbi.child(mmyyy).child("Bill").child(k).child("b08").setValue(due.getText().toString());
                                                    b8 = Double.parseDouble(due.getText().toString());
                                                } else if (jk != -1 && due.getText().toString().substring(jk + 1).length() == 1) {
                                                    dbi.child(mmyyy).child("Bill").child(k).child("b08").setValue(due.getText().toString() + "0");
                                                    b8 = Double.parseDouble(due.getText().toString() + "0");
                                                } else if (jk != -1 && due.getText().toString().substring(jk + 1).length() == 0) {
                                                    dbi.child(mmyyy).child("Bill").child(k).child("b08").setValue(due.getText().toString() + "00");
                                                    b8 = Double.parseDouble(due.getText().toString() + "00");
                                                } else {
                                                    dbi.child(mmyyy).child("Bill").child(k).child("b08").setValue(due.getText().toString() + ".00");
                                                    b8 = Double.parseDouble(due.getText().toString() + ".00");
                                                }

                                                double bal = Double.parseDouble(b7) - (Double.parseDouble(due.getText().toString()) + Double.parseDouble(String.valueOf(d3)));
                                                if (bal == 0) {
                                                    dbi.child(mmyyy).child("Bill").child(k).child("b09").setValue("0" + dc.format(bal));
                                                    bal = Double.parseDouble("0" + dc.format(bal));
                                                } else {
                                                    dbi.child(mmyyy).child("Bill").child(k).child("b09").setValue(dc.format(bal));
                                                    bal = Double.parseDouble(dc.format(bal));
                                                }
                                                if (d3 > 0) {
                                                    dbi.child(mmyyy).child("Bill").child(k).child("b11").setValue(String.valueOf(dc.format(d3)));
                                                }

                                                Calendar calendar = Calendar.getInstance();
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat fmbill = new SimpleDateFormat("HH:mm:ss");

                                                dbi.child(mmyyy).child("Bill").child(k).child("b10").setValue(fmbill.format(calendar.getTime()));
                                                dbi.child(mmyyy).child("Bill").child(k).child("key").setValue(k);
                                                dbi.child(mmyyy).child("Bill").child(k).child("pay").setValue(payment);
                                                dbi.child(mmyyy).child("Bill").child(k).child("p22").setValue(loctext.getText().toString());

                                                //for web
                                                if (d3 > 0) {
                                                    padd = "[{\"name\":\"b01\",\"value\":\"" + repkey + "-" + BillNo + "\"}," +
                                                            "{\"name\":\"b02\",\"value\":\"" + b2 + "\"}," +
                                                            "{\"name\":\"b04\",\"value\":\"" + b4 + "\"}," +
                                                            "{\"name\":\"b10\",\"value\":\"" + fmbill.format(calendar.getTime()) + "\"}," +
                                                            "{\"name\":\"b05\",\"value\":\"" + repname + "\"}," +
                                                            "{\"name\":\"b06\",\"value\":\"" + itemswebs + "\"}," +
                                                            //"{\"name\":\"b06t\",\"value\":\"" + itemswebsb6t + "\"}," +
                                                            "{\"name\":\"b07\",\"value\":\"" + b7 + "\"}," +
                                                            "{\"name\":\"b11\",\"value\":\"" + d3 + "\"}," +
                                                            "{\"name\":\"b08\",\"value\":\"" + b8 + "\"}," +
                                                            "{\"name\":\"b09\",\"value\":\"" + bal + "\"}," +
                                                            "{\"name\":\"p22\",\"value\":\"" + loctext.getText().toString() + "\"}," +
                                                            "{\"name\":\"key\",\"value\":\"" + k + "\"}," +
                                                            "{\"name\":\"pay\",\"value\":\"" + payment + "\"}]";
                                                } else {
                                                    padd = "[{\"name\":\"b01\",\"value\":\"" + repkey + "-" + BillNo + "\"}," +
                                                            "{\"name\":\"b02\",\"value\":\"" + b2 + "\"}," +
                                                            "{\"name\":\"b04\",\"value\":\"" + b4 + "\"}," +
                                                            "{\"name\":\"b10\",\"value\":\"" + fmbill.format(calendar.getTime()) + "\"}," +
                                                            "{\"name\":\"b05\",\"value\":\"" + repname + "\"}," +
                                                            "{\"name\":\"b06\",\"value\":\"" + itemswebs + "\"}," +
                                                            //"{\"name\":\"b06t\",\"value\":\"" + itemswebsb6t + "\"}," +
                                                            "{\"name\":\"b07\",\"value\":\"" + b7 + "\"}," +
                                                            "{\"name\":\"b08\",\"value\":\"" + b8 + "\"}," +
                                                            "{\"name\":\"b09\",\"value\":\"" + bal + "\"}," +
                                                            "{\"name\":\"p22\",\"value\":\"" + loctext.getText().toString() + "\"}," +
                                                            "{\"name\":\"key\",\"value\":\"" + k + "\"}," +
                                                            "{\"name\":\"pay\",\"value\":\"" + payment + "\"}]";
                                                }
                                                // add to invoices
                                                dbin.getParent().child("invoices").child(mmyyy).child(k).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        dbin.getParent().child("invoices").child(mmyyy).child(k)
                                                                .setValue(padd);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                    }
                                                });

                                                // set count
                                                dupush.getParent().child("Invoice").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        dupush.getParent().child("Invoice").child("count")
                                                                .setValue(String.valueOf(++dutString));
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                    }
                                                });
                                                // set id
                                                dupush.getParent().child("Invoice").child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        dupush.getParent().child("Invoice").child("id")
                                                                .setValue(String.valueOf(keypush));
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError error) {
                                                    }
                                                });

                                                final int icount = itemcnt;
                                                final double amt = Double.parseDouble(dc.format(totamount));

                                                //day
                                                dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String z01 = (String) dataSnapshot.child("z01").getValue();
                                                        String z02 = (String) dataSnapshot.child("z02").getValue();
                                                        String z03 = (String) dataSnapshot.child("z03").getValue();
                                                        String z04 = (String) dataSnapshot.child("z04").getValue();
                                                        String z05 = (String) dataSnapshot.child("z05").getValue();
                                                        String z06 = (String) dataSnapshot.child("z06").getValue();
                                                        String z07 = (String) dataSnapshot.child("z07").getValue();
                                                        String z08 = (String) dataSnapshot.child("z08").getValue();
                                                        String z09 = (String) dataSnapshot.child("z09").getValue();
                                                        //String rep = (String) dataSnapshot.child(repkey).getValue();

                                                        if (dataSnapshot.hasChild("z00")) {

                                                            String date = (String) dataSnapshot.child(dayformatday.format(csummary.getTime())).getValue();
                                                            //String month = (String) dataSnapshot.child("s03").getValue();
                                                            int tcount = Integer
                                                                    .parseInt((String) dataSnapshot.child("z04").getValue()) + 1;
                                                            int count = Integer
                                                                    .parseInt((String) dataSnapshot.child("z03").getValue());
                                                        /*int dreturn = Integer
                                                                .parseInt((String) dataSnapshot.child("s05").getValue());*/
                                                            double value = Double
                                                                    .parseDouble((String) dataSnapshot.child("z02").getValue());
                                                            double grvalue = Double
                                                                    .parseDouble((String) dataSnapshot.child("z08").getValue());
                                                            double dreturn = Double
                                                                    .parseDouble((String) dataSnapshot.child("z05").getValue());
                                                            double tsv = Double
                                                                    .parseDouble((String) dataSnapshot.child("z01").getValue());

                                                            double endtsv = Double
                                                                    .parseDouble((String) dataSnapshot.child("z09").getValue());

                                                            double reptsv = 0.00;
                                                            if (dataSnapshot.hasChild(repkey)) {
                                                                reptsv = Double
                                                                        .parseDouble((String) dataSnapshot.child(repkey).getValue());
                                                            }

                                                            value = Double.parseDouble(dc.format(value));
                                                            grvalue = Double.parseDouble(dc.format(grvalue));
                                                            dreturn = Double.parseDouble(dc.format(dreturn));
                                                            tsv = Double.parseDouble(dc.format(tsv));
                                                            endtsv = Double.parseDouble(dc.format(endtsv));

                                                            //double tiv2 = ((tsv - sums) - value) + dreturn + grvalue;
                                                            double tiv2 = endtsv - sums;

                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z04").setValue(String.valueOf(tcount));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z03").setValue(String.valueOf(count + icount));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z02").setValue(String.valueOf(dc.format(value + amt)));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child(repkey).setValue(String.valueOf(dc.format(reptsv + amt)));
                                                            //Toast.makeText(Trans.this, "S1 - "+ roundOff(tiv2), Toast.LENGTH_SHORT).show();
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z09").setValue(String.valueOf(dc.format(tiv2)));
                                                            zinfotsv.child("tsv").setValue(String.valueOf(dc.format(tiv2)));

                                                            //dbs.child("d").child(dayformat.format(csummary.getTime())).child("s08").setValue(tcount + "");

                                                            String sum = "[{\"name\":\"z00\",\"value\":\"" + dayformatday.format(csummary.getTime()) + "\"}," +
                                                                    "{\"name\":\"z01\",\"value\":\"" + tsv + "\"}," +
                                                                    "{\"name\":\"z02\",\"value\":\"" + dc.format(value + amt) + "\"}," +
                                                                    "{\"name\":\"z03\",\"value\":\"" + (count + icount) + "\"}," +
                                                                    "{\"name\":\"z04\",\"value\":\"" + tcount + "\"}," +
                                                                    "{\"name\":\"z05\",\"value\":\"" + z05 + "\"}," +
                                                                    "{\"name\":\"z06\",\"value\":\"" + z06 + "\"}," +
                                                                    "{\"name\":\"z07\",\"value\":\"" + z07 + "\"}," +
                                                                    "{\"name\":\"z08\",\"value\":\"" + z08 + "\"}," +
                                                                    "{\"name\":\"z09\",\"value\":\"" + dc.format(tiv2) + "\"}]";
                                                            dbsum.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).setValue(sum);

                                                        } else {
                                                            Toast.makeText(Trans.this, "Start the day", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });

                                                //month
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String z01 = (String) dataSnapshot.child("z01").getValue();
                                                        String z02 = (String) dataSnapshot.child("z02").getValue();
                                                        String z03 = (String) dataSnapshot.child("z03").getValue();
                                                        String z04 = (String) dataSnapshot.child("z04").getValue();
                                                        String z05 = (String) dataSnapshot.child("z05").getValue();
                                                        String z06 = (String) dataSnapshot.child("z06").getValue();
                                                        String z07 = (String) dataSnapshot.child("z07").getValue();
                                                        String z08 = (String) dataSnapshot.child("z08").getValue();
                                                        String z09 = (String) dataSnapshot.child("z09").getValue();
                                                        //String rep = (String) dataSnapshot.child(repkey).getValue();

                                                        if (dataSnapshot.hasChild("z00")) {

                                                            String date = (String) dataSnapshot.child(monthformat.format(csummary.getTime())).getValue();
                                                            //String month = (String) dataSnapshot.child("s03").getValue();
                                                            int tcount = Integer
                                                                    .parseInt((String) dataSnapshot.child("z04").getValue()) + 1;
                                                            int count = Integer
                                                                    .parseInt((String) dataSnapshot.child("z03").getValue());
                                                        /*int mcount = Integer
                                                                .parseInt((String) dataSnapshot.child("s07").getValue());*/
                                                            double value = Double
                                                                    .parseDouble((String) dataSnapshot.child("z02").getValue());
                                                            double grvalue = Double
                                                                    .parseDouble((String) dataSnapshot.child("z08").getValue());
                                                            double dreturn = Double
                                                                    .parseDouble((String) dataSnapshot.child("z05").getValue());
                                                            double tsv = Double
                                                                    .parseDouble((String) dataSnapshot.child("z01").getValue());
                                                            double endtsv = Double
                                                                    .parseDouble((String) dataSnapshot.child("z09").getValue());

                                                            double reptsv = 0.00;
                                                            if (dataSnapshot.hasChild(repkey)) {
                                                                reptsv = Double
                                                                        .parseDouble((String) dataSnapshot.child(repkey).getValue());
                                                            }

                                                            value = Double.parseDouble(dc.format(value));
                                                            grvalue = Double.parseDouble(dc.format(grvalue));
                                                            dreturn = Double.parseDouble(dc.format(dreturn));
                                                            tsv = Double.parseDouble(dc.format(tsv));
                                                            endtsv = Double.parseDouble(dc.format(endtsv));

                                                            //double tiv2 = ((tsv - sums) - value) + dreturn + grvalue;
                                                            double tiv2 = endtsv - sums;

                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z04").setValue(String.valueOf(tcount));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z03").setValue(String.valueOf(count + icount));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z02").setValue(String.valueOf(dc.format(value + amt)));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child(repkey).setValue(String.valueOf(dc.format(reptsv + amt)));
                                                            //Toast.makeText(Trans.this, "S1 - "+ roundOff(tiv2), Toast.LENGTH_SHORT).show();
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z09").setValue(String.valueOf(dc.format(tiv2)));
                                                            //dbs.child("d").child(dayformat.format(csummary.getTime())).child("s08").setValue(tcount + "");

                                                            String sum = "[{\"name\":\"z00\",\"value\":\"" + monthformatm.format(csummary.getTime()) + "\"}," +
                                                                    "{\"name\":\"z01\",\"value\":\"" + tsv + "\"}," +
                                                                    "{\"name\":\"z02\",\"value\":\"" + dc.format(value + amt) + "\"}," +
                                                                    "{\"name\":\"z03\",\"value\":\"" + (count + icount) + "\"}," +
                                                                    "{\"name\":\"z04\",\"value\":\"" + tcount + "\"}," +
                                                                    "{\"name\":\"z05\",\"value\":\"" + z05 + "\"}," +
                                                                    "{\"name\":\"z06\",\"value\":\"" + z06 + "\"}," +
                                                                    "{\"name\":\"z07\",\"value\":\"" + z07 + "\"}," +
                                                                    "{\"name\":\"z08\",\"value\":\"" + z08 + "\"}," +
                                                                    "{\"name\":\"z09\",\"value\":\"" + dc.format(tiv2) + "\"}]";
                                                            dbsum.child("m").child(monthformat.format(csummary.getTime())).setValue(sum);

                                                        } else {
                                                            Toast.makeText(Trans.this, "Start the month", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });

                                                //month money
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").addListenerForSingleValueEvent(new ValueEventListener() {

                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        String z01 = (String) dataSnapshot.child("z01").getValue();
                                                        String z03 = (String) dataSnapshot.child("z03").getValue();
                                                        String z05 = (String) dataSnapshot.child("z05").getValue();
                                                        String z06 = (String) dataSnapshot.child("z06").getValue();
                                                        String z08 = (String) dataSnapshot.child("z08").getValue();
                                                        String z09 = (String) dataSnapshot.child("z09").getValue();
                                                        String z10 = (String) dataSnapshot.child("z10").getValue();

                                                        //if (dataSnapshot.hasChild("z03")) {

                                                        if (cashc.isChecked() && chequec.isChecked()) {
                                                            //cash
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            double totalcheque = Double.parseDouble(z06) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()) + Double.parseDouble(String.valueOf(damount)));

                                                            if (totalcheque == 0) {
                                                                totalcheque = Double.parseDouble("0" + totalcheque);
                                                            }
                                                            if (totalcash == 0) {
                                                                totalcash = Double.parseDouble("0" + totalcash);
                                                            }
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            double daycheque = Double.parseDouble(z05) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()) /*+ Double.parseDouble(String.valueOf(damount))*/);
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            //zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                            //cheque
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            //zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));

                                                        } else if (cashc.isChecked() && creditc.isChecked()) {
                                                            //cash
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()) /*+ Double.parseDouble(String.valueOf(damount))*/);

                                                            if (totalcredit == 0) {
                                                                totalcredit = Double.parseDouble("0" + totalcredit);
                                                            }
                                                            if (totalcash == 0) {
                                                                totalcash = Double.parseDouble("0" + totalcash);
                                                            }
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            double daycredit = Double.parseDouble(z08) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()) /*+ Double.parseDouble(String.valueOf(damount))*/);
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            //zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                            //cheque
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            //zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (chequec.isChecked() && creditc.isChecked()) {
                                                            //cash
                                                            double totalcheque = Double.parseDouble(z06) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()) /*+ Double.parseDouble(String.valueOf(damount))*/);

                                                            if (totalcredit == 0) {
                                                                totalcredit = Double.parseDouble("0" + totalcredit);
                                                            }
                                                            if (totalcheque == 0) {
                                                                totalcheque = Double.parseDouble("0" + totalcheque);
                                                            }

                                                            double daycheque = Double.parseDouble(z05) + Double.parseDouble(due.getText().toString());
                                                            double daycredit = Double.parseDouble(z08) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()) /*+ Double.parseDouble(String.valueOf(damount))*/);
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            //zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));
                                                            //cheque
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            //zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (chequec.isChecked()) {
                                                            double daycheque = Double.parseDouble(z05) + Double.parseDouble(due.getText().toString());
                                                            double totalcheque = Double.parseDouble(z06) + Double.parseDouble(due.getText().toString());
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));

                                                        } else if (creditc.isChecked()) {
                                                            double daycredit = Double.parseDouble(z08) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + Double.parseDouble(due.getText().toString());
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));

                                                        } else if (cashc.isChecked()) {
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                        }

                                                        if (d3 > 0) {
                                                            double discou = Double.parseDouble(z10) + Double.parseDouble(discount.getText().toString());
                                                            //Toast.makeText(Trans.this, (int) Double.parseDouble(z10) + "\\" + Double.parseDouble(discount.getText().toString()), Toast.LENGTH_SHORT).show();
                                                            dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z10").setValue(dc.format(discou));
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
                                                        String z01 = (String) dataSnapshot.child("z01").getValue();
                                                        String z03 = (String) dataSnapshot.child("z03").getValue();
                                                        String z05 = (String) dataSnapshot.child("z05").getValue();
                                                        String z06 = (String) dataSnapshot.child("z06").getValue();
                                                        String z08 = (String) dataSnapshot.child("z08").getValue();
                                                        String z09 = (String) dataSnapshot.child("z09").getValue();
                                                        String z10 = (String) dataSnapshot.child("z10").getValue();

                                                        //if (dataSnapshot.hasChild("z03")) {

                                                        if (cashc.isChecked() && chequec.isChecked()) {
                                                            //cash
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            double totalcheque = Double.parseDouble(z06) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                            if (totalcheque == 0) {
                                                                totalcheque = Double.parseDouble("0" + totalcheque);
                                                            }
                                                            if (totalcash == 0) {
                                                                totalcash = Double.parseDouble("0" + totalcash);
                                                            }
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            double daycheque = Double.parseDouble(z05) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                            //cheque
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));

                                                        } else if (cashc.isChecked() && creditc.isChecked()) {
                                                            //cash
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                            if (totalcredit == 0) {
                                                                totalcredit = Double.parseDouble("0" + totalcredit);
                                                            }
                                                            if (totalcash == 0) {
                                                                totalcash = Double.parseDouble("0" + totalcash);
                                                            }
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            double daycredit = Double.parseDouble(z08) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                            //cheque
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (chequec.isChecked() && creditc.isChecked()) {
                                                            //cash
                                                            double totalcheque = Double.parseDouble(z06) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                            if (totalcredit == 0) {
                                                                totalcredit = Double.parseDouble("0" + totalcredit);
                                                            }
                                                            if (totalcheque == 0) {
                                                                totalcheque = Double.parseDouble("0" + totalcheque);
                                                            }
                                                            double daycheque = Double.parseDouble(z05) + Double.parseDouble(due.getText().toString());
                                                            double daycredit = Double.parseDouble(z08) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));
                                                            //cheque
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (chequec.isChecked()) {
                                                            double daycheque = Double.parseDouble(z05) + Double.parseDouble(totamt.getText().toString());
                                                            double totalcheque = Double.parseDouble(z06) + Double.parseDouble(totamt.getText().toString());
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));

                                                        } else if (creditc.isChecked()) {
                                                            double daycredit = Double.parseDouble(z08) + Double.parseDouble(totamt.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + Double.parseDouble(totamt.getText().toString());
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (cashc.isChecked()) {
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(totamt.getText().toString());
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(totamt.getText().toString());
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                        }
                                                        if (cashc.isChecked() && chequec.isChecked()) {
                                                            //cash
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            double totalcheque = Double.parseDouble(z06) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                            if (totalcheque == 0) {
                                                                totalcheque = Double.parseDouble("0" + totalcheque);
                                                            }
                                                            if (totalcash == 0) {
                                                                totalcash = Double.parseDouble("0" + totalcash);
                                                            }
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            double daycheque = Double.parseDouble(z05) + (Double.parseDouble(totamt.getText().toString()) - (Double.parseDouble(due.getText().toString())));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                            //cheque
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));

                                                        } else if (cashc.isChecked() && creditc.isChecked()) {
                                                            //cash
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                            if (totalcredit == 0) {
                                                                totalcredit = Double.parseDouble("0" + totalcredit);
                                                            }
                                                            if (totalcash == 0) {
                                                                totalcash = Double.parseDouble("0" + totalcash);
                                                            }
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            double daycredit = Double.parseDouble(z08) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                            //cheque
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (chequec.isChecked() && creditc.isChecked()) {
                                                            //cash
                                                            double totalcheque = Double.parseDouble(z06) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                            if (totalcredit == 0) {
                                                                totalcredit = Double.parseDouble("0" + totalcredit);
                                                            }
                                                            if (totalcheque == 0) {
                                                                totalcheque = Double.parseDouble("0" + totalcheque);
                                                            }
                                                            double daycheque = Double.parseDouble(z05) + Double.parseDouble(due.getText().toString());
                                                            double daycredit = Double.parseDouble(z08) + (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));
                                                            //cheque
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (chequec.isChecked()) {
                                                            double daycheque = Double.parseDouble(z05) + Double.parseDouble(due.getText().toString());
                                                            double totalcheque = Double.parseDouble(z06) + Double.parseDouble(due.getText().toString());
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                            zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));

                                                        } else if (creditc.isChecked()) {
                                                            double daycredit = Double.parseDouble(z08) + Double.parseDouble(due.getText().toString());
                                                            double totalcredit = Double.parseDouble(z09) + Double.parseDouble(due.getText().toString());
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                            zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                        } else if (cashc.isChecked()) {
                                                            double daycash = Double.parseDouble(z01) + Double.parseDouble(due.getText().toString());
                                                            totalcash = Double.parseDouble(z03) + Double.parseDouble(due.getText().toString());
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                            zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                        }

                                                        if (d3 > 0) {
                                                            double discou = Double.parseDouble(z10) + Double.parseDouble(discount.getText().toString());
                                                            //Toast.makeText(Trans.this, (int) Double.parseDouble(z10) + "\\" + Double.parseDouble(discount.getText().toString()), Toast.LENGTH_SHORT).show();
                                                            dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z10").setValue(dc.format(discou));
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });

                                                //need update
                                                //zinfotsv.child("us").setValue("1");

                                                // log
                                                db.getParent().child("Log").child("log")
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                String l = (String) dataSnapshot.getValue();
                                                                String date = l.substring(0, 10);
                                                                Calendar calendar = Calendar.getInstance();
                                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat fm = new SimpleDateFormat("HH:mm");
                                                                if (format.format(calendar.getTime()).equals(date)) {
                                                                    l = l.substring(11);
                                                                    db.getParent().child("Log").child("log")
                                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                                    + fm.format(calendar.getTime())
                                                                                    + "] Transaction. Amount:" + b7 + "\n" + l);
                                                                } else {
                                                                    db.getParent().child("Log").child("log")
                                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                                    + fm.format(calendar.getTime())
                                                                                    + "] Transaction. Amount:" + b7 + "\n\n" + l);
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError error) {
                                                            }
                                                        });
                                                billdb.child("Bill").removeValue();
                                                Toast.makeText(Trans.this, "Bill Saved", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(getApplicationContext(), Bill.class);
                                                i.putExtra("key", k);
                                                i.putExtra("date", mmyyy + "/");
                                                i.putExtra("rep", repkey);
                                                startActivity(i);
                                                finish();
                                                // clear();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            });
                            alert.setNegativeButton(R.string.trans_no, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //billdb.child("Bill").removeValue();
                                    dialog.dismiss();
                                }
                            });
                            alert.show();

                        }else{
                            //startthemonth();
                        }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                            }
                        });

                    } else {
                        Toast.makeText(Trans.this, R.string.trans_nocusdetail, Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(Trans.this, R.string.trans_filldetail, Toast.LENGTH_SHORT).show();
                }

            }
        });

        custbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                    custbtn.setText("REGULAR CUSTOMER");
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    custbtn.setText("NOT REG. CUSTOMER");
                }*/
            }
        });

        //get inventory
        db.child("Items").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                models.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Model model = ds.getValue(Model.class);
                    models.add(model);
                }

                act.setThreshold(1);
                adapter = new AutoAdapter(Trans.this, R.layout.m_trans, R.id.item_name, models);
                act.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //order customer by route
        dbc.child("Person").orderByChild("p13").equalTo(act4.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                modelcustomers.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Modelcustomer model = ds.getValue(Modelcustomer.class);
                    modelcustomers.add(model);
                }

                act2 = (AutoCompleteTextView) findViewById(R.id.actcust);
                act2.setThreshold(1);
                autoCustAdapter = new AutoCustAdapter(Trans.this, R.layout.m_trans, R.id.item_name, modelcustomers);
                act2.setAdapter(autoCustAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get routes
        dbr.child("R").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                modelroute.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Modelroute model = ds.getValue(Modelroute.class);
                    modelroute.add(model);
                }

                act4 = (AutoCompleteTextView) findViewById(R.id.route);
                act4.setThreshold(1);
                autoRouteAdapter = new AutoRouteAdapter(Trans.this, R.layout.m_trans, R.id.item_name, modelroute);
                act4.setAdapter(autoRouteAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //get salesrep
        dbsr.child("Person").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                modelcustomers.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Modelcustomer model = ds.getValue(Modelcustomer.class);
                    modelcustomers.add(model);
                }

                act3 = (AutoCompleteTextView) findViewById(R.id.docText);
                act3.setThreshold(1);
                autoRepAdapter = new AutoRepAdapter(Trans.this, R.layout.m_trans, R.id.item_name, modelcustomers);
                act3.setAdapter(autoRepAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listmodel.clear();
        lv = (ListView) findViewById(R.id.list);
        listadapter = new ListAdapter(Trans.this, R.layout.list_items, listmodel);
        lv.setAdapter(listadapter);
        Utility.setListViewHeightBasedOnChildren(lv);
    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }
        updateLocationUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            loctext.setText(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());

            // giving a blink animation on TextView
            loctext.setAlpha(0);
            loctext.animate().alpha(1).setDuration(300);

            // location last updated time
            //txtUpdatedOn.setText("Last updated on: " + mLastUpdateTime);
        }

        //toggleButtons();
    }

    /*private void toggleButtons() {
        if (mRequestingLocationUpdates) {
            btnStartUpdates.setEnabled(false);
            btnStopUpdates.setEnabled(true);
        } else {
            btnStartUpdates.setEnabled(true);
            btnStopUpdates.setEnabled(false);
        }
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Trans.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(Trans.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        //toggleButtons();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationUpdates();
        }
    }

    public void locationc(View v){
        loctext.setText("");
        //stop location capture
        mRequestingLocationUpdates = false;
        stopLocationUpdates();
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

                    AlertDialog.Builder alert = new AlertDialog.Builder(Trans.this);
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
                                        time = "month";
                                        zinfotsv.child("day").setValue(dayformatday.format(csummary.getTime()));
                                        zinfotsv.child("newprice").setValue("001");
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
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z10").setValue("0.00");

                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("e-expense").child("z01").setValue("Expense");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("e-expense").child("z02").setValue("Expense");
                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("e-expense").child("z03").setValue("0.00");

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
                                        du.child("summary").child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).setValue(summary);
                                        du.child("summary").child("m").child(monthformat.format(csummary.getTime())).setValue(summary);
                                        gen.setText(R.string.trans_generatebill);
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
                            gen.setText("START");
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

                    AlertDialog.Builder alert = new AlertDialog.Builder(Trans.this);
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
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z10").setValue("0.00");

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
                                        du.child("summary").child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).setValue(summary);
                                        gen.setText(R.string.trans_generatebill);
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
                            gen.setText("START");
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
        du.child("Inventory").child("Items").addListenerForSingleValueEvent(
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

    public void creditcb(View v){
        if(cashc.isChecked() || chequec.isChecked()){
            due.setText(String.valueOf(dc.format(damount)));
            due.setEnabled(true);
        }else{
            due.setText("0.00");
            due.setEnabled(false);
        }
    }
    public void chequecb(View v){
        if(cashc.isChecked()){
            due.setText(String.valueOf(dc.format(damount)));
            due.setEnabled(true);
        }else{
            due.setText("0.00");
            due.setEnabled(false);
        }
    }
    public void cashcb(View v){
        due.setEnabled(true);
        due.setText(String.valueOf(dc.format(damount)));
    }

    public void textchange(){
        dbc.child("Person").orderByChild("p13").equalTo(act4.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                modelcustomers.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Modelcustomer model = ds.getValue(Modelcustomer.class);
                    modelcustomers.add(model);
                }

                act2 = (AutoCompleteTextView) findViewById(R.id.actcust);
                act2.setThreshold(1);
                autoCustAdapter = new AutoCustAdapter(Trans.this, R.layout.m_trans, R.id.item_name, modelcustomers);
                act2.setAdapter(autoCustAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void croute(View v){
        if(!act4.getText().toString().isEmpty()){
            act4.setText("");
        }
        act4.setLongClickable(true);
        act4.setClickable(true);
        act4.setFocusableInTouchMode(true);
        act4.setInputType(InputType.TYPE_CLASS_TEXT);
        act4.setEnabled(true);
    }
    public void ccustomer(View v){
        if(!act2.getText().toString().trim().isEmpty()){
            act2.setText("");
        }
        act2.setLongClickable(true);
        act2.setClickable(true);
        act2.setFocusableInTouchMode(true);
        act2.setInputType(InputType.TYPE_CLASS_TEXT);
        act2.setEnabled(true);
    }
    public void cproduct(View v){
        if(!act.getText().toString().trim().isEmpty()){act.setText("");}
    }
    // Functions
    public boolean validate() {
        int flag = 0;
        double balb7;
        double baldue = 0;

        if(totamt.getText().toString().equals("")){
            balb7 = 0.00;
        }else{
            balb7 = Double.parseDouble(totamt.getText().toString());
        }
        if(!due.getText().toString().equals("")) {
            baldue = Double.parseDouble(due.getText().toString().trim());
        }

        if (p13.getText().toString().trim().isEmpty()) {
            flag = 1;
            date.setError(getString(R.string.trans_cantempty));
        } else {
            date.setErrorEnabled(false); }

        if (listmodel.size() == 0) {
            flag = 1;
        }
        if(act4.getText().toString().isEmpty()){
            flag = 1;
            act4.setError(getString(R.string.trans_rcantempty));
        } else if (act2.getText().toString().trim().isEmpty()){
            flag = 1;
            act2.setError(getString(R.string.trans_ccantempty));
        }  else if(act3.getText().toString().isEmpty()){
            flag = 1;
            act3.setError(getString(R.string.trans_srcantempty));
        } else if(due.getText().toString().trim().isEmpty()) {
            flag = 1;
            due.setError(getString(R.string.trans_acantempty));
        } else if(due.getText().toString().trim().isEmpty() || due.getText().toString().trim().equals("0.00") && cashc.isChecked()) {
            flag = 1;
            due.setError("Enter customer paid amount");
        } else if(cashc.isChecked() && chequec.isChecked() && creditc.isChecked()){
            Toast.makeText(Trans.this,R.string.trans_ccorc, Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(cashc.isChecked() && creditc.isChecked() && baldue >= balb7){
            Toast.makeText(Trans.this,"Enter the paid cash, Credit will be added automatically", Toast.LENGTH_LONG).show();
            due.setError("Paid cash can't be equal to or greater than total");
            flag = 1;
        } else if(cashc.isChecked() && creditc.isChecked() && due.getText().toString().trim().equals("0.00") || due.getText().toString().trim().isEmpty()){
            Toast.makeText(Trans.this,"Paid cash can't be empty", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(cashc.isChecked() && chequec.isChecked() && baldue >= balb7){
            Toast.makeText(Trans.this,"Enter the paid cash, Cheque will be added automatically", Toast.LENGTH_LONG).show();
            due.setError("Paid cash can't be equal to or greater than total");
            flag = 1;
        } else if(cashc.isChecked() && chequec.isChecked() && due.getText().toString().trim().equals("0.00") || due.getText().toString().trim().isEmpty()){
            Toast.makeText(Trans.this,"Paid cash can't be empty or 0.00", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(chequec.isChecked() && creditc.isChecked() && baldue >= balb7){
            Toast.makeText(Trans.this,"Enter the paid cheque, Credit will be added automatically", Toast.LENGTH_LONG).show();
            due.setError("Paid cheque can't be equal to or greater than total");
            flag = 1;
        } else if(chequec.isChecked() && creditc.isChecked() && due.getText().toString().trim().equals("0.00") || due.getText().toString().trim().isEmpty()){
            Toast.makeText(Trans.this,"Paid cheque can't be empty or 0.00", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(baldue > balb7) {
            flag = 1;
            due.setError("Paid amount is greater than total");
        } else if(!cashc.isChecked() && !chequec.isChecked() && !creditc.isChecked()){
            Toast.makeText(Trans.this,R.string.trans_ccorc, Toast.LENGTH_LONG).show();
            flag = 1;
        }

        return flag != 1;
    }

    public boolean custValidate() {
        String agpt = act2.getText().toString();
        if(!agpt.contains("-")){
            //StringTokenizer tokens = new StringTokenizer(agpt, "-");
            cusname = act2.getText().toString();//tokens.nextToken();// this will contain "Name"
            //set = false;
        }else {
            StringTokenizer tokens = new StringTokenizer(agpt, "-");
            cusname = tokens.nextToken();// this will contain "Name"
            if(cusaddr == null){
                cusaddr = "";
            }else {
                cusaddr = tokens.nextToken();// this will contain "Address"
            }
        }

        if (!autoCustAdapter.detcust()[0].equals("") && !due.getText().toString().equals("")
                && autoCustAdapter.detcust()[2].equals(cusname)) {
            set = true;
            return true;

        } else if (autoCustAdapter.detcust()[0].equals("") && due.getText().toString().equals("")
                && act2.getText().toString().equals("")) {
            set = false;
            return true;
        } else {
            return false;
        }
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
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                p13.setText(format.format(calendar.getTime()));
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    public void createData() {
        //Firebase Read Listener
        mTasksDatabaseReference.addChildEventListener(new ChildEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Running Foreach loop
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    //Getting Value from 1 to 10 in ArrayList(tasks)
                    tasks.add(Objects.requireNonNull(d.getValue()).toString()/*+" ("+Objects.requireNonNull(d.getKey())+")"*/);
                    personNames.add(Objects.requireNonNull(d.getValue()).toString());
                    Collections.sort(personNames);
                }

                //Putting key & tasks(ArrayList) in HashMap
                itemsMap.put(dataSnapshot.getKey(),tasks);
                //personNames.add(String.valueOf(tasks).replaceAll("[\\[\\]]",""));

                headersList.add(dataSnapshot.getKey());

                tasks=new ArrayList<>();

                android.util.Log.d(TAG, "onChildAdded: dataSnapshot.getChildren: "+dataSnapshot.getChildren());
                android.util.Log.d(TAG, "onChildAdded: KEY"+dataSnapshot.getKey()+" value "+ Objects.requireNonNull(dataSnapshot.getValue()).toString());
            }

            @SuppressLint("NewApi")
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: "+ Objects.requireNonNull(dataSnapshot.getValue()).toString());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //task.remove("" + dataSnapshot.getValue());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void addbtn() {
        if (listmodel.size() < 30) {
            final String item = adapter.det()[0];
            final String itemen = adapter.det()[1];
            final String p8 = adapter.det()[2];
            final String batch = adapter.det()[3];
            final String t = adapter.det()[4];
            final String k = adapter.det()[5];
            final String o = adapter.det()[6];
            final String u = adapter.det()[7];
            final String p6 = adapter.det()[8];
            final String p7 = adapter.det()[9];
            final String p11 = adapter.det()[10];
            final String p4 = adapter.det()[11];
            final String p18 = adapter.det()[12];
            final String p19 = adapter.det()[13];
            final String p20 = adapter.det()[14];
            final String p21 = adapter.det()[15];
            final String match = item + " (" + adapter.det()[5] + ")";

            if (!item.equals("") && !p8.equals("") && match.equals(act.getText().toString())) {

                LayoutInflater layoutInflater = LayoutInflater.from(Trans.this);
                @SuppressLint("InflateParams") View promptView = layoutInflater.inflate(R.layout.input_dia, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Trans.this);
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                editText.setFocusable(true);
                editText.requestFocus();
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInputFromInputMethod(editText.getWindowToken(), 0);
                alertDialogBuilder.setCancelable(true).setPositiveButton(R.string.trans_set, new DialogInterface.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    public void onClick(DialogInterface dialog, int id) {

                        String qt = "";
                        String amount = "";
                        String amountd = "";
                        double amountdo = 0.00;
                        double d3d = 0.00;
                        qt = editText.getText().toString().trim();
                        if (!qt.equals("") && !qt.equals("0")) {

                            if (Integer.parseInt(qt) <= Integer.parseInt(o)) {

                                double d2 = (Double.parseDouble(qt) / Double.parseDouble(u)) * Double.parseDouble(p8);
                                if(p21 != null) {
                                    String[] condition = p21.split("\\|");
                                    String c1 = condition[0];
                                    int c2 = Integer.parseInt(condition[1]);
                                    double c3 = Double.parseDouble(condition[2]);

                                    if(c1.equals(">=")) {
                                        if(Integer.parseInt(qt) >= c2) {
                                            d3d = Integer.parseInt(qt) * c3;
                                            amountdo = Double.parseDouble(dc.format(d2)) - d3d;
                                            amountd = String.valueOf(dc.format(amountdo));
                                            d2 = Double.parseDouble(dc.format(d2));
                                            amount = String.valueOf(dc.format(d2));
                                        }else{
                                            d3d = 0.00;
                                            amountd = String.valueOf(dc.format(d2));
                                            d2 = Double.parseDouble(dc.format(d2));
                                            amount = String.valueOf(dc.format(d2));
                                        }
                                    }else{
                                        d3d = 0.00;
                                        amountd = String.valueOf(dc.format(d2));
                                        d2 = Double.parseDouble(dc.format(d2));
                                        amount = String.valueOf(dc.format(d2));
                                    }

                                }else{
                                    d3d = 0.00;
                                    amountd = String.valueOf(dc.format(d2));
                                    d2 = Double.parseDouble(dc.format(d2));
                                    amount = String.valueOf(dc.format(d2));
                                }

                                String current = Locale.getDefault().getDisplayLanguage();
                                if (current.toLowerCase().contains("en")) {
                                    ListModel list = new ListModel(item, itemen, p8, qt, amount, batch, t, k, o, u, p6, p7, p11, p4, p18, p19, p21, String.valueOf(d3d));

                                    if(SList.getText().toString().contains(k+"-")){
                                        Toast.makeText(Trans.this, R.string.trans_alreadyin, Toast.LENGTH_SHORT).show();
                                    }else{
                                        totamount = totamount + Double.parseDouble(amount);
                                        totamount = Double.parseDouble(dc.format(totamount));
                                        damount = damount + Double.parseDouble(amountd);
                                        damount = Double.parseDouble(dc.format(damount));
                                        d3 = d3 + d3d;
                                        d3 = Double.parseDouble(dc.format(d3));
                                        //SList[0] = SList[0] + "," +k;
                                        SList.setText(SList.getText().toString()+k+"-");
                                        listmodel.add(list);
                                    }
                                }else{
                                    ListModel list = new ListModel(itemen, item, p8, qt, amount, batch, t, k, o, u, p6, p7, p11, p4, p18, p19, p21, String.valueOf(d3d));

                                    if(SList.getText().toString().contains(k+"-")){ //SList[0].contains(k)
                                        Toast.makeText(Trans.this, R.string.trans_alreadyin, Toast.LENGTH_SHORT).show();
                                    }else{
                                        totamount = totamount + Double.parseDouble(amount);
                                        totamount = Double.parseDouble(dc.format(totamount));
                                        damount = damount + Double.parseDouble(amountd);
                                        damount = Double.parseDouble(dc.format(damount));
                                        d3 = d3 + d3d;
                                        d3 = Double.parseDouble(dc.format(d3));
                                        //SList[0] = SList[0] + "," +k;
                                        SList.setText(SList.getText().toString()+k+"-");
                                        listmodel.add(list);
                                    }
                                }

                                listadapter.notifyDataSetChanged();
                                act.setText("");
                                totamt.setText(String.valueOf(dc.format(totamount)));
                                due.setText(String.valueOf(dc.format(damount)));
                                discount.setText(String.valueOf(dc.format(d3)));
                                //gen.setText(R.string.trans_generatebill);

                                Utility.setListViewHeightBasedOnChildren(lv);

                                dialog.dismiss();

                            } else {

                                Toast.makeText(Trans.this, R.string.trans_qtynotinstock, Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(Trans.this, R.string.trans_validqty, Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton(R.string.trans_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                Objects.requireNonNull(alert.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                alert.show();
            } else {
                Toast.makeText(Trans.this, R.string.trans_itemnotmatch, Toast.LENGTH_SHORT).show();
            }
        }

        else {
            Toast.makeText(Trans.this, R.string.trans_nofurtheritem, Toast.LENGTH_SHORT).show();
        }
    }


    public void clear() {
        p13.setText("");
        ept.setText("");
        eage.setText("");
        totamt.setText("");
        totamount = 0.00;
        damount = 0.00;
        listmodel.clear();
        act2.setText("");
        due.setText("");
        listadapter.notifyDataSetChanged();
    }

    public void updateQty(final String key, final String med, final String p3, String qty, final String orgqty, String p6, String p7, String exp, String bat, final String pr, String unit, String p11, String p4, String p18, String p19, String p20, final String p21) {
        int oqt = Integer.parseInt(orgqty);
        final int qt = Integer.parseInt(qty);
        oqt = oqt - qt;

        //Toast.makeText(Trans.this, "Key "+key, Toast.LENGTH_SHORT).show();

        final double cal = ((Double.parseDouble(p6) / 100) * Double.parseDouble(p7)) + Double.parseDouble(p6);
        //Toast.makeText(Trans.this, "cal "+cal, Toast.LENGTH_SHORT).show();
        final double cal1 = cal * oqt;
        //Toast.makeText(Trans.this, "cal1 "+cal1, Toast.LENGTH_SHORT).show();

        final String paddw = "[{\"name\":\"p01\",\"value\":\""+key+"\"}," +
                "{\"name\":\"p02\",\"value\":\""+bat+"\"}," +
                "{\"name\":\"p03\",\"value\":\""+med+"\"}," +
                "{\"name\":\"p04\",\"value\":\""+p4+"\"}," +
                "{\"name\":\"p05\",\"value\":\""+exp+"\"}," +
                "{\"name\":\"p06\",\"value\":\""+p6+"\"}," +
                "{\"name\":\"p07\",\"value\":\""+p7+"\"}," +
                "{\"name\":\"p08\",\"value\":\""+pr+"\"}," +
                "{\"name\":\"p09\",\"value\":\""+unit+"\"}," +
                "{\"name\":\"p10\",\"value\":\""+ oqt +"\"}," +
                "{\"name\":\"p11\",\"value\":\""+p11+"\"}," +
                "{\"name\":\"p17\",\"value\":\""+dc.format(cal1)+"\"}," +
                "{\"name\":\"p21\",\"value\":\""+p21+"\"}]";//Discount rule in inventory 21

        // add to products
        dup.getParent().child("products").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dup.getParent().child("products").child(key)
                        .setValue(paddw);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        db.child("Items").child(key).child("p10").setValue(String.valueOf(oqt));
        db.child("Items").child(key).child("p17").setValue(String.valueOf(cal1));

        final int finalOqt = oqt;

        csummary = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformatday = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformatm = new SimpleDateFormat("MM/yyyy");

        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sales = (String) dataSnapshot.child("z02").getValue();
                String salesv = (String) dataSnapshot.child("z03").getValue();

                if (dataSnapshot.hasChild("z02")) {

                    int newqty = Integer.parseInt(sales) + qt;
                    double soldvalue = Double.parseDouble(salesv) + (qt * Integer.parseInt(pr));

                    if(cashc.isChecked()){
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z02").setValue(String.valueOf(newqty));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z00").setValue(String.valueOf(pr));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z01").setValue(String.valueOf(med));
                        //dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z09").setValue(String.valueOf(p3));
                    if(cashc.isChecked()) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z02").setValue(String.valueOf(qt));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //day sales
        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sales = (String) dataSnapshot.child("z02").getValue();
                String salesv = (String) dataSnapshot.child("z03").getValue();

                if (dataSnapshot.hasChild("z02")) {

                    int newqty = Integer.parseInt(sales) + qt;
                    double soldvalue = Double.parseDouble(salesv) + (qt * Integer.parseInt(pr));

                    if(cashc.isChecked()){
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z02").setValue(String.valueOf(newqty));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z00").setValue(String.valueOf(pr));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z01").setValue(String.valueOf(med));
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z09").setValue(String.valueOf(p3));
                    if(cashc.isChecked()) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z02").setValue(String.valueOf(qt));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //product discount cash - month
        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salesd = (String) dataSnapshot.child("z03d").getValue();

                if (dataSnapshot.hasChild("z03d")) {
                    if(cashc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        double solddis = Double.parseDouble(salesd) + Double.parseDouble(p21);
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03d").setValue(String.valueOf(dc.format(solddis)));
                    }
                }else{
                    if(cashc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03d").setValue(String.valueOf(p21));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //product discount cash - day
        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salesd = (String) dataSnapshot.child("z03d").getValue();

                if (dataSnapshot.hasChild("z03d")) {
                    if(cashc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        double solddis = Double.parseDouble(salesd) + Double.parseDouble(p21);
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03d").setValue(String.valueOf(dc.format(solddis)));
                    }
                }else{
                    if(cashc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03d").setValue(String.valueOf(p21));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //month sales credit
        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salesc = (String) dataSnapshot.child("z02c").getValue();
                String salesvc = (String) dataSnapshot.child("z03c").getValue();

                if (dataSnapshot.hasChild("z02c")) {

                    int newqtyc = Integer.parseInt(salesc) + qt;
                    double soldvaluec = Double.parseDouble(salesvc) + (qt * Integer.parseInt(pr));

                    if(creditc.isChecked()) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z02c").setValue(String.valueOf(newqtyc));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03c").setValue(String.valueOf(dc.format(soldvaluec)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                    if(creditc.isChecked()) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z02c").setValue(String.valueOf(qt));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03c").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //day sales credit
        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salesc = (String) dataSnapshot.child("z02c").getValue();
                String salesvc = (String) dataSnapshot.child("z03c").getValue();

                if (dataSnapshot.hasChild("z02c")) {

                    int newqtyc = Integer.parseInt(salesc) + qt;
                    double soldvaluec = Double.parseDouble(salesvc) + (qt * Integer.parseInt(pr));

                    if(creditc.isChecked()) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z02c").setValue(String.valueOf(newqtyc));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03c").setValue(String.valueOf(dc.format(soldvaluec)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                    if(creditc.isChecked()) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z02c").setValue(String.valueOf(qt));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03c").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //product discount credit - month
        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salescd = (String) dataSnapshot.child("z03cd").getValue();

                if (dataSnapshot.hasChild("z03cd")) {
                    if(creditc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        double soldcdis = Double.parseDouble(salescd) + Double.parseDouble(p21);
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03cd").setValue(String.valueOf(dc.format(soldcdis)));
                    }
                }else{
                    if(creditc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z03cd").setValue(String.valueOf(p21));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //product discount credit - day
        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salescd = (String) dataSnapshot.child("z03cd").getValue();

                if (dataSnapshot.hasChild("z03cd")) {
                    if(creditc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        double soldcdis = Double.parseDouble(salescd) + Double.parseDouble(p21);
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03cd").setValue(String.valueOf(dc.format(soldcdis)));
                    }
                }else{
                    if(creditc.isChecked() && p21 != null && !p21.equals("0.0")) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z03cd").setValue(String.valueOf(p21));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //month stock
        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("z06")) {

                    dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z07").setValue(String.valueOf(finalOqt));
                    dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z08").setValue(String.valueOf(dc.format(cal1)));

                }else{

                    dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z06").setValue(orgqty);
                    dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z07").setValue(String.valueOf(finalOqt));
                    dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z08").setValue(String.valueOf(dc.format(cal1)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //day stock
        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("z06")) {

                    dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z07").setValue(String.valueOf(finalOqt));
                    dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z08").setValue(String.valueOf(dc.format(cal1)));

                }else{

                    dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z06").setValue(orgqty);
                    dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z07").setValue(String.valueOf(finalOqt));
                    dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z08").setValue(String.valueOf(dc.format(cal1)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        adapter.notifyDataSetChanged();
    }

    public void updateCust(final String key, String amt, String orgamt, String name, String add, String p13, String p12) {

        double org = Double.valueOf(orgamt);
        double am = Double.valueOf(amt);
        double samt = totamount - am - d3;
        org = org + samt;
        final double tot = totamount;
        final double balance = org;
        final double paid = am;

        dbc.child("Person").child(key).child("p14").setValue(String.valueOf(dc.format(org)));
        dbc.child("Person").child(key).child("log").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue();

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                dbc.child("Person").child(key).child("log").setValue(value + "[" + format.format(calendar.getTime())
                        + "] Transaction. Total: " + tot + " Paid: " + paid + " Balance: " + balance + "\n");
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        final String paddc = "[{\"name\":\"p03\",\"value\":\""+name+"\"}," +
                "{\"name\":\"p12\",\"value\":\""+p12+"\"}," +
                "{\"name\":\"p13\",\"value\":\""+p13+"\"}," +
                "{\"name\":\"p15\",\"value\":\""+add+"\"}," +
                "{\"name\":\"p14\",\"value\":\""+dc.format(org)+"\"}," +
                "{\"name\":\"key\",\"value\":\""+key+"\"}," +
                "{\"name\":\"log\",\"value\":\""+"[" + format.format(calendar.getTime()) + "] " + "Account "+name+" updated. Balance:" + org+"\"}]";

        // set customer balance
        dup = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("customers");
        dup.getParent().child("customers").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dup.getParent().child("customers").child(key)
                        .setValue(paddc);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

    }

    public double roundOff(double d) {
        d = Math.round(d * 100.00);
        d = d / 100.00;
        return d;
    }
}
