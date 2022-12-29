package com.example.applicationble.views;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applicationble.R;


import com.example.common_lib.AppConstants;
import com.example.mqtt_module.mqtt_conn.MqttService;
import com.poc.bluetooth_ble.service.BluetoothServiceClass;
import com.poc.bluetooth_ble.util.Utils;

public class BluetoothActionActivity extends AppCompatActivity {

    MqttService mqttService;
    Button b1;
    private BluetoothServiceClass mBluetoothLeService;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetoothexample);
        b1=(Button)findViewById(R.id.button);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( AppConstants.BLUETOOTH_DEVICE != null && Utils.isConnected(AppConstants.BLUETOOTH_DEVICE)) {
                    mBluetoothLeService.sendToBLEJsonUpateData(AppConstants.COMMAND_AIR_POWER_OFF);
                }
                else {
                    mqttService.commandControl(AppConstants.MATRRESS_OFF, getApplicationContext());
                }
            }
        });
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((BluetoothServiceClass.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;

        }
    };
    private final ServiceConnection mServiceConnection1 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mqttService = ((MqttService.LocalBinder) iBinder).getService();
            mqttService.mqttConnection(BluetoothActionActivity.this);
            Log.e("SINDHU", "mservice");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        try {
            Intent gattServiceIntent = new Intent(this, BluetoothServiceClass.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        } catch (Exception e) {
        }
        try {
            Intent serviceIntent = new Intent(BluetoothActionActivity.this, MqttService.class);
            bindService(serviceIntent, mServiceConnection1, BIND_AUTO_CREATE);
        } catch (Exception e) {
        }
    }
}