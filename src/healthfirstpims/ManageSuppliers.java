package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageSuppliers extends JFrame {

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    private JTextField nameField;
    private JTextField contactField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressField;

    public ManageSuppliers() {

        setTitle("HealthFirst Pharmacy - Manage Suppliers");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titleLabel = new JLabel(
                "Manage Suppliers",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Name",
                    "Contact Person",
                    "Phone",
                    "Email",
                    "Address"
                },
                0
        );

        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane tableScrollPane =
                new JScrollPane(supplierTable);

        mainPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        JPanel formPanel =
                new JPanel(new GridLayout(5, 2, 8, 8));

        nameField = new JTextField();
        contactField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressField = new JTextArea(3, 20);

        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);

        formPanel.add(new JLabel("Supplier Name"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Contact Person"));
        formPanel.add(contactField);

        formPanel.add(new JLabel("Phone"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Address"));
        formPanel.add(new JScrollPane(addressField));

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();

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

        loadSuppliers();

        addButton.addActionListener(e ->
                addSupplier()
        );

        updateButton.addActionListener(e ->
                updateSupplier()
        );

        deleteButton.addActionListener(e ->
                deleteSupplier()
        );

        clearButton.addActionListener(e ->
                clearFields()
        );

        refreshButton.addActionListener(e ->
                loadSuppliers()
        );

        supplierTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()
                            && supplierTable.getSelectedRow() != -1) {

                        int row =
                                supplierTable.getSelectedRow();

                        nameField.setText(
                                tableModel.getValueAt(row, 1)
                                        .toString()
                        );

                        contactField.setText(
                                tableModel.getValueAt(row, 2)
                                        .toString()
                        );

                        phoneField.setText(
                                tableModel.getValueAt(row, 3)
                                        .toString()
                        );

                        emailField.setText(
                                tableModel.getValueAt(row, 4)
                                        .toString()
                        );

                        addressField.setText(
                                tableModel.getValueAt(row, 5)
                                        .toString()
                        );
                    }
                });
    }

    private void loadSuppliers() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT supplier_id, name, contact_person, "
                + "phone, email, address "
                + "FROM suppliers "
                + "ORDER BY supplier_id";

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
                            result.getInt("supplier_id"),
                            result.getString("name"),
                            result.getString("contact_person"),
                            result.getString("phone"),
                            result.getString("email"),
                            result.getString("address")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load suppliers:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addSupplier() {

        String name = nameField.getText().trim();
        String contact = contactField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter the supplier name.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "INSERT INTO suppliers "
                + "(name, contact_person, phone, email, address) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, name);
            statement.setString(2, contact);
            statement.setString(3, phone);
            statement.setString(4, email);
            statement.setString(5, address);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier added successfully."
            );

            clearFields();
            loadSuppliers();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add supplier:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateSupplier() {

        int row = supplierTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a supplier to update.",
                    "Update Supplier",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int supplierId =
                Integer.parseInt(
                        tableModel.getValueAt(row, 0)
                                .toString()
                );

        String sql =
                "UPDATE suppliers SET "
                + "name = ?, "
                + "contact_person = ?, "
                + "phone = ?, "
                + "email = ?, "
                + "address = ? "
                + "WHERE supplier_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    nameField.getText().trim()
            );

            statement.setString(
                    2,
                    contactField.getText().trim()
            );

            statement.setString(
                    3,
                    phoneField.getText().trim()
            );

            statement.setString(
                    4,
                    emailField.getText().trim()
            );

            statement.setString(
                    5,
                    addressField.getText().trim()
            );

            statement.setInt(6, supplierId);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier updated successfully."
            );

            clearFields();
            loadSuppliers();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update supplier:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteSupplier() {

        int row = supplierTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a supplier to delete.",
                    "Delete Supplier",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int supplierId =
                Integer.parseInt(
                        tableModel.getValueAt(row, 0)
                                .toString()
                );

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this supplier?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM suppliers "
                + "WHERE supplier_id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, supplierId);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier deleted successfully."
            );

            clearFields();
            loadSuppliers();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete supplier.\n"
                    + "The supplier may be linked to existing medicines.\n\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {

        nameField.setText("");
        contactField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");

        supplierTable.clearSelection();
    }
}