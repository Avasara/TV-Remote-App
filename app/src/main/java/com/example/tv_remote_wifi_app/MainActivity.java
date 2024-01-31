package com.example.tv_remote_wifi_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManagerListener;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.command.ServiceCommandError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DiscoveryManagerListener {

    public DiscoveryManager discoveryManager;
    private ArrayList<String> devicesNameList;
    private ArrayList<String> deviceKeyList;
    private ArrayList<String> selectedDeviceList;
    private ArrayAdapter<String> devicesAdapter;

    //deviceList array holding deviceKeys to ensure we aren't adding duplicates.
    private ArrayList<ConnectableDevice> devicesList;
    private ConnectableDeviceListener deviceListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The array that will hold the deviceKey for discovered devices and their names that'll be displayed to the user.
        devicesNameList = new ArrayList<>();
        devicesList = new ArrayList<>();
        deviceKeyList = new ArrayList<>();
        selectedDeviceList = new ArrayList<>();

        //Adapter will display devices to the user using the specified layout, the ID of the layout and the deviceName Array.
        devicesAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.list_item, devicesNameList);

        //Initializing Discovery Manager and setting up the listener to begin the discovery process
        DiscoveryManager.init(this);
        discoveryManager = DiscoveryManager.getInstance();
        discoveryManager.setPairingLevel(DiscoveryManager.PairingLevel.ON);
        discoveryManager.registerDefaultDeviceTypes();
        discoveryManager.addListener(MainActivity.this);
        DiscoveryManager.getInstance().start();

        showOptions();
        Log.d("Device-Pairing-Level", "The device pairing level is: " + discoveryManager.getPairingLevel());


        deviceListener = new ConnectableDeviceListener() {
            @Override
            public void onDeviceReady(ConnectableDevice device) {
                final Button button = findViewById(R.id.home_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Log.d("Home-Clicked", "The button was clicked");
                        }
                        catch (Exception exception) {
                            Log.e("Home-Key-Error", "The key didn't go through");
                        }
                    }
                });
            }

            @Override
            public void onDeviceDisconnected(ConnectableDevice device) {
                Log.d("Device-Listener" , "We disconnected from the device");
            }

            @Override
            public void onPairingRequired(ConnectableDevice device, DeviceService service, DeviceService.PairingType pairingType) {
                Log.d("Device-Pairing", "We need to pair with them.");
                Log.d("Device-Pairing", "Connected to " + device.getIpAddress());

                switch (pairingType) {
                    case FIRST_SCREEN:
                    case MIXED:
                    case NONE:
                        Log.d("Device-Pairing", "First Screen");
                        pairingAlertDialog();
                        break;

                    default:
                        Log.d("Device-Pairing", "Man the default? Fuck that.");
                        break;
                }
            }

            @Override
            public void onCapabilityUpdated(ConnectableDevice device, List<String> added, List<String> removed) {
                Log.d("Device-Listener", "The device listener detected a capabilities update in: " + device.getFriendlyName());
            }

            @Override
            public void onConnectionFailed(ConnectableDevice device, ServiceCommandError error) {
                Log.e("Device-Listener", "The connection to the device failed. I'm sorry");
            }
        };

    }

    void connectToDevice(String deviceSelected) {
        //Attempting to connect to the device based on the name in the deviceSelected string.
        try {
            for (ConnectableDevice device : devicesList) {
                if (device.getFriendlyName().equals(deviceSelected)) {
                    //If all goes well. It connects. If not, eh
                    Log.d("Device-ConnectToDevice", "Beginning connection to device: " + device.getFriendlyName());

                    //Handler to delay the connection by 5 seconds. Giving ConnectSDK enough time to add in all the services.
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Log.d("Device-connectToDevice" , "The device is: " + device);
                                device.addListener(deviceListener);
                                device.setPairingType(PairingType.FIRST_SCREEN);
                                device.connect();
                            }
                            catch (Exception exception) {
                                Log.e("Device-connectToDevice-Error", "The connection failed due to: " + exception.getMessage());
                            }
                        }
                    }, 20000);
                } else {
                    Log.d("Device-ConnectToDevice", "The selected device does not match up. DeviceName = " + device.getFriendlyName() + ". selectedDevice Name = " + deviceSelected);
                }
            }
        }
        catch (Exception exception) {
            Log.e("Device-connectToDevice-Error", "Failed to connect to the selected device");
        }
    }

    void showOptions() {
        //Alert dialog to show the users the list of devices
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialogTheme));
        builder.setTitle("Select a Device");
        builder.setCancelable(false);
        builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                discoveryManager.start();
                builder.show();
            }
        });

        builder.setAdapter(devicesAdapter, (dialog, which) -> {
            //Handling device selection
            String selectedDevice = devicesNameList.get(which);
            selectedDeviceList.add(selectedDevice);
            Log.d("Selected-Device" , "The device selected is" + selectedDevice);
            //Once a device is selected, the connectToDevice method is called on its name and it handles the rest.
            connectToDevice(selectedDevice);
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //The alert dialog that should pop up prompting the user to accept the connection on their tv.
    void pairingAlertDialog() {
        AlertDialog.Builder pairingAlertDialog = new AlertDialog.Builder(this);
        pairingAlertDialog.setTitle("Pairing with TV");
        pairingAlertDialog.setMessage("Please confirm your selection on your TV");
        pairingAlertDialog.setPositiveButton("Okay", null);

        //This is lambda. Basically shortens the code? I'll have to learn more about that. It's very neat.
        pairingAlertDialog.setNegativeButton("Nope", (dialog, which) -> dialog.dismiss());
       pairingAlertDialog.show(); //Does both create and show in one.
    }

    //deviceKey for preventing services posing as devices.
    String deviceKey;
    @Override
    public void onDeviceAdded(DiscoveryManager manager, ConnectableDevice device) {
        //Making it look encryptedy
        deviceKey = "://" + device.getFriendlyName() + "/:/" + device.getIpAddress() + "//:";

        //The methods that just may save us :)

        //JSONObject adiutrix = device.toJSONObject();
        //String germina = device.getConnectedServiceNames();

       try {
            if (deviceKeyList.contains(deviceKey)) {
                Log.d("Device-KeyExists", "A device with key " + deviceKey + " already exists. Adding service to Device");
            } else {
                //If the deviceKey is not in the list, then all this happens.
                devicesList.add(device);
                deviceKeyList.add(deviceKey);
                devicesNameList.add(device.getFriendlyName());
                devicesAdapter.notifyDataSetChanged();
                Log.d("Device-Added", "A new device was added to the list. Device: " + deviceKey);
            }
        }
        catch (Exception exception) {
            Log.e("Device-Error", "A error was caught with a device. Error: " + exception.getMessage());
        }
    }

    @Override
    public void onDeviceUpdated(DiscoveryManager manager, ConnectableDevice device) {
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeviceRemoved(DiscoveryManager manager, ConnectableDevice device) {
        //Handling for device disconnection is more robust now. Accounting for devices that go off then come back on
        devicesNameList.remove(device.getFriendlyName());
        Log.d("Disconnection", "Device: " + device + " has been removed");
        devicesList.remove(device);
        devicesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDiscoveryFailed(DiscoveryManager manager, ServiceCommandError error) {
        // Handle discovery failure
        Log.e("Error", "Hmm, an error has occurred due to: " + error.getMessage());
    }
}