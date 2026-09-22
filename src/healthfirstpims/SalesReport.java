package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SalesReport extends JFrame {

    private JTable salesTable;
    private DefaultTableModel tableModel;

    public SalesReport() {

        setTitle("HealthFirst Pharmacy - Sales Report");
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
                        "Sales Report",
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
                            "Sale ID",
                            "Sale Date",
                            "Cashier",
                            "Total Amount"
                        },
                        0
                );

        salesTable =
                new JTable(tableModel);

        JScrollPane tableScrollPane =
                new JScrollPane(salesTable);

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

        loadSales();

        refreshButton.addActionListener(e ->
                loadSales()
        );

        closeButton.addActionListener(e ->
                dispose()
        );
    }

    private void loadSales() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT s.sale_id, "
                + "s.sale_date, "
                + "u.full_name, "
                + "s.total_amount "
                + "FROM sales s "
                + "LEFT JOIN users u "
                + "ON s.user_id = u.user_id "
                + "ORDER BY s.sale_date DESC";

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
                            result.getInt("sale_id"),
                            result.getTimestamp("sale_date"),
                            result.getString("full_name"),
                            result.getBigDecimal("total_amount")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load sales report:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}