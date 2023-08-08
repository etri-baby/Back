package task.Sensor;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "soil")
@Entity
@NoArgsConstructor
public class Soilhumidity {

    @Column(name = "kittype")
    private String kitType;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "value")
    private Float value;
}
