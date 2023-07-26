package task.poject.server.SmartFarm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/smartfarm")
@RequiredArgsConstructor
public class SmartFarmController {

    @Autowired
    private SmartFarmService service;

    public SmartFarmController(SmartFarmService smartFarmService) {
        this.service = smartFarmService;
    }

    @GetMapping
    public List<SmartFarm> getAll() {
        return service.getAll();
    }

    @PostMapping("/actuator")
    public ResponseEntity smaEntity(@RequestParam("kitType") String kitType,
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