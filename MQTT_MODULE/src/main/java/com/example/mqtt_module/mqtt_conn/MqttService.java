package com.example.mqtt_module.mqtt_conn;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;


import com.example.common_lib.AppConstants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLSocketFactory;

public class MqttService extends Service {

    private final IBinder mBinder = new LocalBinder();
    MqttAndroidClient client;
    String clientId = MqttClient.generateClientId();
    public static String ACTION = "action";
    private int statusCheck = 0,onlineCheck=0;
    private String topic;

    public class LocalBinder extends Binder {
        public MqttService getService() {

            return MqttService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void mqttConnection(Context con){

        try {
            client = new MqttAndroidClient(con, AppConstants.SERVER_URI, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(AppConstants.MQTT_USER_NAME);
            options.setPassword(AppConstants.MQTT_PASSWORD.toCharArray());
            options.setConnectionTimeout(20);
            options.setKeepAliveInterval(20);
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                SSLSocketFactory socketFactory = SslUtil.getSSLSocketFactory(AppConstants.MQTT_PASSWORD,con);
                options.setSocketFactory(socketFactory);
            } else {
                SSLSocketFactory socketFactory = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    socketFactory = SslUtil.getSSLSocketFactory(AppConstants.MQTT_PASSWORD,con);
                }
                //  SSLSocketFactory socketFactory = SslService.trustCert(context, "admin123").getSocketFactory();
                options.setSocketFactory(socketFactory);
            }

            System.out.println("starting connect the server...");
            Log.e("SINDHU", "starting connect the server");

            try {

                client.connect(options, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                        Log.e("MqttServer State","success-->"+asyncActionToken.getTopics());

                        try {
                            /*if(onlineCheck == 0 ) {
                                onlineCheck =1;
                                topic = (AppConstants.MATTRESS_IS_CONNECTED);
                            }else {
                                topic = (AppConstants.MATTRESS_SUBSCRIBE_TOPIC);

                            }*/
                            topic = (AppConstants.MATTRESS_SUBSCRIBE_TOPIC);
                            Log.e("topic", topic);
                            client.subscribe(topic, 0, null, new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    Log.e("Mqtt", "Subscribed!");
                                    if(onlineCheck == 1 && statusCheck == 0){

                                            JSONObject json = new JSONObject();
                                            try {
                                                statusCheck = 1;
                                                Log.e("getting", "refresh");
                                                json.put("CMD", AppConstants.PUBLISH_MATRESS_GET_STATUS);
                                                publishToDevice(json.toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                    }

                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    Log.e("Mqtt", "Subscribed fail!");
                                }
                            });

                        } catch (Exception e) {
                            Log.w("Mqtt", "Publish: " + AppConstants.SERVER_URI + e.toString());
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("Mqtt", "Failed to connect to: " + AppConstants.SERVER_URI  + exception.toString());
                    }
                });


                client.setCallback(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean b, String s) {
                        Log.e("mqtt", "SAR" + s);
                    }

                    @Override
                    public void connectionLost(Throwable throwable) {
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                        String result = mqttMessage.toString();
                        Log.e("SINDHU res from MQTT",result);
                        broadcastUpdate(ACTION,result);

                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                           Log.e("Mqtt SINDHU","deliver");
                    }
                });

            } catch (MqttException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public String publishToDevice(String message) {
        try {
            MqttMessage mesg = new MqttMessage();
            mesg.setPayload(message.getBytes());
            String topicName = (AppConstants.MATTRESS_PUBLISH_TOPIC);
            Log.e("SINDHU--", topicName.toString());
            client.publish(topicName, mesg);
            Log.e("SINDHU--", "success");
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SINDHU--", "fail");
            return "Fail";

        }
    }
    public void commandControl(String command,Context context)
    {
                  JSONObject json = new JSONObject();
            try {
                json.put("CMD",command);
                Log.e("SINDHU", json.toString());
                publishToDevice(json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }



    private void broadcastUpdate(final String action,
                                 final String data) {
        Log.e("BluetoothData== > ", data);
        final Intent intent = new Intent(action);
        Log.w(TAG, "broadcastUpdate()");
        intent.putExtra("action", data);
        sendBroadcast(intent);
    }




}
