package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.FragmentManager;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    View v;
    private GoogleApiClient mGoogleApiClient;
    TextView navhead, navsub, navvcode;
    private DatabaseReference du, lan, lansave;
    private FirebaseAuth mAuth;
    public static String sname, sstorename = "", mlang;
    String uid;
    View headerView;
    public static int flag = 0;
    SharedPreferences prefs;
    @SuppressLint("StaticFieldLeak")
    public static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerView = navigationView.getHeaderView(0);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        du = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        lan = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        lansave = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("zinfo");
        //du.keepSynced(true);
        //loadLocale();1

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new home()).commit();

        //sharedPref = getPreferences(Context.MODE_PRIVATE);
        prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        LocaleUtils.updateConfig(this,prefs.getString("My_Lang", ""));

        du.child("Inventory").child("Items").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            //Get map of users in datasnapshot
                            collectItems((Map<String, Object>) dataSnapshot.getValue());
                        }else{
                            //TODO
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        du.child("zinfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("i01").exists()) {
                    Intent i = new Intent(MainActivity.this, Register.class);
                    finish();
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        du.child("zinfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                sname = map.get("i01");
                sstorename = map.get("i02");
                navhead = (TextView) headerView.findViewById(R.id.navhead);
                navsub = (TextView) headerView.findViewById(R.id.navsub);
                navvcode = (TextView) headerView.findViewById(R.id.navvcode);
                navhead.setText(sstorename);
                navsub.setText(sname);
                navvcode.setText("v"+BuildConfig.VERSION_NAME);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        lan.child("zinfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                mlang = map.get("mlan");
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void collectItems(Map<String,Object> users) {

        ArrayList<String> phoneNumbers = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((String) singleUser.get("p01")+".jpeg");
            //phoneNumbers.add((String) singleUser.get("p03"));
        }
        String p01r = phoneNumbers.toString();//.replaceAll("[\\[\\]]","");
        AsyncTaskExample asyncTask=new AsyncTaskExample();
        asyncTask.execute(phoneNumbers);
        Toast.makeText(MainActivity.this,p01r, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (flag == 0) {
            super.onBackPressed();
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to exit?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.show();*/
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new home()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.order_ename).setVisible(false);
        menu.findItem(R.id.order_qty).setVisible(false);
        menu.findItem(R.id.order_exp).setVisible(false);
        menu.findItem(R.id.order_due).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_updateinfo) {
            Intent i = new Intent(this, UpdateInfo.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_logout) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(i);
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_inventory) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new items()).commit();
        } else if (id == R.id.nav_home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new home()).commit();
        } else if (id == R.id.nav_transaction) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new transactions()).commit();
        } else if (id == R.id.nav_return) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new returnsR()).commit();
        } else if (id == R.id.nav_customers) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new customers()).commit();
        } else if (id == R.id.nav_reps) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new salesreps()).commit();
        } else if (id == R.id.nav_summary) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new summary()).commit();
        } else if (id == R.id.nav_expense) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new expenses()).commit();
        } else if (id == R.id.en) {
            lansave.getParent().child("zinfo").child("mlan").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    lansave.getParent().child("zinfo").child("mlan")
                            .setValue("en");
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
            String lang="en";//pass your language here
            /*SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putString("lang", lang);
                    editor.putBoolean("langSelected", true);
            editor.apply();*/
            prefs.edit().putString("My_Lang", lang).apply();
            LocaleUtils.updateConfig(this,lang);
            Intent intent = this.getIntent();
            this.overridePendingTransition(0, 0);
            this.finish();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.overridePendingTransition(0, 0);
            this.startActivity(intent);
            //setLocale("en");
            recreate();
        } else if (id == R.id.ta) {
            lansave.getParent().child("zinfo").child("mlan").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    lansave.getParent().child("zinfo").child("mlan")
                            .setValue("ta");
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
            String lang="ta";//pass your language here
            /*SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.putString("lang", lang);
            editor.putBoolean("langSelected", true);
            editor.apply();*/
            prefs.edit().putString("My_Lang", lang).apply();
            LocaleUtils.updateConfig(this,lang);
            Intent intent = this.getIntent();
            this.overridePendingTransition(0, 0);
            this.finish();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.overridePendingTransition(0, 0);
            this.startActivity(intent);
            //setLocale("ta");
            recreate();
        } else if (id == R.id.nav_dev) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(this, SampleBill.class);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setLocale(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //sharedpreferences
        //SharedPreferences.Editor editor = getSharedPreferences("langs",MODE_PRIVATE).edit();
        //editor.putString("My_Lang", lang);
        //editor.apply();
    }

    public void loadLocale(){
        /*lan.child("zinfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                mlang = map.get("mlan");
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
        SharedPreferences prefs = getSharedPreferences("langs", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);*/
    }

    public void homebtn(View view) {
        int id = view.getId();
        if (R.id.btnnewt == id) {
            Intent i = new Intent(this, Trans.class);
            startActivity(i);

        } else if (R.id.btncust == id) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new customers()).commit();

        } else if (R.id.btntrans == id) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new transactions()).commit();

        } /*else if (R.id.btninventory == id) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new items()).commit();

        } */else if (R.id.btnreturn == id) {
            Intent r = new Intent(this, Returns.class);
            startActivity(r);

        } else if (R.id.btnreturns == id) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new returnsR()).commit();

        }else if (R.id.btnreps == id) {
            Intent nc = new Intent(this, Newcustomer.class);
            startActivity(nc);
        }
    }

    public void logout(View view) {
        signOut();
    }

    public void updatei(View view) {
        Intent i = new Intent(this, UpdateInfo.class);
        startActivity(i);
    }
}
