package RentalCars;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class AdminFrame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/rentalcar";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "7557";

    private JButton viewRentalsButton, addCarButton, backButton;  
    private JPanel rentalsPanel;  

    public AdminFrame() {
        setTitle("Admin Paneli");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

       
        rentalsPanel = new JPanel();
        rentalsPanel.setLayout(new BoxLayout(rentalsPanel, BoxLayout.Y_AXIS));  
        JScrollPane scrollPane = new JScrollPane(rentalsPanel);
        panel.add(scrollPane, BorderLayout.CENTER);

    
        viewRentalsButton = new JButton("Kiralama Bilgilerini Görüntüle");
        addCarButton = new JButton("Araba Ekle");
        backButton = new JButton("Geri Dön");  

  
        viewRentalsButton.setBackground(Color.BLUE);  
        viewRentalsButton.setForeground(Color.WHITE);  
        addCarButton.setBackground(Color.ORANGE);  
        addCarButton.setForeground(Color.WHITE);  
        backButton.setBackground(Color.RED);   
        backButton.setForeground(Color.WHITE);  

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));  
        buttonPanel.add(viewRentalsButton);
        buttonPanel.add(addCarButton);
        buttonPanel.add(backButton);  

        panel.add(buttonPanel, BorderLayout.SOUTH);

       
        backButton.addActionListener(e -> {
            new LoginFrame();  
            dispose();  
        });

        
        viewRentalsButton.addActionListener(e -> loadRentalsData());
        
        
        addCarButton.addActionListener(e -> new AddCarFrame());

        add(panel);
        setVisible(true);
    }

    
    private void loadRentalsData() {
        rentalsPanel.removeAll();  

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Veritabanına başarıyla bağlandı.");  
            String query = "SELECT u.kullaniciadi, c.model, c.price, r.rental_date, r.return_date " +
                           "FROM rentals r " +
                           "JOIN users u ON r.user_id = u.id " +
                           "JOIN cars c ON r.car_id = c.id";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                System.out.println("SQL sorgusu başarıyla çalıştırıldı.");  

                boolean foundRental = false;  

                while (resultSet.next()) {
                    String kullaniciAdi = resultSet.getString("kullaniciadi");
                    String arabaModeli = resultSet.getString("model");
                    int fiyat = resultSet.getInt("price");
                    Date kiralamaTarihi = resultSet.getDate("rental_date");
                    Date teslimTarihi = resultSet.getDate("return_date");

                    JPanel rentalPanel = new JPanel();
                    rentalPanel.setLayout(new BoxLayout(rentalPanel, BoxLayout.Y_AXIS));
                    rentalPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                    rentalPanel.add(new JLabel("Kullanıcı Adı: " + kullaniciAdi));
                    rentalPanel.add(new JLabel("Araba Modeli: " + arabaModeli));
                    rentalPanel.add(new JLabel("Fiyat: " + fiyat + " TL"));
                    rentalPanel.add(new JLabel("Kiralama Tarihi: " + kiralamaTarihi));
                    rentalPanel.add(new JLabel("Teslim Tarihi: " + teslimTarihi));
                    rentalPanel.add(Box.createVerticalStrut(10));  

                    rentalsPanel.add(rentalPanel);
                    foundRental = true;  
                }

                if (!foundRental) {
                    JOptionPane.showMessageDialog(this, "Kayıt bulunamadı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Veriler başarıyla yüklendi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                }

                rentalsPanel.revalidate();
                rentalsPanel.repaint();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı bağlantı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminFrame frame = new AdminFrame();
        });
    }
}
