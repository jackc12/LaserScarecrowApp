package com.example.jc.newlaserscarecrow.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Matt Constant
 * Editted by Andrew on 7/27/17.
 *
 * Directories
 * Creates and returns the parent and patient directories that will store
 * the patient's CSV files.
 */

public class Directories
{
    public static File getRootFile(Context context)
    {
        File root;
        root = new File("/storage/sdcard1");
        if (!root.exists() || !root.canWrite()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                root = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOCUMENTS);
            } else {
                root = new File(Environment.getExternalStorageDirectory(), "Documents");
            }
        }

        File directory;
        directory = new File(root, ".Laser_Scarecrow");

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                Log.d("MAIN", "Made parent directories");
            }
        }
        return directory;
    }
}
