import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by drashko on 15.05.16.
 */
public class AmqMessageListener implements MessageListener {

    private Message lastMessage;
    CountDownLatch startSignal = new CountDownLatch(1);

    @Override
    public void onMessage(Message message) {
        System.out.println(message.getClass().getName());
        String mqttMessage = new MqttMessage(message.getBody()).toString();
        System.out.println(mqttMessage);
        lastMessage = message;
        startSignal.countDown();
    }

    public Message receive() throws InterruptedException {
        startSignal.await();
        return lastMessage;
    }
}
