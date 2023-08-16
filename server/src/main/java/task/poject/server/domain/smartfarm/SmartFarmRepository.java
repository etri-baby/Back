package task.poject.server.domain.smartfarm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SmartFarmRepository extends JpaRepository<SmartFarm, Long> {
    @Query(value = "select f.sensor from farm f where f.timestamp between ?1 and ?2", nativeQuery = true)
    List<String> findByStartDateBetween(LocalDate start, LocalDate end);

    @Query(value = "select f.sensor, f.timestamp from farm f where f.timestamp between ?1 and ?2", nativeQuery = true)
    List<Object> findByDateBetween(LocalDate start, LocalDate end);

}
