package RentalCars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.sql.Date;

public class RentalFrame extends JFrame {
    private JComboBox<String> carModelCombo;
    private JSpinner startDateSpinner, endDateSpinner;
    private JLabel resultLabel, priceLabel;
    private JButton calculateButton, rentButton, backButton;  
    private String kullaniciAdi;
    private int selectedCarPrice;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/rentalcar";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "7557";

    public RentalFrame(String kullaniciAdi) {
        this.kullaniciAdi = kullaniciAdi;

        setTitle("Araba Kiralama");
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10)); 
        panel.setBackground(Color.LIGHT_GRAY);

        JLabel carModelLabel = new JLabel("Araba Modeli:");
        carModelCombo = new JComboBox<>();

        loadAvailableCars(); 

        JLabel startDateLabel = new JLabel("Başlangıç Tarihi:");
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "dd-MM-yyyy");
        startDateSpinner.setEditor(startDateEditor);

        JLabel endDateLabel = new JLabel("Bitiş Tarihi:");
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "dd-MM-yyyy");
        endDateSpinner.setEditor(endDateEditor);

        priceLabel = new JLabel("Fiyat: 0 TL", SwingConstants.CENTER);
        resultLabel = new JLabel("Sonuç: ", SwingConstants.CENTER);

        calculateButton = new JButton("Hesapla");
        calculateButton.setBackground(Color.ORANGE);
        calculateButton.setForeground(Color.WHITE);

        rentButton = new JButton("Kiralama İşlemi Tamamla");
        rentButton.setBackground(Color.ORANGE);
        rentButton.setForeground(Color.WHITE);

        backButton = new JButton("Geri Dön");  
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);

        
        calculateButton.addActionListener(e -> calculatePrice());
        rentButton.addActionListener(e -> rentCar());
        backButton.addActionListener(e -> goBackToLogin());  

        panel.add(carModelLabel);
        panel.add(carModelCombo);
        panel.add(startDateLabel);
        panel.add(startDateSpinner);
        panel.add(endDateLabel);
        panel.add(endDateSpinner);
        panel.add(calculateButton);
        panel.add(priceLabel);
        panel.add(new JLabel(""));
        panel.add(rentButton);
        panel.add(resultLabel);
        panel.add(new JLabel(""));  
        panel.add(backButton);  

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    
    private void loadAvailableCars() {
        
        carModelCombo.removeAllItems();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT model, price FROM cars";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String carModel = resultSet.getString("model");
                    carModelCombo.addItem(carModel);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı bağlantı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void calculatePrice() {
        try {
            String selectedCar = (String) carModelCombo.getSelectedItem();
            int dailyRate = 0;

            if (selectedCar != null) {
                
                try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                    String query = "SELECT price FROM cars WHERE model = ?";
                    try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        stmt.setString(1, selectedCar);
                        ResultSet resultSet = stmt.executeQuery();

                        if (resultSet.next()) {
                            dailyRate = resultSet.getInt("price");
                        }
                    }
                }

                LocalDate startDate = ((SpinnerDateModel) startDateSpinner.getModel()).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = ((SpinnerDateModel) endDateSpinner.getModel()).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
                int totalAmount = (int) (daysBetween * dailyRate);
                selectedCarPrice = dailyRate;

                priceLabel.setText("Fiyat: " + totalAmount + " TL");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Hesaplama hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

   
    private void rentCar() {
        try {
            String selectedCar = (String) carModelCombo.getSelectedItem();
            LocalDate startDate = ((SpinnerDateModel) startDateSpinner.getModel()).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = ((SpinnerDateModel) endDateSpinner.getModel()).getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            int totalAmount = (int) (daysBetween * selectedCarPrice);

            
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO rentals (user_id, car_id, rental_date, return_date, total_amount) " +
                        "VALUES ((SELECT id FROM users WHERE kullaniciadi = ?), (SELECT id FROM cars WHERE model = ?), ?, ?, ?)";

                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, kullaniciAdi);
                    stmt.setString(2, selectedCar);
                    stmt.setDate(3, Date.valueOf(startDate));
                    stmt.setDate(4, Date.valueOf(endDate));
                    stmt.setInt(5, totalAmount);
                    stmt.executeUpdate();
                    resultLabel.setText("Kiralama başarıyla tamamlandı! Toplam Tutar: " + totalAmount + " TL.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kiralama işlemi başarısız oldu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void goBackToLogin() {
        new LoginFrame();  
        dispose();  
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RentalFrame("Emre"));
    }
}
