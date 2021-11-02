package com.example.distributedproject;

import android.app.Application;
import android.util.Log;
import android.view.Menu;

import androidx.lifecycle.MutableLiveData;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PahoClient {
    final String serverUri = "tcp://192.168.80.1:1883";
    final String subscriptionTopic = "light";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "ExampleAndroidClient";

    private MutableLiveData<Float> lastReceivedLightPrice;

    public PahoClient(Application application) {
        clientId = clientId + System.currentTimeMillis();
        mqttAndroidClient = new MqttAndroidClient(application.getApplicationContext(), serverUri, clientId);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                // Not implemented
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String price = new String(message.getPayload());
                Float priceFloat = new Float(price);
                Log.d("PAHO", priceFloat.toString());
                lastReceivedLightPrice.postValue(priceFloat);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setCleanSession(true);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("PAHO", "Failed to connect to: " + serverUri +
                            ". Cause: " + ((exception.getCause() == null)?
                            exception.toString() : exception.getCause()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("PAHO", "Subscribed to: " + subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("PAHO", "Failed to subscribe");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public MutableLiveData<Float> getLightValue() {
        if (lastReceivedLightPrice == null) {
            lastReceivedLightPrice = new MutableLiveData<Float>();
        }
        return lastReceivedLightPrice;
    }
}
