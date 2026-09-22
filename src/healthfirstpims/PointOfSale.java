package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PointOfSale extends JFrame {

    private JTextField searchField;
    private JTextField quantityField;

    private JTable medicineTable;
    private JTable cartTable;

    private DefaultTableModel medicineModel;
    private DefaultTableModel cartModel;

    private JLabel totalLabel;

    private double total = 0.0;

    private String username;
    private String fullName;

    public PointOfSale(String username, String fullName) {

        this.username = username;
        this.fullName = fullName;

        setTitle("HealthFirst Pharmacy - Point of Sale");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "Point of Sale",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        JPanel searchPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.add(
                new JLabel("Search Medicine:")
        );

        searchField = new JTextField(25);

        JButton searchButton =
                new JButton("Search");

        JButton showAllButton =
                new JButton("Show All");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        mainPanel.add(
                searchPanel,
                BorderLayout.BEFORE_FIRST_LINE
        );

        medicineModel =
                new DefaultTableModel(
                        new String[]{
                            "ID",
                            "Medicine",
                            "Company",
                            "Price",
                            "Stock"
                        },
                        0
                );

        medicineTable =
                new JTable(medicineModel);

        medicineTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane medicineScrollPane =
                new JScrollPane(medicineTable);

        medicineScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Available Medicines"
                )
        );

        cartModel =
                new DefaultTableModel(
                        new String[]{
                            "ID",
                            "Medicine",
                            "Price",
                            "Quantity",
                            "Subtotal"
                        },
                        0
                );

        cartTable =
                new JTable(cartModel);

        JScrollPane cartScrollPane =
                new JScrollPane(cartTable);

        cartScrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Shopping Cart"
                )
        );

        JPanel centerPanel =
                new JPanel(new GridLayout(1, 2, 10, 10));

        centerPanel.add(medicineScrollPane);
        centerPanel.add(cartScrollPane);

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        JPanel bottomPanel =
                new JPanel(new BorderLayout());

        JPanel quantityPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        quantityPanel.add(
                new JLabel("Quantity:")
        );

        quantityField =
                new JTextField("1", 5);

        JButton addToCartButton =
                new JButton("Add to Cart");

        quantityPanel.add(quantityField);
        quantityPanel.add(addToCartButton);

        JButton removeButton =
                new JButton("Remove Item");

        JButton clearCartButton =
                new JButton("Clear Cart");

        JButton processSaleButton =
                new JButton("Process Sale");

        JButton closeButton =
                new JButton("Close");

        JPanel actionPanel =
                new JPanel();

        actionPanel.add(removeButton);
        actionPanel.add(clearCartButton);
        actionPanel.add(processSaleButton);
        actionPanel.add(closeButton);

        totalLabel =
                new JLabel("Total: R0.00");

        totalLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        JPanel totalPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        totalPanel.add(totalLabel);

        bottomPanel.add(
                quantityPanel,
                BorderLayout.WEST
        );

        bottomPanel.add(
                actionPanel,
                BorderLayout.CENTER
        );

        bottomPanel.add(
                totalPanel,
                BorderLayout.EAST
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        loadMedicines("");

        searchButton.addActionListener(e ->
                loadMedicines(
                        searchField.getText().trim()
                )
        );

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadMedicines("");
        });

        addToCartButton.addActionListener(e ->
                addToCart()
        );

        removeButton.addActionListener(e ->
                removeFromCart()
        );

        clearCartButton.addActionListener(e ->
                clearCart()
        );

        processSaleButton.addActionListener(e ->
                processSale()
        );

        closeButton.addActionListener(e ->
                dispose()
        );

        searchField.addActionListener(e ->
                loadMedicines(
                        searchField.getText().trim()
                )
        );
    }

    private void loadMedicines(String search) {

        medicineModel.setRowCount(0);

        String sql;

        if (search.isEmpty()) {

            sql =
                    "SELECT medicine_id, name, company, "
                    + "price, quantity_in_stock "
                    + "FROM medicines "
                    + "ORDER BY name";

        } else {

            sql =
                    "SELECT medicine_id, name, company, "
                    + "price, quantity_in_stock "
                    + "FROM medicines "
                    + "WHERE name LIKE ? "
                    + "OR company LIKE ? "
                    + "ORDER BY name";
        }

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (!search.isEmpty()) {

                statement.setString(
                        1,
                        "%" + search + "%"
                );

                statement.setString(
                        2,
                        "%" + search + "%"
                );
            }

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                medicineModel.addRow(
                        new Object[]{
                            result.getInt("medicine_id"),
                            result.getString("name"),
                            result.getString("company"),
                            result.getBigDecimal("price"),
                            result.getInt("quantity_in_stock")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load medicines:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addToCart() {

        int row =
                medicineTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine."
            );

            return;
        }

        try {

            int medicineId =
                    Integer.parseInt(
                            medicineModel
                                    .getValueAt(row, 0)
                                    .toString()
                    );

            String medicineName =
                    medicineModel
                            .getValueAt(row, 1)
                            .toString();

            double price =
                    Double.parseDouble(
                            medicineModel
                                    .getValueAt(row, 3)
                                    .toString()
                    );

            int stock =
                    Integer.parseInt(
                            medicineModel
                                    .getValueAt(row, 4)
                                    .toString()
                    );

            int quantity =
                    Integer.parseInt(
                            quantityField
                                    .getText()
                                    .trim()
                    );

            if (quantity <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Quantity must be greater than zero."
                );

                return;
            }

            if (quantity > stock) {

                JOptionPane.showMessageDialog(
                        this,
                        "Not enough stock available."
                );

                return;
            }

            for (int i = 0;
                    i < cartModel.getRowCount();
                    i++) {

                int existingId =
                        Integer.parseInt(
                                cartModel
                                        .getValueAt(i, 0)
                                        .toString()
                        );

                if (existingId == medicineId) {

                    int existingQuantity =
                            Integer.parseInt(
                                    cartModel
                                            .getValueAt(i, 3)
                                            .toString()
                            );

                    int newQuantity =
                            existingQuantity + quantity;

                    if (newQuantity > stock) {

                        JOptionPane.showMessageDialog(
                                this,
                                "The total quantity exceeds available stock."
                        );

                        return;
                    }

                    double subtotal =
                            price * newQuantity;

                    cartModel.setValueAt(
                            newQuantity,
                            i,
                            3
                    );

                    cartModel.setValueAt(
                            subtotal,
                            i,
                            4
                    );

                    calculateTotal();

                    return;
                }
            }

            double subtotal =
                    price * quantity;

            cartModel.addRow(
                    new Object[]{
                        medicineId,
                        medicineName,
                        price,
                        quantity,
                        subtotal
                    }
            );

            calculateTotal();

            quantityField.setText("1");

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid quantity."
            );
        }
    }

    private void removeFromCart() {

        int row =
                cartTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select an item in the cart."
            );

            return;
        }

        cartModel.removeRow(row);

        calculateTotal();
    }

    private void clearCart() {

        cartModel.setRowCount(0);

        calculateTotal();
    }

    private void calculateTotal() {

        total = 0.0;

        for (int i = 0;
                i < cartModel.getRowCount();
                i++) {

            double subtotal =
                    Double.parseDouble(
                            cartModel
                                    .getValueAt(i, 4)
                                    .toString()
                    );

            total += subtotal;
        }

        totalLabel.setText(
                String.format(
                        "Total: R%.2f",
                        total
                )
        );
    }

    private void processSale() {

        if (cartModel.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "The cart is empty."
            );

            return;
        }

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        String.format(
                                "Process sale for R%.2f?",
                                total
                        ),
                        "Confirm Sale",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        String findUserSql =
                "SELECT user_id FROM users "
                + "WHERE username = ?";

        String saleSql =
                "INSERT INTO sales "
                + "(total_amount, user_id) "
                + "VALUES (?, ?)";

        String itemSql =
                "INSERT INTO sale_items "
                + "(sale_id, medicine_id, quantity_sold, "
                + "price_at_sale) "
                + "VALUES (?, ?, ?, ?)";

        String stockSql =
                "UPDATE medicines "
                + "SET quantity_in_stock = "
                + "quantity_in_stock - ? "
                + "WHERE medicine_id = ? "
                + "AND quantity_in_stock >= ?";

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);

            int userId;

            try (
                    PreparedStatement userStatement =
                            connection.prepareStatement(
                                    findUserSql
                            )
            ) {

                userStatement.setString(
                        1,
                        username
                );

                ResultSet userResult =
                        userStatement.executeQuery();

                if (!userResult.next()) {

                    connection.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "Cashier account not found."
                    );

                    return;
                }

                userId =
                        userResult.getInt("user_id");
            }

            int saleId;

            try (
                    PreparedStatement saleStatement =
                            connection.prepareStatement(
                                    saleSql,
                                    java.sql.Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                saleStatement.setDouble(
                        1,
                        total
                );

                saleStatement.setInt(
                        2,
                        userId
                );

                saleStatement.executeUpdate();

                ResultSet keys =
                        saleStatement.getGeneratedKeys();

                if (!keys.next()) {

                    connection.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "Unable to create sale."
                    );

                    return;
                }

                saleId =
                        keys.getInt(1);
            }

            try (
                    PreparedStatement itemStatement =
                            connection.prepareStatement(
                                    itemSql
                            );

                    PreparedStatement stockStatement =
                            connection.prepareStatement(
                                    stockSql
                            )
            ) {

                for (int i = 0;
                        i < cartModel.getRowCount();
                        i++) {

                    int medicineId =
                            Integer.parseInt(
                                    cartModel
                                            .getValueAt(i, 0)
                                            .toString()
                            );

                    int quantity =
                            Integer.parseInt(
                                    cartModel
                                            .getValueAt(i, 3)
                                            .toString()
                            );

                    double price =
                            Double.parseDouble(
                                    cartModel
                                            .getValueAt(i, 2)
                                            .toString()
                            );

                    itemStatement.setInt(
                            1,
                            saleId
                    );

                    itemStatement.setInt(
                            2,
                            medicineId
                    );

                    itemStatement.setInt(
                            3,
                            quantity
                    );

                    itemStatement.setDouble(
                            4,
                            price
                    );

                    itemStatement.executeUpdate();

                    stockStatement.setInt(
                            1,
                            quantity
                    );

                    stockStatement.setInt(
                            2,
                            medicineId
                    );

                    stockStatement.setInt(
                            3,
                            quantity
                    );

                    int updated =
                            stockStatement.executeUpdate();

                    if (updated == 0) {

                        connection.rollback();

                        JOptionPane.showMessageDialog(
                                this,
                                "Sale cancelled because there is not enough stock."
                        );

                        return;
                    }
                }
            }

            connection.commit();

            String receipt =
                    generateReceipt(saleId);

            clearCart();

            loadMedicines(
                    searchField.getText().trim()
            );

            showReceipt(receipt);

        } catch (Exception e) {

            try {

                if (connection != null) {
                    connection.rollback();
                }

            } catch (Exception rollbackError) {
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to process sale:\n"
                    + e.getMessage(),
                    "Sale Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            try {

                if (connection != null) {
                    connection.close();
                }

            } catch (Exception closeError) {
            }
        }
    }

    private String generateReceipt(int saleId) {

        StringBuilder receipt =
                new StringBuilder();

        String date =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm:ss"
                ).format(new Date());

        receipt.append(
                "========================================\n"
        );

        receipt.append(
                "          HEALTHFIRST PHARMACY\n"
        );

        receipt.append(
                "       PHARMACY INVENTORY SYSTEM\n"
        );

        receipt.append(
                "========================================\n"
        );

        receipt.append(
                "Sale ID: "
        ).append(saleId).append("\n");

        receipt.append(
                "Date: "
        ).append(date).append("\n");

        receipt.append(
                "Cashier: "
        ).append(fullName).append("\n");

        receipt.append(
                "----------------------------------------\n"
        );

        receipt.append(
                String.format(
                        "%-20s %5s %10s\n",
                        "Medicine",
                        "Qty",
                        "Amount"
                )
        );

        receipt.append(
                "----------------------------------------\n"
        );

        for (int i = 0;
                i < cartModel.getRowCount();
                i++) {

            String medicine =
                    cartModel
                            .getValueAt(i, 1)
                            .toString();

            int quantity =
                    Integer.parseInt(
                            cartModel
                                    .getValueAt(i, 3)
                                    .toString()
                    );

            double subtotal =
                    Double.parseDouble(
                            cartModel
                                    .getValueAt(i, 4)
                                    .toString()
                    );

            if (medicine.length() > 20) {
                medicine = medicine.substring(0, 20);
            }

            receipt.append(
                    String.format(
                            "%-20s %5d %10.2f\n",
                            medicine,
                            quantity,
                            subtotal
                    )
            );
        }

        receipt.append(
                "----------------------------------------\n"
        );

        receipt.append(
                String.format(
                        "%-26s R%10.2f\n",
                        "TOTAL:",
                        total
                )
        );

        receipt.append(
                "========================================\n"
        );

        receipt.append(
                "        Thank you for your purchase!\n"
        );

        receipt.append(
                "========================================\n"
        );

        return receipt.toString();
    }

    private void showReceipt(String receipt) {

        JTextArea receiptArea =
                new JTextArea();

        receiptArea.setText(receipt);

        receiptArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );

        receiptArea.setEditable(false);

        JScrollPane scrollPane =
                new JScrollPane(receiptArea);

        scrollPane.setPreferredSize(
                new Dimension(500, 500)
        );

        JButton saveButton =
                new JButton("Save Receipt");

        JButton printButton =
                new JButton("Print Receipt");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.add(saveButton);
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        JPanel panel =
                new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        panel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        panel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        JDialog receiptDialog =
                new JDialog(
                        this,
                        "HealthFirst Pharmacy - Receipt",
                        true
                );

        receiptDialog.setSize(550, 600);
        receiptDialog.setLocationRelativeTo(this);
        receiptDialog.add(panel);

        saveButton.addActionListener(e ->
                saveReceipt(
                        receipt,
                        receiptDialog
                )
        );

        printButton.addActionListener(e -> {

            try {

                boolean printed =
                        receiptArea.print();

                if (printed) {

                    JOptionPane.showMessageDialog(
                            receiptDialog,
                            "Receipt sent to printer."
                    );
                }

            } catch (PrinterException ex) {

                JOptionPane.showMessageDialog(
                        receiptDialog,
                        "Unable to print receipt:\n"
                        + ex.getMessage(),
                        "Print Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        closeButton.addActionListener(e ->
                receiptDialog.dispose()
        );

        receiptDialog.setVisible(true);
    }

    private void saveReceipt(
            String receipt,
            JDialog receiptDialog
    ) {

        JFileChooser fileChooser =
                new JFileChooser();

        fileChooser.setDialogTitle(
                "Save Receipt"
        );

        fileChooser.setSelectedFile(
                new File(
                        "HealthFirst_Receipt.txt"
                )
        );

        int result =
                fileChooser.showSaveDialog(
                        receiptDialog
                );

        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file =
                fileChooser.getSelectedFile();

        try (
                FileWriter writer =
                        new FileWriter(file)
        ) {

            writer.write(receipt);

            JOptionPane.showMessageDialog(
                    receiptDialog,
                    "Receipt saved successfully."
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    receiptDialog,
                    "Unable to save receipt:\n"
                    + e.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}