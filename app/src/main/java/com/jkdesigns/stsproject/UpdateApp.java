package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateApp extends AsyncTask<String,Void,Void> {
    @SuppressLint("StaticFieldLeak")
    private Context context;
    public void setContext(Context contextf){
        context = contextf;
    }

    @Override
    protected Void doInBackground(String... arg0) {
        try {
            URL url = new URL(arg0[0]);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            File directory;
            if (Environment.getExternalStorageState() == null) {
                directory = new java.io.File(String.valueOf(Environment.getDownloadCacheDirectory()));
            }else{
                directory = new java.io.File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
            }

            android.util.Log.e("UpdateAPP", "Dir" + directory);
            java.io.File file = new java.io.File(String.valueOf(directory));
            java.io.File outputFile = new java.io.File(file, "/update.apk");
            if(outputFile.exists()){
                outputFile.delete();
            }
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            File filed = new File(directory + "/update.apk");
            Uri data = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID +".provider",filed);
            intent.setDataAndType(data, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // without this flag android returned a intent error!
            context.getApplicationContext().startActivity(intent);

        } catch (Exception e) {
            android.util.Log.e("UpdateAPP", "Update error! " + e.getMessage());
        }
        return null;
    }
}
