package com.softcod.pesquisacorona.utils;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.redeindustrial.mtbf.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
//import static com.redeindustrial.mtbf.R.raw.alarme;

/**
 * Created by Igor on 19/01/17.
 */

public class Utils {

    /**
     * Checks if email is valid or invalid
     *
     * @param email the email to be verified
     * @return boolean true for valid false for invalid
     */
    public static boolean isValidEmail(CharSequence email) {

        return true;//email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }
    int alarme = 2;
    public static boolean isValidPhone(CharSequence phone) {

        return phone != null && phone.length() >= 10;

    }

    public static void showEditTextDialog(Context context, int dialogTitleId, int dialogMessageId, EditText editText, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(dialogTitleId);
        if (dialogMessageId != 0) {

            builder.setMessage(dialogMessageId);

        }
        builder.setView(editText);
        builder.setPositiveButton(R.string.ok, onClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alert.getWindow().getAttributes());

        int width = editText.getLayoutParams().width;
        int height = editText.getLayoutParams().height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        int marginPx = (int) (20 * context.getResources().getDisplayMetrics().density);
        params.setMargins(marginPx, 0, marginPx, 0);
        editText.setLayoutParams(params);

    }

    public static void showEditTextDialog(Context context, int dialogTitleId, int dialogMessageId, EditText editText, DialogInterface.OnClickListener yesOnClickListener, DialogInterface.OnClickListener noOnClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(dialogTitleId);
        if (dialogMessageId != 0) {

            builder.setMessage(dialogMessageId);

        }
        builder.setView(editText);
        builder.setPositiveButton(R.string.ok, yesOnClickListener);
        builder.setNegativeButton(R.string.cancel, noOnClickListener);

        AlertDialog alert = builder.create();
        alert.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alert.getWindow().getAttributes());

        int width = editText.getLayoutParams().width;
        int height = editText.getLayoutParams().height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        int marginPx = (int) (20 * context.getResources().getDisplayMetrics().density);
        params.setMargins(marginPx, 0, marginPx, 0);
        editText.setLayoutParams(params);

    }

    public static void showOkDialog(Context context, int dialogTitleId, int dialogMessageId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(dialogTitleId);
        builder.setMessage(dialogMessageId);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.cancel();
        });
        builder.show();

    }

    public static void showOkCancelDialog(Context context, int dialogTitleId, int dialogMessageId, int buttonTitleId, DialogInterface.OnClickListener onClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(dialogTitleId);
        builder.setMessage(dialogMessageId);
        builder.setPositiveButton(buttonTitleId, onClickListener);
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.show();

    }

    public static String formatDateTime(String dateString, String utc) {
        Log.d("UTC",utc);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Calendar cal = Calendar.getInstance();
        try {

            cal.setTime(sdfDate.parse(dateString));
            cal.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(utc));
            return sdfDate.format(cal.getTime());
        } catch (Exception e) {

            e.printStackTrace();
            return sdfDate.format(new Date());

        }

    }

    public static void log(String message) {

        Log.i("log", message);

    }

    public static void saveDefaultSoundToFile (Context context) {

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionCheck == PERMISSION_GRANTED) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);
            // Make sure the directory exists
            // noinspection ResultOfMethodCallIgnored
            path.mkdirs();
            //String filename = context.getResources().getResourceEntryName(alarme) + ".mp3";
            //File outFile = new File(path, filename);

            String mimeType = "audio/mpeg";

            // Write the file
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                //inputStream = context.getResources().openRawResource(alarme);
              //  outputStream = new FileOutputStream(outFile);

                // Write in 1024-byte chunks
                byte[] buffer = new byte[1024];
                int bytesRead;
                // Keep writing until `inputStream.read()` returns -1, which means we reached the
                //  end of the stream
                while ((bytesRead = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Set the file metadata
                /*String outAbsPath = outFile.getAbsolutePath();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DATA, outAbsPath);
                contentValues.put(MediaStore.MediaColumns.TITLE, context.getString(R.string.app_name));
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
                contentValues.put(MediaStore.Audio.Media.IS_ALARM, false);
                contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, false);
                contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
*/
  //              Uri contentUri = MediaStore.Audio.Media.getContentUriForPath(outAbsPath);

                // If the ringtone already exists in the database, delete it first
    //            context.getContentResolver().delete(contentUri, MediaStore.MediaColumns.DATA + "=\"" + outAbsPath + "\"", null);

                // Add the metadata to the file in the database
      //          Uri newUri = context.getContentResolver().insert(contentUri, contentValues);

                // Tell the media scanner about the new ringtone
          /*      MediaScannerConnection.scanFile(
                        context,
        //                new String[]{newUri.toString()},
                        new String[]{mimeType},
                        null
                );
*/
       //         Log.d("ConfigActivity", "Copied alarm tone alarme.mp3 to " + outAbsPath);
  ///              Log.d("ConfigActivity", "ID is " + newUri.toString());
            } catch (Exception e) {
     //           Log.e("ConfigActivity", "Error writing " + filename, e);
            } finally {
                // Close the streams
                try {
                    if (inputStream != null)
                        inputStream.close();
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                    // Means there was an error trying to close the streams, so do nothing
                }
            }
        } else {

            Toast.makeText(context, "Permissão não concedida", Toast.LENGTH_SHORT).show();

        }

    }

    public static String getStringFromResourceName(Context context, String resourceName) {

        return context.getResources().getString(context.getResources().getIdentifier(resourceName, "string", context.getPackageName()));

    }

    public static String getLastBitFromUri(final String uri){
        return uri.replaceFirst(".*/([^/?]+).*", "$1");
    }

    public static String makeTopicString(String firebaseEmail, String firebasePassword) {

        return firebaseEmail.replace("@", "_at_") + "_" + firebasePassword;

    }

    public static String getDeviceSoundName(Context context, String soundFileUri) {

        RingtoneManager manager = new RingtoneManager(context);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();

        while (cursor.moveToNext()) {

            String ringtoneUri = manager.getRingtoneUri(cursor.getPosition()).toString();
            if (ringtoneUri.equals(soundFileUri)) {

                return cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);

            }

        }

        return context.getString(R.string.app_name);

    }

}
