package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.jkdesigns.stsproject.MainActivity.fab;

import android.widget.Toast;

public class home extends Fragment {
    View v;
    ImageView i;
    Animation rotate;
    private DatabaseReference du, dupush;
    String s;
    private FirebaseAuth mAuth;
    String uid, id, idget;
    public boolean isFirstRun;
    @SuppressLint("StaticFieldLeak")
    static Button reg;
    int dutString, keypush;
    SharedPreferences Rep;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home, container, false);
        getActivity().setTitle(getString(R.string.home_title));

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        dupush = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Device");
        //du.keepSynced(true);
        MainActivity.flag = 0;
        return v;
    }

    public void regdevice(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        alert.setTitle("Register Device");
        alert.setMessage("Click yes to register this device");
        alert.setCancelable(false);
        alert.setPositiveButton(R.string.trans_yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                final String dv = Build.VERSION.RELEASE;
                final String model = Build.MODEL;
                final String dweb = "[{\"name\":\"de01\",\"value\":\""+id+"\"}," + //
                        "{\"name\":\"de02\",\"value\":\""+model+"\"}," +
                        "{\"name\":\"de03\",\"value\":\""+dv+"\"}," +
                        "{\"name\":\"de04\",\"value\":\""+format.format(calendar.getTime())+"\"}]";
                du.getParent().child(uid).child("Device").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        du.getParent().child(uid).child("Device").child(id).child("de01").setValue(id);
                        du.getParent().child(uid).child("Device").child(id).child("de02").setValue(model);
                        du.getParent().child(uid).child("Device").child(id).child("de03").setValue(dv);
                        du.getParent().child(uid).child("Device").child(id).child("de04").setValue(String.valueOf(format.format(calendar.getTime())));
                        du.getParent().child(uid).child("devices").child(id).setValue(dweb);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
                // set count
                dupush.getParent().child("Device").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        dupush.getParent().child("Device").child("count")
                                .setValue(String.valueOf(++dutString));
                        dupush.getParent().child("Device").child("id")
                                .setValue(String.valueOf(++keypush));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
                reg.setVisibility(View.GONE);
                getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", false).apply();
            }
        });
        alert.setNegativeButton(R.string.trans_no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                reg.setVisibility(View.VISIBLE);
                /*getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                        .putBoolean("isFirstRun", true).apply();*/
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        Rep = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        // get count
        dupush = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Device");
        dupush.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        reg = (Button) v.findViewById(R.id.regbtn);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                regdevice(v);
            }
        });
        id = Build.ID.replace(".", "");
        isFirstRun = this.getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        du.child("Device").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("de01").exists()){
                    regdevice(v);
                    Toast.makeText(getActivity(), "Device Changed", Toast.LENGTH_SHORT).show();

                }else if(dataSnapshot.child("de01").exists()) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                    idget = (map.get("de01"));
                    if(dataSnapshot.child("de05").exists()) {
                        String de5 = (map.get("de05"));
                        Rep.edit().putString("SRep", de5).apply();
                    }else{
                        Rep.edit().putString("SRep", "").apply();
                        //Toast.makeText(getActivity(), "SalesRep not Paired", Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(getActivity(), "Device Paired", Toast.LENGTH_SHORT).show();
                    /*getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("isFirstRun", false).apply();*/
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        du.child("zinfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                TextView tv = (TextView) v.findViewById(R.id.hometext);
                TextView ad = (TextView) v.findViewById(R.id.address);

                String current = Locale.getDefault().getDisplayLanguage();
                if (current.toLowerCase().contains("en")) {
                    tv.setText(map.get("i02"));
                    ad.setText(map.get("i04"));
                }else{
                    tv.setText(map.get("i02t"));
                    ad.setText(map.get("i04t"));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        fab.hide();
    }

}
