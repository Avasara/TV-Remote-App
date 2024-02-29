package com.example.tv_remote_wifi_app;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.command.ServiceCommandError;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DiscoveryManagerListener {

    public DiscoveryManager discoveryManager;

    //Array holding device names that will be displayed to the user.
    private ArrayList<String> devicesIPList;

    //deviceList array holding deviceKeys to ensure we aren't adding duplicates.
    private ArrayList<ConnectableDevice> devicesList;

    //Using deviceKey, it prevents the addition of duplicates.
    private ArrayList<String> deviceKeyList;

    //Making the selectedDevice available for all other methods that might have a need for it.
    private ArrayList<String> selectedDeviceList;

    //Adapter facilitates display of devices to the user using the specified layout the devicesNameList Array.
    private ArrayAdapter<String> devicesAdapter;
    private ConnectableDeviceListener deviceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesIPList = new ArrayList<>();
        devicesList = new ArrayList<>();
        deviceKeyList = new ArrayList<>();
        selectedDeviceList = new ArrayList<>();

        devicesAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.list_item, devicesIPList);

        //Initializing Discovery Manager and setup for Discovery
        DiscoveryManager.init(this);
        discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.setPairingLevel(DiscoveryManager.PairingLevel.ON);
        discoveryManager.registerDefaultDeviceTypes();
        discoveryManager.addListener(MainActivity.this);
        discoveryManager.start();

        Log.d("Device-Pairing-Level", "The device pairing level is: " + discoveryManager.getPairingLevel());

        showOptions();

        deviceListener = new ConnectableDeviceListener() {
            @Override
            public void onDeviceReady(ConnectableDevice connectableDevice) {

            }

            @Override
            public void onDeviceDisconnected(ConnectableDevice connectableDevice) {

            }

            @Override
            public void onPairingRequired(ConnectableDevice connectableDevice, DeviceService deviceService, DeviceService.PairingType pairingType) {

            }

            @Override
            public void onCapabilityUpdated(ConnectableDevice connectableDevice, List<String> list, List<String> list1) {
                Log.d("Device-Listener", "The device was updated in capabilities");
            }

            @Override
            public void onConnectionFailed(ConnectableDevice connectableDevice, ServiceCommandError serviceCommandError) {

            }
        };

    }

    void connectToDevice(String selectedDeviceIpAddress) {
        try {
            //Creating a new device and assigning the selected device to it.
            ConnectableDevice device = discoveryManager.getDeviceByIpAddress(selectedDeviceIpAddress);

            Log.d("Device-ConnectToDevice", "Beginning connection to device: " + device.getFriendlyName());

                    //Handler delaying the connection by 15 seconds. Giving ConnectSDK enough time to add in all the services.
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            Log.d("Device-connectToDevice" , "The device is: " + device);
                            device.addListener(deviceListener);
                            device.setPairingType(null);
                            device.connect();
                        }
                        catch (Exception exception) {
                            Log.e("Device-connectToDevice-Error", "The connection failed due to: " + exception.getMessage());
                        }
                    }, 15000);
        }
        catch (Exception exception) {
            Log.e("Device-connectToDevice-Error", "Failed to connect to the selected device");
        }
    }

    void showOptions() {
        //Displaying list of devices to users.
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setTitle("Select a Device");
        builder.setCancelable(false);
        builder.setPositiveButton("Refresh", (dialog, dev) -> {
            discoveryManager.start();
            builder.show();
        });

        builder.setAdapter(devicesAdapter, (dialog, index) -> {

            //Getting the selected device, updating the array and connecting to it.
            String selectedDevice = devicesIPList.get(index);
            selectedDeviceList.add(selectedDevice);
            Log.d("Selected-Device" , "The device selected is " + selectedDevice);
            connectToDevice(selectedDevice);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        //deviceKey for preventing services posing as devices.
        String deviceKey = "://" + device.getFriendlyName() + "/:/" + device.getIpAddress() + "//:";

        if (deviceKeyList.contains(deviceKey)) {
            Log.d("Device-KeyExists", "A device with key " + deviceKey + " already exists. Adding service to Device");
            //Not sure what to put here to add the service since ConnectSDK is supposed to do that automatically.

        }
        else {
           devicesList.add(device);
           deviceKeyList.add(deviceKey);
           devicesIPList.add(device.getIpAddress());
           devicesAdapter.notifyDataSetChanged();
           Log.d("Device-Added", "A new device was added to the list. Device: " + deviceKey);
        }
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        devicesIPList.remove(device.getFriendlyName());
        Log.d("Disconnection", "Device: " + device + " has been removed");
        devicesList.remove(device);
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        Log.e("Error", "Hmm, an error has occurred due to: " + error.getMessage());
    }
}