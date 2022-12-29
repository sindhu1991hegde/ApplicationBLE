package com.example.applicationble.views;

import static com.poc.bluetooth_ble.service.BluetoothServiceClass.ACTION_DATA_AVAILABLE;
import static com.poc.bluetooth_ble.service.BluetoothServiceClass.ACTION_GATT_DISCONNECTED;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.applicationble.R;

import com.example.applicationble.views.AppUtil.APIExecutor;
import com.example.applicationble.views.AppUtil.Status;
import com.example.common_lib.AppConstants;
import com.example.mqtt_module.mqtt_conn.MqttService;
import com.example.mqtt_module.mqtt_conn.SessionManager;
import com.poc.bluetooth_ble.service.BluetoothServiceClass;
import com.poc.bluetooth_ble.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceConfigureActivity extends AppCompatActivity {

    /*MQTT*/
    SessionManager sessionManager;
    MqttService mqttService;
    /*----*/
    TextView tv1,tv2,tv3,tv4,tv;
    private boolean notNowCheck = false;
    BluetoothAdapter mBluetoothAdapter;
    private static final int RQS_ENABLE_BLUETOOTH = 123;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothServiceClass mBluetoothLeService;
    String deviceName,displayData;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,

    };
    private StringBuilder name_password = new StringBuilder();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothconfigure);
        tv = (TextView) findViewById(R.id.tv);
        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);

        mHandler = new Handler();
        tv4.setText("Device Status");
        //request permission
        if (!Utils.hasPermissions(getApplicationContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(DeviceConfigureActivity.this, PERMISSIONS, PERMISSION_ALL);
        }
        else {
            mBluetoothLeService.closeGatt();
            startBluetooth();
        }

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothLeService.sendToBLEJsonUpateData(AppConstants.GET_SKEY);
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                String ssidLength = "";
                String passwordLength = "";
                String spinTxt = "Radiant_Dev";
                String password = "rpsdev@BLR2017";

                if (spinTxt.length() < 10) {
                    ssidLength = "0" + spinTxt.length();
                } else {
                    ssidLength = "" + spinTxt.length();
                }


                if (password.length() < 10) {
                    passwordLength = "0" + password.length();
                } else {
                    passwordLength = "" + password.length();
                }
                //air purifier
                name_password = new StringBuilder();
                name_password.append("{\"");
                name_password.append("CMD\":");
                name_password.append("\"IOT+CMD=14,");
                name_password.append(ssidLength + ",");
                name_password.append(spinTxt + ",");
                name_password.append(passwordLength + ",");
                name_password.append(password.trim() + "~" + "\"}");
                if (!notNowCheck) {
                    notNowCheck = true;
                  mBluetoothLeService.sendToBLEJsonUpateData(name_password.toString());
                }

            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if( AppConstants.BLUETOOTH_DEVICE != null && Utils.isConnected(AppConstants.BLUETOOTH_DEVICE)) {
                    mBluetoothLeService.sendToBLEJsonUpateData(AppConstants.COMMAND_AIR_POWER_ON);
                }
                else {
                    mqttService.commandControl(AppConstants.MATRRESS_ON, getApplicationContext());
                }

            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeviceConfigureActivity.this, BluetoothActionActivity.class));

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ALL) {
            mBluetoothLeService.closeGatt();
            startBluetooth();
        }
    }

    @SuppressLint("MissingPermission")
    private void startBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intent1 = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent1, RQS_ENABLE_BLUETOOTH);
        } else {
            scanLeDevice(true);
        }
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RQS_ENABLE_BLUETOOTH) {
            scanLeDevice(true);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeService.stopLE();
                }
            }, SCAN_PERIOD);
            mBluetoothLeService.startLE();
        } else {
            mBluetoothLeService.stopLE();
        }
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothServiceClass.LocalBinder) service).getService();
            mBluetoothAdapter=mBluetoothLeService.getBluetoothAdapter(getApplicationContext());
            if (!mBluetoothLeService.initialize()) {
                finish();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        deviceName = "LIV - MATTRESS";
        try {
            Intent gattServiceIntent = new Intent(this, BluetoothServiceClass.class);
            gattServiceIntent.putExtra("DEVICE",deviceName);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        } catch (Exception e) {
        }
        sessionManager = new SessionManager(DeviceConfigureActivity.this);
        callCertificateApi();
    }
    private final ServiceConnection mServiceConnection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService = ((MqttService.LocalBinder) iBinder).getService();
            mqttService.mqttConnection(DeviceConfigureActivity.this);
            Log.e("SINDHU", "mservice");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private void callCertificateApi() {
        Call<Status> call = APIExecutor.getApiServiceWithHeader(getApplicationContext()).getMqttCertificates();
        call.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {

                if (response.body() != null) {
                    Status response1 = response.body();
                    Log.e("responsecert", response1.getStatus());
                    if (response1.getStatus().equalsIgnoreCase("success")) {

                        sessionManager.storeCertificate(response1.getData().getCertificates().getCaCrt(), response1.getData().getCertificates().getClientCrt(), response1.getData().getCertificates().getClientKey());

                        Intent serviceIntent = new Intent(DeviceConfigureActivity.this, MqttService.class);
                        bindService(serviceIntent, mServiceConnection1, BIND_AUTO_CREATE);
                        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());

                    }
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {

            }
        });

    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothServiceClass.ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(MqttService.ACTION);
        return intentFilter;
    }

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (BluetoothServiceClass.ACTION_GATT_CONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(),"GATT CONNECTED",Toast.LENGTH_SHORT).show();
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(),"GATT DISCONNECTED",Toast.LENGTH_SHORT).show();
            }else if(ACTION_DATA_AVAILABLE.equals(action))
            {
                displayData=intent.getStringExtra(AppConstants.EXTRA_DATA);
                updateUI(displayData);
                Log.e("SINDHU","from BLE");

            }
            else if(mqttService.ACTION.equals(action))
            {
                displayData = intent.getStringExtra("action");
                updateUI(displayData);
                Log.e("SINDHU","from MQTT");

            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(gattUpdateReceiver);
    }

    private void updateUI(String displayData)
    {
        try {
            JSONObject json = new JSONObject(displayData);

            if(json.has("WV")) {

                String[] wifiVersion = json.getString("WV").split(",");

                JSONArray jsonArray = json.getJSONArray("ST");

                JSONObject jsonRightMattress = jsonArray.getJSONObject(0);
                String[] rightMattress = jsonRightMattress.getString("RT").split(",");
                AppConstants.MATTRESS_RIGHT_POWER = rightMattress[0];
                if(AppConstants.MATTRESS_RIGHT_POWER.equalsIgnoreCase("1")) {
                    tv4.setText("Right Mattress status: ON");
                }
                else{
                    tv4.setText("Right Mattress status: OFF");
                }

                JSONObject jsonLeftMattress = jsonArray.getJSONObject(1);
                String[] leftMattress = jsonLeftMattress.getString("LT").split(",");

            }
            else if (json.has("{\"SKEY\"")) {

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            AppConstants.MATTRESS_S_KEY = json.getString("SKEY");
                            AppConstants.MATTRESS_MAC_ID = json.getString("MAC");

                            Log.e("SINDHU",   AppConstants.MATTRESS_S_KEY+"=="+   AppConstants.MATTRESS_MAC_ID);
                            //  gotoDeviceDetails();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 2000);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}