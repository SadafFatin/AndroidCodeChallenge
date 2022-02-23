package com.fieldnation.userprofile.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * A utility class containing methods for creating and manipulating
 * Uri objects.
 */
public class UriUtils {
    /**
     * Logging tag.
     */
    private static final String TAG = "UriUtils";

    /**
     * File provider identifier.
     */
    private static final String FILE_PROVIDER_AUTHORITY =
            "com.fieldnation.userprofile.fileProvider";
    // base buffer size
    private static int BASE_BUFFER_SIZE = 2048;

    // if you want to modify size use binary multiplier 2, 4, 6, 8
    private static int DEFAULT_BUFFER_SIZE = BASE_BUFFER_SIZE * 4;

    private UriUtils() {
        throw new AssertionError();
    }

    /**
     * Builds an action intent and converts the passed local file uri
     * to a content uri with read permission for all applications that
     * can process the intent. This method is designed to work on all
     * versions of Android.
     *
     * @param context  A context.
     * @param pathName A local file path.
     * @param action   The intent action.
     * @param type     The intent type.
     * @return The built intent.
     */
    public static Intent buildFileProviderReadUriIntent(Context context,
                                                        String pathName,
                                                        String action,
                                                        String type) {
        // Build a content uri.
        Uri uri = FileProvider.getUriForFile
                (context,
                        getFileProviderAuthority(),
                        new File(pathName));

        // Create and initialize the intent.
        Intent intent =
                new Intent()
                        .setAction(action)
                        .setDataAndType(uri, type);

        // Call helper method that uses the most secure permission granting
        // model for the each API.
        grantUriPermissions
                (context,
                        intent,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;
    }

    /**
     * Builds an action intent and converts the passed local file uri
     * to a content uri with read permission for all applications that
     * can process the intent. This method is designed to work on all
     * versions of Android.
     *
     * @param context A context.
     * @param uri     A local file uri.
     * @param action  The intent action.
     * @param type    The intent type.
     * @return The built intent.
     */
    public static Intent buildFileProviderReadUriIntent(Context context,
                                                        Uri uri,
                                                        String action,
                                                        String type) {
        return buildFileProviderReadUriIntent(context,
                getPathNameFromFileUri(uri),
                action,
                type);
    }

    /**
     * @return Application file provider authority.
     */
    public static String getFileProviderAuthority() {
        return FILE_PROVIDER_AUTHORITY;
    }

    /**
     * Grants the specified uri permissions to all packages that can
     * process the intent. The most secure granting model is used for
     * the current API. This method is designed to work on all
     * versions of Android but has been tested only on API 23, and 24.
     *
     * @param context     A context.
     * @param intent      An intent containing a data uri that was obtained from
     *                    FileProvider.getUriForFile().
     * @param permissions The permissions to grant.
     */
    public static void grantUriPermissions(Context context,
                                           Intent intent,
                                           int permissions) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            // Find all packages that support this intent and grant
            // them the specified permissions.
            List<ResolveInfo> resInfoList =
                    context.getPackageManager().queryIntentActivities(
                            intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(
                        packageName,
                        intent.getData(),
                        permissions);
            }
        } else {
            // Just grant permissions to all apps.
            intent.addFlags(permissions);
        }
    }

    /**
     * Converts a local file uri to a local path name. This will throw an
     * IllegalArgumentException if the passed uri is not a valid file uri.
     *
     * @param uri A uri that references a local file.
     * @return The path name suitable for passing to the File class.
     */
    public static String getPathNameFromFileUri(Uri uri) {
        if (!URLUtil.isFileUrl(uri.toString()))
            throw new IllegalArgumentException("Invalid file uri");
        return uri.getPath();
    }



    /**
     *
     * @param context
     * @param fileUri
     * @param formFieldName
     * @return a Temp File necessary for uploading.
     * File created in the app's own cache directory from a content URI.
     * Does not need the dangerous read or write permissions
     */
    public static File getTempFileFromContentUri(Context context, Uri fileUri, String formFieldName)  {
        String ext = UriUtils.getFileExtensionFromUri(context , fileUri);
        String fileName = UriUtils.getTempFileName(formFieldName , ext);
        InputStream inputStream = null;
        try {
            inputStream = context.getApplicationContext().getContentResolver().openInputStream(fileUri);
            return createTempFileFromFilePickerInputForUpload(context,inputStream,fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * @param context
     * @param fileUri
     * @return fileName from content URI
     */
    public static String getFileNameFromUri(Context context,Uri fileUri) {
        /*
         * Get the file's content URI from the incoming Intent,
         * then query the server app to get the file's display name
         */

        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */

       Cursor returnCursor =
               context.getContentResolver().query(fileUri, null, null, null, null);
       if(returnCursor!=null){
       if(returnCursor.moveToFirst()){
           int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
           return returnCursor.getString(nameIndex);
       }
       }
       return "";

    }

    /**
     *
     * @param context
     * @param fileUri
     * @return calculated fileSize from Content Uri
     */
   public static double getFileSizeFromUri(Context context,Uri fileUri) {
        /*
         * Get the file's content URI from the incoming Intent,
         * then query the server app to get the file's size
         */
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        Cursor returnCursor =
                context.getContentResolver().query(fileUri, null, null, null, null);

        if(returnCursor!=null){
            if(returnCursor.moveToFirst()) {
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                if(returnCursor.getString(sizeIndex)!=null){
                    double size = Double.parseDouble(returnCursor.getString(sizeIndex))/1024;
                    return size;
                }
            }
        }

       return 0;
    }

    /**
     *
     * @param context
     * @param fileUri
     * @return Mimetype of a file from Content URI or File URI
     */
   public static String getMimeTypeFromUri(Context context,Uri fileUri) {
       String mimeType = "*/*";
       if (ContentResolver.SCHEME_CONTENT.equals(fileUri.getScheme())) {
           ContentResolver cr = context.getApplicationContext().getContentResolver();
           mimeType = cr.getType(fileUri);
       } else {
           String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
           Log.d(TAG,"File extension from File URI to extract MimeType "+fileExtension);
           mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                   fileExtension.toLowerCase());
       }


       return mimeType;
   }

    /**
     *
     * @param context
     * @param fileUri
     * @return Files Extension from content uri
     */
   public static String getFileExtensionFromUri(Context context, Uri fileUri) {
        /*
         * Get the file's content URI from the incoming Intent,
         * then query the server app to get the file's Mimetype
         */
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        String extension;
        if (ContentResolver.SCHEME_CONTENT.equals(fileUri.getScheme())) {
            ContentResolver cr = context.getApplicationContext().getContentResolver();
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(cr.getType(fileUri));

        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
        }
        Log.d(TAG, " File extension:"+extension+" from URI "+fileUri);
        return extension;

    }
   public static String getTempFileName(@NonNull String fileName , String fileExtension) {
       if(fileExtension!=null){
           return fileName+"."+fileExtension;
       }
       return fileName;
   }

    /**
     *
     * @param files
     * @return A list Containing a list of MultipartBody.Part List for
     * all the selected files
     */
    public static List<MultipartBody.Part> getAttachmentsRequestMultiPart(Context context, List<File> files){
        List<MultipartBody.Part> filePartList = new ArrayList<>();
        for (File file:files) {
            String mimeTypeFromUri = getMimeTypeFromUri(context ,Uri.fromFile(file));
            Log.d(TAG, "File For Request Body: "+ file.getName() + " Absolute Path: " + file.getAbsolutePath()+ " Mimetype:" + mimeTypeFromUri);
            if(mimeTypeFromUri == null){
                mimeTypeFromUri = "image/*";
            }
            RequestBody requestFileBody = RequestBody.create(file,MediaType.parse(mimeTypeFromUri));
            MultipartBody.Part filePart =
                    MultipartBody.Part.createFormData("files[]", file.getName(), requestFileBody);
            filePartList.add(filePart);
        }
        return filePartList;
    }


    /**
     * Decode an InputStream into a Bitmap and store it in a file on
     * the device.
     *
     * @param context	   the context in which to write the file.
     * @param inputStream  the Input Stream.
     * @param fileName     name of the file.
     *
     * @return the absolute path to the downloaded image file
     *         on the file system.
     */
    public static File createTempFileFromFilePickerInputForUpload(Context context,
                                                                  InputStream inputStream,
                                                                  String fileName) {
        // Create a name of a directory in external storage.
        File directory = context.getCacheDir();

        if (!directory.exists()) {
            // Create a directory in external storage.
            File newDirectory =
                    new File(directory.getAbsolutePath());
            newDirectory.mkdirs();
        }

        // Make a new temporary file name.
        File outputFile = new File(context.getCacheDir(),fileName);
        if (outputFile.exists())
            // Delete the file if it already exists.
            outputFile.delete();

        // Save the pdf_doc to the output file.
        try (FileOutputStream outputStream =
                     new FileOutputStream(outputFile)) {

            // create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) !=-1){
                outputStream.write(buffer,0,bufferLength);
            }
            inputStream.close();
            outputStream.flush();
        } catch (Exception e) {
            // Indicate a failure.
            return null;
        }

        // Get the absolute path of the image.
        String absolutePathToImage = outputFile.getAbsolutePath();

        Log.d(TAG,
                "absolute path to File file is "
                        + absolutePathToImage);

        // Return the absolute path of the image file.
        return outputFile;
    }





    /**
     * Decode an InputStream into a PDF and store it in a file on
     * the device.
     *
     * @param context	   the context in which to write the file.
     * @param inputStream  the Input Stream.
     * @param fileName     name of the file.
     *
     * @return the absolute path to the downloaded image file
     *         on the file system.
     */
    public static File saveDownloadedPdfFile(Context context,
                                             InputStream inputStream,
                                             String fileName) {

        fileName = getTempFileName(fileName, ".pdf");
        // Create a name of a directory in external storage.
        File directory = new File(context.getCacheDir()+"/docs");

        if (!directory.exists()) {
            // Create a directory in external storage.
            File newDirectory =
                    new File(directory.getAbsolutePath());
            newDirectory.mkdirs();
        }

        // Make a new temporary file name.
        File file = new File(directory,
                fileName);
        if (file.exists())
            // Delete the file if it already exists.
            file.delete();

        // Save the pdf_doc to the output file.
        try (FileOutputStream outputStream =
                     new FileOutputStream(file)) {

            // create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) !=-1){
                outputStream.write(buffer,0,bufferLength);
            }
            inputStream.close();
            outputStream.flush();
        } catch (Exception e) {
            // Indicate a failure.
            return null;
        }

        // Get the absolute path of the image.
        String absolutePathToImage = file.getAbsolutePath();

        Log.d(TAG,
                "Absolute path for downloaded file is" + absolutePathToImage);

        // Return the absolute path of the image file.
        return file;
    }

    public static String generateUserFileUrlFromName(String phone, String fileName){
        //return BASE_URL + USER_FILE + "user=" + phone + "&filename=" + fileName;
        return "";
    }



}

