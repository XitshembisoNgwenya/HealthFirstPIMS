package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckStock extends JFrame {

    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CheckStock() {

        setTitle("HealthFirst Pharmacy - Check Stock");
        setSize(1000, 600);
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
                        "Check Stock",
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
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        searchPanel.add(
                new JLabel("Search Medicine:")
        );

        searchField =
                new JTextField(25);

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

        tableModel =
                new DefaultTableModel(
                        new String[]{
                            "ID",
                            "Medicine",
                            "Company",
                            "Type",
                            "Price",
                            "Stock",
                            "Reorder Level",
                            "Expiry Date",
                            "Supplier"
                        },
                        0
                );

        stockTable =
                new JTable(tableModel);

        stockTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane tableScrollPane =
                new JScrollPane(stockTable);

        mainPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        JLabel infoLabel =
                new JLabel(
                        "Items at or below the reorder level are considered low stock.",
                        SwingConstants.CENTER
                );

        infoLabel.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        mainPanel.add(
                infoLabel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        loadStock("");

        searchButton.addActionListener(e ->
                loadStock(
                        searchField.getText().trim()
                )
        );

        showAllButton.addActionListener(e -> {
            searchField.setText("");
            loadStock("");
        });

        searchField.addActionListener(e ->
                loadStock(
                        searchField.getText().trim()
                )
        );
    }

    private void loadStock(String search) {

        tableModel.setRowCount(0);

        String sql;

        if (search.isEmpty()) {

            sql =
                    "SELECT m.medicine_id, "
                    + "m.name, "
                    + "m.company, "
                    + "m.medicine_type, "
                    + "m.price, "
                    + "m.quantity_in_stock, "
                    + "m.reorder_level, "
                    + "m.expiry_date, "
                    + "s.name AS supplier "
                    + "FROM medicines m "
                    + "LEFT JOIN suppliers s "
                    + "ON m.supplier_id = s.supplier_id "
                    + "ORDER BY m.name";

        } else {

            sql =
                    "SELECT m.medicine_id, "
                    + "m.name, "
                    + "m.company, "
                    + "m.medicine_type, "
                    + "m.price, "
                    + "m.quantity_in_stock, "
                    + "m.reorder_level, "
                    + "m.expiry_date, "
                    + "s.name AS supplier "
                    + "FROM medicines m "
                    + "LEFT JOIN suppliers s "
                    + "ON m.supplier_id = s.supplier_id "
                    + "WHERE m.name LIKE ? "
                    + "OR m.company LIKE ? "
                    + "ORDER BY m.name";
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

                tableModel.addRow(
                        new Object[]{
                            result.getInt("medicine_id"),
                            result.getString("name"),
                            result.getString("company"),
                            result.getString("medicine_type"),
                            result.getBigDecimal("price"),
                            result.getInt("quantity_in_stock"),
                            result.getInt("reorder_level"),
                            result.getDate("expiry_date"),
                            result.getString("supplier")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load stock:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}