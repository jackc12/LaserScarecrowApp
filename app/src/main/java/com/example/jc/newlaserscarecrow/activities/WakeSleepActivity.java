package com.example.jc.newlaserscarecrow.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jc.newlaserscarecrow.R;
import com.example.jc.newlaserscarecrow.application.CommandInterface;
import com.example.jc.newlaserscarecrow.application.DevDataTransfer;
import com.example.jc.newlaserscarecrow.services.BluetoothService;

import java.util.Map;

public class WakeSleepActivity extends Fragment {
    private static final String TAG = "wakeSleepActivity";
    private int[] newValues;
    private Context mContext;
    private SeekBar lightSensorThresholdBar;
    private Spinner wakeAtHours, wakeAtMinutes, sleepAtHours, sleepAtMinutes;
    private TextView currentLight, lightSensorThreshold, currentTime;
    private RadioButton radioLight, radioWakeSleep;
    private BottomNavigationView mbottomNavigation;
    private Map<String, int[]> map;
    private Button applyButton;
    private Boolean radioLightBool, radioWakeSleepBool;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wake_sleep, container, false);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.wake_sleep);
        mContext = getContext();

        map = DevDataTransfer.createHashtable();

        // Find the seek bar widgets
        lightSensorThresholdBar = view.findViewById(R.id.lightSensorThresholdBar);

        // Find text views
        currentLight = view.findViewById(R.id.currentLight);
        lightSensorThreshold = view.findViewById(R.id.lightSensorThreshold);
        currentTime = view.findViewById(R.id.currentTime);

        // Find the radio buttons
        radioLight = view.findViewById((R.id.radioLight));
        radioWakeSleep = view.findViewById(R.id.radioWakeSleep);

        radioLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioLightBool = !radioLightBool;
                radioLight.setChecked(true);
                radioWakeSleep.setChecked(false);
            }
        });
        radioWakeSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioWakeSleepBool = !radioWakeSleepBool;
                radioWakeSleep.setChecked(true);
                radioLight.setChecked(false);
            }
        });

        wakeAtHours = view.findViewById(R.id.sleepHour);
        wakeAtMinutes = view.findViewById(R.id.sleepMinute);
        sleepAtHours = view.findViewById(R.id.wakeHour);
        sleepAtMinutes = view.findViewById(R.id.wakeMinute);


        ArrayAdapter<CharSequence> adapterHours = ArrayAdapter.createFromResource(mContext,
                R.array.hours, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterMinSec = ArrayAdapter.createFromResource(mContext,
                R.array.minutesSeconds, android.R.layout.simple_spinner_item);

        adapterHours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMinSec.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        wakeAtHours.setAdapter(adapterHours);
        wakeAtMinutes.setAdapter(adapterMinSec);
        sleepAtHours.setAdapter(adapterHours);
        sleepAtMinutes.setAdapter(adapterMinSec);

        applyButton = view.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        setArrayValues();
        setWidgets();


        lightSensorThresholdBar.setOnSeekBarChangeListener(new yourListener());
        return view;
    }

    private class yourListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // Log the progress
            Log.d("DEBUG", "Progress is: "+progress);
            //set textView's text
            lightSensorThreshold.setText(""+progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }


    private void setWidgets()
    {
        // Set values from map to seek bar widgets
        try{
            currentLight.setText("" + map.get(CommandInterface.Commands.L_CURRENT_LIGHT)[0]);
            lightSensorThreshold.setText("" + map.get(CommandInterface.Commands.L_LIGHT_THRES)[0]);
            currentTime.setText("" + map.get(CommandInterface.Commands.L_HOUR_MIN_SEC)[0] + ":" + map.get(CommandInterface.Commands.L_HOUR_MIN_SEC)[1] + ":" + map.get(CommandInterface.Commands.L_HOUR_MIN_SEC)[2]);
            lightSensorThresholdBar.setProgress(map.get(CommandInterface.Commands.L_LIGHT_THRES)[0]);
            if (map.get(CommandInterface.Commands.L_CYCLE_MODE)[0] == 1) {
                radioWakeSleepBool = true;
                radioLightBool = false;

            }
            else if (map.get(CommandInterface.Commands.L_CYCLE_MODE)[0] == 0) {
                radioWakeSleepBool = false;
                radioLightBool = true;
            }
            else {
                radioWakeSleepBool = false;
                radioLightBool = false;
            }
            radioWakeSleep.setChecked(radioWakeSleepBool);
            radioLight.setChecked(radioLightBool);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error: " + e);
        }
    }

    /**                 setArrayValues()
     *  The newValues array will be the values that are used to
     *  write the send commands to the Arduino. This is where they
     *  are set.
     *  Called in onCreate() and on updateSettings()
     */
    private void setArrayValues()
    {
        newValues = new int[CommandInterface.Commands.NUM_COMMANDS];
        /*newValues[0] = motionSpeedBar.getProgress();
        newValues[1] = angleMinimumBar.getProgress();*/
        newValues[5] = lightSensorThresholdBar.getProgress();
        newValues[11] = 0;
    }


    /**                 updateSettings()
     *  Updates the settings on the Arduino by taking the values from the
     *  newValues array and sending them to writeSetCommands(), where they
     *  are added to the set command string and sent to the Arduino.
     *
     */
    private void updateSettings()
    {
        Log.i(TAG, "Logging new settings...");
        setArrayValues();

        if(newValues != null)
        {
            DevDataTransfer.writeSetCommands(mContext, newValues);
            DevDataTransfer.clearValues();
        }

        Toast.makeText(mContext, "Scarecrow Updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setWidgets();

    }

    /**                     --onBackPressed()--
     *  Returns to the parent activity (ScanActivity) if the back button is pressed
     *  Also, disconnects from the current device
     */
    /*@Override
    public void onBackPressed()
    {
        DevDataTransfer.clearValues();
        BluetoothService.disconnect(mContext);
        Log.e(TAG, "Back pressed. Navigating to " + getParentActivityIntent());
        Intent intent = this.getParentActivityIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        super.onBackPressed();
        Toast.makeText(mContext, "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy()
    {
        BluetoothService.disconnect(mContext);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.print("Find me " + map.get(CommandInterface.Commands.L_ROT_STATE)[0]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Log.e(TAG, "Home item pressed. Navigating to " + getParentActivityIntent());
                BluetoothService.disconnect(mContext);
                Intent intent = this.getParentActivityIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }*/
}



