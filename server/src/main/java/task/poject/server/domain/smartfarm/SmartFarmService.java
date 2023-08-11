package task.poject.server.domain.smartfarm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import task.poject.server.config.MqttConfiguration;

@Service
public class SmartFarmService {

    private MqttConfiguration.MqttGateway mqttGateway;
    private Float temp, illu, humi, soil;

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

    public void getTemp(Float tempValue) {
        temp = tempValue;
    }

    public Float nowTemp() {
        return temp;
    }

    public void getIllu(Float illuValue) {
        illu = illuValue;
    }

    public Float nowIllu() {
        return illu;
    }

    public void getHumi(Float humiValue) {
        humi = humiValue;
    }

    public Float nowHumi() {
        return humi;
    }

    public void getSoil(Float soilValue) {
        soil = soilValue;
    }

    public Float nowSoil() {
        return soil;
    }

    public List<String> getSensorHistory(LocalDate start, LocalDate end) {
        return repo.findByStartDateBetween(start, end);
    }
}
