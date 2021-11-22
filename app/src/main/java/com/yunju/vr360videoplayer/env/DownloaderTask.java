package com.yunju.vr360videoplayer.env;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ALEX
 */
public class DownloaderTask extends AsyncTask<Void, Float, Integer> {
    private final String TAG = "DownloaderTask";

//    private static final String URL_PREFIX = "";
String name;
    public interface OnDownloadListener {
        void onComplete(String filename);
        void onComplete2(String filename, long time);
        void onProgress(float progress);
        void onFail(long time);
        void onFailNoSpace();
    }

    public interface OnCancelListener {
        void onCancel();
    }

    public interface OnFinishListener {
        void onFinish(DownloaderTask downloader);
    }

    final Context context;
    String strVideoServerUrl;
    String strVideoDownloadPath;
    String strURLPath;
    File outputFile;
    OnDownloadListener listener;
    OnCancelListener cancelListener;
    OnFinishListener finishListener;
    boolean writingFile;

    private DownloaderTask(Context context){
        this.context = context;
    }
    public static String getVideoDownloadDir(Context context) {
        String path = context.getExternalFilesDir(null) + "/" + ".vrytvideoprrol/";
        File f = new File(path);
        if(!f.exists()) f.mkdirs();
        return path;
    }
    public DownloaderTask(Context context, String strURLPath, OnFinishListener finishListener, OnDownloadListener listener) {

        this.context = context;
        this.strURLPath = strURLPath;
        name = strURLPath.substring(strURLPath.lastIndexOf("/")+1, strURLPath.length());
        strVideoDownloadPath = getVideoDownloadDir(context) + name;
     //   System.out.println(name+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        outputFile = new File(strVideoDownloadPath);
        this.finishListener = finishListener;
        this.listener = listener;
        writingFile = false;
    }
   long time1;
    public void setOnDownloadListener(OnDownloadListener listener) {
        this.listener = listener;
    }

    public boolean isWriting() {
        return writingFile;
    }
    public boolean isRunning() {
        return (getStatus() == Status.RUNNING);
    }

    public void setOnCancelListener(OnCancelListener l) {
        cancelListener = l;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        DateTime dt = new DateTime();
        time1 = dt.getMillis();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        final int TIMEOUT_CONNECT = 4000;
        final int TIMEOUT_READ = 10000;
        final int BUFFER_SIZE = 4096;

        String fileURL = strURLPath;

        URL url;
        try {
            url = new URL(fileURL);
        } catch (MalformedURLException e) {
            return -1;
        }

        int success = -1;
        HttpURLConnection httpConn = null;

        try {
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(TIMEOUT_CONNECT);
            httpConn.setReadTimeout(TIMEOUT_READ);

            if(isCancelled()) {
                httpConn.disconnect();
                Log.v(TAG, "File downloaded Cancelled ");
                return -1;
            }

            int responseCode = httpConn.getResponseCode();

            // always check HTTP response code first
            if (responseCode == HttpURLConnection.HTTP_OK) {
                writingFile = true;

                String disposition = httpConn.getHeaderField("Content-Disposition");
                String contentType = httpConn.getContentType();
                int contentLength = httpConn.getContentLength();


                // opens input stream from the HTTP connection
                InputStream inputStream = httpConn.getInputStream();

                // opens an output stream to save into file
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                int read = 0;
                publishProgress(0f);
                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);

                    if(isCancelled()) {
                        outputStream.close();
                        inputStream.close();
                        httpConn.disconnect();
                        Log.v(TAG, "File downloaded Cancelled ");
                        return -1;
                    }

                    read += bytesRead;
                    if(contentLength > 0) publishProgress((float)read / (float)contentLength);
                }

                outputStream.close();
                inputStream.close();

                Log.v(TAG, "File downloaded ");
                success = 0;
            } else {
                Log.v(TAG, "No file to download. Server replied HTTP code: " + responseCode);
            }
        } catch(IOException e)
        {
//            if(e.toString().contains("java.net.UnknownHostException"))
            if(e.toString().contains("java.io.IOException: write failed"))
                success = 1;
            else
                success = -1;
        }
        catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            } else {
                Log.e(TAG, "---- downloading error ----");
            }
        }

        if(httpConn != null) {
            httpConn.disconnect();
        }

        return success;
    }

    @Override
    protected void onProgressUpdate(Float... progress) {
        try {
            if (listener != null) {
                listener.onProgress(Math.min(1f, progress[0]));
               System.out.println(Math.min(1f, progress[0]));
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            } else {
                Log.e(TAG, "---- downloading progress error ----");
            }
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        try {
            DateTime dt = new DateTime();
            long time = time1-dt.getMillis();
            if (result == 0) {
                if (listener != null) {

                    //listener.onComplete(name);
                    listener.onComplete2(name, time);
                }
            } else if(result == 1) {
                outputFile.delete();
                if (listener != null) {
                    listener.onFailNoSpace();
                }

            } else{
                outputFile.delete();
                if (listener != null) {
                    listener.onFail(time);
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            } else {
                Log.e(TAG, "---- downloading on postexecute error ----");
            }
        }
        if (finishListener != null) {
            finishListener.onFinish(this);
        }
    }

    @Override
    protected void onCancelled (Integer result) {
        outputFile.delete();
        if(cancelListener != null) {
            cancelListener.onCancel();
        }
        if (finishListener != null) {
            finishListener.onFinish(this);
        }
    }

}