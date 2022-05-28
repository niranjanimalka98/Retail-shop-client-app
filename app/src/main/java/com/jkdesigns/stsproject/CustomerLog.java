package com.jkdesigns.stsproject;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class CustomerLog extends AppCompatActivity {

    String uid;
    DatabaseReference db;
    private FirebaseAuth mAuth;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_log);
        Intent i = getIntent();
        String k = i.getStringExtra("k");
        t = (TextView) findViewById(R.id.textView);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Customer").child("Person")
                .child(k);
        db.keepSynced(true);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                };
                final Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                setTitle(map.get("p03"));

                t.setText(map.get("log"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }

        });

    }
}
