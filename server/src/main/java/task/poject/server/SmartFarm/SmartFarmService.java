package task.poject.server.SmartFarm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmartFarmService {

    @Autowired
    private SmartFarmRepository repo;

    public List<SmartFarm> getAll() {
        return repo.findAll();
    }
}
