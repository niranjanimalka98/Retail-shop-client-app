package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import static com.jkdesigns.stsproject.returnsR.datei;

public class MyAdapterReturns extends RecyclerView.Adapter<MyViewHolderTrans> implements Filterable {

    Context c;

    String uid;
    private int dutString;
    ArrayList<Modeltrans> models, filterList;
    DatabaseReference db, dbp, dut;
    private FirebaseAuth mAuth;
    CustomFilterReturns filter;

    public MyAdapterReturns(Context c, ArrayList<Modeltrans> models) {

        this.c = c;
        this.models = models;
        this.filterList = models;
    }

    @Override
    public MyViewHolderTrans onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.transcard, parent, false);
        return new MyViewHolderTrans(v);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolderTrans holder, @SuppressLint("RecyclerView") final int position) {

        holder.trans.setText("SN: "+models.get(position).getB01());
        holder.date.setText(models.get(position).getB04());
        holder.time.setText(models.get(position).getB10());
        holder.money.setText(models.get(position).getB07());
        holder.custname.setText(models.get(position).getB02());
        holder.payment.setText(models.get(position).getPay());

        if(models.get(position).getB11() != null){
            holder.discount.setVisibility(View.VISIBLE);
            holder.discount.setText(models.get(position).getB11());
        }else{
            holder.discount.setVisibility(View.INVISIBLE);
        }

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Return").child(datei).child("R")
                .child(models.get(position).getKey());
        dbp = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("returns").child(datei).child(models.get(position).getKey());
        db.keepSynced(true);

        final SharedPreferences rep = PreferenceManager
                .getDefaultSharedPreferences(c);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(c, BillR.class);
                i.putExtra("key", models.get(position).getKey());
                i.putExtra("date", datei);
                i.putExtra("rep", rep.getString("SRep", ""));
                c.startActivity(i);
            }
        });

        holder.delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String item = models.get(position).getKey();
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(c);
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete this return?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dataSnapshot.getRef().removeValue();
                                dialog.dismiss();
                                models.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, models.size());

                                // delete returns
                                dbp.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                    }
                                });

                                //get count
                                dut = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Return");
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

                                //update count
                                dut.getParent().child("Return").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        dut.getParent().child("Return").child("count")
                                                .setValue(String.valueOf(--dutString));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                    }
                                });

                                // log
                                db.getParent().getParent().getParent().child("Log").child("log")
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
                                                    db.getParent().getParent().getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime()) + "] " + item
                                                                    + " Return Deleted\n" + l);
                                                } else {
                                                    db.getParent().getParent().getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime()) + "] " + item
                                                                    + " Return Deleted\n\n" + l);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {
                                            }
                                        });

                                Toast.makeText(c, item + " has been removed succesfully.", Toast.LENGTH_SHORT).show();
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
            }

        });

    }

    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new CustomFilterReturns(filterList, this);
        }
        return filter;
    }
}
