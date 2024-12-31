package RentalCars;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/rentalcar";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "7557";

    private JTable rentalTable;
    private JScrollPane scrollPane;
    private JButton refreshButton;

    public AdminPanel() {
        setTitle("Admin Panel - Kiralanan Arabalar");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        
        rentalTable = new JTable();
        scrollPane = new JScrollPane(rentalTable);
        panel.add(scrollPane, BorderLayout.CENTER);

       
        refreshButton = new JButton("Yenile");
        refreshButton.addActionListener(e -> loadRentalData());
        panel.add(refreshButton, BorderLayout.SOUTH);

        add(panel);

        
        loadRentalData();
    }

    
    private void loadRentalData() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT u.kullaniciadi, c.model, r.rental_date, r.return_date, r.total_amount " +
                    "FROM rentals r " +
                    "JOIN users u ON r.user_id = u.id " +
                    "JOIN cars c ON r.car_id = c.id";

            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet resultSet = stmt.executeQuery()) {

                
                DefaultTableModel tableModel = new DefaultTableModel();
                tableModel.addColumn("Kullanıcı Adı");
                tableModel.addColumn("Araba Modeli");
                tableModel.addColumn("Başlangıç Tarihi");
                tableModel.addColumn("Bitiş Tarihi");
                tableModel.addColumn("Toplam Tutar");

                while (resultSet.next()) {
                    String userName = resultSet.getString("kullaniciadi");
                    String carModel = resultSet.getString("model");
                    Date rentalDate = resultSet.getDate("rental_date");
                    Date returnDate = resultSet.getDate("return_date");
                    int totalAmount = resultSet.getInt("total_amount");

                   
                    tableModel.addRow(new Object[]{userName, carModel, rentalDate, returnDate, totalAmount});
                }

                
                rentalTable.setModel(tableModel);

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
        SwingUtilities.invokeLater(() -> new AdminPanel().setVisible(true));
    }
}
