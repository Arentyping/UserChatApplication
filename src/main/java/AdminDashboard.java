import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(640, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        setVisible(true);
    }


    private void initComponents() {

        JTable userTable = new JTable();
        DefaultTableModel model = new DefaultTableModel(new String[]{"Users", "Role", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };
        userTable.setModel(model);

        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();

        JButton banButton = new JButton("Ban");
        JButton removeButton = new JButton("Remove");
        JButton promoteButton = new JButton("Promote to Admin");
        JButton demoteButton = new JButton("Demote to User only");

        buttonPanel.add(banButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(promoteButton);
        buttonPanel.add(demoteButton);
        add(buttonPanel, BorderLayout.SOUTH);


        // Load users into the table
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Users, Roles, Status FROM Credentials")) {

            while (rs.next()) {
                String username = rs.getString("Users");
                String role = rs.getString("Roles");
                String status = rs.getString("Status");
                model.addRow(new Object[]{username, role, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        // Button Actions
        banButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                String username = (String) model.getValueAt(row, 0);
                updateUserStatus(username, "Banned");
                model.setValueAt("Banned", row, 2);
            }
        });

        removeButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                String username = (String) model.getValueAt(row, 0);
                deleteUser(username);
                model.removeRow(row);
            }
        });

        promoteButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                String username = (String) model.getValueAt(row, 0);
                updateUserRole(username, "Administrator");
                model.setValueAt("Administrator", row, 1);
            }
        });

        demoteButton.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                String username = (String) model.getValueAt(row, 0);
                updateUserRole(username, "User");
                model.setValueAt("User", row, 1);
            }
        });
    }

    private void updateUserStatus(String username, String newStatus) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Credentials SET Status = ? WHERE Users = ?")) {
            stmt.setString(1, newStatus);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update status: " + e.getMessage());
        }
    }

    private void deleteUser(String username) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Credentials WHERE Users = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete user: " + e.getMessage());
        }
    }

    private void updateUserRole(String username, String newRole) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Credentials SET Roles = ? WHERE Users = ?")) {
            stmt.setString(1, newRole);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to promote user: " + e.getMessage());
        }
    }

}

