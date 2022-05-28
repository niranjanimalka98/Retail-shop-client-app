package com.jkdesigns.stsproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncTaskExample extends AsyncTask<ArrayList<String>, String, Bitmap> {

    URL ImageUrl;
    Bitmap bmImg = null;
    @SuppressLint("StaticFieldLeak")
    Context context;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @SafeVarargs
    @Override
    protected final Bitmap doInBackground(ArrayList<String>... strings) {
        /*FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://sale-f4270.appspot.com");
        File storagePath = new File(Environment.getExternalStorageDirectory(), "products");
        int count = strings.length;

        for (String fileName : strings) {

            StorageReference childReference = storageReference.child("products");
            StorageReference fileReference = storageReference.child("products/" + fileName);
            File localFile = null;

            File storagePath1 = new File(Environment.getExternalStorageDirectory(), "products/" + fileName);
            if (!storagePath1.exists()) {
                if (!storagePath.exists()) {
                    storagePath.mkdirs();
                } else {
                    //TODO
                }
                localFile = new File(storagePath, fileName);
                fileReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(AsyncTaskExample,"File downloaded",LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(AsyncTaskExample.this,"Download failed. Try again!", LENGTH_SHORT).show();
                    }
                });
            } else {
                //TODO
            }
        }*/

        File filepath = new File(Environment.getExternalStorageDirectory()+"/products");
        if (!filepath.exists()) {
            filepath.mkdirs();
        } else {
            //TODO
        }
        ArrayList<String> passedFiles = strings[0];
        InputStream is = null;
        int code = 0;

        for (String string : passedFiles) {

            try {

                URL u = new URL ( "https://pos.jkdesigns.app/demo/uploads/" + string);
                HttpURLConnection huc =  ( HttpURLConnection )  u.openConnection ();
                huc.setRequestMethod ("GET");  //OR  huc.setRequestMethod ("HEAD");
                huc.connect () ;
                code = huc.getResponseCode() ;
                //System.out.println(code);

                if(code == 200){
                    ImageUrl = new URL("https://pos.jkdesigns.app/demo/uploads/" + string);
                }else {
                    ImageUrl = new URL("https://pos.jkdesigns.app/demo/uploads/noimage.jpeg");
                }
                // myFileUrl1 = args[0];

                HttpURLConnection conn = (HttpURLConnection) ImageUrl
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                bmImg = BitmapFactory.decodeStream(is, null, options);

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                File file = new File(filepath, string);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                /*MediaScannerConnection.scanFile(context,
                        new String[]{file.getPath()},
                        new String[]{"image/jpeg"}, null);*/

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(code != 200){
                    File old=new File(filepath,"noimage.jpeg");
                    File to = new File(filepath, string);
                    boolean success=old.renameTo(to);
                }
        }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

    }
}