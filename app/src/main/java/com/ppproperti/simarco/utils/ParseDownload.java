package com.ppproperti.simarco.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class ParseDownload extends AsyncTask<String, String, File> {
    private static final String TAG = ParseDownload.class.getSimpleName();
    private Context context;
    private String url;
    private File outputDirs;
    private String fileName;

    public ParseDownload(Context context, String url, File outputDirs, String fileName) {
        this.context = context;
        this.url = url;
        this.outputDirs = outputDirs;
        this.fileName = fileName;
    }

    @Override
    protected File doInBackground(String... strings) {
        Log.d(TAG, "doInBackground:url:"+url);
        try {
            URL u = new URL(url);
            URLConnection conn = u.openConnection();
            int contentLength = conn.getContentLength();

            DataInputStream stream = new DataInputStream(u.openStream());

            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();

            if (!outputDirs.exists()){
                outputDirs.mkdirs();
            }

            File outputFile = new File(outputDirs, fileName);

            DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
            fos.write(buffer);
            fos.flush();
            fos.close();

            Log.d(TAG, "doInBackground:outputFile:"+outputFile.getTotalSpace());

            return outputFile;
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
    }
}
