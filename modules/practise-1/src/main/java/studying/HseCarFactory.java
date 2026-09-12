package studying;

import java.util.ArrayList;
import java.util.List;

public class HseCarFactory {
    private int vinNumber;
    private List<Car> cars = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public boolean createCar(int pedSize) {
        var car = new Car(vinNumber++, pedSize);
        return addCar(car);
    }

    public boolean addCar(Car car) {
        return cars.add(car);
    }
    public boolean removeCar(Car car) {
        return cars.remove(car);
    }

    public boolean addCustomer(Customer customer) {
        return customers.add(customer);
    }
    public boolean removeCustomer(Customer customer) {
        return customers.remove(customer);
    }

    public void saleCar() {
        for (Customer customer : customers) {
            if (cars.isEmpty()) {
                break;
            }

            Car car = cars.getFirst();
            customer.setCar(car);
            removeCar(car);
        }

        cars.clear();
    }

    public void printCars() {
        cars.forEach(System.out::println);
    }

    public void printCustomers() {
        customers.forEach(System.out::println);
    }
}
