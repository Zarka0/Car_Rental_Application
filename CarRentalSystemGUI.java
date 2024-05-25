import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CarRentalSystemGUI extends JFrame {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalSystemGUI() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
        initializeCars();
        initializeGUI();
    }

    private void initializeCars() {
        cars.add(new Car("C01", "Toyota", "Camry", 60.0));
        cars.add(new Car("C02", "Honda", "Accord", 70.0));
        cars.add(new Car("C03", "Mahindra", "Thar", 150.0));
        cars.add(new Car("C04", "Ford", "Mustang", 120.0));
        cars.add(new Car("C05", "Chevrolet", "Cruze", 80.0));
        cars.add(new Car("C06", "BMW", "X5", 200.0));
        cars.add(new Car("C07", "Mercedes-Benz", "E-Class", 180.0));
        cars.add(new Car("C08", "Audi", "Q7", 220.0));
        cars.add(new Car("C09", "Nissan", "Altima", 75.0));
        cars.add(new Car("C10", "Hyundai", "Tucson", 90.0));
    }

    private void initializeGUI() {
        setTitle("Car Rental System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        JButton rentCarButton = new JButton("Rent a Car");
        rentCarButton.addActionListener(new RentCarActionListener());

        JButton returnCarButton = new JButton("Return a Car");
        returnCarButton.addActionListener(new ReturnCarActionListener());

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        add(rentCarButton);
        add(returnCarButton);
        add(exitButton);
    }

    private class RentCarActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String customerName = JOptionPane.showInputDialog("Enter your name:");
            if (customerName == null || customerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty.");
                return;
            }

            displayAvailableCars();
            String carId = JOptionPane.showInputDialog("Enter the car ID you want to rent:");
            if (carId == null || carId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Car ID cannot be empty.");
                return;
            }

            Car selectedCar = getCarById(carId);
            if (selectedCar == null || !selectedCar.isAvailable()) {
                JOptionPane.showMessageDialog(null, "Invalid car selection or car not available for rent.");
                return;
            }

            String rentalDaysStr = JOptionPane.showInputDialog("Enter the number of days for rental:");
            int rentalDays;
            try {
                rentalDays = Integer.parseInt(rentalDaysStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid number of days.");
                return;
            }

            double totalPrice = selectedCar.calculatePrice(rentalDays);
            StringBuilder rentalInfo = new StringBuilder();
            rentalInfo.append("Customer Name: ").append(customerName).append("\n")
                    .append("Car: ").append(selectedCar.getBrand()).append(" ").append(selectedCar.getModel())
                    .append("\n")
                    .append("Rental Days: ").append(rentalDays).append("\n")
                    .append("Total Price: $").append(String.format("%.2f", totalPrice));

            int confirm = JOptionPane.showConfirmDialog(null, rentalInfo.toString(), "Confirm Rental",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Customer newCustomer = new Customer("CUS" + (rentals.size() + 1), customerName);
                addCustomer(newCustomer);
                rentCar(selectedCar, newCustomer, rentalDays);
                JOptionPane.showMessageDialog(null, "Car rented successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Rental canceled.");
            }
        }
    }

    private class ReturnCarActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String carId = JOptionPane.showInputDialog("Enter the car ID you want to return:");
            if (carId == null || carId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Car ID cannot be empty.");
                return;
            }

            Car carToReturn = getCarById(carId);
            if (carToReturn == null) {
                JOptionPane.showMessageDialog(null, "Car not found.");
                return;
            }

            if (carToReturn.isAvailable()) {
                JOptionPane.showMessageDialog(null, "Car is already available.");
                return;
            }

            returnCar(carToReturn);
            JOptionPane.showMessageDialog(null, "Car returned successfully.");
        }
    }

    private void addCar(Car car) {
        cars.add(car);
    }

    private void addCustomer(Customer customer) {
        customers.add(customer);
    }

    private void rentCar(Car car, Customer customer, int days) {
        car.rent();
        rentals.add(new Rental(car, customer, days));
    }

    private void returnCar(Car car) {
        car.returnCar();
        rentals.removeIf(rental -> rental.getCar() == car);
    }

    private void displayAvailableCars() {
        StringBuilder carList = new StringBuilder("Available Cars:\n");
        for (Car car : cars) {
            if (car.isAvailable()) {
                carList.append(car).append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, carList.toString());
    }

    private Car getCarById(String carId) {
        for (Car car : cars) {
            if (car.getCarId().equals(carId)) {
                return car;
            }
        }
        return null;
    }

    private static class Car {
        private String carId;
        private String brand;
        private String model;
        private double basePricePerDay;
        private boolean isAvailable;

        public Car(String carId, String brand, String model, double basePricePerDay) {
            this.carId = carId;
            this.brand = brand;
            this.model = model;
            this.basePricePerDay = basePricePerDay;
            this.isAvailable = true;
        }

        public String getCarId() {
            return carId;
        }

        public String getBrand() {
            return brand;
        }

        public String getModel() {
            return model;
        }

        public double calculatePrice(int rentalDays) {
            return basePricePerDay * rentalDays;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public void rent() {
            isAvailable = false;
        }

        public void returnCar() {
            isAvailable = true;
        }

        @Override
        public String toString() {
            return carId + " - " + brand + " " + model;
        }
    }

    private static class Customer {
        private String customerId;
        private String name;

        public Customer(String customerId, String name) {
            this.customerId = customerId;
            this.name = name;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getName() {
            return name;
        }
    }

    private static class Rental {
        private Car car;
        private Customer customer;
        private int days;

        public Rental(Car car, Customer customer, int days) {
            this.car = car;
            this.customer = customer;
            this.days = days;
        }

        public Car getCar() {
            return car;
        }

        public Customer getCustomer() {
            return customer;
        }

        public int getDays() {
            return days;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarRentalSystemGUI carRentalSystemGUI = new CarRentalSystemGUI();
            carRentalSystemGUI.setVisible(true);
        });
    }
}
