package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageMedicines extends JFrame {

    private JTable medicineTable;
    private DefaultTableModel tableModel;

    private JTextField nameField;
    private JTextField companyField;
    private JTextField typeField;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField reorderField;
    private JTextField expiryField;
    private JComboBox<String> supplierBox;

    public ManageMedicines() {

        setTitle("HealthFirst Pharmacy - Manage Medicines");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(
                "Manage Medicines",
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{
                    "ID",
                    "Name",
                    "Company",
                    "Type",
                    "Price",
                    "Stock",
                    "Reorder",
                    "Expiry",
                    "Supplier"
                }, 0
        );

        medicineTable = new JTable(tableModel);
        medicineTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane tableScrollPane = new JScrollPane(medicineTable);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(4, 4, 8, 8));

        nameField = new JTextField();
        companyField = new JTextField();
        typeField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();
        reorderField = new JTextField();
        expiryField = new JTextField();
        supplierBox = new JComboBox<>();

        formPanel.add(new JLabel("Medicine Name"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Company"));
        formPanel.add(companyField);

        formPanel.add(new JLabel("Medicine Type"));
        formPanel.add(typeField);

        formPanel.add(new JLabel("Price"));
        formPanel.add(priceField);

        formPanel.add(new JLabel("Quantity"));
        formPanel.add(quantityField);

        formPanel.add(new JLabel("Reorder Level"));
        formPanel.add(reorderField);

        formPanel.add(new JLabel("Expiry Date"));
        formPanel.add(expiryField);

        formPanel.add(new JLabel("Supplier"));
        formPanel.add(supplierBox);

        JPanel bottomPanel = new JPanel(new BorderLayout());

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

        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadSuppliers();
        loadMedicines();

        addButton.addActionListener(e -> addMedicine());
        updateButton.addActionListener(e -> updateMedicine());
        deleteButton.addActionListener(e -> deleteMedicine());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> {
            loadSuppliers();
            loadMedicines();
        });

        medicineTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()
                    && medicineTable.getSelectedRow() != -1) {

                int row = medicineTable.getSelectedRow();

                nameField.setText(
                        tableModel.getValueAt(row, 1).toString()
                );

                companyField.setText(
                        tableModel.getValueAt(row, 2).toString()
                );

                typeField.setText(
                        tableModel.getValueAt(row, 3).toString()
                );

                priceField.setText(
                        tableModel.getValueAt(row, 4).toString()
                );

                quantityField.setText(
                        tableModel.getValueAt(row, 5).toString()
                );

                reorderField.setText(
                        tableModel.getValueAt(row, 6).toString()
                );

                expiryField.setText(
                        tableModel.getValueAt(row, 7).toString()
                );

                supplierBox.setSelectedItem(
                        tableModel.getValueAt(row, 8).toString()
                );
            }
        });
    }

    private void loadSuppliers() {

        supplierBox.removeAllItems();

        String sql = "SELECT supplier_id, name FROM suppliers ORDER BY name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                supplierBox.addItem(
                        result.getInt("supplier_id")
                        + " - "
                        + result.getString("name")
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load suppliers:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadMedicines() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT m.medicine_id, m.name, m.company, "
                + "m.medicine_type, m.price, m.quantity_in_stock, "
                + "m.reorder_level, m.expiry_date, s.name AS supplier "
                + "FROM medicines m "
                + "LEFT JOIN suppliers s "
                + "ON m.supplier_id = s.supplier_id "
                + "ORDER BY m.medicine_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                tableModel.addRow(new Object[]{
                    result.getInt("medicine_id"),
                    result.getString("name"),
                    result.getString("company"),
                    result.getString("medicine_type"),
                    result.getBigDecimal("price"),
                    result.getInt("quantity_in_stock"),
                    result.getInt("reorder_level"),
                    result.getDate("expiry_date"),
                    result.getString("supplier")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load medicines:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addMedicine() {

        try {

            String sql =
                    "INSERT INTO medicines "
                    + "(name, company, medicine_type, price, "
                    + "quantity_in_stock, reorder_level, expiry_date, supplier_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, nameField.getText().trim());
                statement.setString(2, companyField.getText().trim());
                statement.setString(3, typeField.getText().trim());
                statement.setBigDecimal(
                        4,
                        new java.math.BigDecimal(priceField.getText().trim())
                );
                statement.setInt(
                        5,
                        Integer.parseInt(quantityField.getText().trim())
                );
                statement.setInt(
                        6,
                        Integer.parseInt(reorderField.getText().trim())
                );
                statement.setDate(
                        7,
                        Date.valueOf(expiryField.getText().trim())
                );

                int supplierId = Integer.parseInt(
                        supplierBox.getSelectedItem()
                                .toString()
                                .split(" - ")[0]
                );

                statement.setInt(8, supplierId);

                statement.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine added successfully."
            );

            clearFields();
            loadMedicines();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to add medicine:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateMedicine() {

        int row = medicineTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a medicine to update."
            );
            return;
        }

        try {

            int medicineId =
                    Integer.parseInt(
                            tableModel.getValueAt(row, 0).toString()
                    );

            String sql =
                    "UPDATE medicines SET "
                    + "name = ?, company = ?, medicine_type = ?, "
                    + "price = ?, quantity_in_stock = ?, "
                    + "reorder_level = ?, expiry_date = ?, supplier_id = ? "
                    + "WHERE medicine_id = ?";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, nameField.getText().trim());
                statement.setString(2, companyField.getText().trim());
                statement.setString(3, typeField.getText().trim());
                statement.setBigDecimal(
                        4,
                        new java.math.BigDecimal(priceField.getText().trim())
                );
                statement.setInt(
                        5,
                        Integer.parseInt(quantityField.getText().trim())
                );
                statement.setInt(
                        6,
                        Integer.parseInt(reorderField.getText().trim())
                );
                statement.setDate(
                        7,
                        Date.valueOf(expiryField.getText().trim())
                );

                int supplierId = Integer.parseInt(
                        supplierBox.getSelectedItem()
                                .toString()
                                .split(" - ")[0]
                );

                statement.setInt(8, supplierId);
                statement.setInt(9, medicineId);

                statement.executeUpdate();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine updated successfully."
            );

            clearFields();
            loadMedicines();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to update medicine:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void deleteMedicine() {

        int row = medicineTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select a medicine to delete."
            );
            return;
        }

        int medicineId =
                Integer.parseInt(
                        tableModel.getValueAt(row, 0).toString()
                );

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this medicine?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM medicines WHERE medicine_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, medicineId);
            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine deleted successfully."
            );

            clearFields();
            loadMedicines();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to delete medicine:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearFields() {

        nameField.setText("");
        companyField.setText("");
        typeField.setText("");
        priceField.setText("");
        quantityField.setText("");
        reorderField.setText("");
        expiryField.setText("");

        if (supplierBox.getItemCount() > 0) {
            supplierBox.setSelectedIndex(0);
        }

        medicineTable.clearSelection();
    }
}