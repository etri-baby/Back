package task.poject.server.SmartFarm;

import org.hibernate.mapping.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SmartFarmRepository extends JpaRepository<SmartFarm, Long> {
    @Query(value = "select * from farm where type = :#{#kitType}", nativeQuery = true)
    Optional<SmartFarm> findAllByType(String kitType);
}
