package com.example.camera.util;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SubscribeClient {
    //太坑了，设置为tcp://120.46.149.254:8083就会出错
    String HOST = "ws://120.46.149.254:8083/mqtt";
    String TOPIC;
    int qos = 1;
    String clientid = "mqttsubscribe_";
    String userName = "admin";
    String passWord = "public";
    static int cnt=0;
    private MqttClient client ;
    // MQTT的连接设置
    MqttConnectOptions options = new MqttConnectOptions();

    /**
     * new MqttCallback() {
     *
     *                 public void connectionLost(Throwable cause) {
     *                     System.out.println("connectionLost");
     *                 }
     *
     *                 public void messageArrived(String topic, MqttMessage message) throws Exception {
     *                     System.out.println("topic:"+topic);
     *                     System.out.println("Qos:"+message.getQos());
     *                     System.out.println("message content:"+new String(message.getPayload()));
     *
     *                 }
     *
     *                 public void deliveryComplete(IMqttDeliveryToken token) {
     *                     System.out.println("deliveryComplete---------"+ token.isComplete());
     *                 }
     *
     *             }
     * @param callback 自定义回调函数
     */
    public SubscribeClient(String topic,MqttCallback callback){
        try {
            clientid+=cnt++;
            TOPIC=topic;
            // host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存,这个就是异步的
            client= new MqttClient(HOST, clientid, new MemoryPersistence());


            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(30);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(15);
            // 设置回调函数
            client.setCallback(callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void connect()
    {
        try {
            if(!client.isConnected()){
                client.connect(options);
            }
            //订阅消息
            client.subscribe(TOPIC, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect()
    {
        try {
            if(client.isConnected())
                client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MqttException {

        MqttCallback mc=new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("message content:"+new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };
        SubscribeClient s= new SubscribeClient("test",mc);

    }

}
