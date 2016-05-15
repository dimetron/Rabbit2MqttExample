### Playground project for Spring Integration and MQTT



1 - start RabbitMQ server with MQTT using docker-compose

        docker-compose up

2 - start Mosuqitto client to listen for all messages

        brew install mosquitto
        mosquitto_sub -h 127.0.0.1 -t /#

3 - run project test

        ./gradlew test

Result of the test 2 messages will be published:

        Message from Mqtt
        Message From Amqp Test