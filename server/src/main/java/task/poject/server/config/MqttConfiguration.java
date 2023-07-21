package task.poject.server.config;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import task.poject.server.SmartFarm.SmartFarmRepository;
import task.poject.server.SmartFarm.SmartFarmService;

@Configuration
public class MqttConfiguration {

    private String BROKER_URL;
    private String TOPIC;
    private String PUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private SmartFarmRepository smartFarmRepository;
    private SmartFarmService farmService;

    @Autowired
    public MqttConfiguration(@Value("${mqtt.url}") String BROKER_URL,
            @Value("${mqtt.topic}") String TOPIC,
            @Value("${mqtt.port}") String PORT,
            SmartFarmRepository smartFarmRepository,
            SmartFarmService FarmService) {
        this.BROKER_URL = BROKER_URL + ":" + PORT;
        this.smartFarmRepository = smartFarmRepository;
        this.farmService = FarmService;
        this.TOPIC = TOPIC;
    }

    private MqttConnectOptions connectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setServerURIs(new String[] { BROKER_URL });
        return options;
    }

    @Bean
    public DefaultMqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(connectOptions());
        return factory;
    }

    @Bean
    public MessageChannel mqttPub() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOrderMessageHandler() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(PUB_CLIENT_ID, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(2);
        messageHandler.setDefaultTopic(TOPIC);
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttOrderGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
    }
}
