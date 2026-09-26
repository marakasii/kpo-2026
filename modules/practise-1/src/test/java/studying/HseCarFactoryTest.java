package studying;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class HseCarFactoryTest {
    @Test
    void saleCarAssignsCarsInQueueOrderAndLiquidatesRemainingStock() {
        var firstCustomer = new Customer("Иван Иванов");
        var secondCustomer = new Customer("Пётр Петров");
        var lateCustomer = new Customer("Анна Смирнова");

        factory.addCar(1);
        factory.addCar(2);
        factory.addCar(3);
        factory.addCustomer(firstCustomer);
        factory.addCustomer(secondCustomer);

        factory.saleCar();

        assertNotNull(firstCustomer.getCar());
        assertNotNull(secondCustomer.getCar());

        factory.addCustomer(lateCustomer);
        factory.saleCar();

        assertNull(lateCustomer.getCar());
    }
}
