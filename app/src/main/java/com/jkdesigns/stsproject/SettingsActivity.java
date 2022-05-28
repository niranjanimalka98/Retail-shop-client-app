package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.Map;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static DatabaseReference sett;
    private static FirebaseAuth mAuth;
    private static String uid, i2, i2t, mlan;
    private static final String TAG = com.jkdesigns.stsproject.SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.action_settings));
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @SuppressLint("HardwareIds")
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            // gallery EditText change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_gallery_name)));

            // notification preference change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            mAuth = FirebaseAuth.getInstance();
            uid = mAuth.getCurrentUser().getUid();
            sett = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            sett.child("zinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> map = dataSnapshot.getValue(genericTypeIndicator);
                    mlan = map.get("mlan");
                    i2 = map.get("i02");
                    i2t = map.get("i02t");
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });

            // feedback preference click listener
            Preference myPref = findPreference("key_send_feedback");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });

            Preference about = (Preference) findPreference("vcode");
            about.setSummary("v"+BuildConfig.VERSION_CODE);

            Preference SERIAL = (Preference) findPreference("SERIAL");
            SERIAL.setSummary(Build.SERIAL);

            Preference MODEL = (Preference) findPreference("MODEL");
            MODEL.setSummary(Build.MODEL);

            Preference ID = (Preference) findPreference("ID");
            ID.setSummary(Build.ID);

            Preference MANUFACTURE = (Preference) findPreference("MANUFACTURE");
            MANUFACTURE.setSummary(Build.MANUFACTURER);

            Preference TYPE = (Preference) findPreference("TYPE");
            TYPE.setSummary(Build.TYPE);

            Preference USER = (Preference) findPreference("USER");
            USER.setSummary(Build.USER);

            Preference INCREMENTAL = (Preference) findPreference("INCREMENTAL");
            INCREMENTAL.setSummary(Build.VERSION.INCREMENTAL);

            Preference BOARD = (Preference) findPreference("BOARD");
            BOARD.setSummary(Build.BOARD);

            Preference BRAND = (Preference) findPreference("BRAND");
            BRAND.setSummary(Build.BRAND);

            Preference HOST = (Preference) findPreference("HOST");
            HOST.setSummary(Build.HOST);

            Preference VCODE = (Preference) findPreference("VCODE");
            VCODE.setSummary(Build.VERSION.RELEASE);

            Preference HARDWARE = (Preference) findPreference("HARDWARE");
            HARDWARE.setSummary(Build.HARDWARE);

            Preference BLOADER = (Preference) findPreference("BLOADER");
            BLOADER.setSummary(Build.BOOTLOADER);

            Preference SRESOLUTION = (Preference) findPreference("SRESOLUTION");

            DecimalFormat dc = new DecimalFormat(".00");
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
            double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
            double screenInches = Math.sqrt(x + y);
            String rounded = dc.format(screenInches);
            double densityDpi = (int) (dm.density * 160f);

            SRESOLUTION.setSummary(dm.heightPixels + " * " + dm.widthPixels + " PIXELS | " + rounded + "INCHES");

            Preference SDENSITY = (Preference) findPreference("SDENSITY");
            SDENSITY.setSummary(densityDpi + " DPI");
        }
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }*/

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    /*private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(R.string.summary_choose_ringtone);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_gallery_name")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };*/

    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {

        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jkdesigns.app@gmail.com"});
        if(mlan.equals("en")){
            intent.putExtra(Intent.EXTRA_SUBJECT, "POS System - " + i2);
        }else {
            intent.putExtra(Intent.EXTRA_SUBJECT, "POS System - " + i2);
        }
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }
}
