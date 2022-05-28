package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

public class BillR extends AppCompatActivity implements Runnable{

    public SharedPreferences sharedPreferences;
    CheckBox slang;
    String btDr, btAr, items;
    TextView ph1, ph2, storename, storeaddr, transid, date, time, gst, ptname, b3, docname, totamt, name, totamtpaid, totamtbal, discount;
    TextView medname[], unit[], batch[], qty[], rate[], exp[], amt[], dis[];
    String ids[], brno, storeaddronly, cusname, cusaddr, current, storeaddronlyt, storenamet;
    private DatabaseReference du, di, dc, billdb;
    private FirebaseAuth mAuth;
    String uid;
    ProgressDialog pd;
    double totbal = 0.00;
    Button mScan, mPrint;

    boolean checkBoxValueE;

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    TextView stat, repmob, cadd;
    private static OutputStream outputStream;
    public boolean isFirstRun;
    private AdView mAdView;
    RelativeLayout fh;
    int height = 0;
    int kc = -1;

    String store1, sadd, ph, cus, cusadd1, r1, r2, d1, q1, line1, list1, list2, tot2, cp2, bal2, t1, t2, t3, eline, rby, cid1, rs, sib;
    String list = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.invoice);
        getSupportActionBar().hide();

        MobileAds.initialize(this, String.valueOf(R.string.app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mPrint = (Button) findViewById(R.id.mPrint);
        mScan = (Button)findViewById(R.id.Scan);
        stat = (TextView)findViewById(R.id.bpstatus);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        isFirstRun = getSharedPreferences("PREFERENCER", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        btDr = sharedPreferences.getString("btDr", "");
        btAr = sharedPreferences.getString("btAr", "");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        LocaleUtils.updateConfig(this,prefs.getString("My_Lang", ""));
        current = prefs.getString("My_Lang", "");//Locale.getDefault().getDisplayLanguage();
        //if (current.toLowerCase().contains("en")) {}

        if(mBluetoothAdapter.isEnabled()) {
            if (!isFirstRun) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Log.v(TAG, "Coming incoming address " + btAr);
                mBluetoothDevice = mBluetoothAdapter
                        .getRemoteDevice(btAr);
                mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                        "Connecting...", btDr + " : "
                                + btAr, true, true);
                Thread mBlutoothConnectThread = new Thread(this);
                mBlutoothConnectThread.start();
            }
        }

        pd = new ProgressDialog(BillR.this);
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        //mac = settings.getString(bluetoothAddressKey, btAr);

        ph1 = (TextView) findViewById(R.id.ph1);
        ph2 = (TextView) findViewById(R.id.ph2);
        storename = (TextView) findViewById(R.id.storename);
        storeaddr = (TextView) findViewById(R.id.storeaddr);
        transid = (TextView) findViewById(R.id.transid);
        date = (TextView) findViewById(R.id.date);
        time = (TextView) findViewById(R.id.time);
        gst = (TextView) findViewById(R.id.gst);
        ptname = (TextView) findViewById(R.id.ptname);
        b3 = (TextView) findViewById(R.id.b3);
        docname = (TextView) findViewById(R.id.docname);
        totamt = (TextView) findViewById(R.id.totamt);
        discount = (TextView) findViewById(R.id.discount);
        name = (TextView) findViewById(R.id.name);
        totamtpaid = (TextView) findViewById(R.id.totamtpaid);
        totamtbal = (TextView) findViewById(R.id.totamtbal);
        fh = (RelativeLayout) findViewById(R.id.frameh);
        slang = (CheckBox) findViewById(R.id.slang);

        medname = new TextView[30];
        unit = new TextView[30];
        batch = new TextView[30];
        qty = new TextView[30];
        rate = new TextView[30];
        exp = new TextView[30];
        amt = new TextView[30];
        dis = new TextView[30];

        ids = new String[] { "medname", "unit", "batch", "qty", "rate", "exp", "amt", "dis"  };
        for (int j = 1; j <= 30; j++) {
            medname[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[0] + j, "id", getPackageName()));
            unit[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[1] + j, "id", getPackageName()));
            batch[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[2] + j, "id", getPackageName()));
            qty[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[3] + j, "id", getPackageName()));
            rate[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[4] + j, "id", getPackageName()));
            exp[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[5] + j, "id", getPackageName()));
            amt[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[6] + j, "id", getPackageName()));
            dis[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[7] + j, "id", getPackageName()));
        }

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
        du.keepSynced(true);

        du.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                if (current.toLowerCase().contains("en")) {
                    storename.setText(map.get("i02"));
                    storeaddronly = map.get("i04");
                    storenamet = map.get("0i2");
                    storeaddronlyt = map.get("i04");
                    storeaddr.setText(map.get("i04"));
                }else{
                    storename.setText(map.get("i02t"));
                    storeaddronly = map.get("i04t");
                    storenamet = map.get("i02");
                    storeaddronlyt = map.get("i04");
                    storeaddr.setText(map.get("i04t"));
                }
                // + " | BR: " + map.get("i03")
                brno = "BR: " + map.get("i03");
                ph1.setText(map.get("i05"));
                ph2.setText(map.get("i06"));
                String dateTime[] = getDateTime();
                gst.setText(dateTime[1]);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        Intent intent = getIntent();
        String id = intent.getStringExtra("key");
        String iddate = intent.getStringExtra("date");
        final String rep = intent.getStringExtra("rep");

        billdb = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Billr");
        billdb.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                if (dataSnapshot.exists()) {
                    if (map.get("Rep").equals(rep)) {
                        billdb.removeValue();
                    } else {
                        //TODO
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        di = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Return").child(iddate).child("R").child(id);
        di.keepSynced(true);

        di.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                totamt.setText(map.get("b07"));
                date.setText(map.get("b04"));
                time.setText(map.get("b10"));
                docname.setText(map.get("b05"));

                String disc = map.get("b11");
                if(disc != null){
                    discount.setText(map.get("b11"));
                }else{
                    discount.setText("0.00");
                }

                if(!Objects.requireNonNull(map.get("b02")).contains("-")){
                    StringTokenizer tokens = new StringTokenizer(map.get("b02"), "-");
                    cusname = tokens.nextToken();// this will contain "Name"
                    ptname.setText(cusname);
                    b3.setText("");
                }else {
                    StringTokenizer tokens = new StringTokenizer(map.get("b02"), "-");
                    cusname = tokens.nextToken();// this will contain "Name"
                    cusaddr = tokens.nextToken();// this will contain "Address"

                    ptname.setText(cusname);
                    b3.setText(cusaddr);
                }
                transid.setText(Objects.requireNonNull(map.get("b01")));
                totamtpaid.setText(map.get("b08"));
                totamtbal.setText(map.get("b09"));

                if (current.toLowerCase().contains("en")) {
                    slang.isChecked();
                }

                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                //boolean checkBoxValueE = sharedPreferences.getBoolean("CheckBox_Slang", false);

                if (current.toLowerCase().contains("en")) {
                    items = map.get("b06");
                    /*checkBoxValueE = sharedPreferences.getBoolean("CheckBox_Slang", true);
                    if (checkBoxValueE) {
                        items = map.get("b06");
                    } else {
                        items = map.get("b06t");
                    }
                }else{
                    checkBoxValueE = sharedPreferences.getBoolean("CheckBox_Slang", true);
                    if (checkBoxValueE) {
                        items = map.get("b06");
                    } else {
                        items = map.get("b06t");
                    }*/
                }

                StringTokenizer st = new StringTokenizer(items, "®");
                while (st.hasMoreTokens()) {
                    kc++;
                    // Toast.makeText(BillR.this,""+kc , Toast.LENGTH_SHORT).show();
                    StringTokenizer sti = new StringTokenizer(st.nextToken(), "©");
                    while (sti.hasMoreTokens()) {
                        medname[kc].setText(sti.nextToken());
                        unit[kc].setText(sti.nextToken());//"1x" + sti.nextToken()
                        //batch[kc].setText(sti.nextToken());
                        qty[kc].setText(sti.nextToken());
                        rate[kc].setText(sti.nextToken());
                        //exp[kc].setText(sti.nextToken());
                        amt[kc].setText(sti.nextToken());
                        if(sti.hasMoreTokens()){
                            dis[kc].setText(sti.nextToken());
                        }
                        medname[kc].measure(0, 0);
                        height = medname[kc].getMeasuredHeight();
                    }
                    fh.getLayoutParams().height += height * (kc+1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void SwitchLang(View v){
        savePreferences("CheckBox_Slang", slang.isChecked());
        recreate();
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void printEbill(View v){

        AlertDialog.Builder alert = new AlertDialog.Builder(BillR.this);
        alert.setTitle(R.string.invo_print);
        ///alert.setMessage("Generate Bill?");
        alert.setPositiveButton(R.string.bill_original, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                printOri();
            }
        });
        alert.setNegativeButton(R.string.bill_copy, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                printCopy();

            }
        });
        alert.setNeutralButton(R.string.trans_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    public void printOri(){
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();

                    if(mBluetoothSocket == null){
                        //Intent BTIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                        //this.startActivityForResult(BTIntent, DeviceListActivity.REQUEST_CONNECT_BT);
                        Toast.makeText(BillR.this,  R.string.bill_bnotconnect, Toast.LENGTH_SHORT).show();
                    } else {
                        OutputStream opstream = null;
                        try {
                            opstream = mBluetoothSocket.getOutputStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        outputStream = opstream;
                        //print command
                        try {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            outputStream = mBluetoothSocket.getOutputStream();
                            byte[] printformat = new byte[]{0x1B,0x21,0x03};
                            outputStream.write(printformat);

                            String sn = storenamet; //+" ("+brno+")"
                            if(sn.length() <= 48){
                                int sn48 = (48 - sn.length())/2;
                                String spa = new String(new char[sn48]).replace("\0", " ");

                                if ( ( sn48 % 2 ) == 0 ) {
                                    printCustom(spa + sn + spa,1,1); //Is even
                                } else {
                                    String spa1 = new String(new char[sn48+1]).replace("\0", " ");
                                    printCustom(spa1 + sn + spa,1,1);//Is odd
                                }
                            }

                            if(storeaddronlyt.length() <= 63){
                                final int sn48 = (63 - storeaddronlyt.length())/2;
                                String spa = new String(new char[sn48]).replace("\0", " ");

                                if ( ( sn48 % 2 ) == 0 ) {
                                    printCustom(spa + storeaddronlyt + spa,0,1); //Is even
                                } else {
                                    String spa1 = new String(new char[sn48+1]).replace("\0", " ");
                                    printCustom(spa1 + storeaddronlyt + spa,0,1);//Is odd
                                }
                            }

                            String pe = ph1.getText().toString() + " | " + ph2.getText().toString();
                            if(pe.length() <= 62){
                                final int sn48 = (62 - pe.length())/2;
                                String spa = new String(new char[sn48]).replace("\0", " ");
                                printCustom(spa + pe + spa,0,1);
                            }

                            printCustom(new String(new char[63]).replace("\0", " "),0,1);
                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);
                            String ib = "RETURN BILL";
                            int sn48i = (63 - ib.length())/2;
                            String ibl = new String(new char[sn48i]).replace("\0", " ");
                            String ibl1 = new String(new char[sn48i+1]).replace("\0", " ");
                            printCustom(ibl1 + ib + ibl,0,1);//Is odd

                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);
                            printCustom(new String(new char[63]).replace("\0", " "),0,1);

                            String c = "Customer: " + ptname.getText().toString();
                            if(c.length() <= 63){
                                final int sn48 = 63 - c.length();
                                printCustom(c + new String(new char[sn48]).replace("\0", " "),0,1);
                            }

                            String ca1 = "          " + b3.getText().toString();
                            if(ca1.length() <= 63){
                                final int sn48 = 63 - ca1.length();
                                printCustom(ca1 + new String(new char[sn48]).replace("\0", " "),0,1);
                            }else{
                                String ca2 = cadd.getText().toString();
                                int index= ca2.lastIndexOf(" ");
                                String add1 = ca2.substring(0, index);
                                String add2 = ca2.substring(index+1);
                                if(add1.length() <= 53){
                                    int sn48 = 53 - add1.length();
                                    int sn48a = 53 - add2.length();
                                    printCustom("          " + add1 + new String(new char[sn48]).replace("\0", " "),0,1);
                                    printCustom("          " + add2 + new String(new char[sn48a]).replace("\0", " "),0,1);
                                }else {
                                    int index1= add1.lastIndexOf(" ");
                                    String add3 = add1.substring(0, index1);
                                    String add4 = add1.substring(index1+1);
                                    int sn48b = 53 - add3.length();
                                    int sn48c = 53 - (add4.length()+add2.length());
                                    printCustom("          " + add3 + new String(new char[sn48b]).replace("\0", " "),0,1);
                                    printCustom("          " + add4 + " " + add2 + new String(new char[sn48c]).replace("\0", " "),0,1);
                                }
                            }

                            printCustom(new String(new char[63]).replace("\0", " "),0,1);

                            String r = "Rep: " + docname.getText().toString();
                            String oc = "ORIGINAL";
                            int oc1 = r.length() + oc.length();

                            if(oc1 < 63){
                                final int sn48 = 63 - oc1;
                                printCustom(r + new String(new char[sn48]).replace("\0", " ") + oc,0,1);
                            }

                            String d = date.getText().toString() + " | " + gst.getText().toString();
                            String id = "SN: " + transid.getText().toString();
                            int lengthdid = d.length() + id.length();

                            if(lengthdid <= 63){
                                final int sn48 = 63 - lengthdid;
                                printCustom(d + new String(new char[sn48]).replace("\0", " ") + id,0,1);
                            }

                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);

                            String q = "Products:" + new String(new char[25]).replace("\0", " ");
                            String un = "Rate:" + new String(new char[8]).replace("\0", " ");
                            String qt = "Qty:" + new String(new char[6]).replace("\0", " ");
                            String p = "Value:";

                            printCustom(q  + qt  + un + p,0,1);

                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);

                            medname = new TextView[30];
                            unit = new TextView[30];
                            batch = new TextView[30];
                            qty = new TextView[30];
                            rate = new TextView[30];
                            exp = new TextView[30];
                            amt = new TextView[30];
                            dis = new TextView[30];

                            ids = new String[] { "medname", "unit", "batch", "qty", "rate", "exp", "amt", "dis" };
                            for (int j = 1; j <= kc+1; j++) {
                                medname[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[0] + j, "id", getPackageName()));
                                unit[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[1] + j, "id", getPackageName()));
                                batch[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[2] + j, "id", getPackageName()));
                                qty[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[3] + j, "id", getPackageName()));
                                rate[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[4] + j, "id", getPackageName()));
                                exp[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[5] + j, "id", getPackageName()));
                                amt[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[6] + j, "id", getPackageName()));
                                dis[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[7] + j, "id", getPackageName()));
                                String smedname = medname[j - 1].getText().toString();
                                if (!smedname.equals("")){

                                    String string1 = medname[j - 1].getText().toString();
                                    String string1q = qty[j - 1].getText().toString();
                                    String string1r = rate[j - 1].getText().toString();
                                    String string2 = amt[j - 1].getText().toString();
                                    String string3 = dis[j - 1].getText().toString();
                                    int s1len = string1.length();

                                    if(string1.length() <= 32){
                                        if(j == 1) {
                                            printCustom(string1 + new String(new char[37-(string1.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[11-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[15-string2.length()]).replace("\0", " ") +string2,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + "-"+string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + "-"+string3, 0, 1);
                                                }
                                            }
                                        }else{
                                            printCustom(string1 + new String(new char[37-(string1.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[11-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[15-string2.length()]).replace("\0", " ") +string2,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + "-"+string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + "-"+string3, 0, 1);
                                                }
                                            }
                                        }
                                    } else {

                                        String s20 = string1.substring(0, 28);
                                        int index1= s20.lastIndexOf(" ");
                                        String add3 = s20.substring(0, index1);
                                        String add4 = s20.substring(index1+1);
                                        String add5 = string1.substring(21, s1len);

                                        if(j == 1) {
                                            printCustom(add3 + new String(new char[26-(add3.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[10-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[10-string2.length()]).replace("\0", " ") + string2
                                                    + "\r\n" + add4 + " " + add5,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + string3, 0, 1);
                                                }
                                            }
                                        }else{
                                            printCustom(add3 + new String(new char[26-(add3.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[10-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[10-string2.length()]).replace("\0", " ") + string2
                                                    + "\r\n" + add4 + " " + add5,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + string3, 0, 1);
                                                }
                                            }
                                        }

                                    }
                                }
                                printCustom(new String(new char[63]).replace("\0", " "),0,1);
                            }
                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);

                            int totspace = totamt.getText().toString().length();
                            String tot = "Total: " + totamt.getText().toString();
                            if(tot.length() <= 63){
                                int to = 63 - tot.length();
                                printCustom(new String(new char[to]).replace("\0", " ") + tot,0,1);
                            }
                            if(!discount.getText().toString().equals("0.00")) {
                                String totdis = "Discount: " + discount.getText().toString();
                                if (totdis.length() <= 63) {
                                    int todis = 63 - totdis.length();
                                    printCustom("Discount: " + new String(new char[todis]).replace("\0", " ") + discount.getText().toString(), 0, 1);
                                }
                            }
                            String cp = "Customer Paid: " + totamt.getText().toString();
                            if(cp.length() <= 63){
                                int p2 = 63 - ("Customer Paid: " + new String(new char[totspace-totamtpaid.getText().toString().length()]).replace("\0", " ") + totamtpaid.getText().toString()).length();
                                printCustom(new String(new char[p2]).replace("\0", " ") + "Customer Paid: " + new String(new char[totspace-totamtpaid.getText().toString().length()]).replace("\0", " ") + totamtpaid.getText().toString(),0,1);
                            }
                            String bal = "" + totbal;
                            if(bal.length() <= 63){
                                int bal1 = totspace - bal.length();
                                int p1 = 63-("Balance: " + new String(new char[bal1]).replace("\0", " ") + bal).length();
                                printCustom(new String(new char[p1]).replace("\0", " ") + "Balance: " + new String(new char[bal1]).replace("\0", " ") + bal,0,1);
                            }

                            printCustom(new String(new char[63]).replace("\0", " "),0,1);

                            String thank = "THANK YOU FOR THE BUSINESS WITH US";
                            int than = (63 - thank.length())/2;
                            String thanks = new String(new char[than]).replace("\0", " ");
                            printCustom( thanks + thank + thanks,0,1);

                            String thank1 = "CALL US FOR THE NEXT ORDER";
                            int than1 = (63 - thank1.length())/2;
                            String thanks1 = new String(new char[than1]).replace("\0", " ");
                            printCustom( thanks1 + thank1 + thanks1,0,1);

                            String thank2 = "POS System & App by: pos.jkdesigns.app";
                            int than2 = (63 - thank2.length())/2;
                            String thanks2 = new String(new char[than2]).replace("\0", " ");
                            printCustom( thanks2 + thank2 + thanks2,0,1);
                            printNewLine();
                            printNewLine();
                            printNewLine();
                            outputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Log.e("PrintActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    public void printCopy(){
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();

                    if(mBluetoothSocket == null){
                        //Intent BTIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                        //this.startActivityForResult(BTIntent, DeviceListActivity.REQUEST_CONNECT_BT);
                        Toast.makeText(BillR.this, R.string.bill_bnotconnect, Toast.LENGTH_SHORT).show();
                    } else {
                        OutputStream opstream = null;
                        try {
                            opstream = mBluetoothSocket.getOutputStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        outputStream = opstream;
                        //print command
                        try {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            outputStream = mBluetoothSocket.getOutputStream();
                            byte[] printformat = new byte[]{0x1B,0x21,0x03};
                            outputStream.write(printformat);

                            String sn = storenamet; //+" ("+brno+")"
                            if(sn.length() <= 48){
                                int sn48 = (48 - sn.length())/2;
                                String spa = new String(new char[sn48]).replace("\0", " ");

                                if ( ( sn48 % 2 ) == 0 ) {
                                    printCustom(spa + sn + spa,1,1); //Is even
                                } else {
                                    String spa1 = new String(new char[sn48+1]).replace("\0", " ");
                                    printCustom(spa1 + sn + spa,1,1);//Is odd
                                }
                            }

                            if(storeaddronlyt.length() <= 63){
                                final int sn48 = (63 - storeaddronlyt.length())/2;
                                String spa = new String(new char[sn48]).replace("\0", " ");

                                if ( ( sn48 % 2 ) == 0 ) {
                                    printCustom(spa + storeaddronlyt + spa,0,1); //Is even
                                } else {
                                    String spa1 = new String(new char[sn48+1]).replace("\0", " ");
                                    printCustom(spa1 + storeaddronlyt + spa,0,1);//Is odd
                                }
                            }

                            String pe = ph1.getText().toString() + " | " + ph2.getText().toString();
                            if(pe.length() <= 62){
                                final int sn48 = (62 - pe.length())/2;
                                String spa = new String(new char[sn48]).replace("\0", " ");
                                printCustom(spa + pe + spa,0,1);
                            }

                            printCustom(new String(new char[63]).replace("\0", " "),0,1);
                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);
                            String ib = "RETURN BILL";
                            int sn48i = (63 - ib.length())/2;
                            String ibl = new String(new char[sn48i]).replace("\0", " ");
                            String ibl1 = new String(new char[sn48i+1]).replace("\0", " ");
                            printCustom(ibl1 + ib + ibl,0,1);//Is odd

                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);
                            printCustom(new String(new char[63]).replace("\0", " "),0,1);

                            String c = "Customer: " + ptname.getText().toString();
                            if(c.length() <= 63){
                                final int sn48 = 63 - c.length();
                                printCustom(c + new String(new char[sn48]).replace("\0", " "),0,1);
                            }

                            String ca1 = "          " + b3.getText().toString();
                            if(ca1.length() <= 63){
                                final int sn48 = 63 - ca1.length();
                                printCustom(ca1 + new String(new char[sn48]).replace("\0", " "),0,1);
                            }else{
                                String ca2 = cadd.getText().toString();
                                int index= ca2.lastIndexOf(" ");
                                String add1 = ca2.substring(0, index);
                                String add2 = ca2.substring(index+1);
                                if(add1.length() <= 53){
                                    int sn48 = 53 - add1.length();
                                    int sn48a = 53 - add2.length();
                                    printCustom("          " + add1 + new String(new char[sn48]).replace("\0", " "),0,1);
                                    printCustom("          " + add2 + new String(new char[sn48a]).replace("\0", " "),0,1);
                                }else {
                                    int index1= add1.lastIndexOf(" ");
                                    String add3 = add1.substring(0, index1);
                                    String add4 = add1.substring(index1+1);
                                    int sn48b = 53 - add3.length();
                                    int sn48c = 53 - (add4.length()+add2.length());
                                    printCustom("          " + add3 + new String(new char[sn48b]).replace("\0", " "),0,1);
                                    printCustom("          " + add4 + " " + add2 + new String(new char[sn48c]).replace("\0", " "),0,1);
                                }
                            }

                            printCustom(new String(new char[63]).replace("\0", " "),0,1);

                            String r = "Rep: " + docname.getText().toString();
                            String oc = "COPY";
                            int oc1 = r.length() + oc.length();

                            if(oc1 < 63){
                                final int sn48 = 63 - oc1;
                                printCustom(r + new String(new char[sn48]).replace("\0", " ") + oc,0,1);
                            }

                            String d = date.getText().toString() + " | " + gst.getText().toString();
                            String id = "SN: " + transid.getText().toString();
                            int lengthdid = d.length() + id.length();

                            if(lengthdid <= 63){
                                final int sn48 = 63 - lengthdid;
                                printCustom(d + new String(new char[sn48]).replace("\0", " ") + id,0,1);
                            }

                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);

                            String q = "Products:" + new String(new char[25]).replace("\0", " ");
                            String un = "Rate:" + new String(new char[8]).replace("\0", " ");
                            String qt = "Qty:" + new String(new char[6]).replace("\0", " ");
                            String p = "Value:";

                            printCustom(q  + qt  + un + p,0,1);

                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);

                            medname = new TextView[30];
                            unit = new TextView[30];
                            batch = new TextView[30];
                            qty = new TextView[30];
                            rate = new TextView[30];
                            exp = new TextView[30];
                            amt = new TextView[30];
                            dis = new TextView[30];

                            ids = new String[] { "medname", "unit", "batch", "qty", "rate", "exp", "amt", "dis" };
                            for (int j = 1; j <= kc+1; j++) {
                                medname[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[0] + j, "id", getPackageName()));
                                unit[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[1] + j, "id", getPackageName()));
                                batch[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[2] + j, "id", getPackageName()));
                                qty[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[3] + j, "id", getPackageName()));
                                rate[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[4] + j, "id", getPackageName()));
                                exp[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[5] + j, "id", getPackageName()));
                                amt[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[6] + j, "id", getPackageName()));
                                dis[j - 1] = (TextView) findViewById(getResources().getIdentifier(ids[7] + j, "id", getPackageName()));
                                String smedname = medname[j - 1].getText().toString();
                                if (!smedname.equals("")){

                                    String string1 = medname[j - 1].getText().toString();
                                    String string1q = qty[j - 1].getText().toString();
                                    String string1r = rate[j - 1].getText().toString();
                                    String string2 = amt[j - 1].getText().toString();
                                    String string3 = dis[j - 1].getText().toString();
                                    int s1len = string1.length();

                                    if(string1.length() <= 32){
                                        if(j == 1) {
                                            printCustom(string1 + new String(new char[37-(string1.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[11-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[15-string2.length()]).replace("\0", " ") +string2,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + string3, 0, 1);
                                                }
                                            }
                                        }else{
                                            printCustom(string1 + new String(new char[37-(string1.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[11-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[15-string2.length()]).replace("\0", " ") +string2,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + string3, 0, 1);
                                                }
                                            }
                                        }
                                    } else {

                                        String s20 = string1.substring(0, 28);
                                        int index1= s20.lastIndexOf(" ");
                                        String add3 = s20.substring(0, index1);
                                        String add4 = s20.substring(index1+1);
                                        String add5 = string1.substring(21, s1len);

                                        if(j == 1) {
                                            printCustom(add3 + new String(new char[26-(add3.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[10-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[10-string2.length()]).replace("\0", " ") + string2
                                                    + "\r\n" + add4 + " " + add5,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + string3, 0, 1);
                                                }
                                            }
                                        }else{
                                            printCustom(add3 + new String(new char[26-(add3.length()+string1q.length())]).replace("\0", " ")
                                                    + string1q + new String(new char[10-string1r.length()]).replace("\0", " ") + string1r
                                                    + new String(new char[10-string2.length()]).replace("\0", " ") + string2
                                                    + "\r\n" + add4 + " " + add5,0,1);
                                            if(!string3.equals(null)) {
                                                String totd = "Discount: " + string3;
                                                if (totd.length() <= 63) {
                                                    int tod = 63 - totd.length();
                                                    printCustom("Discount: " + new String(new char[tod]).replace("\0", " ") + string3, 0, 1);
                                                }
                                            }
                                        }

                                    }
                                }
                                printCustom(new String(new char[63]).replace("\0", " "),0,1);
                            }
                            printCustom(new String(new char[63]).replace("\0", "-"),0,1);

                            int totspace = totamt.getText().toString().length();
                            String tot = "Total: " + totamt.getText().toString();
                            if(tot.length() <= 63){
                                int to = 63 - tot.length();
                                printCustom(new String(new char[to]).replace("\0", " ") + tot,0,1);
                            }
                            if(!discount.getText().toString().equals("0.00")) {
                                String totdis = "Discount: " + discount.getText().toString();
                                if (totdis.length() <= 63) {
                                    int todis = 63 - totdis.length();
                                    printCustom("Discount: " + new String(new char[todis]).replace("\0", " ") + discount.getText().toString(), 0, 1);
                                }
                            }
                            String cp = "Customer Paid: " + totamt.getText().toString();
                            if(cp.length() <= 63){
                                int p2 = 63 - ("Customer Paid: " + new String(new char[totspace-totamtpaid.getText().toString().length()]).replace("\0", " ") + totamtpaid.getText().toString()).length();
                                printCustom(new String(new char[p2]).replace("\0", " ") + "Customer Paid: " + new String(new char[totspace-totamtpaid.getText().toString().length()]).replace("\0", " ") + totamtpaid.getText().toString(),0,1);
                            }
                            String bal = "" + totbal;
                            if(bal.length() <= 63){
                                int bal1 = totspace - bal.length();
                                int p1 = 63-("Balance: " + new String(new char[bal1]).replace("\0", " ") + bal).length();
                                printCustom(new String(new char[p1]).replace("\0", " ") + "Balance: " + new String(new char[bal1]).replace("\0", " ") + bal,0,1);
                            }

                            printCustom(new String(new char[63]).replace("\0", " "),0,1);

                            String thank = "THANK YOU FOR THE BUSINESS WITH US";
                            int than = (63 - thank.length())/2;
                            String thanks = new String(new char[than]).replace("\0", " ");
                            printCustom( thanks + thank + thanks,0,1);

                            String thank1 = "CALL US FOR THE NEXT ORDER";
                            int than1 = (63 - thank1.length())/2;
                            String thanks1 = new String(new char[than1]).replace("\0", " ");
                            printCustom( thanks1 + thank1 + thanks1,0,1);

                            String thank2 = "POS System & App by: pos.jkdesigns.app";
                            int than2 = (63 - thank2.length())/2;
                            String thanks2 = new String(new char[than2]).replace("\0", " ");
                            printCustom( thanks2 + thank2 + thanks2,0,1);
                            printNewLine();
                            printNewLine();
                            printNewLine();
                            outputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    Log.e("PrintActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B,0x21,0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text
        try {
            switch (size){
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align){
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }

            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if(bmp!=null){
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            }else{
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    //print unicode
    public void printUnicode(){
        try {
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String leftRightAlign(String str1, String str2) {
        String ans = str1 + str2;
        Log.d("str1", String.valueOf(str1.length()));
        Log.d("str2", String.valueOf(str2.length()));

        if(ans.length() < 48){
            int m = (48 - ans.length());
            ans = str1 + new String(new char[m]).replace("\0", " ") + str2;
            Log.d("m", String.valueOf(String.valueOf(m).length()));
        }
        return ans;
    }

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    public void ConnectBt(View v){

        if(mScan.getText().equals("Connect"))
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(BillR.this, R.string.invo_nobadapter, Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                }
            }

        }
        else if(mScan.getText().equals("Disconnect") || mScan.getText().equals("துண்டி"))
        {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.disable();
            stat.setText("");
            stat.setText(R.string.invo_discon);
            stat.setTextColor(Color.rgb(199, 59, 59));
            mPrint.setEnabled(false);
            mScan.setEnabled(true);
            mScan.setText(R.string.invo_connect);
            //SavePreferences("btDr", "");
            SavePreferences("btAr", "");
            getSharedPreferences("PREFERENCER", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRunR", true).apply();
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                android.util.Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void connectDisconnect(View view) {
        if(mScan.getText().toString().equals("Connect") || mScan.getText().toString().equals("இணை"))
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(BillR.this, "Message1", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BillR.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);
                }
            }

        }else{
            if (mBluetoothAdapter != null)
            {
                mBluetoothAdapter.disable();
            }
            else{
                stat.setText("");
                stat.setText(R.string.invo_discon);
                stat.setTextColor(Color.rgb(199, 59, 59));
                mPrint.setEnabled(false);
                /*mDisc.setEnabled(false);
                mDisc.setBackgroundColor(Color.rgb(161, 161, 161));*/
                mPrint.setBackgroundColor(Color.rgb(161, 161, 161));
                mScan.setBackgroundColor(Color.rgb(0,0,0));
                mScan.setEnabled(true);
                mScan.setText(R.string.invo_disconnect);
            }
        }
    }

    public void shareEbill(View v){

        pd.setMessage(getString(R.string.bill_shareinvo));
        pd.show();

        LinearLayout savingLayout = findViewById(R.id.view);
        Bitmap bitmap =getBitmapFromView(savingLayout);
        try {
            File file = new File(this.getExternalCacheDir(),"BillReturn.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpg");
            startActivity(Intent.createChooser(intent, getString(R.string.bill_sharevia)));
            pd.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            getString(R.string.bill_connecting), mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                    //if (isFirstRun) {
                    SavePreferences("btDr", mBluetoothDevice.getName());
                    SavePreferences("btAr", mBluetoothDevice.getAddress());
                    getSharedPreferences("PREFERENCER", MODE_PRIVATE).edit()
                            .putBoolean("isFirstRun", false).apply();
                    //}
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(BillR.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(BillR.this, R.string.bill_btcanceled, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void SavePreferences(String key, String value){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
        }
    }
    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            stat.setText("");
            stat.setText("Connected");
            stat.setTextColor(Color.rgb(97, 170, 74));
            mPrint.setEnabled(true);
            mScan.setText("Disconnect");
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }
}
