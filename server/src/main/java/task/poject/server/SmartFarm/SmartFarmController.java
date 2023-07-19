package task.poject.server.SmartFarm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/smartfarm")
@RequiredArgsConstructor
public class SmartFarmController {

    @Autowired
    private SmartFarmService service;

    @GetMapping
    public List<SmartFarm> getAll() {
        return service.getAll();
    }
}