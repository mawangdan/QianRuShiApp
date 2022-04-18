package com.example.camera.util;

import androidx.annotation.NonNull;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class PublicClient {
    int qos = 1;
    String HOST = "ws://120.46.149.254:8083/mqtt";
    String userName = "admin";
    String password = "public";
    String clientId = "mqttpublic_";
    MqttClient sampleClient;
    static int cnt=0;
    private final MqttConnectOptions connOpts = new MqttConnectOptions();

    public void sendMessage(@NonNull String msg, String topic)
    {
        try{
            // 建立连接
            sampleClient.connect(connOpts);
            // 创建消息
            MqttMessage message = new MqttMessage(msg.getBytes());
            // 设置消息的服务质量
            message.setQos(qos);
            // 发布消息
            sampleClient.publish(topic, message);
            // 断开连接
            sampleClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(@NonNull String msg)
    {
        try{
            // 建立连接
            sampleClient.connect(connOpts);
            // 创建消息
            MqttMessage message = new MqttMessage(msg.getBytes());
            // 设置消息的服务质量
            message.setQos(qos);
            // 发布消息
            sampleClient.publish("globalTopic", message);
            // 断开连接
            sampleClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public PublicClient()
    {
        try {
            // 内存存储
            MemoryPersistence persistence = new MemoryPersistence();
            // 创建客户端
            sampleClient = new MqttClient(HOST, clientId+cnt++, persistence);
            // 创建链接参数
            // 在重新启动和重新连接时记住状态
            connOpts.setCleanSession(false);
            // 设置连接的用户名
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new PublicClient().sendMessage("testtopic","food");
        new PublicClient().sendMessage("testtopic","food");
    }
}
