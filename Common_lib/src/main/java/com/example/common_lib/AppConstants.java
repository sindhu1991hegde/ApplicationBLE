package com.example.common_lib;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

public class AppConstants {
    public static String SERVER_URI = "ssl://mqttsh.rpsapi.in:8883";
    public static String MQTT_USER_NAME = "admin";
    public static String MQTT_PASSWORD = "admin123";
    // public static String SERVER_URI = "ssl://mqttsh-dev.rpsapi.in:8883";

    public static String MATTRESS_SUBSCRIBE_TOPIC = "THM/STATUS/SM14BD621000005";
    public static String MATTRESS_PUBLISH_TOPIC = "THM/CMD/SM14BD621000005";
    public static String PUBLISH_MATRESS_GET_STATUS = "IOT+CMD=13~";
    public static String MATRRESS_ON = "IOT+CMD=01,1~";
    public static String MATRRESS_OFF = "IOT+CMD=01,0~";
    public static String MATTRESS_RIGHT_POWER = "";

    public static String MATTRESS_MAC_ID = "";
    public static String MATTRESS_S_KEY = "";

    public static String MATTRESS_IS_CONNECTED = "THM/ONLINE/SM14BD621000005";
    // public static String AUTH_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiJIU0gwMDQyMjAiLCJrZXkiOiJiNzQxYWM3NmJhNmM3ZDlkNjZmOTAwNTEwOGI3NDQzNzg4Yzg0ZGMzNDUzMzE3MTMwYTQzNGVmZjNkMDJmNWYwNTAzYmRlNTA1NmNlODgwYWUwNmUyYzU5MzBlMmYzMDQiLCJpYXQiOjE2NzExNzI5OTN9.2b7BipQh7eZAJqbTbJZVLX54cWhFu0L54XoTI3le_R0";
    public static String AUTH_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiJIU0gwMzU4OTkiLCJrZXkiOiI3ZjkxYWJlZTQ5ODliNjA4OTZjZDUxZTM1NDRhYmY2MmM2MjBlZmFhNTZkYjUwMjQxNTQ3YjliN2Y3NDM4MjIyZjZmMWJkZTU3OTFlZjA2NDg0OWNmZTA0NTg4ZDg3OWUiLCJpYXQiOjE2NzIxNDA0NzN9.KpahHXr2wj2g8J3nrreFjIelljQMhHitJKAzGpU_AZM";


    public static String COMMAND_AIR_POWER_ON = "{\"CMD\":\"IOT+CMD=01,1~\"}";
    public static String COMMAND_AIR_POWER_OFF = "{\"CMD\":\"IOT+CMD=01,0~\"}";

    public static String GET_SKEY ="{\"CMD\":\"IOT+CMD=24~\"}";


    /*BLE*/
    public static BluetoothDevice BLUETOOTH_DEVICE;

    public static String BEL_ENC_KEY = "3Lw9S2nE0Af8Zl1q";
    public static String String_GENUINO101_SERVICE_READ =
            "000000ee-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_GENUINO101_serviceRead =
            UUID.fromString(String_GENUINO101_SERVICE_READ);
    public final static String EXTRA_DATA =
            "android-er.EXTRA_DATA";

    public static String String_GENUINO101_SERVICE_WRITE =
            "000000ff-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_GENUINO101_serviceWrite =
            UUID.fromString(String_GENUINO101_SERVICE_WRITE);

    public static String String_GENUINO102_CHARREAD =
            "0000ee01-0000-1000-8000-00805f9b34fb";
    public final static UUID UUID_GENUINO102_charRead =
            UUID.fromString(String_GENUINO102_CHARREAD);

    public static String String_GENUINO102_CHARWRITE =
            "0000ff01-0000-1000-8000-00805f9b34fb";

    public final static UUID UUID_GENUINO102_charWrite =
            UUID.fromString(String_GENUINO102_CHARWRITE);
}
