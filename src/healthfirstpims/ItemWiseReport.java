package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ItemWiseReport extends JFrame {

    private JTable itemTable;
    private DefaultTableModel tableModel;

    public ItemWiseReport() {

        setTitle("HealthFirst Pharmacy - Item-Wise Report");
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
                        "Item-Wise Sales Report",
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
                            "Total Quantity Sold",
                            "Total Sales Amount"
                        },
                        0
                );

        itemTable =
                new JTable(tableModel);

        JScrollPane tableScrollPane =
                new JScrollPane(itemTable);

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

        loadItemReport();

        refreshButton.addActionListener(e ->
                loadItemReport()
        );

        closeButton.addActionListener(e ->
                dispose()
        );
    }

    private void loadItemReport() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT "
                + "m.medicine_id, "
                + "m.name, "
                + "SUM(si.quantity_sold) AS total_quantity, "
                + "SUM(si.quantity_sold * si.price_at_sale) "
                + "AS total_sales "
                + "FROM sale_items si "
                + "INNER JOIN medicines m "
                + "ON si.medicine_id = m.medicine_id "
                + "GROUP BY m.medicine_id, m.name "
                + "ORDER BY total_quantity DESC";

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
                            result.getInt("total_quantity"),
                            result.getBigDecimal("total_sales")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load item-wise report:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}