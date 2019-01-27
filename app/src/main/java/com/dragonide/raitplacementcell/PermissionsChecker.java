package com.dragonide.raitplacementcell;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;


/**
 * Created by Ankit on 11/18/2016.
 */

public class PermissionsChecker {

    @TargetApi(23)
    public static boolean requestPermission(AppCompatActivity activity, String permission, int rationale, int requestCode) {
        if (hasPermission(activity, permission))
            return true;
        if (activity.shouldShowRequestPermissionRationale(permission) && rationale != 0)
            Toast.makeText(activity, rationale, Toast.LENGTH_LONG).show();

        activity.requestPermissions(new String[]{permission}, requestCode);
        return false;
    }

    @TargetApi(23)
    public static boolean hasPermission(AppCompatActivity activity, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
}