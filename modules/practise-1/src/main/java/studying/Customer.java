package studying;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Customer {
    @Getter
    @Setter
    private Car car;

    @Getter
    private String FIO;

    public Customer(Car car, String FIO) {
        this.car = car;
        this.FIO = FIO;
    }

    public Customer(String FIO) {
        this.FIO = FIO;
    }
}
