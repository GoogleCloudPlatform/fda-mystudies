package org.sagebionetworks.bridge.researchstack;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import org.researchstack.backbone.AppPrefs;
import org.researchstack.backbone.PermissionRequestManager;

import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 12/12/16.
 */

public class CrfPermissionRequestManager extends PermissionRequestManager {

    public static final String PERMISSION_NOTIFICATIONS = "Crf.permission.NOTIFICATIONS";

    private static final int RESULT_REQUEST_CODE_NOTIFICATION = 143;

    public CrfPermissionRequestManager() {
        // If Build is M or >, add needed permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            {
                PermissionRequestManager.PermissionRequest permission = new PermissionRequestManager.PermissionRequest(
                        Manifest.permission.CAMERA,
                        R.drawable.ic_camera_alt_black_24dp,
                        R.string.permission_camera_title,
                        R.string.permission_camera_desc);
                permission.setIsBlockingPermission(true);
                permission.setIsSystemPermission(true);

                addPermission(permission);
            }

            {
                PermissionRequestManager.PermissionRequest permission = new PermissionRequestManager.PermissionRequest(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        R.drawable.rsb_ic_location_24dp,
                        R.string.rsb_permission_location_title,
                        R.string.rsb_permission_location_desc);
                permission.setIsBlockingPermission(true);
                permission.setIsSystemPermission(true);

                addPermission(permission);
            }
        }
//
//        // We have some unique permissions that tie into Settings. You will need
//        // to handle the UI for this permission along w/ storing the result.
//        PermissionRequestManager.PermissionRequest notifications =
//                new PermissionRequestManager.PermissionRequest(
//                        PERMISSION_NOTIFICATIONS,
//                        R.drawable.rsb_ic_notification_24dp,
//                        R.string.rsb_permission_notification_title,
//                        R.string.rsb_permission_notification_desc
//                );
//        addPermission(notifications);
    }

    @Override
    public boolean hasPermission(Context context, String permissionId) {
        switch (permissionId) {
            case PERMISSION_NOTIFICATIONS:
                return AppPrefs.getInstance(context).isTaskReminderEnabled();
            default: // This is a system permission, simply ask the system
                return ContextCompat.checkSelfPermission(context, permissionId) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Used to tell if the permission-id should be handled by the system (using
     * {@link Activity#requestPermissions(String[], int)}) or through our own custom implementation
     * in {@link #onRequestNonSystemPermission}
     *
     * @param permissionId
     * @return
     */
    @Override
    public boolean isNonSystemPermission(String permissionId) {
        // SampleApplication.PERMISSION_NOTIFICATIONS is our non-system permission so we return true
        // if permissionId's are the same
        return permissionId.equals(PERMISSION_NOTIFICATIONS);
    }

    /**
     * This method is called when {@link #isNonSystemPermission} returns true. For example, if using
     * Google+ Sign In, you would create your signIn-Intent and start that activity. Any result
     * will then be passed through to {#link onNonSystemPermissionResult}
     *
     * @param permissionId
     */
    @Override
    public void onRequestNonSystemPermission(Activity activity, String permissionId) {
        // TODO: show custom notification permission activity for specific permissionId
//        Intent intent = new Intent(activity, NotificationPermissionActivity.class);
//        activity.startActivityForResult(intent, RESULT_REQUEST_CODE_NOTIFICATION);
    }

    /**
     * Method is called when your Activity called in {@link #onRequestNonSystemPermission} has
     * returned with a result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    @Override
    public boolean onNonSystemPermissionResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_REQUEST_CODE_NOTIFICATION) {
            AppPrefs.getInstance(activity).setTaskReminderComplete(resultCode == Activity.RESULT_OK);
            return true;
        }

        return false;
    }
}
