package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class expenses extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {
    View v;
    public static String datei;
    String uid;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    MyAdapterExpense adapter;
    private RecyclerView rv;
    ArrayList<Modelexpense> models = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    boolean order = false;
    public SharedPreferences dates;
    EditText p13;
    TextInputLayout date;
    private int mYear, mMonth, mDay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.customers, container, false);
        getActivity().setTitle("Expenses");
        MainActivity.flag = 1;
        setHasOptionsMenu(true);

        date = (TextInputLayout) v.findViewById(R.id.dateLayout);
        p13 = (EditText) v.findViewById(R.id.dateText);
        p13.setVisibility(View.VISIBLE);
        p13.setInputType(InputType.TYPE_NULL);
        p13.setFocusable(false);
        p13.setOnClickListener(this);

        dates = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        // INITIALIZE RV
        rv = (RecyclerView) v.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        //db.keepSynced(true);

        final Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        p13.setText(format.format(c.getTime()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Customer Fab", Snackbar.LENGTH_LONG)
                // .setAction("Action", null).show();
                Intent i = new Intent(getContext(), Newexpense.class);
                startActivity(i);
            }
        });
        MainActivity.fab.show();
        // ADAPTER
        datei = p13.getText().toString().replace("/", "").substring(2);
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Expense").child(datei);
        //db.keepSynced(true);

        adapter = new MyAdapterExpense(getContext(), retrieve());
        rv.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_updateinfo).setVisible(false);
        menu.findItem(R.id.order_ename).setVisible(false);
        menu.findItem(R.id.order_qty).setVisible(false);
        menu.findItem(R.id.order_exp).setVisible(false);
        // menu.findItem(R.id.order_transdate).setVisible(false);
        menu.findItem(R.id.order_due).setVisible(true);
        inflater.inflate(R.menu.search_bar, menu);
        final MenuItem searchitem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchitem);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(searchitem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.findItem(R.id.order_due).setVisible(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.findItem(R.id.order_due).setVisible(false);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.order_due) {
            if (!item.isChecked()) {
                if (models.size() > 0) {
                    Collections.sort(models, new Comparator<Modelexpense>() {
                        @Override
                        public int compare(Modelexpense p1, Modelexpense p2) {
                            return Double.compare(Double.parseDouble(p2.getP18()),
                                    Double.parseDouble(p1.getB07()));
                        }
                    });
                }
                order = true;
                item.setChecked(true);
                adapter.notifyDataSetChanged();
            } else {
                if (models.size() > 0) {
                    Collections.sort(models, new Comparator<Modelexpense>() {
                        @Override
                        public int compare(final Modelexpense object1, final Modelexpense object2) {
                            return object1.getP03().compareTo(object2.getP03());
                        }
                    });
                }
                order = false;
                item.setChecked(false);
                adapter.notifyDataSetChanged();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        adapter.getFilter().filter(newText);
        return false;
    }

    // IMPLEMENT FETCH DATA AND FILL ARRAYLIST
    private void fetchData(DataSnapshot dataSnapshot) {
        models.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Modelexpense model = ds.getValue(Modelexpense.class);
            models.add(model);
        }
        if (order) {
            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Modelexpense>() {
                    @Override
                    public int compare(Modelexpense p1, Modelexpense p2) {
                        return Double.compare(Double.parseDouble(p2.getP18()), Double.parseDouble(p1.getB07()));
                    }
                });
            }
        } else {
            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Modelexpense>() {
                    @Override
                    public int compare(final Modelexpense object1, final Modelexpense object2) {
                        return object1.getKey().compareTo(object2.getKey());
                    }
                });
            }
        }
        adapter.notifyDataSetChanged();
    }

    // READ BY HOOKING ONTO DATABASE OPERATION CALLBACKS
    public ArrayList<Modelexpense> retrieve() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (models.size() == 1) {
                    models.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return models;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
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
                dates.edit().putString("billdate", format.format(calendar.getTime())).apply();
                Recreate();
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Recreate() {
        //Toast.makeText(this.getActivity(), datei, Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
