package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static com.jkdesigns.stsproject.MainActivity.fab;

public class summary extends Fragment implements View.OnClickListener, Runnable {
    View v;
    Button sample, log, psum, pinv, mScan, mPrint;
    private DatabaseReference du;
    String btD, btA, s, daycash, expense, z03, z05, r;
    private FirebaseAuth mAuth;
    TextView t, load, mon, day;
    String uid, mformat, dis, path, dateformat, sumtitle;
    TextInputLayout date;
    private int mYear, mMonth, mDay;
    EditText p13;
    //SharedPreferences dates;
    TextView stat;
    public SharedPreferences sharedPreferences, dates;

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    private static OutputStream outputStream;
    int kc = -1;
    public boolean isFirstRun;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.summary, container, false);
        Objects.requireNonNull(getActivity()).setTitle("Summary");
        MainActivity.flag = 1;
        sample = (Button) v.findViewById(R.id.sample);
        log = (Button) v.findViewById(R.id.log);
        psum = (Button) v.findViewById(R.id.psum);
        psum.setText("Load Summary");
        pinv = (Button) v.findViewById(R.id.pinv);
        pinv.setText("Load Inventory");
        date = (TextInputLayout) v.findViewById(R.id.dateLayout);
        p13 = (EditText) v.findViewById(R.id.dateText);
        p13.setInputType(InputType.TYPE_NULL);
        p13.setFocusable(false);
        p13.setOnClickListener(this);
        stat = (TextView) v.findViewById(R.id.bpstatus);
        mScan = (Button) v.findViewById(R.id.Scan);
        mPrint = (Button) v.findViewById(R.id.mPrint);
        mon = (TextView) v.findViewById(R.id.mon);
        day = (TextView) v.findViewById(R.id.day);
        day.setBackgroundColor(Color.parseColor("#EC700B"));

        dates = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        isFirstRun = this.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        sharedPreferences = this.getActivity().getPreferences(MODE_PRIVATE);
        btD = sharedPreferences.getString("btD", "");
        btA = sharedPreferences.getString("btA", "");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        psum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                psum(v);
            }
        });
        pinv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinv(v);
            }
        });
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectBt(v);
            }
        });
        mPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print(v);
            }
        });
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runday(v);
            }
        });
        mon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runmon(v);
            }
        });
        final Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat fmmonth = new SimpleDateFormat("MMyyyy");
        p13.setText(format.format(c.getTime()));
        //dates.edit().putString("cashdate", format.format(c.getTime())).apply();
        //Recreate();

        mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        //du.keepSynced(true);

        t = (TextView) v.findViewById(R.id.title);
        load = (TextView) v.findViewById(R.id.load);

        /*if(mBluetoothAdapter.isEnabled()) {
            if (!isFirstRun) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                android.util.Log.v(TAG, "Coming incoming address " + btA);
                mBluetoothDevice = mBluetoothAdapter
                        .getRemoteDevice(btA);
                mBluetoothConnectProgressDialog = ProgressDialog.show(getActivity(),
                        "Connecting...", btD + " : "
                                + btA, true, true);
                Thread mBlutoothConnectThread = new Thread(this);
                mBlutoothConnectThread.start();
            }
        }*/
        pd = new ProgressDialog(getActivity());

        return v;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClick(View v) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                p13.setText(format.format(calendar.getTime()));
                dates.edit().putString("cashdate", format.format(calendar.getTime())).apply();
                Recreate();
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Recreate() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.hide();
        sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), SampleBill.class);
                startActivity(i);
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Log.class);
                startActivity(i);
            }
        });
        du.child("zinfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                t.setText(map.get("i02"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        dateformat = p13.getText().toString().replace("/", "");
        mformat = dateformat.substring(2);
        path = "d/" + mformat + "/" + dateformat;
        sumtitle = p13.getText().toString();
        runsum();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runday(View v){
        path = "d/" + mformat + "/" + dateformat;
        day.setBackgroundColor(Color.parseColor("#EC700B"));
        mon.setBackgroundColor(Color.parseColor("#ECBF9A"));
        load.setText("Click Load...");
        psum.setText("Load Summary");
        pinv.setText("Load Inventory");
        sumtitle = p13.getText().toString();
        runsum();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void runmon(View v){
        path = "m/" + mformat;
        day.setBackgroundColor(Color.parseColor("#ECBF9A"));
        mon.setBackgroundColor(Color.parseColor("#EC700B"));
        load.setText("Click Load...");
        psum.setText("Load Summary");
        pinv.setText("Load Inventory");
        sumtitle = p13.getText().toString().substring(3);
        runsum();
    }

    public void runsum(){
        du.child("Summary").child(path).child("01").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DecimalFormat df = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.ENGLISH));
                String rs = "Rs. ";

                if (dataSnapshot.hasChild("z03")) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };

                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                    ((TextView) (v.findViewById(R.id.incash))).setText(rs + df.format((double) Double.valueOf(map.get("z00"))));
                    ((TextView) (v.findViewById(R.id.daycash))).setText(rs + df.format((double) Double.valueOf(map.get("z01"))));
                    daycash = map.get("z01");
                    ((TextView) (v.findViewById(R.id.cash))).setText(rs + df.format((double) Double.valueOf(map.get("z03"))));
                    ((TextView) (v.findViewById(R.id.rtodayamt))).setText(rs + df.format((double) Double.valueOf(map.get("z02"))));
                    ((TextView) (v.findViewById(R.id.dis))).setText(rs + df.format((double) Double.valueOf(map.get("z10"))));
                }else{
                    ((TextView) (v.findViewById(R.id.cash))).setText("START THE DAY");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        du.child("Summary").child(path).child("00").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DecimalFormat df = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.ENGLISH));
                String rs = "Rs. ";

                if (dataSnapshot.hasChild("z00")) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                    ((TextView) (v.findViewById(R.id.tiv))).setText(rs + df.format((double) Double.valueOf(map.get("z01"))));

                    ((TextView) (v.findViewById(R.id.todayamt))).setText(rs + df.format((double) Double.valueOf(map.get("z02"))));
                    ((TextView) (v.findViewById(R.id.todaycount))).setText(map.get("z03"));
                    ((TextView) (v.findViewById(R.id.mcount))).setText(map.get("z06"));
                    ((TextView) (v.findViewById(R.id.mamt))).setText(rs + df.format((double) Double.valueOf(map.get("z05"))));
                    ((TextView) (v.findViewById(R.id.rtodaycount))).setText(map.get("z08"));
                    ((TextView) (v.findViewById(R.id.tivend))).setText(rs + df.format((double) Double.valueOf(map.get("z09"))));
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void collectPhoneNumbers(Map<String,Object> users) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();

            String d = (String) singleUser.get("z00");
            String i = (String) singleUser.get("z01");
            s = (String) singleUser.get("z02");
            z03 = (String) singleUser.get("z03");
            r = (String) singleUser.get("z04");
            z05 = (String) singleUser.get("z05");
            String gr = (String) singleUser.get("z12");

            if (z03 == null || s == null ){
                z03 = "0.00";
                s = "0";
            }
            if (z05 == null || r == null ){
                z05 = "0.00";
                r = "0";
            }
            if (!Objects.requireNonNull(s).equals("0") || !Objects.requireNonNull(r).equals("0")) { //|| gr != null || !Objects.requireNonNull(d).contains("/") || !Objects.requireNonNull(i).matches(".*[a-zA-Z]+.*")

                if(Objects.requireNonNull(i).equals("Expense")){
                    //noting
                    String no = "";
                }else if(Objects.requireNonNull(d).contains("/")) {
                    //noting
                    String no = "";
                }else if(!Objects.requireNonNull(i).matches(".*[a-zA-Z]+.*")) {

                    String di = "Discounts: ";
                    int incad = 43 - (di.length() + ((String) Objects.requireNonNull(singleUser.get("z10"))).length());
                    dis = di + new String(new char[incad]).replace("\0", " ") + (String) singleUser.get("z10") + "\n\n";

                    String incash = "Initial Cash: ";
                    int inca = 43 - (incash.length() + ((String) Objects.requireNonNull(singleUser.get("z00"))).length());
                    String in1 = incash + new String(new char[inca]).replace("\0", " ") + (String) singleUser.get("z00");

                    String scash = "Sales Cash: ";
                    int sca = 43 - (scash.length() + ((String) Objects.requireNonNull(singleUser.get("z01"))).length());
                    String s1 = scash + new String(new char[sca]).replace("\0", " ") + (String) singleUser.get("z01");

                    String ecash = "Expenses: ";
                    int eca = 43 - (ecash.length() + s.length());
                    String e1 = ecash + new String(new char[eca]).replace("\0", " ") + s;

                    String tcash = "Total Cash: ";
                    int tca = 43 - (tcash.length() + z03.length());
                    String t1 = tcash + new String(new char[tca]).replace("\0", " ") + z03;

                    expense = in1 + "\n" + s1 + "\n" + e1 + "\n" + t1 + "\n\n";
                }else {
                    if(!Objects.requireNonNull(s).equals("")) {
                        phoneNumbers.add((String) singleUser.get("z01") + "\n");

                        String charac = " *Sales: " + singleUser.get("z00") + " x " + s;
                        int cha = 43 - (charac.length() + Objects.requireNonNull(z03).length());

                        if (cha > 2) {
                            phoneNumbers.add(charac + new String(new char[cha]).replace("\0", " ") + z03 + "\n");
                        } else {
                            int index = charac.lastIndexOf(" ");
                            String add1 = charac.substring(0, index);
                            String add2 = charac.substring(index + 1);
                            int cha1 = 43 - (add1.length() + Objects.requireNonNull(z03).length());

                            phoneNumbers.add(add1 + new String(new char[cha1]).replace("\0", " ") + z03 + "\n");
                            phoneNumbers.add(add2 + "\n");

                        }

                        if (!r.equals("0")) {
                            String rcharac = " *Returns: " + singleUser.get("z00") + " x " + r;
                            int rcha = 43 - (rcharac.length() + Objects.requireNonNull(z05).length());

                            if (rcha > 2) {
                                phoneNumbers.add(rcharac + new String(new char[rcha]).replace("\0", " ") + z05 + "\n");
                            } else {
                                int index = rcharac.lastIndexOf(" ");
                                String add1 = rcharac.substring(0, index);
                                String add2 = rcharac.substring(index + 1);
                                int cha1 = 43 - (add1.length() + Objects.requireNonNull(z05).length());

                                phoneNumbers.add(add1 + new String(new char[cha1]).replace("\0", " ") + z05 + "\n");
                                phoneNumbers.add(add2 + "\n");
                            }
                        }
                        if (gr != null) {
                            phoneNumbers.add(" *Stock: " + (String) singleUser.get("z06") + " - " + s + " + " + r + " + " + (String) singleUser.get("z12") + " = " + (String) singleUser.get("z07") + " ||" + "\n\n");
                        } else {
                            phoneNumbers.add(" *Stock: " + (String) singleUser.get("z06") + " - " + s + " + " + r + " = " + (String) singleUser.get("z07") + " ||" + "\n\n");
                        }
                    }
                }
            }

            String comma = phoneNumbers.toString().replace(",", "");
            String bracee = comma.replace("]", "");
            String brace = bracee.replace("[", "");
            String rload = "Summary of " + sumtitle + "\n\n" + brace + dis + expense ;
            load.setText(rload);
            psum.setText("Close Summary");
        }
    }

    public void psum(final View v){

        if(daycash == null) {
            load.setText("No Summary!");
            pinv.setText("Load Inventory");
        }else if(psum.getText().equals("Close Summary")){
            load.setText("Click Load...");
            psum.setText("Load Summary");
        }else{
            pinv.setText("Load Inventory");
            //get all child
            //Get datasnapshot at your "users" root node
            du.child("Summary").child(path).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                //Get map of users in datasnapshot
                                collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                            }else{
                                load.setText("No Summary!");
                                pinv.setText("Load Inventory");
                                psum.setText("Load Summary");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }
    }

    private void collectItems(Map<String,Object> users) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            //phoneNumbers.add("SKU: "+(String) singleUser.get("p01")+"\n");
            //phoneNumbers.add("Category: "+(String) singleUser.get("p02")+"\n");
            phoneNumbers.add(""+(String) singleUser.get("p03")+"\n");
            //phoneNumbers.add("Brand: "+(String) singleUser.get("p04")+"\n");
            //phoneNumbers.add("Cost: "+(String) singleUser.get("p06")+" // Unit: "+ singleUser.get("p7")+" // Cost Profit: "+ singleUser.get("p7")+"\n");
            //phoneNumbers.add("Unit: "+(String) singleUser.get("p07")+"\n");
            //phoneNumbers.add("Cost Profit: "+(String) singleUser.get("p07")+"\n");
            phoneNumbers.add(/*"Profit: "+(String) singleUser.get("p11")+*/"Price: "+ singleUser.get("p08")+"\n");
            //phoneNumbers.add("Price: "+(String) singleUser.get("p08")+"\n");
            //phoneNumbers.add("T.Stock: "+(String) singleUser.get("p19")+" => T.Value: "+ singleUser.get("p20")+"\n");
            //phoneNumbers.add("T.Value: "+(String) singleUser.get("p20")+"\n");
            phoneNumbers.add("C.Stock: "+(String) singleUser.get("p10")+" => C.Value: "+(String) singleUser.get("p17")+"\n\n");
            //phoneNumbers.add("C.Value: "+(String) singleUser.get("p17")+"\n\n");
        }
        String comma = phoneNumbers.toString().replace(",","");
        String bracee = comma.replace("]","");
        String brace = bracee.replace("[","");
        load.setText("INVENTORY"+"\n\n"+brace);
        //System.out.println(phoneNumbers.toString());
        pinv.setText("Close Inventory");
    }

    public void pinv(View v){

        if(pinv.getText().equals("Close Inventory")){
            load.setText("Click Load...");
            pinv.setText("Load Inventory");
        }else {
            psum.setText("Load Summary");
            //get all child
            //Get datasnapshot at your "users" root node
            du.child("Inventory").child("Items").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            //Get map of users in datasnapshot
                            collectItems((Map<String, Object>) dataSnapshot.getValue());
                        }else{
                            load.setText("No Inventory!");
                            pinv.setText("Load Inventory");
                            psum.setText("Load Summary");
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        }
    }

    public void print(View v){
        Toast.makeText(getActivity(), "Printing...", Toast.LENGTH_SHORT).show();
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();

                    if(mBluetoothSocket == null){
                        //Intent BTIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                        //this.startActivityForResult(BTIntent, DeviceListActivity.REQUEST_CONNECT_BT);
                        Toast.makeText(getActivity(), R.string.bill_bnotconnect, Toast.LENGTH_SHORT).show();
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

                            String sn = load.getText().toString(); //+" ("+brno+")"
                            printCustom(sn,1,0);
                            printCustom(new String(new char[63]).replace("\0", " "),0,1);

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
                    android.util.Log.e("PrintActivity", "Exe ", e);
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

    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
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

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void ConnectBt(View v){

        if(mScan.getText().equals(getString(R.string.invo_connect)))
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getActivity(), R.string.invo_nobadapter, Toast.LENGTH_SHORT).show();
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
            SavePreferences("btD", "");
            SavePreferences("btA", "");
            Objects.requireNonNull(this.getActivity()).getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", true).apply();
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

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            android.util.Log.e("Tag", "Exe ", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = Objects.requireNonNull(mExtra).getString("DeviceAddress");
                    android.util.Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(getActivity(),
                            getString(R.string.bill_connecting), mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                    //if (isFirstRun) {
                    SavePreferences("btD", mBluetoothDevice.getName());
                    SavePreferences("btA", mBluetoothDevice.getAddress());
                    Objects.requireNonNull(this.getActivity()).getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("isFirstRun", false).apply();
                    //}
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(getActivity(),
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(getActivity(), R.string.bill_btcanceled, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void SavePreferences(String key, String value){
        SharedPreferences sharedPreferences = Objects.requireNonNull(this.getActivity()).getPreferences(MODE_PRIVATE);
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
            android.util.Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
        }
    }
    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            android.util.Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            android.util.Log.d(TAG, "CouldNotCloseSocket");
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            stat.setText("");
            stat.setText(R.string.invo_pconnected);
            stat.setTextColor(Color.rgb(97, 170, 74));
            mPrint.setEnabled(true);
            mScan.setText(R.string.invo_disconnect);
        }
    };

}
