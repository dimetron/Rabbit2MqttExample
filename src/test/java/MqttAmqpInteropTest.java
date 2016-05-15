import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by drashko on 15.05.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/context.xml"})
public class MqttAmqpInteropTest {

    private static final String clientId = "spring-client-id";

    @Autowired
    RabbitTemplate template;

    @Autowired
    private DefaultMqttPahoClientFactory mqttClientFactory;

    @Autowired
    private AmqMessageListener amqMessageListener;

    @Test
    public void testAmqpMessage2Mqtt() {
        String messagePayload = "Message from Amqp !!!";

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();

        //queue to test messages
        QueueChannel outputChannel = new QueueChannel();

        MqttPahoMessageDrivenChannelAdapter inbound = new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883", "testClient", "#");
        inbound.setCompletionTimeout(5000);
        inbound.setConverter(new DefaultPahoMessageConverter());
        inbound.setQos(1);
        inbound.setOutputChannel(outputChannel);
        inbound.start();

        //sending message AMQP but Receive by Mqtt
        template.convertAndSend(messagePayload);

        Message<?> message1 = outputChannel.receive(10000);
        assertNotNull(message1);
        inbound.stop();

        assertEquals(messagePayload, message1.getPayload());
        assertEquals("/users/registration", message1.getHeaders().get(MqttHeaders.TOPIC));
    }

    @Test
    public void testMqttMessage2Amqp() throws InterruptedException {
        String messagePayload = "Message from Mqtt !!!";

        MqttPahoMessageHandler handler = new MqttPahoMessageHandler("tcp://localhost:1883", clientId, mqttClientFactory);
        handler.setDefaultTopic("/users/registration");
        handler.afterPropertiesSet();
        handler.start();

        //sending message to Mqtt and Receiving by AMQP using Rabbit template
        Message<String> message = new GenericMessage<String>(messagePayload);
        handler.handleMessage(message);
        handler.stop();

        //resulted message will be captured by - amqMessageListener
        org.springframework.amqp.core.Message messageFromRabbit = amqMessageListener.receive();
        assertNotNull(messageFromRabbit);
        System.out.println(messageFromRabbit.toString());

        MqttMessage mqttMessage = new MqttMessage(messageFromRabbit.getBody());
        assertEquals(messagePayload, mqttMessage.toString());
    }
}
