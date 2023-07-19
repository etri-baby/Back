package task.poject.server.SmartFarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmartFarmRepository extends JpaRepository<SmartFarm, Long> {

}
