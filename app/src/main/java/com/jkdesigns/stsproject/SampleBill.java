package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.UUID;

public class SampleBill extends AppCompatActivity implements Runnable{

    public SharedPreferences sharedPreferences;
    public String btD, btA;
    TextView ph1, ph2, storename, storeaddr, transid, dlnum, date, gst, ptname, b3, docname, totamt, name;
    private DatabaseReference du;
    private FirebaseAuth mAuth;
    String uid;

    ProgressDialog pd;
    Button mScan, mPrint;

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;

    TextView stat;
    private static OutputStream outputStream;
    boolean isFirstRun;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.invoice);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mPrint = (Button) findViewById(R.id.mPrint);
        mScan = (Button)findViewById(R.id.Scan);
        stat = (TextView)findViewById(R.id.bpstatus);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pd = new ProgressDialog(SampleBill.this);

        ph1 = (TextView) findViewById(R.id.ph1);
        ph2 = (TextView) findViewById(R.id.ph2);
        storename = (TextView) findViewById(R.id.storename);
        storeaddr = (TextView) findViewById(R.id.storeaddr);
        transid = (TextView) findViewById(R.id.transid);
        date = (TextView) findViewById(R.id.date);
        gst = (TextView) findViewById(R.id.gst);
        ptname = (TextView) findViewById(R.id.ptname);
        b3 = (TextView) findViewById(R.id.b3);
        docname = (TextView) findViewById(R.id.docname);
        totamt = (TextView) findViewById(R.id.totamt);
        name = (TextView) findViewById(R.id.name);

        mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
        //du.keepSynced(true);
        du.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                name.setText(map.get("i1"));
                storename.setText(map.get("i2"));
                storeaddr.setText(map.get("i4"));
                ph1.setText(map.get("i5"));
                ph2.setText(map.get("i6"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);


        sharedPreferences = getPreferences(MODE_PRIVATE);
        btD = sharedPreferences.getString("btD", "");
        btA = sharedPreferences.getString("btA", "");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter.isEnabled()) {
            if (!isFirstRun) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                Log.v(TAG, "Coming incoming address " + btA);
                mBluetoothDevice = mBluetoothAdapter
                        .getRemoteDevice(btA);
                mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                        "Connecting...", btD + " : "
                                + btA, true, true);
                Thread mBlutoothConnectThread = new Thread(this);
                mBlutoothConnectThread.start();
            }
        }
    }

    public void printEbill(View v){

        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();

                    if(mBluetoothSocket == null){
                        Toast.makeText(SampleBill.this, "Bluetooth Not Connected", Toast.LENGTH_SHORT).show();
                    }
                    else{
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

                            printCustom(new String(new char[61]).replace("\0", " "),0,1);
                            printCustom(storename.getText().toString(),1,1);
                            printCustom(ph1.getText().toString() + " | " + ph2.getText().toString(),0,1);
                            printCustom(new String(new char[61]).replace("\0", " "),0,1);
                            printCustom("Customer: " + ptname.getText().toString(),1,0);
                            printCustom(new String(new char[61]).replace("\0", " "),0,1);
                            printCustom("Rep: " +docname.getText().toString(),0,0);
                            printText(leftRightAlign(date.getText().toString() + " | " + gst.getText().toString(), " ID: " + transid.getText().toString()));
                            printCustom(new String(new char[61]).replace("\0", "-"),0,1);
                            printText(leftRightAlign("Qty: Products" , "Price"));
                            printCustom(new String(new char[61]).replace("\0", "-"),0,1);
                            printText(leftRightAlign("Total:" , totamt.getText().toString()));
                            printCustom(new String(new char[61]).replace("\0", "."),0,1);
                            printCustom("THANK YOU FOR THE BUSINESS WITH US.",0,1);
                            printCustom("CALL FOR THE NEXT ORDER.",0,1);
                            printCustom("POS System & App by: pos.jkdesigns.app",0,1);
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

        if(ans.length() < 63){
            int m = (63 - ans.length());
            ans = str1 + new String(new char[m]).replace("\0", " ") + str2;
            Log.d("m", String.valueOf(String.valueOf(m).length()));
        }

        //}
        return ans;
    }

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String[] dateTime = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ c.get(Calendar.MONTH) +"/"+ c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        return dateTime;
    }

    public void ConnectBt(View v){

        if(mScan.getText().equals("Connect"))
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(SampleBill.this, "Message1", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(SampleBill.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent,
                            REQUEST_CONNECT_DEVICE);

                }
            }

        }
        else if(mScan.getText().equals("Disconnect"))
        {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.disable();
            stat.setText("");
            stat.setText("Disconnected");
            stat.setTextColor(Color.rgb(199, 59, 59));
            mPrint.setEnabled(false);
            mScan.setEnabled(true);
            mScan.setText("Connect");
            SavePreferences("btD", "");
            SavePreferences("btA", "");
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
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

    public void connectDisconnect(View view)
    {
        if(mScan.getText().toString().equals("Connect"))
        {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(SampleBill.this, "Message1", Toast.LENGTH_SHORT).show();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent,
                            REQUEST_ENABLE_BT);
                } else {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(SampleBill.this,
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
                stat.setText("Disconnected");
                stat.setTextColor(Color.rgb(199, 59, 59));
                mPrint.setEnabled(false);
                /*mDisc.setEnabled(false);
                mDisc.setBackgroundColor(Color.rgb(161, 161, 161));*/
                mPrint.setBackgroundColor(Color.rgb(161, 161, 161));
                mScan.setBackgroundColor(Color.rgb(0,0,0));
                mScan.setEnabled(true);
                mScan.setText("Disconnect");
            }
        }

    }

    @SuppressLint("SetWorldReadable")
    public void shareEbill(View v){

        pd.setMessage("Sharing Invoice");
        pd.show();

        LinearLayout savingLayout = findViewById(R.id.view);
        Bitmap bitmap =getBitmapFromView(savingLayout);
        try {
            File file = new File(this.getExternalCacheDir(),"logicchip.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpg");
            startActivity(Intent.createChooser(intent, "Share invoice via"));
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
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    //pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                    if (isFirstRun) {
                        SavePreferences("btD", mBluetoothDevice.getName());
                        SavePreferences("btA", mBluetoothDevice.getAddress());
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                .putBoolean("isFirstRun", false).apply();
                    }

                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(SampleBill.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(SampleBill.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void SavePreferences(String key, String value){
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

            // Snackbar snackbar = Snackbar.make(layout, "Bluetooth Printer is Connected!", Snackbar.LENGTH_LONG);
            // snackbar.show();
            stat.setText("");
            stat.setText("Connected");
            stat.setTextColor(Color.rgb(97, 170, 74));
            mPrint.setEnabled(true);
            mScan.setText("Disconnect");
            //mDisc.setEnabled(true);
            //mDisc.setBackgroundColor(Color.rgb(0, 0, 0));
            //mScan.setEnabled(false);
            //mScan.setBackgroundColor(Color.rgb(161, 161, 161));
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
