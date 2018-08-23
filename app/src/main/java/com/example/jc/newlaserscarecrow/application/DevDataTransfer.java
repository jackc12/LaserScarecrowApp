package com.example.jc.newlaserscarecrow.application;

import android.content.Context;
import android.util.Log;

import com.example.jc.newlaserscarecrow.services.BluetoothService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Jack and Andrew
 *
 * DevDataTransfer
 *
 * Acts as a holding area for the values gathered from the Bluetooth device before
 * the values are sent to the SettingsActivity
 *
 * Called when settings button in ScanActivity is clicked / when SettingsActivity is created
 *
 */
public class DevDataTransfer implements CommandInterface
{
    private static final String TAG = "DevDataTransfer";
    private static String codes = "";
    private static ArrayList<String> values = new ArrayList<String>();

    /**                 --parseResponse(...)--
     * Takes the string from the BluetoothService read() and creates two strings
     * that can be parsed and made into a hashtable
     *
     * @param response
     */
    public static void parseResponse(String response)
    {
        // Preprocesses the string
        Log.e("res", response);
        String[] result = response.split("\\r?\\n");
        for(String line : result)
        {
            // Seperates the string into two different ones
            if(!line.equals("ok") || !line.equals("error(6)"))
            {
                Pattern pattern = Pattern.compile("  *");
                Matcher matcher = pattern.matcher(line);
                System.out.print("Find this " + line);
                if (matcher.find()) {
                    codes += line.substring(0, matcher.start()) + ' ';
                    values.add(line.substring(matcher.end()) + ' ');
                }
            }
        }
        Log.e(TAG, "Codes: " + codes);
        Log.e(TAG, "Values: " + values);
    }

    /**             createHashTable()
     *
     * @return: Returns the map to the SettingsActivity
     */
    public static Map<String, int[]> createHashtable() {
        //Log.e(TAG, "Values: " + values);
        Map<String, int[]> map = new HashMap();
        String[] keys = codes.split(" ");
        //String[] vals = values.split(" ");
        for (int i = 0 ; i < keys.length ; i++) {
            try {
                if(values.get(i).equals("error(6)") || values.get(i).equals(null) || values.get(i).equals("ok"))
                {
                    Log.e(TAG, values.get(i) + "is error(6) or null. Changing to 0.");
                    values.set(i, "0");
                }

                String[] arrayOfStringValues = values.get(i).toString().trim().split(" ");
                int[] arrayOfIntValues = new int[arrayOfStringValues.length];

                for (int j = 0 ; j < arrayOfIntValues.length ; j++) {
                    arrayOfIntValues[j] = Integer.parseInt(arrayOfStringValues[j]);
                }

                map.put(keys[i], arrayOfIntValues);

            }
            catch (Exception e) {
                System.out.println("Request " + keys[i]);
            }
        }
        return map;
    }

    public static void demoRot(Context mContext) {
        BluetoothService.write(mContext, "S121 400");
    }

    /**
     *              --writeLookCommands()--
     *      Writes the look commands to the Arduino
     * @param mContext
     */
    public static void writeLookCommands(Context mContext)
    {
        for(int i = 0; i < Commands.NUM_COMMANDS; i++)
        {
            switch(i)
            {
                case 0:
                    BluetoothService.write(mContext, Commands.L_STEPPER_SPEED);
                    Log.e(TAG, "Looked for Stepper Speed");
                    break;
                case 1:
                    BluetoothService.write(mContext, Commands.L_PITCH_MIN);
                    Log.e(TAG, "Looked for Pitch Min");
                    break;
                case 2:
                    BluetoothService.write(mContext, Commands.L_PITCH_RANGE);
                    Log.e(TAG, "Looked for Pitch Range");
                    break;
                case 3:
                    BluetoothService.write(mContext, Commands.L_ROT_ANGLE);
                    Log.e(TAG, "Looked for Rotation Angle");
                    break;
                case 4:
                    BluetoothService.write(mContext, Commands.L_CYCLE_MODE);
                    Log.e(TAG, "Looked for Cycle Mode");
                    break;
                case 5:
                    BluetoothService.write(mContext, Commands.L_LIGHT_THRES);
                    Log.e(TAG, "Looked for Light Threshold");
                    break;
                case 6:
                    BluetoothService.write(mContext, Commands.L_YEAR_MONTH_DAY);
                    Log.e(TAG, "Looked for Year, Month, Day");
                    break;
                case 7:
                    BluetoothService.write(mContext, Commands.L_HOUR_MIN_SEC);
                    Log.e(TAG, "Looked for Hour, Min, Sec");
                    break;
                case 8:
                    BluetoothService.write(mContext, Commands.L_WAKE_TIME);
                    Log.e(TAG, "Looked for Wake Time");
                    break;
                case 9:
                    BluetoothService.write(mContext, Commands.L_SLEEP_TIME);
                    Log.e(TAG, "Looked for Sleep time");
                    break;
                case 10:
                    BluetoothService.write(mContext, Commands.L_ROTATE_POS);
                    Log.e(TAG, "Looked for micro-steps positive rotation");
                    break;
                case 11:
                    BluetoothService.write(mContext, Commands.L_ROTATE_NEG);
                    Log.e(TAG, "Looked for micro-steps negative rotation");
                    break;
                case 12:
                    BluetoothService.write(mContext, Commands.L_ROT_STATE);
                    Log.e(TAG, "Looked for Manual Control");
                    break;
                case 13:
                    BluetoothService.write(mContext, Commands.L_CURRENT_LIGHT);
                    Log.e(TAG, "Looked for Current Light");
                    break;
            }
        }
    }

    /**
     *              --writeLookCommands()--
     *      Writes the set commands to the Arduino
     * @param mContext
     * @param values: Values obtained from SettingsActivity widgets
     */
    public static void writeSetCommands(Context mContext, int[] values)
    {
        for(int i = 0; i < Commands.NUM_COMMANDS; i++) // 6 for now, change to Commands.NUM_COMMANDS later
        {
            switch(i)
            {
                case 0:
                    BluetoothService.write(mContext, Commands.S_STEPPER_SPEED + values[i]);
                    Log.e(TAG, "Set for Stepper Speed");
                    break;
                case 1:
                    BluetoothService.write(mContext, Commands.S_PITCH_MIN + values[i]);
                    Log.e(TAG, "Set for Pitch Min");
                    break;
                case 2:
                    BluetoothService.write(mContext, Commands.S_PITCH_RANGE + values[i]);
                    Log.e(TAG, "Set for Pitch Range");
                    break;
                case 3:
                    BluetoothService.write(mContext, Commands.S_ROT_ANGLE + values[i]);
                    Log.e(TAG, "Set for Rotation Angle");
                    break;
                case 4:
                    BluetoothService.write(mContext, Commands.S_CYCLE_MODE + values[i]);
                    Log.e(TAG, "Set for Cycle Mode");
                    break;
                case 5:
                    BluetoothService.write(mContext, Commands.S_LIGHT_THRES + values[i]);
                    Log.e(TAG, "Set for Light Threshold");
                    break;
                case 6:
                    BluetoothService.write(mContext, Commands.S_YEAR_MONTH_DAY + values[i]);
                    Log.e(TAG, "Set for Year, Month, Day");
                    break;
                case 7:
                    BluetoothService.write(mContext, Commands.S_HOUR_MIN_SEC + values[i]);
                    Log.e(TAG, "Set for Hour, Min, Sec");
                    break;
                case 8:
                    BluetoothService.write(mContext, Commands.S_WAKE_TIME + values[i]);
                    Log.e(TAG, "Set for Wake Time");
                    break;
                case 9:
                    BluetoothService.write(mContext, Commands.S_SLEEP_TIME + values[i]);
                    Log.e(TAG, "Set for Sleep time");
                    break;
                case 10:
                    BluetoothService.write(mContext, Commands.S_ROTATE_POS + values[i]);
                    Log.e(TAG, "Set for micro-steps positive rotation");
                    break;
                case 11:
                    BluetoothService.write(mContext, Commands.S_ROTATE_NEG + values[i]);
                    Log.e(TAG, "Set for micro-steps negative rotation");
                    break;
                case 12:
                    BluetoothService.write(mContext, Commands.S_ROT_STATE + values[i]);
                    Log.e(TAG, "Set for Manual Control");
                    break;
            }
        }
    }

    /**                     clearValues()
     * Clears the static variables after the writeSetCommands() method is called
     * in the Settings Activity
     * This is to avoid the values from being appended to on successive connections
     *
     */
    public static void clearValues()
    {
        codes = "";
        values.clear();
    }
}
