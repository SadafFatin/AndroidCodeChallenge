package com.fieldnation.userprofile.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.fieldnation.userprofile.R;
import com.google.android.material.snackbar.Snackbar;
import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * This utility class defines static methods shared by various Activities.
 */
public class UiUtils {
    /**
     * Debugging tag.
     */
    private static final String TAG =
            UiUtils.class.getCanonicalName();

    /**
     * Ensure this class is only used as a utility.
     */
    private UiUtils() {
        throw new AssertionError();
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * shows toast via Snackbar
     */
    public static void showToast(View view, Context context, String msg) {
        Snackbar.make(
                view,
                msg,
                Snackbar.LENGTH_LONG).show();
    }

    /**
     * shows toast via alert dialog
     */
    public static void showAlertInfoDialog(Activity activity, String title, String msg) {
        DialogInterface.OnClickListener okListener = (dialog, which) ->
        {
            dialog.dismiss();
        };
        new AlertDialog.Builder(activity)
                .setMessage(msg)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setPositiveButton(R.string.prompt_cancel, okListener)
                .create().show();
    }



    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }


    /**
     * Change App language
     *
     * @return void
     */
    public static void setLocale(Activity context, String localeString) {
        Locale locale;
        locale = new Locale(localeString, localeString.toUpperCase());
        Configuration config = new Configuration(context.getBaseContext().getResources().getConfiguration());
        Resources res = context.getBaseContext().getResources();
        Locale.setDefault(locale);
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
        Log.d(TAG, "Config changed");
    }

    /**
     * Return an uppercase version of the input or null if user gave
     * no input.  If user gave no input and @a showToast is true a
     * toast is displayed to this effect.
     */
    public static String uppercaseInput(Context context,
                                        String input,
                                        boolean showToast) {
        if (input.isEmpty()) {
            if (showToast)
                UiUtils.showToast(context,
                        "no input provided");
            return null;
        } else
            // Convert the input entered by the user so it's in
            // uppercase.
            return input.toUpperCase(Locale.ENGLISH);
    }


    /**
     * Shows a progress dialog while doing background task
     */
    public static class ProcessingDialog {

        private WeakReference<Activity> activity;
        private Dialog dialog;

        //..we need the context else we can not create the dialog so get context in constructor
        public ProcessingDialog(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        public void showDialog() {

            dialog = new Dialog(activity.get());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //...set cancelable false so that it's never get hidden
            dialog.setCancelable(false);
            //...that's the layout i told you will inflate later
            dialog.setContentView(R.layout.item_layout_loading);
            dialog.show();
        }

        //..also create a method which will hide the dialog when some work is done
        public void hideDialog() {
            dialog.dismiss();
        }

    }

    public static void showDialog(ProcessingDialog mProgressDialog) {
        if (mProgressDialog != null) {
            mProgressDialog.showDialog();
        }
    }

    public static void hideDialog(ProcessingDialog mProgressDialog) {
        if (mProgressDialog != null) {
            mProgressDialog.hideDialog();
        }
    }

    /**
     * @param context
     * @param message
     * @param title   Alert dialog for showing alerts
     */
    public static void showAlertDialog(Activity context, String message, String title) {
        final AlertDialog.Builder alertDialog
                = new AlertDialog.Builder(context.getApplicationContext());

        alertDialog.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setNeutralButton(R.string.prompt_ok, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alert = alertDialog.create();
        alert.show();
    }



}
