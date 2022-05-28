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
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class Returns extends AppCompatActivity implements View.OnClickListener {

    View v;
    String uid, cusname, cusaddr, pay, rda1, rtc1, rta1, rmo1, rmc1, padd, repname, repmob, item, p3, repkey, current, startcash, starttsv, startcheque, startcredit;
    CheckBox cashc, chequec, creditc;
    int dutString, keypush;
    double sum2, b8;
    private DatabaseReference db, dbi, dbc, dbs, dbsr, dbr, dbsum, dbre, dupush, dup, dbzsum, zinfotsv, du, billdb;
    private FirebaseAuth mAuth;
    AutoCompleteTextView act, act2, act3, act4;
    AutoAdapter adapter;
    AutoCustAdapter autoCustAdapter;
    AutoRepAdapter autoRepAdapter;
    AutoRouteAdapter autoRouteAdapter;
    ListAdapter listadapter;
    ArrayList<Model> models = new ArrayList<>();
    TextInputLayout date, pt, b3;
    EditText p13;
    EditText ept;
    EditText eage;
    @SuppressLint("StaticFieldLeak")
    static EditText due;
    private int mYear, mMonth, mDay;
    ArrayList<ListModel> listmodel = new ArrayList<>();
    ArrayList<Modelcustomer> modelcustomers = new ArrayList<>();
    ArrayList<Modelroute> modelroute = new ArrayList<>();
    ListView lv;
    Button btn, custbtn;
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
    LinearLayout actLayout;
    String time = "";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.m_returns);
        setTitle(getString(R.string.nav_drawer_returns));

        dc = new DecimalFormat(".00");

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

        act2 = (AutoCompleteTextView) findViewById(R.id.actcust);
        act2.setFocusable(true);
        act2.requestFocus();
        ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInputFromInputMethod(act2.getWindowToken(), 0);
        act2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(act2.getWindowToken(), 0);
            }
        });
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

        act4.setInputType(InputType.TYPE_NULL);
        act4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                textchange();
                act4.setFocusable(false);
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(act4.getWindowToken(), 0);
            }
        });

        //due = (EditText) findViewById(R.id.amtdue);
        //totamt = (TextView) findViewById(R.id.totamt);

        p13.setInputType(InputType.TYPE_NULL);
        p13.setFocusable(false);
        p13.setOnClickListener(this);
        act = (AutoCompleteTextView) findViewById(R.id.act);
        act.setInputType(InputType.TYPE_NULL);
        final Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat fmmonth = new SimpleDateFormat("MMyyyy");
        p13.setText(format.format(c.getTime()));

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

        due = (EditText) findViewById(R.id.amtdue);
        totamt = (TextView) findViewById(R.id.totamt);
        discount = (TextView) findViewById(R.id.discount);

        mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Inventory");
        dbi = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Return");
        dbre = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("returns");
        dbc = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer");
        dbsr = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("SalesRep");
        dbs = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Summary");
        dbsum = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("summary");
        dbr = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Route");
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        dupush = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Return");
        zinfotsv = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
        billdb = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        btn = (Button) findViewById(R.id.btnadd);
        gen = (Button) findViewById(R.id.genbtn);
        custbtn = (Button) findViewById(R.id.regcust);
        linearLayout = (LinearLayout) findViewById(R.id.cust);

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
                    Toast.makeText(Returns.this,"SalesRep not paired", Toast.LENGTH_LONG).show();
                    /*Intent k = new Intent(Trans.this, MainActivity.class);
                    startActivity(k);*/
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
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
                /*Toast.makeText(Returns.this,
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

                //Toast.makeText(Returns.this, headersList.get(groupPosition), Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(Returns.this, headersList.get(groupPosition), Toast.LENGTH_SHORT).show();
            }
        });
        //handling the header items click
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Toast.makeText(Returns.this, headersList.get(groupPosition) + "--" + itemsMap.get(headersList.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                String finals = itemsMap.get(headersList.get(groupPosition)).get(childPosition);

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
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                addbtn();
                actLayout.setVisibility(View.INVISIBLE);
                //Toast.makeText(Returns.this," selected", Toast.LENGTH_LONG).show();
            }
        });

        // get count
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

        gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gen.getText().equals("START") && !act3.getText().toString().isEmpty()){

                    startthemonth();
                }

                if (validate()) {
                    if (custValidate()) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(Returns.this);
                        alert.setTitle(R.string.trans_confirm);

                        double totalpay = Double.parseDouble(totamt.getText().toString().trim()) - Double.parseDouble(due.getText().toString().trim());

                        if(cashc.isChecked() && chequec.isChecked()){
                            pay = "Cash & Cheque";
                            alert.setMessage("RETURN BILL"+"\n"+"\n"+"Total: "+totamt.getText().toString()+"\n"+"Cash: "+due.getText().toString()+"\n"
                                    +"Cheque: "+totalpay+"\n"+"Payment Method: "+pay);
                        } else if(cashc.isChecked() && creditc.isChecked()){
                            pay = "Cash & Credit";
                            alert.setMessage("RETURN BILL"+"\n"+"\n"+"Total: "+totamt.getText().toString()+"\n"+"Cash: "+due.getText().toString()+"\n"
                                    +"Credit: "+totalpay+"\n"+"Payment Method: "+pay);
                        } else if(chequec.isChecked() && creditc.isChecked()){
                            pay = "Cheque & Credit";
                            alert.setMessage("RETURN BILL"+"\n"+"\n"+"Total: "+totamt.getText().toString()+"\n"+"Cheque: "+due.getText().toString()+"\n"
                                    +"Credit: "+totalpay+"\n"+"Payment Method: "+pay);
                        } else if(chequec.isChecked()){
                            pay = "Cheque";
                            alert.setMessage("RETURN BILL"+"\n"+"\n"+"Total: "+totamt.getText().toString()+"\n"+"Cash: "+due.getText().toString()+
                                    "\n"+"Cheque: "+totamt.getText().toString()+"\n"+"Payment Method: "+pay);
                        } else if(creditc.isChecked()){
                            pay = "Credit";
                            alert.setMessage("RETURN BILL"+"\n"+"\n"+"Total: "+totamt.getText().toString()+"\n"+"Cash: "+due.getText().toString()+
                                    "\n"+"Credit: "+totamt.getText().toString()+"\n"+"Payment Method: "+pay);
                        }else if(cashc.isChecked()){
                            pay = "Cash";

                            if(Double.parseDouble(totamt.getText().toString().trim()) > Double.parseDouble(due.getText().toString().trim()) + Double.parseDouble(String.valueOf(damount))){
                                alert.setMessage("RETURN BILL"+"\n"+"\n"+"Total: "+totamt.getText().toString()+"\n"+"Cash: "+due.getText().toString()+
                                        "\n"+"Credit: "+totalpay+"\n"+"Payment Method: "+pay);
                            }else{
                                alert.setMessage("RETURN BILL"+"\n"+"\n"+"Total: "+totamt.getText().toString()+"\n"+"Cash: "+due.getText().toString()+
                                        "\n"+"Payment Method: "+pay);
                            }
                        }
                        final String payment = pay;

                        alert.setPositiveButton(R.string.trans_yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final int size = listmodel.size();
                                final String b4 = p13.getText().toString().trim();
                                final String b5 = act3.getText().toString().trim();
                                final String b2 = cusname;
                                final String gage = autoCustAdapter.detcust()[3];

                                int index= b5.lastIndexOf(" ");
                                final String reponly = b5.substring(0,index);
                                repkey = b5.substring(index+1);

                                billdb.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild("Billr")) {
                                            Toast.makeText(Returns.this, "Another return bill in process", Toast.LENGTH_SHORT).show();
                                        }else{
                                            billdb.child("Billr").child("Rep").setValue(repkey);

                                            // Updating customerdetail
                                            if (set) {
                                                updateCust(autoCustAdapter.detcust()[0], due.getText().toString(), autoCustAdapter.detcust()[1],
                                                        autoCustAdapter.detcust()[2], autoCustAdapter.detcust()[3], autoCustAdapter.detcust()[4],
                                                        autoCustAdapter.detcust()[5]);
                                            }

                                            if(!b5.contains("-")){
                                                StringTokenizer tokens = new StringTokenizer(b5, "-");
                                                repname = tokens.nextToken();// this will contain "Name"
                                                repmob = "";
                                                set = false;
                                            }else {
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
                                                double p61 = Double.parseDouble(listmodel.get(i).getP06());
                                                double p71 = Double.parseDouble(listmodel.get(i).getP07());
                                                double p67 = (p61 / 100 * p71) + p61;

                                                String getp21 = listmodel.get(i).getP21();
                                                String getp21w = "0.00";
                                                if(getp21.equals("") || getp21.isEmpty() || Double.parseDouble(getp21) < 1){
                                                    getp21 = "0.00";
                                                }else{
                                                    getp21 = listmodel.get(i).getP21();
                                                    getp21w = "<br>" + "Discount: " + listmodel.get(i).getP21();
                                                }

                                                itemlist[i] = listmodel.get(i).getMed() + "©" + listmodel.get(i).getUnit() + "©"
                                                        /*+ listmodel.get(i).getBatch() + "©"*/ + listmodel.get(i).getQty() + "©"
                                                        + p67 + "©" /*+ listmodel.get(i).getExpdate() + "©"*/
                                                        + listmodel.get(i).getAmount() + "©" + getp21;
                                                //itemlistb6t[i] = listmodel.get(i).getP03() + "©" + listmodel.get(i).getUnit() + "©"
                                                        ///*+ listmodel.get(i).getBatch() + "©"*/ + listmodel.get(i).getQty() + "©"
                                                        //+ p67 + "©" /*+ listmodel.get(i).getExpdate() + "©"*/
                                                        //+ listmodel.get(i).getAmount() + "©" + getp21;

                                                if (items.equals("")) {
                                                    items = items + itemlist[i];
                                                    //itemsb6t = itemsb6t + itemlistb6t[i];
                                                    itemswebs = itemswebs + listmodel.get(i).getMed() + " - " + listmodel.get(i).getUnit() + " x "
                                                            + listmodel.get(i).getQty() + " x " + p67 + " = "
                                                            + listmodel.get(i).getAmount() + getp21w;
                                                    /*itemswebsb6t = itemswebsb6t + listmodel.get(i).getP03() + " - " + listmodel.get(i).getUnit() + " x "
                                                            + listmodel.get(i).getQty() + " x " + p67 + " = "
                                                            + listmodel.get(i).getAmount() + getp21w;*/
                                                }else {
                                                    items = items + "®" + itemlist[i];
                                                    //itemsb6t = itemsb6t + "®" + itemlistb6t[i];
                                                    itemswebs = itemswebs + "<br>" + listmodel.get(i).getMed() + " - " + listmodel.get(i).getUnit() + " x "
                                                            + listmodel.get(i).getQty() + " x " + p67 + " = "
                                                            + listmodel.get(i).getAmount() + getp21w;
                                                    /*itemswebsb6t = itemswebsb6t + "<br>" + listmodel.get(i).getP03() + " - " + listmodel.get(i).getUnit() + " x "
                                                            + listmodel.get(i).getQty() + " x " + p67 + " = "
                                                            + listmodel.get(i).getAmount() + getp21w;*/
                                                }

                                                itemcnt += Integer.parseInt(listmodel.get(i).getQty());

                                                // updating itemquantity
                                                updateQty(listmodel.get(i).getKey(),listmodel.get(i).getMed(), listmodel.get(i).getP03(), listmodel.get(i).getQty(), listmodel.get(i).getOrgqty(),
                                                        listmodel.get(i).getP06(),listmodel.get(i).getP07(), listmodel.get(i).getExpdate(), listmodel.get(i).getBatch(),
                                                        listmodel.get(i).getPr(), listmodel.get(i).getUnit(), listmodel.get(i).getP11(), listmodel.get(i).getP04(), listmodel.get(i).getP18(),
                                                        listmodel.get(i).getP19(), listmodel.get(i).getP20(), listmodel.get(i).getP21());

                                                //Toast.makeText(Returns.this, itemswebs, Toast.LENGTH_SHORT).show();
                                                //double sum = p61 / 100 * p71;
                                                //double sum1 = p61 + sum;
                                                sum2 = Double.parseDouble(listmodel.get(i).getQty()) * p67;
                                                sumlist[i] = Double.parseDouble(String.valueOf(sum2));

                                                sums = sums + sumlist[i];
                                                //Toast.makeText(Returns.this, String.valueOf(sums), Toast.LENGTH_SHORT).show();

                                                discounts[i] = Double.parseDouble(String.valueOf(listmodel.get(i).getP21()));
                                                disc = disc + discounts[i];
                                            }

                                            final String b7 = totamt.getText().toString();
                                            keypush = keypush + 1;
                                            @SuppressLint("DefaultLocale") String BillNo = String.format("%06d", keypush);

                                            csummary = Calendar.getInstance();
                                            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformatday = new SimpleDateFormat("dd/MM/yyyy");
                                            @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");
                                            @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformatm = new SimpleDateFormat("MM/yyyy");
                                            @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
                                            final String mmyyy = monthformat.format(csummary.getTime()).toString().replace("/","");

                                            final String k = dbi.child(mmyyy).child("R").push().getKey();
                                            // save to firebase for app
                                            dbi.child(mmyyy).child("R").child(k).child("b01").setValue(repkey+"-"+BillNo);
                                            dbi.child(mmyyy).child("R").child(k).child("b04").setValue(b4);
                                            dbi.child(mmyyy).child("R").child(k).child("b05").setValue(reponly);
                                            dbi.child(mmyyy).child("R").child(k).child("b02").setValue(b2+"-"+gage);
                                            dbi.child(mmyyy).child("R").child(k).child("b06").setValue(items);
                                            //dbi.child(mmyyy).child("R").child(k).child("b06t").setValue(itemsb6t);
                                            dbi.child(mmyyy).child("R").child(k).child("b07").setValue(b7);

                                            int jk = due.getText().toString().lastIndexOf('.');
                                            if(jk != -1 && due.getText().toString().substring(jk + 1).length() == 2) {
                                                dbi.child(mmyyy).child("R").child(k).child("b08").setValue(due.getText().toString());
                                                b8 = Double.parseDouble(due.getText().toString());
                                            }else if(jk != -1 && due.getText().toString().substring(jk + 1).length() == 1) {
                                                dbi.child(mmyyy).child("R").child(k).child("b08").setValue(due.getText().toString()+"0");
                                                b8 = Double.parseDouble(due.getText().toString()+"0");
                                            }else if(jk != -1 && due.getText().toString().substring(jk + 1).length() == 0){
                                                dbi.child(mmyyy).child("R").child(k).child("b08").setValue(due.getText().toString()+"00");
                                                b8 = Double.parseDouble(due.getText().toString()+"00");
                                            }else{
                                                dbi.child(mmyyy).child("R").child(k).child("b08").setValue(due.getText().toString()+".00");
                                                b8 = Double.parseDouble(due.getText().toString()+".00");
                                            }

                                            double bal = Double.parseDouble(b7) - (Double.parseDouble(due.getText().toString()) + Double.parseDouble(String.valueOf(d3)));
                                            if(bal == 0){
                                                dbi.child(mmyyy).child("R").child(k).child("b09").setValue("0"+dc.format(bal));
                                                bal = Double.parseDouble("0"+dc.format(bal));
                                            }else{
                                                dbi.child(mmyyy).child("R").child(k).child("b09").setValue(dc.format(bal));
                                                bal = Double.parseDouble(dc.format(bal));
                                            }
                                            if(d3 > 0) {
                                                dbi.child(mmyyy).child("R").child(k).child("b11").setValue(String.valueOf(dc.format(d3)));
                                            }

                                            Calendar calendar = Calendar.getInstance();
                                            @SuppressLint("SimpleDateFormat") SimpleDateFormat fmbill = new SimpleDateFormat("HH:mm:ss");

                                            dbi.child(mmyyy).child("R").child(k).child("b10").setValue(fmbill.format(calendar.getTime()));
                                            dbi.child(mmyyy).child("R").child(k).child("key").setValue(k);
                                            dbi.child(mmyyy).child("R").child(k).child("pay").setValue(payment);

                                            //for web
                                            if(d3 > 0) {
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
                                                        "{\"name\":\"key\",\"value\":\"" + k + "\"}," +
                                                        "{\"name\":\"pay\",\"value\":\"" + payment + "\"}]";
                                            }else{
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
                                                        "{\"name\":\"key\",\"value\":\"" + k + "\"}," +
                                                        "{\"name\":\"pay\",\"value\":\"" + payment + "\"}]";
                                            }

                                            // add to returns
                                            dbre.getParent().child("returns").child(mmyyy).child(k).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    dbre.getParent().child("returns").child(mmyyy).child(k)
                                                            .setValue(padd);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                }
                                            });

                                            // set count
                                            dupush.getParent().child("Return").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    dupush.getParent().child("Return").child("count")
                                                            .setValue(String.valueOf(++dutString));
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                }
                                            });
                                            // set id
                                            dupush.getParent().child("Return").child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    dupush.getParent().child("Return").child("id")
                                                            .setValue(String.valueOf(keypush));
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                }
                                            });

                                            final int icount = itemcnt;
                                            final double amt = totamount;

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

                                                    if (dataSnapshot.hasChild("z00")) {

                                                        String date = (String) dataSnapshot.child(dayformatday.format(csummary.getTime())).getValue();
                                                        //String month = (String) dataSnapshot.child("s03").getValue();
                                                        int tcount = Integer
                                                                .parseInt((String) dataSnapshot.child("z07").getValue()) + 1;
                                                        int count = Integer
                                                                .parseInt((String) dataSnapshot.child("z06").getValue());
                                                        int tprocount = Integer
                                                                .parseInt((String) dataSnapshot.child("z03").getValue());
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

                                                        value = Double.parseDouble(dc.format(value));
                                                        grvalue = Double.parseDouble(dc.format(grvalue));
                                                        dreturn = Double.parseDouble(dc.format(dreturn));
                                                        tsv = Double.parseDouble(dc.format(tsv));
                                                        endtsv = Double.parseDouble(dc.format(endtsv));

                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z07").setValue(String.valueOf(tcount));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z06").setValue(String.valueOf(count + icount));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z05").setValue(String.valueOf(dc.format(dreturn + amt)));

                                                        //double tiv2 = ((tsv + sums) - value) + dreturn + grvalue;
                                                        double tiv2 = endtsv + sums;

                                                        //Toast.makeText(Returns.this, "S1 - "+ roundOff(tiv2), Toast.LENGTH_SHORT).show();
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("00").child("z09").setValue(String.valueOf(dc.format(tiv2)));
                                                        zinfotsv.child("tsv").setValue(String.valueOf(dc.format(tiv2)));
                                                        //dbs.child("d").child(dayformat.format(csummary.getTime())).child("s15").setValue(tcount + "");

                                                        String sum = "[{\"name\":\"z00\",\"value\":\"" + dayformatday.format(csummary.getTime()) + "\"}," +
                                                                "{\"name\":\"z01\",\"value\":\"" + tsv + "\"}," +
                                                                "{\"name\":\"z02\",\"value\":\""+ z02 +"\"}," +
                                                                "{\"name\":\"z03\",\"value\":\""+ z03 +"\"}," +
                                                                "{\"name\":\"z04\",\"value\":\""+ z04 +"\"}," +
                                                                "{\"name\":\"z05\",\"value\":\""+ dc.format(dreturn + amt) + "" +"\"}," +
                                                                "{\"name\":\"z06\",\"value\":\""+ (count + icount) +"\"}," +
                                                                "{\"name\":\"z07\",\"value\":\""+ tcount +"\"}," +
                                                                "{\"name\":\"z08\",\"value\":\""+ z08 +"\"}," +
                                                                "{\"name\":\"z09\",\"value\":\""+ dc.format(tiv2) +"\"}]";
                                                        dbsum.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).setValue(sum);

                                                    } else {
                                                        Toast.makeText(Returns.this, "Start the day", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });

                                            //month stock
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

                                                    if (dataSnapshot.hasChild("z00")) {

                                                        String date = (String) dataSnapshot.child(monthformat.format(csummary.getTime())).getValue();
                                                        //String month = (String) dataSnapshot.child("s03").getValue();
                                                        int tcount = Integer
                                                                .parseInt((String) dataSnapshot.child("z07").getValue()) + 1;
                                                        int count = Integer
                                                                .parseInt((String) dataSnapshot.child("z06").getValue());
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

                                                        value = Double.parseDouble(dc.format(value));
                                                        grvalue = Double.parseDouble(dc.format(grvalue));
                                                        dreturn = Double.parseDouble(dc.format(dreturn));
                                                        tsv = Double.parseDouble(dc.format(tsv));
                                                        endtsv = Double.parseDouble(dc.format(endtsv));

                                                        //double tiv2 = ((tsv + sums) - value) + dreturn + grvalue;
                                                        double tiv2 = endtsv + sums;

                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z07").setValue(String.valueOf(tcount));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z06").setValue(String.valueOf(count + icount));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z05").setValue(dc.format(dreturn + amt) + "");

                                                        //Toast.makeText(Returns.this, "S1 - "+ roundOff(tiv2), Toast.LENGTH_SHORT).show();
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("00").child("z09").setValue(String.valueOf(dc.format(tiv2)));
                                                        //dbs.child("d").child(dayformat.format(csummary.getTime())).child("s15").setValue(tcount + "");

                                                        String sum = "[{\"name\":\"z00\",\"value\":\"" + monthformatm.format(csummary.getTime()) + "\"}," +
                                                                "{\"name\":\"z01\",\"value\":\"" + tsv + "\"}," +
                                                                "{\"name\":\"z02\",\"value\":\""+ z02 +"\"}," +
                                                                "{\"name\":\"z03\",\"value\":\""+ z03 +"\"}," +
                                                                "{\"name\":\"z04\",\"value\":\""+ z04 +"\"}," +
                                                                "{\"name\":\"z05\",\"value\":\""+ dc.format(dreturn + amt) + "" +"\"}," +
                                                                "{\"name\":\"z06\",\"value\":\""+ (count + icount) + "" +"\"}," +
                                                                "{\"name\":\"z07\",\"value\":\""+ tcount +"\"}," +
                                                                "{\"name\":\"z08\",\"value\":\""+ z08 +"\"}," +
                                                                //"{\"name\":\"z081\",\"value\":\""+ z081 +"\"}," +
                                                                "{\"name\":\"z09\",\"value\":\""+ dc.format(tiv2) +"\"}]";
                                                        dbsum.child("m").child(monthformat.format(csummary.getTime())).setValue(sum);

                                                    } else {
                                                        Toast.makeText(Returns.this, "Start the month", Toast.LENGTH_SHORT).show();
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

                                                    if(cashc.isChecked() && chequec.isChecked()){
                                                        totalcash = Double.parseDouble(z03) - Double.parseDouble(due.getText().toString());
                                                        double totalcheque = Double.parseDouble(z06) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                        if(totalcheque == 0){
                                                            totalcheque = Double.parseDouble("0"+totalcheque);
                                                        }
                                                        if(totalcash == 0){
                                                            totalcash = Double.parseDouble("0"+totalcash);
                                                        }
                                                        double daycash = Double.parseDouble(z01) - Double.parseDouble(due.getText().toString());
                                                        double daycheque = Double.parseDouble(z05) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                        //cheque
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));

                                                    } else if(cashc.isChecked() && creditc.isChecked()){
                                                        //cash
                                                        totalcash = Double.parseDouble(z03) - Double.parseDouble(due.getText().toString());
                                                        double totalcredit = Double.parseDouble(z09) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                        if(totalcredit == 0){
                                                            totalcredit = Double.parseDouble("0"+totalcredit);
                                                        }
                                                        if(totalcash == 0){
                                                            totalcash = Double.parseDouble("0"+totalcash);
                                                        }
                                                        double daycash = Double.parseDouble(z01) - Double.parseDouble(due.getText().toString());
                                                        double daycredit = Double.parseDouble(z08) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                        //zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                        //cheque
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                        //zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                    } else if(chequec.isChecked() && creditc.isChecked()){
                                                        //cash
                                                        double totalcheque = Double.parseDouble(z06) - Double.parseDouble(due.getText().toString());
                                                        double totalcredit = Double.parseDouble(z09) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                        if(totalcredit == 0){
                                                            totalcredit = Double.parseDouble("0"+totalcredit);
                                                        }
                                                        if(totalcheque == 0){
                                                            totalcheque = Double.parseDouble("0"+totalcheque);
                                                        }
                                                        double daycheque = Double.parseDouble(z05) - Double.parseDouble(due.getText().toString());
                                                        double daycredit = Double.parseDouble(z08) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                        //zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));
                                                        //cheque
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                        //zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                    } else if(chequec.isChecked()){
                                                        double daycheque = Double.parseDouble(z05) - Double.parseDouble(due.getText().toString());
                                                        double totalcheque = Double.parseDouble(z06) - Double.parseDouble(due.getText().toString());
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));

                                                    } else if(creditc.isChecked()){
                                                        double daycredit = Double.parseDouble(z08) - Double.parseDouble(due.getText().toString());
                                                        double totalcredit = Double.parseDouble(z09) - Double.parseDouble(due.getText().toString());
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));

                                                    }else if(cashc.isChecked()){
                                                        double daycash = Double.parseDouble(z01) - Double.parseDouble(due.getText().toString());
                                                        totalcash = Double.parseDouble(z03) - Double.parseDouble(due.getText().toString());
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                    }
                                                    //zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));

                                                    if(d3 > 0) {
                                                        double discou = Double.parseDouble(z10) - Double.parseDouble(discount.getText().toString());
                                                        dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z10").setValue(dc.format(discou));
                                                    }
                                        /*}else{

                                            if(cashc.isChecked() && chequec.isChecked()){
                                                totalcash = Double.parseDouble(due.getText().toString());
                                                double totalcheque = (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(startcash));
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue(dc.format(totalcash));
                                                double fullcash = Double.parseDouble(cashm) - Double.parseDouble(String.valueOf(totalcash));
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(fullcash));
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("Z06").setValue(dc.format(totalcheque));
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");

                                            } else if(chequec.isChecked()){
                                                double totalcheque = Double.parseDouble(due.getText().toString());
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(startcash);
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");

                                            } else if(creditc.isChecked()){
                                                double totalcredit = Double.parseDouble(totamt.getText().toString());
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue("-"+dc.format(totalcredit));
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                                                dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(startcash);

                                            }else if(cashc.isChecked()){
                                                if(Double.parseDouble(due.getText().toString()) < Double.parseDouble(totamt.getText().toString())){
                                                    totalcash = Double.parseDouble(due.getText().toString());
                                                    double balcredit = Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString());
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(startcash));
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue(dc.format(totalcash));
                                                    double fullcash = Double.parseDouble(cashm) - Double.parseDouble(String.valueOf(totalcash));
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(fullcash));
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(balcredit));
                                                }else{
                                                    totalcash = Double.parseDouble(due.getText().toString());
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(startcash));
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z02").setValue("-"+dc.format(totalcash));
                                                    double fullcash = Double.parseDouble(cashm) - Double.parseDouble(String.valueOf(totalcash));
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(fullcash));
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                                                    dbs.child("m").child(monthformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");
                                                }
                                            }
                                            //zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                        }*/
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

                                                    if(cashc.isChecked() && chequec.isChecked()){
                                                        totalcash = Double.parseDouble(z03) - Double.parseDouble(due.getText().toString());
                                                        double totalcheque = Double.parseDouble(z06) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                        if(totalcheque == 0){
                                                            totalcheque = Double.parseDouble("0"+totalcheque);
                                                        }
                                                        if(totalcash == 0){
                                                            totalcash = Double.parseDouble("0"+totalcash);
                                                        }
                                                        double daycash = Double.parseDouble(z01) - Double.parseDouble(due.getText().toString());
                                                        double daycheque = Double.parseDouble(z05) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                        zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                        //cheque
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                        zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));

                                                    } else if(cashc.isChecked() && creditc.isChecked()){
                                                        //cash
                                                        totalcash = Double.parseDouble(z03) - Double.parseDouble(due.getText().toString());
                                                        double totalcredit = Double.parseDouble(z09) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                        if(totalcredit == 0){
                                                            totalcredit = Double.parseDouble("0"+totalcredit);
                                                        }
                                                        if(totalcash == 0){
                                                            totalcash = Double.parseDouble("0"+totalcash);
                                                        }
                                                        double daycash = Double.parseDouble(z01) - Double.parseDouble(due.getText().toString());
                                                        double daycredit = Double.parseDouble(z08) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                        zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                        //cheque
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                        zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                    } else if(chequec.isChecked() && creditc.isChecked()){
                                                        //cash
                                                        double totalcheque = Double.parseDouble(z06) - Double.parseDouble(due.getText().toString());
                                                        double totalcredit = Double.parseDouble(z09) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                        if(totalcredit == 0){
                                                            totalcredit = Double.parseDouble("0"+totalcredit);
                                                        }
                                                        if(totalcheque == 0){
                                                            totalcheque = Double.parseDouble("0"+totalcheque);
                                                        }
                                                        double daycheque = Double.parseDouble(z05) - Double.parseDouble(due.getText().toString());
                                                        double daycredit = Double.parseDouble(z08) - (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                        zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));
                                                        //cheque
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                        zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                    } else if(chequec.isChecked()){
                                                        double daycheque = Double.parseDouble(z05) - Double.parseDouble(due.getText().toString());
                                                        double totalcheque = Double.parseDouble(z06) - Double.parseDouble(due.getText().toString());
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z05").setValue(dc.format(daycheque));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                        zinfotsv.child("cheque").setValue(String.valueOf(dc.format(totalcheque)));

                                                    } else if(creditc.isChecked()){
                                                        double daycredit = Double.parseDouble(z08) - Double.parseDouble(due.getText().toString());
                                                        double totalcredit = Double.parseDouble(z09) - Double.parseDouble(due.getText().toString());
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z08").setValue(dc.format(daycredit));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalcredit));
                                                        zinfotsv.child("credit").setValue(String.valueOf(dc.format(totalcredit)));

                                                    }else if(cashc.isChecked()){
                                                        double daycash = Double.parseDouble(z01) - Double.parseDouble(due.getText().toString());
                                                        totalcash = Double.parseDouble(z03) - Double.parseDouble(due.getText().toString());
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(daycash));
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(totalcash));
                                                        zinfotsv.child("cash").setValue(String.valueOf(dc.format(totalcash)));
                                                    }

                                                    if(d3 > 0) {
                                                        double discou = Double.parseDouble(z10) - Double.parseDouble(discount.getText().toString());
                                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z10").setValue(dc.format(discou));
                                                    }
                                        /*}else{

                                            if(cashc.isChecked() && chequec.isChecked()){
                                                totalcash = Double.parseDouble(due.getText().toString());
                                                double totalcheque = (Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString()));

                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(startcash));
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue("-"+dc.format(totalcash));
                                                double fullcash = Double.parseDouble(cashm) - Double.parseDouble(String.valueOf(totalcash));
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(fullcash));
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");
                                                zinfotsv.child("cash").setValue(String.valueOf(dc.format(fullcash)));

                                            } else if(chequec.isChecked()){
                                                double totalcheque = Double.parseDouble(due.getText().toString());
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue(dc.format(totalcheque));
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(startcash);
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");

                                            } else if(creditc.isChecked()){
                                                double totalredit = Double.parseDouble(totamt.getText().toString());
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(totalredit));
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                                                dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(startcash);

                                            }else if(cashc.isChecked()){
                                                if(Double.parseDouble(due.getText().toString()) < Double.parseDouble(totamt.getText().toString())){
                                                    totalcash = Double.parseDouble(due.getText().toString());
                                                    double balcredit = Double.parseDouble(totamt.getText().toString()) - Double.parseDouble(due.getText().toString());

                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(startcash));
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue("-"+dc.format(totalcash));
                                                    double fullcash = Double.parseDouble(cashm) - Double.parseDouble(due.getText().toString());
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(fullcash));
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue(dc.format(balcredit));
                                                    zinfotsv.child("cash").setValue(String.valueOf(dc.format(fullcash)));
                                                }else{
                                                    totalcash = Double.parseDouble(due.getText().toString());
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z01").setValue(dc.format(startcash));
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z02").setValue("-"+dc.format(totalcash));
                                                    double fullcash = Double.parseDouble(cashm) - Double.parseDouble(due.getText().toString());
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z03").setValue(dc.format(fullcash));
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z06").setValue("0.00");
                                                    dbs.child("d").child(dayformat.format(csummary.getTime())).child("01").child("z09").setValue("0.00");
                                                    zinfotsv.child("cash").setValue(String.valueOf(dc.format(fullcash)));
                                                }
                                            }
                                        }*/
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });

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
                                                                                + "] Return. Amount:" + b7 + "\n" + l);
                                                            } else {
                                                                db.getParent().child("Log").child("log")
                                                                        .setValue(format.format(calendar.getTime()) + "\n["
                                                                                + fm.format(calendar.getTime())
                                                                                + "] Return. Amount:" + b7 + "\n\n" + l);
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError error) {
                                                        }
                                                    });
                                            billdb.child("Billr").removeValue();
                                            Toast.makeText(Returns.this, "Return Saved", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getApplicationContext(), BillR.class);
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
                                //dialog.dismiss();
                                Toast.makeText(Returns.this, "Bill not Added", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });

                        alert.show();
                    } else {
                        Toast.makeText(Returns.this, R.string.trans_nocusdetail, Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(Returns.this, R.string.trans_filldetail, Toast.LENGTH_SHORT).show();
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
                adapter = new AutoAdapter(Returns.this, R.layout.m_trans, R.id.item_name, models);
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
                autoCustAdapter = new AutoCustAdapter(Returns.this, R.layout.m_trans, R.id.item_name, modelcustomers);
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
                autoRouteAdapter = new AutoRouteAdapter(Returns.this, R.layout.m_trans, R.id.item_name, modelroute);
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
                autoRepAdapter = new AutoRepAdapter(Returns.this, R.layout.m_trans, R.id.item_name, modelcustomers);
                act3.setAdapter(autoRepAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listmodel.clear();
        lv = (ListView) findViewById(R.id.list);
        listadapter = new ListAdapter(Returns.this, R.layout.list_items, listmodel);
        lv.setAdapter(listadapter);
        Utility.setListViewHeightBasedOnChildren(lv);
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

                    AlertDialog.Builder alert = new AlertDialog.Builder(Returns.this);
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
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("01").child("z10").setValue("0.00");

                                        //add expense 0.00
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z01").setValue("Expense");
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z02").setValue("Expense");
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

                    AlertDialog.Builder alert = new AlertDialog.Builder(Returns.this);
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
                                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child("e-expense").child("z02").setValue("Expense");
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
                autoCustAdapter = new AutoCustAdapter(Returns.this, R.layout.m_trans, R.id.item_name, modelcustomers);
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

        if(totamt.getText().toString().equals("")){
            balb7 = 0.00;
        }else{
            balb7 = Double.parseDouble(totamt.getText().toString());
        }
        double baldue = Double.parseDouble(due.getText().toString().trim());

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
        } else if(cashc.isChecked() && chequec.isChecked() && creditc.isChecked()){
            Toast.makeText(Returns.this,R.string.trans_ccorc, Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(cashc.isChecked() && creditc.isChecked() && baldue >= balb7){
            Toast.makeText(Returns.this,"Enter the paid cash, Credit will be added automatically", Toast.LENGTH_LONG).show();
            due.setError("Paid cash can't be equal to or greater than total");
            flag = 1;
        } else if(cashc.isChecked() && creditc.isChecked() && due.getText().toString().trim().equals("0.00") || due.getText().toString().trim().isEmpty()){
            Toast.makeText(Returns.this,"Paid cash can't be empty", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(cashc.isChecked() && chequec.isChecked() && baldue >= balb7){
            Toast.makeText(Returns.this,"Enter the paid cash, Cheque will be added automatically", Toast.LENGTH_LONG).show();
            due.setError("Paid cash can't be equal to or greater than total");
            flag = 1;
        } else if(cashc.isChecked() && chequec.isChecked() && due.getText().toString().trim().equals("0.00") || due.getText().toString().trim().isEmpty()){
            Toast.makeText(Returns.this,"Paid cash can't be empty or 0.00", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(chequec.isChecked() && creditc.isChecked() && baldue >= balb7){
            Toast.makeText(Returns.this,"Enter the paid cheque, Credit will be added automatically", Toast.LENGTH_LONG).show();
            due.setError("Paid cheque can't be equal to or greater than total");
            flag = 1;
        } else if(chequec.isChecked() && creditc.isChecked() && due.getText().toString().trim().equals("0.00") || due.getText().toString().trim().isEmpty()){
            Toast.makeText(Returns.this,"Paid cheque can't be empty or 0.00", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(chequec.isChecked() && !due.getText().toString().trim().equals("0.00")){
            Toast.makeText(Returns.this,"Paid should be 0.00, if only cheque", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(creditc.isChecked() && !due.getText().toString().trim().equals("0.00")){
            Toast.makeText(Returns.this,"Paid should be 0.00, if only credit", Toast.LENGTH_LONG).show();
            flag = 1;
        } else if(baldue > balb7) {
            flag = 1;
            due.setError("Paid amount is greater than total");
        } else if(due.getText().toString().trim().isEmpty()) {
            flag = 1;
            due.setError(getString(R.string.trans_acantempty));
        } else if(due.getText().toString().trim().isEmpty() || due.getText().toString().trim().equals("0.00") && cashc.isChecked()) {
            flag = 1;
            due.setError("Enter customer paid amount");
        } else if(!cashc.isChecked() && !chequec.isChecked() && !creditc.isChecked()){
            Toast.makeText(Returns.this,R.string.trans_ccorc, Toast.LENGTH_LONG).show();
            flag = 1;
        }

        return flag != 1;
    }

    public boolean custValidate() {
        String agpt = act2.getText().toString();
        if(!agpt.contains("-")){
            StringTokenizer tokens = new StringTokenizer(agpt, "-");
            cusname = tokens.nextToken();// this will contain "Name"
            set = false;
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

                }

                //Putting key & tasks(ArrayList) in HashMap
                itemsMap.put(dataSnapshot.getKey(),tasks);

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
            item = adapter.det()[0];
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

                LayoutInflater layoutInflater = LayoutInflater.from(Returns.this);
                @SuppressLint("InflateParams") View promptView = layoutInflater.inflate(R.layout.input_dia, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Returns.this);
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                editText.setFocusable(true);
                editText.requestFocus();
                ((InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInputFromInputMethod(editText.getWindowToken(), 0);
                alertDialogBuilder.setCancelable(true).setPositiveButton(R.string.trans_set, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String qt = "";
                        String amount = "";
                        String amountd = "";
                        double amountdo = 0.00;
                        double d3d = 0.00;
                        qt = editText.getText().toString().trim();
                        if (!qt.equals("") && !qt.equals("0")) {

                            //double d = (Double.valueOf(qt) / Double.valueOf(u)) * selpr2;
                            double d2 = (Double.valueOf(qt) / Double.valueOf(u)) * Double.valueOf(p8);

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
                                    Toast.makeText(Returns.this, R.string.trans_alreadyin, Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(Returns.this, R.string.trans_alreadyin, Toast.LENGTH_SHORT).show();
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
                            gen.setText(R.string.return_generatereturn);

                            Utility.setListViewHeightBasedOnChildren(lv);

                            dialog.dismiss();

                        } else {
                            Toast.makeText(Returns.this, R.string.trans_validqty, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Returns.this, R.string.trans_itemnotmatch, Toast.LENGTH_SHORT).show();
            }
        }

        else {
            Toast.makeText(Returns.this, R.string.trans_nofurtheritem, Toast.LENGTH_SHORT).show();
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

    public void updateQty(final String key, final String med, final String p3, String qty, final String orgqty, String p6, String p7, String exp, String bat, final String pr, String unit, String p11, String p4, String p18, String p19, String p20, String p21) {
        int oqt = Integer.parseInt(orgqty);
        final int qt = Integer.parseInt(qty);
        oqt = oqt + qt;

        //Toast.makeText(Returns.this, "Key "+key, Toast.LENGTH_SHORT).show();

        final double cal = ((Double.parseDouble(p6) / 100) * Double.parseDouble(p7)) + Double.parseDouble(p6);
        //Toast.makeText(Returns.this, "cal "+cal, Toast.LENGTH_SHORT).show();
        final double cal1 = cal * oqt;
        //Toast.makeText(Returns.this, "cal1 "+cal1, Toast.LENGTH_SHORT).show();

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
        db.child("Items").child(key).child("p17").setValue(String.valueOf(dc.format(cal1)));

        final int finalOqt = oqt;

        csummary = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dayformatday = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformat = new SimpleDateFormat("MMyyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat monthformatm = new SimpleDateFormat("MM/yyyy");

        //month returns
        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sales = (String) dataSnapshot.child("z04").getValue();
                String salesv = (String) dataSnapshot.child("z05").getValue();

                if (dataSnapshot.hasChild("z04")) {

                    int newqty = Integer.parseInt(sales) + qt;
                    double soldvalue = Double.parseDouble(salesv) + (qt * Integer.parseInt(pr));

                    if(cashc.isChecked()) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z04").setValue(String.valueOf(newqty));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z05").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z00").setValue(String.valueOf(pr));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z01").setValue(String.valueOf(med));
                        //dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z09").setValue(String.valueOf(p3));
                    if(cashc.isChecked()) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z04").setValue(String.valueOf(qt));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z05").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //day returns
        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sales = (String) dataSnapshot.child("z04").getValue();
                String salesv = (String) dataSnapshot.child("z05").getValue();

                if (dataSnapshot.hasChild("z04")) {

                    int newqty = Integer.parseInt(sales) + qt;
                    double soldvalue = Double.parseDouble(salesv) + (qt * Integer.parseInt(pr));

                    if(cashc.isChecked()) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z04").setValue(String.valueOf(newqty));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z05").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z00").setValue(String.valueOf(pr));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z01").setValue(String.valueOf(med));
                        //dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z09").setValue(String.valueOf(p3));

                    if(cashc.isChecked()) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z04").setValue(String.valueOf(qt));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z05").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //month credit returns
        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salesc = (String) dataSnapshot.child("z04c").getValue();
                String salesvc = (String) dataSnapshot.child("z05c").getValue();

                if (dataSnapshot.hasChild("z04c")) {

                    int newqtyc = Integer.parseInt(salesc) + qt;
                    double soldvaluec = Double.parseDouble(salesvc) + (qt * Integer.parseInt(pr));

                    if(creditc.isChecked()) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z04c").setValue(String.valueOf(newqtyc));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z05c").setValue(String.valueOf(dc.format(soldvaluec)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                    if(creditc.isChecked()) {
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z04c").setValue(String.valueOf(qt));
                        dbs.child("m").child(monthformat.format(csummary.getTime())).child(key).child("z05c").setValue(String.valueOf(dc.format(soldvalue)));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //day credit returns
        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String salesc = (String) dataSnapshot.child("z04c").getValue();
                String salesvc = (String) dataSnapshot.child("z05c").getValue();

                if (dataSnapshot.hasChild("z04c")) {

                    int newqtyc = Integer.parseInt(salesc) + qt;
                    double soldvaluec = Double.parseDouble(salesvc) + (qt * Integer.parseInt(pr));

                    if(creditc.isChecked()) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z04c").setValue(String.valueOf(newqtyc));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z05c").setValue(String.valueOf(dc.format(soldvaluec)));
                    }
                }else{
                    double soldvalue = qt * Integer.parseInt(pr);

                    if(creditc.isChecked()) {
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z04c").setValue(String.valueOf(qt));
                        dbs.child("d").child(monthformat.format(csummary.getTime())).child(dayformat.format(csummary.getTime())).child(key).child("z05c").setValue(String.valueOf(dc.format(soldvalue)));
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
        org = org - samt;
        final double tot = totamount;
        final double balance = org;
        final double paid = am;

        dbc.child("Person").child(key).child("p14").setValue(String.valueOf(dc.format(org)));
        dbc.child("Person").child(key).child("log").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue();

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                dbc.child("Person").child(key).child("log").setValue(value + "[" + format.format(calendar.getTime())
                        + "] Return. Total: " + tot + " Paid: " + paid + " Balance: " + balance + "\n");
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
