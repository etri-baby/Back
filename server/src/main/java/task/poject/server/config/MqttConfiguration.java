package task.poject.server.config;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import task.poject.server.SmartFarm.SmartFarmRepository;

@Configuration
public class MqttConfiguration {

    private String BROKER_URL;
    private String TOPIC;
    private String PUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private SmartFarmRepository smartFarmRepository;

    @Autowired
    public MqttConfiguration(@Value("${mqtt.url}") String BROKER_URL,
            @Value("${mqtt.topic}") String TOPIC,
            @Value("${mqtt.port}") String PORT,
            SmartFarmRepository smartFarmRepository) {
        this.smartFarmRepository = smartFarmRepository;
        this.BROKER_URL = BROKER_URL;
        this.TOPIC = TOPIC;
    }

    @Bean
    public MessageChannel mqttPub() {
        return new DirectChannel();
    }
}
