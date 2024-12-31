package RentalCars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddCarFrame extends JFrame {
    private JTextField carModelField, carPriceField;
    private JButton addCarButton;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/rentalcar";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "7557";

    public AddCarFrame() {
        setTitle("Araba Ekle");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Araba Modeli:"));
        carModelField = new JTextField(15);
        panel.add(carModelField);

        panel.add(new JLabel("Araba Fiyatı (TL):"));
        carPriceField = new JTextField(15);
        panel.add(carPriceField);

        addCarButton = new JButton("Araba Ekle");
        
       
        addCarButton.setBackground(Color.ORANGE);  
        addCarButton.setForeground(Color.WHITE);   
        
        panel.add(addCarButton);

        addCarButton.addActionListener(e -> addCar());

        add(panel);
        setVisible(true);
    }

    private void addCar() {
        String carModel = carModelField.getText();
        String carPrice = carPriceField.getText();

        if (carModel.isEmpty() || carPrice.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.");
        } else {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO cars (model, price) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, carModel);
                    statement.setInt(2, Integer.parseInt(carPrice));
                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Araba başarıyla eklendi.");
                    dispose();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        // Ensure SwingUtilities is imported
        SwingUtilities.invokeLater(() -> new AddCarFrame());
    }
}
