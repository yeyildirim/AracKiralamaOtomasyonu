package RentalCars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField kullaniciAdiField;
    private JPasswordField sifreField;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/rentalcar";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "7557";

    public LoginFrame() {
        setTitle("EMRE Araba Kiralama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        kullaniciAdiField = new JTextField(15);
        sifreField = new JPasswordField(15);

        JButton loginButton = new JButton("Üye Girişi");
        JButton registerButton = new JButton("Üye Ol");
        JButton adminButton = new JButton("Admin Girişi");

        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(Color.BLUE);
        registerButton.setForeground(Color.WHITE);
        adminButton.setBackground(Color.ORANGE);
        adminButton.setForeground(Color.WHITE);

        panel.add(new JLabel("Kullanıcı Adı:"));
        panel.add(kullaniciAdiField);
        panel.add(new JLabel("Şifre:"));
        panel.add(sifreField);
        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(adminButton);

        loginButton.addActionListener(e -> {
            if (verifyLogin(kullaniciAdiField.getText(), new String(sifreField.getPassword()))) {
                new RentalFrame(kullaniciAdiField.getText());  
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Geçersiz kullanıcı adı veya şifre", "Giriş Hatası", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            new RegisterPage();
            dispose();
        });

        adminButton.addActionListener(e -> {
            String adminUsername = kullaniciAdiField.getText();
            String adminPassword = new String(sifreField.getPassword());
            if (verifyAdminLogin(adminUsername, adminPassword)) {
                new AdminFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Geçersiz admin kullanıcı adı veya şifre", "Admin Girişi Hatası", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private boolean verifyLogin(String kullaniciAdi, String sifre) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM rental WHERE kullaniciadi = ? AND sifre = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, kullaniciAdi);
                statement.setString(2, sifre);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean verifyAdminLogin(String adminUsername, String adminPassword) {
        return adminUsername.equals("admin") && adminPassword.equals("admin123");  
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
