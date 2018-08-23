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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.jc.newlaserscarecrow.R;
import com.example.jc.newlaserscarecrow.services.BluetoothService;
import com.example.jc.newlaserscarecrow.services.DataLogService;
import com.example.jc.newlaserscarecrow.application.CommandInterface;
import com.example.jc.newlaserscarecrow.application.DevDataTransfer;

import java.util.Map;

public class MotionControlActivity extends Fragment implements CommandInterface {
    private static final String TAG = "motionControlActivity";
    private int[] newValues;
    private Context mContext;
    private Button rotationStateButton, minButton, midButton, maxButton, applyButton;
    private SeekBar motionSpeedBar, motionRotationBar, angleMinimumBar, angleRangeBar;
    private BottomNavigationView mbottomNavigation;
    private DataLogService DLS;
    private Map<String, int[]> map;
    private boolean setupComplete = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.motion_control, container, false);

        //setTitle("Wake/Sleep Settings");
        // Sets up the activity
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.motion_control);
        mContext = getContext();

        map = DevDataTransfer.createHashtable();


        // Find the seek bar widgets
        motionSpeedBar = view.findViewById(R.id.motionSpeedBar);
        motionRotationBar = view.findViewById(R.id.motionRotationBar);
        angleMinimumBar = view.findViewById(R.id.angleMinimumBar);
        angleRangeBar = view.findViewById(R.id.angleRangeBar);

        motionRotationBar.setProgress(512);

        motionSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                DevDataTransfer.demoRot(mContext);
                DevDataTransfer.clearValues();
            }
        });


        // Find the button widgets and give them click functionality
        rotationStateButton = view.findViewById(R.id.rotationStateButton);
        rotationStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        minButton = view.findViewById(R.id.minButton);
        minButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        midButton = view.findViewById(R.id.midButton);
        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        maxButton = view.findViewById(R.id.maxButton);
        maxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        applyButton = view.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSettings();
            }
        });

        setArrayValues();
        setWidgets();
        return view;

    }


    private void setWidgets()
    {
        // Set values from map to seek bar widgets
        try{
            Log.i("Find me", "set widgets called");
            int a = map.get(Commands.L_STEPPER_SPEED)[0];
            a *= 6.82; //This is because S101 is 0-1023 but L101 is 0-150. 1024/150 = 6.82
            motionSpeedBar.setProgress(a);
            //motionRotationBar.setProgress(map.get(Commands.L_ROTATE_NEG));
            angleMinimumBar.setProgress(map.get(Commands.L_PITCH_MIN)[0]);
            angleRangeBar.setProgress(map.get(Commands.L_PITCH_RANGE)[0]);
            rotationStateButton.setText(map.get(Commands.L_ROT_STATE)[0]);
            if (map.get(Commands.L_ROT_STATE)[0] == 1) {
                rotationStateButton.setText("Paused");
            }
            //if (map.get(Commands.L_STATE) == 0) {
            else    rotationStateButton.setText("Playing");
            //}
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
        newValues = new int[Commands.NUM_COMMANDS];
        newValues[0] = motionSpeedBar.getProgress();
        newValues[1] = angleMinimumBar.getProgress();
        newValues[2] = angleRangeBar.getProgress();
        newValues[3] = motionRotationBar.getProgress();
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

    /**                     --onBackPressed()--
     *  Returns to the parent activity (ScanActivity) if the back button is pressed
     *  Also, disconnects from the current device
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setWidgets();

    }

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
        setWidgets();
        System.out.print("Find me " + map.get(Commands.L_ROT_STATE)[0]);
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



