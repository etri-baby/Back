package task.poject.server.SmartFarm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.dsl.context.IntegrationFlowContext.IntegrationFlowRegistration;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import task.poject.server.config.MqttConfiguration;

@RestController
@RequestMapping("/api/smartfarm")
@RequiredArgsConstructor
public class SmartFarmController {

    @Autowired
    private SmartFarmRepository smartFarmRepository;

    @Autowired
    private SmartFarmService service;

    @Autowired
    private MqttConfiguration.MqttGateway mqttGateway;

    @Autowired
    private IntegrationFlowContext flowContext;

    @Autowired
    private MessageChannel mqttInputChannel;

    @Value("${mqtt.url}")
    String BROKER_URL;

    private IntegrationFlowRegistration addAdapter(String... topics) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(BROKER_URL,
                MqttAsyncClient.generateClientId(), topics);
        StandardIntegrationFlow flow = IntegrationFlows.from(adapter)
                .channel(mqttInputChannel)
                .get();
        return this.flowContext.registration(flow).register();
    }

    public SmartFarmController(SmartFarmService smartFarmService) {
        this.service = smartFarmService;
    }

    @GetMapping("/sensor")
    public List<SmartFarm> getAll() {
        return service.getAll();
    }

    @PostMapping("/actuator")
    public ResponseEntity ActuatorControl(@RequestParam("kitType") String kitType,
            @RequestParam("sensor") String sensor,
            @RequestParam("control") String control) {
        service.sentToMqtt(kitType, sensor, control);
        if (control.equals("0")) {
            System.out.println(kitType + "의 " + sensor + "가 꺼질거에요");
            return new ResponseEntity(kitType + "의 " + sensor + "가 꺼질거에요", HttpStatus.OK);
        } else if (control.equals("1")) {
            System.out.println(kitType + "의 " + sensor + "가 켜질거에요");
            return new ResponseEntity(kitType + "의 " + sensor + "가 켜질거에요", HttpStatus.OK);
        } else
            System.out.println("삐빅");
        return new ResponseEntity("삐빅", HttpStatus.BAD_REQUEST);
    }
}