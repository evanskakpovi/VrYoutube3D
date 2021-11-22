package com.yunju.vr360videoplayer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.ekm.youtubevr3dvideosprod.R;


/**
 * Created by alex on 4/20/2016.
 */
public class DownloadStatusDialog extends Dialog {

    private static DownloadStatusDialog dialog = null;
    private ProgressIndicator downloadProgress;
    public DownloadStatusDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_download_status);

        this.setCancelable(false);
        downloadProgress = (ProgressIndicator) findViewById(R.id.pi_download_status);
    }
    public void setProgress(float progress) {
        try {
            downloadProgress.setProgress(progress);
        } catch (Exception e) {

        }
    }

    public static void setDownloadProgress(float progress) {
        if (dialog == null) {
            return;
        }
        dialog.setProgress(progress);
    }
    public static DownloadStatusDialog showDialog(Context context) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new DownloadStatusDialog(context);
//        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        if (EngListeningApp.singleton != null) {
//            Analytics.sendScreenName(EngListeningApp.singleton, "Recommended App Selected");
//        }
        dialog.show();
        return dialog;
    }

    public static void hideDialog() {
        if (dialog == null)
            return;
        dialog.dismiss();
    }
}
