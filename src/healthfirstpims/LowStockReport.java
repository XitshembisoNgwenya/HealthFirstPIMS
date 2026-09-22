package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LowStockReport extends JFrame {

    private JTable stockTable;
    private DefaultTableModel tableModel;

    public LowStockReport() {

        setTitle("HealthFirst Pharmacy - Low Stock Report");
        setSize(900, 600);
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
                        "Low Stock Report",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        mainPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        tableModel =
                new DefaultTableModel(
                        new String[]{
                            "Medicine ID",
                            "Medicine",
                            "Company",
                            "Stock",
                            "Reorder Level",
                            "Supplier"
                        },
                        0
                );

        stockTable =
                new JTable(tableModel);

        JScrollPane tableScrollPane =
                new JScrollPane(stockTable);

        mainPanel.add(
                tableScrollPane,
                BorderLayout.CENTER
        );

        JButton refreshButton =
                new JButton("Refresh");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        loadLowStock();

        refreshButton.addActionListener(e ->
                loadLowStock()
        );

        closeButton.addActionListener(e ->
                dispose()
        );
    }

    private void loadLowStock() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT "
                + "m.medicine_id, "
                + "m.name, "
                + "m.company, "
                + "m.quantity_in_stock, "
                + "m.reorder_level, "
                + "s.name AS supplier "
                + "FROM medicines m "
                + "LEFT JOIN suppliers s "
                + "ON m.supplier_id = s.supplier_id "
                + "WHERE m.quantity_in_stock <= m.reorder_level "
                + "ORDER BY m.quantity_in_stock ASC";

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
                            result.getInt("medicine_id"),
                            result.getString("name"),
                            result.getString("company"),
                            result.getInt("quantity_in_stock"),
                            result.getInt("reorder_level"),
                            result.getString("supplier")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load low stock report:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}