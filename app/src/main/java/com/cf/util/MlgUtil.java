package com.cf.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.cf.wifi.DevGroup;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;

public class MlgUtil extends Activity {
    public static Toast toast = null;
    public static String TAG = "MLTUtil: ";

    public static void ShowMessage(String messages, Activity activity) {
        TastyToast.makeText(activity,messages,TastyToast.LENGTH_SHORT,TastyToast.INFO);
    }

    // 隐藏键盘
    public static void hideKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public static void ShowLog(String logs) {
        Log.e("Debug msg:", logs);
    }

    public static void showDevGrop(DevGroup devGroup) {
        Log.e(TAG, "showDevGrop: " + devGroup.devid0 + "------" + devGroup.devid0ip);
        Log.e(TAG, "showDevGrop: " + devGroup.devid1 + "------" + devGroup.devid1ip);
        Log.e(TAG, "showDevGrop: " + devGroup.devid2 + "------" + devGroup.devid2ip);
        Log.e(TAG, "showDevGrop: " + devGroup.devid3 + "------" + devGroup.devid3ip);
        Log.e(TAG, "showDevGrop: " + devGroup.devid4 + "------" + devGroup.devid4ip);
    }

    public void toast_warning(String messages) {
        TastyToast.makeText(getApplicationContext(),
                messages,
                TastyToast.LENGTH_SHORT,
                TastyToast.WARNING);
    }

    public void toast_default(String messages) {
        TastyToast.makeText(getApplicationContext(),
                messages,
                TastyToast.LENGTH_SHORT,
                TastyToast.DEFAULT);
    }

    public void toast_error(String messages) {
        TastyToast.makeText(getApplicationContext(),
                messages,
                TastyToast.LENGTH_SHORT,
                TastyToast.ERROR);
    }

    public void toast_success(String messages) {
        TastyToast.makeText(getApplicationContext(),
                messages,
                TastyToast.LENGTH_SHORT,
                TastyToast.SUCCESS);
    }

    public void toast_info(String messages) {
        TastyToast.makeText(getApplicationContext(),
                messages,
                TastyToast.LENGTH_SHORT,
                TastyToast.INFO);
    }

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        assert sdDir != null;
        return sdDir.toString();
    }

}
