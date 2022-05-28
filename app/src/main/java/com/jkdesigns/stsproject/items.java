package com.jkdesigns.stsproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class items extends Fragment implements SearchView.OnQueryTextListener {
    View v;
    String uid;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    MyAdapter adapter;
    private RecyclerView rv;
    ArrayList<Model> models = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    boolean menuqty = false, menuexp = false;
    MenuItem orderN, orderQ, orderE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.m_items, container, false);
        getActivity().setTitle("Inventory");
        MainActivity.flag = 1;
        setHasOptionsMenu(true);

        // INITIALIZE RV
        rv = (RecyclerView) v.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Inventory");
        db.keepSynced(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Newitem.class);
                startActivity(i);
            }
        });
        MainActivity.fab.show();
        // ADAPTER
        adapter = new MyAdapter(getContext(), retrieve());

        rv.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_updateinfo).setVisible(false);
        menu.findItem(R.id.order_due).setVisible(false);
        menu.findItem(R.id.order_ename).setVisible(true);
        menu.findItem(R.id.order_exp).setVisible(true);
        menu.findItem(R.id.order_qty).setVisible(true);
        inflater.inflate(R.menu.search_bar, menu);
        final MenuItem searchitem = menu.findItem(R.id.search);
        orderQ = menu.findItem(R.id.order_qty);
        orderE = menu.findItem(R.id.order_exp);
        orderN = menu.findItem(R.id.order_ename);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchitem);
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(searchitem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.findItem(R.id.order_ename).setVisible(true);
                menu.findItem(R.id.order_exp).setVisible(true);
                menu.findItem(R.id.order_qty).setVisible(true);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.findItem(R.id.order_ename).setVisible(false);
                menu.findItem(R.id.order_exp).setVisible(false);
                menu.findItem(R.id.order_qty).setVisible(false);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.order_exp) {

            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Model>() {
                    DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

                    @Override
                    public int compare(final Model object1, final Model object2) {
                        try {
                            return f.parse(object1.getP05()).compareTo(f.parse(object2.getP05()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
            }
            menuqty = false;
            menuexp = true;
            orderE.setChecked(true);
            orderN.setChecked(false);
            orderQ.setChecked(false);
            adapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.order_qty) {

            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Model>() {
                    @Override
                    public int compare(Model p1, Model p2) {
                        return Integer.parseInt(p1.getP10()) - Integer.parseInt(p2.getP10());
                    }
                });
            }
            menuqty = true;
            menuexp = false;
            orderE.setChecked(false);
            orderN.setChecked(false);
            orderQ.setChecked(true);
            adapter.notifyDataSetChanged();
            return true;
        }

        if (id == R.id.order_ename) {

            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Model>() {
                    @Override
                    public int compare(final Model object1, final Model object2) {
                        return object1.getP03().compareTo(object2.getP03());
                    }
                });
            }
            menuqty = false;
            menuexp = false;
            orderE.setChecked(false);
            orderN.setChecked(true);
            orderQ.setChecked(false);
            adapter.notifyDataSetChanged();
            return true;
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
            Model model = ds.getValue(Model.class);
            models.add(model);
        }

        if (menuqty) {
            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Model>() {
                    @Override
                    public int compare(Model p1, Model p2) {
                        return Integer.parseInt(p1.getP10()) - Integer.parseInt(p2.getP10());
                    }
                });
            }
        } else if (menuexp) {
            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Model>() {
                    DateFormat f = new SimpleDateFormat("MM/yyyy");

                    @Override
                    public int compare(final Model object1, final Model object2) {
                        try {
                            return f.parse(object1.getP05()).compareTo(f.parse(object2.getP05()));
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
            }
        } else {
            if (models.size() > 0) {
                Collections.sort(models, new Comparator<Model>() {
                    @Override
                    public int compare(final Model object1, final Model object2) {
                        return object1.getP03().compareTo(object2.getP03());
                    }
                });
            }
        }
        adapter.notifyDataSetChanged();
    }

    // READ BY HOOKING ONTO DATABASE OPERATION CALLBACKS
    public ArrayList<Model> retrieve() {
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
}
