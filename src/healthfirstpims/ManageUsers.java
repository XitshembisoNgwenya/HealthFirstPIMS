package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageUsers extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JTextField fullNameField;

    public ManageUsers() {

        setTitle("HealthFirst Pharmacy - Manage Users");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
                "Manage Users",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Username",
                    "Full Name",
                    "Role"
                },
                0
        );

        userTable = new JTable(tableModel);

        userTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane tableScrollPane =
                new JScrollPane(userTable);

        mainPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        JPanel formPanel =
                new JPanel(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleBox = new JComboBox<>(
                new String[]{"Admin", "Cashier"}
        );
        fullNameField = new JTextField();

        formPanel.add(
                new JLabel("Username")
        );
        formPanel.add(usernameField);

        formPanel.add(
                new JLabel("Password")
        );
        formPanel.add(passwordField);

        formPanel.add(
                new JLabel("Role")
        );
        formPanel.add(roleBox);

        formPanel.add(
                new JLabel("Full Name")
        );
        formPanel.add(fullNameField);

        JButton addButton =
                new JButton("Add");

        JButton updateButton =
                new JButton("Update");

        JButton deleteButton =
                new JButton("Delete");

        JButton clearButton =
                new JButton("Clear");

        JButton refreshButton =
                new JButton("Refresh");

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        JPanel bottomPanel =
                new JPanel(new BorderLayout(10, 10));

        bottomPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        bottomPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        loadUsers();

        addButton.addActionListener(e ->
                addUser()
        );

        updateButton.addActionListener(e ->
                updateUser()
        );

        deleteButton.addActionListener(e ->
                deleteUser()
        );

        clearButton.addActionListener(e ->
                clearFields()
        );

        refreshButton.addActionListener(e ->
                loadUsers()
        );

        userTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()
                            && userTable.getSelectedRow() != -1) {

                        int row =
                                userTable.getSelectedRow();

                        usernameField.setText(
                                tableModel.getValueAt(row, 1)
                                        .toString()
                        );

                        fullNameField.setText(
                                tableModel.getValueAt(row, 2)
                                        .toString()
                        );

                        roleBox.setSelectedItem(
                                tableModel.getValueAt(row, 3)
                                        .toString()
                        );

                        passwordField.setText("");
                    }
                });
    }

    private void loadUsers() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT user_id, username, full_name, role "
                + "FROM users "
                + "ORDER BY user_id";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                tableModel.addRow(
                        new Object[]{
                            result.getInt("user_id"),
                            result.getString("username"),
                            result.getString("full_name"),
                            result.getString("role")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load users:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addUser() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        String role =
                roleBox.getSelectedItem().toString();

        String fullName =
                fullNameField.getText().trim();

        if (username.isEmpty()
                || password.isEmpty()
                || fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "INSERT INTO users "
                + "(username, password, role, full_name) "
                + "VALUES (?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            statement.setString(4, fullName);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "User added successfully."
            );

            clearFields();
            loadUsers();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add user:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateUser() {

        int row =
                userTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a user to update.",
                    "Update User",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int userId =
                Integer.parseInt(
                        tableModel.getValueAt(row, 0)
                                .toString()
                );

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        String role =
                roleBox.getSelectedItem().toString();

        String fullName =
                fullNameField.getText().trim();

        if (username.isEmpty()
                || fullName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Username and full name are required.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            if (password.isEmpty()) {

                String sql =
                        "UPDATE users SET "
                        + "username = ?, "
                        + "role = ?, "
                        + "full_name = ? "
                        + "WHERE user_id = ?";

                try (
                        Connection connection =
                                DatabaseConnection.getConnection();

                        PreparedStatement statement =
                                connection.prepareStatement(sql)
                ) {

                    statement.setString(1, username);
                    statement.setString(2, role);
                    statement.setString(3, fullName);
                    statement.setInt(4, userId);

                    statement.executeUpdate();
                }

            } else {

                String sql =
                        "UPDATE users SET "
                        + "username = ?, "
                        + "password = ?, "
                        + "role = ?, "
                        + "full_name = ? "
                        + "WHERE user_id = ?";

                try (
                        Connection connection =
                                DatabaseConnection.getConnection();

                        PreparedStatement statement =
                                connection.prepareStatement(sql)
                ) {

                    statement.setString(1, username);
                    statement.setString(2, password);
                    statement.setString(3, role);
                    statement.setString(4, fullName);
                    statement.setInt(5, userId);

                    statement.executeUpdate();
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "User updated successfully."
            );

            clearFields();
            loadUsers();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update user:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteUser() {

        int row =
                userTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a user to delete.",
                    "Delete User",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int userId =
                Integer.parseInt(
                        tableModel.getValueAt(row, 0)
                                .toString()
                );

        String username =
                tableModel.getValueAt(row, 1)
                        .toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete user "
                        + username + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM users "
                + "WHERE user_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "User deleted successfully."
            );

            clearFields();
            loadUsers();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete user.\n"
                    + "The user may be linked to existing sales.\n\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {

        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");

        if (roleBox.getItemCount() > 0) {
            roleBox.setSelectedIndex(0);
        }

        userTable.clearSelection();
    }
}