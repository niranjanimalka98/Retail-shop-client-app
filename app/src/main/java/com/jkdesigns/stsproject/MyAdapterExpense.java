package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class MyAdapterExpense extends RecyclerView.Adapter<MyViewHolderCustomer> implements Filterable {

    Context c;
    int dutString;
    String uid;
    ArrayList<Modelexpense> models, filterList;
    DatabaseReference db,dbp,dut;
    private FirebaseAuth mAuth;
    CustomFilterExpense filter;

    public MyAdapterExpense(Context c, ArrayList<Modelexpense> models) {

        this.c = c;
        this.models = models;
        this.filterList = models;
    }

    @Override
    public MyViewHolderCustomer onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(c).inflate(R.layout.expensecard, parent, false);
        return new MyViewHolderCustomer(v);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolderCustomer holder, @SuppressLint("RecyclerView") final int position) {

        holder.item.setText(models.get(position).getP03());
        holder.date.setText(models.get(position).getP18());
        holder.money.setText(models.get(position).getB07());
        //holder.b3.setText(models.get(position).getP15());
        //holder.phone.setText(models.get(position).getKey());

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

                Intent i = new Intent(c, UpdateExpense.class);
                i.putExtra("Keys", models.get(position).getKey());
                c.startActivity(i);
            }
        });
        holder.log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(c, ExpenseLog.class);
                i.putExtra("k", models.get(position).getKey());
                c.startActivity(i);

            }
        });
        holder.delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String item = models.get(position).getP03();
                mAuth = FirebaseAuth.getInstance();
                uid = mAuth.getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Expense")
                        .child("E").child(models.get(position).getKey());
                dbp = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("expenses").child(models.get(position).getKey());
                //db.keepSynced(true);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(c);
                        alert.setTitle("Alert!!");
                        alert.setMessage("Are you sure to delete this expense?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dataSnapshot.getRef().removeValue();
                                dialog.dismiss();
                                models.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, models.size());

                                // delete customers
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
                                dut = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("tables").child("Expense");
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
                                dut.getParent().child("Expense").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        dut.getParent().child("Expense").child("count")
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
                                                                    + " Expense Deleted\n" + l);
                                                } else {
                                                    db.getParent().getParent().getParent().child("Log").child("log")
                                                            .setValue(format.format(calendar.getTime()) + "\n["
                                                                    + fm.format(calendar.getTime()) + "] " + item
                                                                    + " Expense Deleted\n\n" + l);
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
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item = models.get(position).getP03();
                mAuth = FirebaseAuth.getInstance();
                uid = mAuth.getCurrentUser().getUid();
                db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Expense")
                        .child("E").child(models.get(position).getKey());
                //db.keepSynced(true);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(c);
                        final EditText edittext = new EditText(c);
                        edittext.setFilters(new InputFilter[] { new DecimalDigitsInputFilter(2) });

                        GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                        };
                        final Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);

                        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                                | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        edittext.setMaxLines(1);
                        edittext.setHint("Enter amount");
                        // alert.setMessage("Enter amount :");
                        alert.setTitle("Expense Amount :");

                        alert.setView(edittext);

                        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // What ever you want to do with the value
                                final Editable value = edittext.getText();
                                if (!value.toString().trim().isEmpty()) {
                                    BigDecimal a = new BigDecimal(value.toString());
                                    BigDecimal due = BigDecimal.valueOf(Double.parseDouble(map.get("p14"))).subtract(a);//on update minus value

                                    db.child("p14").setValue(due.toString());

                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                    String formattedDate = df.format(c.getTime());
                                    db.child("p13").setValue(formattedDate);

                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                    db.child("log").setValue(map.get("log") + "[" + format.format(calendar.getTime())
                                            + "] (" + value.toString() + ") Amount:" + due.toString() + "\n");

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
                                                                        + fm.format(calendar.getTime()) + "] "
                                                                        + map.get("p03")
                                                                        + " Expense Amount update ("
                                                                        + value.toString() + ")\n" + l);
                                                    } else {
                                                        db.getParent().getParent().getParent().child("Log").child("log")
                                                                .setValue(format.format(calendar.getTime()) + "\n["
                                                                        + fm.format(calendar.getTime()) + "] "
                                                                        + map.get("p03")
                                                                        + " Expense Amount update ("
                                                                        + value.toString() + ")\n\n" + l);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {
                                                }
                                            });

                                    notifyDataSetChanged();
                                }

                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                                dialog.dismiss();
                            }
                        });
                        AlertDialog b = alert.create();
                        b.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        b.show();
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
            filter = new CustomFilterExpense(filterList, this);
        }
        return filter;
    }
}
