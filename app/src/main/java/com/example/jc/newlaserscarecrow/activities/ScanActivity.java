package com.example.jc.newlaserscarecrow.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jc.newlaserscarecrow.R;
import com.example.jc.newlaserscarecrow.services.BluetoothService;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Created by Andrew on 2/13/18.
 */

public class ScanActivity extends Fragment
{
    private static final String TAG = "ScanActivity";
    private Context             mContext;
    private ListView            mDeviceList;
    private SimpleAdapter       listAdapter;
    private String              connectionAddress;
    private BluetoothAdapter    mBluetoothAdapter;
    private ArrayList<HashMap<String,String>> myList = new ArrayList<HashMap<String,String>>();
    private BottomNavigationView mbottomNavigation;

    /**              --onCreate(...)--
     *   Executes when the ScanActivity is launched.
     *
     *  Main responsibility is to...
     * -Get all paired devices and load them into device list
     * -Allow the user to choose a paired device and connect to it
     * -On successful connection, SettingsActivity will launch
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_scan, container, false);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scan);
        //getSupportActionBar().setHomeButtonEnabled(false);
        mContext = getActivity();

        // Sets up the paired device list
        mDeviceList = view.findViewById(R.id.device_list);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        getPairedDevices();
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
        return view;
    }

    private void loadListVIew() {
        mBluetoothAdapter.cancelDiscovery();
        if(myList.size() > 0)
        {
            // Creates a simple adapter with address strings and name strings, then sets that to list
            listAdapter = new SimpleAdapter(mContext, myList, R.layout.activity_scan_list_items, new String[] { "address","name"},
                    new int[] {R.id.address, R.id.name});
            mDeviceList.setAdapter(listAdapter);
        }
        if (BluetoothService.connected) {
            //mbottomNavigation.setVisibility(View.VISIBLE);
        }
        else
            Log.i(TAG, "Paired Devices array error.");

        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                //Log.e("Find this", "onItemClick called");
                connectionAddress = ((HashMap<String,String>)adapterView.getItemAtPosition(i)).get("address");
                Log.e("Find this", "" + connectionAddress);

                if (BluetoothService.connected) {
                    BluetoothService.read(mContext);
                }
                else {
                    BluetoothService.connect(mContext, connectionAddress);
                }

                //mbottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismiss progress dialog
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                HashMap<String, String> btDevice = new HashMap<>(); //need to use a hashmap for the multiline display
                if (device.getName().substring(0, 3).equals("LS-")) {
                    btDevice.put("name", device.getName());
                    btDevice.put("address", device.getAddress());
                    if (!myList.contains(btDevice)) {
                        myList.add(btDevice);
                    }
                    device.createBond();
                }

                loadListVIew();
                Log.e("Connected to:", device.getName());

                Log.i(TAG, "Paired device found: " + device.getAddress());
            }
        }
    };


    /*@Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }*/

    /**                             --getPairedDevices()--
     *
     *  Bluetooth serial communication seems to need the device to be paired to your
     *  phone before communication starts. This activity enables bluetooth if it's disabled,
     *  then gets a list of each bluetooth address in your paired devices.
     */
    private void getPairedDevices()
    {
        // Gets the Bluetooth adapter, used to perform any Bluetooth task
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Log.d(TAG, "Bluetooth adaptor is NULL.");
        }

        // Checks if Bluetooth is enabled, enables it if not
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        // Gets paired devices, lists the addresses, then stores them in array
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                HashMap<String, String> btDevice = new HashMap<>(); //need to use a hashmap for the multiline display
                Log.i(TAG, "Paired device found: " + device.getAddress());
                if (device.getName().substring(0, 3).equals("LS-")) {
                    btDevice.put("name", device.getName());
                    btDevice.put("address", device.getAddress());
                    myList.add(btDevice);
                }
            }
        }
    }

    /**                     --onBackPressed()--
     *  Returns to the parent activity (MainActivity) if the back button is pressed
     */
    /*@Override
    public void onBackPressed()
    {
        Log.e(TAG, "Back pressed. Navigating to Home");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
        finish();
    }*/

    /**               --onOptionsItemSelected(...)--
     *
     * Same functionality as pressing the back button. This will open the parent activity (MainActivity)
     *
     * @param item: Item from the action bar that was selected, in this case the back button
     * @return
     */

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.e(TAG, "Home item pressed. Navigating to Home");
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }*/

}
