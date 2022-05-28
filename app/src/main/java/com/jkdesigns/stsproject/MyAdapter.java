package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Locale;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements Filterable {

    Context c;

    String uid, item;
    ArrayList<Model> models, filterList;
    DatabaseReference db,dbp,dut;
    private FirebaseAuth mAuth;
    CustomFilter filter;
    int dutString;

    public MyAdapter(Context c, ArrayList<Model> models) {

        this.c = c;
        this.models = models;
        this.filterList = models;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        String current = Locale.getDefault().getDisplayLanguage();
        //if (current.toLowerCase().contains("en")) {}
        if (current.toLowerCase().contains("en")) {
            holder.item.setText(models.get(position).getP03());
        }else{
            holder.item.setText(models.get(position).getP03t());
        }

        holder.btc.setText(models.get(position).getP02());
        holder.exp.setText(models.get(position).getP05());
        holder.mrp.setText(models.get(position).getP07());
        holder.qty.setText(models.get(position).getP10());
        holder.sp.setText(models.get(position).getP08());
        holder.cName.setText(models.get(position).getP04());
        holder.code.setText(models.get(position).getP01());
        holder.cp.setText(models.get(position).getP06());
        holder.desc.setText(models.get(position).getP11());
        holder.unit.setText(models.get(position).getP09());

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.gone.getVisibility() == View.VISIBLE) {
                    holder.gone.setVisibility(View.GONE);
                } else {
                    holder.gone.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(c, UpdateItem.class);
                i.putExtra("Keys", models.get(position).getP01());
                c.startActivity(i);
            }
        });

        holder.delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String current = Locale.getDefault().getDisplayLanguage();
                //if (current.toLowerCase().contains("en")) {}

                if (current.toLowerCase().contains("en")) {
                    item = models.get(position).getP03();
                }else{
                    item = models.get(position).getP03t();
                }
                mAuth = FirebaseAuth.getInstance();
                uid = mAuth.getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Inventory")
                        .child("Items").child(models.get(position).getP01());
                dbp = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("products")
                        .child(models.get(position).getP01());
                db.keepSynced(true);
                dbp.keepSynced(true);

                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(c);
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete this item?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dataSnapshot.getRef().removeValue();
                                dialog.dismiss();
                                models.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, models.size());

                                // delete products
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
                                dut = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Inventory");
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
                                dut.getParent().child("Inventory").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        dut.getParent().child("Inventory").child("count")
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
                                                                    + " Item Deleted\n" + l);
                                                } else {
                                                    db.getParent().getParent().getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime()) + "] " + item
                                                                    + " Item Deleted\n\n" + l);
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
            filter = new CustomFilter(filterList, this);
        }
        return filter;
    }
}
