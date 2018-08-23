package com.example.jc.newlaserscarecrow.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class DataLogService{
    // Creates a file to save configurations if one has not yet been created
    public DataLogService(Context context)
    {
        try
        {
            csv = new File(Directories.getRootFile(context), "/data.csv");
            if (!csv.exists()) {
                csv.createNewFile();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private File csv;

    // Gets the names of items in column 1
    public String[] getNames()
    {
        ArrayList<String> names = new ArrayList<String>();

        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(csv.getAbsoluteFile()));
            //Reads file line by line for these names
            while ((sCurrentLine = br.readLine()) != null) {
                names.add(sCurrentLine.substring(0,sCurrentLine.indexOf(",")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return names.toArray(new String[names.size()]);
    }

    // For a certain name, retrieve the values located in that row
    public String[] getValues(String name)
    {
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(csv.getAbsoluteFile()));
            // Checks line by line for name
            while ((sCurrentLine = br.readLine()) != null) {
                if (sCurrentLine.substring(0,sCurrentLine.indexOf(",")).equals(name))
                {
                    // Returns an array of all the values
                    return sCurrentLine.split(",");
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    // Deletes a full row from the file
    public boolean deleteValues(String name)
    {
        for (String csvName : getNames())
        {
            if (name.equals(csvName))
            {
                try
                {
                    // Creates a temporary file
                    File tempFile = new File(csv.getAbsolutePath() + ".tmp");

                    BufferedReader br = new BufferedReader(new FileReader(csv));
                    PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

                    String line = null;

                    //Read from the original file and write to the new
                    //unless content matches data to be removed.
                    while ((line = br.readLine()) != null) {

                        if (!line.trim().substring(0,line.indexOf(",")).equals(name)) {
                            pw.println(line);
                            pw.flush();
                        }
                    }
                    pw.close();
                    br.close();

                    //Delete the original file
                    if (!csv.delete()) {
                        System.out.println("Could not delete file");
                        return false;
                    }

                    //Rename the new file to the filename the original file had.
                    if (!tempFile.renameTo(csv))
                    {
                        System.out.println("Could not rename file");
                        return false;
                    }
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    // Write values into the CSV file
    public boolean writeValues(String[] values)
    {
        try
        {
            deleteValues(values[0]);
            String content = "";
            for (String val : values)
            {
                content = content + val + ",";
            }
            content = content.substring(0,content.length() - 1);

            FileOutputStream fileOutputStream = new FileOutputStream(csv, true);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}