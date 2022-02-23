package com.fieldnation.userprofile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Utils {


    private static final String TAG = Utils.class.getName();
    private static final Gson builder;

    static {
        builder =  new GsonBuilder().create();
    }


    /**
     * @return True if the caller is running on the UI thread, else
     * false.
     */
    public static boolean runningOnUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }


    /**
     * @param json is regular string representation of JSON object
     * @return Base64 encoded json string
     */
    public static String encodeJsn2Str(String json) {
        try {
            Log.d(TAG, "init_base_64_encoding_of_string: " + Base64.encodeToString(json.getBytes(), Base64.DEFAULT).trim().replaceAll("\n", ""));
            return Base64.encodeToString(json.getBytes(), Base64.DEFAULT).trim().replaceAll("\n", "");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param serializesString is Base64 encoded
     * @return Base64 encoded string to decoded String
     */
    public static String decodeStr2Jsn(String serializesString) {
        try {
            Log.d(TAG, "init_base_64_deCoding_of_string " + URLDecoder.decode(new String(Base64.decode(serializesString.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8"));
            return URLDecoder.decode(new String(Base64.decode(serializesString.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param value
     * @return true if string
     */
    public static boolean validateRequiredField(String value) {
        if (value == null) {
            return false;
        } else return value.trim().length()!=0 && !value.equals("") && !value.equals(" ");
    }


    /**
     * @param src is a Object
     * @return String representation of source object
     */
    public static String toJsonString(Object src) {
        return builder.toJson(src);
    }
    /**
     *
     * @param response
     * @param typeKey
     * @param <T>
     * @return Object casted from json object string
     */
    public static <T> T fromJson(String response, Class<T> typeKey) {
        return builder.fromJson(response,typeKey);
    }
    /**
     * Converting dp to pixel
     */
    public static int dpToPx(Context context, int dp) {
        Resources r = context.getApplicationContext().getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    /**
     *
     * @param dateString
     * @return formatted date
     */
    public static String formatDate(String dateString) {

        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US);
        SimpleDateFormat updatedFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm aaa", Locale.US);
        try {
            Date date = originalFormat.parse(dateString);
            String strDate = updatedFormat.format(date);
            return strDate;

        } catch (Exception e) {
            e.printStackTrace();
            return "Date";
        }


    }
    /**
     * handles coneected but no internet cases
     *
     * @param timeOut
     * @return true if network connection available
     */
    public boolean internetConnectionAvailable(int timeOut) {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }
    public enum FormErrors {
        INVALID_EMAIL,
        INVALID_ETIN,
        INVALID_PHN,
        INVALID_USR_NAME,
        INVALID_PASSWORD,
        PASSWORDS_NOT_MATCHING,
        REQUIRED_FIELD,
        INVALID_NID,
        INVALID_DOB,
        INVALID_ADDRESS,
        INVALID_ROLE, INVALID_DONATION_AMOUNT;
    }




}
