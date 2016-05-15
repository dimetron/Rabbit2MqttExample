### Playground project for Spring Integration and MQTT



1 - start RabbitMQ server with MQTT using docker-compose.
    RabbitMQ management console will be available at [http://127.0.0.1:15672](http://127.0.0.1:15672)
    with default user/pass ~> guest/guest

        docker-compose up -d
        docker-compose ps

2 - start Mosuqitto client to listen for all messages

        brew install mosquitto
        mosquitto_sub -h 127.0.0.1 -v -t /#

3 - run project [MqttAmqpInteropTest.java](https://github.com/dimetron/Rabbit2MqttExample/blob/master/src/test/java/MqttAmqpInteropTest.java)

        ./gradlew test

Result of the test 2 messages will be published:

        /users/registration Message from Mqtt !!!
        /users/registration Message from Amqp !!!
