package com.jkdesigns.stsproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference du;
    Animation fadein, rotate;
    ImageView itext, ilogo;
    private AdView mAdView;
    LinearLayout loginep;
    //new
    String uid, versiononline, urlonline;
    public static final int PERMISSION_READ_WRITE_REQUEST_CODE = 34;
    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        MobileAds.initialize(this, String.valueOf(R.string.app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        loginep = (LinearLayout) findViewById(R.id.loginep);
        //GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
        //        .requestIdToken(getString(R.string.default_web_client_id)).build();
        //mGoogleApiClient = new GoogleApiClient.Builder(this)
        //        .enableAutoManage(this /* FragmentActivity */, null /* OnConnectionFailedListener */)
        //        .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        //findViewById(R.id.sign_in_button).setOnClickListener(this);

        requestPermission();
        new JsonTask().execute();

        mAuth = FirebaseAuth.getInstance();
        initializeUI();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserExist1();
            }
        });

        du = FirebaseDatabase.getInstance().getReference().child("Users");
        du.keepSynced(true);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);
        if (mAuth.getCurrentUser() != null) {
            loginep.setVisibility(View.INVISIBLE);
        }else {
            loginep.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<Void, Void, JSONObject> {

        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(getApplicationContext(), "running", Toast.LENGTH_LONG).show();
        }

        protected JSONObject doInBackground(Void... params) {

            String str= "";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(str);
                urlConn = url.openConnection();

                if(urlConn != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                    StringBuilder stringBuffer = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line);
                    }

                    return new JSONObject(stringBuffer.toString());
                }
            }
            catch(Exception ex)
            {
                android.util.Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response)
        {
            if(response != null)
            {
                try {
                    android.util.Log.e("App", "Success: " + response.getString("latestVersion") );
                    versiononline = response.getString("latestVersion");
                    urlonline = response.getString("url");
                    //Toast.makeText(getApplicationContext(), response.getString("latestVersion") + response.getString("url"), Toast.LENGTH_LONG).show();

                    int versionCode = BuildConfig.VERSION_CODE;
                    String versionName = BuildConfig.VERSION_NAME;

                    if(versiononline != null) {
                        if (Integer.parseInt(versiononline) > versionCode) {
                            UpdateApp atualizaApp = new UpdateApp();
                            atualizaApp.setContext(getApplicationContext());
                            atualizaApp.execute(urlonline);
                        }else{
                            //Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        //Toast.makeText(getApplicationContext(), "Update Null", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException ex) {
                    android.util.Log.e("App", "Failure", ex);
                    //Toast.makeText(getApplicationContext(), (CharSequence) ex, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                        Manifest.permission.READ_EXTERNAL_STORAGE} ,
                PERMISSION_READ_WRITE_REQUEST_CODE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_WRITE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //yeee permission granted
            }
        }
    }

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserExist1();
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void checkUserExist1() {
        if (mAuth.getCurrentUser() != null) {
            final String uid = mAuth.getCurrentUser().getUid();
            du.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(uid)) {
                        updateUI(true);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Intent i = new Intent(Login.this, Register.class);
                        finish();
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else {
            loginUserAccount();
        }
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.email);
        passwordTV = findViewById(R.id.password);

        loginBtn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onStart() {
        super.onStart();
        itext = (ImageView) findViewById(R.id.imageView3);
        ilogo = (ImageView) findViewById(R.id.imageView2);
        fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadein);
        rotate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        itext.startAnimation(fadein);
        ilogo.startAnimation(rotate);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
        case R.id.sign_in_button: {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null)
                checkUserExist();
            else
                signIn();
            break;
        }
        }*/
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(Login.this, "SignIn failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
        } else {
            Toast.makeText(Login.this, "handleSignInResult failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUI(boolean ans) {
        if (ans) {
            Intent i = new Intent(this, MainActivity.class);
            finish();
            startActivity(i);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                } else
                    checkUserExist();
            }
        });
    }

    private void checkUserExist() {
        if (mAuth.getCurrentUser() != null) {
            final String uid = mAuth.getCurrentUser().getUid();
            du.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(uid)) {
                        updateUI(true);
                    } else {
                        Intent i = new Intent(Login.this, Register.class);
                        finish();
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

}
