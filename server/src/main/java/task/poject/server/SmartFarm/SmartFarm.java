package task.poject.server.SmartFarm;

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
    @Column(name = "id")
    private int id;

    @Column(name = "sensor")
    private String sensor;

    @Column(name = "value")
    private int value;
}
