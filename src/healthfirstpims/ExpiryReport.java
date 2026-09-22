package healthfirstpims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ExpiryReport extends JFrame {

    private JTable expiryTable;
    private DefaultTableModel tableModel;

    public ExpiryReport() {

        setTitle("HealthFirst Pharmacy - Expiry Report");
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
                        "Expiry Report",
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
                            "Expiry Date",
                            "Stock",
                            "Supplier"
                        },
                        0
                );

        expiryTable =
                new JTable(tableModel);

        JScrollPane tableScrollPane =
                new JScrollPane(expiryTable);

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

        loadExpiryReport();

        refreshButton.addActionListener(e ->
                loadExpiryReport()
        );

        closeButton.addActionListener(e ->
                dispose()
        );
    }

    private void loadExpiryReport() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT "
                + "m.medicine_id, "
                + "m.name, "
                + "m.company, "
                + "m.expiry_date, "
                + "m.quantity_in_stock, "
                + "s.name AS supplier "
                + "FROM medicines m "
                + "LEFT JOIN suppliers s "
                + "ON m.supplier_id = s.supplier_id "
                + "WHERE m.expiry_date >= CURDATE() "
                + "AND m.expiry_date <= DATE_ADD(CURDATE(), INTERVAL 1 MONTH) "
                + "ORDER BY m.expiry_date ASC";

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
                            result.getDate("expiry_date"),
                            result.getInt("quantity_in_stock"),
                            result.getString("supplier")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load expiry report:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}