package task.poject.server.controller;

import java.time.LocalDate;
import java.util.List;
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
import task.poject.server.domain.smartfarm.SmartFarm;
import task.poject.server.domain.smartfarm.SmartFarmService;

@RestController
@RequestMapping("/api/smartfarm")
@RequiredArgsConstructor
public class SmartFarmController {

    @Autowired
    private SmartFarmService service;

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

    @PostMapping("/actuator/control")
    public ResponseEntity ActuatorControl(@RequestParam("kitType") String kitType,
            @RequestParam("actuator") String actuator,
            @RequestParam("control") String control) {
        service.sentToMqtt(kitType, actuator, control);
        if (control.equals("0")) {
            System.out.println(kitType + "의 " + actuator + "가 꺼질거에요");
            return new ResponseEntity(kitType + "의 " + actuator + "가 꺼질거에요", HttpStatus.OK);
        } else if (control.equals("1")) {
            System.out.println(kitType + "의 " + actuator + "가 켜질거에요");
            return new ResponseEntity(kitType + "의 " + actuator + "가 켜질거에요", HttpStatus.OK);
        } else
            System.out.println("삐빅");
        return new ResponseEntity("삐빅", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/sensor/temp")
    public ResponseEntity nowTemperature() {
        return new ResponseEntity(service.nowTemp().toString(), HttpStatus.OK);
    }

    @GetMapping("/sensor/humi")
    public ResponseEntity nowHumidity() {
        return new ResponseEntity(service.nowHumi().toString(), HttpStatus.OK);
    }

    @GetMapping("/sensor/illu")
    public ResponseEntity nowIlluminance() {
        return new ResponseEntity(service.nowIllu().toString(), HttpStatus.OK);
    }

    @GetMapping("/sensor/soil")
    public ResponseEntity nowSoilhumidity() {
        return new ResponseEntity(service.nowSoil().toString(), HttpStatus.OK);
    }

    @GetMapping("/sensor/date")
    public List<Object> getSensorDateHistory(@RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end) {
        return service.getSensorDate(start, end);
    }

    @GetMapping("/actuator")
    public ResponseEntity nowActuator() {
        return new ResponseEntity(service.nowActuator(), HttpStatus.OK);
    }
}