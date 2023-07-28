package task.poject.server.SmartFarm;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "farm")
@NoArgsConstructor
@Data

public class SmartFarm {

    @Id
    @Column(name = "type")
    private String id;

    @Column(name = "sensor")
    private String sensor;

    @Column(name = "value")
    private Float value;

    @Column(name = "timestamp")
    private LocalDateTime timeStamp;
}
