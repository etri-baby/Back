package task.poject.server.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import task.poject.server.config.MqttConfiguration;

@Service
public class SmartFarmService {

    private MqttConfiguration.MqttGateway mqttGateway;

    @Autowired
    private SmartFarmRepository repo;

    @Autowired
    public SmartFarmService(MqttConfiguration.MqttGateway mqttGateway) {
        this.mqttGateway = mqttGateway;
    }

    public List<SmartFarm> getAll() {
        return repo.findAll();
    }

    public void sentToMqtt(String kitType, String sensor, String control) {
        String topic = "smart/" + kitType + "/actuator/" + sensor;
        mqttGateway.sendToMqtt(control, topic);
    }

    public void save(SmartFarm smartFarm) {
        smartFarm.setTimeStamp(LocalDateTime.now());

        repo.save(smartFarm);

        System.out.println(LocalDateTime.now() + " insert 성공");
    }
}
