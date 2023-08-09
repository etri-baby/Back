package task.poject.server.config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import task.Sensor.Temperature;
import task.poject.server.domain.SmartFarm;
import task.poject.server.domain.SmartFarmRepository;
import task.poject.server.domain.SmartFarmService;

@Configuration
public class MqttConfiguration {

    private String BROKER_URL;
    private String TOPIC;
    private String PUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private String SUB_CLIENT_ID = MqttAsyncClient.generateClientId();
    private SmartFarmService service;
    private SmartFarmRepository repository;

    @Autowired
    public MqttConfiguration(@Value("${mqtt.url}") String BROKER_URL,
            @Value("${mqtt.topic}") String TOPIC,
            @Value("${mqtt.port}") String PORT,
            SmartFarmRepository smartFarmRepository,
            SmartFarmService FarmService) {
        this.BROKER_URL = BROKER_URL + ":" + PORT;
        this.TOPIC = TOPIC;
        this.service = FarmService;
        this.repository = smartFarmRepository;
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

    // Subscrib
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inboundChannel() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(BROKER_URL, SUB_CLIENT_ID,
                TOPIC);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler inboundMessageHandler() {
        return message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            System.out.println("Topic:" + topic);
            System.out.println("Payload" + message.getPayload());

            String[] token = topic.split("/");
            String payload = message.getPayload().toString();

            String kitType = token[1];
            String type = token[2];

            if (type.equals("message")) {
                JSONParser parser = new JSONParser();
                try {
                    JSONObject object = (JSONObject) parser.parse(payload);
                    JSONObject sensor = (JSONObject) object.get("Sensor");

                    service.getTemp(Float.parseFloat(sensor.get("temperature").toString()));
                    service.getHumi(Float.parseFloat(sensor.get("humidity").toString()));
                    service.getIllu(Float.parseFloat(sensor.get("illuminance").toString()));
                    service.getSoil(Float.parseFloat(sensor.get("soilhumidity").toString()));

                    SmartFarm smartFarm = new SmartFarm();
                    smartFarm.setKitType(kitType);
                    smartFarm.setSensor(sensor.toJSONString());
                    service.save(smartFarm);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    // Publish
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttMessageHandler() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(PUB_CLIENT_ID, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(2);
        messageHandler.setDefaultTopic(TOPIC);
        return messageHandler;
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
    }
}
